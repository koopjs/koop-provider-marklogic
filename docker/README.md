# Example Docker setup for Koop with the MarkLogic Koop provider

Assuming you already have MarkLogic installed and configured with [Geo Data Services](https://github.com/marklogic-community/marklogic-geo-data-services), this dockerfile and docker compose file can be used to start up a container that runs a node.js express server configured with Koop and the MarkLogic Koop provider.

## Quick Start

1. Edit `provider-config.json` and set the connection information that the provider should use to communicate with MarkLogic as well as ports Koop will run on, whether or not you want to enable SSL, and whether or not you want authentication enabled (see below for details on all of the configuration parameters)
2. If using SSL (HTTPS), copy the cert and key that you want to use for the server into `certs/cert.pem` and `certs/key.pem`  
3. If using authentication, edit the `user-store.json` file to add the users and credentials you want to enable
4. Run `docker compose up`
5. Koop should be listening on port 9000 (and 9443 if ssl is enabled). Test it out using a URL to one of your feature services

```
http://localhost:9000/marklogic/<service name>/FeatureServer
```
or
```
https://localhost:9443/marklogic/<service name>/FeatureServer
```

## Provider Configuration
The `provider-config.json` file contains the configuration paramters for the MarkLogic Koop provider. The example config file looks like this:

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
      "user":     "<user name>",
      "password": "<password>",
      "authType": "DIGEST"
    }
  },
  "auth" : {
    "enabled" : false,
    "plugin" : "auth-direct-file",
    "options" : {
      "secret" : "<secret for token generation>",
      "identityStore" : "auth/user-store.json",
      "tokenExpirationMinutes" : 60,
      "useHttp" : true
    }
  }
}
```

### logger
`level`: `"debug"|"verbose"|"info"|"warn"|"error"` - the log level for the provider. Valid values are: 

### port 
`<number>` - the port that Koop will listen for HTTP connections on

### ssl
`enabled`: `true|false` - wether or not SSL/HTTPS is enabled

`port`: `<number>` - the port that Koop will listen for HTTPS connections on

`cert`: `"<path to the certificate file>"` - path to the SSL certificate that should be used for the server 

`key`: `"<path to the certificate key file>"` - path to the SSL certificate key that should be used for the server

### marklogic
#### connection
`host`: `"<marklogic host name>"` - the hostname or IP address of the MarkLogic server the provider should connect to

`port`: `<marklogic port>` - the port on the MarkLogic server the provider should connect to

`user`: `"<user name>"` - the username the provider should use when connecting to MarkLogic

`password`: `"<password>"` - the password the provider should use when connecting to MarkLogic

`authType`: `"DIGEST|BASIC"` - the username the provider should use when connecting to MarkLogic

### auth
`enabled` : `true|false`

`plugin` : `auth-direct-file`
#### options
`secret` : `"<secret for token generation>"` - the secret to use when generating tokens

`identityStore` : `"auth/user-store.json"` - the path and filename of the identity store where usernames and passwords are kept

`tokenExpirationMinutes` : `60` - the expiration time of the tokens in minutes

`useHttp` : `true|false` - whether or not to allow the auth protocol over http. If this is `false` trying to use auth without https will generate an error. Being able to set this to true is helpful if you have koop running behind a loadbalancer or reverse proxy that is doing SSL termination.

## Authentication
Koop supports the Esri secure feature services API and the MarkLogic Koop Provider currently supports the [koop-auth-driect](https://github.com/koopjs/koop-auth-direct-file) plugin for authentication.

To enable authentication, set the `auth.enabled` property to `true` in the `provider-config.json`. 

To add users, edit the `user-store.json` file.

Run `docker compose build` to rebuild the container.

See [https://koopjs.github.io/docs/usage/authorization](https://koopjs.github.io/docs/usage/authorization) for more details about Koop authentication.

## Container Configuration
As configured, the "koop" container exposes ports 9000 and 9443 externally. If you would like to change those ports, edit the `docker-compose.yml` and change the port mappings before running `docker-compose up` or `docker-compose build`.
