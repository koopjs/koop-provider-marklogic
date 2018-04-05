'use strict';

var geojson = require('/MarkLogic/geospatial/geojson.xqy');

function transform(content, context)
{
  const geojson = content.value;

  const envelope = {
    envelope : {
      feature : geojson,
      ctsRegion : geojson.parseGeojson(geojson.root.geometry)
    }
  };

  content.value = envelope;
  return content;
};

exports.transform = transform;
