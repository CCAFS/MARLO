[#ftl]
[#assign title = "Feedback Status" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "blueimp-file-upload", "cytoscape","cytoscape-panzoom"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/safeguards.js?20220510a",
  [#-- "${baseUrlCdn}/global/js/autoSave.js", --]
  "${baseUrlCdn}/global/js/impactGraphic.js",
  "${baseUrlCdn}/global/js/fieldsValidation.js"
  ]
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/safeguard.css?20220512a",
  "${baseUrlMedia}/css/impactPathway/outcomes.css?20202209",
  "${baseUrlCdn}/global/css/impactGraphic.css"
  ]
/]

[#assign currentStage = "feedback" /]
[#assign hideJustification = true /]
[#assign isCenterProject = (action.isProjectCenter(project.id))!false ]
[#--  [#assign hasFile = safeguard.file?? && safeguard.file.id?? /]--]

  [#assign currentSection = "clusters" /]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"${currentSection}", "action":"${(crpSession)!}/projectsList"},
    {"text":"C${project.id}", "nameSpace":"${currentSection}", "action":"${crpSession}/feedback", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
    {"label":"feedbackStatus", "nameSpace":"${currentSection}", "action":""}
  ] /]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]


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
                <div>
                  <div class="templateContainer">
                    <a href="${baseUrlCdn}/global/documents/E&S_Reporting_AICCRA_Template_V2_20220329.docx" download>[@s.text name="project.safeguards.downloadText"][/@s.text]<img src="${baseUrlCdn}/global/images/word.png" style="float: none !important;"/></a>
                  </div>
                </div>
              </div>

              <br>
              <div class="dropFileParentContainer">
                <div class="dropFileContainer">
                  <div style="width: 90%;">
                    [@uploadfileMacro safeguard=safeguard isTemplate=false /]               
                  </div>
                </div>
              </div>
              <br>
                   
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
  <div id="safeguard" class="fileUploadContainer">
    [#local hasFile = safeguard.file?? && safeguard.file.id?? /]
    <div class="uploadPDFTitleContainer">
      <img src="${baseUrlCdn}/global/images/pdf.png" class="fileIcon"/>
      <label>[@customForm.text name="project.safeguards.uploadText" readText=!editable /]</label>
      <br>
      <label>━━━ OR ━━━</label>
      <br>
    </div>
    
    <input class="fileID" type="hidden" name="${safeguard}.id" value="${(safeguard.file.id)!}"/>
    <input type="hidden" class="safeguardID" name="${safeguard}.id" value="${(safeguard.id)!}"/>

    [#-- Input File --]
    [#if editable]
      <div class="fileUpload" style="display:${hasFile?string('none','block')}">
        <input type="file" id="file" name="file" class="upload" data-url="${baseUrl}/safeguardUploadFile.do" accept="application/pdf" style="display: none;"/>
        <label class="browseFile" for="file">Browse file</label>
      </div>
    [/#if]
    [#-- Uploaded File --]
    <p class="fileUploaded textMessage checked" style="display:${hasFile?string('block','none')}">
      <span class="contentResult">[#if safeguard.hasFile??]
        <a target="_blank" href="${action.getBaseLineFileURL((safeguard.id?string)!-1)}&filename=${(safeguard.file.fileName)!}" target="_blank" class="downloadBaseline"> ${(safeguard.file.fileName)!('No file name')} </a>
        [/#if]</span>
      [#if editable]<span class="removeIcon"> </span> [/#if]
    </p>         
  </div>
[/#macro]


