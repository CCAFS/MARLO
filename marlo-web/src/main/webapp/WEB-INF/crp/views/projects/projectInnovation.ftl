[#ftl]
[#assign title = "Innovations" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${innovationID}-phase-${(actualPhase.id)!}" /]
[#-- TODO: Remove unused pageLibs--]
[#assign pageLibs = ["select2","font-awesome", "flag-icon-css"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectInnovations.js?20211126A",
  "${baseUrlCdn}/global/js/autoSave.js", 
  "${baseUrlCdn}/global/js/fieldsValidation.js"
] /]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/projectInnovations.css?20211025a",
  "${baseUrlCdn}/global/css/global.css?20211126a"
  ] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "innovations" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"text":"P${project.id}", "nameSpace":"/projects", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
  {"label":"innovationsList", "nameSpace":"/projects", "action":"${(crpSession)!}/innovationsList" ,"param":"projectID=${projectID}"},
  {"label":"innovationInformation", "nameSpace":"/projects", "action":""}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#import "/WEB-INF/global/macros/studiesTemplates.ftl" as studies /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="projectInnovations.help" /]

<section class="container">
  <div class="row">
    [#-- Project Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
    </div>
    [#-- Project Section Content --]
    <div class="col-md-9">
      [#-- Section Messages --]
      [#include "/WEB-INF/crp/views/projects/messages-innovation.ftl" /]
        
      [@s.form action=actionName cssClass="pure-form" enctype="multipart/form-data" ]
      
      <span id="actualCrpID" style="display: none;">${(action.getCurrentCrp().id)!-1}</span>
      <span id="actualPhase" style="display: none;">${(action.isSelectedPhaseAR2021())?c}</span>
      <span id="innovationID" style="display: none;">${(innovation.id)!-1}</span>
      <span id="isSubmitted" style="display: none;">${submission?c}</span>
      [#assign actualPhaseAR2021 = action.isSelectedPhaseAR2021()!false]
      [#assign isQAIncluded = action.isIndicatorIncludedInQA("innovation", (innovation.id)!-1, (action.getActualPhase().id)!-1)!false]

      [#if actualPhaseAR2021 && isQAIncluded]
        <div class="containerTitleElementsProject">
          <div class="containerTitleMessage">
            <div id="qualityAssessedIcon" class="pendingForReview-mode text-center animated flipInX" style="height: auto;">
              <p>
                [@s.text name="annualReport2018.policies.table2.pendingForReview"][/@s.text]
              </p>
            </div> 
          </div>
        </div>
      [/#if]

        [#-- Back --]
        <small class="pull-right">
          <a href="[@s.url action='${crpSession}/innovationsList'][@s.param name="projectID" value=project.id /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
            <span class="glyphicon glyphicon-circle-arrow-left"></span> [@s.text name="projectInnovations.back" /]
          </a>
        </small>
        
        [#--  Innovation Title --]
        <h3 class="headTitle">[@s.text name="projectInnovations" /]</h3> 

        <div id="innovations" class="borderBox clearfix">   

        <div class="">        
          <div class="form-group row">
            <div class="col-md-4">
              [@customForm.select name="innovation.projectInnovationInfo.year" className="setSelect2" i18nkey="policy.year" listName="getInnovationsYears(${innovationID})" header=false required=true editable=editable /]
              
            </div>
            <div class="col-md-8">
              [#assign guideSheetURL = "https://docs.google.com/document/d/1jSkicfXhtLJqwjzinPIn9BFPWnr7mcfm/edit" /]
              <small class="pull-right"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" /> #C1 Innovations  -  Guideline </a> </small>
            </div>
          </div>
          <hr />
        
          [#-- Title --]
          <div class="form-group">
            [@customForm.input name="innovation.projectInnovationInfo.title" type="text" i18nkey="projectInnovations.title"  placeholder="" className="limitWords-30" help="projectInnovations.title.helpText" helpIcon=false required=true editable=editable /]
          </div>
        
          [#-- Narrative --] 
          <div class="form-group">
            [@customForm.textArea name="innovation.projectInnovationInfo.narrative"  i18nkey="projectInnovations.narrative"  placeholder="" className="limitWords-75" help="projectInnovations.narrative.helpText" helpIcon=false required=false editable=editable /]
          </div>
        
          [#-- Phase of research and Stage of innovation --] 
          <div class="form-group row">
            <div class="col-md-6 ">
              [@customForm.select name="innovation.projectInnovationInfo.repIndStageInnovation.id" label=""  i18nkey="projectInnovations.stage" listName="stageInnovationList" keyFieldName="id"  displayFieldName="name"   required=true  className="stageInnovationSelect" editable=editable/]
              [#assign isStageFour = (innovation.projectInnovationInfo.repIndStageInnovation.id == 4)!false]
            </div>
            [#assign isGenetic = ((innovation.projectInnovationInfo.repIndInnovationType.id == 1))!false ]
            <div class="col-md-6">
              <div class="form-group numberInnovations-block" style="display:${isGenetic?string('block','none')}">
                  [@customForm.input name="innovation.projectInnovationInfo.innovationNumber" type="number" i18nkey="projectInnovations.innovationNumber" editable=editable /]   
                  <i class="helpLabel" style="position: absolute; text-align: justify; margin-right: 15px;">[@s.text name="projectInnovations.innovationNumber.help" /]</i>
                  <span id="oldInnovationNumber" style="display: none;">${innovation.projectInnovationInfo.innovationNumber!"-1"}</span>
              </div>
            </div>
          </div>
          

          <div class="form-group row">  
            <div class="col-md-6 ">
              [@customForm.select name="innovation.projectInnovationInfo.repIndInnovationType.id" label="" i18nkey="projectInnovations.innovationType" listName="innovationTypeList" keyFieldName="id"  displayFieldName="name" required=true  className="innovationTypeSelect" editable=editable/]
            </div>
          </div>
        
        
          [#-- Contribution of CRP --] 
          <div class="form-group row">
          
            [#-- Other Innovation Type --]
            [#assign isTypeSix = (innovation.projectInnovationInfo.repIndInnovationType.id == 6)!false]
            <div class="col-md-6 ">
              <div class="form-group typeSixBlock" style="display:${isTypeSix?string('block','none')}">              
                [@customForm.input name="innovation.projectInnovationInfo.otherInnovationType"  type="text" i18nkey="projectInnovations.otherInnovation" helpIcon=false required=true editable=editable  /]
              </div>
            </div>
          </div> 
          
          
          [#-- Degree of Innovation --] 
          [#--  <div class="form-group row">
            <div class="col-md-6 ">
              [@customForm.select name="innovation.projectInnovationInfo.repIndDegreeInnovation.id" label=""  i18nkey="projectInnovations.degreeInnovation" listName="degreeInnovationList" keyFieldName="id"  displayFieldName="name" required=true  className="" editable=editable/]
            </div>
          </div>--]
          
          [#-- Specify next user organizational type (Only if stage 4) --]
          <div class="form-group stageFourBlock-true" style="display:${isStageFour?string('block','none')}">
            [@customForm.elementsListComponent name="innovation.organizations" elementType="repIndOrganizationType" elementList=innovation.organizations label="projectInnovations.nextUserOrganizationalType"  listName="organizationTypeList" keyFieldName="id" displayFieldName="name"/]
          </div>

          [#-- 6.  Geographic scope - Countries  --]
          <div class="form-group geographicScopeBlock">
            [#assign geographicScopeList = (innovation.geographicScopes)![] ]
            [#assign isRegional =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeRegional) /]
            [#assign isMultiNational = findElementID(geographicScopeList,  action.reportingIndGeographicScopeMultiNational) /]
            [#assign isNational =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeNational) /]
            [#assign isSubNational =   findElementID(geographicScopeList,  action.reportingIndGeographicScopeSubNational) /]
            
            <div class="form-group">
              <div class="row">
                <div class="col-md-6">
                  [#-- Geographic Scope --]
                  [@customForm.elementsListComponent name="innovation.geographicScopes" elementType="repIndGeographicScope" elementList=innovation.geographicScopes  label="projectInnovations.geographicScope" listName="geographicScopeList" keyFieldName="id" displayFieldName="name" required=true /]
                </div>
              </div>
              <div class="form-group regionalBlock" style="display:${(isRegional)?string('block','none')}">
                [#-- Regional scope --]
                [@customForm.elementsListComponent name="innovation.regions" elementType="locElement" elementList=innovation.regions label="projectInnovations.region"  listName="regions" keyFieldName="id" displayFieldName="composedName" required=false /]
              </div>
              <div class="form-group nationalBlock" style="display:${(isMultiNational || isNational || isSubNational)?string('block','none')}">
                [#-- Multinational, National and Subnational scope --]
                [@customForm.select name="innovation.countriesIds" label="" i18nkey="projectInnovations.countries" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="innovation.countriesIds" multiple=true required=true className="countriesSelect" disabled=!editable/]
              </div>
            </div>
          </div>
          
          [#-- Description of Stage reached--] 
          <div class="form-group">
            [@customForm.textArea name="innovation.projectInnovationInfo.descriptionStage" i18nkey="projectInnovations.stageDescription" help="projectInnovations.stageDescription.help" helpIcon=false placeholder="" className="limitWords-50" required=true editable=editable /]
          </div>
          
          [#-- Is clear lead  --]
          [#assign isClearLead = (innovation.projectInnovationInfo.clearLead)!false /]

           <div class="form-group isClearLead">
            [@customForm.checkmark id="isClearLeadToAddRequired" name="clearLead" i18nkey="projectInnovations.clearLead" help="" paramText="" value="true" helpIcon=true disabled=false editable=editable checked=(innovation.projectInnovationInfo.clearLead)!false cssClass="isClearLead" cssClassLabel=""  /]
           </div>
          [#-- Lead Organization --]
          <div class="form-group lead-organization" style="display:${isClearLead?string('none','block')}">
            [@customForm.select name="innovation.projectInnovationInfo.leadOrganization.id" label=""  i18nkey="projectInnovations.leadOrganization" listName="institutions" keyFieldName="id"  displayFieldName="composedName" className="" editable=editable required=true /]
          </div>
          
          [#-- Top Five Contributing Organizations --]
          <div class="form-group top-five-contributing">
            [@customForm.elementsListComponent name="innovation.contributingOrganizations" i18nkey="innovation.contributingOrganizations" maxLimit=5 elementType="institution" elementList=innovation.contributingOrganizations label="projectInnovations.contributingOrganizations" help="projectInnovations.contributingOrganizations.help" listName="institutions" keyFieldName="id" displayFieldName="composedName" /]
          </div>
          
          [#-- Novel or Adaptive research --]
          [#--<div class="form-group">
            [@customForm.textArea name="innovation.projectInnovationInfo.adaptativeResearchNarrative" i18nkey="projectInnovations.novelOrAdaptative" placeholder="" className="" required=false editable=editable /]
          </div>--]
          
          [#-- Request partner adition --]
          [#if editable]
          <p id="addPartnerText" class="helpMessage">
            If you cannot find the organization you are looking for, please 
            <a class="popup" href="[@s.url action='${crpSession}/partnerSave' namespace="/projects"][@s.param name='projectID']${(projectID)!}[/@s.param][@s.param name='context'](${(actionName)!}: ID-${(innovation.id)!})[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              click here to [@s.text name="projectPartners.addPartnerMessage.second" /]
            </a>
          </p>
          <br />
          [/#if]
        
          [#-- Specify an Outcome Case Study (Only if stage 4) --]
          <div class="form-group stageFourBlock-true" style="display:${isStageFour?string('block','none')}">
            [#--  
            [@customForm.select name="innovation.projectInnovationInfo.projectExpectedStudy.id" label=""  i18nkey="projectInnovations.outcomeCaseStudy" listName="expectedStudyList" keyFieldName="id"  displayFieldName="composedNameAlternative"  multiple=false required=true  className="keyOutput" editable=editable/]
            --]
            [@customForm.elementsListComponent name="innovation.studies" elementType="projectExpectedStudy" elementList=innovation.studies label="projectInnovations.outcomeCaseStudy" helpIcon=false listName="expectedStudyList" keyFieldName="id" displayFieldName="composedNameAlternative" required=isEvidenceRequired/]
          </div>
          
          <label for="">[@customForm.helpLabel name="projectInnovations.mandatoryFields.help" showIcon=false editable=editable/]</label>
          <div class="form-group simpleBox">   

            [#-- Evidence Link --] 
            <div class="form-group stageFourBlock-false" style="display:${isStageFour?string('none','block')}">
              <span id="warningEmptyLinksTag" class="errorTag glyphicon glyphicon-info-sign" style="left: 750px;" title="" aria-describedby="ui-id-5"> </span>
              [@customForm.input name="innovation.projectInnovationInfo.evidenceLink"  type="text" i18nkey="projectInnovations.evidenceLink" help="projectInnovations.evidenceLink.help2"  placeholder="marloRequestCreation.webSiteLink.placeholder2" className="" required=true editable=editable helpIcon=false /]
              <label for="">[@customForm.helpLabel name="projectInnovations.evidenceLink.help" showIcon=false editable=editable/]</label>
              <div class="linksBlock ">
                <div class="linksList">
                  [#list (innovation.innovationLinks)![{}] as link]
                    [@customForm.multiInput name="innovation.innovationLinks" element=link index=link_index class="links" placeholder="global.webSiteLink.placeholder" /]
                  [/#list]
                </div>
                [#if editable]
                <div class="addButtonLink button-green pull-right"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> Add Link </div>
                <div class="clearfix"></div>
                [/#if]
              </div>
               [#-- Element item Template --]
              <div style="display:none">
                [@customForm.multiInput name="innovation.innovationLinks" element={} index=-1 template=true class="links" placeholder="global.webSiteLink.placeholder" /]
              </div>
            </div>
          
            [#-- Or Deliverable ID (optional) --]
            <div class="form-group">
              [@customForm.elementsListComponent name="innovation.deliverables" elementType="deliverable" elementList=innovation.deliverables label="projectInnovations.deliverableId"  listName="deliverableList" required=false keyFieldName="id" displayFieldName="tagTitle"/]
            </div>
          </div>
         <br>
          
         [#-- Milestones Contribution --]
        <div class="form-group">          
          <label for="">[@s.text name="innovation.milestones" /]:[@customForm.req required=editable /][@customForm.helpLabel name="innovation.milestones.help" showIcon=false editable=editable/]</label>
          [#assign innovationMilestoneLink = "innovationMilestoneLink"]
          [#assign showMilestoneIndicator = (innovation.projectInnovationInfo.hasMilestones)!false /]
          [@customForm.radioFlat id="${innovationMilestoneLink}-yes" name="innovation.projectInnovationInfo.hasMilestones" label="Yes" value="true" checked=(showMilestoneIndicator == true) cssClass="radioType-${innovationMilestoneLink}" cssClassLabel="radio-label-yes" editable=editable /]
          [@customForm.radioFlat id="${innovationMilestoneLink}-no" name="innovation.projectInnovationInfo.hasMilestones" label="No" value="false" checked=(showMilestoneIndicator == false) cssClass="radioType-${innovationMilestoneLink}" cssClassLabel="radio-label-no" editable=editable /]
        </div> 
        [#assign isAR2021 = !(action.isSelectedPhaseAR2021())]
        <div class="form-group simpleBox block-${innovationMilestoneLink}" style="display:${(showMilestoneIndicator == true)?string('block','none')}">
          [@customForm.elementsListComponent name="innovation.milestones" elementType="crpMilestone" elementList=(innovation.milestones)![] label="innovation.milestones" helpIcon=false listName="milestones" keyFieldName="id" displayFieldName="composedName" required=isAR2021 hasPrimary=true /]
          [#-- [@customForm.primaryListComponent name="innovation.milestones" checkName="milestonePrimaryId" elementType="crpMilestone" elementList=(innovation.milestones)!"" label="innovation.milestones" labelPrimary="policy.primaryMilestone" helpIcon=false listName="milestones" keyFieldName="id" displayFieldName="composedName" required=false /] --]
         <div class="note">[@s.text name="innovation.milestones.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/contributionsCrpList'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">clicking here</a>[/@][/@]</div>
         <br/>
        </div> 
        <span id="actualCRP" style="display: none;">${action.getLoggedCrp().acronym}</span>
          [#-- Contributing CRPs/Platforms --]
          <div class="form-group innovationContributingCRP">
            [@customForm.elementsListComponent name="innovation.crps" elementType="globalUnit" elementList=innovation.crps label="projectInnovations.contributing"  listName="crpList" keyFieldName="id" displayFieldName="composedName" required=false /]
          </div>
          
          [#-- Contributing Centers/ PPA partners  --]
          <div class="form-group">
            [@customForm.elementsListComponent name="innovation.centers" i18nkey="innovation.centers" elementType="institution" elementList=innovation.centers label="projectInnovations.contributingCenters"  listName="centers" keyFieldName="id" displayFieldName="composedName" /]
            <div class="note">[@s.text name="innovation.ppapartner.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/partners'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">clicking here</a>[/@][/@]</div>
          </div>

        
        [#-- Sub IDOs (maxLimit=3 -Requested for AR2019) --]      
        <div class="form-group simpleBox">
          [@customForm.elementsListComponent name="innovation.subIdos" elementType="srfSubIdo" elementList=(innovation.subIdos)![] label="innovation.subIDOs" helpIcon=false listName="subIdos" maxLimit=3 keyFieldName="id" displayFieldName="composedName" required=true hasPrimary=true/]
         [#--  <div class="buttonSubIdo-content"><br> <div class="selectSubIDO" ><span class=""></span>View sub-IDOs</div></div> --]
          [#-- [@customForm.primaryListComponent name="innovation.subIdos" checkName="subIdoPrimaryId" elementType="srfSubIdo" elementList=(innovation.subIdos)!"" label="innovation.subIDOs" labelPrimary="policy.primarySubIdo" listName="subIdos" maxLimit=3 keyFieldName="id" displayFieldName="description" required=false /]
          --]
        </div> 
       
        </div>
        
         
      </div>
        
      [#-- Projects shared --]
      <h3 class="headTitle">[@s.text name="projectInnovations.sharedProjects.title" /]</h3>
      <div class="borderBox">
        [@customForm.elementsListComponent name="innovation.sharedInnovations" elementType="project" elementList=(innovation.sharedInnovations)![] label="projectInnovations.sharedProjects"  listName="myProjects" keyFieldName="id" displayFieldName="composedName" required=false /]
      </div>

      
      [#-- Section Buttons & hidden inputs--]
      [#include "/WEB-INF/crp/views/projects/buttons-innovation.ftl" /]
        
      [/@s.form] 
  </div>  
</section>

[#include "/WEB-INF/global/pages/footer.ftl"]

[#function findElementID list id]
  [#list (list)![] as item]
    [#if (item.repIndGeographicScope.id == id)!false][#return true][/#if]
  [/#list]
  [#return false]
[/#function]