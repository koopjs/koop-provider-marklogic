xquery version "1.0-ml";

module namespace trans = "http://marklogic.com/rest-api/transform/example-gkg-transform";


declare function trans:transform($context as map:map, $params as map:map, $content as document-node())
as document-node()
{
  document {
    <html>
      <head>
        <style>
        body
        &#123;
          font-family: Verdana, Arial;
          font-size: 12px;
        &#125;

        .name
        &#123;
          background-color: #508abb;
          color: #FFFFFF;
          padding: 2px;
        &#125;

        .value
        &#123;
          background-color: #f4fbff;
          padding: 5px;
          word-wrap: break-word;
        &#125;
        </style>
      </head>
      <body>
        {
          for $p in $content//properties/* return 
          <div class="row">
            <div class="name">{ fn:name($p) }</div>
            <div class="value">{ fn:string($p) }</div>
          </div>
        }
      </body>
    </html>
  }
};