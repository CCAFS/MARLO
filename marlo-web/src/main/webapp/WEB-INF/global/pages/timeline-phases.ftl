[#ftl]  
[#if phases??]
  [#if phases?size > 1]
    <div class="container hidden-print"> 
      <link rel="stylesheet" type="text/css" href="${baseUrlCdn}/global/css/timeline-phases.css?20210225" />
        
        [#-- Timeline Scroll --]
        <div id="timelineScroll" class="example pagespan">
          <div class="loading timeline-loader" style="display:none"></div>
          <button class="backward"><span class="glyphicon glyphicon-chevron-left"></span></button>
          <button class="forward"><span class="glyphicon glyphicon-chevron-right"></span></button>
          <div class="frame">
            [#assign activePhasesCount = 0]
            <ul>
              [#list phases as phase]
                [#if phase.editable]
                  [#assign activePhasesCount = activePhasesCount + 1]
                [/#if]
                <li id="phase-${(phase.id)!}" class="phaseBox text-left ${(phase.isReporting())?string('reporting','planning')} [#if (actualPhase.id == phase.id)][#assign currenPhaseIndex = phase_index  /]active phaseSelected[#else]phaseNoSelected[/#if] ${phase.editable?string('', '')}">
                  <h4> [#if centerGlobalUnit] ${(phase.year)!} [#else] ${(phase.composedName)!} [/#if] <small class="pull-right">${phase.editable?string('', '<span class="label label-danger">Closed</span>')}</small></h4>
                  [#-- <small><strong>From: </strong>${phase.startDate} | <strong>Until: </strong>${phase.endDate}</small> --]
                </li>
              [/#list] 
            </ul>
          </div>
          [#if activePhasesCount > 1]
            <div class="scrollbar">
              <div class="handle">
                <div class="mousearea"></div>
              </div>
            </div>
          [/#if]
        </div>
        
        [#-- Phase floating tag --]
        <div id="phaseTag" class="phaseTag">
          <span class="${(actualPhase.isReporting())?string('reporting','planning')}" style="display:none;"> [#if centerGlobalUnit] ${(actualPhase.year)!} [#else] ${(actualPhase.composedName)!} [/#if] </span>
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
        [#assign customJS = [ "${baseUrlCdn}/global/js/timeline-phases.js"  ] + customJS  /]
      [#else]
        [#assign customJS = [ "${baseUrlCdn}/global/js/timeline-phases.js"  ] /]
      [/#if]
    
    </div>

  [/#if]
[/#if]