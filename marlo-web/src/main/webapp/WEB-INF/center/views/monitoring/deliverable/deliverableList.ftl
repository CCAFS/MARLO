[#ftl]
[#assign title = "MARLO Deliverables" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/monitoring/deliverable/deliverableList.js" 
  ] 
/]
[#assign customCSS = [
  "${baseUrl}/global/css/customDataTable.css",
  "${baseUrlMedia}/css/deliverable/projectDeliverable.css"
  ] 
  /]
[#assign currentSection = "monitoring" /]
[#assign currentStage = "deliverables" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/monitoring", "action":"${(centerSession)!}/projectList"},
  {"label":"deliverables", "nameSpace":"/monitoring", "action":"${(centerSession)!}/deliverableList"}
] /]

[#include "/WEB-INF/center/pages/header.ftl" /]
[#include "/WEB-INF/center/pages/main-menu.ftl" /]
[#import "/WEB-INF/center/macros/deliverableListTemplate-center.ftl" as deliverableList /]
[#-- Help text --]
<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.png" />
    <p class="col-md-10"> [@s.text name="deliverableList.help"][/@s.text] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>

<section class="container">
  <article class="row" id="mainInformation">
  [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/center/views/monitoring/project/menu-projects.ftl" /]
      </div>
    <div class="col-md-9">
    [#-- Projects data information --]
        [#include "/WEB-INF/center/views/monitoring/project/dataInfo-projects.ftl" /]
        <br />
        
      [#-- deliverable List (My Projects) --]
      <h3 class="headTitle text-center">${selectedProgram.name}- Project Key Deliverables</h3>
      <br />
      <div class="loadingBlock"></div>
      <div style="display:none">[@deliverableList.deliverableList deliverables=deliverables canValidate=true canEdit=true namespace="/monitoring" defaultAction="${(centerSession)!}/projectDeliverable" /]</div>
  
      [#-- Section Buttons --]
      <div class="buttons">
        <div class="buttons-content">        
          <a class="addButton" href="[@s.url action='${(centerSession)!}/addNewDeliverable'][@s.param name='projectID']${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">[@s.text name="Add deliverable" /]</a>
          <div class="clearfix"></div>
        </div>
      </div>

      
      <div class="clearfix"></div>
    </div>
    
  </article>
</section>
[@customForm.confirmJustificationDeliverable action="deleteDeliverable.do" namespace="/${currentSection}" title="Remove Deliverable" /]


[#include "/WEB-INF/center/pages/footer.ftl"]
