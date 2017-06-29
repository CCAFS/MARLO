[#ftl]
[#assign title = "POWB Report" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ ] /]
[#assign customJS = [ ] /]
[#assign customCSS = ["${baseUrl}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "budget" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/delivery"},
  {"label":"powbBudget", "nameSpace":"powb", "action":"${crpSession}/budget"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="powbBudget.help" /]
    
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
        <h3 class="headTitle">[@s.text name="powbBudget.title" /]</h3>
        <div class="borderBox">
         
          [#-- A.1.3 Use of different Funding Sources --] 
          <div class="form-group">
            [@customForm.textArea name="liaisonInstitution.powb.useFundingSource" help="liaisonInstitution.powb.useFundingSource.help" required=true className="limitWords-100" editable=editable /]
          </div>
          
          [#-- Deliverables contribution to key outputs --]
          <div class="form-group">
            <h5 class="sectionSubTitle">[@s.text name="powbBudget.plannedBudget"][@s.param]${currentCycleYear}[/@s.param] [/@s.text]</h5>
            <table class="">
              <thead>
                <tr>
                  <th>Flagship</th>
                  <th>W1/W2</th>
                  <th>W3/Bilateral</th>
                  <th><strong>Total</strong></th> 
                </tr>
              </thead>
              <tbody>
                [#list 1..4 as deliverable]
                <tr>
                  <td>FP1 - Priorities and Policies for CSA</td>
                  <td>
                    [@customForm.input name="" showTitle=false editable=editable /]
                    <i>Suggested US$ 3,643.00</i>
                  </td>
                  <td>
                    [@customForm.input name="" showTitle=false editable=editable /]
                    <i>Suggested US$ 3,643.00</i>
                  </td>
                  <td> US$ 0.00</td>
                </tr>
                [/#list]
                <tr>
                  <td><strong>Total</strong></td>
                  <td> US$ 0.00</td>
                  <td> US$ 0.00</td>
                  <td> US$ 0.00</td>
                </tr>
              </tbody>
            </table>
          </div>
        
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]