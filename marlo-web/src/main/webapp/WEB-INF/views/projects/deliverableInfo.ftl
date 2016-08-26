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
    [@customForm.select name="deliverable.deliverableType.id" label=""  i18nkey="Subtype" listName="" keyFieldName=""  displayFieldName="" value="" multiple=false required=true  className=" form-control input-sm subTypeSelect" disabled=!editable/]
  </div>
</div>

[#-- Description textArea --] 
<div class="form-group">
  <div class="col-md-12">[@customForm.textArea value="" name="" i18nkey="Description" required=true className="limitWords-15" editable=editable /]</div>
</div>

[#-- Status and year expected selects --] 
<div class="form-group">
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
<div class="form-group">
  <div class="col-md-12">[@customForm.textArea  name="deliverable.statusDescription" i18nkey="Status justification" required=true className="limitWords-150" editable=editable /]</div>
</div>
[/#if]

[#-- Flagship select --] 
<div class=" form-group">
  <div class="col-md-12">
    [@customForm.select name="" label=""  i18nkey="Flagship" listName="projectPrograms" keyFieldName="crpProgram.id"  displayFieldName="crpProgram.composedName"  multiple=false required=true  className=" form-control input-sm flagship" disabled=!editable/]
  </div>
</div>

[#-- Outcome select --] 
<div class=" form-group">
  <div class="col-md-12">
    [@customForm.select name="" label=""  i18nkey="Outcome" listName="" keyFieldName=""  displayFieldName="" value="" multiple=false required=true  className=" form-control input-sm outcome" disabled=!editable/]
  </div>
</div>

[#-- CoA select --] 
<div class="form-group">
  <div class="col-md-12">
    [@customForm.select name="" label=""  i18nkey="CoA" listName="" keyFieldName=""  displayFieldName="" value="" multiple=false required=true  className=" form-control input-sm cluster" disabled=!editable/]
  </div>
</div>

[#-- CoA Outputs select --] 
<div class=" form-group">
  <div class="col-md-12">
    [@customForm.select name="" label=""  i18nkey="CoA Outputs" listName="" keyFieldName=""  displayFieldName="" value="" multiple=false required=true  className=" form-control input-sm keyOutput" disabled=!editable/]
  </div>
</div>