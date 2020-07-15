# Koop Provider

The Koop Provider MarkLogic enables communication with several Esri applications. The provider is a configurable component that allows documents in MarkLogic to be queried and exposed as Esri _Features_ through one or more Esri _Feature Services_.

## Releases

The git tag 1.0.0 is for the tested connector prior to the MarkLogic Geo Data Services code split from this repo.
The git tag 1.1.0 is for the connector after the MarkLogic Geo Data Services split and will therefore need you to make use of an mlBundle to build feature services into your codebase.

---

## Quick Start

1. Configure `/config/<environment>.json`
2. Install `npm install`
3. Environment Setting `export NODE_ENV=<environment>`
4. Start Koop `node server.js`

## Architecture

The connector has two primary components:

1. A [Koop](http://koopjs.github.io/) provider plugin
2. [MarkLogic Geo Data Services](https://github.com/marklogic-community/marklogic-geo-data-services)

The Koop provider plugin runs as a Node.js Express server and makes calls out to the MarkLogic Geo Data Service to service queries.

### MarkLogic Koop Provider Plugin

[Koop](http://koopjs.github.io/) parses incoming feature service requests and hands them off to the MarkLogic Koop provider plugin via the defined plugin API function and request object. The plugin is a "pass though" plugin so almost all of the logic is implemented in the REST resource extension running in MarkLogic.

Reponses from the MarkLogic Koop provider plugin are GeoJSON objects (with some minor extensions). The GeoJSON reponses are returned back to Koop and Koop transforms the GeoJSON into the required Esri JSON response objects. The GeoJSON returned from MarkLogic must be in WGS84 coordinate reference system and Koop will do any required transformations if the client requested data in a CRS other than WGS84.

Supports running with HTTP as well as HTTPS.

### MarkLogic Geo Data Services

For information on how to configure MarkLogic, please see the [marklogic-geo-data-services](https://github.com/marklogic-community/marklogic-geo-data-services) project.

---

## Limitations

### _Feature Service API Coverage_

Not all the capabilities defined in the feature service API spec have been implemented. Most notable is that this is a read-only API and it currently support "generateRenderer" requests. Many of the limitations are documented and tracked as enhancement issues.

### _Security_

The connector currently supports connecting to MarkLogic as a single user. Additional work needs to be done to develop an enhanced security model for the connector and how it will work with Esri tools.

### _Esri Insights_

We could only get Esri Insights to work with HTTP requests on port 80 and HTTPS requests on port 443. Running on these ports often requires sudo privileges. If you are not using Esri Insights though, you can likely just disable SSL and run Koop on a port other than 80. Please see your `/config/<environment>.json` to modify the ports and protocols Koop will utilize.

---

## Configuring the Connector

### Settings

The project uses the node [config](https://www.npmjs.com/package/config) package to manage configurations. Update the necessary `config/FILENAME.json` file. You can use the `config/default.json` as a starting point. To make use of your configuration execute `export NODE_ENV=<environment>` before you run `node server.js`.

### Authentication

The project supports the following authentication strategies:

- None (Default)
- MarkLogic
- File-based

#### No Authentication

All Koop services will be publicly accessible.  Koop will still need valid MarkLogic credentials to communicate with MarkLogic.  See `config/default.json` for an example.

#### MarkLogic Authentication

This uses a _direct authentication_ pattern similar to [Koop-Auth-Direct-File](https://github.com/koopjs/koop-auth-direct-file), with MarkLogic being responsible for authenticating user credentials.  The credentials supplied must match a valid MarkLogic server user account.

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

#### File-based Authentication

This uses the [Koop-Auth-Direct-File](https://github.com/koopjs/koop-auth-direct-file) module for authentication.  Check out its [official project page](https://github.com/koopjs/koop-auth-direct-file) for more information.

To setup, add an `auth` section to your `config/FILENAME.json`, for example:

```json
"auth": {
  "plugin": "auth-direct-file",
  "enabled": true,
  "options": {
    "secret": "7072c433-a4e7-4749-86f3-849a3ed0ee95",
    "tokenExpirationMinutes": 60,
    "identityStore": "tests/user-store.json",
    "useHttp": true
  }
}
```

| Option                 | Description                                   | Value                       | Default value       |
|------------------------|-----------------------------------------------|-----------------------------|---------------------|
| secret                 | Used to verify JSON web tokens                | string                      | auto-generated UUID |
| tokenExpirationMinutes | The validity of tokens in minutes             | Number                      | 60                  |
| identityStore          | Path to JSON file containing list of users    | string                      | none                |
| useHttp                | Allow HTTP for token services                 | boolean                     | false               |

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

## Working with the ArcGIS Pro add-in

The [MarkLogic ArcGIS Pro add-in](https://github.com/marklogic-community/marklogic-arcgis-pro-addin) relies on Koop and the connector to serve as its feature server and proxy for accessing [geo data services](https://github.com/marklogic-community/marklogic-geo-data-services). If you intend to use the add-in with the Koop server, you must explicitly enable the *service proxy* in your `config/<environment>.json` file by adding the following property:

```js
{
  // ...other configuration settings
  "enableServiceProxy": true
}

```

For security purposes, the *service proxy* is **disabled** by default.

## Tests

### Setup Test Environment

1. Ensure you have a local MarkLogic server running.
2. Go to `tests/ml` directory.
3. Run `./gradlew mlDeploy` to deploy the test database.

### Run Tests

1. Go to the project root directory.
2. Run `./run-tests.sh` to run all tests.

### Run Individual Tests

The tests are organized in several test suites:

1. test:default - no authentication tests
2. test:auth-ml - MarkLogic authentication tests
3. test:auth-direct-file - Direct File authentication tests

Use `npm run` to run them individually, for example:

```bash
npm run test:auth-ml
```