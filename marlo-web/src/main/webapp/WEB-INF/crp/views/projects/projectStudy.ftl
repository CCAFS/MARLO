[#ftl]
[#assign title = "Cluster OICR" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${expectedID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [ "select2", "blueimp-file-upload", "flag-icon-css", "components-font-awesome"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectStudy.js?20240812",
  "${baseUrlCdn}/global/js/fieldsValidation.js",
  "${baseUrlCdn}/crp/js/feedback/feedbackAutoImplementation.js?20240313"
  ] 
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/projectStudies.css?20230106"
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
<input type="hidden"  name="expectedStudy.id" value="${(expectedStudy.id)!}" />
[#assign isOutcomeCaseStudy = ((expectedStudy.projectExpectedStudyInfo.studyType.id == 1)!false) && reportingActive/]
[#assign indexTab = 0]
[#if isOutcomeCaseStudy]
  <!--  <div class="container helpText viewMore-block">
    <div class="helpMessage infoText">
      <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-help.jpg" />
      <p class="col-md-10"> [@s.text name="study.help" /] </p>
    </div> 
    <div style="display:none" class="viewMore closed"></div>
  </div>  -->
  <div class="animated flipInX container  viewMore-block containerAlertMargin">
  <div class=" containerAlert alert-leftovers alertColorBackgroundInfo " id="containerAlert"> 
    <div class="containerLine alertColorInfo"></div>
    <div class="containerIcon">
      <div class="containerIcon alertColorInfo">
        <img src="${baseUrlCdn}/global/images/icon-question.png" />         
      </div>
    </div>
    <div class="containerText col-md-12 alertCollapse">
      <p class="alertText">
       [@s.text name="study.help" /]
      </p>
    </div>
    <div  class="viewMoreCollapse closed"></div>
  </div>  
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
          [#if (expectedStudy.projectExpectedStudyInfo.studyType?has_content) && (expectedStudy.projectExpectedStudyInfo.studyType.id == 1)]
            <h3 class="headTitle">[@s.text name="projectStudies.caseStudyInformationOICR" /]</h3>
          [#else]  
            <h3 class="headTitle">[@s.text name="projectStudies.caseStudyInformation" /]</h3>
          [/#if]

          <div id="caseStudiesBlock" class="">

            [#-- General: Component were the information is always visible --]
            [@studies.studyGeneral element=(expectedStudy)!{} name="expectedStudy" index=0  /]

            [#-- Content: All the information of the case study --]
            <input id="indexTab" name="indexTab" type="hidden" value="${(indexTab)!0}">

            <div class="studiesTab">
              [#-- Tab navigation --]
              [#if expectedStudy.projectExpectedStudyInfo.studyType.id == 1]
                <ul class="nav nav-tabs" role="tablist">
                  [#assign isOicrGeneralInformationComplete = action.isOicrGeneralInformationComplete()!false /]
                  <li role="presentation" class="[#if indexTab==1 || indexTab==0]active[/#if] col-md-3 ${isOicrGeneralInformationComplete?then('submitted','toSubmit')}"><a index="1" href="#study-generalInformation" aria-controls="info" role="tab" data-toggle="tab">[@s.text name="study.general.generalInformation" /]</a></li>

                  [#assign isAllianceContribution = false /]
                  [#list expectedStudy.centers as center]
                  [#if center.institution.id == 7320]
                    [#assign isAllianceContribution = true /]
                    [#break /]
                  [/#if]
                  [/#list]
                  
                  [#assign isOicrAllianceAlignmentComplete = action.isOicrAllianceAlignmentComplete()!false /]
                  <li role="presentation" class="[#if indexTab==2]active[/#if] col-md-3 ${isOicrAllianceAlignmentComplete?then('submitted','toSubmit')}" style="display:${isAllianceContribution?then('block','none')}" id="allianceTab"><a index="2" href="#study-alliance" aria-controls="metadata" role="tab" data-toggle="tab">[@s.text name="study.general.allianceAlignment" /]</a></li>

                  [#assign isOicrOneCgiarAlignmentComplete = action.isOicrOneCgiarAlignmentComplete()!false /]
                  <li role="presentation" class="[#if indexTab==3]active[/#if] col-md-3 ${isOicrOneCgiarAlignmentComplete?then('submitted','toSubmit')}"><a index="3" href="#study-onecgiar" aria-controls="quality" role="tab" data-toggle="tab">[@s.text name="study.general.oneCGIARAlignment" /]</a></li>

                  [#assign isOicrCommunicationsComplete = action.isOicrCommunicationsComplete()!false /]  
                  <li role="presentation" class="[#if indexTab==4]active[/#if] col-md-3 ${isOicrCommunicationsComplete?then('submitted','toSubmit')}"><a index="4" href="#study-communications" aria-controls="communications" role="tab" data-toggle="tab">[@s.text name="study.general.communications" /]</a></li>
                </ul>
              [/#if]

              [#-- Tab content --]
              <div class="tab-content ">

                <div id="study-generalInformation" role="tabpanel" class="tab-pane fade [#if indexTab==1 || indexTab==0]in active[/#if]">
                  [@studies.studyMacro element=(expectedStudy)!{} name="expectedStudy" index=0  /]
                </div>

                <div id="study-alliance" role="tabpanel" class="tab-pane fade [#if indexTab==2]in active[/#if]">
                  Alliance
                </div>

                <div id="study-onecgiar" role="tabpanel" class="tab-pane fade [#if indexTab==3]in active[/#if]">
                  [@studies.studyOneCGIAR element=(expectedStudy)!{} name="expectedStudy" index=0  /]
                </div>

                <div id="study-communications" role="tabpanel" class="tab-pane fade [#if indexTab==4]in active[/#if]">
                  [@studies.studyCommunications element=(expectedStudy)!{} name="expectedStudy" index=0  /]
                </div>
              </div>
                
            </div>

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