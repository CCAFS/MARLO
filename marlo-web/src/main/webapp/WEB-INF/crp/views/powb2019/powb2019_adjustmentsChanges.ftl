[#ftl]
[#assign title = "POWB Report" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${powbSynthesisID}" /]
[#assign pageLibs = [ "blueimp-file-upload", "trumbowyg"] /]
[#assign customJS = [ "${baseUrlMedia}/js/powb2019/powb2019_adjustmentsChanges.js" ] /]
[#assign customCSS = ["${baseUrlMedia}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "adjustmentsChanges" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb2019", "action":"${crpSession}/adjustmentsChanges"},
  {"label":"adjustmentsChanges", "nameSpace":"powb2019", "action":"${crpSession}/adjustmentsChanges"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="adjustmentsChanges.help" /]
    
<section class="container">
  [#-- Program (Flagships and PMU) --]
  [#include "/WEB-INF/crp/views/powb2019/submenu-powb2019.ftl" /]
  
  <div class="row">
    [#-- POWB Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/crp/views/powb2019/menu-powb2019.ftl" /]
    </div> 
    <div class="col-md-9">
      [#-- Section Messages --]
      [#include "/WEB-INF/crp/views/powb2019/messages-powb2019.ftl" /]
      
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
         
        [#-- Title --]
        <h3 class="headTitle">[@s.text name="adjustmentsChanges.title" /]</h3>
        <div class="borderBox">
        
          [#-- Provide any major modifications to the overall balance of the program and/or Theory of change --]
          <div class="form-group margin-panel">
            [@customForm.textArea name="powbSynthesis.powbToc.tocOverall" i18nkey="liaisonInstitution.powb.adjustmentsChanges" help="liaisonInstitution.powb.adjustmentsChanges.help" helpIcon=false required=true className="" editable=editable allowTextEditor=true  powbInclude=PMU /]
            [#-- ${action.getOnlyReadHtmlText(powbSynthesis.powbToc.tocOverall)} --]
          </div>
          
          [#if PMU]
          <div class="form-group">
            <h4 class="subTitle headTitle">[@s.text name="adjustmentsChanges.flagshipsTable.title" /]</h4>
            <hr />
            [@FlagshipsTableACMacro elements=tocList/]
          </div>
          [/#if]
          
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]

[#macro FlagshipsTableACMacro elements={}]
  <table class="table-flagshipsAdjustments table-border-powb" id="table-flagshipsAdjustments">
    <thead>
      <tr class="subHeader">
        <th id="tb-name" class="col-md-1">[@s.text name="adjustmentsChanges.flagshipsTable.flagshipName" /]</th>
        <th id="tb-narrative" class="text-center" width="60%">[@s.text name="adjustmentsChanges.flagshipsTable.narrative" /]</th>
      </tr>
    </thead>
    <tbody>
    [#if elements?has_content ]
      [#list elements as element ]
        <tr>
          [#-- Flagship Name --]
          <td class="left flagship-name">
            <span class="programTag" style="border-color:${(element.liaisonInstitution.crpProgram.color)!'#fff'}">
              ${element.liaisonInstitution.acronym}
            </span>
          </td>
          
          [#-- Narrative--]
          <td class="left narrative">
            [#if (element.overall?has_content)!false]
             <div class="">${element.overall}</div> 
            [#else]
              <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
            [/#if]
          </td>
        </tr>
      [/#list]
    [#else]
      <tr>
        <td colspan="2" class="text-center">
          <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
        </td>
      </tr>
    [/#if]
    </tbody>
  </table>
[/#macro]
