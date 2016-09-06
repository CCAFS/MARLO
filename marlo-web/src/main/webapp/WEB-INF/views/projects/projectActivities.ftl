[#ftl]
[#assign title = "Project Activities" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectActivities.js"] /]
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
           
          
          <h3 class="headTitle">[@s.text name="projectActivities.title" /]</h3>
          
          
          
          <div class="activitiesOG-content simpleBox">
          <h4 class="subTitle headTitle">Activities on going</h4>
          <hr />
          [#if project.projectActivities?has_content]
            [#list project.projectActivities as activity]
              [#if activity.activityStatus==4 || activity.activityStatus==2]
                [@projectActivityMacro element=activity name="activity"  index=index /]
              [/#if]
            [/#list]
          [/#if]
          </div>
          [#if editable && canEdit]
            <div id="addPartnerBlock" class="addPerson text-right">
              <div class="button-blue  addActivity"><span class="glyphicon glyphicon-plus-sign"></span> [@s.text name="form.buttons.addActivity" /]</div>
            </div>
          [/#if]
    
          
          <div class="activitiesC-content simpleBox">
          <h4 class="subTitle headTitle">Completed Activities</h4>
          <hr />
          [#if project.projectActivities?has_content]
            [#list project.projectActivities as activity]
              [#if activity.activityStatus==3 || activity.activityStatus==5]
                [@projectActivityMacro element=activity name="activity"  index=index  readMode=true/]
              [/#if]
            [/#list]
          [/#if]
          </div>
          
            
      </div> 
           
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
             
         
          [/@s.form] 
      </div> 
</section>

[#-- Activity Template --]
[@projectActivityMacro element={} name=""  index=0 isTemplate=true readMode=false/]
  
[#include "/WEB-INF/global/pages/footer.ftl"]

[#macro projectActivityMacro element name index=0 isTemplate=false readMode=false]
  [#assign customName = "${name}[${index}]" /]
    <div id="projectActivity-${isTemplate?string('template',(projectActivity.id)!)}" class="projectActivity expandableBlock borderBox"  style="display:${isTemplate?string('none','block')}">
    <div class="activityIndex"><span><b>Activity #${index+1}</b></span></div>
    [#if !readMode ] [#--&& (isTemplate) --]
      <div class="removeLink"><div id="removeActivity" class="removeActivity removeElement removeLink" title="[@s.text name="projectActivities.removeActivity" /]"></div></div>
    [/#if]
    [#-- Partner Title --]
    <div class="blockTitle closed">
      <b>${(element.title)!'New Activity'}</b>
    <div class="clearfix"></div>
    </div>
    
    <div class="blockContent" style="display:none">
      [#-- Title --]
      <div class="form-group">
        [@customForm.input name="${customName}.title" value="${(element.title)!'New Activity'}" type="text" i18nkey="Title"  placeholder="" className="activityTitle limitWords-15" required=true editable=!readMode /]
      </div>
      <div class="form-group">
        [@customForm.textArea  name="${customName}.description" i18nkey="Activity description" value="${(element.description)!}" required=true className="limitWords-150 activityDescription" editable=!readMode /]
      </div>
      <div class="form-group row">  
        [#-- Start Date --]
        <div class="col-md-6">
          [@customForm.input name="${customName}.startDate" className="startDate" type="text" disabled=!editable  required=true editable=!readMode /]
        </div> 
        [#-- End Date --]
        <div class="col-md-6">
         [@customForm.input name="${customName}.endDate" className="endDate" type="text" disabled=!editable required=true editable=!readMode  /]
        </div>
      </div>
      
      [#-- Activity leader --]
      <div class="form-group">
        [@customForm.select name="${customName}.leader" label=""  i18nkey="Activity leader" listName="partnerPersons" keyFieldName="id"  displayFieldName="composedName"  multiple=false required=true  className=" activityLeader" disabled=readMode/]
      </div>
      
      [#-- Activity status --]
      <div class="form-group">
        [@customForm.select name="${customName}.status" label=""  i18nkey="Activity status" listName="status" keyFieldName=""  displayFieldName=""  multiple=false required=true  className=" activityStatus" disabled=readMode/]
      </div>
      
      [#-- Progress in reporting cycle --]
      [@customForm.textArea  name="${customName}.activityProgress" i18nkey="Describe overall activity or progress made during this reporting cycle" value="${(element.progressDescription)!}" required=true className="limitWords-150 progressDescription" editable=!readMode /]
      
      [#-- Activity deliverables --]
      <label for="" class="${editable?string('editable', 'readOnly')}">Deliverables in this activity:</label>
      <div class="deliverableWrapper simpleBox form-group">
        <p class="center"> [@s.text name="projectDeliverable.partnership.emptyText" /] </p>
        <div id="deliverableActivity-template" class="deliverableActivity  borderBox" >
          [#if editable]<div class="removeDeliverable removeIcon" title="Remove deliverable"></div> [/#if]          
          <input class="id" type="hidden" name="" value="5545" />
          <span class="name">Example</span>
          <div class="clearfix"></div>
        </div>
      </div>
      <div class="form-group">
        [@customForm.select name="" label=""  i18nkey="Select to add a deliverable" listName="project.projectDeliverables" keyFieldName="id"  displayFieldName="title"  multiple=false required=true  className=" deliverableList" disabled=readMode/]
      </div>
    </div>
  
  </div>
[/#macro]

[#macro deliverablesMacro element name index=-1 isTemplate=false]  
  [#assign deliverableCustomName = "${name}[${index}]" /]
  <div id="deliverableActivity-${isTemplate?string('template',(projectActivity.id)!)}" class="deliverableActivity  borderBox"  style="display:${isTemplate?string('none','block')}">
    [#if editable]<div id="removeActivity" class="removeDeliverable removeElement removeLink" title="[@s.text name="projectActivities.removeActivity" /]"></div>[/#if] 
    <input class="id" type="hidden" name="${deliverableCustomName}.id" value="${(element.id)!-1}" />
    <span class="name">${(element.title)!'null'}</span>
    <div class="clearfix"></div>
  </div>
[/#macro]

