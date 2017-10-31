/*
 * Copyright © 2017 MarkLogic Corporation
 */

'use strict';

const op = require('/MarkLogic/optic');
const geojson = require('/MarkLogic/geospatial/geojson.xqy');
const sql2optic = require('/ext/sql/sql2optic.sjs');
const geostats = require("/ext/geo/geostats.js");

const MAX_RECORD_COUNT = 5000;

function post(context, params, input) {
  // assume the input is the request that was sent to the koop provider getData() function

  try {
    return getData(fn.head(xdmp.fromJSON(input)));
  } catch (err) {
    console.trace(err);

    returnErrToClient(500, 'Error handling request', err.toString());
    // unreachable
  }
}

function returnErrToClient(statusCode, statusMsg, body) {
  fn.error(
    null,
    'RESTAPI-SRVEXERR',
    Sequence.from([statusCode, statusMsg, body])
  );
  // unreachable - control does not return from fn.error.
};

// the same as the koop provider function without the callback parameter
function getData(req) {
  console.log(req);

  if (req.params.method == "query") {
    return query(req);
  } else if (req.params.method == "generateRenderer") {
    return queryClassificationValues(req);
  } else {
    if (req.params.layer >= 0) {
      return generateLayerDescriptor(req.params.id, req.params.layer);
    } else {
      const sd = generateServiceDescriptor(req.params.id);

      // check to see if this is a "/layers" call to just get the layer list
      if (req.url && req.url.endsWith("/layers")) {
        return sd.layers;
      } else {
        return sd;
      }
    }
  }

  // return an unsupported error
  returnErrToClient(501, 'Request parameters not supported', xdmp.quote(req));
}

function getServiceModel(serviceName) {
  // TODO: These should be cached

  const collection = "http://marklogic.com/feature-services";

  let model = fn.head(
    cts.search(cts.andQuery([
      cts.collectionQuery(collection),
      cts.jsonPropertyValueQuery("name", serviceName)
    ]))
  );

  if (model) {
    return model.toObject();
  } else {
    throw "No service info found for: " + serviceName;
  }
}

function getLayerModel(serviceName, layerId) {
  // TODO: These should be cached

  const serviceModel = getServiceModel(serviceName);

  let layer = null;
  if (serviceModel) {
    layer =
      serviceModel.layers.find((l) => {
        return l.id == layerId;
      });
  }

  if (layer) {
    // default the schema to the service id if not specified
    layer.schema = layer.schema || serviceName;

    return layer;
  } else {
    throw "Layer " + layerId + " not found.";
  }
}

function getSchema(layerDesc, serviceName) {
  return layerDesc.schema || serviceName;
}

function generateServiceDescriptor(serviceName) {
  console.log("generating service descriptor for " + serviceName);

  // TODO: we should cache this instead of generating it every time

  const model = getServiceModel(serviceName);

  const desc = {
    description: model.info.description,
    maxRecordCount: MAX_RECORD_COUNT
  };

  // copy all the properties from the info section
  for (var propName in model.info) {
    desc[propName] = model.info[propName];
  }

  desc.layers = [];

  for (var layerModel of model.layers) {
    const layer = {
      metadata: {
        maxRecordCount: MAX_RECORD_COUNT
      }
    };

    // copy all the properties from the layer model
    for (var propName in layerModel) {
      layer.metadata[propName] = layerModel[propName];
    }

    // add the list of fields to the metadata
    layer.metadata.fields = [];

    const schema = getSchema(layerModel, serviceName);
    const viewDef = tde.getView(schema, layerModel.view);
    viewDef.view.columns.map((c) => {
      layer.metadata.fields.push({
        name : c.column.name,
        type : getFieldType(c.column.scalarType)
      });
    });

    desc.layers.push(layer);
  }

  return desc;
}

function generateLayerDescriptor(serviceName, layerNumber) {
  console.log("generating layer descriptor for " + serviceName + ":" + layerNumber);

  const serviceDesc = generateServiceDescriptor(serviceName);

  // find the layer we need
  const layer = serviceDesc.layers.find((l) => {
    return l.metadata.id == layerNumber;
  });

  if (layer) {
    return layer;
  } else {
    throw "No layer number " + layerNumber + "found";
  }
}

