[#ftl]
[#assign title = "Project Description" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectDescription.js", "${baseUrl}/js/global/autoSave.js","${baseUrl}/js/global/fieldsValidation.js"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "description" /]
[#assign hideJustification = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectDescription", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    [#-- <div  class="removeHelp"><span class="glyphicon glyphicon-remove"></span></div> --]
    <img class="col-md-2" src="${baseUrl}/images/global/icon-help.jpg" />
    <p class="col-md-10"> 
      [#if project.projectEditLeader]  global.managementLiaison
        [#if reportingActive] 
          [@s.text name="projectDescription.help3" ] [@s.param][@s.text name="global.managementLiaison" /][/@s.param] [/@s.text]
        [#else] 
          [@s.text name="projectDescription.help2" ] [@s.param][@s.text name="global.managementLiaison" /][/@s.param] [/@s.text]
        [/#if]  
      [#else]
        [@s.text name="projectDescription.help1" /] 
      [/#if]
    </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>
    
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/views/projects/messages-projects.ftl" /]
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          
          <h3 class="headTitle">[@s.text name="projectDescription.title" /]</h3>  
          <div id="projectDescription" class="borderBox">
            [#-- Project Title --]
            <div class="form-group">
              [@customForm.textArea name="project.title" required=true className="project-title" editable=editable && action.hasPermission("title") /]
            </div>
            <div class="form-group row">
              [#-- Project Program Creator --]
              <div class="col-md-6">
               
                [@customForm.select name="project.liaisonInstitution.id" className="liaisonInstitutionSelect" i18nkey="project.liaisonInstitution"  disabled=!editable  listName="liaisonInstitutions" keyFieldName="id"  displayFieldName="composedName" required=true editable=editable && action.hasPermission("managementLiaison") /]
              </div>
              [#--  Project Owner Contact Person --]
              <div class="col-md-6"> 
                
                [@customForm.select name="project.liaisonUser.id" className="liaisonUserSelect" i18nkey="project.liaisonUser"  listName="allOwners" keyFieldName="id"  displayFieldName="composedName" required=true editable=editable && action.hasPermission("managementLiaison")/]
                <span id="liaisonUserSelected" style="display:none">${(project.liaisonUser.id)!-1}</span>
              </div> 
            </div>  
            <div class="form-group row">  
              [#-- Start Date --]
              <div class="col-md-6">
                [@customForm.input name="project.startDate" type="text" disabled=!editable  required=true editable=editable && action.hasPermission("startDate")  /]
              </div> 
              [#-- End Date --]
              <div class="col-md-6">
                [@customForm.input name="project.endDate" type="text" disabled=!editable required=true editable=editable && action.hasPermission("endDate")  /]
              </div>
            </div>
            <div class="form-group row">
              [#-- Project Type 
              <div class="col-md-6"> 
                [@customForm.select name="project.type" value="${(project.type)!}" i18nkey="project.type" listName="projectTypes" disabled=true editable=false stringKey=true /]
              </div>
              --]
            </div> 
            
            [#-- Project Summary --]
            <div class="form-group">
              [@customForm.textArea name="project.summary" required=!((project.bilateralProject)!false) className="project-description" editable=editable && action.hasPermission("summary") /]
            </div>
            
            [#-- Project status 
            [#if reportingActive ]
            <div class="form-group">
              <br />
              <div class="dottedBox ${reportingActive?string('fieldFocus','')}">
                <div class="row">
                  <div class="col-md-6">
                    [@customForm.select name="project.status" value="${(project.status)!}" i18nkey="project.status" listName="projectStatuses" header=false editable=editable /]
                  </div>
                </div>
                <div class="row">
                  <div class="col-md-12">
                    [@customForm.textArea name="project.statusJustification" required=!((project.bilateralProject)!false) className="project-statusJustification limitWords-100" editable=editable  /]
                  </div>
                </div>
              </div>
            </div>
            [/#if]
            --]
            
            [#--  Regions/global and Flagships that the project is working on --]
            [#if !project.administrative]
            
            [#if regionFlagships?has_content]
              [#-- For the CRPs which has Regional Programs --]
              <h5>[@customForm.text name="projectDescription.projectWorkingWithRegions" readText=!editable /]:</h5>
            [#else]
              [#-- For those CRPs which do not have Regional programs please phrase this question --]
              <h5>[@customForm.text name="projectDescription.projectWorking" readText=!editable /]:</h5>
            [/#if]
            
            <div id="projectWorking" class="fullBlock dottedBox clearfix">
              [#-- Flagships --] 
              <div class="col-md-6">
                <div id="projectFlagshipsBlock" class="${customForm.changedField('project.flagshipValue')}">
                  <p><label>[@s.text name="projectDescription.flagships" /]:[@customForm.req required=editable && action.hasPermission("flagships") /] </label></p>
                  [#if editable && action.hasPermission("flagships")]
                    [@s.fielderror cssClass="fieldError" fieldName="project.flagshipValue"/]
                    [@s.checkboxlist name="project.flagshipValue" list="programFlagships" listKey="id" listValue="composedName" cssClass="checkboxInput fpInput"  value="flagshipIds" /]
                  [#else]
                    <input type="hidden" name="project.flagshipValue" value="${(project.flagshipValue)!}"/>
                    [#if project.flagships?has_content]
                      [#list project.flagships as element]<p class="checked">${element.composedName}</p>[/#list]
                    [#else]
                      [#if !((project.bilateralProject)!false)]<span class="fieldError">[@s.text name="form.values.required" /]</span>[/#if]
                    [/#if]
                  [/#if]
                </div>
              </div>
              [#-- Regions --] 
              <div class="col-md-6"> 
                [#if regionFlagships?has_content] 
                  <div id="projectRegionsBlock" class="${customForm.changedField('project.flagshipValue')}">
                    <p><label>[@s.text name="projectDescription.regions" /]:[@customForm.req required=editable && action.hasPermission("regions") /]</label></p>
                    [#if editable && action.hasPermission("regions")]
                      [@s.fielderror cssClass="fieldError" fieldName="project.regionsValue"/]
                      <input type="checkbox" name="project.noRegional" value="true" id="projectNoRegional" class="checkboxInput" [#if (project.isNoRegional())!false ]checked="checked"[/#if] />
                      <label for="projectNoRegional" class="checkboxLabel"> <i>[@s.text name="project.noRegional" /]</i> </label>
                      [@s.checkboxlist name="project.regionsValue" list="regionFlagships" listKey="id" listValue="composedName" cssClass="checkboxInput rpInput" value="regionsIds" /]
                    [#else]
                      [#if (project.isNoRegional())!false ]
                        <input type="hidden" name="project.noRegional" value="true" />
                        <p class="checked"> [@s.text name="project.noRegional" /]</p>
                      [/#if]
                      <input type="hidden" name="project.regionsValue" value="${(project.regionsValue)!}"/>
                      [#if project.regions?has_content]
                        [#list project.regions as element]<p class="checked">${element.composedName}</p>[/#list]
                      [#else]
                        [#--  --if !((project.bilateralProject)!false)]<span class="fieldError">[@s.text name="form.values.required" /]</span>[/#if--]
                      [/#if]
                    [/#if]
                  </div>
                [/#if]
              </div>
              <div class="clearfix"></div>
            </div> 
            [/#if]
            
            [#-- Cluster of Activities --]
            [#if !project.administrative && !phaseOne]
            <div class="panel tertiary">
              <div class="panel-head ${customForm.changedField('project.clusterActivities')}"> 
                <label for="">[@customForm.text name="projectDescription.clusterActivities" readText=!editable /]:[@customForm.req required=editable  && action.hasPermission("activities") /]</label>
              </div>
              <div id="projectsList" class="panel-body" listname="project.clusterActivities"> 
                <ul class="list">
                [#if project.clusterActivities?has_content]
                  [#list project.clusterActivities as element]
                    <li class="clusterActivity clearfix [#if !element_has_next]last[/#if]">
                      [#if editable && action.hasPermission("activities") ]<span class="listButton remove popUpValidation">[@s.text name="form.buttons.remove" /]</span>[/#if] 
                      <input class="id" type="hidden" name="project.clusterActivities[${element_index}].crpClusterOfActivity.id" value="${element.crpClusterOfActivity.id}" />
                      <input class="cid" type="hidden" name="project.clusterActivities[${element_index}].id" value="${(element.id)!}" />
                      <span class="name">${(element.crpClusterOfActivity.composedName)!'null'}</span>
                      <div class="clearfix"></div>
                      <ul class="leaders">
                        [#if element.crpClusterOfActivity.leaders??]
                          [#list element.crpClusterOfActivity.leaders as leader]<li class="leader">${(leader.user.composedName?html)!'null'}</li>[/#list]
                        [/#if]
                      </ul>
                    </li>
                  [/#list]
                [#else]
                  [#if !action.hasPermission("activities")] <p class="emptyText"> [@s.text name="projectDescription.clusterActivities.empty" /]</p> [/#if]
                [/#if]  
                </ul>
                [#if editable  && action.hasPermission("activities")]
                  <span id="coaSelectedIds" style="display:none">[#if project.clusterActivities?has_content][#list project.clusterActivities as e]${e.crpClusterOfActivity.id}[#if e_has_next],[/#if][/#list][/#if]</span>  
                  [@customForm.select name="" label="" disabled=!canEdit i18nkey="" listName="clusterofActivites" keyFieldName="id" displayFieldName="ComposedName" className="CoASelect" value="" /]
                [/#if] 
              </div>
            </div>
            [/#if]
            
            [#if project.projectEditLeader && !phaseOne]
              [#--  What type of gender analysis informed the design of this project and how? --]
              <div class="form-group">
                [@customForm.textArea name="project.genderAnalysis" required=true className=" limitWords-50" editable=editable /]
              </div>
              
              [#-- Select the cross-cutting dimension(s) to this project? --]
              <div class="form-group">
                <label for="">[@customForm.text name="project.crossCuttingDimensions" readText=!editable/] [@customForm.req required=editable/]</label>
                <div class="row">
                  <div class="col-md-12">
                    [#if editable]
                      <label class="checkbox-inline"><input type="checkbox" name="project.crossCuttingGender"   id="gender"   value="true" [#if (project.crossCuttingGender)!false ]checked="checked"[/#if]> Gender</label>
                      <label class="checkbox-inline"><input type="checkbox" name="project.crossCuttingYouth"    id="youth"    value="true" [#if (project.crossCuttingYouth)!false ]checked="checked"[/#if]> Youth</label>
                      <label class="checkbox-inline"><input type="checkbox" name="project.crossCuttingCapacity" id="capacity" value="true" [#if (project.crossCuttingCapacity)!false ]checked="checked"[/#if]> Capacity Development</label>
                      <label class="checkbox-inline"><input type="checkbox" name="project.crossCuttingNa"       id="na"       value="true" [#if (project.crossCuttingNa)!false ]checked="checked"[/#if]> N/A</label>
                    [#else]
                      <div class="${customForm.changedField('project.crossCuttingGender')}">[#if (project.crossCuttingGender)!false ] <p class="checked"> Gender</p>[/#if] </div>
                      <div class="${customForm.changedField('project.crossCuttingYouth')}">[#if (project.crossCuttingYouth)!false ] <p class="checked"> Youth</p>[/#if]</div>
                      <div class="${customForm.changedField('project.crossCuttingCapacity')}">[#if (project.crossCuttingCapacity)!false ] <p class="checked"> Capacity Development</p>[/#if]</div>
                      <div class="${customForm.changedField('project.crossCuttingNa')}">[#if (project.crossCuttingNa)!false ] <p class="checked"> N/A</p>[/#if]</div>
                    [/#if]
                  </div>
                </div>
                <br />
              </div>
              
              [#-- If no gender dimension, then please explain why not --]
              <div id="gender-question" class="form-group" style="display:${((project.crossCuttingGender)!false)?string('none','block')}">
                [@customForm.textArea name="project.dimension" required=true className=" limitWords-50" editable=editable /]
              </div>
            [/#if]
          </div> 
           
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
             
         
          [/@s.form] 
      </div>
    </div>  
</section>


<span id="liaisonInstitutionsPrograms" style="display:none">{[#list liaisonInstitutions as institution]"${institution}" : ${(institution.crpProgram.id)!-1}[#if institution_has_next], [/#if][/#list]}</span>


[#-- Cluster of activity list template --]
<ul style="display:none">
  <li id="cpListTemplate" class="clusterActivity clearfix">
    <span class="listButton remove">[@s.text name="form.buttons.remove" /]</span>
    <input class="id" type="hidden" name="project.clusterActivities[-1].crpClusterOfActivity.id" value="" />
    <input class="cid" type="hidden" name="project.clusterActivities[-1].id" value="" />
    <span class="name"></span>
    <div class="clearfix"></div>
    <ul class="leaders"></ul>
  </li>
</ul>

[#-- project scope template --]
<ul style="display:none">
  <li id="projecScope-template" class="projecScope clearfix">
    <span class="listButton remove">[@s.text name="form.buttons.remove" /]</span>
    <input class="id" type="hidden" name="project.scopes[-1].locElementType.id" value="" />
    <input class="cid" type="hidden" name="project.scopes[-1].id" value="" />
    <span class="name"></span>
    <div class="clearfix"></div>
    <ul class="leaders"></ul>
  </li>
</ul>

[#-- Remove project contribution popup --]
<div id="removeContribution-dialog" title="Remover Cluster of Activity" style="display:none">
  <ul class="messages"><li>[@s.text name="projectDescription.removeCoADialog" /]</li></ul>
</div>

[#-- File upload Template--]
[@customForm.inputFile name="file" fileUrl="${(workplanURL)!}" fileName="project.workplan.fileName" template=true /]

[@customForm.inputFile name="fileReporting" template=true /] 
  
[#include "/WEB-INF/global/pages/footer.ftl"]