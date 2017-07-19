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


[#macro helpBox name="" ]
  <div class="container helpText viewMore-block">
    <div class="helpMessage infoText">
      <img class="col-md-2" src="${baseUrlMedia}/images/global/icon-help.jpg" />
      <p class="col-md-10"> [@s.text name=name /] </p>
    </div> 
    <div style="display:none" class="viewMore closed"></div>
  </div>
[/#macro]

