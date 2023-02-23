/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
  log.info(`Request URL: ${req.url}`);

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
    url: req.url,
    params: req.params,
    query: req.query
  }

  // The authentication plugin is expected to populate this parameter on the request to identify how a cached
  // MarkLogic client object should be obtained for this particular request.
  const dbClient = dbClientManager.getCachedMarkLogicClient(req.markLogicClientCacheKey);

	  new MarkLogicQuery().providerGetData(providerRequest, dbClient)
	    .then(data => {
	      callback(null, data);
      })
      .catch(function(error) {
        // Not certain that the error will always be a JSON object, but that is the case so far.
        log.error(JSON.stringify(error));
        // Per https://koopjs.github.io/docs/usage/provider, Koop wants "error.code" to have the HTTP response code,
        // which will be "error.statusCode" if this error comes from the MarkLogic Node Client.
        error.code = error.statusCode;
        // The nesting of the real error message is done by the MarkLogic Node Client. We want to bump it up to
        // "message" so that a Koop client will see it.
        if (error.body && error.body.errorResponse && error.body.errorResponse.message) {
          error.message = error.body.errorResponse.message;
        }
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

module.exports = MarkLogic
