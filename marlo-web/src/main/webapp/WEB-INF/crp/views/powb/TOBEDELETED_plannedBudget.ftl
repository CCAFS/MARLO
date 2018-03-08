[#ftl]
[#assign title = "POWB Report" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ ] /]
[#assign customJS = [ ] /]
[#assign customCSS = ["${baseUrlMedia}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "plannedBudget" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/summaryHighlight"},
  {"label":"plannedBudget", "nameSpace":"powb", "action":"${crpSession}/plannedBudget"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="plannedBudget.help" /]
    
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
        <h3 class="headTitle">[@s.text name="plannedBudget.title" /]</h3>
        <div class="borderBox">
        
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
        [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/crp/pages/footer.ftl"]