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
    
    [#-- Importing JavaScript constants --]
    [#include "/WEB-INF/global/pages/javascript-constants.ftl" /]
    
    [#-- Importing Tawk.to Widget configuration --]
    [#include "/WEB-INF/global/pages/tawkto-widget.ftl" /]
    
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
    <script type="text/javascript" src="${baseUrl}/global/js/utils.js" ></script>
    <script type="text/javascript" src="${baseUrl}/global/js/global.js" ></script>
    [#if logged]
      [#-- Pusher app --]
      <script type="text/javascript" src="${baseUrl}/global/js/pusher-app.js" ></script>
    [/#if]
    [#-- import the custom JS and CSS --]
    [#if customJS??][#list customJS as js]<script src="${js}"></script>[/#list][/#if]
    
    [#-- Changes on Save --]
    <script type="text/javascript" src="${baseUrl}/global/js/changes.js" ></script>
    
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