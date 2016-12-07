[#ftl]
[#assign canEdit = true /]
[#assign editable = true /]
[#assign liaisonInstitutionID = 2 /]
[#assign liaisonInstitutions = [] /]
[#assign indicatorsType = [] /]


[#assign title = "CRP Indicators" /]
[#assign currentSectionString = "synthesis-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = ["${baseUrl}/js/synthesis/crpIndicators.js"] /]
[#assign customCSS = ["${baseUrl}/css/synthesis/crpIndicators.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "crpIndicators" /]

[#assign breadCrumb = [
  {"label":"synthesis", "nameSpace":"/synthesis", "action":"crpIndicators"},
  {"label":"crpIndicators", "nameSpace":"/synthesis", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]


<section class="container">
    <div class="row"> 
      <div class="col-md-12">
        [#-- Help Message --]
        <div class="container helpText viewMore-block">
          <div class="helpMessage infoText">
            <img class="col-md-2" src="${baseUrl}/images/global/icon-help.jpg" />
            <p class="col-md-10"> [@s.text name="synthesis.crpIndicators.help" /]</p>
          </div> 
          <div style="display:none" class="viewMore closed"></div>
        </div>
        
        <article class="form-group clearfix" id="crpIndicators">
          [@s.form action="crpIndicators" method="POST" enctype="multipart/form-data" cssClass="pure-form"]
          [#-- Program (Regions and Flagships) --]
          <ul id="liaisonInstitutions" class="horizontalSubMenu">
            [#list liaisonInstitutions as institution]
              [#assign isActive = (institution.id == liaisonInstitutionID)/]
              [#assign isCompleted = false /]
              [#assign hasPermission = action.hasSynthesisPermission('update', institution.id)/]
              <li class="${isActive?string('active','')} ${hasPermission?string('canEdit','')}">
                <a href="[@s.url][@s.param name ="liaisonInstitutionID"]${institution.id}[/@s.param][@s.param name ="edit"]true[/@s.param][/@s.url]">${institution.acronym}</a>
                [#if isCompleted] <p class="synthesisCompleted"> <img src="${baseUrl}/images/global/icon-check-tiny${isActive?string('-white','')}.png"/> </p> [/#if]
              </li>
            [/#list]
          </ul>
          
           
          [#-- Title --]
          <h1 class="contentTitle">[@s.text name="synthesis.crpIndicators.title" /]</h1>
          
          <div id="crpIndicatorsTabs">
            <ul>
              [#list indicatorsType as indicatorType]  
                <li title="${indicatorType.name}"><a href="#indicatorType-${indicatorType.id}">[@utilities.wordCutter string=indicatorType.name maxPos=36 /]</a></li>
              [/#list]
            </ul>
      
            [#list indicatorsType as indicatorType]
              <div id="indicatorType-${indicatorType.id}" class="indicatorsByType">
                [#assign customName= "indicatorReports[${action.getIndicatorIndex(indicatorReport.indicator.id,indicatorReport.indicator.type.id)}]"]

                [#-- List of indicators by type --]
                [#list action.getCrpIndicatorsByType(indicatorType.id) as indicatorReport]
                <div class="simpleBox">
                  <h6 class="title" style="font-size: 1.2em;margin-bottom: 5px;">${indicatorReport.indicator.id}.  ${indicatorReport.indicator.name}
                    [#if indicatorReport.indicator.description?has_content]
                      <a id="showIndicatorDesc-${indicatorReport.indicator.id}" class="showIndicatorDesc" href="#"><img src="${baseUrl}/images/global/icon-info.png" title="Show indicator description" alt="" /></a>
                    [/#if]
                  </h6>
                  [#if indicatorReport.indicator.description?has_content]
                    <div class="fullPartBlock"><p id="indicatorDesc-${indicatorReport.indicator.id}" style="display:none">${indicatorReport.indicator.description}</p></div>
                  [/#if]
                  [#-- Targets --]
                  <div class="fullPartBlock">
                    <div class="thirdPartBlock">[@customForm.input name="${customName}.target" type="text" i18nkey="synthesis.crpIndicators.target" className="isNumeric" help="form.message.numericValue" paramText="${reportingYear}" editable=false /]</div>
                    <div class="thirdPartBlock">[@customForm.input name="${customName}.actual" type="text" i18nkey="synthesis.crpIndicators.actual" className="isNumeric" help="form.message.numericValue" paramText="${reportingYear}" required=canEdit editable=editable /]</div>
                    <div class="thirdPartBlock">[@customForm.input name="${customName}.nextYearTarget" type="text" i18nkey="synthesis.crpIndicators.nextYearTarget" className="isNumeric" help="form.message.numericValue" paramText="${reportingYear+1}" required=canEdit editable=editable /]</div>
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
          
          [#-- Hidden inputs --]
          <input type="hidden" name="liaisonInstitutionID" value="${liaisonInstitutionID}"  /> 
          
           
          [/@s.form] 
        </article>
    </div>
  </div>
</section> 


[#include "/WEB-INF/global/pages/footer.ftl"]