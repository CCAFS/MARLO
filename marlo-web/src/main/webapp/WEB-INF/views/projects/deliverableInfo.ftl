[#ftl]
[#-- Title input --] 
<div class="col-md-12 form-group">
  <div class="col-md-1"><label  for="">Title<span class="red">*</span>:</label></div>
  <div class="col-md-11"><input class="form-control" name="deliverable.title" value="" type="text" /></div>
</div>

[#-- Type and subtype inputs --] 
<div class="col-md-12 form-group">
  <div class="col-md-6">
    [@customForm.select name="deliverable.deliverableType.id" label=""  i18nkey="Type" listName="" keyFieldName=""  displayFieldName="" value="" multiple=false required=true  className=" form-control input-sm" disabled=!editable/]
  </div>
  <div class="col-md-6">
    [@customForm.select name="" label=""  i18nkey="Subtype" listName="" keyFieldName=""  displayFieldName="" value="" multiple=false required=true  className=" form-control input-sm" disabled=!editable/]
  </div>
</div>

[#-- Description textArea --] 
<div class="col-md-12 form-group">
  <div class="col-md-12">[@customForm.textArea name="" i18nkey="Description" required=true className="limitWords-15" editable=editable /]</div>
</div>

[#-- Status and year expected selects --] 
<div class="col-md-12 form-group">
  <div class="col-md-6">
    [@customForm.select name="deliverable.status" label=""  i18nkey="Status" listName="" keyFieldName=""  displayFieldName="" value="" multiple=false required=true  className=" form-control input-sm" disabled=!editable/]
  </div>
  <div class="col-md-6">
    [@customForm.select name="deliverable.year" label=""  i18nkey="Year of expected completion" listName="" keyFieldName=""  displayFieldName="" value="" multiple=false required=true  className=" form-control input-sm" disabled=!editable/]
  </div>
</div>

[#-- Status justification textArea --] 
<div class="col-md-12 form-group">
  <div class="col-md-12">[@customForm.textArea name="deliverable.statusDescription" i18nkey="Status justification" required=true className="limitWords-15" editable=editable /]</div>
</div>

[#-- Flagship select --] 
<div class="col-md-12 form-group">
  <div class="col-md-12">
    [@customForm.select name="" label=""  i18nkey="Flagship" listName="" keyFieldName=""  displayFieldName="" value="" multiple=false required=true  className=" form-control input-sm" disabled=!editable/]
  </div>
</div>

[#-- Outcome select --] 
<div class="col-md-12 form-group">
  <div class="col-md-12">
    [@customForm.select name="" label=""  i18nkey="Outcome" listName="" keyFieldName=""  displayFieldName="" value="" multiple=false required=true  className=" form-control input-sm" disabled=!editable/]
  </div>
</div>

[#-- CoA select --] 
<div class="col-md-12 form-group">
  <div class="col-md-12">
    [@customForm.select name="" label=""  i18nkey="CoA" listName="" keyFieldName=""  displayFieldName="" value="" multiple=false required=true  className=" form-control input-sm" disabled=!editable/]
  </div>
</div>

[#-- CoA Outputs select --] 
<div class="col-md-12 form-group">
  <div class="col-md-12">
    [@customForm.select name="" label=""  i18nkey="CoA Outputs" listName="" keyFieldName=""  displayFieldName="" value="" multiple=false required=true  className=" form-control input-sm" disabled=!editable/]
  </div>
</div>