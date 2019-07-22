# MarkLogic Esri Connector
The MarkLogic Esri Connector is a configurable component that allows documents in MarkLogic to be queried and exposed as Esri _features_ through one or more Esri _Feature Services_.

For those who don't want to read all the details below, skip right to the [installation instructions](#Install-the-Connector).

## Releases
The git tag 1.0.0 is for the tested connector prior to the MarkLogic Geo Data Services code split from this repo.
The git tag 1.1.0 is for the connector after the MarkLogic Geo Data Services split and will therefore need you to make use of an mlBundle to build feature services into your codebase.

---
## Architecture
The connector has two primary components: 1) A [Koop](http://koopjs.github.io/) __provider plugin__ and 2) A MarkLogic __REST resource extension__. The Koop provider plugin runs inside a Node.js Express server and makes calls out to the MarkLogic REST resource extension to service queries.

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
mlUsername=<username>
mlPassword=<password>

# The name of the database that the connector should run queries against
mlContentDatabaseName=<database name>

# The name of the schemas database to install TDE templates to
# The connector requires your database to have an associated schemas database
mlSchemasDatabaseName=<schemas database name>

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

See the `gradle-example-connector.properties` file for a typical customized property file.

<a name="Feature-Service-Descriptors"></a>
### Feature Service Descriptors
The connector requires a _service descriptor_ document to be created for each ESRI feature service that you want to expose. Service descriptors allow you to define information about service and all of the layers that are available through that service.

Currently, the service descriptors live in the same database as the data that is being queried but that could change. They must be in the `http://marklogic.com/feature-services` collection for the connector to be able to find them.

Service descriptors should be placed in the `config/<config name>/services` directory. They will be automically installed and placed into the required collection when you run the `./gradlew -PenvironmentName=<env name> -PfsConfig=<config name> installServices` command.

See `config/example/services/example-gkg.json` for an example service descriptor.

<a name="TDE-Templates"></a>
### TDE Templates
The connector relies on MarkLogic TDE views to provide data to ESRI feature layers and tables. Follow the [MarkLogic documentation](https://docs.marklogic.com/guide/app-dev/TDE) and the GKG example provided `example/templates/example-gkg.tdex` to create TDE templates to build views for the data you want to expose.

TDE templates should be placed in the `config/<config name>/templates` directory. They will be automically installed when you run the `./gradlew -PenvironmentName=<env name> -PfsConfig=<config name> installServices` command.

---
<a name="Install-the-Connector"></a>
## Install the Connector
Installing the connector will create a new MarkLogic app server and modules database, install the server-side MarkLogic code, load the example feature service descriptors into the content database, load the example TDE templates into the schemas database, copy the Koop code to `build/koop` and do an `npm install` to install the required Koop dependencies.

The project uses the "gradle wrapper" so the `gradlew` or `gradlew.bat` files should be used to run gradle commands.

### _Prerequisites_
1) MarkLogic 9.0-3.1 or later
1) gcc to build the variance native plugin
1) The install script uses https://github.com/srs/gradle-node-plugin to manage the installation and execution of node.js for Koop. If node/npm is not already installed, the plugin will download the most recent version for you. If you already have node/npm installed locally, the plugin will use it. If you want to control what version of node/npm is used, see https://github.com/srs/gradle-node-plugin/blob/master/docs/node.md#configuring-the-plugin for details on how to configure the _npmVersion_ and other properties of the gradle node plugin in your `build.gradle` file.
1) Connection to the internet - If you need to install the connector on a machine that is not connected to the internet, see [Build an Archive to Run in Disconnected Mode](#Build-Disconnected-Archive)

> Note: The native plugin is a C++ program required to support standard deviation and variance aggregation calls for the feature service. gcc is used to build the plugin and then it is installed to MarkLogic via the management API. Because of this _the connector build must run on a machine that is the same platform that the MarkLogic cluster is running on_. If the build is run on a Windows machine, the build and install of the native plugin will be skipped.

<a name="Install-to-Existing"></a>
### Install for an Existing Database
If you have an existing database setup and you would like to use the connector to expose feature services for your data, use the following steps. In these instructions, the content database is called `MyContentDatabase`, the schemas database is called `MySchemasDatabase` and the environment name is `myenv`. Change these to fit your environment.
>WARNING: Install will add TDE templates and geospatial index settings that may trigger a redindex of an existing database if the redindexer is enabled. It is highly recommended that you install the connector and configure the required TDE templates on a small test dataset, verify the functionality and then move to your larger environment with a larger database.

1) Create the `gradle-myenv-connector.properties` and set the MarkLogic port you would like to install the backend service on, your MarkLogic host name, username and password and what port you want the Koop server to run on.
    ```
    mlHost=localhost
    mlRestPort=8095
    mlUsername=admin
    mlPassword=admin
    mlContentDatabaseName=MyContentDatabase
    mlSchemasDatabaseName=MySchemasDatabase
    koopPort=9080
    koopSSLEnabled=false
    ```
    Keep in mind that if these databases do not exist in your cluster, they will be created when the `installConnector` task is run. Also, if you specify the content database and no schemas database, the schemas database will be created an the content database will be changed to use the new schemas database.
