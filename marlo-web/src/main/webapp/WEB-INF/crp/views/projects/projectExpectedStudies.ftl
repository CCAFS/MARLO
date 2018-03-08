[#ftl]
[#assign title = "Project Expected Studies" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [
  "${baseUrl}/global/js/fieldsValidation.js",
  "${baseUrlMedia}/js/projects/projectExpectedStudies.js", 
  "${baseUrl}/global/js/autoSave.js"
  ] 
/]
[#assign customCSS = ["${baseUrlMedia}/css/projects/projectExpectedStudies.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "expectedStudies" /]
[#assign hideJustification = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"expectedStudies", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectExpectedStudies.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>


<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Expected Study Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/projects/messages-projects.ftl" /]
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
           
          <h3 class="headTitle">[@s.text name="projectExpectedStudies.title" /]</h3>
          <div class="simpleBox">
            <div class="expectedStudies-list" listname="">
            [#if project.expectedStudies?has_content]
              [#list project.expectedStudies as expectedStudy]
                [@expectedStudyMacro element=expectedStudy name="project.expectedStudies"  index=expectedStudy_index isEditable=editable  /]
              [/#list]
            [/#if]
            </div>
            [#if canEdit && editable]
            <div class="text-right">
              <div class="addExpectedStudy bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addExpectedStudy"/]</div>
            </div> 
            [/#if]
          </div>
          
          <hr />
          
          
          [#if project.sharedExpectedStudies?has_content]
            <h3 class="headTitle">Shared Expected Studies with this project </h3>
            <div class="simpleBox">
              <table class="table">
                <thead>
                  <tr>
                    <th class="col-md-1" scope="col" title="Project ID">ID</th>
                    <th scope="col">Planned topic of study</th>
                    <th scope="col">Type</th>
                    <th scope="col">Geographic scope</th>
                  </tr>
                </thead>
                <tbody>
                  [#list project.sharedExpectedStudies as expectedStudy]
                  [#assign expectedStudyURL][@s.url namespace="/projects" action="${crpSession}/expectedStudies.do"][@s.param name='projectID']${expectedStudy.project.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#assign]
                  <tr>
                    <td><a href="${expectedStudyURL}">P${expectedStudy.project.id}</a></td>
                    <td><a href="${expectedStudyURL}">${(expectedStudy.topicStudy)!'Untitled'}</a></td>
                    <td>${(expectedStudy.typeName)!'None'}</td>
                    <td>${(expectedStudy.scopeName)!'None'}</td>
                  </tr>
                  [/#list]
                </tbody>
              </table>
              
              
            </div>
          [/#if]
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
          
          <div class="clearfix"></div>
         
          [/@s.form] 
      </div> 
    </div> 
</section>

[#-- Expected Study Template --]
[@expectedStudyMacro element={} name="project.expectedStudies"  index=-1 template=true/]

[#-- Expected Study shared project Template --]
[@sharedProject element={} name="project.expectedStudies[-1].projects" index=-1 template=true /]

[#include "/WEB-INF/crp/pages/footer.ftl"]

[#macro expectedStudyMacro element name index template=false isEditable=true]
  [#local customName = "${name}[${index}]" /]
  <div id="expectedStudy-${template?string('template', index)}" class="expectedStudy borderBox form-group" style="position:relative; display:${template?string('none','block')}">
    
    [#-- Index --]
    <div class="leftHead"><span class="index">${index+1}</span></div>
    [#-- Remove Button --]
    [#if isEditable]<div class="removeExpectedStudy removeElement" title="Remove Expected Study"></div>[/#if]
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}"/> 
    <br />
    
    
    <div class="form-group"> 
      [#-- Title --] 
      [@customForm.input name="${customName}.topicStudy" i18nkey="expectedStudy.topicStudy"  placeholder="" className="limitWords-100" required=true editable=isEditable /]
    </div>
    
    <div class="form-group row"> 
      [#-- Type --] 
      <div class="col-md-6">
        [@customForm.select name="${customName}.type" label="" i18nkey="expectedStudy.type" listName="types"  required=true  className="" editable=isEditable/]
      </div>
      [#-- Geographic Scope --]
      <div class="col-md-6">
        [@customForm.select name="${customName}.scope" label=""  i18nkey="expectedStudy.scope" listName="scopes"  required=true  className="" editable=isEditable/]
      </div>
    </div>
    
    [#-- Relevant to Sub-IDO --] 
    <div class="form-group "> 
      [@customForm.select name="${customName}.srfSubIdo.id" label=""  i18nkey="expectedStudy.srfSubIdo" listName="subIdos"  required=true  className="" editable=isEditable/]
    </div>
    
    [#-- SRF target if appropriate --] 
    <div class="form-group "> 
      [@customForm.select name="${customName}.srfSloIndicator.id" label=""  i18nkey="expectedStudy.srfSloIndicator" listName="targets"  required=false  className="" editable=isEditable/]
    </div>
    
    [#-- Comments --] 
    <div class="form-group"> 
      [@customForm.textArea name="${customName}.comments" i18nkey="expectedStudy.comments"  placeholder="" className="limitWords-100" required=true editable=isEditable /]
    </div>
    
    <div class="clearfix"></div>
    <hr />
    [#-- Projects shared --]
    <div id="expectedStudyProjectsList" class="panel tertiary">
      <div class="panel-head"><label for=""> This study is done jointly with the following project(s), please select below: </label></div>
      <div class="expectedStudyProjectsList panel-body" > 
        <ul class="list">
        [#if element.projects?has_content ]
          [#list element.projects as projectLink]
            [@sharedProject element=projectLink name="${customName}.projects" index=projectLink_index template=false /]
          [/#list]
        [#else]
          <p class="emptyText">[@s.text name="expectedStudy.projectsEmpty" /]</p> 
        [/#if]  
        </ul>
        [#if editable ]
          [@customForm.select name="" label="" showTitle=false i18nkey="" listName="myProjects"  keyFieldName="id"  displayFieldName="composedName" required=true className="addSharedProject" editable=editable/]
        [/#if] 
      </div>
    </div>
    
  </div>
[/#macro]

[#macro sharedProject element name index=-1 template=false]
[#local customName = "${name}[${index}]" /]
<li id="sharedProject-${template?string('template', index)}" class="sharedProject" style="display:${template?string('none','block')}">
  [#-- Remove button --]
  [#if editable]<div class="removeProject removeIcon" title="Remove Project"></div>[/#if] 
  [#-- Hidden inputs --]
  <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}" />
  <input class="projectId" type="hidden" name="${customName}.myProject.id" value="${(element.myProject.id)!}" />
  [#-- title --]
  <p><span title="${(element.myProject.title)!}" class="name">${(element.myProject.composedName)!'Undefined'}</span></p>
  <div class="clearfix"></div>
</li>
[/#macro]