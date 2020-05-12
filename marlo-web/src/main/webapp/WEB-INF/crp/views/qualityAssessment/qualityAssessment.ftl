[#ftl]
[#assign title = "Quality Assessment" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [""] /]
[#assign customJS = ["" ] /]
[#assign customCSS = [
  ""
  ] 
/]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]

[#assign breadCrumb = [
  {"label":"${currentSection}",   "nameSpace":"",             "action":""},  
  {"label":"${currentStage}",     "nameSpace":"qa", "action":"${actionName}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utils /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

    <section class="container">   
      <div class="row">
            <h3 class="headTitle">[@s.text name="summaries.qualityAssessment.title" /]</h3>
            <div id="" class="">
              <div>
                <iframe width="100%" height="1000px" src="http://qatest.ciat.cgiar.org/crp?crp_id=${qATokenAuth.crpId}&token=${qATokenAuth.token}" frameborder="0"></iframe>
              </div>
            </div>  
      </div>
    </section>
     
[#include "/WEB-INF/global/pages/footer.ftl"]
