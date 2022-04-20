[#ftl]
[#assign title = "Safeguards" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "blueimp-file-upload", "cytoscape","cytoscape-panzoom"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/impactPathway/programSubmit.js",
  "${baseUrlMedia}/js/projects/safeguards.js?20220420a",
  [#-- "${baseUrlCdn}/global/js/autoSave.js", --]
  "${baseUrlCdn}/global/js/impactGraphic.js",
  "${baseUrlCdn}/global/js/fieldsValidation.js"
  ]
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/impactPathway/outcomes.css?20202209",
  "${baseUrlCdn}/global/css/impactGraphic.css"
  ]
/]

[#assign currentStage = "safeguards" /]
[#assign hideJustification = true /]
[#assign isCenterProject = (action.isProjectCenter(project.id))!false ]
[#--  [#assign hasFile = safeguard.file?? && safeguard.file.id?? /]--]

  [#assign currentSection = "clusters" /]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"${currentSection}", "action":"${(crpSession)!}/projectsList"},
    {"text":"C${project.id}", "nameSpace":"${currentSection}", "action":"${crpSession}/safeguards", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
    {"label":"safeguards", "nameSpace":"${currentSection}", "action":""}
  ] /]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]


[#--  
<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <div  class="removeHelp"><span class="glyphicon glyphicon-remove"></span></div>
    <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-help.jpg" />
    <p class="col-md-10">
      [#if (project.projectInfo.isProjectEditLeader())!false]
        [#if (reportingActive)!false]
          [@s.text name="projectDescription.help3" ] [@s.param][@s.text name="global.managementLiaison" /][/@s.param] [/@s.text]
        [#else]
          [@s.text name="projectDescription.help2" ] [@s.param][@s.text name="global.managementLiaison${isCenterProject?string('Center', '')}" /][/@s.param] [/@s.text]
        [/#if]
      [#else]
        [@s.text name="projectDescription.help1" /]
      [/#if]
    </p>
  </div>
  <div style="display:none" class="viewMore closed"></div>
</div>
--]

[#if (!availabePhase)!false]
  [#include "/WEB-INF/crp/views/projects/availability-projects.ftl" /]
[#else]
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/projects/messages-projects.ftl" /]

        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]

          <h3 class="headTitle">[@s.text name="project.safeguards.sectionName" /]</h3>
          <div id="projectDescription" class="borderBox">

            [#-- Project Title --]
            <div class="form-group">
            <br>
              [@s.text name="project.safeguards.title"  /]
              </br>
              </br>
              [@s.text name="project.safeguards.description"  /]
              <br>
              <br>
              
              <div class="helpMessage infoText2">
                [#--  <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-transparent-warning.png" />--]
                  <div class="form-group" align="left">
                        [#--  <label for="">[@s.text name="Download Template" /]:</label> --]
                        <br>
                        <div align="left">
                          <a href="${baseUrlCdn}/global/documents/E&S_Reporting_Template.docx" download>[@s.text name="project.safeguards.downloadText"][/@s.text]<img src="${baseUrlCdn}/global/images/word.png" style="float: none !important;"/></a>
                        </div>
                  </div>
              </div>
              
              <br>   
              <div class="form-group" align="center">
               <input type="hidden"  name="safeguard" value="${(safeguard)!}"  />
               <input type="hidden"  name="safeguardID" value="${(safeguard.id)!}"  />
                    [@uploadfileMacro safeguard=safeguard isTemplate=false /]               
              </div>
                           
            </div>          
          </div>
                   
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
         


          [/@s.form]
      </div>
    </div>
</section>
[/#if]


[#include "/WEB-INF/global/pages/footer.ftl"]

        [#-- Upload a PDF with baseline instructions --]
        [#macro uploadfileMacro safeguard isTemplate=false]
          [#-- Outcome ID Parameter --]
          <div id="safeguard" class="form-group fileUploadContainer">
            <div class="uploadPDFTitleContainer">
            <img src="${baseUrlCdn}/global/images/pdf.png" class="fileIcon"/>
            <label>[@customForm.text name="project.safeguards.uploadText" readText=!editable /]:</label>
            </div>
              <br>
              [#local hasFile = safeguard.file?? && safeguard.file.id?? /]
              <input class="fileID" type="hidden" name="${safeguard}.id" value="${(safeguard.file.id)!}"/>
              <input type="hidden" class="safeguardId" name="${safeguard}.id" value="${(safeguard.id)!}"/>

              [#-- Input File --]
              [#if editable]
                <div class="fileUpload" style="display:${hasFile?string('none','block')}"> <input class="upload" type="file" name="file" data-url="${baseUrl}/safeguardUploadFile.do" accept="application/pdf"></div>
              [/#if]
              [#-- Uploaded File --]
              <p class="fileUploaded textMessage checked" style="display:${hasFile?string('block','none')}">
                <span class="contentResult">[#if safeguard.hasFile??]
                  <a target="_blank" href="${action.getBaseLineFileURL((safeguard.id?string)!-1)}&filename=${(safeguard.file.fileName)!}" target="_blank" class="downloadBaseline"><img src="${baseUrlCdn}/global/images/pdf.png" width="38px" alt="Download document" /> ${(safeguard.file.fileName)!('No file name')} </a>
                  [/#if]</span>
                [#if editable]<span class="removeIcon"> </span> [/#if]
              </p>         
          </div>
          <br />
        [/#macro]


