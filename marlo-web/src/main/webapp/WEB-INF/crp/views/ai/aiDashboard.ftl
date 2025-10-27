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

        <div id="projectDescription" class="borderBox" style="border-radius: 10px; padding: 20px;">
				  <style>
				    .ai-lead { line-height: 1.6; color: #555; }
				    .ai-card  { border-radius: 10px; padding: 16px; }
				    .ai-card + .ai-card { margin-top: 16px; }
				    .ai-card h5 { margin: 10px 0 6px; }
				    .ai-btn  { display:inline-block; width:100%; min-width:280px; padding:8px 0;
				               border-radius:6px; font-weight:600; letter-spacing:.2px; color:#fff;
				               text-decoration:none; }
				  </style>
				
				  <div class="row">
				    <div class="col-lg-1 col-xl-2"></div>
				
				    <div class="col-md-12 col-sm-12 col-xs-12 col-lg-10 col-xl-8">
				
				      <!-- Title -->
				      <h4 style="text-align:center; margin-bottom: 20px; font-weight:700;">AI-CCRA</h4>
				
				      <!-- header -->
				      <div class="row align-items-start">
				        <div class="col-md-2 col-sm-3 col-3 text-center">
				          <img src="${baseUrlCdn}/global/images/asistente-de-inteligencia-artificial.png"
				               width="90" alt="AI Assistant" style="margin-top: 8px;">
				        </div>
				        <div class="col-md-10 col-sm-9 col-9">
				          <h4 class="modal-title" style="margin-top: 4px;">Hello everyone,</h4>
				          <p class="ai-lead" style="margin-top: 10px;">
				            [@s.text name="userIdea.description" /]
				          </p>
				        </div>
				      </div>
				
				      <!-- Card: Report Generator -->
				      <div class="simpleBox ai-card">
				        <p class="ai-lead" style="margin-top: 6px;">
				          🧾 <strong>AICCRA Report Generator</strong>
				        </p>
				        <p class="ai-lead" style="margin: 6px 0 0;">
				          The AICCRA Report Generator automates the preparation of key narrative and tabular content required for the Annual Report submitted to the World Bank.
				          This service consolidates information from multiple internal sources — including contributions, deliverables, and performance indicators — ensuring consistency,
				          accuracy, and alignment with official reporting structures. It produces templated outputs such as indicator overviews, tables with cumulative expected and achieved values,
				          and sections like Challenges and Lessons Learned, significantly reducing manual work and accelerating the reporting process to support evidence-based decision-making and accountability.
				        </p>
				        <div class="text-start" style="margin-top: 20px;">
				          <a href="https://aiccra-reports-generator.streamlit.app/" target="_blank" class="button-blue ai-btn">
				            <span></span> [@s.text name="Go to Report Generator" /]
				          </a>
				        </div>
				      </div>
				
				      <!-- Card: Chatbot -->
				      <div class="simpleBox ai-card">
				        <p class="ai-lead" style="margin-top: 6px;">
				          💬 <strong>AICCRA Chatbot</strong>
				        </p>
				        <p class="ai-lead" style="margin: 6px 0 0;">
				          The AICCRA Chatbot is an interactive assistant designed to answer specific, focused questions about the program’s data, indicators, and results.
				          It provides quick, accurate, and contextualized responses drawn from AICCRA’s internal knowledge base — helping users retrieve relevant information without navigating full reports or datasets.
				          Ideal for program staff, partners, and stakeholders, the chatbot improves access to institutional knowledge and supports informed decision-making through a conversational and user-friendly interface.
				        </p>
				        <div class="text-start" style="margin-top: 20px;">
				          <a href="https://chatbot-aiccra.streamlit.app/" target="_blank" class="button-blue ai-btn">
				            <span></span> [@s.text name="Go to AICCRA chatbot" /]
				          </a>
				        </div>
				      </div>
				
				      <!-- separator + textarea -->
				      <hr style="margin: 24px 0; border: none; border-top: 1px solid #ddd;">
				      <div class="form-group metadataElement-objectives modal-title" style="margin-top: 8px;">
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
				</div>

  			<div class="clearfix"></div>


        [#-- Buttons & hidden inputs inside the form --]
        [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
      [/@s.form]

    </div>
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]
