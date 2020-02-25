[#ftl]
[#assign title = "MARLO BI" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [""] /]
[#assign customJS = ["" ] /]
[#assign customCSS = [
  "${baseUrl}/crp/css/bi/biDashboard.css"
  ] 
/]
[#assign currentSection = "bi" /] 


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

    <section class="container">
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
               <iframe width="1100" height="720" src="https://app.powerbi.com/view?r=eyJrIjoiZGU4MTk2ZTQtYjRiNC00NzkxLWFlZWYtNGYwZmI1NGQ1ODA0IiwidCI6IjZhZmEwZTAwLWZhMTQtNDBiNy04YTJlLTIyYTdmOGMzNTdkNSIsImMiOjh9" frameborder="0" allowFullScreen="true"></iframe>
              </div>
              <div id="project-deliverables" role="tabpanel" class="tab-pane fade ">
                <iframe width="1100" height="1600" src="https://app.powerbi.com/view?r=eyJrIjoiNGNiNWRkODEtMWMxYy00MjdjLWIwZTctOGVmYWVhZTliMGIxIiwidCI6IjZhZmEwZTAwLWZhMTQtNDBiNy04YTJlLTIyYTdmOGMzNTdkNSIsImMiOjh9" frameborder="0" allowFullScreen="true"></iframe>
              </div>
            </div>
           </div>
     </section>

[#include "/WEB-INF/global/pages/footer.ftl"]
