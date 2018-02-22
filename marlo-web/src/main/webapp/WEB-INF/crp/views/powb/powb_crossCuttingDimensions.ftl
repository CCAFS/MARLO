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
              [@customForm.textArea name="powbSynthesis.powbCrossCuttingDimension.summarize" i18nkey="liaisonInstitution.powb.summarizeCorssCutting"  help="liaisonInstitution.powb.summarizeCorssCutting.help" paramText="${(actualPhase.year)!}" required=true className="limitWords-100" editable=editable && PMU /]
          </div>
        
          [#-- Open Data and Intellectual Assets --] 
          <div class="form-group">
            [@customForm.textArea name="powbSynthesis.powbCrossCuttingDimension.assets" i18nkey="liaisonInstitution.powb.openDataIntellectualAssests" help="liaisonInstitution.powb.openDataIntellectualAssests.help" paramText="${(actualPhase.year)!}" required=true className="limitWords-100" editable=editable && PMU /]
          </div>
          
          [#-- Table C: Cross-cutting Aspect of Expected Deliverables (OPTIONAL) --]
          <div class="form-group">
            <h4 class="subTitle headTitle">[@s.text name="crossCuttingDimensions.tableC.title" /]</h4>
            <hr />
            [@tableHMacro /]
          </div>
          
          <div class="form-group">
            [@tableDeliverablesMacro /]
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
          <tr>
            <td class="row">Gender</td>
            <td class="text-center"> <span class="animated flipInX">${tableC.percentageGenderPrincipal}%</span> </td>
            <td class="text-center"> <span class="animated flipInX">${tableC.percentageGenderSignificant}%</span> </td>
            <td class="text-center"> <span class="animated flipInX">${tableC.percentageGenderNotScored}%</span> </td> 
            <td rowspan="3" class="text-center"> 
              <h3 class="animated flipInX">${tableC.total}</span> </h3>
            </td> 
          </tr>
          <tr>
            <td class="row">Youth</td>
            <td class="text-center"> <span class="animated flipInX">${tableC.percentageYouthPrincipal}%</span> </td>
            <td class="text-center"> <span class="animated flipInX">${tableC.percentageYouthSignificant}%</span> </td>
            <td class="text-center"> <span class="animated flipInX">${tableC.percentageYouthNotScored}%</span> </td>            
          </tr>
          <tr>
            <td class="row">CapDev</td>
            <td class="text-center"> <span class="animated flipInX">${tableC.percentageCapDevPrincipal}%</span> </td>
            <td class="text-center"> <span class="animated flipInX">${tableC.percentageCapDevSignificant}%</span> </td>
            <td class="text-center"> <span class="animated flipInX">${tableC.percentageCapDevNotScored}%</span> </td>            
          </tr>
      </tbody>
    </table>
  </div>
[/#macro]


[#macro tableDeliverablesMacro ]
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
        [#if deliverableList?has_content]
          [#list deliverableList as dInfo]
          <tr>
            <td class="row">
              <p> <strong>D${(dInfo.deliverable.id)!}</strong> ${(dInfo.title)!'Not Defined'}</p>
              <small>(${(dInfo.deliverableType.name)!'Not Defined'})</small>
            </td>
            <td>
              <nobr>
              [#if (dInfo.crossCuttingGender)!false]
                [#if dInfo.genderScoreName??]${(dInfo.genderScoreName)!}[/#if]
              [#else]
                 0 - Not Targeted
              [/#if]
              </nobr>
            </td>
            <td>
              <nobr>
              [#if (dInfo.crossCuttingYouth)!false]
                [#if dInfo.youthScoreName??]${(dInfo.youthScoreName)!}[/#if]
              [#else]
                 0 - Not Targeted
              [/#if]
              </nobr>
            </td>
            <td>
              <nobr>
              [#if (dInfo.crossCuttingCapacity)!false]
                [#if dInfo.capDevScoreName??]${(dInfo.capDevScoreName)!}[/#if]
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