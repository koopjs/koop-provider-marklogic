const config = require('config');
const winston = require('winston');

const logger = new winston.createLogger({
  level: config.logger.level,
  transports: [
    new winston.transports.Console({
      'timestamp': true,
      'prettyPrint': true
    })
  ]
});

module.exports = logger;
