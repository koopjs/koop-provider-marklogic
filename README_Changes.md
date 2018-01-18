# MarkLogic Esri Connector
The MarkLogic Esri Connector is a configurable component that allows documents in MarkLogic to be queried and exposed as Esri _features_ through one or more Esri _Feature Services_.

For those who don't want to read all the details below, skip right to the [installation instructions](#Install-the-Connector).

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

### _GeoJSON_
Documents must contain GeoJSON to be able to return GeoJSON feature data from the connector
Documents must contain GeoJSON to support geospatial queries using the hard-coded geospatial path indexes
```
'//geometry[type = "MultiPoint"]//array-node("coordinates")',
'//geometry[type = "Point"]//array-node("coordinates")'
```
### _Geospatial Queries_
Only supports point geospatial queries right now but can be easily extended to support regions (placeholder code in place) 

<a name="OBJECTIDs"></a>
### _OBJECTIDs_
The features returned by the Koop provider service should contain a field named `OBJECTID` or a field that can be identified as the OBJECTID in to the ESRI Feature Service clients. In order to support pagination across large result sets, the OBJECTIDs need to be increasing numbers. They don't have to be continguous to but should be fairly evenly distributed between the minimum and maximum values.

OBJECTIDs can either be added to the documents and then exposed as a column in a TDE view or computed by an expression in a TDE template column using using existing field(s) in the documents.

### _Requires a Native Plugin for STDEV/VAR_
STDEV and VAR are not currently supported in Optic API so a native plugin is included to support these aggregate functions.

### _Testing_
A suite of tests is currently under development.

### _Security_
The connector currently supports connecting to MarkLogic as a single user. Additional work needs to be done to develop an enhanced security model for the connector and how it will work with Esri tools.

