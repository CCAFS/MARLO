[#ftl]
[#assign title = "TIP" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign customJS = ["${baseUrlMedia}/js/tip/tipEmbedded.js?20230824"] /]
[#assign customCSS = [
  "${baseUrl}/crp/css/tip/tipEmbedded.css?20230627a",  "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"
  ] 
/]
[#assign currentSection = "tip" /] 
 [#--
[#assign breadCrumb = [
    {"label":"${currentSection}",   "nameSpace":"",             "action":""}
  ]
/]
--]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

<span id="userCanLeaveComments" style="display: none;">${(action.canLeaveComments()?c)!}</span>


    <section class="container containerBI">  
          <div id="embeddedPage"></div>
    </section>

[#include "/WEB-INF/global/pages/footer.ftl"]