1) Run `./gradlew -PenvironmentName=myenv installConnector`
1) Decide on a name for your configuration. Use that as the `<config name>` in the following steps. E.g. `example` is the config name for the example configuration.
1) Create one or more service descriptors as explained in the [Feature Service Descriptors](#Feature-Service-Descriptors) section and put them in the `config/<config name>/services` directory. Use the files in `config/example/services/` as a starting point.
1) Create one or more TDE templates as explained in the [TDE Templates](#TDE-Templates) section and put them in the `config/<config name>/templates` directory. Use the files in `config/example/templates/` as a starting point.
Note: If you have existing views in your MarkLogic database, you may be able to leverage them rather than creating new ones. Be sure to read the [OBJECTIDs Limitation](#OBJECTIDs) though.
1) Install the feature services `./gradlew -PfsConfig=<config name> -PenvironmentName=myenv installServices`
1) See [Running the Connector](#Running-the-Connector) for how to start the Koop server

<a name="Install-with-Example"></a>
### Install the Example as a New Database
If you would like to try out the connector using the included example database, follow these steps to install to the `example` database configuration. This will create an app server, modules database, and content and schemas databases called `esri-example-app-content` and `esri-example-app-schemas` respectively.
> NOTE: The connector project uses ml-gradle so, technically, you could use the connector project to configure databases or other MarkLogic cluster configuration items but it is preferable to manage your overall MarkLogic configuration in a separate ml-gradle (or other tool) project. 
1) Edit the `gradle-example-app.properties` and set the MarkLogic port you would like to install the backend service on, your MarkLogic host name, username and password and what port you want the Koop server to run on.
    ```
    mlAppName=esri-example-app
    mlHost=localhost
    mlRestPort=8095
    mlUsername=admin
    mlPassword=admin
    koopPort=9080
    koopSSLEnabled=false
    ```

__Install the example database__
1) Run `./gradlew -PenvironmentName=example-app mlDeploy` to install the example database
1) Load the example data `./gradlew -PenvironmentName=example-app loadExampleData`

__Install the connector against the example database__
1) Run `./gradlew -PenvironmentName=example-connector installConnector`
1) Install the example feature services `./gradlew -PenvironmentName=example-connector installServices`
Note: This will load TDE templates that define views used by the example feature services. This causes a reindex so check to see that the reindex is complete using the MarkLogic database status admin UI before moving on.
1) You can test that the Koop provider service is working by doing the following
  ```
  ./gradlew -PenvironmentName=example-connector testExampleService
  ```
  This gets all the fields from the first 5 records from the features in layer 0 in the example service.
