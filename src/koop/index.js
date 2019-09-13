/*
 * Copyright © 2019 MarkLogic Corporation
 */

const pkg = require('../../package.json');
const provider = {
  name: 'marklogic',
  hosts: false,
  Model: require('./marklogic'),
  version: pkg.version,
  type: 'provider'
};

module.exports = provider;
