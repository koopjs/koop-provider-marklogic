/*
 * Copyright © 2017 MarkLogic Corporation
 */

const marklogic = require('marklogic');
const config = require('config');
const log = require('./logger');

const marklogicSessions = [];

function MarkLogicQuery(request) {
  this.conn = JSON.parse(JSON.stringify(config.marklogic.connection));
  console.log("config.marklogic.connection: " + JSON.stringify(config.marklogic.connection));

  const authorizationString = extractAndDecodeAuthorizationString(request);
  if (authorizationString !== undefined) {
    const authenticationTokens = authorizationString.split(":");
    this.conn.user = authenticationTokens[0];
    this.conn.password = authenticationTokens[1];
  }


  if (marklogicSessions[this.conn.user]) {
    console.log("Found session");
    this.db = marklogicSessions[this.conn.user].client;
  } else {
    console.log("No session");
    this.db = marklogic.createDatabaseClient(this.conn);
    marklogicSessions[this.conn.user] = {
      client: this.db,
      token: request.token
    }
  }

  console.log("marklogicSessions length: " + Object.keys(marklogicSessions));
}

MarkLogicQuery.prototype.providerGetData = function providerGetData(request) {
  return new Promise((resolve, reject) => {
    this.db.resources.post({
      name: 'KoopProvider',
      params: { },
      documents : request
    }).result((response) => {
      resolve(response);
    }).catch(function(error){
      reject(new Error(error.body.errorResponse.message));
      console.log(error);
		})
	})
};

function extractAndDecodeAuthorizationString(req) {
  let retVal = undefined;
  const headerValue = req.get("authorization");
  if (headerValue !== undefined) {
    const headerTokens = headerValue.split(" ");
    const encodedAuthorization = headerTokens[1];
    const authorizationBuffer = new Buffer(encodedAuthorization, 'base64');
    retVal = authorizationBuffer.toString('utf-8');
  }
  return retVal;
}

module.exports = MarkLogicQuery
