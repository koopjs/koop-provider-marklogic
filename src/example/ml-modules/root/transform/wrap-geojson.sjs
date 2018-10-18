'use strict';

const geojson = require('/MarkLogic/geospatial/geojson.xqy');

function transform(content, context)
{
  const feature = content.value;

  const envelope = {
    envelope : {
      feature : feature,
      ctsRegion : geojson.parseGeojson(feature.root.geometry)
    }
  };

  content.value = envelope;
  return content;
};

exports.transform = transform;
