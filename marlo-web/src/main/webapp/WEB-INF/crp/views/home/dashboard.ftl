[#ftl]
[#assign title = "Welcome to MARLO" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["cytoscape","cytoscape-panzoom","cytoscape-qtip","qtip2","datatables.net", "datatables.net-bs"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/home/dashboard.js?20250509",
  "${baseUrlCdn}/global/js/impactGraphic.js"
  ]
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/home/dashboard.css?20250930",
  "${baseUrlCdn}/global/css/customDataTable.css?20250509",
  "${baseUrlCdn}/global/css/impactGraphic.css",
  "https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css"
  ]
/]
[#assign currentSection = "home" /]
[#assign breadCrumb = [
  {"label":"home", "nameSpace":"", "action":""}
]/]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/crp/macros/projectsListTemplate.ftl" as projectList /]
[#import "/WEB-INF/global/macros/homeDashboard.ftl" as indicatorLists /]

[#assign timeline = [
  {"id":"1", "startDate":"11/28/2016", "endDate":"11/30/2016","what":"MARLO opens for Impact Pathway","who":"Flagship Leaders"},
  {"id":"2", "startDate":"12/01/2016", "endDate":"12/02/2016","what":"Create new projects according to new budget distribution; Assign W1/W2 budget to all projects.","who":"Finance Manager"},
  {"id":"3", "startDate":"12/01/2016", "endDate":"12/06/2016","what":"Pre-set projects portfolio","who":"Flagship Leaders and Regional Program Leaders"},
  {"id":"4", "startDate":"12/07/2016", "endDate":"01/16/2017","what":"MARLO opens for planning (Project Leaders) ","who":"Project Leaders"},
  {"id":"5", "startDate":"12/19/2016", "endDate":"01/10/2017","what":"Management liaison to review the plan, liaise with the PL and approve/make recommendations for project submission","who":"Flagship Leaders and Regional Program Leaders"},
  {"id":"6", "startDate":"01/11/2017", "endDate":"01/13/2017","what":"PLs to make changes accordingly and submit the project","who":"Project Leaders"},
  {"id":"5", "startDate":"01/16/2017", "endDate":"",          "what":"MARLO closes planning stage","who":"KDS Team"},
  {"id":"7", "startDate":"02/01/2017", "endDate":"02/17/2017","what":"Project Leaders and Contact Pounts will be responsible to input detailed information regarding their projects for 2016.","who":""},
  {"id":"8", "startDate":"02/20/2017", "endDate":"02/24/2017","what":"<small>Contact Points will be responsible to report on the CRP indicators and on any publications that are not directly linked to a particular project. <br>Regional Program Leaders will be responsible to complete the synthesis by MOG and by CCAFS Outcome, based on the information reported by Project Leaders.</small>","who":""},
  {"id":"9", "startDate":"02/27/2017", "endDate":"03/03/2017","what":"Flagship Program Leaders will be responsible to report on the CRP indicators, synthesis by MOG and synthesis by CCAFS Outcome based on the information reported by project leaders and Regional Program leaders.","who":""}
]/]

