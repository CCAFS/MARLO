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

        <div id="projectDescription" class="borderBox" style="height; display: flex; align-items: center;">
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
            
						<div class="simpleBox">
						  <p><strong>AICCRA Report Generator</strong></p>
						  The AICCRA Report Generator automates the preparation of key narrative and tabular content required for the Annual Report submitted to the World Bank. This service consolidates information from multiple internal sources — including contributions, deliverables, and performance indicators — ensuring consistency, accuracy, and alignment with official reporting structures. It produces templated outputs such as indicator overviews, tables with cumulative expected and achieved values, and sections like Challenges and Lessons Learned, significantly reducing manual work and accelerating the reporting process to support evidence-based decision-making and accountability.
						  
						  <div class="text-center" style="margin-top: 35px;">
						    <a href="https://aiccra-reports-generator.streamlit.app/" target="_blank" class="button-blue"
						       style="display: inline-block;
						              width: 65%;
						              min-width: 280px;
						              margin: 0 auto;
						              padding: 5px 0;
						              border-radius: 8px;
						              font-weight: 600;
						              font-size: 16px;
						              letter-spacing: 0.2px;
						              text-decoration: none;
						              color: white;">
						      <span class=""></span> [@s.text name="Go to Report Generator" /]
						    </a>
						  </div>
						</div>
						
						<div class="simpleBox" style="margin-top: 45px;">
						  <p><strong>AICCRA Chatbot</strong></p>
						  The AICCRA Chatbot is an interactive assistant designed to answer specific, focused questions about the program’s data, indicators, and results. It provides quick, accurate, and contextualized responses drawn from AICCRA’s internal knowledge base — helping users retrieve relevant information without navigating full reports or datasets. Ideal for program staff, partners, and stakeholders, the chatbot improves access to institutional knowledge and supports informed decision-making through a conversational and user-friendly interface.
						  
						  <div class="text-center" style="margin-top: 35px;">
						    <a href="https://chatbot-aiccra.streamlit.app/" target="_blank" class="button-blue"
						       style="display: inline-block;
						              width: 65%;
						              min-width: 280px;
						              margin: 0 auto;
						              padding: 5px 0;
						              border-radius: 8px;
						              font-weight: 600;
						              font-size: 16px;
						              letter-spacing: 0.2px;
						              text-decoration: none;
						              color: white;">
						      <span class=""></span> [@s.text name="Go to AICCRA chatbot" /]
						    </a>
						  </div>
						</div>



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
