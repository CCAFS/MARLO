[#ftl]
[#assign title = "TIP" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign customJS = ["${baseUrlMedia}/js/tip/tipEmbedded.js?20231121a"] /]
[#assign customCSS = [
  "${baseUrl}/crp/css/tip/tipEmbedded.css?20231121a",  "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"
  ] 
/]
[#assign currentSection = "tip" /] 

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

    <section class="container">      
          <h4 class="sectionTitle sectionTitle-tip">Deliverable Dissemination</h4>
          <div id="tip">        
          
             If you need to disseminate a deliverable...           
          </div>           
          </br>
          </br>         
          
          <div id="embeddedPage"></div>
    </section>
    
[#include "/WEB-INF/global/pages/footer.ftl"]

