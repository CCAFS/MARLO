[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]


[#macro indicatorInformation name list index id="indicatorID" label="" editable=true]
  <div class="form-group">
    [#local customName = "${name}[${index}]"]
    [#local element = (list[index])!{} ]
    [#-- Hidden Inputs Indicator --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
    <input type="hidden"  name="${customName}.repIndSynthesisIndicator.id" value="${(element.repIndSynthesisIndicator.id)!}"/>
    
    [#-- Data --]
    <div class="form-group margin-panel">
      [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
      [@customForm.textArea name="${customName}.data" i18nkey="${label}.${id}.data" help="${label}.${id}.data.help" paramText="${(actualPhase.year)!}" fieldEmptyText="global.prefilledByPmu" className="" helpIcon=false required=true editable=editable /]
    </div>
    
    [#-- Comments/Analysis --]
    <div class="form-group margin-panel">
      [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
      [@customForm.textArea name="${customName}.comment" i18nkey="${label}.${id}.comments" help="${label}.${id}.comments.help" fieldEmptyText="global.prefilledByPmu" className="" helpIcon=false required=true editable=editable /]
    </div>
  </div>
[/#macro]


[#macro tableFPSynthesis tableName="tableName" list=[] columns=[] crpProgramField="reportSynthesis.liaisonInstitution.crpProgram" showEmptyRows=true showTitle=true showHeader=true]
  <div class="form-group">
    [#if showTitle]
      <h4 class="simpleTitle">[@s.text name="${tableName}.title" /]</h4>
    [/#if]
    <table class="table table-bordered">
      [#if showHeader]
      <thead>
        <tr>
          <th class="col-md-1 text-center"> [@s.text name="annualReport2018.tableFP" /] </th>
          [#list columns as column]<th class="text-center"> [@s.text name="${tableName}.column${column_index}" /] </th>[/#list]
        </tr>
      </thead>
      [/#if]
      <tbody>
        [#if list?has_content]
          [#list list as item] 
            [#-- CRP Program --]
            [#local crpProgram = (item)!{} /]
            [#list  (crpProgramField?split(".")) as level][#local crpProgram = (crpProgram[level])!{} /][/#list]
            [#-- Check if there is information available --]
            [#local isEmptyRow = true /]
            [#list columns as column][#if (item[column]?has_content)!false][#local isEmptyRow = false /][#break][/#if][/#list]
            [#if (isEmptyRow && showEmptyRows) || (!isEmptyRow) ]
            <tr>
              <td class="col-md-1 text-center">
                <span class="programTag" style="border-color:${(crpProgram.color)!'#fff'}">${(crpProgram.acronym)!}</span>
              </td>
              [#list columns as column]
                <td>
                  [#if (item[column]?has_content)!false] 
                    ${item[column]?replace('\n', '<br>')} 
                  [#else]
                    <i style="opacity:0.5">[@s.text name="global.prefilledByFlagship"/]</i>
                  [/#if]
                </td>
              [/#list]
            </tr>
            [/#if]
          [/#list]
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]


[#macro evidencesPopup element list]
  [#local className = 'SrfSloIndicatorTarget'/]
  [#local composedID = "${className}-${(element.id)!}"]
  <div id="${composedID}" class="form-group elementRelations ${className}">
    [#if list?has_content]
      [#-- Button --]
      <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#modal-projects-${composedID}">
        <span class="icon-20 files"></span> <strong>${list?size}</strong> [@s.text name="global.evidence" /](s)
      </button>
      
      [#-- Modal --]
      <div class="modal fade" id="modal-projects-${composedID}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        <div class="modal-dialog modal-lg" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
              <h4 class="modal-title" id="myModalLabel">
                [@s.text name="global.evidences" /] that are contributing to this [@s.text name="global.${className}" /] 
                <br />
                <small>${(element.narrative)!}</small>
              </h4>
            </div>
            <div class="modal-body">
              [#-- Projects table --]
              <table class="table">
                <thead>
                  <tr>
                    <th id="">Title</th>
                    <th id="">SRF Targets</th>
                    <th id="">Type</th>
                    <th id="">Year</th>
                    <th></th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  [#list list as item]
                    [#local url][@s.url namespace="/projects" action="${(crpSession)!}/study"][@s.param name='expectedID']${item.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                    [#local summaryPDF = "${baseUrl}/projects/${crpSession}/studySummary.do?studyID=${(item.id)!}&cycle=Reporting&year=${(actualPhase.year)!}"]
                    <tr>
                      <td class="col-md-5">  
                        [@utils.tableText value=(item.composedName)!"" /]
                        [#if item.project??]<br /> <small>(From Project P${item.project.id})</small> [/#if]
                      </td>
                      <td class="col-md-4"> [@utils.tableList list=(item.srfTargets)![] displayFieldName="srfSloIndicator.title" /] </td>
                      <td> [@utils.tableText value=(item.projectExpectedStudyInfo.studyType.name)!"" /] </td>
                      <td> [@utils.tableText value=(item.projectExpectedStudyInfo.year)!"" /] </td>
                      <td> <a href="${summaryPDF}" target="_blank"><img src="${baseUrl}/global/images/pdf.png" height="25" title="[@s.text name="projectsList.downloadPDF" /]" /></a>  </td>
                      <td> <a href="${url}" target="_blank"><span class="glyphicon glyphicon-new-window"></span></a>  </td>
                    </tr>
                  [/#list]
                </tbody>
              </table>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
          </div>
        </div>
      </div>
    [/#if]
  </div>

[/#macro]

[#function getMarker element name]
  [#list (element.crossCuttingMarkers)![] as ccm]
    [#if ccm.cgiarCrossCuttingMarker.name == name]
      [#return (ccm.repIndGenderYouthFocusLevel.powbName)!'0 - Not Targeted' ]
    [/#if]
  [/#list]
  [#return "0 - Not Targeted" ]
[/#function]