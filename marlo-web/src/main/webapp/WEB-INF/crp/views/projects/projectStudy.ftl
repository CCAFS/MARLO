[#ftl]
[#assign title = "Project Study" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${expectedID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [ "select2", "blueimp-file-upload", "flag-icon-css", "components-font-awesome"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectStudy.js?20220322A",
  "${baseUrlCdn}/global/js/autoSave.js",
  "${baseUrlCdn}/global/js/fieldsValidation.js"
  ] 
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/projectStudies.css?20211213A",
  "${baseUrlCdn}/global/css/global.css?20211126a"
  ] 
/]

[#assign currentSection = "projects" /]
[#assign currentStage = "projectStudies" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"caseStudies", "nameSpace":"/projects", "action":"${(crpSession)!}/studies", "param": "projectID=${projectID}"},
  {"label":"caseStudy", "nameSpace":"/projects", "action":""}
] /]

[#assign params = {
  "caseStudies": {"id":"caseStudiesName", "name":"project.caseStudies"}
  }
/] 

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/studiesTemplates.ftl" as studies /]

[#-- Helptext --]
[@utilities.helpBox name="study.help" /]

<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/projects/messages-caseStudy.ftl" /]
        
        [@s.form action=actionName cssClass="pure-form" enctype="multipart/form-data" ] 

          <span id="actualCrpID" style="display: none;">${(action.getCurrentCrp().id)!-1}</span>
          <span id="actualPhase" style="display: none;">${(action.isSelectedPhaseAR2021())?c}</span>
          <span id="isOICR" style="display: none;">${(((expectedStudy.projectExpectedStudyInfo.studyType.id == 1)!false) && (reportingActive || upKeepActive))?c}</span>
          <span id="studyID" style="display: none;">${(expectedStudy.id)!-1}</span>
          <span id="isSubmitted" style="display: none;">${submission?c}</span>
          [#assign actualPhaseAR2021 = action.isSelectedPhaseAR2021()!false]
          [#assign isOutcomeCaseStudy = (((expectedStudy.projectExpectedStudyInfo.studyType.id == 1)!false) && (reportingActive || upKeepActive))!false]
          [#assign isQAIncluded = action.isIndicatorIncludedInQA(isOutcomeCaseStudy?then('oicr', 'melia'), (expectedStudy.id)!-1, (action.getActualPhase().id)!-1)!false]

          [#if actualPhaseAR2021 && isQAIncluded]
            <div class="containerTitleElementsProject">
              <div class="containerTitleMessage">
                <div id="qualityAssessedIcon" class="pendingForReview-mode text-center animated flipInX" style="height: auto;">
                  <p>
                    [@s.text name="annualReport2018.policies.table2.pendingForReview"][/@s.text]
                  </p>
                </div> 
              </div>
            </div>
          [/#if] 

          [#-- Back --]
          <small class="pull-right">
            <a href="[@s.url action='${crpSession}/studies'][@s.param name="projectID" value=project.id /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              <span class="glyphicon glyphicon-circle-arrow-left"></span> [@s.text name="projectStudies.backProjectStudies" /]
            </a>
          </small>
          
          [#-- Outcome case studies list --]
          <h3 class="headTitle">[@s.text name="projectStudies.caseStudyInformation" /]</h3>
          
          <div id="caseStudiesBlock" class="">
            [@studies.studyMacro element=(expectedStudy)!{} name="expectedStudy" index=0  /]
          </div> 
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/projects/buttons-projectOutcomesCaseStudies.ftl" /]
         
        [/@s.form]
  
      </div>
    </div>  
</section>

[#-- Internal parameters --]
[#list params?keys as prop]<input id="${params[prop].id}" type="hidden" value="${params[prop].name}" />[/#list]

[#include "/WEB-INF/global/pages/footer.ftl"]