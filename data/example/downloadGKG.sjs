'use strict';

/*
Use this in a qconsole workspace to download new GDelt GKG data if needed
*/

const url = "http://api.gdeltproject.org/api/v1/gkg_geojson?TIMESPAN=1440&MAXROWS=250000&OUTPUTFIELDS=name,geores,url,domain,sharingimage,lang,themes,names,tone,wordcount,numcounts,urlpubtimedate";

const result = fn.head(xdmp.documentGet(url, { format : "json", encoding : "auto" })).toObject();

const date = result.features[0].properties.urlpubtimedate;
const manifest = [];
const features = [];

// IMPORTANT: set this to a value greater than other OBJECTIDs you have in your data already
let id = 6000;

const dir = "gkg_geojson_" + date.replace(/-|:/g, "_");

for (const f of result.features) {
  f.properties.OBJECTID = id++;
  features.push(xdmp.toJSON(f));

  manifest.push({
    path : dir + "/" + f.properties.OBJECTID + ".json"
  })
}

const zip = xdmp.zipCreate(manifest, features);

xdmp.save("/tmp/" + dir + ".zip", zip)
