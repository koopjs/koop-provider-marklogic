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
2. A Koop server on port 8092 that requires MarkLogic-based authentication.

To run the tests with both Koop servers active, do the following:

    cd test
    ./gradlew runKoopServers test

You can also run these tests from Intellij. You'll need to run the Koop servers manually. For most of the tests, 
run this first from the root project directory so that you have a Koop server that doesn't require auth and points to 
port 8096 in your MarkLogic instance:

    npm run start-no-auth

If you want to run `MarkLogicAuthTest`, you'll also need to run this in a separate terminal:

    npm run start-ml-auth
