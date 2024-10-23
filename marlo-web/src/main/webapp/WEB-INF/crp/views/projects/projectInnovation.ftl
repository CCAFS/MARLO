[#ftl]
[#assign title = "Innovations" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${innovationID}-phase-${(actualPhase.id)!}" /]
[#-- TODO: Remove unused pageLibs--]
[#assign pageLibs = ["select2","font-awesome", "flag-icon-css"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectInnovations.js?20220707B",
  "${baseUrlCdn}/global/js/fieldsValidation.js",
  "${baseUrlCdn}/crp/js/feedback/feedbackAutoImplementation.js?20231017"
] /]
[#assign customCSS = ["${baseUrlMedia}/css/projects/projectInnovations.css?20240517"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "innovations" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"text":"C${project.id}", "nameSpace":"/projects", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
  {"label":"innovationsList", "nameSpace":"/projects", "action":"${(crpSession)!}/innovationsList" ,"param":"projectID=${projectID}"},
  {"label":"innovationInformation", "nameSpace":"/projects", "action":""}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/innovationTemplates.ftl" as innovations /]


<span id="parentID" style="display: none;">${innovationID!}</span>
<span id="phaseID" style="display: none;">${phaseID!}</span>
<span id="userID" style="display: none;">${currentUser.id!}</span>
<span id="projectID" style="display: none;">${projectID!}</span>
<span id="userCanManageFeedback" style="display: none;">${(action.canManageFeedback(projectID)?c)!}</span>
<span id="userCanLeaveComments" style="display: none;">${(action.canLeaveComments()?c)!}</span>
<span id="userCanApproveFeedback" style="display: none;">${(action.canApproveComments(projectID)?c)!}</span>
<span id="canTrackComments" style="display: none;">${(action.canTrackComments()?c)!}</span>
<span id="isFeedbackActive" style="display: none;">${(action.hasSpecificities('feedback_active')?c)!}</span>
<input type="hidden" id="sectionNameToFeedback" value="innovation" />

[#assign geographicScopeList = (element.geographicScopes)![] ]
[#assign isGlobal =        findElementID(geographicScopeList,  action.reportingIndGeographicScopeGlobal) /]
[#assign isRegional =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeRegional) /]
[#assign isMultiNational = findElementID(geographicScopeList,  action.reportingIndGeographicScopeMultiNational) /]
[#assign isNational =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeNational) /]
[#assign isSubNational =   findElementID(geographicScopeList,  action.reportingIndGeographicScopeSubNational) /]

[#if action.hasSpecificities('feedback_active') ]
  [#list feedbackComments as feedback]
    [@customForm.qaPopUpMultiple fields=feedback.qaComments name=feedback.fieldDescription index=feedback_index canLeaveComments=(action.canLeaveComments()!false)/]
  [/#list]
  <div id="qaTemplate" style="display: none">
    [@customForm.qaPopUpMultiple canLeaveComments=(action.canLeaveComments()!false) template=true/]
  </div>
[/#if]


[#-- Helptext --]
[@utilities.helpBox name="projectInnovations.help" /]

<section class="container">
  <div class="row">
    [#-- Project Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
    </div>
    [#-- Project Section Content --]
    <div class="col-md-9">
      [#-- Section Messages --]
      [#include "/WEB-INF/crp/views/projects/messages-innovation.ftl" /]

      [#-- Back --]
      <small class="pull-right">
        <a href="[@s.url action='${crpSession}/innovationsList'][@s.param name="projectID" value=project.id /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
          <span class="glyphicon glyphicon-circle-arrow-left"></span> [@s.text name="projectInnovations.back" /]
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
      
      [#--  Innovation Title --]
      <h3 class="headTitle">[@s.text name="projectInnovations" /]</h3> 

      [@s.form action=actionName cssClass="pure-form" enctype="multipart/form-data" ]
        
        [#assign isProgressActive = action.isProgressActive() /]

        [#-- Innovation Description --]
        [@innovations.innovationDescription element=(innovation)!{} name="innovation" index=0 /]          

        [#-- Innovation General --]
        [@innovations.innovationGeneral element=(innovation)!{} name="innovation" index=0 /]          
      
      [#-- Section Buttons & hidden inputs--]
      [#include "/WEB-INF/crp/views/projects/buttons-innovation.ftl" /]
        
      [/@s.form] 
  </div>  
</section>

[#include "/WEB-INF/global/pages/footer.ftl"]

[#function findElementID list id]
  [#list (list)![] as item]
    [#if (item.repIndGeographicScope.id == id)!false][#return true][/#if]
  [/#list]
  [#return false]
[/#function]