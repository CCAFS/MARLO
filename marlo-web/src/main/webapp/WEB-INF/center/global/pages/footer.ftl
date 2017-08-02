[#ftl]
<span id="test" class="errorTag glyphicon glyphicon-info-sign" style="display:none;"> </span>
    <!-- ${actionName} Content end-->
    <footer class="footer">  
      <div class="before">
      <div class="col-md-4"></div>
      <div class="col-md-4"></div>
      <div class="col-md-4"></div>
      </div>    
      <div class="container">
        <div class="col-md-5 infoLinks">
          <div class="col-md-5 contact"><a href="">MARLOSupport@cgiar.org</a></div>
          <div class="col-md-2 scrollup"><a href="#">Top</a></div>
        </div>
        <div class="col-md-2 copyRight">@ Copyright 2016 <br /> Current version ${action.getVersion()}</div>
       <!--
        <div class="col-md-4 col-md-offset-1 glossary">[@s.text name="footer.glossary"][@s.param]<a target="_blank" href="[@s.url namespace="/" action='glossary'][/@s.url]">[@s.text name="global.here" /][/@s.param][/@s.text]</a>
        -->
        </div>
      </div>
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
      var hashScroll = true;
     [#-- var forceChange = ${draft?string};--]
      
      var GOOGLE_API_KEY="${config.googleApiKey}";
      var PUSHER_KEY = "${config.pushApiKey}";
      
      var actionName = "${actionName}";
      var production = ${config.production?string};
      var baseURL = "${baseUrl}";
      var baseUrlMedia = "${baseUrlMedia}";
      var debugMode = ${config.debug?string};
      var editable = ${editable?string};
      var canEdit = ${canEdit?string};
      var autoSaveActive = ${((currentUser.autoSave)!false)?string};
      var currentCenterID=${(centerID)!'-1'};
      var currentCycleYear = ${(currentCycleYear)!1999};
      var reportingActive= ${((reportingActive)!false)?string};
      var centerSession="${(centerSession)!}";
      
      
      [#-- MARLO Develop ID as default --]
      [#assign tawktoSiteId = "583c8368de6cd808f31aee05"]
      
      [#-- Tawk.to Widget --]
      var Tawk_API=Tawk_API||{}, Tawk_LoadStart=new Date();
      Tawk_LoadStart = new Date();
      Tawk_API.visitor = {
        'name': '${(currentUser.composedCompleteName)!}',
      };
      
      Tawk_API.onLoad = function() {
        Tawk_API.setAttributes({
            'fullName': '${(currentUser.composedCompleteName)!}',
            'userName' : '${(currentUser.username)!}',
            'userId': '${(currentUser.id)!}',
            'roles': '',
            'liaisonInstitutions': ''
            
        }, function(error) {
        });
        Tawk_API.addTags(['MARLO', '${config.production?string('Production','Development')}', '${(centerSession)!}'], function(error){});
      };
      (function() {
        var s1 = document.createElement("script"), s0 = document.getElementsByTagName("script")[0];
        s1.async = true;
        s1.src = 'https://embed.tawk.to/${tawktoSiteId}/default';
        s1.charset = 'UTF-8';
        s1.setAttribute('crossorigin', '*');
        s0.parentNode.insertBefore(s1, s0);
        
      })();
      
      
      
      
    </script>
  
    [#-- Global Javascript --]
    <script type="text/javascript" src="${baseUrlMedia}/js/global/utils.js" ></script>
    <script type="text/javascript" src="${baseUrlMedia}/js/global/global.js" ></script>
    [#if logged]
      <script type="text/javascript" src="${baseUrlMedia}/js/global/pusher-app.js" ></script>
    [/#if]
    [#-- import the custom JS and CSS --]
    [#if customJS??][#list customJS as js]<script src="${js}"></script>[/#list][/#if]
    
    [/#compress]
    
    <div id="draggable-button">
      <p><span class="glyphicon glyphicon-comment"></span> Chat </p> <span class="status"></span>
    </div>
    
    [#if !config.production && config.debug]
      <div id="debugPanel" class="ui-widget-content">
        <div id="accordion">
          <h3>Debug Panel</h3>
          <div>
            <ul class="list-unstyled">
              <li><strong>canEdit:</strong> ${canEdit?string}</li>
              <li><strong>Editable:</strong> ${editable?string}</li>
              <li><strong>currentSectionString:</strong> ${(currentSectionString)!}</li>
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