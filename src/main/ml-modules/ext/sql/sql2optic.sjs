/*
 * Copyright © 2017 MarkLogic Corporation
 */

'use strict';

const op = require('/MarkLogic/optic');

function where(where) {
  // pass in a SQL WHERE clause and get an optic query that can be used in
  // an Optic where call

  return sql2optic(where2ast(where));
}

function where2ast(where) {

  // the flora parser does not support date/time literals currently
  // (see https://github.com/godmodelabs/flora-sql-parser/issues/3)
  // we can replace the literals with functions and handle the functions
  // correctly but this sensitive the regex matches working correctly

  where = where
    .replace(/TIMESTAMP +'(\d{4}-\d{1,2}-\d{1,2} \d{2}:\d{2}:\d{2}\.?\d*)'/gi, "timestamp('$1')")
    .replace(     /DATE +'(\d{4}-\d{1,2}-\d{1,2})'/gi, "date('$1')")
    .replace(     /TIME +'(\d{2}:\d{2}:\d{2}\.?\d*)'/gi, "time('$1')");

  console.log("Rewrote where: " + where);

  const Parser = require('./flora-sql-parser/index.js').Parser;
  const ast = new Parser().parse("SELECT * WHERE " + where);

  //console.log(ast);

  return ast.where;
}

function sql2optic(expr) {
  switch(expr.type.toLowerCase()) {
    case "binary_expr":
      return binaryExpression(expr);
    case "unary_expr":
      return unaryExpression(expr);
    case "column_ref":
      return op.col(expr.column);
    case "null":
      return op.jsonNull();  // should this be jsonNull or just null? maybe handle IS NULL with op.isDefined()?
    case "function":
      return functionExpression(expr);
    case "expr_list":
      return expr.value.map(sql2optic);
    default:
      return expr.value;
  }
}

function binaryExpression(expr) {
  switch(expr.operator.toLowerCase()) {
    case "or":
      return op.or(sql2optic(expr.left), sql2optic(expr.right));
    case "and":
      return op.and(sql2optic(expr.left), sql2optic(expr.right));
    case "=":
      return op.eq(sql2optic(expr.left), sql2optic(expr.right));
    case "<>":
    case "!=":
      return op.ne(sql2optic(expr.left), sql2optic(expr.right));
    case ">":
      return op.gt(sql2optic(expr.left), sql2optic(expr.right));
    case "<":
      return op.lt(sql2optic(expr.left), sql2optic(expr.right));
    case ">=":
      return op.ge(sql2optic(expr.left), sql2optic(expr.right));
    case "<=":
      return op.le(sql2optic(expr.left), sql2optic(expr.right));
    case "+":
      return op.add(sql2optic(expr.left), sql2optic(expr.right));
    case "-":
      return op.subtract(sql2optic(expr.left), sql2optic(expr.right));
    case "*":
      return op.multiply(sql2optic(expr.left), sql2optic(expr.right));
    case "/":
      return op.divide(sql2optic(expr.left), sql2optic(expr.right));
    case "%":
      return op.modulo(sql2optic(expr.left), sql2optic(expr.right));
    case "is":
      return op.not(op.isDefined(sql2optic(expr.left)));  //assume the right is NULL
    case "is not":
      return op.isDefined(sql2optic(expr.left));  //assume the right is NULL
    case "in": {
      const left = sql2optic(expr.left);
      return op.or(...sql2optic(expr.right).map(value => op.eq(left, value)));
    }
    case "not in": {
      const left = sql2optic(expr.left);
      return op.not(op.or(...sql2optic(expr.right).map(value => op.eq(left, value))));
    }
    case "between": {
      const column = sql2optic(expr.left);
      const nums = sql2optic(expr.right);
      console.log(nums);

      return op.and(
        op.ge(column, nums[0]),
        op.le(column, nums[1])
      )
    }
    case "not between": { // between is inclusive of the bounds so make this exclusive?
      const column = sql2optic(expr.left);
      const nums = sql2optic(expr.right);
      return op.and(
        op.lt(column, nums[0]),
        op.gt(column, nums[1])
      )
    }
    case "like":
      // we could leverage the https://docs.marklogic.com/sql:like function here
      // using an op.call()
      return op.eq(
        op.fn.matches(sql2optic(expr.left), like2match(expr.right.value), "i"),
        true
      )
    case "not like":
      return op.eq(
        op.fn.matches(sql2optic(expr.left), like2match(expr.right.value), "i"),
        false
      )
  }
}

function unaryExpression(expr) {
  switch(expr.operator.toLowerCase()) {
    case "not":
      return op.not(sql2optic(expr.expr));
  }
}

function functionExpression(expr) {
  // just support "fn" functions for now
  // these are case sensitive
  var name = expr.name;
  var xpathNamespace = 'http://www.w3.org/2005/xpath-functions';

  switch(name.toLowerCase()) {
    case "to_date":
      var args = sql2optic(expr.args);
      return op.xdmp.parseYymmdd(convertSqlDatePicture(args[1]), args[0]);
    case "timestamp":
      var args = sql2optic(expr.args);
      return xs.dateTime(xdmp.parseDateTime("[Y]-[M]-[D] [H]:[m]:[s][Z]", args[0]+"-0"));
    case "date":
      var args = sql2optic(expr.args);
      return xs.date(xdmp.parseDateTime("[Y]-[M]-[D][Z]", args[0]+"-0"));
    case "time":
      var args = sql2optic(expr.args);
      return xs.time(xdmp.parseDateTime("[H]:[m]:[s][Z]", args[0]+"-0"));
    default:
      return op.call(xpathNamespace, name,  sql2optic(expr.args));
  }
}

function convertSqlDatePicture(pic) {
  // this is not a complete mapping

  let newPic = pic;

  // years
  if (newPic.includes("YYYY")) {
    newPic = newPic.replace("YYYY", "yyyy");
  }

  // days
  if (newPic.includes("DD")) {
    newPic = newPic.replace("DD", "dd");
  }

  // hours
  if (newPic.includes("HH24")) {
    newPic = newPic.replace("HH24", "HH");
  } else if (newPic.includes("HH12")) {
    newPic = newPic.replace("HH12", "KK");
  } else if (newPic.includes("HH")) {
    newPic = newPic.replace("HH", "KK");
  }

  // minutes
  if (newPic.includes("MI")) {
    newPic = newPic.replace("MI", "mm");
  }

  // seconds
  if (newPic.includes("SS")) {
    newPic = newPic.replace("SS", "ss");
  }

  return newPic;
}

function like2match(expr) {
  // % matches zero, one, or multiple characters
  // _ matches a single character
  return "^" + expr.replace(/%/g, ".*").replace(/_/g, ".") + "$";
}

exports.where = where;
exports.where2ast = where2ast;
