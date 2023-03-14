/**
* This module is intended to be a singleton manager of marklogic databaseClient
* instances.  It's important to always require it the same way (case sensitive) in order
* for node's module caching to make it effectively a singleton, and to also track any
* changes to node's implementation and module caching behavior.
*/
const config = require('config');
const marklogic = require('marklogic');
const Agent = require('yakaa');
const log = require('./logger');
const NodeCache = require( "node-cache" );

let _clientCache = new NodeCache({useClones:false});
// "static" = a single client for all users to use for connecting to MarkLogic.
let _staticClient = null;
let _keepAliveAgent = new Agent({ keepAlive: true });
let _useStaticClient = false;

/**
 * The marklogic.js module uses this to obtain a client instance, which is expected to have been configured via an
 * authorization plugin. Or if no plugin was configured, then the "static" client is returned.
 *
 * @param clientCacheKey
 * @returns {DatabaseClient|unknown}
 */
function getCachedMarkLogicClient(clientCacheKey) {
  if (config.auth == null || _useStaticClient) {
    if (_staticClient == null) {
      log.info("Creating single MarkLogic client for all users to use");
      if (config.marklogic && config.marklogic.connection) {
        let connectionParams = JSON.parse(JSON.stringify(config.marklogic.connection));
        connectionParams.agent = _keepAliveAgent;
        _staticClient = marklogic.createDatabaseClient(connectionParams);
      } else {
        throw Error("Must define marklogic.connection block in JSON configuration file");
      }
    }
    return _staticClient;
  }
  return _clientCache.get(clientCacheKey);
}

function useStaticClient(staticClientEnabled) {
  _useStaticClient = staticClientEnabled;
}

/**
 * Expected to be used by authorization plugins to create a client and connect to MarkLogic, and if done successfully,
 * cache the client as well so that marklogic.js can access it via an authorization-specific cache key.
 *
 * As expected from the signature, only supports basic/digest authentication today since username/password are required.
 *
 * @param username
 * @param password
 * @param ttlInSeconds
 * @param clientCacheKey
 * @returns {Promise<unknown>}
 */
function connectAndCacheClient(username, password, ttlInSeconds=0, clientCacheKey=username) {
  const connectionParams = JSON.parse(JSON.stringify(config.marklogic.connection));
  connectionParams.user = username;
  connectionParams.password = password;
  connectionParams.agent = _keepAliveAgent;

  const client = marklogic.createDatabaseClient(connectionParams);
  return new Promise((resolve, reject) => {
    client
      .checkConnection()
      .result(
        function (response) {
          log.debug(`checkConnection response: ${JSON.stringify(response)}`);
          if (response.connected) {
            if (ttlInSeconds > 0) {
              _clientCache.set(clientCacheKey, client, ttlInSeconds);
            } else {
              _clientCache.set(clientCacheKey, client);
            }
            resolve({"authenticated": true});
          } else {
            _clientCache.del(clientCacheKey);
            resolve({"authenticated": false, "httpStatusMessage": response.httpStatusMessage});
          }
        }
      )
      .catch(error => {
        // Add a message for context; the error already contains the message from the MarkLogic client.
        error.message = `Received error while connecting as user ${username}`;
        reject(error);
      });
  });
}

module.exports={
  connectAndCacheClient,
  getCachedMarkLogicClient,
  useStaticClient
};
