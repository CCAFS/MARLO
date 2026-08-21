[#ftl]
[#assign title = "Welcome to MARLO" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["cytoscape","cytoscape-panzoom","cytoscape-qtip","qtip2","datatables.net", "datatables.net-bs"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/home/dashboard.js?20260819",
  "${baseUrlMedia}/js/home/schedule.js?20260819",
  "${baseUrlCdn}/global/js/impactGraphic.js"
  ]
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/home/dashboard.css?20260819",
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
    Schedule.

    Split by what the server can know. Dates are server business: the label
    column, the countdown pills, the counts and every visible string are
    rendered here and never reach JavaScript except as {0} templates. Geometry
    is not: pixels-per-day depends on the measured width of the track and on the
    zoom stop, so bars, pills, ticks and the lane packing are drawn by
    schedule.js from the payload in data-schedule.

    Phase status uses only what Phase actually carries. `editable` is the source
    of truth for an open phase — an administrator can reopen one whose dates have
    passed — and dates only separate a closed phase from one that has not
    started. There is no completion figure anywhere in the model, so none is
    shown. Closed phases are not lanes here at all; they live in the "All phases"
    popover in the selector above.
  --]
  [#assign scDayMs = 86400000 /]
  [#assign scToday = .now?date /]

  [#function scPhaseStatus phase]
    [#if (phase.editable)!false][#return "inProgress"][/#if]
    [#if (phase.startDate)?? && phase.startDate?date gt scToday][#return "notStarted"][/#if]
    [#return "closed"]
  [/#function]

  [#-- The card plots activities only, so the sole thing still needed from
       `phases` is the soonest one yet to open: the fallback for the
       "what's next" panel. A phase needs both dates to be datable at all. --]
  [#assign scNotStarted = [] /]
  [#list (phases)![] as phase]
    [#if (phase.startDate)?? && (phase.endDate)?? && scPhaseStatus(phase) == "notStarted"]
      [#assign scNotStarted = scNotStarted + [phase] /]
    [/#if]
  [/#list]

  [#-- Timeline activities are global-unit wide: not per project, not per phase.
       DashboardAction sorts them; anything missing a date or a description
       cannot be drawn. --]
  [#assign scItems = [] /]
  [#list (scheduleActivities)![] as activity]
    [#if (activity.startDate)?? && (activity.endDate)?? && ((activity.description)!'')?trim?has_content]
      [#assign scItems = scItems + [activity] /]
    [/#if]
  [/#list]

  [#-- Month names come from the server so the axis stays in the bundle's
       language; schedule.js only assembles them. --]
  [#assign scMonths = [] /]
  [#list 1..12 as scMonthIndex]
    [#assign scMonths = scMonths + [("2001-" + scMonthIndex?string("00") + "-01")?date("yyyy-MM-dd")?string("MMM")] /]
  [/#list]

  [#-- The next activity is the soonest one that has not started yet; scItems is
       ordered by the admin's `order`, not by date, so this has to scan. --]
  [#assign scNextItem = [] /]
  [#list scItems as activity]
    [#if activity.startDate?date gt scToday]
      [#if !scNextItem?has_content || activity.startDate?date lt scNextItem[0].startDate?date]
        [#assign scNextItem = [activity] /]
      [/#if]
    [/#if]
  [/#list]

  [#-- Fallback when nothing is ahead in the timeline: the soonest phase still
       to open. Used to be the zero-open-phases state's only content. --]
  [#assign scNextPhase = [] /]
  [#list scNotStarted as phase]
    [#if !scNextPhase?has_content || phase.startDate?date lt scNextPhase[0].startDate?date]
      [#assign scNextPhase = [phase] /]
    [/#if]
  [/#list]

  [#assign scItemJson = [] /]
  [#list scItems as activity]
    [#assign scSame = activity.startDate?date?string("yyyy-MM-dd") == activity.endDate?date?string("yyyy-MM-dd") /]
    [#assign scItemJson = scItemJson + ['{"id":' + activity.id?c + ',"name":"' + ((activity.description)!'')?trim?json_string + '","start":"' + activity.startDate?date?string("yyyy-MM-dd") + '","end":"' + activity.endDate?date?string("yyyy-MM-dd") + '","dates":"' + scSame?then(activity.startDate?date?string("dd MMM yyyy"), activity.startDate?date?string("dd MMM") + ' \\u2013 ' + activity.endDate?date?string("dd MMM yyyy")) + '","order":' + ((activity.order)??)?then(((activity.order)!0)?c, 'null') + '}'] /]
  [/#list]

  <section class="scheduleCard" id="scheduleCard"
    [#-- One JSON payload. json_string covers the JSON layer; the attribute layer
         is FreeMarker's own auto-escaping, which Struts 6.8 switches on
         unconditionally together with the HTML output format -- which is also
         why ?html cannot be used here, it is a parse error under that policy.
         Both layers are load-bearing: activity descriptions are free text typed
         by users. The en dash is written as a JSON \u escape so the payload
         stays pure ASCII. --]
    data-schedule="${('{"today":"' + scToday?string("yyyy-MM-dd") + '","months":["' + scMonths?join('","') + '"]' + ',"activities":[' + scItemJson?join(",") + ']}')}"
    data-label-notstarted="[@s.text name="dashboard.schedule.legend.notStarted" /]"
    data-label-inprogress="[@s.text name="dashboard.schedule.legend.inProgress" /]"
    data-label-completed="[@s.text name="dashboard.schedule.legend.completed" /]"
    data-label-today="[@s.text name="dashboard.schedule.legend.today" /]"
    data-label-overflow="[@s.text name="dashboard.schedule.overflowHeading" /]"
    data-tpl-item="[@s.text name="dashboard.schedule.item.accessibleName"][@s.param]{0}[/@s.param][@s.param]{1}[/@s.param][@s.param]{2}[/@s.param][/@s.text]"
    data-tpl-more="[@s.text name="dashboard.schedule.more"][@s.param]{0}[/@s.param][/@s.text]">

    <div class="scheduleCard__head">
      <div class="scheduleCard__heading">
        <h2 class="scheduleCard__title">[@s.text name="dashboard.schedule.title" /]</h2>
        <span class="scheduleCard__subtitle">
          [@s.text name="dashboard.schedule.today"][@s.param]${scToday?string("dd MMMM yyyy")}[/@s.param][/@s.text] &middot;
          [#if scItems?size == 0][@s.text name="dashboard.schedule.noActivities" /]
          [#elseif scItems?size == 1][@s.text name="dashboard.schedule.oneActivity" /]
          [#else][@s.text name="dashboard.schedule.activityCount"][@s.param]${scItems?size?c}[/@s.param][/@s.text][/#if]
        </span>
      </div>
      [#-- Status is never colour alone: every swatch is paired with its name,
           exactly as every bar and pill carries a text label. --]
      <div class="scheduleCard__legend">
        <span class="scheduleCard__key scheduleCard__key--notStarted">[@s.text name="dashboard.schedule.legend.notStarted" /]</span>
        <span class="scheduleCard__key scheduleCard__key--inProgress">[@s.text name="dashboard.schedule.legend.inProgress" /]</span>
        <span class="scheduleCard__key scheduleCard__key--completed">[@s.text name="dashboard.schedule.legend.completed" /]</span>
        <span class="scheduleCard__key scheduleCard__key--today">[@s.text name="dashboard.schedule.legend.today" /]</span>
      </div>
    </div>

    <div class="scheduleCard__layout">
      <div class="scheduleCard__main" id="scheduleMain">
          <div class="scheduleCard__controls">
            <div class="scheduleCard__zoom" role="group" aria-label="[@s.text name="dashboard.schedule.zoom.label" /]">
              [#list [2, 4, 8, 16] as scWeeks]
                <button type="button" class="scheduleCard__zoomBtn" data-weeks="${scWeeks?c}"
                  aria-pressed="${(scWeeks == 8)?string}"
                  aria-label="[@s.text name="dashboard.schedule.zoom.accessibleName"][@s.param]${scWeeks?c}[/@s.param][/@s.text]">[@s.text name="dashboard.schedule.zoom.weeks"][@s.param]${scWeeks?c}[/@s.param][/@s.text]</button>
              [/#list]
            </div>
            <button type="button" class="scheduleCard__jump" id="scheduleJump">
              <span>[@s.text name="dashboard.schedule.jumpToToday" /]</span>
            </button>
          </div>

          <div class="scheduleCard__frame">
            <div class="scheduleCard__scroll" id="scheduleScroll" tabindex="0"
              aria-label="[@s.text name="dashboard.schedule.scroll.accessibleName" /]">
              <div class="scheduleCard__canvas" id="scheduleCanvas">
                <div class="scheduleCard__gridLayer" id="scheduleGrid" aria-hidden="true"></div>
                <div class="scheduleCard__nowLayer" id="scheduleNow" aria-hidden="true"></div>

                [#-- No label column: the timeline carries activities only, so the
                     lane names carried no meaning and the counts they showed are
                     already in the footer. The section header row went with them,
                     since labelling the section was its only job. --]
                <div class="scheduleCard__row scheduleCard__row--axis">
                  <div class="scheduleCard__cell scheduleCard__cell--track" id="scheduleAxis"></div>
                </div>

                [#-- Three reserved lanes and one overflow strip, whatever the
                     activity count. A lane carries no meaning of its own: an
                     activity can change lane as the window changes, which is the
                     price of a container that never grows. --]
                [#list 0..2 as scLane]
                  <div class="scheduleCard__row scheduleCard__row--lane">
                    <div class="scheduleCard__cell scheduleCard__cell--track" data-lane="${scLane?c}"></div>
                  </div>
                [/#list]

                <div class="scheduleCard__row scheduleCard__row--overflow">
                  <div class="scheduleCard__cell scheduleCard__cell--track" id="scheduleOverflowTrack"></div>
                </div>
              </div>
            </div>
          </div>

          [#if scItems?size == 0]
            <p class="scheduleCard__laneEmpty">[@s.text name="dashboard.schedule.activities.none" /]</p>
          [/#if]

          [#-- The footer reports the packing honestly rather than implying every
               activity found a lane. --]
          <div class="scheduleCard__foot">
            <span id="scheduleFootLeft"
              data-window="[@s.text name="dashboard.schedule.foot.window"][@s.param]{0}[/@s.param][/@s.text]"
              data-span="[@s.text name="dashboard.schedule.foot.span"][@s.param]{0}[/@s.param][@s.param]{1}[/@s.param][/@s.text]"></span>
            <span id="scheduleFootRight"
              data-placed="[@s.text name="dashboard.schedule.foot.placed"][@s.param]{0}[/@s.param][@s.param]{1}[/@s.param][@s.param]{2}[/@s.param][/@s.text]"
              data-all-placed="[@s.text name="dashboard.schedule.foot.allPlaced"][@s.param]{0}[/@s.param][@s.param]{1}[/@s.param][/@s.text]"
              data-hint="[@s.text name="dashboard.schedule.foot.hint" /]"></span>
          </div>
      </div>

      [#-- "What's next" prefers the next activity and falls back to the next
           phase, so the panel stays useful mid-cycle when every phase is
           already open and only activities are still ahead. --]
      [#if scNextItem?has_content]
        [#assign scNextStart = scNextItem[0].startDate?date /]
        [#assign scNextIn = ((scNextItem[0].startDate?long - scToday?long) / scDayMs)?round /]
        [#assign scNextSame = scNextItem[0].startDate?date?string("yyyy-MM-dd") == scNextItem[0].endDate?date?string("yyyy-MM-dd") /]
        <aside class="scheduleCard__next">
          <span class="scheduleCard__nextEyebrow">[@s.text name="dashboard.schedule.next.activityEyebrow" /]</span>
          <span class="scheduleCard__nextName">${((scNextItem[0].description)!'')?trim}</span>
          <span class="scheduleCard__nextDates">[@s.text name="dashboard.schedule.next.runs"][@s.param][#if scNextSame]${scNextStart?string("dd MMM yyyy")}[#else]${scNextStart?string("dd MMM")} &ndash; ${scNextItem[0].endDate?date?string("dd MMM yyyy")}[/#if][/@s.param][/@s.text]</span>
          <span class="scheduleCard__nextChip">
            [#if scNextIn lte 1][@s.text name="dashboard.schedule.next.startsTomorrow" /]
            [#else][@s.text name="dashboard.schedule.next.startsIn"][@s.param]${scNextIn?c}[/@s.param][/@s.text][/#if]
          </span>
        </aside>
      [#elseif scNextPhase?has_content]
        [#assign scNextIn = ((scNextPhase[0].startDate?long - scToday?long) / scDayMs)?round /]
        <aside class="scheduleCard__next">
          <span class="scheduleCard__nextEyebrow">[@s.text name="dashboard.schedule.next.phaseEyebrow" /]</span>
          <span class="scheduleCard__nextName">${(scNextPhase[0].composedName)!}</span>
          <span class="scheduleCard__nextDates">[@s.text name="dashboard.schedule.next.phaseDates"][@s.param]${scNextPhase[0].startDate?date?string("dd MMM yyyy")}[/@s.param][@s.param]${scNextPhase[0].endDate?date?string("dd MMM yyyy")}[/@s.param][/@s.text]</span>
          <span class="scheduleCard__nextChip">
            [#if scNextIn lte 1][@s.text name="dashboard.schedule.opensTomorrow" /]
            [#else][@s.text name="dashboard.schedule.opensIn"][@s.param]${scNextIn?c}[/@s.param][/@s.text][/#if]
          </span>
        </aside>
      [/#if]
    </div>
  </section>
[/#if]

  [#assign browseScope = (actualPhase.composedName)!'' /]
  [#assign phaseEditable = (actualPhase.editable)!false /]

  <section class="dashboardBrowse">
    <div class="dashboardBrowse__rail">
      <h3 class="dashboardBrowse__railTitle">[@s.text name="dashboard.browse.title" /]</h3>
      <div class="dashboardBrowse__cats">
        <button type="button" class="dashboardBrowse__cat is-active" id="projects" aria-pressed="true"
          data-pane="myProjects" data-scope="[@s.text name="dashboard.myProjects.title" /]">
          <img class="dashboardBrowse__catIcon" alt="" aria-hidden="true"
            src="${baseUrlCdn}/global/images/1309-load-balancer-outline.png">
          <img class="dashboardBrowse__catIcon dashboardBrowse__catIcon--anim" alt="" aria-hidden="true"
            src="${baseUrlCdn}/global/images/1309-load-balancer-outline.gif">
          <span class="dashboardBrowse__catLabel">[@s.text name="dashboard.myProjects.title" /]</span>
          <span class="dashboardBrowse__catCount">${(myProjects?size)!0}</span>
        </button>
        [#if action.isAiccra()]
          <button type="button" class="dashboardBrowse__cat" id="deliverables" aria-pressed="false"
            data-pane="myDeliverables" data-scope="[@s.text name="dashboard.myDeliverables.title" /]">
            <img class="dashboardBrowse__catIcon" alt="" aria-hidden="true"
              src="${baseUrlCdn}/global/images/verification.png">
            <img class="dashboardBrowse__catIcon dashboardBrowse__catIcon--anim" alt="" aria-hidden="true"
              src="${baseUrlCdn}/global/images/verification.gif">
            <span class="dashboardBrowse__catLabel">[@s.text name="dashboard.myDeliverables.title" /]</span>
            <span class="dashboardBrowse__catCount">${(myDeliverables?size)!0}</span>
          </button>
          <button type="button" class="dashboardBrowse__cat" id="studies" aria-pressed="false"
            data-pane="myStudies" data-scope="[@s.text name="dashboard.studies.table.title" /]">
            <img class="dashboardBrowse__catIcon" alt="" aria-hidden="true"
              src="${baseUrlCdn}/global/images/oicrs_icon.png">
            <img class="dashboardBrowse__catIcon dashboardBrowse__catIcon--anim" alt="" aria-hidden="true"
              src="${baseUrlCdn}/global/images/oicrs_icon.gif">
            <span class="dashboardBrowse__catLabel">[@s.text name="dashboard.studies.table.title" /]</span>
            <span class="dashboardBrowse__catCount">${(myStudies?size)!0}</span>
          </button>
          [#if action.hasSpecificities('innovation_section_active') ]
            <button type="button" class="dashboardBrowse__cat" id="innovations" aria-pressed="false"
              data-pane="myInnovations" data-scope="[@s.text name="dashboard.innovations.table.title" /]">
              <img class="dashboardBrowse__catIcon" alt="" aria-hidden="true"
                src="${baseUrlCdn}/global/images/innovationDashboard.png">
              <img class="dashboardBrowse__catIcon dashboardBrowse__catIcon--anim" alt="" aria-hidden="true"
                src="${baseUrlCdn}/global/images/innovationDashboard.gif">
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