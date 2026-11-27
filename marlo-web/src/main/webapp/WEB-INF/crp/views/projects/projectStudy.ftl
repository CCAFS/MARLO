[#ftl]
[#assign title = "Cluster OICR" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${expectedID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [ "select2", "blueimp-file-upload", "flag-icon-css", "components-font-awesome"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectStudy.js?20250402",
  "${baseUrlCdn}/global/js/fieldsValidation.js",
  "${baseUrlCdn}/crp/js/feedback/feedbackAutoImplementation.js?20260929",
  [#-- A2-2743: the shared form redesign script (save bar state) and what the OICR form adds --]
  "${baseUrlMedia}/js/projects/deliverables/deliverableFormRedesign.js?20261105",
  "${baseUrlMedia}/js/projects/oicrForm.js?20261127"
  ]
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/projectStudies.css?20230106",
  [#-- A2-2743: the deliverable form redesign (.dlf) plus what the OICR form adds (.ocf) --]
  "${baseUrl}/crp/css/projects/deliverableForm.css?20261105-1",
  "${baseUrlMedia}/css/projects/oicrForm.css?20261127-2"
  ]
/]

[#assign currentSection = "projects" /]
[#assign currentStage = "projectStudies" /]
[#-- forms.ftl draws the redesigned list empty states ("Not provided" read-only) when this is set, as in the
     deliverable and innovation forms --]
[#global deliverableFormRedesign = true /]
[#-- The cluster is named in the form header instead of the shared top quote --]
[#assign hideTopQuote = true /]
[#-- "project" / "cluster" / "team", per global unit --]
[#assign sectionNoun = (action.getText("projects.menu.status.noun"))!"project" /]

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
    [@customForm.qaPopUpMultiple fields=feedback.qaComments name=feedback.fieldDescription index=feedback_index canLeaveComments=(action.canLeaveComments(projectID)!false)/]
  [/#list]
  <div id="qaTemplate" style="display: none">
    [@customForm.qaPopUpMultiple canLeaveComments=(action.canLeaveComments(projectID)!false) template=true/]
  </div>
[/#if]
<input type="hidden"  name="expectedStudy.id" value="${(expectedStudy.id)!}" />
[#assign isOicr = (expectedStudy.projectExpectedStudyInfo.studyType.id == 1)!false /]
[#assign indexTab = 0]

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

          [#assign isAllianceContribution = false /]
          [#list (expectedStudy.centers)![] as center]
            [#if (center.institution.name?lower_case?contains("alliance"))!false]
              [#assign isAllianceContribution = true /]
              [#break /]
            [/#if]
          [/#list]

          <div class="dlf ocf[#if !editable] is-readonly[/#if]" data-ocf>

            [#-- Back to the list --]
            <a class="dlf-back" href="[@s.url action='${crpSession}/studies'][@s.param name="projectID" value=project.id /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              <svg width="12" height="12" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M8 2.5 4.5 6 8 9.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"></path></svg>
              [@s.text name="projectStudies.studiesTitle" /]
            </a>

            [#if action.hasSpecificities('feedback_active')]
              <div class="dlf-chips ocf-feedback">
                <span class="dlf-feedbackLegend" role="note">
                  <span class="dlf-feedbackLegend__title">[@s.text name="project.deliverable.redesign.feedbackStatus" /]</span>
                  <span class="dlf-feedbackLegend__item"><img src="${baseUrlCdn}/global/images/comment.png" class="qaCommentStatus feedbackStatus" alt="" />[@s.text name="feedbackStatus.blue" /]</span>
                  <span class="dlf-feedbackLegend__item"><img src="${baseUrlCdn}/global/images/comment_yellow.png" class="qaCommentStatus feedbackStatus" alt="" />[@s.text name="feedbackStatus.yellow" /]</span>
                  <span class="dlf-feedbackLegend__item"><img src="${baseUrlCdn}/global/images/comment_green.png" class="qaCommentStatus feedbackStatus" alt="" />[@s.text name="feedbackStatus.green" /]</span>
                </span>
              </div>
            [/#if]

            [#if isOicr]
              [@ocfHeader element=(expectedStudy)!{} isAllianceContribution=isAllianceContribution /]

              [#-- RFME guidance --]
              <div class="dlf-note" role="note">
                <span class="dlf-note__icon" aria-hidden="true">i</span>
                <p class="dlf-note__text">[@s.text name="study.help" /]</p>
              </div>
            [#else]
              [#-- Other study types keep the legacy general block (its study type select is editable there) --]
              [@studies.studyGeneral element=(expectedStudy)!{} name="expectedStudy" index=0 isAllianceContribution=isAllianceContribution /]
            [/#if]

            [#if !editable && canEdit && !(transaction??)]
              <div class="ocf-readOnly" role="note">
                <svg class="dlf-icon" width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><rect x="3.2" y="7" width="9.6" height="6.6" rx="1.6" stroke="currentColor" stroke-width="1.5"></rect><path d="M5.6 7V5.2a2.4 2.4 0 0 1 4.8 0V7" stroke="currentColor" stroke-width="1.5"></path></svg>
                <span class="ocf-readOnly__text">[@s.text name="study.redesign.readOnly" /]</span>
                [#if ((action.getActualPhase().editable)!false) || action.canAccessSuperAdmin()]
                  <a class="dlf-btn ocf-readOnly__edit" href="[@s.url][@s.param name="expectedID" value=expectedID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
                    <svg class="dlf-icon" width="13" height="13" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M10.6 2.8 13.2 5.4 5.6 13H3v-2.6l7.6-7.6Z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"></path></svg>
                    [@s.text name="form.buttons.edit" /]
                  </a>
                [/#if]
              </div>
            [/#if]

            [#-- Content: All the information of the case study --]
            <input id="indexTab" name="indexTab" type="hidden" value="${(indexTab)!0}">

            <div class="studiesTab dlf-tabs ocf-tabs">
              [#if isOicr]
                <ul class="nav nav-tabs" role="tablist" aria-label="[@s.text name="study.redesign.tabs.label" /]">
                  [@ocfTab key="general" index=1 pane="study-generalInformation" label="study.general.generalInformation" complete=((action.isOicrGeneralInformationComplete())!false) active=(indexTab==1 || indexTab==0) /]
                  [@ocfTab key="alliance" index=2 pane="study-alliance" label="study.general.allianceAlignment" complete=((action.isOicrAllianceAlignmentComplete())!false) active=(indexTab==2) id="allianceTab" shown=isAllianceContribution /]
                  [@ocfTab key="cgiar" index=3 pane="study-onecgiar" label="study.general.oneCGIARAlignment" complete=((action.isOicrOneCgiarAlignmentComplete())!false) active=(indexTab==3) /]
                  [@ocfTab key="comms" index=4 pane="study-communications" label="study.general.communications" complete=((action.isOicrCommunicationsComplete())!false) active=(indexTab==4) /]
                  <li class="ocf-tabs__note" data-ocf-alliance-note[#if isAllianceContribution] hidden[/#if]>[@s.text name="study.redesign.allianceTabNote" /]</li>
                </ul>
              [/#if]

              [#-- Tab content --]
              <div class="tab-content">
                <div id="study-generalInformation" role="tabpanel" class="tab-pane fade [#if indexTab==1 || indexTab==0]in active[/#if]">
                  [#if isOicr]
                    [@studies.oicrGeneral element=(expectedStudy)!{} name="expectedStudy" /]
                  [#else]
                    [@studies.studyMacro element=(expectedStudy)!{} name="expectedStudy" index=0 /]
                  [/#if]
                </div>
                <div id="study-alliance" role="tabpanel" class="tab-pane fade [#if indexTab==2]in active[/#if]">
                  [@studies.studyAlliance element=(expectedStudy)!{} name="expectedStudy" index=0 /]
                </div>
                <div id="study-onecgiar" role="tabpanel" class="tab-pane fade [#if indexTab==3]in active[/#if]">
                  [@studies.studyOneCGIAR element=(expectedStudy)!{} name="expectedStudy" index=0 /]
                </div>
                <div id="study-communications" role="tabpanel" class="tab-pane fade [#if indexTab==4]in active[/#if]">
                  [@studies.studyCommunications element=(expectedStudy)!{} name="expectedStudy" index=0 /]
                </div>
              </div>
            </div>

            [#-- Save bar: save state, the fields to fix and the shared last-edit message on the left, the buttons on
                 the right. deliverableFormRedesign.js moves the message out of the buttons include and tracks
                 unsaved changes. --]
            [#assign issueTotal = 0 /]
            [#assign issueTabs = [] /]
            [#list [["general", "study.general.generalInformation"], ["alliance", "study.general.allianceAlignment"], ["cgiar", "study.general.oneCGIARAlignment"], ["comms", "study.general.communications"]] as tab]
              [#assign tabMissing = (action.tabMissingCounts[tab[0]])!0 /]
              [#if tab[0] == "alliance" && !isAllianceContribution][#assign tabMissing = 0 /][/#if]
              [#if tabMissing gt 0]
                [#assign issueTotal = issueTotal + tabMissing /]
                [#assign issueTabs = issueTabs + [action.getText(tab[1])] /]
              [/#if]
            [/#list]
            <div class="dlf-saveBar">
              <div class="dlf-saveBar__info">
                [#if editable && !(transaction??)]
                  <span class="dlf-saveBar__state" role="status" aria-live="polite">
                    <span class="dlf-saveBar__dot" aria-hidden="true"></span>
                    <span data-dlf-save-state
                      data-saved="[@s.text name="project.deliverable.redesign.saveBar.saved" /]"
                      data-unsaved="[@s.text name="project.deliverable.redesign.saveBar.unsaved" /]">[@s.text name="project.deliverable.redesign.saveBar.saved" /]</span>
                  </span>
                  [#if isOicr && issueTotal gt 0]
                    <button type="button" class="ocf-issues" data-ocf-issues>
                      [#if issueTotal == 1][@s.text name="study.redesign.saveBar.issue" /][#else][@s.text name="study.redesign.saveBar.issues"][@s.param]${issueTotal?c}[/@s.param][/@s.text][/#if]
                      · ${issueTabs?join(", ")}
                    </button>
                  [/#if]
                [/#if]
              </div>
              <div class="dlf-saveBar__actions">
                [#-- Section Buttons & hidden inputs--]
                [#include "/WEB-INF/crp/views/projects/buttons-projectOutcomesCaseStudies.ftl" /]
              </div>
            </div>

          </div>

        [/@s.form]

      </div>
    </div>
</section>

[#-- Internal parameters --]
[#list params?keys as prop]<input id="${params[prop].id}" type="hidden" value="${params[prop].name}" />[/#list]

[#include "/WEB-INF/global/pages/footer.ftl"]


[#-- -- MACROS -- --]

[#-- One form tab: its label and its state, a check when complete or the count of missing required fields --]
[#macro ocfTab key index pane label complete active id="" shown=true]
  [#local missing = (action.tabMissingCounts[key])!0 /]
  <li role="presentation"[#if id?has_content] id="${id}"[/#if] class="[#if active]active[/#if] ${complete?then('submitted','toSubmit')}" data-ocf-tab="${key}"[#if !shown] style="display: none;"[/#if]>
    <a href="#${pane}" role="tab" data-toggle="tab" index="${index}">
      <span class="ocf-tab__label">[@s.text name=label /]</span>
      [#if complete]
        <svg class="dlf-icon ocf-tab__ok" width="15" height="15" viewBox="0 0 16 16" fill="none" role="img" aria-label="[@s.text name="study.redesign.tab.complete" /]"><circle cx="8" cy="8" r="6.4" fill="currentColor"></circle><path d="M5.2 8.2 7.1 10l3.7-4" stroke="#fff" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"></path></svg>
      [#elseif missing gt 0]
        <span class="ocf-tab__missing" role="img" aria-label="[@s.text name="study.redesign.tab.missing"][@s.param]${missing?c}[/@s.param][/@s.text]"><span class="ocf-tab__dot" aria-hidden="true"></span>${missing?c}</span>
      [/#if]
    </a>
  </li>
[/#macro]

[#-- OICR header: identity, title, owner and sharing, then the meta row (status, current report year, tag, Alliance OICR
     ID, score). Every field keeps the name and the customForm markup the legacy general block posted, so the save,
     projectStudy.js and the feedback bubbles read them as before. --]
[#macro ocfHeader element isAllianceContribution]
  [#local customName = "expectedStudy" /]
  [#local info = (element.projectExpectedStudyInfo)! /]
  [#local statusId = (info.status.id?c)!"" /]
  [#local title = (info.title)!"" /]
  [#local ownerName][#if (project.acronym)?has_content]${project.acronym}[#else]C${project.id?c}[/#if][/#local]
  [#local shared = [] /]
  [#list (element.projects)![] as share]
    [#local shareName = (share.project.acronym)!"" /]
    [#if !shareName?has_content && (share.project.id)??][#local shareName = "C" + share.project.id?c /][/#if]
    [#if shareName?has_content][#local shared = shared + [shareName] /][/#if]
  [/#list]
  [#local cycleYear = (actualPhase.year)!0 /]
  [#local studyYear = (info.year)!cycleYear /]
  [#local isScoreVisible = (action.canAccessSuperAdmin() || action.isPMU()) && action.hasSpecificities('oicr_score_field_active') /]
  [#local tagHasInformation = ((info.tag.id)?has_content)!false /]
  [#local tagEditable = editable && action.canAccessSuperAdmin() && action.hasSpecificities('oicr_tag_field_manual_manage_active') /]

  <div class="dlf-card ocf-head">
    <div class="ocf-head__top">
      <div class="ocf-head__main">
        <span class="ocf-head__eyebrow">
          <span class="ocf-head__id">[@s.text name="study.redesign.eyebrow"][@s.param]${(element.id?c)!}[/@s.param][/@s.text]</span>
          <span class="ocf-aid" data-ocf-aid-chip title="[@s.text name="study.general.allianceID" /]"[#if !(isAllianceContribution && (info.allianceOicr)?has_content)] hidden[/#if]>${(info.allianceOicr)!}</span>
          <span class="ocf-status" data-ocf-status-chip><span class="ocf-status__dot ocf-dot--${statusId}" aria-hidden="true"></span><span data-ocf-status-text>${(statuses[statusId])!(info.statusName)!}</span></span>
        </span>
        <h1 class="ocf-head__title[#if !title?trim?has_content] is-empty[/#if]">[#if title?trim?has_content]${title}[#else][@s.text name="study.redesign.untitled" /][/#if]</h1>
        <span class="ocf-head__owner">
          [@s.text name="study.redesign.owner"][@s.param]${ownerName}[/@s.param][@s.param]${project.id?c}[/@s.param][/@s.text]<span data-ocf-shared-line[#if !shared?has_content] hidden[/#if]> · [@s.text name="study.redesign.sharedWith" /] <span data-ocf-shared-names>${shared?join(", ")}</span></span>
        </span>
      </div>
      <span class="ocf-head__actions generalStudyOptions">
        <button type="button" class="dlf-btn dlf-btn--ghost ocf-headBtn" data-ocf-share-toggle aria-expanded="false" aria-controls="ocf-share">
          <svg class="dlf-icon" width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="4.4" cy="8" r="2" stroke="currentColor" stroke-width="1.5"></circle><circle cx="11.6" cy="4" r="2" stroke="currentColor" stroke-width="1.5"></circle><circle cx="11.6" cy="12" r="2" stroke="currentColor" stroke-width="1.5"></circle><path d="m6.2 7 3.6-2M6.2 9l3.6 2" stroke="currentColor" stroke-width="1.5"></path></svg>
          [@s.text name="study.redesign.share"][@s.param]${sectionNoun}[/@s.param][/@s.text]
          <span class="ocf-headBtn__count" id="modalCounterShared">${shared?size?c}</span>
        </button>
        <button type="button" class="dlf-btn dlf-btn--ghost ocf-headBtn copyButton">
          <svg class="dlf-icon" width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M6.5 9.5 9.5 6.5M7 4.6l1.4-1.4a2.6 2.6 0 0 1 3.7 3.7L10.7 8.3M9 11.4l-1.4 1.4a2.6 2.6 0 0 1-3.7-3.7l1.4-1.4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"></path></svg>
          [@s.text name="study.redesign.copyLink" /]
        </button>
        [#local summaryPDF = "${baseUrl}/projects/${crpSession}/studySummary.do?studyID=${(element.id)!}&cycle=Reporting&year=${(actualPhase.year)!}"]
        [@customForm.input name="${customName}.projectExpectedStudyInfo.link" i18nkey="study.general.link" className="form-control input-sm urlInput" value="${summaryPDF}" editable=editable display=false readOnly=true/]
        <span class="message" style="display:none">[@s.text name="study.general.link.copy" /]</span>
      </span>
    </div>

    [#-- Sharing, inline: the same list component the legacy modal held, so the posted fields do not change --]
    <div class="ocf-share sharedClusterMessage" id="ocf-share" data-ocf-share hidden>
      <p class="ocf-share__text">[@s.text name="study.redesign.share.text"][@s.param]${sectionNoun}[/@s.param][@s.param]${ownerName}[/@s.param][/@s.text]</p>
      [@customForm.elementsListComponent name="${customName}.projects" elementType="project" elementList=(element.projects)![] label="study.sharedProjects.message" listName="myProjects" keyFieldName="id" displayFieldName="composedName" required=false showTitle=false /]
    </div>

    [#-- Study type: fixed for an OICR, posted as before --]
    [@customForm.select name="${customName}.projectExpectedStudyInfo.studyType.id" value="${(info.studyType.id)!-1}" className="setSelect2 studyType" i18nkey="study.general.type" listName="studyTypes" keyFieldName="id" displayFieldName="name" required=true editable=false display=false /]

    <div class="ocf-meta">
      [#-- Status: a segmented control over the statuses the action allows (Extended only when it may extend).
           Planning keeps only On-going selectable, as projectStudy.js did, but never disables the checked one:
           a disabled radio is not posted. --]
      <div class="ocf-meta__field ocf-meta__field--status">
        <span class="ocf-meta__label" id="ocf-status-label">[@s.text name="study.general.status" /][#if editable]<span class="ocf-req" aria-hidden="true">*</span>[/#if]</span>
        <span class="ocf-seg" role="radiogroup" aria-labelledby="ocf-status-label">
          [#list (statuses)!{} as key, label]
            [#local checked = key == statusId /]
            [#local locked = !editable || (!reportingActive && ["3", "4", "5"]?seq_contains(key) && !checked) /]
            <label class="ocf-seg__opt[#if checked] is-on[/#if][#if locked] is-locked[/#if]">
              <input type="radio" class="ocf-seg__input statusSelect" name="${customName}.projectExpectedStudyInfo.status.id" value="${key}"[#if checked] checked[/#if][#if locked] disabled[/#if] />
              <span class="ocf-seg__dot ocf-dot--${key}" aria-hidden="true"></span>${label}
            </label>
          [/#list]
        </span>
        [#-- Read-only, the radios are disabled and post nothing: keep posting the value, as the legacy select did --]
        [#if !editable]<input type="hidden" name="${customName}.projectExpectedStudyInfo.status.id" value="${statusId}" />[/#if]
        <div class="commentNumberContainer">
          <div class="numberOfCommentsBubble"><p></p></div>
          <img src="${baseUrlCdn}/global/images/comment.png" class="qaComment" name="${customName}.projectExpectedStudyInfo.status.id" fieldID="" description="">
        </div>
      </div>

      [#-- Current report year: an earlier year turns the field amber and opens the year note --]
      <div class="ocf-meta__field ocf-meta__field--year[#if studyYear lt cycleYear] is-earlier[/#if]" data-ocf-year data-cycle="${cycleYear?c}">
        [@customForm.select name="${customName}.projectExpectedStudyInfo.year" className="ocf-select" i18nkey="study.general.year" listName="getExpectedStudiesYears(${(expectedID)!})" header=false required=true editable=editable /]
      </div>

      [#-- Tag: set on save (validateOICRTag); a SuperAdmin edits it only with oicr_tag_field_manual_manage_active --]
      [#if tagHasInformation]
        <div class="ocf-meta__field ocf-meta__field--tag[#if !tagEditable] is-locked[/#if]" title="[@s.text name="study.redesign.tag.locked" /]">
          [@customForm.select name="${customName}.projectExpectedStudyInfo.tag.id" value="${(info.tag.id)!-1}" className="ocf-select studyTag" i18nkey="study.general.tag" listName="tagList" keyFieldName="id" displayFieldName="tagName" required=false editable=tagEditable /]
        </div>
      [/#if]

      [#-- Alliance OICR ID: only while an Alliance center contributes (projectStudy.js toggles the container) --]
      <div class="ocf-meta__field ocf-meta__field--aid" id="allianceOICRIdContainer"[#if !isAllianceContribution] style="display: none;"[/#if]>
        <button type="button" class="ocf-help" data-ocf-aid-help aria-expanded="false" aria-controls="ocf-aid-help" aria-label="[@s.text name="study.redesign.allianceId.help" /]">?</button>
        [@customForm.input name="${customName}.projectExpectedStudyInfo.allianceOicr" i18nkey="study.general.allianceID" helpIcon=false required=false editable=editable className="targetValueAllianceId" placeholder="XXX-0000" /]
      </div>

      [#-- OICR score: SuperAdmin or PMU, with oicr_score_field_active --]
      [#if isScoreVisible]
        <div class="ocf-meta__field ocf-meta__field--score">
          <span class="ocf-meta__badge">[@s.text name="study.redesign.score.badge" /]</span>
          [@customForm.input name="${customName}.projectExpectedStudyInfo.score" i18nkey="study.general.score" type="number" helpIcon=false required=false editable=editable /]
        </div>
      [/#if]
    </div>

    <p class="ocf-aidHelp" id="ocf-aid-help" hidden>[@s.text name="study.redesign.allianceId.helpText" /]</p>

    <div class="ocf-yearNote" role="note" data-ocf-year-note[#if !(studyYear lt cycleYear)] hidden[/#if]
      data-template="[@s.text name="study.redesign.yearNote" /]" data-phase="${(actualPhase.name)!} ${cycleYear?c}">
      <svg class="dlf-icon" width="16" height="16" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="8" cy="8" r="6.4" fill="#C08A2E"></circle><path d="M8 4.6v4.2" stroke="#fff" stroke-width="1.7" stroke-linecap="round"></path><circle cx="8" cy="11.1" r="1" fill="#fff"></circle></svg>
      <span class="ocf-yearNote__text" data-ocf-year-note-text>[@s.text name="study.redesign.yearNote"][@s.param]${studyYear?c}[/@s.param][@s.param]${cycleYear?c}[/@s.param][@s.param]${(actualPhase.name)!} ${cycleYear?c}[/@s.param][/@s.text]</span>
    </div>
  </div>
[/#macro]
