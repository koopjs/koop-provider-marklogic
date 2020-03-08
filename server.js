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
const koop = new Koop();

// install the Marklogic Provider
const provider = require('./src/koop');
koop.register(provider);

// create the "global" app
const app = express();
// allow direct access to Geo Data Services REST extensions
app.use('/v1/resources', 
  proxy.create(/\/geoQueryService|\/geoSearchService|\/modelService|\/documentService/));
// otherwise route to Koop
app.use('/', koop.server);

// create HTTP server
http.createServer(app)
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
