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

[#assign startYear = (project.startDate?string.yyyy)?number /]
[#assign endYear = (project.endDate?string.yyyy)?number /]
    
<section class="container">
  <article class="fullBlock col-md-12" id="mainInformation">
  
  [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
  
  <h4 class="sectionTitle">[@s.text name="Project summary overview" /]</h4>
  <div class="col-md-12">
  <h4 class="headTitle">General information</h4> 
    <div class="borderBox">
   [#-- Project title --]
      <div class="form-group">
        <div class="row">
          <div class="col-md-12">[@customForm.textArea name="project.title" i18nkey="projectCofunded.title" editable=editable /] </div>
        </div>
      </div>
      [#-- start date, end date and finance code --]
      <div class="form-group">
        <div class="row">
           <div class="col-md-4">[@customForm.input name="project.startDate" i18nkey="projectCofunded.startDate" editable=editable/] </div>
           <div class="col-md-4">[@customForm.input name="project.endDate" i18nkey="projectCofunded.endDate" editable=editable/] </div>
           <div class="col-md-4">[@customForm.input name="project.financeCode" i18nkey="projectCofunded.financeCode" editable=editable/] </div>
        </div>
      </div>
      [#-- Agreement status and total budget --]
      <div class="form-group">
        <div class="row">
          <div class="col-md-6">[@customForm.select name="project.agreement" i18nkey="projectCofunded.agreementStatus"  listName="status" keyFieldName=""  displayFieldName="" editable=editable /] </div>
          <div class="col-md-6">[@customForm.input name="project.budget" i18nkey="projectCofunded.budgetAgreementPeriod" className="" editable=editable /]</div>
        </div>
      </div>
      [#-- CGIAR lead center --]
      <div class="form-group">
        <div class="row">
          <div class="col-md-12">
            [@customForm.select name="project.liaisonInstitution" i18nkey="CGIAR lead center"  listName="liaisonInstitutions" keyFieldName="id"  displayFieldName="composedName" required=true editable=editable /]
          </div>
        </div>
      </div>
      [#-- Contact person name and email --]
      <div class="form-group">
        <div class="row">
          <div class="col-md-6">[@customForm.input name="project.contactPersonName" i18nkey="projectCofunded.contactName" editable=editable /]</div>
          <div class="col-md-6">[@customForm.input name="project.contactPersonEmail" i18nkey="projectCofunded.contactEmail" editable=editable /]</div>
        </div>
      </div>
      [#-- Donor --]
      <div class="form-group">
        <div class="row">
          <div class="col-md-12">
            [@customForm.select name="project.institution" i18nkey="projectCofunded.donor"  listName="institutions" keyFieldName="id"  displayFieldName="ComposedName" required=true editable=editable /]
          </div>
        </div>
      </div>
      <div class="note">
        [@s.text name="projectCofunded.donor.disclaimer" /]
      </div>
    </div>
    
    
    <h4 class="headTitle">Annual project contribution</h4> 
    <div class="">
      [#-- Year Tabs --]
          <ul class="nav nav-tabs budget-tabs" role="tablist">
            [#list startYear .. endYear as year]
              <li class="[#if year == currentCycleYear]active[/#if]"><a href="#year-${year}" role="tab" data-toggle="tab">${year} </a></li>
            [/#list]
          </ul>
          
          [#-- Years Content --]
          <div class="tab-content">
            [#list startYear .. endYear as year]
              <div role="tabpanel" class="tab-pane [#if year == currentCycleYear]active[/#if]" id="year-${year}">
                  
                </div>
              </div>
            [/#list]  
          </div>
      
      
    </div>
    [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
  </div>
  
  [/@s.form] 
  </article>
</section>


[#include "/WEB-INF/global/pages/footer.ftl"]
