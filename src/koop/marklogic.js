/*
 * Copyright © 2017 MarkLogic Corporation
 */

 // use this to convert incoming ESRI geometry objects to GeoJSON in WGS84
const options = require('winnow/dist/options');

var MarkLogicQuery = require('./query');

function MarkLogic () {}

MarkLogic.prototype.getData = function getData (req, callback) {
    console.log(":::req.url");
    console.log(req.url);
    console.log(":::req.params");
    console.log(req.params);
    console.log(":::req.query");
    console.log(req.query);

    // fix typing
    req.query = coerceQuery(req.query);

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

    var mq = new MarkLogicQuery();
	  mq.providerGetData(providerRequest)
	    .then(data => {
	      logResult(data);
	      callback(null, data);
	    });
}

// TODO: we may want to conver the statistics JSON strings too
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
    else if (! isNaN(params[param])) { params[param] = Number(params[param]); }
  })
  return params;
}

function logResult(geojson) {
  var nl = (process.platform === 'win32' ? '\r\n' : '\n')
  console.log(nl)
  console.log(nl)
  console.log(geojson);
  console.log(nl)
  console.log(nl)
}

module.exports = MarkLogic
