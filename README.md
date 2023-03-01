# Koop Provider

The Koop Provider MarkLogic enables communication with several Esri applications. The provider is a configurable component that allows documents in MarkLogic to be queried and exposed as Esri _Features_ through one or more Esri _Feature Services_.

---

## Quick Start

The MarkLogic Koop Provider can be used in one of two ways - either via a downloadable zip file that contains a 
ready-to-run application that can be configured for your environment, or via a new Koop application created with the 
Koop CLI.

**TODO** Both sets of these instructions require a MarkLogic application that has GDS installed. That's described a 
little below, but it really needs to be front and center here as our Koop provider can't do anything without this 
application already existing.

### Using the application zip 

**TODO** This will be updated before the 2.0 release. 

### Using the Koop CLI

The [Koop CLI](https://koopjs.github.io/docs/basics/quickstart), as its documentation states, "provides a rapid and 
easy way to launch a Koop instance". 

To use the Koop CLI to create a Koop project and launch it, first follow its quickstart instructions for installing 
the Koop CLI and creating a new project folder. Then, when the instructions describe adding a Koop provider, run the 
following command:

    koop add provider @koopjs/provider-marklogic

Then, before running `koop server`, you will need to configure the MarkLogic Koop provider so that it knows how to 
connect to your MarkLogic application that includes the 
[marklogic-geo-data-services modules](https://github.com/marklogic-community/marklogic-geo-data-services). To do so, 
open the `config/default.json` file that the Koop CLI created in your project directory and replace its contents 
with the following:

```
{
  "logLevel": "info",
  "port": 8080,
  "marklogic": {
    "connection": {
      "host": "localhost",
      "port": 8000,
      "user": "changeme",
      "password": "changeme",
      "authType": "DIGEST"
    }
  }
}
```

You will need to edit the following values in the above config:

1. `port` should be the port of the REST API server that includes the marklogic-geo-data-services modules in its 
   modules database. 
2. `user` is a MarkLogic user that can access the data that you wish to query via the MarkLogic Koop provider.
3. `password` is the password for the MarkLogic user.

You can also set the top-level `port` to any available port number - it controls the port that the Koop server will 
listen on. 

Once you've saved your changes to the `config/default.json` file, you can run the Koop instance:

    koop serve

You should see console logging indicating that the MarkLogic Koop provider was registered and that the Koop server 
is listening on the port that you chose. You can now connect any application that understands the 
[GeoServices Query API](https://koopjs.github.io/docs/basics/what-is-koop#what-output-formats-are-available) to 
the Koop server and access geospatial data in MarkLogic.


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
