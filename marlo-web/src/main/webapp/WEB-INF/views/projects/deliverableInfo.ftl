[#ftl]
<div class="simpleBox">
  [#-- Title input --] 
  <div class="form-group">
    [@customForm.input name="deliverable.title" value="${(deliverable.title)!}" type="text" i18nkey="project.deliverable.generalInformation.title"  placeholder="" className="limitWords-15" required=true editable=editable /]
  </div>
  
  [#-- Type and subtype inputs --] 
  <div class="form-group row">
    <div class="col-md-6 ">
      [@customForm.select name="deliverable.deliverableType.deliverableType.id" label=""  i18nkey="project.deliverable.generalInformation.type" listName="deliverableTypeParent" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className=" form-control input-sm typeSelect" editable=editable/]
    </div>
    <div class="col-md-6">
      [@customForm.select name="deliverable.deliverableType.id" label=""  i18nkey="project.deliverable.generalInformation.subType" listName="deliverableSubTypes" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className=" form-control input-sm subTypeSelect" editable=editable/]
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
      [#if crpSession == "ccafs"]<p><small>[@s.text name="project.deliverable.generalInformation.disclaimerMessage" /]</small></p>[/#if]
    </div>
    
     
    <div class="clearfix"></div>
  </div>
  
   <div class="clearfix"></div>
  [#-- Status and year expected selects --]
  <div class="${reportingActive?string('fieldFocus','')}">
  <div class="form-group row">
    <div class="col-md-4">
      [@customForm.select name="deliverable.status" label=""   i18nkey="project.deliverable.generalInformation.status" listName="status"  multiple=false required=true header=false className=" status" editable=editable/]
    </div>
    <div class="col-md-4 form-group">
      [#-- If is editable, deliverable is old, there is a saved year and the year is < to the current cycle year --]
      [#assign canNotEditYear = (deliverable.status??) && ( ( !action.isDeliverableNew(deliverable.id) && (deliverable.year??) && (deliverable.year == currentCycleYear)) || (deliverable.status == 4)  || (deliverable.status == 5) ) /]
      [#if canNotEditYear] <input type="hidden" name="deliverable.year" value="${(deliverable.year)!}"/>  [/#if]
      [@customForm.select name="deliverable.year" label=""  i18nkey="project.deliverable.generalInformation.year" listName="project.allYears" header=false  multiple=false required=true  className="yearExpected" disabled=canNotEditYear editable=editable/]
      [#if !editable]${(deliverable.year)!}[/#if]
    </div>
    [#-- New Expected Year - Extended = 4 --]
    [#if reportingActive]
      [#assign canViewNewExpectedYear = (deliverable.status??) && ((deliverable.status == 4)  ||  ( (deliverable.status == 3) &&  deliverable.newExpectedYear?has_content  )) /]
    [#else]
      [#assign canViewNewExpectedYear = !action.isDeliverableNew(deliverable.id) && (currentCycleYear gt deliverable.year) && (deliverable.status??) && ((deliverable.status == 4)  ||  ( (deliverable.status == 3) &&  deliverable.newExpectedYear?has_content  )) /]
    [/#if]
    <div id="newExpectedYear" class="col-md-4" style="display:${canViewNewExpectedYear?string('block','none')}">
      [#if editable]
        [#if reportingActive]
          [#assign startExpectedYear = currentCycleYear-1]
        [#else]
          [#assign startExpectedYear = (deliverable.year)!currentCycleYear ]
        [/#if]
        [@customForm.select name="deliverable.newExpectedYear"  label=""  listName="project.getYears(${startExpectedYear})" header=false  multiple=false required=true  className="yearNewExpected" editable=editable/]
      [#else]
        <input type="hidden" name="deliverable.newExpectedYear" value="${(deliverable.newExpectedYear)!}"/>
        <div class="select">
          <label for="">Expected year:</label>
        	<p>${(deliverable.newExpectedYear)!}</p>
        </div>
      [/#if]
    </div> 
    <div class="clearfix"></div>
  </div>
  
  [#-- Status justification textArea --]
  [#if !action.isDeliverableNew(deliverable.id)]
    [#assign justificationRequired = (deliverable.year??) && (deliverable.status??) &&  ((deliverable.status == 4)  || (deliverable.status == 5)) ]
    <div class="form-group">
      <div id="statusDescription" class="col-md-12" style="display:${justificationRequired?string('block','none')}">
        [@customForm.textArea name="deliverable.statusDescription" className="statusDescription limitWords-150" i18nkey="deliverable.statusJustification.status${(deliverable.status)!'NotSelected'}" editable=editable/]
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
  [#if !project.administrative && !phaseOne]
    <div class="form-group">
      [@customForm.select name="deliverable.crpClusterKeyOutput.id" label=""  i18nkey="project.deliverable.generalInformation.keyOutput" listName="keyOutputs" keyFieldName="id"  displayFieldName="composedName"  multiple=false required=true  className="keyOutput" editable=editable/]
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
            <span title="${(element.fundingSource.composedName)!'undefined'}" class="name">[@utils.wordCutter string=(element.fundingSource.composedName)!"undefined" maxPos=100 substr=" "/]</span>
            <div class="clearfix"></div>
          </li>
        [/#list]
        <p style="display:none;" class="emptyText"> [@s.text name="project.deliverable.fundingSource.empty" /]</p>   
      [#else]
        <p class="emptyText"> [@s.text name="project.deliverable.fundingSource.empty" /]</p> 
      [/#if]
      </ul>
      [#if editable ]
        [@customForm.select name="deliverable.fundingSource.id" label=""  showTitle=false  i18nkey="" listName="fundingSources" keyFieldName="id"  displayFieldName="composedName"  multiple=false required=true  className="fundingSource" editable=editable/]
      [/#if] 
    </div>
  </div>
  [/#if]
</div>

<h3 class="headTitle">Gender, social inclusion and/ or Youth Dimensions of the deliverable </h3>
<div class="simpleBox">
  [#-- Does this deliverable have a cross-cutting dimension --]
  <div class="form-group">
    <label for="">[@customForm.text name="deliverable.crossCuttingDimensions" readText=!editable/] [@customForm.req required=editable/]</label>
    <div class="row">
      <div class="col-md-12">
        [#if editable]
          <label class="checkbox-inline"><input type="checkbox" name="deliverable.crossCuttingGender"   id="gender"   value="true" [#if (deliverable.crossCuttingGender)!false ]checked="checked"[/#if]> Gender</label>
          <label class="checkbox-inline"><input type="checkbox" name="deliverable.crossCuttingYouth"    id="youth"    value="true" [#if (deliverable.crossCuttingYouth)!false ]checked="checked"[/#if]> Youth</label>
          <label class="checkbox-inline"><input type="checkbox" name="deliverable.crossCuttingCapacity" id="capacity" value="true" [#if (deliverable.crossCuttingCapacity)!false ]checked="checked"[/#if]> Capacity Development</label>
          <label class="checkbox-inline"><input type="checkbox" name="deliverable.crossCuttingNa"       id="na"       value="true" [#if (deliverable.crossCuttingNa)!false ]checked="checked"[/#if]> N/A</label>
        [#else]
          [#if (deliverable.crossCuttingGender)!false ] <p class="checked"> Gender</p>[/#if]
          [#if (deliverable.crossCuttingYouth)!false ] <p class="checked"> Youth</p>[/#if]
          [#if (deliverable.crossCuttingCapacity)!false ] <p class="checked"> Capacity Development</p>[/#if]
          [#if (deliverable.crossCuttingNa)!false ] <p class="checked"> N/A</p>[/#if]
        [/#if]
      </div>
    </div>
  </div>
  
  [#-- If gender dimension, select with ones --]
  <div id="gender-levels" class="panel tertiary" style="display:${((deliverable.crossCuttingGender)!false)?string('block','none')}">
  [#if !action.hasSpecificities('crp_one_gender')]
     <div class="panel-head"><label for=""> [@customForm.text name="deliverable.genderLevels" readText=!editable /]:[@customForm.req required=editable /]</label></div>
      <div id="genderLevelsList" class="panel-body" listname="deliverable.genderLevels"> 
        <ul class="list">
        [#if deliverable.genderLevels?has_content]
          [#list deliverable.genderLevels as element]
            <li class="genderLevel clearfix">
              [#if editable]<div class="removeGenderLevel removeIcon" title="Remove Gender Level"></div>[/#if] 
              <input class="id" type="hidden" name="deliverable.genderLevels[${element_index}].id" value="${(element.id)!}" />
              <input class="fId" type="hidden" name="deliverable.genderLevels[${element_index}].genderLevel" value="${(element.genderLevel)!}" />
              <span title="${(element.nameGenderLevel)!'undefined'}" class="name">[@utils.wordCutter string=(element.nameGenderLevel)!"undefined" maxPos=100 substr=" "/]</span>
              <div class="clearfix"></div>
            </li>
          [/#list]
        [#else]
          <p class="emptyText"> [@s.text name="deliverable.genderLevels.empty" /]</p> 
        [/#if]  
        </ul>
        [#if editable ]
          [@customForm.select name="" label="" showTitle=false i18nkey="" listName="genderLevels" keyFieldName="id" displayFieldName="description"  required=true  className="genderLevelsSelect add" editable=editable/]
        [/#if] 
      </div>
  [#else]
    <input class="id" type="hidden" name="deliverable.genderLevels[0].id" value="${(deliverable.genderLevels[0].id)!}" />
    [@customForm.select name="deliverable.genderLevels[0].genderLevel" label="" i18nkey="deliverable.genderLevels" listName="genderLevels" keyFieldName="id" displayFieldName="description"  required=true  className="genderLevelsSelect" editable=editable/]
  [/#if]
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
  
  [#-- Gender level list template --]
  <ul style="display:none">
    <li id="genderLevel-template" class="genderLevel clearfix" style="display:none;">
      <div class="removeGenderLevel removeIcon" title="Remove Gender Level"></div>
      <input class="id" type="hidden" name="deliverable.genderLevels[-1].id" value="" />
      <input class="fId" type="hidden" name="deliverable.genderLevels[-1].genderLevel" value="" />
      <span class="name"></span>
      <div class="clearfix"></div>
    </li>
  </ul>
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
    <div class="simpleBox personList listname="deliverable.otherPartners" style="display:${displayOtherPerson}">
      [#if deliverable.otherPartners?has_content]
        [#list deliverable.otherPartners as dp]
          [@deliverableList.deliverablePartner dp=dp dp_name="deliverable.otherPartners" dp_index=dp_index editable=editable /]
        [/#list]
      [#else]
        <p class="emptyText center"> [@s.text name="project.deliverable.partnership.emptyText" /] </p>
      [/#if]
    </div>
    [#if editable && canEdit]
      <div id="addPartnerBlock" class="addPerson text-right">
        <div class="button-blue  addPartner"><span class="glyphicon glyphicon-plus-sign"></span> [@s.text name="form.buttons.addPerson" /]</div>
      </div>
    [/#if]
  </div>
  
  [#if editable]
    <div class="partnerListMsj note">
      [@s.text name="project.deliverable.generalInformation.partnerNotList" /]
      <a href="[@s.url namespace="/${currentSection}" action='${(crpSession)!}/partners'] [@s.param name="projectID"]${projectID}[/@s.param][/@s.url]"> 
        [@s.text name="project.deliverable.generalInformation.partnersLink" /] 
      </a>
    </div>
  [/#if]
</div>
