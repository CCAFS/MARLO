[#ftl]
[#assign title = "MARLO BI" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["powerbi-client"] /]
[#assign customJS = ["${baseUrlMedia}/js/bi/biDashboard.js?20201410_1109", "${baseUrlCdn}/global/bower_components/powerbi-client/dist/powerbi.min.js" ] /]
[#assign customCSS = [
  "${baseUrl}/crp/css/bi/biDashboard.css"
  ] 
/]
[#assign currentSection = "bi" /] 
 [#--
[#assign breadCrumb = [
    {"label":"${currentSection}",   "nameSpace":"",             "action":""}
  ]
/]
--]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

[#attempt]
  [#assign canAccessCCAFS = false ]
  [#assign canAccessWLE = (action.crpID == 4)!false ]
[#recover]
  [#assign canAccessCCAFS = false ]
  [#assign canAccessWLE = false ]
[/#attempt]

[#assign crp = "CCAFS" /]

    <section class="container containerBI">  
      [#if biReports?has_content][#-- 
         <div class="dashboard-tabs">
          [#-- Menu --][#-- 
          <ul class="nav nav-tabs" role="tablist">
          [#list (biReports)![] as report]
            <li role="presentation" class="[#if report?index ==0]active[/#if]">
              <a index="${report?index+1}" class="report-${report.id}" href="#report-${report.id}" aria-controls="..." role="tab" data-toggle="tab">${report.reportName}</a>
            </li>
          [/#list]
          </ul>
          <div class="tab-content ">
          [#list (biReports)![] as report]
            <div id="report-${report.id}" role="tabpanel" class="tab-pane fade [#if report?index ==0]active in[/#if]">
              <div id="dashboardContainer-${report.id}" style="height: 720px;width: 1100px;"></div>
              <input type="hidden" id="reportName-${report.id}" name="reportName" value=${report.reportName} />
              <input type="hidden" id="datasetId-${report.id}" name="datasetId" value=${report.datasetId} />
              <input type="hidden" id="embeUrl-${report.id}" name="embedUrl" value=${report.embedUrl} /> 
              <input type="hidden" id="reportID-${report.id}" name="reportId" value=${report.reportId} />
            </div>
          [/#list]
         </div>
  
       --]
        [#--  Reports header  --]
       <div class="headTitle-row-container">
        <span class="selectedReportBI col-md-1">
        <div class="col-md-12 btn button-bg">
            <p class="menu-item-title">[@s.text name="biDashboard.menu.title"/] </p>
            <span class="glyphicon reportsButtonsIcon glyphicon-chevron-up" style="color: #1da5ce"></span>
        </div>
        </span>
        <h3 class="headTitle text-left col-md-8">
        </h3>
        <span class="setFullScreen col-md-1 btn button-bg">
            <p class="menu-item-title">Fullscreen</p>
            <span class="glyphicon reportsButtonsIcon glyphicon-fullscreen" style="color: #1da5ce"></span>
        </span>
       </div>
        [#--  Reports header  --]

        [#--  Reports Tabs --] 
        <div id="repportsMenu" class="reportsButtons col-md-2">
          <div class="menuList">
          [#list (biReports)![] as report]
              <div id="BIreport-${report.id}" report-title="${report.reportTitle}" class="button-bg col-md-8 col-md-offset-1 reportSection [#if report?index == 0]current[/#if]">
                <a index="${report?index+1}" class="BIreport-${report.id}" href="">[@s.text name=report.reportName /]</a>
              </div>
          [/#list]
          </div>
        </div>

        
        [#--  Reports Content --]
        <div class="summariesContent col-md-12" style="min-height:550px;">
          <div class="">
            [#list (biReports)![] as report]
                <div id="BIreport-${report.id}-contentOptions" class="" style="display:[#if report?index !=0]none[/#if];">
                  <div id="dashboardContainer-${report.id}" class="dashboardContainer-${report.id}"></div>
                  <input type="hidden" id="reportName-${report.id}" name="reportName" value=${report.reportName} />
                  <input type="hidden" id="embeUrl-${report.id}" name="embedUrl" value=${report.embedUrl} /> 
                  <input type="hidden" id="reportID-${report.id}" name="reportId" value=${report.reportId} />
                </div>
            [/#list] 
          </div>
        </div>
      [/#if]
    </section>

[#include "/WEB-INF/global/pages/footer.ftl"]