function getFieldType(datatype) {
  switch(datatype) {
    case "anyURI":
    case "iri":
      return "string";
    case "duration":
    case "dayTimeDuration":
    case "yearMonthDuration":
    case "gDay":
    case "gMonth":
    case "gMonthDay":
    case "gYear":
    case "gYearMonth":
      return "String";
    case "hexBinary":
    case "base64Binary":
      return "String";
    case "boolean":
      return "Integer";
    case "string":
      return "String";
    case "byte":
    case "unsignedByte":
      return "Integer";
    case "time":
      return "Date";
    case "date":
      return "Date";
    case "dateTime":
      return "Date";
    case "short":
    case "unsignedInt":
    case "int":
    case "unsignedLong":
    case "integer":
    case "unsignedShort":
    case "long":
    case "nonNegativeInteger":
      return "Integer";
    case "nonPositiveInteger":
    case "negativeInteger":
      return "Integer";
    case "float":
    case "decimal":
    case "double":
      return "Double";
    case "array":
      return "String";
    default:
      return "String";
  }
}


function query(req) {
  // always return a FeatureCollection for now
  const geojson = {
    type : 'FeatureCollection',
    metadata : {
      name: req.params.id,
    },
    filtersApplied: {
      geometry: true, // true if a geometric filter has already been applied to the data
      where: true // true if a sql-like where filter has already been applied to the data
    }
  };

  if (req.query.returnCountOnly) {
    console.log("getting count");

    req.query.outStatistics = [
      { outStatisticFieldName : "count", statisticType : "count" }
    ];

    geojson.count = Array.from(aggregate(req))[0].count;

  } else if (req.query.outStatistics != null) {

    console.log("running aggregation");
    geojson.statistics = Array.from(aggregate(req));

  } else {

    console.log("getting objects");
    geojson.features = Array.from(getObjects(req));

    // we chould only get this once in the process but do this for now to test
    const serviceId = req.params.id;
    const layerModel = generateLayerDescriptor(serviceId, req.params.layer);

    // set the field metadata in the response
    // but only set it for fields we are returning

    // we already called parseOutfields to get the list of fields
    // maybe we should move all the parse steps into one place and
    // create a parsed request object we can use throughout?
    const outFields = {};
    parseOutFields(req.query).map(f => { outFields[f] = true; });

    if (Object.keys(outFields).length === 0 || outFields["*"]) {
      geojson.metadata.fields = layerModel.metadata.fields;
    } else {
      geojson.metadata.fields = layerModel.metadata.fields.filter(f => {
        return outFields[f.name];
      });
    }

    geojson.metadata.idField = layerModel.metadata.idField;
    geojson.metadata.displayField = layerModel.metadata.displayField;
  }

  return geojson;
}

/**
 * This is used for generateRenderer requests. Generates an aggregation query to return
 * all the unique values in a field.
 * @param {Object} req - The request from Koop
 */
function queryClassificationValues(req) {

  const def = parseClassificationDef(req.query);

  const q = {
    groupByFieldsForStatistics : null,
    outStatistics : [{
      statisticType : "count",
      onStatisticField : null,
      outStatisticFieldName : "count"
    }],
    where : req.query.where
  };

  switch (def.type) {
    case "classBreaksDef":
      q.groupByFieldsForStatistics = def.classificationField;
      break;
    case "uniqueValueDef":
      if (typeof def.uniqueValueFields === "string") {
        q.groupByFieldsForStatistics = def.uniqueValueFields.split(def.fieldDelimiter).join(",");
      } else {
        q.groupByFieldsForStatistics = def.uniqueValueFields.join(",");
      }

      break;
    default:
      throw "Unsupported classificationDef.type: " + def.type;
  }

  const result = query({ params : req.params, query : q });

  //http://pro.arcgis.com/en/pro-app/help/mapping/symbols-and-styles/data-classification-methods.htm

  if (def.type === "classBreaksDef") {
    const values = [];
    result.statistics.map((stat) => {
      values.push(stat[def.classificationField]);
    });

    switch (def.classificationMethod) {
      case "esriClassifyNaturalBreaks":
        result.statistics = (new geostats(values)).getClassJenks(def.breakCount);
        break;
      case "esriClassifyEqualInterval":
        result.statistics = (new geostats(values)).getClassEqInterval(def.breakCount);
        break;
      case "esriClassifyQuantile":
        result.statistics = (new geostats(values)).getClassQuantile(def.breakCount);
        break;
      case "esriClassifyStandardDeviation":
        // this one doesn't seem to be implemented correctly so leaving it commented out for now
        //result.statistics = (new geostats(values)).getClassStdDeviation(def.standardDeviationInterval);
        //break;
        throw "Unsupported classificationMethod: " + def.classificationMethod;
      case "esriClassifyGeometricalInterval":
        result.statistics = (new geostats(values)).getClassGeometricProgression(def.breakCount);
        break;
      default:
        throw "Unsupported classificationMethod: " + def.classificationMethod;
    }
  }

  return result;
}


