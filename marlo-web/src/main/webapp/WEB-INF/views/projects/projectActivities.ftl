[#ftl]
[#assign title = "Project Activities" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectActivities.js"] /]
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
          
          
          <h4 class="subTitle headTitle">Activities on going</h4>
          <div class="activitiesOG-content borderBox">
          
          </div>
          [#if editable && canEdit]
            <div id="addPartnerBlock" class="addPerson text-right">
              <div class="button-blue  addActivity"><span class="glyphicon glyphicon-plus-sign"></span> [@s.text name="form.buttons.addActivity" /]</div>
            </div>
          [/#if]
    
          <h4 class="subTitle headTitle">Completed Activities</h4>
          <div class="activitiesC-content">
          
          </div>
          
            
      </div> 
           
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
             
         
          [/@s.form] 
      </div>
    </div>  
</section>

[#-- Activity Template --]
[@projectActivityMacro element={} name=""  index=0 isTemplate=true /]
  
[#include "/WEB-INF/global/pages/footer.ftl"]

[#macro projectActivityMacro element name index=-1 isTemplate=false]
  
    <div id="projectActivity-${isTemplate?string('template',(projectActivity.id)!)}" class="projectActivity expandableBlock borderBox"  style="display:${isTemplate?string('none','block')}">
    <div class="activityIndex"><span>Activity #${index}</span></div>
    [#-- Partner Title --]
    <div class="blockTitle closed">
      [#-- Title --]
      <div class="col-md-12 form-group">
        [@customForm.input name="deliverable.title" value="${(element.title)!'New Project Partner'}" type="text" i18nkey="Title"  placeholder="" className="activityTitle limitWords-15" required=true editable=editable /]
      </div>
    </div>
    
    <div class="blockContent" style="display:block">
      <hr />
      
      [@customForm.textArea  name="${customName}.description" i18nkey="Activity description" value="${(element.description)!}" required=true className="limitWords-150 activityDescription" editable=editable /]
      
      <div class="form-group row">  
        [#-- Start Date --]
        <div class="col-md-6">
          [@customForm.input name="activities.startDate" type="text" disabled=!editable  required=true editable=editable && action.hasPermission("startDate")  /]
        </div> 
        [#-- End Date --]
        <div class="col-md-6">
         [@customForm.input name="activities.endDate" type="text" disabled=!editable required=true editable=editable && action.hasPermission("endDate")  /]
        </div>
      </div>
      
      [#-- Activity leader --]
      <div class="col-md-12">
        [@customForm.select name="activities.leader" label=""  i18nkey="Activity leader" listName="activityLeaders" keyFieldName=""  displayFieldName=""  multiple=false required=true  className=" activityLeader" disabled=!editable/]
      </div>
      
      [#-- Activity status --]
      <div class="col-md-12">
        [@customForm.select name="activities.status" label=""  i18nkey="Activity status" listName="activityStatus" keyFieldName=""  displayFieldName=""  multiple=false required=true  className=" activityStatus" disabled=!editable/]
      </div>
      
      [#-- Progress in reporting cycle --]
      [@customForm.textArea  name="${customName}.progressDescription" i18nkey="Describe overall activity or progress made during this reporting cycle" value="${(element.progressDescription)!}" required=true className="limitWords-150 activityDescription" editable=editable /]
      
      [#-- Activity deliverables --]
      <div class="col-md-12">
        [@customForm.select name="activities.deliverables" label=""  i18nkey="Select deliverables" listName="deliverablesList" keyFieldName=""  displayFieldName=""  multiple=false required=true  className=" deliverableList" disabled=!editable/]
      </div>
    </div>
  
  </div>
[/#macro]