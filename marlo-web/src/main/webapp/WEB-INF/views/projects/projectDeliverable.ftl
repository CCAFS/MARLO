[#ftl]
[#assign title = "Deliverable information" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/projects/deliverables/deliverableInfo.js"] /]
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
          
          <div class="deliverableTabs">      
          
          [#--  Deliverable Menu --] 
            
            <ul class="nav nav-tabs" role="tablist"> 
                <li role="presentation" class="active"><a href="#deliverable-mainInformation" aria-controls="info" role="tab" data-toggle="tab">General Information</a></li>
                <li role="presentation" class=""><a href="#deliverable-qualityCheck" aria-controls="quality" role="tab" data-toggle="tab">Quality check</a></li>
                <li role="presentation" class=""><a href="#deliverable-disseminationMetadata" aria-controls="metadata" role="tab" data-toggle="tab">Dissemination & Metadata</a></li>
                <li role="presentation" class=""><a href="#deliverable-dataSharing" aria-controls="datasharing" role="tab" data-toggle="tab">Data Sharing</a></li>
            </ul>
            
            <div class="tab-content col-md-12">
              <div id="deliverable-mainInformation" role="tabpanel" class="tab-pane fade in active">
                <h3 class="headTitle">Deliverable information</h3>  
                [#-- Deliverable Information --] 
                [#include "/WEB-INF/views/projects/deliverableInfo.ftl" /]
              </div>
              
              <div id="deliverable-qualityCheck" role="tabpanel" class="tab-pane fade">
                <h3 class="headTitle">Deliverable quality check</h3>  
                [#-- Deliverable qualityCheck --]
              </div>
              
              <div id="deliverable-disseminationMetadata" role="tabpanel" class="tab-pane fade">
                <h3 class="headTitle">Dissemination & Metadata</h3>  
                [#-- Deliverable disseminationMetadata --] 
              </div>
              
              <div id="deliverable-dataSharing" role="tabpanel" class="tab-pane fade">
              <h3 class="headTitle">Deliverable Data Sharing</h3>  
                [#-- Deliverable dataSharing --] 
              </div>
            </div>
           </div>
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-deliverables.ftl" /]
             
         
          [/@s.form] 
      </div>
    </div>  
</section>

  
[#include "/WEB-INF/global/pages/footer.ftl"]