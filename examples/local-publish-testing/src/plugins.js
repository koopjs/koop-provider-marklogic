const providerMarklogic = require('./provider-marklogic/initialize')();
const outputs = [];
const auths = [];
const caches = [];
const plugins = [providerMarklogic];
module.exports = [...outputs, ...auths, ...caches, ...plugins];