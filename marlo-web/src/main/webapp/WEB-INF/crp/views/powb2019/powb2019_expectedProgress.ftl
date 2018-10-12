[#ftl]
[#assign title = "POWB Synthesis" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${powbSynthesisID}" /]
[#assign pageLibs = [ "trumbowyg"] /]
[#assign customJS = [ ] /]
[#assign customCSS = ["${baseUrlMedia}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "expectedProgress" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb2019", "action":"${crpSession}/adjustmentsChanges"},
  {"label":"expectedProgress", "nameSpace":"powb2019", "action":""}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "powbSynthesis.expectedProgress" /]
[#assign customLabel= "powbSynthesis.${currentStage}" /]


[#-- Helptext --]
[@utilities.helpBox name="${customLabel}.help" /]
    
<section class="container">
  [#-- Program (Flagships and PMU) --]
  [#include "/WEB-INF/crp/views/powb2019/submenu-powb2019.ftl" /]
  
  <div class="row">
    [#-- POWB Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/crp/views/powb2019/menu-powb2019.ftl" /]
    </div> 
    <div class="col-md-9">
      [#-- Section Messages --]
      [#include "/WEB-INF/crp/views/powb2019/messages-powb2019.ftl" /]
      
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
        
        [#-- Title --]
        <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
        <div class="borderBox">
          <div class="form-group margin-panel">
            [#-- Provide a short narrative of expected highlights of the CRP/PTF in the coming year --]
            <div class="form-group">
              [#if PMU][@utilities.tag label="powb.docBadge" tooltip="powb.docBadge.tooltip"/][/#if]
              [@customForm.textArea name="${customName}.narrative" i18nkey="${customLabel}.narrative" help="${customLabel}.narrative.help" helpIcon=false required=true className="" editable=editable allowTextEditor=true   /]
            </div>
            
            [#if PMU]
            <div class="form-group">
              [@tableFlagshipSynthesis tableName="narrativeFlagshipsTable" list=tocList columns=["narrative"] /]
            </div>
            [/#if]
          </div>
          
          <hr />
          
          [#-- Table 2A: Planned Milestones  --]
          [@utilities.tag label="powb.docBadge" tooltip="powb.docBadge.tooltip"/]
          <button type="button" class="pull-right btn btn-link btn-sm" data-toggle="modal" data-target="#tableA-bigger"> 
              <span class="glyphicon glyphicon-fullscreen"></span> See Full Table 2A
            </button>
          <div id="tableA-bigger" class="modal fade bs-example-modal-lg " tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
            <div class="modal-dialog modal-lg bigger" role="document">
              <div class="modal-content">
                <div class="modal-header">
                  <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                </div>
                [@tableA2Milestones list=flagships includeAllColumns=true /]
              </div>
            </div>
          </div>
            
          <h4 class="sectionSubTitle">[@s.text name="${customLabel}.tableA2Milestones.title" /]</h4>
          <div class="form-group">
            [@tableA2Milestones list=flagships includeAllColumns=false /]
          </div>
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>

[#include "/WEB-INF/global/pages/footer.ftl"]

[#----------------------------------------------------- MACROS --------------------------------------------------------]

[#macro tableFlagshipSynthesis tableName="tableName" list=[] columns=[] ]
  <div class="form-group">
    <h4 class="simpleTitle">[@s.text name="${customLabel}.${tableName}.title" /]</h4>
    <table class="table table-bordered">
      <thead>
        <tr>
          <th class="col-md-1 text-center"> FP </th>
          [#list columns as column]<th> [@s.text name="${customLabel}.${tableName}.column${column_index}" /] </th>[/#list]
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
            [#local crpProgram = (item.liaisonInstitution.crpProgram)!{} ]
            <tr>
              <td>
                <span class="programTag" style="border-color:${(crpProgram.color)!'#fff'}">${(crpProgram.acronym)!}</span>
              </td>
              [#list columns as column]
                <td>
                  [#if (item[column]?has_content)!false] 
                    ${item[column]?replace('\n', '<br>')} 
                  [#else]
                    <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
                  [/#if]
                </td>
              [/#list]
            </tr>
          [/#list]
        [#else]
          <tr>
            <td class="text-center" colspan="${columns?size + 1}"><i>No flagships loaded...</i></td>
          </tr>
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]

[#macro tableA2Milestones  list=[] allowPopups=false id="" includeAllColumns=true  ]
  <div class="">[#-- <div class="table-responsive"> --]
    <table id="table2A-POWB2019" class="table table-bordered">
      <thead>
        <tr>
          [#if PMU || includeAllColumns]<th rowspan="2" > FP </th>[/#if]
          [#if includeAllColumns]<th rowspan="2" > Mapped to Sub-IDO </th>[/#if]
          [#if includeAllColumns]<th rowspan="2" > 2022 FP outcomes </th>[/#if]
          <th rowspan="2" >[@s.text name="expectedProgress.tableA.milestone" /]</th>
          [#if includeAllColumns]<th rowspan="2" > Indicate of the following </th>[/#if]
          <th rowspan="2" >[@s.text name="expectedProgress.tableA.meansVerification" /]</th>
          <th rowspan="1" colspan="4" class="text-center"> CGIAR Cross-Cutting Markers </th> 
          <th rowspan="2" class="text-center" > Assessment of risk (L/M/H) </th>
          [#if includeAllColumns]<th rowspan="2" class="text-center"> Main risk for (M/H) </th>[/#if]
          [#if flagship && !includeAllColumns] <th rowspan="2" class="text-center"> Include in POWB</th>[/#if]
        </tr>
        <tr>
          <th class="text-center"> Gender </th>
          <th class="text-center"> Youth </th>
          <th class="text-center"> CapDev</th>
          <th class="text-center"> Climate Change</th>
        </tr>
      </thead>
      <tbody>
        [#list list as fp]
          [#assign milestoneSize = fp.milestones?size]
          [#list fp.outcomes as outcome]
            [#assign outcomesSize = outcome.milestones?size]
            [#list outcome.milestones as m]
              [#assign isFlagshipRow = (outcome_index == 0) && (m_index == 0)]
              [#assign isOutcomeRow = (m_index == 0)]
              
              <tr class="fp-index-${fp_index} outcome-index-${outcome_index} milestone-index-${m_index}">
                [#-- Flagship --]
                [#if isFlagshipRow && (PMU || includeAllColumns)]
                  <th rowspan="${milestoneSize}" class="milestoneSize-${milestoneSize}" style="background:${(fp.color)!'#fff'}"><span class="programTag" style="border-color:${(fp.color)!'#fff'}">${fp.acronym}</span></th>
                [/#if]
                [#-- Sub-IDO --]
                [#if isOutcomeRow && includeAllColumns]<td rowspan="${outcomesSize}"> 
                  <ul>[#list outcome.subIdos as subIdo]<li> [#if (subIdo.srfSubIdo.srfIdo.isCrossCutting)!false] <strong title="Cross-Cutting IDO">CC</strong> [/#if]${(subIdo.srfSubIdo.description)!}</li>[/#list]</ul>
                </td>
                [/#if]
                [#-- Outcomes --]
                [#if isOutcomeRow && includeAllColumns]
                  <td rowspan="${outcomesSize}" class="milestonesSize-${outcomesSize}"> ${(outcome.composedName)!}</td>
                [/#if]
                [#-- Milestone --]
                <td> ${(m.composedName)!}  [#-- <div class="pull-right">[@milestoneContributions element=milestone tiny=true /] --]  </div></td>
                [#-- Indicate of the following --]
                [#if includeAllColumns]
                  <td> [#if (m.powbIndFollowingMilestone.name?has_content)!false]${m.powbIndFollowingMilestone.name}[#else] [@utils.prefilledTag /] [/#if] </td>
                [/#if]
                [#-- Means Verification --]
                <td class="col-md-4">[#if (m.powbMilestoneVerification?has_content)!false]${m.powbMilestoneVerification}[#else] [@utils.prefilledTag /] [/#if]</td>
                [#-- Gender --]
                <td class="text-center"> [#if (m.genderFocusLevel?has_content)!false] <p class="dacMarker level-${m.genderFocusLevel.id}" title="${m.genderFocusLevel.name}">${m.genderFocusLevel.acronym}</p> [#else][@utils.prefilledTag /][/#if] </td>
                [#-- Youth --]
                <td class="text-center"> [#if (m.youthFocusLevel?has_content)!false] <p class="dacMarker level-${m.youthFocusLevel.id}" title="${m.youthFocusLevel.name}">${m.youthFocusLevel.acronym}</p> [#else][@utils.prefilledTag /][/#if] </td>
                [#-- CapDev --]
                <td class="text-center"> [#if (m.capdevFocusLevel?has_content)!false] <p class="dacMarker level-${m.capdevFocusLevel.id}" title="${m.capdevFocusLevel.name}">${m.capdevFocusLevel.acronym}</p> [#else][@utils.prefilledTag /][/#if] </td>
                [#-- Climate Change --]
                <td class="text-center"> [#if (m.climateFocusLevel?has_content)!false] <p class="dacMarker level-${m.climateFocusLevel.id}" title="${m.climateFocusLevel.name}">${m.climateFocusLevel.acronym}</p> [#else][@utils.prefilledTag /][/#if] </td>
                [#-- Assessment Risk --]
                <td class="center">[#if (m.powbIndAssesmentRisk?has_content)!false]${m.powbIndAssesmentRisk.name}[#else] [@utilities.prefilledTag /] [/#if]</td>
                [#-- For medium/high please select the main risk from the list --]
                [#if includeAllColumns]
                  <td>
                    [#if (m.powbIndMilestoneRisk?has_content)!false]
                      ${m.powbIndMilestoneRisk.name}
                      [#if (m.powbMilestoneOtherRisk?has_content)!false]<br />(<i>${m.powbMilestoneOtherRisk}</i>)[/#if]
                    [#else]
                      [@utilities.prefilledTag /] 
                    [/#if]
                  </td>
                [/#if]
                [#-- Include in POWB --]
                [#if flagship && !includeAllColumns]
                  [#local isMilestoneChecked = ((!powbSynthesis.expectedProgress.milestonesIds?seq_contains(m.id))!true) /]
                  <td class="text-center"> [@customForm.checkmark id="milestoneCheck-${(m.id)!''}" name="${customName}.milestonesValue" value="${(m.id)!''}" checked=isMilestoneChecked editable=editable centered=true/] </td> 
                [/#if]
              </tr>
            [/#list]
          [/#list]
        [/#list]
      </tbody>
    </table>
  </div>
[/#macro]