[#ftl]
[#assign title = "Innovations" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${innovationID}-phase-${(actualPhase.id)!}" /]
[#-- TODO: Remove unused pageLibs--]
[#assign pageLibs = ["select2","font-awesome", "flag-icon-css", "datatables.net", "datatables.net-bs"] /]
[#assign customJS = [
  "${baseUrlCdn}/global/js/sortableList.js?20250604",
  "${baseUrlMedia}/js/projects/projectInnovations.js?20250805-A",
  "${baseUrlCdn}/global/js/fieldsValidation.js?20250805",
  "${baseUrlCdn}/crp/js/feedback/feedbackAutoImplementation.js?20250717"
] /]

[#assign moduleJS = [
  "${baseUrlMedia}/js/dist/components/my-component.js",
  "${baseUrlMedia}/js/dist/components/mal-multiselect.js",
  "${baseUrlMedia}/js/dist/components/mal-select.js"
] /]



[#assign customCSS = [
  "${baseUrlMedia}/css/projects/projectInnovations.css?20250801",
  "${baseUrlCdn}/global/css/customDataTable.css?20250801-A"
] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "innovations" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"text":"C${project.id}", "nameSpace":"/projects", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
  {"label":"innovationsList", "nameSpace":"/projects", "action":"${(crpSession)!}/innovationsList" ,"param":"projectID=${projectID}"},
  {"label":"innovationInformation", "nameSpace":"/projects", "action":""}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#import "/WEB-INF/global/macros/deliverableMacros.ftl" as deliverableMacros /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/innovationTemplates.ftl" as innovations /]


<span id="parentID" style="display: none;">${innovationID!}</span>
<span id="phaseID" style="display: none;">${phaseID!}</span>
<span id="userID" style="display: none;">${currentUser.id!}</span>
<span id="projectID" style="display: none;">${projectID!}</span>
<span id="userCanManageFeedback" style="display: none;">${(action.canManageFeedback(projectID)?c)!}</span>
<span id="userCanLeaveComments" style="display: none;">${(action.canLeaveComments(projectID)?c)!}</span>
<span id="userCanApproveFeedback" style="display: none;">${(action.canApproveComments(projectID)?c)!}</span>
<span id="canTrackComments" style="display: none;">${(action.canTrackComments()?c)!}</span>
<span id="isFeedbackActive" style="display: none;">${(action.hasSpecificities('feedback_active')?c)!}</span>
<span id="isFeedbackNewCommentFieldActive" style="display: none;">${(action.hasSpecificities('feedback_new_comment_field_active')?c)!"false"}</span>
<span id="isSuperAdmin" style="display: none;">${(action.canAccessSuperAdmin()?c)!}</span>

<input type="hidden" id="sectionNameToFeedback" value="innovation" />

[#assign indexTab = 0]

[#assign geographicScopeList = (element.geographicScopes)![] ]
[#assign isGlobal =        findElementID(geographicScopeList,  action.reportingIndGeographicScopeGlobal) /]
[#assign isRegional =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeRegional) /]
[#assign isMultiNational = findElementID(geographicScopeList,  action.reportingIndGeographicScopeMultiNational) /]
[#assign isNational =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeNational) /]
[#assign isSubNational =   findElementID(geographicScopeList,  action.reportingIndGeographicScopeSubNational) /]

[#if action.hasSpecificities('feedback_active') ]
  [#list feedbackComments as feedback]
    [@customForm.qaPopUpMultiple fields=feedback.qaComments name=feedback.fieldDescription index=feedback_index canLeaveComments=(action.canLeaveComments(projectID)!false)/]
  [/#list]
  <div id="qaTemplate" style="display: none">
    [@customForm.qaPopUpMultiple canLeaveComments=(action.canLeaveComments(projectID)!false) template=true/]
  </div>
[/#if]


[#-- Helptext - DEPRECATED - NEW VERSION IN SYSTEM --]
[#-- [@utilities.helpBox name="projectInnovations.generalInformation.help" /] --]

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

      [@s.form action=actionName cssClass="pure-form" enctype="multipart/form-data" ]
        
        [#assign isProgressActive = action.isProgressActive() /]

        [#assign isAllianceContribution = false /]
        [#if innovation.centers?size > 0]
          [#list innovation.centers as center]

          [#if (center.institution??)&&(center.institution.name??)&&(center.institution.name?lower_case?contains("alliance"))]
            [#assign isAllianceContribution = true /]
            [#break /]
          [/#if]
          [/#list]
        [/#if]

        [#-- Back --]
        <small class="pull-right">
          <a href="[@s.url action='${crpSession}/innovationsList'][@s.param name="projectID" value=project.id /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
            <span class="glyphicon glyphicon-circle-arrow-left"></span> [@s.text name="projectInnovations.back" /]
          </a>
        </small>

        [#--  Innovation Title --]
        <h3 class="headTitle">[@s.text name="projectInnovations" /]</h3> 


        [#-- Innovation Description --]
        [@innovations.innovationDescription element=(innovation)!{} name="innovation" index=0 /]          

        [#-- Content: All the information of the case study --]
        <input id="indexTab" name="indexTab" type="hidden" value="${(indexTab)!0}">
        
        <div class="innovationTabs">
          [#-- Tabs Navigation --]
          <ul class="nav nav-tabs" role="tablist">

            [#assign isInnovationGeneralInformationComplete = (action.isInnovationGeneralInformationComplete())!false /]
            <li role="presentation" style="width:${isAllianceContribution?then('20%','25%')}" class="[#if indexTab==1 || indexTab==0]active[/#if] col-md ${isInnovationGeneralInformationComplete?then('submitted','toSubmit')}">
              <a href="#innovationGeneral" role="tab" data-toggle="tab">[@s.text name="projectInnovations.tab.general" /]</a>
            </li>

            [#assign isInnovationAllianceAlignmentComplete = (action.isInnovationAllianceAlignmentComplete())!false /]
            <li role="presentation" style="display: ${isAllianceContribution?then('block','none')}; width:${isAllianceContribution?then('20%','25%')}; " class="[#if indexTab==2]active[/#if] col-md ${isInnovationAllianceAlignmentComplete?then('submitted','toSubmit')}" id="allianceTab">
              <a href="#innovationAlliance" role="tab" data-toggle="tab">[@s.text name="projectInnovations.tab.allianceAlignment" /]</a>
            </li>

            [#assign isInnovationOneCgiarAlignmentComplete = (action.isInnovationOneCgiarAlignmentComplete())!false /]
            <li role="presentation" style="width:${isAllianceContribution?then('20%','25%')}" class="[#if indexTab==3]active[/#if] col-md ${isInnovationOneCgiarAlignmentComplete?then('submitted','toSubmit')}">
              <a href="#innovationOneCGIAR" role="tab" data-toggle="tab">[@s.text name="projectInnovations.tab.oneCGIARAlignment" /]</a>
            </li>

            [#assign isInnovationBundleComplete = (action.isInnovationBundleComplete())!false /]
            <li role="presentation" style="width:${isAllianceContribution?then('20%','25%')}" class="[#if indexTab==4]active[/#if] col-md ${isInnovationBundleComplete?then('submitted','toSubmit')}">
              <a href="#innovationBundle" role="tab" data-toggle="tab">[@s.text name="projectInnovations.tab.bundleComposition" /]</a>
            </li>

            [#assign isInnovationRightsComplete = (action.isInnovationRightsComplete())!false /]
            <li role="presentation" style="width:${isAllianceContribution?then('20%','25%')}" class="[#if indexTab==5]active[/#if] col-md ${isInnovationRightsComplete?then('submitted','toSubmit')}">
              <a href="#innovationSharing" role="tab" data-toggle="tab">[@s.text name="projectInnovations.tab.innovationSharing" /]</a>
            </li>
          </ul>

          [#-- Tabs Content --]
          <div class="tab-content">
            <div role="tabpanel" class="tab-pane fade [#if indexTab==1 || indexTab==0]in active[/#if]" id="innovationGeneral">
              [#-- Innovation General --]
              [@innovations.innovationGeneral element=(innovation)!{} name="innovation" index=0 /]
            </div>
            <div role="tabpanel" class="tab-pane fade [#if indexTab==2]in active[/#if]" id="innovationAlliance">
              [#-- Innovation Alliance Aligment--]
              [@innovations.innovationAlliance element=(innovation)!{} name="innovation" index=0 /]
            </div>
            <div role="tabpanel" class="tab-pane fade [#if indexTab==3]in active[/#if]" id="innovationOneCGIAR">
              [#-- Innovation OneCGIAR Aligment--]
              [@innovations.innovationOneCGIAR element=(innovation)!{} name="innovation" index=0 /]
            </div>
            <div role="tabpanel" class="tab-pane fade [#if indexTab==4]in active[/#if]" id="innovationBundle">
              [#-- Innovation Bundle Composition --]
              [@innovations.innovationBundleComposition element=(innovation)!{} name="innovation" index=0 /]
            </div>
            <div role="tabpanel" class="tab-pane fade [#if indexTab==5]in active[/#if]" id="innovationSharing">
              [#-- Innovation IP Rights and Funding --]
              [@innovations.innovationSharing element=(innovation)!{} name="innovation" index=0 /]
            </div>
          </div>

        </div>
        
      
      [#-- Section Buttons & hidden inputs--]
      [#include "/WEB-INF/crp/views/projects/buttons-innovation.ftl" /]
        
      [/@s.form] 
  </div>  
</section>

[#-- Partner users TEMPLATE --]
<div id="partnerUsers" style="display:none">
  [#list partners as partner]
    <div class="institution-${partner.institution.id}">
      [#assign usersList = (action.getUserList(partner.institution.id))![]]
      <div class="users-1">
        [#list usersList as user]
          [@deliverableMacros.deliverableUserMacro element={} user=user index=user_index name="innovation.partnerships[0].partnershipPersons" isUserChecked=false isResponsable=true /]
        [/#list]
      </div>
    </div>
  [/#list]
</div>

[#include "/WEB-INF/global/pages/footer.ftl"]

[#function findElementID list id]
  [#list (list)![] as item]
    [#if (item.repIndGeographicScope.id == id)!false][#return true][/#if]
  [/#list]
  [#return false]
[/#function]