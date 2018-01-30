[#ftl]
[#assign title = "POWB Report" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ ] /]
[#assign customJS = [ ] /]
[#assign customCSS = ["${baseUrlMedia}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "summaryHighlight" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/summaryHighlight"},
  {"label":"summaryHighlight", "nameSpace":"powb", "action":"${crpSession}/summaryHighlight"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="summaryHighlight.help" /]
    
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
        <h3 class="headTitle">[@s.text name="summaryHighlight.title" /]</h3>
        <div class="borderBox">
          [#-- Highlight expected Outcomes and Outputs --] 
          <div class="form-group">
            [@customForm.textArea name="liaisonInstitution.powb.highlightExpectedYear" help="liaisonInstitution.powb.highlightExpectedYear.help" required=true className="limitWords-100" editable=editable /]
          </div>
          
          [#-- Flagship  - Deliverables contribution to key outputs --]
          [#if flagship]
          <div class="form-group">
            <h5 class="sectionSubTitle">[@s.text name="summaryHighlight.deliverableContributions" /]</h5>
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
            <h5 class="sectionSubTitle">[@s.text name="summaryHighlight.flagshipsExpectedOutcomesOutputs" /]</h5>
            <table class="">
              <thead>
                <tr>
                  <th class="col-md-4">Flagship</th>
                  <th class="col-md-8">Narrative</th>
                </tr>
              </thead>
              <tbody>
                [#list 1..4 as flagship]
                <tr>
                  <td>FP${flagship} - Flagship name</td>
                  <td>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Modi nihil quas dolorem animi nesciunt vero possimus placeat debitis iure nulla tempore molestiae quod nostrum porro repellat nam cum assumenda molestias!</td>
                </tr>
                [/#list]
              </tbody>
            </table>
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