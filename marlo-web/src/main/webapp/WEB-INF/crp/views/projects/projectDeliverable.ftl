[#ftl]
[#assign title = "Deliverable information" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${deliverableID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2","font-awesome","dropzone","blueimp-file-upload","jsUri", "flag-icon-css", "pickadate", "vue"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/deliverables/deliverableInfo.js?20240723",
  "${baseUrlMedia}/js/projects/deliverables/deliverableShfrm.js?20240711",
  "${baseUrlMedia}/js/projects/deliverables/deliverableDissemination.js?20240729",
  "${baseUrlMedia}/js/projects/deliverables/deliverableQualityCheck.js?20220721",
  "${baseUrlCdn}/crp/js/feedback/feedbackAutoImplementation.js?20240313",
  [#--  "${baseUrlMedia}/js/projects/deliverables/deliverableDataSharing.js?20180523",--]
  [#--  "${baseUrlCdn}/global/js/autoSave.js",--]
  "${baseUrlCdn}/global/js/fieldsValidation.js?20180529"
] /]
[#assign customCSS = [
  "${baseUrl}/crp/css/projects/projectDeliverable.css?20240729",
  "${baseUrlCdn}/global/css/404.css?20240523"
  ] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "deliverableList" /]
[#assign hideJustification = true /]

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

<div class="animated flipInX container  viewMore-block containerAlertMargin">
  <div class=" containerAlert  alert-leftovers alertColorBackgroundInfo"  id="containerAlert">
    <div class="containerLine alertColorInfo"></div>
    <div class="containerIcon">
      <div class="containerIcon alertColorInfo">
        <img src="${baseUrlCdn}/global/images/icon-question.png" />      
      </div>
    </div>
    <div class="containerText col-md-12">
      <p class="alertText">[#if reportingActive] [@s.text name="project.deliverable.help2" /] [#else] [@s.text name="project.deliverable.help1" /] [/#if] </p>
    </div>
    <div  class="viewMoreCollapse closed"></div>
  </div>
</div>

[#if completeInPreviousPhase]
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
<span id="userCanLeaveComments" style="display: none;">${(action.canLeaveComments()?c)!"false"}</span>
<span id="userCanApproveFeedback" style="display: none;">${(action.canApproveComments(projectID!-1)?c)!"false"}</span>
<span id="canTrackComments" style="display: none;">${(action.canTrackComments()?c)!"false"}</span>
<span id="isFeedbackActive" style="display: none;">${(action.hasSpecificities('feedback_active')?c)!"false"}</span>
<input type="hidden" id="sectionNameToFeedback" value="deliverable" />

<span id="existCurrentCluster" style="display: none;">${(existCurrentCluster?c)!}</span>


[#if action.hasSpecificities('feedback_active') ]
  [#list feedbackComments as feedback]
    [@customForm.qaPopUpMultiple fields=feedback.qaComments name=feedback.fieldDescription index=feedback_index canLeaveComments=(action.canLeaveComments()!false)/]
  [/#list]
  <div id="qaTemplate" style="display: none">
    [@customForm.qaPopUpMultiple canLeaveComments=(action.canLeaveComments()!false) template=true/]
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
        
        [#if action.hasSpecificities('duplicated_deliverables_functionality_active') && duplicated]
          <div class="animated flipInX container viewMore-block containerAlertMargin">
            <div class=" containerAlert  alert-leftovers alertColorBackgroundWarning"  id="containerAlert">
              <div class="containerLine alertColorWarning"></div>
              <div class="containerIcon">
                <div class="containerIcon alertColorWarning">
                  <img src="${baseUrlCdn}/global/images/icon-alert.png" />      
                </div>
              </div>
              <div class="containerText col-md-12">
                <p class="alertText"> 
                  [@s.text name="project.deliverable.duplicatedHelp" /] 
                </p>
              </div>
              [#--  <div class="viewMoreCollapse closed"></div>--]
            </div>
          </div>
        [/#if]

          <div class="form-group">
            <br />
            [#-- Back --]
            <small>
              <a href="[@s.url action='${crpSession}/deliverableList'][@s.param name="projectID" value=project.id /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
                <span class="glyphicon glyphicon-circle-arrow-left"></span> Back to the cluster deliverables
              </a>
            </small>

          [#--  Feedback Status --]
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

            [#-- FAIR Compliant Mini --]
            <div class="fairComplian-block" style="display:${deliverable.deliverableInfo.requeriedFair()?string('block','none')}">
              <div class="pull-right">
                [#-- Findable --]
                <div class="fairCompliant mini findable [#attempt][#if action.isF(deliverable.id)??][#if action.isF(deliverable.id)] achieved [#else] not-achieved [/#if][/#if][#recover][/#attempt]"><div class="sign">F</div></div>
                [#-- Accessible --]
                <div class="fairCompliant mini accessible [#attempt][#if action.isA(deliverable.id)??][#if action.isA(deliverable.id)] achieved [#else] not-achieved [/#if][/#if][#recover][/#attempt]"><div class="sign">A</div></div>
                [#-- Interoperable --]
                <div class="fairCompliant mini interoperable [#attempt][#if action.isI(deliverable.id)??][#if action.isI(deliverable.id)] achieved [#else] not-achieved [/#if][/#if][#recover][/#attempt]"><div class="sign">I</div></div>
                [#-- Reusable --]
                <div class="fairCompliant mini reusable [#attempt][#if action.isR(deliverable.id)??][#if action.isR(deliverable.id)] achieved [#else] not-achieved [/#if][/#if][#recover][/#attempt]"><div class="sign">R</div></div>
              </div>
            </div>

          </div>
          [#-- Is deliverable complete --]
          [#assign isDeliverableComplete = action.isDeliverableComplete(deliverable.id, actualPhase.id) /]

          [#-- Is deliverable new --]
          [#assign isDeliverableNew = action.isDeliverableNew(deliverable.id) /]

          [#-- Is deliverable in progress phase and is not completed --]
          [#assign validateIsProgressWithStatus = action.validateIsProgressWithStatus(deliverable.deliverableInfo.status)]

          <input id="indexTab" name="indexTab" type="hidden" value="${(indexTab)!0}">
          <div class="deliverableTabs">
            [#--  Deliverable Menu  --]
            <ul class="nav nav-tabs" role="tablist">
                <li role="presentation" class="[#if indexTab==1 || indexTab==0]active[/#if]"><a index="1" href="#deliverable-mainInformation" aria-controls="info" role="tab" data-toggle="tab">[@s.text name="project.deliverable.generalInformation.titleTab" /]</a></li>

                [#if (reportingActive || actualPhase.upkeep) && action.hasSpecificities("crp_has_disemination") ]
                <li role="presentation" class="[#if indexTab==2]active[/#if]"><a index="2" href="#deliverable-disseminationMetadata" aria-controls="metadata" role="tab" data-toggle="tab">Dissemination & Metadata</a></li>

                [#assign isRequiredQuality = deliverable.deliverableInfo.requeriedFair() || ((action.hasDeliverableRule(deliverable.deliverableInfo, deliverableComplianceCheck))!false) /]
                <li role="presentation" class="[#if indexTab==3]active[/#if]" style="display:${isRequiredQuality?string('block','none')};"><a index="3" href="#deliverable-qualityCheck" aria-controls="quality" role="tab" data-toggle="tab">Quality check</a></li>

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
              [#if (reportingActive || actualPhase.upkeep) && action.hasSpecificities("crp_has_disemination") ]
              [#-- Deliverable disseminationMetadata --]
              <div id="deliverable-disseminationMetadata" role="tabpanel" class="tab-pane fade [#if indexTab==2]in active[/#if]">
                [#-- Is this deliverable already disseminated? --]
                [@deliverableMacros.alreadyDisseminatedMacro /]

                [#-- Is this deliverable Open Access? --]
                [@deliverableMacros.isOpenaccessMacro /]

                [#-- Have you adopted a license?  --]
                [@deliverableMacros.deliverableLicenseMacro /]

                [#--  Does this deliverable involve Participants and Trainees? --]
                [@deliverableMacros.deliverableParticipantsMacro /]

                [#-- Metadata (included publications) --]
                <h3 class="headTitle">[@s.text name="project.deliverable.dissemination.metadataSubtitle" /]</h3>
                <div class="simpleBox">
                  [@deliverableMacros.deliverableMetadataMacro validateIsProgressWithStatus=validateIsProgressWithStatus/]
                </div>
              </div>
              [#-- Deliverable qualityCheck --]
              <div id="deliverable-qualityCheck" role="tabpanel" class="tab-pane fade [#if indexTab==3]in active[/#if]">
                [#--  Database/Dataset/Data documentation -- Maps/Geospatial data --]
                <div id="complianceCheck" style="display:${deliverableMacros.displayDeliverableRule(deliverable, deliverableComplianceCheck)!};">
                  [#-- Compliance check (Data products only) --]
                  [@deliverableMacros.complianceCheck /]
                </div>
                <div class="fairComplian-block" style="display:${deliverable.deliverableInfo.requeriedFair()?string('block','none')}">
                  [#-- Fair Compliant --]
                  [@deliverableMacros.fairCompliant /]
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
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/projects/buttons-deliverables.ftl" /]

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