function parseWhere(query) {
  // Any legal SQL where clause operating on the fields in the layer is allowed
  // Example: where=POP2000 > 350000

  const where = query.where;
  let whereQuery = null;
  if (!where || where === "1=1" || where === "") {
    whereQuery = cts.trueQuery();
  } else {
    whereQuery = sql2optic.where(where);
  }

  console.log("where: " + whereQuery);

  return whereQuery;
}

function parseGeometry(query) {
  // the koop provider code will convert the ESRI geometry objects into GeoJSON
  // in WGS84 and place it in the query.extension.geometry property
  let geoQuery = null;
  if (query.extension && query.extension.geometry) {

    let regions = null;
    if (!query.geometryType || query.geometryType.toLowerCase() === "esrigeometryenvelope") {
      // handle this because the koop server changes boxes to GeoJSON polygons but
      // a box is better for this
      regions = convertEnvelopPolygon(query);
    } else {
      // convert GeoJSON into cts regions
      regions = geojson.parseGeojson(query.extension.geometry);
    }

    const operation = parseRegionOperation(query);

    const pointPaths = [
      '//geometry[type = "MultiPoint"]//array-node("coordinates")',
      '//geometry[type = "Point"]//array-node("coordinates")'
    ];

    // "type=long-lat-point" is needed for GeoJSON because GeoJSON uses longitude-first
    // coordinate order while the default in MarkLogic is latitude-first
    // may need to use the "boundaries-included" or "boundaries-excluded" options depending
    // on the spatialRel parameter
    const pointOptions = [ "type=long-lat-point" ];

    const pointQuery = cts.pathGeospatialQuery(
      pointPaths,
      regions,
      pointOptions
    )

    const regionPaths = [
      cts.geospatialRegionPathReference('/envelope/cts-region')
    ];

    const regionOptions = [];

    // assume all regions are placed in the /envelope/cts-region property
    const regionQuery = cts.geospatialRegionQuery(
      regionPaths,
      operation,
      regions,
      regionOptions
    )

    //geoQuery = cts.orQuery([ pointQuery, regionQuery ]);
    // there seem to be some issues with region queries so leave that off for now
    geoQuery = cts.orQuery([ pointQuery ]);
  } else {
    // just match everything
    geoQuery = cts.trueQuery();
  }

  console.log("geometry: " + geoQuery);

  return geoQuery;
}

function convertEnvelopPolygon(query) {
  // the koop server converts ESRI envelopes to GeoJSON polygons
  // convert them to boxes for more efficient seach
  // TODO: file an issue about the winding order (they do not follow the right hand rule)
  //{
  //  type: 'Polygon',
  //  coordinates: [[
  //    [geom.xmin, geom.ymin],
  //    [geom.xmin, geom.ymax],
  //    [geom.xmax, geom.ymax],
  //    [geom.xmax, geom.ymin],
  //    [geom.xmin, geom.ymin]
  //  ]]
  //}

  const coords = query.extension.geometry.coordinates[0];
  const south = coords[0][1];
  const west = coords[0][0];
  const north = coords[2][1];
  const east = coords[2][0];
  const box = { south : south, west : west, north : north, east : east }

  return splitBox(box).toArray();
}

