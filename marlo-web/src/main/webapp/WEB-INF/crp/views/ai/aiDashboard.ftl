[#ftl]
[#assign title = "AI-CRA" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2","flag-icon-css"] /]
[#assign customJS = ["${baseUrlCdn}/global/js/autoSave.js"] /]
[#assign customCSS = [ "${baseUrlMedia}/css/ai/aiDashboard.css",  "https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/css/select2.min.css",
  "https://cdnjs.cloudflare.com/ajax/libs/select2-bootstrap-theme/0.1.0-beta.10/select2-bootstrap.min.css" ] /]
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
				        <p class="ai-lead" style="margin-top: 7px;">
				          🧾 <strong>AICCRA Report Generator</strong>
				        </p>
				        <p class="ai-lead" style="margin: 6px 0 0;">
				          [@s.text name="userIdea.reportGeneratorNarrative" /]
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
				        	[@s.text name="userIdea.ChatbotNarrative" /]
				        </p>
				        <div class="text-start" style="margin-top: 20px;">
				          <a href="https://chatbot-aiccra.streamlit.app/" target="_blank" class="button-blue ai-btn">
				            <span></span> [@s.text name="Go to AICCRA chatbot" /]
				          </a>
				        </div>
				      </div>
							<br>
				      					    
							  <div class=" containerAlert alert-leftovers alertColorBackgroundWarning" id="containerAlert">
							    <div class="containerLine alertColorWarning"></div>
							    <div class="containerIcon">
							      <div class="containerIcon alertColorWarning"> 
							        <img src="${baseUrlCdn}/global/images/icon-warning.png" />      
							      </div>
							    </div>
							    <div class="containerText col-md-12">
							      <p class="alertText">Disclaimer:<br>[@s.text name="userIdea.disclaimer" /]</p>
							      <br>
							    </div>
							  </div>


							  <div class=" containerAlert alert-leftovers alertColorBackgroundInfo" id="containerAlert" style="margin-top: 40px;">
							    <div class="containerLine alertColorInfo"></div>
							    <div class="containerText col-md-12">
							      <p class="alertText">[@s.text name="userIdea.question.default" /]</p>
							    </div>
							  </div>

								<div class="form-group metadataElement-objectives modal-title ai-textarea-group">
								  <div class="ai-textarea-wrapper">
								    [@customForm.textArea
								      name="userIdea.answer"
								      i18nkey=" "
								      required=false
								      className="metadataValue ai-textarea-field"
								      editable=true
								    /]
								
								    <!-- Inline Send Button -->
								    <button type="button" class="button-send-inline" title="Send Idea" disabled>
								      <span class="glyphicon glyphicon-send" aria-hidden="true"></span>
								      <span></span>
								    </button>
								  </div>
								</div>


								<script>
								  document.addEventListener('DOMContentLoaded', function() {
								    const textarea = document.querySelector('textarea[name="userIdea.answer"]');
								    const inlineSendBtn = document.querySelector('.button-send-inline');
								    const saveBtn = document.querySelector('button[name="save"]'); // from buttons-projects.ftl include
								
								    if (textarea && inlineSendBtn && saveBtn) {
								      const toggleButton = () => {
								        const hasText = textarea.value.trim().length > 0;
								        inlineSendBtn.disabled = !hasText;
								      };
								
								      // Link inline button to original Save button
								      inlineSendBtn.addEventListener('click', function() {
								        saveBtn.click();
								      });
								
								      // Initial state + input listener
								      toggleButton();
								      textarea.addEventListener('input', toggleButton);
								    }
								  });
								</script>



				
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
