const jwt = require('jsonwebtoken');
const log = require('./logger');
const { v4: uuidv4 } = require('uuid');

//Important to require dbClientManager as exactly "./dbClientManager"
//to get the node module cache to make this be a singleton.
const dbClientManager = require("./dbClientManager");

function tokenMarkLogicAuthentication(options) {
    options = options || {};
    this._secret = options.secret || uuidv4();
    this._tokenExpirationMinutes = options.tokenExpirationMinutes || 60;
    return {
        authorize,
        validateCredentials
    };
}

function authorize(req) {
    return new Promise((resolve, reject) => {
        const token = req.query.token || req.headers.authorization;
        if (!token) {
          log.debug(`No authorization token found`);
          reject({code: 401});
        } else {
          jwt.verify(token, _secret, function (err, decoded) {
            if (err) {
              log.debug(`Token validation failed: ${JSON.stringify(err)}`);
              err.code = 401;
              reject(err);
            }
            const username = decoded.sub;
            req.markLogicClientCacheKey = username;

            // Verify that the client in the client cache still exists. If it expired, then force the user to
            // re-authenticate.
            if (dbClientManager.getCachedMarkLogicClient(username)) {
              resolve(decoded);
            } else {
              reject({code: 401});
            }
          });
        }
    });
}

function validateCredentials(req, username, password, resolve, reject) {
  dbClientManager.connectAndCacheClient(username, password).then(response => {
    if (response.authenticated) {
      let expiration = _tokenExpirationMinutes;
      // Create access token and wrap in response object
      if (req && req.query) {
        expiration = req.query.expiration || _tokenExpirationMinutes;
        if (expiration > _tokenExpirationMinutes) {
          expiration = _tokenExpirationMinutes;
        }
      }
      let expires = Date.now() + (expiration * 60 * 1000);
      let json = {
        token: jwt.sign({exp: Math.floor(expires / 1000), sub: username}, _secret),
        expires
      };
      req.markLogicClientCacheKey = username;
      resolve(json);
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

module.exports = tokenMarkLogicAuthentication;
