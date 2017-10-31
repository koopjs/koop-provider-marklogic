/*
 * Copyright © 2017 MarkLogic Corporation
 */

var marklogic = require('marklogic');

const config = require('config');

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
		  console.log(JSON.stringify(error, null, 2));
		});
  })
}

module.exports = MarkLogicQuery
