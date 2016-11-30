[#ftl]
[#assign title = "Project Activities" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/global/fieldsValidation.js","${baseUrl}/js/projects/projectActivities.js", "${baseUrl}/js/global/autoSave.js"] /]
[#assign customCSS = ["${baseUrl}/css/projects/projectActivities.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "activities" /]

[#assign breadCrumb = [
  {"label":"activities", "nameSpace":"/projects", "action":"${(crpSession)!}/activities"}]/]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/images/global/icon-help.jpg" />
    <p class="col-md-10">[#if reportingActive] [@s.text name="project.activities.help2" /] [#else] [@s.text name="project.activities.help1" /] [/#if] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>
    
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Activities Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/views/projects/messages-projects.ftl" /]
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
           
          
          <h3 class="headTitle">[@s.text name="project.activities.title" /]</h3>
          
          <div class="activitiesOG-content simpleBox" listname="project.openProjectActivities">
          <h4 class="subTitle headTitle">[@s.text name="project.activities.onGoing" /]</h4>
          <hr />
          [#if action.getActivities(true)?has_content]
            [#list action.getActivities(true) as activity]
                [@projectActivityMacro element=activity name="project.projectActivities"  index=action.getIndexActivities((activity.id)!-1) isActive=true /]
            [/#list]
          [/#if]
          </div>
          [#if editable && canEdit]
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
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
             
         
          [/@s.form] 
      </div> 
    </div> 
</section>

[#-- Activity Template --]
[@projectActivityMacro element={} name="project.projectActivities"  index=-1 isTemplate=true isActive=true/]

[#-- Activity Template --]
[@deliverablesMacro element={} name="project.projectActivities[-1].deliverables" index=-1 isTemplate=true /]


  
[#include "/WEB-INF/global/pages/footer.ftl"]

[#macro projectActivityMacro element name index=0 isTemplate=false isActive=false]
  [#assign customName = "${name}[${index}]" /]
    <div id="projectActivity-${isTemplate?string('template',(element.id)!)}" class="projectActivity expandableBlock borderBox"  style="display:${isTemplate?string('none','block')}">
    <div class="activityIndex"><span>[@s.text name="project.activities.index" /] [#if element.id?? && element.id?number != -1]${(element.id)!}[/#if]</span></div>
    [#if isActive && editable] [#--&& (isTemplate) --]
      <div class="removeLink">
        <div id="removeActivity" class="removeActivity removeElement removeLink" title="[@s.text name='projectActivities.removeActivity' /]"></div>
      </div>
    [/#if]
    [#-- Partner Title --]
    <div class="blockTitle closed">
    [#if element.title?has_content]
    ${(element.title)!'New Activity'}
    [#else]
    New Activity
    [/#if]
      
    <div class="clearfix"></div>
    </div>
    
    <div class="blockContent" style="display:none">
      [#-- Title --]
      <div class="form-group">
        [@customForm.input name="${customName}.title" value="${(element.title)!'New Activity'}" type="text" i18nkey="project.activities.inputTitle"  placeholder="" className="activityTitle limitWords-15" required=true editable=editable /]
        <input class="activityId" type="hidden" name="${customName}.id" value="${(element.id)!-1}" />
        <span class="index hidden">${index}</span>
      </div>
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
        [@customForm.select name="${customName}.projectPartnerPerson.id" label=""  i18nkey="project.activities.inputLeader" listName="partnerPersons" keyFieldName="id"  displayFieldName="composedName"  multiple=false required=true  className=" activityLeader" editable=editable/]
      </div>
      
      [#-- Activity status --]
      <div class="form-group">
        [@customForm.select name="${customName}.activityStatus" label=""  i18nkey="project.activities.inputStatus" listName="status" keyFieldName=""  displayFieldName=""  multiple=false required=true header=false className=" activityStatus" editable=editable/]
      </div>
      
      [#if reportingActive]
      [#-- Progress in reporting cycle --]
      [@customForm.textArea  name="${customName}.activityProgress" i18nkey="Describe overall activity or progress made during this reporting cycle" value="${(element.progressDescription)!}" required=true className="limitWords-150 progressDescription" editable=editable /]
      [/#if]
      
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
  <div id="deliverableActivity-${isTemplate?string('template',(projectActivity.id)!)}" class="deliverableActivity  borderBox"  style="display:${isTemplate?string('none','block')}">
    [#if editable]<div class="removeDeliverable removeIcon" title="Remove deliverable"></div>[/#if] 
    <input class="id" type="hidden" name="${deliverableCustomName}.deliverable.id" value="${(element.deliverable.id)!-1}" />
    <input class="idTable" type="hidden" name="${deliverableCustomName}.id" value="${(element.id)!-1}" />
    <input class="title" type="hidden" name="${deliverableCustomName}.deliverable.title" value="${(element.deliverable.title)!'null'}" />
    <span class="name">${(element.deliverable.composedName)!'null'}</span>
    <div class="clearfix"></div>
  </div>
[/#macro]

