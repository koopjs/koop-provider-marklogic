This guide provides instructions on developing and testing changes to this project.

First, before doing anything, run:

    npm install

## Running the default Koop server for ad hoc testing

If you'd like to do some manual ad hoc testing, you should first clone the marklogic-geo-data-services project and read
the CONTRIBUTING guide there for instructions on deploying the examples/sample-project application. That application 
installs an app server in your MarkLogic instance on port 8095. You can then run the default Koop server (which does not 
require any authentication):

    npm run start

This will expose a Koop server at http://localhost:80 . 

## Running the automated tests

The tests for this project are located in a Gradle project in the `./test` directory. These tests are expected to run 
against the test application that is deployed by the marklogic-geo-data-services project. So you'll first need to 
follow the CONTRIBUTING guide for that repository in order to deploy the test application in that repository. 

These tests depend on hitting 2 different Koop servers:

1. A Koop server on port 8090 that doesn't require authentication.
2. A Koop server on port 8091 that requires MarkLogic-based authentication.

To run the tests with both Koop servers active, do the following:

    cd test
    ./gradlew runKoopServers test

You can also run these tests from Intellij. You'll need to run the Koop servers manually. For most of the tests, 
run this first from the root project directory so that you have a Koop server that doesn't require auth and points to 
port 8096 in your MarkLogic instance:

    npm run start-for-tests

## Testing the application zip

As part of each release, we publish to Github a tgz file that a user can download, extract, configure, and run, with a 
goal of not having to touch any code. The user is always free to follow the [Koop quickstart directions]
(https://koopjs.github.io/docs/basics/quickstart) and instead embed our Koop provider. But the benefit of this 
application zip is that a user can quickly try out our Koop provider without having to worry about any of the mechanics
of coding a Koop server. 

To build and test our application zip, first run:

    npm pack

This will create a `koopjs-provider-marklogic-(version).tgz` file in the root project directory. This will include 
all dependencies due to the presence of `"bundledDependencies":true` in the `package.json` file. And it excludes all the
files listed in the `.npmignore` file in the root project directory.

Next, unzip and extract the contents of the file (replacing the version in the filename as needed):

    tar zxvf koopjs-provider-marklogic-2.0-SNAPSHOT.tgz

This will create a directory named `package` that contains the source code and dependencies for our Koop provider and a
runnable Koop server. To run that server, do the following:

    cd package
    npm run start

This will connect to MarkLogic based on the contents of the `./package/config/default.json` file - feel free to adjust
that file as needed before running `npm run start`.
