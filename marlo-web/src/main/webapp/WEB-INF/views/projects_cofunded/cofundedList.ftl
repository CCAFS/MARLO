[#ftl]
[#assign title = "MARLO Projects cofunded" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customJS = ["${baseUrl}/js/projects_cofunded/cofundedList.js" ] /]
[#assign customCSS = ["${baseUrl}/css/global/customDataTable.css"] /]
[#assign currentSection = "projects cofunded" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/bilaterals", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/forms.ftl" as customForm /]
[#import "/WEB-INF/global/macros/cofundedListTemplate.ftl" as CofundedList /]
    
<section class="container">
  <article class="fullBlock" id="mainInformation">
    [#-- Projects List (My Projects) --]
    <h3 class="headTitle text-center">[@s.text name="Bilateral Co-Funded Projects you are leading  "/]</h3>
    <div class="loadingBlock"></div>
    <div style="display:none">[@CofundedList.cofundedList projects=myProjects canValidate=true canEdit=true namespace="/bilaterals" defaultAction="${(crpSession)!}/cofunded" /]</div>
    <div class="clearfix"></div>
    
  </article>
</section>
[@customForm.confirmJustification action="deleteProject" namespace="/${currentSection}" title="Remove Project" /]


[#include "/WEB-INF/global/pages/footer.ftl"]
