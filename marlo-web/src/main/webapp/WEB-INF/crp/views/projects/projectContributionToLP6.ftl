[#ftl]
[#assign title = "Project Contributions to LP6" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "font-awesome", "flag-icon-css"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectContributionsToLP6.js",
  "${baseUrlCdn}/global/js/autoSave.js",
  "${baseUrlCdn}/global/js/fieldsValidation.js"
  ] /]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/projectsContributionToLP6.css",
  "${baseUrlMedia}/css/projects/projectContributionsCrpList.css?20230106"
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
          
          <p class="note lp6-contribution-note"><small>[@s.text name="projects.LP6Contribution.infoText"/] (<span class="lp6-view-more" data-toggle="modal" data-target=".lp6info-modal">view more</span>)</small></p>
          [#-- LP6 Help Text expanded --]
          <div class="modal fade extended-table-modal lp6info-modal" tabindex="-1" role="dialog" aria-labelledby="extendedTableModal" aria-hidden="true">
            <div class="modal-dialog modal-lg">
               <div class="modal-content">
                 <button type="button" class="close lp6-close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                   <div class="lp6-help">[@s.text name="projects.LP6Contribution.helpText"/]</div>
                </div>
            </div>
          </div>
            [#if (project.projectLp6Contribution.contribution)!false]
          
            <div class="form-group contributionNarrative">
              [#-- Contribution to LP6 narrative --]
              <input type="hidden" name="project.projectLp6Contribution.contribution" value="${(project.projectLp6Contribution.contribution?string)!}"/>
              [@customForm.textArea name="project.projectLp6Contribution.narrative" i18nkey="projects.LP6Contribution.narrativeContribution"  className="limitWords-100" required=true editable=editable /]
             </div>
              [#-- Deliverables --]
               <div class="form-group simpleBox">
                 [@customForm.elementsListComponent id="deliverableSelect" name="project.projectLp6Contribution.deliverables" elementType="deliverable" elementList=(project.projectLp6Contribution.deliverables) label="projects.LP6Contribution.evidenceDeliverables" listName="deliverables" keyFieldName="id" displayFieldName="composedName" required=false/]
                [#if editable]
                <p class="note">[@s.text name="projects.LP6Contribution.deliverablesTooltip" /] <a href="[@s.url action="${crpSession}/deliverableList"][@s.param name="projectID" value=projectID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">clicking here.</a></p>
                [/#if]
               </div>
              [#-- Geographic Scope Narrative --]
              <div class="form-group">
                [@customForm.textArea name="project.projectLp6Contribution.geographicScopeNarrative"  i18nkey="projects.LP6Contribution.geographicScopeNarrative" className="limitWords-100" required=true editable=editable /] 
              </div>
              [#-- Work across flagships --]
              [@contributionForm name="workingAcrossFlagships" textName="workingAcrossFlagshipsNarrative" i18nkey="flagshipLevels" checkedValue=(project.projectLp6Contribution.workingAcrossFlagships?string)!"" className="limitWords-100"/]
              [#-- Efforts to position CGIAR --]
              [@contributionForm name="undertakingEffortsLeading" textName="undertakingEffortsLeadingNarrative" i18nkey="positionCGIAR" checkedValue=(project.projectLp6Contribution.undertakingEffortsLeading?string)!"" className="limitWords-100"/]
              [#-- Innovative Pathways / Tools --]
              [@contributionForm name="providingPathways" textName="providingPathwaysNarrative" i18nkey="innovativePathways" checkedValue=(project.projectLp6Contribution.providingPathways?string)!"" className="limitWords-100"/]
            <div class="form-group">
              [#-- Top 3 Partners --]
              [@customForm.textArea name="project.projectLp6Contribution.topThreePartnershipsNarrative" i18nkey="projects.LP6Contribution.partnerships" className="limitWords-100" required=true editable=editable /]
            </div>
              [#-- Scaling CSA --]
              [@contributionForm name="undertakingEffortsCsa" textName="undertakingEffortsCsaNarrative" i18nkey="scalingCSA" checkedValue=(project.projectLp6Contribution.undertakingEffortsCsa?string)!"" className="limitWords-100"/]
              [#-- Climate finance --]
              [@contributionForm name="initiativeRelated" textName="initiativeRelatedNarrative" i18nkey="climateFinance" checkedValue=(project.projectLp6Contribution.initiativeRelated?string)!"" className="limitWords-100"/]
            [#else]
              <p> [@s.text name="projects.LP6Contribution.noContributionMessage" /]</p>
            [/#if]
         </div>
         [#-- Section Buttons & hidden inputs--]
         [#if (project.projectLp6Contribution.contribution)!false]
          [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
         [/#if]
        [/@s.form]
      
 </section>

[#include "/WEB-INF/global/pages/footer.ftl"]


[#macro contributionForm name textName i18nkey className checkedValue=""]
  <div class="contributionForm">
    <div class="form-group row">
      [#-- Radio buttons --]
      <div class="col-md-9"> 
         <label>[@s.text name="projects.LP6Contribution.${i18nkey}"/][@customForm.req required=true /]</label>
      </div>
      <div class="col-md-3 radio-yes-no">
         [@customForm.radioFlat id="${i18nkey}-yes" name="project.projectLp6Contribution.${name}" label="Yes" value="true" checked=(checkedValue == "true") cssClass="input-yn" cssClassLabel="radio-label-yes" editable=editable /]
         [@customForm.radioFlat id="${i18nkey}-no" name="project.projectLp6Contribution.${name}" label="No" value="false" checked=(checkedValue == "false") cssClass="input-yn" cssClassLabel="radio-label-no" editable=editable /]
      </div>
    </div>
    [#-- Text --]
    <div class="form-group narrativeBlock" style="display:${((checkedValue == "true"))?string('block','none')}">
       [@customForm.textArea name="project.projectLp6Contribution.${textName}"  i18nkey="projects.LP6Contribution.${i18nkey}.question"  className="${className} narrativeInput" required=true editable=editable /]
    </div>
  </div>
[/#macro]
