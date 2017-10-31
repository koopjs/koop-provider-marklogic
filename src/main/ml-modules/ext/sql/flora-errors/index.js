'use strict';

const has = require('../has/src/index.js');

class FloraError extends Error {
    constructor(message, ...args) {
        super(message, ...args);

        Error.captureStackTrace(this, this.constructor);
        this.name = this.constructor.name;

        /**
         * @type {string}
         * @name FloraError#message
         * @readonly
         */
        this.message = message;

        /**
         * @type {number}
         * @name FloraError#httpStatusCode
         * @readonly
         */
        this.httpStatusCode = 500; // HTTP status for "other errors"
    }
}

class RequestError extends FloraError {
    constructor(...args) {
        super(...args);
        this.httpStatusCode = 400;
    }
}

class AuthenticationError extends FloraError {
    constructor(...args) {
        super(...args);
        this.httpStatusCode = 401;
    }
}

class AuthorizationError extends FloraError {
    constructor(...args) {
        super(...args);
        this.httpStatusCode = 403;
    }
}

class NotFoundError extends FloraError {
    constructor(...args) {
        super(...args);
        this.httpStatusCode = 404;
    }
}

class ImplementationError extends FloraError {
    constructor(...args) {
        super(...args);
        this.httpStatusCode = 500;
    }
}

class DataError extends FloraError {
    constructor(...args) {
        super(...args);
        this.httpStatusCode = 500;
    }
}

class ConnectionError extends FloraError {
    constructor(...args) {
        super(...args);
        this.httpStatusCode = 503;
    }
}

/**
 * Converts an error object to a stringifyable object format for use
 * in Flora responses.
 *
 * @param {FloraError} err object
 * @param {Object=} options
 */
function format(err, options) {
    const error = { message: 'Internal Server Error' };

    options = options || {};

    if (err.httpStatusCode && err.httpStatusCode < 500) error.message = err.message;

    // TODO: code: err.code ??

    if (options.exposeErrors) {
        error.message = err.message;
        error.stack = err.stack.split(/\r?\n/);

        if (err.info) {
            Object.keys(err.info).forEach((key) => {
                if (!has(error, key) &&
                    has(err.info, key)) error[key] = err.info[key];
            });
        }
    }

    return error;
}

module.exports = {
    RequestError,
    AuthenticationError,
    AuthorizationError,
    NotFoundError,
    ImplementationError,
    DataError,
    ConnectionError,
    FloraError,
    format
};
