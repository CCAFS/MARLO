[#ftl]

[#assign indicators = [
  { 'title': 'FP3 Indicator: # of low emissions plans developed that have significant mitigation potential for 2025, i.e. will contribute to at least 5% GHG reduction or reach at least 10,000 farmers, including at least 10% women.' }
] /]

[#assign mogs = [
  { 'title': 'FP3 - MOG #1: Methods and data for quantifying low-emissions agriculture options appropriate to smallholder farmers.' },
  { 'title': 'FP3 - MOG #2: Decision support for identifying and prioritizing low-emissions CSA options, including synergies and tradeoffs with development objectives' },
  { 'title': 'FP3 - MOG #3: Incentives and innovations for scale-up of low-emissions practices and avoided deforestation by agricultural commodities' }
] /]



[#assign outcomes = [
  { 'title': 'RP LAM - Outcome 2019', 'indicators': indicators, 'mogs': mogs, 'description': 'LAM-Outcome 2019: National governments formulate and implement NAMAS and LEDS based on improved data on smallholder agricultural GHG emissions and implement equitable policies to strengthen linkages among environment and agriculture in order to avoid deforestation from commodity agriculture, promote restoration to increase carbon sequestration and reduce GHG emissions from livestock and commodities. Research organizations generate improved data on smallholder agricultural GHG emissions. Local governments contribute to the development of NAMAS and LEDS action plans at local level.' }
] /]




[#assign title = "Project CCAFS Outcomes" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2", "jsUri"] /]
[#assign customJS = ["${baseUrl}/js/global/fieldsValidation.js"] /]
[#assign customCSS = [ ] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "ccafsOutcomes" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectCcafsOutcomes", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign startYear = (project.startDate?string.yyyy)?number /]
[#assign endYear = (project.endDate?string.yyyy)?number /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/images/global/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectCcafsOutcomes.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>
    
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          
          <h3 class="headTitle">[@s.text name="projectCcafsOutcomes.title" /]</h3>  
          
          [#list midOutcomesSelected as outcome]
            <div class="borderBox">
              <h4 class="sectionSubTitle">${outcome.ipProgram.acronym} - Outcome</h4>
              <p>${outcome.description}</p>
              
              
                   
              
              <p><strong>Indicators</strong></p>
             
              [#list outcome.indicators as indicator]
                <div class="simpleBox">
                  <p class="grayBox">${indicator.title}</p>
                  
                  <ul class="nav nav-tabs projectOutcomeYear-tabs" role="tablist">
                    [#list startYear .. endYear as year]
                      <li class="[#if year == currentCycleYear]active[/#if]"><a href="#year-${year}" aria-controls="settings" role="tab" data-toggle="tab">${year} [@customForm.req required=isYearRequired(year) /] </a></li>
                    [/#list]
                  </ul> 
                  
                  <div class="tab-content projectOutcomeYear-content">
                    [#list startYear .. endYear as year]
                      <div role="tabpanel" class="tab-pane [#if year == currentCycleYear]active[/#if]" id="year-${year}">

                          <div class="communicationsBlock form-group">
                            <div class="form-group">
                              [@customForm.textArea name="projectOutcome.communications[${comunicationIndex}].communication" i18nkey="projectOutcome.communicationEngagement" required=isYearRequired(year) className="limitWords-100 fieldFocus" editable=editable /]
                            </div>
                            <div class="form-group">
                              [@customForm.textArea name="projectOutcome.communications[${comunicationIndex}].analysisCommunication" i18nkey="projectOutcome.analysisCommunication" className="limitWords-100 ${reportingActive?string('fieldFocus','')}" editable=editable /]
                            </div>
                          </div>
                          
                      </div>
                    [/#list]
                  </div>
                  
                  
                </div>
              [/#list]
             
              <p><strong>Mogs</strong></p>
           
            </div> 
          [/#list]
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
          
          
        [/@s.form] 
      </div>
    </div>  
</section>


[#include "/WEB-INF/global/pages/footer.ftl"]


[#-- Get if the year is required--]
[#function isYearRequired year]
  [#if project.endDate??]
    [#assign endDate = (project.endDate?string.yyyy)?number]
    [#if reportingActive]
      [#return  (year == currentCycleYear)  && (endDate gte year) ]
    [#else]
      [#return  (year == currentCycleYear) && (endDate gte year) ]
    [/#if]
  [#else]
    [#return false]
  [/#if]
[/#function]