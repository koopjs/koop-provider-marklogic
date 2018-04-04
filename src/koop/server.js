/*
 * Copyright © 2017 MarkLogic Corporation
 */

// clean shutdown on `cntrl + c`
process.on('SIGINT', () => process.exit(0))
process.on('SIGTERM', () => process.exit(0))

const config = require('config');
const log = require('./logger');
const fs = require('fs');
const express = require('express');
const app = express();
const Koop = require('koop');
const koop = new Koop();

// Install the marklogic Provider
const provider = require('./')
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
