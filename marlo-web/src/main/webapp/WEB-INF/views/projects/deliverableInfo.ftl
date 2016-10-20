[#ftl]
[#-- Title input --] 
<div class="col-md-12 form-group">
  [@customForm.input name="deliverable.title" value="${(deliverable.title)!}" type="text" i18nkey="project.deliverable.generalInformation.title"  placeholder="" className="limitWords-15" required=true editable=editable /]
</div>

[#-- Type and subtype inputs --] 
  <div class="col-md-6 form-group">
    [@customForm.select name="deliverable.deliverableType.deliverableType.id" label=""  i18nkey="project.deliverable.generalInformation.type" listName="deliverableTypeParent" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className=" form-control input-sm typeSelect" editable=editable/]
  </div>
  <div class="col-md-6 form-group">
    [@customForm.select name="deliverable.deliverableType.id" label=""  i18nkey="project.deliverable.generalInformation.subType" listName="deliverableSubTypes" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className=" form-control input-sm subTypeSelect" editable=editable/]
  </div>

[#-- Description textArea --] 
<div class="form-group" style="display:none;">
  <div class="col-md-12">[@customForm.textArea value="" name="" i18nkey="project.deliverable.generalInformation.description" required=true className="limitWords-15" editable=editable /]</div>
</div>

[#-- Status and year expected selects --] 
  <div class="col-md-6">
    [@customForm.select name="deliverable.status" label=""   i18nkey="project.deliverable.generalInformation.status" listName="status"  multiple=false required=true  className=" status" editable=editable/]
  </div>
  <div class="col-md-6 form-group">
    [@customForm.select name="deliverable.year" label=""  i18nkey="project.deliverable.generalInformation.year" listName="project.allYears"   multiple=false required=true  className="yearExpected" editable=editable/]
    [#if !editable]${(deliverable.year)!}[/#if]
  </div>

[#-- Status justification textArea --] 
<div class="col-md-12 form-group justificationContent" style="display:none;">
  <div class="col-md-12">[@customForm.textArea  name="deliverable.statusDescription" i18nkey="Status justification" required=true className="limitWords-150" editable=editable /]</div>
</div>

[#-- CoA Outputs select --] 
  <div class="col-md-12 ">
    [@customForm.select name="deliverable.crpClusterKeyOutput.id" label=""  i18nkey="project.deliverable.generalInformation.keyOutput" listName="keyOutputs" keyFieldName="id"  displayFieldName="keyOutput"  multiple=false required=true  className="keyOutput" editable=editable/]
  </div>
  [#if editable && !(keyOutputs?has_content)]
    <div class="partnerListMsj note col-md-12">
      [@s.text name="project.deliverable.generalInformation.keyOutputNotList1" /]
      <a href="[@s.url namespace="/${currentSection}" action='${(crpSession)!}/description'] [@s.param name="projectID"]${projectID}[/@s.param][@s.param name="edit"]true[/@s.param][/@s.url]#projectsList"> 
        [@s.text name="project.deliverable.generalInformation.partnersLink" /] 
      </a>
      [@s.text name="project.deliverable.generalInformation.keyOutputNotList2" /]
    </div>
  [/#if]

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
    <p style="display:${displayOtherPerson}"><b>[@customForm.text name="Other contact person(s) that will contribute to this deliverable:" readText=!editable/]</b></p>
    <div class="simpleBox personList col-md-12" listname="deliverable.otherPartners" style="display:${displayOtherPerson}">
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