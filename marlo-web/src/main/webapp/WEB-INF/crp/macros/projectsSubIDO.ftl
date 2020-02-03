[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]
[#import "/WEB-INF/global/macros/forms.ftl" as forms/]
[#macro subIDOMacro subIdo name index listName="" keyFieldName="" displayFieldName="" maxLimit=0 indexLevel=1 isTemplate=false]
  [#local subIDOCustomName = "${name}[${index}]" /]
  [#local subIDOCustomID = "${name}_${index}"?replace("\\W+", "", "r") /]
  [#attempt]
    [#local list = ((listName?eval)?sort_by(displayFieldName))![] /] 
  [#recover]
    [#local list = [] /] 
  [/#attempt]
  <div id="subIdo-${isTemplate?string('template', index)}" class="subIdo simpleBox" style="display:${isTemplate?string('none','block')}">
    <div class="loading" style="display:none"></div>
    <div class="leftHead blue sm">
      <span class="index">${index+1}</span>
      <span class="elementId">[@s.text name="outcome.subIDOs.index.title"/]</span>
    </div>
    [#-- Hidden inputs --]
    <input type="hidden" class="programSubIDOId" name="${subIDOCustomName}.id" value="${(subIdo.id)!}"/>

    [#-- Remove Button --]
    [#if editable && action.canBeDeleted((subIdo.id)!-1,(subIdo.class.name)!"" )]
      <div class="removeSubIdo removeElement sm" title="Remove Sub IDO"></div>
    [#elseif editable]
      <div class="removeElement sm disable" title="[@s.text name="global.SrfSubIdo"/] can not be deleted"></div>
    [/#if] 
    [#-- Primary Option --]
    <div class="">
      [@customForm.radioFlat id="${subIDOCustomName}.primary" name="${subIDOCustomName}.primary" label="Set this Sub-IDO as primary" disabled=false editable=editable value="true" checked=(subIdo.primary)!false cssClass="setPrimaryRadio" cssClassLabel="radio-label-yes" inline=false /]
    </div>
    [#-- Sub IDO --]
    <div class="form-group">
      <div class="subIdoBlock" >
        <label for="">[@s.text name="outcome.subIDOs.inputSubIDO.label"/]:[#if editable]<span class="red">*</span>[/#if]</label>
        
          <div class="">
            <div id="" class="${subIDOCustomID} subIdoSelected">
            
              [@utils.letterCutter string="${(subIdo.srfSubIdo.description)!'<i>No Sub-IDO Selected</i>'}" maxPos=65 /]
              
            </div>
          </div>
          
        <input type="hidden" class="subIdoId" name="${subIDOCustomName}.srfSubIdo.id" value="${(subIdo.srfSubIdo.id)!}" />
      </div>
      
      
      <div class="buttonSubIdo-block" >
        [#if editable]
          <div class="buttonSubIdo-content"><br> <div class="button-blue selectSubIDO" ><span class=""></span> Select a Sub-IDO</div></div>
        [/#if]
      </div>
      <div class="contributionBlock">[@customForm.input name="${subIDOCustomName}.contribution" type="text" i18nkey="outcome.subIDOs.inputContribution.label" placeholder="% of contribution" className="contribution" required=true editable=editable /]</div>
      <div class="clearfix"></div>
    </div>
    <hr /> 
    [#-- Assumptions List --]
    <div class="row" style="position: relative;">
      <div class="col-md-9">
        <label for="">[@s.text name="outcome.subIDOs.assumptions.label" /]:</label>
        <div class="assumptions-list" listname="${subIDOCustomName}.assumptions">
          [#if subIdo.assumptions?has_content]
            [#list subIdo.assumptions as assumption]
              [@assumptionMacro assumption=assumption name="${subIDOCustomName}.assumptions" index=assumption_index /]
            [/#list]
          [#else]
          [@assumptionMacro assumption={} name="${subIDOCustomName}.assumptions" index=0 /]
          [#-- <p class="message text-center">[@s.text name="outcome.subIDOs.section.notAssumptions.span"/]</p> --]
          [/#if]
        </div>
      </div>
      [#-- Add Assumption Button --]
      [#if editable]<div class="addAssumption button-green"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addAssumption"/]</div>[/#if]
    </div>
  </div>
[/#macro]


[#macro assumptionMacro assumption name index isTemplate=false]
  [#assign assumptionCustomName = "${name}[${index}]" /]
  <div id="assumption-${isTemplate?string('template', index)}" class="assumption form-group" style="position:relative; display:${isTemplate?string('none','block')}">
    [#-- Remove Button --]
    [#if editable]
    <div class="removeAssumption removeIcon" title="Remove assumption"></div>
    [/#if]
    <input type="hidden" class="assumptionId" name="${assumptionCustomName}.id" value="${(assumption.id)!}"/>
    [#if !editable] 
      [#if assumption.description?has_content]
        <div class="input"><p> <strong>${index+1}.</strong> ${(assumption.description)!}</p></div>
      [/#if] 
    [#else]
      [@customForm.input name="${assumptionCustomName}.description" type="text" showTitle=false placeholder="" className="statement limitWords-100" required=true editable=editable /]
    [/#if]
    <div class="clearfix"></div>
  </div>
[/#macro]