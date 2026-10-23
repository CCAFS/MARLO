[#ftl]
[#assign title = "Innovations" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${innovationID}-phase-${(actualPhase.id)!}" /]
[#-- TODO: Remove unused pageLibs--]
[#assign pageLibs = ["select2","font-awesome", "flag-icon-css", "datatables.net", "datatables.net-bs"] /]
[#-- Vue and PrimeVue back the <mal-multiselect> / <mal-select> components in this form.
     A2-2457: keep the versions pinned. Unversioned unpkg URLs resolve to "latest", and
     primevue 5.x adds a license gate that renders an "Invalid PrimeUI License" badge.
     primevue 4.5.5 pairs with @primeuix/themes 2.0.3 (both on @primeuix/styled 0.7.x).
     Vue must stay on a full build: the components declare string templates, so the
     runtime-only build cannot render them.
     A2-2464: it is the production build, so the components must not read dev-only Vue
     internals such as app._instance (see mal-multiselect.js updateVueComponent). --]
[#assign customJS = [
  "https://unpkg.com/vue@3.5.42/dist/vue.global.prod.js",
  "https://unpkg.com/primevue@4.5.5/umd/primevue.min.js",
  "https://unpkg.com/@primeuix/themes@2.0.3/umd/aura.js",
  "${baseUrlCdn}/global/js/sortableList.js?20250604",
  "${baseUrlMedia}/js/projects/projectInnovations.js?20261023-1",
  "${baseUrlCdn}/global/js/fieldsValidation.js?20250825",
  "${baseUrlCdn}/crp/js/feedback/feedbackAutoImplementation.js?20260929",
  [#-- The form redesign reuses the deliverable form's (A2-2607): save state, labels and word counters --]
  "${baseUrlMedia}/js/projects/deliverables/deliverableFormRedesign.js?20261023-3",
  "${baseUrlMedia}/js/projects/innovationFormRedesign.js?20261023-5"
] /]

[#assign moduleJS = [
  "${baseUrlMedia}/js/dist/components/my-component.js?20250902",
  "${baseUrlMedia}/js/dist/components/mal-multiselect.js?20261022",
  "${baseUrlMedia}/js/dist/components/mal-select.js?20250902"
] /]



[#assign customCSS = [
  "${baseUrlMedia}/css/projects/projectInnovations.css?20250915",
  "${baseUrlCdn}/global/css/customDataTable.css?20250825",
  [#-- A2-2613: the deliverable form redesign (.dlf) plus what the innovation form adds (.inf) --]
  "${baseUrl}/crp/css/projects/deliverableForm.css?20261021-1",
  "${baseUrlMedia}/css/projects/innovationForm.css?20261023-8"
] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "innovations" /]
[#-- The innovation and cluster chips are drawn in the form header instead of the shared top quote --]
[#assign hideTopQuote = true /]
[#-- "project" / "cluster" / "team", per global unit --]
[#assign sectionNoun = (action.getText("projects.menu.status.noun"))!"project" /]

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

[#-- The tab the user saved from (ProjectInnovationAction keeps it across the save redirect); 0 or 1 is General --]
[#assign indexTab = (action.indexTab)!0]

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

      [@s.form action=actionName cssClass="pure-form" enctype="multipart/form-data" ]

        [#assign isProgressActive = action.isProgressActive() /]

        [#-- A2-2613: the Alliance Alignment tab is gone unless innovation_alliance_alignment_active is on (off for
             AICCRA-FSRP IV only, see its migrations). On, the tab shows when an Alliance center is selected, as before. --]
        [#assign withoutAllianceAlignment = !action.hasSpecificities('innovation_alliance_alignment_active') /]
        [#-- A2-2613: the Cross-cutting alignment (One CGIAR Alignment) tab, behind
             innovation_cross_cutting_alignment_active (on for every global unit); off, its stored data is kept --]
        [#assign withCrossCutting = (action.isCrossCuttingAlignmentActive())!true /]
        [#assign isAllianceContribution = false /]
        [#if !withoutAllianceAlignment]
          [#list (innovation.centers)![] as center]
            [#if (center.institution.name?lower_case?contains("alliance"))!false]
              [#assign isAllianceContribution = true /]
              [#break /]
            [/#if]
          [/#list]
        [/#if]

        [#-- Bundle composition is a tab only for a bundled innovation --]
        [#assign isBundleCompositionRequired =  false /]
        [#if innovation.projectInnovationInfo?has_content && innovation.projectInnovationInfo.innovationBundle?has_content]
          [#assign isBundleCompositionRequired = innovation.projectInnovationInfo.innovationBundle!false /]
        [/#if]

        [#-- A tab that does not show for this innovation (any more) falls back to General Information --]
        [#if (indexTab == 2 && !isAllianceContribution) || (indexTab == 3 && !withCrossCutting)
          || (indexTab == 4 && !isBundleCompositionRequired) || indexTab gt 5]
          [#assign indexTab = 1 /]
        [/#if]

        <div class="dlf inf[#if !editable] is-readonly[/#if]">

          [#-- Back to the list --]
          <a class="dlf-back" href="[@s.url action='${crpSession}/innovationsList'][@s.param name="projectID" value=project.id /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
            <svg width="12" height="12" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M8 2.5 4.5 6 8 9.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"></path></svg>
            [@s.text name="projectInnovations.redesign.back"][@s.param]${sectionNoun}[/@s.param][/@s.text]
          </a>

          [#-- Innovation and cluster chips, the required fields state, the bundle flag and the feedback status legend --]
          [#assign isInnovationComplete = (action.hasInnovationMissingFields(innovation.id))!false /]
          <div class="dlf-chips">
            <span class="dlf-chip dlf-chip--id" title="${(innovation.projectInnovationInfo.title)!}">I${innovation.id?c}</span>
            <a class="dlf-chip dlf-chip--owner" href="[@s.url namespace="/projects" action='${crpSession}/description'][@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]"
              title="C${project.id?c} - ${(project.projectInfo.title)!}">
              <span class="dlf-chip__avatar" aria-hidden="true">${sectionNoun?cap_first?substring(0, 1)}</span>
              [#if (project.acronym)?has_content]${project.acronym}[#else]C${project.id?c}[/#if]
            </a>
            <span class="dlf-chip dlf-chip--fields ${isInnovationComplete?string('is-ok', 'is-warn')}">
              <span class="dlf-chip__dot" aria-hidden="true"></span>
              [#if isInnovationComplete][@s.text name="project.deliverable.redesign.fields.complete" /][#else][@s.text name="project.deliverable.redesign.fields.missing" /][/#if]
            </span>
            [#-- innovationFormRedesign.js follows the Innovation/Bundle answer --]
            <span class="dlf-chip inf-chip--bundle" data-inf-bundle-chip[#if !isBundleCompositionRequired] hidden[/#if]>[@s.text name="projectInnovations.innovationBundle.bundleOption" /]</span>
            [#if action.hasSpecificities('feedback_active')]
              <span class="dlf-feedbackLegend" role="note">
                <span class="dlf-feedbackLegend__title">[@s.text name="project.deliverable.redesign.feedbackStatus" /]</span>
                <span class="dlf-feedbackLegend__item"><img src="${baseUrlCdn}/global/images/comment.png" class="qaCommentStatus feedbackStatus" alt="" />[@s.text name="feedbackStatus.blue" /]</span>
                <span class="dlf-feedbackLegend__item"><img src="${baseUrlCdn}/global/images/comment_yellow.png" class="qaCommentStatus feedbackStatus" alt="" />[@s.text name="feedbackStatus.yellow" /]</span>
                <span class="dlf-feedbackLegend__item"><img src="${baseUrlCdn}/global/images/comment_green.png" class="qaCommentStatus feedbackStatus" alt="" />[@s.text name="feedbackStatus.green" /]</span>
              </span>
            [/#if]
          </div>

          [#-- Title, with the share and copy actions --]
          <div class="dlf-head">
            <h1 class="dlf-head__title">[#if (innovation.projectInnovationInfo.title)?has_content]${innovation.projectInnovationInfo.title}[#else][@s.text name="projectInnovations.redesign.untitled" /][/#if]</h1>
            [@innovations.innovationHeaderActions element=(innovation)!{} /]
          </div>

          [#-- Annual Report year and PRMS mapping --]
          [@innovations.innovationReportingStrip element=(innovation)!{} /]

          [#-- Webinar note, shown to every global unit as before the redesign --]
          <div class="dlf-note" role="note">
            <span class="dlf-note__icon" aria-hidden="true">i</span>
            <p class="dlf-note__text">[@s.text name="projectInnovations.webinar.note" /]</p>
            <a class="dlf-btn dlf-btn--ghost dlf-btn--sm inf-webinar" href="https://cgiar.sharepoint.com/:p:/s/AICCRA/EdfDKeegVkxHn2mdcBYmsy4Bh1kkzjX73X5673y3Ynjj7w?e=uCzD2T" target="_blank" rel="noopener noreferrer">
              <svg width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M9 2.6h4.4V7M13.2 2.8 7.6 8.4M11.6 9.6v3.6H2.8V4.4h3.6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"></path></svg>
              [@s.text name="projectInnovations.webinar.link" /]
            </a>
          </div>

          [#if !editable && canEdit && !(transaction??)]
            <div class="inf-readOnly" role="note">
              <svg class="dlf-icon" width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><rect x="3.2" y="7" width="9.6" height="6.6" rx="1.6" stroke="currentColor" stroke-width="1.5"></rect><path d="M5.6 7V5.2a2.4 2.4 0 0 1 4.8 0V7" stroke="currentColor" stroke-width="1.5"></path></svg>
              <span class="inf-readOnly__text">[@s.text name="projectInnovations.redesign.readOnly" /]</span>
              [#-- In a closed phase only a super admin can edit (EditInnovationInterceptor); nobody else gets the button --]
              [#if ((action.getActualPhase().editable)!false) || action.canAccessSuperAdmin()]
              <a class="dlf-btn inf-readOnly__edit" href="[@s.url][@s.param name="innovationID" value=innovationID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
                <svg class="dlf-icon" width="13" height="13" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M10.6 2.8 13.2 5.4 5.6 13H3v-2.6l7.6-7.6Z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"></path></svg>
                [@s.text name="form.buttons.edit" /]
              </a>
              [/#if]
            </div>
          [/#if]

          [#-- Content: All the information of the innovation --]
          <input id="indexTab" name="indexTab" type="hidden" value="${(indexTab)!0}">

          [#-- Tabs share the row equally: 33% each, 25% each when Bundle composition shows (innovationFormRedesign.css) --]
          <div class="innovationTabs dlf-tabs inf-tabs">
            <ul class="nav nav-tabs" role="tablist" aria-label="[@s.text name="projectInnovations.redesign.tabs.label" /]">
              [@infTab key="general" index=1 pane="innovationGeneral" label="projectInnovations.tab.general" complete=((action.isInnovationGeneralInformationComplete())!false) active=(indexTab==1 || indexTab==0) /]
              [#if !withoutAllianceAlignment]
                [@infTab key="alliance" index=2 pane="innovationAlliance" label="projectInnovations.tab.allianceAlignment" complete=((action.isInnovationAllianceAlignmentComplete())!false) active=(indexTab==2) id="allianceTab" shown=isAllianceContribution /]
              [/#if]
              [#if withCrossCutting]
                [@infTab key="crossCutting" index=3 pane="innovationOneCGIAR" label="projectInnovations.tab.oneCGIARAlignment" complete=((action.isInnovationOneCgiarAlignmentComplete())!false) active=(indexTab==3) /]
              [/#if]
              [@infTab key="bundle" index=4 pane="innovationBundle" label="projectInnovations.tab.bundleComposition" complete=((action.isInnovationBundleComplete())!false) active=(indexTab==4) id="bundleTab" shown=isBundleCompositionRequired /]
              [@infTab key="knowledge" index=5 pane="innovationSharing" label="projectInnovations.tab.innovationSharing" complete=((action.isInnovationRightsComplete())!false) active=(indexTab==5) /]
            </ul>

            [#-- Tabs Content --]
            <div class="tab-content">
              <div role="tabpanel" class="tab-pane fade [#if indexTab==1 || indexTab==0]in active[/#if]" id="innovationGeneral">
                [#-- Innovation General --]
                [@innovations.innovationGeneral element=(innovation)!{} name="innovation" index=0 /]
              </div>
              [#if !withoutAllianceAlignment]
                <div role="tabpanel" class="tab-pane fade [#if indexTab==2]in active[/#if]" id="innovationAlliance">
                  [#-- Innovation Alliance Aligment--]
                  [@innovations.innovationAlliance element=(innovation)!{} name="innovation" index=0 /]
                </div>
              [/#if]
              [#if withCrossCutting]
                <div role="tabpanel" class="tab-pane fade [#if indexTab==3]in active[/#if]" id="innovationOneCGIAR">
                  [#-- Innovation OneCGIAR Aligment--]
                  [@innovations.innovationOneCGIAR element=(innovation)!{} name="innovation" index=0 /]
                </div>
              [/#if]
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

          [#-- Without the Alliance Alignment tab its stored answers are posted back unchanged --]
          [#if withoutAllianceAlignment]
            [@innovations.innovationAllianceStored element=(innovation)!{} /]
          [/#if]

          [#-- Save bar: save state and the shared last-edit message on the left, the buttons on the right.
               deliverableFormRedesign.js moves the message out of the buttons include and tracks unsaved changes. --]
          <div class="dlf-saveBar">
            <div class="dlf-saveBar__info">
              [#if editable && !(transaction??)]
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
              [#include "/WEB-INF/crp/views/projects/buttons-innovation.ftl" /]
            </div>
          </div>

        </div>

      [/@s.form]
  </div>  
</section>

[#-- Partner users TEMPLATE --]
<div id="partnerUsers" style="display:none">
  [#list partners as partner]
    <div class="institution-${partner.institution.id}">
      [#assign usersList = (action.getUserList(partner.institution.id))![]]
      <div class="users-2">
        [#list usersList as user]
          [@deliverableMacros.deliverableUserMacro element={} user=user index=user_index name="_TEMPLATE_innovation.partnerships[0].partnershipPersons" isUserChecked=false isResponsable=false /]
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
[#-- One form tab: its label and its state, a check when complete or the count of missing required fields --]
[#macro infTab key index pane label complete active id="" shown=true]
  [#local missing = (action.tabMissingCounts[key])!0 /]
  <li role="presentation"[#if id?has_content] id="${id}"[/#if] class="[#if active]active[/#if] ${complete?then('submitted','toSubmit')}" data-inf-tab="${key}"[#if !shown] style="display: none;"[/#if]>
    <a href="#${pane}" role="tab" data-toggle="tab" index="${index}">
      <span class="inf-tab__label" title="[@s.text name=label /]">[@s.text name=label /]</span>
      [#if complete]
        <svg class="dlf-icon inf-tab__ok" width="15" height="15" viewBox="0 0 16 16" fill="none" role="img" aria-label="[@s.text name="projectInnovations.redesign.tab.complete" /]"><circle cx="8" cy="8" r="6.4" fill="currentColor"></circle><path d="M5.2 8.2 7.1 10l3.7-4" stroke="#fff" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"></path></svg>
      [#elseif missing gt 0]
        <span class="inf-tab__missing" role="img" aria-label="[@s.text name="projectInnovations.redesign.tab.missing"][@s.param]${missing?c}[/@s.param][/@s.text]"><span class="inf-tab__dot" aria-hidden="true"></span>${missing?c}</span>
      [/#if]
    </a>
  </li>
[/#macro]
