[#ftl]
[#assign canEdit = true /]
[#assign editable = true /]
[#assign program = {
  'flagshipProgram' : true
} /]
[#assign liaisonInstitutionID = 1 /]
[#assign liaisonInstitutions = [
  { 'id': 1, 'acronym': 'F1', 'name': 'Priorities and Policies for CSA'},
  { 'id': 2, 'acronym': 'F2', 'name': 'Climate-Smart Technologies and Practices'},
  { 'id': 3, 'acronym': 'F3', 'name': 'Low emissions development'},
  { 'id': 4, 'acronym': 'F4', 'name': 'Climate services and safety nets'}
] /]

[#-- TODO: Remove fake data above --]

[#assign title = "POWB Report" /]
[#assign currentSectionString = "synthesis-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ ] /]
[#assign customJS = [ ] /]
[#assign customCSS = ["${baseUrl}/css/synthesis/synthesisGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "delivery" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/delivery"},
  {"label":"powbDelivery", "nameSpace":"powb", "action":"${crpSession}/delivery"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/images/global/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>
    
<section class="container">
  [#-- Program (Flagships and PMU) --]
  [#include "/WEB-INF/views/synthesis/submenu-powb.ftl" /]
  
  <div class="row">
    [#-- POWB Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/views/synthesis/menu-powb.ftl" /]
    </div> 
    <div class="col-md-9">
      [#-- Section Messages --]
      [#include "/WEB-INF/views/synthesis/messages-powb.ftl" /]
      
      [#-- Title --]
      <h3 class="headTitle">Delivery</h3>
      
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
         
        <div class="borderBox">
        
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/views/synthesis/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>


[#include "/WEB-INF/global/pages/footer.ftl"]