---
## Configuring the Connector
### Settings
The project uses [gradle properties plugin](https://github.com/stevesaliman/gradle-properties-plugin) to manage properties for different environments. Create a `gradle-local.properties` file in the base project directory to specify your local environment settings.

The following properties can be overriden:
```
mlAppName=esri-connector

mlHost=localhost
mlRestPort=8095
mlUsername=admin
mlPassword=admin

# The name of the database that the connector should run queries against
mlContentDatabaseName=<database name>

# The name of the schemas database to install TDE templates to
# The connector requires your database to have an associated schemas database
mlSchemasDatabaseName=<schemas database name>

# Koop server setttings
# Port the feature service will service HTTP requests on
koopPort=80
# Whether or not to enable an HTTPS server as well
koopSSLEnabled=true
# Port the feature service will service HTTPS requests on
koopSSLPort=443
# The SSL certificate to user for the HTTPS server
koopSSLCert=ssl/cert.pem
# The SSL certificate key to user for the HTTPS server
koopSSLKey=ssl/key.pem
```

To configure an environment other than **local**, create a ```gradel-<env>.properties``` file and add the ```-PenvironmentName=<env>``` argument when running gradle.

See the `gradle-example.properties` file for a typical customized property file.

<a name="Feature-Service-Descriptors"></a>
### Feature Service Descriptors
The connector requires a _service descriptor_ document to be created for each ESRI feature service that you want to expose. Service descriptors allow you to define information about service and all of the layers that are available through that service.

Currently, the service descriptors live in the same database as the data that is being queried but that could change. They must be in the `http://marklogic.com/feature-services` collection for the connector to be able to find them.

Service descriptors should be placed in the `config/feature-services` directory. They will be automically installed and placed into the required collection when you run the `./gradlew installServices` command.

See `example/feature-services/example-gkg.json` for an example service descriptor.

<a name="TDE-Templates"></a>
### TDE Templates
The connector relies on MarkLogic TDE views to provide data to ESRI feature layers and tables. Follow the [MarkLogic documentation](https://docs.marklogic.com/guide/app-dev/TDE) and the GKG example provided `example/templates/example-gkg.tdex` to create TDE templates to build views for the data you want to expose.

TDE templates should be placed in the `config/templates` directory. They will be automically installed when you run the `./gradlew installServices` command.

---
<a name="Install-the-Connector"></a>
## Install the Connector
Installing the connector will create a new MarkLogic app server and modules database, install the server-side MarkLogic code, load the example feature service descriptors into the content database, load the example TDE templates into the schemas database, copy the Koop code to `build/koop` and do an `npm install` to install the required Koop dependencies.

The project uses the "gradle wrapper" so the `gradlew` or `gradlew.bat` files should be used to run gradle commands.

### _Prerequisites_
1) MarkLogic 9.0-3.1 or newer
1) gcc to build the variance native plugin
1) This installation is pre-configured to use a script that uses the gradle node plugin (https://github.com/srs/gradle-node-plugin) to manage the installation and execution of node.js for Koop, so you don't need to install it. However, If you have npm installed locally (npm local vs. global install; https://www.youtube.com/watch?time_continue=167&v=JDSfqFFbNYQ), it should use that as the build file does not specify a _npmVersion_. See https://github.com/srs/gradle-node-plugin/blob/master/docs/node.md#configuring-the-plugin for details on how to configure the node section of your build.gradle file to use your desired version of node.js and npm.
1) Connection to the internet - If you need to install the connector on a machine that is not connected to the internet, see [Build an Archive to Run in Disconnected Mode](#Build-Disconnected-Archive)

> Note: The native plugin is a C++ program required to support standard deviation and variance aggregation calls for the feature service. gcc is used to build the plugin and then it is installed to MarkLogic via the management API. Because of this _the connector build must run on a machine that is the same platform that the MarkLogic cluster is running on_. I.E. you cannot build the connector from a Windows machine and install to MarkLogic running on Linux machines. If you don't need the standard deviation and variance aggregation functions, you can modify `build.gradle` to remove the dependencies on the `buildPlugin` and `installPlugin` tasks from the `buildConnector` and `installConnector` tasks.

<a name="Install-to-Existing"></a>
### Install for an Existing Database
If you have an existing database setup and you would like to use the connector to expose feature services for your data, use the following steps. In these instructions, the content database is called `MyContentDatabase`, the schemas database is called `MySchemasDatabase` and the environment name is `myenv`. Change these to fit your environment.
>WARNING: Install will add TDE templates and geospatial index settings that may trigger a redindex of an existing database if the redindexer is enabled. It is highly recommended that you install the connector and configure the required TDE templates on a small test dataset, verify the functionality and then move to your larger environment with a larger database.

1) Create the `gradle-myenv.properties` and set the MarkLogic port you would like to install the backend service on, your MarkLogic host name, username and password and what port you want the Koop server to run on.
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
1) Create one or more service descriptors as explained in the [Feature Service Descriptors](#Feature-Service-Descriptors) section and put them in the `config/feature-services` directory. Use the files in `example/feature-services/` as a starting point.
1) Create one or more TDE templates as explained in the [TDE Templates](#TDE-Templates) section and put them in the `config/templates` directory. Use the files in `example/features-services/` as a starting point.
Note: If you have existing views in your MarkLogic database, you may be able to leverage them rather than creating new ones. Be sure to read the [OBJECTIDs Limitation](#OBJECTIDs) though.
1) Install the feature services `./gradlew -PenvironmentName=myenv installServices`
1) See [Running the Connector](#Running-the-Connector) for how to start the Koop server

<a name="Install-with-Example"></a>
### Install the Example as a New Database
If you would like to try out the connector using the included example, follow these steps to install to the `example` environment. In addition to the modules database, this will also create new content and schemas databases called `esri-connector-example-content` and `esri-connector-example-schemas` respectively.
> NOTE: The connector project uses ml-gradle so, technically, you could use the connector project to configure databases or other MarkLogic cluster configuration items but it is preferable to manage your overall MarkLogic configuration in a separate ml-gradle (or other tool) project. 
1) Edit the `gradle-example.properties` and set the MarkLogic port you would like to install the backend service on, your MarkLogic host name, username and password and what port you want the Koop server to run on.
    ```
    mlHost=localhost
    mlRestPort=8095
    mlUsername=admin
    mlPassword=admin
    mlContentDatabaseName=esri-connector-example-content
    mlSchemasDatabaseName=esri-connector-example-schemas
    koopPort=9080
    koopSSLEnabled=false
    ```
