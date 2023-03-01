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

## Testing this locally

The ./examples/local-publish-testing project can be used to test publishing this project's npm package to a local 
registry and then using it in an example project built with [the Koop CLI](https://koopjs.github.
io/docs/basics/quickstart). 

You'll first need a local npm registry. For that, [install verdaccio](https://verdaccio.org/):

    npm install -g verdaccio

Then run it:

    verdaccio

You can go to http://localhost:4873 to verify that it's running correctly. And you'll need to follow the first
instruction there, which is to add a user:

   npm adduser --registry http://localhost:4873

You can add any username/password you want, and the email does not need to be a real address.

Now, publish this project's npm package to the local verdaccio registry (it's okay if you forget to specify the 
registry; the operation will fail because you're not authorized to publish to the real npm):

    npm publish --registry http://localhost:4873

After doing so, http://localhost:4873/-/web/detail/@koopjs/provider-marklogic should now show the package that you just
published. 

You can now run the local-publish-testing project. You'll first need to install the 
[Koop CLI](https://koopjs.github.io/docs/basics/quickstart) if you have not already:

    npm install -g @koopjs/cli

Now run the local-publish-testing project using the Koop CLI and the package you just published locally:

    cd examples/local-publish-testing
    npm install
    koop serve

This should launch a Koop server on port 8080. You can verify this via the following URLs:

- http://localhost:8080/ = should display "Welcome to Koop!"
- http://localhost:8080/marklogic/rest/services/GDeltExample/FeatureServer/0 = should return a JSON feature service 
  descriptor


