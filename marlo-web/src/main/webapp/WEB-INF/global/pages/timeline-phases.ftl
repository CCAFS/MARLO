[#ftl]  
[#if phases??]
<div class="container"> 
  <link rel="stylesheet" type="text/css" href="${baseUrl}/global/css/timeline-phases.css" />
    
    [#-- Timeline Scroll --]
    <div id="timelineScroll" class="example pagespan">
      <div class="loading timeline-loader" style="display:none"></div>
      <button class="backward"><span class="glyphicon glyphicon-chevron-left"></span></button>
      <button class="forward"><span class="glyphicon glyphicon-chevron-right"></span></button>
      <div class="frame">
        <ul>
          [#if phases?size > 1]
            [#list phases as phase]
              <li id="phase-${(phase.id)!}" class="phaseBox text-left ${(phase.isReporting())?string('reporting','planning')} [#if (actualPhase.id == phase.id)][#assign currenPhaseIndex = phase_index  /]active phaseSelected[#else]phaseNoSelected[/#if] ${phase.editable?string('', '')}">
                <h4> ${(phase.description)!} ${(phase.year)!} <small class="pull-right">${phase.editable?string('', '<span class="label label-danger">Closed</span>')}</small></h4>
                <small><strong>From: </strong>${phase.startDate} | <strong>Until: </strong>${phase.endDate}</small>
              </li>
            [/#list] 
          [/#if]
        </ul>
      </div>
      <div class="scrollbar">
        <div class="handle">
          <div class="mousearea"></div>
        </div>
      </div>
    </div>
    
    [#-- Phase floating tag --]
    <div id="phaseTag" class="phaseTag">
      <span class="${(actualPhase.isReporting())?string('reporting','planning')}" style="display:none;">${(actualPhase.description)!} ${(actualPhase.year)!}</span>
    </div>
  </div>
  <script>
    var currenPhaseIndex = ${(currenPhaseIndex)!0};
  </script>
  
  [#if pageLibs??]
    [#assign pageLibs = pageLibs + ["sly", "jsUri"] /]
  [#else]
    [#assign pageLibs = ["sly", "jsUri"] /]
  [/#if]
  [#if customJS??]
    [#assign customJS = [ "${baseUrl}/global/js/timeline-phases.js"  ] + customJS  /]
  [#else]
    [#assign customJS = [ "${baseUrl}/global/js/timeline-phases.js"  ] /]
  [/#if]

</div>


[/#if]