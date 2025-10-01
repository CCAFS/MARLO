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

        <div id="projectDescription" class="borderBox" style="height: calc(100vw / 4); display: flex; align-items: center;">
          <br/>
          <div class="col-lg-1 col-xl-2"></div>
          <div class="col-md-12 col-sm-12 col-xs-12 col-lg-10 col-xl-8 ">
            <div class="row align-items-center">
              <div class="col-md-1 col-sm-2 col-xs-2 col-lg-1 col-xl-1">
                <img src="${baseUrlCdn}/global/images/asistente-de-inteligencia-artificial.png"
                    width="90" alt="AI Assistant"/>
              </div>
              <div class="col-md-10 col-sm-9 col-xs-9 col-lg-10 col-xl-10" style="margin-left: 5%;">
                <h4 class="modal-title">Hello everyone,</h4>
                <br>
                [@s.text name="userIdea.description" /]
              </div>
            </div>
            <br>
            <div class="clearfix"></div>

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
          <div class="col-lg-1 col-xl-2"></div>
        </div>
  			<div class="clearfix"></div>


        [#-- Botones & hidden inputs dentro del form --]
        [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
      [/@s.form]

    </div>
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]
