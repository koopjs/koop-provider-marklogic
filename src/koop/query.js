/*
 * Copyright © 2017 MarkLogic Corporation
 */

const marklogic = require('marklogic');
const config = require('config');
const log = require('./logger');

function MarkLogicQuery() {
  this.conn = config.marklogic.connection;
  this.db = marklogic.createDatabaseClient(this.conn);
}

MarkLogicQuery.prototype.providerGetData = function providerGetData(request) {
  return new Promise((resolve, reject) => {
		this.db.resources.post({
		  name: 'KoopProvider',
		  params: { },
		  documents : request
		}).result((response) => {
		  resolve(response);
		}, (error) => {
		  log.error(error);
		});
  })
}

module.exports = MarkLogicQuery
