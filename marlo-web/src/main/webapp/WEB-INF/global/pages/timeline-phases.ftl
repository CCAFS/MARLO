[#ftl]
[#--
  Reporting phase selector.

  Open phases are surfaced as pills; everything else lives behind the
  "All phases" panel, which groups by year and can be filtered.
  Phase switching still goes through setPhaseID() in timeline-phases.js.
--]
[#if phases??]
  [#if phases?size > 1]

    [#--
      `editable` is the source of truth for an open phase — an administrator
      can reopen a phase whose dates have passed. Dates are only used to tell
      a closed phase apart from one that has not started yet.
    --]
    [#function phaseStatus phase]
      [#if (phase.editable)!false][#return "open"][/#if]
      [#if (phase.startDate)?? && phase.startDate?date gt .now?date][#return "upcoming"][/#if]
      [#return "closed"]
    [/#function]

    [#macro phaseBadge status solid=false]
      <span class="phaseBadge phaseBadge--${status}[#if solid] phaseBadge--solid[/#if]">[@s.text name="phaseSelector.status.${status}" /]</span>
    [/#macro]

    [#assign openCount = 0 /]
    [#assign closedCount = 0 /]
    [#assign upcomingCount = 0 /]
    [#assign phaseYears = [] /]
    [#assign pinnedPhases = [] /]
    [#list phases as phase]
      [#assign status = phaseStatus(phase) /]
      [#if status == "open"]
        [#assign openCount = openCount + 1 /]
      [#elseif status == "upcoming"]
        [#assign upcomingCount = upcomingCount + 1 /]
      [#else]
        [#assign closedCount = closedCount + 1 /]
      [/#if]
      [#-- Pills show every open phase plus the loaded one, so the current
           phase stays visible even when it is already closed. --]
      [#if status == "open" || ((actualPhase.id)!-1) == phase.id]
        [#assign pinnedPhases = pinnedPhases + [phase] /]
      [/#if]
      [#if !phaseYears?seq_contains(phase.year)]
        [#assign phaseYears = phaseYears + [phase.year] /]
      [/#if]
    [/#list]

    [#attempt]
      [#assign canManagePhases = (action.canAcessCrpAdmin())!false /]
    [#recover]
      [#assign canManagePhases = false /]
    [/#attempt]

    [#-- .container keeps the selector on the same 32px gutters as the rest of
         the page; #timelineScroll is kept because global.js anchors the
         floating phase tag to it. --]
    <div class="container hidden-print">
    <div id="timelineScroll" class="phaseSelector">
      <div class="loading timeline-loader" style="display:none"></div>

      <span class="phaseSelector__label">[@s.text name="phaseSelector.label" /]</span>

      [#list pinnedPhases as phase]
        [#assign status = phaseStatus(phase) /]
        [#assign isCurrent = ((actualPhase.id)!-1) == phase.id /]
        <button type="button"
          class="phasePill[#if isCurrent] phasePill--current[/#if]"
          data-phase-id="${phase.id?c}"
          aria-pressed="${isCurrent?string}"
          [#if isCurrent]disabled="disabled"[/#if]>
          <span class="phasePill__text">
            <span class="phasePill__name">${(phase.composedName)!}</span>
            [#if (phase.endDate)??]
              <span class="phasePill__dates">[@s.text name="phaseSelector.endsOn"][@s.param]${phase.endDate?date?string("dd MMM yyyy")}[/@s.param][/@s.text]</span>
            [/#if]
          </span>
          [@phaseBadge status=status solid=isCurrent /]
          [#if isCurrent]
            <svg class="phasePill__check" width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M3 8.4 6.2 11.6 13 4.8" stroke="currentColor" stroke-width="1.9" stroke-linecap="round" stroke-linejoin="round"/></svg>
          [/#if]
        </button>
      [/#list]

      <div class="phaseSelector__all">
        <button type="button" id="allPhasesToggle" class="phaseAllBtn"
          aria-expanded="false" aria-controls="allPhasesPanel" aria-haspopup="dialog">
          <svg width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="8" cy="8" r="6.2" stroke="currentColor" stroke-width="1.5"/><path d="M8 4.6V8l2.4 1.6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
          <span>[@s.text name="phaseSelector.allPhases"][@s.param]${phases?size?c}[/@s.param][/@s.text]</span>
          <svg class="phaseAllBtn__caret" width="10" height="10" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M2.5 4.5 6 8l3.5-3.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/></svg>
        </button>

        <div id="allPhasesPanel" class="phasePanel" role="dialog"
          aria-label="[@s.text name="phaseSelector.allPhasesLabel" /]" hidden>
          <div class="phasePanel__search">
            <span class="phaseSearchBox">
              <svg width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="7" cy="7" r="4.6" stroke="currentColor" stroke-width="1.5"/><path d="M10.6 10.6 14 14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
              <input type="text" id="phaseSearchInput" autocomplete="off"
                placeholder="[@s.text name="phaseSelector.search" /]"
                aria-label="[@s.text name="phaseSelector.search" /]" />
            </span>
          </div>

          <div class="phasePanel__list">
            [#list phaseYears?reverse as phaseYear]
              <div class="phasePanel__year" data-phase-year="${phaseYear?c}">${phaseYear?c}</div>
              [#list phases as phase]
                [#if phase.year == phaseYear]
                  [#assign status = phaseStatus(phase) /]
                  [#assign isCurrent = ((actualPhase.id)!-1) == phase.id /]
                  <button type="button"
                    class="phaseRow phaseRow--${status}[#if isCurrent] phaseRow--current[/#if]"
                    data-phase-id="${phase.id?c}"
                    data-phase-search="${((phase.composedName)!)?lower_case}"
                    [#if isCurrent]disabled="disabled"[/#if]>
                    <span class="phaseRow__text">
                      <span class="phaseRow__name">${(phase.composedName)!}[#if isCurrent]<em>[@s.text name="phaseSelector.currentlyLoaded" /]</em>[/#if]</span>
                      [#if (phase.startDate)?? && (phase.endDate)??]
                        <span class="phaseRow__dates">${phase.startDate?date?string("dd MMM")} &ndash; ${phase.endDate?date?string("dd MMM yyyy")}</span>
                      [/#if]
                    </span>
                    [@phaseBadge status=status /]
                  </button>
                [/#if]
              [/#list]
            [/#list]
            <p class="phasePanel__empty" hidden>[@s.text name="phaseSelector.noResults" /]</p>
          </div>

          <div class="phasePanel__footer">
            <span class="phasePanel__summary">[@s.text name="phaseSelector.summary"][@s.param]${openCount?c}[/@s.param][@s.param]${closedCount?c}[/@s.param][@s.param]${upcomingCount?c}[/@s.param][/@s.text]</span>
            [#if canManagePhases]
              <a href="[@s.url namespace="/admin" action="${(crpSession)!}/crpPhases"][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">[@s.text name="phaseSelector.manager" /]</a>
            [/#if]
          </div>
        </div>
      </div>
    </div>

    [#-- Floating tag that reveals the loaded phase once the selector scrolls away. --]
    <div id="phaseTag" class="phaseTag">
      <span class="${(actualPhase.isReporting())?string('reporting','planning')}" style="display:none;">[#if centerGlobalUnit]${(actualPhase.year?c)!}[#else]${(actualPhase.composedName)!}[/#if]</span>
    </div>
    </div>

    [#if pageLibs??]
      [#assign pageLibs = pageLibs + ["jsUri"] /]
    [#else]
      [#assign pageLibs = ["jsUri"] /]
    [/#if]
    [#if customJS??]
      [#assign customJS = [ "${baseUrlCdn}/global/js/timeline-phases.js?20260813" ] + customJS /]
    [#else]
      [#assign customJS = [ "${baseUrlCdn}/global/js/timeline-phases.js?20260813" ] /]
    [/#if]

  [/#if]
[/#if]
