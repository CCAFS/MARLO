[#ftl]
[#assign title = "POWB Report" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ ] /]
[#assign customJS = [ ] /]
[#assign customCSS = ["${baseUrlMedia}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "contFunds" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/delivery"},
  {"label":"powbContFunds", "nameSpace":"powb", "action":"${crpSession}/contFunds"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="powbContFunds.help" /]
    
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
        <h3 class="headTitle">[@s.text name="powbContFunds.title" /]</h3>
        <div class="borderBox">
          [#-- B.1.3 Contribution of W1-2 Funds --]
          <div class="form-group">
            [@customForm.textArea name="liaisonInstitution.powb.contributionW12Funds" help="liaisonInstitution.powb.contributionW12Funds.help" required=true className="limitWords-100" editable=editable /]
          </div>
          
          <h5 class="sectionSubTitle">[@s.text name="powbContFunds.flagshipOutcomes"][@s.param]${liaisonInstitution.acronym}[/@s.param][/@s.text]</h5>
          [#list 1..3 as outcome]
            <div class="form-group outcomeBlock simpleBox">
              <div class="form-group grayBox outcomeStatement">
                [#-- Outcome Statement --]
                <strong>Outcome 2022:</strong> # of policy decisions taken (in part) based on engagement and information dissemination by CCAFS
              </div>
              
              [#-- Key Outputs --]
              <div class="form-group">
                <h5 class="sectionSubTitle">[@s.text name="powbContFunds.flagshipOutcomes"][@s.param]2017[/@s.param][/@s.text]</h5>
                <table class="">
                  <thead>
                    <tr>
                      <th>W1/W2</th>
                      <th>W3</th>
                      <th>Bilateral</th>
                      <th>Center Funds</th>
                      <th>W3/Bilateral/Center Funds</th>
                    </tr>
                  </thead>
                  <tbody>
                    [#list 1..2 as milestones]
                    <tr>
                      <td>
                        [@customForm.input name="" showTitle=false editable=editable /]
                        <i>Suggested US$ 163,000.00</i>
                      </td>
                      <td>
                        [@customForm.input name="" showTitle=false editable=editable /]
                        <i>Suggested US$ 149,000.00</i>
                      </td>
                      <td>
                        [@customForm.input name="" showTitle=false editable=editable /]
                        <i>Suggested US$ 83,000.00</i>
                      </td>
                      <td>
                        [@customForm.input name="" showTitle=false editable=editable /]
                        <i>Suggested US$ 0.00</i>
                      </td>
                      <td>
                        US$ 233,000.00
                      </td>
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