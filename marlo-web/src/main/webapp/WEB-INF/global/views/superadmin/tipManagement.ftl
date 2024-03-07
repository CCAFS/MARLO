[#ftl]
[#assign title = "TIP Management" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [] /]
[#assign customJS = [ "${baseUrlCdn}/global/js/superadmin/tipManagement.js?20240305",  "${baseUrlCdn}/global/js/fieldsValidation.js"
 ] /]
[#assign customCSS = [ "${baseUrlCdn}/global/css/superadmin/superadmin.css" ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "tipManagement" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"tipManagement", "nameSpace":"", "action":""}
]/]


[#include "/WEB-INF/global/pages/header.ftl" /]
<hr />

<div class="container">
  [#include "/WEB-INF/global/pages/breadcrumb.ftl" /]
</div>
[#include "/WEB-INF/global/pages/generalMessages.ftl" /]

  
<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/global/views/superadmin/menu-superadmin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]
        
        [#-- System Level Outcomes --]
        <h4 class="sectionTitle">[@s.text name="tipParameterManagement.title" /]</h4>
 
        <div id="tipManagement" class="borderBox">
              <div class="form-group">
                [@customForm.input name="tipParameter.tipBaseUrl" i18nkey="tipParameter.tipBaseUrl" className="description limitWords-100" required=true /]
              </div>
              <div class="form-group">
                [@customForm.input name="tipParameter.privateKey" i18nkey="tipParameter.privateKey" className="description limitWords-100" required=true /]
              </div>
              <div class="clearfix"></div>
              <div class="form-group">
                [@customForm.input name="tipParameter.tipTokenService" i18nkey="tipParameter.tipTokenService" className="description limitWords-100" required=true /]
              </div>
              <div class="clearfix"></div>      
              <div class="form-group ">
                [@customForm.input name="tipParameter.tipLoginService" i18nkey="tipParameter.tipLoginService" className="description limitWords-100" required=true /]
              </div>
              <div class="clearfix"></div>
              <div class="form-group">
                [@customForm.input name="tipParameter.tipStatusService" i18nkey="tipParameter.tipStatusService" className="description limitWords-100" required=true /]
              </div>
              <div class="clearfix"></div>
              <div class="form-group" style="display:block">
                [@customForm.input id="tipParameter.tokenValue" name="tipParameter.tokenValue" i18nkey="tipParameter.tokenValue" className="description limitWords-100" required=true /]
                <a id="updateTokenBtn" class="addButton">[@s.text name="tipParameter.updateToken" /]</a>
              </div>
              <div class="clearfix"></div>           
              <div class="form-group">
                [@customForm.input name="tipParameter.tokenDueDate" i18nkey="tipParameter.tokenDueDate" className="description limitWords-100" required=false editable=false /]
              </div>
              <div class="clearfix"></div>           
        </div>
 
        
        [#-- Section Buttons--]
        <div class="buttons">
          <div class="buttons-content">
            [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /][/@s.submit]
          </div>
        </div>
        
        [/@s.form]
        
      </div>
    </div>
  </div>
</section>


[#include "/WEB-INF/global/pages/footer.ftl" /]