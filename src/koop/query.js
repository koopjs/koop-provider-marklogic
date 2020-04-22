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
			name: 'geoQueryService',
			params: { },
			documents : request
		}).result((response) => {
			resolve(response);
		}).catch(function(error) { 
			console.log(error);
			reject(new Error(error));
		})
	})
}

module.exports = MarkLogicQuery
