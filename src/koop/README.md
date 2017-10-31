# MarkLogic Koop Provider
The koop provider code is located in this directory. It is a thin layer that passes
calls to a backend MarkLogic service and then hands the responses back to koop.  

## HTTPS
Add certificates to the _ssl_ directory to use for HTTPS if needed.

## Running 
The Marklogic Koop Provider can be run using the following commands after installing
via the instructions in the top-level README.md:

``` npm install ```
``` node server.js ```

