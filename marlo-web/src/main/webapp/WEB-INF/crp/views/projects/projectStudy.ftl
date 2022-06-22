[#ftl]
[#assign title = "Project Study" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${expectedID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [ "select2", "blueimp-file-upload", "flag-icon-css", "components-font-awesome"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectStudy.js?20220622A",
  "${baseUrlCdn}/global/js/autoSave.js",
  "${baseUrlCdn}/global/js/fieldsValidation.js",
  "${baseUrlCdn}/crp/js/feedback/feedbackAutoImplementation.js?20220622A"
  ] 
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/projectStudies.css"
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

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/studiesTemplates.ftl" as studies /]
[#if action.hasSpecificities('feedback_active') ]
  [#list feedbackComments as feedback]
    [@customForm.qaPopUpMultiple fields=feedback.qaComments name=feedback.fieldDescription index=feedback_index canLeaveComments=(action.canLeaveComments()!false)/]
  [/#list]
  <div id="qaTemplate" style="display: none">
    [@customForm.qaPopUpMultiple canLeaveComments=(action.canLeaveComments()!false) template=true/]
  </div>
[/#if]

[#assign isOutcomeCaseStudy = ((expectedStudy.projectExpectedStudyInfo.studyType.id == 1)!false) && reportingActive/]
[#if isOutcomeCaseStudy]
  <div class="container helpText viewMore-block">
    <div class="helpMessage infoText">
      <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-help.jpg" />
      <p class="col-md-10"> [@s.text name="study.help" /] </p>
    </div> 
    <div style="display:none" class="viewMore closed"></div>
  </div>
[/#if]

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

          [#-- Back --]
          <small class="pull-right">
            <a href="[@s.url action='${crpSession}/studies'][@s.param name="projectID" value=project.id /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              <span class="glyphicon glyphicon-circle-arrow-left"></span> [@s.text name="projectStudies.backProjectStudies" /]
            </a>
          </small>

          [#if action.hasSpecificities('feedback_active') ]
            <div class="form-group col-md-12 legendContent-global">
              <div class="colors-global">
                <div class="col-md-12 form-group "><b>Feedback status:</b></div>
                <div class="color col-md-4"><img src="${baseUrlCdn}/global/images/comment.png" class="qaCommentStatus feedbackStatus">[@s.text name="feedbackStatus.blue" /]</div>
                <div class="color col-md-4"><img src="${baseUrlCdn}/global/images/comment_yellow.png" class="qaCommentStatus feedbackStatus">[@s.text name="feedbackStatus.yellow" /]</div>
                <div class="color col-md-4"><img src="${baseUrlCdn}/global/images/comment_green.png" class="qaCommentStatus feedbackStatus">[@s.text name="feedbackStatus.green" /]</div>
              </div>
            </div>
          [/#if]
          
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