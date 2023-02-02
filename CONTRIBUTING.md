This guide provides instructions on developing and testing changes to this project.

The tests for this project are located in a Gradle project in the `./test` directory. These tests are expected to run 
against the test application that is deployed by the marklogic-geo-data-services project. So you'll first need to 
follow the CONTRIBUTING guide for that repository in order to deploy that test application. 

These tests depend on hitting a Koop server that contains our Koop provider. The Koop server is then expected to 
send requests to the test application from the marklogic-geo-data-services repository. The `config/default.json` file 
defaults to pointing to this configuration. 

To launch the Koop server, do the following:

    npm install
    export NODE_ENV=default
    node server.js

This should result in a Koop server running on port 80 and sending requests to the marklogic-geo-data-services test 
application on port 8096. 

You can then run the tests:

    cd test
    ./gradlew test

You can also run these tests from IntelliJ.
