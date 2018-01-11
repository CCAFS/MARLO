[#ftl]
[#assign title = "Project Description" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${actualPhase.id}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectDescription.js", 
  "${baseUrl}/global/js/autoSave.js",
  "${baseUrl}/global/js/fieldsValidation.js"
  ] 
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/projectDescription.css"
  ] 
/]
[#assign currentSection = "projects" /]
[#assign currentStage = "description" /]
[#assign hideJustification = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectDescription", "nameSpace":"/projects", "action":""}
] /]

[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    [#-- <div  class="removeHelp"><span class="glyphicon glyphicon-remove"></span></div> --]
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
    <p class="col-md-10"> 
      [#if (project.projectInfo.isProjectEditLeader())!false]
        [#if (reportingActive)!false] 
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
    
[#if (!availabePhase)!false]
  [#include "/WEB-INF/crp/views/projects/availability-projects.ftl" /]
[#else]
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
          
          <h3 class="headTitle">[@s.text name="projectDescription.title" /]</h3>  
          <div id="projectDescription" class="borderBox">
            
             <input class="projectInfo" type="hidden" name="project.projectInfo.id" value="${project.projectInfo.id}" />
            [#-- Project Title --]
            <div class="form-group">
              [@customForm.textArea name="project.projectInfo.title" i18nkey="project.title" required=true className="project-title limitWords-30" editable=editable && action.hasPermission("title") /]
            </div>
            <div class="form-group row">
              [#-- Project Program Creator --]
              <div class="col-md-6">
               
                [@customForm.select name="project.projectInfo.liaisonInstitution.id" className="liaisonInstitutionSelect" i18nkey="project.liaisonInstitution"  disabled=!editable  listName="liaisonInstitutions" keyFieldName="id"  displayFieldName="composedName" required=true editable=editable && action.hasPermission("managementLiaison") /]
              </div>
              [#--  Project Owner Contact Person --]
              <div class="col-md-6"> 
                [#-- Loading --]
                <div class="loading liaisonUsersBlock" style="display:none"></div>
                [@customForm.select name="project.projectInfo.liaisonUser.id" className="liaisonUserSelect" i18nkey="project.liaisonUser"  listName="allOwners" keyFieldName="id"  displayFieldName="composedName" required=true editable=editable && action.hasPermission("managementLiaison")/]
                <span id="liaisonUserSelected" style="display:none">${(project.projectInfo.liaisonUser.id)!-1}</span>
              </div> 
            </div>  
            <div class="form-group row">  
              [#-- Start Date --]
              <div class="col-md-6">
                [@customForm.input name="project.projectInfo.startDate" className="startDate" i18nkey="project.startDate" type="text" disabled=!editable  required=true editable=editable && action.hasPermission("startDate")  /]
              </div> 
              [#-- End Date --]
              <div class="col-md-6">
                [@customForm.input name="project.projectInfo.endDate" className="endDate"  i18nkey="project.endDate" type="text" disabled=!editable required=true editable=editable && action.hasPermission("endDate")  /]
              </div>
            </div>
            <div class="form-group row">
              [#-- Project Type 
              <div class="col-md-6"> 
                [@customForm.select name="project.projectInfo.type" value="${(project.projectInfo.type)!}" i18nkey="project.type" listName="projectTypes" disabled=true editable=false stringKey=true /]
              </div>
              --]
            </div> 
            
            [#-- Project Summary --]
            <div class="form-group">
              [@customForm.textArea name="project.projectInfo.summary"  i18nkey="project.summary" required=!((project.bilateralProject)!false) className="project-description limitWords-250" editable=editable && action.hasPermission("summary") /]
            </div>
            
            [#-- Project status --]
            <div class="form-group ${reportingActive?string('fieldFocus','')}">
              <div class="form-group row">
                <div class="col-md-6">
                  [@customForm.select name="project.projectInfo.status" value="${(project.projectInfo.status)!}" i18nkey="project.status" className="description_project_status" listName="projectStatuses" header=false editable=(editable || editStatus) /]
                </div>
              </div>
              <div id="statusDescription" class="form-group" style="display:${project.projectInfo.statusJustificationRequired?string('block','none')}">
                [@customForm.textArea name="project.projectInfo.statusJustification" i18nkey="project.statusJustification" required=!((project.bilateralProject)!false) className="project-statusJustification limitWords-100" editable=action.hasPermission("statusDescription")  /]
              </div>
            </div>
            
            [#--  Regions/global and Flagships that the project is working on --]
            [#if !project.projectInfo.administrative]
            
            [#if regionFlagships?has_content]
              [#-- For the CRPs which has Regional Programs --]
              <h5>[@customForm.text name="projectDescription.projectWorkingWithRegions" readText=!editable /]:</h5>
            [#else]
              [#-- For those CRPs which do not have Regional programs please phrase this question --]
              <h5>[@customForm.text name="projectDescription.projectWorking" readText=!editable /]:</h5>
            [/#if]
            
            <div id="projectWorking" class="fullBlock dottedBox clearfix">
              [#-- Flagships --] 
              <div class="col-md-${(regionFlagships?has_content)?string('6','12')}">
                <div id="projectFlagshipsBlock" class="${customForm.changedField('project.flagshipValue')}">
                  <p><label>[@s.text name="projectDescription.flagships" /]:[@customForm.req required=editable && action.hasPermission("flagships") /] </label></p>
                  [#if editable && action.hasPermission("flagships")]
                    [@s.fielderror cssClass="fieldError" fieldName="project.flagshipValue"/]
                    [#if programFlagships??]
                      [#-- Contributions allowed to this flagship --]
                      [#list programFlagships as element]
                        [#assign outcomesContributions = (action.getContributionsOutcome(project.id, element.id))![] /]
                        [#assign clustersContributions = (action.getClusterOutcome(project.id, element.id))![] /]
                        [#assign totalContributions = outcomesContributions?size + clustersContributions?size ]
                        
                        [#if (totalContributions != 0)] 
                          <p class="checkDisable"> 
                             ${element.composedName} [@outcomesRelationsPopup  element outcomesContributions clustersContributions /] 
                            <input type="hidden" class="defaultChecked" name="project.flagshipValue" value="${element.id}"/>
                          </p>
                        [#else]
                          <p class=""> 
                          [@customForm.checkBoxFlat id="projectFp-${element.id}" name="project.flagshipValue" label="${element.composedName}" disabled=false editable=editable value="${element.id}" checked=((flagshipIds?seq_contains(element.id))!false) cssClass="fpInput" /]
                          </p>
                        [/#if]
                      [/#list]
                    [/#if]
                  [#else]
                    [#-- If does no have permissions --]
                    <input type="hidden" name="project.flagshipValue" value="${(project.flagshipValue)!}"/>
                    [#-- Selected Flagships --]
                    [#if project.flagships?has_content][#list project.flagships as element]<p class="checked">${element.composedName}</p>[/#list][/#if]
                  [/#if]
                </div>
              </div>
              [#-- Regions --] 
              <div class="col-md-${(regionFlagships?has_content)?string('6','12')}"> 
                [#if regionFlagships?has_content] 
                  <div id="projectRegionsBlock" class="${customForm.changedField('project.regionsValue')}">
                    <p><label>[@s.text name="projectDescription.regions" /]:[@customForm.req required=editable && action.hasPermission("regions") /]</label></p>
                    [#if editable && action.hasPermission("regions")]
                      [@s.fielderror cssClass="fieldError" fieldName="project.regionsValue"/]
                      [#assign noRegionalLabel]<i>[@s.text name="project.noRegional" /]</i>[/#assign]
                      [@customForm.checkBoxFlat id="projectNoRegional" name="project.projectInfo.noRegional" label="${noRegionalLabel}" disabled=false editable=editable value="true" checked=((project.projectInfo.noRegional)!false) cssClass="checkboxInput" /]
                      [#if regionFlagships??]
                        [#list regionFlagships as element]
                          [@customForm.checkBoxFlat id="projectRegion-${element.id}" name="project.regionsValue" label="${element.composedName}" disabled=false editable=editable value="${element.id}" checked=((regionsIds?seq_contains(element.id))!false) cssClass="checkboxInput rpInput" /]
                        [/#list]
                      [/#if]
                       
                    [#else]
                      [#if (project.projectInfo.isNoRegional())!false ]
                        <input type="hidden" name="project.projectInfo.noRegional" value="true" />
                        <p class="checked"> [@s.text name="project.projectInfo.noRegional" /]</p>
                      [/#if]
                      <input type="hidden" name="project.regionsValue" value="${(project.regionsValue)!}"/>
                      [#if project.regions?has_content]
                        [#list project.regions as element]<p class="checked">${element.composedName}</p>[/#list]
                      [#else]
                        <p>[@s.text name="form.values.fieldEmpty" /]</p>
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
            [#if !project.projectInfo.administrative && !phaseOne]
            <div class="panel tertiary">
              <div class="panel-head ${customForm.changedField('project.clusterActivities')}"> 
                <label for="">[@s.text name="projectDescription.clusterActivities"][@s.param][@s.text name="global.clusterOfActivities" /][/@s.param] [/@s.text]:[@customForm.req required=editable  && action.hasPermission("activities") /]</label>
              </div>
              <div id="projectsList" class="panel-body" listname="project.clusterActivities">
                [#-- Loading --]
                <div class="loading clustersBlock" style="display:none"></div>
                <ul class="list">
                [#if project.clusterActivities?has_content]
                  [#list project.clusterActivities as element]
                    <li class="clusterActivity clearfix [#if !element_has_next]last[/#if]">
                      <input class="id" type="hidden" name="project.clusterActivities[${element_index}].crpClusterOfActivity.id" value="${element.crpClusterOfActivity.id}" />
                      <input class="cid" type="hidden" name="project.clusterActivities[${element_index}].id" value="${(element.id)!}" />
                      [#if editable && action.hasPermission("activities") ]<span class="listButton remove popUpValidation pull-right">[@s.text name="form.buttons.remove" /]</span>[/#if] 
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
                  [#assign multipleCoA = action.hasSpecificities('crp_multiple_coa')]
                  <span id="coaSelectedIds" style="display:none">[#if project.clusterActivities?has_content][#list project.clusterActivities as e]${e.crpClusterOfActivity.id}[#if e_has_next],[/#if][/#list][/#if]</span>  
                  [@customForm.select name="" label="" disabled=!canEdit i18nkey="" listName="clusterofActivites" keyFieldName="id" displayFieldName="ComposedName" className="CoASelect multipleCoA-${multipleCoA?string}" value="" /]
                [#else]
                  [#if !project.clusterActivities?has_content]
                    <p>[@s.text name="form.values.fieldEmpty" /]</p>
                  [/#if]
                [/#if] 
              </div>
            </div>
            [/#if]
            
            [#if project.projectInfo.isProjectEditLeader() && !phaseOne]
              [#--  What type of gender analysis informed the design of this project and how? --]
              <div class="form-group">
                [@customForm.textArea name="project.projectInfo.genderAnalysis" i18nkey="project.genderAnalysis" required=true className=" limitWords-100" editable=editable /]
              </div>
              
              [#-- Select the cross-cutting dimension(s) to this project? --]
              <div class="form-group">
                <label for="">[@customForm.text name="project.crossCuttingDimensions"  readText=!editable/] [@customForm.req required=editable/]</label>
                <div class="row">
                  <div class="col-md-12">
                    [#if editable]
                      <label class="checkbox-inline"><input type="checkbox" name="project.projectInfo.crossCuttingGender"   id="gender"   value="true" [#if (project.projectInfo.crossCuttingGender)!false ]checked="checked"[/#if]> Gender</label>
                      <label class="checkbox-inline"><input type="checkbox" name="project.projectInfo.crossCuttingYouth"    id="youth"    value="true" [#if (project.projectInfo.crossCuttingYouth)!false ]checked="checked"[/#if]> Youth</label>
                      <label class="checkbox-inline"><input type="checkbox" name="project.projectInfo.crossCuttingCapacity" id="capacity" value="true" [#if (project.projectInfo.crossCuttingCapacity)!false ]checked="checked"[/#if]> Capacity Development</label>
                      <label class="checkbox-inline"><input type="checkbox" name="project.projectInfo.crossCuttingNa"       id="na"       value="true" [#if (project.projectInfo.crossCuttingNa)!false ]checked="checked"[/#if]> N/A</label>
                    [#else]
                      <div class="${customForm.changedField('project.projectInfo.crossCuttingGender')}">[#if (project.projectInfo.crossCuttingGender)!false ] <p class="checked"> Gender</p><input type="hidden" name="project.projectInfo.crossCuttingGender" value="true">[/#if] </div>
                      <div class="${customForm.changedField('project.projectInfo.crossCuttingYouth')}">[#if (project.projectInfo.crossCuttingYouth)!false ] <p class="checked"> Youth</p><input type="hidden" name="project.projectInfo.crossCuttingYouth" value="true">[/#if]</div>
                      <div class="${customForm.changedField('project.projectInfo.crossCuttingCapacity')}">[#if (project.projectInfo.crossCuttingCapacity)!false ] <p class="checked"> Capacity Development</p><input type="hidden" name="project.projectInfo.crossCuttingCapacity" value="true">[/#if]</div>
                      <div class="${customForm.changedField('project.projectInfo.crossCuttingNa')}">[#if (project.projectInfo.crossCuttingNa)!false ] <p class="checked"> N/A</p><input type="hidden" name="project.projectInfo.crossCuttingNa" value="true">[/#if]</div>
                      [#-- Message when there's nothing to show -> "Prefilled if avaible" --]
                      [#if ((!project.crossCuttingGender)!false) && ((!project.crossCuttingYouth)!false) && ((!project.crossCuttingCapacity)!false) && ((!project.crossCuttingNa)!false)]<p>[@s.text name="form.values.fieldEmpty" /]</p>[/#if]
                    [/#if]
                  </div>
                </div>
                <br />
              </div>
              
              [#-- If no gender dimension, then please explain why not --]
              <div id="gender-question" class="form-group" style="display:${((project.projectInfo.crossCuttingGender)!false)?string('none','block')}">
                [@customForm.textArea name="project.projectInfo.dimension" i18nkey="project.dimension"  required=true className=" limitWords-50" editable=editable /]
              </div>
            [/#if]
          </div> 
           
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
             
         
          [/@s.form]
      </div>
    </div>  
</section>
[/#if]


<span id="liaisonInstitutionsPrograms" style="display:none">{[#list liaisonInstitutions as institution]"${institution}" : ${(institution.crpProgram.id)!-1}[#if institution_has_next], [/#if][/#list]}</span>


[#-- Cluster of activity list template --]
<ul style="display:none">
  <li id="cpListTemplate" class="clusterActivity clearfix">
    <span class="listButton remove pull-right">[@s.text name="form.buttons.remove" /]</span>
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
    <span class="listButton remove pull-right">[@s.text name="form.buttons.remove" /]</span>
    <input class="id" type="hidden" name="project.scopes[-1].locElementType.id" value="" />
    <input class="cid" type="hidden" name="project.scopes[-1].id" value="" />
    <span class="name"></span>
    <div class="clearfix"></div>
    <ul class="leaders"></ul>
  </li>
</ul>

[#-- Remove project contribution popup --]
<div id="removeContribution-dialog" title="Remove  [@s.text name="global.clusterOfActivities" /]" style="display:none">
  <ul class="messages"><li>[@s.text name="projectDescription.removeCoADialog" /]</li></ul>
</div>


[@customForm.inputFile name="fileReporting" template=true /] 
  
[#include "/WEB-INF/crp/pages/footer.ftl"]


[#macro outcomesRelationsPopup  element outcomesContributions clustersContributions]
  [#-- Button --]
  <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#modal-outcomesContributions-${element.id}">
    <span class="icon-20 outcomesCont"></span> <strong>${outcomesContributions?size + clustersContributions?size}</strong>
  </button>
  
  [#-- Modal --]
  <div class="modal fade" id="modal-outcomesContributions-${element.id}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog modal-lg" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
          <h4 class="modal-title" id="myModalLabel">
            Project flagship contributions
            <br />
            <small>${element.composedName}</small>
          </h4>
        </div>
        <div class="modal-body">
          [#-- Outcomes table --]
          [#if outcomesContributions?has_content]
          <table class="table">
            <thead>
              <tr>
                <th id="ids">Outcome</th>
                <th id="ids">Target unit</th>
                <th id="ids">Year</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              [#list outcomesContributions as oc]
                [#local projectUrl][@s.url namespace="/projects" action="${(crpSession)!}/contributionsCrpList"][@s.param name='projectID']${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                <tr>
                  <td>${(oc.description)!'None'}</td> 
                  <td>${(oc.value)!} ${(oc.srfTargetUnit.name)!'None'}</td>
                  <td>${(oc.year)!'None'}</td>
                  <td> <a href="${projectUrl}" target="_blank"><span class="glyphicon glyphicon-new-window"></span></a> </td>
                </tr>
              [/#list]
            </tbody>
          </table>
          [/#if]
          
          [#-- Cluters of Activities table --]
          [#if clustersContributions?has_content]
          <table class="table">
            <thead>
              <tr>
                <th id="ids">[@s.text name="global.clusterOfActivities" /]</th>
              </tr>
            </thead>
            <tbody>
              [#list clustersContributions as cc]
                <tr>
                  <td>${(cc.description)!'None'}</td> 
                </tr>
              [/#list]
            </tbody>
          </table>
          [/#if]
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        </div>
      </div>
    </div>
  </div>
  
  
[/#macro]