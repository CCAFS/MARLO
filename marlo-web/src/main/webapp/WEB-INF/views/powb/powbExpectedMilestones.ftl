[#ftl]
[#assign title = "POWB Report" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ ] /]
[#assign customJS = [ ] /]
[#assign customCSS = ["${baseUrlMedia}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "expectedMilestones" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/delivery"},
  {"label":"powbExpectedMilestones", "nameSpace":"powb", "action":"${crpSession}/expectedMilestones"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="powbExpectedMilestones.help" /]
    
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
        <h3 class="headTitle">[@s.text name="powbExpectedMilestones.title" /]</h3>
        <div class="borderBox">
          <h5 class="sectionSubTitle">[@s.text name="powbExpectedMilestones.flagshipOutcomes"][@s.param]${liaisonInstitution.acronym}[/@s.param][/@s.text]</h5>
          [#list 1..3 as outcome]
            <div class="form-group outcomeBlock simpleBox">
              <div class="form-group grayBox outcomeStatement">
                [#-- Popup button --]
                <button type="button" class="btn btn-default btn-xs pull-right outcomeProjects-" data-toggle="modal" data-target="#outcomeProjectsModal">
                  <span class="glyphicon glyphicon-pushpin"></span> Contributions to the Outcome
                </button>
                [#-- Outcome Statement --]
                <strong>Outcome 2022:</strong> # of policy decisions taken (in part) based on engagement and information dissemination by CCAFS
              </div>
              
              <div class="form-group">
                [@customForm.textArea name="liaisonInstitution.powb.expAnualMilestones" help="liaisonInstitution.powb.expAnualMilestones.help" required=true className="limitWords-100" editable=editable /]
              </div>
              
              [#-- SRF Sub-IDO --]
              <div class="form-group">
                <h5 class="sectionSubTitle">[@s.text name="powbExpectedMilestones.srfSubIdo" /]</h5>
                <table class="">
                  <thead>
                    <tr>
                      <th>SRF Sub-IDO</th>
                      <th>2017 Target</th>
                    </tr>
                  </thead>
                  <tbody>
                    [#list 1..2 as country]
                    <tr>
                      <td>Increased capacity for innovation in partner development organizations and in poor and vulnerable communities</td>
                      <td>[@customForm.input name="" showTitle=false editable=editable /]</td> 
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