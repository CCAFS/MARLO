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
          <div class="col-md-5 contact">Contact person: <a href="mailto:MARLOSupport@cgiar.org">MARLOSupport@cgiar.org</a></div>
          <div class="col-md-2 scrollup"><a href="#">Top</a></div>
        </div>
        <div class="col-md-2 copyRight">@ Copyright 2016 <br /> Current version ${action.getVersion()}</div>
        <div class="col-md-4 col-md-offset-1 glossary">[@s.text name="footer.glossary"][@s.param]<a target="_blank" href="[@s.url namespace="/" action='glossary'][/@s.url]">[@s.text name="global.clickHere" /][/@s.param][/@s.text] <span class="glyphicon glyphicon-hand-left"></span> </a>
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
      var forceChange = false;
      
      var GOOGLE_API_KEY="${config.googleApiKey}";
      var PUSHER_KEY = "${config.pushApiKey}";
      
      var MIN_DATE = '2010-01-01';
      var MAX_DATE = '2030-12-31';
      
      var actionName = "${actionName}";
      var production = ${config.production?string};
      var baseURL = "${baseUrl}";
      var baseUrlMedia = "${baseUrlMedia}";
      var debugMode = ${config.debug?string};
      var editable = ${editable?string};
      var canEdit = ${canEdit?string};
      var draft = ${draft?string};
      var autoSaveActive = ${((currentUser.autoSave)!false)?string};
      var currentCrpID=${(crpID)!'-1'};
      var currentCrpSession='${(crpSession)!'-1'}';
      var currentCycleYear = ${(currentCycleYear)!1999};
      var reportingActive= ${((reportingActive)!false)?string};
      var projectPreSetting= ${((project.projectEditLeader)!false)?string('0','1')};
      
      
      
      
      [#-- MARLO Develop ID as default --]
      [#assign tawktoSiteId = "57864c4b7e9d57372d381198"]
      [#if config.production]
        [#if crpSession?? && logged]
          [#if crpSession == "a4nh"]
            [#-- MARLO Production (A4NH) --]
            [#assign tawktoSiteId = "582f0db6fccdfa3ec8373386"]
          [#elseif crpSession == "ccafs"]
            [#-- MARLO Production (CCAFS) --]
            [#assign tawktoSiteId = "582f0d28fccdfa3ec8373342"]
          [#elseif crpSession == "livestock"]
            [#-- MARLO Production (LIVESTOCK) --]
            [#assign tawktoSiteId = "582f0df2fccdfa3ec837347a"]
          [#elseif crpSession == "pim"]
            [#-- MARLO Production (PIM) --]
            [#assign tawktoSiteId = "582f0d82fccdfa3ec837336e"]
          [#elseif crpSession == "wle"]
            [#-- MARLO Production (WLE) --]
            [#assign tawktoSiteId = "582f0d9cfccdfa3ec837337d"]
          [#elseif crpSession == "fta"]
            [#-- MARLO Production (FTA) --]
            [#assign tawktoSiteId = "59773fa85dfc8255d623ed45"]
          [#elseif crpSession == "maize"]
            [#-- MARLO Production (MAIZE) --]
            [#assign tawktoSiteId = "597740fc5dfc8255d623ed4d"]
          [#elseif crpSession == "wheat"]
            [#-- MARLO Production (WHEAT) --]
            [#assign tawktoSiteId = "5977436f5dfc8255d623ed52"]
          [/#if]
        [#else]
          [#-- MARLO Production - Public --]
          [#assign tawktoSiteId = "582f0c81f9976a1964b0c240"]
        [/#if]
      [/#if]
      
      [#-- User tag --]
      [#assign userTag][#if !config.production]([#if config.debug]Develop[#else]Testing[/#if])[/#if][/#assign]
      
      [#-- Tawk.to Widget --]
      var Tawk_API=Tawk_API||{}, Tawk_LoadStart=new Date();
      Tawk_LoadStart = new Date();
      Tawk_API.visitor = {
        'name': '${(userTag)!} ${(currentUser.composedCompleteName)!}',
      };
      
      Tawk_API.onLoad = function() {
        Tawk_API.setAttributes({
            'fullName': '${(userTag)!} ${(currentUser.composedCompleteName)!"No Name"}',
            'userName' : '${(currentUser.username)!"No User name"}',
            'userId': '${(currentUser.id)!"No ID"}',
            'composedId': '${(currentUser.composedID)!"No Composed ID"}',
            'userTags': '[${(roles)!}${(roles?has_content && liasons?has_content)?string(',','')}${(liasons)!}]'
            
        }, function(error) {
           
        });
        [#--  Tawk_API.addTags(['MARLO', '${config.production?string('Production','Development')}', '${(crpSession)!}'], function(error){});--]
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
    
    
    [#if baseUrl = "http://marlodev.ciat.cgiar.org"]
      <!-- Hotjar Tracking Code for http://marlodev.ciat.cgiar.org/ -->
      <script>
          (function(h,o,t,j,a,r){
              h.hj=h.hj||function(){(h.hj.q=h.hj.q||[]).push(arguments)};
              h._hjSettings={hjid:303261,hjsv:5};
              a=o.getElementsByTagName('head')[0];
              r=o.createElement('script');r.async=1;
              r.src=t+h._hjSettings.hjid+j+h._hjSettings.hjsv;
              a.appendChild(r);
          })(window,document,'//static.hotjar.com/c/hotjar-','.js?sv=');
      </script>
    [/#if]
    
    [#-- Global Javascript --]
    <script type="text/javascript" src="${baseUrlMedia}/js/global/utils.js" ></script>
    <script type="text/javascript" src="${baseUrlMedia}/js/global/global.js" ></script>
    [#if logged]
      [#-- Pusher app --]
      <script type="text/javascript" src="${baseUrlMedia}/js/global/pusher-app.js" ></script>
    [/#if]
    [#-- import the custom JS and CSS --]
    [#if customJS??][#list customJS as js]<script src="${js}"></script>[/#list][/#if]
    
    [#-- Changes on Save --]
    <script type="text/javascript" src="${baseUrlMedia}/js/global/changes.js" ></script>
    
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