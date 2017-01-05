[#ftl]
[#assign title = "Project Deliverables" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customJS = ["${baseUrl}/js/projects/deliverables/deliverableList.js","${baseUrl}/js/global/fieldsValidation.js"] /] [#-- "${baseUrl}/js/global/autoSave.js" --]
[#assign customCSS = ["${baseUrl}/css/projects/projectBudgetByPartners.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "overviewByMogs" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"overviewByMogs", "nameSpace":"/overviewByMogs", "action":"overviewByMogs"}
]/]

[#assign years = ["2015","2016","2017"]/]

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
              [#list years as year]
                <li class="[#if year == selectedYear]active[/#if]"><a href="#year-${year}" role="tab" data-toggle="tab">${year}  </a></li>
              [/#list]
            </ul>
            
            [#-- Years Content --]
            <div class="tab-content budget-content">
              [#list years as year]
                <div role="tabpanel" class="tab-pane [#if year == selectedYear]active[/#if]" id="year-${year}">
                <div class="mog simpleBox clearfix">
                    [#-- Hidden values 
                    <input type="hidden" name="project.outputsOverview[${index}].id" value="${outputOverview.id!"-1"}" />
                    <input type="hidden" name="project.outputsOverview[${index}].year" value="${year}" />
                    <input type="hidden" name="project.outputsOverview[${index}].output.id" value="${output.id}" />
                    --]
                    [#-- MOG Title --]
                    <div class="fullPartBlock"><p class="checked">test - MOG #2: test lorem ipsum etc </p></div>
                    [#-- Brief bullet points of your expected annual year contribution towards the selected MOG --]
                    <div class="fullBlock">
                      [@customForm.textArea name="" value="" i18nkey="planning.project.overviewByMogs.expectedBulletPoints" showTitle=true editable=editable  /]
                    </div>
                    [#-- Brief summary of your actual annual contribution --]
                    <div class="fullBlock">
                      [@customForm.textArea name="" value="" i18nkey="reporting.project.overviewByMogs.summaryAnnualContribution" showTitle=true  editable=editable /]
                    </div>
                    [#-- Brief plan of the gender and social inclusion dimension of the expected annual output --]
                    <div class="fullBlock">
                      [@customForm.textArea name="" value="" i18nkey="planning.project.overviewByMogs.expectedSocialAndGenderPlan" showTitle=true  editable=editable /]
                    </div>
                    
                    [#-- Summary of the gender and social inclusion dimension --]
                    <div class="fullBlock">
                      [@customForm.textArea name="" value="" i18nkey="reporting.project.overviewByMogs.summarySocialInclusionDimmension"  showTitle=true editable=editable  /]
                    </div>
                  </div>
                [#if project.outputs?has_content]
                [#list project.outputs as output]
                  <div class="mog simpleBox clearfix">
                    [#-- Hidden values 
                    <input type="hidden" name="project.outputsOverview[${index}].id" value="${outputOverview.id!"-1"}" />
                    <input type="hidden" name="project.outputsOverview[${index}].year" value="${year}" />
                    <input type="hidden" name="project.outputsOverview[${index}].output.id" value="${output.id}" />
                    --]
                    [#-- MOG Title --]
                    <div class="fullPartBlock"><p class="checked">test - MOG #2: test lorem ipsum etc </p></div>
                    [#-- Brief bullet points of your expected annual year contribution towards the selected MOG --]
                    <div class="fullBlock">
                      [@customForm.textArea name="" value="" i18nkey="planning.project.overviewByMogs.expectedBulletPoints" showTitle=true editable=editable  /]
                    </div>
                    [#-- Brief summary of your actual annual contribution --]
                    <div class="fullBlock">
                      [@customForm.textArea name="" value="" i18nkey="reporting.project.overviewByMogs.summaryAnnualContribution" showTitle=true  editable=editable /]
                    </div>
                    [#-- Brief plan of the gender and social inclusion dimension of the expected annual output --]
                    <div class="fullBlock">
                      [@customForm.textArea name="" value="" i18nkey="planning.project.overviewByMogs.expectedSocialAndGenderPlan" showTitle=true  editable=editable /]
                    </div>
                    
                    [#-- Summary of the gender and social inclusion dimension --]
                    <div class="fullBlock">
                      [@customForm.textArea name="" value="" i18nkey="reporting.project.overviewByMogs.summarySocialInclusionDimmension"  showTitle=true editable=editable  /]
                    </div>
                  </div>
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
