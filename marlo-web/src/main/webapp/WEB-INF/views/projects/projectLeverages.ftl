[#ftl]
[#assign title = "Project Leverage" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/global/fieldsValidation.js","${baseUrl}/js/projects/projectLeverages.js", "${baseUrl}/js/global/autoSave.js"] /]
[#assign customCSS = ["${baseUrl}/css/projects/projectLeverages.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "leverages" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"leverage", "nameSpace":"/projects", "action":""}
] /]




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
           
          
          <h3 class="headTitle">[@s.text name="Leverages" /]</h3>
          
          <div class="leverage-list simpleBox" listname="project.openProjectActivities">
          [#if project.leverages?has_content]
            [#list project.leverages as leverage]
              [@leverageMacro leverage=leverage name="project.leverages"  index=leverage_index  /]
            [/#list]
          [#else]
            [@leverageMacro leverage={} name="project.leverages"  index=0  /]
          [/#if]
          </div>
          
          <div class="text-right">
            <div class="addLeverage button-blue text-right"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="Add a new leverage"/]</div>
          </div>
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
             
         
          [/@s.form] 
      </div> 
    </div> 
</section>

[#-- Activity Template --]
[@leverageMacro leverage={} name="project.leverages"  index=-1 template=true/]

  
[#include "/WEB-INF/global/pages/footer.ftl"]

[#macro leverageMacro leverage name index template=false]
  [#assign leverageCustomName = "${name}[${index}]" /]
  <div id="leverage-${template?string('template', index)}" class="leverage borderBox form-group" style="position:relative; display:${template?string('none','block')}">
  
  <div class="leftHead">
      <span class="index">${index+1}</span>
    </div>
    [#-- Remove Button --]
    [#if editable]
    <div class="removeLeverage removeIcon" title="Remove leverage"></div>
    [/#if]
    <input type="hidden" class="leverageId" name="${leverageCustomName}.id" value="${(leverage.id)!}"/>
  <div class="form-group">
    [#-- title --] 
    [@customForm.input name="${leverageCustomName}.title" value="${(leverage.title)!}" type="text" i18nkey="Title"  placeholder="" className="limitWords-15" required=true editable=editable /]
  </div>
   
  <div class="form-group">
    [#-- Partner name --]
    [@customForm.select name="${leverageCustomName}.institution.id" label=""  i18nkey="Partner name" listName="allInstitutions"  multiple=false required=true  className="partnerSelect form-control input-sm " editable=editable/]
  </div>
  
  <div class="form-group">
  <div class="row">  
    [#-- Type select --]
    <div class="col-md-6">
    [@customForm.select name="${leverageCustomName}.crpProgram.id" label=""  i18nkey="Flagship" listName="flagships"   multiple=false required=true  className="flagshipSelect form-control input-sm " editable=editable/]
    </div> 
    [#-- SubType select --]
    <div class="col-md-6">
    [@customForm.input name="${leverageCustomName}.budget" value="${(leverage.budget)!}"  i18nkey="Budget"  required=true  className="buudgetInput form-control input-sm " editable=editable/]
    </div>
  </div>
  </div>
   
  </div>
[/#macro]