[#if switchSession]
  <script type="text/javascript">
    window.location.href = window.location.href;
  </script>
[/#if]
<!--  africa-color.svg  -->


  <div class="container">
    [#-- What do you want to do --]

  <section class="marlo-content">
  [#-- Hide map section only when this specificity is active --]
  [#if !action.hasSpecificities('homepage_hide_section_map')]
  [#--
    Cluster banner. The 12 map hotspots keep project id and label together so
    they cannot drift apart the way they did when the ids lived here and the
    labels lived in dashboard.js. Coordinates are the percentage of the map
    image at which each dot is centred, measured from the previous layout.
  --]
  [#assign clusterHotspots = [
    {"projectID": 102076, "label": "Senegal: Activities led by ILRI",                              "x": "11.25", "y": "31.57"},
    {"projectID": 102088, "label": "Ethiopia: Activities led by ILRI",                             "x": "77.25", "y": "48.87"},
    {"projectID": 102081, "label": "Ghana: Activities led by IITA",                                "x": "25.25", "y": "41.39"},
    {"projectID": 102085, "label": "Kenya: Activities led by ILRI",                                "x": "74.25", "y": "36.71"},
    {"projectID": 102082, "label": "Zambia: Activities led by IWMI",                               "x": "61.25", "y": "73.19"},
    {"projectID": 102084, "label": "Theme 1: Activities led by ILRI",                              "x": "44.25", "y": "1.64"},
    {"projectID": 102077, "label": "Theme 2: Activities led by the Alliance",                      "x": "8.75",  "y": "11.46"},
    {"projectID": 102086, "label": "West Africa",                                                  "x": "2.75",  "y": "37.65"},
    {"projectID": 102087, "label": "Theme 4: Activities led by Alliance",                          "x": "37.25", "y": "62.90"},
    {"projectID": 102090, "label": "Theme 3: Gender and Social Inclusion Leader (Lead by ILRI)",   "x": "81.75", "y": "73.66"},
    {"projectID": 102080, "label": "East and Southern Africa",                                     "x": "78.25", "y": "24.55"},
    {"projectID": 102083, "label": "Mali: Activities led by AfricaRice",                           "x": "23.75", "y": "26.89"}
  ]/]

  <section class="clusterBanner" id="clusterBanner">
    <div class="clusterBanner__body">
      <div class="clusterBanner__head">
        <svg class="clusterBanner__icon" width="17" height="17" viewBox="0 0 18 18" fill="none" aria-hidden="true"><circle cx="9" cy="9" r="7.2" stroke="currentColor" stroke-width="1.5"/><path d="M9 8.1v4.3" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/><circle cx="9" cy="5.6" r="1" fill="currentColor"/></svg>
        <h2 class="clusterBanner__title">[@s.text name="dashboard.cluster.title" /]</h2>
        <button type="button" class="clusterBanner__toggle" id="clusterBannerToggle"
          aria-expanded="true" aria-controls="clusterBannerContent"
          data-label-hide="[@s.text name="dashboard.cluster.hide" /]"
          data-label-show="[@s.text name="dashboard.cluster.show" /]">
          <span>[@s.text name="dashboard.cluster.hide" /]</span>
          <svg width="10" height="10" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M2.5 4.5 6 8l3.5-3.5" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"/></svg>
        </button>
      </div>

      <div class="clusterBanner__content" id="clusterBannerContent">
        <p class="clusterBanner__text">[@s.text name="dashboard.cluster.description" /]</p>
        <div class="clusterBanner__links">
          <a class="clusterBanner__link" href="[@s.url namespace="/clusters" action='${(crpSession)!}/projectsList'][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
            [@s.text name="dashboard.cluster.browse" /]
            <svg width="12" height="12" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M4 2.5 7.5 6 4 9.5" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </a>
          <a class="clusterBanner__link clusterBanner__link--muted" target="_blank" rel="noreferrer noopener" href="[@s.url namespace="/" action='glossary'][/@s.url]">[@s.text name="dashboard.cluster.glossary" /]</a>
        </div>
      </div>
    </div>

    <div class="clusterBanner__map" id="clusterBannerMap">
      <div class="clusterMap">
        <img src="${baseUrlCdn}/global/images/Map_africa.svg" alt="[@s.text name="dashboard.cluster.mapAlt" /]">
        [#list clusterHotspots as hotspot]
          <a class="clusterMap__spot" style="left:${hotspot.x}%;top:${hotspot.y}%"
            href="[@s.url namespace="/clusters" action='${(crpSession)!}/description'][@s.param name='projectID']${hotspot.projectID?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]"
            target="_blank" rel="noreferrer noopener" aria-label="${hotspot.label}">
            <span class="clusterMap__tip">${hotspot.label}</span>
          </a>
        [/#list]
      </div>
    </div>
  </section>
  [/#if]

[#if action.hasSpecificities('homepage_timeline_active') ]
  [#--
    Reporting timeline.

    Every coordinate is derived from the phase dates: the axis runs from the
    first day of the earliest phase's month to the first day of the month after
    the latest phase's end, and positions are that span expressed as a
    percentage. Lane colour follows the same open/upcoming/closed rule as the
    phase selector, so both components cannot disagree.
  --]
  [#assign tlDayMs = 86400000 /]
  [#assign tlToday = .now?date /]

  [#function tlPhaseStatus phase]
    [#if (phase.editable)!false][#return "open"][/#if]
    [#if (phase.startDate)?? && phase.startDate?date gt tlToday][#return "upcoming"][/#if]
    [#return "closed"]
  [/#function]

  [#-- Only phases with a usable range can be plotted. --]
  [#assign tlPhases = [] /]
  [#list phases as phase]
    [#if (phase.startDate)?? && (phase.endDate)??]
      [#assign tlPhases = tlPhases + [phase] /]
    [/#if]
  [/#list]

  [#if tlPhases?has_content]
    [#assign tlActive = [] /]
    [#assign tlClosed = [] /]
    [#list tlPhases as phase]
      [#if tlPhaseStatus(phase) == "closed"]
        [#assign tlClosed = tlClosed + [phase] /]
      [#else]
        [#assign tlActive = tlActive + [phase] /]
      [/#if]
    [/#list]

    [#assign tlOpenCount = 0 /]
    [#list tlActive as phase][#if tlPhaseStatus(phase) == "open"][#assign tlOpenCount = tlOpenCount + 1 /][/#if][/#list]

    [#-- Lanes are the non-closed phases; closed ones sit behind the toggle. --]
    [#assign tlLanes = tlActive /]

    <section class="reportTimeline">
      <div class="reportTimeline__head">
        <div class="reportTimeline__heading">
          <h2 class="reportTimeline__title">[@s.text name="dashboard.reportingTimeline.title" /]</h2>
          <span class="reportTimeline__subtitle">
            [@s.text name="dashboard.reportingTimeline.today"][@s.param]${tlToday?string("dd MMMM yyyy")}[/@s.param][/@s.text] &middot;
            [#if tlOpenCount == 0][@s.text name="dashboard.reportingTimeline.noPhasesOpen" /]
            [#elseif tlOpenCount == 1][@s.text name="dashboard.reportingTimeline.onePhaseOpen" /]
            [#else][@s.text name="dashboard.reportingTimeline.phasesOpen"][@s.param]${tlOpenCount?c}[/@s.param][/@s.text][/#if]
          </span>
        </div>
        <div class="reportTimeline__legend">
          <span class="reportTimeline__key reportTimeline__key--open">[@s.text name="dashboard.reportingTimeline.legend.open" /]</span>
          <span class="reportTimeline__key reportTimeline__key--upcoming">[@s.text name="dashboard.reportingTimeline.legend.upcoming" /]</span>
          <span class="reportTimeline__key reportTimeline__key--closed">[@s.text name="dashboard.reportingTimeline.legend.closed" /]</span>
          <span class="reportTimeline__key reportTimeline__key--today">[@s.text name="dashboard.reportingTimeline.legend.today" /]</span>
        </div>
      </div>

      [#if tlLanes?has_content]
        [#-- Axis bounds, snapped to whole months so the labels line up. --]
        [#assign tlMinStart = tlLanes[0].startDate?date /]
        [#assign tlMaxEnd = tlLanes[0].endDate?date /]
        [#list tlLanes as phase]
          [#if phase.startDate?date lt tlMinStart][#assign tlMinStart = phase.startDate?date /][/#if]
          [#if phase.endDate?date gt tlMaxEnd][#assign tlMaxEnd = phase.endDate?date /][/#if]
        [/#list]

        [#assign tlY0 = tlMinStart?string("yyyy")?number /]
        [#assign tlM0 = tlMinStart?string("MM")?number /]
        [#assign tlY1 = tlMaxEnd?string("yyyy")?number /]
        [#assign tlM1 = tlMaxEnd?string("MM")?number /]
        [#assign tlMonths = (tlY1 - tlY0) * 12 + (tlM1 - tlM0) + 1 /]

        [#assign tlAxisStart = (tlY0?c + "-" + tlM0?string("00") + "-01")?date("yyyy-MM-dd") /]
        [#assign tlEndIdx = tlM0 + tlMonths /]
        [#assign tlEndYear = tlY0 + ((tlEndIdx - 1) / 12)?floor /]
        [#assign tlEndMonth = tlEndIdx - (((tlEndIdx - 1) / 12)?floor) * 12 /]
        [#assign tlAxisEnd = (tlEndYear?c + "-" + tlEndMonth?string("00") + "-01")?date("yyyy-MM-dd") /]
        [#assign tlSpan = tlAxisEnd?long - tlAxisStart?long /]

        [#function tlPct instant]
          [#return (((instant?long - tlAxisStart?long) / tlSpan) * 100)?string("0.##")]
        [/#function]

        <div class="reportTimeline__axis">
          <div class="reportTimeline__labelCol"></div>
          <div class="reportTimeline__scale">
            [#list 0..(tlMonths - 1) as i]
              [#assign tlIdx = tlM0 + i /]
              [#assign tlYear = tlY0 + ((tlIdx - 1) / 12)?floor /]
              [#assign tlMonth = tlIdx - (((tlIdx - 1) / 12)?floor) * 12 /]
              [#assign tlTick = (tlYear?c + "-" + tlMonth?string("00") + "-01")?date("yyyy-MM-dd") /]
              <span class="reportTimeline__month" style="left:${tlPct(tlTick)}%">${tlTick?string("MMM")?upper_case}</span>
            [/#list]
          </div>
          <div class="reportTimeline__statusCol"></div>
        </div>

        <div class="reportTimeline__lanes">
          <div class="reportTimeline__grid" aria-hidden="true">
            [#list 1..(tlMonths - 1) as i]
              [#assign tlIdx = tlM0 + i /]
              [#assign tlYear = tlY0 + ((tlIdx - 1) / 12)?floor /]
              [#assign tlMonth = tlIdx - (((tlIdx - 1) / 12)?floor) * 12 /]
              <span class="reportTimeline__gridline" style="left:${tlPct((tlYear?c + "-" + tlMonth?string("00") + "-01")?date("yyyy-MM-dd"))}%"></span>
            [/#list]
            [#if tlToday gte tlAxisStart && tlToday lt tlAxisEnd]
              <span class="reportTimeline__now" style="left:${tlPct(tlToday)}%"></span>
              <span class="reportTimeline__nowTag" style="left:${tlPct(tlToday)}%">[@s.text name="dashboard.reportingTimeline.legend.today" /]</span>
            [/#if]
          </div>

          [#list tlLanes as phase]
            [#assign tlStatus = tlPhaseStatus(phase) /]
            [#assign tlLeft = tlPct(phase.startDate?date) /]
            [#assign tlWidth = (((phase.endDate?long - phase.startDate?long) / tlSpan) * 100)?string("0.##") /]
            [#assign tlRemaining = ((phase.endDate?long - tlToday?long) / tlDayMs)?round /]
            [#assign tlUntilOpen = ((phase.startDate?long - tlToday?long) / tlDayMs)?round /]
            <div class="reportTimeline__lane">
              <div class="reportTimeline__labelCol">
                <span class="reportTimeline__laneName reportTimeline__laneName--${tlStatus}">${(phase.composedName)!}</span>
                <span class="reportTimeline__laneDates">${phase.startDate?date?string("dd MMM")} &ndash; ${phase.endDate?date?string("dd MMM yyyy")}</span>
              </div>
              <div class="reportTimeline__track">
                <div class="reportTimeline__bar reportTimeline__bar--${tlStatus}"
                  style="left:${tlLeft}%;width:${tlWidth}%"
                  title="${(phase.composedName)!} &middot; ${phase.startDate?date?string("dd MMM")} &ndash; ${phase.endDate?date?string("dd MMM yyyy")}">
                  <span>${(phase.composedName)!}</span>
                </div>
              </div>
              <div class="reportTimeline__statusCol">
                [#if tlStatus == "open"]
                  <span class="reportTimeline__chip[#if tlRemaining lte 21] reportTimeline__chip--urgent[/#if]">
                    [#if tlRemaining gt 0][@s.text name="dashboard.reportingTimeline.daysLeft"][@s.param]${tlRemaining?c}[/@s.param][/@s.text]
                    [#else][@s.text name="dashboard.reportingTimeline.lastDay" /][/#if]
                  </span>
                [#elseif tlStatus == "upcoming"]
                  <span class="reportTimeline__muted">[@s.text name="dashboard.reportingTimeline.opensIn"][@s.param]${tlUntilOpen?c}[/@s.param][/@s.text]</span>
                [/#if]
              </div>
            </div>
          [/#list]

          [#if tlClosed?has_content]
            <div class="reportTimeline__closed" id="reportTimelineClosed" hidden>
              [#list tlClosed?reverse as phase]
                <div class="reportTimeline__lane">
                  <div class="reportTimeline__labelCol">
                    <span class="reportTimeline__laneName reportTimeline__laneName--closed">${(phase.composedName)!}</span>
                    <span class="reportTimeline__laneDates">${phase.startDate?date?string("dd MMM")} &ndash; ${phase.endDate?date?string("dd MMM yyyy")}</span>
                  </div>
                  <div class="reportTimeline__track"><div class="reportTimeline__hatch"></div></div>
                  <div class="reportTimeline__statusCol">
                    <span class="phaseBadge phaseBadge--closed">[@s.text name="dashboard.reportingTimeline.legend.closed" /]</span>
                    <button type="button" class="reportTimeline__view" data-phase-id="${phase.id?c}">[@s.text name="dashboard.reportingTimeline.view" /]</button>
                  </div>
                </div>
              [/#list]
            </div>
          [/#if]
        </div>
      [#else]
        [#-- No open or upcoming phase: show what is coming instead of an empty chart. --]
        <div class="reportTimeline__empty">
          <div class="reportTimeline__emptyMain">
            <span class="reportTimeline__emptyTitle">[@s.text name="dashboard.reportingTimeline.nothingDue" /]</span>
            <p>[@s.text name="dashboard.reportingTimeline.nothingDueText" /]</p>
          </div>
        </div>
      [/#if]

      [#if tlClosed?has_content]
        <div class="reportTimeline__foot">
          <button type="button" class="reportTimeline__toggle" id="reportTimelineToggle"
            aria-expanded="false" aria-controls="reportTimelineClosed"
            data-label-show="[@s.text name="dashboard.reportingTimeline.showClosed"][@s.param]${tlClosed?size?c}[/@s.param][/@s.text]"
            data-label-hide="[@s.text name="dashboard.reportingTimeline.hideClosed" /]">
            <svg width="12" height="12" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M2.5 4.5 6 8l3.5-3.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/></svg>
            <span>[@s.text name="dashboard.reportingTimeline.showClosed"][@s.param]${tlClosed?size?c}[/@s.param][/@s.text]</span>
          </button>
        </div>
      [/#if]
    </section>
  [/#if]
[/#if]

  [#assign browseScope = (actualPhase.composedName)!'' /]
  [#assign phaseEditable = (actualPhase.editable)!false /]

  <section class="dashboardBrowse">
    <div class="dashboardBrowse__rail">
      <h3 class="dashboardBrowse__railTitle">[@s.text name="dashboard.browse.title" /]</h3>
      <div class="dashboardBrowse__cats">
        <button type="button" class="dashboardBrowse__cat is-active" id="projects" aria-pressed="true"
          data-pane="myProjects" data-scope="[@s.text name="dashboard.myProjects.title" /]">
          <svg width="18" height="18" viewBox="0 0 20 20" fill="none" aria-hidden="true"><rect x="2.5" y="2.5" width="6" height="6" rx="1.5" stroke="currentColor" stroke-width="1.5"/><rect x="11.5" y="2.5" width="6" height="6" rx="1.5" stroke="currentColor" stroke-width="1.5"/><rect x="2.5" y="11.5" width="6" height="6" rx="1.5" stroke="currentColor" stroke-width="1.5"/><rect x="11.5" y="11.5" width="6" height="6" rx="1.5" stroke="currentColor" stroke-width="1.5"/></svg>
          <span class="dashboardBrowse__catLabel">[@s.text name="dashboard.myProjects.title" /]</span>
          <span class="dashboardBrowse__catCount">${(myProjects?size)!0}</span>
        </button>
        [#if action.isAiccra()]
          <button type="button" class="dashboardBrowse__cat" id="deliverables" aria-pressed="false"
            data-pane="myDeliverables" data-scope="[@s.text name="dashboard.myDeliverables.title" /]">
            <svg width="18" height="18" viewBox="0 0 20 20" fill="none" aria-hidden="true"><path d="M5 2.5h6.5L16 7v10.5H5V2.5Z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/><path d="M11.5 2.5V7H16" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/></svg>
            <span class="dashboardBrowse__catLabel">[@s.text name="dashboard.myDeliverables.title" /]</span>
            <span class="dashboardBrowse__catCount">${(myDeliverables?size)!0}</span>
          </button>
          <button type="button" class="dashboardBrowse__cat" id="studies" aria-pressed="false"
            data-pane="myStudies" data-scope="[@s.text name="dashboard.studies.table.title" /]">
            <svg width="18" height="18" viewBox="0 0 20 20" fill="none" aria-hidden="true"><rect x="3" y="2.5" width="14" height="15" rx="2" stroke="currentColor" stroke-width="1.5"/><path d="M6.5 7h7M6.5 10.5h7M6.5 14h4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
            <span class="dashboardBrowse__catLabel">[@s.text name="dashboard.studies.table.title" /]</span>
            <span class="dashboardBrowse__catCount">${(myStudies?size)!0}</span>
          </button>
          [#if action.hasSpecificities('innovation_section_active') ]
            <button type="button" class="dashboardBrowse__cat" id="innovations" aria-pressed="false"
              data-pane="myInnovations" data-scope="[@s.text name="dashboard.innovations.table.title" /]">
              <svg width="18" height="18" viewBox="0 0 20 20" fill="none" aria-hidden="true"><path d="M7 13.5a5 5 0 1 1 6 0V15H7v-1.5Z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/><path d="M8 17.5h4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
              <span class="dashboardBrowse__catLabel">[@s.text name="dashboard.innovations.table.title" /]</span>
              <span class="dashboardBrowse__catCount">${(myInnovations?size)!0}</span>
            </button>
          [/#if]
        [/#if]
      </div>
      <p class="dashboardBrowse__note">[@s.text name="dashboard.browse.note" /]</p>
    </div>

    <div class="dashboardBrowse__panel">
      <div class="dashboardBrowse__scope">
        <span class="dashboardBrowse__scopeLabel">[@s.text name="dashboard.browse.showing" /]</span>
        <span class="dashboardBrowse__scopeChip" id="dashboardScopeChip"
          data-scope-template="[@s.text name="dashboard.browse.scope"][@s.param]{0}[/@s.param][@s.param]${browseScope}[/@s.param][/@s.text]">
          [@s.text name="dashboard.browse.scope"][@s.param][@s.text name="dashboard.myProjects.title" /][/@s.param][@s.param]${browseScope}[/@s.param][/@s.text]
        </span>
        [#if phaseEditable]
          <span class="dashboardBrowse__state dashboardBrowse__state--open">
            <svg width="12" height="12" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M4.5 5.5V4a1.5 1.5 0 0 1 3 0" stroke="currentColor" stroke-width="1.3" stroke-linecap="round"/><rect x="2.8" y="5.4" width="6.4" height="4.4" rx="1.2" stroke="currentColor" stroke-width="1.3"/></svg>
            [@s.text name="dashboard.browse.editable" /]
          </span>
        [#else]
          <span class="dashboardBrowse__state dashboardBrowse__state--locked">
            <svg width="12" height="12" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M4 5.4V4a2 2 0 0 1 4 0v1.4" stroke="currentColor" stroke-width="1.3" stroke-linecap="round"/><rect x="2.8" y="5.4" width="6.4" height="4.4" rx="1.2" stroke="currentColor" stroke-width="1.3"/></svg>
            [@s.text name="dashboard.browse.readOnly" /]
          </span>
        [/#if]
      </div>

      <div class="tab-content">
        <div role="tabpanel" class="tab-pane fade in active" id="myProjects">
          [#if !action.isAiccra()]
            [@projectList.dashboardProjectsList projects=myProjects canValidate=true canEdit=true namespace="/projects" defaultAction="${(crpSession)!}/description" /]
          [#else]
            [@projectList.dashboardProjectsList projects=myProjects canValidate=true canEdit=true namespace="/clusters" defaultAction="${(crpSession)!}/description" /]
          [/#if]
        </div>

        <div role="tabpanel" class="tab-pane fade" id="myDeliverables">
          [@indicatorLists.deliverablesHomeList deliverables=myDeliverables canValidate=true canEdit=true namespace="/clusters" defaultAction="${(crpSession)!}/deliverable" /]
        </div>

        <div role="tabpanel" class="tab-pane fade" id="myStudies">
          [@indicatorLists.studiesHomeList studies=myStudies canValidate=true canEdit=true namespace="/clusters" defaultAction="${(crpSession)!}/study" /]
        </div>

        <div role="tabpanel" class="tab-pane fade" id="myInnovations">
          [@indicatorLists.innovationsHomeList innovations=myInnovations canValidate=true canEdit=true namespace="/clusters" defaultAction="${(crpSession)!}/innovation" /]
        </div>

        <div role="tabpanel" class="tab-pane fade" id="impactP">
          <div id="infoRelations" class="panel panel-default">
            <div class="panel-heading"><strong>Relations</strong></div>
            <div id="infoContent" class="panel-body"><ul></ul></div>
          </div>
          <div id="contentGraph">
            <div id="impactGraphic"></div>
            <span title="View full graph" id="fullscreen" class="glyphicon glyphicon-fullscreen"></span>
          </div>
        </div>
      </div>
    </div>
  </section>


  

    [#if !action.isAiccra()]
    <div class="homeTitle"><b>[@s.text name="dashboard.decisionTree.title" /]</b></div>
  [/#if]
    [#-- What do you want to do --]
    <div id="decisionTree">

      [#if centerGlobalUnit]
        [#-- CENTER Impact patchway --]
        <div class="flex-container">
          <div id="newImpactPathway" class="option hvr-float">
            <a href="[@s.url action="impactPathway/${centerSession}/programimpacts"][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              <p>[@s.text name="dashboard.decisionTree.defineImpact" /]</p>
            </a>
          </div>
        </div>

        [#-- Projects --]
        <div class="flex-container">
          <div id="startMonitoring" class="option hvr-float">
            <a href="[@s.url action="monitoring/${centerSession}/monitoringOutcomesList"][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              <p>[@s.text name="dashboard.decisionTree.startMonitoring" /]</p>
            </a>
          </div>
        </div>

        [#-- Summaries --]
        <div class="flex-container">
          <div id="finalDes" class="option hvr-float"">
            <a href="[@s.url namespace="/projects" action='${crpSession}/projectsList'][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              <p>[@s.text name="dashboard.decisionTree.updateProject" /]</p>
            </a>
          </div>
        </div>

      [#else]

        [#if !aiccra]
          [#-- Add new Project --]
          <div class="flex-container">
          [#assign canAddCoreProject = (action.canAddCoreProject()) && (!crpClosed) && (!reportingActive) && (action.getActualPhase().editable)]
          [#if canAddCoreProject]<a href="[@s.url namespace="/projects" action='${crpSession}/addNewCoreProject'][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">[/#if]
            <div id="newProject" class="hvr-float option ${(!canAddCoreProject)?string('disabled','')}" ${(!canAddCoreProject)?string('title="This link is disabled"','')}>
              <p>[@s.text name="dashboard.decisionTree.newProject" /]</p>
            </div>
          [#if canAddCoreProject]</a>[/#if]
          </div>

          [#-- Update an ongoing Project --]
          <div class="flex-container">
          [#assign canUpdateOngoingProjects = !crpClosed && canEditPhase ]
          [#if canUpdateOngoingProjects]<a href="[@s.url namespace="/projects" action='${crpSession}/projectsList'][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]"> [/#if]
            <div id="updatePlanning" class="hvr-float option ${(!canUpdateOngoingProjects)?string('disabled','')}" ${(!canUpdateOngoingProjects)?string('title="This link is disabled"','')}>
              <p>[@s.text name="dashboard.decisionTree.updateProject" /]</p>
            </div>
          [#if canUpdateOngoingProjects]</a>[/#if]
          </div>

          [#-- Evaluate Project --]
          <div class="flex-container">
            <div id="reportProject" class="option disabled" title="This link is disabled">
              <p>[@s.text name="dashboard.decisionTree.evaluateProject" /]</p>
            </div>
          </div>
        [/#if]

      <div class="clearfix"></div>
    </div>
    [/#if]


    [#-- Shorcuts --]
    <div id="shorcuts"  class="col-md-5">

        [#-- The AICCRA cluster copy now lives in the banner at the top of the
             page (dashboard.cluster.* in the properties files). --]
        [#if !aiccra]
            [@s.text name="dashboard.aiccra.instructions" ] [@s.param] <a href="https://docs.google.com/document/d/1hy2yt6E4pJ5orGqHxBSX_ACcr72pPTwaSesQ9P6vHYQ/edit" target="_blank">here</a>.[/@s.param][/@s.text]
            <img src="${baseUrlCdn}/global/images/aiccra-planning.png" width="450">
        [/#if]

    </div>


    <div id="impactGraphic-content"  style="display:none;" >

  [#-- Information panel --]
  <div id="infoRelation" class="panel panel-default">
    <div class="panel-heading"><strong>Relations</strong></div>
    <div id="infoContent" class="panel-body">
     <ul></ul>
    </div>
  </div>

  [#-- Controls --]
  <div id="controls" class="">
    <span id="zoomIn" class="glyphicon glyphicon-zoom-in tool"></span>
    <span id="zoomOut" class="glyphicon glyphicon-zoom-out tool "></span>
    <span id="panRight" class="glyphicon glyphicon-arrow-right tool "></span>
    <span id="panDown" class="glyphicon glyphicon-arrow-down tool "></span>
    <span id="panLeft" class="glyphicon glyphicon-arrow-left tool "></span>
    <span id="panUp" class="glyphicon glyphicon-arrow-up tool "></span>
    <span id="resize" class="glyphicon glyphicon-resize-full  tool"></span>
  </div>

  [#-- Download button--]
  <a class="download" href=""><span title="download" id="buttonDownload"><span class="glyphicon glyphicon-download-alt"></span></span></a>

  <div id="impactGraphic-fullscreen"></div>
</div>
  </div>


</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]