/*
 * Copyright © 2017 MarkLogic Corporation
 */

// See https://koopjs.github.io/docs/development/provider/model for the spec for this module.

const normalizeGeometryFilter = require('@koopjs/winnow/src/normalize-query-options/geometry-filter');
const log = require('./logger');

 //Important to require dbClientManager as exactly "./dbClientManager"
//to get the node module cache to make this be a singleton.
const dbClientManager = require("./dbClientManager");
const MarkLogicQuery = require('./query');

function MarkLogic () {}

MarkLogic.prototype.getData = function getData (req, callback) {
    log.info("req.url:", req.url);

    log.debug("req.params:", req.params);
    log.debug("req.query: ", req.query);

    // fix typing and parse JSON
    // this actually modifies the underlying request object so downstream
    // koop functions will use the modified values
    coerceQuery(req.query);

    // returnGeometry should default to true
    if (!req.query.hasOwnProperty('returnGeometry')) {
      req.query.returnGeometry = true;
    }

    // convert incoming geometry into GeoJSON in WGS84
    const geometry = normalizeGeometryFilter(req.query);

    if (req.query && req.query.geometry) {
    	req.query.extension = {
    		geometry : geometry
    	};
    }

    const providerRequest = {
        url : req.url,
        params : req.params,
        query : req.query
    }

    log.debug("provider request: ", providerRequest);

    let dbClient = dbClientManager.getDBClient(req.marklogicUsername);

    const mq = new MarkLogicQuery();
	  mq.providerGetData(providerRequest, dbClient)
	    .then(data => {
	      logResult(data);
	      callback(null, data);
      })
      .catch(function(error) {
        callback(error)
      });
}

function coerceQuery (params) {
  Object.keys(params).forEach(function (param) {
    if (params[param] === 'false') { params[param] = false; }
    else if (params[param] === 'true') { params[param] = true; }
    else if (param === 'inSR') {
      params[param] = (typeof params[param] === "string" && params[param].startsWith("{") ? JSON.parse(params[param]) : params[param]);
    }
    else if (param === 'geometry') {
      params[param] = (typeof params[param] === "string" && params[param].startsWith("{") ? JSON.parse(params[param]) : params[param]);
    }
    else if (param === 'outStatistics') {
      params[param] = (typeof params[param] === "string") ? JSON.parse(params[param]) : params[param];
    }
    else if (param === 'classificationDef') {
      params[param] = (typeof params[param] === "string") ? JSON.parse(params[param]) : params[param];
    }
    else if (! isNaN(params[param])) { params[param] = Number(params[param]); }
  })
  return params;
}

function logResult(geojson) {
  log.debug("result:", geojson);
}

module.exports = MarkLogic
