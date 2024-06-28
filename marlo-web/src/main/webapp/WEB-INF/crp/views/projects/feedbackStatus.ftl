[#ftl]
[#assign title = "Feedback Status" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/feedbackStatus.js?20230201"
  ]
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/feedbackStatus.css?20221010"
  ]
/]

[#assign currentStage = "feedback" /]

  [#assign currentSection = "clusters" /]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"${currentSection}", "action":"${(crpSession)!}/projectsList"},
    {"text":"C${project.id}", "nameSpace":"${currentSection}", "action":"${crpSession}/feedback", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
    {"label":"feedbackStatus", "nameSpace":"${currentSection}", "action":""}
  ] /]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]


[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

[#if (!availabePhase)!false]
  [#include "/WEB-INF/crp/views/projects/availability-projects.ftl" /]
[#else]
<section class="container">
<div id="actualPhase">${actualPhase.id}</div>
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

          <div id="projectDescription" class="borderBox">
              
             <section class="local-container">  
              [#if biReports?has_content]
              <div class="headContainer" style="display: none;">
                <div class='selectedReportBIContainer col-md-2'>
                    <span class="selectedReportBI col-md button-bg" style="max-width:200px">
                        [#--  <p class="menu-item-title">[@s.text name="biDashboard.menu.title"/] </p>  --]
                        [#--  <p class="menu-item-title">Dashboards:</p>  --]
                      [#--  <span class="glyphicon reportsButtonsIcon glyphicon-chevron-up" style="color: #1da5ce"></span>  --]
                    </span>

                    [#--  Reports Tabs --] 
                    <div id="repportsMenu" class="reportsButtons">
                      <div class="menuList col-md-12" style="padding:0">
                      [#list (biReports)?sort_by("reportOrder")![] as report]
                          <div id="BIreport-${report.id}" report-title="${report.reportTitle}"  has-filters="${report.hasFilters?c}" class="button-bg reportSection [#if report?index == 0]current[/#if]">
                          <a index="${report?index+1}" class="BIreport-${report.id}" id="BIreport-${report.embedReport}" href="">[@s.text name=report.reportName /]</a>
                          </div>
                      [/#list]
                      </div>
                    </div>
                    [#--  Reports Tabs --] 
                
                  </div>
                
                  [#--  Reports header  --]
                 <div class="headTitle-row-container">
          
                  <h3 class="headTitle text-left col-md-8">
                  </h3>
                  <span class="setFullScreen col-md-1 btn button-bg">
                      <p class="menu-item-title">Fullscreen</p>
                      <span class="glyphicon reportsButtonsIconFS glyphicon-fullscreen" style="color: #1da5ce"></span>
                  </span>
                 </div>
                  [#--  Reports header  --]
              </div>
        
                [#--  Reports Content --]
                <div class="summariesContent col-md-12">
                  <div class="">
                    [#list (biReports)?sort_by("reportOrder")![] as report]
                        <div id="BIreport-${report.id}-contentOptions" class="" style="display:[#if report?index !=0]none[/#if];">
                          <div id="dashboardContainer-${report.id}" class="dashboardContainer-${report.id}"></div>
                          <input type="hidden" id="reportName-${report.id}" name="reportName" value=${report.reportName} />
                          <input type="hidden" id="embeUrl-${report.id}" name="embedReport" value=${report.embedReport} /> 
                          <input type="hidden" id="projectID-${project.id}" name="projectID" value=${project.id} />
                        </div>
                    [/#list] 
                  </div>
                </div>
              [/#if]
             </section>
          </div>
        
          [/@s.form]
      </div>
    </div>
</section>
[/#if]

    [#assign BiAppURL = biParameters?filter(param -> param.parameterName = "bi_widget_url" )]


    <script src="${BiAppURL[0].parameterValue}" charset="utf-8"></script>
    

[#include "/WEB-INF/global/pages/footer.ftl"]