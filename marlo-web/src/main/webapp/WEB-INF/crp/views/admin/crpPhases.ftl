[#ftl]
[#assign title = "Phases" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["bootstrap-select","jquery-ui", "pickadate"] /]
[#assign customJS = [ 
  "${baseUrlMedia}/js/admin/crpPhases.js?20260916",
  "${baseUrlCdn}/global/js/fieldsValidation.js" 
  ] 
/]
[#assign customCSS = [ "${baseUrlMedia}/css/admin/crpPhases.css?20260916" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "crpPhases" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"crpPhases", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

[#--
  Planning / Reporting cycles.

  The screen is one table of the global unit's phases: what each one is, when its
  window runs, what follows it, and the two switches an administrator actually
  owns here -- whether the phase shows on the timeline (`visible`) and whether it
  is open for editing (`editable`) -- plus the single radio that picks the landing
  phase (`defaultPhaseID`).

  Everything else the row shows is read-only on this screen. Dates, the `next`
  link and the owning unit are not edited here and never were: the previous
  version kept the two date inputs in the markup behind `display:none`, and that
  is still what happens below. They have to stay submitted -- `prepare()` clears
  `phasesAction` on POST, so Struts rebuilds each Phase from the request alone and
  any field the form drops is written back as null.

  Class names are prefixed `cyc*` on purpose. `.phaseRow`, `.phaseRow__name` and
  `.phaseRow__dates` are already taken by the phase selector
  (`global/pages/timeline-phases.ftl`, styled in marlo-redesign.css section 4),
  which main-menu.ftl renders on this very page.
--]

[#-- ============================== Derived data ============================== --]
[#-- Midnight today. `.now?date` keeps the time of day, which makes a day count
     off by up to one; formatting through yyyy-MM-dd and parsing back truncates
     it properly. --]
[#assign cycToday = .now?string("yyyy-MM-dd")?date("yyyy-MM-dd") /]
[#assign cycDayMs = 86400000 /]
[#assign cycCurrentYear = .now?string("yyyy")?number /]
[#assign cycPhases = (phasesAction)![] /]
[#assign cycTotal = cycPhases?size /]

[#-- The colour rail on the left of a row, and the order of the kind filters.
     `description` is the phase kind in the data model ("Planning", "Progress
     reporting", "Reporting"); "progress" is tested first because its own
     description also contains "reporting". --]
[#function cycTone phase]
  [#local text = ((phase.description)!'')?lower_case /]
  [#if text?contains("progress")][#return "progress"/][/#if]
  [#if text?contains("report")][#return "reporting"/][/#if]
  [#return "planning"]
[/#function]

[#assign cycOpen = 0 /]
[#assign cycYears = [] /]
[#-- Phase names are per global unit (AICCRA reports AWPB / Progress / AR, its
     first years POWB), so the kind filters are whatever the data holds, ordered
     the way a cycle runs rather than the way the rows happen to be sorted. --]
[#assign cycPlanningNames = [] /]
[#assign cycProgressNames = [] /]
[#assign cycReportingNames = [] /]
[#list cycPhases as phase]
  [#if (phase.editable)!false][#assign cycOpen = cycOpen + 1 /][/#if]
  [#if !cycYears?seq_contains(phase.year)][#assign cycYears = cycYears + [phase.year] /][/#if]
  [#assign cycName = (phase.name)!'' /]
  [#if cycName?has_content]
    [#assign cycKind = cycTone(phase) /]
    [#if cycKind == "progress"]
      [#if !cycProgressNames?seq_contains(cycName)][#assign cycProgressNames = cycProgressNames + [cycName] /][/#if]
    [#elseif cycKind == "reporting"]
      [#if !cycReportingNames?seq_contains(cycName)][#assign cycReportingNames = cycReportingNames + [cycName] /][/#if]
    [#else]
      [#if !cycPlanningNames?seq_contains(cycName)][#assign cycPlanningNames = cycPlanningNames + [cycName] /][/#if]
    [/#if]
  [/#if]
[/#list]
[#assign cycNames = cycPlanningNames + cycProgressNames + cycReportingNames /]
[#assign cycYearsDesc = cycYears?sort?reverse /]

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]

        <div id="phaseCycles" class="phaseCycles"
          data-editable="${editable?string}"
          data-tpl-showing-all="[@s.text name="crpPhases.showingAll"][@s.param]{0}[/@s.param][/@s.text]"
          data-tpl-showing="[@s.text name="crpPhases.showing"][@s.param]{0}[/@s.param][@s.param]{1}[/@s.param][/@s.text]"
          data-tpl-phase-one="[@s.text name="crpPhases.count.one"][@s.param]{0}[/@s.param][/@s.text]"
          data-tpl-phase-many="[@s.text name="crpPhases.count.many"][@s.param]{0}[/@s.param][/@s.text]"
          data-tpl-open="[@s.text name="crpPhases.count.open"][@s.param]{0}[/@s.param][/@s.text]"
          data-tpl-empty="[@s.text name="crpPhases.empty.title"][@s.param]{0}[/@s.param][/@s.text]"
          data-tpl-changed-one="[@s.text name="crpPhases.unsaved.one"][@s.param]{0}[/@s.param][/@s.text]"
          data-tpl-changed-many="[@s.text name="crpPhases.unsaved.many"][@s.param]{0}[/@s.param][/@s.text]"
          data-label-saved="[@s.text name="crpPhases.allSaved" /]">

          [#-- ------------------------------ Heading ------------------------------ --]
          <div class="phaseCycles__head">
            <div class="phaseCycles__heading">
              <h1 class="phaseCycles__title">[@s.text name="CRPAdmin.menu.crpPhases" /]</h1>
              <p class="phaseCycles__subtitle">
                [#if cycTotal == 1][@s.text name="crpPhases.subtitle.one"][@s.param]${cycTotal?c}[/@s.param][@s.param]${crpSession}[/@s.param][/@s.text]
                [#else][@s.text name="crpPhases.subtitle.many"][@s.param]${cycTotal?c}[/@s.param][@s.param]${crpSession}[/@s.param][/@s.text][/#if] &middot;
                <span class="phaseCycles__openCount">[@s.text name="crpPhases.count.open"][@s.param]${cycOpen?c}[/@s.param][/@s.text]</span> &middot;
                [@s.text name="crpPhases.subtitle.today"][@s.param]${cycToday?string("d MMM yyyy")}[/@s.param][/@s.text]
              </p>
            </div>
          </div>

          [#if cycTotal == 0]
            <div class="phaseCycles__card">
              <p class="cycEmpty__text cycEmpty--noData">[@s.text name="crpPhases.noPhases" /]</p>
            </div>
          [#else]
          <div class="phaseCycles__card">

            [#-- ----------------------------- Toolbar ----------------------------- --]
            <div class="cycToolbar">
              <div class="cycToolbar__row">
                <div class="cycSearch">
                  <svg width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true" focusable="false">
                    <circle cx="7" cy="7" r="4.6" stroke="currentColor" stroke-width="1.5"></circle>
                    <path d="M10.6 10.6 14 14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"></path>
                  </svg>
                  <input type="text" id="cycSearchInput" class="cycSearch__input" autocomplete="off"
                    placeholder="[@s.text name="crpPhases.search.placeholder" /]"
                    aria-label="[@s.text name="crpPhases.search.label" /]" />
                  <button type="button" class="cycSearch__clear" hidden
                    aria-label="[@s.text name="crpPhases.search.clear" /]">&#10005;</button>
                </div>

                <div class="cycTabs" role="group" aria-label="[@s.text name="crpPhases.filter.type" /]">
                  <button type="button" class="cycTabs__item is-selected" data-type="all" aria-pressed="true">[@s.text name="crpPhases.filter.all" /]</button>
                  [#list cycNames as cycName]
                    <button type="button" class="cycTabs__item" data-type="${cycName}" aria-pressed="false">${cycName}</button>
                  [/#list]
                </div>

                <button type="button" class="cycToggle" aria-pressed="false">
                  <span class="cycToggle__dot" aria-hidden="true"></span>[@s.text name="crpPhases.filter.openOnly" /]
                </button>

                <span class="cycResult" role="status" aria-live="polite">[@s.text name="crpPhases.showingAll"][@s.param]${cycTotal?c}[/@s.param][/@s.text]</span>
              </div>

              <div class="cycToolbar__row cycToolbar__row--years">
                <span class="cycToolbar__label">[@s.text name="crpPhases.filter.year" /]</span>
                <button type="button" class="cycYear is-selected" data-year="all" aria-pressed="true">
                  [@s.text name="crpPhases.filter.all" /]<span class="cycYear__count">${cycTotal?c}</span>
                </button>
                [#list cycYearsDesc as cycYear]
                  [#assign cycYearCount = 0 /]
                  [#list cycPhases as phase][#if phase.year == cycYear][#assign cycYearCount = cycYearCount + 1 /][/#if][/#list]
                  <button type="button" class="cycYear" data-year="${cycYear?c}" aria-pressed="false">
                    ${cycYear?c}<span class="cycYear__count">${cycYearCount?c}</span>
                  </button>
                [/#list]
              </div>
            </div>

            [#-- ------------------------------ Table ------------------------------ --]
            <div class="cycTable">
              <div class="cycTable__inner">

                <div class="cycHead" role="row">
                  <span class="cycHead__cell">[@s.text name="crpPhases.column.phase" /]</span>
                  <span class="cycHead__cell">[@s.text name="crpPhases.column.window" /]</span>
                  <span class="cycHead__cell">[@s.text name="crpPhases.column.nextPhase" /]</span>
                  [#-- Short column headings, not the field labels. `projectPhases.visible`
                       ("Display on timeline") wraps to two lines at this width and would
                       push the sticky year headers out of place; it still names the
                       control itself, on the switch's aria-label. --]
                  <span class="cycHead__cell">[@s.text name="crpPhases.column.onTimeline" /]</span>
                  <span class="cycHead__cell">[@s.text name="crpPhases.column.status" /]</span>
                  <span class="cycHead__cell cycHead__cell--end">[@s.text name="crpPhases.column.landing" /]</span>
                </div>

                [#list cycYearsDesc as cycYear]
                  [#assign cycYearCount = 0 /]
                  [#assign cycYearOpen = 0 /]
                  [#list cycPhases as phase]
                    [#if phase.year == cycYear]
                      [#assign cycYearCount = cycYearCount + 1 /]
                      [#if (phase.editable)!false][#assign cycYearOpen = cycYearOpen + 1 /][/#if]
                    [/#if]
                  [/#list]

                  <div class="cycGroup" data-year="${cycYear?c}">
                    <div class="cycGroup__head">
                      <span class="cycGroup__year">${cycYear?c}</span>
                      <span class="cycGroup__meta">
                        <span class="cycGroup__count">[#if cycYearCount == 1][@s.text name="crpPhases.count.one"][@s.param]${cycYearCount?c}[/@s.param][/@s.text][#else][@s.text name="crpPhases.count.many"][@s.param]${cycYearCount?c}[/@s.param][/@s.text][/#if]</span><span
                          class="cycGroup__openWrap"[#if cycYearOpen == 0] hidden[/#if]> &middot; <span class="cycGroup__open">[@s.text name="crpPhases.count.open"][@s.param]${cycYearOpen?c}[/@s.param][/@s.text]</span></span>
                      </span>
                      [#if cycYear == cycCurrentYear]
                        <span class="cycBadge cycBadge--current">[@s.text name="crpPhases.currentYear" /]</span>
                      [/#if]
                    </div>

                    [#list cycPhases as phase]
                      [#if phase.year == cycYear]
                        [#assign customName = "phasesAction[${phase_index}]" /]
                        [#assign cycIsLanding = (defaultPhaseID??) && (defaultPhaseID == phase.id) /]
                        [#assign cycIsOpen = (phase.editable)!false /]
                        [#-- Kept verbatim from the previous version: 2018 and earlier, and the
                             2019 POWB, cannot be reopened from this screen unless they already
                             are open. The control is still rendered -- hidden, never removed --
                             because Struts rebuilds the Phase from the request and a missing
                             field is saved as null. --]
                        [#assign yearLimit = 2018 /]
                        [#assign canOpenClose = ((phase.year > yearLimit) && !((phase.year == (yearLimit + 1)) && (phase.name == "POWB"))) || cycIsOpen /]
                        [#assign cycHasDates = (phase.startDate)?? && (phase.endDate)?? /]
                        [#if cycHasDates]
                          [#assign cycStart = phase.startDate?date /]
                          [#assign cycEnd = phase.endDate?date /]
                          [#assign cycLive = (cycStart?long <= cycToday?long) && (cycEnd?long >= cycToday?long) /]
                          [#assign cycFuture = cycStart?long > cycToday?long /]
                          [#-- AICCRA has rows whose end_date precedes their start_date (429, 426
                               and 427 as of this writing). The window is still worth showing --
                               it is how an administrator spots the bad data -- but a span of
                               "-259 days" is not, so the count is dropped when it is negative. --]
                          [#assign cycSpan = ((cycEnd?long - cycStart?long) / cycDayMs)?round /]
                        [/#if]

                        <div id="crpPhase-${phase.id?c}"
                          class="cycRow cycRow--${cycTone(phase)}[#if cycIsOpen] is-open[/#if][#if cycIsLanding] is-landing[/#if]"
                          data-name="${(phase.name)!}"
                          data-year="${phase.year?c}"
                          [#-- Every `!` default needs its own parentheses: the operator binds
                               looser than `+`, so an unbracketed one swallows the rest of the
                               concatenation as its default value. --]
                          data-search="${(((phase.name)!'') + ' ' + phase.year?c + ' ' + ((phase.description)!'') + ' ' + phase.id?c)?lower_case}">

                          [#-- Hidden Inputs --]
                          <input type="hidden" name="${customName}.id" value="${phase.id?c}" />
                          <input type="hidden" name="${customName}.description" value="${(phase.description)!}" />
                          <input type="hidden" name="${customName}.name" value="${(phase.name)!}" />
                          <input type="hidden" name="${customName}.upkeep" value="${((phase.upkeep)!false)?string}" />
                          <input type="hidden" name="${customName}.year" value="${phase.year?c}" />
                          <input type="hidden" name="${customName}.next.id" value="${(phase.next.id?c)!}" />
                          <input type="hidden" name="${customName}.crp.id" value="${phase.crp.id?c}" />

                          [#-- Phase --]
                          <div class="cycRow__cell cycRow__phase">
                            <span class="cycRow__tone" aria-hidden="true"></span>
                            <span class="cycRow__identity">
                              <span class="cycRow__name">${(phase.name)!} ${phase.year?c}<span
                                class="cycBadge cycBadge--landing">[@s.text name="crpPhases.column.landing" /]</span></span>
                              <span class="cycRow__meta">#${phase.id?c} &middot; ${(phase.description)!} &middot; ${(phase.crp.acronym)!}</span>
                            </span>
                          </div>

                          [#-- Window --]
                          <div class="cycRow__cell cycRow__window">
                            [#if cycHasDates]
                              <span class="cycRow__dates">${cycStart?string("d MMM yyyy")} &ndash; ${cycEnd?string("d MMM yyyy")}</span>
                              [#if cycLive || cycFuture || (cycSpan >= 0)]
                                <span class="cycRow__duration[#if cycLive] cycRow__duration--live[/#if]">
                                  [#if cycLive]
                                    [@s.text name="crpPhases.window.daysLeft"][@s.param]${(((cycEnd?long - cycToday?long) / cycDayMs)?round)?c}[/@s.param][/@s.text]
                                  [#elseif cycFuture]
                                    [@s.text name="crpPhases.window.opensIn"][@s.param]${(((cycStart?long - cycToday?long) / cycDayMs)?round)?c}[/@s.param][/@s.text]
                                  [#else]
                                    [@s.text name="crpPhases.window.days"][@s.param]${cycSpan?c}[/@s.param][/@s.text]
                                  [/#if]
                                </span>
                              [/#if]
                            [#else]
                              <span class="cycRow__dates cycRow__dates--none">[@s.text name="crpPhases.window.none" /]</span>
                            [/#if]
                          </div>

                          [#-- Next phase --]
                          <div class="cycRow__cell cycRow__next">
                            [#if (phase.next)??]
                              <span class="cycRow__nextName">&rarr; ${(phase.next.name)!} ${(phase.next.year?c)!}</span>
                            [#else]
                              <span class="cycRow__nextName cycRow__nextName--none">[@s.text name="crpPhases.next.none" /]</span>
                            [/#if]
                          </div>

                          [#-- Visible on the timeline --]
                          <div class="cycRow__cell cycRow__timeline">
                            [#if editable]
                              <span class="cycSwitch" role="radiogroup" aria-label="[@s.text name="projectPhases.visible" /]">
                                <input type="radio" class="cycSwitch__input visible-yes" id="visible-yes-${phase_index}" name="${customName}.visible" value="true"[#if (phase.visible)!false] checked[/#if] />
                                <label class="cycSwitch__option" for="visible-yes-${phase_index}">[@s.text name="crpPhases.visible.yes" /]</label>
                                <input type="radio" class="cycSwitch__input visible-no" id="visible-no-${phase_index}" name="${customName}.visible" value="false"[#if !((phase.visible)!false)] checked[/#if] />
                                <label class="cycSwitch__option" for="visible-no-${phase_index}">[@s.text name="crpPhases.visible.no" /]</label>
                              </span>
                            [#else]
                              <span class="cycStatic">[#if (phase.visible)!false][@s.text name="crpPhases.visible.yes" /][#else][@s.text name="crpPhases.visible.no" /][/#if]</span>
                            [/#if]
                          </div>

                          [#-- Open / closed --]
                          <div class="cycRow__cell cycRow__status">
                            [#if editable]
                              <span class="cycSwitch cycSwitch--status[#if !canOpenClose] cycSwitch--locked[/#if]" role="radiogroup" aria-label="[@s.text name="projectPhases.editable" /]">
                                <input type="radio" class="cycSwitch__input editable-yes" id="editable-yes-${phase_index}" name="${customName}.editable" value="true"[#if cycIsOpen] checked[/#if] />
                                <label class="cycSwitch__option cycSwitch__option--open" for="editable-yes-${phase_index}">[@s.text name="crpPhases.status.open" /]</label>
                                <input type="radio" class="cycSwitch__input editable-no" id="editable-no-${phase_index}" name="${customName}.editable" value="false"[#if !cycIsOpen] checked[/#if] />
                                <label class="cycSwitch__option" for="editable-no-${phase_index}">[@s.text name="crpPhases.status.closed" /]</label>
                              </span>
                              [#if !canOpenClose]
                                <span class="cycStatic cycStatic--locked" title="[@s.text name="crpPhases.status.locked" /]">[#if cycIsOpen][@s.text name="crpPhases.status.open" /][#else][@s.text name="crpPhases.status.closed" /][/#if]</span>
                              [/#if]
                            [#else]
                              <span class="cycStatic">[#if cycIsOpen][@s.text name="crpPhases.status.open" /][#else][@s.text name="crpPhases.status.closed" /][/#if]</span>
                            [/#if]
                          </div>

                          [#-- Default landing phase --]
                          <div class="cycRow__cell cycRow__landing">
                            [#if editable]
                              <input type="radio" class="cycLanding__input" id="defaultPhaseID-${phase.id?c}" name="defaultPhaseID" value="${phase.id?c}"[#if cycIsLanding] checked[/#if] />
                              <label class="cycLanding" for="defaultPhaseID-${phase.id?c}">
                                <span class="cycLanding__dot" aria-hidden="true"></span>
                                <span class="sr-only">[@s.text name="crpPhases.landing.set"][@s.param]${(phase.name)!} ${phase.year?c}[/@s.param][/@s.text]</span>
                              </label>
                            [#else]
                              [#-- Decoration only: in read-only mode the row's "Landing" badge is
                                   what a screen reader reads. --]
                              <span class="cycLanding cycLanding--static" aria-hidden="true">
                                <span class="cycLanding__dot"></span>
                              </span>
                            [/#if]
                          </div>

                          [#-- Dates. Not editable on this screen, but they must still be posted
                               back: see the note at the top of the file. --]
                          <div class="cycRow__dateInputs">
                            [@customForm.input name="${customName}.startDate" value="${(phase.startDate?string.medium)!}" i18nkey="From" placeholder="" editable=editable className="startDate datePicker" /]
                            [@customForm.input name="${customName}.endDate" value="${(phase.endDate?string.medium)!}" i18nkey="Until" placeholder="" editable=editable className="endDate datePicker" /]
                          </div>
                        </div>
                      [/#if]
                    [/#list]
                  </div>
                [/#list]

                <div class="cycEmpty" hidden>
                  <p class="cycEmpty__text"></p>
                  <button type="button" class="cycEmpty__reset">[@s.text name="crpPhases.empty.reset" /]</button>
                </div>

              </div>
            </div>

            [#-- ------------------------------ Footer ----------------------------- --]
            [#-- The standard buttons-admin.ftl include is not used here: global.js
                 pins any `.buttons` block to the bottom of the viewport with an
                 inline `right` offset, which would tear this bar out of the card.
                 The hidden phaseID input it also carries is reproduced below. --]
            <div class="cycFooter">
              <input type="hidden" name="phaseID" value="${(actualPhase.id?c)!}" />
              [#if editable]
                <span class="cycFooter__state" role="status" aria-live="polite">
                  <svg class="cycFooter__check" width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true" focusable="false">
                    <path d="M3 8.4 6.2 11.6 13 4.8" stroke="currentColor" stroke-width="1.9" stroke-linecap="round" stroke-linejoin="round"></path>
                  </svg>
                  <span class="cycFooter__dot" aria-hidden="true"></span>
                  <span class="cycFooter__text">[@s.text name="crpPhases.allSaved" /]</span>
                </span>
              [/#if]
              <span class="cycFooter__actions">
              [#if editable]
                <button type="button" class="cycFooter__discard" disabled>[@s.text name="crpPhases.discard" /]</button>
                [@s.submit type="button" name="save" cssClass="button-save cycFooter__save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /][/@s.submit]
              [#else]
                [#if canEdit]
                  <a href="[@s.url][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> [@s.text name="form.buttons.edit" /]</a>
                [/#if]
              [/#if]
              </span>
            </div>

          </div>
          [/#if]
        </div>

        [/@s.form]
        
      </div>
    </div>
  </div>
</section>


[#include "/WEB-INF/global/pages/footer.ftl" /]
