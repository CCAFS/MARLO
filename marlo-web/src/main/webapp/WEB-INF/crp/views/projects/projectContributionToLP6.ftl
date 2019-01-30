[#ftl]
[#assign title = "Project Contributions to LP6" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "font-awesome", "flat-flags"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectContributionsToLP6.js",
  "${baseUrl}/global/js/autoSave.js",
  "${baseUrl}/global/js/fieldsValidation.js"
  ] /]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/projectContributionsCrpList.css"
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
                <p class="note">[@s.text name="projects.LP6Contribution.deliverablesTooltip" /] <a href="[@s.url action="${crpSession}/deliverableList"][@s.param name="projectID" value=projectID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">clicking here.</a></p>
                [/#if]
               </div>
              [#-- Geographic Scope --]
              <div class="form-group geographicScopeBlock">      
                [#assign geographicScope = ((project.projectLp6Contribution.geographicScope.id)!-1) ]
                [#assign isRegional = ((geographicScope == action.reportingIndGeographicScopeRegional)!false) ]
                [#assign isMultiNational = ((geographicScope == action.reportingIndGeographicScopeMultiNational)!false) ]
                [#assign isNational = ((geographicScope == action.reportingIndGeographicScopeNational)!false) ]
                [#assign isSubNational = ((geographicScope == action.reportingIndGeographicScopeSubNational)!false) ]
                
                <div class="form-group simpleBox">
                  <div class="form-group row">
                    <div class="col-md-6">
                      [#-- Geographic Scope --]
                      [@customForm.select name="project.projectLp6Contribution.geographicScope.id" className="setSelect2 geographicScopeSelect" i18nkey="projects.LP6Contribution.geographicScope" listName="geographicScopes" keyFieldName="id"  displayFieldName="name" required=false className="geographicScopeSelect" editable=editable/]
                    </div>
                  </div>
                  <div class="form-group regionalBlock" style="display:${(isRegional)?string('block','none')}">
                    [#-- Regional scope --]
                      [@customForm.elementsListComponent name="project.projectLp6Contribution.regions" elementType="locElement" id="region" elementList=(project.projectLp6Contribution.regions)![] label="projects.LP6Contribution.region"  listName="regions" keyFieldName="id" displayFieldName="name" required=false /]
                  </div>
                  <div class="form-group nationalBlock" style="display:${(isMultiNational || isNational || isSubNational)?string('block','none')}">
                    [#-- Multinational, National and Subnational scope --]
                    [@customForm.select name="project.projectLp6Contribution.countriesIds" label="" i18nkey="projects.LP6Contribution.countries" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="project.projectLp6Contribution.countriesIds" multiple=true required=false className="countriesSelect" disabled=!editable/]
                  </div>
                </div>
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
