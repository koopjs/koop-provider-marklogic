/*
 * Copyright © 2017 MarkLogic Corporation
 */

function MarkLogicQuery() {
}

MarkLogicQuery.prototype.providerGetData = function providerGetData(request, dbClient) {
  return new Promise((resolve, reject) => {
    dbClient.resources.post({
      name: 'geoQueryService',
      params: {},
      documents: request
    }).result((response) => {
      resolve(response);
    }).catch(function (error) {
      reject(error);
    })
  })
}

module.exports = MarkLogicQuery
