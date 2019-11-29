[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

[#macro deliverableGeographicScope]
  <div class="block-geographicScope geographicScopeBlock">
    [#assign geographicScopeList = (deliverable.geographicScopes)![] ]
    [#assign isRegional =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeRegional) /]
    [#assign isMultiNational = findElementID(geographicScopeList,  action.reportingIndGeographicScopeMultiNational) /]
    [#assign isNational =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeNational) /]
    [#assign isSubNational =   findElementID(geographicScopeList,  action.reportingIndGeographicScopeSubNational) /]
    
    <div class="form-group">
      <div class="row">
        <div class="col-md-6">
          [#-- Geographic Scope --]
          [@customForm.elementsListComponent name="deliverable.geographicScopes" elementType="repIndGeographicScope" elementList=deliverable.geographicScopes label="deliverable.geographicScope" listName="repIndGeographicScopes" keyFieldName="id" displayFieldName="name" required=true /]
        </div>
      </div>
      <div class="form-group regionalBlock" style="display:${(isRegional)?string('block','none')}">
        [#-- Regional scope --]
        [@customForm.elementsListComponent name="deliverable.deliverableRegions" elementType="locElement" elementList=deliverable.deliverableRegions label="deliverable.region"  listName="repIndRegions" keyFieldName="id" displayFieldName="composedName" required=false /]
      </div>
      <div class="form-group nationalBlock" style="display:${(isMultiNational || isNational || isSubNational)?string('block','none')}">
        [#-- Multinational, National and Subnational scope --]
        [@customForm.select name="deliverable.countriesIds" label="" i18nkey="deliverable.countries" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="deliverable.countriesIds" multiple=true required=true className="countriesSelect" disabled=!editable/]
      </div>
    </div>
   
  </div>
[/#macro]

[#macro deliverableCrossCuttingMacroOld label="deliverable.crossCuttingDimensions" ]
  [#-- Does this deliverable have a cross-cutting dimension --]
  <div class="form-group">
    <label for="">[@customForm.text name=label readText=!editable/] [@customForm.req required=editable/]</label>
    [#assign crossCuttingMarkers = [
        { "id":"gender",    "name": "crossCuttingGender",   "scoreName": "crossCuttingScoreGender" },
        { "id":"youth",     "name": "crossCuttingYouth",    "scoreName": "crossCuttingScoreYouth" },
        { "id":"capacity",  "name": "crossCuttingCapacity", "scoreName": "crossCuttingScoreCapacity" },
        { "id":"climate",   "name": "crossCuttingClimate",  "scoreName": "crossCuttingScoreClimate" },
        { "id":"na",        "name": "crossCuttingNa" }
      ] 
    /]
    [#if editable]
      [#list crossCuttingMarkers as marker]
        <label class="checkbox-inline"><input type="checkbox" name="deliverable.deliverableInfo.${marker.name}" class="[#if marker.id != "na"]crosscutingDimension[/#if]" id="${marker.id}" value="true" [#if (deliverable.deliverableInfo[marker.name])!false ]checked="checked"[/#if]> [@s.text name="crossCuttingMarker.${marker.id}" /]</label>
      [/#list]
    [#else]
      [#assign checkedItems = false /]
      [#list crossCuttingMarkers as marker]
        [#if (deliverable.deliverableInfo[marker.name])!false ]
          <div class="${customForm.changedField('deliverable.deliverableInfo.${marker.name}')}">
            <p class="checked"> [@s.text name="crossCuttingMarker.${marker.id}" /]</p> <input type="hidden" name="deliverable.deliverableInfo.${marker.name}" value="true">
          </div>
          [#assign checkedItems = true /]
        [/#if] 
      [/#list]
      [#-- Message when there's nothing to show -> "Prefilled if avaible" --]
      [#if !checkedItems]<div class="input"><p>[@s.text name="form.values.fieldEmpty" /]</p></div>[/#if]
    [/#if]
  </div>
  
  [#-- If gender dimension, select with ones --]
  <div id="gender-levels" class="panel tertiary" style="display:${((deliverable.deliverableInfo.crossCuttingGender)!false)?string('block','none')}">
  [#if !action.hasSpecificities('crp_one_gender')]
    [#if deliverable.genderLevels?has_content]
      <div class="panel-head"><label for=""> [@customForm.text name="deliverable.genderLevels" readText=!editable /]:</label></div>
      <div id="genderLevelsList" class="panel-body" listname="deliverable.genderLevels"> 
        <ul class="list">
          [#list deliverable.genderLevels as element]
            <li class="genderLevel clearfix">
              <input class="id" type="hidden" name="deliverable.genderLevels[${element_index}].id" value="${(element.id)!}" />
              <input class="fId" type="hidden" name="deliverable.genderLevels[${element_index}].genderLevel" value="${(element.genderLevel)!}" />
              <span title="${(element.nameGenderLevel)!'undefined'}" class="name">[@utils.wordCutter string=(element.nameGenderLevel)!"undefined" maxPos=100 substr=" "/]</span>
              <div class="clearfix"></div>
            </li>
          [/#list]
        </ul>
      </div>
    [/#if]  
  [#else]
    [#if ((deliverable.genderLevels[0])?? && deliverable.genderLevels[0].descriptionGenderLevel??)]
    <label for="">[@customForm.text name="deliverable.genderLevels" readText=!editable /]:</label>
    <div class="input"> 
      <span>${(deliverable.genderLevels[0].nameGenderLevel)!'Prefilled if available'}</span> - <i><span>${(deliverable.genderLevels[0].descriptionGenderLevel)!}</span></i>
      <input type="hidden" name="deliverable.genderLevels[0].genderLevel" value="${(deliverable.genderLevels[0].genderLevel)!}" />
    </div>
    [/#if]
  [/#if]
  </div>
  
  [#-- Cross-cutting dimensions blocks --]
  <div class="row">
  [#list crossCuttingMarkers as marker]
    [#if marker.scoreName??]
      <div id="ccDimension-${marker.id}"  class="col-md-3 ccDimension" style="display:${((deliverable.deliverableInfo[marker.name])!false)?string('block-inline','none')}">
        [@customForm.select name="deliverable.deliverableInfo.${marker.scoreName}" label="" i18nkey="crossCuttingMarker.${marker.id}" help="deliverable.ccDimension.${marker.id}.help" listName="crossCuttingScoresMap" required=true header=false className="crossCuttingDimensionsSelect" editable=editable/]
      </div>
    [/#if]
  [/#list]
  </div>
  
  [#-- Gender Types List --]
  <div style="display:none">
    [#if genderLevels?has_content]
      [#list genderLevels as element]
        <span id="genderLevel-${(element.id)!}">
          <span class="description">${(element.description)!}</span><br />
          <i><span class="completeDescription">${(element.completeDescription)!}</span></i>
        </span>
      [/#list]
    [/#if]
  </div>
[/#macro]

[#macro deliverableCrossCuttingMacro label="deliverable.crossCuttingDimensions" ]
  [#-- CGIAR Cross-cutting Markers  --]
  <div class="form-group">
    <h5 class="labelheader">[@s.text name=label /]</h5>
    <div class="row">
      [#list (cgiarCrossCuttingMarkers)![] as marker]
        [#local customName = "deliverable.crossCuttingMarkers[${marker_index}]" /]
        <div class="col-md-3">
          [#local markerElement = (action.getDeliverableCrossCuttingMarker(marker.id))!{} ]
          <input type="hidden"  name="${customName}.id" value="${(markerElement.id)!}"/>
          <input type="hidden"  name="${customName}.cgiarCrossCuttingMarker.id" value="${marker.id}"/>
          [@customForm.select   name="${customName}.repIndGenderYouthFocusLevel.id" value="${(markerElement.repIndGenderYouthFocusLevel.id)!-1}" valueName="${(markerElement.repIndGenderYouthFocusLevel.powbName)!}" className="setSelect2" i18nkey="${marker.name}" listName="focusLevels" keyFieldName="id"  displayFieldName="powbName"  required=true editable=editable/]
        </div>
      [#else]
        <p class="col-md-12">No cgiarCrossCuttingMarkers loaded</p>
      [/#list]
    </div>
  </div>
[/#macro]

[#macro deliverableLicenseMacro ]
<div class="simpleBox">
  <div class="form-group row yesNoInputDeliverable">
    <label class="col-md-9 yesNoLabel" for="">[@s.text name="project.deliverable.dissemination.adoptedLicenseQuestion" /] [@customForm.req required=reportingActive /]</label>
    <div class="col-md-3">[@customForm.yesNoInputDeliverable name="deliverable.deliverableInfo.adoptedLicense"  editable=editable inverse=false  cssClass="type-license text-center" /] </div>  
  </div>  
</div>
[/#macro]


[#macro isOpenaccessMacro ]
  [#local customName = "deliverable.dissemination"]
  <div class="simpleBox form-group">
    <input type="hidden"  name="${customName}.id" value="${(deliverable.dissemination.id)!}" />
    <div class="row yesNoInputDeliverable">
      <label class="col-md-9 yesNoLabel" for="">Is this deliverable Open Access? [@customForm.req required=reportingActive /]</label>
      <div class="col-md-3">[@customForm.yesNoInputDeliverable name="${customName}.isOpenAccess"  editable=editable inverse=false cssClass="type-accessible inverted-true text-center" /]  </div>
    </div> 
    <div class="block-accessible" style="display: ${((!deliverable.dissemination.isOpenAccess)!false)?string("block","none")};">
      <hr />
      [#local oaRestrictions = [
        {"value": "intellectualProperty", "isChecked": (deliverable.dissemination.intellectualProperty)!false },
        {"value": "limitedExclusivity", "isChecked": (deliverable.dissemination.limitedExclusivity)!false }
        {"value": "restrictedUseAgreement", "isChecked": (deliverable.dissemination.restrictedUseAgreement)!false }
        {"value": "effectiveDateRestriction", "isChecked": (deliverable.dissemination.effectiveDateRestriction)!false }
        {"value": "notDisseminated", "isChecked": (deliverable.dissemination.notDisseminated)!false }
      ] /]
      
      <label for="">Select the Open Access restriction:[@customForm.req required=editable /]</label>
      [#list oaRestrictions as restriction]
        [#local restrictionLabel][@s.text name="deliverable.oaRestriction.${restriction.value}" /][/#local]
        <p>[@customForm.radioFlat id="oaRestrictions-${restriction_index}" name="${customName}.type" label="${restrictionLabel}" editable=editable value="${restriction.value}" checked=restriction.isChecked cssClass="" cssClassLabel="font-normal" /]</p>
      [/#list]

      <div class="row restrictionDate-block" style="display:[#if ((deliverable.dissemination.restrictedUseAgreement)!false) || ((deliverable.dissemination.effectiveDateRestriction)!false) ]block[#else]none [/#if];">
        <div class="col-md-5">
          [@customForm.input name="${customName}.${(deliverable.dissemination.restrictedUseAgreement?string('restrictedAccessUntil','restrictedEmbargoed'))!'restrictedAccessUntil'}" type="text" i18nkey="${(deliverable.dissemination.restrictedUseAgreement?string('Restricted access until','Restricted embargoed date'))!}"  placeholder="" className="restrictionDate col-md-6" required=true editable=editable /]
        </div>
      </div>
    </div>
  </div>
[/#macro]

[#-- Does this deliverable involve Participants and Trainees? --]
[#macro deliverableParticipantsMacro ]
[#local customName = "deliverable.deliverableParticipant" /]
<div class="simpleBox">
  <div class="form-group row yesNoInputDeliverable">
    <label class="col-md-9 yesNoLabel" for="">[@s.text name="deliverable.involveParticipants.title" /] [@customForm.req required=reportingActive /]</label>
    <div class="col-md-3">[@customForm.yesNoInputDeliverable name="${customName}.hasParticipants"  editable=editable inverse=false  cssClass="type-involveParticipants text-center" neutral=true  /] </div>  
  </div>
  
  <div class="block-involveParticipants" style="display:${((deliverable.deliverableParticipant.hasParticipants)!false)?string('block','none')}">
    <hr />
    [#-- Title Event/Activity --]
    <div class="form-group">
      [@customForm.input name="${customName}.eventActivityName" i18nkey="involveParticipants.title" className="limitWords-20" required=editable editable=editable /]
    </div>
    
    [#-- Type of activity --]
    <div class="form-group row">
      <div class="col-md-6">
        [@customForm.select name="${customName}.repIndTypeActivity.id" className="setSelect2 trainingType" i18nkey="involveParticipants.typeActivity" listName="repIndTypeActivities" keyFieldName="id"  displayFieldName="name" editable=editable required=editable /]
      </div>
      [#-- Formal training: Academic Degree --]
      [#local isAcademicDegree = (deliverable.deliverableParticipant.repIndTypeActivity.id == 1)!false /]
      <div class="col-md-6 block-academicDegree" style="display:${isAcademicDegree?string('block','none')}">
        [@customForm.input name="${customName}.academicDegree" i18nkey="involveParticipants.academicDegree" help="involveParticipants.academicDegree.help" className="" required=true editable=editable /]
      </div>
    </div>
    
    [#-- Total number of Participants: --]
    <div class="form-group row">
      <div class="col-md-6">
        [@customForm.input name="${customName}.participants" i18nkey="involveParticipants.participants" help="involveParticipants.participants.help" placeholder="global.number" className="numericInput" required=editable editable=editable /]
        <div class="dottedBox">
          [@customForm.checkBoxFlat id="estimateParticipants" name="${customName}.estimateParticipants" label="involveParticipants.estimate" value="true" editable=editable checked=((deliverable.deliverableParticipant.estimateParticipants)!false) cssClass="" cssClassLabel="font-italic" /]
        </div>
      </div>
      <div class="col-md-6 femaleNumbers">
        [@customForm.input name="${customName}.females" i18nkey="involveParticipants.females" help="involveParticipants.females.help" placeholder="global.number" className="numericInput" required=true editable=editable disabled=(deliverable.deliverableParticipant.dontKnowFemale)!false /]
        <div class="dottedBox">
          [@customForm.checkBoxFlat id="estimateFemales" name="${customName}.estimateFemales" label="involveParticipants.estimate" value="true" editable=editable checked=((deliverable.deliverableParticipant.estimateFemales)!false) cssClass="" cssClassLabel="font-italic" /]
          [@customForm.checkBoxFlat id="dontKnowFemale" name="${customName}.dontKnowFemale" label="involveParticipants.dontKnow" help="involveParticipants.dontKnow.help" value="true" editable=editable checked=((deliverable.deliverableParticipant.dontKnowFemale)!false) cssClass="" cssClassLabel="font-italic" /]
        </div>
      </div>
    </div>
    
    [#-- Type of Participant(s): --]
    <div class="form-group row">
      <div class="col-md-6">
        [@customForm.select name="${customName}.repIndTypeParticipant.id" className="setSelect2" i18nkey="involveParticipants.participantsType" listName="repIndTypeParticipants" keyFieldName="id"  displayFieldName="name" editable=editable required=editable /]
      </div>
    </div>
    
    [#-- Training period of time: (Only if formal training) --]
    [#local isFormal = ([1, 3, 2, 4]?seq_contains(deliverable.deliverableParticipant.repIndTypeActivity.id))!false /]
    <div class="form-group block-periodTime" style="display:${isFormal?string('block','none')}">
      <label for="">[@s.text name="involveParticipants.trainingPeriod" /]:[@customForm.req required=editable /]
        [@customForm.helpLabel name="involveParticipants.trainingPeriod.help" showIcon=false editable=editable/] 
      </label><br />
      [#list (repIndTrainingTerms)![] as item]
        [@customForm.radioFlat id="trainingPeriod-${item.id}"  name="${customName}.repIndTrainingTerm.id" label="${item.name}"  value="${item.id}"  checked=(deliverable.deliverableParticipant.repIndTrainingTerm.id == item.id)!false cssClass="" cssClassLabel="font-normal" editable=editable /]
      [#else]
        LIST NOT FOUND
      [/#list]
    </div> 
    
  </div>
</div>
[/#macro]


[#macro alreadyDisseminatedMacro ]
  [#local name = "deliverable.dissemination"  /]
  <div class="simpleBox form-group">
    <div class=" row yesNoInputDeliverable">
      <span class="col-md-9">
        <label class="yesNoLabel" for="">[@s.text name="project.deliverable.dissemination.alreadyDisseminatedQuestion" /] [@customForm.req required=reportingActive /]</label>
        <p><small>[@s.text name="project.deliverable.dissemination.alreadyDisseminatedSubQ" /] </small></p>
      </span>
      <div class="col-md-3">
        [@customForm.yesNoInputDeliverable name="${name}.alreadyDisseminated"  editable=editable inverse=false cssClass="type-findable text-center" /] 
      </div>  
    </div>
    [#local isDisseminated = (deliverable.dissemination.alreadyDisseminated?string)!""]
    <div class="block-findable findableOptions" style="display:${(isDisseminated == "true")?string('block', 'none')}">
      <hr />
      [@findableOptions /]
    </div>
    <div class="block-notFindable findableOptions" style="display:${(isDisseminated == "false")?string('block', 'none')}">
      <hr />
      [#-- Is this deliverable confidential and/or a management/internal deliverable? --]
      <div class="form-group">
        <label for="">[@s.text name="project.deliverable.dissemination.confidential" /]?[@customForm.req required=editable /]</label> <br />
        [#assign isConfidential = (deliverable.dissemination.confidential?string)!""]
        [@customForm.radioFlat id="confidetial-yes" name="${name}.confidential" label="Yes" value="true"  checked=(isConfidential == "true")  cssClass="radioType-confidential" cssClassLabel="radio-label-yes" editable=editable /]
        [@customForm.radioFlat id="confidetial-no"  name="${name}.confidential" label="No"  value="false" checked=(isConfidential == "false") cssClass="radioType-confidential" cssClassLabel="radio-label-no"  editable=editable /]
      </div>
      
      [#-- Confidential URL --]
      <div class="form-group confidentialBlock-true" style="display:${(isConfidential == "true")?string('block', 'none')}">
        [@customForm.input name="${name}.confidentialUrl" type="text" i18nkey="project.deliverable.dissemination.confidentialUrl" help="project.deliverable.dissemination.confidentialUrl.help" helpIcon=false placeholder="global.webSiteLink.placeholder" className="" required=true editable=editable /]
      </div>
      <div class="form-group confidentialBlock-false" style="display:${(isConfidential == "false")?string('block', 'none')}">
        <p class="note"> [@s.text name="project.deliverable.dissemination.confidentialNoMessage" /] </p>
      </div>
    </div>
  </div>
[/#macro]

[#macro findableOptions ]
  [#local isSynced = (deliverable.dissemination.synced)!false ]
  [#local customName = "deliverable.dissemination" /]
  <div class="disseminationChannelBlock" style="display:${isSynced?string('none','block')};">
    [#-- Note --]
    <div class="note">[@s.text name="project.deliverable.dissemination.channelInfo" /]</div>
    <div class="form-group row">
      <div class="col-md-4">
        [#if editable]
          [@customForm.select name="${customName}.disseminationChannel" value="'${(deliverable.dissemination.disseminationChannel)!}'"  stringKey=true label=""  i18nkey="project.deliverable.dissemination.selectChannelLabel" listName="repositoryChannels" displayFieldName="name" keyFieldName="shortName" className="disseminationChannel"   multiple=false required=true   editable=editable/]
        [#else]
        <div class="input">
          <label for="disChannel" style="display:block;">Dissemination channel:</label>
          <p>${((deliverable.dissemination.disseminationChannel?upper_case)!)!'Prefilled if available'}</p>
        </div>
        [/#if]
      </div>
      <div class="col-md-8">
        [#if editable]
          [#list (repositoryChannels)![]  as channel]
            [#if channel.shortName != "other"]
              [#-- Examples & instructions --]
              [@channelExampleMacro name=channel.shortName url=channel.urlExample /]
            [/#if]
          [/#list]
        [/#if]
      </div>
    </div>
  </div>
  
  [#assign channelsArray = [] /] 
  <ul id="channelsList" style="display:none">
    [#list (repositoryChannels)![]  as channel]
      [#if channel.shortName != "other"]
        <li>
          [#assign channelsArray = [ channel.shortName ] + channelsArray  /]
          <span class="id">${channel.shortName}</span>
          <span class="name">${channel.name}</span>
        </li>
      [/#if]
    [/#list]
  </ul>
  [#local channelSelected = (deliverable.dissemination.disseminationChannel)!'-1' /]
  <div id="disseminationUrl" style="display:[#if channelsArray?seq_contains(channelSelected) || (channelSelected == "other") ]block[#else]none[/#if];">
    <div class="form-group" > 
      <div class="url-field">
        [@customForm.input name="${customName}.disseminationUrl" type="text" i18nkey="project.deliverable.dissemination.disseminationUrl"  placeholder="" className="deliverableDisseminationUrl" required=true readOnly=isSynced editable=editable /]
      </div>
      <div class="buttons-field">
        [#if editable]
          [#local showSync = (channelsArray?seq_contains(deliverable.dissemination.disseminationChannel))!false ]
          <div id="fillMetadata" style="display:${showSync?string('block','none')};">
            <input type="hidden" name="deliverable.dissemination.synced" value="${isSynced?string}" />
            [#-- Sync Button --]
            <div class="checkButton" style="display:${isSynced?string('none','block')};">[@s.text name="project.deliverable.dissemination.sync" /]</div>
            <div class="unSyncBlock" style="display:${isSynced?string('block','none')};">
              [#-- Update Button --]
              <div class="updateButton">[@s.text name="project.deliverable.dissemination.update" /]</div>
              [#-- Unsync Button --]
              <div class="uncheckButton">[@s.text name="project.deliverable.dissemination.unsync" /]</div>
            </div>
          </div>
        [/#if]
      </div>
    </div>
    <div class="clearfix"></div>
  </div>
  <div id="metadata-output"></div>
[/#macro]

[#macro channelExampleMacro name="" url="" ]
  <div class="exampleUrl-block channel-${name}" style="display:[#if (deliverable.dissemination.disseminationChannel==name)!false]block[#else]none[/#if];">
    <label for="">[@s.text name="project.deliverable.dissemination.exampleUrl" /]:</label>
    <p><small>${url}</small></p>
  </div>
[/#macro]

[#macro deliverableMetadataMacro flagshipslistName="programs" crpsListName="crps" allowFlagships=true]
  <div class="form-group">
    [@metadataField title="title" encodedName="dc.title" type="input" require=false/]
  </div>
  <div class="form-group">
    [@metadataField title="description" encodedName="dc.description.abstract" type="textArea" require=false/]
  </div>
  <div class="form-group row">
    <div class="col-md-6">
      [@metadataField title="publicationDate" encodedName="dc.date" type="input" require=false/]
    </div>
    <div class="col-md-6">
      [@metadataField title="language" encodedName="dc.language" type="input" require=false/]
    </div>
  </div>
  <div class="form-group row">
    <div class="col-md-6">
      [@metadataField title="countries" encodedName="cg:coverage.country" type="input" require=false/]
    </div>
    <div class="col-md-6">
      [@metadataField title="keywords" encodedName="marlo.keywords" type="input" require=false/]
    </div>
  </div>
  <div class="form-group"> 
    [@metadataField title="citation" encodedName="dc.identifier.citation" type="textArea" require=false/]
  </div>
  <div class="form-group row">
    <div class="col-md-6">
      [@metadataField title="handle" encodedName="marlo.handle" type="input" require=false/]
    </div>
    <div class="col-md-6">
      [@metadataField title="doi" encodedName="marlo.doi" type="input" require=false/]
    </div>
  </div>
   
  <hr />
   
  [#-- Creator/Authors --]
  <div class="form-group">
    <label for="">[@s.text name="metadata.creator" /]:  </label>
    [#-- Hidden input --]
    [@metadataField title="authors" encodedName="marlo.authors" type="hidden" require=false/]
    [#-- Some Instructions  --]
    [#if editable]
      <div class="note authorVisibles" style="display:${isMetadataHide("marlo.authors")?string('none','block')}">
      [@s.text name = "project.deliverable.dissemination.authorsInfo" /]
      </div>
    [/#if]
    [#-- Authors List --]
    <div class="authorsList simpleBox row" >
      [#if deliverable.users?has_content]
        [#list deliverable.users as author]
          [@deliverableMacros.authorMacro element=author index=author_index name="deliverable.users"  /]
        [/#list]
      [#else]
        <p class="emptyText text-center "> [@s.text name="project.deliverable.dissemination.notCreators" /]</p>
      [/#if]
    </div>
    [#-- Add an author --]
    [#if editable]
    <div class="dottedBox authorVisibles" style="display:${isMetadataHide("marlo.authors")?string('none','block')}">
    <label for="">Add an Author:</label>
    <div class="form-group">
      <div class="pull-left" style="width:25%"><input class="form-control input-sm lName"  placeholder="Last Name" type="text" /> </div>
      <div class="pull-left" style="width:25%"><input class="form-control input-sm fName"  placeholder="First Name" type="text" /> </div>
      <div class="pull-left" style="width:36%"><input class="form-control input-sm oId"    placeholder="ORCID (e.g. orcid.org/0000-0002-6066...)" type="text" title="ORCID is a nonprofit helping create a world in which all who participate in research, scholarship and innovation are uniquely identified and connected to their contributions and affiliations, across disciplines, borders, and time."/> </div>
      <div class="pull-right" style="width:14%">
        <div id="" class="addAuthor text-right"><div class="button-blue"><span class="glyphicon glyphicon-plus-sign"></span> [@s.text name="project.deliverable.dissemination.addAuthor" /]</div></div>
      </div>
    </div>
    <div class="clearfix"></div>
    </div>
    [/#if] 
  </div>
  
  <div class="publicationMetadataBlock" style="display:${displayDeliverableRule(deliverable, deliverablePublicationMetadata)!};">
    <br />
    <h4 class="sectionSubTitle">[@s.text name="project.deliverable.dissemination.publicationTitle"/]</h4>
    <input type="hidden" name="deliverable.publication.id" value="${(deliverable.publication.id)!}"/>
    [#if editable] <p class="note">[@s.text name="project.deliverable.dissemination.journalFields" /]</p> [/#if]
    <div class="form-group row">
      <div class="col-md-4 metadataElement metadataElement-volume no-lock">
        [@customForm.input name="deliverable.publication.volume" i18nkey="project.deliverable.dissemination.volume" className="metadataValue" type="text" disabled=!editable  required=true editable=editable /]
      </div> 
      <div class="col-md-4 metadataElement metadataElement-issue no-lock">
        [@customForm.input name="deliverable.publication.issue" i18nkey="project.deliverable.dissemination.issue" className="metadataValue" type="text" disabled=!editable  required=true editable=editable /]
      </div>
      <div class="col-md-4 metadataElement metadataElement-pages no-lock">
        [@customForm.input name="deliverable.publication.pages" i18nkey="project.deliverable.dissemination.pages" className="metadataValue" type="text" disabled=!editable  required=true editable=editable /]
      </div>
    </div>
    <div class="form-group metadataElement metadataElement-journal no-lock">
      [@customForm.input name="deliverable.publication.journal" i18nkey="project.deliverable.dissemination.journalName" className="metadataValue" type="text" disabled=!editable  required=true editable=editable /]
    </div>
    
    [#-- Is ISI Journal --]
    <div class="form-group">
      <label for="">[@s.text name="deliverable.isiPublication" /] [@customForm.req required=editable /]
      [@customForm.helpLabel name="deliverable.isiPublication.help" showIcon=false editable=editable/]</label> <br />
      [#local isISI = (deliverable.publication.isiPublication?string)!"" /]
      [@customForm.radioFlat id="optionISI-yes"  name="deliverable.publication.isiPublication" i18nkey="Yes"  value="true"  checked=(isISI == "true")  cssClass="radioType-optionISI" cssClassLabel="font-normal radio-label-yes" editable=editable /] 
      [@customForm.radioFlat id="optionISI-no"   name="deliverable.publication.isiPublication" i18nkey="No"   value="false" checked=(isISI == "false") cssClass="radioType-optionISI" cssClassLabel="font-normal radio-label-no"  editable=editable /] 
    </div>
    
    [#-- Journal Indicators --]
    [#-- <div class="form-group">
      <label for="">[@s.text name="project.deliverable.dissemination.indicatorsJournal" /]:
      <div class="checkbox">
        [#if editable]
          <label for="nasr"><input type="checkbox" id="nasr" name="deliverable.publication.nasr" value="true" [#if (deliverable.publication.nasr)!false]checked[/#if]/>Does this article have a co-author from a developing country National Agricultural Research System (NARS) ?</label>
          <label for="coAuthor"><input type="checkbox" id="coAuthor" name="deliverable.publication.coAuthor" value="true" [#if (deliverable.publication.coAuthor)!false]checked[/#if] />Does this article have a co-author based in an Earth System Science-related academic department?</label>
        [#else]
          <p [#if (deliverable.publication.nasr)!false]class="checked"[#else]class="noChecked"[/#if]>Does this article have a co-author from a developing country National Agricultural Research System (NARS) ?</p>
          <p [#if (deliverable.publication.coAuthor)!false]class="checked"[#else]class="noChecked"[/#if]>Does this article have a co-author based in an Earth System Science-related academic department?</p>
        [/#if]
      </div>
    </div>  --]
    <hr />
    [#-- Does the publication acknowledge {CRP}? --]
    <div class="row yesNoInputDeliverable">
      <div class="col-md-9">
        <label class="yesNoLabel">[@s.text name="project.deliverable.dissemination.acknowledgeQuestion" ][@s.param]${(crpSession?upper_case)!}[/@s.param][/@s.text]</label>
        <p class="message"><i><small>[@s.text name="project.deliverable.dissemination.acknowledgeQuestion.help" ][@s.param]${(crpSession?upper_case)!}[/@s.param][/@s.text]</small></i></p>
      </div>
      <div class="col-md-3">[@customForm.yesNoInputDeliverable name="deliverable.publication.publicationAcknowledge"  editable=editable inverse=false  cssClass="acknowledge text-center" /] </div> 
    </div>
    <hr />
    
    <div class="form-group">
      <label for="">[@s.text name="project.deliverable.dissemination.publicationContribution" /]</label>
      <div class="flagshipList simpleBox">
        [#if deliverable.crps?has_content]
          [#list deliverable.crps as flagShips]
            [@deliverableMacros.flagshipMacro element=flagShips index=flagShips_index name="deliverable.crps"  isTemplate=false /]
          [/#list]
        [#else]
          <p class="emptyText text-center "> [@s.text name="project.deliverable.dissemination.Notflagships" /]</p> 
        [/#if]
      </div>
      [#if editable]
        <div class="form-group row">
          <div class="col-md-${allowFlagships?string('5','12')}">
            [@customForm.select name="" label=""  i18nkey="project.deliverable.dissemination.selectCRP" listName=crpsListName keyFieldName="id"  displayFieldName="composedName" className="crpSelect" editable=editable/]
          </div>
          [#if allowFlagships]
            <div class="col-md-7">
              [@customForm.select name="" label=""  i18nkey="project.deliverable.dissemination.selectFlagships" paramText="${(crpSession)!'CRP'}" listName=flagshipslistName keyFieldName="id"  displayFieldName="composedName" className="flaghsipSelect" editable=editable/]
            </div>
          [/#if]
        </div>
      [/#if] 
    </div>
    
  </div>
[/#macro]


[#macro complianceCheck ]
<h4 class="headTitle">[@s.text name="project.deliverable.quality.compliance" /]</h4>
<div class="simpleBox" >
  <div class="row">
    <div class="col-md-10">
      <p class="note">[@s.text name = "project.deliverable.quality.info" /]</p>
    </div>
    <div class="col-md-2">
      <div id="box-out">
        <div id="box-inside">
          <div id="red" [#if action.goldDataValue(deliverableID)>14 && action.goldDataValue(deliverableID)<=74]class="highlightRed"[/#if]></div>
          <div id="yellow" [#if action.goldDataValue(deliverableID)>74 && action.goldDataValue(deliverableID)<=104]class="highlightYellow"[/#if]></div>
          <div id="green" [#if action.goldDataValue(deliverableID)>104]class="highlightGreen"[/#if]></div>
        </div>
      </div>
    </div>
  </div>

  <input type="hidden" name="deliverable.qualityCheck.id" value="${(deliverable.qualityCheck.id)!"-1"}">
  [#-- Question1 --]
  <div class="question ">
    <h5>[@s.text name="project.deliverable.quality.question1" /]</h5>
    <hr />
    
    <div class="col-md-4">
    [#list (answers)![] as answer]
      <div class="radio radio-block">
        [#if editable]
        <label><input type="radio" class="qualityAssurance" name="deliverable.qualityCheck.qualityAssurance.id" value="${(answer.id)!}" [#if deliverable.qualityCheck?? && deliverable.qualityCheck.qualityAssurance?? && deliverable.qualityCheck.qualityAssurance.id==answer.id] checked="checked"[/#if]>${(answer.name)!}</label>
        [#else]
        <p [#if (deliverable.qualityCheck.qualityAssurance.id==answer.id)!false] class="checked"[#else]class="noChecked"[/#if]>${(answer.name)!} </p>
        [/#if]
      </div>
    [/#list]
    </div>
    
    [#-- FILE --]
    <div class="col-md-8 fileOptions" style="display:[#if deliverable.qualityCheck?? && deliverable.qualityCheck.qualityAssurance?? && deliverable.qualityCheck.qualityAssurance.id==2] block [#else] none[/#if]">
      <div class="col-md-6 form-group  fileAssuranceContent">
        <label>[@customForm.text name="project.deliverable.proofSubmission" readText=!editable /]:</label>
        [#assign hasFile = deliverable.qualityCheck?? && deliverable.qualityCheck.fileAssurance?? && deliverable.qualityCheck.fileAssurance.id?? /]
        <input class="fileID" type="hidden" name="deliverable.qualityCheck.fileAssurance.id" value="${(deliverable.qualityCheck.fileAssurance.id)!"-1"}" />
        [#-- Input File --]
        [#if editable]
        <div class="fileUpload" style="display:${hasFile?string('none','block')}"> <input class="uploadFileAssurance upload" type="file" name="file" data-url="${baseUrl}/deliverableUploadFile.do"></div>
        [/#if]
        [#-- Uploaded File --]
        <p class="fileUploaded textMessage checked" style="display:${hasFile?string('block','none')}">
          <span class="contentResult" title="${(deliverable.qualityCheck.fileAssurance.fileName)!}">[#if deliverable.qualityCheck?? && deliverable.qualityCheck.fileAssurance?? && deliverable.qualityCheck.fileAssurance??][@utils.wordCutter string=((deliverable.qualityCheck.fileAssurance.fileName)!"") maxPos=20 substr=" "/][/#if]</span> 
          [#if editable]<span class="removeIcon"> </span> [/#if]
        </p>
      </div>
      <span class="col-md-1"> <br /> or</span>
      <div class="col-md-5">
      <br />
      [@customForm.input name="deliverable.qualityCheck.linkAssurance" value="${(deliverable.qualityCheck.linkAssurance)!}" className="urlLink" i18nkey="" showTitle=false placeholder="Please give us the link" required=true  editable=editable /]
      </div>
    </div>
    <div class="clearfix"></div>
  </div>
  [#-- Question2 --]
  <div class="question ">
    <h5>[@s.text name="project.deliverable.quality.question2" /]</h5>
    <hr />
    
    <div class="col-md-4">
    [#list (answers)![] as answer] 
      <div class="radio radio-block">
      [#if editable]
        <label><input type="radio" class="dataDictionary" name="deliverable.qualityCheck.dataDictionary.id" value="${(answer.id)!}" [#if deliverable.qualityCheck?? && deliverable.qualityCheck.dataDictionary?? && deliverable.qualityCheck.dataDictionary.id==answer.id] checked="checked"[/#if]>${(answer.name)!}</label>
      [#else]
        <p [#if deliverable.qualityCheck?? && deliverable.qualityCheck.dataDictionary?? && deliverable.qualityCheck.dataDictionary.id==answer.id] class="checked"[#else]class="noChecked"[/#if]>${(answer.name)!} </p>
      [/#if]
      </div>
    [/#list]
    </div>
    
    [#-- FILE --]
    <div class="col-md-8 fileOptions" style="display:[#if deliverable.qualityCheck?? && deliverable.qualityCheck.dataDictionary?? && deliverable.qualityCheck.dataDictionary.id==2] block[#else]none[/#if]">
      <div class="col-md-6 form-group fileDictionaryContent">
        <label>[@customForm.text name="project.deliverable.proofSubmission" readText=!editable /]:</label>
         [#assign hasFile = deliverable.qualityCheck?? && deliverable.qualityCheck.fileDictionary?? && deliverable.qualityCheck.fileDictionary.id?? /]
        <input class="fileID" type="hidden" name="deliverable.qualityCheck.fileDictionary.id" value="${(deliverable.qualityCheck.fileDictionary.id)!"-1"}" />
        [#-- Input File --]
        [#if editable]
        <div class="fileUpload" style="display:${hasFile?string('none','block')}"> <input class="uploadFileDictionary upload" type="file" name="file" data-url="${baseUrl}/deliverableUploadFile.do"></div>
        [/#if]
        [#-- Uploaded File --]
        <p class="fileUploaded textMessage checked" style="display:${hasFile?string('block','none')}">
          <span class="contentResult" title="${(deliverable.qualityCheck.fileDictionary.fileName)!}">[#if deliverable.qualityCheck?? && deliverable.qualityCheck.fileDictionary?? && deliverable.qualityCheck.fileDictionary??][@utils.wordCutter string=((deliverable.qualityCheck.fileDictionary.fileName)!"") maxPos=20 substr=" "/] [/#if]</span> 
          [#if editable]<span class="removeIcon"> </span> [/#if]
        </p>
      </div>
      <span class="col-md-1"> <br /> or</span>
      <div class="col-md-5">
      <br />
      [@customForm.input name="deliverable.qualityCheck.linkDictionary" value="${(deliverable.qualityCheck.linkDictionary)!}" className="urlLink" i18nkey="" showTitle=false placeholder="Please give us the link" required=true  editable=editable /]
      </div>
    </div>
    <div class="clearfix"></div>
  </div>
  [#-- Question3 --]
  <div class="question ">
    <h5>[@s.text name="project.deliverable.quality.question3" /]</h5>
    <hr />
    
    <div class="col-md-4">
    [#list (answers)![] as answer]
      <div class="radio radio-block">
      [#if editable]
        <label><input type="radio" class="dataTools" name="deliverable.qualityCheck.dataTools.id" value="${(answer.id)!}" [#if deliverable.qualityCheck?? && deliverable.qualityCheck.dataTools?? && deliverable.qualityCheck.dataTools.id==answer.id] checked="checked"[/#if]>${(answer.name)!}</label>
      [#else]
        <p [#if deliverable.qualityCheck?? && deliverable.qualityCheck.dataTools?? && deliverable.qualityCheck.dataTools.id==answer.id] class="checked"[#else]class="noChecked"[/#if]>${(answer.name)!} </p>
      [/#if]
      </div>
    [/#list]
    </div>
    
    [#-- FILE --]
    <div class="col-md-8 fileOptions" style="display:[#if deliverable.qualityCheck?? && deliverable.qualityCheck.dataTools?? && deliverable.qualityCheck.dataTools.id==2]block[#else]none[/#if]">
      <div class="col-md-6 form-group fileToolsContent">
        <label>[@customForm.text name="project.deliverable.proofSubmission" readText=!editable /]:</label>
        [#assign hasFile = deliverable.qualityCheck?? && deliverable.qualityCheck.fileTools?? && deliverable.qualityCheck.fileTools.id?? /]
        <input class="fileID" type="hidden" name="deliverable.qualityCheck.fileTools.id" value="${(deliverable.qualityCheck.fileTools.id)!"-1"}" />
        [#-- Input File --]
        [#if editable]
        <div class="fileUpload" style="display:${hasFile?string('none','block')}"> <input class="uploadFileTools upload" type="file" name="file" data-url="${baseUrl}/deliverableUploadFile.do"></div>
        [/#if]
        [#-- Uploaded File --]
        <p class="fileUploaded textMessage checked" style="display:${hasFile?string('block','none')}">
          <span class="contentResult" title="${(deliverable.qualityCheck.fileTools.fileName)!}">[#if deliverable.qualityCheck?? && deliverable.qualityCheck.fileTools?? && deliverable.qualityCheck.fileTools??][@utils.wordCutter string=((deliverable.qualityCheck.fileTools.fileName)!"") maxPos=20 substr=" "/][/#if]</span> 
          [#if editable]<span class="removeIcon"> </span> [/#if]
        </p>
      </div>
      <span class="col-md-1"> <br /> or</span>
      <div class="col-md-5">
      <br />
      [@customForm.input name="deliverable.qualityCheck.linkTools" value="${(deliverable.qualityCheck.linkTools)!}" className="urlLink" i18nkey="" showTitle=false placeholder="Please give us the link" required=true  editable=editable /]
      </div>
    </div>
    <div class="clearfix"></div>
  </div>
</div>
[/#macro]

[#macro fairCompliant]
<h4 class="headTitle">[@s.text name="project.deliverable.quality.fairTitle" /]</h4>
  
<div class="simpleBox" > 
  [#-- Findable --] 
  <div class="fairCompliant findable [#attempt][#if action.isF(deliverable.id)??][#if action.isF(deliverable.id)] achieved [#else] not-achieved [/#if][/#if][#recover][/#attempt]">
    <div class="row">
      <div class="col-md-2">
        <div class="sign">F</div>
      </div>
      <div class="col-md-10">
        <strong>[@s.text name="project.deliverable.quality.FLabel" /]</strong>
        <p>[@s.text name="project.deliverable.quality.Fpoint1" /]</p>
        <p>[@s.text name="project.deliverable.quality.Fpoint2" /]</p>
        <p>[@s.text name="project.deliverable.quality.Fpoint3" /]</p>
      </div>
    </div>
  </div>
  
  [#-- Accessible --] 
  <div class="fairCompliant accessible [#attempt][#if action.isA(deliverable.id)??][#if action.isA(deliverable.id)] achieved [#else] not-achieved [/#if][/#if][#recover][/#attempt]">
    <div class="row">
      <div class="col-md-2">
        <div class="sign">A</div>
      </div>
      <div class="col-md-10">
        <strong>[@s.text name="project.deliverable.quality.ALabel" /]</strong>
        <p>[@s.text name="project.deliverable.quality.Apoint1" /]</p>
        <p>[@s.text name="project.deliverable.quality.Apoint2" /]</p>
        <p>[@s.text name="project.deliverable.quality.Apoint3" /]</p>
      </div>
    </div>
  </div>
  
  [#-- Interoperable --] 
  <div class="fairCompliant interoperable [#attempt][#if action.isI(deliverable.id)??][#if action.isI(deliverable.id)] achieved [#else] not-achieved [/#if][/#if][#recover][/#attempt]">
    <div class="row">
      <div class="col-md-2">
        <div class="sign">I</div>
      </div>
      <div class="col-md-10">
        <strong>[@s.text name="project.deliverable.quality.ILabel" /]</strong>
        <p>[@s.text name="project.deliverable.quality.Ipoint1" /]</p>
        <p>[@s.text name="project.deliverable.quality.Ipoint2" /]</p>
        <p>[@s.text name="project.deliverable.quality.Ipoint3" /]</p>
      </div>
    </div>
  </div>
  
  [#-- Reusable --] 
  <div class="fairCompliant reusable [#attempt][#if action.isR(deliverable.id)??][#if action.isR(deliverable.id)] achieved [#else] not-achieved [/#if][/#if][#recover][/#attempt]">
    <div class="row">
      <div class="col-md-2">
        <div class="sign">R</div>
      </div>
      <div class="col-md-10">
        <strong>[@s.text name="project.deliverable.quality.RLabel" /]</strong>
        <p>[@s.text name="project.deliverable.quality.Ipoint1" /]</p>
        <p>[@s.text name="project.deliverable.quality.Ipoint2" /]</p>
      </div>
    </div>
  </div>
</div>
[/#macro]

[#-- Metadata Macro --]
[#macro metadataField title="" encodedName="" type="input" list="" require=false cssLabelName=""]
  [#local metadataID = (deliverable.getMetadataID(encodedName))!-1 /]
  [#local metadataIndex = (deliverable.getMetadataIndex(encodedName))!-1 /]
  [#local mElementID = (deliverable.getMElementID(metadataID))!'' /]
  [#local metadataValue = (deliverable.getMetadataValue(metadataID))!'' /]
  [#local mElementHide = isMetadataHide(encodedName) /]
  
  [#local customName = 'deliverable.metadataElements[${metadataIndex}]' /]

  <div class="metadataElement metadataElement-${title}">
    <input type="hidden" name="${customName}.id" value="${mElementID}" />
    <input type="hidden" class="hide" name="${customName}.hide" value="${mElementHide?string}" />
    <input type="hidden" name="${customName}.metadataElement.id" value="${metadataID}" />
    [#if type == "input"]
      [@customForm.input name="${customName}.elementValue" required=require value="${metadataValue}" className="metadataValue "  type="text" i18nkey="metadata.${title}" help="metadata.${title}.help" readOnly=mElementHide editable=editable/]
    [#elseif type == "textArea"]
      [@customForm.textArea name="${customName}.elementValue" required=require value="${metadataValue}" className="metadataValue " i18nkey="metadata.${title}" help="metadata.${title}.help" readOnly=mElementHide editable=editable/]
    [#elseif type == "select"]
      [@customForm.select name="${customName}.elementValue" required=require value="${metadataValue}" className="metadataValue " i18nkey="metadata.${title}" listName=list disabled=mElementHide editable=editable /]
    [#elseif type == "hidden"]
      <input type="hidden" name="${customName}.elementValue" value="${metadataValue}" class="metadataValue "/>
    [#elseif type == "text"]
      <input type="hidden" name="${customName}.elementValue" value="${metadataValue}" class="metadataValue "/>
      <p class="${cssLabelName}" style="display:${(metadataValue?has_content)?string('block', 'none')}"> Recorded in the public repository: <span class="metadataText">${metadataValue}</span> </p>
    [/#if]
  </div>
[/#macro]

[#function isMetadataHide encodedName]
  [#local metadataID = (deliverable.getMetadataID(encodedName))!-1 /]
  [#local mElement = (deliverable.getMetadata(metadataID))!{} /]
  [#return (mElement.hide)!false]
[/#function]

[#function getMetadataValueByCode encodedName]
  [#local metadataID = (deliverable.getMetadataID(encodedName))!-1 /]
  [#local metadataValue = (deliverable.getMetadataValue(metadataID))!'' /]
  [#return (metadataValue)!false]
[/#function]

[#macro authorMacro element index name  isTemplate=false]
  [#local customName = "${name}[${index}]" /]
  [#local displayVisible = "display:${isMetadataHide('marlo.authors')?string('none','block')};" /]
  <div id="author-${isTemplate?string('template',(element.id)!)}" class="author simpleBox col-md-4 ${isMetadataHide("marlo.authors")?string('hideAuthor','')}"  style="display:${isTemplate?string('none','block')}">
    [#if editable]
      <div class="removeLink authorVisibles" style="${displayVisible}"><div class="removeAuthor removeIcon" title="Remove author/creator"></div></div>
    [/#if]
    [#local firstName = (element.firstName?replace(',',''))!'' ]
    [#local lastName = (element.lastName?replace(',',''))!'' ]
    [#local orcid = (element.elementId?replace('https://|http://','','r'))!'' ]
    [#-- Last name & First name --]
    <span class="lastName [#if editable]cursor-pointer[/#if]">${lastName}</span>, <span class="firstName [#if editable]cursor-pointer[/#if]">${firstName} </span><br />
    [#-- ORCID --]
    <span><small class="orcidId [#if editable]cursor-pointer[/#if]"><strong>[#if orcid?has_content]${orcid}[#else]<i class="authorVisibles" style="${displayVisible}">No ORCID</i>[/#if]</strong></small></span>
    [#-- Hidden inputs --]
    <input type="hidden"  name="${customName}.id"         class="id"              value="${(element.id)!}" />
    <input type="hidden"  name="${customName}.lastName"   class="lastNameInput"   value="${(element.lastName?replace(',',''))!}" />
    <input type="hidden"  name="${customName}.firstName"  class="firstNameInput"  value="${(element.firstName?replace(',',''))!}" />
    <input type="hidden"  name="${customName}.elementId"  class="orcidIdInput"    value="${(element.elementId)!}" />
    <div class="clearfix"></div>
  </div>
[/#macro]

[#macro flagshipMacro element index name  isTemplate=false]
  [#assign customName = "${name}[${index}]" /]
  <div id="flagship-${isTemplate?string('template',(projectActivity.id)!)}" class="flagships"  style="display:${isTemplate?string('none','block')}">
    [#if editable]<div class="removeFlagship removeIcon" title="Remove flagship"></div>[/#if]
    [#-- Hidden Inputs --]
    <input class="idElemento" type="hidden" name="${customName}.id" value="${(element.id)!-1}" />
    <input class="idGlobalUnit" type="hidden" name="${customName}.globalUnit.id" value="${(element.globalUnit.id)!}" />
    <input class="idCRPProgram" type="hidden" name="${customName}.crpProgram.id" value="${(element.crpProgram.id)!}" />
    [#-- Title --]
    <span class="name">
      [#if (element.globalUnit.id??)!false]${(element.globalUnit.composedName)!}[/#if]
      [#if (element.crpProgram.id??)!false]${(element.crpProgram.composedName)!}[/#if]
    </span>
    <div class="clearfix"></div>
  </div>
[/#macro]

[#macro deliverablePartnerMacro element name index=-1 defaultType=2 isTemplate=false]
  [#local typeID = (element.deliverablePartnerType.id)!defaultType ]
  [#local isResponsable = (typeID == 1) ]
  [#local customName = "${name}[${index}]" ]
  [#if isResponsable][#local customName = "${name}[0]" ][/#if]
  
  <div id="deliverablePartnerItem-${isTemplate?string('template', (element.id)! )}" class="simpleBox deliverablePartnerItem" style="display:${isTemplate?string('none', 'block')}">
    [#-- Remove --]
    [#if editable && !isResponsable]<div class="removePartnerItem removeElement removeLink sm" title="[@s.text name="project.deliverable.removePartnerContribution" /]"></div> [/#if]
    [#-- Deliverable Partner ID --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}"/>
    [#-- Type --] 
    <input type="hidden" name="${customName}.deliverablePartnerType.id" class="partnerTypeID" value="${typeID}"/>
    [#-- Partner Institution --]
    <div class="form-group"> 
      [@customForm.select name="${customName}.institution.id" value="${(element.institution.id)!'-1'}"  i18nkey="" showTitle=false listName="partners" keyFieldName="institution.id"  displayFieldName="composedName" className="partnerInstitutionID" editable=editable required=true /]
    </div>
    [#-- Users Selected--]
    [#local selectedUsersID = []]
    [#if (element.partnershipPersons?has_content)!false][#local selectedUsersID = (action.getPersonsIds(element))![]][/#if]
    [#-- List of users--]
    <div class="row form-group usersBlock"> 
      [#list (action.getUserList(element.institution.id))![] as user]
        [#local isUserChecked =  selectedUsersID?seq_contains(user.id) ]
        [@deliverableUserMacro element=element user=user index=user_index name="${customName}.partnershipPersons" isUserChecked=isUserChecked isResponsable=isResponsable /]
      [/#list]
    </div>
    
  </div>
[/#macro]

[#macro deliverableUserMacro element user index name isUserChecked=false isResponsable=false]
  [#local customName = "${name}[${index}]"]
  [#local customID = "${index}-${isResponsable?string('1', '2')}-${user.id}"]
  [#if isResponsable][#local customName = "${name}[0]" ][/#if]
  [#-- Get Deliverable User --]
  [#local deliverableUser = {} ]
  [#list (element.partnershipPersons)![] as dpp]
    [#if (dpp.user.id == user.id)!false][#local deliverableUser = dpp ][#break][/#if]
  [/#list]
  
  <div class="deliverableUserItem col-md-6">
    [#-- Deliverable User ID --]
    [#if (!isResponsable) || (deliverableUser.id??)!false]
      <input type="hidden" name="${customName}.id" value="${(deliverableUser.id)!}" />
    [/#if]
    [#-- User ID Radio/Check --]
    [#if isResponsable]
      [@customForm.radioFlat id="${customID}" name="${customName}.user.id" label="${user.composedCompleteName}" disabled=false editable=editable value="${user.id}" checked=isUserChecked cssClass="" cssClassLabel="radio-label-yes" inline=false /]
    [#else]
      [@customForm.checkBoxFlat id="${customID}" name="${customName}.user.id" label="${user.composedCompleteName}" help="" paramText="" helpIcon=true disabled=false editable=editable value="${user.id}" checked=isUserChecked cssClass="" cssClassLabel="" /]
    [/#if]
  </div>
[/#macro]

[#function displayDeliverableRule element ruleName]
  [#if (action.hasDeliverableRule(element.deliverableInfo, ruleName))!false ]
    [#return "block"]
  [#else]
    [#return "none"]
  [/#if]
  [#return "none"]
[/#function]

[#function findElementID list id]
  [#list (list)![] as item]
    [#if (item.repIndGeographicScope.id == id)!false][#return true][/#if]
  [/#list]
  [#return false]
[/#function]
