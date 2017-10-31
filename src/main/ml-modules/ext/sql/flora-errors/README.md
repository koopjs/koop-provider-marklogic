Flora Errors
============

[![Build Status](https://travis-ci.org/godmodelabs/flora-errors.svg?branch=master)](https://travis-ci.org/godmodelabs/flora-errors)
[![NPM version](https://badge.fury.io/js/flora-errors.svg)](https://www.npmjs.com/package/flora-errors)
[![Dependencies](https://img.shields.io/david/godmodelabs/flora-errors.svg)](https://david-dm.org/godmodelabs/flora-errors)

Error definitions for [Flora](https://github.com/godmodelabs/flora).

This module exposes error classes for different types of errors to be able to determine whether an error should be displayed to the client (such as AuthenticationError) or not (internal errors).


Error classes
-------------

### RequestError

Generic request error (the user made an invalid request). HTTP status code `400`, the `message` will be displayed.

### AuthenticationError

The user is either not authenticated, or the credentials are wrong. HTTP status code `401`.

### AuthorizationError

The user may be authenticated, but is not authorized for the requested action. HTTP status code `403`.

### NotFoundError

Either the requested resource could not be found, or a specific entity of the resource. HTTP status code `404`, `message` will be displayed.

### ImplementationError

There is a problem with the implementation, e.g. of one of the resources. This should not happen. HTTP status code `500`.

### DataError

There is a problem with the data retrieved by one of the data sources. HTTP status code `500`.

### ConnectionError

There is a problem with the connection to a data source. May work on retry (e.g. temporary network problems). HTTP status code `503`.


License
-------

[MIT](LICENSE)
