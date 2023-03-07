---
layout: default
title: Configuration
nav_order: 3
---

TODO This needs to be revised before the 2.0 release.

### Settings

The project uses the node [config](https://www.npmjs.com/package/config) package to manage configurations. Update the necessary `config/FILENAME.json` file. You can use the `config/default.json` as a starting point. To make use of your configuration execute `export NODE_ENV=<environment>` before you run `node server.js`.

### Logging

The MarkLogic Koop provider uses the [Koop-Logger](https://github.com/koopjs/koop/tree/master/packages/logger)
component for logging. The primary field to configure in a config JSON file is `logLevel`, which is a top-level
field. This field accepts values of `debug`, `info`, `warn`, and `error`.

For additional configuration options in Koop-Logger, please see [its source code](https://github.
com/koopjs/koop/blob/master/packages/logger/src/index.js) as there does not yet appear to be public documentation for
this component yet.

### Authentication

The project supports the following authentication strategies:

- None (Default)
- MarkLogic

#### No Authentication

All Koop services will be publicly accessible.  Koop will still need valid MarkLogic credentials to communicate with MarkLogic.  See `config/default.json` for an example.

#### MarkLogic Authentication

This uses a _direct authentication_ pattern, with MarkLogic being responsible for authenticating user credentials.  The credentials supplied must match a valid MarkLogic server user account.

To setup, add an `auth` section to your `config/FILENAME.json`, for example:

```json
"auth": {
  "plugin": "auth-marklogic-digest-basic",
  "enabled": true,
  "options": {
    "secret": "7072c433-a4e7-4749-86f3-849a3ed0ee95",
    "tokenExpirationMinutes": 60
  }
}
```

| Option                 | Description                                   | Value                       | Default value       |
|------------------------|-----------------------------------------------|-----------------------------|---------------------|
| secret                 | Used to verify JSON web tokens                | string                      | auto-generated UUID |
| tokenExpirationMinutes | The validity of tokens in minutes             | Number                      | 60                  |

## Running the Connector

The connector uses Koop as the client-facing HTTP/S service. Koop utilizes a Node.js Express server to handle feature service requests.

Koop's settings for this provider are stored in `/config/<environment>.json`.

1. Configure `/config/<environment>.json`
2. Install `npm install`
3. Environment Setting `export NODE_ENV=<environment>`
4. Start Koop `node server.js`

## Accessing Feature Services

The connector Feature service URLs are structured as follows

```
http://<host>:<port>/marklogic/<service name>/FeatureServer
```

The service name is set via the `info.name` property in the service descriptor of the marklogic-geo-data-services configuration.

The example feature service will be available at the following URL (if deployed):

```
http://<host>:<port>/marklogic/GDeltExample/FeatureServer
```

If you didn't change any of the defaults, you can use this link to test access
<http://localhost/marklogic/GDeltExample/FeatureServer>

To retrieve the first 10 features from layer 0, use the following link
<http://localhost/marklogic/GDeltExample/FeatureServer/0/query?where=1=1&resultRecordCount=10>
