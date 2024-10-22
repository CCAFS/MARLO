[#ftl]

[#macro innovationGeneral element name index=-1 template=false ]
        [#local isProgressActive = action.isProgressActive() ]
        [#--  Innovation Title --]
        <h3 class="headTitle">[@s.text name="projectInnovations" /]</h3> 
        
        <div class="borderBox generalInformationInnovations">    
          [#-- General Inputs --]
          <div class="form-group row">
      
            [#-- hr in elements --]
            <hr class="col-md-12 line-hr" />
          
            [#-- Innovation ID --]
            <div class="col-md-4">
              [@customForm.input name="${(innovationID)!}" i18nkey="projectInnovations.id" helpIcon=false required=false editable=false readOnly=true /]
            </div>
      
            [#-- Year --]        
            <div class="col-md-3">                   
              [@customForm.select name="innovation.projectInnovationInfo.year" className="setSelect2" i18nkey="policy.year" listName="getInnovationsYears(${innovationID})" header=false required=true editable=editable /]                        
            </div>                  
          </div>         
        </div>
        
        <div id="innovations" class="borderBox clearfix">   

        <div class="">        
          <div class="form-group row">
          [#--  
            <div class="col-md-4">
              [@customForm.select name="innovation.projectInnovationInfo.year" className="setSelect2" i18nkey="policy.year" listName="getInnovationsYears(${innovationID})" header=false required=true editable=editable /]
            </div>
            --]
            <div class="col-md-12">
              [#local guideSheetURL = "https://drive.google.com/file/d/1JvceA0bdvqS5Een056ctL7zJr3hidToe/view" /]
              <small class="pull-right"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" /> #C1 Innovations  -  Guideline </a> </small>
            </div>
          </div>
          <hr />
        
          [#-- Title --]
          <div class="form-group">
            [@customForm.input name="innovation.projectInnovationInfo.title" type="text" i18nkey="projectInnovations.title"  placeholder="" className="limitWords-30" help="projectInnovations.title.helpText" helpIcon=false required=true editable=editable /]
          </div>
          
          [#-- Short Title --]
          <div class="form-group">
            [@customForm.input name="innovation.projectInnovationInfo.shortTitle" type="text" i18nkey="projectInnovations.shortTitle"  placeholder="" className="limitWords-30" help="projectInnovations.shortTitle.helpText" helpIcon=false required=true editable=editable /]
          </div>
        
          [#-- Narrative --] 
          <div class="form-group">
            [@customForm.textArea name="innovation.projectInnovationInfo.narrative"  i18nkey="projectInnovations.narrative"  placeholder="" className="limitWords-80" help="" helpIcon=false required=false editable=editable /]         
          </div>
          <div class="note--2">
              <p>[@s.text name="projectInnovations.narrative.helpText" /]</p>
          </div>
          
          [#-- Phase of research and Stage of innovation --] 
          <div class="form-group row">
            <div class="col-md-6 ">
              [@customForm.select name="innovation.projectInnovationInfo.repIndStageInnovation.id" label=""  i18nkey="projectInnovations.stage" listName="stageInnovationList" keyFieldName="id"  displayFieldName="name"   required=!isProgressActive  className="stageInnovationSelect" editable=editable/]
              [#local isStageFour = (innovation.projectInnovationInfo.repIndStageInnovation.id == 4)!false]
            </div>
            <div class="col-md-6 ">
            </div>
          </div>
          
          [#-- Innovation nature --]
          <div class="form-group row">  
            <!-- Primera columna: Innovation Nature -->
            <div class="col-md-6">
              [@customForm.select name="innovation.projectInnovationInfo.repIndInnovationNature.id" label="" i18nkey="projectInnovations.innovationNature" listName="innovationNatureList" keyFieldName="id" displayFieldName="name" required=!isProgressActive className="innovationTypeSelect" editable=editable/]
              <div class="note--2">
                <p>[@s.text name="projectInnovations.innovationNature.helpText" /]</p>
              </div>
            </div>
          
            <!-- second column: Innovation Type -->
            <div class="col-md-6">
              [@customForm.select name="innovation.projectInnovationInfo.repIndInnovationType.id" label="" i18nkey="projectInnovations.innovationType" listName="innovationTypeList" keyFieldName="id" displayFieldName="name" required=!isProgressActive className="innovationTypeSelect" editable=editable/]
              <div class="note--2">
                <p>[@s.text name="projectInnovations.innovationType.helpText" /]</p>
              </div>
            </div>
          </div>
        
        
          [#-- Contribution of CRP --] 
          <div class="form-group row">
          
            [#-- Other Innovation Type --]
            [#local isTypeSix = (innovation.projectInnovationInfo.repIndInnovationType.id == 6)!false]
            <div class="col-md-6 ">
              <div class="form-group typeSixBlock" style="display:${isTypeSix?string('block','none')}">              
                [@customForm.input name="innovation.projectInnovationInfo.otherInnovationType"  type="text" i18nkey="projectInnovations.otherInnovation" helpIcon=false required=!isProgressActive editable=editable  /]
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

          [#-- 6.  Geographic scope - Countries
          <div class="form-group geographicScopeBlock">
            [#local geographicScopeList = (innovation.geographicScopes)![] ]
            [#local isRegional =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeRegional) /]
            [#local isMultiNational = findElementID(geographicScopeList,  action.reportingIndGeographicScopeMultiNational) /]
            [#local isNational =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeNational) /]
            [#local isSubNational =   findElementID(geographicScopeList,  action.reportingIndGeographicScopeSubNational) /]
            
            <div class="form-group">
              <div class="row">
                <div class="col-md-6">
                  [@customForm.elementsListComponent name="innovation.geographicScopes" elementType="repIndGeographicScope" elementList=innovation.geographicScopes maxLimit=1 label="projectInnovations.geographicScope" listName="geographicScopeList" keyFieldName="id" displayFieldName="name" required=!isProgressActive /]
                </div>
              </div>
              <div class="form-group regionalBlock" style="display:${(isRegional)?string('block','none')}">
                [@customForm.elementsListComponent name="innovation.regions" elementType="locElement" elementList=innovation.regions label="projectInnovations.region"  listName="regions" keyFieldName="id" displayFieldName="composedName" required=false /]
              </div>
              <div class="form-group nationalBlock" style="display:${(isMultiNational || isNational || isSubNational)?string('block','none')}">
                [@customForm.select name="innovation.countriesIds" label="" i18nkey="projectInnovations.countries" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="innovation.countriesIds" multiple=true required=!isProgressActive className="countriesSelect" disabled=!editable/]
              </div>
            </div>
          </div>
          --]
          [#--  6. Geographic scope - Countries  --]
          <div class="form-group geographicScopeBlock">
            [#local geographicScopeList = (element.geographicScopes)![] ]
            [#local isGlobal =        findElementID(geographicScopeList,  action.reportingIndGeographicScopeGlobal) /]
            [#local isRegional =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeRegional) /]
            [#local isMultiNational = findElementID(geographicScopeList,  action.reportingIndGeographicScopeMultiNational) /]
            [#local isNational =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeNational) /]
            [#local isSubNational =   findElementID(geographicScopeList,  action.reportingIndGeographicScopeSubNational) /]
            
            <label for="" class="label--2">[@s.text name="study.generalInformation.geographicScopeTopic" /]:[@customForm.req required=(editable && reportingActive) /]
              <div>
                  [@customForm.helpLabel name="study.generalInformation.geographicScopeTopic.note" showIcon=false isNote=true editable=editable/]
              </div>
            </label>
            <div class="form-group ('','simpleBox') geographicScopeInput">
              <div class="form-group row">
                <div class="col-md-12 margin-top-10">
                  [#local isDisplayTitleScope = ((isMultiNational || isNational || isSubNational || isRegional) || (isGlobal && (geographicScopeList.length >1)))!false /]
                  <label for="innovation.geographicScopes" class="col-md-4">[@s.text name="study.generalInformation.geographicScope" /]: [@customForm.req required=(editable && reportingActive) /] </label>
                  <label for="" name="study.generalInformation.geographicImpact" class="col-md-8" style="display:${isDisplayTitleScope?string('block','none')}">[@s.text name="study.generalInformation.geographicImpact" /]: [@customForm.req required=(editable && reportingActive) /]</label>
                </div>
              </div>
              <div class="form-group row">
                <div class="form-group col-md-4">
                  [#-- Geographic Scope --]
                  [@customForm.elementsListComponent name="innovation.geographicScopes" elementType="repIndGeographicScope" elementList=innovation.geographicScopes maxLimit=1 label="projectInnovations.geographicScope" listName="geographicScopeList" keyFieldName="id" displayFieldName="name" required=!isProgressActive /]
                </div>
                <div class="form-group nationalBlock col-md-4" style="display:${(isMultiNational || isNational || isSubNational)?string('block','none')}">
                  [#-- Multinational, National and Subnational scope --]
                  [@customForm.select name="innovation.countriesIds" label="" i18nkey="projectInnovations.countries" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="innovation.countriesIds" multiple=true required=!isProgressActive className="countriesSelect" disabled=!editable/]
                </div>
                <div class="form-group regionalBlock col-md-4" style="display:${(isRegional)?string('block','none')}">
                  [#-- Regional scope --]
                  [@customForm.elementsListComponent name="innovation.regions" elementType="locElement" elementList=innovation.regions label="projectInnovations.region"  listName="regions" keyFieldName="id" displayFieldName="composedName" required=false /]
                </div>
                [#--  
                <div class="form-group col-md-12">
                  [@customForm.textArea name="${customName}.projectExpectedStudyInfo.scopeComments" className="limitWords-30" i18nkey="study.generalInformation.geographicScopeComments" help="study.generalInformation.geographicScopeComments.help" helpIcon=false  editable=editable required=false/]
                </div>
                --]
              </div>
            </div>
          </div>
          
          [#-- Description of Stage reached--] 
          <div class="form-group">
            [@customForm.textArea name="innovation.projectInnovationInfo.descriptionStage" i18nkey="projectInnovations.stageDescription" help="projectInnovations.stageDescription.help" helpIcon=false placeholder="" className="limitWords-50" required=!isProgressActive editable=editable /]
          </div>
          
          [#-- Is clear lead  --]
          [#local isClearLead = (innovation.projectInnovationInfo.clearLead)!false /]

           <div class="form-group isClearLead">
            [@customForm.checkmark id="isClearLeadToAddRequired" name="clearLead" i18nkey="projectInnovations.clearLead" help="" paramText="" value="true" helpIcon=true disabled=false editable=editable checked=(innovation.projectInnovationInfo.clearLead)!false cssClass="isClearLead" cssClassLabel=""  /]
           </div>
          [#-- Lead Organization --]
          <div class="form-group lead-organization" style="display:${isClearLead?string('none','block')}">
            [@customForm.select name="innovation.projectInnovationInfo.leadOrganization.id" label=""  i18nkey="projectInnovations.leadOrganization" listName="institutions" keyFieldName="id"  displayFieldName="composedName" className="" editable=editable required=!isProgressActive /]
          </div>
          
          [#-- Top Five Contributing Organizations --]
          <div class="form-group top-five-contributing">
            [@customForm.elementsListComponent name="innovation.contributingOrganizations" i18nkey="innovation.contributingOrganizations" maxLimit=5 elementType="institution" elementList=innovation.contributingOrganizations label="projectInnovations.contributingOrganizations"  listName="institutions" keyFieldName="id" displayFieldName="composedName" /]
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
            [@customForm.elementsListComponent name="innovation.studies" elementType="projectExpectedStudy" elementList=innovation.studies label="projectInnovations.outcomeCaseStudy" helpIcon=false listName="expectedStudyList" keyFieldName="id" displayFieldName="composedNameAlternative" required=(isEvidenceRequired!false && !isProgressActive)/]
          </div>
                
          [#-- Evidence Link --] 
          <div class="form-group stageFourBlock-false" style="display:${isStageFour?string('none','block')}">
            [@customForm.input name="innovation.projectInnovationInfo.evidenceLink"  type="text" i18nkey="projectInnovations.evidenceLink"  placeholder="marloRequestCreation.webSiteLink.placeholder" className="" required=!isProgressActive editable=editable /]
          </div>
        
          [#-- Or Deliverable ID (optional) --]
          <div class="form-group">
            [@customForm.elementsListComponent name="innovation.deliverables" elementType="deliverable" elementList=innovation.deliverables label="projectInnovations.deliverableId"  listName="deliverableList" required=false keyFieldName="id" displayFieldName="tagTitle"/]
          </div>
          
         [#-- Milestones Contribution --]
        <div class="form-group">    
            <label for="">[@s.text name="innovation.outcomes" /]:[@customForm.req required=(editable && !isProgressActive) /]
              <div class="feedback-flex-items">
                [@customForm.helpLabel name="innovation.outcomes.help" showIcon=false editable=editable/]
              </div> 
            </label>
            [#local innovationMilestoneLink = "innovationMilestoneLink"]
            [#local showMilestoneIndicator = (innovation.projectInnovationInfo.hasMilestones?string)!"" /]
            [@customForm.radioFlat id="${innovationMilestoneLink}-yes" name="innovation.projectInnovationInfo.hasMilestones" label="Yes" value="true" checked=(showMilestoneIndicator == "true") cssClass="radioType-${innovationMilestoneLink}" cssClassLabel="radio-label-yes" editable=editable /]
            [@customForm.radioFlat id="${innovationMilestoneLink}-no" name="innovation.projectInnovationInfo.hasMilestones" label="No" value="false" checked=(showMilestoneIndicator == "false") cssClass="radioType-${innovationMilestoneLink}" cssClassLabel="radio-label-no" editable=editable /]
        </div> 
        <div class="note left">
          <span class="glyphicon glyphicon-question-sign"></span>
          [@s.text name="project.deliverable.generalInformation.keyOutputNotice2"][@s.param] <a href="[@s.url namespace=namespace action="${crpSession}/contributionsCrpList"][@s.param name='projectID']${projectID?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">&nbsp;clicking here</a>[/@] [/@]  
        </div>
         
        [#--    
        <div class="form-group simpleBox block-${innovationMilestoneLink}" style="display:${(showMilestoneIndicator == "true")?string('block','none')}">
          [@customForm.elementsListComponent name="innovation.milestones" elementType="crpMilestone" elementList=(innovation.milestones)![] label="innovation.milestones" helpIcon=false listName="milestones" keyFieldName="id" displayFieldName="composedName" required=false hasPrimary=true /]
          [#-- [@customForm.primaryListComponent name="innovation.milestones" checkName="milestonePrimaryId" elementType="crpMilestone" elementList=(innovation.milestones)!"" label="innovation.milestones" labelPrimary="policy.primaryMilestone" helpIcon=false listName="milestones" keyFieldName="id" displayFieldName="composedName" required=false /]
         <div class="note">[@s.text name="innovation.milestones.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/contributionsCrpList'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">clicking here</a>[/@][/@]</div>
         <br/>
        </div> 
        --] 
        
        [#--  
        <div class="form-group simpleBox block-${innovationMilestoneLink}" style="display:${(showMilestoneIndicator == "true")?string('block','none')}">
          [@customForm.elementsListComponent name="innovation.projectOutcomes" elementType="projectOutcome" elementList=(innovation.projectOutcomes)![] label="innovation.outcomes" helpIcon=false listName="projectOutcomes" keyFieldName="id" displayFieldName="composedName" required=false /]
          [@customForm.primaryListComponent name="innovation.milestones" checkName="milestonePrimaryId" elementType="crpMilestone" elementList=(innovation.milestones)!"" label="innovation.milestones" labelPrimary="policy.primaryMilestone" helpIcon=false listName="milestones" keyFieldName="id" displayFieldName="composedName" required=false /]
         <div class="note">[@s.text name="innovation.outcomes.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/contributionsCrpList'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">clicking here</a>[/@][/@]</div>
         <br/>
        </div> 
        --]
        
        
        <div class="form-group simpleBox block-${innovationMilestoneLink}" style="display:${(showMilestoneIndicator == "true")?string('block','none')}">
          [@customForm.elementsListComponent name="innovation.crpOutcomes" elementType="crpOutcome" elementList=(innovation.crpOutcomes)![] label="innovation.outcomes" helpIcon=false listName="crpOutcomes" keyFieldName="id" displayFieldName="composedName" required=!isProgressActive /]
         <div class="note">[@s.text name="innovation.outcomes.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/contributionsCrpList'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">clicking here</a>[/@][/@]</div>
         <br/>
        </div> 
        
          [#-- Contributing CRPs/Platforms --]
         [#if !action.isAiccra()]
          <div class="form-group">
            [@customForm.elementsListComponent name="innovation.crps" elementType="globalUnit" elementList=innovation.crps label="projectInnovations.contributing"  listName="crpList" keyFieldName="id" displayFieldName="composedName" required=false /]
          </div>
         [/#if]
          
          [#-- Contributing Centers/ PPA partners  --]
          <div class="form-group">
            [@customForm.elementsListComponent name="innovation.centers" i18nkey="innovation.centers" elementType="institution" elementList=innovation.centers label="projectInnovations.contributingCenters"  listName="centers" keyFieldName="id" displayFieldName="composedName" required=!isProgressActive /]
            <div class="note"><span class="glyphicon glyphicon-question-sign"></span> [@s.text name="study.generalInformation.ppapartner.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/partners'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">clicking here</a>[/@][/@]</div>
          </div>

        [#if !action.isAiccra()]
        [#-- Sub IDOs (maxLimit=3 -Requested for AR2019) --]      
        <div class="form-group simpleBox">
          [@customForm.elementsListComponent name="innovation.subIdos" elementType="srfSubIdo" elementList=(innovation.subIdos)![] label="innovation.subIDOs" helpIcon=false listName="subIdos" maxLimit=3 keyFieldName="id" displayFieldName="description" required=!isProgressActive hasPrimary=true/]
         [#--  <div class="buttonSubIdo-content"><br> <div class="selectSubIDO" ><span class=""></span>View sub-IDOs</div></div> --]
          [#-- [@customForm.primaryListComponent name="innovation.subIdos" checkName="subIdoPrimaryId" elementType="srfSubIdo" elementList=(innovation.subIdos)!"" label="innovation.subIDOs" labelPrimary="policy.primarySubIdo" listName="subIdos" maxLimit=3 keyFieldName="id" displayFieldName="description" required=false /]
          --]
        </div> 
        [/#if]
        </div>
        
         
      </div>
        
      [#-- Projects shared --]
      <h3 class="headTitle">[@s.text name="projectInnovations.sharedProjects.title" /]</h3>
      <div class="borderBox">
        [@customForm.elementsListComponent name="innovation.sharedInnovations" elementType="project" elementList=(innovation.sharedInnovations)![] label="projectInnovations.sharedProjects"  listName="myProjects" keyFieldName="id" displayFieldName="composedName" required=false /]
      </div>
[/#macro]

[#function findElementID list id]
  [#list (list)![] as item]
    [#if (item.repIndGeographicScope.id == id)!false][#return true][/#if]
  [/#list]
  [#return false]
[/#function]

[#macro innovationAlliance element name index=-1 template=false ]
[/#macro]