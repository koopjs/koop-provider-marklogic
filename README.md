# Koop Provider MarkLogic
The Koop Provider MarkLogic enables communication with several Esri applications. The provider is a configurable component that allows documents in MarkLogic to be queried and exposed as Esri _Features_ through one or more Esri _Feature Services_.

## Releases
The git tag 1.0.0 is for the tested connector prior to the MarkLogic Geo Data Services code split from this repo.
The git tag 1.1.0 is for the connector after the MarkLogic Geo Data Services split and will therefore need you to make use of an mlBundle to build feature services into your codebase.

---
## Quick Start
1. Configure `/config/<environment>.json`
2. Install `npm install`
3. Environment Setting `export NODE_ENV=<environment>`
3. Start Koop `node server.js`

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

### Test Settings
The test project uses the above node settings for the node server and [gradle properties plugin](https://github.com/stevesaliman/gradle-properties-plugin) to manage environment properties for gradle. Create a `/test/gradle-<environment>.properties` file to specify your environment settings that match your `/config/<environment>.json`.

The following properties can be overriden:

```
mlAppName=<the name of the connector>

mlHost=<host>
mlRestPort=<port>
koopMlUsername=<username>
koopMlPassword=<password>


# Koop server setttings
# Port the feature service will service HTTP requests on
koopPort=<http port>
# Whether or not to enable an HTTPS server as well
koopSSLEnabled=<true|false>
# Port the feature service will service HTTPS requests on
koopSSLPort=<https port>
# The SSL certificate to user for the HTTPS server
koopSSLCert=<path to certificate pem file>
# The SSL certificate key to user for the HTTPS server
koopSSLKey=<path to the certificate public key file>
```

Add the `-PenvironmentName=<environment>` argument when running gradle to utilize your configuration, you can copy the `/test/gradle.properties` default file as a starting point.


---
### Tested Configuration
```
              NPM   6.4.1
             Node   10.2.1
        MarkLogic   9.0-10
MarkLogic Geo Data Services   1.1.0
```

## Running the Connector
The connector uses Koop as the client-facing HTTP/S service. Koop utilizes a Node.js Express server to handle feature service requests.

Koop's settings for this provider are stored in `/config/<environment>.json`.

1. Configure `/config/<environment>.json`
2. Install `npm install`
3. Environment Setting `export NODE_ENV=<environment>`
3. Start Koop `node server.js`

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
http://localhost/marklogic/GDeltExample/FeatureServer

To retrieve the first 10 features from layer 0, use the following link
http://localhost/marklogic/GDeltExample/FeatureServer/0/query?where=1=1&resultRecordCount=10
