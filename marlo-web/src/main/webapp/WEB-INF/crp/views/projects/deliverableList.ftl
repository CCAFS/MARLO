[#ftl]
[#assign title = "Project Deliverables" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
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
  {"text":"P${project.id}", "nameSpace":"/projects", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
  {"label":"deliverableList", "nameSpace":"/projects", "action":""}
]/]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
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
              <img src="${baseUrl}/global/images/FAIR_Principles_in_MARLO_20170919.png" alt="" width="100%" />
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
            <img src="${baseUrl}/global/images/FAIR_Principles_in_MARLO_20170919.png" alt="" width="100%" />
          </div>
          
          
          <h3 class="subTitle headTitle">On going deliverables</h3>
          <span class="extended-simple-version" data-toggle="modal" data-target=".ongoing-modal">Extended version</span>
          <div class="deliverables-extended-version" data-toggle="modal" data-target=".ongoing-modal"><span class="glyphicon glyphicon-eye-open"></span></div>
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
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h3 class="subTitle headTitle">On going deliverables</h3>
                <hr />
                  [@deliverableList.deliverablesListExtended deliverables=action.getDeliverables(true,false) canValidate=true canEdit=candit FAIRColumn=false namespace="/projects" defaultAction="${(crpSession)!}/deliverable"/]
              </div>
            </div>
          </div>
          [#-- Simple table --]
           [@deliverableList.deliverablesList deliverables=action.getDeliverables(true,false) canValidate=true canEdit=candit  isReportingActive=reportingActive namespace="/projects" defaultAction="${(crpSession)!}/deliverable"/]
                     
          [#if action.getDeliverables(false,false)?has_content]
            <div class="deliverables-table-header">
              <h3 class="subTitle headTitle">Completed deliverables</h3>
              <span class="extended-simple-version" data-toggle="modal" data-target=".completed-modal">Extended version</span>
              <div class="deliverables-extended-version" data-toggle="modal" data-target=".completed-modal"><span class="glyphicon glyphicon-eye-open"></span></div>
            </div>
            <hr />
            [#-- Completed Extended table (Modal) --]
            <div class="modal fade extended-table-modal completed-modal" tabindex="-1" role="dialog" aria-labelledby="extendedTableModal" aria-hidden="true">
              <div class="modal-dialog modal-lg">
                <div class="modal-content">
                  <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
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
            <div class="deliverables-table-header">
              <h3 class="subTitle headTitle">Cancelled deliverables</h3>
              <span class="extended-simple-version" data-toggle="modal" data-target=".cancelled-modal">Extended version</span>
              <div class="deliverables-extended-version" data-toggle="modal" data-target=".cancelled-modal"><span class="glyphicon glyphicon-eye-open"></span></div>
            </div>
            <hr />
            [#-- Cancelled Extended table (Modal) --]
            <div class="modal fade extended-table-modal cancelled-modal" tabindex="-1" role="dialog" aria-labelledby="extendedTableModal" aria-hidden="true">
              <div class="modal-dialog modal-lg">
                <div class="modal-content">
                  <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                  <h3 class="subTitle headTitle">Cancelled deliverables</h3>
                  <hr />
                    [@deliverableList.deliverablesListExtended deliverables=action.getDeliverables(false,true) canValidate=true canEdit=candit namespace="/projects" defaultAction="${(crpSession)!}/deliverable"/]
                </div>
              </div>
            </div>
            [@deliverableList.deliverablesList deliverables=action.getDeliverables(false,true) canValidate=true canEdit=candit namespace="/projects" defaultAction="${(crpSession)!}/deliverable"/] 
          [/#if]
          <input type="hidden" name="projectID" value="${projectID}" />
          
          [#-- Add Deliverable Button --]
          [#if canEdit && action.hasPermission("addDeliverable")]
          <div class="buttons">
            <div class="buttons-content">
              <div class="addDeliverable button-blue"><a  href="[@s.url namespace="/${currentSection}" action='${(crpSession)!}/addNewDeliverable'][@s.param name="projectID"]${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
                <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addDeliverable" /]
              </a></div>
              <div class="clearfix"></div>
            </div>
          </div>
          [/#if]
          
        [/@s.form] 
      </div>
    </div>  
</section>

  
[#include "/WEB-INF/global/pages/footer.ftl"]
