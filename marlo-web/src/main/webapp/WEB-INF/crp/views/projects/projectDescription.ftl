[#ftl]
[#assign title = "Project Description" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectDescription.js", 
  "${baseUrlCdn}/global/js/autoSave.js",
  "${baseUrlCdn}/global/js/fieldsValidation.js"
  ] 
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/projectDescription.css"
  ] 
/]
[#assign currentSection = "projects" /]
[#assign currentStage = "description" /]
[#assign hideJustification = true /]
[#assign isCrpProject = (action.isProjectCrpOrPlatform(project.id))!false ]
[#assign isCenterProject = (action.isProjectCenter(project.id))!false ]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"text":"P${project.id}", "nameSpace":"/projects", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
  {"label":"projectDescription", "nameSpace":"/projects", "action":""}
] /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    [#-- <div  class="removeHelp"><span class="glyphicon glyphicon-remove"></span></div> --]
    <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-help.jpg" />
    <p class="col-md-10"> 
      [#if (project.projectInfo.isProjectEditLeader())!false]
        [#if (reportingActive)!false] 
          [@s.text name="projectDescription.help3" ] [@s.param][@s.text name="global.managementLiaison" /][/@s.param] [/@s.text]
        [#else] 
          [@s.text name="projectDescription.help2" ] [@s.param][@s.text name="global.managementLiaison${isCenterProject?string('Center', '')}" /][/@s.param] [/@s.text]
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
            
            [#-- Project Title --]
            <div class="form-group">
              [@customForm.textArea name="project.projectInfo.title" i18nkey="project.title" required=true className="project-title limitWords-30" editable=editable && action.hasPermission("title") /]
            </div>
            
            [#if isCrpProject]
            <div class="form-group row">
              [#-- Project Program Creator --]
              <div class="col-md-6">
                [@customForm.select name="project.projectInfo.liaisonInstitution.id" className="liaisonInstitutionSelect" i18nkey="project.liaisonInstitution"  disabled=!editable  listName="liaisonInstitutions" keyFieldName="id"  displayFieldName="composedName" required=true editable=editable && action.hasPermission("managementLiaison") /]
              </div>             
            </div>
            [/#if]
            [#if isCenterProject ]
            <div class="form-group row">
              [#-- CENTER Research program --]
              <div class="col-md-6 researchProgram ">
                [@customForm.select name="project.projectInfo.liaisonInstitution.id" listName="liaisonInstitutions" paramText="${currentCrp.acronym}" keyFieldName="id" displayFieldName="composedName" i18nkey="project.researchProgram" className="liaisonInstitutionSelect" help="project.researchProgram.help" required=true editable=editable /]
              </div>
            </div>
            [/#if]
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
            
            [#-- Project Summary --]
            <div class="form-group">
              [@customForm.textArea name="project.projectInfo.summary"  i18nkey="project.summary" required=!((project.bilateralProject)!false) className="project-description limitWords-250" editable=editable && action.hasPermission("summary") /]
            </div>
            
            [#-- Project status --]
            <div class="form-group ${reportingActive?string('fieldFocus','')}">
              <div class="form-group row">
                <div class="col-md-6">
                  [@customForm.select name="project.projectInfo.status" value="${(project.projectInfo.status)!}" i18nkey="project.status" className="description_project_status" listName="projectStatuses" header=false required=true editable=(editable || editStatus) /]
                </div>
              </div>
              <div id="statusDescription" class="form-group" style="display:${project.projectInfo.statusJustificationRequired?string('block','none')}">
                [@customForm.textArea name="project.projectInfo.statusJustification" i18nkey="project.statusJustification" required=!((project.bilateralProject)!false) className="project-statusJustification limitWords-100" editable=(editable || editStatus)   /]
              </div>
            </div>
            
            [#--  Regions/global and Flagships that the project is working on --]
            [#if (!project.projectInfo.administrative)!false]
            
            [#if regionFlagships?has_content]
              [#-- For the CRPs which has Regional Programs --]
              <h5>[@customForm.text name="projectDescription.projectWorkingWithRegions${isCenterProject?string('Center','')}" readText=!editable /]:</h5>
            [#else]
              [#-- For those CRPs which do not have Regional programs please phrase this question --]
              <h5>[@customForm.text name="projectDescription.projectWorking${isCenterProject?string('Center','')}" readText=!editable /]:</h5>
            [/#if]
            
            <div id="projectWorking" class="fullBlock dottedBox clearfix">
              [#-- Flagships --] 
              <div class="col-md-${(regionFlagships?has_content)?string('6','12')}">
                <div id="projectFlagshipsBlock" class="${customForm.changedField('project.flagshipValue')}">
                  <p><label>[@s.text name="projectDescription.flagships${isCenterProject?string('Center','')}" /]:[@customForm.req required=editable && action.hasPermission("flagships") /] </label></p>
                  [#if editable && action.hasPermission("flagships")]
                    [@s.fielderror cssClass="fieldError" fieldName="project.flagshipValue"/]
                     
                    [#-- Contributions allowed to this flagship --]
                    [#list (programFlagships)![] as element]
                      [#assign flagshipName][#if isCrpProject]${element.composedName}[#else]${element.centerComposedName}[/#if][/#assign]
                      
                      [#assign outcomesContributions = (action.getContributionsOutcome(project.id, element.id))![] /]
                      [#assign clustersContributions = (action.getClusterOutcome(project.id, element.id))![] /]
                      [#assign totalContributions = outcomesContributions?size + clustersContributions?size ]
                      
                      [#if (totalContributions != 0)] 
                        <p class="checkDisable"> 
                          ${flagshipName} [#if outcomesContributions?size > 0] [@outcomesRelationsPopup  element outcomesContributions clustersContributions /][/#if]
                          <input type="hidden" class="defaultChecked" name="project.flagshipValue" value="${element.id}"/>
                        </p>
                      [#else]
                        [@customForm.checkBoxFlat id="projectFp-${element.id}" name="project.flagshipValue" label="${flagshipName}" disabled=false editable=editable value="${element.id}" checked=((flagshipIds?seq_contains(element.id))!false) cssClass="fpInput ${isCenterProject?string('getCenterOutcomes','')}" cssClassLabel="font-normal" /]
                      [/#if]
                    [/#list]
                     
                  [#else]
                    [#-- If does no have permissions --]
                    <input type="hidden" name="project.flagshipValue" value="${(project.flagshipValue)!}"/>
                    [#-- Selected Flagships --]
                    [#list (project.flagships)![] as element]<p class="checked">${element.composedName}</p>[/#list]
                  [/#if]
                </div>
              </div>
              [#-- Regions --] 
              <div class="col-md-${(regionFlagships?has_content)?string('6','12')}"> 
                [#if regionFlagships?has_content] 
                  <div id="projectRegionsBlock" class="${customForm.changedField('project.regionsValue')}">
                    <p><label>[@s.text name="projectDescription.regions${isCenterProject?string('Center','')}" /]:[@customForm.req required=editable && action.hasPermission("regions") /]</label></p>
                    [#if editable && action.hasPermission("regions")]
                      [#if isCrpProject]
                        [@s.fielderror cssClass="fieldError" fieldName="project.regionsValue"/]
                        [#assign noRegionalLabel][@s.text name="project.noRegional" /][/#assign]
                        [@customForm.checkBoxFlat id="projectNoRegional" name="project.projectInfo.noRegional" label="${noRegionalLabel}" disabled=false editable=editable value="true" checked=((project.projectInfo.noRegional)!false) cssClass="checkboxInput" cssClassLabel="font-italic" /]
                      [/#if]
                      [#list (regionFlagships)![] as element]
                        [#assign regionName][#if isCrpProject]${element.composedName}[#else]${element.name}[/#if][/#assign]
                        [@customForm.checkBoxFlat id="projectRegion-${element.id}" name="project.regionsValue" label="${regionName}" disabled=false editable=editable value="${element.id}" checked=((regionsIds?seq_contains(element.id))!false) cssClass="checkboxInput rpInput"  cssClassLabel="font-normal"/]
                      [/#list]
                       
                    [#else]
                      [#if (project.projectInfo.getNoRegional())!false ]
                        <input type="hidden" name="project.projectInfo.noRegional" value="true" />
                        <p class="checked"> [@s.text name="project.noRegional" /]</p>
                      [/#if]
                      <input type="hidden" name="project.regionsValue" value="${(project.regionsValue)!}"/>
                      [#list (project.regions)![] as element]
                        [#assign regionName][#if isCrpProject]${element.composedName}[#else]${element.name}[/#if][/#assign]
                        <p class="checked">${regionName}</p>
                      [/#list]
                    [/#if]
                  </div>
                [/#if]
              </div>
              <div class="clearfix"></div>
            </div> 
            [/#if]
            
            [#-- Cluster of Activities --]
            [#if !((project.projectInfo.administrative)!false) && !phaseOne && isCrpProject ]
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
                      <input class="identifier" type="hidden" name="project.clusterActivities[${element_index}].crpClusterOfActivity.identifier" value="${(element.crpClusterOfActivity.identifier)!}" />
                      [#if editable && !reportingActive && action.hasPermission("activities") ]<span class="listButton remove popUpValidation pull-right">[@s.text name="form.buttons.remove" /]</span>[/#if] 
                      <span class="name">${(element.crpClusterOfActivity.composedName)!'null'}</span>
                      <div class="clearfix"></div>
                      <ul class="leaders">
                        [#list (element.crpClusterOfActivity.leaders)![] as leader]<li class="leader">${(leader.user.composedName?html)!'null'}</li>[/#list]
                      </ul>
                    </li>
                  [/#list]
                [#else]
                  [#if !action.hasPermission("activities")] <p class="emptyText"> [@s.text name="projectDescription.clusterActivities.empty" /]</p> [/#if]
                [/#if]  
                </ul>
                [#if editable && !reportingActive && action.hasPermission("activities")]
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
            
            [#if isCenterProject]
            [#-- CENTER Reserach Outcomes (SA Version) --] 
            <div class="form-group">
              [@customForm.elementsListComponent name="project.centerOutcomes" elementType="centerOutcome" elementList=project.centerOutcomes label="projectDescription.researchOutcomes" listName="centerOutcomes" keyFieldName="id" displayFieldName="listComposedName" required=editable /]
            </div>
            [/#if]
            
            [#if (project.projectInfo.isProjectEditLeader() && !phaseOne)!false]
                     
              [#-- Select the cross-cutting dimension(s) to this project? --]
              <div class="form-group">
                <label for="">[@customForm.text name="project.crossCuttingDimensions"  readText=!editable/] [@customForm.req required=editable/]</label>
                <div class="row">
                  <div class="col-md-12">
                    [#assign crossCuttingMarkers = [
                        { "id":"gender", "name": "crossCuttingGender" },
                        { "id":"youth", "name": "crossCuttingYouth" },
                        { "id":"capacity", "name": "crossCuttingCapacity" },
                        { "id":"climate", "name": "crossCuttingClimate" },
                        { "id":"na", "name": "crossCuttingNa" }
                      ] 
                    /]
                    [#if editable]
                      [#list crossCuttingMarkers as marker]
                        <label class="checkbox-inline"><input type="checkbox" name="project.projectInfo.${marker.name}" id="${marker.id}" class="[#if marker.id != "na"]ccMarker[/#if]" value="true" [#if (project.projectInfo[marker.name])!false ]checked="checked"[/#if]>[@s.text name="crossCuttingMarker.${marker.id}" /]</label>
                      [/#list]
                    [#else]
                      [#assign checkedItems = false /]
                      [#list crossCuttingMarkers as marker]
                        [#if (project.projectInfo[marker.name])!false ]
                          <div class="${customForm.changedField('project.projectInfo.${marker.name}')}">
                            <p class="checked"> [@s.text name="crossCuttingMarker.${marker.id}" /]</p> <input type="hidden" name="project.projectInfo.${marker.name}" value="true">
                          </div>
                          [#assign checkedItems = true /]
                        [/#if] 
                      [/#list]
                      [#-- Message when there's nothing to show -> "Prefilled if avaible" --]
                      [#if !checkedItems]<div class="input"><p>[@s.text name="form.values.fieldEmpty" /]</p></div>[/#if]
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


<span id="liaisonInstitutionsPrograms" style="display:none">{[#list liaisonInstitutions as li]"${li.id}" : ${(li.crpProgram.id)!-1}[#if li_has_next], [/#if][/#list]}</span>


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
  
[#include "/WEB-INF/global/pages/footer.ftl"]


[#macro outcomesRelationsPopup  element outcomesContributions clustersContributions]
  [#-- Button --]
  <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#modal-outcomesContributions-${element.id}">
    <span class="icon-20 outcomesCont"></span> <strong>${outcomesContributions?size}</strong>
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