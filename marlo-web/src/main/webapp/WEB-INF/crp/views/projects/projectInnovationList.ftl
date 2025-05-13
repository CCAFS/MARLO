[#ftl]
[#assign title = "Innovations" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectInnovationsList.js?20250509",
  [#-- "${baseUrlCdn}/global/js/autoSave.js", --]
  "${baseUrlCdn}/global/js/fieldsValidation.js"
] /]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/projectInnovations.css?20250509",
  "${baseUrlCdn}/global/css/customDataTable.css?20250509"
  ] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "innovations" /]
[#assign isListSection = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"text":"C${project.id}", "nameSpace":"/projects", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
  {"label":"innovationsList", "nameSpace":"/projects", "action":""}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utils /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utils.helpBox name="projectInnovations.help" /]

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

        [#-- [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""] --]
          
          [#-- Innovations List --]
          <h3 class="headTitle">[@s.text name="projectInnovations" /]</h3>
          <div class="simpleBox table-responsive">
            [@innovationsTableMacro list=(projectInnovations)![] /]
          </div>
          
          [#-- Add Innovation Button --]
          [#if canEdit]
          <div class="buttons">
            <div class="buttons-content">
              <div class="addInnovation button-blue"><a  href="[@s.url namespace="/${currentSection}" action='${(crpSession)!}/addNewInnovation'][@s.param name="projectID"]${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
                <span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addInnovation" /]
              </a></div>
              <div class="clearfix"></div>
            </div>
          </div>
          [/#if]

          [#-- Previous Innovations List --]
          <br />
          <h3 class="headTitle">Previous [@s.text name="projectInnovations" /]</h3>
          <div class="simpleBox table-responsive">
            [@innovationsTableMacro list=(projectOldInnovations)![] currentTable=false/]
          </div>
        [#-- [/@s.form] --]
        
     
    </div>
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl"]


[#-- -- MACROS -- --]

[#macro innovationsTableMacro list currentTable=true ]
  <table id="table-innovations" class="table table-striped table-hover">
    <thead>
      <tr class="subHeader">
        [#-- ID --]
        <th id="tb-id" width="1%" rowspan="1">ID</th>
        [#-- Title --]
        <th id="tb-title" width="32.5%" rowspan="1">[@s.text name="projectInnovations.table.title" /]</th>
        [#-- Type --]
        <th id="tb-type" width="15%" rowspan="1">[@s.text name="projectInnovations.table.type" /]</th>
        [#-- Nature --]
        <th id="tb-stage" width="10%" rowspan="1">[@s.text name="projectInnovations.table.nature" /]</th>
        [#-- Readiness Level --]
        <th id="tb-readinessLevel" width="15%" rowspan="1">[@s.text name="projectInnovations.table.readinessLevel" /]</th>
        [#-- Year --]
        <th id="tb-year" width="5%" rowspan="1">[@s.text name="projectInnovations.table.year" /]</th>
        [#if action.hasSpecificities('feedback_active') ]
          [#-- Feedback Status --]
          <th id="feedbackStatus" width="10%" rowspan="1">Feedback Status</th>
        [/#if]
        [#--  <th class="owner" width="10%">Owner</th>  --]
        [#-- Action --]
        [#if currentTable]
          <th class="no-sort" colspan="3" width="11.5%">Action</th>
        [#else]
          <th class="no-sort" colspan="1" width="11.5%">Action</th>
        [/#if]

        [#-- Hidden column Don't remove it is neccesary for the library --]
        <th style="display: none"></th>
        
        [#--
        [#if currentTable]
        <th id="tb-missingFields" class="no-sort" width="1%"><p style="display: none;">Innovation RF</p></th>
        [/#if]
        <th id="tb-projectDownload" width="1%" class="no-sort"></th>
        [#if currentTable]
        <th id="tb-remove" width="1%" class="no-sort"></th>
        [/#if] --]
      </tr>
    </thead>
    <tbody>
    [#if list?has_content]
      [#list list as innovation]
        [#local tsURL][@s.url namespace="/projects" action="${(crpSession)!}/innovation"][@s.param name='innovationID']${(innovation.id)!''}[/@s.param][@s.param name='projectID']${(innovation.project.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        [#-- Is this complete --]
        [#local isThisComplete = (action.hasInnovationMissingFields(innovation.id))!false ]
        [#-- Is New --]
        [#local isNew = (action.isInnovationNew(innovation.id))!false ]
        [#-- Owner --]
        [#local isOwner = (innovation.project.id == projectID)!false]

        [#local scalingInnovationTitles = ["Idea", "Basic Research", "Formulation", "Proof of Concept", "Controlled Testing","Model/Early Prototype","Semi-Controlled Testing","Prototype","Uncontrolled Testing","Proven Innovation"]]

        [#local isScalingReadines = ((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.readinessScale?has_content))!false ]
          
        [#local scaleReadiness = (innovation.projectInnovationInfo.readinessScale-1)!"" ]

        [#local shortTitle]
          [#if ((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.title?has_content))]
            [@utils.wordCutter string=(innovation.projectInnovationInfo.title)!"" maxPos=120 /]
          [/#if]
        [/#local]
        <tr>
          [#-- ID --]
          <td class="tb-id text-center">
            <a href="${tsURL}" >${(innovation.id)!}</a>
          </td>
          [#-- Title --]
          <td class="tb-title">
            [#if isNew] <span class="label label-info">[@s.text name="global.new" /]</span> [/#if] 
            <a href="${tsURL}">[@utils.tableText value=shortTitle /]</a>
          </td>
          [#-- Type --]
          <td class="text-center">
            [@utils.tableText value=(innovation.projectInnovationInfo.repIndInnovationType.name)!"" /]
          </td>
          [#-- Nature --]
          <td class="text-center">
            [@utils.tableText value=(innovation.projectInnovationInfo.repIndInnovationNature.name)!"" /]
          </td>
          [#-- Readiness Level --]
          <td class="text-center">
            [#if isScalingReadines]
              <span class="inno-scale inno-scale-${scaleReadiness}">[@utils.tableText value=scaleReadiness /]</span>
              <span>${scalingInnovationTitles[scaleReadiness]!""}</span>
            [#else]
              [@utils.tableText value=(innovation.projectInnovationInfo.readinessLevel.name)!"" /]
            [/#if]

          </td>
          [#-- Year --]
          <td class="text-center">
            [@utils.tableText value=(innovation.projectInnovationInfo.year)!"" /]
          </td>
          [#-- Feedback Status --]
          [#if action.hasSpecificities('feedback_active') ]
              <td class="feedbackStatus">
                [@utils.tableText value=(innovation.commentStatus)!"" /]
              </td>
          [/#if]
          [#--      <td class="owner text-center">
                      [#if isOwner] <small><nobr>This Cluster</nobr></small>  [#else][#if innovation.project?has_content]${(innovation.project.acronym)!''}[#else]Not defined[/#if][/#if]
                    </td>  --]
          [#-- Missing fields --]
          [#if currentTable]
          <td class="text-center">
            [#if isThisComplete]
              <span class="icon-20 icon-check" title="Complete"><p style="display: none;">Complete</p></span> 
            [#else]
              <span class="icon-20 icon-uncheck" title=""><p style="display: none;">Incomplete</p></span> 
            [/#if]
          </td>
          [/#if]
          [#-- Summary PDF download --]
          <td class="text-center">
            <a href="[@s.url namespace="/summaries" action='${(crpSession)!}/projectInnovationSummary'][@s.param name='innovationID']${innovation.id?c}[/@s.param][@s.param name='phaseID']${(innovation.projectInnovationInfo.phase.id)!''}[/@s.param][/@s.url]" target="_blank">
              <img src="${baseUrlCdn}/global/images/pdf.png" height="25" title="[@s.text name="projectsList.downloadPDF" /]" />
            </a>            
          </td>
          [#-- Remove --]
          [#if currentTable]
            <td class="text-center">
              [#if canEdit && isOwner ]
                <a id="removeElement-${(innovation.id)!}" class="removeElementList" href="#" title="" data-toggle="modal" data-target="#removeItem-${innovation_index}" >
                [#--<a id="remove-innovation" class="remove-innovation" href="[@s.url namespace="/projects" action="${(crpSession)!}/deleteInnovation"][@s.param name='innovationID']${(innovation.id)!''}[/@s.param][@s.param name='projectID']${(innovation.project.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" title="" > --]
                  <img src="${baseUrlCdn}/global/images/trash.png" title="[@s.text name="projectInnovations.table.remove" /]" /> 
                </a>
                <div id="removeItem-${innovation_index}" class="modal fade" tabindex="-1" role="dialog">
                  <div class="modal-dialog" role="document">
                      <div class="modal-content">
                        [@s.form action="deleteInnovation.do"]
                          <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                            <h4 class="modal-title">Remove this item <br /> <small>${(innovation.projectInnovationInfo.title)!}</small> </h4>
                          </div>
                          <div class="modal-body">
                            [@customForm.textArea name="justification" i18nkey="projectInnovations.removeJustification" required=false className="removeJustification"/]
                            <input type="hidden"  name="innovationID" value="${(innovation.id)!}" />
                            <input type="hidden"  name="projectID" value="${(projectID)!}" />
                            <input type="hidden"  name="phaseID"  value="${(actualPhase.id)!}"/>
                          </div>
                          <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                            <button type="submit" class="btn btn-danger">Remove</button>
                          </div>
                        [/@s.form]
                      </div>
                    </div>
                </div>
              [#else]
                <img src="${baseUrlCdn}/global/images/trash_disable.png" title="[@s.text name="projectInnovations.table.cantDelete" /]" />
              [/#if]
            </td>
          [/#if]
          <td style="display: none"></td>
        </tr>
      [/#list]
    [#else]
      <tr>
        <td class="text-center" colspan="[#if currentTable]9[#else]6[/#if]">
          <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
        </td>
        [#-- Don't remove it is neccesary for the library --]
        <td style="display: none"></td>
      </tr>
    [/#if]
    </tbody>
  </table>
[/#macro]