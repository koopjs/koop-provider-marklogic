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

const config = require('config');
const dbClientManager = require("./dbClientManager");
const log = require('./logger');

const clientCacheTtl = config.auth.clientCacheTtl || 3600;
log.info(`Client cache time-to-live in seconds: ${clientCacheTtl}`);

// Spec is defined at https://koopjs.github.io/docs/development/authorization
function auth() {
  return {
    type: 'auth',
    authenticationSpecification,
    authenticate,
    authorize
  };
}

function authenticationSpecification() {
  // Not clear yet based on the Koop docs what else can be defined here.
  return {
    useHttp: true
  };
}

/**
 * Function spec - https://koopjs.github.io/docs/development/authorization#authenticate-authenticatereq--promise .
 *
 * This seems to be used only when a client calls e.g. "/marklogic/tokens" to generate a token. For the use case for
 * this plugin, where a client is expected to send a basic authorization header containing a username/password on each
 * request to Koop, this function does not need to be implemented.
 *
 * @param req
 * @returns {Promise<unknown>}
 */
function authenticate(req) {
  return new Promise((result, reject) => {
    const err = new Error('Token generation not supported');
    err.code = 400;
    reject(err);
  });
}

function authorize(request) {
  log.debug(`Authorizing based on basic authorization header value`);
  return new Promise((resolve, reject) => {
    const basicHeaderValue = _getBasicHeaderValue(request);
    let usernameAndPassword = null;
    if (basicHeaderValue != null) {
      usernameAndPassword = _getUsernameAndPassword(basicHeaderValue);
    }
    if (usernameAndPassword === null) {
      const err = new Error("Unauthorized");
      err.code = 401;
      reject(err);
    }

    const clientCacheKey = basicHeaderValue;
    const existingClient = dbClientManager.getCachedMarkLogicClient(clientCacheKey);

    if (existingClient !== undefined) {
      request.markLogicClientCacheKey = clientCacheKey;
      resolve({});
    } else {
      const username = usernameAndPassword[0];
      const password = usernameAndPassword[1];
      dbClientManager.connectAndCacheClient(username, password, clientCacheTtl, clientCacheKey).then(response => {
        if (response.authenticated) {
          request.markLogicClientCacheKey = clientCacheKey;
          resolve({});
        } else {
          // Per https://koopjs.github.io/docs/development/authorization#authorize-function-authorizereq--promise,
          // a 401 should be returned here, though Koop will turn that into a 200 with the response body containing a
          // JSON object that defines the error.
          const err = new Error(response.httpStatusMessage);
          err.code = 401;
          reject(err);
        }
      }).catch(error => {
        // No need to log, Koop will log this automatically at the "error" level.
        reject(error);
      });
    }
  });
}

function _getBasicHeaderValue(request) {
  // QGIS uses 'authorization', but it's commonly 'Authorization' as well.
  const basicHeader = request.headers['Authorization'] || request.headers['authorization'];
  return basicHeader && basicHeader.startsWith('Basic ') ? basicHeader.substring(6) : null;
}

function _getUsernameAndPassword(basicHeaderValue) {
  const decoded = Buffer.from(basicHeaderValue, "base64").toString("utf-8");
  const usernameAndPassword = decoded.split(":");
  if (usernameAndPassword.length == 2) {
    return usernameAndPassword;
  }
  return null;
}

module.exports = auth;
