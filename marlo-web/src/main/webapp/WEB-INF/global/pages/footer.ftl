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
    [#compress]
    [#-- Importing JavaScript files --]
    [#if globalLibs??]
      [#list globalLibs as libraryName][@components.js_imports libraryName=libraryName/][/#list]
    [/#if]
    
    [#-- Importing JavaScript files --]
    [#if pageLibs??]
      [#list pageLibs as libraryName][@components.js_imports libraryName=libraryName/][/#list]
    [/#if]
    
    [#-- Second, import global javascripts and templates. --]
    <script type="text/javascript">
      var currentPlanningYear, currentReportingYear;
      var formBefore;
      var justificationLimitWords = 100;
      var errorMessages = [];
      var forceChange = false;
      var hashScroll = true;
      var Tawk_API, Tawk_LoadStart;
      
      var GOOGLE_API_KEY="${config.googleApiKey}";
      
      var production = ${config.production?string};
      var baseURL = "${baseUrl}";
      var debugMode = ${config.debug?string};
      var editable = ${editable?string};
      var canEdit = ${canEdit?string};
      
      var PUSHER_KEY = "${config.production?string('530082f1cccc805bcf69','a83864709700a9aca7a5')}";
      
    </script>
    
    [#-- Global Javascript --]
    <script type="text/javascript" src="${baseUrl}/js/global/utils.js" ></script>
    <script type="text/javascript" src="${baseUrl}/js/global/global.js" ></script>
    [#-- import the custom JS and CSS --]
    [#if customJS??][#list customJS as js]<script src="${js}"></script>[/#list][/#if]
    
    [/#compress]
    
    [#if !config.production && config.debug]
      <div id="debugPanel" class="ui-widget-content">
        <div id="accordion">
          <h3>Debug Panel</h3>
          <div>
            <ul class="list-unstyled">
              <li>Editable: ${editable?string}</li>
              <li>canEdit: ${canEdit?string}</li>
            </ul> 
          </div>
          <h3>Form outputs</h3>
          <div>
            <div class="getSerializeForm">
          </div> 
        </div> 
      </div>
    [/#if]
  </body>
</html>