[#ftl]
[#assign title = "MARLO Projects Bilateral Co-funded" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customJS = ["${baseUrl}/js/projects_cofunded/projectCofunded.js" ] /]
[#assign customCSS = ["${baseUrl}/css/global/customDataTable.css"] /]
[#assign currentSection = "project co-funded" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/bilaterals", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
    
<section class="container">
  <article class="fullBlock col-md-12" id="mainInformation">
  
  [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
  
  <h4 class="sectionTitle">[@s.text name="Project summary overview" /]</h4>
  <div class="col-md-12">
    <div class="borderBox">
   [#-- Project title --]
      <div class="form-group">
        <div class="row">
          <div class="col-md-12">[@customForm.textArea name="project.title" i18nkey="projectCofunded.title"/] </div>
        </div>
      </div>
      [#-- start date, end date and finance code --]
      <div class="form-group">
        <div class="row">
           <div class="col-md-4">[@customForm.input name="project.startDate" i18nkey="projectCofunded.startDate"/] </div>
           <div class="col-md-4">[@customForm.input name="project.endDate" i18nkey="projectCofunded.endDate"/] </div>
           <div class="col-md-4">[@customForm.input name="project.financeCode" i18nkey="projectCofunded.financeCode"/] </div>
        </div>
      </div>
      [#-- Agreement status and total budget --]
      <div class="form-group">
        <div class="row">
          <div class="col-md-6">[@customForm.select name="project.agreement" i18nkey="projectCofunded.agreementStatus"  listName="status" keyFieldName=""  displayFieldName="" /] </div>
          <div class="col-md-6">[@customForm.input name="project.budget" i18nkey="projectCofunded.budgetAgreementPeriod" className=""/]</div>
        </div>
      </div>
      [#-- CGIAR lead center --]
      <div class="form-group">
        <div class="row">
          <div class="col-md-12">
            [@customForm.select name="project.liaisonInstitution" i18nkey="CGIAR lead center"  listName="liaisonInstitutions" keyFieldName="id"  displayFieldName="composedName" required=true /]
          </div>
        </div>
      </div>
      [#-- Contact person name and email --]
      <div class="form-group">
        <div class="row">
          <div class="col-md-6">[@customForm.input name="project.contactPersonName" i18nkey="projectCofunded.contactName"/]</div>
          <div class="col-md-6">[@customForm.input name="project.contactPersonEmail" i18nkey="projectCofunded.contactEmail"/]</div>
        </div>
      </div>
      [#-- Donor --]
      <div class="form-group">
        <div class="row">
          <div class="col-md-12">
            [@customForm.select name="project.institution" i18nkey="projectCofunded.donor"  listName="institutions" keyFieldName="id"  displayFieldName="ComposedName" required=true /]
          </div>
        </div>
      </div>
      <div class="note">
        [@s.text name="projectCofunded.donor.disclaimer" /]
      </div>
    </div>
    [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
  </div>
  
  [/@s.form] 
  </article>
</section>


[#include "/WEB-INF/global/pages/footer.ftl"]
