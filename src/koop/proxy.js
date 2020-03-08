const express = require('express');
const http = require('http');
const config = require('config');
const log = require('./logger');

/**
 * Creates an Express Router that proxies requests to the underlying MarkLogic server.  Incoming requests
 * are expected to support the same authentication type (basic or digest) used by the MarkLogic server.
 * 
 * Due to how digest authentication works, the route path has to be the same as that on MarkLogic, 
 * e.g. to proxy requests to v1/resources, the route on this (Express) server should also be v1/resources.
 */ 
function createProxy(routePath) {
  const router = express.Router();
  router.all(routePath, 
    (clientReq, clientRes) => {
      const options = {
        hostname: "unknown",//config.marklogic.connection.host,
        port: config.marklogic.connection.port,
        path: clientReq.originalUrl,
        method: clientReq.method,
        headers: clientReq.headers
      };
      log.info(`Request ${options.method} ${options.path} proxied to MarkLogic at ${options.hostname}:${options.port}`);

      const proxy = http.request(options, (serverRes) => {
        log.info(`MarkLogic response for proxied request ${options.method} ${options.path} returned ${serverRes.statusCode} ${serverRes.statusMessage}`);
        clientRes.writeHead(serverRes.statusCode, serverRes.headers);
        serverRes.pipe(clientRes, { end: true });
      })
      .on('error', (err) => {
        const errorObj = err.code === 'ENOTFOUND' ? { 
          // either MarkLogic is unavailable or hostname cannot be resolved
          statusCode: 504,
          status: 'Gateway Timeout'
        } : {
          // some other issue has occurred
          statusCode: 500,
          status: 'Internal Server Error',
          errorData: err
        };

        clientRes.statusCode = errorObj.statusCode;
        clientRes.statusMessage = errorObj.status;
        clientRes.json(errorObj);
        clientRes.end();
        
        log.warn(`Error '${err.code}' trying to proxy request to ${err.hostname}:${err.port} - sending back response ${errorObj.statusCode} ${errorObj.status}`);
      });

      clientReq.pipe(proxy, { end: true });
    }
  );
  return router;
}

module.exports.create = createProxy;