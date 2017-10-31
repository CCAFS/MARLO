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
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="powbOutputs.help" /]
    
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
        <h3 class="headTitle">[@s.text name="powbOutputs.title" /]</h3>
        <div class="borderBox">
          <h5 class="sectionSubTitle">[@s.text name="powbOutputs.flagshipOutcomes"][@s.param]${liaisonInstitution.acronym}[/@s.param][/@s.text]</h5>
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
                <h5 class="sectionSubTitle">[@s.text name="powbOutputs.keyOutputs" /]</h5>
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
                      <td>
                        [#-- Popup button --]
                        <button type="button" class="btn btn-default btn-xs pull-right outcomeProjects-" data-toggle="modal" data-target="#keyOutputContributionsModal">
                          <span class="glyphicon glyphicon-pushpin"></span>
                        </button>
                        1.1.1 Cutting-edge scenario development methodology...
                      </td>
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
        [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>

[#-- Contributions Modal --]
<!-- Modal -->
<div class="modal fade" id="keyOutputContributionsModal" tabindex="-1" role="dialog" aria-labelledby="keyOutputContributionsModal">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">Key Output deliverables</h4>
      </div>
      <div class="modal-body">
        [#-- Key Output Contributions --]
        <div class="form-group">
           
          <table class="">
            <thead>
              <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Sub-type</th>
                <th>G</th>
                <th>G Levels</th>
                <th>Y</th>
                <th>CD</th>
              </tr>
            </thead>
            <tbody>
              [#list 1..2 as contribution]
              <tr>
                <td>D615</td>
                <td>ssessment of opportunities to collected data on farm management activities within smallholder dairy value chains.</td>
                <td>Discussion Paper/working Paper/white Paper</td>
                <td><span class="icon-20 icon-check"></span></td>
                <td>
                  <small>
                    <ul>
                    	<li>Collection of sex-disaggregated data</li>
                    	<li>Analysis of sex-disaggregated data</li>
                    </ul>
                  </small>
                </td>
                <td><span class="icon-20 icon-neutral"></span></td>
                <td><span class="icon-20 icon-neutral"></span></td>
              </tr>
              [/#list]
            </tbody>
          </table>
        </div>
        <div class="grayBox"><strong>G</strong> = Gender, <strong>Y</strong> = Youth, <strong>CD</strong> = Capacity Development;</div>
      </div> 
    </div>
  </div>
</div>

[#include "/WEB-INF/crp/pages/footer.ftl"]


[#macro targetMacro]
  <select name="" class="form-control input-sm" id="">
    <option value="">Not targeted</option>
    <option value="">Significant</option>
    <option value="">Principal</option>
  </select>
[/#macro]