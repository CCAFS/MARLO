[#ftl]

[#-- WordCutter macro takes a string and return the substring from position 0
     to the last ocurrence of substr before the maxPos position
     if substr is not in the string return the substring(0, maxPos)
--]

[#macro wordCutter string maxPos substr=" "]
  [#if string?length < maxPos]    
    ${string}     
  [#else]
    [#if string?last_index_of(substr, maxPos) == -1]
      ${string?substring(0, maxPos)}
    [#else]
      ${string?substring(0, string?last_index_of(substr, maxPos) )}...
    [/#if]
  [/#if]
[/#macro]

[#macro helpBox name="" param=""]
  
  [#if param?has_content]
    [#assign text][@s.text name=name][@s.param]${param}[/@s.param][/@s.text][/#assign]
  [#else]
    [#assign text][@s.text name=name /][/#assign]
  [/#if]
  
  [#if text?has_content]
  <!--
  <div class="container helpText viewMore-block">
    <div class="helpMessage infoText">
      <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-help.jpg" />
      [#if text?starts_with("<ul>") || text?starts_with("<ol>")]
        <div class="col-md-10"> ${text} </div>
      [#else]
        <p class="col-md-10"> ${text} </p>
      [/#if]
    </div> 
    <div style="display:none" class="viewMore closed"></div>
  </div>
  -->
  <div class="animated flipInX container  viewMore-block viewMore-block">
    <div class=" containerAlert alert-leftovers alertColorBackgroundInfo ">
      <div class="containerLine alertColorInfo"></div>
      <div class="containerIcon">
        <div class="containerIcon alertColorInfo">
          <img src="${baseUrlCdn}/global/images/icon-question.png" />       
        </div>
      </div>
      <div class="containerText col-md-12">
        [#if text?starts_with("<ul>") || text?starts_with("<ol>")]
          <div class="alertText"> ${text} </div>
        [#else]
          <p class="alertText"> ${text} </p>
        [/#if]
      </div>
    </div>
  </div>
  [/#if]
[/#macro]

[#-- letterCutter, is the same of WordCutter but cut by letters --]
[#macro letterCutter string maxPos substr=" "]
  [#if string?length < maxPos]    
    ${string}     
  [#else]
    ${string?substring(0, maxPos)}...
  [/#if]
[/#macro]

[#macro helpInfos hlpInfo]
  [#-- Help Text to be used in Research Impacts, Research Topics, Research Outcomes and Research Outputs. 
  Specify the appropriate value for hlpInfo parameter in the corresponding ftl file. --] 
<div class="container helpText viewMore-block">
  <div style="display:none;" class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-help.png" />
    <p class="col-md-10"> [@s.text name="${hlpInfo}" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>
[/#macro]


[#macro underConstruction title="" show=true width="" height="" ]
  <span class="under-construction-icon" style="display:${show?string('inline','none')};" title="[@s.text name="${title}" /]">
    <img src="${baseUrlCdn}/global/images/under-construction.png" width="${width!'10px'}" height="${height!'10px'}" />
  </span>
[/#macro]

[#macro tag label="" tooltip=""]
  <span class="doc-badge pull-right" title="[@s.text name="${tooltip}" /]">
    <img src="${baseUrlCdn}/global/images/icons/file-doc.png" style="width:18px;" alt=""> [@s.text name="${label}" /] 
  </span>
[/#macro]

[#macro tagPMU label="" tooltip=""]
  <span class="doc-badge pull-right" title="[@s.text name="${tooltip}" /]">
    <img src="${baseUrlCdn}/global/images/icons/file-pmu.png" style="width:18px;" alt=""> [@s.text name="${label}" /] 
  </span>
[/#macro]

[#macro prefilledTag]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#macro]

[#macro tableText value nobr=false emptyText="global.notDefined"]
  [#if (value?trim?has_content)!false] <span style="${nobr?string('white-space: nowrap;','word-wrap: break-word;')}"> ${value?trim} </span> [#else]<i style="font-weight: normal;opacity:0.8;"><nobr>[@s.text name="${emptyText}"/]</nobr></i>[/#if]
[/#macro]

[#macro tableList list displayFieldName="title" showEmpty=true nobr=false emptyText="global.notDefined" class="" scroll=false]
  [#local levels = displayFieldName?split(".")]
  [#local valuesArray = [] /]
  [#if (list?has_content)!false]
    
    [#list list as item]
      [#local itemValue = (item)!'null' /]
      [#list levels as level][#local itemValue = (itemValue[level])!'null' /][/#list]
      [#local valuesArray = valuesArray + [itemValue] /]
    [/#list]
    
    [#if valuesArray?size > 1 ]
      <ul style="padding: 0" class="${class}" [#if scroll]data-mcs-theme="dark"[/#if]>
        [#list valuesArray as item]<li style="list-style-position: inside;${nobr?string('white-space: nowrap;','')}">${item}</li>[/#list]
      </ul>
    [#else]
      ${valuesArray[0]}
    [/#if]
  [#else]
    [#if showEmpty]
      <i style="font-weight: normal;opacity:0.8;"><nobr>[@s.text name="${emptyText}"/]</nobr></i>
    [/#if]
  [/#if]
[/#macro]

[#macro tableCheckIcon state stateMessage="Complete"]
  [#if state ]
    <span class="icon-20 icon-check" title="${stateMessage}"></span> 
  [#else]
    <span class="icon-20 icon-uncheck" title=""></span> 
  [/#if]
[/#macro]



