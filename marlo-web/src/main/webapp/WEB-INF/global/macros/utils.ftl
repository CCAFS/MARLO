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
  <div class="container helpText viewMore-block">
    <div class="helpMessage infoText">
      <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
      [#if param?has_content]
        <p class="col-md-10">[@s.text name=name][@s.param]${param}[/@s.param][/@s.text] </p>
      [#else]
        [@s.text name=name /]
      [/#if]
    </div> 
    <div style="display:none" class="viewMore closed"></div>
  </div>
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
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.png" />
    <p class="col-md-10"> [@s.text name="${hlpInfo}" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>
[/#macro]

