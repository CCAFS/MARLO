[#ftl]  
<div class="container">
  [#-- Phases Timeline --]
  [@components.css_imports libraryName="TimelineJS3" /]
  <link rel="stylesheet" type="text/css" href="${baseUrlMedia}/css/global/timeline-phases.css" />
  
  <script type="text/javascript">
    var dataObject = {
      "events": [
      [#if phases?size > 1]
        [#list phases as phase]
          [#-- [#if (actualPhase.id == phase.id)](Current)[/#if] --]
        
          [#-- Timeline Event --]
          [#if phase.isReporting()]
            [#assign yearEvent = phase.year +1 /]
            [#if (actualPhase.id == phase.id)][#assign currenPhaseIndex = phase_index +1 /][/#if]
          [#else]
            [#assign yearEvent = phase.year -1 /]
            [#if (actualPhase.id == phase.id)][#assign currenPhaseIndex = phase_index - 1 /][/#if]
          [/#if]
          {
              "text": {
                "headline": "${(phase.description)!}  ${(phase.year)!} ",
              },
              "start_date": {
                  "year": "${yearEvent}",
                  "month": "${(phase.isReporting())?string('02','10')}",
                  "day": "01"
              },
              "end_date": {
                  "year": "${yearEvent}",
                  "month": "${(phase.isReporting())?string('04','12')}",
                  "day": "01"
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
    
    console.log(dataObject);
    console.log(currenPhaseIndex);
  </script>
  
  <div style="width: 100%;height: 80px;">
    <div class="loading timeline-loader" style="display:none"></div>
    <div id="timeline-phases" ></div>
  </div>
  [@components.js_imports libraryName="TimelineJS3" /]
  [@components.js_imports libraryName="jsUri" /]
  [#assign customJS = [ "${baseUrlMedia}/js/global/timeline-phases.js"  ] + customJS  /]

 
</div>