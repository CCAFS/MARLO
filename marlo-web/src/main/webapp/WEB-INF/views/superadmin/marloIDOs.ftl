[#ftl]
[#assign title = "MARLO IDOs" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = [] /]
[#assign customJS = [ "${baseUrlMedia}/js/superadmin/marloIDOs.js" ] /]
[#assign customCSS = [ "${baseUrlMedia}/css/superadmin/superadmin.css" ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "idos" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"marloIDOs", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
<hr />

<div class="container">
  [#include "/WEB-INF/global/pages/breadcrumb.ftl" /]
</div>
[#include "/WEB-INF/global/pages/generalMessages.ftl" /]

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/superadmin/menu-superadmin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]
        <h4 class="sectionTitle">Intermediate Development Outcomes (IDOs) &  Cross Cutting Issues</h4>
        <div class="idos-list">
        [#if idosList?has_content]
          [#list idosList as ido]
            [@srfIdoMacro element=ido name="idosList[${ido_index}]" index=ido_index  /]
          [/#list]
        [#else]
          
        [/#if]
        </div>
        [#-- Add Outcome Button --]
        <div class="addIdo bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addIdo"/]</div>
        
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

[#-- IDO Template --]
[@srfIdoMacro element={} name="idosList[-1]" index=-1 isTemplate=true /]

[#-- Sub-IDO Template --]
[@srfSubIdoMacro element={} name="idosList[-1].srfSubIdos[-1]" index=-1 isTemplate=true /]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro srfIdoMacro element name index isTemplate=false]
  <div id="srfIdo-${isTemplate?string('template',index)}" class="srfIdo borderBox" style="display:${isTemplate?string('none','block')}">
    [#-- Remove Button --]
    <div class="remove-element removeElement sm" title="Remove"></div>
    
    [#-- IDO Title --]
    <div class="blockTitle closed">
      <strong><span></span> ${((element.isCrossCutting)!false)?string('Cross-Cutting IDO','IDO')}:</strong> ${(element.description)!'New IDO'} <small>(Sub IDOs: ${(element.srfSubIdos?size)!0})</small>
    </div>
    
    <div class="blockContent" style="display:none">
      <hr />
      [#-- IDO ID  --]
      <input type="hidden" name="${name}.id" value="${(element.id)!}"/>
      
      [#-- IDO Description  --]
      <div class="form-group">
        [@customForm.input name="${name}.description" i18nkey="srfIdo.description" type="text" className="description limitWords-100" required=true /]
      </div>
      
      [#-- Is Cross-cutting IDO?  --]
      [@customForm.yesNoInput name="${name}.isCrossCutting" label="srfIdo.isCrossCutting"  value="${((element.isCrossCutting)!false)?string}" cssClass="text-left" /]
      
      [#-- SLOs List --]
      <div class="aditional-slos grayBlock" style="display:${((element.isCrossCutting)!false)?string('none','block')}">
       [@s.checkboxlist name="${name}.srfSloIdos" list="slosList" listKey="id" listValue="title" cssClass="checkboxInput"  value="${(element.srfSloIdos)!}" /]
      </div>
      
      [#-- Cross-cutting Issues --]
      <div class="aditional-isCrossCutting grayBlock" style="display:${((element.isCrossCutting)!false)?string('block','none')}">
        [@customForm.select name="${name}.srfCrossCuttingIssue.id"  i18nkey="srfIdo.srfCrossCuttingIssue" className="" listName="srfCrossCuttingIssues" keyFieldName="id"  displayFieldName="name" /]
        <div class="clearfix"></div>
      </div>
      
      [#-- Sub-IDOs --]
      <div class="srfSubIdos grayBlock">
        <h5>[@s.text name="marloIDOs.subIdosList" /]:</h5>
        <div class="subIdosList form-group">
          [#if element.srfSubIdos?has_content]
            [#list element.srfSubIdos as subIdo]
              [@srfSubIdoMacro element=subIdo name="${name}.srfSubIdos[${subIdo_index}]" index=subIdo_index  /]
            [/#list]
          [#else]
            [@srfSubIdoMacro element={} name="${name}.srfSubIdos[0]" index=0 /]
          [/#if]
        </div>
        [#-- Add Sub-IDO Button --]
        <div class="text-right">
          <div class="addSubIdo button-green"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addSubIDO"/]</div>
        </div>
      </div>
    
    </div>
  </div>
[/#macro]

[#macro srfSubIdoMacro element name index isTemplate=false]
  <div id="srfSubIdo-${isTemplate?string('template',index)}" class="srfSubIdo form-group" style="position:relative;display:${isTemplate?string('none','block')}">
    [#-- Remove Button --]
    <div class="remove-element removeIcon" title="Remove"></div>
    [#-- Sub-IDO ID --]
    <input type="hidden" name="${name}.id" value="${(element.id)!}" />
    [#-- Sub-IDO Description --]
    [@customForm.input name="${name}.description" value="${(element.description)!}" showTitle=false type="text" className="description" required=true /]
    <div class="clearfix"></div>
  </div>
[/#macro]
