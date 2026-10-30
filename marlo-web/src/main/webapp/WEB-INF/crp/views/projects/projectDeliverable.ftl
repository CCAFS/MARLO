[#ftl]
[#assign title = "Deliverable information" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${deliverableID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2","font-awesome","dropzone","blueimp-file-upload","jsUri", "flag-icon-css", "pickadate", "vue"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/deliverables/deliverableInfo.js?20261021",
  "${baseUrlMedia}/js/projects/deliverables/deliverableShfrm.js?20240711",
  "${baseUrlMedia}/js/projects/deliverables/deliverableDissemination.js?20261021",
  "${baseUrlMedia}/js/projects/deliverables/deliverableQualityCheck.js?20220721",
  "${baseUrlCdn}/crp/js/feedback/feedbackAutoImplementation.js?20260929",
  [#--  "${baseUrlMedia}/js/projects/deliverables/deliverableDataSharing.js?20180523",--]
  [#--  "${baseUrlCdn}/global/js/autoSave.js",--]
  "${baseUrlCdn}/global/js/fieldsValidation.js?20180529",
  "${baseUrlMedia}/js/projects/deliverables/deliverableFormRedesign.js?20261030-3"
] /]
[#assign customCSS = [
  "${baseUrl}/crp/css/projects/projectDeliverable.css?20240729",
  "${baseUrlCdn}/global/css/404.css?20240523",
  "${baseUrl}/crp/css/projects/deliverableForm.css?20261030-5"
  ] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "deliverableList" /]
[#assign hideJustification = true /]
[#-- The deliverable and cluster chips are drawn in the form header instead of the shared top quote --]
[#assign hideTopQuote = true /]
[#-- Read by deliverableMacros.ftl: parts drawn only in the redesigned form (the publication form shares the macros).
     A global, as the macros are an imported library and do not see this template's assigns. --]
[#global deliverableFormRedesign = true /]
[#-- "project" / "cluster" / "team", per global unit --]
[#assign sectionNoun = (action.getText("projects.menu.status.noun"))!"project" /]
[#-- Dissemination and Quality check only exist in reporting or upkeep, and only with dissemination on --]
[#assign showDisseminationTabs = ((reportingActive || actualPhase.upkeep) && action.hasSpecificities("crp_has_disemination"))!false /]
[#-- AICCRA III layout: Participants/Trainees is a General information section, not a Dissemination question --]
[#assign participantsInGeneralInfo = action.hasSpecificities("deliverable_participants_general_info_active") /]

[#if !action.isAiccra()]
  [#assign currentSection = "projects" /]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"${currentSection}", "action":"${(crpSession)!}/projectsList"},
    {"text":"P${project.id}", "nameSpace":"${currentSection}", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
    {"label":"deliverableList", "nameSpace":"${currentSection}", "action":"${(crpSession)!}/deliverableList" ,"param":"projectID=${projectID}"},
    {"label":"deliverableInformation", "nameSpace":"${currentSection}", "action":""}
  ]/]
[#else]
  [#assign currentSection = "clusters" /]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"${currentSection}", "action":"${(crpSession)!}/projectsList"},
    {"text":"C${project.id}", "nameSpace":"${currentSection}", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
    {"label":"deliverableList", "nameSpace":"${currentSection}", "action":"${(crpSession)!}/deliverableList" ,"param":"projectID=${projectID}"},
    {"label":"deliverableInformation", "nameSpace":"${currentSection}", "action":""}
  ]/]
[/#if]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/deliverableListTemplate.ftl" as deliverableList /]
[#import "/WEB-INF/global/macros/deliverableMacros.ftl" as deliverableMacros /]

[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

<!-- 
<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-help.jpg" />
    <p class="col-md-10">[#if reportingActive] [@s.text name="project.deliverable.help2" /] [#else] [@s.text name="project.deliverable.help1" /] [/#if] </p>
  </div>
  <div style="display:none" class="viewMore closed"></div>
</div>
-->

[#if action.hasSpecificities('deliverable_completed_in_previous_phases_active') && completeInPreviousPhase]
  <div class="animated flipInX container  viewMore-block containerAlertMargin">
    <div class=" containerAlert  alert-leftovers alertColorBackgroundWarning"  id="containerAlert">
      <div class="containerLine alertColorWarning"></div>
      <div class="containerIcon">
        <div class="containerIcon alertColorWarning">
          <img src="${baseUrlCdn}/global/images/icon-alert.png" />      
        </div>
      </div>
      <div class="containerText col-md-12">
        <p class="alertText"> [@s.text name="project.deliverable.completePreviousPhase" /]</p>
      </div>
    </div>
  </div>
[/#if]

[#if !((deliverable.deliverableInfo.id??)!false)]
  [#include "/WEB-INF/crp/views/projects/availability-projects.ftl" /]
[#else]

<!--  [@customForm.qaPopUp /]  -->

<span id="parentID" style="display: none;">${deliverableID!}</span>
<span id="phaseID" style="display: none;">${phaseID!}</span>
<span id="userID" style="display: none;">${currentUser.id!}</span>
<span id="projectID" style="display: none;">${projectID!}</span>
<span id="userCanManageFeedback" style="display: none;">${(action.canManageFeedback(projectID!-1)?c)!"false"}</span>
<span id="userCanLeaveComments" style="display: none;">${(action.canLeaveComments(projectID!)?c)!"false"}</span>
<span id="userCanApproveFeedback" style="display: none;">${(action.canApproveComments(projectID!-1)?c)!"false"}</span>
<span id="canTrackComments" style="display: none;">${(action.canTrackComments()?c)!"false"}</span>
<span id="isFeedbackActive" style="display: none;">${(action.hasSpecificities('feedback_active')?c)!"false"}</span>
<span id="isFeedbackNewCommentFieldActive" style="display: none;">${(action.hasSpecificities('feedback_new_comment_field_active')?c)!"false"}</span>
<span id="isSuperAdmin" style="display: none;">${(action.canAccessSuperAdmin()!false)?c!"false"}</span>
<input type="hidden" id="sectionNameToFeedback" value="deliverable" />


<span id="existCurrentCluster" style="display: none;">${(existCurrentCluster?c)!}</span>


[#if action.hasSpecificities('feedback_active') ]
  [#list feedbackComments as feedback]
    [@customForm.qaPopUpMultiple fields=feedback.qaComments name=feedback.fieldDescription index=feedback_index canLeaveComments=(action.canLeaveComments(projectID!)!false)/]
  [/#list]
  <div id="qaTemplate" style="display: none">
    [@customForm.qaPopUpMultiple canLeaveComments=(action.canLeaveComments(projectID!)!false) template=true/]
  </div>
[/#if]



<section class="container">

  <div class="modal-deliverable ui-dialog modal-evidences" style="display: none; background-color: #50505070 !important;">
    <div class="content-modal">
      <div class="ui-dialog-titlebar">
        <p class="title-modal-evidences">Remove cluster</p>
        <div class="button-exit close-modal-evidences">
          <div class="x-close-modal" ></div>
        </div>
      </div>

      
      <div class="text-modal-evidences">
        <p>It is not possible to remove the shared cluster from the list, this cluster has already been submitted or/and has trainees related.</p>
        <br>
        <p>We suggest the following actions so you can save the information correctly:</p>
        <ul>
          <li>Distribute the remaining values across the shared cluster list</li>
          <li>Contact the cluster leader to unsubmit the cluster and update the information.</li>
        </ul>       
      </div>                
      <div class="container-buttons-evidences"> 
        <div class="button-close-modal close-modal-evidences">
          <p>Close</p>
        </div>
      </div> 
    </div>              
  </div>

  <div class=" modal-deliverable ui-dialog modal-indicator" style="display: none;  background-color: #50505070 !important;">
    <div class="content-modal">
      <div class="ui-dialog-titlebar">
        <p class="title-modal-evidences title-modal-indicator">Remove Indicator</p>
        <div class="button-exit close-modal-indicator">
          <div class="x-close-modal" ></div>
        </div>
      </div>
      
      <div class="text-modal-evidences text-modal-indicator">
        <p>It is not possible to remove the indicator from the list, since this deliverable has shared clusters that have already been submitted and have related trainees.</p>

      </div>                
      <div class="container-buttons-evidences"> 
        <div class="button-close-modal close-modal-indicator">
          <p>Close</p>
        </div>
      </div> 
    </div>   
  </div>

  <div class=" modal-deliverable ui-dialog modal-status" style="display: none;  background-color: #50505070 !important;">
    <div class="content-modal">
      <div class="ui-dialog-titlebar">
        <p class="title-modal-evidences title-modal-status">Change Status</p>
        <div class="button-exit close-modal-indicator">
          <div class="x-close-modal" ></div>
        </div>
      </div>
      
      <div class="text-modal-evidences text-modal-status">
        <p>You should not be allowed to change the status of a deliverable to cancel or extend if there is already related information of Trainees and/or if one of the shared clusters is already submitted. Made the following recommendations:</p>
        <ul>
          <li>Unsubmit the shared cluster(s)</li>
          <li>Remove the trainees related information to the shared cluster(s)</li>
        </ul>
      </div>                
      <div class="container-buttons-evidences"> 
        <div class="button-close-modal close-modal-status">
          <p>Close</p>
        </div>
      </div> 
    </div>   
  </div>

    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/projects/messages-deliverables.ftl" /]

        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]       
        
        [#-- is-readonly: closed phase or no edit permission; deliverableForm.css makes every control inert --]
        <div class="dlf[#if !editable] is-readonly[/#if]">

          [#-- Back to the list --]
          <a class="dlf-back" href="[@s.url action='${crpSession}/deliverableList'][@s.param name="projectID" value=project.id /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
            <svg width="12" height="12" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M8 2.5 4.5 6 8 9.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"></path></svg>
            [@s.text name="project.deliverable.redesign.back"][@s.param]${sectionNoun}[/@s.param][/@s.text]
          </a>

          [#-- Is deliverable complete: the last saved validation of the section, drawn as the header chip --]
          [#assign isDeliverableComplete = action.isDeliverableComplete(deliverable.id, actualPhase.id) /]

          [#-- Deliverable and cluster chips, the required fields state, and the feedback status legend --]
          <div class="dlf-chips">
            <span class="dlf-chip dlf-chip--id" title="${(deliverable.deliverableInfo.title)!}">D${deliverable.id?c}</span>
            <a class="dlf-chip dlf-chip--owner" href="[@s.url namespace="/projects" action='${crpSession}/description'][@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]"
              title="[#if action.isAiccra()]C${project.id?c} - [/#if]${(project.projectInfo.title)!}">
              <span class="dlf-chip__avatar" aria-hidden="true">${sectionNoun?cap_first?substring(0, 1)}</span>
              [#if !action.isAiccra()]P${project.id?c}[#elseif (project.acronym)?has_content]${project.acronym}[#else]C${project.id?c}[/#if]
            </a>
            <span class="dlf-chip dlf-chip--fields ${isDeliverableComplete?string('is-ok', 'is-warn')}">
              <span class="dlf-chip__dot" aria-hidden="true"></span>
              [#if isDeliverableComplete][@s.text name="project.deliverable.redesign.fields.complete" /][#else][@s.text name="project.deliverable.redesign.fields.missing" /][/#if]
            </span>
            [#if action.hasSpecificities('feedback_active')]
              <span class="dlf-feedbackLegend" role="note">
                <span class="dlf-feedbackLegend__title">[@s.text name="project.deliverable.redesign.feedbackStatus" /]</span>
                <span class="dlf-feedbackLegend__item"><img src="${baseUrlCdn}/global/images/comment.png" class="qaCommentStatus feedbackStatus" alt="" />[@s.text name="feedbackStatus.blue" /]</span>
                <span class="dlf-feedbackLegend__item"><img src="${baseUrlCdn}/global/images/comment_yellow.png" class="qaCommentStatus feedbackStatus" alt="" />[@s.text name="feedbackStatus.yellow" /]</span>
                <span class="dlf-feedbackLegend__item"><img src="${baseUrlCdn}/global/images/comment_green.png" class="qaCommentStatus feedbackStatus" alt="" />[@s.text name="feedbackStatus.green" /]</span>
              </span>
            [/#if]
          </div>

          [#-- Title, with the FAIR status of the deliverable --]
          <div class="dlf-head">
            <h1 class="dlf-head__title">[#if (deliverable.deliverableInfo.title)?has_content]${deliverable.deliverableInfo.title}[#else][@s.text name="project.deliverable.redesign.untitled" /][/#if]</h1>
            [#-- FAIR Compliant Mini: deliverableQualityCheck.js keeps the achieved / not-achieved classes current --]
            <div class="dlf-head__fair fairComplian-block" style="display:${deliverable.deliverableInfo.requeriedFair()?string('flex','none')}">
              <span class="dlf-head__fairLabel">[@s.text name="project.deliverable.redesign.fairStatus" /]</span>
              <div class="dlf-head__fairBoxes">
                [#-- Findable --]
                <div class="fairCompliant mini findable [#attempt][#if action.isF(deliverable.id)??][#if action.isF(deliverable.id)] achieved [#else] not-achieved [/#if][/#if][#recover][/#attempt]" title="[@s.text name="project.deliverable.quality.FLabel" /]"><div class="sign">F</div></div>
                [#-- Accessible --]
                <div class="fairCompliant mini accessible [#attempt][#if action.isA(deliverable.id)??][#if action.isA(deliverable.id)] achieved [#else] not-achieved [/#if][/#if][#recover][/#attempt]" title="[@s.text name="project.deliverable.quality.ALabel" /]"><div class="sign">A</div></div>
                [#-- Interoperable --]
                <div class="fairCompliant mini interoperable [#attempt][#if action.isI(deliverable.id)??][#if action.isI(deliverable.id)] achieved [#else] not-achieved [/#if][/#if][#recover][/#attempt]" title="[@s.text name="project.deliverable.quality.ILabel" /]"><div class="sign">I</div></div>
                [#-- Reusable --]
                <div class="fairCompliant mini reusable [#attempt][#if action.isR(deliverable.id)??][#if action.isR(deliverable.id)] achieved [#else] not-achieved [/#if][/#if][#recover][/#attempt]" title="[@s.text name="project.deliverable.quality.RLabel" /]"><div class="sign">R</div></div>
              </div>
            </div>
          </div>

          [#-- Instructions --]
          <div class="dlf-note" role="note">
            <span class="dlf-note__icon" aria-hidden="true">i</span>
            <p class="dlf-note__text">[#if reportingActive][@s.text name="project.deliverable.help2" /][#else][@s.text name="project.deliverable.help1" /][/#if]</p>
          </div>

          [#if action.hasSpecificities('duplicated_deliverables_functionality_active') && duplicated]
            <div class="dlf-note dlf-note--warn" role="note">
              <span class="dlf-note__icon" aria-hidden="true">!</span>
              <p class="dlf-note__text">[@s.text name="project.deliverable.duplicatedHelp" /]</p>
            </div>
          [/#if]

          [#-- Is deliverable new --]
          [#assign isDeliverableNew = action.isDeliverableNew(deliverable.id) /]

          [#-- Is deliverable in progress phase and is not completed --]
          [#assign validateIsProgressWithStatus = action.validateIsProgressWithStatus(deliverable.deliverableInfo.status)]

          <input id="indexTab" name="indexTab" type="hidden" value="${(indexTab)!0}">
          <div class="deliverableTabs dlf-tabs">
            [#--  Deliverable Menu  --]
            <ul class="nav nav-tabs" role="tablist" aria-label="[@s.text name="project.deliverable.redesign.tabs.label" /]">
                <li role="presentation" class="[#if indexTab==1 || indexTab==0]active[/#if]"><a index="1" href="#deliverable-mainInformation" aria-controls="info" role="tab" data-toggle="tab">[@s.text name="project.deliverable.generalInformation.titleTab" /]</a></li>

                [#if showDisseminationTabs]
                <li role="presentation" class="[#if indexTab==2]active[/#if]"><a index="2" href="#deliverable-disseminationMetadata" aria-controls="metadata" role="tab" data-toggle="tab">[@s.text name="project.deliverable.redesign.tab.dissemination" /]</a></li>

                [#assign isRequiredQuality = deliverable.deliverableInfo.requeriedFair() || ((action.hasDeliverableRule(deliverable.deliverableInfo, deliverableComplianceCheck))!false) /]
                <li role="presentation" class="[#if indexTab==3]active[/#if]" style="display:${isRequiredQuality?string('block','none')};"><a index="3" href="#deliverable-qualityCheck" aria-controls="quality" role="tab" data-toggle="tab">[@s.text name="project.deliverable.redesign.tab.quality" /]</a></li>

                [#--
                [#assign isRequiredDataSharing = (deliverable.dissemination.alreadyDisseminated)!false /]
                <li role="presentation" class="dataSharing [#if indexTab==4]active[/#if]" style="display:${isRequiredDataSharing?string('none','block')};"><a index="4" href="#deliverable-dataSharing" aria-controls="datasharing" role="tab" data-toggle="tab">Data Sharing</a></li>
                 --]
                [/#if]

            </ul>
              
            <div class="tab-content ">
              [#-- Deliverable Information --]
              <div id="deliverable-mainInformation" role="tabpanel" class="tab-pane fade [#if indexTab==1 || indexTab==0]in active[/#if]">
                [#include "/WEB-INF/crp/views/projects/deliverableInfo.ftl" /]
              </div>
              [#if showDisseminationTabs]
              [#-- Deliverable disseminationMetadata --]
              <div id="deliverable-disseminationMetadata" role="tabpanel" class="tab-pane fade [#if indexTab==2]in active[/#if]">
                <div class="dlf-sections">
                  [#-- ─── Dissemination ─── --]
                  <section class="dlf-card" id="dlf-dissemination">
                    <h2 class="dlf-card__title">[@s.text name="project.deliverable.redesign.section.dissemination" /]</h2>

                    [#-- Is this deliverable already disseminated? --]
                    <div class="dlf-question dlf-question--dissemination">
                      [@deliverableMacros.alreadyDisseminatedMacro /]
                    </div>

                    [#-- Is this deliverable Open Access? --]
                    <div class="dlf-question">
                      [@deliverableMacros.isOpenaccessMacro /]
                    </div>

                    [#-- Have you adopted a license?  --]
                    <div class="dlf-question">
                      [@deliverableMacros.deliverableLicenseMacro /]
                    </div>

                    [#--  Does this deliverable involve Participants and Trainees? (General information in the AICCRA III layout) --]
                    [#if !participantsInGeneralInfo]
                      <div class="dlf-question dlf-question--trainees">
                        [@deliverableMacros.deliverableParticipantsMacro /]
                      </div>
                    [/#if]
                  </section>

                  [#-- ─── Metadata (included publications) ─── --]
                  <section class="dlf-card" id="dlf-metadata">
                    <h2 class="dlf-card__title">[@s.text name="project.deliverable.dissemination.metadataSubtitle" /]</h2>
                    [@deliverableMacros.deliverableMetadataMacro validateIsProgressWithStatus=validateIsProgressWithStatus/]
                  </section>
                </div>
              </div>
              [#-- Deliverable qualityCheck --]
              <div id="deliverable-qualityCheck" role="tabpanel" class="tab-pane fade [#if indexTab==3]in active[/#if]">
                <div class="dlf-sections">
                  [#--  Database/Dataset/Data documentation -- Maps/Geospatial data: deliverableInfo.js toggles #complianceCheck by sub-category --]
                  <section class="dlf-card dlf-compliance" id="complianceCheck" style="display:${deliverableMacros.displayDeliverableRule(deliverable, deliverableComplianceCheck)!};">
                    <h2 class="dlf-card__title">[@s.text name="project.deliverable.quality.compliance" /]</h2>
                    [#-- Compliance check (Data products only) --]
                    [@deliverableMacros.complianceCheck /]
                  </section>

                  [#-- FAIR compliant: deliverableQualityCheck.js keeps each .fairCompliant row's achieved / not-achieved class current --]
                  <section class="dlf-card dlf-fairCard fairComplian-block" id="dlf-fair" style="display:${deliverable.deliverableInfo.requeriedFair()?string('flex','none')}">
                    <div class="dlf-fairCard__head">
                      <h2 class="dlf-card__title">[@s.text name="project.deliverable.quality.fairTitle" /]</h2>
                      <span class="dlf-fairCard__summary" data-dlf-fair-summary data-label="[@s.text name="project.deliverable.redesign.fair.summary" /]"></span>
                      <span class="dlf-fairCard__note">[@s.text name="project.deliverable.redesign.fair.calculated" /]</span>
                    </div>
                    <div class="dlf-fairCard__rows">
                      [@dlfFairRow key="findable" letter="F" points=["Fpoint1", "Fpoint2", "Fpoint3"] fix="project.deliverable.redesign.fair.fix.findable" target=".type-findable" /]
                      [@dlfFairRow key="accessible" letter="A" points=["Apoint1", "Apoint2", "Apoint3"] fix="project.deliverable.redesign.fair.fix.accessible" target=".type-accessible" /]
                      [@dlfFairRow key="interoperable" letter="I" points=["Ipoint1", "Ipoint2", "Ipoint3"] /]
                      [#-- Reusable lists the same two points the legacy block showed --]
                      [@dlfFairRow key="reusable" letter="R" points=["Ipoint1", "Ipoint2"] fix="project.deliverable.redesign.fair.fix.reusable" target=".type-license" /]
                    </div>
                  </section>
                </div>
              </div>
              [#-- Deliverable dataSharing
              <div id="deliverable-dataSharing" role="tabpanel" class="tab-pane fade [#if indexTab==4]in active[/#if]">
                [#include "/WEB-INF/crp/views/projects/deliverableDataSharing.ftl" /]
              </div>
              --]
              [/#if]
            </div>
           </div>
          [#-- Save bar: save state and the shared last-edit message on the left, the buttons on the right.
               deliverableFormRedesign.js moves the message out of the buttons include and tracks unsaved changes. --]
          <div class="dlf-saveBar">
            <div class="dlf-saveBar__info">
              [#if editable || editStatus]
                <span class="dlf-saveBar__state" role="status" aria-live="polite">
                  <span class="dlf-saveBar__dot" aria-hidden="true"></span>
                  <span data-dlf-save-state
                    data-saved="[@s.text name="project.deliverable.redesign.saveBar.saved" /]"
                    data-unsaved="[@s.text name="project.deliverable.redesign.saveBar.unsaved" /]">[@s.text name="project.deliverable.redesign.saveBar.saved" /]</span>
                </span>
              [/#if]
            </div>
            <div class="dlf-saveBar__actions">
              [#-- Section Buttons & hidden inputs--]
              [#include "/WEB-INF/crp/views/projects/buttons-deliverables.ftl" /]
            </div>
          </div>

        </div>

          [/@s.form]
      </div>
    </div>
</section>
[/#if]


[@deliverableMacros.projectPartnerMacro  element={} name="deliverable.shfrmPriorityActions[-1]" isTemplate=true /]
[@deliverableMacros.subactionsSelectsctrlList /]

[#-- ----------------------------------- Deliverable Type Rules ------------------------------------------------]
[#--  Publication Metadata
      49 -> Articles and Books --]
[@setDeliverableRule element=deliverable ruleName=(deliverablePublicationMetadata)!'' /]

[#--  Computer License
      52 -> Data portal/Tool/Model code/Computer software --]
[@setDeliverableRule element=deliverable ruleName=(deliverableComputerLicense)!'' /]

[#--  DataLicense
      52 -> Data portal/Tool/Model code/Computer software
      74 -> Maps/Geospatial data --]
[@setDeliverableRule element=deliverable ruleName=(deliverableDataLicense)!'' /]

[#--  Compliance Check
      51 -> Database/Dataset/Data documentation
      74 -> Maps/Geospatial data --]
[@setDeliverableRule element=deliverable ruleName=(deliverableComplianceCheck)!'' /]

[#--  Peer-review Journal Articles
      63 -> Journal Article (peer reviewed) --]
[@setDeliverableRule element=deliverable ruleName=(deliverableJournalArticles)!'' /]


[#-- One FAIR principle of the Quality check card. The state comes from action.isF/isA/isI/isR on load and from
     deliverableQualityCheck.js afterwards; the pill text and the fix button follow the class through the CSS. --]
[#macro dlfFairRow key letter points fix="" target=""]
  [#local state = "" /]
  [#attempt]
    [#if key == "findable"][#local value = action.isF(deliverable.id)!""][/#if]
    [#if key == "accessible"][#local value = action.isA(deliverable.id)!""][/#if]
    [#if key == "interoperable"][#local value = action.isI(deliverable.id)!""][/#if]
    [#if key == "reusable"][#local value = action.isR(deliverable.id)!""][/#if]
    [#if value?is_boolean][#local state = value?then("achieved", "not-achieved") /][/#if]
  [#recover]
    [#local state = "" /]
  [/#attempt]
  <div class="dlf-fairRow fairCompliant ${key} ${state}">
    <span class="dlf-fairRow__badge sign" aria-hidden="true">${letter}</span>
    <div class="dlf-fairRow__body">
      <span class="dlf-fairRow__title">
        <span class="dlf-fairRow__name">[@s.text name="project.deliverable.quality.${letter}Label" /]</span>
        <span class="dlf-fairRow__pill dlf-fairRow__pill--on">[@s.text name="project.deliverableList.fairExplanation.achieved" /]</span>
        <span class="dlf-fairRow__pill dlf-fairRow__pill--off">[@s.text name="project.deliverableList.fairExplanation.notAchieved" /]</span>
      </span>
      <ul class="dlf-fairRow__items">
        [#list points as point]<li>[@s.text name="project.deliverable.quality.${point}" /]</li>[/#list]
      </ul>
      [#if fix?has_content]
        <button type="button" class="dlf-btn dlf-btn--ghost dlf-btn--sm dlf-fairRow__fix" data-dlf-fair-fix="${target}">[@s.text name=fix /]</button>
      [/#if]
    </div>
  </div>
[/#macro]

[#macro setDeliverableRule element ruleName]
  [#attempt]
    [#local deliverableRuleValue = ((action.hasDeliverableRule(element.deliverableInfo, ruleName))!false)?string ]
    [#local deliverableTypesValues = (action.getDeliverableTypesByRule(ruleName)?replace("[", "")?replace("]", "") )! ]
  [#recover]
    [#local deliverableRuleValue = "false" ]
    [#local deliverableTypesValues = "[ ]" ]
  [/#attempt]
  <input type="hidden" id="hasDeliverableRule-${ruleName}" value="${deliverableRuleValue}" />
  <input type="hidden" id="getDeliverableTypesByRule-${ruleName}" value="${deliverableTypesValues}" />
[/#macro]

[#-- Activities list template --]
<ul style="display:none">
  <li id="activitiesTemplate" class="activities clearfix" style="display:none;">
    <div class="removeActivity removeIcon" title="Remove activity"></div>
    <input class="id" type="hidden" name="deliverable.activities[-1].id" value="" />
    <input class="aId" type="hidden" name="deliverable.activities[-1].activity.id" value="" />
    <span class="name"></span>
    <div class="clearfix"></div>
  </li>
</ul>

[#-- Funding Source list template --]
<ul style="display:none">
  <li id="fsourceTemplate" class="fundingSources clearfix" style="display:none;">
    <div class="removeFundingSource removeIcon" title="Remove funding source"></div>
    <input class="id" type="hidden" name="deliverable.fundingSources[-1].id" value="" />
    <input class="fId" type="hidden" name="deliverable.fundingSources[-1].fundingSource.id" value="" />
    <span class="name"></span>
    <div class="clearfix"></div>
  </li>
</ul>

[#-- File Input template --]
<div id="fileInputTemplate" class="fileInput" style="display:none">
  <img class="removeInput" src="${baseUrlCdn}/global/images/icon-remove.png" alt="Remove">
  <input name="filesUploaded" type="file" />
</div>

[#-- File uploaded template --]
<ul>
  <li id="deliverableFileTemplate" class="fileUploaded" style="display:none">
    <input class="fileID" name="" type="hidden">
    <input class="fileHosted" name="" type="hidden">
    <input class="fileName" name="" type="hidden">
    <div class="fileName">filename</div>
    <div class="fileFormat">- -</div>
    <div class="fileSize">- -</div>
    <img class="removeInput" src="${baseUrlCdn}/global/images/icon-remove.png" alt="Remove"/>
  </li>
</ul>

[#-- Gender level list template --]
<ul style="display:none">
  <li id="genderLevel-template" class="genderLevel clearfix" style="display:none;">
    <div class="removeGenderLevel removeIcon" title="Remove Gender Level"></div>
    <input class="id" type="hidden" name="deliverable.genderLevels[-1].id" value="" />
    <input class="fId" type="hidden" name="deliverable.genderLevels[-1].genderLevel" value="" />
    <span class="name"></span>
    <div class="clearfix"></div>
  </li>
</ul>

[#-- Remove deliverable files modal  template --]
<div id="removeDeliverableFiles" style="display:none" title="Modal title"></div>

[#-- Deliverable Partner Template --]
[@deliverableMacros.deliverablePartnerMacro element={} name="deliverable.otherPartnerships" index=-1 defaultType=2 isTemplate=true /]

[#-- Partner users TEMPLATE --]
<div id="partnerUsers" style="display:none">
  [#list partners as partner]
    <div class="institution-${partner.institution.id}">
      [#assign usersList = (action.getUserList(partner.institution.id))![]]
      <div class="users-1">
        [#list usersList as user]
          [@deliverableMacros.deliverableUserMacro element={} user=user index=user_index name="deliverable.responsiblePartnership[0].partnershipPersons" isUserChecked=false isResponsable=true /]
        [/#list]
      </div>
      <div class="users-2">
        [#list usersList as user]
          [@deliverableMacros.deliverableUserMacro element={} user=user index=user_index name="deliverable.otherPartnerships[-1].partnershipPersons" isUserChecked=false isResponsable=false /]
        [/#list]
      </div>
    </div>
  [/#list]
</div>


[#if reportingActive || upKeepActive]
  [@deliverableMacros.authorMacro element={} index=-1 name="deliverable.users"  isTemplate=true /]
  [@deliverableMacros.flagshipMacro element={} index=-1 name="deliverable.crps"  isTemplate=true /]
[/#if]

[#include "/WEB-INF/global/pages/footer.ftl"]
