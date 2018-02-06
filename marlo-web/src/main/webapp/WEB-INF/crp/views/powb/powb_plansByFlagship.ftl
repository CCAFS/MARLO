[#ftl]
[#assign title = "POWB Synthesis" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ "blueimp-file-upload" ] /]
[#assign customJS = [ "${baseUrlMedia}/js/powb/powb_plansByFlagship.js" ] /]
[#assign customCSS = [ "${baseUrlMedia}/css/powb/powbGlobal.css" ] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "plansByFlagship" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/adjustmentsChanges"},
  {"label":"plansByFlagship", "nameSpace":"powb", "action":"${crpSession}/plansByFlagship"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="plansByFlagship.help" /]
    
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
        <h3 class="headTitle">[@s.text name="plansByFlagship.title" /]</h3>
        <div class="borderBox">
          
          [#-- Summarize the plans for each flagship in 2018 --] 
          [#if flagship]
          <div class="form-group">
            [@customForm.textArea  name="powbSynthesis.powbFlagshipPlans.planSummary" i18nkey="liaisonInstitution.powb.planSummary" help="liaisonInstitution.powb.planSummary.help" paramText="${actualPhase.year}" required=true className="limitWords-100" editable=editable /]
          </div>
          [/#if]
          
          [#-- If major changes have been made to your flagship since the CRP proposal was published, please annex a brief summary of the current flagship program with the updated theory of change. --]
          [#if flagship]
          <div class="form-group" style="position:relative" listname="">
            [@customForm.fileUploadAjax 
              fileDB=(powbSynthesis.powbFlagshipPlans.flagshipProgramFile)!{} 
              name="powbSynthesis.powbFlagshipPlans.flagshipProgramFile.id" 
              label="liaisonInstitution.powb.flagshipProgramFile" 
              dataUrl="${baseUrl}/uploadPowbSynthesis.do" 
              path="${action.path}"
              isEditable=editable
            /]
          </div>
          [/#if]
          
          [#-- Table: Overall Flagship Plans --]
          [#if PMU]
          <div class="form-group">
            <h4 class="subTitle headTitle">[@s.text name="plansByFlagship.tableOverall.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h4>
            <hr />
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
  [#assign flagships = [ 
    { "acronym": "FP1"
    },
    { "acronym": "FP2"
    
    },
    { "acronym": "FP3"
    
    },
    { "acronym": "FP4"
    
    }
    ]
  /]

  <div class="">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th> [@s.text name="plansByFlagship.tableOverall.fp" /] </th>
          <th> [@s.text name="plansByFlagship.tableOverall.flagshipPlan" /] </th>
          <th> [@s.text name="plansByFlagship.tableOverall.attached" /] </th>
        </tr>
      </thead>
      <tbody>
        [#list flagships as fp]
          <tr>
            <td>${fp.acronym}</td>
            <td></td>
            <td></td>
          </tr>
        [/#list]
      </tbody>
    </table>
  </div>
[/#macro]