[#ftl]
[#-- Title input --] 
<div class="col-md-12 form-group">
  [@customForm.input name="deliverable.title" value="${(deliverable.title)!}" type="text" i18nkey="Title"  placeholder="" className="limitWords-15" required=true editable=editable /]
</div>

[#-- Type and subtype inputs --] 
<div class=" form-group">
  <div class="col-md-6">
    [@customForm.select name="deliverable.deliverableType.deliverableType.id" label=""  i18nkey="Type" listName="deliverableTypeParent" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className=" form-control input-sm typeSelect" disabled=!editable/]
  </div>
  <div class="col-md-6">
    [@customForm.select name="deliverable.deliverableType.id" label=""  i18nkey="Subtype" listName="deliverableSubTypes" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className=" form-control input-sm subTypeSelect" disabled=!editable/]
  </div>
</div>

[#-- Description textArea --] 
<div class="form-group">
  <div class="col-md-12">[@customForm.textArea value="" name="" i18nkey="Description" required=true className="limitWords-15" editable=editable /]</div>
</div>

[#-- Status and year expected selects --] 
<div class="col-md-12 form-group">
[#if reportingActive]
  <div class="col-md-6">
    [@customForm.select name="deliverable.status" label=""   i18nkey="project.deliverable.status" listName="status"  multiple=false required=true  className=" form-control input-sm" disabled=!editable/]
  </div>
[/#if]  
  <div class="col-md-6">
    [@customForm.select name="deliverable.year" label=""  i18nkey="Year of expected completion" listName="project.allYears"   multiple=false required=true  className=" form-control input-sm" disabled=!editable/]
  </div>
</div>

[#-- Status justification textArea --] 
[#if reportingActive]
<div class="col-md-12 form-group">
  <div class="col-md-12">[@customForm.textArea  name="deliverable.statusDescription" i18nkey="Status justification" required=true className="limitWords-150" editable=editable /]</div>
</div>
[/#if]

[#-- Outcome select --] 
<div class="col-md-12 form-group">

  <div class="col-md-12">
  ${deliverable.crpProgramOutcome.id}
    [@customForm.select name="deliverable.crpProgramOutcome.id" label=""  i18nkey="Outcome" listName="projectOutcome" keyFieldName="crpProgramOutcome.id"  displayFieldName="crpProgramOutcome.composedName"  multiple=false required=true  className=" outcome" disabled=!editable/]
  </div>
</div>

[#-- CoA Outputs select --] 
<div class="col-md-12 form-group">
  <div class="col-md-12">
    [@customForm.select name="deliverable.crpClusterKeyOutput.id" label=""  i18nkey="CoA Outputs" listName="keyOutputs" keyFieldName="id"  displayFieldName="keyOutput"  multiple=false required=true  className="keyOutput" disabled=!editable/]
  </div>
</div>

[#-- Partners --] 

<div id="deliverable-partnership" class="clearfix col-md-12">
<h3 class="headTitle">[@s.text name="Partners contributing to this deliverable" /]</h3>  
  <div class="fullBlock partnerWrapper">
    [#-- Partner who is responsible --]
    <div class="fullBlock responsibleWrapper">
    [@deliverableList.deliverablePartner dp=deliverable.responsiblePartner dp_name="deliverable.responsiblePartner.projectPartnerPerson" dp_index=0 isResponsable=true  editable=editable /]
    </div>
    [#-- Other contact person that will contribute --]
    [#assign displayOtherPerson = (!deliverable.otherPartners?has_content && !editable)?string('none','block') /]
    <p style="display:${displayOtherPerson}">[@customForm.text name="Other contact person(s) that will contribute to this deliverable:" readText=!editable/]</p>
    <div class="simpleBox personList col-md-12" style="display:${displayOtherPerson}">
    [#if deliverable.otherPartners?has_content]
        [#list deliverable.otherPartners as dp]
          [@deliverableList.deliverablePartner dp=dp dp_name="deliverable.otherPartners" dp_index=dp_index editable=editable /]
        [/#list]
      [#else]
        <p class="emptyText center"> [@s.text name="projectDeliverable.partnership.emptyText" /] </p>
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
      [@s.text name="projectDeliverable.generalInformation.partnerNotList" /]
      <a href="[@s.url namespace="/${currentSection}" action='${(crpSession)!}/partners'] [@s.param name="projectID"]${projectID}[/@s.param][/@s.url]"> 
        [@s.text name="projectDeliverable.generalInformation.partnersLink" /] 
      </a>
    </div>
  [/#if]
</div>

