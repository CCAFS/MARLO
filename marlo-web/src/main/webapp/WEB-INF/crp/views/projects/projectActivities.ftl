[#ftl]
[#assign title = "Cluster Activities" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [
  "${baseUrlCdn}/global/js/fieldsValidation.js",
  "${baseUrlMedia}/js/projects/projectActivities.js?20210208"
  ] 
/]
[#-- ,  
  "${baseUrlCdn}/global/js/autoSave.js?20210616"  --]
[#assign customCSS = ["${baseUrlMedia}/css/projects/projectActivities.css?20230106"] /]
[#assign currentStage = "activities" /]
[#assign hideJustification = true /]

[#if !action.isAiccra()]
  [#assign currentSection = "projects" /]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"${currentSection}", "action":"${(crpSession)!}/projectsList"},
    {"text":"P${project.id}", "nameSpace":"${currentSection}", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
    {"label":"activities", "nameSpace":"${currentSection}", "action":""}
  ] /]
[#else]
  [#assign currentSection = "clusters" /]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"${currentSection}", "action":"${(crpSession)!}/projectsList"},
    {"text":"C${project.id}", "nameSpace":"${currentSection}", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
    {"label":"activities", "nameSpace":"${currentSection}", "action":""}
  ] /]
[/#if]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]


[#import "/WEB-INF/crp/macros/relationsPopupMacro.ftl" as popUps /]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

<!--
<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-help.jpg" />
    <p class="col-md-10">[#if reportingActive] [@s.text name="project.activities.help2" /] [#else] [@s.text name="project.activities.help1" /] [/#if] </p>
  </div>
  <div style="display:none" class="viewMore closed"></div>
</div>
-->
<div class="animated flipInX container  viewMore-block containerAlertMargin">
  <div class=" containerAlert alert-leftovers alertColorBackgroundInfo " id="containerAlert">
    <div class="containerLine alertColorInfo"></div>
    <div class="containerIcon">
      <div class="containerIcon alertColorInfo">
        <img src="${baseUrlCdn}/global/images/icon-question.png" />    
      </div>
    </div>
    <div class="containerText col-md-12">
      <p class="alertText">
        [#if reportingActive] [@s.text name="project.activities.help2" /] [#else] [@s.text name="project.activities.help1" /] [/#if] 
      </p>
    </div>
    <div  class="viewMoreCollapse closed"></div>
  </div>
</div>


[#if (!availabePhase)!false]
  [#include "/WEB-INF/crp/views/projects/availability-projects.ftl" /]
[#else]
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Activities Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/projects/messages-projects.ftl" /]

        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]

          <h3 class="headTitle">[@s.text name="project.activities.title" /]</h3>

          [#if deliverablesMissingActivity?size > 0]
            <div class="container helpText viewMore-block">
              <div class="helpMessage infoText2 col-md-8">
                [#-- <div  class="removeHelp"><span class="glyphicon glyphicon-remove"></span></div> --]
                <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-transparent-warning.png" />
                
                  [@s.text name="deliverable.missing.activity.alert" ][@s.param]${deliverablesMissingActivity?size}[/@s.param] [/@s.text]
                
              </div>
              <div class="viewMore closed"></div>
            </div>
            <div class="pull-right">
              [@popUps.deliverablesMissingActivities deliverables=deliverablesMissingActivity /]
            </div>
            <br>
          [/#if]

          <div class="activitiesOG-content simpleBox" listname="project.openProjectActivities">
          <h4 class="subTitle headTitle">[@s.text name="project.activities.onGoing" /]</h4>
          <hr />
          [#if action.getActivities(true)?has_content]
            [#list action.getActivities(true) as activity]
                [@projectActivityMacro element=activity name="project.projectActivities"  index=action.getIndexActivities((activity.id)!-1) isActive=true /]
            [/#list]
          [/#if]
          </div>
          [#if editable && canEdit && (action.canAccessSuperAdmin() || action.canAcessCrpAdmin() || action.isRole("PC") || action.isRole("PL"))]
            <div id="addPartnerBlock" class="addPerson text-right">
              <div class="button-blue  addActivity"><span class="glyphicon glyphicon-plus-sign"></span> [@s.text name="form.buttons.addActivity" /]</div>
            </div>
          [/#if]

          [#if action.getActivities(false)?has_content]
          <div class="activitiesC-content simpleBox" listname="project.closedProjectActivities">
            <h4 class="subTitle headTitle">[@s.text name="project.activities.completed" /]</h4>
            <hr />
            [#list action.getActivities(false) as activity]
                [@projectActivityMacro element=activity name="project.projectActivities"  index=action.getIndexActivities((activity.id)!-1) isActive=false /]
            [/#list]
          </div>
          [/#if]


          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]


          [/@s.form]
      </div>
    </div>
</section>
[/#if]

[#-- Activity Template --]
[@projectActivityMacro element={} name="project.projectActivities"  index=-1 isTemplate=true isActive=true/]

[#-- Activity Template --]
[@deliverablesMacro element={} name="project.projectActivities[-1].deliverables" index=-1 isTemplate=true /]


<!-- Confirm removeactivity Modal -->
<div class="modal fade" id="removeactivityModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="exampleModalLabel" style="font-size: 1.3em; font-weight: 700;">Are you sure you want to remove this activity?</h5>
        <br>

      </div>
      <div class="modal-body">
        <p><strong style="font-size: 1.2em;">Title: </strong><span id="activityName"></span></p>

      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">No</button>
        <button type="button" class="btn btn-danger removeActivity" data-dismiss="modal">Yes, remove</button>
      </div>
    </div>
  </div>
</div>




[#include "/WEB-INF/global/pages/footer.ftl"]

[#macro projectActivityMacro element name index=0 isTemplate=false isActive=false]
  [#assign customName = "${name}[${index}]" /]
    <div id="projectActivity-${isTemplate?string('template',(element.id)!)}" class="projectActivity expandableBlock borderBox"  style="display:${isTemplate?string('none','block')}">
    [#--
    <div class="activityIndex">
      <span>[@s.text name="project.activities.index" /] [#if element.id?? && element.id?number != -1]${(element.id)!}[/#if]</span>
    </div>
    --]
    [#if isActive && editable && (action.canAccessSuperAdmin() || action.canAcessCrpAdmin() || action.isRole("PC") || action.isRole("PL"))] [#--&& (isTemplate) --]
      <div class="removeLink">
        <div id="removeActivity" class="removeActivityBtnInList removeElement removeLink" data-toggle="modal" data-target="#removeactivityModal" title="[@s.text name='project.activities.removeActivity' /]"></div>
      </div>
    [/#if]
    [#-- Partner Title --]
    <div class="blockTitle closed">
      [#if element.title?has_content]${(element.activityTitle.title)!'New Activity'}[#else]New Activity[/#if]
      <div class="clearfix"></div>
    </div>

    <div class="blockContent" style="display:none">
      [#-- Title --]
      
      [#if !action.isAiccra()]
      <div class="form-group">
        [@customForm.input name="${customName}.title" value="${(element.activityTitle.title)!'New Activity'}" type="text" i18nkey="project.activities.inputTitle"  placeholder="" className="activityTitle limitWords-30" required=true editable=false /]
      </div>
      [/#if]
        <input class="activityId" type="hidden" name="${customName}.id" value="${(element.id)!-1}" />
        <input class="activityId" type="hidden" name="${customName}.composeID" value="${(element.composeID)!}" />
        <span class="index hidden">${index}</span>
     
      
      [#-- Activity Title --]
      [#if action.isAiccra()]
        <div class="form-group">
          [@customForm.select name="${customName}.activityTitle.id" label="" className="activityTitle" i18nkey="project.activities.inputTitle" listName="activityTitles" keyFieldName="id" displayFieldName="title" multiple=false required=true editable=(action.canAccessSuperAdmin() || action.isRole("PC") || action.isRole("PL")) && isActive/]
        </div>
      [/#if]
      [#-- Description --]
      <div class="form-group">
        [@customForm.textArea  name="${customName}.description" i18nkey="project.activities.inputDescription" value="${(element.description)!}" required=true className="limitWords-150 activityDescription" editable=editable /]
      </div>
      <div class="form-group row">
        [#-- Start Date --]
        <div class="col-md-6">
          [@customForm.input name="${customName}.startDate" i18nkey="project.activities.inputStartDate" className="startDate" type="text" disabled=!editable  required=true editable=editable /]
        </div>
        [#-- End Date --]
        <div class="col-md-6">
         [@customForm.input name="${customName}.endDate" i18nkey="project.activities.inputEndDate" className="endDate" type="text" disabled=!editable required=true editable=editable  /]
        </div>
        <div class="clearfix"></div>
      </div>

      [#-- Activity leader --]
      <div class="form-group">
        [@customForm.select name="${customName}.projectPartnerPerson.id" label=""  i18nkey="project.activities.inputLeader" listName="partnerPersons" keyFieldName="id"  displayFieldName="composedInstitutionName"  multiple=false required=true  className=" activityLeader" editable=editable/]
      </div>

      <div class="row activityStatusBlock ${reportingActive?string('fieldFocus','')}">
      [#-- Activity status --]
      <div class="col-md-12 form-group">
        [@customForm.select name="${customName}.activityStatus" label=""  i18nkey="project.activities.inputStatus" listName="status" keyFieldName=""  displayFieldName=""  multiple=false required=true header=false className=" activityStatus" editable=editable/]
      </div>

      [#if reportingActive]
      [#-- Progress in reporting cycle --]
      <div class="statusDescriptionBlock col-md-12">
        [@customForm.textArea  name="${customName}.activityProgress" i18nkey="project.activities.statusJustification.status${(element.activityStatus)!'NotSelected'}" value="${(element.activityProgress)!}" required=true className="limitWords-150 progressDescription" editable=editable /]
        <div id="statusesLabels" style="display:none">
          <div id="status-2">[@s.text name="project.activities.statusJustification.status2" /]:<span class="red">*</span></div>[#-- Ongoing("2", "On-going") --]
          <div id="status-3">[@s.text name="project.activities.statusJustification.status3" /]:<span class="red">*</span></div>[#-- Complete("3", "Complete") --]
          <div id="status-4">[@s.text name="project.activities.statusJustification.status4" /]:<span class="red">*</span></div>[#-- Extended("4", "Extended") --]
          <div id="status-5">[@s.text name="project.activities.statusJustification.status5" /]:<span class="red">*</span></div>[#-- Cancelled("5", "Cancelled") --]
        </div>
      </div>
      [/#if]
      </div>

      [#-- Activity deliverables --]
      <label for="" class="${editable?string('editable', 'readOnly')}">[@s.text name="project.activities.deliverableList" /]:</label>
      <div class="deliverableWrapper simpleBox form-group" listname="${customName}.deliverables">
        [#if element.deliverables?has_content]
          [#list element.deliverables as deliverable]
              [@deliverablesMacro element=deliverable name="${customName}.deliverables"  index=deliverable_index /]
          [/#list]
        [/#if]
        <p class="text-center inf" style="display:${(element.deliverables?has_content)?string('none','block')}">[@s.text name="project.activities.notDeliverables" /]</p>
      </div>
      [#if editable]
      <div class="form-group">
        [@customForm.select name="" label=""  i18nkey="project.activities.deliverableSelect" listName="project.projectDeliverables" keyFieldName="id"  displayFieldName="composedName"  multiple=false required=false  className=" deliverableList" disabled=!editable/]
      </div>
      [/#if]
    </div>

  </div>
[/#macro]

[#macro deliverablesMacro element name index=-1 isTemplate=false]
  [#assign deliverableCustomName = "${name}[${index}]" /]
  <div id="deliverableActivity-${isTemplate?string('template',(projectActivity.id)!)}" class="deliverableActivity"  style="display:${isTemplate?string('none','block')}">
    [#if editable]<div class="removeDeliverable removeIcon" title="Remove deliverable"></div>[/#if]
    <input class="id" type="hidden" name="${deliverableCustomName}.deliverable.id" value="${(element.deliverable.id)!-1}" />
    <input class="idTable" type="hidden" name="${deliverableCustomName}.id" value="${(element.id)!-1}" />
    <input class="title" type="hidden" name="${deliverableCustomName}.deliverable.devliverableInfo.title" value="${(element.deliverable.devliverableInfo.title)!'null'}" />
    <span class="name">${(element.deliverable.composedName)!'null'}</span>
    <div class="clearfix"></div>
  </div>
[/#macro]
