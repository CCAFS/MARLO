
[#ftl]
<div class="simpleBox">
  [#-- Title input --] 
  <div class="form-group">
    [@customForm.input name="deliverable.deliverableInfo.title" value="${(deliverable.deliverableInfo.title)!}" type="text" i18nkey="project.deliverable.generalInformation.title"  placeholder="" className="limitWords-25" required=true editable=editable /]
  </div>
  
  [#-- Description input on Planning only --] 
  <div class="form-group">
    [@customForm.textArea name="deliverable.deliverableInfo.description" value="${(deliverable.deliverableInfo.description)!}" i18nkey="project.deliverable.generalInformation.description"  placeholder="" className="limitWords-50" required=true editable=editable /]
  </div> 
  [#-- Type and subtype inputs --] 
  <div class="form-group row">
    <div class="col-md-6 ">
      [@customForm.select name="deliverable.deliverableInfo.deliverableType.deliverableType.id" label=""  i18nkey="project.deliverable.generalInformation.type" listName="deliverableTypeParent" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className=" form-control input-sm typeSelect" editable=editable/]
    </div>
    <div class="col-md-6 subType-select">
      [@customForm.select name="deliverable.deliverableInfo.deliverableType.id" label=""  i18nkey="project.deliverable.generalInformation.subType" listName="deliverableSubTypes" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className=" form-control input-sm subTypeSelect" editable=editable/]
    </div>
  </div>
  [#-- Deliverable table with categories and sub categories --]
  <div class="form-group deliverableTypeMessage">
    <div id="dialog" title="Deliverable types" style="display: none">
      <table id="deliverableTypes" style="height:700px; width:950px;">
        <th> [@s.text name="project.deliverables.dialogMessage.part1" /] </th>
        <th> [@s.text name="project.deliverables.dialogMessage.part2" /] </th>
        <th> [@s.text name="project.deliverables.dialogMessage.part3" /] </th>
        [#if deliverableTypeParent?has_content]
        [#list deliverableTypeParent as mt]
          [#list action.getDeliverablesSubTypes(mt.id) as st]
            [#if st_index == 0]
            <tr>
              <th rowspan="${action.getDeliverablesSubTypes(mt.id).size()}" class="text-center"> ${mt.name} </th>
              <td> ${st.name} </td>
              <td> ${(st.description)!}</td>
            </tr>
            [#else]
            <tr>
              <td> ${st.name} </td>
              <td> ${(st.description)!} </td>
            </tr>
            [/#if]
          [/#list]
        [/#list]
        [/#if]  
      </table>
    </div> <!-- End dialog-->
      
     
    <div class="note left">
      <div id="popup" class="helpMessage3">
        <p><a id="opener"> <span class="glyphicon glyphicon-info-sign"></span> [@s.text name="project.deliverable.generalInformation.deliverableType" /]</a></p>
      </div>
    </div>
    
     
    <div class="clearfix"></div>
  </div>
  
  
  <div class="clearfix"></div>
  [#-- Status and year expected selects --]
  <div class="${reportingActive?string('fieldFocus','')}">
  <div class="form-group row">
    <div class="col-md-4">
      [@customForm.select name="deliverable.deliverableInfo.status" label=""   i18nkey="project.deliverable.generalInformation.status" listName="status"  multiple=false required=true header=false className=" status" editable=editable || editStatus/]
    </div>
    <div class="col-md-4 form-group">
      [#-- If is editable, deliverable is old, there is a saved year and the year is < to the current cycle year --]
      [#assign canNotEditYear =!action.candEditYear(deliverable.id)/]
      
      [#if editable ]
      
      [#if canNotEditYear]
      [@customForm.select name="deliverable.deliverableInfo.year" label=""  i18nkey="project.deliverable.generalInformation.year" listName="project.projectInfo.AllYears" header=false  multiple=false required=true  className="yearExpected" disabled=canNotEditYear /]
      
      [#else]
      [@customForm.select name="deliverable.deliverableInfo.year" label=""  i18nkey="project.deliverable.generalInformation.year" listName="project.projectInfo.AllYearsPhase" header=false  multiple=false required=true  className="yearExpected" disabled=canNotEditYear /]
      
      [/#if]
        
      [#else]
         <div class="select">
          <label for="">[@s.text name="project.deliverable.generalInformation.year" /]:</label>
          <p>${(deliverable.deliverableInfo.year)!}</p>
          [#if canNotEditYear] <input type="hidden" name="deliverable.deliverableInfo.year" value="${(deliverable.deliverableInfo.year)!}"/>  [/#if]
        </div>
      [/#if]
    </div>
    [#-- New Expected Year - Extended = 4 --]
    [#assign canViewNewExpectedYear =action.candEditExpectedYear(deliverable.id) /]
    
    <div id="newExpectedYear" class="col-md-4" style="display:${canViewNewExpectedYear?string('block','none')}">
      [#if editable || editStatus]
        [#if reportingActive]
          [#assign startExpectedYear = currentCycleYear-1]
        [#else]
          [#assign startExpectedYear = (deliverable.deliverableInfo.year)!currentCycleYear ]
        [/#if]
        [@customForm.select name="deliverable.deliverableInfo.newExpectedYear"  i18nkey="deliverable.newExpectedYear"  listName="project.projectInfo.getYears(${startExpectedYear})" header=true  multiple=false required=true  className="yearNewExpected" editable=editable || editStatus/]
      [#else]
        <div class="select">
          <label for="">[@s.text name="deliverable.newExpectedYear" /]:</label>
          <p>${(deliverable.deliverableInfo.newExpectedYear)!}</p>
          <input type="hidden" name="deliverable.deliverableInfo.newExpectedYear" value="${(deliverable.deliverableInfo.newExpectedYear)!}" />
        </div>
      [/#if]
    </div> 
    <div class="clearfix"></div>
  </div>
  
  [#-- Status justification textArea --]
  [#if !action.isDeliverableNew(deliverable.id)]
    [#assign justificationRequired = (deliverable.deliverableInfo.year??) && (deliverable.deliverableInfo.status??) &&  ((deliverable.deliverableInfo.status == 4)  || (deliverable.deliverableInfo.status == 5)) ]
    <div class="form-group">
      <div id="statusDescription" class="col-md-12" style="display:${justificationRequired?string('block','none')}">
        [@customForm.textArea name="deliverable.deliverableInfo.statusDescription" className="statusDescription limitWords-150" i18nkey="deliverable.statusJustification.status${(deliverable.deliverableInfo.status)!'NotSelected'}" editable=editable || editStatus/]
        <div id="statusesLabels" style="display:none">
          <div id="status-2">[@s.text name="deliverable.statusJustification.status2" /]:<span class="red">*</span></div>[#-- Ongoing("2", "On-going") --]
          <div id="status-3">[@s.text name="deliverable.statusJustification.status3" /]:<span class="red">*</span></div>[#-- Complete("3", "Complete") --]
          <div id="status-4">[@s.text name="deliverable.statusJustification.status4" /]:<span class="red">*</span></div>[#-- Extended("4", "Extended") --]
          <div id="status-5">[@s.text name="deliverable.statusJustification.status5" /]:<span class="red">*</span></div>[#-- Cancelled("5", "Cancelled") --]
        </div>
      </div>
    </div>
    <div class="clearfix"></div>
  [/#if]
  </div>
  
  [#-- Key Outputs select --]
  [#if !project.projectInfo.administrative && !phaseOne]
    <div class="form-group">

      [@customForm.select name="deliverable.deliverableInfo.crpClusterKeyOutput.id" label=""  i18nkey="project.deliverable.generalInformation.keyOutput" listName="keyOutputs" keyFieldName="id"  displayFieldName="composedName"  multiple=false required=true  className="keyOutput" editable=editable/]
    </div>
  [/#if]
  
  [#-- Funding Source --]
  [#if !phaseOne]
  <div class="panel tertiary">
   <div class="panel-head"><label for=""> [@customForm.text name="project.deliverable.fundingSource" readText=!editable /]:[@customForm.req required=editable /]</label></div>
    <div id="fundingSourceList" class="panel-body" listname="deliverable.fundingSources"> 
      <ul class="list">
      [#if deliverable.fundingSources?has_content]
        [#list deliverable.fundingSources as element]
          <li class="fundingSources clearfix">
            [#if editable]<div class="removeFundingSource removeIcon" title="Remove funding source"></div>[/#if] 
            <input class="id" type="hidden" name="deliverable.fundingSources[${element_index}].id" value="${(element.id)!}" />
            <input class="fId" type="hidden" name="deliverable.fundingSources[${element_index}].fundingSource.id" value="${(element.fundingSource.id)!}" />
            <span class="name">
              <strong>FS${(element.fundingSource.id)!} - ${(element.fundingSource.fundingSourceInfo.budgetType.name)!} [#if (element.fundingSource.fundingSourceInfo.w1w2)!false] (Co-Financing)[/#if]</strong> <br />
              <span class="description">${(element.fundingSource.fundingSourceInfo.title)!}</span><br />
            </span>
            <div class="clearfix"></div>
          </li>
        [/#list]
        <p style="display:none;" class="emptyText"> [@s.text name="project.deliverable.fundingSource.empty" /]</p>   
      [#else]
        <p class="emptyText"> [@s.text name="project.deliverable.fundingSource.empty" /]</p> 
      [/#if]
      </ul>
      [#if editable ]
        [@customForm.select name="deliverable.fundingSource.id" label=""  showTitle=false  i18nkey="" listName="fundingSources" keyFieldName="id"  displayFieldName="composedName"  header=true required=true  className="fundingSource" editable=editable/]
      [/#if] 
    </div>
  </div>
  
  [#-- Funding source List --]
  <div style="display:none">
    [#if fundingSources?has_content]
      [#list fundingSources as element]
        <span id="fundingSource-${(element.id)!}">
          <strong>FS${(element.id)!} - ${(element.fundingSourceInfo.budgetType.name)!} [#if (element.w1w2)!false] (Co-Financing) [/#if]</strong> <br />
          <span class="description">${(element.fundingSourceInfo.title)!}</span><br />
        </span>
      [/#list]
    [/#if]
  </div>
  
  [/#if]
</div>

<h3 class="headTitle">[@s.text name="deliverable.crossCuttingDimensionsTitle" /] </h3>
<div class="simpleBox">
  [#-- Does this deliverable have a cross-cutting dimension --]
  <div class="form-group">
    <label for="">[@customForm.text name="deliverable.crossCuttingDimensions" readText=!editable/] [@customForm.req required=editable/]</label>
    <div class="row">
      <div class="col-md-12">
        [#if editable]
          <label class="checkbox-inline"><input type="checkbox" name="deliverable.deliverableInfo.crossCuttingGender"   class="crosscutingDimension"  id="gender"   value="true" [#if (deliverable.deliverableInfo.crossCuttingGender)!false ]checked="checked"[/#if]> Gender</label>
          <label class="checkbox-inline"><input type="checkbox" name="deliverable.deliverableInfo.crossCuttingYouth"    class="crosscutingDimension"  id="youth"    value="true" [#if (deliverable.deliverableInfo.crossCuttingYouth)!false ]checked="checked"[/#if]> Youth</label>
          <label class="checkbox-inline"><input type="checkbox" name="deliverable.deliverableInfo.crossCuttingCapacity" class="crosscutingDimension"  id="capacity" value="true" [#if (deliverable.deliverableInfo.crossCuttingCapacity)!false ]checked="checked"[/#if]> Capacity Development</label>
          <label class="checkbox-inline"><input type="checkbox" name="deliverable.deliverableInfo.crossCuttingNa"       class=""                      id="na"       value="true" [#if (deliverable.deliverableInfo.crossCuttingNa)!false ]checked="checked"[/#if]> N/A</label>
        [#else]
          [#if (deliverable.deliverableInfo.crossCuttingGender)!false ] <p class="checked"> Gender</p> <input type="hidden" name="deliverable.deliverableInfo.crossCuttingGender" value="true">[/#if]
          [#if (deliverable.deliverableInfo.crossCuttingYouth)!false ] <p class="checked"> Youth</p><input type="hidden" name="deliverable.deliverableInfo.crossCuttingYouth" value="true">[/#if]
          [#if (deliverable.deliverableInfo.crossCuttingCapacity)!false ] <p class="checked"> Capacity Development</p><input type="hidden" name="deliverable.deliverableInfo.crossCuttingCapacity" value="true">[/#if]
          [#if (deliverable.deliverableInfo.crossCuttingNa)!false ] <p class="checked"> N/A</p><input type="hidden" name="deliverable.deliverableInfo.crossCuttingNa" value="true">[/#if]
          
          [#-- Message when there's nothing to show -> "Prefilled if avaible" --]
          [#if (!deliverable.deliverableInfo.crossCuttingGender?has_content) && (!deliverable.deliverableInfo.crossCuttingYouth?has_content) && (!deliverable.deliverableInfo.crossCuttingCapacity?has_content) && (!deliverable.deliverableInfo.crossCuttingNa?has_content)]<p>[@s.text name="form.values.fieldEmpty" /]</p>[/#if]
        [/#if]
      </div>
    </div>
  </div>
  
  [#-- If gender dimension, select with ones --]
  <div id="gender-levels" class="panel tertiary" style="display:${((deliverable.deliverableInfo.crossCuttingGender)!false)?string('block','none')}">
  [#if !action.hasSpecificities('crp_one_gender')]
    [#if deliverable.genderLevels?has_content]
      <div class="panel-head"><label for=""> [@customForm.text name="deliverable.genderLevels" readText=!editable /]:[@customForm.req required=editable /]</label></div>
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
  <div id="ccDimension-gender"    class="form-group ccDimension" style="display:${((deliverable.deliverableInfo.crossCuttingGender)!false)?string('block','none')}">
    [@customForm.select name="deliverable.deliverableInfo.crossCuttingScoreGender" label="" i18nkey="deliverable.ccDimension.gender" listName="crossCuttingScoresMap" required=true header=false className="crossCuttingDimensionsSelect" editable=editable/]
  </div>
  <div id="ccDimension-youth"     class="form-group ccDimension" style="display:${((deliverable.deliverableInfo.crossCuttingYouth)!false)?string('block','none')}">
    [@customForm.select name="deliverable.deliverableInfo.crossCuttingScoreYouth" label="" i18nkey="deliverable.ccDimension.youth" listName="crossCuttingScoresMap"  required=true header=false className="crossCuttingDimensionsSelect" editable=editable/]
  </div>
  <div id="ccDimension-capacity"  class="form-group ccDimension" style="display:${((deliverable.deliverableInfo.crossCuttingCapacity)!false)?string('block','none')}">
    [@customForm.select name="deliverable.deliverableInfo.crossCuttingScoreCapacity" label="" i18nkey="deliverable.ccDimension.capacity" listName="crossCuttingScoresMap" required=true header=false className="crossCuttingDimensionsSelect" editable=editable/]
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
  
</div>


[#-- Partners --] 
<h3 class="headTitle">[@s.text name="Partners contributing to this deliverable" /]</h3>  
<div id="deliverable-partnership" class="form-group simpleBox">

  <div class="partnerWrapper">
    [#-- Partner who is responsible --]
    <label for="">[@customForm.text name="project.deliverable.indicateResponsablePartner" readText=!editable/]:[@customForm.req required=editable /]</label>
    <div class="form-group responsibleWrapper simpleBox">
      [@deliverableList.deliverablePartner dp=deliverable.responsiblePartner dp_name="deliverable.responsiblePartner" dp_index=0 isResponsable=true  editable=editable /]
    </div>
    
    <br />
    
    [#-- Other contact person that will contribute --]
    [#assign displayOtherPerson = (!deliverable.otherPartners?has_content && !editable)?string('none','block') /]
    <label for="" style="display:${displayOtherPerson}">[@customForm.text name="projectDeliverable.otherContactContributing" readText=!editable/]</label>
    <div class="partnersList listname="deliverable.otherPartners" style="display:${displayOtherPerson}">
      [#if action.getSelectedPartners()?has_content]
        [@deliverableList.deliverablePartnerOther dp=action.getSelectedPartners() dp_name="deliverable.otherPartners" editable=editable /]
      [#else]
        <p class="simpleBox emptyText center"> [@s.text name="project.deliverable.partnership.emptyText" /] </p>
      [/#if]
    </div>
    [#if editable && canEdit]
      <div id="addPartnerBlock" class="addPerson text-right">
        <div class="button-blue  addPartner"><span class="glyphicon glyphicon-plus-sign"></span> [@s.text name="form.buttons.addPartner" /]</div>
      </div>
    [/#if]
  </div>
  
  [#if editable]
    <div class="partnerListMsj note">
      [@s.text name="project.deliverable.generalInformation.partnerNotList" /]
      <a href="[@s.url namespace="/${currentSection}" action='${(crpSession)!}/partners'] [@s.param name="projectID"]${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]"> 
        [@s.text name="project.deliverable.generalInformation.partnersLink" /] 
      </a>
    </div>
  [/#if]
</div>