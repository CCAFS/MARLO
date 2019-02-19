[#ftl]
[#assign title = "Annual Report" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs"  ] /]
[#assign customJS = [
  "https://cdn.datatables.net/buttons/1.3.1/js/dataTables.buttons.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.html5.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.print.min.js", 
  "${baseUrlMedia}/js/annualReport/annualReportGlobal.js"
] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"annualReport", "nameSpace":"annualReport", "action":"${crpSession}/crpProgress"},
  {"label":"${currentStage}", "nameSpace":"annualReport", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "reportSynthesis.reportSynthesisCrossCuttingDimension" /]
[#assign customLabel= "annualReport.${currentStage}" /]

[#-- Helptext --]
[@utilities.helpBox name="annualReport.${currentStage}.help" /]
    
<section class="container">
  [#if !reportingActive]
    <div class="borderBox text-center">Annual Report is availbale only at Reporting cycle</div>
  [#else]
    [#-- Program (Flagships and PMU) --]
    [#include "/WEB-INF/crp/views/annualReport/submenu-annualReport.ftl" /]
    
    <div class="row">
      [#-- POWB Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/annualReport/menu-annualReport.ftl" /]
      </div> 
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/annualReport/messages-annualReport.ftl" /]
        
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
          <div class="borderBox">
           
            [#-- 1.3.1 Gender --]
            [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/][/#if]
            <h5 class="sectionSubTitle">[@s.text name="${customLabel}.genderTitle" /]</h5>
            [#-- Describe any important CRP research findings, capacity development or outcomes in 2017 related to Gender issues. --]
            <div class="form-group">
              [@customForm.textArea name="${customName}.genderDescription" i18nkey="${customLabel}.describeGenderIssues" help="${customLabel}.describeGenderIssues.help" className="" helpIcon=false required=true editable=editable /]
            </div>
            
            [#-- Please briefly highlight any lessons and implications for your future work on Gender. --]
            <div class="form-group">
              [@customForm.textArea name="${customName}.genderLessons" i18nkey="${customLabel}.lessonsGender" help="${customLabel}.lessonsGender.help" className="" helpIcon=false required=true editable=editable /]
            </div>
            
            [#-- Gender Synthesis Table--]
            [#if PMU]
            <div class="form-group">
              <div class="viewMoreSyntesis-block" >
                [@tableFlagshipSynthesis tableName="tableGender" list=flagshipCCDimensions columns=["genderDescription","genderLessons"] /]
              </div>
            </div>
            [/#if]
            
            [#-- 1.3.2 Youth --]
            [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/][/#if]
            <h5 class="sectionSubTitle">[@s.text name="${customLabel}.youthTitle" /]</h5>
            [#-- Describe any important CRP research findings, capacity development or outcomes in 2017 related to Youth issues. --]
            <div class="form-group">
              [@customForm.textArea name="${customName}.youthDescription" i18nkey="${customLabel}.describeYouthIssues" help="${customLabel}.describeYouthIssues.help" className="" helpIcon=false required=true editable=editable /]
            </div>
            
            [#-- Please briefly highlight any lessons and implications for your future work on Youth. --]
            <div class="form-group">
              [@customForm.textArea name="${customName}.youthLessons" i18nkey="${customLabel}.lessonsYouth" help="${customLabel}.lessonsYouth.help" className="" helpIcon=false required=true editable=editable /]
            </div>
            
            [#-- Youth Synthesis Table--]
            [#if PMU]
            <div class="form-group">
              <div class="viewMoreSyntesis-block" >
                [@tableFlagshipSynthesis tableName="tableYouth" list=flagshipCCDimensions columns=["youthDescription","youthLessons"] /]
              </div>
            </div>
            [/#if]
            
            [#-- 1.3.3 Other Aspects of Equity / “Leaving No-one Behind ” --]
            [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/][/#if]
            <h5 class="sectionSubTitle">[@s.text name="${customLabel}.otherAspectsTitle" /]</h5>
            [#-- Add information on other aspects of equity and your CRP’s contribution to “leaving no-one behind” --]
            <div class="form-group">
              [@customForm.textArea name="${customName}.otherAspects" i18nkey="${customLabel}.infoOtherAspects" help="${customLabel}.infoOtherAspects.help" className="" helpIcon=false required=true editable=editable /]
            </div>
            
            [#-- Other Table--]
            [#if PMU]
            <div class="form-group">
              <div class="viewMoreSyntesis-block" >
                [@tableFlagshipSynthesis tableName="tableOther" list=flagshipCCDimensions columns=["otherAspects"] /]
              </div>
            </div>
            [/#if]
            
            [#-- 1.3.4 Capacity Development --]
            [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/][/#if]
            <h5 class="sectionSubTitle">[@s.text name="${customLabel}.capDevTitle" /]</h5>
            [#-- Please summarize key achievements and learning points in Capacity Development this year--]
            <div class="form-group">
              [@customForm.textArea name="${customName}.capDev" i18nkey="${customLabel}.infoCapDev" help="${customLabel}.infoCapDev.help" className="" helpIcon=false required=true editable=editable /]
            </div>
            
            [#-- Capdev Table--]
            [#if PMU]
            <div class="form-group">
              <div class="viewMoreSyntesis-block" >
                [@tableFlagshipSynthesis tableName="tableCapDev" list=flagshipCCDimensions columns=["capDev"] /]
                
              </div>
            </div>
            [/#if]
            
            [#-- Table C: Cross-cutting Aspect of Outputs (ONLY READ)--]
            [#if PMU]
              [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
              <h4 class="simpleTitle">[@s.text name="${customLabel}.tableCTitle" /]</h4>
              <div class="form-group">
                [@tableCMacro /]
              </div>
            [/#if]
            
            [#-- 1.3.5 Open Data --]
            [#if PMU]
              [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/][/#if]
              <h5 class="sectionSubTitle">[@s.text name="${customLabel}.openDataTitle" /]</h5>
              [#-- Please provide a brief summary on CRP progress, challenges, and lessons with implementing the open data commitment. --]
              <div class="form-group">
                [@customForm.textArea name="${customName}.openData" i18nkey="${customLabel}.openData" help="${customLabel}.openData.help" className="" helpIcon=false required=true editable=editable /]
              </div>
            [/#if]
            
            [#-- Table D-2: List of CRP Innovations in 2017 (From indicator #C1 in Table D-1)  --]
            [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
            <h4 class="simpleTitle">[@customForm.text name="${customLabel}.tableD2Title" param="${currentCycleYear}" /]</h4>
            <div class="form-group">
              [#if flagship]
                [@tableD2InnovationsList name="${customName}.innovationsValue" list=innovationsList /]
              [#else]
                <div class="viewMoreSyntesis-block" >
                  [@tableD2InnovationsList name="${customName}.innovationsValue" list=flagshipPlannedInnovations  isPMU=PMU /]
                  
                </div>
              [/#if]
            </div>
            
            [#-- 1.3.6 Intellectual Assets --]
            [#if PMU && action.hasSpecificities(action.crpDeliverableIntellectualAsset())]
              [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
              <h5 class="sectionSubTitle">[@s.text name="${customLabel}.intellectualAssetsTitle" /]</h5>
              [#-- Please provide a brief summary under the three following headings --]
              <div class="form-group">
                [@customForm.textArea name="${customName}.intellectualAssets" i18nkey="${customLabel}.intellectualAssets" help="${customLabel}.intellectualAssets.help" className="" helpIcon=false required=true editable=editable /]
              </div>
            [/#if]
            
            [#-- Table E: Intellectual Assets  --]
            [#if action.hasSpecificities(action.crpDeliverableIntellectualAsset())]
              [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
              <h4 class="simpleTitle">[@s.text name="${customLabel}.tableETitle" /]</h4>
              <div class="form-group">
                [#if flagship]
                  [@tableEIntellectualAssets name="${customName}.assetsValue" list=assetsList  /]
                [#else]
                  <div class="viewMoreSyntesis-block" >
                    [@tableEIntellectualAssets name="${customName}.assetsValue" list=flagshipPlannedAssets  isPMU=PMU /]
                    
                  </div>
                [/#if]
              </div>
            [/#if]
            
            
          </div>
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/annualReport/buttons-annualReport.ftl" /]
          
        [/@s.form] 
      </div> 
    </div>
  [/#if]
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]

[#---------------------------------------------------- MACROS ----------------------------------------------------]

[#macro tableCMacro ]
  <div class="">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th> [@s.text name="crossCuttingDimensions.tableC.crossCutting" /] </th>
          <th> [@s.text name="crossCuttingDimensions.tableC.principal" /] </th>
          <th> [@s.text name="crossCuttingDimensions.tableC.significant" /] </th>
          <th> [@s.text name="crossCuttingDimensions.tableC.notTargeted" /] </th>
          <th> [@s.text name="crossCuttingDimensions.tableC.overall" /] </th>
        </tr>
      </thead>
      <tbody>
        [#if tableC??]
          <tr>
            <td class="row"><strong>Gender</strong></td>
            <td class="text-center"> <span class="animated flipInX">${tableC.percentageGenderPrincipal?string(",##0.00")}%</span> </td>
            <td class="text-center"> <span class="animated flipInX">${tableC.percentageGenderSignificant?string(",##0.00")}%</span> </td>
            <td class="text-center"> <span class="animated flipInX">${tableC.percentageGenderNotScored?string(",##0.00")}%</span> </td> 
            <td rowspan="3" class="text-center"> 
              <h3 class="animated flipInX">
                <a class="btn btn-default btn-lg" data-toggle="modal" data-target="#overallOutputsMacro">
                  <span class="glyphicon glyphicon-fullscreen" style="color:#b3b3b3"></span> ${tableC.total}
                </a>
              </h3>
            </td> 
          </tr>
          <tr>
            <td class="row"><strong>Youth</strong></td>
            <td class="text-center"> <span class="animated flipInX">${tableC.percentageYouthPrincipal?string(",##0.00")}%</span> </td>
            <td class="text-center"> <span class="animated flipInX">${tableC.percentageYouthSignificant?string(",##0.00")}%</span> </td>
            <td class="text-center"> <span class="animated flipInX">${tableC.percentageYouthNotScored?string(",##0.00")}%</span> </td>            
          </tr>
          <tr>
            <td class="row"><strong>CapDev</strong></td>
            <td class="text-center"> <span class="animated flipInX">${tableC.percentageCapDevPrincipal?string(",##0.00")}%</span> </td>
            <td class="text-center"> <span class="animated flipInX">${tableC.percentageCapDevSignificant?string(",##0.00")}%</span> </td>
            <td class="text-center"> <span class="animated flipInX">${tableC.percentageCapDevNotScored?string(",##0.00")}%</span> </td>            
          </tr>
        [#else]
          <tr class="text-center">
            <td colspan="5"><i class="text-center">No deliverables found.</i></td>
          </tr>
        [/#if]
      </tbody>
    </table>
  </div>
  
  <!-- Modal -->
  <div class="modal fade" id="overallOutputsMacro" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog modal-lg" style="" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
          <h4 class="subTitle headTitle"> Deliverables </h4>
        </div>
        <div class="modal-body">
          [@tableDeliverablesMacro dList=(deliverableList)![] /]
        </div>
        <div class="modal-footer"><button type="button" class="btn btn-default" data-dismiss="modal">Close</button></div>
      </div>
    </div>
  </div>
[/#macro]

[#macro tableDeliverablesMacro dList]
  <div class="">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th>Deliverable </th>
          <th>Gender</th>
          <th>Youth</th>
          <th>CapDev</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        [#if dList?has_content]
          [#list dList as dInfo]
          <tr>
            <td class="row">
              <span title="${(dInfo.title)!}"><strong>D${(dInfo.deliverable.id)!}</strong> [@utilities.wordCutter string="${(dInfo.title)!'Not Defined'}" maxPos=70 /] </span> <br />
              <small>(${(dInfo.deliverableType.name)!'Not Defined'})</small>
            </td>
            <td>
              <nobr>
              [#if (dInfo.crossCuttingGender)!false]
                [#if dInfo.genderScoreName??]${(dInfo.genderScoreName)!}[#else]0 - Not Targeted[/#if]
              [#else]
                 0 - Not Targeted
              [/#if]
              </nobr>
            </td>
            <td>
              <nobr>
              [#if (dInfo.crossCuttingYouth)!false]
                [#if dInfo.youthScoreName??]${(dInfo.youthScoreName)!}[#else]0 - Not Targeted[/#if]
              [#else]
                 0 - Not Targeted
              [/#if]
              </nobr>
            </td>
            <td>
              <nobr>
              [#if (dInfo.crossCuttingCapacity)!false]
                [#if dInfo.capDevScoreName??]${(dInfo.capDevScoreName)!}[#else]0 - Not Targeted[/#if]
              [#else]
                 0 - Not Targeted
              [/#if]
              </nobr>
            </td>
            <td>
              [#local dURL][@s.url namespace="/projects" action="${(crpSession)!}/deliverable"][@s.param name='deliverableID']${dInfo.deliverable.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
              <a href="${dURL}" target="_blank"><span class="glyphicon glyphicon-new-window"></span></a>
            </td>
          </tr>
          [/#list]
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]

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
            [#local crpProgram = (item.reportSynthesis.liaisonInstitution.crpProgram)!{} ]
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
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]

[#macro tableD2InnovationsList name list=[]  isPMU=false ]
  <div class="">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th> [@s.text name="${customLabel}.tableD2.titleInnovation" /] </th>
          <th> [@s.text name="${customLabel}.tableD2.stage" /] </th>
          <th> [@s.text name="${customLabel}.tableD2.degree" /] </th>
          <th> [@s.text name="${customLabel}.tableD2.contributionCRP" /] </th>
          <th> [@s.text name="${customLabel}.tableD2.scope" /] </th>
          [#if !isPMU]<th class="col-md-1"> [@s.text name="${customLabel}.tableD2.includeAR" /] </th>[/#if]
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
            [#if isPMU]
              [#local element = item.projectInnovation ]
            [#else]
              [#local element = item ]
            [/#if]
            [#local customName = "${name}" /]
            [#local URL][@s.url namespace="/projects" action="${(crpSession)!}/innovation"][@s.param name='innovationID']${(element.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
            <tr>
              <td>
                [#-- Title --]
                <a href="${URL}" target="_blank">[#if ((element.projectInnovationInfo.title)?has_content)!false] ${element.projectInnovationInfo.title}[#else]Untitled[/#if]</a>
                [#-- Project ID --]
                [#if (element.project.id??)!false]<br /><i style="opacity:0.5">(From Project P${(element.project.id)!})</i> [/#if]
                [#-- Flagships --]
                [#if isPMU]
                  <div class="clearfix"></div>
                  [#list (item.liaisonInstitutions)![] as liaisonInstitution]
                    <span class="programTag" style="border-color:${(liaisonInstitution.crpProgram.color)!'#fff'}">${(liaisonInstitution.crpProgram.acronym)!}</span>
                  [/#list]
                [/#if]
              </td>
              [#-- Stage of Innovation --]
              <td>
                [#if (element.projectInnovationInfo.repIndStageInnovation?has_content)!false]
                  ${element.projectInnovationInfo.repIndStageInnovation.name}
                [#else]
                  <i class="text-center" style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
                [/#if]
              </td>
              [#-- Degree of Innovation --]
              <td>
                [#if (element.projectInnovationInfo.repIndDegreeInnovation?has_content)!false]
                  ${element.projectInnovationInfo.repIndDegreeInnovation.name}
                [#else]
                  <i class="text-center" style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
                [/#if]
              </td>
              [#-- Contribution of CRP --]
              <td>
                [#if (element.projectInnovationInfo.repIndContributionOfCrp?has_content)!false]
                  ${element.projectInnovationInfo.repIndContributionOfCrp.name}
                [#else]
                  <i class="text-center" style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
                [/#if]
              </td>
              [#-- Geographic Scope --]
              <td>
                [#if (element.projectInnovationInfo.repIndGeographicScope?has_content)!false]
                  ${element.projectInnovationInfo.repIndGeographicScope.name}
                [#else]
                  <i class="text-center" style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
                [/#if]
              </td>
              [#if !isPMU]
              <td class="text-center">
                [@customForm.checkmark id="innovation-${item_index}" name="${customName}" label="" value="${(element.id)!}" editable=editable checked=(!reportSynthesis.reportSynthesisCrossCuttingDimension.innovationIds?seq_contains(element.id))!false cssClass="" /]
              </td>
              [/#if]
            </tr>
          [/#list]
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]

[#macro tableEIntellectualAssets name list=[]  isPMU=false ]
  <div class="">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th> [@s.text name="${customLabel}.tableE.title" /] </th>
          <th> [@s.text name="${customLabel}.tableE.year" /] </th>
          <th> [@s.text name="${customLabel}.tableE.applicants" /] </th>
          <th> [@s.text name="${customLabel}.tableE.pattentPvp" /] </th>
          <th> [@s.text name="${customLabel}.tableE.additional" /] </th>
          <th> [@s.text name="${customLabel}.tableE.link" /] </th>
          <th> [@s.text name="${customLabel}.tableE.communication" /] </th>
          [#if !isPMU]<th class="col-md-1"> [@s.text name="${customLabel}.tableE.includeAR" /] </th>[/#if]
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
            [#if isPMU]
              [#local element = item.deliverableIntellectualAsset ]
            [#else]
              [#local element = item ]
            [/#if]
            [#local customName = "${name}" /]
            [#local URL][@s.url namespace="/projects" action="${(crpSession)!}/deliverable"][@s.param name='deliverableID']${(element.deliverable.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
            <tr>
              <td>
                [#-- Title --]
                <a href="${URL}" target="_blank">[#if ((element.title)?has_content)!false] ${element.title}[#else]Untitled[/#if]</a>
                [#-- Project ID --]
                [#if (element.deliverable.id??)!false]<br /><i style="opacity:0.5">(From Deliverable D${(element.deliverable.id)!})</i> [/#if]
                [#-- Flagships --]
                [#if isPMU]
                  <div class="clearfix"></div>
                  [#list (item.liaisonInstitutions)![] as liaisonInstitution]
                    <span class="programTag" style="border-color:${(liaisonInstitution.crpProgram.color)!'#fff'}">${(liaisonInstitution.crpProgram.acronym)!}</span>
                  [/#list]
                [/#if]
              </td>
              [#-- Year reported --]
              <td class="text-center">
                [#local dInfo = (element.deliverable.deliverableInfo)!{}]
                [#if ((dInfo.newExpectedYear?has_content)!false) && (dInfo.newExpectedYear != -1)]
                  ${dInfo.newExpectedYear} <small>(Extended)</small>
                [#elseif ((dInfo.year?has_content)!false) && (dInfo.year != -1)]
                  ${dInfo.year} 
                [#else]
                  <i class="text-center" style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
                [/#if]
              </td>
              [#-- Applicant(s) / owner(s) (Center or partner) --]
              <td>
                [#if (element.applicant?has_content)!false]
                  ${element.applicant}
                [#else]
                  <i class="text-center" style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
                [/#if]
              </td>
              [#-- Type (Patent or PVP) --]
              <td>
                [#if (element.type?has_content)!false]
                  ${(element.type == 1)?string('Patent', 'PVP')}
                [#else]
                  <i class="text-center" style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
                [/#if]
              </td>
              [#-- Additional Information --]
              <td>
                [#if (element.additionalInformation?has_content)!false]
                  ${element.additionalInformation}
                [#else]
                  <i class="text-center" style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
                [/#if]
              </td>
              [#-- Link or PDF of published application/ registration --]
              <td>
                [#if (element.link?has_content)!false]
                  ${element.link}
                [#else]
                  <i class="text-center" style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
                [/#if]
              </td>
              [#-- Public communication relevant to the application/registration --]
              <td>
                [#if (element.publicCommunication?has_content)!false]
                  ${element.publicCommunication}
                [#else]
                  <i class="text-center" style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
                [/#if]
              </td>
              [#if !isPMU]
              <td class="text-center">
                [@customForm.checkmark id="innovation-${item_index}" name="${customName}" label="" value="${(element.id)!}" editable=editable checked=(!reportSynthesis.reportSynthesisCrossCuttingDimension.assetIds?seq_contains(element.id))!false cssClass="" /]
              </td>
              [/#if]
            </tr>
          [/#list]
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]