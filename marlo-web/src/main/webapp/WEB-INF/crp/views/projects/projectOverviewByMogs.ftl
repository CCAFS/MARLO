[#ftl]
[#assign title = "Overview by MOGs" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectOverviewByMogs.js",
  "${baseUrl}/global/js/fieldsValidation.js",
  "${baseUrl}/global/js/autoSave.js"
  ] 
/]
[#assign customCSS = ["${baseUrlMedia}/css/projects/projectBudgetByPartners.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "overviewByMogs" /]
[#assign hideJustification = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"overviewByMogs", "nameSpace":"/overviewByMogs", "action":"overviewByMogs"}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
    
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
        
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
        
        [#-- Section Title --]
          <h3 class="headTitle">[@s.text name="breadCrumb.menu.overviewByMogs" /]</h3>
        [#assign selectedYear = "2015" /]
        [#-- Year Tabs --]
            <ul class="nav nav-tabs budget-tabs" role="tablist">
              [#list allYears as year]
              [#if year <=currentCycleYear]
                <li class="[#if year == currentCycleYear]active[/#if]"><a href="#year-${year}" role="tab" data-toggle="tab">${year} [#if year == currentCycleYear] <span class="red">*</span> [/#if] </a></li>
              [/#if]
              [/#list]
            </ul>
            
            [#-- Years Content --]
            <div class="tab-content budget-content">
              [#list allYears as year]
                [#if year <=currentCycleYear]
                
                <div role="tabpanel" class="tab-pane [#if year == currentCycleYear]active[/#if]" id="year-${year}">
               
                [#if project.mogs?has_content]
                [#list project.mogs as output]
                
                    [#assign overviewMog = (action.getOverview(year,output.id))!{} /]
                    [#assign overviewMogIndex = (action.getIndex(year,output.id))!-1 /]
                    [#if year<currentCycleYear][#assign editableByYear = false /][#else][#assign editableByYear = true /] [/#if]
                     [#if overviewMog?has_content]
                  <div class="mog simpleBox clearfix">
                  
                    <input type="hidden" name="project.overviews[${overviewMogIndex}].id" value="${overviewMog.id!"-1"}" />
                    <input type="hidden" name="project.overviews[${overviewMogIndex}].year" value="${year}" />
                    <input type="hidden" name="project.overviews[${overviewMogIndex}].ipElement.id" value="${(overviewMog.ipElement.id)!-1}" />
                    
                    [#-- MOG Title --]
                    <div class="fullPartBlock"><p class="checked">${output.composedId} ${output.description}</p></div>
                    [#-- Brief bullet points of your expected annual year contribution towards the selected MOG --]
                    <div class="fullBlock">
                    <label>[@customForm.text name="planning.project.overviewByMogs.expectedBulletPoints" readText=reportingActive param="${year}" /]:</label>  
                      [@customForm.textArea name="project.overviews[${overviewMogIndex}].anualContribution"  showTitle=false  editable=(!reportingActive && editable && editableByYear) /]
                    </div>
                    
                    [#-- Brief summary of your actual annual contribution --]
                    <div class="fullBlock" >
                     <label>[@customForm.text name="reporting.project.overviewByMogs.summaryAnnualContribution" readText=!reportingActive param="${year}" /]:<span class="red">*</span></label>  
                      [@customForm.textArea name="project.overviews[${overviewMogIndex}].briefSummary" showTitle=false  editable=(reportingActive && editable && editableByYear) className="limitWords-50 ${(year == currentCycleYear)?string('fieldFocus','')}" /]
                    </div>
                    
                    [#-- Brief plan of the gender and social inclusion dimension of the expected annual output --]
                    <div class="fullBlock">
                      <label>[@customForm.text name="planning.project.overviewByMogs.expectedSocialAndGenderPlan" readText=reportingActive param="${year}" /]:</label>  
                      [@customForm.textArea name="project.overviews[${overviewMogIndex}].genderContribution" showTitle=false  editable=(!reportingActive && editable && editableByYear) /]
                    </div>
                    
                    [#-- Summary of the gender and social inclusion dimension --]
                    <div class="fullBlock" >
                      <label>[@customForm.text name="reporting.project.overviewByMogs.summarySocialInclusionDimmension" readText=!reportingActive param="${year}" /]:<span class="red">*</span></label>  
                      [@customForm.textArea name="project.overviews[${overviewMogIndex}].summaryGender" showTitle=false editable=(reportingActive && editable && editableByYear) className="limitWords-50 ${(year == currentCycleYear)?string('fieldFocus','')}"  /]
                    </div>
                  </div>
                   [/#if]
                  [/#list]
                [/#if]
                </div>
                [/#if]
              [/#list]  
            </div>
            
            [#-- Section Buttons & hidden inputs--]
            [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
    
        [/@s.form] 
      </div>
    </div>  
</section>

  
[#include "/WEB-INF/global/pages/footer.ftl"]
