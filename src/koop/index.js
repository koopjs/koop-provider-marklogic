/*
 * Copyright © 2019 MarkLogic Corporation
 */

// See https://koopjs.github.io/docs/development/provider/registration for the spec for this module.

const pkg = require('../../package.json');
const provider = {
  name: 'marklogic',
  hosts: false,
  Model: require('./marklogic'),
  version: pkg.version,
  type: 'provider'
};

module.exports = provider;
