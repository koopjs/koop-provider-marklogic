# Koop Provider MarkLogic 
The Koop Provider MarkLogic enables communication with several Esri applications. The provider is a configurable component that allows documents in MarkLogic to be queried and exposed as Esri _features_ through one or more Esri _Feature Services_.

## Releases
The git tag 1.0.0 is for the tested connector prior to the MarkLogic Geo Data Services code split from this repo.
The git tag 1.1.0 is for the connector after the MarkLogic Geo Data Services split and will therefore need you to make use of an mlBundle to build feature services into your codebase.

---
## Quick Start
1. Configure gradle.properties 
2. Install Koop Setting `gradle installKoop`
3. Start Koop `gradle runKoop`

## Architecture
The connector has two primary components: 

1. A [Koop](http://koopjs.github.io/) provider plugin
2. A MarkLogic REST resource extension.

The Koop provider plugin runs inside a Node.js Express server and makes calls out to the MarkLogic REST resource extension to service queries.

### MarkLogic Koop Provider Plugin
[Koop](http://koopjs.github.io/) parses incoming feature service requests and hands them off to the MarkLogic Koop provider plugin via the defined plugin API function and request object. The plugin is a "pass though" plugin so almost all of the logic is implemented in the REST resource extension running in MarkLogic.

Reponses from the MarkLogic Koop provider plugin are GeoJSON objects (with some minor extensions). The GeoJSON reponses are returned back to Koop and Koop transforms the GeoJSON into the required Esri JSON response objects. The GeoJSON returned from MarkLogic must be in WGS84 coordinate reference system and Koop will do any required transformations if the client requested data in a CRS other than WGS84.

Supports running with HTTP as well as HTTPS.

### REST Resource Extension
The REST resource extension is the backend for the MarkLogic Koop provider. It can be thought of as the __Koop Provider Service__, servicing all the calls from the MarkLogic Koop provider running in the Node.js Express server. It handles geospatial queries, SQL WHERE clauses and aggregations. Responses are sent back to the MarkLogic Koop provider as extended GeoJSON with all coordinates in the WGS84 CRS.

#### Service Descriptors
The Koop provider service requires one or more _service descriptors_ to exist in the database the connector has been congifured to use. Service descriptors tell the Koop provider service the details about ESRI Feature Services that it can handle requests for. Service descriptors list the ESRI layers that can be queried and connect those layers to the MarkLogic TDE _views_ that will be used.

Each layer in a service descriptor can include a _bounding query_. The bounding query is a CTS query serialized as JSON. When servicing queries, the Koop provider service reads the bounding query from the layer in the service descriptor and applies it at the begining of the Optic pipelines. This limits the results from the Optic pipelines to only those from documents that match the bounding query. This is helpful in limiting the scope of the features queried and returned by each layer.

Service descriptors and their layers can be programatically controlled so additional tools or user interfaces can create and control the descriptors on the fly, dynamically controlling the features that are displayed when ESRI tools access those layers.

#### Views
The Koop provider service uses MarkLogic views defined by TDE templates (technically it could use lexicon-based views as well but this hasn't been tested). Each layer in a service descriptor tells the Koop provider service which view to use when handling queries for that layer. 

#### Queries
The Koop provider service uses the Optic API to process all queries. Esri Feature Service requests (see [the Feature Service API](https://resources.arcgis.com/en/help/rest/apiref/featureserver.html) for details) are either requests for service or layer metadata or queries against a specific layer. Queries are translated into CTS and Optic queries and handled by one of two pipelines: feature queries or aggregations.

ESRI Feature Services support SQL WHERE clauses to query features (see [Query - Feature Service](https://resources.arcgis.com/en/help/rest/apiref/fsquery.html) for details). To support this, the Koop provider service parses SQL WHERE clauses using [Flora SQL Parser](https://github.com/godmodelabs/flora-sql-parser) and transforms the resulting AST into Optic queries.

The Koop provider service supports geospatial queries by translating the incoming ESRI geometry into MarkLogic geo queries and applying the geo query at the begining of the Optic pipelines.

#### Aggregations
The Koop provider service supports ESRI Feature Service aggregations (see the "groupByFieldsForStatistics" and "outStatistics" parameter descriptions in the [Query - Feature Service API](https://resources.arcgis.com/en/help/rest/apiref/fsquery.html)). These are handled by translating the ESRI aggregation requests into Optic aggregation operators.

---
## Limitations

### _Feature Service API Coverage_
Not all the capabilities defined in the feature service API spec have been implemented. Most notable is that this is a read-only API for now and it currently does not support the "time" query parameter nor does it support "generateRenderer" requests. Many of the limitations are documented and tracked as enhancement issues already.

<a name="OBJECTIDs"></a>
### _OBJECTIDs_
The features returned by the Koop provider service should contain a field named `OBJECTID` or a field that can be identified as the OBJECTID in to the ESRI Feature Service clients. In order to support pagination across large result sets, the OBJECTIDs need to be increasing numbers. They don't have to be continguous to but should be fairly evenly distributed between the minimum and maximum values.

OBJECTIDs can either be added to the documents and then exposed as a column in a TDE view or computed by an expression in a TDE template column using using existing field(s) in the documents.

### _Requires a Native Plugin for STDEV/VAR_
STDEV and VAR are not currently supported in Optic API so a native plugin is included to support these aggregate functions.

### _Security_
The connector currently supports connecting to MarkLogic as a single user. Additional work needs to be done to develop an enhanced security model for the connector and how it will work with Esri tools.

---
## Configuring the Connector
### Settings
The project uses [gradle properties plugin](https://github.com/stevesaliman/gradle-properties-plugin) to manage properties for different environments. Create a `gradle-<environment>.properties` file in the base project directory to specify your environment settings.

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

To configure an environment other than **local**, create a ```gradel-<env>.properties``` file and add the ```-PenvironmentName=<env>``` argument when running gradle.


---
## Install the Connector
Installing the connector will create a new MarkLogic app server and modules database, install the server-side MarkLogic code, load the example feature service descriptors into the content database, load the example TDE templates into the schemas database, copy the Koop code to `build/koop` and do an `npm install` to install the required Koop dependencies.

The project uses the "gradle wrapper" so the `gradlew` or `gradlew.bat` files should be used to run gradle commands.

### _Prerequisites_
1. MarkLogic 9.0-9 or later
2. The install script uses https://github.com/srs/gradle-node-plugin to manage the installation and execution of node.js for Koop. If node/npm is not already installed, the plugin will download the most recent version for you. If you already have node/npm installed locally, the plugin will use it. If you want to control what version of node/npm is used, see https://github.com/srs/gradle-node-plugin/blob/master/docs/node.md#configuring-the-plugin for details on how to configure the _npmVersion_ and other properties of the gradle node plugin in your `build.gradle` file.
3. Connection to the internet - If you need to install the connector on a machine that is not connected to the internet, see [Build an Archive to Run in Disconnected Mode](#Build-Disconnected-Archive)
4. Deployed project with the [MarkLogic Geo Data Services](https://github.com/prestonmcgowan/marklogic-geo-data-services) capability.

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
