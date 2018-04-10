[#ftl]
[#assign title = "POWB Synthesis" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ "blueimp-file-upload" ] /]
[#assign customJS = [ "${baseUrlMedia}/js/powb/powb_crpStaffing.js" ] /]
[#assign customCSS = [ "${baseUrlMedia}/css/powb/powbGlobal.css" ] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "crpStaffing" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/adjustmentsChanges"},
  {"label":"crpStaffing", "nameSpace":"powb", "action":"${crpSession}/crpStaffing"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="crpStaffing.help" /]
    
<section class="container">
  [#-- Program (Flagships and PMU) --]
  [#include "/WEB-INF/crp/views/powb/submenu-powb.ftl" /]
  
  <div class="row">
    [#-- POWB Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/crp/views/powb/menu-powb.ftl" /]
    </div> 
    <div class="col-md-9">
      [#-- Section Messages --]
      [#include "/WEB-INF/crp/views/powb/messages-powb.ftl" /]
      
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
        
        [#-- Title --]
        <h3 class="headTitle">[@s.text name="crpStaffing.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h3>
        <div class="borderBox">
          
          [#-- Briefly summarize any staffing issues or constraints relevant to CRP capacity --] 
          <div class="form-group">
            <input type="hidden" name="powbSynthesis.crpStaffing.id" value="${(powbSynthesis.crpStaffing.id)!}" />
            [@customForm.textArea  name="powbSynthesis.crpStaffing.staffingIssues" i18nkey="powbSynthesis.crpStaffing.staffingIssues" help="powbSynthesis.crpStaffing.staffingIssues.help" helpIcon=false fieldEmptyText="global.prefilledByPmu" paramText="${actualPhase.year}" required=true className="" editable=editable && PMU powbInclude=true /]
          </div>
          
          [#-- Table D: CRP Staffing (OPTIONAL IN POWB 2018)  --]
          <div class="form-group">
            <h4 class="subTitle headTitle powb-table">[@s.text name="crpStaffing.tableD.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h4>
            <span class="powb-doc badge label-powb-table" title="[@s.text name="powb.includedField.title" /]">[@s.text name="powb.includedField" /] <span class="glyphicon glyphicon-save-file"></span></span>
            [@tableD /]
          </div>
        
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#if PMU]
          [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        [/#if]
        
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]

[#---------------------------------------------- MACROS ----------------------------------------------]

[#macro tableD ]
  <div class="">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th rowspan="2">[@s.text name="crpStaffing.tableD.category" /]</th>
          <th colspan="2" class="col-md-3 text-center">[@s.text name="crpStaffing.tableD.female" /] </th>
          <th colspan="2" class="col-md-3 text-center">[@s.text name="crpStaffing.tableD.male" /]</th>
          <th rowspan="2">[@s.text name="crpStaffing.tableD.total" /]</th>
          <th rowspan="2">[@s.text name="crpStaffing.tableD.percFemale" /]</th>
        </tr>
        <tr class="text-small">
          <th class="text-center"> [@s.text name="global.cgiar"/]    </th>
          <th class="text-center"> [@s.text name="global.nonCgiar"/] </th>
          <th class="text-center"> [@s.text name="global.cgiar"/]    </th>
          <th class="text-center"> [@s.text name="global.nonCgiar"/] </th>
        </tr>
      </thead>
      <tbody>
        [#if powbCrpStaffingCategories??]
          [#list powbCrpStaffingCategories  as crpStaffingCategory]
            [#assign customName = "powbSynthesis.powbSynthesisCrpStaffingCategoryList[${crpStaffingCategory_index}]" /]
            [#assign element = (action.getSynthesisCrpStaffingCategory(crpStaffingCategory.id))!{}]
            <tr>
              <td>
                <span>${crpStaffingCategory.category}</span>
                <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
                <input type="hidden" name="${customName}.powbCrpStaffingCategory.id" value="${(crpStaffingCategory.id)!}" />
              </td>
              <td class="text-center"> [@customForm.input name="${customName}.female" value="${(element.female)!'0'}" i18nkey="" showTitle=false className="currencyInput numericValue text-center type-female category-${crpStaffingCategory_index}" required=true editable=editable && PMU /]</td>
              <td class="text-center"> [@customForm.input name="${customName}.femaleNoCgiar" value="${(element.femaleNoCgiar)!'0'}" i18nkey="" showTitle=false className="currencyInput numericValue text-center type-femaleNon category-${crpStaffingCategory_index}" required=true editable=editable && PMU /]</td>
              <td class="text-center"> [@customForm.input name="${customName}.male" value="${(element.male)!'0'}" i18nkey="" showTitle=false className="currencyInput numericValue text-center type-male category-${crpStaffingCategory_index}" required=true editable=editable && PMU /] </td>
              <td class="text-center"> [@customForm.input name="${customName}.maleNoCgiar" value="${(element.maleNoCgiar)!'0'}" i18nkey="" showTitle=false className="currencyInput numericValue text-center type-maleNon category-${crpStaffingCategory_index}" required=true editable=editable && PMU /] </td>
              <td class="text-center"> <span class="label-total category-${crpStaffingCategory_index}">${(element.totalFTE)!"0"}</span> </td>
              <td class="text-center"> <span class="label-percFemale category-${crpStaffingCategory_index}">${(element.femalePercentage)!"0"}</span>% </td>
            </tr>
          [/#list]
        [/#if]
      </tbody>
      <tfoot>
        <tr>
          <td>
            <span>Total CRP</span>
          </td>
          <td class="text-center"> <span class="type-female totalByType">0</span> </td>
          <td class="text-center"> <span class="type-femaleNon totalByType">0</span> </td>
          <td class="text-center"> <span class="type-male totalByType">0</span> </td>
          <td class="text-center"> <span class="type-maleNon totalByType">0</span> </td>
          <td class="text-center"> <span class="label-total grandTotal">0</span> </td>
          <td class="text-center"> <span class="label-percFemale grandTotal">0</span>% </td>
        </tr>
      </tfoot>
    </table>
    <i>[@s.text name="crpStaffing.tableD.help" /]</i>
  </div>
[/#macro]