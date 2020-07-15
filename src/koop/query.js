/*
 * Copyright © 2017 MarkLogic Corporation
 */

const marklogic = require('marklogic');
const config = require('config');
const log = require('./logger');

function MarkLogicQuery() {
}

MarkLogicQuery.prototype.providerGetData = function providerGetData(request, dbClient) {
	return new Promise((resolve, reject) => {
		dbClient.resources.post({
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
