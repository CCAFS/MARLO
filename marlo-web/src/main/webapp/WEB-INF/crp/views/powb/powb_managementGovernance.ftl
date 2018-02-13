[#ftl]
[#assign title = "POWB Report" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${powbSynthesisID}" /]
[#assign pageLibs = [ "blueimp-file-upload" ] /]
[#assign customJS = [  ] /]
[#assign customCSS = ["${baseUrlMedia}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "managementGovernance" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/adjustmentsChanges"},
  {"label":"managementGovernance", "nameSpace":"powb", "action":"${crpSession}/managementGovernance"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="managementGovernance.help" /]
    
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
        <h3 class="headTitle">[@s.text name="managementGovernance.title" /]</h3>
        <div class="borderBox">
        
          [#-- Provide any major modifications to the overall balance of the program and/or Theory of change --] 
          <div class="form-group margin-panel">
            [@customForm.textArea name="powbSynthesis.powbManagementGovernance.description"  help="powbSynthesis.powbManagementGovernance.description.help" paramText="${(actualPhase.year)!}" required=true className="limitWords-100" editable=editable /]
          </div>
          
          
          [#if PMU]
          <div class="form-group">
            <h4 class="subTitle headTitle">[@s.text name="managementGovernance.tableOverall.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h4>
            [@tableOverallMacro /]
          </div>
          [/#if]
          
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/crp/pages/footer.ftl"]

[#---------------------------------------------- MACROS ----------------------------------------------]

[#macro tableOverallMacro ]
  <div class="">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th class="col-md-1"> [@s.text name="managementGovernance.tableOverall.fp" /] </th>
          <th> [@s.text name="managementGovernance.tableOverall.narrative" /] </th>
        </tr>
      </thead>
      <tbody>
        [#if managementGovernanceList??]
          [#list managementGovernanceList as element]
            <tr>
              <td><span class="programTag" style="border-color:${(element.liaisonInstitution.crpProgram.color)!'#fff'}">${element.liaisonInstitution.crpProgram.acronym}</span></td>              
              <td>[#if (element.description?has_content)!false]${element.description?replace('\n', '<br>')}[#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]</td>
            </tr>
          [/#list]
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]
