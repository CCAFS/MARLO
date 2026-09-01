[#ftl]
[#assign title = "MARLO Admin" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [] /]
[#assign customJS = [
  "${baseUrlCdn}/global/js/superadmin/marloParameters.js?20260827",
  "//cdn.datatables.net/1.13.1/js/jquery.dataTables.min.js"
  ]
/]
[#assign customCSS = [
  "${baseUrlCdn}/global/css/superadmin/superadmin.css?20260827",
  "//cdn.datatables.net/1.13.1/css/jquery.dataTables.min.css"
  ] /]
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
        [#include "/WEB-INF/global/views/superadmin/menu-superadmin.ftl" /]
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
        [#-- Roles(1, "Roles"), Specificities(2, "Specificities"), Settings(3, "Settings"); --]
        <ul class="nav nav-tabs" role="tablist">
        [#list parametersTypes as type]
          <li class="${type?is_first?string('active','')}"><a href="#type-${type_index}-${element.id}" role="tab" data-toggle="tab">${type.format}</a></li>
        [/#list]
        </ul>
        <div class="tab-content">
          [#list parametersTypes as type]
            <div role="tabpanel" class="tab-pane ${type?is_first?string('active','')}" id="type-${type_index}-${element.id}">
              <div class="parameter-search-wrap">
                <input type="text" class="form-control parameter-search" placeholder="Search parameter..." data-target="table-${type_index}-${element.id}" />
                <div class="iconSearch parameter-search-icon">
                  <span class="glyphicon glyphicon-search" aria-hidden="true"></span>
                </div>
              </div>
              <table class="table table-striped table-hover parametersTable" id="table-${type_index}-${element.id}" width="100%">
                <thead>
                  <tr>
                    <th id="parameterNames-${type_index}-${element.id}">[@s.text name="marloParameters.table.parameter" /]</th>
                    <th id="parameterValues-${type_index}-${element.id}" class="col-md-3 text-center">[@s.text name="marloParameters.table.value" /]</th>
                  </tr>
                </thead>
                <tbody>
                [#list element.parameters as crpParameter]
                  [#if type.id ==crpParameter.parameter.category]
                    [@parameterMacro element=crpParameter name="${customName}.parameters" index=crpParameter_index crpIndex=index/]
                  [/#if]
                [/#list]
                </tbody>
              </table>
            </div>
          [/#list]
        </div>
      [/#if]
      
    </div>
  </div>
[/#macro]

[#macro parameterMacro element name index crpIndex=-1 isTemplate=false]
  [#local customName = "${name}[${index}]"]
  <tr id="parameter-${isTemplate?string('template',index)}" class="parameter" style="display:${isTemplate?string('none','table-row')}">
    <td>
      <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
      <input type="hidden" name="${customName}.parameter.key" value="${(element.parameter.key)!}" />
      <input type="hidden" name="${customName}.parameter.id" value="${(element.parameter.id)!}" />
      
      [#if isTemplate]
        [@customForm.input name="${customName}.paramater.description" placeholder="Description" showTitle=false /]
        [@customForm.input name="${customName}.paramater.key" placeholder="Key" showTitle=false /]
        <input type="hidden" name="${customName}.category" value="4" />
      [#else]
        <input type="hidden" name="${customName}.parameter.description" value="${(element.parameter.description)!}" />
        <input type="hidden" name="${customName}.paramater.key" value="${(element.parameter.key)!}" />
        <input type="hidden" name="${customName}.parameter.category" value="${(element.parameter.category)!}" />
        [#--
          IMPORTANT (developers): the label rendered below is a DISPLAY-ONLY relabeling of 'crp' to 'System'.
          Nothing is renamed in the database: the `parameters` table keeps the original `crp_` prefix in
          `key` and the original wording in `description`, and the hidden inputs above submit them verbatim,
          so saving this form never rewrites them.

          To look a parameter up, ALWAYS use the stored key, never the label shown on screen:
            SELECT * FROM parameters WHERE `key` = 'crp_has_contact_point';    -- correct
            SELECT * FROM parameters WHERE `key` = 'system_has_contact_point'; -- returns no rows
          Same rule for custom_parameters (joined through parameter_id) and for the APConstants constants in
          marlo-data and marlo-web: their value MUST keep matching parameters.key, so they stay 'crp_...'.

          Only relabel here. Renaming a key for real means a Flyway migration plus both APConstants files
          plus every usage, and it is out of scope for this view.
        --]
        [#--
          The 'crp' -> 'system' relabeling rule only covers the 'crp_' prefix. 'crp_email_pl_crpAdmin_fl'
          also carries 'crpAdmin' in the middle of the key, which falls outside that rule, so it is
          relabeled here explicitly. Display only: the stored key stays 'crp_email_pl_crpAdmin_fl'.
        --]
        [#local displayKey = (element.parameter.key?replace(r'^crp_', 'system_', 'r')?replace('crpAdmin', 'systemAdmin'))!'' ]
        [#local displayDescription = (element.parameter.description?replace('\n', '<br>')?replace(r'\bCRPs\b', 'Systems', 'ri')?replace(r'\bCRP\b', 'System', 'ri'))!'' ]
        <strong>${displayKey} </strong> <br /> <small><i>(${displayDescription})</i></small>
      [/#if]
    </td>
    <td class="col-md-3 text-center">
      [#--  Boolean(1, "Boolean"), Date(2, "Date"), Int(3, "Int"), Text(4, "Text"); --]
      [#if (element.parameter.format == 1)!false]
        <div class="radioFlat radio-inline">
          <input id="yes-${(element.id)!}" type="radio" name="${customName}.value" value="true" [#if (element.value == "true")!false]checked[/#if] />
          <label for="yes-${(element.id)!}" class="radio-label radio-label-yes"> Yes</label>
        </div>
        <div class="radioFlat radio-inline">
          <input id="no-${(element.id)!}" type="radio" name="${customName}.value" value="false"  [#if (element.value == "false")!false]checked[/#if]/>
          <label for="no-${(element.id)!}" class="radio-label radio-label-no"> No</label>
        </div>
      [#else]
        [#if element.parameter.category == 1]
          [@customForm.select name="${customName}.value" i18nkey="" className="parameterValue type-${element.parameter.format}" listName="crps[${crpIndex}].roles" keyFieldName="id" displayFieldName="description" showTitle=false  /]
        [#else]
          [@customForm.input name="${customName}.value" className="parameterValue type-${element.parameter.format}" placeholder="${(element.parameter.defaultValue)!'Value'}" showTitle=false /]
        [/#if]
      [/#if]
    </td>
  </tr>
[/#macro]