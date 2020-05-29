/**
* This module is intended to be a singleton manager of marklogic databaseClient
* instances.  It's important to always require it the same way (case sensitive) in order
* for node's module caching to make it effectively a singleton, and to also track any
* changes to node's implementation and module caching behavior.
*/
const config = require('config');
const marklogic = require('marklogic');
const http = require('http');
const Agent = require('yakaa');
const log = require('./logger');
const NodeCache = require( "node-cache" );

let _dbClientCache = new NodeCache({useClones:false});
let _staticClient = null;
let _keepAliveAgent = new Agent({ keepAlive: true });
let _useStaticClient = false;

const dbc = {
        getDBClient,
        useStaticClient,
        connectClient,
        ensureClientConnected
    };

function getDBClient(username) {
    
    if (config.auth == null || _useStaticClient) {
        log.debug("getting static db client");
        if (_staticClient == null) {
            log.debug("creating static dbClient");
            let connectionParams = JSON.parse(JSON.stringify(config.marklogic.connection));
            connectionParams.agent = _keepAliveAgent;
             _staticClient = marklogic.createDatabaseClient(connectionParams);
        }
        return _staticClient;
    }
    log.debug("getting dbClient for username " + username);
    return _dbClientCache.get(username);
}

function ensureClientConnected(username, password) {
    if (config.auth == null || _useStaticClient) {
        return true;
    }
    if (_dbClientCache.has(username)) {
        return true;
    }
    else {
        return connectClient(username, password).then(response => {
            log.debug("ensureClientConnected connectClient() result: ");
            log.debug(response);
            return response.authenticated;
        }).catch(err => {
            log.debug("ensureClientConnected connectClient() error: ");
            log.debug(err);
            return false;
        });
    }
}

function useStaticClient(staticClientEnabled) {
    if (staticClientEnabled == true) {
        _useStaticClient = true;
    } else {
        _useStaticClient = false;
    }
}

function checkDbClientConnection(db, username) {
    let connectionInfo = {};
    return new Promise((resolve, reject) => {
        db.checkConnection().result(function(response) {
            log.debug("db.checkConnection result:");
            log.debug(response);
            if (response.connected) {
                log.debug("response is connected");
                connectionInfo.authenticated = true;
                log.debug("setting db client in cache...");
                _dbClientCache.set(username, db);
                resolve(connectionInfo);
            }
            else {
                log.debug("response is not connected");
                connectionInfo.authenticated = false;
                connectionInfo.httpStatusMessage = response.httpStatusMessage;
                connectionInfo.httpStatusCode = response.httpStatusCode;
                log.debug("clearing db client from cache...");
                _dbClientCache.del(username);
                log.debug("resolving connectionInfo:");
                log.debug(connectionInfo);
                resolve(connectionInfo);
            }
        },
        function(error) {
            log.debug("error occurred:");
            log.error(error);
            connectionInfo.error = true;
            connectionInfo.errorMsg = error.message;
            _dbClientCache.del(username);
            log.debug("resolving connection error connectionInfo:");
            log.debug(connectionInfo);
            resolve(connectionInfo);
        }).catch(error => {
            log.error(error);
            //throw new Error("Error connecting client");
            log.debug("rejecting connectionInfo:");
            log.debug(connectionInfo);
            reject(connectionInfo);
        })
    });
}

function connectClient(username, password) {
    //we make a deep copy of the connection params so we don't leak 
    //usernames/passwords across requests and so we can specify the
    //agent.  Setting the agent to the global agent makes the
    //marklogic.createDatabaseClient() call as well as the database client
    //itself both lightweight 
    let connectionParams = JSON.parse(JSON.stringify(config.marklogic.connection));
    connectionParams.user = username;
    connectionParams.password = password;
    connectionParams.agent = _keepAliveAgent;

    log.debug("creating dbClient...");
    let db = marklogic.createDatabaseClient(connectionParams);
    log.debug (db)
    return checkDbClientConnection(db,username);
}

module.exports=dbc;
