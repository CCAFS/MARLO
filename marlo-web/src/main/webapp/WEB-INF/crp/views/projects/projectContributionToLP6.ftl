[#ftl]
[#assign canEdit = true /]
[#assign editable = true /]
[#assign title = "Project Contributions to LP6" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "font-awesome", "flat-flags"] /]
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
            [@customForm.textArea name="projectLp6Contribution.narrative" i18nkey="projects.LP6Contribution.narrativeContribution"  className="limitWords-100" required=true editable=canEdit /]
           <br>
           </div>
            [#-- Deliverables --]
             <div class="form-group simpleBox">
               [@customForm.elementsListComponent id="deliverableSelect" name="projectLp6Contribution.deliverables" elementType="deliverable" elementList=(projectLp6Contribution.deliverables) label="projects.LP6Contribution.evidenceDeliverables" listName="deliverables" keyFieldName="id" displayFieldName="composedName" required=editable/]
              <p class="note">[@s.text name="projects.LP6Contribution.deliverablesTooltip" /] <a href="[@s.url action="${crpSession}/deliverableList"][@s.param name="projectID" value=projectID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">clicking here.</a></p>
             </div>
            [#-- Geographic Scope --]
            <div class="form-group geographicScopeBlock">        
              [#assign isRegional = ((projectsLp6Contribution.repIndGeographicScope.id == action.reportingIndGeographicScopeRegional)!false) ]
              [#assign isMultiNational = ((projectsLp6Contribution.repIndGeographicScope.id == action.reportingIndGeographicScopeMultiNational)!false) ]
              [#assign isNational = ((projectsLp6Contribution.repIndGeographicScope.id == action.reportingIndGeographicScopeNational)!false) ]
              [#assign isSubNational = ((projectsLp6Contribution.repIndGeographicScope.id == action.reportingIndGeographicScopeSubNational)!false) ]
              
              <div class="form-group simpleBox">
                <div class="form-group row">
                  <div class="col-md-6">
                    [#-- Geographic Scope --]
                    [@customForm.select name="projectLp6Contribution.geographicScope.id" className="setSelect2 geographicScopeSelect" i18nkey="projects.LP6Contribution.geographicScope" listName="repIndGeographicScopes" keyFieldName="id"  displayFieldName="name" className="geographicScopeSelect" editable=editable/]
                  </div>
                </div>
                <div class="form-group regionalBlock" style="display:${(isRegional)?string('block','none')}">
                  [#-- Regional scope --]
                    [@customForm.elementsListComponent name="projectLp6Contribution.region" elementType="locElement" id="region" elementList=(projects.LP6Contribution.regions)![] label="projects.LP6Contribution.region"  listName="repIndRegions" keyFieldName="id" displayFieldName="name" required=false /]
                </div>
                <div class="form-group nationalBlock" style="display:${(isMultiNational || isNational || isSubNational)?string('block','none')}">
                  [#-- Multinational, National and Subnational scope --]
                  [@customForm.select name="projectLp6Contribution.countriesIds" label="" i18nkey="projects.LP6Contribution.countries" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="projectsLp6Contribution.countriesIds" multiple=true className="countriesSelect" disabled=!editable/]
                </div>
              </div>
            </div>
            [#-- Work across flagships --]
            [@contributionForm name="workingAcrossFlagships" textName="workingAcrossFlagshipsNarrative" i18nkey="flagshipLevels" checkedValue=(projectLp6Contribution.workingAcrossFlagships?string)!"" className="limitWords-100"/]
            [#-- Efforts to position CGIAR --]
            [@contributionForm name="undertakingEffortsLeading" textName="undertakingEffortsLeadingNarrative" i18nkey="positionCGIAR" checkedValue=(projectLp6Contribution.undertakingEffortsLeading?string)!"" className="limitWords-100"/]
            [#-- Innovative Pathways / Tools --]
            [@contributionForm name="providingPathways" textName="providingPathwaysNarrative" i18nkey="innovativePathways" checkedValue=(projectLp6Contribution.providingPathways?string)!"" className="limitWords-100"/]
          <div class="form-group">
            [#-- Top 3 Partners --]
            [@customForm.textArea name="projectLp6Contribution.topThreePartnershipsNarrative" i18nkey="projects.LP6Contribution.partnerships" className="limitWords-100" required=true editable=canEdit /]
          </div>
            [#-- Scaling CSA --]
            [@contributionForm name="undertakingEffortsCsa" textName="undertakingEffortsCsaNarrative" i18nkey="scalingCSA" checkedValue=(projectLp6Contribution.undertakingEffortsCsa?string)!"" className="limitWords-100"/]
            [#-- Climate finance --]
            [@contributionForm name="initiativeRelated" textName="initiativeRelatedNarrative" i18nkey="climateFinance" checkedValue=(projectLp6Contribution.initiativeRelated?string)!"" className="limitWords-100"/]
       </div>
       [#-- Section Buttons & hidden inputs--]
       [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]  
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
      <div class="col-md-3">
         [@customForm.radioFlat id="${i18nkey}-yes" name="projectLp6Contribution.${name}" label="Yes" value="true" checked=(checkedValue == "true") cssClass="input-yn" cssClassLabel="radio-label-yes" editable=canEdit /]
         [@customForm.radioFlat id="${i18nkey}-no" name="projectLp6Contribution.${name}" label="No" value="false" checked=(checkedValue == "false") cssClass="input-yn" cssClassLabel="radio-label-no" editable=canEdit /]
      </div>
    </div>
    [#-- Text --]
    <div class="form-group narrativeBlock" style="display:${((checkedValue == "true"))?string('block','none')}">
       [@customForm.textArea name="projectLp6Contribution.${textName}"  i18nkey="projects.LP6Contribution.${i18nkey}.question"  className="${className}" required=true editable=canEdit /]
    </div>
  </div>
[/#macro]
