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
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="powbExpectedMilestones.help" /]
    
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
        [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>

[#-- Contributions Modal --]
<!-- Modal -->
<div class="modal fade" id="outcomeProjectsModal" tabindex="-1" role="dialog" aria-labelledby="outcomeProjectsModal">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">Outcome and Milestone Contributions</h4>
      </div>
      <div class="modal-body">
        <div class="form-group">
          <div class="grayBox">
            <strong>Outcome 2022:</strong> # of policy decisions taken (in part) based on engagement and information dissemination by CCAFS
          </div>
        </div>
      
        [#-- Project contribution to this outcome --]
        <div class="form-group">
          <h5 class="sectionSubTitle">[@s.text name="powbExpectedMilestones.outcomeContributions" /]</h5>
          <table class="">
            <thead>
              <tr>
                <th>Project ID</th>
                <th>Project Title</th>
                <th>Target Value and Unit</th>
                <th>Expected Narrative</th>
              </tr>
            </thead>
            <tbody>
              [#list 1..2 as contribution]
              <tr>
                <td>P1</td>
                <td>(ICRISAT WA) Capacitating science-policy exchange platforms to mainstream climate change...</td>
                <td>3 - Policy decisions taken</td>
                <td>Outcome and knowledge produced through the CCAFS network will be used to inform policy actors, including Parliament Members on...</td>
              </tr>
              [/#list]
            </tbody>
          </table>
        </div>
        [#-- Project contribution to the milestones --]
        <div class="form-group">
          <h5 class="sectionSubTitle">[@s.text name="powbExpectedMilestones.milestoneContributions" /]</h5>
          <table class="">
            <thead>
              <tr>
                <th>Project ID</th>
                <th>Project Title</th>
                <th>Milestone</th>
                <th>Target Value and Unit</th>
                <th>Expected Narrative</th>
              </tr>
            </thead>
            <tbody>
              [#list 1..2 as contribution]
              <tr>
                <td>P269</td>
                <td>[Gender and Social Inclusion] GSI: Engagement, synthesis and support in Gender</td>
                <td>2017 - Training materials developed and workshops held to strengthen partner capacity...</td>
                <td>N/A</td>
                <td>3 Briefing note and advocacy strategy developed for policy makers (one in each country) and Workshops...</td>
              </tr>
              [/#list]
            </tbody>
          </table>
        </div>
      </div> 
    </div>
  </div>
</div>

[#include "/WEB-INF/crp/pages/footer.ftl"]


