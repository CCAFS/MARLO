[#ftl]
[#assign title = "AI-CRA" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2","flag-icon-css"] /]
[#assign customJS = ["${baseUrlCdn}/global/js/autoSave.js"] /]
[#assign currentSection = "ai" /]
[#assign currentStage = "description" /]

[#assign breadCrumb = [
  {"label":"ai", "nameSpace":"${currentSection}", "action":"${(crpSession)!}/ai", "param": "edit=true&phaseID=${(actualPhase.id)!}"},
  {"label":"userIdea", "nameSpace":"/monitoring", "action":""}
] /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section class="container">
  <div class="row">
    <div class="col-md-12">

      [@s.form action=actionName method="POST" cssClass=""]
        [#--  <h3 class="headTitle">[@s.text name="userIdea.title" /]</h3>--]


        <div id="projectDescription" class="borderBox">
          <br/>

          <div class="row align-items-center">
            <div class="col-md-1">
              <img src="${baseUrlCdn}/global/images/asistente-de-inteligencia-artificial.png"
                   width="90" alt="AI Assistant"/>
            </div>
            <div class="col-md-11">
       				<h4 class="modal-title">Hello everyone,</h4>
       				<br>
            	[@s.text name="userIdea.description" /]
            </div>
          </div>
          <br>
					<br>
					<div class="clearfix"></div>

          [#-- 
          <div class="form-group metadataElement-description">
            [@customForm.input
              name="userIdea.question"
              i18nkey="userIdea.question"
              required=true
              className="project-title metadataValue"
              editable=false
            /]
          </div>
--]

          [#-- Respuesta (editable) --]
          <div class="form-group metadataElement-objectives modal-title">
            [@customForm.textArea
              name="userIdea.answer"
              i18nkey="userIdea.question.default"
              required=false
              className="metadataValue"
              editable=true
            /]
          </div>
        </div>
  			<div class="clearfix"></div>


        [#-- Botones & hidden inputs dentro del form --]
        [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
      [/@s.form]

    </div>
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]
