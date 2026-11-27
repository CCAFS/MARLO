[#ftl]
[#import "/WEB-INF/global/macros/deliverableMacros.ftl" as deliverableMacros /]

[#macro studyMacro element name index=-1 template=false fromProject=true scoreInHeader=false]
  [#local customName = "${name}"/]
  [#local customId = "study-${template?string('template',index)}" /]
  [#local isOutcomeCaseStudy = ((element.projectExpectedStudyInfo.studyType.id == 1)!false)/]
  [#local isNew = (action.isEvidenceNew(element.id))!false /]
  
  [#local isPolicy = ((element.projectExpectedStudyInfo.isContribution)!false) ]
  [#local stageProcessOne = ((element.projectExpectedStudyInfo.repIndStageProcess.id == 1))!false ]
  [#local isStatusExtended = (element.projectExpectedStudyInfo.status.id == 4)!false]
  [#local isOtherStatus = (element.projectExpectedStudyInfo.status.id != 4)!false]

  <span id="parentID" style="display: none;">${expectedID!}</span>
  <span id="phaseID" style="display: none;">${phaseID!}</span>
  <span id="userID" style="display: none;">${currentUser.id!}</span>
  <span id="projectID" style="display: none;">${projectID!}</span>
  <span id="userCanManageFeedback" style="display: none;">${(action.canManageFeedback(projectID)?c)!}</span>
  <span id="userCanLeaveComments" style="display: none;">${(action.canLeaveComments(projectID)?c)!}</span>
  <span id="userCanApproveFeedback" style="display: none;">${(action.canApproveComments(projectID)?c)!"false"}</span>
  <span id="canTrackComments" style="display: none;">${(action.canTrackComments()?c)!"false"}</span>
  <span id="isFeedbackActive" style="display: none;">${(action.hasSpecificities('feedback_active')?c)!}</span>
  <span id="isFeedbackNewCommentFieldActive" style="display: none;">${(action.hasSpecificities('feedback_new_comment_field_active')?c)!"false"}</span>
  <span id="isSuperAdmin" style="display: none;">${(action.canAccessSuperAdmin()?c)!}</span>
  <input type="hidden" id="sectionNameToFeedback" value="study" />


  
  <div id="${customId}" class="caseStudy evidenceBlock isNew-${isNew?string}" style="display:${template?string('none','block')}">

    [#if ((element.projectExpectedStudyInfo.status)?has_content) && ((element.projectExpectedStudyInfo.status.id)?has_content)]
      [#assign validateIsProgressWithStatus = action.validateIsProgressWithStatus(element.projectExpectedStudyInfo.status.id) /]
    [#else]
      [#assign validateIsProgressWithStatus = true /]
    [/#if]



  	
    [#-- General Information: General Information Manage for all studies --]
    <div class="borderBox">
      [#-- 0. Link to PDF version of this study: AR 2020 and onwards -> ALL OICRs are ALWAYS public--]
        [#if !isOutcomeCaseStudy]
          <div class="form-group">
            <div class="optionPublicComponent form-group" style="display:block">         
              <br />
              <div class="input-group">
                <span class="input-group-btn">
                  <button class="btn btn-default btn-sm copyButton" type="button"> <span class="glyphicon glyphicon-duplicate"></span> Copy URL </button>
                </span>
                [#local summaryPDF = "${baseUrl}/projects/${crpSession}/studySummary.do?studyID=${(element.id)!}&cycle=Reporting&year=${(actualPhase.year)!}"]
                [@customForm.input name="${customName}.projectExpectedStudyInfo.link" i18nkey="study.general.link" className="form-control input-sm urlInput" value="${summaryPDF}" editable=editable readOnly=true/]
                <!--input type="text" class="form-control input-sm urlInput" value="${summaryPDF}" readonly-->
              </div>
              <div class="message text-center" style="display:none">Copied!</div>
            </div>
          </div>
        [/#if]
        
      
      [#-- OICR Score --]
      [#-- A2-2743: the redesigned OICR form draws the score in its header (scoreInHeader) --]
      [#if !scoreInHeader && (action.canAccessSuperAdmin() || action.isPMU()) && action.hasSpecificities('oicr_score_field_active')]
        <div class="form-group row">
          <div class="col-md-2 mb-4">
            [@customForm.input name="${customName}.projectExpectedStudyInfo.score" i18nkey="study.general.score" type="number" helpIcon=false required=false editable=editable isMainTitle=true /]
          </div>
        </div>
      [/#if]

      [#-- 1. Title (up to 25 words) --]
      <div class="form-group">
        [@customForm.input name="${customName}.projectExpectedStudyInfo.title" i18nkey="study.generalInformation.title" help="study.generalInformation.title.help" className="limitWords-15" helpIcon=!isOutcomeCaseStudy required=true editable=editable isMainTitle=isOutcomeCaseStudy /]
      </div>
      
      [#-- Who is commissioning this study --]
      [#if !isOutcomeCaseStudy]
      <div class="form-group">
        [@customForm.input name="${customName}.projectExpectedStudyInfo.commissioningStudy" i18nkey="study.generalInformation.commissioningStudy" help="study.generalInformation.commissioningStudy.help" className="" helpIcon=false required=!action.isPOWB() editable=editable /]
      </div>
      [/#if]
      
      [#-- 2. Short outcome/impact statement (up to 80 words) --]
      [#if isOutcomeCaseStudy]
      <div class="form-group">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.outcomeImpactStatement" i18nkey="study.generalInformation.outcomeStatement" help="study.generalInformation.outcomeStatement.help" isNote=true className="limitWords-80 margin-top-10" helpIcon=false required=false editable=editable isMainTitle=true /]
      </div>
      [/#if]

      [#-- 3.  Maturity of change reported (tick-box)  --]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne" style="position: relative;">
        <div class="form-group">
          [#assign guideSheetURL = "https://cgiar.sharepoint.com/sites/Alliance-SPRM/Shared%20Documents/Forms/AllItems.aspx?id=%2Fsites%2FAlliance%2DSPRM%2FShared%20Documents%2F2%20Project%20Monitoring%2C%20Evaluation%20and%20Learning%2F2%2E5%20Project%20Monitoring%2C%20Evaluation%20and%20Learning%20%2D%20Projects%2F2%2E5%2E1%20Outcomes%2F2024%20Outcomes%2F2024%20OICR%20Guidance%20Note%20%28Version%2014%2E08%2E24%29%2Epdf&parent=%2Fsites%2FAlliance%2DSPRM%2FShared%20Documents%2F2%20Project%20Monitoring%2C%20Evaluation%20and%20Learning%2F2%2E5%20Project%20Monitoring%2C%20Evaluation%20and%20Learning%20%2D%20Projects%2F2%2E5%2E1%20Outcomes%2F2024%20Outcomes" /]
        </div>
        <label for="${customName}.projectExpectedStudyInfo.repIndStageStudy.id" class="label--2">[@s.text name="study.generalInformation.maturityChange" /]:[@customForm.req required=(editable && reportingActive) /]
          <div>
            [@customForm.helpLabel name="study.generalInformation.maturityChange.help" showIcon=false isNote=true editable=editable/]
            [@customForm.helpLabel name="study.generalInformation.maturityChange.help2" showIcon=false editable=editable/]
          </div>
        </label>
        <div class="form-group">
            [@customForm.select name="${customName}.projectExpectedStudyInfo.repIndStageStudy.id" value="${(element.projectExpectedStudyInfo.repIndStageStudy.id)!-1}" showTitle=false listName="stageStudies" keyFieldName="id" displayFieldName="composedName" required=false editable=editable /]
        </div>
      </div>
      [/#if]
      
      [#--  4. Geographic scope - Countries  --]
      <div class="form-group geographicScopeBlock">
        [#local geographicScopeList = (element.geographicScopes)![] ]
        [#local isGlobal =        findElementID(geographicScopeList,  action.reportingIndGeographicScopeGlobal) /]
        [#local isRegional =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeRegional) /]
        [#local isMultiNational = findElementID(geographicScopeList,  action.reportingIndGeographicScopeMultiNational) /]
        [#local isNational =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeNational) /]
        [#local isSubNational =   findElementID(geographicScopeList,  action.reportingIndGeographicScopeSubNational) /]
        
        <label for="" class="${isOutcomeCaseStudy?then('label--2','')}">[@s.text name="study.generalInformation.geographicScopeTopic" /]:[@customForm.req required=(editable && reportingActive) /]
          <div>
            [#if isOutcomeCaseStudy]
              [@customForm.helpLabel name="study.generalInformation.geographicScopeTopic.note" showIcon=false isNote=true editable=editable/]
            [/#if]
          </div>
        </label>
        <div class="form-group ${isOutcomeCaseStudy?then('','simpleBox')} geographicScopeInput">
          <div class="form-group row">
            <div class="col-md-12 margin-top-10">
              [#local isDisplayTitleScope = ((isMultiNational || isNational || isSubNational || isRegional) || (isGlobal && (geographicScopeList.length >1)))!false /]
              <label for="${customName}.geographicScopes" class="col-md-4">[@s.text name="study.generalInformation.geographicScope" /]: [@customForm.req required=(editable && reportingActive) /] </label>
              <label for="" name="study.generalInformation.geographicImpact" class="col-md-8" style="display:${isDisplayTitleScope?string('block','none')}">[@s.text name="study.generalInformation.geographicImpact" /]: [@customForm.req required=(editable && reportingActive) /]</label>
            </div>
          </div>
          <div class="form-group row">
            <div class="form-group col-md-4">
              [#-- Geographic Scope --]
              [@customForm.elementsListComponent name="${customName}.geographicScopes" elementType="repIndGeographicScope" elementList=element.geographicScopes  listName="geographicScopes" keyFieldName="id" displayFieldName="name" showTitle=false required=(!action.isPOWB() && validateIsProgressWithStatus!true) /]
            </div>
            <div class="form-group nationalBlock col-md-4" style="display:${(isMultiNational || isNational || isSubNational)?string('block','none')}">
              [#-- Multinational, National and Subnational scope --]
              [@customForm.select name="${customName}.countriesIds" label="" i18nkey="study.generalInformation.countries" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="${customName}.countriesIds" multiple=true required=(!action.isPOWB() && validateIsProgressWithStatus!true) className="countriesSelect" isFlex=true  disabled=!editable/]
            </div>
            <div class="form-group regionalBlock col-md-4" style="display:${(isRegional)?string('block','none')}">
              [#-- Regional scope --]
              [@customForm.elementsListComponent name="${customName}.studyRegions" elementType="locElement" elementList=element.studyRegions label="study.generalInformation.region"  listName="regions" keyFieldName="id" displayFieldName="composedName" isFlex=true required=false /]
            </div>
            <div class="form-group col-md-12">
              [#-- Comment box --]
              [@customForm.textArea name="${customName}.projectExpectedStudyInfo.scopeComments" className="limitWords-30" i18nkey="study.generalInformation.geographicScopeComments" help="study.generalInformation.geographicScopeComments.help" helpIcon=false  editable=editable required=false/]
            </div>
          </div>
        </div>
      </div>
      

      [#-- 5. Key Contributors  --]
      <div class="form-group keyContributions">
        [#if isOutcomeCaseStudy || !fromProject]
          <label for="" class="label--2">[@s.text name="study.generalInformation.${isOutcomeCaseStudy?string('keyContributors','keyContributorsOther')}" /]: [@customForm.req required=(editable && !action.isAWPBActive()) /]</label>
          <div class="note"><span class="glyphicon glyphicon-question-sign"></span> [@s.text name="study.generalInformation.ppapartner.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/partners'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">&nbsp;clicking here</a>[/@] [/@]</div>
        [/#if]
        <div class="row">
          [#-- Centers --]
          [#if isOutcomeCaseStudy]
          <div class="col-md-6">
            [@customForm.elementsListComponent name="${customName}.centers" elementType="institution" elementList=element.centers label="study.generalInformation.keyContributors.centers"  listName="centers" keyFieldName="id" displayFieldName="composedName" required=(!action.isPOWB() && validateIsProgressWithStatus!true)/]
          </div>
          [/#if]
          [#-- External Partners --]
          [#if isOutcomeCaseStudy]
          <div class="col-md-6 stageProcessOne">
            <label for="">[@s.text name="study.generalInformation.keyContributors.externalPartners" /]:</label>
            <div id="addPartnerText" class="note--2">
              <p>
                [@s.text name="study.generalInformation.externalPartner.note" /]
                <a class="popup" href="[@s.url namespace="/projects" action='${crpSession}/partnerSave'][@s.param name='expectedID']${(expectedID)!}[/@s.param][/@s.url]">
                  [@s.text name="projectPartners.addPartnerMessage.second" /]
                </a>
              </p>
            </div> 
            [@customForm.elementsListComponent name="${customName}.institutions" elementType="institution" elementList=element.institutions label="study.generalInformation.keyContributors.externalPartners"  listName="institutions" keyFieldName="id" displayFieldName="composedName" help="study.generalInformation.externalPartner.note" showTitle=false helpIcon=false isNote=true required=false /]
            [#-- Request partner adition --]
            [#-- Remove element item of request --]
            [#--[#if editable]
                  <p id="addPartnerText" class="helpMessage">
                    [@s.text name="projectPartners.addPartnerMessage.first" /]
                    <a class="popup" href="[@s.url namespace="/projects" action='${crpSession}/partnerSave'][@s.param name='expectedID']${(expectedID)!}[/@s.param][/@s.url]">
                      [@s.text name="projectPartners.addPartnerMessage.second" /]
                    </a>
                  </p> 
                [/#if]  --]
          </div>
          [/#if]
        </div>

      </div>

      [#-- 6.  CGIAR innovation(s) or findings that have resulted in this outcome or impact.   --]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.cgiarInnovation" i18nkey="study.generalInformation.innovationsNarrative" help="study.generalInformation.innovationsNarrative.help" helpIcon=false isNote=true className="margin-top-10" required=(editable && !(isPolicy && stageProcessOne) && !action.isPOWB() && validateIsProgressWithStatus!true) editable=editable isMainTitle=true /]
         
        [@customForm.elementsListComponent name="${customName}.innovations" elementType="projectInnovation" elementList=element.innovations label="study.generalInformation.innovationsList"  listName="innovationsList" keyFieldName="id" displayFieldName="composedNameAlternative" required=false /]

        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.otherInnovationsNarrative" i18nkey="study.generalInformation.otherInnovations" className="limitWords-30" required=false editable=editable  /]
      </div>
      [/#if]
      
      [#-- 7. Links to the Strategic Results Framework  --]
      [#if !isOutcomeCaseStudy]
        <div class="form-group ${isOutcomeCaseStudy?then('','simpleBox')}">
          [#if isOutcomeCaseStudy]
            <label for="" class="label--2">[@s.text name="study.generalInformation.stratgicResultsLink" /]:[@customForm.req required=(editable && !action.isPOWB() && validateIsProgressWithStatus!true) /]
              [@customForm.helpLabel name="study.generalInformation.stratgicResultsLink.help" showIcon=false editable=editable/]
            </label>
          [/#if]
          
          [#-- Sub IDOs (maxLimit=3 -Requested for AR2019) --]      
          [#-- <div class="form-group simpleBox">
            [@customForm.primaryListComponent name="${customName}.subIdos" checkName="subIdoPrimaryId" elementType="srfSubIdo" elementList=(element.subIdos)!"" label="policy.subIDOs" labelPrimary="policy.primarySubIdo" listName="subIdos" maxLimit=3 keyFieldName="id" displayFieldName="description" required=false /]
          </div>--] 
          
          [#-- SRF Targets (maxLimit=2)  --]
          <div class="form-group stageProcessOne">
              <label for="">[@s.text name="study.generalInformation.targetsOption" /]:[@customForm.req required=(editable && !action.isPOWB() && validateIsProgressWithStatus!true) /]
              <div class="feedback-flex-items">
                [@customForm.helpLabel name="study.generalInformation.targetsOption.help" showIcon=false editable=editable/]
              </div>            
              </label><br />
            [#local targetsOption = (element.projectExpectedStudyInfo.isSrfTarget)!""]
            <div class="row stfTargets">
            [#list ["targetsOptionYes", "targetsOptionNo", "targetsOptionTooEarlyToSay"] as option]
              <div class="col-md-1">
                [@customForm.radioFlat id="option-${option}" name="${customName}.projectExpectedStudyInfo.isSrfTarget" i18nkey="study.generalInformation.${option}" value="${option}" checked=(option == targetsOption) cssClass="radioType-targetsOption" cssClassLabel="font-normal" editable=editable /] 
              </div>
            [/#list]
            </div>
            [#local showTargetsComponent = (element.projectExpectedStudyInfo.isSrfTarget == "targetsOptionYes")!false /]
            <div class="srfTargetsComponent" style="display:${showTargetsComponent?string('block', 'none')}">
              [@customForm.elementsListComponent name="${customName}.srfTargets" elementType="srfSloIndicator" elementList=element.srfTargets label="study.generalInformation.stratgicResultsLink.srfTargets" listName="targets" maxLimit=2  keyFieldName="id" displayFieldName="title" required=(editable && !action.isPOWB() && !(isPolicy && stageProcessOne) && validateIsProgressWithStatus!true)/]          
            </div>
          </div>
          
          [#-- Comments  --]
          [#if isOutcomeCaseStudy]
          <div class="form-group stageProcessOne">
            [@customForm.textArea name="${customName}.projectExpectedStudyInfo.topLevelComments" i18nkey="study.generalInformation.stratgicResultsLink.comments" help="study.generalInformation.stratgicResultsLink.comments.help" helpIcon=false className="limitWords-100" editable=editable required=false /]
          </div>
          [/#if]
        </div>
      [/#if]
      
      [#-- 8. Link to Performance Indicators: / Milestones --]
      [#if !isOutcomeCaseStudy]
        <div class="form-group">          
          <label for="" class="${isOutcomeCaseStudy?then('label--2','')}">[@s.text name="study.generalInformation.outcomes" /]:[@customForm.req required=(editable && !action.isPOWB() && validateIsProgressWithStatus!true) /]
            <div class="feedback-flex-items">
              [@customForm.helpLabel name="study.generalInformation.outcomes.help" showIcon=false editable=editable/]
            </div>
          </label>
          [#assign studyMilestoneLink = "studyMilestoneLink"]
          [#assign showMilestoneIndicator = (expectedStudy.projectExpectedStudyInfo.hasMilestones?string)!"" /]
          <div class="form-group row">
            <div class="col-md-2">[@customForm.radioFlat id="${studyMilestoneLink}-yes" name="${customName}.projectExpectedStudyInfo.hasMilestones" label="Yes" value="true" checked=(showMilestoneIndicator == "true") cssClass="radioType-${studyMilestoneLink}" cssClassLabel="radio-label-yes" editable=editable /]</div>
            <div class="col-md-2">[@customForm.radioFlat id="${studyMilestoneLink}-no" name="${customName}.projectExpectedStudyInfo.hasMilestones" label="No" value="false" checked=(showMilestoneIndicator == "false") cssClass="radioType-${studyMilestoneLink}" cssClassLabel="radio-label-no" editable=editable /]</div>
          </div>
        </div>
        [#else]
        <div class="form-group">
          <label for="" class="label--2">[@s.text name="study.generalInformation.outcomes" /]:[@customForm.req required=(editable && !action.isPOWB() && validateIsProgressWithStatus!true) /]
          </label>
          [#assign studyMilestoneLink = "studyMilestoneLink"]
          [#assign showMilestoneIndicator = (expectedStudy.projectExpectedStudyInfo.hasMilestones?string)!"" /]
          [@customForm.input name="${customName}.projectExpectedStudyInfo.hasMilestones" showTitle=false value="true" type="hidden" /]
        </div>
      [/#if]
        [#--  
       <div class="form-group simpleBox block-${studyMilestoneLink}" style="display:${(showMilestoneIndicator == "true")?string('block','none')}">
          [@customForm.elementsListComponent name="${customName}.milestones" elementType="crpMilestone" elementList=(element.milestones)![] label="study.milestones"  listName="milestones" keyFieldName="id" displayFieldName="composedName" hasPrimary=true /]
          <div class="note">[@s.text name="study.milestones.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/contributionsCrpList'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">clicking here</a>[/@][/@]</div>
         <br/>      
        </div>
        --]
        [#--  Removed 03/22/2023
       <div class="form-group simpleBox block-${studyMilestoneLink}" style="display:${(showMilestoneIndicator == "true")?string('block','none')}">
          [@customForm.elementsListComponent name="${customName}.projectOutcomes" elementType="projectOutcome" elementList=(element.projectOutcomes)![] label="study.outcomes"  listName="projectOutcomes" keyFieldName="id" displayFieldName="composedName" required=!action.isPOWB()/]
          <div class="note">[@s.text name="study.outcomes.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/contributionsCrpList'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">clicking here</a>[/@][/@]</div>
         <br/>      
        </div>
       --]
       
       <div class="form-group ${isOutcomeCaseStudy?then('','simpleBox')} block-${studyMilestoneLink}" >
          [@customForm.elementsListComponent name="${customName}.crpOutcomes" elementType="crpOutcome" elementList=(element.crpOutcomes)![] label="study.generalInformation.outcomes"  listName="crpOutcomes" keyFieldName="id" displayFieldName="composedName" showTitle=false required=(!action.isPOWB()) /]
          <div class="note left">
            <span class="glyphicon glyphicon-question-sign"></span>
            [@s.text name="project.deliverable.generalInformation.keyOutputNotice2"][@s.param] <a href="[@s.url namespace=namespace action="${crpSession}/contributionsCrpList"][@s.param name='projectID']${projectID?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">&nbsp;clicking here</a>[/@] [/@]  
          </div>
         <br/>      
        </div>
        
        [#--<div class="form-group simpleBox block-${studyMilestoneLink}" style="display:${(showMilestoneIndicator == "true")?string('block','none')}">
          [@customForm.primaryListComponent name="${customName}.milestones" checkName="study.milestonePrimaryId" elementType="crpMilestone" elementList=(element.milestones)!"" label="study.milestones" labelPrimary="policy.primaryMilestone" helpIcon=false listName="milestones" keyFieldName="id" displayFieldName="composedName" required=false /]
         <div class="note">[@s.text name="study.milestones.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/contributionsCrpList'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">clicking here</a>[/@][/@]</div>
         <br/>
        </div>--]
        [#-- [/#if] --]
        
      [#-- Performacen indicators    
        <div class="form-group">
          <label for="">[@s.text name="Performance Indicators" /]:</label>
            <div class="form-group simpleBox">
              [@customForm.elementsListComponent name="${customName}.projectOutcomes" elementType="projectOutcome" elementList=(element.projectOutcomes)![] label="study.outcomes"  listName="projectOutcomes" keyFieldName="id" displayFieldName="composedName" required=false/]
            </div>          
        </div>
      --]   
      [#-- End to Link to Performance Indicators --]
      
      [#--  Elaboration of Outcome/Impact Statement  --]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.elaborationOutcomeImpactStatement" i18nkey="study.generalInformation.elaborationStatement" help="study.generalInformation.elaborationStatement.help" helpIcon=false isNote=true className="limitWords-400 margin-top-10" required=(editable && reportingActive) editable=editable isMainTitle=true /]
      </div>
      [/#if]
      
      [#-- 9. References cited  --]
      [#--  
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        <div class="form-group">
          [@customForm.textArea name="${customName}.projectExpectedStudyInfo.referencesText" i18nkey="study.referencesEvidenceCited" help="study.referencesCited.help2" helpIcon=false className="" required=editable && !(isPolicy && stageProcessOne) editable=true  /]
        </div>
        <p class="note"> <small>[@s.text name="message.shortenURLsDisclaimer"][@s.param value="93" /][/@s.text]</small> </p>
   
      </div>
      [/#if]
      --]
      
      [#-- 9. References cited  new--]
      [#if isOutcomeCaseStudy]
      <div class="note"><span class="glyphicon glyphicon-question-sign"></span>[@s.text name="message.shortenURLsDisclaimer"][@s.param value="93" /][/@s.text]</div>
      <div class="form-group stageProcessOne">
        <div class="form-group">
              
        [#if (element.projectExpectedStudyInfo.referencesText)?has_content]     
          <span id="warningEmptyReferencesTag" class="errorTag glyphicon glyphicon-info-sign" style="position: relative; left: 750px;" title="" aria-describedby="ui-id-5"> </span>          
            <div class="feedback-flex-items">
            [@customForm.textAreaReferences name="${customName}.projectExpectedStudyInfo.referencesText" i18nkey="study.generalInformation.referencesEvidenceCited" help="study.generalInformation.referencesCited.help2" helpIcon=false isNote=true className="" required=(!action.isAWPBActive()) editable=editable oldReference=true /]        
            </div>
        [#else]
          <div class="feedback-flex-items">
            [@customForm.textAreaReferences name="${customName}.projectExpectedStudyInfo.referencesText" i18nkey="study.generalInformation.referencesEvidenceCited" help="study.generalInformation.referencesCited.help2" helpIcon=false className="" required=(!action.isAWPBActive()) editable=editable oldReference=false isNote=true isMainTitle=true /]        
          </div>
        [/#if]      
          
            <label style="margin-top: 5px;">[@s.text name="${customName}.multireferences"][/@s.text]</label>
            <div class="referenceBlock ">
              <div class="referenceList">
                <div class="row">
                  <div class="col-sm-6 colTitleCenter" style="font-weight: 600; text-align: center;">Reference[@customForm.req required=(!action.isAWPBActive())  /]</div>
                  <div class="col-sm-4 colTitleCenter" style="font-weight: 600; text-align: center;">URL[@customForm.req required=(!action.isAWPBActive())  /]</div>
                  <div class="col-sm-2 colTitleCenter" style="font-weight: 600; text-align: center;">External Author[@customForm.req required=false  /]</div>
                </div>
                [#list (element.references)![{}] as link ]
                  [@customForm.references name="${customName}.references" element=link index=link_index class="references" /]
                [/#list]
              </div>
              [#if editable]
              <div class="addButtonReference bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>Add Reference </div>
              <div class="clearfix"></div>
              [/#if]
            </div>
            [#-- Element item Template --]
            <div style="display:none">
              [@customForm.references name="${customName}.references" element={} index=-1 template=true class="references" /]
            </div>

        </div>
        
        [#-- 
        <div class="form-group" style="position:relative" listname="">
          [@customForm.fileUploadAjax 
            fileDB=(element.projectExpectedStudyInfo.referencesFile)!{} 
            name="${customName}.projectExpectedStudyInfo.referencesFile.id" 
            label="study.referencesCitedAttach" 
            dataUrl="${baseUrl}/uploadStudies.do" 
            path="${(action.getPath())!}"
            isEditable=editable
            labelClass="label-min-width"
            required=false
          /]          
        </div>
         --]
         
      </div>
      [/#if]
      
      [#-- 10. Quantification (where data is available)  --]
      [#local isLeverOfMaturityOnThree = (element.projectExpectedStudyInfo.repIndStageStudy.id == 3)!false]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne quantificationsBlockContainer" style="display: grid;">
        [#--
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.quantification" i18nkey="study.quantification" help="study.quantification.help" helpIcon=false className=" " required=editable && !(isPolicy && stageProcessOne) editable=editable /]
        --]
        [@customForm.labelText name="expectedStudy.projectExpectedStudyInfo.quantification" text="study.generalInformation.quantification" helpText="study.generalInformation.quantification.help" required=(editable && reportingActive && isLeverOfMaturityOnThree) isMainTitle=true isNote=true className="padding-left-0" /]
        
        <br>
        <div class="quantificationsBlock">
          <div class="quantificationsList">
          [#list (element.quantifications)![] as item]
            [@quantificationMacro name="${customName}.quantifications" element=item index=item_index additionalRequired=isLeverOfMaturityOnThree /]
          [/#list]
          </div>
          [#if editable]
            <div class="addStudyQualification bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>[@s.text name="form.buttons.addStudyQualification"/]</div>
          [/#if]
        </div>
        [#-- Element item Template --]
        <div style="display:none">
          [@quantificationMacro name="${customName}.quantifications" element={} index=-1 template=true additionalRequired=isLeverOfMaturityOnThree /]
        </div>
        <br />
      </div>
      [/#if]
      
      [#-- 11.Cross-cutting markers - Gender, Youth, and Capacity Development  --]
      [#if isOutcomeCaseStudy]
      <div class="form-group" style="position: relative;">
        [#assign ccGuideSheetURL = "https://cgiar.sharepoint.com/sites/Alliance-SPRM/Shared%20Documents/Forms/AllItems.aspx?id=%2Fsites%2FAlliance%2DSPRM%2FShared%20Documents%2F2%20Project%20Monitoring%2C%20Evaluation%20and%20Learning%2F2%2E5%20Project%20Monitoring%2C%20Evaluation%20and%20Learning%20%2D%20Projects%2F2%2E5%2E1%20Outcomes%2F2024%20Outcomes%2F2024%20OICR%20Guidance%20Note%20%28Version%2014%2E08%2E24%29%2Epdf&parent=%2Fsites%2FAlliance%2DSPRM%2FShared%20Documents%2F2%20Project%20Monitoring%2C%20Evaluation%20and%20Learning%2F2%2E5%20Project%20Monitoring%2C%20Evaluation%20and%20Learning%20%2D%20Projects%2F2%2E5%2E1%20Outcomes%2F2024%20Outcomes" /]
        <small class="pull-right"><a href="${ccGuideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" />[@s.text name="study.general.guideSheetURL.crossMarkets" /]</a> </small>
      </div>
      <div class="form-group">
        [@tag name="Indicator #3" /]
        [@customForm.labelText name="expectedStudy.projectExpectedStudyInfo.crossCuttingRelevance" text="study.generalInformation.crossCuttingRelevance" helpText="study.generalInformation.crossCuttingRelevance.help" required=(editable && reportingActive) isMainTitle=true isNote=true className="padding-left-0" /]
        [@customForm.helpLabel name="study.generalInformation.crossCuttingRelevance.help2" showIcon=false editable=editable/]
        <div class="row">
          [#-- Gender --]
          <div class="ccRelevanceBlock col-md-3">
            <label for="">[@s.text name="study.generalInformation.genderRelevance" /]:[@customForm.req required=(editable && reportingActive) /]</label>
            <div class="form-group">
              [#assign genderLevel = (element.projectExpectedStudyInfo.genderLevel.id)!1 ]
              [@customForm.select name="${customName}.projectExpectedStudyInfo.genderLevel.id" label="" listName="focusLevels" keyFieldName="id" displayFieldName="powbName" value="${genderLevel}" required=(editable && reportingActive) editable=editable showTitle=false /]
            </div>
          </div>
          [#-- Youth  --]
          <div class="ccRelevanceBlock col-md-3">
            <label for="">[@s.text name="study.generalInformation.youthRelevance" /]:[@customForm.req required=(editable && reportingActive) /]</label>
            <div class="form-group">
              [#assign youthLevel = (element.projectExpectedStudyInfo.youthLevel.id)!1 ]
              [@customForm.select name="${customName}.projectExpectedStudyInfo.youthLevel.id" label="" listName="focusLevels" keyFieldName="id" displayFieldName="powbName" value="${youthLevel}" required=(editable && reportingActive) editable=editable showTitle=false /]
            </div> 
          </div>
          [#-- CapDev   --]
          <div class="ccRelevanceBlock col-md-3">
            <label for="">[@s.text name="study.generalInformation.capDevRelevance" /]:[@customForm.req required=(editable && reportingActive) /]</label>
            <div class="form-group">
              [#assign capdevLevel = (element.projectExpectedStudyInfo.capdevLevel.id)!1 ]
              [@customForm.select name="${customName}.projectExpectedStudyInfo.capdevLevel.id" label="" listName="focusLevels" keyFieldName="id" displayFieldName="powbName" value="${capdevLevel}" required=(editable && reportingActive) editable=editable showTitle=false /]
            </div>

          </div>
          [#-- Climate Change  --]
          <div class="ccRelevanceBlock col-md-3">
            <label for="">[@s.text name="study.generalInformation.climateChangeRelevance" /]:[@customForm.req required=(editable && reportingActive) /]</label>
            <div class="form-group">
              [#assign climateChangeLevel = (element.projectExpectedStudyInfo.climateChangeLevel.id)!1 ]
              [@customForm.select name="${customName}.projectExpectedStudyInfo.climateChangeLevel.id" label="" listName="focusLevels" keyFieldName="id" displayFieldName="powbName" value="${climateChangeLevel}" required=(editable && reportingActive) editable=editable showTitle=false /]
            </div>
          </div>
          <div class="col-md-12">
            [@customForm.textArea name="${customName}.projectExpectedStudyInfo.commentsRelevance" i18nkey="study.generalInformation.commentsRelevance" className="limitWords-30" required=false editable=editable /]
          </div>
        </div>
        
      </div>
      [/#if]
      
      [#--  Other cross-cutting dimensions   --]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        <label for="" class="label--2">[@s.text name="study.generalInformation.otherCrossCutting" /]:[@customForm.req required=(editable && !action.isAWPBActive()) /]</label> 
         <div class="feedback-flex-items">
          [@customForm.helpLabel name="study.generalInformation.otherCrossCuttingOptions" showIcon=false editable=editable/]<br />
        </div>
        <div class="form-group row">
          [#local otherCrossCuttingSelection = (element.projectExpectedStudyInfo.otherCrossCuttingSelection)!"" ]
          [#list ["Yes", "No", "NA"] as option]
            <div class="col-md-1">
              [@customForm.radioFlat id="option-${option}" name="${customName}.projectExpectedStudyInfo.otherCrossCuttingSelection" i18nkey="study.generalInformation.otherCrossCutting${option}" value="${option}" checked=(otherCrossCuttingSelection == option) cssClass="radioType-otherCrossCuttingOption" cssClassLabel="font-normal" editable=editable /] 
            </div>
          [/#list]
        </div>
        [#local showOtherCrossCuttingOptionsComponent = (element.projectExpectedStudyInfo.otherCrossCuttingSelection == "Yes")!false /] 
        <div class="otherCrossCuttingOptionsComponent form-group" style="display:${showOtherCrossCuttingOptionsComponent?string('block', 'none')}">
          [@customForm.textArea name="${customName}.projectExpectedStudyInfo.otherCrossCuttingDimensions" i18nkey="study.generalInformation.otherCrossCutting.comments" help="study.generalInformation.otherCrossCutting.comments.help" helpIcon=false  className="limitWords-200" required=true editable=editable /]
        </div>
      </div>
      [/#if]
      
      [#--  Communications materials    --]
      [#if isOutcomeCaseStudy]
      [#-- 
      <div class="form-group stageProcessOne">
        <div class="form-group">
          [@customForm.textArea name="${customName}.projectExpectedStudyInfo.comunicationsMaterial" i18nkey="study.communicationMaterials" help="study.communicationMaterials.help" helpIcon=false className=" " required=false editable=editable /]
        </div>
        <div class="form-group" style="position:relative" listname="">
          [@customForm.fileUploadAjax 
            fileDB=(element.projectExpectedStudyInfo.outcomeFile)!{} 
            name="${customName}.projectExpectedStudyInfo.outcomeFile.id" 
            label="study.communicationMaterialsAttach" 
            dataUrl="${baseUrl}/uploadStudies.do" 
            path="${(action.getPath())!}"
            isEditable=editable
            labelClass="label-min-width"
          /]
        </div>
      </div>
       --]
      [/#if]
      
      [#--  Comments for other studies--]
      [#if !isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.topLevelComments" i18nkey="study.generalInformation.activityDescription"  placeholder="" className="limitWords-100" required=(editable && !(isPolicy && stageProcessOne) && !action.isPOWB() && validateIsProgressWithStatus!true) editable=editable /]
      </div>
      
      <div class="form-group stageProcessOne">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.MELIAPublications" i18nkey="study.generalInformation.MELIAPublications"  placeholder="" help="study.generalInformation.MELIAPublications.help" helpIcon=false className="" required=false editable=editable /]
      </div>
      [/#if]
      
    </div>
    
    [#-- Private Option: FOR AR 2020 and onwards -> ALL OICRs are ALWAYS public --]
    [#--if isOutcomeCaseStudy]
      <h3 class="headTitle">[@s.text name="study.confidentialTitle" /]</h3>
      <div class="borderBox">
        <label for="">[@s.text name="study.public" ][@s.param]${(element.projectExpectedStudyInfo.studyType.name)!}[/@][/@] 
          [@customForm.helpLabel name="study.public.help" showIcon=false paramText="${(element.projectExpectedStudyInfo.studyType.name)!}" editable=editable/]
        </label> <br />
        [#local isPublic = (element.projectExpectedStudyInfo.isPublic)!true /]
        [#local isPublic = true /]
        [@customForm.radioFlat id="optionPublic-yes"  name="${customName}.projectExpectedStudyInfo.isPublic" i18nkey="Yes"  value="true"  checked=isPublic  cssClass="radioType-optionPublic" cssClassLabel="font-normal radio-label-yes" editable=editable /] 
        [@customForm.radioFlat id="optionPublic-no"   name="${customName}.projectExpectedStudyInfo.isPublic" i18nkey="No"   value="false" checked=!isPublic cssClass="radioType-optionPublic" cssClassLabel="font-normal radio-label-no"  editable=editable /] 
        
        <div class="optionPublicComponent form-group" style="display:${isPublic?string('block', 'none')}">         
          <br />
          <div class="input-group">
            <span class="input-group-btn">
              <button class="btn btn-default btn-sm copyButton" type="button"> <span class="glyphicon glyphicon-link"></span> Copy URL </button>
            </span>
            [#local summaryPDF = "${baseUrl}/projects/${crpSession}/studySummary.do?studyID=${(element.id)!}&cycle=Reporting&year=${(actualPhase.year)!}"]
            <input type="text" class="form-control input-sm urlInput" value="${summaryPDF}" readonly>
          </div>
          <div class="message text-center" style="display:none">Copied!</div>
        </div>
      </div>
    [/#if--]
    
    [#-- Projects shared --]
    [#if fromProject && !isOutcomeCaseStudy]
    <h3 class="headTitle">[@s.text name="study.sharedProjects.title" /]</h3>
    <div class="borderBox">
      [@customForm.elementsListComponent name="${customName}.projects" elementType="project" elementList=element.projects label="study.sharedProjects.message"  listName="myProjects" keyFieldName="id" displayFieldName="composedName" required=false /]
    </div>
    [/#if]
  </div>
[/#macro]

[#macro studyGeneral element name index=-1 template=false fromProject=true isAllianceContribution=false]
  [#local customName = "${name}"/]
  [#local isOutcomeCaseStudy = ((element.projectExpectedStudyInfo.studyType.id == 1)!false)/]
  [#local isStatusExtended = (element.projectExpectedStudyInfo.status.id == 4)!false]
  [#local isOtherStatus = (element.projectExpectedStudyInfo.status.id != 4)!false]

  <div class="borderBox generalInformationStudies">

    [#-- Note for General Inputs description --]
    [#if isOutcomeCaseStudy] 
      <div class="note--2">
        <p style="display: ${isAllianceContribution?then('block','none')}">[@s.text name="study.general.note.allianceId" /]</p>
        <p>[@s.text name="study.general.note.currentYearReport" /]</p>
      </div>
    [/#if]

    [#-- Template for Other Studies --]
    [#if !isOutcomeCaseStudy]
      [#-- previous link --]
        [#--
        [#assign guideSheetURL = "https://drive.google.com/file/d/1sMmE8RK4mpDmJYl_S-bHy5CVK_ahCHr0/view" /]
        --]
      <div class="form-group">
        [#assign guideSheetURL = "https://cgiar.sharepoint.com/sites/AICCRA/Shared%20Documents/Forms/AllItems.aspx?id=%2Fsites%2FAICCRA%2FShared%20Documents%2F02%2E%20Monitoring%20%26%20Evaluation%2F2%2E2%20MARLO%20Docs%20and%20reports%2F6%2E%20OICR%20reporting%2FGuidance%20for%20AICCRA%20Outcome%20Impact%20Case%20Reports%2Epdf&parent=%2Fsites%2FAICCRA%2FShared%20Documents%2F02%2E%20Monitoring%20%26%20Evaluation%2F2%2E2%20MARLO%20Docs%20and%20reports%2F6%2E%20OICR%20reporting&p=true&ga=1" /]
        <small class="pull-right"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" />[@s.text name="study.general.guideSheetURL" /]</a> </small>
      </div>
      <br />
    [/#if]

    [#-- General Inputs --]
    <div class="form-group row">

      [#-- hr in elements --]
      [#if isOutcomeCaseStudy]
        <hr class="col-md-9 line-hr" />
      [/#if]
    
      [#-- OICR ID --]
      [#if isOutcomeCaseStudy]
        <div class="col-md-1">
          [@customForm.input name="${(expectedID)!}" i18nkey="study.general.id" helpIcon=false required=false editable=false readOnly=true /]
        </div>
      [/#if]

      [#-- Study Type --]
      <div class="${isOutcomeCaseStudy?string('col','col-md-3')}">
        [@customForm.select name="${customName}.projectExpectedStudyInfo.studyType.id" value="${(element.projectExpectedStudyInfo.studyType.id)!-1}" className="setSelect2 studyType" i18nkey="study.general.type" listName="studyTypes" keyFieldName="id"  displayFieldName="name" required=true editable=(editable && !isOutcomeCaseStudy) display=(!isOutcomeCaseStudy) /]
      </div>

      [#-- Alliance OICR ID --]
      [#if isOutcomeCaseStudy]
        <div class="col-md-2" id="allianceOICRIdContainer" style="display: ${isAllianceContribution?string('block','none')};">
          [@customForm.input name="${customName}.projectExpectedStudyInfo.allianceOicr" i18nkey="study.general.allianceID" helpIcon=false required=false editable=editable className="targetValueAllianceId" placeholder="XXX-0000" /]
        </div>
      [/#if]

      [#-- Tag --]
      [#local tagHasInformation = ((element.projectExpectedStudyInfo.tag.id)?has_content)!false]
      [#if isOutcomeCaseStudy]
        [#if tagHasInformation ]          
        <div class="col-md-2">
            [@customForm.select name="${customName}.projectExpectedStudyInfo.tag.id" value="${(element.projectExpectedStudyInfo.tag.id)!-1}" className="setSelect2 studyTag" i18nkey="study.general.tag" listName="tagList" keyFieldName="id"  displayFieldName="tagName" required=false editable=(editable && isOutcomeCaseStudy && action.canAccessSuperAdmin()) && action.hasSpecificities('oicr_tag_field_manual_manage_active') /]
          </div>
        [/#if]
      [/#if]

      [#-- Status --]
      <div class="${isOutcomeCaseStudy?string('col-md-2','col-md-3')}">
        [@customForm.select name="${customName}.projectExpectedStudyInfo.status.id" className="setSelect2 statusSelect" i18nkey="study.general.status" listName="statuses" header=false required=true editable=editable /]
      </div>

      [#-- Year --]        
      <div class="col-md-2">
        [#assign dbExpectedYear = ((element.projectExpectedStudyInfo.year)!currentCycleYear)  ]
        
          [#--
        [@customForm.select name="${customName}.projectExpectedStudyInfo.year" className="setSelect2" i18nkey="study.year" listName="getExpectedStudiesYears(${(expectedID)!})" header=false required=true editable=editable /]
          --]  
        
        [#-- <div class="block-extendedYear" style="display:${isStatusExtended?string('block', 'none')}">
          [@customForm.select name="newExpectedYear" className="setSelect2" i18nkey="study.general.year" listName="project.projectInfo.getYears(${currentCycleYear})" header=false required=true editable=editable /]
        </div> --]
        <div class="block-year" >
          [@customForm.select name="${customName}.projectExpectedStudyInfo.year" className="setSelect2" i18nkey="study.general.year" listName="getExpectedStudiesYears(${(expectedID)!})" header=false required=true editable=editable /]
        </div>
      </div>

      [#-- Buttons - Shared Cluster & Copy --]
      [#if isOutcomeCaseStudy]
        <div class="col-md-3 generalStudyOptions">
          <button type="button" class="btn btn-default btn-sm" style="margin-right: 5px;" data-toggle="modal" data-target="#sharedClusterModal">
            <p><span class="glyphicon glyphicon-log-out"></span>[@s.text name="study.general.button.shared" /]</p>
            <p id="modalCounterShared">0</p>
          </button>

          [#-- Link to PDF version of this study --]
          [#-- 0. Link to PDF version of this study: AR 2020 and onwards -> ALL OICRs are ALWAYS public--]
          <button type="button" class="btn btn-default btn-sm copyButton" style="margin-right: 5px;">
            <p><span class="glyphicon glyphicon-duplicate"></span>[@s.text name="study.general.button.copylink" /]</p> 
          </button>
          [#local summaryPDF = "${baseUrl}/projects/${crpSession}/studySummary.do?studyID=${(element.id)!}&cycle=Reporting&year=${(actualPhase.year)!}"]
          [@customForm.input name="${customName}.projectExpectedStudyInfo.link" i18nkey="study.general.link" className="form-control input-sm urlInput" value="${summaryPDF}" editable=editable display=false readOnly=true/]
          <div class="message text-center" style="display:none; margin-top:6px;">[@s.text name="study.general.link.copy" /]</div>
        </div>
      [/#if]

      [#-- Shared Cluster Modal --]
      <div class="form-group col-md-12 sharedClusterMessage">
        <div class="modal fade" id="sharedClusterModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
          <div class="modal-dialog modal-dialog-scrollable" style=" width:60%" role="document">
            <div class="modal-content">
              <div class="modal-header">
                <h4 class="modal-title" id="exampleModalLabel">[@s.text name="study.sharedProjects.modal.title" /]</h4>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                  <span aria-hidden="true">&times;</span>
                </button>
              </div>
              <div class="modal-body">
                <h5 class="headTitle">[@s.text name="study.sharedProjects.title" /]</h5>
                [@customForm.elementsListComponent name="${customName}.projects" elementType="project" elementList=element.projects label="study.sharedProjects.message"  listName="myProjects" keyFieldName="id" displayFieldName="composedName" required=false /]
              </div>
              <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">[@s.text name="study.sharedProjects.modal.close" /]</button> 
              </div>
            </div>
          </div>
        </div>

        <div class="clearfix"></div>
      </div>
        

    </div>
    
    [#-- Evidences table with types and their descriptions --]
    [#if !((element.projectExpectedStudyInfo.studyType.id == 1)!false)]
        <div class="note left" style="margin: 10px 0px; font-size: .85em; padding: 4px; border-radius: 10px;">
            <div id="popup" class="helpMessage3">
              <p><a style="cursor: pointer;" data-toggle="modal" data-target="#evidenceModal" > <span class="glyphicon glyphicon-info-sign"></span> [@s.text name="study.general.help.studyType" /]</a></p>
            </div>
          </div>

        <div class="form-group evidenceTypeMessage">
          <div class="modal fade" id="evidenceModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-scrollable" style=" width:80%" role="document">
              <div class="modal-content">
                <div class="modal-header">
                  <!-- <h5 class="modal-title" id="exampleModalLabel">Modal title</h5> -->
                  <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                  </button>
                </div>
                <div class="modal-body">
                  
                  <table id="evidenceTypes" class="table ">
                    <thead style="background-color: #0b7ba6; font-weight: 500; color: white;">
                      <tr>
                        <th> [@s.text name="study.general.dialogMessage.part1" /]</th>
                        <th> [@s.text name="study.general.dialogMessage.part2" /] </th>
                        <th> [@s.text name="study.general.dialogMessage.part3" /] / [@s.text name="study.general.dialogMessage.part4" /] </th>
                      </tr>
                    </thead>

                    [#if studyTypes?has_content]
                    [#list studyTypes as st]
                    <tr>
                      [#--if st_index == 0]
                      <th rowspan="${action.getDeliverablesSubTypes(mt.id).size()}" class="text-center"> ${mt.name} </th>
                      [/#if--]
                      <td  >
                        ${st.name}
                      </td>
                      <td style="max-width: 90vw !important;">
                      [#if (st.description?has_content)!false]
                        ${st.description}
                      [#else]
                        <i>([@s.text name="study.general.dialogMessage.notProvided" /])</i>
                      [/#if]
                      </td>
                      <td>
                        [#if (((st.keyIdentifier?has_content)!false) || ((st.forNarrative?has_content)!false) || ((st.example?has_content)!false))]
                          [#if (st.keyIdentifier?has_content)!false]
                            <i><u>How to identify?</u></i> ${st.keyIdentifier}
                          [/#if]
                          [#if (st.forNarrative?has_content)!false]
                            <br><i><u>For:</u></i> ${st.forNarrative}
                          [/#if]
                          [#if (st.example?has_content)!false]
                            <br /> (<i><small>Example: ${st.example}</small></i>)
                          [/#if]
                        [#else]
                          <i>([@s.text name="study.general.dialogMessage.notProvided" /])</i>
                        [/#if]
                      </td>
                    </tr>
                    [/#list]
                    [/#if]
                  </table>
                </div>
                <div class="modal-footer">
                  <button type="button" class="btn btn-secondary" data-dismiss="modal">[@s.text name="study.general.dialogMessage.close" /]</button>
                </div>
              </div>
            </div>
          </div>

          <div class="clearfix"></div>
        </div>
      [/#if]  
        [#-- REMOVED FOR AR 2020 --]
        [#--]if isOutcomeCaseStudy]
          <hr />
          [#-- Tags]
          <div class="form-group">
            <label for="">[@s.text name="study.tags" /]:[@customForm.req required=editable /]</label>
            [#local tagValue = (element.projectExpectedStudyInfo.evidenceTag.id)!-1 ]
            [#list tags as tag]
              <br /> [@customForm.radioFlat id="tag-${tag_index}" name="${customName}.projectExpectedStudyInfo.evidenceTag.id" label="${tag.name}" value="${tag.id}" checked=(tagValue == tag.id) cssClass="radioType-tags" cssClassLabel="font-normal" editable=editable /] 
            [/#list]
          </div>
        [/#if] --]
  </div>
[/#macro]

[#macro studyCommunications element name index=-1 template=false fromProject=true]
  [#local customName = "${name}"/]
  [#local isOutcomeCaseStudy = ((element.projectExpectedStudyInfo.studyType.id == 1)!false)/]
  [#local isStatusExtended = (element.projectExpectedStudyInfo.status.id == 4)!false]
  [#local isOtherStatus = (element.projectExpectedStudyInfo.status.id != 4)!false]
  
  [#local isPolicy = ((element.projectExpectedStudyInfo.isContribution)!false) ]
  [#local stageProcessOne = ((element.projectExpectedStudyInfo.repIndStageProcess.id == 1))!false ]

  <div class="borderBox">

    [#-- Outcome story for communications use. REPLACED "comunicationsMaterial" --]
    [#if isOutcomeCaseStudy]
    <div class="form-group">
    [#--  
      [@customForm.textArea name="${customName}.projectExpectedStudyInfo.comunicationsMaterial" i18nkey="study.outcomestory" help="study.outcomestory.help" className="limitWords-400" helpIcon=false required=true editable=editable /]
    
      <br />
      --]
      [@customForm.labelText name="expectedStudy.outcomestoryLinks" text="study.generalInformation.outcomestoryLinks" required=false className="padding-left-0" /]
      [@customForm.helpLabel name="study.generalInformation.outcomestoryLinks.help" paramText="<a href='https://hdl.handle.net/10568/99384' target='_blank'>Personal data use authorization form</a>" showIcon=false editable=editable/]
      <br>
      <div class="linksBlock ">
        <div class="linksList">
          [#list (element.links)![{}] as link ]
            [@studyLink name="${customName}.links" element=link index=link_index /]
          [/#list]
        </div>
        [#if editable]
        <div class="addButtonLink bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add Link </div>
        <div class="clearfix"></div>
        [/#if]
      </div>
      [#-- Element item Template --]
      <div style="display:none">
        [@studyLink name="${customName}.links" element={} index=-1 template=true /]
      </div>
    </div>
    [/#if]

    [#-- Publications --]
    <div class="form-group">
      [@customForm.labelText name="study.publications" text="study.communications.publications" required=false className="padding-left-0" /]
      [@customForm.helpLabel name="study.communications.publications.help" showIcon=false isNote=true/]
      <div class="publicationsBlock">
        <div class="publicationsList">
          <div class="row">
            <div class="col-sm-4 colTitleCenter" style="font-weight: 600; text-align: center;">Name</div>
            <div class="col-sm-4 colTitleCenter" style="font-weight: 600; text-align: center;">Position</div>
            <div class="col-sm-4 colTitleCenter" style="font-weight: 600; text-align: center;">Affiliation</div>
          </div>
          [#if (element.publications?has_content) && (element.publications?size > 0)]
            [#list (element.publications)![{}] as publication]
              [@publicationMacro name="${customName}.publications" element=publication index=publication_index /]
            [/#list]
          [/#if]
        </div>
        [#if editable]
          <div class="addPublication bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add Publication </div>
          <div class="clearfix"></div>
        [/#if]
      </div>
      [#-- Element item Template --]
      <div style="display:none">
        [@publicationMacro name="${customName}.publications" element={} index=-1 template=true /]
      </div>

    </div>

    [#-- Partner users TEMPLATE --]
    <div id="partnerUsers" style="display:none">
      [#list partners as partner]
        <div class="institution-${partner.institution.id}">
          [#assign usersList = (action.getUserList(partner.institution.id))![]]
          <div class="users-2">
            [#list usersList as user]
              [@deliverableMacros.deliverableUserMacro element={} user=user index=user_index name="_TEMPLATE_${customName}.partnerships[0].partnershipPersons" isUserChecked=false isResponsable=false /]
            [/#list]
          </div>
        </div>
      [/#list]
    </div>
      

    [#--  Contact person    --]
    [#if isOutcomeCaseStudy]
    <div class="form-group stageProcessOne">
      <label for="">[@s.text name="study.communications.contacts" /]:</label>
      <div id="addPartnerText" class="note--2">
        <p>
          [@s.text name="study.communications.contacts.help" /]
          <a class="popup" href="[@s.url namespace="/projects" action='${crpSession}/partnerSave'][@s.param name='expectedID']${(expectedID)!}[/@s.param][/@s.url]">
            [@s.text name="study.communications.contacts.help2" /]
          </a>
        </p>
      </div>
      <div class="projectExpectedStudyPartners">
        [@deliverableMacros.deliverablePartnerMacro element=(element.partnerships[0])!{} name="${customName}.partnerships" index=0 defaultType=2 /]
      </div>
    </div>
    [/#if]
  
  </div>
[/#macro]

[#macro studyAlliance element name index=-1 template=false fromProject=true]
  [#local customName = "${name}"/]
  <div class="borderBox">
    [#-- Levers --]
    <div class="form-group">
      <label class="label--2" for=""><b>[@s.text name="study.allianceAligment.linkToLevers.title" /]</b>[@customForm.req required=reportingActive /]</label>
      [@customForm.helpLabel name="study.allianceAligment.linkToLevers.note" showIcon=false editable=editable isNote=true /]
      [#-- Primary Levers --]
      <div class="form-group">
        [@customForm.selectableCheckToCheckboxMacro label="study.allianceAligment.linkToLevers.options.primaryLever" name="${customName}" for="${customName}.allianceAligment.primary" fieldName="allianceLever" listName=allianceLeverList keyFieldName="sdgContributions" isPrimaryLever=true  subtitleInnerCheckbox="study.allianceAligment.linkToLevers.options.text.contributionSDG" listNameInnerCheckbox="leverSdgContributions" classReferenceInnerCheckbox="sDGContribution" element=element required=reportingActive /]
      </div>
      [#-- Related Levers --]
      <div class="form-group">
        [@customForm.selectableCheckToCheckboxMacro label="study.allianceAligment.linkToLevers.options.relatedLever" name="${customName}" for="${customName}.allianceAligment.related" fieldName="allianceLevers" listName=allianceLeverList keyFieldName="sdgContributions" isPrimaryLever=false subtitleInnerCheckbox="study.allianceAligment.linkToLevers.options.text.contributionSDG" listNameInnerCheckbox="leverSdgContributions" classReferenceInnerCheckbox="sDGContribution" isRadioButton=false element=element className="containerRelatedLever" required=false /]
      </div>
    </div>

    [#-- SDG Representation --]
    <div class="form-group listSelectedLeverContainer">
      <label class="label--2 titleSelectedLeverContainer" for=""><b>[@s.text name="study.allianceAligment.generalContributionSDG" /]</b></label>
      <div class="_TEMPLATE_selectedLeverContainer" style="display:none;">
        <div class="selectedLeverContainer__image">
          <img src="" alt="Selected Lever"  />
        </div>
        <div class="selectedLeverContainer__content">
          <b><p class="selectedLeverContainer__content__lever">No Selected Lever</p></b>
          <p class="selectedLeverContainer__content__contributionSDG">No Selected the Contribution to SDG</p>
        </div>
      </div>
    </div>

  </div>
[/#macro]

[#macro studyOneCGIAR element name index=-1 template=false fromProject=true]
  [#local customName = "${name}"/]

  [#local hasContributionToCGIAR = (element.projectExpectedStudyInfo.hasCgiarContribution)! ]

  <div class="borderBox">
    <div class="form-group">
      <label class="label--2">[@s.text name="study.oneCGIARAligment.contributionToCGIAR" /]:[@customForm.req required=(editable) /]</label>
      <div class="form-group row">
      
        <div class="col-md-1">
          [@customForm.radioFlat id="optionOneCGIAR-Yes" name="${customName}.projectExpectedStudyInfo.hasCgiarContribution" i18nkey="study.oneCGIARAligment.contributionToCGIARYes" value="true" checked=(((element.projectExpectedStudyInfo.hasCgiarContribution??) && (hasContributionToCGIAR))!false) cssClass="radioType-contributionToCGIAR" cssClassLabel="font-normal" editable=editable /]
        </div>
        <div class="col-md-1">
          [@customForm.radioFlat id="optionOneCGIAR-No" name="${customName}.projectExpectedStudyInfo.hasCgiarContribution" i18nkey="study.oneCGIARAligment.contributionToCGIARNo" value="false" checked=(((element.projectExpectedStudyInfo.hasCgiarContribution??) && (!hasContributionToCGIAR))!false) cssClass="radioType-contributionToCGIAR" cssClassLabel="font-normal" editable=editable /]
        </div>
      </div>
      <div class="form-group contributionToCGIARComment" [#if element.projectExpectedStudyInfo.hasCgiarContribution??]style="display:${((hasContributionToCGIAR?c) == 'false')?string('block','none')};"[#else] style="display: none;" [/#if] >
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.reasonNotCgiarContribution" i18nkey="study.oneCGIARAligment.contributionToCGIAR.reasonToNoProvided"  helpIcon=false className="limitWords-200" required=(editable) editable=editable /]
      </div>
    </div>
    <div class="form-group linkToImpactAndTarget"  [#if element.projectExpectedStudyInfo.hasCgiarContribution??] style="display:${((hasContributionToCGIAR?c) == 'true')?string('block','none')};" [#else] style="display: none;" [/#if]>
      <label class="label--2" for=""><b>[@s.text name="study.oneCGIARAligment.linkToImpactAndTarget.title" /]</b>[@customForm.req required=(editable) /]</label>
      
      [@customForm.selectableCheckToCheckboxMacro label="study.oneCGIARAligment.linkToImpactAndTarget.descripition" name="${customName}" for="${customName}.oneCgiar.impactAreas" fieldName="impactArea" listName=impactAreasList keyFieldName="globalTargets" isPrimaryLever=false subtitleInnerCheckbox="" listNameInnerCheckbox="globalTargets" classReferenceInnerCheckbox="impactArea" isRadioButton=true isDirectInfo=true element=element /]
    </div>
  </div>
[/#macro]

[#macro tag name=""]
  [#-- <span class="label label-info pull-right"> <i class="fas fa-tag"></i> ${name} </span> --]
[/#macro]

[#macro studyLink name element index=-1 template=false]
  [#local customName = "${template?string('_TEMPLATE_', '')}${name}[${index}]"]
  <div id="studyLink-${(template?string('template', ''))}" class="studyLink form-group grayBox">
    <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
    <span class="pull-left" style="width:4%"><strong><span class="indexTag">${index + 1}</span>.</strong></span>
    <span class="pull-left" style="width:90%">[@customForm.input name="${customName}.link" placeholder="global.webSiteLink.placeholder" showTitle=false i18nkey="" className="" editable=editable /]</span>
    [#if editable]<div class="removeElement sm removeIcon removeLink" title="Remove"></div>[/#if]
    <div class="clearfix"></div>
  </div>
[/#macro]

[#macro quantificationMacro name element index=-1 template=false additionalRequired=false]
  [#local customName = "${template?string('_TEMPLATE_', '')}${name}[${index}]"]
  <div id="quantification-${(template?string('template', ''))}" class="quantification form-group simpleBox">
    <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
    <div class="form-group">
      [#-- Index --]
      <div class="leftHead gray sm">
        <span class="index">${index+1}</span>
      </div>
      [#-- Remove Button --]
      [#if editable]<div class="removeIcon removeElement removeQuantification" title="Remove"></div>[/#if]
    </div>
    [#-- Units --]
    <div class="form-group row">
      [#-- Quantification type --]
      <div class="col-md-4">
        [@customForm.select name="${customName}.quantificationType.id" value="${(element.quantificationType.id)!-1}" className="setSelect2" i18nkey="study.quantificationType" listName="quantificationTypes"  keyFieldName="id" displayFieldName="name" required=(editable && reportingActive && additionalRequired) editable=editable /]
      </div>
      [#-- Number --]
      <div class="col-md-4">
        [@customForm.input name="${customName}.number" type="number" i18nkey="study.quantification.number" className="numericInput" required=(editable && reportingActive && additionalRequired) editable=editable /]
      </div>
      [#-- Unit --]
      <div class="col-md-4"> 
        [@customForm.input name="${customName}.targetUnit" i18nkey="study.quantification.targetUnit" className="" required=(editable && reportingActive && additionalRequired) editable=editable /]
      </div> 
    </div>
    [#-- Comments --]
    <div class="form-group">
      [@customForm.textArea name="${customName}.comments" i18nkey="study.quantification.comments" help="study.quantification.comments.help"  placeholder="" className="" required=(editable && reportingActive && additionalRequired) editable=editable /]
    </div>
  </div>
[/#macro]

[#function findElementID list id]
  [#list (list)![] as item]
    [#if (item.repIndGeographicScope.id == id)!false][#return true][/#if]
  [/#list]
  [#return false]
[/#function]

[#macro publicationMacro name element index=-1 template=false class=""  ]
  [#local customName = "${template?string('_TEMPLATE_', '')}${name}[${index}]"]
  <div id="studyPublication-${(template?string('template', ''))}" class="studyPublication form-group grayBox ${class}">
    <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
    <span class="pull-left" style="width:4%"><strong><span class="indexTag">${index + 1}</span>.</strong></span>
    <span class="pull-left" style="width:30%; margin-left: 12px ">[@customForm.input name="${customName}.name" showTitle=false i18nkey="" className="" editable=editable required=false /]</span>
    <span class="pull-left" style="width:30%; margin-left: 12px">[@customForm.input name="${customName}.position" showTitle=false i18nkey="" className="" editable=editable required=false /]</span>
    <span class="pull-left" style="width:30%; margin-left: 12px">[@customForm.input name="${customName}.affiliation" showTitle=false i18nkey="" className="" editable=editable required=false /]</span>

    [#if editable]<div class="removeElement sm removeIcon removePublication ${class}" title="Remove"></div>[/#if]
    <div class="clearfix"></div>
  </div>
    
[/#macro]
[#-- ════════════════════════════════════════════════════════════════════════════════════════════════════════════════
     A2-2743: the cluster OICR form's General Information tab, drawn as the design's cards over the deliverable form
     redesign (.dlf). It is the OICR branch of studyMacro (study type 1) with the same customForm calls, names and
     classes, because projectStudy.js, fieldsValidation.js, the feedback module and the save read them. studyMacro
     stays as it was for the other study types and for studies/study.ftl.
     Three controls are redrawn over hidden fields that keep posting: the level of maturity (radio cards), the
     geographic scopes (checkbox cards over the list component) and the cross-cutting markers (segmented 0 / 1 / 2 /
     N/A); oicrForm.js syncs them and fires the events projectStudy.js listens to.
     ════════════════════════════════════════════════════════════════════════════════════════════════════════════════ --]
[#-- A2-2743: a field's label line in the redesigned OICR form. The field itself is drawn with showTitle=false, so the
     label, its "*", the "What to cover" toggle and the feedback bubble live here: `bubble` names the field whose
     bubble customForm prints after the control, and oicrForm.js moves it onto this line. Hints and toggles are
     editing help: read-only shows neither. --]
[#macro ocfLabel label for="" required=false optional=false hint="" hintParam="" moreId="" moreKey="study.redesign.whatToCover" lessKey="study.redesign.whatToCover.hide" bubble="" id="" heading=false]
  <div class="ocf-label[#if heading] ocf-label--heading[/#if]"[#if bubble?has_content] data-ocf-bubble-for="${bubble}"[/#if]>
    [#if heading]
      <h2 class="dlf-card__title"[#if id?has_content] id="${id}"[/#if]>[#if for?has_content]<label for="${for}">[@s.text name=label /]</label>[#else][@s.text name=label /][/#if]</h2>
    [#else]
      <label class="ocf-label__text"[#if for?has_content] for="${for}"[/#if][#if id?has_content] id="${id}"[/#if]>[@s.text name=label /]</label>
    [/#if]
    [#if required && editable]<span class="ocf-label__req" aria-hidden="true">*</span>[/#if]
    [#if optional && editable]<span class="ocf-label__opt">[@s.text name="study.redesign.optional" /]</span>[/#if]
    [#if moreId?has_content && editable]
      <button type="button" class="ocf-more__toggle" aria-expanded="false" aria-controls="${moreId}" data-ocf-more data-label-more="[@s.text name=moreKey /]" data-label-less="[@s.text name=lessKey /]">[@s.text name=moreKey /]</button>
    [/#if]
    [#nested /]
  </div>
  [#if hint?has_content && editable]<p class="ocf-hint">[#if hintParam?has_content][@s.text name=hint][@s.param]${hintParam}[/@s.param][/@s.text][#else][@s.text name=hint /][/#if]</p>[/#if]
[/#macro]

[#-- The "What to cover" text a label's toggle opens --]
[#macro ocfMore id]
  [#if editable]<div class="ocf-more" id="${id}" hidden>[#nested /]</div>[/#if]
[/#macro]

[#-- A2-2743: one quantification as the design's card. Same names, classes (.quantification, span.index,
     .removeQuantification) and template id as quantificationMacro, which projectStudy.js clones and reindexes. --]
[#macro oicrQuantification name element index=-1 template=false additionalRequired=false]
  [#local customName = "${template?string('_TEMPLATE_', '')}${name}[${index}]"]
  [#local rowRequired = (editable && reportingActive && additionalRequired) /]
  <div id="quantification-${(template?string('template', ''))}" class="quantification ocf-quant">
    <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
    <div class="ocf-quant__head">
      <span class="ocf-quant__title">[@s.text name="study.redesign.quant.item"][@s.param]<span class="index">${index+1}</span>[/@s.param][/@s.text]</span>
      [#if editable]<div class="removeIcon removeElement removeQuantification" title="Remove"></div>[/#if]
    </div>
    <div class="ocf-quant__grid">
      <div class="ocf-quant__field">
        [@ocfLabel label="study.redesign.quant.type" for="${customName}.quantificationType.id" bubble="${customName}.quantificationType.id" /]
        [@customForm.select name="${customName}.quantificationType.id" value="${(element.quantificationType.id)!-1}" className="setSelect2" showTitle=false listName="quantificationTypes" keyFieldName="id" displayFieldName="name" required=rowRequired editable=editable /]
      </div>
      <div class="ocf-quant__field">
        [@ocfLabel label="study.redesign.quant.number" for="${customName}.number" bubble="${customName}.number" /]
        [@customForm.input name="${customName}.number" type="number" showTitle=false className="numericInput" required=rowRequired editable=editable /]
      </div>
      <div class="ocf-quant__field">
        [@ocfLabel label="study.redesign.quant.unit" for="${customName}.targetUnit" bubble="${customName}.targetUnit" /]
        [@customForm.input name="${customName}.targetUnit" showTitle=false placeholder="study.redesign.quant.unit.placeholder" required=rowRequired editable=editable /]
      </div>
    </div>
    <div class="ocf-quant__field">
      [@ocfLabel label="study.redesign.quant.comments" for="${customName}.comments" hint="study.redesign.quant.comments.hint" bubble="${customName}.comments" /]
      [@customForm.textArea name="${customName}.comments" showTitle=false required=rowRequired editable=editable /]
    </div>
  </div>
[/#macro]

[#-- A2-2743 redesign: the OICR General Information tab, card by card. Every field keeps the name, class and
     customForm markup projectStudy.js, the feedback bubbles and the save read; only the presentation is the design's.
     Labels and hints are the design's (study.redesign.*): the study.generalInformation.* texts stay for the PDF,
     other units and the legacy form. --]
[#macro oicrGeneral element name]
  [#local customName = "${name}"/]
  [#local isNew = (action.isEvidenceNew(element.id))!false /]
  [#local info = (element.projectExpectedStudyInfo)! /]
  [#local isPolicy = ((info.isContribution)!false) ]
  [#local stageProcessOne = ((info.repIndStageProcess.id == 1))!false ]
  [#if ((info.status)?has_content) && ((info.status.id)?has_content)]
    [#local validateIsProgressWithStatus = action.validateIsProgressWithStatus(info.status.id) /]
  [#else]
    [#local validateIsProgressWithStatus = true /]
  [/#if]
  [#local cardReq = (editable && reportingActive) /]
  [#local listReq = (!action.isPOWB() && validateIsProgressWithStatus!true) /]

  <span id="parentID" style="display: none;">${expectedID!}</span>
  <span id="phaseID" style="display: none;">${phaseID!}</span>
  <span id="userID" style="display: none;">${currentUser.id!}</span>
  <span id="projectID" style="display: none;">${projectID!}</span>
  <span id="userCanManageFeedback" style="display: none;">${(action.canManageFeedback(projectID)?c)!}</span>
  <span id="userCanLeaveComments" style="display: none;">${(action.canLeaveComments(projectID)?c)!}</span>
  <span id="userCanApproveFeedback" style="display: none;">${(action.canApproveComments(projectID)?c)!"false"}</span>
  <span id="canTrackComments" style="display: none;">${(action.canTrackComments()?c)!"false"}</span>
  <span id="isFeedbackActive" style="display: none;">${(action.hasSpecificities('feedback_active')?c)!}</span>
  <span id="isFeedbackNewCommentFieldActive" style="display: none;">${(action.hasSpecificities('feedback_new_comment_field_active')?c)!"false"}</span>
  <span id="isSuperAdmin" style="display: none;">${(action.canAccessSuperAdmin()?c)!}</span>
  <input type="hidden" id="sectionNameToFeedback" value="study" />

  <div id="study-0" class="caseStudy evidenceBlock isNew-${isNew?string} dlf-sections ocf-general">

    [#-- ─── Outcome statement ──────────────────────────────────────────────── --]
    [#local titleName = "${customName}.projectExpectedStudyInfo.title" /]
    [#local shortName = "${customName}.projectExpectedStudyInfo.outcomeImpactStatement" /]
    <section class="dlf-card" id="sec-outcome">
      <h2 class="dlf-card__title">[@s.text name="study.redesign.section.outcome" /]</h2>
      [#-- A2-2743 (approved): the counter allows 30 words, as ProjectExpectedStudiesValidator does --]
      <div class="dlf-field ocf-field">
        [@ocfLabel label="study.generalInformation.title" for=titleName required=true hint="study.redesign.title.hint" bubble=titleName /]
        [@customForm.input name=titleName showTitle=false className="limitWords-30" required=true editable=editable /]
      </div>
      <div class="dlf-field ocf-field">
        [@ocfLabel label="study.redesign.short" for=shortName hint="study.redesign.short.hint" moreId="ocf-more-short" bubble=shortName /]
        [@ocfMore id="ocf-more-short"]<ul><li>[@s.text name="study.redesign.short.more1" /]</li><li>[@s.text name="study.redesign.short.more2" /]</li><li>[@s.text name="study.redesign.short.more3" /]</li></ul>[/@ocfMore]
        [@customForm.textArea name=shortName showTitle=false className="limitWords-80" required=false editable=editable /]
      </div>
    </section>

    [#-- ─── Level of maturity: radio cards over the hidden select ─────────── --]
    [#local stageId = (info.repIndStageStudy.id?c)!"" /]
    [#local stageName = "${customName}.projectExpectedStudyInfo.repIndStageStudy.id" /]
    <section class="dlf-card stageProcessOne" id="sec-maturity">
      [@ocfLabel label="study.generalInformation.maturityChange" heading=true id="ocf-maturity-label" required=cardReq hint="study.redesign.maturity.hint" bubble=stageName /]
      <div class="ocf-cards" role="radiogroup" aria-labelledby="ocf-maturity-label" data-ocf-cards-for="${stageName}">
        [#list (stageStudies)![] as stage]
          [#local isOn = (stage.id?c == stageId) /]
          [#local stageKey = "study.redesign.stage.${stage.id?c}" /]
          [#local sphere][@s.text name="${stageKey}.sphere" /][/#local]
          [#local hasKeys = (sphere?markup_string != "${stageKey}.sphere") /]
          <button type="button" class="ocf-card[#if isOn] is-on[/#if]" role="radio" aria-checked="${isOn?c}" data-value="${stage.id?c}"[#if !editable] disabled[/#if]>
            <span class="ocf-card__head"><span class="ocf-card__ring" aria-hidden="true"></span><span class="ocf-card__label">${(stage.name)!}</span>[#if hasKeys]<span class="ocf-card__sphere">${sphere}</span>[/#if]</span>
            [#if hasKeys]
              <span class="ocf-card__text">[@s.text name="${stageKey}.desc" /]</span>
              <span class="ocf-card__ev">[@s.text name="${stageKey}.evidence" /]</span>
            [#elseif (stage.description)?has_content]
              <span class="ocf-card__text">${stage.description}</span>
            [/#if]
          </button>
        [/#list]
      </div>
      <div class="ocf-hiddenField">
        [@customForm.select name=stageName value="${(info.repIndStageStudy.id)!-1}" showTitle=false listName="stageStudies" keyFieldName="id" displayFieldName="composedName" required=false editable=editable /]
      </div>
    </section>

    [#-- ─── Geographic scope: checkboxes over the multi-scope list component projectStudy.js drives ─── --]
    [#-- The innovation and deliverable forms take one scope (radio cards); an OICR takes several, so the same card
         carries a checkbox. The list component stays, out of sight: it posts the scopes and fires the events that
         open the regions and countries. --]
    [#local geographicScopeList = (element.geographicScopes)![] ]
    [#local isRegional =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeRegional) /]
    [#local isMultiNational = findElementID(geographicScopeList,  action.reportingIndGeographicScopeMultiNational) /]
    [#local isNational =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeNational) /]
    [#local isSubNational =   findElementID(geographicScopeList,  action.reportingIndGeographicScopeSubNational) /]
    [#local savedScopeIds = geographicScopeList?map(g -> ((g.repIndGeographicScope.id)!0)?c) /]
    [#local scopesName = "${customName}.geographicScopes" /]
    [#local scopeCommentsName = "${customName}.projectExpectedStudyInfo.scopeComments" /]
    <section class="dlf-card geographicScopeBlock" id="dlf-geographicScope">
      <h2 class="dlf-card__title">[@s.text name="study.redesign.geo.title" /]</h2>
      <div class="geographicScopeInput ocf-geo">
        <div class="dlf-field ocf-field">
          [@ocfLabel label="study.redesign.geo.scope" id="ocf-scope-label" required=(editable && listReq) bubble=scopesName /]
          <div class="ocf-scopes" role="group" aria-labelledby="ocf-scope-label" data-ocf-scopes>
            [#list (geographicScopes)![] as scope]
              [#local isOn = savedScopeIds?seq_contains(scope.id?c) /]
              [#local scopeKey = "study.redesign.geo.scope.${scope.id?c}" /]
              [#local scopeLabel][@s.text name=scopeKey /][/#local]
              <button type="button" class="ocf-scope[#if isOn] is-on[/#if]" aria-pressed="${isOn?c}" data-value="${scope.id?c}"[#if !editable] disabled[/#if]><span class="ocf-scope__box" aria-hidden="true"></span>[#if scopeLabel?markup_string != scopeKey]${scopeLabel}[#else]${scope.name}[/#if]</button>
            [/#list]
          </div>
          [#if editable]<p class="ocf-hint ocf-scopes__note" data-ocf-scope-note data-note="[@s.text name="study.redesign.geo.note" /]" data-note-tbd="[@s.text name="study.redesign.geo.noteTbd" /]">[@s.text name="study.redesign.geo.note" /]</p>[/#if]
          <div class="ocf-hiddenField">
            [@customForm.elementsListComponent name=scopesName elementType="repIndGeographicScope" elementList=element.geographicScopes listName="geographicScopes" keyFieldName="id" displayFieldName="name" showTitle=false required=listReq /]
          </div>
        </div>
        <div class="dlf-field ocf-field regionalBlock" data-ocf-placeholder="[@s.text name="study.redesign.geo.regions.add" /]" style="display:${(isRegional)?string('block','none')}">
          [@ocfLabel label="study.redesign.geo.regions" required=editable bubble="${customName}.studyRegions" /]
          [@customForm.elementsListComponent name="${customName}.studyRegions" elementType="locElement" elementList=element.studyRegions listName="regions" keyFieldName="id" displayFieldName="composedName" showTitle=false required=false /]
        </div>
        <div class="dlf-field ocf-field nationalBlock" style="display:${(isMultiNational || isNational || isSubNational)?string('block','none')}">
          [@ocfLabel label="study.redesign.geo.countries" for="${customName}.countriesIds" required=(editable && listReq) bubble="${customName}.countriesIds" /]
          [@customForm.select name="${customName}.countriesIds" label="" showTitle=false listName="countries" keyFieldName="isoAlpha2" displayFieldName="name" value="${customName}.countriesIds" multiple=true required=listReq className="countriesSelect" disabled=!editable/]
        </div>
        <div class="dlf-field ocf-field">
          [@ocfLabel label="study.redesign.geo.comments" for=scopeCommentsName optional=true hint="study.redesign.geo.comments.hint" bubble=scopeCommentsName /]
          [@customForm.textArea name=scopeCommentsName showTitle=false className="limitWords-30" editable=editable required=false/]
        </div>
      </div>
    </section>

    [#-- ─── Key contributors ──────────────────────────────────────────────── --]
    <section class="dlf-card keyContributions" id="sec-contrib">
      <h2 class="dlf-card__title">[@s.text name="study.redesign.contrib.title" /]</h2>
      <div class="ocf-twoCols ocf-contrib">
        <div class="dlf-field ocf-field" data-ocf-placeholder="[@s.text name="study.redesign.contrib.centers.add" /]" data-ocf-empty="[@s.text name="study.redesign.contrib.centers.empty" /]">
          [@ocfLabel label="study.redesign.contrib.centers" required=(editable && !action.isAWPBActive()) hint="study.redesign.contrib.centers.hint" bubble="${customName}.centers" /]
          [@customForm.elementsListComponent name="${customName}.centers" elementType="institution" elementList=element.centers listName="centers" keyFieldName="id" displayFieldName="composedName" showTitle=false required=listReq /]
          [#if editable]
            [#local partnersLink][@s.url namespace="/projects" action='${crpSession}/partners'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
            <p class="ocf-hint ocf-hint--after ocf-contrib__note">[@s.text name="study.redesign.contrib.footer"][@s.param]<a href="${partnersLink}" target="_blank" rel="noopener">[@s.text name="study.redesign.contrib.footer.link" /]</a>[/@s.param][/@s.text]</p>
          [/#if]
        </div>
        <div class="dlf-field ocf-field stageProcessOne" data-ocf-placeholder="[@s.text name="study.redesign.contrib.partners.add" /]" data-ocf-empty="[@s.text name="study.redesign.contrib.partners.empty" /]">
          [@ocfLabel label="study.redesign.contrib.partners" optional=true hint="study.redesign.contrib.partners.hint" bubble="${customName}.institutions" /]
          [@customForm.elementsListComponent name="${customName}.institutions" elementType="institution" elementList=element.institutions listName="institutions" keyFieldName="id" displayFieldName="composedName" showTitle=false required=false /]
          [#if editable]
            [#-- A partner missing from the MARLO catalogue is requested here (utils.js opens a.popup in a window) --]
            [#local requestLink][@s.url namespace="/projects" action='${crpSession}/partnerSave'][@s.param name='expectedID']${(expectedID)!}[/@s.param][/@s.url][/#local]
            <p id="addPartnerText" class="ocf-hint ocf-hint--after ocf-contrib__note">[@s.text name="study.redesign.contrib.partners.request"][@s.param]<a class="popup" href="${requestLink}">[@s.text name="study.redesign.contrib.partners.request.link" /]</a>[/@s.param][/@s.text]</p>
          [/#if]
        </div>
      </div>
    </section>

    [#-- ─── CGIAR innovations or findings ─────────────────────────────────── --]
    [#local innovName = "${customName}.projectExpectedStudyInfo.cgiarInnovation" /]
    [#local otherInnovName = "${customName}.projectExpectedStudyInfo.otherInnovationsNarrative" /]
    [#local innovReq = (editable && !(isPolicy && stageProcessOne) && !action.isPOWB() && validateIsProgressWithStatus!true) /]
    <section class="dlf-card stageProcessOne" id="sec-innov">
      <h2 class="dlf-card__title">[@s.text name="study.redesign.section.innovations" /]</h2>
      <div class="dlf-field ocf-field">
        [@ocfLabel label="study.redesign.innov.narrative" for=innovName required=innovReq hint="study.redesign.innov.narrative.hint" bubble=innovName /]
        [@customForm.textArea name=innovName showTitle=false required=innovReq editable=editable /]
      </div>
      <div class="dlf-field ocf-field" data-ocf-placeholder="[@s.text name="study.redesign.innov.add" /]" data-ocf-empty="[@s.text name="study.redesign.innov.empty" /]">
        [@ocfLabel label="study.redesign.innov.list" optional=true bubble="${customName}.innovations" /]
        [@customForm.elementsListComponent name="${customName}.innovations" elementType="projectInnovation" elementList=element.innovations listName="innovationsList" keyFieldName="id" displayFieldName="composedNameAlternative" showTitle=false required=false /]
      </div>
      <div class="dlf-field ocf-field">
        [@ocfLabel label="study.redesign.innov.other" for=otherInnovName optional=true bubble=otherInnovName /]
        [@customForm.textArea name=otherInnovName showTitle=false className="limitWords-30" required=false editable=editable /]
      </div>
    </section>

    [#-- ─── Link to Performance Indicators ────────────────────────────────── --]
    [#local indLink][@s.url namespace=namespace action="${crpSession}/contributionsCrpList"][@s.param name='projectID']${projectID?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
    [#local indLinkHtml]<a href="${indLink}" target="_blank" rel="noopener">[@s.text name="study.redesign.ind.hint.link" /]</a>[/#local]
    <section class="dlf-card block-studyMilestoneLink" id="sec-ind">
      [@ocfLabel label="study.redesign.ind.title" heading=true required=(editable && !action.isPOWB() && validateIsProgressWithStatus!true) hint="study.redesign.ind.hint" hintParam=indLinkHtml bubble="${customName}.crpOutcomes" /]
      [@customForm.input name="${customName}.projectExpectedStudyInfo.hasMilestones" showTitle=false value="true" type="hidden" display=false /]
      <div class="dlf-field ocf-indicators" data-ocf-placeholder="[@s.text name="study.redesign.ind.add" /]" data-ocf-empty="[@s.text name="study.redesign.ind.empty" /]">
        [@customForm.elementsListComponent name="${customName}.crpOutcomes" elementType="crpOutcome" elementList=(element.crpOutcomes)![] listName="crpOutcomes" keyFieldName="id" displayFieldName="composedName" showTitle=false required=(!action.isPOWB()) /]
      </div>
    </section>

    [#-- ─── Elaboration ───────────────────────────────────────────────────── --]
    [#local elabName = "${customName}.projectExpectedStudyInfo.elaborationOutcomeImpactStatement" /]
    <section class="dlf-card stageProcessOne" id="sec-elab">
      [@ocfLabel label="study.redesign.elab.title" heading=true for=elabName required=(editable && reportingActive) hint="study.redesign.elab.hint" moreId="ocf-more-elab" moreKey="study.redesign.moreGuidance" lessKey="study.redesign.lessGuidance" bubble=elabName /]
      [@ocfMore id="ocf-more-elab"][@s.text name="study.redesign.elab.more" /][/@ocfMore]
      <div class="dlf-field">
        [@customForm.textArea name=elabName showTitle=false className="limitWords-400" required=(editable && reportingActive) editable=editable /]
      </div>
    </section>

    [#-- ─── Evidence and references ───────────────────────────────────────── --]
    [#local refsReq = (!action.isAWPBActive()) /]
    [#local refsTextName = "${customName}.projectExpectedStudyInfo.referencesText" /]
    <section class="dlf-card stageProcessOne" id="sec-refs">
      [@ocfLabel label="study.redesign.refs.title" heading=true required=(editable && refsReq) hint="study.redesign.refs.hint" hintParam="93" bubble=refsTextName]
        <span class="ocf-label__meta" data-ocf-count="refs" data-one="[@s.text name="study.redesign.refs.count.one" /]" data-many="[@s.text name="study.redesign.refs.count" /]"></span>
      [/@ocfLabel]
      <div class="dlf-field ocf-refs__legacy">
        [@customForm.textAreaReferences name=refsTextName showTitle=false className="" required=refsReq editable=editable oldReference=((info.referencesText)?has_content) /]
      </div>
      <div class="referenceBlock ocf-refs">
        <div class="referenceList">
          <div class="ocf-refs__head" aria-hidden="true">
            <span>[@s.text name="study.redesign.refs.reference" /][#if editable && refsReq] <span class="ocf-label__req">*</span>[/#if]</span>
            <span>[@s.text name="study.redesign.refs.link" /][#if editable && refsReq] <span class="ocf-label__req">*</span>[/#if]</span>
            <span>[@s.text name="study.redesign.refs.externalAuthor" /]</span>
          </div>
          [#list (element.references)![{}] as link]
            [@customForm.references name="${customName}.references" element=link index=link_index class="references" /]
          [/#list]
        </div>
        [#if editable]
          <button type="button" class="addButtonReference ocf-addRow">
            <svg class="dlf-icon" width="13" height="13" viewBox="0 0 14 14" fill="none" aria-hidden="true"><path d="M7 2.6v8.8M2.6 7h8.8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>
            [@s.text name="study.redesign.refs.add" /]
          </button>
        [/#if]
      </div>
      [#-- Element item Template --]
      <div style="display:none">
        [@customForm.references name="${customName}.references" element={} index=-1 template=true class="references" /]
      </div>
    </section>

    [#-- ─── Quantification ────────────────────────────────────────────────── --]
    [#local isLeverOfMaturityOnThree = (info.repIndStageStudy.id == 3)!false]
    <section class="dlf-card stageProcessOne quantificationsBlockContainer" id="sec-quant" data-ocf-quant>
      <div class="ocf-label ocf-label--heading" data-ocf-bubble-for="expectedStudy.projectExpectedStudyInfo.quantification">
        <h2 class="dlf-card__title">[@s.text name="study.redesign.quant.title" /]</h2>
        [#if editable && reportingActive][@customForm.req required=isLeverOfMaturityOnThree /][/#if]
        <span class="ocf-label__meta" data-ocf-count="quant" data-one="[@s.text name="study.redesign.quant.count.one" /]" data-many="[@s.text name="study.redesign.quant.count" /]"></span>
        <div class="commentNumberContainer">
          <div class="numberOfCommentsBubble"><p></p></div>
          <img src="${baseUrlCdn}/global/images/comment.png" class="qaComment" name="expectedStudy.projectExpectedStudyInfo.quantification" fieldID="" description="">
        </div>
      </div>
      [#if editable]
        <p class="ocf-hint">[@s.text name="study.redesign.quant.hint" /]
          <span data-ocf-quant-req[#if !isLeverOfMaturityOnThree] hidden[/#if]>[@s.text name="study.redesign.quant.req" /]</span><span data-ocf-quant-opt[#if isLeverOfMaturityOnThree] hidden[/#if]>[@s.text name="study.redesign.quant.complete" /]</span></p>
      [/#if]
      <div class="quantificationsBlock">
        <div class="quantificationsList">
          [#list (element.quantifications)![] as item]
            [@oicrQuantification name="${customName}.quantifications" element=item index=item_index additionalRequired=isLeverOfMaturityOnThree /]
          [/#list]
        </div>
        [#if editable]
          <button type="button" class="addStudyQualification ocf-addRow">
            <svg class="dlf-icon" width="13" height="13" viewBox="0 0 14 14" fill="none" aria-hidden="true"><path d="M7 2.6v8.8M2.6 7h8.8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>
            [@s.text name="study.redesign.quant.add" /]
          </button>
        [/#if]
      </div>
      [#-- Element item Template --]
      <div style="display:none">
        [@oicrQuantification name="${customName}.quantifications" element={} index=-1 template=true additionalRequired=isLeverOfMaturityOnThree /]
      </div>
    </section>

    [#-- ─── Cross-cutting markers: segmented 0 / 1 / 2 / N/A over the hidden selects ─── --]
    [#assign ccGuideSheetURL = "https://cgiar.sharepoint.com/sites/Alliance-SPRM/Shared%20Documents/Forms/AllItems.aspx?id=%2Fsites%2FAlliance%2DSPRM%2FShared%20Documents%2F2%20Project%20Monitoring%2C%20Evaluation%20and%20Learning%2F2%2E5%20Project%20Monitoring%2C%20Evaluation%20and%20Learning%20%2D%20Projects%2F2%2E5%2E1%20Outcomes%2F2024%20Outcomes%2F2024%20OICR%20Guidance%20Note%20%28Version%2014%2E08%2E24%29%2Epdf&parent=%2Fsites%2FAlliance%2DSPRM%2FShared%20Documents%2F2%20Project%20Monitoring%2C%20Evaluation%20and%20Learning%2F2%2E5%20Project%20Monitoring%2C%20Evaluation%20and%20Learning%20%2D%20Projects%2F2%2E5%2E1%20Outcomes%2F2024%20Outcomes" /]
    [#local ccCommentsName = "${customName}.projectExpectedStudyInfo.commentsRelevance" /]
    <section class="dlf-card" id="sec-cc">
      [@ocfLabel label="study.redesign.cc.title" heading=true hint="study.redesign.cc.hint" moreId="ocf-more-cc" moreKey="study.redesign.howToScore" lessKey="study.redesign.hideScoring"]
        <a class="ocf-label__link" href="${ccGuideSheetURL}" target="_blank" rel="noopener">[@s.text name="study.general.guideSheetURL.crossMarkets" /]</a>
      [/@ocfLabel]
      [@ocfMore id="ocf-more-cc"][@s.text name="study.redesign.cc.more" /][/@ocfMore]
      <div class="ocf-markers">
        [#-- Climate change is not validated (ProjectExpectedStudiesValidator), so it carries no "*" --]
        [#list [["genderLevel", "study.redesign.cc.gender", true], ["youthLevel", "study.redesign.cc.youth", true], ["capdevLevel", "study.redesign.cc.capdev", true], ["climateChangeLevel", "study.redesign.cc.climate", false]] as marker]
          [#local fieldName = "${customName}.projectExpectedStudyInfo.${marker[0]}.id" /]
          [#local currentLevel = ((info[marker[0]].id)?c)!"1" /]
          <div class="ccRelevanceBlock ocf-marker">
            [@ocfLabel label=marker[1] id="ocf-marker-${marker[0]}" required=(cardReq && marker[2]) /]
            <span class="ocf-seg ocf-seg--sm" role="radiogroup" aria-labelledby="ocf-marker-${marker[0]}" data-ocf-seg-for="${fieldName}">
              [#list (focusLevels)![] as level]
                [#local levelName = (level.powbName)!(level.name)!"" /]
                [#local levelKey = "study.redesign.cc.level.${level.id?c}" /]
                [#local levelLabel][@s.text name=levelKey /][/#local]
                [#local isOn = (level.id?c == currentLevel) /]
                <button type="button" class="ocf-seg__opt[#if isOn] is-on[/#if]" role="radio" aria-checked="${isOn?c}" data-value="${level.id?c}"
                  title="${levelName}[#if (level.definition)?has_content]: ${level.definition}[/#if]"[#if !editable] disabled[/#if]>[#if levelLabel?markup_string != levelKey]${levelLabel}[#else]${levelName}[/#if]</button>
              [/#list]
            </span>
            <div class="ocf-hiddenField">
              [@customForm.select name=fieldName label="" listName="focusLevels" keyFieldName="id" displayFieldName="powbName" value="${currentLevel}" required=cardReq editable=editable showTitle=false /]
            </div>
          </div>
        [/#list]
      </div>
      <div class="dlf-field ocf-field">
        [@ocfLabel label="study.redesign.cc.comments" for=ccCommentsName optional=true bubble=ccCommentsName /]
        [@customForm.textArea name=ccCommentsName showTitle=false className="limitWords-30" required=false editable=editable /]
      </div>
    </section>

    [#-- ─── Other cross-cutting dimensions ────────────────────────────────── --]
    [#local otherCrossCuttingSelection = (info.otherCrossCuttingSelection)!"" ]
    [#local occName = "${customName}.projectExpectedStudyInfo.otherCrossCuttingSelection" /]
    [#local occTextName = "${customName}.projectExpectedStudyInfo.otherCrossCuttingDimensions" /]
    <section class="dlf-card stageProcessOne" id="sec-occ">
      [@ocfLabel label="study.redesign.occ.title" heading=true id="ocf-occ-label" required=(editable && !action.isAWPBActive()) bubble=occName /]
      <p class="ocf-question">[@s.text name="study.redesign.occ.question" /]</p>
      <div class="ocf-radioSeg" role="radiogroup" aria-labelledby="ocf-occ-label">
        [#list ["Yes", "No", "NA"] as option]
          [@customForm.radioFlat id="option-${option}" name=occName i18nkey="study.generalInformation.otherCrossCutting${option}" value="${option}" checked=(otherCrossCuttingSelection == option) cssClass="radioType-otherCrossCuttingOption" cssClassLabel="font-normal" editable=true disabled=!editable canComment=(option_index == 0) /]
        [/#list]
      </div>
      [#local showOtherCrossCuttingOptionsComponent = (info.otherCrossCuttingSelection == "Yes")!false /]
      <div class="otherCrossCuttingOptionsComponent dlf-field ocf-field" style="display:${showOtherCrossCuttingOptionsComponent?string('block', 'none')}">
        [@ocfLabel label="study.redesign.occ.describe" for=occTextName required=true hint="study.redesign.occ.describe.hint" bubble=occTextName /]
        [@customForm.textArea name=occTextName showTitle=false className="limitWords-200" required=true editable=editable /]
      </div>
    </section>

  </div>
[/#macro]
