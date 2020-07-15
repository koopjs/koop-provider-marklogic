#!/usr/bin/env node

/*
 * Copyright © 2017 MarkLogic Corporation
 */

// clean shutdown on `ctrl + c`
process.on('SIGINT', () => process.exit(0));
process.on('SIGTERM', () => process.exit(0));

const config = require('config');
const log = require('./src/koop/logger');
const fs = require('fs');
const express = require('express');
const http = require('http');
const proxy = require('./src/koop/proxy');
const Koop = require('koop');
//Important to require dbClientManager case sensitively as "dbClientManager"
//to get the node module cache to effectively make this be a singleton.
const dbClientManager = require('./src/koop/dbClientManager');
const koop = new Koop();

// Configure the auth plugin by executing its exported function with required args
if (config.auth && config.auth.enabled) {
  let auth = null;
  if (config.auth.plugin === 'auth-direct-file') {
    auth = require('@koopjs/auth-direct-file')(config.auth.options.secret, config.auth.options.identityStore, config.auth.options);
    dbClientManager.useStaticClient(true);
  } else if (config.auth.plugin === 'auth-marklogic-digest-basic') {
    auth = require("./src/koop/authMarkLogic")(config.auth.options);
  } else if (config.auth.plugin) { 
    //if it's something we don't recognize, try to require it by plugin name and pass in the options object
    //if this provider wants to use the static client, it will have to call dbClientManager.useStaticClient()
    //itself in the exported function.
    try {
      auth = require(config.auth.plugin)(config.auth.options);
    }
    catch(err) {
      throw new Error(`auth plugin ${config.auth.plugin} not recognized`);
    }
  }

  if (auth) {
    koop.register(auth);
  }

  log.info(`Using auth plugin ${config.auth.plugin}`);

} else {
  //if there's no auth provider configured, we have to use a direct pre-authenticated db client
  dbClientManager.useStaticClient(true);

  log.info(`No auth plugin specified, relying on configured MarkLogic credentials`);
}
// install the Marklogic Provider
const provider = require('./src/koop');
koop.register(provider);

// create the "global" app
const app = express();

if (config.enableServiceProxy) {
  // proxy requests for Geo Data Services REST extensions and v1/documents
  app.use(/\/(v1|LATEST)/, 
    proxy.create(/\/(resources\/(modelService|geoSearchService|geoQueryService)|documents)/));
}
log.info(`Service proxy for geo data services is ${(config.enableServiceProxy ? 'enabled' : 'disabled')}`);

// otherwise route to Koop
app.use('/', koop.server);

// create HTTP server
const server = http.createServer(app)
  .listen(config.port || 80);
log.info(`Koop MarkLogic Provider listening for HTTP on ${config.port}`);

// also create an HTTPS server if enabled
if (config.ssl.enabled) {
  const https = require('https');
  const options = {
    key: fs.readFileSync(config.ssl.key),
    cert: fs.readFileSync(config.ssl.cert)
  };
  https.createServer(options, app)
    .listen(config.ssl.port || 443);
  log.info(`Koop MarkLogic Provider listening for HTTPS on ${config.ssl.port}`);
}

console.log('Press control + c to exit');

module.exports = server; // make it testable