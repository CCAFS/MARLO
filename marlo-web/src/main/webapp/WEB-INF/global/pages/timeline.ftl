[#ftl]  
[#if phases??]
<div class="container">
  [#-- Phases Timeline --]
  [@components.css_imports libraryName="TimelineJS3" /]
  <link rel="stylesheet" type="text/css" href="${baseUrl}/global/css/timeline-phases.css" />
  
  <script type="text/javascript">
    var dataObject = {
      "events": [
      [#if phases?size > 1]
        [#list phases as phase]
          [#-- [#if (actualPhase.id == phase.id)](Current)[/#if] --]
             [#if (actualPhase.id == phase.id)][#assign currenPhaseIndex = phase_index  /][/#if]
      
          {
              "text": {
                "headline": "${(phase.description)!}  ${(phase.year)!} ",
              },
              "start_date": {
                  "year": "${phase.startDate.year}",
                  "month": "${phase.startDate.month}",
                  "day":  "${phase.startDate.day}"
              },
              "end_date": {
                   "year": "${phase.endDate.year}",
                  "month": "${phase.endDate.month}",
                  "day":  "${phase.endDate.day}"
              },
              "background": {
                "color": "#${(phase.isReporting())?string('777','337ab7')}"
              },
              "group": "${(phase.isReporting())?string('Reporting','Planning')}",
              "unique_id": "phase-${(phase.id)!}"
          },
        [/#list]
      [/#if]
      ]
    }
    var currenPhaseIndex = ${(currenPhaseIndex)!0};

  </script>
 
  <div style="width: 100%;height: 80px;">
    <div class="loading timeline-loader" style="display:none"></div>
    <div id="timeline-phases" ></div>
  </div>
  [@components.js_imports libraryName="TimelineJS3" /]
  [@components.js_imports libraryName="jsUri" /]
  [#assign customJS = [ "${baseUrl}/global/js/timeline-phases.js"  ] + customJS  /]

[/#if]
</div>