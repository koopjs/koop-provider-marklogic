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
    log.debug("called authorize");
    return new Promise((resolve, reject) => {
        let token;
        //if (req && req.query && req.query.token) token = req.query.token;
        if (req && req.query && req.query.token) token = req.query.token;
        if ((req && req.headers && req.headers.authorization)) token = req.headers.authorization;
        if (!token) {
            let tokenErr = new Error('No authorization token.');
            tokenErr.code = 401;
            reject(tokenErr);
        }
        // Verify token with async decoded function
        jwt.verify(token, _secret, function (err, decoded) {
            // If token invalid, reject
            if (err) {
                log.debug(err);
                err.code = 403;
                reject(err);
            }
            log.debug("setting req.marklogicUsername to " + decoded.sub);
            let username = decoded.sub;
            req.marklogicUsername = username;

            //now we need to make sure the client didn't get booted out of cache
            if (dbClientManager.getDBClient(username)) {
                log.debug("authorize successful: dbClient previusly connected");
                // Resolve the decoded token (an object)
                resolve(decoded);
            }
            else {
                //if it did get booted out, we can't talk to ML until the
                //client re-authenticates; reject with a 401
                reject({message:"Please re-authenticate", code:401});
            }
        });
    });
}

function validateCredentials(req, username, password, resolve, reject) {
    log.debug("calling dbClientManager.connectClient()...");
    dbClientManager.connectClient(username, password).then(response => {
    log.debug("response from connectClient: ");
    log.debug(response);
    if (response.authenticated) {
        log.debug("successfully authenticated " + username);
        let expiration = _tokenExpirationMinutes;
        // Create access token and wrap in response object
        if (req && req.query) {
            expiration = req.query.expiration || _tokenExpirationMinutes;
            if (expiration > _tokenExpirationMinutes)
                expiration = _tokenExpirationMinutes;
        }
        log.debug("about to make jwt");
        let expires = Date.now() + (expiration * 60 * 1000);
        let json = {
            token: jwt.sign({exp: Math.floor(expires / 1000), sub: username}, _secret),
            expires
        };
        req.marklogicUsername = username;

        log.debug("json token:");
        log.debug(json);
        resolve(json);
    } else if (response.authenticated == false) {
        let err = new Error('Invalid credentials.');
        err.code = 401;
        err.httpStatusMessage = response.httpStatusMessage;
        err.httpStatusCode = response.httpStatusCode;
        reject(err);
    }
    else {
        let err = new Error("MarkLogic error");
        err.code = 500;
        log.debug("MarkLogic error");
        reject(err);
    }
}).catch(caughtErr => {
    let err = new Error("MarkLogic error:");
        err.code=500;
        err.message = caughtErr.message;
        log.debug("MarkLogic error:");
        log.debug(caughtErr);
        reject(err);
});
}

module.exports = tokenMarkLogicAuthentication;
