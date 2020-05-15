[#ftl]
[#assign title = "MARLO BI" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["powerbi-client"] /]
[#assign customJS = ["${baseUrlMedia}/js/bi/biDashboard.js", "${baseUrlCdn}/global/bower_components/powerbi-client/dist/powerbi.min.js" ] /]
[#assign customCSS = [
  "${baseUrl}/crp/css/bi/biDashboard.css"
  ] 
/]
[#assign currentSection = "bi" /] 

[#assign breadCrumb = [
  {"label":"${currentSection}",   "nameSpace":"",             "action":""}
]/]

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

    <section class="container">
    [#if biReports?has_content]
       <div class="dashboard-tabs">
        [#-- Menu --]
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
     [/#if]
    </section>

[#include "/WEB-INF/global/pages/footer.ftl"]