function splitCtsBox(box) {
  return splitBox({ south : cts.boxSouth(box), west : cts.boxWest(box), north : cts.boxNorth(box), east : cts.boxEast(box) })
}

function splitBox(box) {
  if (Math.round(Math.abs(box.west - box.east)) >= 179) {
    // check east/west
    const middle = (box.west + box.east) / 2.0;
    return Sequence.from([
      { south : box.south, west : box.west, north : box.north, east : middle },
      { south : box.south, west : middle, north : box.north, east : box.east }
    ].map(splitBox))
  } else if (Math.round(Math.abs(box.south - box.north)) >= 179) {
    // check north/south
    const middle = (box.south + box.north) / 2.0;
    return Sequence.from([
      { south : box.south, west : box.west, north : middle, east : box.east },
      { south : middle, west : box.west, north : box.north, east : box.east }
    ].map(splitBox))
  }
  return Sequence.from([ cts.box(box.south, box.west, box.north, box.east) ]);
}

function parseRegionOperation(query) {
  // cts region operators: contains, covered-by, covers, disjoint, intersects, overlaps, within
  // default to intersects

  // TODO: verify mapping of ESRI spatial relations to MarkLogic operations
  // can we implement the other ESRI relations with combinations of the MarkLogic
  // operations?

  if (query.spatialRel) {
    switch(query.spatialRel.toLowerCase()) {
      case "esrispatialrelintersects":
        return "intersects";
      case "esrispatialrelcontains":
        return "contains";
      case "esrispatialrelcrosses":
        return "intersects";
      case "esrispatialrelwithin":
        return "within";
      case "esrispatialreloverlaps":
      case "esrispatialreltouches":
      case "esrispatialrelenvelopeintersects":
      case "esrispatialrelindexintersects":
      case "esrispatialrelrelation":
      default:
        throw "Unsupported geospatial operation: " + query.spatialRel;
    }
  } else {
    return "intersects";
  }
}

function parseOutStatistics(query) {
  // outStatistics may be a JSON string of an array of stats definitions
  // or it may be converted already
  // see http://resources.arcgis.com/en/help/rest/apiref/fsquery.html

  return (typeof query.outStatistics === "string") ?
    JSON.parse(query.outStatistics) : query.outStatistics;
}

function parseClassificationDef(query) {
  return (typeof query.classificationDef === "string") ?
    JSON.parse(query.classificationDef) : query.classificationDef;
}


function parseOrderByFields(query) {
  // orderByFields is supported on only those layers / tables that indicate supportsAdvancedQueries is true.
  // orderByFields defaults to ASC (ascending order) if <ORDER> is unspecified.

  // Syntax: orderByFields=field1 <ORDER>, field2 <ORDER>, field3 <ORDER>
  // Example: orderByFields=STATE_NAME ASC, RACE DESC, GENDER

  // return an array of { field, order } objects
  let fields = [];
  if (query.orderByFields) {
    fn.tokenize(query.orderByFields, ", ?").toArray().map((field) => {
     const parts = fn.tokenize(field, " +").toArray();
     fields.push(
       { "field" : parts[0], "order" : (parts[1] || "ASC" ) }
     );
    });
  }

  return fields;
}

function parseObjectIds(query) {
  //Syntax: objectIds=<objectId1>, <objectId2>
  //Example: objectIds=37, 462
  let ids = null;
  if (query.objectIds) {
    if (typeof query.objectIds === "string") {
      ids = fn.tokenize(query.objectIds, ", ?").toArray();
    } else {
      ids = [ query.objectIds ];
    }
  }
  return ids;
}

function parseOutFields(query) {
  // Description: The list of fields to be included in the returned resultset. This list is a comma delimited list of field names.
  // You can also specify the wildcard "*" as the value of this parameter. In this case, the query results include all the field values.
  // Note that the wildcard also implicitly implies returnGeometry=true and setting returnGeometry to false will have no effect.

  // Example: outFields=AREANAME,ST,POP2000
  // Example (wildcard usage): outFields=*
  let fields = [];
  if (query.outFields) {
    fields = fn.tokenize(query.outFields, ", ?").toArray();
  }

  return fields;
}

