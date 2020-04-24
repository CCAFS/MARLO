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

[#assign biReports = [
   {"id":"1",   "reportName": "Submition",   "datasetId": "9bd72c88-3162-4a6b-a4cc-8422e61e9eeb",   "embeUrl": "https://app.powerbi.com/reportEmbed?reportId=50e6f7be-fef1-43cd-9983-4008f47f4a4d&groupId=37376d13-3df2-4447-aaa5-49c047533b4f&config=eyJjbHVzdGVyVXJsIjoiaHR0cHM6Ly93YWJpLW5vcnRoLWV1cm9wZS1yZWRpcmVjdC5hbmFseXNpcy53aW5kb3dzLm5ldC8ifQ%3D%3D",   "reportID": "50e6f7be-fef1-43cd-9983-4008f47f4a4d"}
   {"id":"2", "reportName": "Deliverables",   "datasetId": "9bd72c88-3162-4a6b-a4cc-8422e61e9eeb",   "embeUrl": "https://app.powerbi.com/reportEmbed?reportId=50e6f7be-fef1-43cd-9983-4008f47f4a4d&groupId=37376d13-3df2-4447-aaa5-49c047533b4f&config=eyJjbHVzdGVyVXJsIjoiaHR0cHM6Ly93YWJpLW5vcnRoLWV1cm9wZS1yZWRpcmVjdC5hbmFseXNpcy53aW5kb3dzLm5ldC8ifQ%3D%3D",   "reportID": "50e6f7be-fef1-43cd-9983-4008f47f4a4d"}
] /]

    <section class="container">
     [#if biReports?has_content]

       <div class="dashboard-tabs">
        [#-- Menu --]
        <ul class="nav nav-tabs" role="tablist">
        [#list (biReports)![] as report]
          <li role="presentation" class="[#if report?index ==0]active[/#if]">
            <a index="${report?index+1}" href="#${report.id}" aria-controls="..." role="tab" data-toggle="tab">${report.reportName}</a>
          </li>
        [/#list]
        </ul>
        <div class="tab-content ">
        [#list (biReports)![] as report]
          <div id="${report.id}" role="tabpanel" class="tab-pane fade [#if report?index ==0]in active[/#if]">
            <div id="dashboardContainer-${report.id}" style="height: 720px;width: 1100px;"></div>
            <input type="hidden" id="reportName-${report.id}" name="reportName" value=${report.reportName} />
            <input type="hidden" id="datasetId-${report.id}" name="datasetId" value=${report.datasetId} />
            <input type="hidden" id="embeUrl-${report.id}" name="embeUrl" value=${report.embeUrl} />
            <input type="hidden" id="crp-${report.id}" name="crp" value=${crp} />
            <input type="hidden" id="reportID-${report.id}" name="reportID" value=${report.reportID} />
          </div>
        [/#list]
       </div>
     [/#if]

     
         [#if canAccessCCAFS]
           <div class="dashboard-tabs">
            [#-- Menu --]
            <ul class="nav nav-tabs" role="tablist"> 
                <li role="presentation" class="active">
                  <a index="1" href="#project-submission-status" aria-controls="info" role="tab" data-toggle="tab">Project Submission Status</a>
                </li>
                <li role="presentation" class="">
                  <a index="2" href="#project-deliverables" aria-controls="metadata" role="tab" data-toggle="tab">Deliverables</a>
                </li>
            </ul>
            <div class="tab-content ">
              <div id="project-submission-status" role="tabpanel" class="tab-pane fade in active">
               <iframe width="1100" height="720" src="https://app.powerbi.com/view?r=eyJrIjoiYThkZjAxODktODA0MC00NDk3LTg0YzQtODhmMTIzMDk4MzBkIiwidCI6IjZhZmEwZTAwLWZhMTQtNDBiNy04YTJlLTIyYTdmOGMzNTdkNSIsImMiOjh9" frameborder="0" allowFullScreen="true"></iframe>
              </div>
              <div id="project-deliverables" role="tabpanel" class="tab-pane fade ">
                <iframe width="1100" height="1605" src="https://app.powerbi.com/view?r=eyJrIjoiZjFjNjY5MjUtNzIwMS00MjA5LTk1NTYtOWJmYWM4ZTJmNWJkIiwidCI6IjZhZmEwZTAwLWZhMTQtNDBiNy04YTJlLTIyYTdmOGMzNTdkNSIsImMiOjh9" frameborder="0" allowFullScreen="true"></iframe>
              </div>
            </div>
           </div>
           [#elseif canAccessWLE]
            <div>
               <iframe width="1100" height="720" src="https://app.powerbi.com/view?r=eyJrIjoiNzYzNjE1NTctZmJiOS00NmEyLTliMDQtZTAyN2Y2MDgyMDRhIiwidCI6IjZhZmEwZTAwLWZhMTQtNDBiNy04YTJlLTIyYTdmOGMzNTdkNSIsImMiOjh9" frameborder="0" allowFullScreen="true"></iframe>
              </div>
           [/#if]
     </section>

[#include "/WEB-INF/global/pages/footer.ftl"]
