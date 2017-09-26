[#ftl]
[#assign title = "MARLO SLOs" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = [] /]
[#assign customJS = [ "${baseUrl}/global/js/superadmin/marloSLOs.js" ] /]
[#assign customCSS = [ "${baseUrl}/global/css/superadmin/superadmin.css" ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "crossCutting" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"crossCutting", "nameSpace":"", "action":""}
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
        
        [#-- Cross-Cutting Issues --]
        <h4 class="sectionTitle">[@s.text name="marloSLOs.crossCuttingsTitle" /]</h4>
        <div class="issues-list">
        [#if srfCrossCuttingIssues?has_content]
          [#list srfCrossCuttingIssues as issue]
            [@srfCCIssueMacro element=issue name="srfCrossCuttingIssues[${issue_index}]" index=issue_index /]
          [/#list]
        [/#if]
        </div>
        [#-- Add Outcome Button --]
        <div class="addCrossCuttingIssue bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addCrossCuttingIssue"/]</div>
        
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

[#-- Cross-Cutting Template --]
[@srfCCIssueMacro element={} name="srfCrossCuttingIssues[-1]" index=-1 isTemplate=true /]

[#include "/WEB-INF/crp/pages/footer.ftl" /]

[#macro srfSloMacro element name index isTemplate=false]
  <div id="srfSlo-${isTemplate?string('template',index)}" class="srfSlo borderBox" style="display:${isTemplate?string('none','block')}">
    [#-- Remove Button --]
    <div class="remove-element removeElement sm" title="Remove"></div>
    
    [#-- SLO Title --]
    <div class="blockTitle closed">
      <strong>SLO: </strong>${(element.title)!'New SLO'} <small>(Indicators: ${(element.srfSloIndicators?size)!0})</small>
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
      <div class="srfIndicators grayBlock">
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
          <div class="addIndicator button-green"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addIndicator"/]</div>
        </div>
      </div>
    
    </div>
  </div>
[/#macro]

[#macro srfSloIndicator element name index isTemplate=false]
  <div id="srfSloIndicator-${isTemplate?string('template',index)}" class="srfSloIndicator form-group" style="position:relative;display:${isTemplate?string('none','block')}">
    [#-- Remove Button --]
    <div class="remove-element removeIcon" title="Remove"></div>
    [#-- Sub-IDO ID --]
    <input type="hidden" name="${name}.id" value="${(element.id)!}" />
    [#-- Sub-IDO Description --]
    [@customForm.textArea name="${name}.title" value="${(element.title)!}" showTitle=false className="title" required=true /]
    <div class="clearfix"></div>
  </div>
[/#macro]


[#macro srfCCIssueMacro element name index isTemplate=false]
  <div id="srfCCIssue-${isTemplate?string('template',index)}" class="srfCCIssue borderBox" style="display:${isTemplate?string('none','block')}">
    [#-- Remove Button --]
    <div class="remove-element removeElement sm" title="Remove"></div>
    
    [#-- SLO Title --]
    <div class="blockTitle closed">
      [@customForm.input name="${name}.name" value="${(element.name)!}" i18nkey="srfCrossCuttingIssue.name" type="text" className="name limitWords-100" required=true /]
    </div>
  </div>
[/#macro]
