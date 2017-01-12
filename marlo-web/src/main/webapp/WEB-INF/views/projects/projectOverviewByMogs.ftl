[#ftl]
[#assign title = "Overview by MOGs" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectOverviewByMogs.js","${baseUrl}/js/global/fieldsValidation.js","${baseUrl}/js/global/autoSave.js"] /] [#-- "${baseUrl}/js/global/autoSave.js" --]
[#assign customCSS = ["${baseUrl}/css/projects/projectBudgetByPartners.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "overviewByMogs" /]
[#assign hideJustification = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"overviewByMogs", "nameSpace":"/overviewByMogs", "action":"overviewByMogs"}
]/]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/deliverableListTemplate.ftl" as deliverableList /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/images/global/icon-help.jpg" />
    <p class="col-md-10">[#if reportingActive] [@s.text name="" /] [#else] [@s.text name="" /] [/#if] </p>
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
        
        [#-- Section Title --]
          <h3 class="headTitle">[@s.text name="breadCrumb.menu.overviewByMogs" /]</h3>
        [#assign selectedYear = "2015" /]
    [#-- Year Tabs --]
            <ul class="nav nav-tabs budget-tabs" role="tablist">
              [#list allYears as year]
                <li class="[#if year == currentCycleYear]active[/#if]"><a href="#year-${year}" role="tab" data-toggle="tab">${year}  </a></li>
              [/#list]
            </ul>
            
            [#-- Years Content --]
            <div class="tab-content budget-content">
              [#list allYears as year]
                <div role="tabpanel" class="tab-pane [#if year == currentCycleYear]active[/#if]" id="year-${year}">
               
                [#if project.mogs?has_content]
                [#list project.mogs as output]
                
                    [#assign overviewMog = (action.getOverview(year,output.id))!{} /]
                    [#assign overviewMogIndex = (action.getIndex(year,output.id))!-1 /]
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
                      [@customForm.textArea name="project.overviews[${overviewMogIndex}].anualContribution"  showTitle=false  editable=(!reportingActive && editable) /]
                    </div>
                    [#-- Brief summary of your actual annual contribution --]
                    <div class="fullBlock fieldFocus" style="padding: 20px 15px !important; ">
                     <label>[@customForm.text name="reporting.project.overviewByMogs.summaryAnnualContribution" readText=!reportingActive param="${year}" /]:</label>  
                      [@customForm.textArea name="project.overviews[${overviewMogIndex}].briefSummary" showTitle=false  editable=(reportingActive && editable) className="limitWords-50" /]
                    </div>
                    [#-- Brief plan of the gender and social inclusion dimension of the expected annual output --]
                    <div class="fullBlock">
                      <label>[@customForm.text name="planning.project.overviewByMogs.expectedSocialAndGenderPlan" readText=reportingActive param="${year}" /]:</label>  
                      [@customForm.textArea name="project.overviews[${overviewMogIndex}].genderContribution" showTitle=false  editable=(!reportingActive && editable) /]
                    </div>
                    
                    [#-- Summary of the gender and social inclusion dimension --]
                    <div class="fullBlock fieldFocus" style="padding: 20px 15px !important; ">
                      <label>[@customForm.text name="reporting.project.overviewByMogs.summarySocialInclusionDimmension" readText=!reportingActive param="${year}" /]:</label>  
                      [@customForm.textArea name="project.overviews[${overviewMogIndex}].summaryGender" showTitle=false editable=(reportingActive && editable) className="limitWords-50"  /]
                    </div>
                  </div>
                   [/#if]
                  [/#list]
                [/#if]
                </div>
              [/#list]  
            </div>
            
            [#-- Section Buttons & hidden inputs--]
            [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
    
        [/@s.form] 
      </div>
    </div>  
</section>

  
[#include "/WEB-INF/global/pages/footer.ftl"]
