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

    <section class="container">
    <div id="dashboardContainer" style="
    height: 720px;
    width: 1100px;"></div>
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