function parseGroupByFields(query) {
  // Description: One or more field names on which the values need to be grouped for calculating the statistics.
  // Note: groupByFieldsForStatistics is valid only when outStatistics parameter is used.
  // Syntax: groupByFieldsForStatistics=field1, field2
  // Example: groupByFieldsForStatistics=STATE_NAME, GENDER

  let fields = [];
  if (query.groupByFieldsForStatistics) {
    fields = fn.tokenize(query.groupByFieldsForStatistics, ", ?").toArray();
  }

  return fields;
}

// returns a Sequence of documents
function getObjects(req) {
  const layerModel = getLayerModel(req.params.id, req.params.layer);

  const query = req.query;
  const orderByFields = parseOrderByFields(query);
  const geoQuery = parseGeometry(query);
  const whereQuery = parseWhere(query);

  let outFields = null;
  if (query.returnIdsOnly) {
    outFields = [ "OBJECTID" ];
  } else {
    outFields = parseOutFields(query);
  }

  const returnGeometry = (query.returnGeometry || outFields[0] === "*") ? true : false;

  const boundingQueries = [ geoQuery ];

  // get the layer bounding query from the layer model
  if (layerModel.boundingQuery) {
    boundingQueries.push(cts.query(layerModel.boundingQuery));
  }

  // TODO: look into how to deal with object ids more generally
  // we could also look into putting the ids into a "literal" row set and joining via optic
  const ids = parseObjectIds(query);
  if (ids) {
    // this assumes we are querying against the OBJECTID field as a number
    // should use a range index if we have one
    const idsQuery = cts.jsonPropertyValueQuery("OBJECTID", ids.map(Number));
    console.log("getting ids: " + ids);

    boundingQueries.push(idsQuery);
  }

  const boundingQuery = cts.andQuery(boundingQueries);

  console.log("bounding query: " + xdmp.toJsonString(boundingQuery));

  const offset = (!query.resultOffset ? 0 : Number(query.resultOffset));
  console.log("offset: " + offset);

  // what if the number of ids passed in is more than the max?
  const limit = (!query.resultRecordCount ? MAX_RECORD_COUNT : Number(query.resultRecordCount));
  console.log("limit: " + limit);

  const bindParams = {
    "offset" : offset,
    "limit" : limit
  };

  const columnNames = [];
  const columnDefs = [];

  const viewDef = tde.getView(layerModel.schema, layerModel.view);
  viewDef.view.columns.map((c) => {
    columnNames.push(c.column.name);
    columnDefs.push(c.column);
  });

  let pipeline = op.fromView(layerModel.schema, layerModel.view, null, "DocId")
    .where(boundingQuery)
    .where(whereQuery)
    .orderBy(getOrderByDef(orderByFields))
    .offset(op.param("offset"))
    .limit(op.param("limit"))

  // only join in the document if we need to get the geometry
  if (returnGeometry) {
    pipeline = pipeline
      .joinDoc(op.col('doc'), op.fragmentIdCol('DocId'))
  }

  // TODO: see if there is any benefit to pushing the column select earlier in the pipeline
  // transform the rows into GeoJSON
  pipeline = pipeline
    .select(getSelectDef(outFields, columnDefs, returnGeometry));

  return pipeline.result(null, bindParams);
}

// returns a Sequence of aggregated results
function aggregate(req) {
  // When using outStatistics the only other parameters that will be used are
  // groupByFieldsForStatistics, orderByFields, time, and where.

  // this will be the koop provider "id"
  const layerModel = getLayerModel(req.params.id, req.params.layer);

  const query = req.query;
  const stats = parseOutStatistics(query)
  const groupByFields = parseGroupByFields(query);
  const orderByFields = parseOrderByFields(query);

  const geoQuery = parseGeometry(query);

  const boundingQueries = [ geoQuery ];

  // get the layer bounding query from the layer model
  if (layerModel.boundingQuery) {
    boundingQueries.push(cts.query(layerModel.boundingQuery));
  }

  const boundingQuery = cts.andQuery(boundingQueries);
  console.log("bounding query: " + xdmp.toJsonString(boundingQuery));

  const whereQuery = parseWhere(query);

  console.log("group by: " + groupByFields);
  console.log("order by: " + orderByFields);

  // Hard code to 0 and max for now as these aren't technically supported for
  // the feature service aggregations but we may want to support limiting if there
  // are a lot of values.
  const offset = 0;
  const limit = Number.MAX_SAFE_INTEGER;

  const bindParams = {
    "offset" : offset,
    "limit" : limit
  };

  let pipeline = op.fromView(layerModel.schema, layerModel.view)
    .where(boundingQuery)
    .where(whereQuery)
    .groupBy(
      groupByFields,
      getAggregateGroupByDef(stats)
    )
    .orderBy(getOrderByDef(orderByFields))
    .offset(op.param("offset"))
    .limit(op.param("limit"));

  return pipeline.result(null, bindParams);
};

