[#ftl]
  <!-- ${actionName} Content end-->
  [#-- Importing JavaScript files --]
  [#if globalLibs??]
      [#list globalLibs as libraryName]
        [@components.js_imports libraryName=libraryName/]
      [/#list]
    [/#if]
  </body>
</html>