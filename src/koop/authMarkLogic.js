const log = require('./logger');

let _tokenMarklogicAuthentication;

function auth(options) {
    _tokenMarklogicAuthentication = require("./tokenMarkLogicAuthentication")(options);
    return {
        type:'auth',
        authenticationSpecification,
        authenticate,
        authorize
    };
}

function authenticationSpecification() {
    return {
      useHttp: true
    };
}

function authenticate(req) {
    log.debug("called authenticate");
    
    return new Promise((resolve, reject) => {
        let inputUsername;
        let inputPassword;

        if (req && req.query) {
            inputUsername = req.query.username;
            inputPassword = req.query.password;
        }

        log.debug("username:");
        log.debug(inputUsername);

        // Validate user's credentials
        return _tokenMarklogicAuthentication.validateCredentials(req, inputUsername, inputPassword, resolve, reject);
    });
}

function authorize(req) {
    return _tokenMarklogicAuthentication.authorize(req);
}
/*
function authorize(req) {
    log.debug("called authorize");
    return new Promise((resolve, reject) => {
        let token;
        //if (req && req.query && req.query.token) token = req.query.token;
        if (req && req.query && req.query.token) token = req.query.token;
        if ((req && req.headers && req.headers.authorization)) token = req.headers.authorization;
        if (!token) {
            let err = new Error('No authorization token.')
            err.code = 401
            reject(err)
          }
        // Verify token with async decoded function
        jwt.verify(token, _secret, function (err, decoded) {
            // If token invalid, reject
            if (err) {
                err.code = 401;
                reject(err);
            }
            // Resolve the decoded token (an object)
            resolve(decoded);
        });
    });
}
*/


module.exports=auth;
