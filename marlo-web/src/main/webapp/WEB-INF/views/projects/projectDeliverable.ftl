[#ftl]
[#assign title = "Deliverable information" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = [""] /]
[#assign customJS = [""] /]
[#assign customCSS = ["${baseUrl}/css/projects/projectDeliverable.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "deliverable general information" /]

[#assign breadCrumb = [
  {"label":"deliverableList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectDeliverable"},
  {"label":"general information", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container">
  <div class="helpMessage"><img src="${baseUrl}/images/global/icon-help.png" /><p> [@s.text name="projectDescription.help" /] </p></div> 
</div>
    
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/views/projects/messages-projects.ftl" /]
        
       
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
           
          
          <h3 class="headTitle">Deliverable information</h3>  
            [#--  Deliverable Menu --] 
            <ul> 
              <li class=""><a href="#deliverable-mainInformation">General Information</a></li>
                <li class=""><a href="#deliverable-ranking">Quality check</a></li>
                <li class=""><a href="#deliverable-disseminationMetadata">Dissemination & Metadata</a></li>
                <li class=""><a href="#deliverable-dataSharing">Data Sharing</a></li>
            </ul>
          <div id="deliverableInformation" class="borderBox">
          
            <div id="deliverable-mainInformation">
              [#-- Deliverable Information --] 
              [#include "/WEB-INF/projects/deliverable/deliverableInfo.ftl" /]
            </div>
            
            <div id="deliverable-mainInformation">
              [#-- Deliverable Information --] 
              [#include "/WEB-INF/projects/deliverable/deliverableQualityCheck.ftl" /]
            </div>
            
            <div id="deliverable-mainInformation">
              [#-- Deliverable Information --] 
              [#include "/WEB-INF/projects/deliverable/deliverableInfo.ftl" /]
            </div>
            
            <div id="deliverable-mainInformation">
              [#-- Deliverable Information --] 
              [#include "/WEB-INF/projects/deliverable/deliverableInfo.ftl" /]
            </div>
            
          </div> 
           
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
             
         
          [/@s.form] 
      </div>
    </div>  
</section>

  
[#include "/WEB-INF/global/pages/footer.ftl"]