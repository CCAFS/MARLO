[#ftl]
[#assign title = "Impact Pathway - Outcomes" /]
[#assign currentSectionString = "program-${actionName?replace('/','-')}-${crpProgramID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "blueimp-file-upload", "cytoscape","cytoscape-panzoom", "trumbowyg"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/impactPathway/programSubmit.js",
  "${baseUrlMedia}/js/impactPathway/outcomes.js?2026083115",
  [#-- "${baseUrlCdn}/global/js/autoSave.js", --]
  "${baseUrlCdn}/global/js/impactGraphic.js",
  "${baseUrlCdn}/global/js/fieldsValidation.js",
   "//cdn.datatables.net/1.13.1/js/jquery.dataTables.min.js"
  ]
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/impactPathway/outcomes.css?2026083116",
  "${baseUrlCdn}/global/css/impactGraphic.css",
  "//cdn.datatables.net/1.13.1/css/jquery.dataTables.min.css"
  ]
/]
[#assign currentSection = "impactPathway" /]
[#assign currentStage = "outcomes" /]


[#assign breadCrumb = [
  {"label":"impactPathway", "nameSpace":"", "action":"outcomes"},
  {"label":"outcomes", "nameSpace":"", "action":""}
]/]



[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/crp/macros/relationsPopupMacro.ftl" as popUps /]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

<!--
<div class="container helpText viewMore-block">
  <div style="display:none;" class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="outcomes.help" /] </p>
  </div>
  <div style="display:none" class="viewMore closed"></div>
</div>
-->

<section class="marlo-content opi-page">
  <div class="container">
    [#if programs?has_content]

      [#-- AICCRA calls these "indicators"; every other global unit calls them "outcomes". --]
      [#if action.isAiccra()]
        [#assign countNounOne][@s.text name="outcomes.status.count.one"/][/#assign]
        [#assign countNounMany][@s.text name="outcomes.status.count.many"/][/#assign]
      [#else]
        [#assign countNounOne][@s.text name="outcomes.status.count.one.outcome"/][/#assign]
        [#assign countNounMany][@s.text name="outcomes.status.count.many.outcome"/][/#assign]
      [/#if]

      [#-- i18n carrier for outcomes.js (no user-facing literals in the JS) --]
      <span id="opiI18n" style="display:none"
        data-editable="${editable?string}"
        data-now-year="${(actualPhase.year)!''}"
        data-button-show="[@s.text name="form.buttons.show"/]"
        data-button-hide="[@s.text name="form.buttons.hide"/]"
        data-status-complete="[@s.text name="outcomes.status.complete"/]"
        data-status-missing-one="[@s.text name="outcomes.status.missing.one"/]"
        data-status-missing-many="[@s.text name="outcomes.status.missing.many"/]"
        data-count-one="${countNounOne}"
        data-count-many="${countNounMany}"
        data-summary-complete="[@s.text name="outcomes.summary.allComplete"/]"
        data-summary-missing-one="[@s.text name="outcomes.summary.missing.one"/]"
        data-summary-missing-many="[@s.text name="outcomes.summary.missing.many"/]"
        data-required-label="[@s.text name="outcomes.matrix.required"/]"
        data-q-one="[@s.text name="outcomes.questions.count.one"/]"
        data-q-many="[@s.text name="outcomes.questions.count.many"/]"
        data-collapse-all="[@s.text name="outcomes.collapseAll"/]"
        data-expand-all="[@s.text name="outcomes.expandAll"/]"
        data-save-unsaved="[@s.text name="outcomes.saveBar.unsaved"/]"
        data-save-unsaved-detail="[@s.text name="outcomes.saveBar.unsaved.detail"/]"></span>

      [#-- How this section works --]
      <div class="opi-help" id="opiHelp">
        <div class="opi-help__head">
          <svg width="17" height="17" viewBox="0 0 18 18" fill="none" aria-hidden="true"><circle cx="9" cy="9" r="7.2" stroke="#015C7D" stroke-width="1.5"></circle><path d="M9 8.1v4.3" stroke="#015C7D" stroke-width="1.6" stroke-linecap="round"></path><circle cx="9" cy="5.6" r="1" fill="#015C7D"></circle></svg>
          <h2 class="opi-help__title">[@s.text name="outcomes.help.title" /]</h2>
          <button type="button" class="opi-help__toggle" aria-expanded="true" aria-controls="opiHelpBody">[@s.text name="form.buttons.hide" /]</button>
        </div>
        <div class="opi-help__body" id="opiHelpBody">[@s.text name="outcomes.help" /]</div>
      </div>

      <div class="opi-layout">

        [#-- Components sidebar (design's card; the shared dropdown menu stays on clusterActivities) --]
        <aside class="opi-sidebar">
          [#include "/WEB-INF/crp/views/impactPathway/sidebar-opi.ftl" /]
        </aside>

        <div class="opi-main">

          [#-- Section Messages --]
          [#include "/WEB-INF/crp/views/impactPathway/messages-impactPathway.ftl" /]

          [#-- Check if the programID is Valid --]
          [#assign hasAvailableProgramID = false ]
          [#list programs as program]
            [#if (crpProgramID == program.id)!false]
              [#assign hasAvailableProgramID = true ]
              [#break]
            [/#if]
          [/#list]

          [@s.form action=actionName ]

          [#if hasAvailableProgramID]
            [#assign outcomesCount = (outcomesForm?size)!0 /]

            <div class="opi-mainHead">
              <div>
                <h1 class="opi-mainHead__title">[@s.text name="outcomes.title"][@s.param]${(selectedProgram.acronym)!}[/@s.param][/@s.text]</h1>
                <span class="opi-mainHead__summary" data-opi-summary>
                  ${outcomesCount} [#if outcomesCount == 1]${countNounOne}[#else]${countNounMany}[/#if]
                </span>
              </div>
              <div class="cont-btn-min">
                <button type="button" class="btn-expand-all-outcomes btn btn-link">[@s.text name="outcomes.collapseAll"/]</button>
              </div>
            </div>

            <div class="outcomes-list" listname="outcomes">
            [#if outcomesForm?has_content]
              [#list outcomesForm as outcome]
                [@outcomeMacro outcome=outcome name="outcomesForm" index=outcome_index /]
              [/#list]
            [#else]
              [@outcomeMacro outcome={} name="outcomesForm" index=0 /]
            [/#if]
            </div>

            [#-- Add Outcome Button --]
            [#if editable]
              <div class="addOutcome bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addOutcome"/]</div>
            [/#if]
          [#else]
            <div class="opi-empty">
              <span class="opi-empty__title">Please select a [@s.text name="global.flagship" /]</span>
            </div>
          [/#if]

            [#-- Section Buttons--]
            <div class="opi-saveBar">
              [#-- Save state only means something while the form can be edited. --]
              [#if editable]
              <span class="opi-saveBar__state">
                <span class="opi-saveBar__dot"></span>
                <span data-opi-save-state>[@s.text name="outcomes.saveBar.saved"/]</span>
              </span>
              <span class="opi-saveBar__detail" data-opi-save-detail></span>
              [/#if]
              <div class="opi-saveBar__actions">
                [#include "/WEB-INF/crp/views/impactPathway/buttons-impactPathway.ftl" /]
              </div>
            </div>

          [/@s.form]
        </div>
      </div>
    [#else]
      <div class="opi-empty">
        <span class="opi-empty__title">[@s.text name="impactPathway.noFlagshipsAdded" /]</span>
      </div>
    [/#if]
  </div>
</section>

[#-- PopUp to select SubIDOs --]
<div id="subIDOs-graphic" style="overflow:auto; display:none;" >
  <div class="graphic-container" >
  <div class="filterPanel panel-default">
    <div class="panel-heading">
      <form id="filterForm"  role="form">
        <label class="checkbox-inline">Filter By:</label>
        <label class="checkbox-inline">
          <input type="checkbox" value="IDO" checked>IDOs
        </label>
        <label class="checkbox-inline">
          <input type="checkbox" value="CCIDO" checked>Cross-cutting IDOs
        </label>
      </form>
    </div>
  </div>
  [#list srfIdos as ido]
    <div class="idoWrapper ${ido.isCrossCutting?string("crossCutting","ido")} ">
      <div class="IDO${ido.isCrossCutting?string("-CrossCutting","")}"><strong>${ido.isCrossCutting?string("CrossCutting:","")} ${ido.description}</strong></div>
      <div class="subIdoWrapper">
        [#list ido.subIdos as subIdo]
          <div class="line"></div>
          <div id="subIdo-${subIdo.id}" class="subIDO subIDO${ido.isCrossCutting?string("-CrossCutting","")}">${subIdo.smoCode} ${subIdo.description}</div>
        [/#list]
      </div>
    </div>
  [/#list]
  </div>
</div>

[#-- Add other target unit --]
<div id="dialog-targetUnit" class="text-center" style="display:none" title="New Target Unit">
  <div class="form-group text-center">
    <label for="targetUnitName">Insert the new Target Unit</label>
    <input type="text" class="form-control" id="targetUnitName" placeholder="">
  </div>
</div>

[#-- Outcome Template --]
[@outcomeMacro outcome={} name="outcomesForm" index=-1 isTemplate=true /]

[#-- Milestone Template --]
[@milestoneMacro milestone={} name="outcomesForm[0].milestones" index=-1 isTemplate=true  /]

[#-- Matrix Cell Template (AICCRA period-target cells) --]
[@opiCellMacro milestone={} name="outcomesForm[0].milestones" index=-1 isTemplate=true /]

[#-- Sub-Ido Template --]
[@subIDOMacro subIdo={} name="outcomesForm[0].subIdos" index=-1 isTemplate=true /]

[#-- Assumption Template --]
[@assumptionMacro assumption={} name="outcomesForm[-1].subIdos[-1].assumptions" index=-1 isTemplate=true /]

[#-- Baseline Indicator Template --]
[@baselineIndicatorMacro indicator={} name="outcomesForm[-1].indicators" index=-1 isTemplate=true /]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#-----------------------------------  Outcomes Macros  -------------------------------------------]

[#macro outcomeMacro outcome name index isTemplate=false]
  [#assign outcomeCustomName= "${name}[${index}]" /]
  [#local isAiccraUI = action.isAiccra() /]
  [#local showBaselineBlock = action.hasSpecificities('crp_baseline_indicators') && (selectedProgram.baseLine)!false /]
  [#local milestoneCount = (outcome.milestones?size)!0 /]
  [#local indicatorCount = (outcome.indicators?size)!0 /]
  [#local subIdoCount = (outcome.subIdos?size)!0 /]
  [#local nowYear = (actualPhase.year)!-99 /]

  [#-- Group the flat milestone list into the design's matrix: distinct statements
       are the disaggregation rows, distinct years are the period-target columns. --]
  [#local rowStmts = [] /]
  [#local yearCols = [] /]
  [#if !isTemplate && outcome.milestones?has_content]
    [#list outcome.milestones as m]
      [#local mStmt = ((m.title)!"")?trim /]
      [#if !rowStmts?seq_contains(mStmt)][#local rowStmts = rowStmts + [mStmt] /][/#if]
      [#local mYear = (m.year)!-1 /]
      [#if !yearCols?seq_contains(mYear)][#local yearCols = yearCols + [mYear] /][/#if]
    [/#list]
    [#local yearCols = yearCols?sort /]
  [/#if]
  [#-- The row matching the outcome statement is the principal one and goes first. --]
  [#local outcomeStmt = ((outcome.description)!"")?trim /]
  [#if rowStmts?seq_contains(outcomeStmt)]
    [#local reordered = [outcomeStmt] /]
    [#list rowStmts as s][#if s != outcomeStmt][#local reordered = reordered + [s] /][/#if][/#list]
    [#local rowStmts = reordered /]
  [/#if]
  [#if rowStmts?size == 0][#local rowStmts = [outcomeStmt] /][/#if]
  [#local hasDis = (rowStmts?size > 1) /]
  [#local gridCols = "minmax(260px,1fr)" /]
  [#list yearCols as y][#local gridCols = gridCols + " 132px" /][/#list]
  [#local gridCols = gridCols + " 88px" /]

  <div id="outcome-${isTemplate?string('template', index)}" class="outcome opi-card form-group" style="display:${isTemplate?string('none','block')}">

    [#-- Outcome ID Parameters --]
    <input type="hidden" class="outcomeId" name="${outcomeCustomName}.id" value="${(outcome.id)!}"/>
    <input type="hidden" class="outcomeComposeId" name="${outcomeCustomName}.composeID" value="${(outcome.composeID)!}"/>

    [#-- Card head --]
    <div class="opi-card__head">
      <button type="button" class="btn-expand-Outcome opi-card__caret" aria-expanded="true" aria-label="Toggle indicator">
        <svg width="10" height="10" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M4 2.5 7.5 6 4 9.5" stroke="#4B5563" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"></path></svg>
      </button>
      <span class="opi-card__code">[#if (outcome.acronym)?has_content]${outcome.acronym}[#else]${(outcome.year)!'New'}[/#if]</span>
      <span class="opi-card__ident">
        <span class="opi-card__name" data-opi-cardname>[#if (outcome.description)?has_content]${outcome.description}[#else][@s.text name="outcome.index.title"/][/#if]</span>
        <span class="opi-card__meta">
          [#if isAiccraUI]${rowStmts?size - 1} [@s.text name="outcomes.card.disaggregations"/][#if (outcome.srfTargetUnit.name)?has_content] &middot; ${outcome.srfTargetUnit.name}[/#if] &middot; [@s.text name="outcomes.card.closing"/] ${(outcome.year)!'—'}[#if (outcome.value)?has_content]: ${outcome.value}[/#if] &middot; ${yearCols?size} [@s.text name="outcomes.card.years"/][#else]${(selectedProgram.acronym)!} &middot; [@s.text name="outcome.targetYear"/]: ${(outcome.year)!'—'} &middot; ${milestoneCount} [@s.text name="outcome.milestone.sectionTitle"/][/#if]
        </span>
      </span>
      [#if editable]<span class="opi-status" data-opi-status></span>[/#if]
      [#if !isTemplate]
        <span class="opi-card__relations">[@popUps.relationsMacro element=outcome /]</span>
      [/#if]
      [#-- Remove Button --]
      [#if editable && action.canBeDeleted((outcome.id)!-1,(outcome.class.name)!"" )]
        <button type="button" class="removeOutcome opi-card__delete" title="Remove Outcome" aria-label="Remove Outcome">&#10005;</button>
      [#elseif editable]
        <button type="button" class="opi-card__delete disable" disabled title="[@s.text name="global.CrpProgramOutcome"/] can not be deleted" aria-label="Can not be deleted">&#10005;</button>
      [/#if]
    </div>

    [#-- Card body --]
    <div class="opi-card__body to-minimize-outcome">

      [#-- Portfolio --]
      [#if action.hasSpecificities('portfolio_feature_active')]
        <div class="opi-grid5">
          <div>
            [@customForm.select name="${outcomeCustomName}.portfolio.id" i18nkey="outcome.portfolio" listName="portfolios" keyFieldName="id" displayFieldName="name" required=true  className="milestoneStatus opi-select" editable=editable /]
          </div>
        </div>
      [/#if]

      [#-- Acronym + Statement row --]
      <div class="opi-fieldRow">
        <div class="opi-fieldRow__acronym">
          [@customForm.input name="${outcomeCustomName}.acronym" value="${(outcome.acronym)!}" type="text" i18nkey="outcome.acronym" required=false editable=editable /]
        </div>
        <div class="opi-fieldRow__statement">
          [@customForm.textArea name="${outcomeCustomName}.description"  i18nkey="${isAiccraUI?string('outcome.statementIndicator','outcome.statement')}" required=true className="outcome-statement limitWords-100" editable=editable /]
        </div>
      </div>

      [#-- Outcome Indicator --]
      [#if action.hasSpecificities('crp_ip_outcome_indicator')]
      <div class="form-group">
        [@customForm.textArea name="${outcomeCustomName}.indicator"  i18nkey="outcome.inidicator" required=false className="outcome-inidicator limitWords-100" editable=editable /]
      </div>
      [/#if]

      [#-- Baseline year / Closing year / Target unit / Target value / Order --]
      <div class="opi-grid5 target-block">
        [#-- Baseline (start) year --]
        <div>[@customForm.select name="${outcomeCustomName}.startYear" value="${(outcome.startYear)!-1}" i18nkey="${isAiccraUI?string('outcome.baselineYear','outcome.startYear')}" listName="milestoneYears" className="targetYear outcomeYear opi-select" required=true editable=editable /]</div>
        [#-- Baseline value: present in the design but with no column behind it yet, so it
             is rendered read-only and carries no name — nothing is submitted or lost. --]
        [#if isAiccraUI]
        <div class="opi-pendingField">
          <label>[@s.text name="outcome.baselineValue"/]</label>
          <input type="text" class="opi-pendingField__input" value="" readonly
            title="[@s.text name="outcomes.baselineValue.pending"/]" aria-describedby="opi-baselineValue-note-${index}" />
          <span class="opi-pendingField__note" id="opi-baselineValue-note-${index}">[@s.text name="outcomes.baselineValue.pending"/]</span>
        </div>
        [/#if]
        [#-- Closing (target) year --]
        <div>[@customForm.select name="${outcomeCustomName}.year" value="${(outcome.year)!-1}" i18nkey="${isAiccraUI?string('outcome.closingYear','outcome.targetYear')}" listName="milestoneYears" className="targetYear outcomeYear opi-select" required=true editable=editable /]</div>
        [#-- Target Unit --]
        [#if targetUnitList?has_content]
        <div class="targetUnit-block">
          [@customForm.select name="${outcomeCustomName}.srfTargetUnit.id" i18nkey="outcome.selectTargetUnit"  placeholder="outcome.selectTargetUnit.placeholder" className="targetUnit opi-select" listName="targetUnitList" editable=editable  /]
        </div>
        [#else]
        <input type="hidden" name="${outcomeCustomName}.srfTargetUnit.id" value="-1"/>
        [/#if]
        [#-- Target Value --]
        [#local showTargetValue = (targetUnitList?has_content) && (outcome.srfTargetUnit??) && (outcome.srfTargetUnit.id??) && (outcome.srfTargetUnit.id != -1) /]
        <div class="targetValue-block" style="display:${showTargetValue?string('block', 'none')}">
          [@customForm.input name="${outcomeCustomName}.value" i18nkey="outcome.targetValue" help="outcomes.addNewTargetUnit"  placeholder="outcome.inputTargetValue.placeholder" className="targetValue targetValueNumber" required=true editable=editable /]
        </div>
      </div>

      [#if isAiccraUI]
        [#-- =================== Disaggregations (design table) =================== --]
        <div class="opi-panel opi-dis__ask">
          <span class="opi-panel__label">[@s.text name="outcomes.disaggregations.question"/]</span>
          <span class="opi-seg" role="group">
            <button type="button" class="opi-seg__btn opi-dis__yes ${hasDis?string('is-on','')}" aria-pressed="${hasDis?string}" [#if !editable]disabled[/#if]>[@s.text name="outcomes.disaggregations.yes"/]</button>
            <button type="button" class="opi-seg__btn opi-dis__no ${hasDis?string('','is-on')}" aria-pressed="${(!hasDis)?string}" [#if !editable]disabled[/#if]>[@s.text name="outcomes.disaggregations.no"/]</button>
          </span>
          <span class="opi-panel__note" data-opi-disnote data-yes="[@s.text name="outcomes.disaggregations.note.yes"/]" data-no="[@s.text name="outcomes.disaggregations.note.no"/]">[#if hasDis][@s.text name="outcomes.disaggregations.note.yes"/][#else][@s.text name="outcomes.disaggregations.note.no"/][/#if]</span>
        </div>

        <div class="opi-block opi-dis" style="display:${hasDis?string('flex','none')}">
          <div class="opi-block__head">
            <span class="opi-block__label">[@s.text name="outcomes.disaggregations"/]</span>
            <span class="opi-block__hint">[@s.text name="outcomes.disaggregations.hint"/]</span>
          </div>
          <div class="opi-dis__table">
            <div class="opi-dis__thead">
              <span></span>
              <span>#</span>
              <span>[@s.text name="outcomes.disaggregations.code"/]</span>
              <span>[@s.text name="outcomes.disaggregations.statement"/]</span>
              <span>[@s.text name="outcomes.disaggregations.unit"/]</span>
              <span>[@s.text name="outcomes.disaggregations.rule"/]</span>
              <span></span>
            </div>
            <div class="opi-dis__rows">
            [#list rowStmts as stmt]
              [#local isPrincipal = (stmt_index == 0) /]
              [#local rowCode = "" /][#local rowUnitId = "-1" /][#local rowDeletable = true /][#local rowHasMilestones = false /]
              [#if !isTemplate]
                [#list outcome.milestones![] as m]
                  [#if ((m.title)!"")?trim == stmt]
                    [#local rowHasMilestones = true /]
                    [#if rowCode == ""][#local rowCode = (m.code)!"" /][/#if]
                    [#if rowUnitId == "-1" && (m.srfTargetUnit.id)??][#local rowUnitId = m.srfTargetUnit.id?c /][/#if]
                    [#if !action.canBeDeleted((m.id)!-1,(m.class.name)!"")][#local rowDeletable = false /][/#if]
                  [/#if]
                [/#list]
              [/#if]
              <div class="opi-dis__row ${isPrincipal?string('is-principal','')}" data-opi-row="r${index}-${stmt_index}" [#if editable && !isPrincipal]draggable="true"[/#if]>
                <span class="opi-dis__grip" [#if editable && !isPrincipal]title="[@s.text name="outcomes.disaggregations.grip"/]"[/#if] aria-hidden="true">
                  <svg width="12" height="14" viewBox="0 0 12 14" fill="none"><circle cx="4" cy="3" r="1.15" fill="currentColor"></circle><circle cx="8" cy="3" r="1.15" fill="currentColor"></circle><circle cx="4" cy="7" r="1.15" fill="currentColor"></circle><circle cx="8" cy="7" r="1.15" fill="currentColor"></circle><circle cx="4" cy="11" r="1.15" fill="currentColor"></circle><circle cx="8" cy="11" r="1.15" fill="currentColor"></circle></svg>
                </span>
                <span class="opi-dis__n">${stmt_index + 1}</span>
                <span class="opi-dis__code">
                  [#-- Codes are derived from the row order by outcomes.js, never typed. --]
                  <input type="text" class="opi-plainInput opi-dis__codeInput" value="${rowCode}" aria-label="[@s.text name="outcomes.disaggregations.code"/]" readonly tabindex="-1" />
                  [#if isPrincipal]<span class="opi-dis__pBadge">P</span>[/#if]
                </span>
                <span class="opi-dis__stmt">
                  <input type="text" class="opi-plainInput opi-dis__stmtInput" value="${stmt}" aria-label="[@s.text name="outcomes.disaggregations.statement"/]" [#if !editable || isPrincipal]readonly[/#if] [#if isPrincipal]title="[@s.text name="outcomes.disaggregations.fromIndicator"/]"[/#if] />
                </span>
                <span class="opi-dis__unit">
                  <select class="opi-plain opi-dis__unitSelect" aria-label="[@s.text name="outcomes.disaggregations.unit"/]" [#if !editable || isPrincipal]disabled[/#if]>
                    [#-- The design offers three units only. They are matched by name against
                         targetUnitList (a Map<Long,String>) so the ids stay environment-agnostic;
                         -1 is MARLO's existing "no unit" sentinel. --]
                    <option value="-1" [#if rowUnitId == "-1"]selected[/#if]>[@s.text name="outcomes.disaggregations.unit.notApplicable"/]</option>
                    [#if targetUnitList?has_content]
                      [#local unitKeys = targetUnitList?keys /]
                      [#local unitVals = targetUnitList?values /]
                      [#list unitKeys as k]
                        [#local unitName = unitVals[k_index]?trim /]
                        [#if unitName == "# of" || unitName == "%"]
                          <option value="${k?c}" [#if k?c == rowUnitId]selected[/#if]>${unitName}</option>
                        [/#if]
                      [/#list]
                    [/#if]
                  </select>
                </span>
                [#-- Business rule: in the design but with no table behind it yet, so it is
                     read-only and unnamed — nothing is submitted or silently lost. --]
                <span class="opi-dis__rule">
                  <select class="opi-plain opi-dis__ruleSelect" disabled
                    title="[@s.text name="outcomes.disaggregations.rule.pending"/]"
                    aria-label="[@s.text name="outcomes.disaggregations.rule"/]">
                    <option>[@s.text name="outcomes.disaggregations.rule.none"/]</option>
                  </select>
                </span>
                <span class="opi-dis__actions">
                  [#if editable && !isPrincipal && rowDeletable]
                    <button type="button" class="opi-dis__delete" aria-label="Delete disaggregation">&#10005;</button>
                  [/#if]
                </span>
              </div>
            [/#list]
            </div>
          </div>
          [#if editable]
            <button type="button" class="opi-addDis opi-dashedBtn">+ [@s.text name="outcomes.disaggregations.add"/]</button>
          [/#if]
        </div>

        [#-- =================== Period targets (year matrix) =================== --]
        <div class="opi-block opi-matrix-block">
          <div class="opi-block__head">
            <span class="opi-block__label">[@s.text name="outcome.milestone.sectionTitle"/]</span>
            <span class="opi-block__hint">[@s.text name="outcomes.matrix.hint"/]</span>
            <span class="opi-block__legend"><span class="opi-legendSwatch"></span>[@s.text name="outcomes.matrix.missingLegend"/]</span>
          </div>
          <div class="opi-matrix mz">
            <div class="opi-matrix__scroll">
              <div class="opi-matrix__head" style="grid-template-columns:${gridCols}">
                <span class="opi-matrix__corner">[@s.text name="outcomes.matrix.targetStatement"/]</span>
                [#list yearCols as y]
                  <span class="opi-matrix__year ${(y == nowYear)?string('is-now','')}" data-opi-yearcol="${y}">
                    <span class="opi-matrix__yearLabel">${y}</span>
                    [#if y == nowYear]<span class="opi-matrix__now">[@s.text name="outcomes.matrix.now"/]</span>[/#if]
                  </span>
                [/#list]
                <span class="opi-matrix__addcol">
                  [#if editable]<button type="button" class="opi-addYear opi-dashedBtn opi-dashedBtn--sm">+ [@s.text name="outcomes.matrix.addYear"/]</button>[/#if]
                </span>
              </div>
              [#local placed = [] /]
              <div class="milestones-list opi-matrix__rows" listname="${outcomeCustomName}.milestones">
              [#list rowStmts as stmt]
                [#local isPrincipal = (stmt_index == 0) /]
                <div class="opi-matrix__row ${isPrincipal?string('is-principal','')}" data-opi-row="r${index}-${stmt_index}" style="grid-template-columns:${gridCols}">
                  <span class="opi-matrix__label">
                    <span class="opi-matrix__rowcode" data-opi-rowcode>&nbsp;</span>
                    <span class="opi-matrix__stmtWrap">
                      <span class="opi-matrix__stmt" data-opi-rowstmt>${stmt}</span>
                      <span class="opi-matrix__sub" data-opi-rowsub></span>
                    </span>
                  </span>
                  [#list yearCols as y]
                    [#local foundIdx = -1 /]
                    [#if !isTemplate]
                      [#list outcome.milestones![] as m]
                        [#if !placed?seq_contains(m_index) && ((m.title)!"")?trim == stmt && ((m.year)!-1) == y]
                          [#local foundIdx = m_index /]
                          [#local placed = placed + [m_index] /]
                          [#break]
                        [/#if]
                      [/#list]
                    [/#if]
                    [#if foundIdx != -1]
                      [@opiCellMacro milestone=outcome.milestones[foundIdx] name="${outcomeCustomName}.milestones" index=foundIdx rowStmt=stmt /]
                    [#else]
                      <span class="opi-cell is-empty" data-opi-year="${y}">
                        [#if editable]<button type="button" class="opi-cell__create" title="[@s.text name="outcomes.matrix.addValue"/]">+</button>[#else]<span class="opi-cell__dash">&mdash;</span>[/#if]
                      </span>
                    [/#if]
                  [/#list]
                  <span class="opi-matrix__tail"></span>
                </div>
              [/#list]
              </div>
            </div>
          </div>
          [#-- Milestones that could not be placed in the matrix (duplicated statement+year) keep the classic block so no data is hidden. --]
          [#if !isTemplate && outcome.milestones?has_content && placed?size < outcome.milestones?size]
            <div class="opi-matrix__leftovers">
              [#list outcome.milestones as m]
                [#if !placed?seq_contains(m_index)]
                  [@milestoneMacro milestone=m name="${outcomeCustomName}.milestones" index=m_index editable=editable canEditMilestone=action.canEditMileStone(m) /]
                [/#if]
              [/#list]
            </div>
          [/#if]
        </div>
      [#else]
        [#-- =================== Legacy stacked milestones (non-AICCRA: POWB fields, DAC markers) =================== --]
        <div class="opi-block">
          <div class="opi-block__head">
            <span class="opi-block__label">[@s.text name="outcome.milestone.sectionTitle"/]</span>
            <span class="opi-block__hint">[@s.text name="outcomes.periodTargets.hint"/]</span>
            <span class="opi-block__count">${milestoneCount}</span>
          </div>
          <div class="milestones-list" listname="${outcomeCustomName}.milestones">
          [#if outcome.milestones?has_content]
            <div class="cont-btn-min">
              <button type="button" class="btn-expand-all btn btn-link">Collapse all<i class="fas fa-expand-arrows-alt"></i></button>
            </div>
            [#list outcome.milestones as milestone]
              [@milestoneMacro milestone=milestone name="${outcomeCustomName}.milestones" index=milestone_index editable=editable canEditMilestone=action.canEditMileStone(milestone) /]
            [/#list]
          [#else]
            <p class="message text-center">[@s.text name="outcome.milestone.section.notMilestones.span"/]</p>
          [/#if]
          </div>
          [#if editable]
            <div class="addMilestone opi-dashedBtn">+ [@s.text name="form.buttons.addMilestone"/]</div>
            <div class="note"><small>[@s.text name = "outcomes.addNewTargetUnit" /]</small></div>
          [/#if]
        </div>
      [/#if]

      [#-- =================== Progress to target indicators (questions) =================== --]
      [#if showBaselineBlock]
      <div class="opi-block opi-q">
        <div class="opi-block__head">
          <span class="opi-block__label">[#if isAiccraUI][@s.text name="outcome.progressIndicators.title"/][#else][@s.text name="outcome.baselineIndicators.title"/][/#if]</span>
          <span class="opi-block__hint">[@s.text name="outcomes.progressIndicators.hint"/]</span>
          <span class="opi-block__count"><span data-opi-qcount>${indicatorCount}</span> [#if indicatorCount == 1][@s.text name="outcomes.questions.count.one"/][#else][@s.text name="outcomes.questions.count.many"/][/#if]</span>
        </div>

        [#-- Baseline instructions (PDF) --]
        <div class="opi-panel fileUploadContainer">
          <span class="opi-panel__label">[@s.text name="outcomes.baselineInstructions.pdf"/]</span>
          [#if !isTemplate]
            [#if outcome?has_content]
              [#local hasFile = (outcome??) && action.hasBaselineFile(outcome) /]
            [#else]
              [#local hasFile = false/]
            [/#if]
            <input class="fileID" type="hidden" name="${outcomeCustomName}.file.id" value="${(outcome.file.id)!}" />
            [#if editable]
            <div class="fileUpload" style="display:${hasFile?string('none','block')}"> <input class="upload" type="file" name="file" data-url="${baseUrl}/uploadBaseLine.do"></div>
            [/#if]
            <p class="fileUploaded textMessage checked" style="display:${hasFile?string('block','none')}">
              <span class="contentResult">[#if outcome.file??]
                <a target="_blank" href="${action.getBaseLineFileURL((outcome.id?string)!-1)}&filename=${(outcome.file.fileName)!}" class="downloadBaseline"><img src="${baseUrlCdn}/global/images/pdf.png" width="24px" alt="Download document" /> ${(outcome.file.fileName)!('No file name')} </a>
                [/#if]</span>
              [#if editable]<span class="removeIcon opi-fileRemove"> </span>[/#if]
            </p>
            [#if !hasFile]<span class="opi-panel__note">[@s.text name="outcomes.file.none"/]</span>[/#if]
          [#else]
            <p><i>[@customForm.text name="outcome.baselineInstructionsUnavailbale" readText=!editable /] </i></p>
          [/#if]
        </div>

        [#-- Questions table --]
        <div class="opi-q__table baselineIndicators-list">
          <div class="opi-q__thead">
            <span>#</span>
            <span>[@s.text name="outcomes.questions.question"/]</span>
            <span></span>
          </div>
          [#if outcome.indicators?has_content]
            [#list outcome.indicators as baselineIndicator]
              [@baselineIndicatorMacro indicator=baselineIndicator name="${outcomeCustomName}.indicators" index=baselineIndicator_index /]
            [/#list]
          [#else]
            <div class="opi-empty opi-empty--sm opi-q__empty">
              <span class="opi-empty__title">[@s.text name="outcomes.questions.empty.title"/]</span>
              <span class="opi-empty__hint">[@s.text name="outcomes.questions.empty.hint"/]</span>
            </div>
          [/#if]
        </div>
        [#if editable]
          <div class="opi-q__addRow">
            <button type="button" class="addBaselineIndicator opi-dashedBtn" data-blocked-title="[@s.text name="outcomes.questions.addBlocked"/]">+ [@s.text name="outcomes.questions.add"/]</button>
            <span class="opi-q__addNote" data-opi-qnote></span>
          </div>
        [/#if]
      </div>
      [/#if]

      [#-- =================== Instructions =================== --]
      <div class="opi-block">
        <div class="opi-block__head">
          <span class="opi-block__label">[@s.text name="outcome.instructions"/]</span>
          <span class="opi-block__hint">[@s.text name="outcomes.instructions.hint"/]</span>
        </div>
        [@customForm.textArea name="${outcomeCustomName}.instructions" i18nkey="outcome.instructions" showTitle=false required=false className="milestone-statement" editable=editable allowTextEditor=true/]
      </div>

      [#-- =================== Sub-IDOs (non-AICCRA) =================== --]
      [#if !isAiccraUI]
      <div class="opi-block">
        <div class="opi-block__head">
          <span class="opi-block__label">[@s.text name="outcome.subIDOs.sectionTitle"/]</span>
          <span class="opi-block__hint">[@s.text name="outcomes.subIdos.hint"/]</span>
          <span class="opi-block__count">${subIdoCount}</span>
        </div>
        <div class="subIdos-list" listname="${outcomeCustomName}.subIdos">
          [#if outcome.subIdos?has_content]
            [#list outcome.subIdos as subIdo]
              [@subIDOMacro subIdo=subIdo name="${outcomeCustomName}.subIdos" index=subIdo_index /]
            [/#list]
          [#else]
            [@subIDOMacro subIdo={} name="${outcomeCustomName}.subIdos" index=0 /]
          [/#if]
        </div>
        [#if editable]
          <div class="addSubIdo button-blue"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addSubIDO"/]</div>
        [/#if]
      </div>
      [/#if]

    </div>
  </div>
[/#macro]

[#-- One matrix cell: a milestone rendered as (year, value, status). The row-level
     statement / code / unit live in the disaggregations table and are copied into
     this cell's hidden inputs by outcomes.js. --]
[#macro opiCellMacro milestone name index rowStmt="" isTemplate=false]
  [#local cellName = "${name}[${index}]" /]
  [#local cellEditable = editable /]
  [#if !isTemplate]
    [#local cellEditable = editable && action.canEditMileStone(milestone) /]
  [/#if]
  [#local cellStatus = (milestone.milestonesStatus.id)!-1 /]
  [#local showExt = ((milestone.extendedYear?has_content) && (milestone.extendedYear != -1) && milestone.extendedYear != milestone.year) || (cellStatus == 4) /]
  <span [#if isTemplate]id="opiCell-template"[/#if] class="milestone opi-cell" data-opi-year="${(milestone.year)!''}" [#if isTemplate]style="display:none"[/#if]>
    <input type="hidden" class="mileStoneId" name="${cellName}.id" value="${(milestone.id)!}"/>
    <input type="hidden" class="mileStoneComposeId" name="${cellName}.composeID" value="${(milestone.composeID)!}"/>
    [#if cellEditable || isTemplate]
      <input type="hidden" class="opi-cell__title" name="${cellName}.title" value="${(milestone.title)!rowStmt}"/>
      <input type="hidden" class="opi-cell__code" name="${cellName}.code" value="${(milestone.code)!}"/>
      <input type="hidden" class="opi-cell__year" name="${cellName}.year" value="${(milestone.year)!}"/>
      <input type="hidden" class="opi-cell__unit" name="${cellName}.srfTargetUnit.id" value="${(milestone.srfTargetUnit.id)!-1}"/>
      <span class="opi-cell__valueWrap">
        <span class="opi-cell__affix" aria-hidden="true"></span>
        <input type="text" inputmode="numeric" class="targetValue targetValueNumber opi-cell__value" name="${cellName}.value" value="${(milestone.value)!}" aria-label="${rowStmt} ${(milestone.year)!''}"/>
      </span>
      <select class="milestoneStatus opi-plain opi-cell__status" name="${cellName}.milestonesStatus.id" aria-label="Status">
        <option value="-1"></option>
        [#list generalStatuses![] as st]
          <option value="${st.id?c}" [#if st.id == cellStatus]selected[/#if]>${st.name}</option>
        [/#list]
      </select>
      <select class="opi-plain opi-cell__extYear" name="${cellName}.extendedYear" aria-label="[@s.text name="outcome.milestone.inputNewTargetYear"/]" style="display:${showExt?string('block','none')}">
        <option value="-1"></option>
        [#list milestoneYears![] as my]
          <option value="${my?c}" [#if ((milestone.extendedYear)!-1) == my]selected[/#if]>${my?c}</option>
        [/#list]
      </select>
      <span class="opi-cell__hint" data-opi-hint></span>
    [#else]
      [#-- Read-only cell: carry the milestone's full current state. The save chain
           rebuilds each incoming milestone with copyFields(), which also copies
           nulls — a partial submit here would wipe the fields the user cannot
           edit and NPE on the missing target unit (OutcomesAction.saveMilestones). --]
      <input type="hidden" name="${cellName}.title" value="${(milestone.title)!}"/>
      <input type="hidden" name="${cellName}.code" value="${(milestone.code)!}"/>
      <input type="hidden" name="${cellName}.year" value="${(milestone.year)!}"/>
      <input type="hidden" name="${cellName}.value" value="${(milestone.value)!}"/>
      <input type="hidden" name="${cellName}.srfTargetUnit.id" value="${(milestone.srfTargetUnit.id)!-1}"/>
      [#-- Always sent (with -1 defaults), like the legacy macro: a milestone bound
           without a status NPEs OutcomeValidator.validateMilestone. --]
      <input type="hidden" name="${cellName}.milestonesStatus.id" value="${(milestone.milestonesStatus.id?c)!-1}"/>
      <input type="hidden" name="${cellName}.extendedYear" value="${(milestone.extendedYear?c)!-1}"/>
      <span class="opi-cell__read">${(milestone.value)!'&mdash;'}</span>
      <span class="opi-cell__readStatus">${(milestone.milestonesStatus.name)!}</span>
    [/#if]
  </span>
[/#macro]


[#macro milestoneMacro milestone name index isTemplate=false editable=true canEditMilestone=true]
  [#local milestoneCustomName = "${name}[${index}]" /]
  [#local editableMilestone = editable && canEditMilestone]
  [#local hasExtendedYear = (milestone.extendedYear?has_content) && (milestone.extendedYear != -1) && milestone.extendedYear != milestone.year]
  [#local showExtendedYear =  hasExtendedYear || ((milestone.milestonesStatus.id == 4)!false) ]
  [#local milestoneYear =  (milestone.year)!currentCycleYear ]
  [#--if hasExtendedYear
    [#local milestoneYear =  milestone.extendedYear ]
  [/#if --]
  [#local reqMilestonesFields = (milestoneYear == actualPhase.year)!false /]

  [#local isMilestoneNew =  true ]
  [#if !isTemplate]
    [#local isMilestoneNew =  milestone.isNew(actualPhase.id) ]
  [/#if]
  <div id="srfSlo-${isTemplate?string('template',index)}" class="srfSlo borderBox-no-padding" style="display:${isTemplate?string('none','block')}">
    [#-- Remove Button --]
    [#if editableMilestone && action.canBeDeleted((milestone.id)!-1,(milestone.class.name)!"" )]
      <div class="removeMilestone removeElement sm" title="Remove Milestone"></div>
    [#elseif editableMilestone]
      <div class="removeElement sm disable" title="[@s.text name="global.CrpMilestone"/] can not be deleted"></div>
    [/#if]

    [#-- SLO Title --]
    <div class="blockTitle opened">
      <div class="leftHead ${reqMilestonesFields?string('green', '')} sm">
        <!--<span class="index">${index+1}</span>-->
        <span class="index">${(milestone.year)! "[New]"}</span>
        <span class="elementId">${(milestone.crpProgramOutcome.acronym)!milestoneYear!} [@s.text name="outcome.milestone.index.title"/][#if hasExtendedYear] [@s.text name="outcome.milestone.extended.text"/] ${milestone.extendedYear} [/#if][#if isMilestoneNew] [New][/#if]</span>
      </div>
      <!-- <strong>SLO ${index+1}: </strong>  -->
  		<span class="milestoneTitlePreview">${(milestone.title)!""}</span>
      <!-- <small>(Alerts: 5) </small> -->
    </div>
    

    <div class="blockContent" style="display:block">
      <div id="milestone-${isTemplate?string('template', index)}" class="milestone borderBox-no-border isNew-${isMilestoneNew?string}" style="display:${isTemplate?string('none','block')}">

        <input type="hidden" class="mileStoneId" name="${milestoneCustomName}.id" value="${(milestone.id)!}"/>
        <input type="hidden" class="mileStoneComposeId" name="${milestoneCustomName}.composeID" value="${(milestone.composeID)!}"/>

[#-- 
        <div class="pull-right">
          [@popUps.relationsMacro element=(milestone)!{} /]
        </div>
--]
        [#-- Milestone Statement --]
        <div class="form-group">
          [@customForm.textArea name="${milestoneCustomName}.title" i18nkey="outcome.milestone.statement" required=true className="milestone-statement limitWords-100" editable=editableMilestone /]
        </div>

        <div class="form-group row to-minimize">
          [#-- Code --]
        	<div class="col-md-3">     
        		[@customForm.input name="${milestoneCustomName}.code" type="text"  i18nkey="outcome.milestone.code" required=false editable=editable /]
					</div>
        
          [#--  Status  --]
          <div class="col-md-3">
            [@customForm.select name="${milestoneCustomName}.milestonesStatus.id" forcedValue="${(milestone.milestonesStatus.name)!}" i18nkey="outcome.milestone.inputStatus" listName="generalStatuses" keyFieldName="id" displayFieldName="name" required=true  className="milestoneStatus" editable=editable /]
          </div>
          
          [#-- Year --]
          <div class="col-md-3">
            [@customForm.select name="${milestoneCustomName}.year" value="${(milestone.year)!-1}"  i18nkey="outcome.milestone.inputTargetYear" listName="milestoneYears"  required=true  className=" targetYear milestoneYear" editable=editableMilestone /]
          </div>

          [#-- Extended Year --]
          <div class="col-md-3 extendedYearBlock" style="display:${showExtendedYear?string('block', 'none')}">
           [@customForm.select name="${milestoneCustomName}.extendedYear" value="${(milestone.extendedYear)!-1}"  i18nkey="outcome.milestone.inputNewTargetYear" listName="milestoneYears"  required=true  className=" targetYear milestoneExtendedYear" editable=editable /]
           [#if !editableMilestone][#if (milestone.extendedYear != -1)!false ]${(milestone.extendedYear)!}[/#if][/#if]
          </div>
        </div>

        <div class="row form-group target-block to-minimize">
          [#-- Target Unit --]
          [#if targetUnitList?has_content]
          <div class="col-md-3">
            [@customForm.select name="${milestoneCustomName}.srfTargetUnit.id"  i18nkey="outcome.milestone.selectTargetUnit" placeholder="outcome.selectTargetUnit.placeholder" className="targetUnit" listName="targetUnitList" editable=editableMilestone  /]
          </div>
          [/#if]
          [#-- Target Value --]
          [#local showTargetValue = (targetUnitList?has_content) && (milestone.srfTargetUnit??) && (milestone.srfTargetUnit.id??) && (milestone.srfTargetUnit.id != -1) /]
          <div class="col-md-3 targetValue-block" style="display:${showTargetValue?string('block', 'none')}">
            [@customForm.input name="${milestoneCustomName}.value" type="text"  i18nkey="outcome.milestone.inputTargetValue" placeholder="outcome.milestone.inputTargetValue.placeholder" className="targetValue targetValueNumber" required=true editable=editableMilestone /]
          </div>
        </div>

        [#-- POWB 2019 REQUIREMENTS --]
        [#if action.hasSpecificities('impact_pathway_cross_cutting_markets_active')]
        <div class="form-group to-minimize">
          <div class="row">
            [#-- Indicate of the following --]
            <div class="col-md-5">
              [@customForm.select name="${milestoneCustomName}.powbIndFollowingMilestone.id"  i18nkey="outcome.milestone.powbIndFollowingMilestone" className="" keyFieldName="id" displayFieldName="name" listName="followingMilestones" editable=editable required=reqMilestonesFields /]
            </div>
            [#-- Assessment of risk to achievement --]
            <div class="col-md-7">
              [#if globalUnitType != 3]
                <div class="form-group listname="${milestoneCustomName}.powbIndAssesmentRisk.id">
                  <label>[@s.text name="outcome.milestone.powbIndAssesmentRisk" /]:[@customForm.req required = true && editable = editable && reqMilestonesFields  /]</label> <br />
                  [#list (assessmentRisks)![] as assesment]
                    [@customForm.radioFlat id="${milestoneCustomName}-risk-${assesment.id}" name="${milestoneCustomName}.powbIndAssesmentRisk.id" label="${assesment.name}" value="${assesment.id}" checked=(milestone.powbIndAssesmentRisk.id == assesment.id)!false editable=editable cssClass="assesmentLevels" cssClassLabel=""/]
                  [/#list]
                  [#if !editable && (!(milestone.powbIndAssesmentRisk??))!true][@s.text name="form.values.fieldEmpty"/][/#if]
                </div>
              [/#if]
            </div>
          </div>

          [#if globalUnitType != 3]
            <div class="row form-group">
              [#-- For medium/high please select the main risk --]
              [#local showRisk = (milestone.powbIndAssesmentRisk.id >= 2)!false ]
              <div class="col-md-6 milestoneRisk" style="display:${showRisk?string('block', 'none')}">
                [@customForm.select name="${milestoneCustomName}.powbIndMilestoneRisk.id"  i18nkey="outcome.milestone.powbIndMilestoneRisk" className="risksOptions" keyFieldName="id" displayFieldName="name" listName="milestoneRisks" editable=editable required=reqMilestonesFields /]
              </div>
              [#-- Other Risk --]
              [#local showOtherRiskField = (milestone.powbIndMilestoneRisk.id == 7)!false ]
              <div class="col-md-6 milestoneOtherRiskField" style="display:${showOtherRiskField?string('block', 'none')}">
                [@customForm.input name="${milestoneCustomName}.powbMilestoneOtherRisk"  i18nkey="outcome.milestone.powbMilestoneOtherRisk" className="" editable=editable required=reqMilestonesFields /]
              </div>
            </div>
          [/#if]

          [#-- Means of verification --]
          <div class="form-group">
            [@customForm.textArea name="${milestoneCustomName}.powbMilestoneVerification" i18nkey="outcome.milestone.powbMilestoneVerification" required=true className="milestone-powbMilestoneVerification" editable=editable required=reqMilestonesFields /]
          </div>
          [#-- DAC Markers for the milestone --]
          <div class="row form-group">
            <p class="subTitle col-md-12"><i> [@s.text name="outcome.milestone.milestoneMarkers" /] </i> </p><br />
            <div class="col-md-3">
              [@customForm.select name="${milestoneCustomName}.genderFocusLevel.id"  i18nkey="outcome.milestone.genderFocusLevel" className="" keyFieldName="id" displayFieldName="powbName" listName="focusLevels" editable=editable required=reqMilestonesFields  /]
            </div>
            <div class="col-md-3">
              [@customForm.select name="${milestoneCustomName}.youthFocusLevel.id"  i18nkey="outcome.milestone.youthFocusLevel" className="" keyFieldName="id" displayFieldName="powbName" listName="focusLevels" editable=editable required=reqMilestonesFields /]
            </div>
            <div class="col-md-3">
              [@customForm.select name="${milestoneCustomName}.capdevFocusLevel.id"  i18nkey="outcome.milestone.capdevFocusLevel" className="" keyFieldName="id" displayFieldName="powbName" listName="focusLevels" editable=editable required=reqMilestonesFields /]
            </div>
            <div class="col-md-3">
              [@customForm.select name="${milestoneCustomName}.climateFocusLevel.id"  i18nkey="outcome.milestone.climateFocusLevel" className="" keyFieldName="id" displayFieldName="powbName" listName="focusLevels" editable=editable required=reqMilestonesFields /]
            </div>
            <br />
          </div>
        </div>
        [/#if]
      </div>

    </div>
  </div>
  <!-- //MILESTONE NORMAL -->

[/#macro]


[#macro subIDOMacro subIdo name index isTemplate=false]
  [#local subIDOCustomName = "${name}[${index}]" /]
  [#local subIDOCustomID = "${name}_${index}"?replace("\\W+", "", "r") /]
  <div id="subIdo-${isTemplate?string('template', index)}" class="subIdo simpleBox" style="display:${isTemplate?string('none','block')}">
    <div class="loading" style="display:none"></div>
    <div class="leftHead blue sm">
      <span class="index">${index+1}</span>
      <span class="elementId">[@s.text name="outcome.subIDOs.index.title"/]</span>
    </div>
    [#-- Hidden inputs --]
    <input type="hidden" class="programSubIDOId" name="${subIDOCustomName}.id" value="${(subIdo.id)!}"/>

    [#-- Remove Button --]
    [#if editable && action.canBeDeleted((subIdo.id)!-1,(subIdo.class.name)!"" )]
      <div class="removeSubIdo removeElement sm" title="Remove Sub IDO"></div>
    [#elseif editable]
      <div class="removeElement sm disable" title="[@s.text name="global.SrfSubIdo"/] can not be deleted"></div>
    [/#if]
    [#-- Primary Option --]
    <div class="">
      [@customForm.radioFlat id="${subIDOCustomName}.primary" name="${subIDOCustomName}.primary" label="Set this Sub-IDO as primary" disabled=false editable=editable value="true" checked=(subIdo.primary)!false cssClass="setPrimaryRadio" cssClassLabel="radio-label-yes" inline=false /]
    </div>
    [#-- Sub IDO --]
    <div class="form-group">
      <div class="subIdoBlock" >
        <label for="">[@s.text name="outcome.subIDOs.inputSubIDO.label"/]:[#if editable]<span class="red">*</span>[/#if]</label>
        <div id="" class="${subIDOCustomID} subIdoSelected">
          [@utils.letterCutter string="${(subIdo.srfSubIdo.description)!'<i>No Sub-IDO Selected</i>'}" maxPos=65 /]
        </div>
        <input type="hidden" class="subIdoId" name="${subIDOCustomName}.srfSubIdo.id" value="${(subIdo.srfSubIdo.id)!}" />
      </div>
      <div class="buttonSubIdo-block" >
        [#if editable]
          <div class="buttonSubIdo-content"><br> <div class="button-blue selectSubIDO" ><span class=""></span> Select a Sub-IDO</div></div>
        [/#if]
      </div>
      <div class="contributionBlock">[@customForm.input name="${subIDOCustomName}.contribution" type="text" i18nkey="outcome.subIDOs.inputContribution.label" placeholder="% of contribution" className="contribution" required=true editable=editable /]</div>
      <div class="clearfix"></div>
    </div>
    <hr />
    [#-- Assumptions List --]
    <div class="row" style="position: relative;">
      <div class="col-md-9">
        <label for="">[@s.text name="outcome.subIDOs.assumptions.label" /]:</label>
        <div class="assumptions-list" listname="${subIDOCustomName}.assumptions">
          [#if subIdo.assumptions?has_content]
            [#list subIdo.assumptions as assumption]
              [@assumptionMacro assumption=assumption name="${subIDOCustomName}.assumptions" index=assumption_index /]
            [/#list]
          [#else]
          [@assumptionMacro assumption={} name="${subIDOCustomName}.assumptions" index=0 /]
          [#-- <p class="message text-center">[@s.text name="outcome.subIDOs.section.notAssumptions.span"/]</p> --]
          [/#if]
        </div>
      </div>
      [#-- Add Assumption Button --]
      [#if editable]<div class="addAssumption button-green"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addAssumption"/]</div>[/#if]
    </div>
  </div>
[/#macro]

[#macro assumptionMacro assumption name index isTemplate=false]
  [#assign assumptionCustomName = "${name}[${index}]" /]
  <div id="assumption-${isTemplate?string('template', index)}" class="assumption form-group" style="position:relative; display:${isTemplate?string('none','block')}">
    [#-- Remove Button --]
    [#if editable]
    <div class="removeAssumption removeIcon" title="Remove assumption"></div>
    [/#if]
    <input type="hidden" class="assumptionId" name="${assumptionCustomName}.id" value="${(assumption.id)!}"/>
    [#if !editable]
      [#if assumption.description?has_content]
        <div class="input"><p> <strong>${index+1}.</strong> ${(assumption.description)!}</p></div>
      [/#if]
    [#else]
      [@customForm.input name="${assumptionCustomName}.description" type="text" showTitle=false placeholder="" className="statement limitWords-100" required=true editable=editable /]
    [/#if]
    <div class="clearfix"></div>
  </div>
[/#macro]

[#macro baselineIndicatorMacro indicator name index isTemplate=false]
  [#local customName = "${name}[${index}]" /]
  <div id="baselineIndicator-${isTemplate?string('template', index)}" class="baselineIndicator opi-q__row" style="display:${isTemplate?string('none','grid')}">
    [#-- Hidden inputs --]
    <input type="hidden" class="baselineIndicatorId" name="${customName}.id" value="${(indicator.id)!}"/>
    <input type="hidden"  name="${customName}.composeID" value="${(indicator.composeID)!}"/>
    <span class="opi-q__n"><span class="index">${index + 1}</span></span>
    <span class="opi-q__field">
      [#if editable]
        <input type="text" class="opi-plainInput opi-q__input" name="${customName}.indicator" value="${(indicator.indicator)!}" placeholder="[@s.text name="outcomes.questions.placeholder"/]" aria-label="[@s.text name="outcomes.questions.question"/]" />
      [#else]
        [#if indicator.indicator?has_content]
          [#-- decodeHTML turns escaped markup into rendered HTML (legacy rich-text values) --]
          <div class="opi-q__read decodeHTML">${(indicator.indicator)!}</div>
        [#else]
          <div class="opi-q__read">&mdash;</div>
        [/#if]
      [/#if]
    </span>
    <span class="opi-q__actions">
      [#if editable]<button type="button" class="removeBaselineIndicator opi-q__delete" aria-label="Delete question">&#10005;</button>[/#if]
    </span>
  </div>
[/#macro]
