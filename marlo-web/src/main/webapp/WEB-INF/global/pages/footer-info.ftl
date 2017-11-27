[#ftl]
<div class="row">
  [#-- Contact Information --]
  <div class="col-md-4 infoLinks text-left">
    <strong>Contact</strong><br />
    <ul>
      <li><a href="mailto:MARLOSupport@cgiar.org">MARLOSupport@cgiar.org</a></li>
    </ul>
  </div>
  [#-- Legal Information--]
  <div class="col-md-4 infoLinks text-left">
    <strong>Legal</strong><br />
    <ul>
      <li><a target="_blank" href="[@s.url namespace="/" action='legalInformation'][/@s.url]#privacyNotice">Privacy Notice </a></li>
      <li><a target="_blank" href="[@s.url namespace="/" action='legalInformation'][/@s.url]#termsConditions">Terms and Conditions</a></li>
      <li><a target="_blank" href="[@s.url namespace="/" action='legalInformation'][/@s.url]#copyRight">Copyright</a></li>
    </ul>
  </div>
  [#-- Glossary --]
  <div class="col-md-4 glossary text-right">
    [@s.text name="footer.glossary"][@s.param]<a target="_blank" href="[@s.url namespace="/" action='glossary'][/@s.url]">[@s.text name="global.clickHere" /][/@s.param][/@s.text] <span class="glyphicon glyphicon-hand-left"></span> </a>
  </div>
</div>

[#-- Copyright --]
<div class="copyRight">
  <hr />
  <span>  &#169; Copyright CIAT 2017 - Current version ${action.getVersion()}</span><br />
  <span> <a href="https://www.gnu.org/licenses/gpl-3.0.en.html">Some rights reserved</a></span>
</div>