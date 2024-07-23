[#ftl]
[#assign title = "MARLO BI" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign customJS = ["${baseUrlMedia}/js/bi/biDashboard.js?20230824" ] /]
[#assign customCSS = [
  "${baseUrl}/crp/css/bi/biDashboard.css?20230627a"
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
<span id="userCanLeaveComments" style="display: none;">${(action.canLeaveComments()?c)!}</span>

    <section class="container containerBI">  
      [#if biReports?has_content]
      <div class="headContainer">
        <div class='selectedReportBIContainer col-md-2'>
            <span class="selectedReportBI col-md button-bg" style="max-width:200px">
                [#--  <p class="menu-item-title">[@s.text name="biDashboard.menu.title"/] </p>  --]
                <p class="menu-item-title">Dashboards:</p>
              [#--  <span class="glyphicon reportsButtonsIcon glyphicon-chevron-up" style="color: #1da5ce"></span>  --]
            </span>
            [#--  Reports Tabs --] 
            <div id="repportsMenu" class="reportsButtons">
              <div class="menuList col-md-12" style="padding:0">
              [#list (biReports)?sort_by("reportOrder")![] as report]
                  <div id="BIreport-${report.id}" report-title="${report.reportTitle}"  has-filters="${report.hasFilters?c}" has-role-authorization="${report.hasRoleAuthorization?c}" class="button-bg reportSection [#if report?index == 0]current[/#if]">
                  <a index="${report?index+1}" class="BIreport-${report.id}" id="BIreport-${report.embedReport}" href="">[@s.text name=report.reportName /]</a>
                  </div>
              [/#list]
              </div>
            </div>
            [#--  Reports Tabs --] 
        
          </div>
        
      </div>

      [#--  Reports header  --]
         <div class="headTitle-row-container">
          <h3 class="headTitle text-left col-md-8"></h3>
         </div>
      [#--  Reports header  --]

      [#--  Reports Content --]
      <div class="summariesContent col-md-12" >
        <div class="">
          [#list (biReports)?sort_by("reportOrder")![] as report]
              <div id="BIreport-${report.id}-contentOptions" class="" style="display:[#if report?index !=0]none[/#if];">
                <div id="dashboardContainer-${report.id}" class="dashboardContainer-${report.id}"></div>
                <input type="hidden" id="reportName-${report.id}" name="reportName" value=${report.reportName} />
                <input type="hidden" id="embeUrl-${report.id}" name="embedReport" value=${report.embedReport} /> 
              </div>
          [/#list] 
        </div>
      </div>
      [/#if]
    </section>

    [#assign BiAppURL = biParameters?filter(param -> param.parameterName = "bi_widget_url" )]


    <script src="${BiAppURL[0].parameterValue}" charset="utf-8"></script>

[#include "/WEB-INF/global/pages/footer.ftl"]
