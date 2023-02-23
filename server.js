#!/usr/bin/env node

/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// clean shutdown on `ctrl + c`
process.on('SIGINT', () => process.exit(0));
process.on('SIGTERM', () => process.exit(0));

// Following the example at https://koopjs.github.io/docs/usage/koop-core#koop-as-middleware of running Koop
// in an existing Express server. A user can then further customize the Express server as desired.
const express = require('express');
const Koop = require('@koopjs/koop-core');

const config = require('config');
const log = require('./src/koop/logger');
const dbClientManager = require('./src/koop/dbClientManager');

const koop = new Koop({});

// Determine the authorization strategy to use; see https://koopjs.github.io/docs/usage/authorization for more info.
if (config.auth && config.auth.plugin && config.auth.enabled !== false) {
  log.info(`Enabling auth plugin: ${config.auth.plugin}`);
  let auth = null;
  if (config.auth.plugin === 'auth-marklogic-digest-basic') {
    auth = require("./src/koop/authMarkLogic")(config.auth.options);
  } else if (config.auth.plugin === 'auth-marklogic-basic-header') {
    auth = require("./src/koop/authBasicHeader")();
  } else if (config.auth.plugin) {
    auth = require(config.auth.plugin)(config.auth.options);
  }
  koop.register(auth);
} else {
  log.info(`No auth plugin specified, relying on configured MarkLogic credentials`);
  dbClientManager.useStaticClient(true);
}

// Install the Marklogic Provider after installing an authorization plugin.
const provider = require('./src/koop');
koop.register(provider);

// Create and configure the Express app server.
const app = express();
const port = config.port || 8080;
app.use('/', koop.server);
app.listen(port);
log.info("Koop listening on port " + port + "; press ctrl-C to exit");
