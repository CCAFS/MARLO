[#ftl]
[#assign title = "MARLO Admin" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = [] /]
[#assign customJS = [ "${baseUrl}/js/superadmin/marloParameters.js" ] /]
[#assign customCSS = [ "${baseUrl}/css/superadmin/superadmin.css" ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "parameters" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"parameters", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
<hr />

<div class="container">
  [#include "/WEB-INF/global/pages/breadcrumb.ftl" /]
</div>
[#include "/WEB-INF/global/pages/generalMessages.ftl" /]


<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/superadmin/menu-superadmin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]
        
          [#-- MARLO CRPs--]
          <h4 class="sectionTitle">[@s.text name="marloParameters.title" /]</h4>
          <div class="crps-list">
          [#if crpList?has_content]
            [#list crpList as crp]
              [@crpParametersMacro element=crp name="" index=crp_index  /]
            [/#list]
          [/#if]
          </div>
          
        [/@s.form]
      </div>
    </div>
  </div>
</section>


[#macro crpParametersMacro element name index isTemplate=false]
  <div id="crpParameters-${isTemplate?string('template',index)}" class="crpParameters borderBox" style="display:${isTemplate?string('none','block')}">

    [#-- CRP Title --]
    <div class="blockTitle closed">
      <strong>${(element.acronym?upper_case)!}</strong> - ${(element.name)!} <small>(Parameters: ${(element.parameters?size)!0})</small>
    </div>
    
    <div class="blockContent" style="display:none">
      <hr />
      
      [#if element.parameters??]
        [#list element.parameters as parameter]
          ${parameter}
        [/#list]
      [/#if]
      
    </div>
  </div>
[/#macro]

[#include "/WEB-INF/global/pages/footer.ftl" /]