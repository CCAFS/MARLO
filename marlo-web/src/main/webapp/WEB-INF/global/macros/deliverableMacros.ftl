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
      <span>${(deliverable.genderLevels[0].nameGenderLevel)!'Not provided'}</span> - <i><span>${(deliverable.genderLevels[0].descriptionGenderLevel)!}</span></i>
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
          [#local markerElement = (action.getDeliverableCrossCuttingMarker(marker.id))!{} ]          
          [#if action.isAiccra() && marker.id < 3]<div class="col-md-4">
          <input type="hidden"  name="${customName}.id" value="${(markerElement.id)!}"/>
          <input type="hidden"  name="${customName}.cgiarCrossCuttingMarker.id" value="${marker.id}"/>
          [@customForm.select   name="${customName}.repIndGenderYouthFocusLevel.id" value="${(markerElement.repIndGenderYouthFocusLevel.id)!-1}" valueName="${(markerElement.repIndGenderYouthFocusLevel.powbName)!}" className="setSelect2" i18nkey="${marker.name}" listName="focusLevels" keyFieldName="id"  displayFieldName="powbName"  required=true editable=editable/]
          </div>
          [#elseif !(action.isAiccra())]
          <div class="col-md-3">
          <input type="hidden"  name="${customName}.id" value="${(markerElement.id)!}"/>
          <input type="hidden"  name="${customName}.cgiarCrossCuttingMarker.id" value="${marker.id}"/>
          [@customForm.select   name="${customName}.repIndGenderYouthFocusLevel.id" value="${(markerElement.repIndGenderYouthFocusLevel.id)!-1}" valueName="${(markerElement.repIndGenderYouthFocusLevel.powbName)!}" className="setSelect2" i18nkey="${marker.name}" listName="focusLevels" keyFieldName="id"  displayFieldName="powbName"  required=true editable=editable/]
          </div>
          [/#if]
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
    <span class="col-md-9">
      <label class=" yesNoLabel" for="">Is this deliverable Open Access? [@customForm.req required=reportingActive /]</label>
      <p><small>Please make sure the information from the repository you chose and the one on Web of Science match. </small></p>
      <div class="WOS_tag" style="display: none;">
        <p>According to Web of Science you should select: <span id="WOS_tag_IOA_yes" style="color: rgb(9, 211, 70); font-weight: 700;">Yes</span><span id="WOS_tag_IOA_no" style="color: rgb(207, 42, 42); font-weight: 700;">No</span></p>
      </div>
      
    </span>
    <div class="col-md-3">
      [@customForm.yesNoInputDeliverable name="${customName}.isOpenAccess"  editable=editable inverse=false cssClass="type-accessible inverted-true text-center" /]  
    </div>
    
    </div> 
    [#--  <div class="block-accessible" style="display: ${((!deliverable.dissemination.isOpenAccess)!false)?string("block","none")};">
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
     --]
  </div>
[/#macro]

[#-- Does this deliverable involve Participants and Trainees? --]
[#macro deliverableParticipantsMacro ]
[#local customName = "deliverable.deliverableParticipant" /]
[#local capacityEventType = ((deliverable.deliverableInfo?has_content) && (deliverable.deliverableInfo.deliverableType?has_content) && (deliverable.deliverableInfo.deliverableType.id?has_content) && deliverable.deliverableInfo.deliverableType.id == 145)!false]

[#local hasParticipants = (deliverable.deliverableParticipant.hasParticipants?string)!""/]
  
[#if capacityEventType]
  [#assign hasParticipants = "true"]
 [/#if]
  
<div class="simpleBox">
  <div class="form-group row yesNoInputDeliverable">
  <span class="col-md-9">
    <label class="yesNoLabel" for="">[@s.text name="deliverable.involveParticipants.title" /] [@customForm.req required=(reportingActive) /]</label>
    <p><small>[@s.text name="project.deliverable.dissemination.involveParticipantsSub" /] </small></p>
  </span>
    [#if capacityEventType]
        <div class="col-md-3">[@customForm.yesNoInputDeliverableParticipants name="${customName}.hasParticipants"  editable=editable inverse=false  cssClass="type-involveParticipants text-center" value = "true" /] </div>  
    [#else]
        <div class="col-md-3">[@customForm.yesNoInputDeliverableParticipants name="${customName}.hasParticipants"  editable=editable inverse=false  cssClass="type-involveParticipants text-center" neutral=true  /] </div>  
    [/#if]
  </div>

  <div class="block-involveParticipants" 
    [#if !capacityEventType]
      style="display:${((deliverable.deliverableParticipant.hasParticipants)!false)?string('block','none')}"    
    [/#if]
  >
    <hr />
    [#-- Title Event/Activity --]
    <div class="form-group text-area-container">
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
     <div class="note left">
      <a href="#modal-typeOfActivity" data-toggle="modal"> <span class="glyphicon glyphicon-info-sign"></span> [@s.text name="deliverable.typeOfActivity.help" /]</a>
     </div>
     <br />

    
    [#-- Modal --]
    <div class="modal fade" id="modal-typeOfActivity" tabindex="-1" role="dialog" aria-labelledby="modalTypeOfActivity">
      <div class="modal-dialog modal-lg" role="document">
       <div class="modal-content">
         <div class="modal-header">
             <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
             <h4 class="modal-title" id="myModalLabel">[@s.text name="Types of Activities" /]</h4>
          </div>
          <div class="modal-body">
             [@typeOfActivityTable name="deliverable.typeOfActivity" /]
          </div>
          <div class="modal-footer">
             <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
          </div>
         </div>
      </div>
    </div>
    
    [#-- Total number of Participants: --]
    <div class="form-group row">
      <div class="col-md-6">
        <div class="text-area-container">
         [@customForm.input name="${customName}.participants" i18nkey="involveParticipants.participants" help="involveParticipants.participants.help" placeholder="global.number" className="numericInput" required=editable editable=editable /]
        </div>
        <br>
        <div class="dottedBox">
          [@customForm.checkBoxFlat id="estimateParticipants" name="${customName}.estimateParticipants" label="involveParticipants.estimate" value="true" editable=editable checked=((deliverable.deliverableParticipant.estimateParticipants)!false) cssClass="" cssClassLabel="font-italic" /]
        </div>
      </div>
      <div class="col-md-6 femaleNumbers">
        <div class="text-area-container">
          [@customForm.input name="${customName}.females" i18nkey="involveParticipants.females" help="involveParticipants.females.help" placeholder="global.number" className="numericInput" required=true editable=editable disabled=(deliverable.deliverableParticipant.dontKnowFemale)!false /]
        </div>
        <br>
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
    
    [#-- African - Youth: --]
    <div class="form-group row">
      <div class="col-md-6">
        <div class="text-area-container">
          [@customForm.input name="${customName}.african" i18nkey="involveParticipants.african" help="involveParticipants.african.help" placeholder="global.number" className="numericInput" required=editable editable=editable /]
        </div>
        <br>
        <div class="dottedBox">
          [@customForm.checkBoxFlat id="estimateAfrican" name="${customName}.estimateAfrican" label="involveParticipants.estimate" value="true" editable=editable checked=((deliverable.deliverableParticipant.estimateAfrican)!false) cssClass="" cssClassLabel="font-italic" /]
        </div>
      </div>
      <div class="col-md-6 femaleNumbers">
        <div class="text-area-container">
          [@customForm.input name="${customName}.youth" i18nkey="involveParticipants.youth" help="involveParticipants.youth.help" placeholder="global.number" className="numericInput" required=true editable=editable /]
        </div>
        <br>
        <div class="dottedBox">
          [@customForm.checkBoxFlat id="estimateYouth" name="${customName}.estimateYouth" label="involveParticipants.estimate" value="true" editable=editable checked=((deliverable.deliverableParticipant.estimateYouth)!false) cssClass="" cssClassLabel="font-italic" /]
        </div>
      </div>
    </div>
    
    [#-- Focus --]
    <div class="form-group text-area-container">
      [@customForm.textArea name="${customName}.focus" i18nkey="involveParticipants.focus" className="" required=editable editable=editable /]
    </div>
    [#-- Likely Outcomes --]
    <div class="form-group text-area-container">
      [@customForm.textArea name="${customName}.likelyOutcomes" i18nkey="involveParticipants.likelyOutcomes" className="" required=editable editable=editable /]
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
      <div class="form-group confidentialBlock-true text-area-container" style="display:${(isConfidential == "true")?string('block', 'none')}">
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
          <p>${((deliverable.dissemination.disseminationChannel?upper_case)!)!'Not provided'}</p>
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
      <div class="url-field text-area-container">
        [@customForm.input name="${customName}.disseminationUrl" type="text" i18nkey="project.deliverable.dissemination.disseminationUrl"  placeholder="" className="deliverableDisseminationUrl" required=true readOnly=isSynced editable=editable /]
      </div>
      <div class="buttons-field">
        [#if editable]
        <div class="checkButton" id="WOSSyncBtn" style="text-align: center ;display:none;">WOS sync</div>
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

    <div class="form-group row ifIsReadOnly" style="margin-top: 10px;">
      <div class="col-md-6 conditionalRequire handle-bridge text-area-container">
        [@customForm.input name="handle-bridge" required=require value="" className="metadataValue handleField"  type="text" i18nkey="Handle" help="" readOnly=mElementHide editable=editable id="handleField"/]
      </div>
      <div class="col-md-6 conditionalRequire doi-bridge text-area-container" style="position: relative;">
        [@customForm.input name="doi-bridge" required=require value="" className="metadataValue doiField" type="text" i18nkey="DOI" help="nada2" readOnly=mElementHide editable=editable id="doiField"/]
        <p class="invalidDOI" style="position: absolute; bottom: 0 + 15px; color: rgb(207, 40, 40); font-weight: 600; font-size: 0.8em; display: none;">Invalid DOI identifier.<br>Please use the correct format <strong>(e.g. 10.1109/5.771073)</strong></p>
        <p class="validDOI" style="position: absolute; bottom: 0 + 15px; color: rgb(50, 206, 45); font-weight: 600; font-size: 0.8em; display: none;">Valid DOI identifier</p>
      </div>
    </div>
    <br>
    <hr>
    [#assign isOtherUrl = (deliverable.dissemination.hasDOI)!false /]
    <div class="form-group row " style="margin-top:5px; display:block">
      [#-- Alternative url Check --]
      <div class="col-md-12" style="display: none;">
        [@customForm.input name="deliverable.dissemination.hasDOI" type="text" i18nkey="aux checkbox" className="isOtherUrlFiel"  placeholder="" required=false editable=editable /]
       </div>
      <div class="col-md-6 isOtherUrl isOtherUrlTohide">
        <br />
        [@customForm.checkmark id="" name="" i18nkey="project.deliverable.hasDOI" help="" paramText="" value="true" helpIcon=true disabled=false editable=editable checked=(deliverable.dissemination.hasDOI)!false cssClass="isOtherUrl" cssClassLabel=""  /]
      </div>
      [#-- Alternative url TextField --]
      <div class="col-md-6 other-url" style="display:${(isOtherUrl)?string('block','none')}">
        [@customForm.input name="deliverable.dissemination.articleUrl" type="text" i18nkey="project.deliverable.articleURL"  placeholder="" required=true editable=editable /]
      </div>
   </div>

<div class="loading-WOS-container" style="position: relative; ">
  <p style="position: absolute; top: 10px; font-weight: 500; color: rgb(16, 122, 192); font-size: 1.3em;">Synchronizing  with WOS</p>
  <svg class="loading-WOS" width="110px"  height="110px"  xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 100 100" preserveAspectRatio="xMidYMid" style="background: none;"><g transform="translate(20 50)">
    <circle cx="0" cy="0" r="5" fill="#93dbe9" transform="scale(0.629707 0.629707)">
      <animateTransform attributeName="transform" type="scale" begin="-0.3375s" calcMode="spline" keySplines="0.3 0 0.7 1;0.3 0 0.7 1" values="0;1;0" keyTimes="0;0.5;1" dur="1.2s" repeatCount="indefinite"></animateTransform>
    </circle>
    </g><g transform="translate(40 50)">
    <circle cx="0" cy="0" r="5" fill="#689cc5" transform="scale(0.921425 0.921425)">
      <animateTransform attributeName="transform" type="scale" begin="-0.225s" calcMode="spline" keySplines="0.3 0 0.7 1;0.3 0 0.7 1" values="0;1;0" keyTimes="0;0.5;1" dur="1.2s" repeatCount="indefinite"></animateTransform>
    </circle>
    </g><g transform="translate(60 50)">
    <circle cx="0" cy="0" r="5" fill="#5e6fa3" transform="scale(0.974999 0.974999)">
      <animateTransform attributeName="transform" type="scale" begin="-0.1125s" calcMode="spline" keySplines="0.3 0 0.7 1;0.3 0 0.7 1" values="0;1;0" keyTimes="0;0.5;1" dur="1.2s" repeatCount="indefinite"></animateTransform>
    </circle>
    </g><g transform="translate(80 50)">
    <circle cx="0" cy="0" r="5" fill="#3b4368" transform="scale(0.725877 0.725877)">
      <animateTransform attributeName="transform" type="scale" begin="0s" calcMode="spline" keySplines="0.3 0 0.7 1;0.3 0 0.7 1" values="0;1;0" keyTimes="0;0.5;1" dur="1.2s" repeatCount="indefinite"></animateTransform>
    </circle>
    </g></svg>
    
</div>

    <div class="note left" id="WOSModalBtn" style="display: none;">
      <div  class="helpMessage4">
        <p><a style="cursor: pointer;" data-toggle="modal" data-target="#WOSModal" > <span class="glyphicon glyphicon-info-sign"></span> Click here to get the metadata information received from Web of Science
        </a></p>
      </div>
    </div>

  </div>


  <div id="metadata-output"></div>
  <div id="metadata-container">
    <div class="metadata-text-container">
      <div class="metadata-output-persistent" id="output-dissemination" style="display: none;"></div>
      <div class="metadata-output-persistent" id="output-wos" style="display: none;"></div>
    </div>
    [#-- Almetric Image --]
    <div data-toggle="tooltip" class="" title="[@s.text name="project.deliverable.dissemination.altmetricTooltip" /]">
      <a class="altmetricURL" target="_blank" href="">
        <img class="altmetricImg" src="" alt="" srcset="" style="display: none;">
      </a>
    </div>
   </div>

      <!-- Modal WOS -->
      <div class="modal fade" id="WOSModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog " style=" width:80%">
          <div class="modal-content">
            <div class="modal-header" style="position: relative;">
              <h5 class="modal-title" id="exampleModalLabel" style="font-weight: 600; font-size: 1.5em; text-align: center;">WOS metadata information</h5>
              <p style="font-style: italic; color: #868686; font-size: .9em; position: absolute; transform: translateX(-50%);left: 50%;">last updated: <span style="color: #9e9e9e ;" class="currentDate"></span></p>
              <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body">
              
              <table class="table">

                <tbody>
                  <tr>
                    <th scope="row">URL</th>
                    <td id="td-WOS-URL" ></td>
                  </tr>
                  <tr>
                    <th  scope="row">DOI</th>
                    <td id="td-WOS-DOI"></td>
                  </tr>
                  <tr>
                    <th scope="row">Title</th>
                    <td id="td-WOS-Title" ></td>
                  </tr>
                  <tr>
                    <th  scope="row">Publication type</th>
                    <td id="td-WOS-Publication_type"></td>
                  </tr>
                  <tr>
                    <th scope="row">Publication Year</th>
                    <td id="td-WOS-Publication_Year" ></td>
                  </tr>
                  <tr>
                    <th scope="row">Is Open Access</th>
                    <td id="td-WOS-Is_Open_Access" ></td>
                  </tr>
                  <tr>
                    <th scope="row">Open access link</th>
                    <td  id="td-WOS-Open_access_link" ></td>
                  </tr>
                  <tr>
                    <th  scope="row">Is ISI</th>
                    <td id="td-WOS-Is_ISI"></td>
                  </tr>
                  <tr>
                    <th  scope="row">Journal name</th>
                    <td id="td-WOS-Journal_name"></td>
                  </tr>
                  <tr>
                    <th scope="row">Volume</th>
                    <td id="td-WOS-Volume" ></td>
                  </tr>
                  <tr>
                    <th  scope="row">Pages</th>
                    <td id="td-WOS-Pages"></td>
                  </tr>
                  <tr>
                    <th  scope="row">Authors</th>
                    <td id="td-WOS-Authors"></td>
                  </tr>
                  <tr>
                    <th  scope="row">Institutions</th>
                    <td id="td-WOS-Institutions"></td>
                  </tr>
                </tbody>
              </table>
                   <input type="hidden"  id="acceptationPercentageValue" name="acceptationPercentage" value="${(acceptationPercentage)!}">

            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
            </div>
          </div>
        </div>
      </div>

     [@duplicatedDeliverablesMacro /]
    
[/#macro]

[#macro duplicatedDeliverablesMacro ]
   [#if action.hasSpecificities('duplicated_deliverables_functionality_active')]
    <div id="vueApp" class="fieldFocus-deliverable resultList simpleBox form-group" v-if="allDeliverables.length" style="display:none">
      <label for=""><strong>[@s.text name="project.deliverable.duplicated.table.title" /]:</strong></label>    
      [@s.text name="project.deliverable.duplicated.table.title2" /]
      [#--  <button type="button" v-bind:class="{ active: showTable }" v-on:click="showTable = !showTable">{{ showTable ? 'Less Details' : 'See Details' }}</button>
      --]
      
      <button class="button-effect" type="button" v-bind:class="{ active: showTable }" v-on:click="showTable = !showTable" >
        <span v-if="!showTable" class="glyphicon glyphicon-warning-sign"></span>
        {{ showTable ? 'Hide Details' : 'See Details' }}
      </button>
      
      <transition name="slide-transition">
       <div class="table-container" v-show="showTable">
        <table style="border-collapse: collapse;" class="table" >
          <thead>
            <tr>
              <th>Deliverable ID</th>
              <th>Cluster</th>
              <th>Title</th>
              <th>Sub-Category</th>
              <th>Responsible</th>
              <th>Cluster Leader</th>
              <th>Shared Clusters</th>
            </tr>
          </thead>
          <tbody>
            <template v-for="item in crpList">
              <template v-for="fs in item.deliverables">
                <tr>
                  <td>
                    <a target="_blank" v-bind:href="'${baseUrl}/clusters/${crpSession}/deliverable.do?deliverableID='+ fs.deliverableID +'&edit=true&phaseID=${(actualPhase.id)!}'">
                      <strong>D{{ item.deliverableID }}</strong>
                    </a>
                    <span class="label label-warning">Duplicated: {{ fs.duplicatedField }}</span>
                  </td>
                  <td>{{ fs.clusterAcronym }}</td>
                  <td>{{ fs.title }}</td>
                  <td>{{ fs.subCategory }}</td>
                  <td>{{ fs.responsible }}</td>
                  <td>{{ fs.clusterLeader }}</td>
                  <td>{{ fs.sharedClusters }}</td>
                </tr>
              </template>
            </template>
          </tbody>
        </table>
        </div>
        </transition>
      </div>
    [/#if]
[/#macro]


[#macro channelExampleMacro name="" url="" ]
  <div class="exampleUrl-block channel-${name}" style="display:[#if (deliverable.dissemination.disseminationChannel==name)!false]block[#else]none[/#if];">
    <label for="">[@s.text name="project.deliverable.dissemination.exampleUrl" /]:</label>
    <p><small>${url}</small></p>
  </div>
[/#macro]

[#macro deliverableMetadataMacro flagshipslistName="programs" crpsListName="crps" allowFlagships=true]
  <div class="form-group">
    [@metadataField title="title" encodedName="dc.title" type="input" require=true/]
  </div>
  <div class="form-group">
    [@metadataField title="description" encodedName="dc.description.abstract" type="textArea" require=false/]
  </div>
  <div class="form-group row">
    <div class="col-md-6">
      [@metadataField title="publicationDate" encodedName="dc.date" type="input" require=true/]
    </div>
    [#if !action.isAiccra()]
    <div class="col-md-6">
      [@metadataField title="language" encodedName="dc.language" type="input" require=false/]
    </div>
    [/#if]
  </div>
  [#if !action.isAiccra()]
  <div class="form-group row">
    <div class="col-md-6">
      [@metadataField title="countries" encodedName="cg:coverage.country" type="input" require=false/]
    </div>
    <div class="col-md-6">
      [@metadataField title="keywords" encodedName="marlo.keywords" type="input" require=false/]
    </div>
  </div>
  [/#if]
  <div class="form-group"> 
    [@metadataField title="citation" encodedName="dc.identifier.citation" type="textArea" require=false/]
  </div>
  <div class="form-group row ifIsReadOnly" style="display: none;">
    <div class="col-md-6">
      [@metadataField title="handle" encodedName="marlo.handle" type="input" require=false/]
    </div>

    <div class="col-md-6 conditionalRequire">
       [#assign isOtherUrl = (deliverable.dissemination.hasDOI)!false /]
       [@metadataField title="doi" encodedName="marlo.doi" type="input" require=!(isOtherUrl)/]
    </div>
  </div>
  

   
  <hr />
   
  [#-- Creator/Authors --]
  <div class="form-group">
  
    [#if !(deliverable.users?has_content) && deliverable.getMetadataValue(38)?has_content]
      [@metadataField title="creator" encodedName="marlo.authors" type="textArea" require=false/]
    [#else]
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
    
    [#-- Is ISI Journal--]
    <div class="form-group isIsiJournal">
      <label for="">[@s.text name="deliverable.isiPublication" /] [@customForm.req required=editable /]
      <p><i class="helpLabel">Please make sure the information from the repository you chose and the one on Web of Science match.</i></p></label> <br />
      [#local isISI = (deliverable.publication.isiPublication?string)!"" /]
      [@customForm.radioFlat id="optionISI-yes"  name="deliverable.publication.isiPublication" i18nkey="Yes"  value="true"  checked=(isISI == "true")  cssClass="radioType-optionISI" cssClassLabel="font-normal radio-label-yes" editable=editable /] 
      [@customForm.radioFlat id="optionISI-no"   name="deliverable.publication.isiPublication" i18nkey="No"   value="false" checked=(isISI == "false") cssClass="radioType-optionISI" cssClassLabel="font-normal radio-label-no"  editable=editable /] 
      <div class="WOS_tag" style="display: none; margin: 5px 0px" >
        <p>According to Web of Science you should select: <span id="WOS_tag_ISI_yes" style="color: rgb(9, 211, 70); font-weight: 700;">Yes</span><span id="WOS_tag_ISI__no" style="color: rgb(207, 42, 42); font-weight: 700;">No</span></p>
      </div>
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
    <div class="row yesNoInputDeliverable [#if crpSession=="CCAFS"]acknowledgeCCAFS[/#if]">
      <div class="col-md-9">
        <label class="yesNoLabel">[@s.text name="project.deliverable.dissemination.acknowledgeQuestion" ][@s.param]${(crpSession?upper_case)!}[/@s.param][/@s.text][@customForm.req required=false /]</label>
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
    [#list (answersDataDic)![] as answer] 
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

  <div class="metadataElement metadataElement-${title} text-area-container">
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

[#macro typeOfActivityTable name=""]
  <table id="tableA" class="table table-bordered">
    <thead>
      <tr>
        <th class="text-center">[@s.text name="${name}.title" /]</th>
        <th class="text-center">[@s.text name="${name}.description" /]</th>  
      </tr>
    </thead>
    <tbody>
      <tr>
        [#-- Title --]
        <td class="">
         [@s.text name="${name}.cocreation" /]
        </td>
        [#-- Description --]
        <td class="">
         [@s.text name="${name}.cocreation.description" /]
        </td>
       </tr>
       <tr>
        [#-- Title --]
        <td class="">
         [@s.text name="${name}.academic" /]
        </td>
        [#-- Description --]
        <td class="">
         [@s.text name="${name}.academic.description" /]
        </td>
       </tr>
       <tr>
        [#-- Title --]
        <td class="">
         [@s.text name="${name}.longTerm" /]
        </td>
        [#-- Description --]
        <td class="">
         [@s.text name="${name}.longTerm.description" /]
        </td>
       </tr>
       <tr>
        [#-- Title --]
        <td class="">
         [@s.text name="${name}.oneOff" /]
        </td>
        [#-- Description --]
        <td class="">
         [@s.text name="${name}.oneOff.description" /]
        </td>
       </tr>
       <tr>
        [#-- Title --]
        <td class="">
         [@s.text name="${name}.research" /]
        </td>
        [#-- Description --]
        <td class="">
         [@s.text name="${name}.research.description" /]
        </td>
       </tr>
       <tr>
        [#-- Title --]
        <td class="">
         [@s.text name="${name}.knowledge" /]
        </td>
        [#-- Description --]
        <td class="">
         [@s.text name="${name}.knowledge.description" /]
        </td>
       </tr>
       <tr>
        [#-- Title --]
        <td class="">
         [@s.text name="${name}.scaling" /]
        </td>
        [#-- Description --]
        <td class="">
         [@s.text name="${name}.scaling.description" /]
        </td>
       </tr>
       <tr>
        [#-- Title --]
        <td class="">
         [@s.text name="${name}.trials" /]
        </td>
        [#-- Description --]
        <td class="">
         [@s.text name="${name}.trials.description" /]
        </td>
       </tr>
       <tr>
        [#-- Title --]
        <td class="">
         [@s.text name="${name}.other" /]
        </td>
        [#-- Description --]
        <td class="">
         [@s.text name="${name}.other.description" /]
        </td>
       </tr>
    </tbody>
  </table>

[/#macro]
