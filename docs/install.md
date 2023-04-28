---
layout: default
title: Installation Guide
nav_order: 2
---

The MarkLogic Koop Provider can be used in one of two ways - either via a downloadable zip file that contains a
ready-to-run application that can be configured for your environment, or via a new Koop application created with the
Koop CLI.

Both of these approaches require that you have a MarkLogic application with
[MarkLogic Geo Data Services (GDS)](https://github.com/marklogic/marklogic-geo-data-services) installed. 
Please see 
[the GDS documentation](https://marklogic.github.io/marklogic-geo-data-services/) for instructions on 
adding GDS to your MarkLogic application. 

## Using the application zip

Starting with the 2.0.0 release, you can download a zip that provides a starting point for a Koop server that only 
requires configuration for connecting to your MarkLogic application.  

To use this zip to run a Koop server, perform the following steps:

1. Download the zip for [the desired release](https://github.com/koopjs/koop-provider-marklogic/releases).
2. Extract the zip - e.g. `tar zxvf koopjs-provider-marklogic-2.0.0.tgz`. 
3. From the directory in which you extracted the zip, run `cd package`.
4. Edit the MarkLogic connection information in the `config/default.json` file so that it will connect to your 
   MarkLogic application that has GDS installed in it.
5. Run `npm install` to install the application's dependencies.
6. Run `npm start` to start the Koop server with the MarkLogic Koop provider enabled.

The Koop server is configured to run on port 80 by default. To verify that it is running properly and able to 
connect to your MarkLogic application, try accessing the following URLs:

- <http://localhost/marklogic/rest/info> should return a JSON object described the Koop server.
- <http://localhost/marklogic/rest/services/SERVICE NAME/FeatureServer> should return information about a service 
  descriptor in your MarkLogic application; replace "SERVICE_NAME" with the name of any service descriptor. 

### Enabling SSL 

The application zip provides an example of how SSL can be enabled on the Koop application. To enable SSL, perform 
the following steps:

1. In `config/default.json`, change the values of `key` and `cert` in the `ssl` block to point to your private key 
   and server certificate files.
2. In `config/default.json`, change the value of `enabled` in the `ssl` block to `true`.
3. Run `npm start`.

To verify that the Koop server is listening on port 443 and requiring HTTPS, try accessing the following URL:

- <https://localhost/marklogic/rest/info>

### Further customizing the application 

The contents of the application zip are intended to provide a functional starting point for a Koop application with 
the MarkLogic Koop provider installed. Please see [the Koop docs](https://koopjs.github.io/docs/basics/what-is-koop) 
for more information on how to customize a Koop application.


## Using the Koop CLI

The [Koop CLI](https://koopjs.github.io/docs/basics/quickstart), as its documentation states, "provides a rapid and
easy way to launch a Koop instance".

To use the Koop CLI to create a Koop project and launch it, first follow its quickstart instructions for installing
the Koop CLI and creating a new project folder. Then, when the instructions describe adding a Koop provider, run the
following command:

    koop add provider @koopjs/provider-marklogic

Then, before running `koop server`, you will need to configure the MarkLogic Koop provider so that it knows how to
connect to your MarkLogic application that includes the
[marklogic-geo-data-services modules](https://github.com/marklogic/marklogic-geo-data-services). To do so,
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
