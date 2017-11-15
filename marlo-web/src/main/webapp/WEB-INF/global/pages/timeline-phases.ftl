[#ftl]  
[#if phases??]


<div class="container"> 
  <link rel="stylesheet" type="text/css" href="${baseUrl}/global/css/timeline-phases.css" />
    
    [#-- Timeline Scroll --]
    <div id="example" class="example pagespan">
      <div class="loading timeline-loader" style="display:none"></div>
      <button class="backward"> <span class="glyphicon glyphicon-chevron-left"></span> </button>
      <button class="forward"> <span class="glyphicon glyphicon-chevron-right"></span> </button>
      <div class="frame">
        <ul>
          [#if phases?size > 1]
            [#list phases as phase]
              
              <li id="phase-${(phase.id)!}" class="text-left ${(phase.isReporting())?string('reporting','planning')} [#if (actualPhase.id == phase.id)][#assign currenPhaseIndex = phase_index  /]active[/#if]">
                <h4> ${(phase.description)!} ${(phase.year)!}</h4>
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
  </div>
  
  <script>
    var currenPhaseIndex = ${currenPhaseIndex};
  </script>
  
  [#assign pageLibs = pageLibs + ["sly", "jsUri"] /]
  [#assign customJS = [ "${baseUrl}/global/js/timeline-phases.js"  ] + customJS  /]

[/#if]
</div>