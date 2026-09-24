[#ftl]
[#assign title = "Cluster Contribution to Indicators" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectOutcomeID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "trumbowyg", "datatables.net", "datatables.net-bs"] /]
[#assign customJS = [ 
  "${baseUrlMedia}/js/projects/projectContributionCrp.js?20230310", 
  "${baseUrlMedia}/js/projects/projectContributionCrpRedesign.js?20260923",
  "${baseUrlCdn}/global/js/fieldsValidation.js?20221031",
  "${baseUrlCdn}/crp/js/feedback/feedbackAutoImplementation.js?20260826",
  "https://www.gstatic.com/charts/loader.js",
  "https://cdn.datatables.net/buttons/1.3.1/js/dataTables.buttons.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.html5.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.print.min.js",
  "${baseUrlMedia}/js/annualReport2018/annualReport2018_ccDimensions.js?20221031" 
  ] 
/] 
[#assign customCSS = [ 
  "${baseUrlMedia}/css/projects/projectContributionCrp.css?20240517",
  "${baseUrlMedia}/css/projects/projectContributionCrpRedesign.css?20260923",
  "${baseUrlMedia}/css/annualReport/annualReportGlobal.css?20250701",
  "https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css"
  ] 
/]
[#assign currentSection = "clusters" /]
[#assign currentStage = "contributionsCrpList" /]
[#assign afYear = action.getAFIndicatorsEndyear()]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectContributionsCrpList", "nameSpace":"/projects", "action":"${crpSession}/contributionsCrpList" , "param": "projectID=${project.id}"},
  {"label":"projectContributionCrp", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/crp/macros/relationsPopupMacro.ftl" as popUps /]

[#assign startYear = (project.projectInfo.startDate?string.yyyy)?number /]
[#assign endYear = (project.projectInfo.endDate?string.yyyy)?number /]


[#if (!availabePhase)!false]
  [#include "/WEB-INF/crp/views/projects/availability-projects.ftl" /]
[#else]

[#--  [@customForm.qaPopUp /]  --]

[#if action.hasSpecificities('feedback_active') ]
  [#list feedbackComments as feedback]
    [@customForm.qaPopUpMultiple fields=feedback.qaComments name=feedback.fieldDescription index=feedback_index canLeaveComments=(action.canLeaveComments(projectID!)!false)/]
  [/#list]
  <div id="qaTemplate" style="display: none">
    [@customForm.qaPopUpMultiple canLeaveComments=(action.canLeaveComments(projectID!)!false) template=true/]
  </div>
[/#if]

<input type="hidden" id="sectionNameToFeedback" value="projectContributionCrp" />

<section class="container cpi-page">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3 cpi-side">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/projects/messages-projectOutcomes.ftl" /]

        [#-- Cluster quote, immediately before the form so it sits with the section --]
        [#include "/WEB-INF/crp/views/projects/dataInfo-projects.ftl" /]

        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          [#--  Feedback Status --]
          [#if action.hasSpecificities('feedback_active') ]
            <div class="form-group col-md-12 legendContent">
              <div class="colors">
                <div class="col-md-12 form-group "><b>Feedback status:</b></div>
                <div class="color col-md-4"><img src="${baseUrlCdn}/global/images/comment.png" class="qaCommentStatus feedbackStatus">[@s.text name="feedbackStatus.blue" /]</div>
                <div class="color col-md-4"><img src="${baseUrlCdn}/global/images/comment_yellow.png" class="qaCommentStatus feedbackStatus">[@s.text name="feedbackStatus.yellow" /]</div>
                <div class="color col-md-4"><img src="${baseUrlCdn}/global/images/comment_green.png" class="qaCommentStatus feedbackStatus">[@s.text name="feedbackStatus.green" /]</div>
              </div>
            </div>
          [/#if]

          [#-- ═══ A2-2439 · section header ═══ --]
          <div class="cpi-head">
            <div class="cpi-head__titles">
              <h3 class="cpi-head__title">[@s.text name="projectOutcome.projectContribution" /]</h3>
              <span class="cpi-head__subtitle">${(projectOutcome.crpProgramOutcome.crpProgram.acronym)!} &middot; ${(project.projectInfo.title)!''}</span>
            </div>
            <a class="cpi-head__back" href="[@s.url action='${crpSession}/contributionsCrpList'][@s.param name="projectID" value=project.id /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              <span class="glyphicon glyphicon-circle-arrow-left"></span> [@s.text name="projectContributionCrp.backToList" /]
            </a>
          </div>
          <span id="parentID" style="display: none;">${projectOutcomeID!}</span>
          <span id="phaseID" style="display: none;">${phaseID!}</span>
          <span id="userID" style="display: none;">${currentUser.id!}</span>
          <span id="projectID" style="display: none;">${projectID!}</span>
          <span id="userCanManageFeedback" style="display: none;">${(action.canManageFeedback(projectID)?c)!}</span>
          <span id="userCanLeaveComments" style="display: none;">${(action.canLeaveComments(projectID!)?c)!}</span>
          <span id="userCanApproveFeedback" style="display: none;">${(action.canApproveComments(projectID)?c)!}</span>
          <span id="canTrackComments" style="display: none;">${(action.canTrackComments()?c)!}</span>
          <span id="isFeedbackActive" style="display: none;">${(action.hasSpecificities('feedback_active')?c)!}</span>
          <span id="isFeedbackNewCommentFieldActive" style="display: none;">${(action.hasSpecificities('feedback_new_comment_field_active')?c)!"false"}</span>
          <span id="isSuperAdmin" style="display: none;">${(action.canAccessSuperAdmin()?c)!}</span>

          [#-- ═══════════════════════════════════════════════════════════════════════
               A2-2439 · Contribution to Period Targets
               The outcome's milestones are grouped into the same matrix the Overall
               Performance Indicators screen builds: row 0 carries the indicator's own
               statement and is the headline period target, the remaining rows are its
               disaggregated targets, and the distinct years are the period tabs. The
               grouping is lifted from impactPathway/outcomes.ftl so a milestone lands
               on the same row on both screens.
               ═══════════════════════════════════════════════════════════════════════ --]
          [#assign cpiOutcome = projectOutcome.crpProgramOutcome /]
          [#assign cpiUnit = (cpiOutcome.srfTargetUnit.name)!'' /]
          [#assign showOutcomeValue = cpiOutcome.srfTargetUnit?? && cpiOutcome.srfTargetUnit.id?? && (cpiOutcome.srfTargetUnit.id != -1) /]
          [#assign cpiAllMilestones = (milestones)![] /]
          [#assign cpiOutcomeStmt = opiStmtKey((cpiOutcome.description)!"") /]
          [#assign cpiRowStmts = [] /]
          [#assign cpiRowOf = [] /]
          [#assign cpiRowYearTaken = [] /]
          [#assign cpiYears = [] /]
          [#if cpiAllMilestones?has_content]
            [#list cpiAllMilestones as m]
              [#if opiStmtKey((m.title)!"") == cpiOutcomeStmt][#assign cpiRowStmts = [cpiOutcomeStmt] /][#break][/#if]
            [/#list]
            [#list cpiAllMilestones as m]
              [#assign cpiStmt = opiStmtKey((m.title)!"") /]
              [#assign cpiYear = (m.year)!-1 /]
              [#assign cpiRow = -1 /]
              [#list cpiRowStmts as s]
                [#if s == cpiStmt && !cpiRowYearTaken?seq_contains("${s_index?c}@${cpiYear?c}")]
                  [#assign cpiRow = s_index /][#break]
                [/#if]
              [/#list]
              [#if cpiRow == -1]
                [#assign cpiRowStmts = cpiRowStmts + [cpiStmt] /]
                [#assign cpiRow = cpiRowStmts?size - 1 /]
              [/#if]
              [#assign cpiRowYearTaken = cpiRowYearTaken + ["${cpiRow?c}@${cpiYear?c}"] /]
              [#assign cpiRowOf = cpiRowOf + [cpiRow] /]
              [#if !cpiYears?seq_contains(cpiYear)][#assign cpiYears = cpiYears + [cpiYear] /][/#if]
            [/#list]
            [#assign cpiYears = cpiYears?sort /]
          [/#if]
          [#if cpiRowStmts?size == 0][#assign cpiRowStmts = [cpiOutcomeStmt] /][/#if]

          [#-- The tab that opens: the period of the current cycle when the indicator has
               one, otherwise the last period so the screen never opens on a blank pane. --]
          [#assign cpiOpenYear = -1 /]
          [#list cpiYears as y][#if y == currentCycleYear][#assign cpiOpenYear = y /][/#if][/#list]
          [#if cpiOpenYear == -1 && cpiYears?has_content][#assign cpiOpenYear = cpiYears?last /][/#if]

          [#-- ═══ Indicator card ═══ --]
          <div class="cpi-card cpi-indicator">
            <div class="cpi-indicator__top">
              <span class="cpi-indicator__code">${(cpiOutcome.acronym)!(cpiOutcome.composeID)!'&mdash;'}</span>
              <span class="cpi-indicator__name">${(cpiOutcome.description)!}</span>
              [#if (cpiOutcome.instructions?? && cpiOutcome.instructions != '')]
                <button type="button" class="button-evidences cpi-indicator__details">
                  [@s.text name="projectContributionCrp.seeDetails" /]
                  <svg width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="8" cy="8" r="6.4" stroke="currentColor" stroke-width="1.4"/><path d="M8 7.4v3.4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/><circle cx="8" cy="5.3" r=".9" fill="currentColor"/></svg>
                </button>
              [/#if]
            </div>
            <span class="cpi-indicator__meta">
              [#if showOutcomeValue][@s.text name="projectContributionCrp.meta.unit" /]: ${cpiUnit} &middot; [/#if]
              [#-- The closing year is always captured; the start year is optional, so the
                   pair only reads as a period when both are there. --]
              [#if ((cpiOutcome.startYear)!-1) gt 0 && (cpiOutcome.year)?has_content]
                [@s.text name="projectContributionCrp.meta.period" /]: ${cpiOutcome.startYear?c}&ndash;${cpiOutcome.year?c} &middot;
              [#elseif (cpiOutcome.year)?has_content]
                [@s.text name="projectContributionCrp.meta.closingYear" /]: ${cpiOutcome.year?c} &middot;
              [/#if]
              [#if (cpiOutcome.baselineValue)?has_content][@s.text name="projectContributionCrp.meta.baseline" /]: ${(cpiOutcome.baselineValue?string(",##0"))!} &middot; [/#if]
              [@s.text name="projectContributionCrp.meta.closingTarget" /]: [#if (cpiOutcome.value)?has_content]${cpiOutcome.value?string(",##0")}[#else]&mdash;[/#if]
            </span>
            <span class="cpi-indicator__meta">
              [#if cpiRowStmts?size gt 1]
                [@s.text name="projectContributionCrp.meta.disaggregations" /]: ${cpiRowStmts?size - 1}
              [#else]
                [@s.text name="projectContributionCrp.meta.noDisaggregations" /]
              [/#if]
            </span>
            <span class="cpi-indicator__note text-evidences">[@s.text name="projectContributionCrp.evidenceNote" /]</span>
            [#-- Guidance modal, driven by the existing projectContributionCrp.js handlers --]
            <div class="modal-evidences" style="display: none">
              <div class="content-modal">
                <div class="button-exit close-modal-evidences"><div class="x-close-modal"></div></div>
                <p class="title-modal-evidences">[@s.text name="projectContributionCrp.guidance.title" /]</p>
                <div class="text-modal-evidences"><p>${(cpiOutcome.instructions)!}</p></div>
                <div class="container-buttons-evidences">
                  [#if !action.isPOWB() && ((cpiOutcome.file.fileName??)!false)]
                    <a href="${action.getBaseLineFileURL((cpiOutcome.id?string)!-1)}&filename=${(cpiOutcome.file.fileName)!}" target="_blank">
                      <div class="button-pdf-modal">
                        <p>[@s.text name="projectContributionCrp.guidance.readFull" /]</p>
                        <img src="${baseUrlCdn}/global/images/pdf.png" alt="Download document" />
                      </div>
                    </a>
                  [/#if]
                  <div class="button-close-modal close-modal-evidences"><p>[@s.text name="form.buttons.close" /]</p></div>
                </div>
              </div>
            </div>
          </div>

          [#-- ═══ Overall cluster contribution ═══ --]
          <div class="cpi-card">
            <h4 class="cpi-card__title">[@s.text name="projectOutcome.contributionToThisOutcome" /]</h4>
            <div class="cpi-field" style="display:${showOutcomeValue?string('block', 'none')}">
              [#if (action.isAFPhase(actualPhase.id))!false]
                [@customForm.input name="projectOutcome.expectedValue" i18nkey="projectOutcome.expectedValueAF" paramText=(cpiOutcome.year)!afYear type="text" placeholder="" className="targetValue targetValueNumber" required=true editable=editable && !reportingActive && editOutcomeExpectedValue /]
              [#else]
                [@customForm.input name="projectOutcome.expectedValue" type="text" placeholder="" className="targetValue targetValueNumber" required=true editable=editable && !reportingActive && editOutcomeExpectedValue /]
              [/#if]
            </div>
            <div class="cpi-field">
              [@customForm.textArea name="projectOutcome.narrativeTarget" required=true className="limitWords-150" editable=editable && (!reportingActive || (!(projectOutcome.narrativeTarget?has_content)!false)) /]
            </div>
          </div>

          [#-- ═══ Additional questions for this performance indicator ═══ --]
          [#if action.hasSpecificities('crp_baseline_indicators') && (cpiOutcome.indicators?has_content)!false]
            <div class="cpi-card cpi-questions">
              <h4 class="cpi-card__title">[@s.text name="projectContributionCrp.additionalQuestions" /]</h4>
              [#list cpiOutcome.indicators as indicator]
                [@cpiQuestion element=indicator index=indicator_index /]
              [/#list]
            </div>
          [/#if]

          [#-- ═══ Yearly contribution to intermediate targets ═══ --]
          <div class="cpi-periods">
            <h4 class="cpi-card__title cpi-periods__title">[@s.text name="projectOutcome.contributionToMilestones" /]</h4>
            [#if cpiYears?has_content]
              <ul class="nav nav-tabs cpi-tabs" role="tablist">
                [#list cpiYears as year]
                  <li role="presentation" class="[#if year == cpiOpenYear]active[/#if]">
                    <a href="#cpiYear-${year?c}" role="tab" data-toggle="tab">
                      [@s.text name="projectOutcomeMilestone.projectMilestoneTarget" /] ${year?c}
                      [#if year == currentCycleYear]<span class="cpi-tabs__now">[@s.text name="projectContributionCrp.currentPeriod" /]</span>[/#if]
                    </a>
                  </li>
                [/#list]
              </ul>
              <div class="tab-content cpi-panes">
                [#list cpiYears as year]
                  <div role="tabpanel" class="tab-pane cpi-pane [#if year == cpiOpenYear]active[/#if]" id="cpiYear-${year?c}">

                    [#-- Headline period target: the milestone on the principal row --]
                    [#assign cpiHeadlineDone = false /]
                    [#list cpiAllMilestones as m]
                      [#if !cpiHeadlineDone && cpiRowOf[m_index] == 0 && ((m.year)!-1) == year]
                        [#assign cpiHeadlineDone = true /]
                        [@cpiMilestoneFields element=m year=year isPrincipal=true /]
                      [/#if]
                    [/#list]
                    [#if !cpiHeadlineDone]
                      <p class="cpi-empty">[@s.text name="projectContributionCrp.noTargetForYear" /] ${year?c}.</p>
                    [/#if]

                    [#-- Disaggregated targets: every further row of the matrix --]
                    [#if cpiRowStmts?size gt 1]
                      <div class="cpi-dts">
                        <span class="cpi-dts__label">
                          [@s.text name="projectContributionCrp.disaggregatedTargets" /]:
                          <span class="cpi-dts__note">${cpiRowStmts?size - 1} [@s.text name="projectContributionCrp.disaggregatedTargets.note" /]</span>
                        </span>
                        [#list 1..(cpiRowStmts?size - 1) as row]
                          [#assign cpiRowDone = false /]
                          [#list cpiAllMilestones as m]
                            [#if !cpiRowDone && cpiRowOf[m_index] == row && ((m.year)!-1) == year]
                              [#assign cpiRowDone = true /]
                              <div class="cpi-dt">
                                <div class="cpi-dt__head" data-cpi-toggle="cpiDt-${year?c}-${row}" role="button" tabindex="0" aria-controls="cpiDt-${year?c}-${row}" aria-expanded="false">
                                  <span class="cpi-dt__caret" aria-hidden="true"><svg width="10" height="10" viewBox="0 0 12 12" fill="none"><path d="M4 2.5 7.5 6 4 9.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg></span>
                                  <span class="cpi-dt__title">[@s.text name="projectContributionCrp.target" /] ${row}: ${(m.title)!}</span>
                                  [#if (m.code)?has_content]<span class="cpi-chip">${m.code}</span>[/#if]
                                </div>
                                <div class="cpi-dt__body" id="cpiDt-${year?c}-${row}" style="display:none">
                                  [@cpiMilestoneFields element=m year=year isPrincipal=false /]
                                </div>
                              </div>
                            [/#if]
                          [/#list]
                          [#if !cpiRowDone]
                            <div class="cpi-dt cpi-dt--empty">
                              <span class="cpi-dt__title">[@s.text name="projectContributionCrp.target" /] ${row}</span>
                              <span class="cpi-dt__note">[@s.text name="projectContributionCrp.noTargetForYear" /] ${year?c}.</span>
                            </div>
                          [/#if]
                        [/#list]
                      </div>
                    [/#if]

                  </div>
                [/#list]
              </div>
            [#else]
              <p class="cpi-empty">[@s.text name="projectContributionCrp.noPeriods" /]</p>
            [/#if]
          </div>
          [#-- Communications --]
          [#if reportingActive && action.hasSpecificities('crp_show_project_outcome_communications') ]  
          <div class="">
            <h4 class="headTitle">Communications </h4>
            <div class="borderBox" id="communicationsYear-${currentCycleYear}">
              [#assign comunication = action.loadProjectCommunication(currentCycleYear) /]
              [#assign comunicationIndex = action.getIndexCommunication(currentCycleYear) /]
              <input type="hidden" name="projectOutcome.communications.id" value=${(projectOutcome.communications.id)!"-1"} />
              <input type="hidden" name="projectOutcome.communications[${comunicationIndex}].year" value="${currentCycleYear}"/>
              <div class="communicationsBlock form-group">
                <div class="form-group">
                  [@customForm.textArea name="projectOutcome.communications[${comunicationIndex}].communication" i18nkey="projectOutcome.communicationEngagement" required=isYearRequired(currentCycleYear) className="limitWords-100 fieldFocus" editable=editable /]
                </div>
              </div>
            </div>
          </div>
          [/#if]          
                                  
                  
          
          [#-- Next Users --]
          [#-- For A4NH CRP, nextusers aren't required --]
          [#if action.hasSpecificities('crp_next_users')]
          <h4 class="headTitle">(Next) Users </h4>
          <div class="nextUsersBlock borderBox">
            <div class="nextUsersList">
              [#if projectOutcome.nextUsers?has_content]
                [#list projectOutcome.nextUsers as nextUser]
                  [@nextUserMacro element=nextUser name="projectOutcome.nextUsers" index=nextUser_index /]
                [/#list]
              [#else]
                [@nextUserMacro element={} name="projectOutcome.nextUsers" index=0 /]
              [/#if]
            </div>
            [#if editable && !reportingActive]
              <div class="addNextUser bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>[@s.text name="form.buttons.addNextUser"/]</div>
            [/#if]
          </div>
          [/#if]
                    
          [#-- Lessons and progress --]
          [#if !action.isProjectNew(project.id) && action.isReportingActive() && action.isLessonsActive()]
          <div id="lessons" class="borderBox">
            [#-- Lessons learnt from last planning/reporting cycle --]
            [#if (projectOutcome.projectComponentLessonPreview.lessons?has_content)!false]
            <div class="fullBlock">
              <label>[@customForm.text name="projectOutcome.previousLessons.${reportingActive?string('reporting','planning')}" param="${reportingActive?string(reportingYear,planningYear-1)}" /]:</label>
              <div class="textArea"><p>${projectOutcome.projectComponentLessonPreview.lessons}</p></div>
            </div>
            [/#if]
            [#-- Planning/Reporting lessons --]
            <div class="fullBlock ">
              <input type="hidden" name="projectOutcome.projectComponentLesson.id" value=${(projectOutcome.projectComponentLesson.id)!"-1"} />
              <input type="hidden" name="projectOutcome.projectComponentLesson.year" value=${reportingActive?string(reportingYear,planningYear)} />
              <input type="hidden" name="projectOutcome.projectComponentLesson.componentName" value="${actionName}">
              [@customForm.textArea name="projectOutcome.projectComponentLesson.lessons" i18nkey="projectOutcome.lessons.${reportingActive?string('reporting','planning')}" help="projectOutcome.lessons.help" helpIcon=false className=" ${reportingActive?string('fieldFocus','')}" required=true editable=editable /]
            </div>
          </div>
          [/#if]
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/projects/buttons-projectOutcomes.ftl" /]

        [/@s.form] 
      </div>
    </div>  
</section>
[/#if]

[#-- Next user Template --]
[@nextUserMacro element={} name="projectOutcome.nextUsers" index="-1" isTemplate=true /]

[#-- indicators Template --]
[@baselineAiccraIndicatorMacro element={} name="projectOutcome.indicators" index="-1" isTemplate=true AREditable=true /]


  
[#include "/WEB-INF/global/pages/footer.ftl"]


[#-- The key a period-target row is grouped on. Statements reach crp_milestones through
     copy/paste, so the same sentence is stored with a doubled space here and a zero-width
     space left over from a Word paste there. None of it is visible, and none of it may
     split a row -- see impactPathway/outcomes.ftl, which groups on the same key. --]
[#function opiStmtKey raw]
  [#return ((raw!"")?replace("\xA0", " ")?replace("\x200B", "")?replace("\x200C", "")?replace("\x200D", "")?replace("\xFEFF", "")?replace("\\s+", " ", "r"))?trim /]
[/#function]

[#-- One period target: the three values and the two narratives the cluster reports for a
     single milestone, whether that milestone is the indicator's own statement or one of
     its disaggregated targets.

     Which field is open follows the phase that owns it -- the PMC target is set in AWPB,
     the expected value in planning, the achieved value in the annual report -- and, on top
     of that, only the period of the current cycle can be edited at all; earlier and later
     periods stay read-only so a closed cycle is never rewritten from here.

     Every field is rendered even when it is closed: customForm keeps a hidden input
     carrying the stored value, and ProjectOutcomeAction.saveMilestones() writes back
     whatever the form posts, so a field left out of the markup would be saved as empty. --]
[#-- One additional question: the statement configured in Overall Performance
     Indicators, numbered, with the cluster's answer underneath. The question is
     the field's label, so customForm's own label is suppressed and the required
     marker is rendered next to the statement instead. --]
[#macro cpiQuestion element index]
  [#local projectOutcomeIndicator = action.getIndicator(element.id) /]
  [#local customName = "projectOutcome.indicators[${index}]" /]
  <div class="cpi-question">
    <span class="cpi-question__n">${index + 1}</span>
    <div class="cpi-question__body">
      <span class="cpi-question__text">
        [#-- decodeHTML is the functional class (global.js unescapes the stored HTML).
             trumbowyg-editor is the editor's own chrome -- inset shadow, 80px
             min-height, 10px padding and a forced #505050 -- so it is left out. --]
        <span class="decodeHTML">${(element.indicator)!}</span>
        [#if editable]<span class="cpi-question__req">*</span>[/#if]
      </span>
      <input type="hidden" name="${customName}.id" value="${(projectOutcomeIndicator.id)!}" />
      <input type="hidden" name="${customName}.crpProgramOutcomeIndicator.id" value="${(projectOutcomeIndicator.crpProgramOutcomeIndicator.id)!}" />
      <div class="cpi-field cpi-field--text ${editable?string('is-edit','is-read')}">
        [@customForm.textArea name="${customName}.narrative" i18nkey="projectOutcomeBaseline.expectedNarrative" value="${(projectOutcomeIndicator.narrative)!}" required=true className="limitWords-150" editable=editable showTitle=false fieldEmptyText="projectContributionCrp.notAnswered" /]
      </div>
    </div>
  </div>
[/#macro]

[#macro cpiMilestoneFields element year isPrincipal=false]
  [#local projectMilestone = action.getMilestone(element.id, year) /]
  [#local projectMilestoneIndex = action.getIndexMilestone(element.id, year) /]
  [#local customName = "projectOutcome.milestones[${projectMilestoneIndex}]" /]

  [#-- An extended milestone reports against the year it was extended to. --]
  [#local milestoneYear = (element.year)!currentCycleYear /]
  [#if (element.extendedYear?has_content) && (element.extendedYear != -1)]
    [#local milestoneYear = element.extendedYear /]
  [/#if]

  [#local isCurrentPeriod = isYearRequired(milestoneYear) /]
  [#local achievedPhase = reportingActive || action.isUpKeepActive() /]
  [#local canSetted = editable && action.canAccessSuperAdmin() && isCurrentPeriod /]
  [#local canExpected = editable && !reportingActive && isCurrentPeriod /]
  [#local canAchieved = editable && achievedPhase && isCurrentPeriod /]

  <div class="cpi-fields">
    <input type="hidden" name="${customName}.id" value="${(projectMilestone.id)!}" />
    <input type="hidden" name="${customName}.year" class="crpMilestoneYearInput" value="${(year)!}" />
    <input type="hidden" name="${customName}.crpMilestone.id" value="${(element.id)!}" class="crpMilestoneId" />

    <div class="cpi-fields__top">
      <div class="cpi-fields__main">
        [#if isPrincipal]
          <div class="cpi-pane__head">
            <span class="cpi-pane__headline">
              [@s.text name="projectContributionCrp.overallTargetTo" /] ${year?c}:
              <strong>[#if (element.value)?has_content]${element.value?string(",##0")}[#else]&mdash;[/#if]</strong>
              <span class="cpi-chip">[@s.text name="projectContributionCrp.inheritedFromOpi" /]</span>
            </span>
          </div>
        [/#if]
        <div class="cpi-fields__values"[#if !showOutcomeValue] style="display:none"[/#if]>
          [#-- The label is drawn here rather than by customForm so the help sits on the
               label's own line, right of the text. customForm prints its help as an
               <img title> after a block label, which drops it onto a line of its own,
               and only while the field is editable. --]
          <div class="cpi-field ${canSetted?string('is-edit','is-read')}">
            <span class="cpi-field__label">
              <label for="${customName}.settedValue" class="${canSetted?string('editable','readOnly')}">[@s.text name="projectOutcomeMilestone.settedValue" /]:</label>
              [@cpiHelp key="projectOutcomeMilestone.pmcValue.helpText" /]
            </span>
            [@customForm.input name="${customName}.settedValue" i18nkey="projectOutcomeMilestone.settedValue" type="text" placeholder="" className="targetValue targetValueNumber" required=false editable=canSetted showTitle=false /]
            <span class="cpi-field__note">[@s.text name="projectContributionCrp.pmcNote" /]</span>
          </div>
          <div class="cpi-field ${canExpected?string('is-edit', (reportingActive || !isCurrentPeriod)?string('is-read','is-locked'))}">
            [@customForm.input name="${customName}.expectedValue" i18nkey="projectOutcomeMilestone.finalExpectedValue" type="text" placeholder="" className="targetValue targetValueNumber" required=isCurrentPeriod editable=canExpected /]
            [#if !canExpected && !isCurrentPeriod]<span class="cpi-field__note">[@s.text name="projectContributionCrp.otherPeriod" /]</span>[/#if]
          </div>
          <div class="cpi-field ${canAchieved?string('is-edit', achievedPhase?string('is-read','is-locked'))}">
            [@customForm.input name="${customName}.achievedValue" i18nkey="projectOutcomeMilestone.achievedValue" type="text" placeholder="" className="${reportingActive?string('fieldFocus','')} targetValue targetValueNumber" required=isCurrentPeriod && achievedPhase editable=canAchieved /]
            [#if !achievedPhase]<span class="cpi-field__note">[@s.text name="projectContributionCrp.opensInReporting" /]</span>[/#if]
          </div>
        </div>
      </div>

      [#-- Evidence already linked to the indicator: the headline's companion, so it only
           appears on the principal target of the period being reported. --]
      [#if isPrincipal && isCurrentPeriod]
        <div class="cpi-fields__relations">
          [@popUps.relationsMacro element=projectOutcome labelText=true /]
          [@popUps.relationsMacro element=projectOutcome tag="expectedOutcomes" labelText=true /]
          [@popUps.relationsMacro element=projectOutcome tag="innovationOutcomes" labelText=true /]
        </div>
      [/#if]
    </div>

    <div class="cpi-field cpi-field--text ${canExpected?string('is-edit','is-read')}">
      [@customForm.textArea name="${customName}.narrativeTarget" i18nkey="projectOutcomeMilestone.expectedNarrative2021" required=isCurrentPeriod className="limitWords-200" editable=canExpected help="projectOutcomeMilestone.expectedNarrative2021.helpText" helpIcon=false /]
    </div>
    <div class="cpi-field cpi-field--text ${canAchieved?string('is-edit', achievedPhase?string('is-read','is-locked'))}">
      [@customForm.textArea name="${customName}.narrativeAchieved" i18nkey="projectOutcomeMilestone.achievedNarrative" required=isCurrentPeriod && achievedPhase className="limitWords-100 ${reportingActive?string('fieldFocus','')}" editable=canAchieved /]
      [#if !achievedPhase]<span class="cpi-field__note">[@s.text name="projectContributionCrp.opensInReporting" /]</span>[/#if]
    </div>
  </div>
[/#macro]

[#-- A field's help, as a quiet icon beside its label. The text reaches the reader
     through the app-wide jQuery UI tooltip, which every [title] gets on hover and on
     keyboard focus. s.text hands back the key itself when a program has no text for
     it, so an untranslated key renders nothing rather than a tooltip reading the key. --]
[#macro cpiHelp key]
  [#local text][@s.text name=key /][/#local]
  [#local plain = (text?is_markup_output)?then(text?markup_string, text)?trim /]
  [#if plain?has_content && plain != key]
    <span class="cpi-help" tabindex="0" role="img" aria-label="${text}" title="${text}">?</span>
  [/#if]
[/#macro]

[#macro nextUserMacro element name index isTemplate=false]
  <div id="nextUser-${isTemplate?string('template', index)}" class="nextUser simpleBox" style="display:${isTemplate?string('none','block')}">
    [#local customName = "${name}[${index}]" /]
    [#-- Remove Button --]
    [#if editable && !reportingActive]<div class="removeIcon removeNextUser" title="Remove"></div>[/#if]
    <div class="leftHead sm">
      <span class="index">${index+1}</span>
      <span class="elementId">[@s.text name="projectOutcomeNextUser.projectNextUser" /]</span>
    </div>
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
     
    <div class="form-group">
      [#-- Title --]
      <div class="form-group input-container">
        [@customForm.input name="${customName}.nextUser" i18nkey="projectOutcomeNextUser.title" help="projectOutcomeNextUser.title.help" required=true className="limitWords-20" editable=editable && (!reportingActive || (!element.nextUser?has_content)!true) /]
      </div>
      [#-- Knowledge, attitude, skills and practice changes expected in this next user --]
      <div class="form-group ">
        [@customForm.textArea name="${customName}.knowledge" i18nkey="projectOutcomeNextUser.knowledge" help="projectOutcomeNextUser.knowledge.help" required=true className="limitWords-100" editable=editable && (!reportingActive || (!element.knowledge?has_content)!true) /]
      </div>
      [#-- Strategies will be used to encourage and enable this next user to utilize deliverables and adopt changes --]
      <div class="form-group ">
        [@customForm.textArea name="${customName}.strategies" i18nkey="projectOutcomeNextUser.strategies" help="projectOutcomeNextUser.strategies.help" required=true className="limitWords-100" editable=editable && (!reportingActive || (!element.strategies?has_content)!true) /]
      </div>
     
    </div>
    
    [#-- Reporting --]
    [#if reportingActive]
      <br /> 
      <div class="" id="nextUserYear-${currentCycleYear}">
        <div class="form-group ">
          [@customForm.textArea name="${customName}.knowledgeReport" i18nkey="projectOutcomeNextUser.reportOnProgress" help="" required=true className="limitWords-200 ${reportingActive?string('fieldFocus','')}" editable=editable /]
        </div>
        <div class="form-group ">
          [@customForm.textArea name="${customName}.strategiesReport" i18nkey="projectOutcomeNextUser.strategiesEncourage" help="" required=true className="limitWords-100 ${reportingActive?string('fieldFocus','')}" editable=editable /]
        </div> 
        <div class="clearfix"></div>
      </div> 
    [/#if]
  </div>
[/#macro]

[#macro baselineIndicatorMacro element name index isTemplate=false]
  <div id="baselineIndicator-${isTemplate?string('template', index)}" class="baselineIndicator simpleBox" style="display:${isTemplate?string('none','block')}">
    [#local indexIndicator = action.getIndexIndicator(element.id)! /]
    [#local projectOutcomeIndicator  = action.getIndicator(element.id)! /]
    [#local customName = "${name}[${indexIndicator}]" /]
    <div class="leftHead gray sm">
      <span class="index">${index+1}</span>
    </div>
    <div class="form-group grayBox">
      <div class="decodeHTML trumbowyg-editor">${(element.indicator)!}</div>
    </div>
    <input type="hidden" name="${customName}.id" value="${(projectOutcomeIndicator.id)!}" >
    <input type="hidden" name="${customName}.crpProgramOutcomeIndicator.id" value="${(projectOutcomeIndicator.crpProgramOutcomeIndicator.id)!}" >
    <div class="form-group row">
      <div class="col-md-3">
        [@customForm.input name="${customName}.value" i18nkey="projectOutcomeBaseline.expectedValue" className="targetValue" placeholder="Numeric Value" value="${(projectOutcomeIndicator.value)!}" required=true editable=editable && !reportingActive /]
      </div>
      <div class="col-md-3">
        [#if reportingActive]
          [@customForm.input name="${customName}.valueReporting" i18nkey="projectOutcomeBaseline.achievedValue" className="targetValue" placeholder="Numeric Value" required=true editable=editable /]
        [/#if]
      </div>
      <div class="col-md-3"></div>
    </div>
    <div class="form-group">
      [@customForm.textArea name="${customName}.narrative" i18nkey="projectOutcomeBaseline.expectedNarrative" value="${(projectOutcomeIndicator.narrative)!}" required=true className="limitWords-100" editable=editable && !reportingActive /]
    </div>
    [#if reportingActive]
      <div class="form-group">
        [@customForm.textArea name="${customName}.achievedNarrative" i18nkey="projectOutcomeBaseline.achievedNarrative" required=true className="limitWords-100" editable=editable /]
      </div>
    [/#if]
  </div>
[/#macro]

[#macro baselineAiccraIndicatorMacro element name index isTemplate=false AREditable=true]
  <div id="baselineIndicator-${isTemplate?string('template', index)}" class="baselineIndicator simpleBox" style="display:${isTemplate?string('none','block')}">
    [#local indexIndicator = action.getIndexIndicator(element.id) /]
    [#local projectOutcomeIndicator  = action.getIndicator(element.id) /]
    [#-- [#local customName = "${name}[${indexIndicator}]" /] --]
    [#local customName = "${name}[${index}]" /]
    <div class="leftHead gray sm">
      <span class="index">${index+1}</span>
    </div>
    <div class="form-group grayBox">
      <div class="decodeHTML trumbowyg-editor">${(element.indicator)!}</div>
    </div>
    <input type="hidden" name="${customName}.id" value="${(projectOutcomeIndicator.id)!}" >
    <input type="hidden" name="${customName}.crpProgramOutcomeIndicator.id" value="${(projectOutcomeIndicator.crpProgramOutcomeIndicator.id)!}" >
    
    [#-- [#if index==0]
      <div class="form-group row">
        <div class="col-md-3">
          [@customForm.input name="${customName}.value" i18nkey="projectOutcomeBaseline.expectedValue" className="targetValue" placeholder="Numeric Value" value="${(projectOutcomeIndicator.value)!}" required=true editable=editable && !reportingActive /]
        </div>
        <div class="col-md-3">
          [#if reportingActive]
            [@customForm.input name="${customName}.valueReporting" i18nkey="projectOutcomeBaseline.achievedValue" className="targetValue" placeholder="Numeric Value" required=true editable=editable /]
          [/#if]
        </div>
        <div class="col-md-3"></div>
      </div>
    [/#if]
    --]
    
      <div class="form-group ">
        [@customForm.textArea name="${customName}.narrative" i18nkey="projectOutcomeBaseline.expectedNarrative" value="${(projectOutcomeIndicator.narrative)!}" required=true className="limitWords-150" editable=editable && AREditable/]
        [#-- && !reportingActive  --]
      </div>
      [#--  
      [#if reportingActive]
        <div class="form-group">
          [@customForm.textArea name="${customName}.achievedNarrative" i18nkey="projectOutcomeBaseline.achievedNarrative" required=true className="limitWords-100" editable=editable /]
        </div>
      [/#if]
      --]
  </div>
[/#macro]

[#macro tableParticipantsTrainingsMacro list]
  <table id="tableParticipantsTrainingsMacro" class="annual-report-table table-border">
    <thead>
      <tr class="subHeader">
        <th id="tb-id">[@s.text name="Activity Event" /]</th>
        <th id="tb-title">[@s.text name="Activity Type" /]</th>        
        <th id="tb-organization-type">[@s.text name="Trainees Type" /]</th>
        <th id="tb-type">[@s.text name="Total Trainees" /]</th>
        <th id="tb-type">[@s.text name="Males" /]</th>
        <th id="tb-type">[@s.text name="Females" /]</th>
        <th id="tb-type">[@s.text name="Africans" /]</th>
        <th id="tb-type">[@s.text name="Youth" /]</th>
        <th id="tb-training-period">[@s.text name="Training Period" /]</th>
        [#--  
        <th id="tb-training-period">[@s.text name="Event Focus" /]</th>
        <th id="tb-training-period">[@s.text name="Likely Outcomes" /]</th>
        --]
      </tr>
    </thead>
    <tbody>
    [#-- Loading --]
    [#if list?has_content]
      [#list list as item]
        [#local URL][@s.url namespace="/clusters" action="${(crpSession)!}/deliverable"][@s.param name='deliverableID']${(item.deliverable.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        <tr>
          [#-- Title of Innovation --]
          <td class="">
            [@utils.tableText value=(item.eventActivityName)!"" /] 
            [#-- Deliverable ID --]
            <br /><i style="opacity:0.5"><small>(From D${(item.deliverable.id)!''})</small></i>
            <a href="${URL}" target="_blank" class="pull-right"> <span class="glyphicon glyphicon-new-window"></span> </a>
          </td>
          [#-- Activity Type --]
          <td class="">
            <small>[@utils.tableText value=(item.repIndTypeActivity.name)!"" /]</small>
          </td>          
          [#-- Type of participants --]
          <td class="text-center">
            [@utils.tableText value=(item.repIndTypeParticipant.name)!"" /]
          </td>          
          [#assign knowFemale = (item.dontKnowFemale)!false]
          [#assign hasFemale = (item.females?has_content)!false]
          [#assign isEstimateTotalParticipants = (item.estimateParticipants?has_content)!false]
          [#assign isEstimateFemales = (item.estimateFemales?has_content)!false]
          [#assign isEstimateAfricans = (item.estimateAfrican?has_content)!false]
          [#assign isEstimateYouth = (item.estimateYouth?has_content)!false]
          [#-- Total Participants --]
          <td class="text-center">
            ${(item.participants?number?string(",##0"))!0}
            [#if isEstimateTotalParticipants ]
              <i><small> (Estimated value)</small></i>
            [/#if]
          </td>
          [#-- Number of males --]
          <td class="text-center">
            [#if knowFemale && !hasFemale ]
              <i><small>Not specified</small></i>
              [#else]
              ${(item.males?number?string(",##0"))!0}
            [/#if]
          </td>
          [#-- Number of females --]
          <td class="text-center">
          [#if knowFemale && !hasFemale ]
            <i><small>Not specified</small></i>
            [#else]
            ${(item.females?number?string(",##0"))!0}
            [#if isEstimateFemales ]
              <i><small> (Estimated value)</small></i>
            [/#if]
          [/#if]
          </td>
          [#-- Number of african --]
          <td class="text-center">
            ${(item.african?number?string(",##0"))!0}
            [#--<p><i><small>(${(item.africanPercentage?number?string(",##0"))!0}% )</small></i><p>--]
            [#if isEstimateAfricans ]
              <i><small> (Estimated value)</small></i>
            [/#if]
          </td>
          [#-- Number of youth --]
          <td class="text-center">
            ${(item.youth?number?string(",##0"))!0}
            [#--<p><i><small>(${(item.youthPercentage?number?string(",##0"))!0}% )</small></i></p>--]
            [#if isEstimateYouth ]
              <i><small> (Estimated value)</small></i>
            [/#if]
          </td>
          [#-- Training period of time --]
          <td class="text-center">
            [@utils.tableText value=(item.repIndTrainingTerm.name)!"" /]
          </td>
          [#-- Training period of time --]
          [#--  
          <td class="text-center">
            [@utils.tableText value=(item.focus)!"" /]
          </td>
          --]
          [#-- Training period of time --]
          [#--  
          <td class="text-center">
            [@utils.tableText value=(item.likelyOutcomes)!"" /]
            --]
          </td>
        </tr>
      [/#list]
    [#else]
      <tr>
        <td class="text-center" colspan="5">
          <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
        </td>
      </tr>
    [/#if]
    </tbody>
  </table>
[/#macro]

[#macro tableParticipantsTrainingsMacroSharedCluster list]
  <table id="tableParticipantsTrainingsMacro" class="annual-report-table table-border">
    <thead>
      <tr class="subHeader">
        <th colspan="5" id="tb-type-1">[@s.text name="involveParticipants.sharedClusters.popup.subtitle1" /]</th>
        <th colspan="6" id="tb-type-2" class="dark-bg_table_values">[@s.text name="involveParticipants.sharedClusters.popup.subtitle2" /]</th>
      </tr>
      <tr class="subHeader">
        <th id="tb-type">[@s.text name="involveParticipants.sharedClusters.popup.activity" /]</th>
        <th id="tb-type">[@s.text name="involveParticipants.sharedClusters.popup.participants" /]</th>        
        <th id="tb-type">[@s.text name="involveParticipants.sharedClusters.popup.females" /]</th>
        <th id="tb-type">[@s.text name="involveParticipants.sharedClusters.popup.africans" /]</th>
        <th id="tb-type">[@s.text name="involveParticipants.sharedClusters.popup.youth" /]</th>       
        
        <th id="tb-type" class="dark-bg_table_values">[@s.text name="Total Trainees" /]</th>
        <th id="tb-type" class="dark-bg_table_values">[@s.text name="Females" /]</th>
        <th id="tb-type" class="dark-bg_table_values">[@s.text name="Africans" /]</th>
        <th id="tb-type" class="dark-bg_table_values">[@s.text name="Youth" /]</th>
        <th id="tb-title" class="dark-bg_table_text">[@s.text name="Activity Type" /]</th>        
        <th id="tb-organization-type" class="dark-bg_table_text">[@s.text name="Trainees Type" /]</th>
        [#--  
        <th id="tb-training-period">[@s.text name="Event Focus" /]</th>
        <th id="tb-training-period">[@s.text name="Likely Outcomes" /]</th>
        --]
      </tr>
    </thead>
    <tbody>
    [#-- Loading --]
    [#if list?has_content]
      [#list list as item]
        [#local URL][@s.url namespace="/clusters" action="${(crpSession)!}/deliverable"][@s.param name='deliverableID']${(item.deliverable.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        <tr>
          [#-- Title of Innovation --]
          <td class="">
            [@utils.tableText value=(item.eventActivityName)!"" /] 
            [#-- Deliverable ID --]
            <br /><i style="opacity:0.5"><small>(From D${(item.deliverable.id)!''})</small></i>
            <a href="${URL}" target="_blank" class="pull-right"> <span class="glyphicon glyphicon-new-window"></span> </a>
          </td>
          
          [#-- Own participants --]
          <td class="text-center">
              ${(item.ownTrainess?number?string(",##0"))!0}
          </td>
          [#-- Own Females --]
          <td class="text-center">
              ${(item.ownFemales?number?string(",##0"))!0}
          </td>
          [#-- Own African --]
          <td class="text-center">
              ${(item.ownAfricans?number?string(",##0"))!0}
          </td>
          [#-- Own Youth --]
          <td class="text-center">          
              ${(item.ownYouth?number?string(",##0"))!0}
          </td>
          
          [#assign knowFemale = (item.dontKnowFemale)!false]
          [#assign hasFemale = (item.females?has_content)!false]
          [#assign isEstimateTotalParticipants = (item.estimateParticipants?has_content)!false]
          [#assign isEstimateFemales = (item.estimateFemales?has_content)!false]
          [#assign isEstimateAfricans = (item.estimateAfrican?has_content)!false]
          [#assign isEstimateYouth = (item.estimateYouth?has_content)!false]
          [#-- Total Participants --]
          <td class="text-center dark-bg_table_values">
            ${(item.participants?number?string(",##0"))!0}
            [#if isEstimateTotalParticipants ]
              <i><small> (Estimated value)</small></i>
            [/#if]
          </td>
          [#-- Number of females --]
          <td class="text-center dark-bg_table_values">
          [#if knowFemale && !hasFemale ]
            <i><small>Not specified</small></i>
            [#else]
            ${(item.females?number?string(",##0"))!0}
            [#if isEstimateFemales ]
              <i><small> (Estimated value)</small></i>
            [/#if]
          [/#if]
          </td>
          [#-- Number of african --]
          <td class="text-center dark-bg_table_values">
            ${(item.african?number?string(",##0"))!0}
            [#--<p><i><small>(${(item.africanPercentage?number?string(",##0"))!0}% )</small></i><p>--]
            [#if isEstimateAfricans ]
              <i><small> (Estimated value)</small></i>
            [/#if]
          </td>
          [#-- Number of youth --]
          <td class="text-center dark-bg_table_values">
            ${(item.youth?number?string(",##0"))!0}
            [#--<p><i><small>(${(item.youthPercentage?number?string(",##0"))!0}% )</small></i></p>--]
            [#if isEstimateYouth ]
              <i><small> (Estimated value)</small></i>
            [/#if]
          </td>
          [#-- Activity Type --]
          <td class="dark-bg_table_text">
            <small>[@utils.tableText value=(item.repIndTypeActivity.name)!"" /]</small>
          </td>          
          [#-- Type of participants --]
          <td class="text-center dark-bg_table_text">
            [@utils.tableText value=(item.repIndTypeParticipant.name)!"" /]
          </td>          
                  
          </td>
        </tr>
      [/#list]
        <tr class="">
          <th id="tb-footer" class="">[@utils.tableText "Total Reported" /]</th>
          <th id="tb-footer" class="text-center">[@utils.tableText value=(totalOwnParticipants)!"" /]</th>
          <th id="tb-footer" class="text-center">[@utils.tableText value=(totalOwnFemales)!"" /]</th>
          <th id="tb-footer" class="text-center">[@utils.tableText value=(totalOwnAfricans)!"" /]</th>
          <th id="tb-footer" class="text-center">[@utils.tableText value=(totalOwnYouth)!"" /]</th>
          <th id="tb-footer" class="text-center">[@utils.tableText value=(totalParticipants)!"" /]</th>
          <th id="tb-footer" class="text-center">[@utils.tableText value=(totalFemales)!"" /]</th>
          <th id="tb-footer" class="text-center">[@utils.tableText value=(totalAfricans)!"" /]</th>
          <th id="tb-footer" class="text-center">[@utils.tableText value=(totalYouth)!"" /]</th>
        </tr>
    [#else]
      <tr>
        <td class="text-center" colspan="5">
          <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
        </td>
      </tr>
    [/#if]
    </tbody>
  </table>
[/#macro]


[#macro tableJournalsMacro list]
  <table id="tableJournalsMacro" class="annual-report-table table-border">
    <thead>
      <tr class="subHeader">
        <th id="tb-id">[@s.text name="Deliverable Title" /]</th>
        <th id="tb-title">[@s.text name="Deliverable Year" /]</th>        
        <th id="tb-organization-type">[@s.text name="Shared Clusters" /]</th>
      </tr>
    </thead>
    <tbody>
    [#-- Loading --]
    [#if list?has_content]
      [#list list as item]
        [#local URL][@s.url namespace="/clusters" action="${(crpSession)!}/deliverable"][@s.param name='deliverableID']${(item.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        <tr>
          [#-- ID  --]
          <td class="">
          D${(item.id)!''} - [@utils.tableText value=(item.deliverableInfo.title)!"" /]
            <a href="${URL}" target="_blank" class="pull-right"> <span class="glyphicon glyphicon-new-window"></span> </a>
          </td>
   
          [#-- Year --]
          <td class="text-center">
            
            [#if item.deliverableInfo.year== -1]
              None
            [#else]
              [#if
                [#-- ((deliverable.deliverableInfo.status == 4 || deliverable.deliverableInfo.status==3)!false ) --]
                      ((item.deliverableInfo.status == 4 || item.deliverableInfo.status==3 || item.deliverableInfo.status==5)!false )
                      && ((item.deliverableInfo.newExpectedYear != -1)!false)
                    ]
                ${item.deliverableInfo.newExpectedYear} (Extended from 
                ${(item.deliverableInfo.year)!'None'})
              [#else]
                ${(item.deliverableInfo.year)!'None'}
              [/#if]
                            
            [/#if]
          </td>
          [#-- Shared Clusters Type --]
          <td class="text-center">
            [#if item.sharedWithProjects?has_content]${(item.sharedWithProjects)!}[#else]Not shared[/#if]
          </td>          
          
          </td>
        </tr>
      [/#list]
    [#else]
      <tr>
        <td class="text-center" colspan="5">
          <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
        </td>
      </tr>
    [/#if]
    </tbody>
  </table>
[/#macro]

[#-- Get if the year is required--]
[#function isYearRequired year]
  [#if project.projectInfo.endDate??]
    [#assign endDate = (project.projectInfo.endDate?string.yyyy)?number]
    [#if reportingActive]
      [#return  (year == currentCycleYear)  && (endDate gte year) ]
    [#else]
      [#return  (year == currentCycleYear) && (endDate gte year) ]
    [/#if]
  [#else]
    [#return false]
  [/#if]
[/#function]