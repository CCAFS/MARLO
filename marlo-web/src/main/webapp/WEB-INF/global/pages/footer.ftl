[#ftl]
  <!-- ${actionName} Content end-->
  
  [#-- Importing JavaScript files --]
  [#compress]
  [#if globalLibs??]
    [#list globalLibs as libraryName][@components.js_imports libraryName=libraryName/][/#list]
  [/#if]
  
  [#-- Importing JavaScript files --]
  [#if pageLibs??]
    [#list pageLibs as libraryName][@components.js_imports libraryName=libraryName/][/#list]
  [/#if]
  [/#compress]
  
  [#-- Second, import global javascripts and templates. --]
  <script>
    var baseURL = '${baseUrl}';
  </script>
  [#-- Global Javascript --]
  <script type="text/javascript" src="${baseUrl}/js/global/global.js" ></script>
  
  [#-- import the custom JS and CSS --]
  [#if customJS??][#list customJS as js]<script src="${js}"></script>[/#list][/#if]
  </body>
</html>