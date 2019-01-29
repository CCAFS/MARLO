[#ftl]
[#macro studyMacro element name index=-1 template=false fromProject=true ]
  [#local customName = "${name}"/]
  [#local customId = "study-${template?string('template',index)}" /]
  [#local isOutcomeCaseStudy = ((element.projectExpectedStudyInfo.studyType.id == 1)!false) && reportingActive/]
  
  [#local isPolicy = ((element.projectExpectedStudyInfo.isContribution)!false) ]
  [#local stageProcessOne = ((element.projectExpectedStudyInfo.repIndStageProcess.id == 1))!false ]
  
  <div id="${customId}" class="caseStudy" style="display:${template?string('none','block')}">
    <div class="borderBox">
      <div class="form-group row">
        <div class="col-md-4">
          [@customForm.select name="${customName}.projectExpectedStudyInfo.studyType.id" value="${(element.projectExpectedStudyInfo.studyType.id)!-1}" className="setSelect2 studyType" i18nkey="study.type" listName="studyTypes" keyFieldName="id"  displayFieldName="name" required=true editable=editable && !isOutcomeCaseStudy /]
        </div>
        <div class="col-md-4">
          [@customForm.select name="${customName}.projectExpectedStudyInfo.status" className="setSelect2 statusSelect" i18nkey="study.status" listName="statuses" header=false required=true editable=editable /]
        </div>
        <div class="col-md-4">
          [@customForm.select name="${customName}.projectExpectedStudyInfo.year" className="setSelect2" i18nkey="study.year" listName="years" header=false required=true editable=editable /]
        </div>
      </div>
      
      [#if isOutcomeCaseStudy]
        <hr />
        [#-- Tags --]
        <div class="form-group">
          <label for="">[@s.text name="study.tags" /]:[@customForm.req required=editable /][@customForm.helpLabel name="study.tags.help" showIcon=false editable=editable/]</label>
          [#list ["tagNew", "tagUpdateSame", "tagUpdateNew"] as tag]
            <br /> [@customForm.radioFlat id="tag-${tag_index}" name="${customName}.tag" i18nkey="study.${tag}" value="${tag_index + 1}" checked=false cssClass="radioType-tags" cssClassLabel="font-normal" editable=editable /] 
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
      <div class="form-group">
        [@customForm.input name="${customName}.projectExpectedStudyInfo.commissioningStudy" i18nkey="study.commissioningStudy" help="study.commissioningStudy.help" className="" helpIcon=false required=true editable=editable /]
      </div>
      
      [#-- 2. Short outcome/impact statement (up to 80 words) --]
      [#if isOutcomeCaseStudy]
      <div class="form-group">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.outcomeImpactStatement" i18nkey="study.outcomeStatement" help="study.outcomeStatement.help" className="limitWords-80" helpIcon=false required=true editable=editable /]
      </div>
      [/#if]
      
      [#-- 3. Outcome story for communications use. NEW --]
      [#if isOutcomeCaseStudy]
      <div class="form-group">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.outcomestory" i18nkey="study.outcomestory" help="study.outcomestory.help" className="limitWords-400" helpIcon=false required=true editable=editable /]
      
        <br />
        <label for="">[@s.text name="study.outcomestoryLinks" /]:[@customForm.req required=editable /][@customForm.helpLabel name="study.outcomestoryLinks.help" showIcon=false editable=editable/]</label>
        <div class="linksBlock simpleBox">
          <div class="linksList">
            [#list (element.links)![{}] as link ]
              [@studyLink name="study.links" element=link index=link_index /]
            [/#list]
          </div>
          [#if editable]
          <div class="addButtonLink button-green pull-right"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> Add Link </div>
          <div class="clearfix"></div>
          [/#if]
        </div>
        [#-- Element item Template --]
        <div style="display:none">
          [@studyLink name="study.links" element={} index=-1 template=true /]
        </div>
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
          [@customForm.elementsListComponent name="${customName}.policies" elementType="projectPolicy" elementList=element.policies label="study.policies"  listName="policiesList" keyFieldName="id" displayFieldName="description"/]
          [#-- Note --]
          <div class="note">[@s.text name="study.policies.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/policies'][@s.param name='projectID']${(projectID)!}[/@s.param][/@s.url]">clicking here</a>[/@][/@]</div>
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
        
        [#-- SRF Targets  --]
        <div class="form-group simpleBox stageProcessOne">
          <label for="">[@s.text name="study.targetsOption" /]:[@customForm.req required=editable /][@customForm.helpLabel name="study.targetsOption.help" showIcon=false editable=editable/]</label><br />
          [#list ["targetsOptionYes", "targetsOptionNo", "targetsOptionTooEarlyToSay"] as option]
            [@customForm.radioFlat id="option-${option}" name="${customName}.projectExpectedStudyInfo.targetsOption" i18nkey="study.${option}" value="${option}" checked=false cssClass="radioType-targetsOption" cssClassLabel="font-normal" editable=editable /] 
          [/#list]
          [#local showTargetsComponent = (element.projectExpectedStudyInfo.targetsOption == "targetsOptionYes")!false /]
          <div class="srfTargetsComponent" style="display:${showTargetsComponent?string('block', 'none')}">
            [@customForm.elementsListComponent name="${customName}.srfTargets" elementType="srfSloIndicator" elementList=element.srfTargets label="study.stratgicResultsLink.srfTargets" listName="targets" keyFieldName="id" displayFieldName="title" required=editable && !(isPolicy && stageProcessOne)/]          
          </div>
        </div>
        
        [#-- Comments  --]
        [#if isOutcomeCaseStudy]
        <div class="form-group simpleBox stageProcessOne">
          [@customForm.textArea name="${customName}.projectExpectedStudyInfo.topLevelComments" i18nkey="study.stratgicResultsLink.comments" help="study.stratgicResultsLink.comments.help" helpIcon=false className="" editable=editable required=false /]
        </div>
        [/#if]
      </div>
      
      [#-- 6.  Geographic scope - Countries  --]
      <div class="form-group geographicScopeBlock">
        [#local geographicScope = ((element.projectExpectedStudyInfo.repIndGeographicScope.id)!-1) ]
        
        [#local isRegional = ((geographicScope == action.reportingIndGeographicScopeRegional)!false) ]
        [#local isMultiNational = ((geographicScope == action.reportingIndGeographicScopeMultiNational)!false) ]
        [#local isNational = ((geographicScope == action.reportingIndGeographicScopeNational)!false) ]
        [#local isSubNational = ((geographicScope == action.reportingIndGeographicScopeSubNational)!false) ]
        
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
              [@customForm.elementsListComponent name="${customName}.studyRegions" elementType="locElement" elementList=element.studyRegions label="study.region"  listName="regions" keyFieldName="id" displayFieldName="name" required=false /]
           [#-- [@customForm.selectGroup name="${customName}.projectExpectedStudyInfo.repIndRegion.id" list=(regions)![] element=(element.projectExpectedStudyInfo.repIndRegion)!{} subListName="subRegions"  keyFieldName="id" displayFieldName="name" i18nkey="study.region" required=true className="" editable=editable /]--]
          </div>
          <div class="form-group nationalBlock" style="display:${(isMultiNational || isNational || isSubNational)?string('block','none')}">
            [#-- Multinational, National and Subnational scope --]
            [@customForm.select name="${customName}.countriesIds" label="" i18nkey="study.countries" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="${customName}.countriesIds" multiple=true required=true className="countriesSelect" disabled=!editable/]
          </div>
          <div class="form-group">
            [#-- Comment box --]
            [@customForm.textArea name="${customName}.projectExpectedStudyInfo.scopeComments" className="limitWords-30" i18nkey="study.geographicScopeComments" help="study.geographicScopeComments.help" editable=editable required=false/]
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
      
      [#-- 8. Elaboration of Outcome/Impact Statement  --]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.elaborationOutcomeImpactStatement" i18nkey="study.elaborationStatement" help="study.elaborationStatement.help" helpIcon=false className="limitWords-400" required=editable && !(isPolicy && stageProcessOne) editable=editable /]
      </div>
      [/#if]
      
      [#-- 9. References cited  --]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        <div class="form-group">
          [@customForm.textArea name="${customName}.projectExpectedStudyInfo.referencesText" i18nkey="study.referencesCited" help="study.referencesCited.help" helpIcon=false className="" required=editable && !(isPolicy && stageProcessOne) editable=editable /]
        </div>
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
      </div>
      [/#if]
      
      [#-- 10. Quantification (where data is available)  --]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.quantification" i18nkey="study.quantification" help="study.quantification.help" helpIcon=false className=" " required=editable && !(isPolicy && stageProcessOne) editable=editable /]
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
              [@customForm.radioFlat id="genderRelevance-${cc_index}" name="${name}.projectExpectedStudyInfo.genderLevel.id" label="${cc.name}" value="${cc.id}" checked=(genderLevel == cc.id)!false cssClass="" cssClassLabel="font-normal" editable=editable /]
            [/#list]
          </div> 
          <div class="ccCommentBox" style="display:${(genderLevel != 1)?string('block', 'none')}">
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
              [@customForm.radioFlat id="youthRelevance-${cc_index}" name="${name}.projectExpectedStudyInfo.youthLevel.id" label="${cc.name}" value="${cc.id}" checked=(youthLevel == cc.id)!false cssClass="" cssClassLabel="font-normal" editable=editable /]
            [/#list]
          </div> 
          <div class="ccCommentBox" style="display:${(youthLevel != 1)?string('block', 'none')}">
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
              [@customForm.radioFlat id="capDevRelevance-${cc_index}" name="${name}.projectExpectedStudyInfo.capdevLevel.id" label="${cc.name}" value="${cc.id}" checked=(capdevLevel == cc.id)!false cssClass="" cssClassLabel="font-normal" editable=editable /]
            [/#list]
          </div>
          <div class="ccCommentBox" style="display:${(capdevLevel != 1)?string('block', 'none')}">
            <div class="form-group stageProcessOne">
              [@customForm.textArea name="${customName}.projectExpectedStudyInfo.describeCapdev" i18nkey="study.achievementsCapDevRelevance"  className="limitWords-100" required=editable && !(isPolicy && stageProcessOne) editable=editable /]
            </div>
          </div>
        </div> 
      </div>
      [/#if]
      
      [#-- 12. Other cross-cutting dimensions   --]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.otherCrossCuttingDimensions" i18nkey="study.otherCrossCutting" help="study.otherCrossCutting.help" helpIcon=false className="limitWords-100" required=false editable=editable /]
      </div>
      [/#if]
      
      [#-- 13. Communications materials    --]
      [#if isOutcomeCaseStudy]
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
      [/#if]

      [#-- 14. Contact person    --]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.contacts" i18nkey="study.contacts" help="study.contacts.help" className="" helpIcon=false required=editable && !(isPolicy && stageProcessOne) editable=editable /]
      </div>
      [/#if]
      
      [#-- Comments for other studies--]
      [#if !isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.topLevelComments" i18nkey="study.comments"  placeholder="" className="limitWords-100" required=editable && !(isPolicy && stageProcessOne) editable=editable /]
      </div>
      [/#if]
      
    </div>
    
    [#if fromProject]
    <h3 class="headTitle"> Share Study </h3>
    <div class="borderBox">
      [#-- Projects shared --]
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
    <span class="pull-left" style="width:90%">[@customForm.input name="${customName}.value" placeholder="global.webSiteLink.placeholder" showTitle=false i18nkey="" className="" editable=editable /]</span>
    [#if editable]<div class="removeElement sm removeIcon removeLink" title="Remove"></div>[/#if]
    <div class="clearfix"></div>
  </div>
[/#macro]


