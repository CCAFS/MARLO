[#ftl]
[#assign title = "Impact Pathway - Cluster Of Activities" /]
[#assign pageLibs = [] /]
[#assign customJS = [] /]
[#assign customCSS = [ "${baseUrl}/css/impactPathway/clusterActivities.css" ] /]
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
          <div class="form-group CoA">
            <div class="form-group">
              <div class="row">
                <span class="subtitle col-md-11">Cluster of Activity - Title</span>
                <span class="delete col-md-1 glyphicon glyphicon-remove red" ></span>
              </div>
              <div class="col-md-12">
                <input class="col-md-12" type="text" name="CoATilte" value=""/>
              </div>
            </div>
            <div class="form-group">
            </div>
          </div>
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
