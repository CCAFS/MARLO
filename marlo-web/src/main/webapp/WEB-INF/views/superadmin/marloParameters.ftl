[#ftl]
[#assign title = "MARLO Admin" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = [] /]
[#assign customJS = [ "${baseUrl}/js/superadmin/marloParameters.js" ] /]
[#assign customCSS = [ "${baseUrl}/css/superadmin/superadmin.css" ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "parameters" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"parameters", "nameSpace":"", "action":""}
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
        
          [#-- MARLO CRPs--]
          <h4 class="sectionTitle">[@s.text name="marloParameters.title" /]</h4>
          <div class="crps-list">
          [#if crps?has_content]
            [#list crps as crp]
              [@crpParametersMacro element=crp name="crps" index=crp_index  /]
            [/#list]
          [/#if]
          </div>
          
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

[#-- Parameter template --]
<table>
	<tbody>
    [@parameterMacro element={} name="crps[-1].parameters" index=-1 isTemplate=true /]
	</tbody>
</table>

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#-- MACROS --]
[#macro crpParametersMacro element name index isTemplate=false]
  <div id="crpParameters-${isTemplate?string('template',index)}" class="crpParameters borderBox" style="display:${isTemplate?string('none','block')}">
    [#local customName = "${name}[${index}]"]
    [#-- CRP Title --]
    <div class="blockTitle closed">
      <strong>${(element.acronym?upper_case)!}</strong> - ${(element.name)!} <small>(Parameters: ${(element.parameters?size)!0})</small>
    </div>
    
    <div class="blockContent" style="display:none">
      <hr /> 
      [#if element.parameters??]
        <table class="table table-striped table-condensed ">
          <tbody>
          [#list element.parameters as parameter]
            [@parameterMacro element=parameter name="${customName}.parameters" index=parameter_index /]
          [/#list]
          </tbody>
        </table>
      [/#if]
      [#-- Add parameter --]
      <div class="buttonBlock text-right">
        <div class="addParameter button-blue">
          <span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addParameter"/]
        </div>
      </div>
      
    </div>
  </div>
[/#macro]

[#macro parameterMacro element name index isTemplate=false]
  [#local customName = "${name}[${index}]"]
  <tr id="parameter-${isTemplate?string('template',index)}" class="parameter" style="display:${isTemplate?string('none','table-row')}">
    <td>
      <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
      [#if isTemplate]
        [@customForm.input name="${customName}.key" placeholder="Key" showTitle=false /]
      [#else]
        <input type="hidden" name="${customName}.key" value="${(element.key)!}" />
        <strong>${(element.key)!}</strong> Description ${(element.description)!} Type ${(element.type)!}  
      [/#if]
    </td>
    <td>
      [@customForm.input name="${customName}.value" placeholder="Value" showTitle=false /]
    </td>
    <td>
      <div style="position:relative">
        <div class="removeParameter removeIcon" title="Remove"></div>
      </div>
    </td>
  </tr>
[/#macro]