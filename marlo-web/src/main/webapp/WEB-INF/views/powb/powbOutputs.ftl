[#ftl]
[#assign title = "POWB Report" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ ] /]
[#assign customJS = [ ] /]
[#assign customCSS = ["${baseUrlMedia}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "outputs" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/delivery"},
  {"label":"powbOutputs", "nameSpace":"powb", "action":"${crpSession}/outputs"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="powbOutputs.help" /]
    
<section class="container">
  [#-- Program (Flagships and PMU) --]
  [#include "/WEB-INF/views/powb/submenu-powb.ftl" /]
  
  <div class="row">
    [#-- POWB Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/views/powb/menu-powb.ftl" /]
    </div> 
    <div class="col-md-9">
      [#-- Section Messages --]
      [#include "/WEB-INF/views/powb/messages-powb.ftl" /]
      
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
         
        [#-- Title --]
        <h3 class="headTitle">[@s.text name="powbOutputs.title" /]</h3>
        <div class="borderBox">
          <h5 class="sectionSubTitle">[@s.text name="powbExpectedMilestones.flagshipOutcomes"][@s.param]${liaisonInstitution.acronym}[/@s.param][/@s.text]</h5>
          [#list 1..3 as outcome]
            <div class="form-group outcomeBlock simpleBox">
              <div class="form-group grayBox outcomeStatement">
                [#-- Outcome Statement --]
                <strong>Outcome 2022:</strong> # of policy decisions taken (in part) based on engagement and information dissemination by CCAFS
              </div>
              
              [#-- B.1.2 Output towards Outcomes 2022 --]
              <div class="form-group">
                [@customForm.textArea name="liaisonInstitution.powb.outputTowardsOutcomes" help="liaisonInstitution.powb.outputTowardsOutcomes.help" required=true className="limitWords-100" editable=editable /]
              </div>
              
              [#-- Key Outputs --]
              <div class="form-group">
                <h5 class="sectionSubTitle">[@s.text name="powbExpectedMilestones.srfSubIdo" /]</h5>
                <table class="">
                  <thead>
                    <tr>
                      <th>Key Output</th>
                      <th>Gender</th>
                      <th>Youth</th>
                      <th>CapDev</th>
                    </tr>
                  </thead>
                  <tbody>
                    [#list 1..2 as KeyOutput]
                    <tr>
                      <td>1.1.1 Cutting-edge scenario development methodology...</td>
                      <td>[@targetMacro /]</td>
                      <td>[@targetMacro /]</td>
                      <td>[@targetMacro /]</td>
                    </tr>
                    [/#list]
                  </tbody>
                </table>
              </div>
              
            </div>
          [/#list]
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]


[#macro targetMacro]
  <select name="" class="form-control input-sm" id="">
    <option value="">Not targeted</option>
    <option value="">Significant</option>
    <option value="">Principal</option>
  </select>
[/#macro]