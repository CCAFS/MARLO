[#ftl]
[#assign title = "Project Deliverables" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customJS = ["${baseUrl}/js/projects/deliverables/deliverableList.js","${baseUrl}/js/global/fieldsValidation.js"] /] [#-- "${baseUrl}/js/global/autoSave.js" --]
[#assign customCSS = ["${baseUrl}/css/global/customDataTable.css","${baseUrl}/css/projects/projectDeliverable.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "deliverableList" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"deliverableList", "nameSpace":"/projects", "action":""}
]/]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/deliverableListTemplate.ftl" as deliverableList /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/images/global/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="project.deliverableList.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>
    
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
           
          <h3 class="headTitle">[@s.text name="Project Deliverables" /]</h3>  

          <div class="form-group col-md-12 legendContent">
          
          
          <div class="col-md-6 explanation">
            <div class="col-md-12 form-group "><b>FAIR:</b></div>
            <div class="form-group col-md-6 "><span>F</span> Findable </div>
            <div class="form-group col-md-6 "><span>A</span> Accessible</div>
            <div class="form-group col-md-6 "><span>I</span> Interoperable</div>
            <div class="form-group col-md-6 "><span>R</span> Reusable</div>
          </div>
          <div class="col-md-6 colors">
            <div class="col-md-12 form-group "><b>FAIR colors:</b></div>
            <div class="form-group col-md-6 fair"><span id="achieved"></span> Achieved </div>
            <div class="form-group col-md-6 fair"><span id="notAchieved"></span> Not achieved</div>
            <div class="form-group col-md-6 fair"><span id="notDefined"></span> Not defined</div>
          </div>
         </div>
           <h3 class="subTitle headTitle">On going deliverables</h3>
           <hr />
           <div style="">[@deliverableList.deliverablesList deliverables=action.getDeliverables(true) canValidate=true canEdit=candit namespace="/projects" defaultAction="${(crpSession)!}/deliverable"/]</div> 
                     
          <div class="text-right">
            [#if editable && action.hasPermission("addDeliverable")]
            <div class="addDeliverable button-blue"><a  href="[@s.url namespace="/${currentSection}" action='${(crpSession)!}/addNewDeliverable'] [@s.param name="projectID"]${projectID}[/@s.param][/@s.url]">
              <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addDeliverable" /]
            </a></div>
            [/#if]
          </div>
          
          
          [#if action.getDeliverables(false)?has_content]
            <h3 class="subTitle headTitle">Completed deliverables</h3>
            <hr />
            <div style="">[@deliverableList.deliverablesList deliverables=action.getDeliverables(false) canValidate=true canEdit=candit namespace="/projects" defaultAction="${(crpSession)!}/deliverable"/]</div> 
          [/#if]

         
        [/@s.form] 
      </div>
    </div>  
</section>

  
[#include "/WEB-INF/global/pages/footer.ftl"]
