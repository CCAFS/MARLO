[#ftl]
    <!-- ${actionName} Content end-->
    <footer>
      <div class="container">
        <div id="footerContainer" class="little">
          <div id="reportIssue">
            <p><b>[@s.text name="footer.report.issue"][@s.param]<a target="_blank" href="https://docs.google.com/forms/d/1EouZJYHqERbDRB2DaT6Q1cY-_-Tqe0daf4fxXrBU-ts/viewform">[@s.text name="global.here" /]</a>[/@s.param][/@s.text]</b></p>
            <p class="lighter">&#64; Copyright 2016</p>
            <p class="lighter">Current version ${action.getVersion()}</p>
          </div>
          <div id="glossary">
            <p><b>[@s.text name="footer.glossary"][@s.param]<a target="_blank" href="[@s.url namespace="/" action='glossary'][/@s.url]">[@s.text name="global.here" /][/@s.param][/@s.text]</a></b></p>
          </div>
          <div class="clearfix"></div>
        </div>
      </div> <!-- end container -->   
    </footer> 
    [#-- Importing JavaScript files --]
    [#compress]
    [#if globalLibs??]
      [#list globalLibs as libraryName][@components.js_imports libraryName=libraryName/][/#list]
    [/#if]
    
    [#-- Importing JavaScript files --]
    [#if pageLibs??]
      [#list pageLibs as libraryName][@components.js_imports libraryName=libraryName/][/#list]
    [/#if]
    
    [#-- Second, import global javascripts and templates. --]
    <script>
      var baseURL = '${baseUrl}';
    </script>
    [#-- Global Javascript --]
    <script type="text/javascript" src="${baseUrl}/js/global/global.js" ></script>
    <script type="text/javascript" src="${baseUrl}/js/global/utils.js" ></script>
    [#-- import the custom JS and CSS --]
    [#if customJS??][#list customJS as js]<script src="${js}"></script>[/#list][/#if]
    
    [/#compress]
  </body>
</html>