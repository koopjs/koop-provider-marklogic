'use strict';

var util = require('util');

var Parser = require('./index').Parser;
var astToSql = require('./index').util.astToSQL;
var parser = new Parser();
/*var sql = `SELECT
   instruments_id,
   exchanges_id,
   display_name
 FROM instruments_exchanges, (SELECT @lastid := -1) __init__
 WHERE instruments_id IN (133962, 133963, 133964)
   AND CASE WHEN @lastid <> instruments_id THEN @rownum := @limit ELSE 1 END > 0
   AND (@rownum := @rownum - 1) >= 0
   AND (@lastid := instruments_id) IS NOT NULL
 ORDER BY instruments_id`;*/

var sql = `select * from tab where username like '%something%' and img is not null`;

try {
    var ast = parser.parse(sql);
    console.log(util.inspect(ast, { depth: 20, colors: true }));
    console.log(astToSql(ast));
} catch (e) {
    console.log(e);
}
