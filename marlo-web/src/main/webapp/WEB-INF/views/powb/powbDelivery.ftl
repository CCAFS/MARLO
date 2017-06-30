[#ftl]
[#assign title = "POWB Report" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ ] /]
[#assign customJS = [ ] /]
[#assign customCSS = ["${baseUrl}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "delivery" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/delivery"},
  {"label":"powbDelivery", "nameSpace":"powb", "action":"${crpSession}/delivery"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="powbDelivery.help" /]
    
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
        <h3 class="headTitle">[@s.text name="powbDelivery.title" /]</h3>
        <div class="borderBox">
          [#-- A.1.1 Adjustments/changes to your Theories of Change --] 
          [#if PMU]
          <div class="form-group">
            [@customForm.textArea name="liaisonInstitution.powb.adjustmentsToC" help="liaisonInstitution.powb.adjustmentsToC.help" required=true className="limitWords-100" editable=editable /]
          </div>
          [/#if]
          
          [#-- A.1.2 Highlight expected Outcomes and Outputs --] 
          <div class="form-group">
            [@customForm.textArea name="liaisonInstitution.powb.highlightExpected" help="liaisonInstitution.powb.highlightExpected.help" required=true className="limitWords-100" editable=editable /]
          </div>
          
          [#-- Deliverables contribution to key outputs --]
          [#if flagship]
          <div class="form-group">
            <h5 class="sectionSubTitle">[@s.text name="powbDelivery.deliverableContributions" /]</h5>
            <table class="">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Title</th>
                  <th>Sub-Type</th>
                  <th>Outcome</th>
                  <th>Key Outputs</th>
                </tr>
              </thead>
              <tbody>
                [#list 1..3 as deliverable]
                <tr>
                  <td>D1234</td>
                  <td>Best practice guide to socially and gender-inclusive development in the Kenyan intensive dairy sector</td>
                  <td>Guidebook/handbook/good Practice Note</td>
                  <td># of policy decisions taken (in part) based on engagement and information dissemination by CCAFS.</td>
                  <td>Modelling of impacts on specific crop/fish/livestock species and quantification of uncertainties, fuelled partly by next generation...</td>
                </tr>
                [/#list]
              </tbody>
            </table>
          </div>
          [/#if]
          
          [#-- Flagships - Highlight expected Outcomes and Outputs  --]
          [#if PMU]
          <div class="form-group">
            <h5 class="sectionSubTitle">[@s.text name="powbDelivery.flagshipsExpectedOutcomesOutputs" /]</h5>
            <table class="">
              <thead>
                <tr>
                  <th>Flagship</th>
                  <th>Narrative</th>
                </tr>
              </thead>
              <tbody>
                [#list 1..4 as flagship]
                <tr>
                  <td>FP1 - Priorities and Policies for CSA</td>
                  <td><i>Prefilled when available</i></td>
                </tr>
                [/#list]
              </tbody>
            </table>
          </div>
          [/#if]
          
          [#-- A.1.3 Use of different Funding Sources --]
          [#if PMU]
          <div class="form-group">
            <label for="">[@s.text name="liaisonInstitution.powb.useFundingSource" /]</label>
            <p><i>[@s.text name="powbDelivery.useFundingSource" /]</i></p>
          </div>
          [/#if]
          
          [#-- A.1.4 Planned Revisions to your Program of Work --] 
          [#if PMU]
          <div class="form-group">
            [@customForm.textArea name="liaisonInstitution.powb.plannedRevisions" help="liaisonInstitution.powb.plannedRevisions.help" required=true className="limitWords-100" editable=editable /]
          </div>
          [/#if]
        
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]