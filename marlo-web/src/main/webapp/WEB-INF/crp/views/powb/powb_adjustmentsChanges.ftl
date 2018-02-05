[#ftl]
[#assign title = "POWB Report" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${powbSynthesisID}" /]
[#assign pageLibs = [ "blueimp-file-upload" ] /]
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
        
          [#-- Provide any major modifications to the overall balance of the program and/or Theory of change --] 
          <div class="form-group margin-panel">
            [@customForm.textArea name="powbSynthesis.powbToc.tocOverall" i18nkey="liaisonInstitution.powb.adjustmentsChanges" help="liaisonInstitution.powb.adjustmentsChanges.help" required=true className="limitWords-100" editable=editable /]
          </div>
          
          [#-- Annex a brief updated summary of the crp --] 
          [#if PMU]
          <div class="form-group margin-panel">
            <div class="row">
              <div class="col-sm-7">
                [@customForm.fileUploadAjax 
                  fileDB=(powbSynthesis.powbToc.file)!{} 
                  name="powbSynthesis.powbToc.file.id" 
                  label="adjustmentsChanges.uploadFile" 
                  dataUrl="${baseUrl}/uploadPowbSynthesis.do" 
                  path="${action.path}"
                  isEditable=editable
                  required=true
                /]
                [#-- 
                <div class="input-group">
                    <input type="text" class="form-control" readonly>
                    <label class="input-group-btn">
                        <span class="btn btn-info">
                            Select a file<input id="fileInput" type="file" style="display: none;">
                        </span>
                    </label>
                </div>
                 --]
              </div>
            </div>
          </div>
          [/#if]
          
          [#if PMU]
          <div class="form-group">
            <h4 class="subTitle headTitle">[@s.text name="adjustmentsChanges.flagshipsTable.title" /]</h4>
            <hr />
            [@FlagshipsAdjustmentsMacro /]
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

[#macro FlagshipsAdjustmentsMacro ]
  <table class="table-flagshipsAdjustments table-border-powb" id="table-flagshipsAdjustments">
    <thead>
      <tr class="subHeader">
        <th id="tb-name" width="40%">[@s.text name="adjustmentsChanges.flagshipsTable.flagshipName" /]</th>
        <th id="tb-narrative" class="text-center" width="60%">[@s.text name="adjustmentsChanges.flagshipsTable.narrative" /]</th>
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
