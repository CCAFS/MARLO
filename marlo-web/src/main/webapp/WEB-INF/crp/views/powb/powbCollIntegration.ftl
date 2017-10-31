[#ftl]
[#assign title = "POWB Report" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ "flat-flags" ] /]
[#assign customJS = [ ] /]
[#assign customCSS = ["${baseUrlMedia}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "collIntegration" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/delivery"},
  {"label":"powbCollIntegration", "nameSpace":"powb", "action":"${crpSession}/collIntegration"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="powbCollIntegration.help" /]
    
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
        <h3 class="headTitle">[@s.text name="powbCollIntegration.title" /]</h3>
        <div class="borderBox">
          [#-- A.2.1 Contribution to and from Platforms --]
          [#if PMU]
          <div class="form-group">
            [@customForm.textArea name="liaisonInstitution.powb.contributionTo" help="liaisonInstitution.powb.contributionTo.help" required=true className="limitWords-100" editable=editable /]
          </div>
          [/#if]
          
          [#-- A.2.2 Cross-CRP interactions --] 
          <div class="form-group">
            [@customForm.textArea name="liaisonInstitution.powb.crossCrpInteractions" help="liaisonInstitution.powb.crossCrpInteractions.help" required=true className="limitWords-100" editable=editable /]
          </div>
          
          [#-- Flagships Cross CRP interactions --]
          [#if PMU]
          <div class="form-group">
            <h5 class="sectionSubTitle">[@s.text name="powbCollIntegration.flagshipsCrossInteractions" /]</h5>
            <table class="">
              <thead>
                <tr>
                  <th>Flagship</th>
                  <th>Narrative of collaboration</th>
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
          
          [#-- A.2.3 Expected Efforts on Country Coordination --]
          [#if PMU]
          <div class="form-group">
            [@customForm.textArea name="liaisonInstitution.powb.expectedEfforts" help="liaisonInstitution.powb.expectedEfforts.help" required=true className="limitWords-100" editable=editable /]
          </div>
          [/#if]
          
          [#-- Countries contribuions --]
          [#if PMU]
          <div class="form-group">
            <h5 class="sectionSubTitle">[@s.text name="powbCollIntegration.countriesContributions" /]</h5>
            <table class="">
              <thead>
                <tr>
                  <th>Country</th>
                  <th>Projects</th>
                  <th>Funding Sources</th>
                </tr>
              </thead>
              <tbody>
                [#list 1..4 as country]
                <tr>
                  <td><i class="flag-sm flag-sm-VN"></i> Vietnam</td>
                  <td>
                    [#list 100..103 as p]
                    <a href="">P${p}</a>
                    [/#list]
                  </td>
                  <td>
                    [#list 50..53 as fs]
                    <a href="">P${fs}</a>
                    [/#list]
                  </td>
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