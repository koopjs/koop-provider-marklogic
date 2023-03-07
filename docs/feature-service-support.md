---
layout: default
title: ArcGIS Feature Service Support
nav_order: 4
---

The MarkLogic Koop Provider supports multiple operations in the ArcGIS Feature Service API, each of which is detailed
below.

## Query Layer Operation

The following parameters in the
[Query Layer operation](https://developers.arcgis.com/rest/services-reference/enterprise/query-feature-service-layer-.htm)
are supported (these are listed in the order they appear in the ArcGIS documentation):

- `where`
- `objectIds`
- `geometry`
- `geometryType`
- `spatialRel`
- `outFields`
- `returnGeometry`
- `returnIdsOnly`
- `returnCountOnly`
- `orderByFields`
- `groupByFieldsForStatistics`
- `outStatistics`
- `resultOffset`
- `resultRecordCount`

The `geometryType` parameter defines the type of shape represented by the `geometry` parameter, and the `spatialRel`
parameter defines the spatial relationship to be applied to the `geometry` parameter while performing the query. The
table below defines the supported values for `geometryType` along with the supported values for `spatialRel` for each
type:

| Geometry Type     | Supported values for spatialRel                  |
|-------------------|--------------------------------------------------|
| `esriGeometryEnvelope` | `esriSpatialRelIntersects`, `esriSpatialRelContains` |
| `esriGeometryPoint` | `esriSpatialRelIntersects`, `esriSpatialRelContains` | 
| `esriGeometryPolygon` | `esriSpatialRelIntersects`, `esriSpatialRelContains`, `esriSpatialRelOverlaps`, `esriSpatialRelTouches`, `esriSpatialRelWithin` |
| `esriGeometryPolyline` | `esriSpatialRelIntersects`, `esriSpatialRelCrosses` |


## Generate Renderer Operation

TODO Coming soon via DEVEXP-250. 
