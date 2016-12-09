[#ftl]
[#assign canEdit = true /]
[#assign editable = true /]
[#assign liaisonInstitutionID = 2 /]
[#assign liaisonInstitutions = [
  { 'id': 1, 'acronym': 'F1', 'name': 'Priorities and Policies for CSA'},
  { 'id': 2, 'acronym': 'F2', 'name': 'Climate-Smart Technologies and Practices'},
  { 'id': 3, 'acronym': 'F3', 'name': 'Low emissions development'},
  { 'id': 4, 'acronym': 'F4', 'name': 'Climate services and safety nets'},
  { 'id': 5, 'acronym': 'AfricaRice', 'name': ''},
  { 'id': 6, 'acronym': 'BI', 'name': ''},
  { 'id': 7, 'acronym': 'CIAT', 'name': ''},
  { 'id': 8, 'acronym': 'CIFOR', 'name': ''},
  { 'id': 9, 'acronym': 'CIMMYT', 'name': ''},
  { 'id': 10, 'acronym': 'CIP', 'name': ''},
  { 'id': 11, 'acronym': 'ICARDA', 'name': ''},
  { 'id': 12, 'acronym': 'ICRAF', 'name': ''},
  { 'id': 13, 'acronym': 'ICRISAT', 'name': ''},
  { 'id': 14, 'acronym': 'IFPRI', 'name': ''},
  { 'id': 15, 'acronym': 'IITA', 'name': ''},
  { 'id': 16, 'acronym': 'ILRI', 'name': ''},
  { 'id': 17, 'acronym': 'IRRI', 'name': ''},
  { 'id': 18, 'acronym': 'IWMI', 'name': ''},
  { 'id': 19, 'acronym': 'WorldFish', 'name': ''}
] /]
[#assign indicatorsType = [
  { 'id': 1, 'name': 'Knowledge, tools, data'},
  { 'id': 2, 'name': 'Capacity enhancement and innovation platforms'},
  { 'id': 3, 'name': 'Technologies/practices in various stages of development'},
  { 'id': 4, 'name': 'Pollicies in varius stages of development'},
  { 'id': 5, 'name': 'Outcomes on the ground'}
] /]
[#assign indicatorsByType = [
  { 'id': 1, 
    'indicator': {'id': 1, 'name': 'Number of top “products” produced by CRP', 'description': 'These are frameworks and concepts that are significant and complete enough to have been highlighted on web pages, publicized through blog stories, press releases and/or policy briefs. They are significant in that they should be likely to change the way stakeholders along the impact pathway allocate resources and/or implement activities. They should be products that change the way these stakeholders think and act. Tools, decision-support tools, guidelines and/or training manuals are not included in this indicator'}
  },
  { 'id': 2, 
    'indicator': {'id': 2, 'name': '% of top products produced that have explicit target of women farmers/NRM managers', 'description': 'The web pages, blog stories, press releases and policy briefs supporting indicator #1 must have an explicit focus on women farmers/NRM managers to be counted'}
  },
  { 'id': 3, 
    'indicator': {'id': 3, 'name': '% of top products produced that have been assessed for likely gender-disaggregated impact', 'description': 'Reports/papers describing the products should include a focus on gender-disaggregated impacts if they are to be counted'}
  },
  { 'id': 4, 
    'indicator': {'id': 4, 'name': 'Number of ”tools” produced by CRP', 'description': ''}
  }
] /]




[#assign title = "CRP Indicators" /]
[#assign currentSectionString = "synthesis-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = ["${baseUrl}/js/synthesis/crpIndicators.js"] /]
[#assign customCSS = ["${baseUrl}/css/synthesis/synthesisGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "crpIndicators" /]

[#assign breadCrumb = [
  {"label":"synthesis", "nameSpace":"/synthesis", "action":"crpIndicators"},
  {"label":"crpIndicators", "nameSpace":"/synthesis", "action":""}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]

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
          <ul id="liaisonInstitutions" class="horizontalSubMenu text-center">
            [#list liaisonInstitutions as institution]
              [#assign isActive = (institution.id == liaisonInstitutionID)/]
              [#assign isCompleted = false /]
              [#assign hasPermission = (action.hasSynthesisPermission('update', institution.id))!false /]
              <li class="${isActive?string('active','')} ${hasPermission?string('canEdit','')}">
                <a href="[@s.url][@s.param name ="liaisonInstitutionID"]${institution.id}[/@s.param][@s.param name ="edit"]true[/@s.param][/@s.url]">${institution.acronym}</a>
                [#if isCompleted] <p class="synthesisCompleted"> <img src="${baseUrl}/images/global/icon-check-tiny${isActive?string('-white','')}.png"/> </p> [/#if]
              </li>
            [/#list]
          </ul>
          
           
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="synthesis.crpIndicators.title" /]</h3>
          
          <div id="crpIndicatorsTabs">
            <ul class="nav nav-tabs" role="tablist">
              [#list indicatorsType as indicatorType]  
                <li title="${indicatorType.name}" class="[#if indicatorType_index == 0]active[/#if]">
                  <a href="#indicatorType-${indicatorType.id}" role="tab" data-toggle="tab">[@utilities.wordCutter string=indicatorType.name maxPos=36 /]</a>
                </li>
              [/#list]
            </ul>
            <div class="tab-content">
            [#list indicatorsType as indicatorType]
              <div id="indicatorType-${indicatorType.id}" class="tab-pane indicatorsByType [#if indicatorType_index == 0]active[/#if]" role="tabpanel">
                [#assign customIndex = (action.getIndicatorIndex(indicatorReport.indicator.id,indicatorReport.indicator.type.id))!-1 ]
                [#assign customName= "indicatorReports[${customIndex}]"]
                
                [#-- List of indicators by type --> action.getCrpIndicatorsByType(indicatorType.id) as indicatorReport --]
                [#list indicatorsByType as indicatorReport]
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
          </div>
          
          [#-- Hidden inputs --]
          <input type="hidden" name="liaisonInstitutionID" value="${liaisonInstitutionID}"  /> 
          
           
          [/@s.form] 
        </article>
    </div>
  </div>
</section> 


[#include "/WEB-INF/global/pages/footer.ftl"]