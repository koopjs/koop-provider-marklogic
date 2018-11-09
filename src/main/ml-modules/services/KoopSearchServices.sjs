'use strict';


const koopConfigUri = '/koop/config.json';
const collFeatureServices = 'http://marklogic.com/feature-services';

function get(context, params) {
  try {
    var response = {
      searchServices: getSearchServices()
    };
    return response;
  }
  catch (err) {
    console.trace(err);
    returnErrToClient(500, 'Error handling request', err.toString());
  }
}

function getKoopConfig() {
  // could be cached
  return cts.doc(koopConfigUri).toObject();
}

function getFeatureServiceUrl(serviceName) {
  let config = getKoopConfig()
  return fn.concat(
    config.ssl ? 'https' : 'http',
    '://',
    config.host,
    ':',
    config.port,
    '/marklogic/',
    serviceName,
    '/FeatureServer'
  );
}

function getSearchServices() {
  let serviceDocs = cts.search(cts.collectionQuery(collFeatureServices));
  let searchServices = [];
  for (var serviceDoc of serviceDocs) {
    for (var searchNode of serviceDoc.xpath('./search/*')) {
      var searchObj = searchNode.toObject();
      searchObj.serviceName = serviceDoc.xpath('./info/name').valueOf();
      searchObj.serviceUrl = getFeatureServiceUrl(searchObj.serviceName);
      searchObj.name = searchNode.xpath('./fn:name()').valueOf();
      searchServices.push(searchObj);
    }
  }

  return searchServices;
}

function returnErrToClient(statusCode, statusMsg, body) {
  fn.error(
    null,
    'RESTAPI-SRVEXERR',
    Sequence.from([statusCode, statusMsg, body])
  );
};

exports.GET = get;