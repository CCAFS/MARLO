[#ftl]
[#assign title = "Project Activities" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectActivities.js", "${baseUrl}/js/global/autoSave.js"] /]
[#assign customCSS = ["${baseUrl}/css/projects/projectActivities.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "activities" /]

[#assign breadCrumb = [
  {"label":"activities", "nameSpace":"/projects", "action":"${(crpSession)!}/activities"}]/]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container">
  <div class="helpMessage"><img src="${baseUrl}/images/global/icon-help.png" /><p> </p></div> 
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
           
          
          <h3 class="headTitle">Project Activities</h3>
          
          <div class="activitiesOG-content simpleBox">
          <h4 class="subTitle headTitle">On going activities</h4>
          <hr />
          [#if project.openProjectActivities?has_content]
            [#list project.openProjectActivities as activity]
                [@projectActivityMacro element=activity name="project.openProjectActivities"  index=activity_index isActive=true /]
            [/#list]
          [/#if]
          </div>
          [#if editable && canEdit]
            <div id="addPartnerBlock" class="addPerson text-right">
              <div class="button-blue  addActivity"><span class="glyphicon glyphicon-plus-sign"></span> [@s.text name="form.buttons.addActivity" /]</div>
            </div>
          [/#if]
          
          [#if project.closedProjectActivities?has_content]
          <div class="activitiesC-content simpleBox">
            <h4 class="subTitle headTitle">Completed Activities</h4>
            <hr />
            [#list project.closedProjectActivities as activity]
                [@projectActivityMacro element=activity name="project.closedProjectActivities"  index=activity_index isActive=false /]
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
[@projectActivityMacro element={} name=""  index=0 isTemplate=true isActive=true/]

[#-- Activity Template --]
[@deliverablesMacro element={} name="" index=0 isTemplate=true /]


  
[#include "/WEB-INF/global/pages/footer.ftl"]

[#macro projectActivityMacro element name index=0 isTemplate=false isActive=false]
  [#assign customName = "${name}[${index}]" /]
    <div id="projectActivity-${isTemplate?string('template',(element.id)!)}" class="projectActivity expandableBlock borderBox"  style="display:${isTemplate?string('none','block')}">
    <div class="activityIndex"><span>Activity [#if element.id?? && element.id?number != -1]${(element.id)!}[/#if]</span></div>
    [#if isActive && editable] [#--&& (isTemplate) --]
      <div class="removeLink">
        <div id="removeActivity" class="removeActivity removeElement removeLink" title="[@s.text name='projectActivities.removeActivity' /]"></div>
      </div>
    [/#if]
    [#-- Partner Title --]
    <div class="blockTitle closed">
      ${(element.title)!'New Activity'}
    <div class="clearfix"></div>
    </div>
    
    <div class="blockContent" style="display:none">
      [#-- Title --]
      <div class="form-group">
        [@customForm.input name="${customName}.title" value="${(element.title)!'New Activity'}" type="text" i18nkey="Title"  placeholder="" className="activityTitle limitWords-15" required=true editable=editable /]
        <input class="activityId" type="hidden" name="${customName}.id" value="${(element.id)!-1}" />
      </div>
      [#-- Description --]
      <div class="form-group">
        [@customForm.textArea  name="${customName}.description" i18nkey="Activity description" value="${(element.description)!}" required=true className="limitWords-150 activityDescription" editable=editable /]
      </div>
      <div class="form-group row">  
        [#-- Start Date --]
        <div class="col-md-6">
          [@customForm.input name="${customName}.startDate" i18nkey="Start date" className="startDate" type="text" disabled=!editable  required=true editable=editable /]
        </div> 
        [#-- End Date --]
        <div class="col-md-6">
         [@customForm.input name="${customName}.endDate" i18nkey="End date" className="endDate" type="text" disabled=!editable required=true editable=editable  /]
        </div>
        <div class="clearfix"></div>
      </div>
      
      [#-- Activity leader --]
      <div class="form-group">
        [@customForm.select name="${customName}.projectPartnerPerson.id" label=""  i18nkey="Activity leader" listName="partnerPersons" keyFieldName="id"  displayFieldName="composedName"  multiple=false required=true  className=" activityLeader" disabled=!editable/]
      </div>
      
      [#-- Activity status --]
      <div class="form-group">
        [@customForm.select name="${customName}.activityStatus" label=""  i18nkey="Activity status" listName="status" keyFieldName=""  displayFieldName=""  multiple=false required=true  className=" activityStatus" disabled=!editable/]
      </div>
      
      [#if reportingActive]
      [#-- Progress in reporting cycle --]
      [@customForm.textArea  name="${customName}.activityProgress" i18nkey="Describe overall activity or progress made during this reporting cycle" value="${(element.progressDescription)!}" required=true className="limitWords-150 progressDescription" editable=editable /]
      [/#if]
      
      [#-- Activity deliverables --]
      <label for="" class="${editable?string('editable', 'readOnly')}">Deliverables in this activity:</label>
      <div class="deliverableWrapper simpleBox form-group">
        [#if element.deliverables?has_content]
          [#list element.deliverables as deliverable]
              [@deliverablesMacro element=deliverable name="${customName}.deliverables"  index=deliverable_index /]
          [/#list]
        [/#if]
      </div>
      <div class="form-group">
        [@customForm.select name="" label=""  i18nkey="Select to add a deliverable" listName="project.projectDeliverables" keyFieldName="id"  displayFieldName="title"  multiple=false required=true  className=" deliverableList" disabled=!editable/]
      </div>
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
    <span class="name">${(element.deliverable.title)!'null'}</span>
    <div class="clearfix"></div>
  </div>
[/#macro]

