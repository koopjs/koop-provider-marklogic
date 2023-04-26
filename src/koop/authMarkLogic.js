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
    return new Promise((resolve, reject) => {
        let inputUsername;
        let inputPassword;
        if (req && req.query) {
            inputUsername = req.query.username;
            inputPassword = req.query.password;
        }
        return _tokenMarklogicAuthentication.validateCredentials(req, inputUsername, inputPassword, resolve, reject);
    });
}

function authorize(req) {
    return _tokenMarklogicAuthentication.authorize(req);
}

module.exports=auth;
