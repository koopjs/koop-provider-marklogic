---
layout: default
title: Installation Guide
nav_order: 2
---

The MarkLogic Koop Provider can be used in one of two ways - either via a downloadable zip file that contains a
ready-to-run application that can be configured for your environment, or via a new Koop application created with the
Koop CLI.

Both of these approaches require that you have a MarkLogic application with the
[MarkLogic Geo Data Services (GDS)](https://github.com/marklogic-community/marklogic-geo-data-services) modules
installed. Please see the GDS documentation for adding these modules to your MarkLogic application.

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
