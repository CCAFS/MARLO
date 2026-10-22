[#ftl]
<div class="row">
  [#-- Contact Information --]
  <div class="col-md-4 infoLinks text-left">
    <strong>Contact</strong><br />
    <ul>
      <li><a href="mailto:MARLOSupport@cgiar.org">[@s.text name="footer.contactEmail" /]</a></li>
    </ul>
  </div>
  [#-- Legal Information--]
  <div class="col-md-4 infoLinks text-left">
    <strong>Legal</strong><br />
    <ul>
      <li><a target="_blank" href="[@s.url namespace="/" action='legalInformation'][/@s.url]#privacyNotice">Privacy Notice </a></li>
      <li><a target="_blank" href="[@s.url namespace="/" action='legalInformation'][/@s.url]#termsConditions">Terms and Conditions</a></li>
    </ul>
  </div>
  [#-- Glossary --]
  <div class="col-md-4 infoLinks text-left">
    <strong>Others</strong><br />
    <ul>
      <li><a target="_blank" href="[@s.url namespace="/" action='glossary'][/@s.url]">Glossary of relevant items </a></li>
    </ul>
  </div>
</div>

[#-- Copyright --]
<div class="copyRight">
  <hr />
  <span>[@s.text name="footer.copyrighAlert"][@s.param]${.now?string("yyyy")}[/@s.param][/@s.text]</span><br />
  <span style="opacity:0.7;">[@s.text name="footer.developedBy" /]<span><br> 
  <span style="opacity:0.5;">[@s.text name="footer.rightsReserved" /]</span>
</div>

[#-- MARLO Blog --]
<div class="newsButton">
  <a href="https://cgiar-prms.notion.site/AICCRA-Knowledge-and-Data-Sharing-team-achievements-8742c91b3be14f05902f02224a63a2d9" target="__blank">
     <b> [@s.text name="footer.checkBlog" /] </b> <span class="fa fa-external-link-square"></span>
  </a>
</div>