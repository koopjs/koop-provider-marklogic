# Example Docker setup for Koop with the MarkLogic Koop provider

Assuming you already have MarkLogic installed and configured with [Geo Data Services](https://github.com/marklogic-community/marklogic-geo-data-services), this dockerfile and docker compose file can be used to start up a container that runs a node.js express server configured with Koop and the MarkLogic Koop provider.

## Quick Start

1. Create a `.env` file and set the `MARKLOGIC_HOST`, `MARKLOGIC_PORT`, and `ESRI_AUTH_SECRET` variables for your MarkLogic install
2. If needed, edit `config/docker.json` to override any configuration settings you need to (see below for details on all of the configuration parameters)
3. If using SSL (HTTPS), copy the cert and key that you want to use for the server into `certs/cert.pem` and `certs/key.pem`  
3. Run `docker compose up`
4. Koop should be listening on port 9000 (and 9443 if ssl is enabled). Test it out using a URL to one of your feature services

```
http://localhost:9000/marklogic/rest/services/<service name>/FeatureServer
```
or

```
https://localhost:9000/marklogic/rest/services/<service name>/FeatureServer
```

## Provider Configuration
The `config/default.json` file contains the default configuration paramters for the MarkLogic Koop provider. The default config file looks like this:

```
{
  "logger" : {
    "level" : "info"
  },
  "port" : 9000,
  "ssl": {
    "enabled" : false,
    "port" : 9443,
    "cert" : "certs/cert.pem",
    "key" : "certs/key.pem"
  },
  "marklogic": {
    "connection": {
      "host":     "<marklogic host name>",
      "port":     <marklogic port>,
      "authType": "DIGEST"
    }
  },
  "auth" : {
    "enabled" : false,
    "plugin" : "auth-marklogic-digest-basic",
    "options" : {
      "secret" : "<secret for token generation>",
      "tokenExpirationMinutes" : 60,
      "useHttp" : false
    }
  }
}
```

### logger
`level`: `"debug"|"verbose"|"info"|"warn"|"error"` - the log level for the provider. Valid values are: 

### port 
`<number>` - the port that Koop will listen for HTTP connections on

### ssl
`enabled`: `true|false` - wether or not SSL/HTTPS is enabled. Defaults to `false`

`port`: `<number>` - the port that Koop will listen for HTTPS connections on

`cert`: `"<path to the certificate file>"` - path to the SSL certificate that should be used for the server 

`key`: `"<path to the certificate key file>"` - path to the SSL certificate key that should be used for the server

### marklogic
#### connection
`host`: `"<marklogic host name>"` - the hostname or IP address of the MarkLogic server the provider should connect to. This can be set using the `MARKLOGIC_HOST` environment variable as well.

`port`: `<marklogic port>` - the port on the MarkLogic server the provider should connect to. This can be set using the `MARKLOGIC_PORT` environment variable as well.

`authType`: `"DIGEST|BASIC"` - the username the provider should use when connecting to MarkLogic

### auth
`enabled` : `true|false` - set to `true` to require authentication to access the feature services. Defaults to `true`

`plugin` : `auth-marklogic-digest-basic` - delegate authentication to the MarkLogic backend
#### options
`secret` : `"<secret for token generation>"` - the secret to use when generating tokens for the Esri auth protocol to prevent someone from easily spoofing tokens. This can be set using the `ESRI_AUTH_SECRET` environment variable as well.

`tokenExpirationMinutes` : `60` - the expiration time of the tokens in minutes

`useHttp` : `true|false` - whether or not to allow the auth protocol over http. If this is `false` trying to use auth without https will generate an error. Being able to set this to true is helpful if you have koop running behind a loadbalancer or reverse proxy that is doing SSL termination.

## Authentication
Koop supports the Esri secure feature services API and the MarkLogic Koop Provider supports a number of options for authentication. See [https://github.com/koopjs/koop-provider-marklogic#authentication] for more details. The default configuration for this container is to use MarkLogic-based authentication which passes the user identity and credentials through to MarkLogic for verification.

See [https://koopjs.github.io/docs/usage/authorization](https://koopjs.github.io/docs/usage/authorization) for more details about Koop authentication.

## Container Configuration
As configured, the "koop" container exposes ports 9000 and 9443 externally. If you would like to change those ports, edit the `docker-compose.yml` and change the port mappings before running `docker-compose up` or `docker-compose build`.

## Running the Container
If you make any changes to the files in `config`, `Dockerfile`, or `docker-compose.yml`, rebuild the container by running `docker compose build`

Start the container by running `docker compose up`
