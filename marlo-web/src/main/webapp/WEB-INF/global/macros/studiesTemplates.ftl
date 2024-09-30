[#ftl]
[#import "/WEB-INF/global/macros/deliverableMacros.ftl" as deliverableMacros /]

[#macro studyMacro element name index=-1 template=false fromProject=true ]
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
  <span id="userCanLeaveComments" style="display: none;">${(action.canLeaveComments()?c)!}</span>
  <span id="isFeedbackActive" style="display: none;">${(action.hasSpecificities('feedback_active')?c)!}</span>
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
      <div class="form-group stageProcessOne">
        <div class="form-group">
          [#assign guideSheetURL = "https://cgiar.sharepoint.com/sites/Alliance-SPRM/Shared%20Documents/Forms/AllItems.aspx?id=%2Fsites%2FAlliance%2DSPRM%2FShared%20Documents%2F2%20Project%20Monitoring%2C%20Evaluation%20and%20Learning%2F2%2E5%20Project%20Monitoring%2C%20Evaluation%20and%20Learning%20%2D%20Projects%2F2%2E5%2E1%20Outcomes%2F2024%20Outcomes%2F2024%20OICR%20Guidance%20Note%20%28Version%2014%2E08%2E24%29%2Epdf&parent=%2Fsites%2FAlliance%2DSPRM%2FShared%20Documents%2F2%20Project%20Monitoring%2C%20Evaluation%20and%20Learning%2F2%2E5%20Project%20Monitoring%2C%20Evaluation%20and%20Learning%20%2D%20Projects%2F2%2E5%2E1%20Outcomes%2F2024%20Outcomes" /]
          <small class="pull-right"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" />[@s.text name="study.general.guideSheetURL.levelMaturity" /]</a> </small>
        </div>
        <label for="${customName}.projectExpectedStudyInfo.repIndStageStudy.id" class="label--2">[@s.text name="study.generalInformation.maturityChange" /]:[@customForm.req required=(editable && !action.isPOWB() && !(isPolicy && stageProcessOne) && validateIsProgressWithStatus!true) /]
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
        [#local isRegional =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeRegional) /]
        [#local isMultiNational = findElementID(geographicScopeList,  action.reportingIndGeographicScopeMultiNational) /]
        [#local isNational =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeNational) /]
        [#local isSubNational =   findElementID(geographicScopeList,  action.reportingIndGeographicScopeSubNational) /]
        
        <label for="" class="${isOutcomeCaseStudy?then('label--2','')}">[@s.text name="study.generalInformation.geographicScopeTopic" /]:[@customForm.req required=(editable && !action.isPOWB() && validateIsProgressWithStatus!true) /]
          <div>
            [#if isOutcomeCaseStudy]
              [@customForm.helpLabel name="study.generalInformation.geographicScopeTopic.note" showIcon=false isNote=true editable=editable/]
            [/#if]
          </div>
        </label>
        <div class="form-group ${isOutcomeCaseStudy?then('','simpleBox')} geographicScopeInput">
          <div class="form-group row">
            <div class="col-md-12">
              <label for="${customName}.geographicScopes" class="col-md-4">[@s.text name="study.generalInformation.geographicScope" /]: </label>
              <label for="" name="study.generalInformation.geographicImpact" class="col-md-8" style="display:${(isMultiNational || isNational || isSubNational || isRegional)?string('block','none')}">[@s.text name="study.generalInformation.geographicImpact" /]:</label>
            </div>
          </div>
          <div class="form-group row">
            <div class="form-group col-md-4">
              [#-- Geographic Scope --]
              [@customForm.elementsListComponent name="${customName}.geographicScopes" elementType="repIndGeographicScope" elementList=element.geographicScopes  listName="geographicScopes" keyFieldName="id" displayFieldName="name" showTitle=false isFlex=true required=(!action.isPOWB() && validateIsProgressWithStatus!true) /]
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
      
      [#-- 3. (Other Studies) Link to Common Results Reporting Indicator #I3 --]
      [#if !action.isAiccra()]
        [#if isOutcomeCaseStudy]
        <div class="form-group">
          [#-- Does this outcome reflect a contribution of the CGIAR in influencing or modifying policies/ strategies / laws/ regulations/ budgets/ investments or  curricula?  --]
          <div class="form-group">
            
            <label for="">[@s.text name="study.generalInformation.reportingIndicatorThree" /]:[@customForm.req required=(editable && !action.isPOWB() && validateIsProgressWithStatus!true) /][@customForm.helpLabel name="study.generalInformation.reportingIndicatorThree.help" showIcon=false editable=editable/]</label>
            [#assign studyIndicatorThree = "studyIndicatorThree"]
            [#assign showPolicyIndicator = (element.projectExpectedStudyInfo.isContribution?string)!"" /]
            [@customForm.radioFlat id="${studyIndicatorThree}-yes" name="${name}.projectExpectedStudyInfo.isContribution" label="Yes" value="true" checked=(showPolicyIndicator == "true") cssClass="radioType-${studyIndicatorThree}" cssClassLabel="radio-label-yes" editable=editable /]
            [@customForm.radioFlat id="${studyIndicatorThree}-no" name="${name}.projectExpectedStudyInfo.isContribution" label="No" value="false" checked=(showPolicyIndicator == "false") cssClass="radioType-${studyIndicatorThree}" cssClassLabel="radio-label-no" editable=editable /]
          </div>        
          [#-- Disaggregates for CGIAR Indicator   --]
          <div class="form-group simpleBox block-${studyIndicatorThree}" style="display:${(showPolicyIndicator == "true")?string('block','none')}">
            [@customForm.elementsListComponent name="${customName}.policies" elementType="projectPolicy" elementList=element.policies label="study.generalInformation.policies"  listName="policyList" keyFieldName="id" displayFieldName="composedNameAlternative"/]
            [#-- Note --]
            <div class="note">[@s.text name="study.generalInformation.policies.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/policies'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">clicking here</a>[/@][/@]</div>
            [#local policiesGuideSheetURL = "https://drive.google.com/file/d/1GYLsseeZOOXF9zXNtpUtE1xeh2gx3Vw2/view" /]
            <small class="pull-right"><a href="${policiesGuideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" /> #I1 Policies -  Guideline </a> </small>
            <br>
          </div>
        </div>
        [/#if]
      [/#if]

      [#-- 5. Key Contributors  --]
      <div class="form-group keyContributions">
        [#if isOutcomeCaseStudy || !fromProject]
          <label for="" class="label--2">[@s.text name="study.generalInformation.${isOutcomeCaseStudy?string('keyContributors','keyContributorsOther')}" /]:</label>
          <div class="note"><span class="glyphicon glyphicon-question-sign"></span> [@s.text name="study.generalInformation.ppapartner.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/partners'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">&nbsp;clicking here</a>[/@] [/@]</div>
        [/#if]
        [#-- CRPs - To be Removed --]
        [#if !action.isAiccra()]
          [#if isOutcomeCaseStudy]
          <div class="form-group simpleBox">
            [@customForm.elementsListComponent name="${customName}.crps" elementType="globalUnit" elementList=element.crps label="study.generalInformation.keyContributors.crps"  listName="crps" keyFieldName="id" displayFieldName="composedName" required=false /]
          </div>
          [/#if]
        [/#if]
        [#-- Flagships - To be Removed --]
        [#if !action.isAiccra()]
          [#if isOutcomeCaseStudy || !fromProject]
          <div class="form-group simpleBox stageProcessOne">
            [#if !fromProject && editable]
              <p class="note">To the [@s.text name="programManagement.flagship.title"/](s) selected, the system grants permission to edit this ${(element.projectExpectedStudyInfo.studyType.name)!'study'} to their [@s.text name="CrpProgram.leaders"/] and [@s.text name="CrpProgram.managers"/]</p>
            [/#if]
            [@customForm.elementsListComponent name="${customName}.flagships" elementType="crpProgram" id="FP" elementList=element.flagships label="study.generalInformation.keyContributors.flagships"  listName="flagshipList" keyFieldName="id" displayFieldName="composedName" required=false /]
          </div>
          [/#if]
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
      <div class="form-group ${isOutcomeCaseStudy?then('','simpleBox')}">
        [#if isOutcomeCaseStudy]
          <label for="" class="label--2">[@s.text name="study.generalInformation.stratgicResultsLink" /]:[@customForm.req required=(editable && !action.isPOWB() && validateIsProgressWithStatus!true) /]
            [@customForm.helpLabel name="study.generalInformation.stratgicResultsLink.help" showIcon=false editable=editable/]
          </label>
        [#elseif !action.isAiccra()]
          <label for="">[@s.text name="study.generalInformation.relevantTo" /]:[@customForm.req required=(editable && !action.isPOWB() && validateIsProgressWithStatus!true)/]
          </label> 
        [/#if]
        [#-- Sub IDOs (maxLimit=3) --]
        [#if !action.isAiccra()]
          <div class="form-group simpleBox">
            [@customForm.elementsListComponent name="${customName}.subIdos" elementType="srfSubIdo" elementList=element.subIdos label="study.generalInformation.stratgicResultsLink.subIDOs"  listName="subIdos" maxLimit=3 keyFieldName="id" displayFieldName="description" hasPrimary=true/]
          </div> 
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
          <div class="row">
          [#list ["targetsOptionYes", "targetsOptionNo", "targetsOptionTooEarlyToSay"] as option]
            <div class="col-md-3">
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
       
       <div class="form-group ${isOutcomeCaseStudy?then('','simpleBox')} block-${studyMilestoneLink}" style="display:${(showMilestoneIndicator == "true")?string('block','none')}">
          [@customForm.elementsListComponent name="${customName}.crpOutcomes" elementType="crpOutcome" elementList=(element.crpOutcomes)![] label="study.generalInformation.outcomes"  listName="crpOutcomes" keyFieldName="id" displayFieldName="composedName" showTitle=false required=(!action.isPOWB() && validateIsProgressWithStatus!true) /]
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
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.elaborationOutcomeImpactStatement" i18nkey="study.generalInformation.elaborationStatement" help="study.generalInformation.elaborationStatement.help" helpIcon=false isNote=true className="limitWords-400 margin-top-10" required=(editable && !(isPolicy && stageProcessOne) && !action.isPOWB() && validateIsProgressWithStatus!true) editable=editable isMainTitle=true /]
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
            [@customForm.textAreaReferences name="${customName}.projectExpectedStudyInfo.referencesText" i18nkey="study.generalInformation.referencesEvidenceCited" help="study.generalInformation.referencesCited.help2" helpIcon=false isNote=true className="" required=false editable=editable oldReference=true /]        
            </div>
        [#else]
          <!-- <div class="feedback-flex-items"> -->
            [@customForm.textAreaReferences name="${customName}.projectExpectedStudyInfo.referencesText" i18nkey="study.generalInformation.referencesEvidenceCited" help="study.generalInformation.referencesCited.help2" helpIcon=false className="" required=false editable=editable oldReference=false isNote=true isMainTitle=true /]        
          <!-- </div> -->
        [/#if]      
          
            <label style="margin-top: 5px;">[@s.text name="${customName}.multireferences"][/@s.text]</label>
            <div class="referenceBlock ">
              <div class="referenceList">
                <div class="row">
                  <div class="col-sm-6 colTitleCenter" style="font-weight: 600; text-align: center;">Reference[@customForm.req required=(editable && validateIsProgressWithStatus!true)  /]</div>
                  <div class="col-sm-4 colTitleCenter" style="font-weight: 600; text-align: center;">URL[@customForm.req required=(editable && validateIsProgressWithStatus!true)  /]</div>
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
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        [#--
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.quantification" i18nkey="study.quantification" help="study.quantification.help" helpIcon=false className=" " required=editable && !(isPolicy && stageProcessOne) editable=editable /]
        --]
        <label for="" class="label--2">[@s.text name="study.generalInformation.quantification" /]:[@customForm.helpLabel name="study.generalInformation.quantification.help" isNote=true showIcon=false editable=editable/]</label><br />
        <div class="quantificationsBlock">
          <div class="quantificationsList">
          [#list (element.quantifications)![] as item]
            [@quantificationMacro name="${customName}.quantifications" element=item index=item_index /]
          [/#list]
          </div>
          [#if editable]
            <div class="addStudyQualification bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>[@s.text name="form.buttons.addStudyQualification"/]</div>
          [/#if]
        </div>
        [#-- Element item Template --]
        <div style="display:none">
          [@quantificationMacro name="${customName}.quantifications" element={} index=-1 template=true /]
        </div>
        <br />
      </div>
      [/#if]
      
      [#-- 11. Gender, Youth, and Capacity Development  --]
      [#if isOutcomeCaseStudy]
      <div class="form-group">
        [#assign ccGuideSheetURL = "https://drive.google.com/file/d/1oXb5UHABZIbyUUczZ8eqnDsgdzwABXPk/view?usp=sharing" /]
        <small class="pull-right"><a href="${ccGuideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" />[@s.text name="study.general.guideSheetURL.crossMarkets" /]</a> </small>
      </div>
      <div class="form-group">
        [@tag name="Indicator #3" /]
        <label for="" class="label--2">[@s.text name="study.generalInformation.crossCuttingRelevance" /]:
          [@customForm.helpLabel name="study.generalInformation.crossCuttingRelevance.help" isNote=true showIcon=false editable=editable/]
        </label>
        [@customForm.helpLabel name="study.generalInformation.crossCuttingRelevance.help2" showIcon=false editable=editable/]
        <div class="row">
          [#-- Gender --]
          <div class="ccRelevanceBlock col-md-3">
            <label for="">[@s.text name="study.generalInformation.genderRelevance" /]:[@customForm.req required=(editable && !action.isPOWB() && validateIsProgressWithStatus!true) /]</label>
            <div class="form-group">
              [#assign genderLevel = (element.projectExpectedStudyInfo.genderLevel.id)!1 ]
              [@customForm.select name="${customName}.projectExpectedStudyInfo.genderLevel.id" label="" listName="focusLevels" keyFieldName="id" displayFieldName="powbName" value="${genderLevel}" required=(editable && !action.isPOWB() && validateIsProgressWithStatus!true) editable=editable showTitle=false /]
            </div>
          </div>
          [#-- Youth  --]
          <div class="ccRelevanceBlock col-md-3">
            <label for="">[@s.text name="study.generalInformation.youthRelevance" /]:[@customForm.req required=(editable && !action.isPOWB() && validateIsProgressWithStatus!true) /]</label>
            <div class="form-group">
              [#assign youthLevel = (element.projectExpectedStudyInfo.youthLevel.id)!1 ]
              [@customForm.select name="${customName}.projectExpectedStudyInfo.youthLevel.id" label="" listName="focusLevels" keyFieldName="id" displayFieldName="powbName" value="${youthLevel}" required=(editable && !action.isPOWB() && validateIsProgressWithStatus!true) editable=editable showTitle=false /]
            </div> 
          </div>
          [#-- CapDev   --]
          <div class="ccRelevanceBlock col-md-3">
            <label for="">[@s.text name="study.generalInformation.capDevRelevance" /]:[@customForm.req required=(editable && !action.isPOWB() && validateIsProgressWithStatus!true) /]</label>
            <div class="form-group">
              [#assign capdevLevel = (element.projectExpectedStudyInfo.capdevLevel.id)!1 ]
              [@customForm.select name="${customName}.projectExpectedStudyInfo.capdevLevel.id" label="" listName="focusLevels" keyFieldName="id" displayFieldName="powbName" value="${capdevLevel}" required=(editable && !action.isPOWB() && validateIsProgressWithStatus!true) editable=editable showTitle=false /]
            </div>

          </div>
          [#-- Climate Change  --]
          <div class="ccRelevanceBlock col-md-3">
            <label for="">[@s.text name="study.generalInformation.climateChangeRelevance" /]:[@customForm.req required=(editable && !action.isPOWB() && validateIsProgressWithStatus!true) /]</label>
            <div class="form-group">
              [#assign climateChangeLevel = (element.projectExpectedStudyInfo.climateChangeLevel.id)!1 ]
              [@customForm.select name="${customName}.projectExpectedStudyInfo.climateChangeLevel.id" label="" listName="focusLevels" keyFieldName="id" displayFieldName="powbName" value="${climateChangeLevel}" required=(editable && !action.isPOWB() && validateIsProgressWithStatus!true) editable=editable showTitle=false /]
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
        <label for="" class="label--2">[@s.text name="study.generalInformation.otherCrossCutting" /]:</label> 
         <div class="feedback-flex-items">
          [@customForm.helpLabel name="study.generalInformation.otherCrossCuttingOptions" showIcon=false editable=editable/]<br />
        </div>
        <div class="form-group row">
          [#local otherCrossCuttingSelection = (element.projectExpectedStudyInfo.otherCrossCuttingSelection)!"" ]
          [#list ["Yes", "No", "NA"] as option]
            <div class="col-md-2">
              [@customForm.radioFlat id="option-${option}" name="${customName}.projectExpectedStudyInfo.otherCrossCuttingSelection" i18nkey="study.generalInformation.otherCrossCutting${option}" value="${option}" checked=(otherCrossCuttingSelection == option) cssClass="radioType-otherCrossCuttingOption" cssClassLabel="font-normal" editable=editable /] 
            </div>
          [/#list]
        </div>
        [#local showOtherCrossCuttingOptionsComponent = true /] 
        <div class="otherCrossCuttingOptionsComponent form-group" style="display:${showOtherCrossCuttingOptionsComponent?string('block', 'none')}">
          [@customForm.textArea name="${customName}.projectExpectedStudyInfo.otherCrossCuttingDimensions" i18nkey="study.generalInformation.otherCrossCutting.comments" help="study.generalInformation.otherCrossCutting.comments.help" helpIcon=false  className="limitWords-200" required=false editable=editable /]
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

[#macro studyGeneral element name index=-1 template=false fromProject=true]
  [#local customName = "${name}"/]
  [#local isOutcomeCaseStudy = ((element.projectExpectedStudyInfo.studyType.id == 1)!false)/]
  [#local isStatusExtended = (element.projectExpectedStudyInfo.status.id == 4)!false]
  [#local isOtherStatus = (element.projectExpectedStudyInfo.status.id != 4)!false]

  <div class="borderBox generalInformationStudies">

    [#-- Note for General Inputs description --]
    [#if isOutcomeCaseStudy] 
      <div class="note--2">
        <p>[@s.text name="study.general.note.allianceId" /]</p>
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
        <div class="col-md-2">
          [@customForm.input name="${customName}.projectExpectedStudyInfo.allianceOicr" i18nkey="study.general.allianceID" helpIcon=false required=false editable=editable className="targetValueAllianceId" placeholder="XXX-0000" /]
        </div>
      [/#if]

      [#-- Tag --]
      [#if isOutcomeCaseStudy]          
      <div class="col-md-2">
          [@customForm.select name="${customName}.projectExpectedStudyInfo.tag.id" value="${(element.projectExpectedStudyInfo.tag.id)!-1}" className="setSelect2 studyTag" i18nkey="study.general.tag" listName="tagList" keyFieldName="id"  displayFieldName="tagName" required=false editable=(editable && isOutcomeCaseStudy && action.canAccessSuperAdmin()) && action.hasSpecificities('oicr_tag_field_manual_manage_active') /]
        </div>
      [/#if]

      [#-- Score for MELIAs --]
      [#if !isOutcomeCaseStudy && action.hasSpecificities('melia_score_field_active')]
        <div class="col-md-2">
          [@customForm.input name="${customName}.projectExpectedStudyInfo.score" i18nkey="study.general.score" helpIcon=false required=false editable=editable /]
        </div>
      [/#if]

      [#-- Status --]
      <div class="${isOutcomeCaseStudy?string('col-md-2','col-md-3')}">
        [@customForm.select name="${customName}.projectExpectedStudyInfo.status.id" className="setSelect2 statusSelect" i18nkey="study.general.status" listName="statuses" keyFieldName="id"  displayFieldName="name" header=false required=true editable=editable /]
      </div>

      [#-- Year --]        
      <div class="col-md-2">
        [#assign dbExpectedYear = ((element.projectExpectedStudyInfo.year)!currentCycleYear)  ]
        
          [#--
        [@customForm.select name="${customName}.projectExpectedStudyInfo.year" className="setSelect2" i18nkey="study.year" listName="getExpectedStudiesYears(${(expectedID)!})" header=false required=true editable=editable /]
          --]  
        
        <div class="block-extendedYear" style="display:${isStatusExtended?string('block', 'none')}">
          [@customForm.select name="newExpectedYear" className="setSelect2" i18nkey="study.general.year" listName="project.projectInfo.getYears(${currentCycleYear})" header=false required=true editable=editable /]
        </div>
        <div class="block-year" style="display:${(!isStatusExtended && isOtherStatus)?string('block', 'none')}">
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
      [#if (reportingActive)!false]
      <div class="form-group analysisGroup">
        <label for="">[@s.text name="study.general.covidAnalysis" /]:[@customForm.req required=false /]
        </label>        
        <div class="form-group">
          [#assign covidAnalisis = "covidAnalisis"]
          [#assign showAnalysis = (expectedStudy.projectExpectedStudyInfo.hasCovidAnalysis?string)!"" /]
          [@customForm.radioFlat id="${covidAnalisis}-yes" name="${customName}.projectExpectedStudyInfo.hasCovidAnalysis" label="Yes" value="true" checked=(showAnalysis == "true") cssClassLabel="radio-label-yes" editable=editable /]
          [@customForm.radioFlat id="${covidAnalisis}-no" name="${customName}.projectExpectedStudyInfo.hasCovidAnalysis" label="No" value="false" checked=(showAnalysis == "false") cssClassLabel="radio-label-no" editable=editable /]
        </div>  
      </div>
      [/#if]


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
                        <th > [@s.text name="study.general.dialogMessage.part2" /] </th>
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
      <label for="">[@s.text name="study.generalInformation.outcomestoryLinks" /]:
        [@customForm.req required=false /]
        [@customForm.helpLabel name="study.generalInformation.outcomestoryLinks.help" paramText="<a href='https://hdl.handle.net/10568/99384' target='_blank'>Personal data use authorization form</a>" showIcon=false editable=editable/]
      </label>
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
      <label for="">[@s.text name="study.communications.publications" /]:[@customForm.req required=false /]</label>
      [@customForm.helpLabel name="study.communications.publications.help" showIcon=false isNote=true/]
      <div class="publicationsBlock">
        <div class="publicationsList">
          <div class="row">
            <div class="col-sm-4 colTitleCenter" style="font-weight: 600; text-align: center;">Name[@customForm.req required=(editable && validateIsProgressWithStatus!true)  /]</div>
            <div class="col-sm-4 colTitleCenter" style="font-weight: 600; text-align: center;">Position[@customForm.req required=(editable && validateIsProgressWithStatus!true)  /]</div>
            <div class="col-sm-4 colTitleCenter" style="font-weight: 600; text-align: center;">Affiliation[@customForm.req required=(editable && validateIsProgressWithStatus!true)  /]</div>
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
      <label class="label--2" for=""><b>[@s.text name="study.allianceAligment.linkToLevers.title" /]</b>[@customForm.req required=false /]</label>
      [@customForm.helpLabel name="study.allianceAligment.linkToLevers.note" showIcon=false editable=editable isNote=true /]
      [#-- Primary Levers --]
      <div class="form-group">
        [@customForm.selectableCheckToCheckboxMacro label="study.allianceAligment.linkToLevers.options.primaryLever" name="${customName}" fieldName="allianceLever" listName=allianceLeverList keyFieldName="sdgContributions" isPrimaryLever=true  subtitleInnerCheckbox="study.allianceAligment.linkToLevers.options.text.contributionSDG" listNameInnerCheckbox="leverSdgContributions" classReferenceInnerCheckbox="sDGContribution" element=element /]
      </div>
      [#-- Related Levers --]
      <div class="form-group">
        [@customForm.selectableCheckToCheckboxMacro label="study.allianceAligment.linkToLevers.options.relatedLever" name="${customName}" fieldName="allianceLevers" listName=allianceLeverList keyFieldName="sdgContributions" isPrimaryLever=false subtitleInnerCheckbox="study.allianceAligment.linkToLevers.options.text.contributionSDG" listNameInnerCheckbox="leverSdgContributions" classReferenceInnerCheckbox="sDGContribution" isRadioButton=false element=element className="containerRelatedLever" /]
      </div>
    </div>

    [#-- SDG Representation --]
    <div class="form-group">
      <label class="label--2" for=""><b>[@s.text name="study.allianceAligment.generalContributionSDG" /]</b></label>
      <div class="selectedLeverContainer">
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
  [#if (element.projectExpectedStudyInfo.hasCgiarContribution)?has_content]
    [#local hasContributionToCGIAR = (element.projectExpectedStudyInfo.hasCgiarContribution)!false ]
  [/#if]

  <div class="borderBox">
    <div class="form-group">
      <label for="">[@s.text name="study.oneCGIARAligment.contributionToCGIAR" /]:[@customForm.req required=false /]</label>
      <div class="form-group row">
      
        <div class="col-md-2">
          [@customForm.radioFlat id="optionOneCGIAR-Yes" name="${customName}.projectExpectedStudyInfo.hasCgiarContribution" i18nkey="study.oneCGIARAligment.contributionToCGIARYes" value="true" checked=((hasContributionToCGIAR)!false) cssClass="radioType-contributionToCGIAR" cssClassLabel="font-normal" editable=editable /]
        </div>
        <div class="col-md-2">
          [@customForm.radioFlat id="optionOneCGIAR-No" name="${customName}.projectExpectedStudyInfo.hasCgiarContribution" i18nkey="study.oneCGIARAligment.contributionToCGIARNo" value="false" checked=((!hasContributionToCGIAR)!false) cssClass="radioType-contributionToCGIAR" cssClassLabel="font-normal" editable=editable /]
        </div>
      </div>
      <div class="form-group contributionToCGIARComment" style="display:${(hasContributionToCGIAR == false)!false?then('block','none')};" >
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.reasonNotCgiarContribution" i18nkey="study.oneCGIARAligment.contributionToCGIAR.reasonToNoProvided"  helpIcon=false className="limitWords-200" required=false editable=editable /]
      </div>
    </div>
    <div class="form-group">
      <label class="label--2" for=""><b>[@s.text name="study.oneCGIARAligment.linkToImpactAndTarget.title" /]</b>[@customForm.req required=false /]</label>
      
      [@customForm.selectableCheckToCheckboxMacro label="study.oneCGIARAligment.linkToImpactAndTarget.descripition" name="${customName}" fieldName="impactArea" listName=impactAreasList keyFieldName="globalTargets" isPrimaryLever=false subtitleInnerCheckbox="" listNameInnerCheckbox="globalTargets" classReferenceInnerCheckbox="impactArea" isRadioButton=true isDirectInfo=true element=element /]
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

[#macro quantificationMacro name element index=-1 template=false]
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
        [@customForm.select name="${customName}.quantificationType.id" value="${(element.quantificationType.id)!-1}" className="setSelect2" i18nkey="study.quantificationType" listName="quantificationTypes"  keyFieldName="id" displayFieldName="name" required=(editable && !action.isPOWB() && validateIsProgressWithStatus!true) editable=editable /]
      </div>
      [#-- Number --]
      <div class="col-md-4">
        [@customForm.input name="${customName}.number" i18nkey="study.quantification.number" className="numericInput" required=(!action.isPOWB() && validateIsProgressWithStatus!true) editable=editable /]
      </div>
      [#-- Unit --]
      <div class="col-md-4"> 
        [@customForm.input name="${customName}.targetUnit" i18nkey="study.quantification.targetUnit" className="" required=(!action.isPOWB() && validateIsProgressWithStatus!true) editable=editable /]
      </div> 
    </div>
    [#-- Comments --]
    <div class="form-group">
      [@customForm.textArea name="${customName}.comments" i18nkey="study.quantification.comments" help="study.quantification.comments.help"  placeholder="" className="" required=(!action.isPOWB() && validateIsProgressWithStatus!true) editable=editable /]
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
    <span class="pull-left" style="width:30%; margin-left: 12px ">[@customForm.input name="${customName}.name" showTitle=false i18nkey="" className="" editable=editable /]</span>
    <span class="pull-left" style="width:30%; margin-left: 12px">[@customForm.input name="${customName}.position" showTitle=false i18nkey="" className="" editable=editable /]</span>
    <span class="pull-left" style="width:30%; margin-left: 12px">[@customForm.input name="${customName}.affiliation" showTitle=false i18nkey="" className="" editable=editable /]</span>

    [#if editable]<div class="removeElement sm removeIcon removePublication ${class}" title="Remove"></div>[/#if]
    <div class="clearfix"></div>
  </div>
    
[/#macro]