[#ftl]
[#assign title = "Impact Pathway - Cluster Of Activities" /]
[#assign pageLibs = [] /]
[#assign customJS = [] /]
[#assign currentSection = "impactPathway" /]
[#assign currentStage = "clusterActivities" /]

[#assign breadCrumb = [
  {"label":"impactPathway", "nameSpace":"", "action":"outcomes"},
  {"label":"clusterActivities", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#--  marlo cluster of activities--]
<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/impactPathway/menu-impactPathway.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]  
        
        <h4 class="sectionTitle">Flagship {0} - Cluste of Activities</h4>
        <div class="borderBox">
        </div>
        
        <div class="buttons">
          [@s.submit type="button" name="save" cssClass=""][@s.text name="form.buttons.save" /][/@s.submit]
        </div>
        
        [/@s.form]
      </div>
    </div>
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]
