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
            [@customForm.textArea name="narrativeLP6Contribution" i18nkey="projects.LP6Contribution.narrativeContribution"  i18nkey="projects.LP6Contribution.narrativeContribution"  className="limitWords-100" required=true editable=canEdit /]
            [#-- Deliverables --]
             <div class="form-group simpleBox stageProcessOne">
               [@customForm.elementsListComponent name="projects.LP6Contribution.evidenceDeliverables" elementType="locElement" elementList="" label="projects.LP6Contribution.evidenceDeliverables" listName="deliverables" keyFieldName="id" displayFieldName="composedName" required=editable/]
             </div>
             <p class="note">[@s.text name="projects.LP6Contribution.deliverablesTooltip" /] <a href="">clicking here.</a></p>
          </div>

            [#-- Geographic Scope --]
            <div class="form-group geographicScopeBlock">        
              [#assign isRegional = true ]
              [#assign isMultiNational = true ]
              [#assign isNational = false ]
              [#assign isSubNational = false ]
              
              <div class="form-group simpleBox">
                <div class="form-group row">
                  <div class="col-md-6">
                    [#-- Geographic Scope --]
                    [@customForm.select name="projects.LP6Contribution.geographicScope" className="setSelect2 geographicScopeSelect" i18nkey="projects.LP6Contribution.geographicScope" listName="repIndGeographicScopes" keyFieldName="id"  displayFieldName="name" editable=editable/]
                  </div>
                </div>
                <div class="form-group regionalBlock" style="display:${(isRegional)?string('block','none')}">
                  [#-- Regional scope --]
                    [@customForm.elementsListComponent name="projects.LP6Contribution.region" elementType="locElement" id="region" elementList=(projects.LP6Contribution.regions)![] label="projects.LP6Contribution.region"  listName="repIndRegions" keyFieldName="id" displayFieldName="name" required=false /]
                </div>
                <div class="form-group nationalBlock" style="display:${(isMultiNational || isNational || isSubNational)?string('block','none')}">
                  [#-- Multinational, National and Subnational scope --]
                  [@customForm.select name="projects.LP6Contribution.countries" label="" i18nkey="projects.LP6Contribution.countries" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="" multiple=true className="countriesSelect" disabled=!editable/]
                </div>
              </div>
            </div>

            [#-- Work across flagships --]
            [@contributionForm name="isWorkingAcrossFlagships" textName="workingAcrossFlagshipsNarrative" i18nkey="flagshipLevels" className="limitWords-100"/]
            [#-- Efforts to position CGIAR --]
            [@contributionForm name="isUndertakingEffortsLeading" textName="undertakingEffortsLeadingNarrative" i18nkey="positionCGIAR" className="limitWords-100"/]
            [#-- Innovative Pathways / Tools --]
            [@contributionForm name="isProvidingPathways" textName="providingPathwaysNarative" i18nkey="innovativePathways" className="limitWords-100"/]
          <div class="form-group">
            [#-- Top 3 Partners --]
            [@customForm.textArea name="top3Partnerships" i18nkey="projects.LP6Contribution.partnerships"  i18nkey="projects.LP6Contribution.partnerships"  className="limitWords-100" required=true editable=canEdit /]
          </div>
            [#-- Scaling CSA --]
            [@contributionForm name="isUndertakingEffortsCSA" textName="undertakingEffortsCSANarrative" i18nkey="scalingCSA" className="limitWords-100"/]
            [#-- Climate finance --]
            [@contributionForm name="isInitiativeRelated" textName="initiativeRelatedNarrative" i18nkey="climateFinance" className="limitWords-100"/]
       </div>
       [#-- Section Buttons & hidden inputs--]
       [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]  
       [/@s.form]
 </section>

[#include "/WEB-INF/global/pages/footer.ftl"]


[#macro contributionForm name textName i18nkey className]
[#local questionName = "${name}" /]
[#local textName = "${textName}"]
[#local customName = "${i18nkey}" /]
[#local className = "${className}" /]
  
  <div class="form-group row">
    [#-- Climate finance --]
    <div class="col-md-9"> 
       <label>[@s.text name="projects.LP6Contribution.${customName}"/][@customForm.req required=true /]</label>
    </div>
    <div class="col-md-3">
       [@customForm.radioFlat id="${customName}-yes" name="${questionName}" label="Yes" value="true" checked=false cssClassLabel="radio-label-yes" editable=canEdit /]
       [@customForm.radioFlat id="${customName}-no" name="${questionName}" label="No" value="false" checked=false cssClassLabel="radio-label-no" editable=canEdit /]
    </div>
  </div>
  <div class=form-group">
     [@customForm.textArea name="${textName}"  i18nkey="projects.LP6Contribution.${customName}.question"  className="${className}" required=true editable=canEdit /]
  </div>
  
[/#macro]
