[#ftl]
[#macro studyMacro element name index=-1 template=false fromProject=true ]
  [#local customName = "${name}"/]
  [#local customId = "study-${template?string('template',index)}" /]
  [#local isOutcomeCaseStudy = ((element.projectExpectedStudyInfo.studyType.id == 1)!false) && reportingActive/]
  [#local isNew = (action.isEvidenceNew(element.id))!false /]
  
  [#local isPolicy = ((element.projectExpectedStudyInfo.isContribution)!false) ]
  [#local stageProcessOne = ((element.projectExpectedStudyInfo.repIndStageProcess.id == 1))!false ]
  
  <div id="${customId}" class="caseStudy evidenceBlock isNew-${isNew?string}" style="display:${template?string('none','block')}">
    <div class="borderBox">
    
      <div class="form-group row">
        <div class="col-md-4">
          [@customForm.select name="${customName}.projectExpectedStudyInfo.studyType.id" value="${(element.projectExpectedStudyInfo.studyType.id)!-1}" className="setSelect2 studyType" i18nkey="study.type" listName="studyTypes" keyFieldName="id"  displayFieldName="name" required=true editable=editable && !isOutcomeCaseStudy /]
        </div>
        <div class="col-md-4">
          [@customForm.select name="${customName}.projectExpectedStudyInfo.status.id" className="setSelect2 statusSelect" i18nkey="study.status" listName="statuses" keyFieldName="id"  displayFieldName="name" header=false required=true editable=editable /]
        </div>
        <div class="col-md-4">
          [@customForm.select name="${customName}.projectExpectedStudyInfo.year" className="setSelect2" i18nkey="study.year" listName="years" header=false required=true editable=editable /]
        </div>
      </div>
      
      [#if isOutcomeCaseStudy]
        <hr />
        
        [#-- Tags --]
        <div class="form-group">
          <label for="">[@s.text name="study.tags" /]:[@customForm.req required=editable /]</label>
          [#local tagValue = (element.projectExpectedStudyInfo.evidenceTag.id)!-1 ]
          [#list tags as tag]
            <br /> [@customForm.radioFlat id="tag-${tag_index}" name="${customName}.projectExpectedStudyInfo.evidenceTag.id" i18nkey="${tag.name}" value="${tag.id}" checked=(tagValue == tag.id) cssClass="radioType-tags" cssClassLabel="font-normal" editable=editable /] 
          [/#list]
        </div>
      [/#if]
    </div>
    <div class="borderBox">
      [#-- 1. Title (up to 25 words) --]
      <div class="form-group">
        [@customForm.input name="${customName}.projectExpectedStudyInfo.title" i18nkey="study.title" help="study.title.help" className="limitWords-25" helpIcon=!isOutcomeCaseStudy required=true editable=editable /]
      </div>
      
      [#-- Who is commissioning this study --]
      [#if !isOutcomeCaseStudy]
      <div class="form-group">
        [@customForm.input name="${customName}.projectExpectedStudyInfo.commissioningStudy" i18nkey="study.commissioningStudy" help="study.commissioningStudy.help" className="" helpIcon=false required=true editable=editable /]
      </div>
      [/#if]
      
      [#-- 2. Short outcome/impact statement (up to 80 words) --]
      [#if isOutcomeCaseStudy]
      <div class="form-group">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.outcomeImpactStatement" i18nkey="study.outcomeStatement" help="study.outcomeStatement.help" className="limitWords-80" helpIcon=false required=true editable=editable /]
      </div>
      [/#if]
      
      [#-- 3. Outcome story for communications use. REPLACED "comunicationsMaterial" --]
      [#if isOutcomeCaseStudy]
      <div class="form-group">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.comunicationsMaterial" i18nkey="study.outcomestory" help="study.outcomestory.help" className="limitWords-400" helpIcon=false required=true editable=editable /]
      
        <br />
        <label for="">[@s.text name="study.outcomestoryLinks" /]:
          [@customForm.req required=false /]
          [@customForm.helpLabel name="study.outcomestoryLinks.help" paramText="<a href='https://hdl.handle.net/10568/99384' target='_blank'>Personal data use authorization form</a>" showIcon=false editable=editable/]
        </label>
        <div class="linksBlock ">
          <div class="linksList">
            [#list (element.links)![{}] as link ]
              [@studyLink name="${customName}.links" element=link index=link_index /]
            [/#list]
          </div>
          [#if editable]
          <div class="addButtonLink button-green pull-right"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> Add Link </div>
          <div class="clearfix"></div>
          [/#if]
        </div>
        [#-- Element item Template --]
        <div style="display:none">
          [@studyLink name="${customName}.links" element={} index=-1 template=true /]
        </div>
      </div>
      [/#if]
      
      [#-- 3. Link to Common Results Reporting Indicator #I3 --]
      [#if isOutcomeCaseStudy]
      <div class="form-group">
        [#-- Does this outcome reflect a contribution of the CGIAR in influencing or modifying policies/ strategies / laws/ regulations/ budgets/ investments or  curricula?  --]
        <div class="form-group">
          [#local guideSheetURL = "https://drive.google.com/file/d/1GYLsseeZOOXF9zXNtpUtE1xeh2gx3Vw2/view" /]
          <small class="pull-right"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrl}/global/images/icon-file.png" alt="" /> #I1 Policies -  Guideline </a> </small>
          
          <label for="">[@s.text name="study.reportingIndicatorThree" /]:[@customForm.req required=editable /][@customForm.helpLabel name="study.reportingIndicatorThree.help" showIcon=false editable=editable/]</label>
          [#assign studyIndicatorThree = "studyIndicatorThree"]
          [#assign showPolicyIndicator = (element.projectExpectedStudyInfo.isContribution?string)!"" /]
          [@customForm.radioFlat id="${studyIndicatorThree}-yes" name="${name}.projectExpectedStudyInfo.isContribution" label="Yes" value="true" checked=(showPolicyIndicator == "true") cssClass="radioType-${studyIndicatorThree}" cssClassLabel="radio-label-yes" editable=editable /]
          [@customForm.radioFlat id="${studyIndicatorThree}-no" name="${name}.projectExpectedStudyInfo.isContribution" label="No" value="false" checked=(showPolicyIndicator == "false") cssClass="radioType-${studyIndicatorThree}" cssClassLabel="radio-label-no" editable=editable /]
        </div>
        
        [#-- Disaggregates for CGIAR Indicator I3  --]
        <div class="form-group simpleBox block-${studyIndicatorThree}" style="display:${(showPolicyIndicator == "true")?string('block','none')}">
        ${policyList?size}
          [@customForm.elementsListComponent name="${customName}.policies" elementType="projectPolicy" elementList=element.policies label="study.policies"  listName="policyList" keyFieldName="id" displayFieldName="composedName"/]
          [#-- Note --]
          <div class="note">[@s.text name="study.policies.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/policies'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">clicking here</a>[/@][/@]</div>
        </div>
      </div>
      [/#if]
      
      [#-- 4.  Maturity of change reported (tick-box)  --]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        <label for="">[@s.text name="study.maturityChange" /]:[@customForm.req required=editable && !(isPolicy && stageProcessOne) /]
          [@customForm.helpLabel name="study.maturityChange.help" showIcon=false editable=editable/][@customForm.helpLabel name="study.maturityChange.help2" showIcon=true editable=editable/]
        </label>
        <div class="form-group">
          [#list stageStudies as stage]
            <p>[@customForm.radioFlat id="maturityChange-${stage.id}" name="${customName}.projectExpectedStudyInfo.repIndStageStudy.id" label="<small><b>${stage.name}:</b> ${stage.description}</small>" value="${stage.id}" checked=(element.projectExpectedStudyInfo.repIndStageStudy.id == stage.id)!false cssClass="" cssClassLabel="font-normal" editable=editable/]</p> 
          [/#list]
        </div>
      </div>
      [/#if]
      
      [#-- 5. Links to the Strategic Results Framework  --]
      <div class="form-group">
        [#if isOutcomeCaseStudy]
          <label for="">[@s.text name="study.stratgicResultsLink" /]:[@customForm.req required=editable /]
            [@customForm.helpLabel name="study.stratgicResultsLink.help" showIcon=false editable=editable/]
          </label>
        [#else]
          <label for="">[@s.text name="study.relevantTo" /]:[@customForm.req required=editable /]
          </label> 
        [/#if]
        [#-- Sub IDOs (maxLimit=2) --]
        <div class="form-group simpleBox">
          [@customForm.elementsListComponent name="${customName}.subIdos" elementType="srfSubIdo" elementList=element.subIdos label="study.stratgicResultsLink.subIDOs"  listName="subIdos" maxLimit=2 keyFieldName="id" displayFieldName="description"/]
        </div>
        
        [#-- SRF Targets (maxLimit=2)  --]
        <div class="form-group simpleBox stageProcessOne">
          <label for="">[@s.text name="study.targetsOption" /]:[@customForm.req required=editable /][@customForm.helpLabel name="study.targetsOption.help" showIcon=false editable=editable/]</label><br />
          [#local targetsOption = (element.projectExpectedStudyInfo.isSrfTarget)!""]
          [#list ["targetsOptionYes", "targetsOptionNo", "targetsOptionTooEarlyToSay"] as option]
            [@customForm.radioFlat id="option-${option}" name="${customName}.projectExpectedStudyInfo.isSrfTarget" i18nkey="study.${option}" value="${option}" checked=(option == targetsOption) cssClass="radioType-targetsOption" cssClassLabel="font-normal" editable=editable /] 
          [/#list]
          [#local showTargetsComponent = (element.projectExpectedStudyInfo.isSrfTarget == "targetsOptionYes")!false /]
          <div class="srfTargetsComponent" style="display:${showTargetsComponent?string('block', 'none')}">
            [@customForm.elementsListComponent name="${customName}.srfTargets" elementType="srfSloIndicator" elementList=element.srfTargets label="study.stratgicResultsLink.srfTargets" listName="targets" maxLimit=2  keyFieldName="id" displayFieldName="title" required=editable && !(isPolicy && stageProcessOne)/]          
          </div>
        </div>
        
        [#-- Comments  --]
        [#if isOutcomeCaseStudy]
        <div class="form-group simpleBox stageProcessOne">
          [@customForm.textArea name="${customName}.projectExpectedStudyInfo.topLevelComments" i18nkey="study.stratgicResultsLink.comments" help="study.stratgicResultsLink.comments.help" helpIcon=false className="limitWords-100" editable=editable required=false /]
        </div>
        [/#if]
      </div>
      
      [#-- 6.  Geographic scope - Countries  --]
      <div class="form-group geographicScopeBlock">
        [#local geographicScopeList = (element.geographicScopes)![] ]
        [#local isRegional =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeRegional) /]
        [#local isMultiNational = findElementID(geographicScopeList,  action.reportingIndGeographicScopeMultiNational) /]
        [#local isNational =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeNational) /]
        [#local isSubNational =   findElementID(geographicScopeList,  action.reportingIndGeographicScopeSubNational) /]
        
        <label for="">[@s.text name="study.geographicScopeTopic" /]:[@customForm.req required=editable /]</label>
        <div class="form-group simpleBox">
          <div class="form-group row">
            <div class="col-md-6">
              [#-- Geographic Scope --]
              [@customForm.elementsListComponent name="${customName}.geographicScopes" elementType="repIndGeographicScope" elementList=element.geographicScopes  label="study.geographicScope" listName="geographicScopes" keyFieldName="id" displayFieldName="name" required=true /]
            </div>
          </div>
          <div class="form-group regionalBlock" style="display:${(isRegional)?string('block','none')}">
            [#-- Regional scope --]
            [@customForm.elementsListComponent name="${customName}.studyRegions" elementType="locElement" elementList=element.studyRegions label="study.region"  listName="regions" keyFieldName="id" displayFieldName="composedName" required=false /]
          </div>
          <div class="form-group nationalBlock" style="display:${(isMultiNational || isNational || isSubNational)?string('block','none')}">
            [#-- Multinational, National and Subnational scope --]
            [@customForm.select name="${customName}.countriesIds" label="" i18nkey="study.countries" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="${customName}.countriesIds" multiple=true required=true className="countriesSelect" disabled=!editable/]
          </div>
          <div class="form-group">
            [#-- Comment box --]
            [@customForm.textArea name="${customName}.projectExpectedStudyInfo.scopeComments" className="limitWords-30" i18nkey="study.geographicScopeComments" help="study.geographicScopeComments.help" helpIcon=false  editable=editable required=false/]
          </div>
        </div>
      </div>

      [#-- 7. Key Contributors  --]
      <div class="form-group">
        [#if isOutcomeCaseStudy || !fromProject]
          <label for="">[@s.text name="study.${isOutcomeCaseStudy?string('keyContributors','keyContributorsOther')}" /]:</label>
        [/#if]
        [#-- CRPs --]
        [#if isOutcomeCaseStudy]
        <div class="form-group simpleBox">
          [@customForm.elementsListComponent name="${customName}.crps" elementType="globalUnit" elementList=element.crps label="study.keyContributors.crps"  listName="crps" keyFieldName="id" displayFieldName="composedName" required=false /]
        </div>
        [/#if]
        [#-- Flagships --]
        [#if isOutcomeCaseStudy || !fromProject]
        <div class="form-group simpleBox stageProcessOne">
          [#if !fromProject && editable]
            <p class="note">To the [@s.text name="programManagement.flagship.title"/](s) selected, the system grants permission to edit this ${(element.projectExpectedStudyInfo.studyType.name)!'study'} to their [@s.text name="CrpProgram.leaders"/] and [@s.text name="CrpProgram.managers"/]</p>
          [/#if]
          [@customForm.elementsListComponent name="${customName}.flagships" elementType="crpProgram" id="FP" elementList=element.flagships label="study.keyContributors.flagships"  listName="flagshipList" keyFieldName="id" displayFieldName="composedName" required=false /]
        </div>
        [/#if]
        [#-- Regions --]
        [#if (isOutcomeCaseStudy || !fromProject) && regionList?has_content]
          <div class="form-group simpleBox stageProcessOne">
            [#if !fromProject && editable]
              <p class="note">To the Region(s) selected, the system grants permission to edit this ${(element.projectExpectedStudyInfo.studyType.name)!'study'} to their [@s.text name="regionalMapping.CrpProgram.leaders"/] and [@s.text name="regionalMapping.CrpProgram.managers"/]</p>
            [/#if]
            [@customForm.elementsListComponent name="${customName}.regions" elementType="crpProgram" id="RP" elementList=element.regions label="study.keyContributors.regions"  listName="regionList" keyFieldName="id" displayFieldName="composedName" required=false /]
          </div>
        [/#if]
        [#-- External Partners --]
        [#if isOutcomeCaseStudy]
        <div class="form-group simpleBox stageProcessOne">
          [@customForm.elementsListComponent name="${customName}.institutions" elementType="institution" elementList=element.institutions label="study.keyContributors.externalPartners"  listName="institutions" keyFieldName="id" displayFieldName="composedName" required=false /]
          [#-- Request partner adition --]
          [#if editable]
          <p id="addPartnerText" class="helpMessage">
            [@s.text name="projectPartners.addPartnerMessage.first" /]
            <a class="popup" href="[@s.url namespace="/projects" action='${crpSession}/partnerSave'][@s.param name='expectedID']${(expectedID)!}[/@s.param][/@s.url]">
              [@s.text name="projectPartners.addPartnerMessage.second" /]
            </a>
          </p> 
          [/#if]
        </div>
        [/#if]
      </div>
      
      [#--  CGIAR innovation(s) or findings that have resulted in this outcome or impact.   --]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.cgiarInnovation" i18nkey="study.innovationsNarrative" help="study.innovationsNarrative.help" helpIcon=false className="" required=editable && !(isPolicy && stageProcessOne) editable=editable /]
         
        [@customForm.elementsListComponent name="${customName}.innovations" elementType="projectInnovation" elementList=element.innovations label="study.innovationsList"  listName="innovationsList" keyFieldName="id" displayFieldName="composedName" required=false /]
      </div>
      [/#if]
      
      [#--  Elaboration of Outcome/Impact Statement  --]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.elaborationOutcomeImpactStatement" i18nkey="study.elaborationStatement" help="study.elaborationStatement.help" helpIcon=false className="limitWords-400" required=editable && !(isPolicy && stageProcessOne) editable=editable /]
      </div>
      [/#if]
      
      [#-- 9. References cited  --]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        <div class="form-group">
          [@customForm.textArea name="${customName}.projectExpectedStudyInfo.referencesText" i18nkey="study.referencesCited" help="study.referencesCited.help2" helpIcon=false className="" required=editable && !(isPolicy && stageProcessOne) editable=editable /]
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
        <label for="">[@s.text name="study.quantification" /]:[@customForm.helpLabel name="study.quantification.help" showIcon=false editable=editable/]</label><br />
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
        [@tag name="Indicator #3" /]
        <label for="">[@s.text name="study.crossCuttingRelevance" /]:
          [@customForm.helpLabel name="study.crossCuttingRelevance.help" showIcon=false editable=editable/]
        </label>
        [#-- Gender --]
        <div class="simpleBox ccRelevanceBlock">
          <label for="">[@s.text name="study.genderRelevance" /]:[@customForm.req required=editable /]</label>
          <div class="form-group">
            [#assign genderLevel = (element.projectExpectedStudyInfo.genderLevel.id)!1 ]
            [#list focusLevels  as cc]
              [@customForm.radioFlat id="genderRelevance-${cc_index}" name="${name}.projectExpectedStudyInfo.genderLevel.id" label="${cc.powbName}" value="${cc.id}" checked=(genderLevel == cc.id)!false cssClass="" cssClassLabel="font-normal" editable=editable /]
            [/#list]
          </div>
          <div class="ccCommentBox" style="display:${((genderLevel == 2) || (genderLevel == 3))?string('block', 'none')}">
            <div class="form-group stageProcessOne">
              [@customForm.textArea name="${customName}.projectExpectedStudyInfo.describeGender" i18nkey="study.achievementsGenderRelevance" className="limitWords-100" required=editable && !(isPolicy && stageProcessOne)editable=editable /]
            </div>
          </div>
        </div>
        [#-- Youth  --]
        <div class="simpleBox ccRelevanceBlock">
          <label for="">[@s.text name="study.youthRelevance" /]:[@customForm.req required=editable /]</label>
          <div class="form-group">
            [#assign youthLevel = (element.projectExpectedStudyInfo.youthLevel.id)!1 ]
            [#list focusLevels  as cc]
              [@customForm.radioFlat id="youthRelevance-${cc_index}" name="${name}.projectExpectedStudyInfo.youthLevel.id" label="${cc.powbName}" value="${cc.id}" checked=(youthLevel == cc.id)!false cssClass="" cssClassLabel="font-normal" editable=editable /]
            [/#list]
          </div> 
          <div class="ccCommentBox" style="display:${((youthLevel == 2) || (youthLevel == 3))?string('block', 'none')}">
            <div class="form-group stageProcessOne">
              [@customForm.textArea name="${customName}.projectExpectedStudyInfo.describeYouth" i18nkey="study.achievementsYouthRelevance"  className="limitWords-100" required=editable && !(isPolicy && stageProcessOne) editable=editable /]
            </div>
          </div>
        </div>
        [#-- CapDev   --]
        <div class="simpleBox ccRelevanceBlock">
          <label for="">[@s.text name="study.capDevRelevance" /]:[@customForm.req required=editable /]</label>
          <div class="form-group">
            [#assign capdevLevel = (element.projectExpectedStudyInfo.capdevLevel.id)!1 ]
            [#list focusLevels  as cc]
              [@customForm.radioFlat id="capDevRelevance-${cc_index}" name="${name}.projectExpectedStudyInfo.capdevLevel.id" label="${cc.powbName}" value="${cc.id}" checked=(capdevLevel == cc.id)!false cssClass="" cssClassLabel="font-normal" editable=editable /]
            [/#list]
          </div>
          <div class="ccCommentBox" style="display:${((capdevLevel == 2) || (capdevLevel == 3))?string('block', 'none')}">
            <div class="form-group stageProcessOne">
              [@customForm.textArea name="${customName}.projectExpectedStudyInfo.describeCapdev" i18nkey="study.achievementsCapDevRelevance"  className="limitWords-100" required=editable && !(isPolicy && stageProcessOne) editable=editable /]
            </div>
          </div>
        </div>
        [#-- Climate Change  --]
        <div class="simpleBox ccRelevanceBlock">
          <label for="">[@s.text name="study.climateChangeRelevance" /]:[@customForm.req required=editable /]</label>
          <div class="form-group">
            [#assign climateChangeLevel = (element.projectExpectedStudyInfo.climateChangeLevel.id)!1 ]
            [#list focusLevels  as cc]
              [@customForm.radioFlat id="climateChangeRelevance-${cc_index}" name="${name}.projectExpectedStudyInfo.climateChangeLevel.id" label="${cc.powbName}" value="${cc.id}" checked=(climateChangeLevel == cc.id)!false cssClass="" cssClassLabel="font-normal" editable=editable /]
            [/#list]
          </div>
          <div class="ccCommentBox" style="display:${((climateChangeLevel == 2) || (climateChangeLevel == 3))?string('block', 'none')}">
            <div class="form-group stageProcessOne">
              [@customForm.textArea name="${customName}.projectExpectedStudyInfo.describeClimateChange" i18nkey="study.achievementsClimateChangeRelevance"  className="limitWords-100" required=editable && !(isPolicy && stageProcessOne) editable=editable /]
            </div>
          </div>
        </div> 
        
      </div>
      [/#if]
      
      [#--  Other cross-cutting dimensions   --]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        <label for="">[@s.text name="study.otherCrossCutting" /]:</label> 
        [@customForm.helpLabel name="study.otherCrossCuttingOptions" showIcon=false editable=editable/]<br />
        [#local otherCrossCuttingSelection = (element.projectExpectedStudyInfo.otherCrossCuttingSelection)!"" ]
        [#list ["Yes", "No", "NA"] as option]
          [@customForm.radioFlat id="option-${option}" name="${customName}.projectExpectedStudyInfo.otherCrossCuttingSelection" i18nkey="study.otherCrossCutting${option}" value="${option}" checked=(otherCrossCuttingSelection == option) cssClass="radioType-otherCrossCuttingOption" cssClassLabel="font-normal" editable=editable /] 
        [/#list]
        [#local showOtherCrossCuttingOptionsComponent = true /]
        <div class="otherCrossCuttingOptionsComponent form-group" style="display:${showOtherCrossCuttingOptionsComponent?string('block', 'none')}">
          [@customForm.textArea name="${customName}.projectExpectedStudyInfo.otherCrossCuttingDimensions" i18nkey="study.otherCrossCutting.comments" help="study.otherCrossCutting.comments.help" helpIcon=false  className="limitWords-200" required=false editable=editable /]
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

      [#--  Contact person    --]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.contacts" i18nkey="study.contacts" help="study.contacts.help" className="" helpIcon=false required=editable && !(isPolicy && stageProcessOne) editable=editable /]
      </div>
      [/#if]
      
      [#--  Comments for other studies--]
      [#if !isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.topLevelComments" i18nkey="study.comments"  placeholder="" className="limitWords-100" required=editable && !(isPolicy && stageProcessOne) editable=editable /]
      </div>
      [/#if]
      
    </div>
    
    [#-- Private Option --]
    [#if isOutcomeCaseStudy]
    <h3 class="headTitle">[@s.text name="study.confidentialTitle" /]</h3>
    <div class="borderBox">
      <label for="">[@s.text name="study.public" ][@s.param]${(element.projectExpectedStudyInfo.studyType.name)!}[/@][/@] 
        [@customForm.helpLabel name="study.public.help" showIcon=false paramText="${(element.projectExpectedStudyInfo.studyType.name)!}" editable=editable/]
      </label> <br />
      [#local isPublic = (element.projectExpectedStudyInfo.isPublic)!true /]
      [@customForm.radioFlat id="optionPublic-yes"  name="${customName}.projectExpectedStudyInfo.isPublic" i18nkey="Yes"  value="true"  checked=isPublic  cssClass="radioType-optionPublic" cssClassLabel="font-normal radio-label-yes" editable=editable /] 
      [@customForm.radioFlat id="optionPublic-no"   name="${customName}.projectExpectedStudyInfo.isPublic" i18nkey="No"   value="false" checked=!isPublic cssClass="radioType-optionPublic" cssClassLabel="font-normal radio-label-no"  editable=editable /] 
      
      <div class="optionPublicComponent form-group" style="display:${isPublic?string('block', 'none')}">         
        <br />
        <div class="input-group">
          <span class="input-group-btn">
            <button class="btn btn-default btn-sm copyButton" type="button"> <span class="glyphicon glyphicon-link"></span> Copy URL </button>
          </span>
          <input type="text" class="form-control input-sm urlInput" value="${baseUrl}/projects/${crpSession}/studySummary.do?studyID=${(element.id)!}&cycle=Reporting&year=${(actualPhase.year)!}" readonly>
        </div>
        <div class="message text-center" style="display:none">Copied!</div>
      </div>
    </div>
    [/#if]
    
    [#-- Projects shared --]
    [#if fromProject]
    <h3 class="headTitle"> Share Study </h3>
    <div class="borderBox">
      [@customForm.elementsListComponent name="${customName}.projects" elementType="project" elementList=element.projects label="study.sharedProjects"  listName="myProjects" keyFieldName="id" displayFieldName="composedName" required=false /]
    </div>
    [/#if]
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
      [#-- Quantification type --]
      <div class="form-group">
        <label for="">[@s.text name="study.quantificationType" /]:[@customForm.req required=editable /]</label>
        <br />[@customForm.radioFlat id="quantificationType-1" name="${customName}.typeQuantification" i18nkey="study.quantification.quantificationType-1" value="A" checked=((element.typeQuantification == "A")!false) cssClass="" cssClassLabel="font-normal" editable=editable /]
        <br />[@customForm.radioFlat id="quantificationType-2" name="${customName}.typeQuantification" i18nkey="study.quantification.quantificationType-2" value="B" checked=((element.typeQuantification == "B")!false) cssClass="" cssClassLabel="font-normal" editable=editable /]
      </div>
    </div>
    [#-- Units --]
    <div class="form-group row">
      <div class="col-md-4">
        [@customForm.input name="${customName}.number" i18nkey="study.quantification.number" help="study.quantification.number.help" className="" required=true editable=editable /]
      </div>
      <div class="col-md-4"> 
        [@customForm.input name="${customName}.targetUnit" i18nkey="study.quantification.targetUnit" help="study.quantification.targetUnit.help" className="" required=true editable=editable /]
      </div> 
    </div>
    [#-- Comments --]
    <div class="form-group">
      [@customForm.textArea name="${customName}.comments" i18nkey="study.quantification.comments" help="study.quantification.comments.help"  placeholder="" className="" required=true editable=editable /]
    </div>
  </div>
[/#macro]

[#function findElementID list id]
  [#list (list)![] as item]
    [#if (item.repIndGeographicScope.id == id)!false][#return true][/#if]
  [/#list]
  [#return false]
[/#function]
