/*
 * Copyright © 2017 MarkLogic Corporation
 */

 // use this to convert incoming ESRI geometry objects to GeoJSON in WGS84

const config = require('config');
const options = require('winnow/dist/options');
const log = require('./logger');
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

    // convert incoming geometry into GeoJSON in WGS84
    var geometry = options.prepare(req.query).geometry;
    if (req.query && req.query.geometry) {
    	req.query.extension = {
    		geometry : geometry
    	};
    }

    var providerRequest = {
        url : req.url,
        params : req.params,
        query : req.query
    }

    log.debug("provider request: ", providerRequest);

    var mq = new MarkLogicQuery();
	  mq.providerGetData(providerRequest)
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
    else if (param === 'geometry') {
      params[param] = (params[param].startsWith("{") ? JSON.parse(params[param]) : params[param]);
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
