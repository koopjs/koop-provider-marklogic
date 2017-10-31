MarkLogic Native Plugin API Samples
-----------------------------------

Requirements
------------

For all Unix systems (Linux, Mac OS X, Solaris), GNU GCC and GNU make utilities
are required.

For Windows systems, Visual Studio 2013 and Cygwin with GNU make utility are
required. For Vista and above (Windows 7, Windows 8, Windows Server 2008+) you
may need to copy this entire directory out of Program Files to another directory
in order to work around Windows security problems writing into Program Files.


Building
--------

For all platforms, run the "make" command from this directory.


Installing
----------

To install the sample plugin, use the plugin:install-from-zip() function from
the "MarkLogic/plugin/plugin.xqy" library module to install the
"sampleplugin.zip" zip file, i.e.:

xquery version "1.0-ml";
import module namespace plugin = "http://marklogic.com/extension/plugin"
  at "MarkLogic/plugin/plugin.xqy";

plugin:install-from-zip("native",
  xdmp:document-get("Samples/NativePlugins/sampleplugin.zip")/node())


Executing Aggregates
--------------------

To execute an aggregate user defined function from the sample plugin, you can
use the cts:aggregate() function, i.e.:

cts:aggregate("native/sampleplugin", "variance",
  cts:element-reference(xs:QName("amount"), "type=double"))
