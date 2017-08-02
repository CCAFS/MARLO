[#ftl]
[#-- Output --]
<div class="sectionSubMenu">
  [#-- Nav tabs --]
  <ul class="nav nav-tabs" role="tablist">
      <li role="areas" class="[#if currentSubStage == "mainInformation"]active[/#if]"> 
        <a href="[@s.url action="${centerSession}/outputs"][@s.param name="outputID" value=outputID /][@s.param name="edit" value=true /][/@s.url]" aria-controls="home" role="tab" [#-- data-toggle="tab" --]>Main Information</a>
      </li>
      <li role="areas" class="[#if currentSubStage == "partners"]active[/#if]"> 
        <a href="[@s.url action="${centerSession}/outputsPartners"][@s.param name="outputID" value=outputID /][@s.param name="edit" value=true /][/@s.url]" aria-controls="home" role="tab" [#-- data-toggle="tab" --]>Partners</a>
      </li>
  </ul>
  
</div>