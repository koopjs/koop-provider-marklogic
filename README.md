# Koop Provider MarkLogic 
The Koop Provider MarkLogic enables communication with several Esri applications. The provider is a configurable component that allows documents in MarkLogic to be queried and exposed as Esri _Features_ through one or more Esri _Feature Services_.

## Releases
The git tag 1.0.0 is for the tested connector prior to the MarkLogic Geo Data Services code split from this repo.
The git tag 1.1.0 is for the connector after the MarkLogic Geo Data Services split and will therefore need you to make use of an mlBundle to build feature services into your codebase.

---
## Quick Start
1. Configure `config/<environment>.json`
2. Install `npm install`
3. Environment Setting `export NODE_ENV=<environment>`
3. Start Koop `node server.js`

## Architecture
The connector has two primary components: 

1. A [Koop](http://koopjs.github.io/) provider plugin
2. A MarkLogic REST resource extension.

The Koop provider plugin runs as a Node.js Express server and makes calls out to the MarkLogic REST resource extension to service queries.

### MarkLogic Koop Provider Plugin
[Koop](http://koopjs.github.io/) parses incoming feature service requests and hands them off to the MarkLogic Koop provider plugin via the defined plugin API function and request object. The plugin is a "pass though" plugin so almost all of the logic is implemented in the REST resource extension running in MarkLogic.

Reponses from the MarkLogic Koop provider plugin are GeoJSON objects (with some minor extensions). The GeoJSON reponses are returned back to Koop and Koop transforms the GeoJSON into the required Esri JSON response objects. The GeoJSON returned from MarkLogic must be in WGS84 coordinate reference system and Koop will do any required transformations if the client requested data in a CRS other than WGS84.

Supports running with HTTP as well as HTTPS.

For more information on how MarkLogic is configured, please see the [marklogic-geo-data-services](https://github.com/marklogic-community/marklogic-geo-data-services) project.

---
## Limitations

### _Feature Service API Coverage_
Not all the capabilities defined in the feature service API spec have been implemented. Most notable is that this is a read-only API and it currently support "generateRenderer" requests. Many of the limitations are documented and tracked as enhancement issues.

### _Security_
The connector currently supports connecting to MarkLogic as a single user. Additional work needs to be done to develop an enhanced security model for the connector and how it will work with Esri tools.

---
## Configuring the Connector
### Settings
The project uses the [config](https://www.npmjs.com/package/config) package to manage configurations. Update the necessary config/FILENAME.json file. You can use the config/default.json as a starting point. To make use of your configuration execute `export NODE_ENV=<environment>` before you run `node server.js`.

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

Create a `gradle-<environment>.properties` file and add the `-PenvironmentName=<environment>` argument when running gradle, you can copy the `/test/gradle.properties` default file as a starting point.


---
## Install the Connector
Installing the connector will create a new MarkLogic app server and modules database, install the server-side MarkLogic code, load the example feature service descriptors into the content database, and load the example TDE templates into the schemas database. Please see the [marklogic-geo-data-services](https://github.com/marklogic-community/marklogic-geo-data-services) project for setting up the MarkLogic components of this setup.

### Tested Configuration

NPM | 6.4.1
Node | 10.2.1
MarkLogic | 9.0-10
[MarkLogic Geo Data Services](https://github.com/prestonmcgowan/marklogic-geo-data-services) | 0.0.6

## Running the Connector
The connector uses Koop as the client-facing HTTP/S service. Koop runs in a Node.js Express server.

Koop gets its settings from the `build/koop/config/default.json` file which is configured according to the Koop properties set in `gradle.properties` or `gradle-<env>.properties`. By default, Koop will listen for HTTP requests on port 80 and HTTPS requests on port 443 as this is the only configuration we could get ESRI Insights to work with. Running on these ports often requires sudo privileges though so you may have to prefix all of these calls with "sudo". If you are not using ESRI Insights though, you can likely just disable SSL and run Koop on a port other than 80. See `gradle-example-connector.properties` for how to do that.

1. __Copy the settings for the Koop Provider MarkLogic__ `gradle installKoop`

2. __Start Koop__ `gradle runKoop`

## Accessing Feature Services

The connector Feature service URLs are structured as follows
```
http://<host>:<port>/marklogic/<service name>/FeatureServer
```
The service name is set via the `info.name` property in the service descriptor.

The example feature service will be available at the following URL:

```
http://<host>:<port>/marklogic/GDeltExample/FeatureServer
```
If you didn't change any of the defaults, you can directly to it with this link
http://localhost:9080/marklogic/GDeltExample/FeatureServer

To retrieve the first 10 features from layer 0, use the following link
http://localhost:9080/marklogic/GDeltExample/FeatureServer/0/query?where=1=1&resultRecordCount=10

## Using the Connector with ArcGIS Pro

MarkLogic is developing an ArcGIS Pro add-in that allows ArcGIS Pro users to directly query a MarkLogic database and save their searches as feature layers.

There are additional setup requirements in order for the add-in to work properly with the connector.

### Search Profiles

Add a `search` section to a feature service descriptor in order to make the feature service available to the add-in and expose one or more search profiles.  Each profile requires a Search Options and a REST transform to be used by the add-in when performing queries and viewing documents.

An example:

```json
"search": {
    "Articles": {   // search profile's name
        "options": "example-gkg-options",           // search options
        "geometryType": "Point",                    // geometry type
        "geoConstraint": "Location",                // geospatial constraint in the search options
        "values": "points",                         // values name in the search options
        "docTransform": "example-gkg-transform",    // transform used when viewing documents
        "schema": "GDeltGKG",                       // TDE schema
        "view": "Article"                           // TDE view
    }
}
```

These can also be found in the Example's feature service descriptors.

### Protecting Layers

The add-in allows users to replace the configuration of a feature layer with their own search parameters.  To prevent a feature layer from being overwritten, you can add a `readOnly` attribute to the feature layer's descriptor and set it to `true`.

### Koop Server Registration

The MarkLogic server needs to be aware of the Koop service when supplying feature layer addresses to the add-in.  Add a `koopHost` property to your gradle properties file and specify the external host name of Koop.

For example:
```
koopHost=koop.mydomain.com
```

The MarkLogic server will use the value in `mlHost` if this property is not supplied.
