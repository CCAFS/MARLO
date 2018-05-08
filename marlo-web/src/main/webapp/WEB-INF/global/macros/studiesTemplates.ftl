[#ftl]
[#macro studyMacro element name index=-1 template=false fromProject=true ]
  [#local customName = "${name}"/]
  [#local customId = "study-${template?string('template',index)}" /]
  [#local isOutcomeCaseStudy = (element.projectExpectedStudyInfo.studyType.id == 1)!false /]
  
  <div id="${customId}" class="caseStudy" style="display:${template?string('none','block')}">
    <div class="borderBox">
      <div class="form-group row">
        <div class="col-md-6">
          [@customForm.select name="${customName}.projectExpectedStudyInfo.studyType.id" value="${(element.projectExpectedStudyInfo.studyType.id)!-1}" className="setSelect2" i18nkey="study.type" listName="studyTypes" keyFieldName="id"  displayFieldName="name" editable=editable/]
        </div>
        <div class="col-md-6">
          [@customForm.select name="${customName}.projectExpectedStudyInfo.status" className="setSelect2" i18nkey="study.status" listName="statuses"  editable=editable /]
        </div>
      </div>
    </div>
    <div class="borderBox">
    
      [#if (element.caseStudy??)]
       CaseStudy -> ${(element.caseStudy)!}
      [/#if]

      [#-- 1. Title (up to 20 words) --]
      <div class="form-group">
        [@customForm.input name="${customName}.projectExpectedStudyInfo.title" i18nkey="study.title" help="study.title.help" className="limitWords-20" helpIcon=!isOutcomeCaseStudy required=true editable=editable /]
      </div>
      
      [#-- Flagships & Regions --]
      [#if !fromProject]
      <div class="form-group row">
        [#-- Flagships --]
        [#if flagshipsList??]
          <div class="col-md-6">
            <h5>[@s.text name="study.flagships" /]:[@customForm.req required=editable/] </h5>
            <div id="" class="dottedBox">
              [#if editable]
                [#list flagshipsList as flagship]
                  [@customForm.checkBoxFlat id="flagship-${flagship.id}" name="${customName}.flagshipValue" label="${flagship.composedName}" value="${flagship.id}" editable=editable checked=((flagshipIds?seq_contains(element.id))!false) cssClass="checkboxInput fpInput" /]
                [/#list]
              [#else]
                <input type="hidden" name="${customName}.flagshipValue" value="${(element.flagshipValue)!}"/>
                [#if element.programs?has_content]
                  [#list element.programs as flagship]<p class="checked">${(flagship.crpProgram.composedName)!'null'}</p>[/#list]
                [/#if]
              [/#if]
            </div>
          </div>
        [/#if]
        
        [#-- Regions --] 
        [#if regionsList?has_content] 
          <div class="col-md-6"> 
            <h5>[@s.text name="study.regions" /]:[@customForm.req required=editable /]</h5>
            <div id="" class="dottedBox">
              [#if editable]
                [#list regionsList as region]
                  [@customForm.checkBoxFlat id="region-${region.id}" name="${customName}.regionsValue" label="${region.composedName}" value="${region.id}" editable=editable checked=((regionsIds?seq_contains(region.id))!false) cssClass="checkboxInput rpInput" /]
                [/#list]
              [#else] 
                <input type="hidden" name="${customName}.regionsValue" value="${(element.regionsValue)!}"/>
                [#if  element.regions?has_content]
                  [#list element.regions as region]<p class="checked">${(region.crpProgram.composedName)!'null'}</p>[/#list]
                [/#if]
              [/#if]
            </div>
          </div>
        [/#if]
      </div>
      [/#if]
      
      [#-- 2. Short outcome/impact statement (up to 80 words) --]
      [#if isOutcomeCaseStudy]
      <div class="form-group">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.outcomeImpactStatement" i18nkey="study.outcomeStatement" help="study.outcomeStatement.help" className="limitWords-80" helpIcon=false required=true editable=editable /]
      </div>
      [/#if]
      
      [#-- 3. Link to Common Results Reporting Indicator #I3 --]
      [#if isOutcomeCaseStudy]
      <div class="form-group">
        [#-- Does this outcome reflect a contribution of the CGIAR in influencing or modifying policies/ strategies / laws/ regulations/ budgets/ investments or  curricula?  --]
        <div class="form-group">
          <label for="">[@s.text name="study.reportingIndicatorThree" /]:[@customForm.req required=editable /][@customForm.helpLabel name="study.reportingIndicatorThree.help" showIcon=false editable=editable/]</label>
          [#assign studyIndicatorThree = "studyIndicatorThree"]
          [@customForm.radioFlat id="${studyIndicatorThree}-yes" name="${name}.projectExpectedStudyInfo.isContribution" label="Yes" value="true" checked=((element.projectExpectedStudyInfo.isContribution)!false) cssClass="radioType-${studyIndicatorThree}" cssClassLabel="radio-label-yes" editable=editable /]
          [@customForm.radioFlat id="${studyIndicatorThree}-no" name="${name}.projectExpectedStudyInfo.isContribution" label="No" value="false" checked=!((element.projectExpectedStudyInfo.isContribution)!true) cssClass="radioType-${studyIndicatorThree}" cssClassLabel="radio-label-no" editable=editable /]
        </div>
        
        [#-- Disaggregates for CGIAR Indicator I3  --]
        <div class="form-group simpleBox block-${studyIndicatorThree}" style="display:${((element.projectExpectedStudyInfo.isContribution)!false)?string('block','none')}">
          [#local isBudgetInvestment = false]
          <div class="form-group row">
            <div class="col-md-6">
              [#-- Policy/Investment Type --]
              [@customForm.select name="${customName}.projectExpectedStudyInfo.repIndPolicyInvestimentType.id" className="setSelect2 policyInvestimentTypes" i18nkey="study.reportingIndicatorThree.policyType" listName="policyInvestimentTypes" keyFieldName="id"  displayFieldName="name" required=true /]
            </div>
            <div class="col-md-6 block-budgetInvestment" style="display:${isBudgetInvestment?string('block', 'none')}">
              [#-- Amount (Only for Budget or Investment) --]
              [@customForm.input name="${customName}.projectExpectedStudyInfo.policyAmount" i18nkey="study.reportingIndicatorThree.amount" help="study.reportingIndicatorThree.amount.help" className="" required=true editable=editable /]
            </div>
          </div>
          <div class="form-group row">
            <div class="col-md-6">
              [#-- Implementing Organization Type --]
              [@customForm.select name="${customName}.projectExpectedStudyInfo.repIndOrganizationType.id" className="setSelect2" i18nkey="study.reportingIndicatorThree.organizationType" listName="organizationTypes" keyFieldName="id"  displayFieldName="name" required=true /]
            </div>
            <div class="col-md-6">
              [#-- Stage in Process --]
              [@customForm.select name="${customName}.projectExpectedStudyInfo.repIndStageProcess.id" className="setSelect2" i18nkey="study.reportingIndicatorThree.stage" listName="stageProcesses" keyFieldName="id"  displayFieldName="name"required=true  /]
            </div>
          </div>
        </div>
      </div>
      [/#if]
      
      [#-- 4.  Maturity of change reported (tick-box)  --]
      [#if isOutcomeCaseStudy]
      <div class="form-group">
        <label for="">[@s.text name="study.maturityChange" /]:[@customForm.req required=editable /]
          [@customForm.helpLabel name="study.maturityChange.help" showIcon=false editable=editable/][@customForm.helpLabel name="study.maturityChange.help2" showIcon=true editable=editable/]
        </label>
        <div class="form-group">
          [#list stageStudies as stage]
            <p>[@customForm.radioFlat id="maturityChange-${stage.id}" name="${customName}.projectExpectedStudyInfo.repIndStageStudy.id" label="<small><b>${stage.name}:</b> ${stage.description}</small>" value="${stage.id}" checked=false cssClass="" cssClassLabel="font-normal" editable=editable/]</p> 
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
        
        [#-- SRF Targets  --]
        <div class="form-group simpleBox">
          [#-- List --]
          [@customForm.elementsListComponent name="${customName}.srfTargets" elementType="srfSloIndicator" elementList=keyContributions label="study.stratgicResultsLink.srfTargets"  listName="targets" keyFieldName="id" displayFieldName="title"/]
        </div>
        
        [#-- Comments  --]
        [#if isOutcomeCaseStudy]
        <div class="form-group simpleBox">
          [@customForm.textArea name="${customName}.projectExpectedStudyInfo.topLevelComments" i18nkey="study.stratgicResultsLink.comments" help="study.stratgicResultsLink.comments.help" className="" editable=editable /]
        </div>
        [/#if]
      </div>
      
      [#-- 6.  Geographic scope - Countries  --]
      [#if isOutcomeCaseStudy]
      <div class="form-group geographicScopeBlock">
        [#assign isRegional = ((element.geographicScope.id == action.reportingIndGeographicScopeRegional)!false) ]
        [#assign isMultiNational = ((element.geographicScope.id == action.reportingIndGeographicScopeMultiNational)!false) ]
        [#assign isNational = ((element.geographicScope.id == action.reportingIndGeographicScopeNational)!false) ]
        [#assign isSubNational = ((element.geographicScope.id == action.reportingIndGeographicScopeSubNational)!false) ]
        
        <label for="">[@s.text name="study.geographicScopeTopic" /]:[@customForm.req required=editable /]</label>
        <div class="form-group simpleBox">
          <div class="form-group row">
            <div class="col-md-6">
              [#-- Geographic Scope --]
              [@customForm.select name="${customName}.projectExpectedStudyInfo.repIndGeographicScope.id" className="setSelect2 geographicScopeSelect" i18nkey="study.geographicScope" listName="geographicScopes" keyFieldName="id"  displayFieldName="name" editable=editable required=true /]
            </div>
          </div>
          <div class="form-group regionalBlock" style="display:${(isRegional)?string('block','none')}">
            [#-- Regional scope --]
            [@customForm.selectGroup name="${customName}.projectExpectedStudyInfo.repIndRegion.id" list=(regions)![] element=(element.region.id)!{} subListName="subRegions"  keyFieldName="id" displayFieldName="name" i18nkey="study.region" required=true className="" editable=editable /]
          </div>
          <div class="form-group nationalBlock" style="display:${(isMultiNational || isNational || isSubNational)?string('block','none')}">
            [#-- Multinational, National and Subnational scope --]
            [@customForm.select name="${customName}.countriesIds" label="" i18nkey="study.countries" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="${name}.countriesIds" multiple=true required=true className="countriesSelect" disabled=!editable/]
          </div>
          <div class="form-group">
            [#-- Comment box --]
            [@customForm.textArea name="${customName}.projectExpectedStudyInfo.scopeComments" className="limitWords-30" i18nkey="study.geographicScopeComments" help="study.geographicScopeComments.help" editable=editable required=true/]
          </div>
        </div>
      </div>
      [/#if]

      [#-- 7. Key Contributors  --]
      [#if isOutcomeCaseStudy]
      <div class="form-group">
        <label for="">[@s.text name="study.keyContributors" /]:</label>
        <div class="form-group simpleBox">
          [@customForm.elementsListComponent name="${customName}.crps" elementType="globalUnit" elementList=element.crps label="study.keyContributors.crps"  listName="crps" keyFieldName="id" displayFieldName="composedName"/]
        </div>
        <div class="form-group simpleBox">
          [@customForm.elementsListComponent name="${customName}.flagships" elementType="crpProgram" elementList=element.flagships label="study.keyContributors.flagships"  listName="flagshipsList" keyFieldName="id" displayFieldName="composedName"/]
        </div>
        <div class="form-group simpleBox">
          [@customForm.elementsListComponent name="${customName}.institutions" elementType="institution" elementList=element.institutions label="study.keyContributors.externalPartners"  listName="institutions" keyFieldName="id" displayFieldName="composedName"/]
        </div>
      </div>
      [/#if]

      [#-- 8. Elaboration of Outcome/Impact Statement  --]
      [#if isOutcomeCaseStudy]
      <div class="form-group">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.elaborationOutcomeImpactStatement" i18nkey="study.elaborationStatement" help="study.elaborationStatement.help" helpIcon=false className="limitWords-400" required=true editable=editable /]
      </div>
      [/#if]
      
      [#-- 9. References cited  --]
      [#if isOutcomeCaseStudy]
      <div class="form-group">
        <div class="form-group">
          [@customForm.textArea name="${customName}.projectExpectedStudyInfo.references" i18nkey="study.referencesCited" help="study.referencesCited.help" helpIcon=false className="" required=true editable=editable /]
        </div>
        <div class="form-group" style="position:relative" listname="">
          [@customForm.fileUploadAjax 
            fileDB=(element.referencesFile)!{} 
            name="${customName}.referencesFile.id" 
            label="study.communicationMaterialsAttach" 
            dataUrl="${baseUrl}/uploadStudies.do" 
            path="${(action.getPath(expectedID))!}"
            isEditable=editable
            labelClass="label-min-width"
          /]          
        </div>
      </div>
      [/#if]
      
      [#-- 10. Quantification (where data is available)  --]
      [#if isOutcomeCaseStudy]
      <div class="form-group">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.quantification" i18nkey="study.quantification" help="study.quantification.help" helpIcon=false className=" " required=true editable=editable /]
      </div>
      [/#if]
      
      [#-- 11. Gender, Youth, and Capacity Development  --]
      [#if isOutcomeCaseStudy]
      <div class="form-group">
        <label for="">[@s.text name="study.crossCuttingRelevance" /]:
          [@customForm.helpLabel name="study.crossCuttingRelevance.help" showIcon=false editable=editable/]
        </label>
        [#-- Gender --]
        <div class="simpleBox">
          <label for="">[@s.text name="study.genderRelevance" /]:</label>
          <div class="form-group">
            [#list focusLevels  as cc]
              [@customForm.radioFlat id="genderRelevance-${cc_index}" name="${name}.projectExpectedStudyInfo.genderLevel.id" label="${cc.name}" value="${cc.id}" checked=false cssClass="" cssClassLabel="font-normal" editable=editable /]
            [/#list]
          </div>
          <div class="form-group">
            [@customForm.textArea name="${customName}.projectExpectedStudyInfo.describeGender" i18nkey="study.achievementsGenderRelevance" className="limitWords-100" required=true editable=editable /]
          </div>
        </div>
        [#-- Youth  --]
        <div class="simpleBox">
          <label for="">[@s.text name="study.youthRelevance" /]:</label>
          <div class="form-group">
            [#list focusLevels  as cc]
              [@customForm.radioFlat id="youthRelevance-${cc_index}" name="${name}.projectExpectedStudyInfo.youthLevel.id" label="${cc.name}" value="${cc.id}" checked=false cssClass="" cssClassLabel="font-normal" editable=editable /]
            [/#list]
          </div>
          <div class="form-group">
            [@customForm.textArea name="${customName}.projectExpectedStudyInfo.describeYouth" i18nkey="study.achievementsYouthRelevance"  className="limitWords-100" required=true editable=editable /]
          </div>
        </div>
        [#-- CapDev   --]
        <div class="simpleBox">
          <label for="">[@s.text name="study.capDevRelevance" /]:</label>
          <div class="form-group">
            [#list focusLevels  as cc]
              [@customForm.radioFlat id="capDevRelevance-${cc_index}" name="${name}.projectExpectedStudyInfo.capdevLevel.id" label="${cc.name}" value="${cc.id}" checked=false cssClass="" cssClassLabel="font-normal" editable=editable /]
            [/#list]
          </div>
          <div class="form-group">
            [@customForm.textArea name="${customName}.projectExpectedStudyInfo.describeCapdev" i18nkey="study.achievementsCapDevRelevance"  className="limitWords-100" required=true editable=editable /]
          </div>
        </div> 
      </div>
      [/#if]
      
      [#-- 12. Other cross-cutting dimensions   --]
      [#if isOutcomeCaseStudy]
      <div class="form-group">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.otherCrossCuttingDimensions" i18nkey="study.otherCrossCutting" help="study.otherCrossCutting.help" helpIcon=false className="limitWords-100" required=true editable=editable /]
      </div>
      [/#if]
      
      [#-- 13. Communications materials    --]
      [#if isOutcomeCaseStudy]
      <div class="form-group">
        <div class="form-group">
          [@customForm.textArea name="${customName}.projectExpectedStudyInfo.comunicationsMaterial" i18nkey="study.communicationMaterials" help="study.communicationMaterials.help" helpIcon=false className=" " required=true editable=editable /]
        </div>
        
        <div class="form-group" style="position:relative" listname="">
          [@customForm.fileUploadAjax 
            fileDB=(element.outcomeFile)!{} 
            name="${customName}.outcomeFile.id" 
            label="study.referencesCitedAttach" 
            dataUrl="${baseUrl}/uploadStudies.do" 
            path="${(action.getPath(expectedID))!}"
            isEditable=editable
            labelClass="label-min-width"
          /]
        </div>
      </div>
      [/#if]

      [#-- 14. Contact person    --]
      [#if isOutcomeCaseStudy]
      <div class="form-group">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.contacts" i18nkey="study.contacts" help="study.contacts.help" className="" helpIcon=false required=true editable=editable /]
      </div>
      [/#if]
      
      [#-- Comments for other studies--]
      [#if !isOutcomeCaseStudy]
      <div class="form-group"> 
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.topLevelComments" i18nkey="study.comments"  placeholder="" className="limitWords-100" required=true editable=isEditable /]
      </div>
      [/#if]
      
    </div>
    
    [#if fromProject]
    <h3 class="headTitle"> Share Study </h3>
    <div class="borderBox">
      [#-- Projects shared --]
      [@customForm.elementsListComponent name="${customName}.projects" elementType="project" elementList=element.projects label="study.sharedProjects"  listName="myProjects" keyFieldName="id" displayFieldName="composedName"/]
    </div>
    [/#if]
  </div>
[/#macro]
