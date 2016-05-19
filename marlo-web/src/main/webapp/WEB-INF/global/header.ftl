[#ftl]
[#import "bowerComponents.ftl" as components /]
<!doctype html>
  <html lang="en">
  <head>
  	<meta charset="UTF-8" />
  	[#-- Importing CSS files --]
  	[#if globalLibs??]
      [#list globalLibs as libraryName]
        [@components.css_imports libraryName=libraryName/]
      [/#list]
    [/#if]
  </head>
  <body>
  <!-- ${actionName} Content -->