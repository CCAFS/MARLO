[#ftl]
[#assign title = "MARLO Projects Bilateral Co-funded" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectsList.js" ] /]
[#assign customCSS = ["${baseUrl}/css/global/customDataTable.css"] /]
[#assign currentSection = "project co-funded" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/bilaterals", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
    
<section class="container">
  <article class="fullBlock" id="mainInformation">
 [#-- Project title --]
    <div class="form-group">
      <div class="row">
        <div class="col-md-12">[@customForm.textArea name="title" i18nkey="projectCofunded.title"/] </div>
      </div>
    </div>
    <div class="form-group">
      <div class="row">
         <div class="col-md-4">[@customForm.input name="startDate" i18nkey="projectCofunded.startDate"/] </div>
         <div class="col-md-4">[@customForm.input name="endDate" i18nkey="projectCofunded.endDate"/] </div>
         <div class="col-md-4">[@customForm.input name="financeCode" i18nkey="projectCofunded.financeCode"/] </div>
      </div>
    </div>
    <div class="form-group">
      <div class="row">
        <div class="col-md-6">[@customForm.select name="agreementStatus" i18nkey="projectCofunded.agreementStatus"  listName="" keyFieldName=""  displayFieldName="" /] </div>
        <div class="col-md-6">[@customForm.input name="budgetAgreementPeriod" i18nkey="projectCofunded.budgetAgreementPeriod" className=""/]</div>
      </div>
    </div>
    <div class="form-group">
      <div class="row">
        <div class="col-md-6">[@customForm.input name="contactName" i18nkey="projectCofunded.contactName"/]</div>
        <div class="col-md-6">[@customForm.input name="contactEmail" i18nkey="projectCofunded.contactEmail"/]</div>
      </div>
    </div>
    <div class="form-group">
      <div class="row">
        <div class="col-md-12">
          [@customForm.select name="donor" i18nkey="projectCofunded.donor"  listName="" keyFieldName=""  displayFieldName="" required=true /]
        </div>
      </div>
    </div>
    <div class="note">
      [@s.text name="projectCofunded.donor.disclaimer" /]
    </div>
  </article>
</section>


[#include "/WEB-INF/global/pages/footer.ftl"]
