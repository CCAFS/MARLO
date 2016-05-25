[#ftl]
[#assign title = "Welcome to CCAFS P&R" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/home/login.js" ] /]
[#assign currentSection = "home" /]

[#import "/WEB-INF/global/macros/forms.ftl" as customForm /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section> 
  
  <div class="container">
    <div class="col-md-6">
      <h3>Welcome to Managing Agricultural Research for Learning and Outcomes (MARLO)</h3>
      <p>MARLO is an online platform assisting CRPs in their strategic results-based program planning and reporting of research projects. It covers project cycle from planning to project reporting, learning, and outcome-focused programmatic report generation with some additional synthesizing input at the flagship and cross-cutting level.  </p>
    </div>
    <div class="col-md-offset-1 col-md-5">
      [#-- Login Form --]
      [#include "/WEB-INF/global/pages/loginForm.ftl" /]
    </div>
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]