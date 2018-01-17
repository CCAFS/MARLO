[#ftl]
[#assign title = "CRP Indicators" /]
[#assign currentSectionString = "synthesis-${actionName?replace('/','-')}-${liaisonInstitutionID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = [
  "${baseUrlMedia}/js/synthesis/crpIndicators.js",
  "${baseUrl}/global/js/autoSave.js",
  "${baseUrl}/global/js/fieldsValidation.js"
] /]
[#assign customCSS = ["${baseUrlMedia}/css/synthesis/synthesisGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "crpIndicators" /]

[#assign breadCrumb = [
  {"label":"synthesis", "nameSpace":"/synthesis", "action":"crpIndicators"},
  {"label":"crpIndicators", "nameSpace":"/synthesis", "action":""}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]


<section class="container">
    <div class="row"> 
      <div class="col-md-12">
        [#-- Help Message --]
        <div class="container helpText viewMore-block">
          <div class="helpMessage infoText">
            <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
            <p class="col-md-10"> [@s.text name="synthesis.crpIndicators.help" /]</p>
          </div> 
          <div style="display:none" class="viewMore closed"></div>
        </div>
        
        <article class="form-group clearfix" id="crpIndicators">
          [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass="pure-form"]
          [#-- Program (Regions and Flagships) --]
          <ul id="liaisonInstitutions" class="horizontalSubMenu text-center">
            [#list liaisonInstitutions as institution]
              [#assign isActive = (institution.id == liaisonInstitutionID)/]
              [#assign isCompleted = (action.isCompleteCrpIndicator(institution.id))!false /]
              [#assign hasPermission = (action.hasPermissionCrpIndicators(institution.id))!false /]
              <li class="${isActive?string('active','')} ${hasPermission?string('canEdit','')}">
                <a href="[@s.url][@s.param name ="liaisonInstitutionID"]${institution.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">${institution.acronym}</a>
                [#if isCompleted] <p class="synthesisCompleted"> <img src="${baseUrl}/global/images/icon-check-tiny${isActive?string('-white','')}.png"/> </p> [/#if]
              </li>
            [/#list]
          </ul>
          
          [#-- Messages --]
          [#include "/WEB-INF/crp/views/synthesis/messages-crpIndicators.ftl" /]
           
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="synthesis.crpIndicators.title" /]</h3>
          
          <div id="crpIndicatorsTabs">
            <ul class="nav nav-tabs" role="tablist">
              [#list indicatorsType as indicatorType]  
                <li title="${indicatorType.name}" class="[#if indicatorType_index == 0]active[/#if]">
                  <a href="#indicatorType-${indicatorType.id}" role="tab" data-toggle="tab">[@utilities.wordCutter string=indicatorType.name maxPos=34 /]</a>
                </li>
              [/#list]
            </ul>
            <div class="tab-content">
            [#list indicatorsType as indicatorType]
              <div id="indicatorType-${indicatorType.id}" class="tab-pane indicatorsByType [#if indicatorType_index == 0]active[/#if]" role="tabpanel">
                
                
                [#-- List of indicators by type --> action.getCrpIndicatorsByType(indicatorType.id) as indicatorReport --]
                [#list action.getCrpIndicatorsByType(indicatorType.id) as indicatorReport]
                [#assign customIndex = (action.getIndicatorIndex(indicatorReport.crpIndicator.id,indicatorReport.crpIndicator.crpIndicatorType.id))!-1 ]
                [#assign customName= "currentLiaisonInstitution.indicatorReports[${customIndex}]"]
                <div class="simpleBox">
                  [#-- title --]
                  <h6 class="title" style="font-size: 1.2em;margin-bottom: 5px;">${indicatorReport.crpIndicator.id}.  ${indicatorReport.crpIndicator.name}
                    [#if indicatorReport.crpIndicator.description?has_content]
                      <a id="showIndicatorDesc-${indicatorReport.crpIndicator.id}" class="showIndicatorDesc" href="#"><img src="${baseUrl}/global/images/icon-info.png" title="Show indicator description" alt="" /></a>
                    [/#if]
                  </h6>
                  [#if indicatorReport.crpIndicator.description?has_content]
                    <div class="fullPartBlock"><p id="indicatorDesc-${indicatorReport.crpIndicator.id}" >${indicatorReport.crpIndicator.description}</p></div>
                  [/#if]
                  
                  [#-- Label --]
                  [#if action.isFlagship()]
                    [#if indicatorReport.crpIndicator.id lte 6] 
                      <div class="form-group"><span class="label label-warning"><strong> [@s.text name="synthesis.crpIndicators.lookAtCenter" /]</strong></span></div>
                    [#else]
                      <div class="form-group"><span class="label label-info"><strong> [@s.text name="synthesis.crpIndicators.reportOnFlagship" /]</strong></span></div>
                    [/#if]
                  [/#if]
                  
                  [#-- Targets --]
                  <div class="fullPartBlock">
                    <div class="thirdPartBlock">[@customForm.input name="${customName}.target" type="text" i18nkey="synthesis.crpIndicators.target" className="isNumeric" help="form.message.numericValue" paramText="${reportingYear}" editable=false /]</div>
                    <div class="thirdPartBlock">[@customForm.input name="${customName}.actual" type="text" i18nkey="synthesis.crpIndicators.actual" className="isNumeric" help="form.message.numericValue" paramText="${reportingYear}" required=canEdit editable=editable /]</div>
                    <div class="thirdPartBlock">[@customForm.input name="${customName}.nextTarget" type="text" i18nkey="synthesis.crpIndicators.nextYearTarget" className="isNumeric" help="form.message.numericValue" paramText="${reportingYear+1}" required=canEdit editable=editable /]</div>
                  </div>
                  [#-- Link to supporting databases --]
                  <div class="fullPartBlock">
                    [@customForm.textArea name="${customName}.supportLinks" i18nkey="synthesis.crpIndicators.links"  editable=editable /]
                  </div>
                  [#-- Deviation --]
                  <div class="fullPartBlock">
                    [@customForm.textArea name="${customName}.deviation" i18nkey="synthesis.crpIndicators.deviation"  editable=editable /]
                  </div>
                </div>
                [/#list]
              </div>
            [/#list]
            </div>
          </div>

          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/synthesis/buttons-crpIndicators.ftl" /]
           
          [/@s.form] 
        </article>
    </div>
  </div>
</section> 


[#include "/WEB-INF/crp/pages/footer.ftl"]