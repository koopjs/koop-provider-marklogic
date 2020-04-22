#!/usr/bin/env node

/*
 * Copyright © 2017 MarkLogic Corporation
 */

// clean shutdown on `cntrl + c`
process.on('SIGINT', () => process.exit(0))
process.on('SIGTERM', () => process.exit(0))

const config = require('config');
const log = require('./src/koop/logger');
const fs = require('fs');
const express = require('express');
const app = express();
const Koop = require('koop');
const koop = new Koop();

// Configure the auth plugin by executing its exported function with required args
if (config.auth) {
  let auth = null;
  if (config.auth.plugin === 'auth-direct-file') {
    auth = require('@koopjs/auth-direct-file')(config.auth.options.secret, config.auth.options.identityStore, config.auth.options);
  } else {
    throw new Error(`auth plugin ${config.auth.plugin} not recognized`);
  }

  if (auth) {
    koop.register(auth);
  }
}

// Install the marklogic Provider
const provider = require('./src/koop')
koop.register(provider)

if (config.ssl.enabled) {
  const https = require('https');

  const options = {
    key: fs.readFileSync(config.ssl.key),
    cert: fs.readFileSync(config.ssl.cert)
  };

  https.createServer(
    options,
    app.use('/', koop.server)
  ).listen(config.ssl.port || 443);

  log.info("Koop MarkLogic Provider listening for HTTPS on ${config.ssl.port}");
}

koop.server.listen(config.port || 80);

const message = `

Koop MarkLogic Provider listening for HTTP on ${config.port}

Press control + c to exit
`
log.info(message)
