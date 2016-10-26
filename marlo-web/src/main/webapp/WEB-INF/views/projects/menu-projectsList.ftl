[#ftl]
[#-- Projects buttons Menu --]
<ul id="" class="horizontalSubMenu text-left">
  <li class="[#if currentStage == "all"]active[/#if]">
    <a href="[@s.url namespace='/projects' action='${(crpSession)!}/projectsList'][@s.param name="filterBy"]all[/@s.param][/@s.url]">All Projects</a>
  </li>
  <li class="[#if currentStage == "w1"]active[/#if]">
    <a href="[@s.url namespace='/projects' action='${(crpSession)!}/projectsList'][@s.param name="filterBy"]w1[/@s.param][/@s.url]">W1/W2 Projects</a>
  </li>
  <li class="[#if currentStage == "w3"]active[/#if]">
    <a href="[@s.url namespace='/projects' action='${(crpSession)!}/projectsList'][@s.param name="filterBy"]w3[/@s.param][/@s.url]">W3/Bilateral Standalone Projects</a>
  </li>
  [#-- 
  <li class="[#if currentStage == "cofunded"]active[/#if]">
    <a href="[@s.url namespace='/bilaterals' action='${(crpSession)!}/cofundedList'][/@s.url]">Supplement Projects</a>
  </li>
    --]
</ul>