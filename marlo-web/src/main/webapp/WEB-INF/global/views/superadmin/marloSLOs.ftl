[#ftl]
[#assign title = "MARLO SLOs" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [] /]
[#assign customJS = [ "${baseUrl}/global/js/superadmin/marloSLOs.js" ] /]
[#assign customCSS = [ "${baseUrl}/global/css/superadmin/superadmin.css" ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "slos" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"marloSLOs", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/crp/pages/header.ftl" /]
<hr />

<div class="container">
  [#include "/WEB-INF/global/pages/breadcrumb.ftl" /]
</div>
[#include "/WEB-INF/global/pages/generalMessages.ftl" /]

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/global/views/superadmin/menu-superadmin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]
        
        [#-- System Level Outcomes --]
        <h4 class="sectionTitle">[@s.text name="marloSLOs.sloTitle" /]</h4>
        <div class="slos-list">
        [#if slosList?has_content]
          [#list slosList as slo]
            [@srfSloMacro element=slo name="slosList[${slo_index}]" index=slo_index  /]
          [/#list]
        [/#if]
        </div>
        [#-- Add Outcome Button --]
        <div class="addSlo bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addSlo"/]</div>
        
        [#-- Section Buttons--]
        <div class="buttons">
          <div class="buttons-content">
            [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /][/@s.submit]
          </div>
        </div>
        
        [/@s.form]
      	
      </div>
    </div>
  </div>
</section>

[#-- SLO Template --]
[@srfSloMacro element={} name="slosList[-1]" index=-1 isTemplate=true /]

[#-- SLO Indicator Template --]
[@srfSloIndicator element={} name="slosList[-1].srfSloIndicators[-1]" index=-1 isTemplate=true /]

[#-- SLO targetsIndicator Template --]
[@targetIndicator element={} name="slosList[-1].srfSloIndicators[-1].targetsIndicator[-1]" index=-1 isTemplate=true /]

[#include "/WEB-INF/crp/pages/footer.ftl" /]

[#macro srfSloMacro element name index isTemplate=false]
  <div id="srfSlo-${isTemplate?string('template',index)}" class="srfSlo borderBox" style="display:${isTemplate?string('none','block')}">
    [#-- Remove Button --]
    <div class="remove-element removeElement sm" title="Remove"></div>
    
    [#-- SLO Title --]
    <div class="blockTitle closed">
      <strong>SLO ${index+1}: </strong>${(element.title)!'New SLO'} <small>(Indicators: ${(element.srfSloIndicators?size)!0})</small>
    </div>
    
    <div class="blockContent" style="display:none">
      <hr />
      [#-- SLO ID  --]
      <input type="hidden" name="${name}.id" value="${(element.id)!}"/>
      
      [#-- SLO Title  --]
      <div class="form-group">
        [@customForm.input name="${name}.title" i18nkey="srfSlo.title" type="text" className="title limitWords-100" required=true /]
      </div>
      
      [#-- SLO Description  --]
      <div class="form-group">
        [@customForm.textArea name="${name}.description" i18nkey="srfSlo.description" className="description limitWords-400" required=true /]
      </div>
      <div class="clearfix"></div>

      [#-- Indicators --]
      <div class="srfIndicators ">
        <h5>[@s.text name="marloSLOs.indicators" /]:</h5>
        <div class="srfIndicators-list form-group">
          [#if element.srfSloIndicators?has_content]
            [#list element.srfSloIndicators as indicator]
              [@srfSloIndicator element=indicator name="${name}.srfSloIndicators[${indicator_index}]" index=indicator_index  /]
            [/#list]
          [#else]
          [/#if]
        </div>
        [#-- Add Indicator Button --]
        <div class="text-right">
          <div class="addIndicator button-blue"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addIndicator"/]</div>
        </div>
      </div>
    
    </div>
  </div>
[/#macro]

[#macro srfSloIndicator element name index isTemplate=false]
  <div id="srfSloIndicator-${isTemplate?string('template',index)}" class="srfSloIndicator grayBlock form-group" style="position:relative;display:${isTemplate?string('none','block')}">
    [#-- Remove Button --]
    <div class="remove-element removeIcon" title="Remove"></div>
    [#-- Sub-IDO ID --]
    <input type="hidden" name="${name}.id" value="${(element.id)!}" />
    [#-- Sub-IDO Description --]
    [@customForm.textArea name="${name}.title" i18nkey="Indicator Statement" value="${(element.title)!}"  className="title" required=true /]
    
    
    [#assign targetsindicators = [
      {"id": "1",  "unit": "Mio. Farm households",  "value":"100", "year":"2022"},
      {"id": "1",  "unit": "Mio. Farm households",  "value":"350", "year":"2030"},
      
      {"id": "2",  "unit": "Mio. People",  "value":"30", "year":"2022"},
      {"id": "2",  "unit": "Mio. People",  "value":"100", "year":"2030"},
      
      {"id": "3",  "unit": "Rate of yield %/yr",  "value":"1.2-1.5", "year":"2022"},
      {"id": "3",  "unit": "Rate of yield %/yr",  "value":"2.0-2.5", "year":"2030"},
      
      {"id": "4",  "unit": "Mio. People",  "value":"30", "year":"2022"},
      {"id": "4",  "unit": "Mio. People",  "value":"150", "year":"2030"},
      
      {"id": "5",  "unit": "Mio. People",  "value":"150", "year":"2022"},
      {"id": "5",  "unit": "Mio. People",  "value":"500", "year":"2030"},
      
      {"id": "6",  "unit": "%",  "value":"10", "year":"2022"},
      {"id": "6",  "unit": "%",  "value":"33", "year":"2030"},
      
      {"id": "7",  "unit": "%",  "value":"5", "year":"2022"},
      {"id": "7",  "unit": "%",  "value":"20", "year":"2030"},
      
      {"id": "8",  "unit": "Gt CO2-e yr -1 (%)",  "value":"0.2 (5)", "year":"2022"},
      {"id": "8",  "unit": "Gt CO2-e yr -1 (%)",  "value":"0.8 (15)", "year":"2030"},
      
      {"id": "9",  "unit": "Mio. Hectares (ha)",  "value":"55", "year":"2022"},
      {"id": "9",  "unit": "Mio. Hectares (ha)",  "value":"190", "year":"2030"},
      
      {"id": "10",  "unit": "Mio. Hectares (ha)",  "value":"2.5", "year":"2022"},
      {"id": "10",  "unit": "Mio. Hectares (ha)",  "value":"7.5", "year":"2030"}
    ]/]
    
    <div class="targetsList">
    [#if element.id??]
      [#list targetsindicators as target]
        [#if (target.id?number== element.id?number)]
          [@targetIndicator element=target name="" index=target_index /]
        [/#if]
      [/#list]
    [/#if]    
    </div>
    <div class="text-right">
      <div class="addTargets button-green"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> Add a target</div>
    </div>
    <div class="clearfix"></div>
    
    
  </div>
[/#macro]

[#macro targetIndicator element name index isTemplate=false]
  <div id="targetIndicator-${isTemplate?string('template',index)}" class="targetsIndicator" style="position:relative;display:${isTemplate?string('none','block')}">
      <div class="targetsContent">
        <h6>Target year: </h6>
        <input type="text" class="targetYear form-control  input-sm" value="${(element.year)!}"/>
        <h6>Target unit: </h6>
        <input type="text" class="targetUnit form-control  input-sm" value="${(element.unit)!}"/>
        <h6>Target value: </h6>
        <input type="text" class="targetValue form-control  input-sm" value="${(element.value)!}"/>
        <span class="targetRemove remove-element glyphicon glyphicon-remove red"></span>
      </div>      
    <div class="clearfix"></div>
  </div>
[/#macro]
