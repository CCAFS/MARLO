[#ftl]
[#assign title = "POWB Report" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ ] /]
[#assign customJS = [ "${baseUrlMedia}/js/powb/powb_adjustmentsChanges.js" ] /]
[#assign customCSS = ["${baseUrlMedia}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "adjustmentsChanges" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/adjustmentsChanges"},
  {"label":"adjustmentsChanges", "nameSpace":"powb", "action":"${crpSession}/adjustmentsChanges"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="adaptativeManagement.help" /]
    
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
        <h3 class="headTitle">[@s.text name="adjustmentsChanges.title" /]</h3>
        <div class="borderBox">
        
          [#-- CRP Management and governance (up to 1/4 page) --] 
          <div class="form-group margin-panel">
            [@customForm.textArea name="liaisonInstitution.powb.adjustmentsChanges" help="liaisonInstitution.powb.adjustmentsChanges.help" required=true className="limitWords-100" editable=editable /]
          </div>
          
          [#-- Annex a brief updated summary of the crp --] 
          <div class="form-group margin-panel">
            <label for="fileInput">[@s.text name="adjustmentsChanges.uploadFile" /]</label>
            <div class="row">
              <div class="col-sm-6">
                <div class="input-group">
                    <input type="text" class="form-control" readonly>
                    <label class="input-group-btn">
                        <span class="btn btn-info">
                            Select a file<input id="fileInput" type="file" style="display: none;" multiple>
                        </span>
                    </label>
                </div>
              </div>
            </div>
          </div>
          
          <div class="form-group">
            <h4 class="subTitle headTitle">[@s.text name="adjustmentsChanges.flagshipsTable.title" /]</h4>
            <hr />
            [@FlagshipsAdjustmentsMacro /]
          </div>

        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/crp/pages/footer.ftl"]

[#macro FlagshipsAdjustmentsMacro ]
  <table class="table-flagshipsAdjustments" id="table-flagshipsAdjustments">
    <thead>
      <tr class="subHeader">
        <th id="tb-name" width="30%">[@s.text name="adjustmentsChanges.flagshipsTable.flagshipName" /]</th>
        <th id="tb-narrative" width="70%">[@s.text name="adjustmentsChanges.flagshipsTable.narrative" /]</th>
      </tr>
    </thead>
    <tbody>
    [#-- if deliverables?has_content 1--]
      [#-- list deliverables as deliverable 2--]
        <tr>
          [#-- Flagship Name --]
          <td class="left">
            FP2 - Climate-Smart Technologies and Practices
          </td>
          
          [#-- Narrative--]
          <td class="left">
            Lorem ipsum dolor sit amet, consectetur adipiscing elit.
            Lorem ipsum dolor sit amet, consectetur adipiscing elit.
            Lorem ipsum dolor sit amet, consectetur adipiscing elit.
          </td>
          
        </tr>
      [#-- [/#list] 2--]
      [#-- [/#if] 1--]
    </tbody>
  </table>
[/#macro]
