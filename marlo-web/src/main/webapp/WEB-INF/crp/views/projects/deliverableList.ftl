[#ftl]
[#assign title = "Project Deliverables" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/deliverables/deliverableList.js",
  "${baseUrl}/global/js/fieldsValidation.js"
  [#-- "${baseUrl}/global/js/autoSave.js" --]
  ] 
/] 
[#assign customCSS = [
  "${baseUrl}/global/css/customDataTable.css",
  "${baseUrlMedia}/css/projects/projectDeliverable.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "deliverableList" /]
[#assign isListSection = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"deliverableList", "nameSpace":"/projects", "action":""}
]/]


[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]
[#import "/WEB-INF/crp/macros/deliverableListTemplate.ftl" as deliverableList /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
    <p class="col-md-10">[#if reportingActive] [@s.text name="project.deliverableList.help2" /] [#else] [@s.text name="project.deliverableList.help1" /] [/#if] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
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
           
          [#if reportingActive]
            <h3 class="headTitle">[@s.text name="project.deliverableList.title" /]</h3>
            [#--  FAIR LEGEND --]
            <div class="form-group col-md-12 legendContent">
              <div class="col-md-12 fairDiagram" >[@s.text name="project.deliverableList.fairExplanation" /] </div>
              <div class="col-md-6 explanation">
                <div class="col-md-12 form-group "><b>FAIR:</b></div>
                <div class="form-group col-md-6 "><span>F</span> Findable </div>
                <div class="form-group col-md-6 "><span>A</span> Accessible</div>
                <div class="form-group col-md-6 "><span>I</span> Interoperable</div>
                <div class="form-group col-md-6 "><span>R</span> Reusable</div>
              </div>
              <div class="col-md-6 colors">
                <div class="col-md-12 form-group "><b>FAIR colors:</b></div>
                <div class="form-group col-md-6 fair"><span id="achieved"></span> Achieved </div>
                <div class="form-group col-md-6 fair"><span id="notAchieved"></span> Not achieved</div>
                <div class="form-group col-md-6 fair"><span id="notDefined"></span> Not defined</div>
              </div>
            </div>
            <div id="diagramPopup" style="display:none; text-align:center;">
              <img src="${baseUrl}/global/images/FAIR_Principles_in_MARLO_20170919.png" alt="" width="100%" />
            </div>
          [/#if]
          <h3 class="subTitle headTitle">On going deliverables</h3>
          <div class="deliverables-extended-version" data-toggle="modal" data-target=".ongoing-modal"><span class="glyphicon glyphicon-eye-open"></span></div>
          <span class="extended-simple-version" data-toggle="modal" data-target=".ongoing-modal">Extended version</span>
           [#if reportingActive]
             <p class="note">
              [@s.text name="project.deliverableList.focusDeliverablesMessage"][@s.param]${currentCycleYear}[/@s.param][@s.param]<span class="label label-primary" title="Required for this cycle"><span class="glyphicon glyphicon-flash" ></span> Report</span>[/@s.param][/@s.text]
             </p>
           [/#if]
           <hr />
           [#-- On-going Extended table (Modal) --]
           <div class="modal fade extended-table-modal ongoing-modal" tabindex="-1" role="dialog" aria-labelledby="extendedTableModal" aria-hidden="true">
            <div class="modal-dialog modal-lg">
              <div class="modal-content">
                <h3 class="subTitle headTitle">On going deliverables</h3>
                <hr />
                  [@deliverableList.deliverablesListExtended deliverables=action.getDeliverables(true,false) canValidate=true canEdit=candit namespace="/projects" defaultAction="${(crpSession)!}/deliverable"/]
              </div>
            </div>
          </div>
          [#-- Simple table --]
           [@deliverableList.deliverablesList deliverables=action.getDeliverables(true,false) canValidate=true canEdit=candit  isReportingActive=reportingActive namespace="/projects" defaultAction="${(crpSession)!}/deliverable"/]
                     
          <div class="text-right">
            [#if canEdit && action.hasPermission("addDeliverable")]
            <div class="addDeliverable button-blue"><a  href="[@s.url namespace="/${currentSection}" action='${(crpSession)!}/addNewDeliverable'] [@s.param name="projectID"]${projectID}[/@s.param][/@s.url]">
              <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addDeliverable" /]
            </a></div>
            [/#if]
          </div>
          [#if action.getDeliverables(false,false)?has_content]
            <h3 class="subTitle headTitle">Completed deliverables</h3>
            <div class="deliverables-extended-version" data-toggle="modal" data-target=".completed-modal"><span class="glyphicon glyphicon-eye-open"></span></div>
            <span class="extended-simple-version" data-toggle="modal" data-target=".completed-modal">Extended version</span>
            <hr />
            [#-- Completed Extended table (Modal) --]
            <div class="modal fade extended-table-modal completed-modal" tabindex="-1" role="dialog" aria-labelledby="extendedTableModal" aria-hidden="true">
              <div class="modal-dialog modal-lg">
                <div class="modal-content">
                  <h3 class="subTitle headTitle">Completed deliverables</h3>
                  <hr />
                    [@deliverableList.deliverablesListExtended deliverables=action.getDeliverables(false,false) canValidate=true canEdit=candit namespace="/projects" defaultAction="${(crpSession)!}/deliverable"/]
                </div>
              </div>
            </div>
            [#-- Simple table --]
            [@deliverableList.deliverablesList deliverables=action.getDeliverables(false,false) canValidate=true canEdit=candit isReportingActive=reportingActive namespace="/projects" defaultAction="${(crpSession)!}/deliverable"/]
          [/#if]

          [#if action.getDeliverables(false,true)?has_content]
            <h3 class="subTitle headTitle">Cancelled deliverables</h3>
            <hr />
            <div style="">[@deliverableList.deliverablesList deliverables=action.getDeliverables(false,true) canValidate=true canEdit=candit namespace="/projects" defaultAction="${(crpSession)!}/deliverable"/]</div> 
          [/#if]
          <input type="hidden" name="projectID" value="${projectID}" />
        [/@s.form] 
      </div>
    </div>  
</section>

  
[#include "/WEB-INF/crp/pages/footer.ftl"]
