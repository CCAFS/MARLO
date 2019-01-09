[#ftl]
[#assign canEdit = true /]
[#assign editable = true /]
[#assign title = "Project Contributions to LP6" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "jsUri"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectContributionsToLP6.js",
  "${baseUrl}/global/js/autoSave.js",
  "${baseUrl}/global/js/fieldsValidation.js"
  ] /]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/projectsContributionToLP6.css"
  ] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "contributionsLP6" /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl"/]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]

<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
      <div class="col-md-9">
        <h3 class="headTitle">[@s.text name="projects.LP6Contribution.contributionTitle" /]</h3>  
        <div id="projectContributionToLP6" class="borderBox">
          <div class="form-group">
            [#-- Contribution to LP6 narrative --]
            [@customForm.textArea name="projects.LP6Contribution.narrativeContribution"  i18nkey="projects.LP6Contribution.narrativeContribution"  className="limitWords-100" required=true editable=canEdit /]
          </div>
          <div class="form-group row">
            [#-- Geographic Scope --]
            <div class="col-md-6">
              [@customForm.select name="projects.LP6Contribution.scope" label=""  i18nkey="projects.LP6Contribution.geographicScope" listName="" className="" editable=canEdit /]
            </div>
          </div>
          <div class="form-group row">
            [#-- Work across flagships --]
            <div class="col-md-9"> 
              <label>[@s.text name="projects.LP6Contribution.workingAcrossFlagships"/][@customForm.req required=true /]</label>
            </div>
            <div class="col-md-3">
                [@customForm.radioFlat id="flagshipLevels-yes" name="flagshipLevels" label="Yes" value="true" checked=false cssClassLabel="radio-label-yes" editable=canEdit/]
                [@customForm.radioFlat id="flagshipLevels-no" name="flagshipLevels" label="No" value="false" checked=false cssClassLabel="radio-label-no" editable=canEdit/]
            </div>
          </div>
          <div class=form-group">
            [@customForm.textArea name="projects.LP6Contribution.workingAcrossFlagships.question"  i18nkey=""  className="limitWords-100" required=true editable=canEdit/]
          </div>
          <div class="form-group row">
            [#-- Efforts to position CGIAR --]
            <div class="col-md-9"> 
              <label>[@s.text name="projects.LP6Contribution.positionCGIAR"/][@customForm.req required=true /]</label>
            </div>
            <div class="col-md-3">
                [@customForm.radioFlat id="cgiarPosition-yes" name="cgiarPosition" label="Yes" value="true" checked=false cssClassLabel="radio-label-yes" editable=canEdit/]
                [@customForm.radioFlat id="cgiarPosition-no" name="cgiarPosition" label="No" value="false" checked=false cssClassLabel="radio-label-no" editable=canEdit/]
            </div>
          </div>
          <div class=form-group">
            [@customForm.textArea name="projects.LP6Contribution.positionCGIAR.question"  i18nkey=""  className="limitWords-100" required=true editable=canEdit/]
          </div>
          <div class="form-group row">
            [#-- Innovative Pathways / Tools --]
            <div class="col-md-9"> 
              <label>[@s.text name="projects.LP6Contribution.pathways"/][@customForm.req required=true /]</label>
            </div>
            <div class="col-md-3">
                [@customForm.radioFlat id="innovativePathways-yes" name="innovativePathways" label="Yes" value="true" checked=false cssClassLabel="radio-label-yes" editable=canEdit/]
                [@customForm.radioFlat id="innovativePathways-no" name="innovativePathways" label="No" value="false" checked=false cssClassLabel="radio-label-no" editable=canEdit/]
            </div>
          </div>
          <div class=form-group">
            [@customForm.textArea name="projects.LP6Contribution.pathways.question"  i18nkey=""  className="limitWords-100" required=true editable=canEdit /]
          </div>
          <div class="form-group">
            [#-- Top 3 Partners --]
            [@customForm.textArea name="projects.LP6Contribution.partnerships"  i18nkey="projects.LP6Contribution.partnerships"  className="limitWords-100" required=true editable=canEdit /]
          </div>
          <div class="form-group row">
            <div class="col-md-9"> 
              [#-- Scaling CSA --]
              <label>[@s.text name="projects.LP6Contribution.CSA"/][@customForm.req required=true /]</label>
            </div>
            <div class="col-md-3">
                [@customForm.radioFlat id="scalingCSA-yes" name="scalingCSA" label="Yes" value="true" checked=false cssClassLabel="radio-label-yes" editable=canEdit /]
                [@customForm.radioFlat id="scalingCSA-no" name="scalingCSA" label="No" value="false" checked=false cssClassLabel="radio-label-no" editable=canEdit /]
            </div>
          </div>
          <div class=form-group">
            [@customForm.textArea name="projects.LP6Contribution.CSA.question"  i18nkey=""  className="limitWords-100" required=true editable=canEdit /]
          </div>
          <div class="form-group row">
            [#-- Climate finance --]
            <div class="col-md-9"> 
              <label>[@s.text name="projects.LP6Contribution.climateFinance"/][@customForm.req required=true /]</label>
            </div>
            <div class="col-md-3">
                [@customForm.radioFlat id="climateFinance-yes" name="climateFinance" label="Yes" value="true" checked=false cssClassLabel="radio-label-yes" editable=canEdit /]
                [@customForm.radioFlat id="climateFinance-no" name="climateFinance" label="No" value="false" checked=false cssClassLabel="radio-label-no" editable=canEdit /]
            </div>
          </div>
          <div class=form-group">
            [@customForm.textArea name="projects.LP6Contribution.climateFinance.question"  i18nkey=""  className="limitWords-100" required=true editable=canEdit /]
          </div>
        </div>
       </div>
       [#-- Section Buttons & hidden inputs--]
       [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]  
       [/@s.form]
     </div>
 </section>

[#include "/WEB-INF/global/pages/footer.ftl"]

