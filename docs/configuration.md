---
layout: default
title: Configuration
nav_order: 3
---

The MarkLogic Koop provider uses the [config](https://www.npmjs.com/package/config) package to manage configuration 
data. By default, the provider will read from the `config/default.json` file. To use a different file - for example, 
a file named `config/custom.json` - run `export NODE_ENV=custom` before running a Koop server.

## Logging

The MarkLogic Koop provider uses the [Koop-Logger](https://github.com/koopjs/koop/tree/master/packages/logger)
component for logging. The primary field to configure in a config JSON file is `logLevel`, which is a top-level
field. This field accepts values of `debug`, `info`, `warn`, and `error`.

For additional configuration options in Koop-Logger, please see [its source code](https://github.
com/koopjs/koop/blob/master/packages/logger/src/index.js) as there does not yet appear to be public documentation for
this component yet.

## Authentication

The project supports the following authentication strategies:

- None (Default)
- MarkLogic
- Basic Header

### No Authentication

In this approach, no authentication is required to access the endpoints exposed by the Koop server. The MarkLogic 
Koop provider will still need credentials in order to authenticate with MarkLogic. These credentials can be set in 
the `config/default.json` configuration file.

### MarkLogic Authentication

In this approach, a request to any endpoint exposed by the Koop server requires credentials that will be verified by 
authenticating with MarkLogic. After the user's credentials are verified, a JSON web token is returned to the client 
that is expected to be used for subsequent requests.

To configure this approach, add the following block to your `config/default.json` file:

```json
"auth": {
  "plugin": "auth-marklogic-digest-basic",
  "options": {
    "secret": "7072c433-a4e7-4749-86f3-849a3ed0ee95",
    "tokenExpirationMinutes": 60
  }
}
```

The configuration above depends on the following settings:

- `secret` = used to create and verify a JSON web token; a UUID is suitable for this.
- `tokenExpirationInMinutes` = defines how long a JSON web token is valid for; defaults to 60.

### Basic Header Authentication

In this approach, each request to a Koop endpoint is expected to have a "Basic" authorization header present. The 
HTTP header must have a name of either "Authorization" or "authorization". The value of the header must start with 
"Basic ", followed by a base64-encoded string that can be decoded into a username and password separated by a colon. 
The MarkLogic Koop provider will then use this username and password for authenticating with MarkLogic. 

This approach is useful for tools such as [QGIS](https://www.qgis.org/en/site/), which support sending a Basic 
Authorization header to a feature service. 

To configure this approach, add the following block to your `config/default.json` file:

```
"auth": {
  "plugin": "auth-marklogic-basic-header",
  "clientCacheTtlInSeconds": 10
}
```

The `clientCacheTtlInSeconds` setting captures how long the provider will cache a connection to MarkLogic based on the 
username and password extracted from the Basic Authorization header. 
