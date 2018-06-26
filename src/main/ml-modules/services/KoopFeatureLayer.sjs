'use strict';

const search = require('/MarkLogic/appservices/search/search.xqy');
const sut = require('/MarkLogic/rest-api/lib/search-util.xqy');
const ast = require('/MarkLogic/appservices/search/ast.xqy');


const koopConfigUri = '/koop/config.json';
const collFeatureServices = 'http://marklogic.com/feature-services';

// Returns all feature layers of a feature service
function get(context, params) {
  try {
    var response = {};
    
    var serviceName = params.service;
    var model = getServiceModel(serviceName);

    if (model === null) {
      fn.error(null, 'Unable to find service ' + serviceName + '.');
    }

    model = model.toObject();
    var layers = model.layers;
    if (params.readOnly) {
      var filterReadOnly = fn.lowerCase(params.readOnly) === 'true';
      layers = layers.filter(function(l) {
        if (filterReadOnly) {
          return l.readOnly && l.readOnly === true;
        } else {
          return l.readOnly === undefined || (l.readOnly && l.readOnly === false);
        }
      });
    }

    var response = {
      layers: layers
    }

    return response;
  }
  catch (err) {
    console.trace(err);
    returnErrToClient(500, 'Error handling request', err.toString());
  }
}

// Inserts or replaces a feature layer
function put(context, params, input) {
  try {
    var serviceName = params.service;
    var model = getServiceModel(serviceName);
    var schema = params.schema || serviceName;

    if (model === null) {
      fn.error(null, 'Unable to find service ' + serviceName + '.');
    }
    if (schema === null || schema === '') {
      fn.error(null, 'No schema specified.');
    }
    if (params.view === null || params.view === '') {
      fn.error(null, 'No view specified.');
    }
    if (params.searchOptions === null || params.searchOptions === '') {
      fn.error(null, 'No search options specified.');
    }

    const uri = xdmp.nodeUri(model);
    model = model.toObject();
    var layerId = null;

    if (params.layerId) {
      layerId = parseInt(params.layerId);
      var layerIndex = model.layers.findIndex(l => l.id === layerId);
      if (layerIndex <= -1) {
        fn.error(null, 'Unable to find layer with ID of ' + layerId + '.');
      }
      if (model.layers[layerIndex].readOnly && model.layers[layerIndex].readOnly === true) {
        fn.error(null, 'Cannot replace layer with ID of ' + layerId + ' since it is read-only.');
      }
    }
    else {
      layerId = model.layers.length;
    }

    var layer = createNewLayerObj(
      layerId, 
      params.layerName, 
      params.layerDescription, 
      params.geometryType, 
      params.schema,
      params.view
    );

    layer.boundingQuery = createBoundingQuery(params.query, params.qtext, params.searchOptions);

    if (layerId === model.layers.length) {
      model.layers.push(layer);
    }
    else {
      model.layers[layerIndex] = layer;
    }

    xdmp.documentInsert(uri, model, { collections : xdmp.documentGetCollections(uri) });

    var response = {
      featureLayerUrl: getFeatureLayerUrl(serviceName, layerId),
      featureServiceUrl: getFeatureServiceUrl(serviceName),
      layerId: layerId
    };

    return response;
  }
  catch (err) {
    console.trace(err);
    returnErrToClient(500, 'Error handling request', err.toString());
  }
}

function getServiceModel(serviceName) {
  return fn.head(cts.search(
    cts.andQuery([
      cts.collectionQuery(collFeatureServices),
      cts.jsonPropertyValueQuery("name", serviceName)
    ])));
}

function createNewLayerObj(id, name, desc, geometryType, schema, view) {
  return {
    "id": id,
    "name": name || 'New Layer',
    "description": desc || '',
    "geometryType": geometryType || 'Point',
    "idField": "OBJECTID",
    "displayField": "url",
    "geometryPath": "\/geometry",
    "extent": {
      "xmin": -180,
      "ymin": -90,
      "xmax": 180,
      "ymax": 90,
      "spatialReference": {
        "wkid": 4326,
        "latestWkid": 4326
      }
    },
    "schema": schema,
    "view": view
  };
}

function createBoundingQuery(structuredQueryJson, qtext, searchOptions) {
  var queries = [];

  var options = sut.options({ options: searchOptions });

  if (qtext && options) {
    var query = search.parse(qtext, options, 'cts:query');
    if (query) {
      queries.push(query);
    }
  }
  
  if (structuredQueryJson && options) {
    var sQuery = sut.searchFromJson(xdmp.toJSON(structuredQueryJson)).xpath('search:query');
    var ctsQuery = ast.toQuery(sQuery, options);
    queries.push(ctsQuery.query);
  }

  var fullQuery = cts.andQuery(queries);
  return xdmp.toJSON(fullQuery).toObject();
}

function getKoopConfig() {
  // could be cached
  return cts.doc(koopConfigUri).toObject();
}

function getFeatureServiceUrl(serviceName) {
  let config = getKoopConfig()
  return fn.concat(
    config.ssl ? 'https' : 'http',
    '://',
    config.host,
    ':',
    config.port,
    '/marklogic/',
    serviceName,
    '/FeatureServer'
  );
}

function getFeatureLayerUrl(serviceName, layerId) {
  return fn.concat(
    getFeatureServiceUrl(serviceName),
    '/',
    layerId
  );
}

function returnErrToClient(statusCode, statusMsg, body) {
  fn.error(
    null,
    'RESTAPI-SRVEXERR',
    Sequence.from([statusCode, statusMsg, body])
  );
};

exports.GET = get;
exports.PUT = put;