1) Next, start Koop. See [Running the Connector](#Running-the-Connector) for directions for starting the Koop server
1) Test the feature service by accessing the feature service URL from a browser
* See the top-level service descriptor: `http://<host>:<port>/marklogic/GDeltExample/FeatureServer`
* See the layer 0 descriptor: `http://<host>:<port>/marklogic/GDeltExample/FeatureServer/0`
* Query layer 0 for the first 5 features: `http://<host>:<port>/marklogic/GDeltExample/FeatureServer/0/query?resultRecordCount=5`
* Query layer 0 for the count of features: `http://<host>:<port>/marklogic/GDeltExample/FeatureServer/0/query?returnCountOnly=true`
* Query layer 0 for the count of features where the "domain" is "indiatimes.com": `http://<host>:<port>/marklogic/GDeltExample/FeatureServer/0/query?returnCountOnly=true&where=domain='indiatimes.com'`

### Install or update services
Once the connector is installed, you will want to create new services and deploy configuration updates. The service descriptors and supporting TDE templates live under the `config/<config name>` directory. If you add new services or templates or make updates to existing ones, use the `installServices` command to deploy them.
```
./gradlew -PenvironmentName=<environment name> -PfsConfig=<config name> installServices
```

If your environment and feature service configuration match one to one, you can add the `fsConfig` property to your `gradle-<environment name>.properties` file like is shown in `gradle-example-connector.properties`. You can then run `installServices` without the `fsConfig` property.
```
./gradlew -PenvironmentName=<environment name> installServices
```

<a name="Build-Disconnected-Archive"></a>
### Build an Archive to Run in Disconnected Mode
There are times when you may need to install the connector from a machine that is not connected to the internet. To support this, the gradle build supports a number of tasks you can use to build an archive that has all the dependencies packaged up that you can install from.

> Note: You must build the deployer archive from a machine running the same OS as where you will run the install and the connector from. I.E. if you will be running the connector from a Linux machine, you will need to execute these steps on a Linux machine. 

1) From a machine that has internet connectivity and is the same platform that you will be installing to, run `./gradlew buildMlDeployer`
1) If successful, the `buildMlDeployer` task will have created the `build/MarkLogic-Esri-Connector.zip` zip file. The archive contains all of the dependencies needed to install and run the connector, including the node.js binaries and modules for the platform it was built on. The gradle properties in that zip file are set so installs from the archive will run in "disconnected" mode.
1) Copy `MarkLogic-Esri-Connector.zip` to the machine you want to install on (presumably one that does not have internet connectivity)
1) `unzip MarkLogic-Esri-Connector.zip`
1) `cd MarkLogic-Esri-Connector`
1) Follow the instructions to either [install to an existing database](#Install-to-Existing) or [install with the example](#Install-with-Example)

<a name="Running-the-Connector"></a>
## Running the Connector
The connector uses Koop as the client-facing HTTP/S service. Koop runs in a Node.js Express server and the MarkLogic Koop provider and Koop server Javascript code are placed in the `build/koop` directory.

Koop gets its settings from the `build/koop/config/default.json` file which is configured according to the Koop properties set in `gradle.properties` and `gradle-<env>.properties`. By default, Koop will listen for HTTP requests on port 80 and HTTPS requests on port 443 as this is the only configuration we could get ESRI Insights to work with. Running on these ports often requires sudo privileges though so you may have to prefix all of these calls with "sudo". If you are not using ESRI Insights though, you can likely just disable SSL and run Koop on a port other than 80. See `gradle-example-connector.properties` for how to do that.

To start Koop using an environment configuration, use the following gradle command:
```
./gradlew -PenvironmentName=<env> runKoop
```

Use this to start the example configuration
```
./gradlew -PenvironmentName=example-connector runKoop
```

Alternatively, you can start the Koop server directly using your own install of Node.js.
```
cd build/koop
node server.js
```

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
