[#ftl]
[#assign title = "POWB Synthesis" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ ] /]
[#assign customJS = [ ] /]
[#assign customCSS = ["${baseUrlMedia}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "crossCuttingDimensions" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/adjustmentsChanges"},
  {"label":"crossCuttingDimensions", "nameSpace":"powb", "action":"${crpSession}/crossCuttingDimensions"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="crossCuttingDimensions.help" /]
    
<section class="container">
  [#-- Program (Flagships and PMU) --]
  [#include "/WEB-INF/crp/views/powb/submenu-powb.ftl" /]
  
  <div class="row">
    [#-- POWB Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/crp/views/powb/menu-powb.ftl" /]
    </div> 
    <div class="col-md-9">
      [#-- Section Messages --]
      [#include "/WEB-INF/crp/views/powb/messages-powb.ftl" /]
      
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
        
        [#-- Title --]
        <h3 class="headTitle">[@s.text name="crossCuttingDimensions.title" /]</h3>
        <div class="borderBox">
          
          [#-- Briefly summarize the main areas of work in 2018 relevant to cross-cutting dimensions --] 
          <div class="form-group">
            [#if PMU]
              [@customForm.textArea name="powbSynthesis.powbCrossCuttingDimension.summarize" i18nkey="liaisonInstitution.powb.summarizeCorssCutting"  help="liaisonInstitution.powb.summarizeCorssCutting.help" helpIcon=false fieldEmptyText="global.prefilledByPmu" paramText="${(actualPhase.year)!}" required=true className="" editable=editable && PMU powbInclude=true /]
            [#else]
              [@customForm.textArea name="powbSynthesis.flagshipSummarize" i18nkey="liaisonInstitution.powb.summarizeCorssCutting"  help="liaisonInstitution.powb.summarizeCorssCutting.help" helpIcon=false fieldEmptyText="global.prefilledByPmu" paramText="${(actualPhase.year)!}" required=true className="" editable=editable && PMU /]
            [/#if]
          </div>
        
          [#-- Open Data and Intellectual Assets --] 
          <div class="form-group">
            [#if PMU]
              [@customForm.textArea name="powbSynthesis.powbCrossCuttingDimension.assets" i18nkey="liaisonInstitution.powb.openDataIntellectualAssests" help="liaisonInstitution.powb.openDataIntellectualAssests.help" helpIcon=false fieldEmptyText="global.prefilledByPmu" paramText="${(actualPhase.year)!}" required=true className="" editable=editable && PMU powbInclude=true /]
            [#else]
              [@customForm.textArea name="powbSynthesis.flagshipAssets" i18nkey="liaisonInstitution.powb.openDataIntellectualAssests" help="liaisonInstitution.powb.openDataIntellectualAssests.help" helpIcon=false fieldEmptyText="global.prefilledByPmu" paramText="${(actualPhase.year)!}" required=true className="" editable=editable && PMU /]
            [/#if]
          </div>
          
          [#-- Table C: Cross-cutting Aspect of Expected Deliverables (OPTIONAL) --]
          <div class="form-group">
            <h4 class="subTitle headTitle powb-table">[@s.text name="crossCuttingDimensions.tableC.title" /]</h4>
            <span class="powb-doc badge label-powb-table" title="[@s.text name="powb.includedField.title" /]">[@s.text name="powb.includedField" /] <span class="glyphicon glyphicon-save-file"></span></span>
            <hr />
            [@tableHMacro /]
          </div>
          
          
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#if PMU]
          [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        [/#if]
        
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/crp/pages/footer.ftl"]


[#---------------------------------------------- MACROS ----------------------------------------------]

[#macro tableHMacro ]
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
          <tr>
            <td colspan="4"><i class="text-center">No deliverables found.</i></td>
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
          [@tableDeliverablesMacro dList=deliverableList/]
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
          <th> Deliverable </th>
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