1) Run `./gradlew -PenvironmentName=example installConnector`
1) Copy the example feature service descriptor from `example/feature-services/example-gkg.json` to the `config/feature-services` directory
1) Copy the example TDE template from `example/templates/example-gkg.tdex` to the `config/templates` directory
1) Install the feature services `./gradlew -PenvironmentName=example installServices`
1) Load the example data `./gradlew -PenvironmentName=example loadExampleData`
1) You can test that the Koop provider service is working by doing the following
  ```
  ./gradlew testExampleService
  ```
  This gets all the fields from the first 5 records from the features in layer 0 in the example service.
1) See [Running the Connector](#Running-the-Connector) for how to start the Koop server
1) You can test the feature service by access the feature service URL from a browser
* See the top-level service descriptor: `http://<host>/marklogic/GDeltGKG/FeatureServer`
* See the layer 0 descriptor: `http://<host>/marklogic/GDeltGKG/FeatureServer/0`
* Query layer 0 for the first 5 features: `http://<host>/marklogic/GDeltGKG/FeatureServer/0/query?resultRecordCount=5`
* Query layer 0 for the count of features: `http://<host>/marklogic/GDeltGKG/FeatureServer/0/query?returnCountOnly=true`
* Query layer 0 for the count of features where the "domain" is "indiatimes.com": `http://<host>/marklogic/GDeltGKG/FeatureServer/0/query?returnCountOnly=true&where=domain='indiatimes.com'`

<a name="Build-Disconnected-Archive"></a>
### Build an Archive to Run in Disconnected Mode
There are times when you may need to install the connector from a machine that is not connected to the internet. To support this, the gradle build supports a number of tasks you can use to build an archive that has all the dependencies packaged up that you can install from.

> Note: You must build the deployer archive from a machine running the same OS as where you will run the install and the connector from. I.E. if you will be running the connector from a Linux machine, you will need to 

1) From a machine that has internet connectivity and is the same platform that you will be installing to, run `./gradlew buildMlDeployer`
1) If successful, the `buildMlDeployer` task will have created the `build/MarkLogic-Esri-Connector.zip` zip file. The archive contains all of the dependencies needed to install and run the connector, including the node.js binaries and modules for the platform it was built on. The gradle properties in that zip file are set so installs from the archive will run in "disconnected" mode.
1) Copy `MarkLogic-Esri-Connector.zip` to the machine you want to install on (presumably one that does not have internet connectivity)
1) `unzip MarkLogic-Esri-Connector.zip`
1) `cd MarkLogic-Esri-Connector`
1) Follow the instructions to either [install to an existing database](#Install-to-Existing) or [install with the example](#Install-with-Example)

<a name="Running-the-Connector"></a>
## Running the Connector
The connector uses Koop as the client-facing HTTP/S service. Koop runs in a Node.js Express server and the MarkLogic Koop provider and Koop server Javascript code are placed in the `build/koop` directory.

Koop gets its settings from the `build/koop/config/default.json` file which is configured according to the Koop properties set in `gradle.properties` and `gradle-<env>.properties`. By default, Koop will listen for HTTP requests on port 80 and HTTPS requests on port 443 as this is the only configuration we could get ESRI Insights to work with. Running on these ports often requires sudo privileges though so you may have to prefix all of these calls with "sudo". If you are not using ESRI Insights though, you can likely just disable SSL and run Koop on a port other than 80. See `gradle-example.properties` for how to do that.

Start Koop for your local environment with the following gradle command:
```
./gradlew runKoop
```

To start Koop using a different environment configuration, use the following gradle command:
```
./gradlew -PenvironmentName=<env> runKoop
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
http://<host>:<port>/marklogic/GDeltGKG/FeatureServer
```
or
```
https://<host>:<port>/marklogic/GDeltGKG/FeatureServer
```

