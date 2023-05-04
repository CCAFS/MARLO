[#ftl]
[#assign title = "Cluster Deliverables" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/deliverables/deliverableList.js?20230302",
  "${baseUrlCdn}/global/js/fieldsValidation.js"
  [#-- "${baseUrlCdn}/global/js/autoSave.js" --]
  ]
/]
[#assign customCSS = [
  "${baseUrlCdn}/global/css/customDataTable.css",
  "${baseUrlMedia}/css/projects/projectDeliverable.css?20230504"] /]

[#assign currentStage = "deliverableList" /]
[#assign isListSection = true /]

[#if !action.isAiccra()]
  [#assign currentSection = "projects" /]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"${currentSection}", "action":"${(crpSession)!}/projectsList"},
    {"text":"P${project.id}", "nameSpace":"${currentSection}", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
    {"label":"deliverableList", "nameSpace":"${currentSection}", "action":""}
  ]/]
[#else]
  [#assign currentSection = "clusters" /]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"${currentSection}", "action":"${(crpSession)!}/projectsList"},
    {"text":"C${project.id}", "nameSpace":"${currentSection}", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
    {"label":"deliverableList", "nameSpace":"${currentSection}", "action":""}
  ]/]
[/#if]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/deliverableListTemplate.ftl" as deliverableList /]

<!--- 

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-help.jpg" />
    <p class="col-md-10">[#if reportingActive] [@s.text name="project.deliverableList.help2" /] [#else] [@s.text name="project.deliverableList.help1" /] [/#if] </p>
  </div>
  <div style="display:none" class="viewMore closed"></div>
</div>

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-transparent-warning.png" />
    <p class="col-md-10">
      [@s.text name="deliverable.message"][/@s.text]
      <div class="form-group">
            <label for="">[@s.text name="Download Template" /]:</label>
              <a href="${baseUrlCdn}/global/documents/AICCRA_dissemination_templates_2021.xlsx" download><img src="${baseUrlCdn}/global/images/download-excel.png" height="35"/></a>
      </div>
    </p>
  </div>
<div class="viewMore closed"></div>
</div>
-->


<div class="animated flipInX container  viewMore-block containerAlertMargin ">
  <div class=" containerAlert alert-leftovers alertColorBackgroundInfo" id="containerAlert">
    <div class="containerLine alertColorInfo"></div>
    <div class="containerIcon">
      <div class="containerIcon alertColorInfo"> 
        <img src="${baseUrlCdn}/global/images/icon-question.png" />      
      </div>
    </div>
    <div class="containerText col-md-12">
      <p class="alertText">[#if reportingActive] [@s.text name="project.deliverableList.help2" /] [#else] [@s.text name="project.deliverableList.help1" /] [/#if]</p>
    </div>
    <div  class="viewMoreCollapse closed"></div>
  </div>
</div>

<div class="animated flipInX container  viewMore-block containerAlertMargin">
  <div class=" containerAlert  alert-leftovers alertColorBackgroundInfo" id="containerAlert">
    <div class="containerLine alertColorInfo"></div>
    <div class="containerIcon">
      <div class="containerIcon alertColorInfo">
        <img src="${baseUrlCdn}/global/images/icon-exclamation.png" />
      </div>
    </div>
    <div class="containerText col-md-9">
      <p class="alertText">[@s.text name="deliverable.message"][/@s.text]</p>
    </div>
    <div class="ContainerDownload col-md-2">     
    <a href="${baseUrlCdn}/global/documents/AICCRA_dissemination_templates_2022.xlsx">     
      <button class="DownloadButton " >
        <div class="IconDownloadButton">
          <img src="${baseUrlCdn}/global/images/icon-xls.png" height="33"/>
        </div>
        <span for="">[@s.text name="Download Template" /]</span>
			</button>
      </a>
    </div>   
    <div  class="viewMoreCollapse closed"></div>
  </div>
</div>


<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">

      [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/projects/messages-projects.ftl" /]

        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]

          <h3 class="headTitle">[@s.text name="project.deliverableList.title" /]</h3>

          [#if reportingActive]
            [#--  FAIR LEGEND --]
            <div class="form-group col-md-12 legendContent">
              <div class="col-md-12 fairDiagram" >[@s.text name="project.deliverableList.fairExplanation" /] </div>
              <div class="col-md-6 explanation">
                <div class="col-md-12 form-group "><b>[@s.text name="project.deliverableList.fairExplanation.fair" /]:</b></div>
                <div class="form-group col-md-6 "><span>F</span>[@s.text name="project.deliverableList.fairExplanation.findable" /]</div>
                <div class="form-group col-md-6 "><span>A</span> [@s.text name="project.deliverableList.fairExplanation.accessible" /]</div>
                <div class="form-group col-md-6 "><span>I</span> [@s.text name="project.deliverableList.interoperable" /]</div>
                <div class="form-group col-md-6 "><span>R</span> [@s.text name="project.deliverableList.fairExplanation.reusable" /]</div>
              </div>
              <div class="col-md-6 colors">
                <div class="col-md-12 form-group "><b>[@s.text name="project.deliverableList.fairExplanation.fairColors" /]</b></div>
                <div class="form-group col-md-6 fair"><span id="achieved" class="legend-color"></span> [@s.text name="project.deliverableList.fairExplanation.achieved" /] </div>
                <div class="form-group col-md-6 fair"><span id="notAchieved" class="legend-color"></span>[@s.text name="project.deliverableList.fairExplanation.notAchieved" /]</div>
                <div class="form-group col-md-6 fair"><span id="notDefined" class="legend-color"></span>[@s.text name="project.deliverableList.fairExplanation.notDefined" /]</div>
              </div>
            </div>
            <div id="diagramPopup" style="display:none; text-align:center;">
              <img src="${baseUrlCdn}/global/images/FAIR_Principles_in_MARLO_20170919.png" alt="" width="100%" />
            </div>
          [/#if]
          [#--  Status LEGEND --]
          <div class="form-group col-md-12 legendContent">
            <div class="col-md-6 colors">
              <div class="col-md-12 form-group "><b>[@s.text name="project.deliverableList.deliverableStatus" /]:</b></div>
              <div class="form-group col-md-6 fair"><span id="" class="legend-color status-indicator Complete"></span>[@s.text name="project.deliverableList.deliverableStatus.complete" /]</div>
              <div class="form-group col-md-6 fair"><span id="" class="legend-color status-indicator On-going"></span>[@s.text name="project.deliverableList.deliverableStatus.onGoing" /]</div>
              <div class="form-group col-md-6 fair"><span id="" class="legend-color status-indicator Cancelled"></span>[@s.text name="project.deliverableList.deliverableStatus.cancelled" /]</div>
              <div class="form-group col-md-6 fair"><span id="" class="legend-color status-indicator Extended"></span>[@s.text name="project.deliverableList.deliverableStatus.extended" /]</div>
              <div class="form-group col-md-12 fair"><span id="" class="legend-color status-indicator Ready to be reported on"></span>[@s.text name="project.deliverableList.deliverableStatus.readyToReport" /]</div>
            </div>
            <div class="col-md-6 required-explanation">
              <div class="col-md-12 form-group"><b>[@s.text name="project.deliverableList.requiredStatus" /]:</b></div>
              <div class="form-group col-md-12"><span class="icon-check required-fields"></span>[@s.text name="project.deliverableList.requiredStatus.complete" /]</div>
              <div class="form-group col-md-12"><span class="icon-uncheck required-fields"></span>[@s.text name="project.deliverableList.requiredStatus.incomplete" /]</div>
            </div>
          </div>
          <div id="diagramPopup" style="display:none; text-align:center;">
            <img src="${baseUrlCdn}/global/images/FAIR_Principles_in_MARLO_20170919.png" alt="" width="100%" />
          </div>

          [#-- Current table --]
          <div class="">
            <h3 class="subTitle headTitle">Deliverables</h3>
            [#if reportingActive]
             <p class="note">[@s.text name="project.deliverableList.focusDeliverablesMessage"][@s.param]${currentCycleYear}[/@s.param][@s.param]<span class="label label-primary" title="Required for this cycle"><span class="glyphicon glyphicon-flash" ></span> Report</span>[/@s.param][/@s.text]</p>
            [/#if]
            <hr />
              [@deliverableList.deliverablesList deliverables=(currentDeliverableList)![] canValidate=true canEdit=candit  isReportingActive=reportingActive namespace="/clusters" defaultAction="${(crpSession)!}/deliverable" projectID=projectID/]
              [#--  
                [@deliverableList.deliverablesList deliverables=(project.getCurrentDeliverables(actualPhase))![] canValidate=true canEdit=candit  isReportingActive=reportingActive namespace="/clusters" defaultAction="${(crpSession)!}/deliverable" projectID=projectID/]
             --]
          </div>

          [#-- Add Deliverable Button --]
          [#-- if canEdit && action.hasPermission("addDeliverable")--]
          [#if canEdit && (action.hasPermissionDeliverable("addDeliverable", projectID, "deliverableList")|| action.hasPermission("addDeliverable"))]
          <div class="buttons">
            <div class="buttons-content">
              <div class="addDeliverable button-blue"><a  href="[@s.url namespace="/${currentSection}" action='${(crpSession)!}/addNewDeliverable'][@s.param name="projectID"]${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
                <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addDeliverable" /]
              </a></div>
            </div>
          </div>
          <div class="clearfix"></div>
          [/#if]

          [#-- Previous Extended table (Modal) --]
          <div class="">
            <h3 class="subTitle headTitle">Previous deliverables</h3>
            <hr />
            [@deliverableList.deliverablesList deliverables=(project.getPreviousDeliverables(actualPhase) + previousSharedDeliverableList)![] canValidate=true canEdit=candit isReportingActive=reportingActive namespace="/projects" defaultAction="${(crpSession)!}/deliverable" currentTable=false projectID=projectID/]
          </div>

          <input type="hidden" name="projectID" value="${projectID}" />
        [/@s.form]
      </div>
    </div>
</section>


[#include "/WEB-INF/global/pages/footer.ftl"]
