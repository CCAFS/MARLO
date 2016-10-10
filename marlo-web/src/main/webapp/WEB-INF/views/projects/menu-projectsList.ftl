[#ftl]
[#-- Projects buttons Menu --]
<ul id="" class="horizontalSubMenu text-left">
  <li class="[#if currentStage = "all"]active[/#if]"><a href="[@s.url namespace='/projects' action='${(crpSession)!}/projectsList'][@s.param name="filterBy" value="" /][/@s.url]">All Projects</a></li>
  <li class="[#if currentStage = "w1w2"]active[/#if]"><a href="[@s.url namespace='/projects' action='${(crpSession)!}/projectsList'][@s.param name="filterBy" value="w1" /][/@s.url]">W1/W2 Projects</a></li>
  <li class="[#if currentStage = "w3bilateral"]active[/#if]"><a href="[@s.url namespace='/projects' action='${(crpSession)!}/projectsList'][@s.param name="filterBy" value="w3" /][/@s.url]">W3/Bilateral Projects</a></li>
  <li class="[#if currentStage = "cofunded"]active[/#if]"><a href="[@s.url namespace='/bilaterals' action='${(crpSession)!}/cofundedList'][@s.param name="edit" value="true" /][/@s.url]">Co-Funded Projects</a></li>
</ul>