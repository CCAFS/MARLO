[#ftl]
[#assign title = "TIP" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign customJS = ["${baseUrlMedia}/js/tip/tipEmbedded.js?20240307"] /]
[#assign customCSS = [
  "${baseUrl}/crp/css/tip/tipEmbedded.css?20240307",  "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"
  ] 
/]
[#assign currentSection = "tip" /] 

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

    <section class="container">      
          <h4 class="sectionTitle sectionTitle-tip">Deliverable Dissemination</h4>
          <div id="tip">        
            Our main goal is to continuously improve the planning and reporting process for our users in the system. Considering the above and working in line with One CGIAR, we have integrated with the TIP tool of the Alliance, which will allow users to disseminate publications in CGSpace for the AICCRA project collections.</br>
            With this new tool, we will have a more efficient process when putting our publications online.            
          </div>           
          </br>
          </br>         
          
          <div id="embeddedPage"></div>
    </section>
    
[#include "/WEB-INF/global/pages/footer.ftl"]