function getAggregateFieldNames(aggregateDefs) {
  return aggregateDefs.map((def) => {
    return def._outCol._colName;
  });
};

function getSelectDef(outFields, columnDefs, returnGeometry = false) {
  const defs = [
    op.as("type", "Feature")
  ];

  defs.push(
    op.as(
      "properties",
      op.jsonObject(getPropDefs(outFields, columnDefs))
    )
  );

  // only include this if returnGeometry is true or outFields is *
  if (returnGeometry || outFields[0] === "*") {
    defs.push(
      op.as("geometry", op.xpath("doc", "//geometry"))
    )
  }

  return defs;
}

function getPropDefs(outFields, columnDefs) {
  const props = [];

  if (outFields.length === 0 || outFields[0] === "*") {
    // we need to select all the columns here
    // we can't just leave it blank to select everything though because
    // we are selecting other parts of the docs

    columnDefs.map((col) => {
      props.push(
        op.prop(
          col.name,
          op.case(
            op.when(op.isDefined(op.col(col.name)), op.col(col.name)), op.jsonNull()
          )
        )
      )
    });
  } else {
    outFields.map((f) => {
      const col = columnDefs.find((c) => { return c.name === f });
      props.push(
        op.prop(
          col.name,
          op.case(
            op.when(op.isDefined(op.col(col.name)), op.col(col.name)), op.jsonNull()
          )
        )
      )
    });
  }
  return props;
}

function getValueConverter(col) {
  switch (col.scalarType) {
    case "date":
      return "";
    default:
      return op.col(col.name)
  }
}

function getOrderByDef(fields) {
  return fields.map((field) => {
    switch (field.order.toLowerCase()) {
      case "desc":
        return op.desc(field.field);
      case "asc":
        return op.asc(field.field);
      default:
        return op.asc(field.field);
    }
  });
}

// not used any more since we don't return aggregates in properties
function getAggregatePropDefs(groupByFields, stats) {
  const props = [];

  groupByFields.map((f) => {
    props.push(
      op.prop(f, op.col(f))
    )
  });

  stats.map((stat) => {
    props.push(
      op.prop(stat.outStatisticFieldName, op.jsonNumber(op.col(stat.outStatisticFieldName)))
    );
  });
  return props;
}

function getAggregateGroupByDef(stats) {
  return stats.map(getAggregateStatDef);
}

function getAggregateStatDef(stat) {
  const statsType = stat.statisticType;
  const statsFieldName = stat.onStatisticField;
  const statsOutFieldName = stat.outStatisticFieldName;

  //"statisticType": "<count | sum | min | max | avg | stddev | var>"
  switch (statsType.toLowerCase()) {
    case "count":
      return op.count(statsOutFieldName, statsFieldName);
    case "sum":
      return op.sum(statsOutFieldName, statsFieldName);
    case "min":
      return op.min(statsOutFieldName, statsFieldName);
    case "max":
      return op.max(statsOutFieldName, statsFieldName);
    case "avg":
      return op.avg(statsOutFieldName, statsFieldName);
    case "stddev":
      return op.uda(statsOutFieldName, statsFieldName, "native/varianceplugin", "stddev");
    case "var":
      return op.uda(statsOutFieldName, statsFieldName, "native/varianceplugin", "variance");
    default:
      returnErrToClient(
        501,
        "statsType not supported: " + statsType,
        statsType + " aggregations not supported"
      );
  }
}

exports.POST = post;
