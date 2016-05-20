[#ftl]
[#macro text name readText=false param="" ][#assign customName][#if readText]${name}.readText[#else]${name}[/#if][/#assign][@s.text name="${customName}"][@s.param]${param}[/@s.param][/@s.text][/#macro]

[#macro input name value="-NULL" type="text" i18nkey="" disabled=false required=false errorField="" help="" display=true className="" paramText="" readOnly=false showTitle=true editable=true placeholder=false]
  <div class="input ${(className?has_content)?string('input-','')}${className}" style="display:${display?string('block','none')};">
    [#assign labelTitle][#if i18nkey==""][@s.text name="${name}"][@s.param]${paramText}[/@s.param][/@s.text][#else][@s.text name="${i18nkey}"][@s.param]${paramText}[/@s.param][/@s.text][/#if][/#assign]
    [#if showTitle]
      <h6>
        <label for="${name}" class="${editable?string('editable', 'readOnly')}">${labelTitle}:[#if required && editable]<span class="red">*</span>[/#if]</label>
        [#if help != ""]<img src="${baseUrl}/images/global/icon-help2.png" title="[@s.text name="${help}"/]" />[/#if]
      </h6>
    [/#if]
    [#if errorField==""][@s.fielderror cssClass="fieldError" fieldName="${name}"/][#else][@s.fielderror cssClass="fieldError" fieldName="${errorfield}"/][/#if]
    [#if editable]
      <input type="${type}" id="${name}" name="${name}" value="[#if value=="-NULL"][@s.property value="${name?string}"/][#else]${value}[/#if]"  class="${className} ${required?string('required','optional')}" [#if readOnly] readonly="readonly"[/#if] [#if disabled]disabled="disabled"[/#if] [#if !showTitle && placeholder]placeholder="${labelTitle}"[/#if]/>
    [#else]
      <input type="hidden" id="${name}" name="${name}" value="[#if value=="-NULL"][@s.property value="${name?string}"/][#else]${value}[/#if]" class="${className} ${required?string('required','optional')}"/>
      [#assign requiredText][#if required && editable ]<span class="fieldError">[@s.text name="form.values.required" /]</span>[/#if][/#assign] 
      <p>
        [#if value=="-NULL"] 
          [#assign customValue][@s.property value="${name?string}"/][/#assign] 
          [#if !(customValue)?has_content]${requiredText}[@s.text name="form.values.fieldEmpty" /][#else]${customValue}[/#if]
        [#else]
          [#if !value?has_content]${requiredText}[@s.text name="form.values.fieldEmpty" /][#else]${value}[/#if] 
        [/#if]
      </p>
    [/#if]
  </div>
[/#macro]

[#macro textArea name editable value="-NULL" i18nkey="" disabled=false required=false errorfield="" help="" addButton=false showTitle=true display=true className="-NULL" paramText="" editable=true ]
  <div class="textArea [#if addButton] button[/#if]" [#if !display]style="display: none;"[/#if]> 
    [#assign customName]${(i18nkey?has_content)?string(i18nkey,name)}[/#assign]  
    [#assign customLabel][#if !editable]${customName}.readText[#else]${customName}[/#if][/#assign]
    [#assign customValue][#if value=="-NULL"][@s.property value="${name}"/][#else]${value}[/#if][/#assign]
  	[#if showTitle]
	    <h6> 
	      <label for="${name}" class="${editable?string('editable', 'readOnly')}"> [@s.text name="${customLabel}"][@s.param]${paramText}[/@s.param][/@s.text]:[#if required && editable]<span class="red">*</span>[/#if]</label>
	      [#if help != ""]<img src="${baseUrl}/images/global/icon-help2.png" title="[@s.text name="${help}"/]" />[/#if]
	    </h6>
    [/#if]
    [#if errorfield==""][@s.fielderror cssClass="fieldError" fieldName="${name}"/][#else][@s.fielderror cssClass="fieldError" fieldName="${errorfield}"/][/#if]
    [#if editable]
      <textarea rows="4" name="${name}" id="${name}" [#if disabled]disabled="disabled"[/#if]  class="[#if className != "-NULL"]ckeditor ${className}[/#if] ${required?string('required','optional')}" />${customValue}</textarea>
    [#else]
      <input type="hidden" name="${name}" id="${name}" value="${customValue}" class="[#if className != "-NULL"]ckeditor ${className}[/#if] ${required?string('required','optional')}" />
      [#assign requiredText][#if required && editable]<span class="fieldError">[@s.text name="form.values.required" /]</span>[/#if][/#assign] 
      <p>
        [#if value=="-NULL"] 
          [#assign customValue][@s.property value="${name?string}"/][/#assign] 
          [#if !(customValue)?has_content]${requiredText}[@s.text name="form.values.fieldEmpty" /][#else]${customValue?replace('\n', '<br>')}[/#if]
        [#else]
          [#if !value?has_content]${requiredText}[@s.text name="form.values.fieldEmpty" /][#else]${value?replace('\n', '<br>')}[/#if] 
        [/#if]
      </p>
    [/#if] 
  </div>
  [#if addButton]
     <input type="button" class="addButton [@s.text name='${i18nkey}' /]" name="" value="Add [@s.text name='${i18nkey}' /]" />
  [/#if]
[/#macro]

[#macro button i18nkey class="" id="" editable=true]
  <input type="button" class="${class}" id="${id}" value="[@s.text name='${i18nkey}' /]" />
[/#macro]

[#macro checkbox name value="-NULL" label="" i18nkey="" disabled=false className="" checked=false required=false display=true help="" editable=true]
  <div class="checkbox" [#if !display]style="display: none;"[/#if]>
    [#if editable]
      <label for="${name}" class="${editable?string('editable', 'readOnly')}">
        <input type="checkbox" id="${name}" class="${className}" name="${name}" value="${value}" [#if checked]checked="checked"[/#if] [#if disabled]disabled="disabled[/#if] />
        <input type="hidden" id="__checkbox_${name}" name="__checkbox_${name}" value="${value}" />
        <h6>[#if i18nkey==""]${label}[#else][@s.text name="${i18nkey}" /][/#if][#if required && editable]<span class="red">*</span>[/#if]</h6>
        [#if help != ""]<img src="${baseUrl}/images/global/icon-help2.png" title="[@s.text name="${help}"/]" />[/#if]
      </label>
    [#else]
      [#if checked]<p class="checked">[#if i18nkey==""]${label}[#else][@s.text name="${i18nkey}.readText" /][/#if]</p>[/#if]
    [/#if]
  </div>
[/#macro]

[#macro checkboxGroup label name listName displayFieldName="" keyFieldName="" value="-NULL" i18nkey="" disabled=false required=false errorField="" checked=false display=true editable=true]
  <div class="checkbox" [#if !display]style="display: none;"[/#if]>
    <h6>[#if i18nkey==""]${label}[#else][@s.text name="${i18nkey}" /][/#if]:[#if required && editable]<span class="red">*</span>[/#if]</h6>
    [#if errorField==""][@s.fielderror cssClass="fieldError" fieldName="${name}"/][#else][@s.fielderror cssClass="fieldError" fieldName="${errorfield}"/][/#if]
    <div class="checkboxList">
    [#if value=="-NULL"]
      [#assign customValue][@s.property value="${name}" /][/#assign]
    [#else]
      [#assign customValue]${value}[/#assign]
    [/#if]
    [#if editable]
      [#if keyFieldName == ""]
        [@s.checkboxlist name="${name}" list="${listName}" value="${customValue}" disabled="${disabled?string}" /]
      [#else]
        [@s.checkboxlist name="${name}" list="${listName}" listKey="${keyFieldName}" listValue="${displayFieldName}" value="${customValue}" disabled="${disabled?string}" /]
      [/#if]
    [#elseif keyFieldName == ""]  
      [#assign requiredText][#if required && editable]<span class="fieldError">[@s.text name="form.values.required" /]</span>[/#if][/#assign] 
      ${requiredText}  [@s.text name="form.values.fieldEmpty" /]
    [#else]
      ${customValue}
    [/#if] 
    </div>    
  </div> 
[/#macro]

[#macro radioButtonGroup label name listName class="" displayFieldName="" keyFieldName="" value="-NULL" i18nkey="" disabled=false required=false errorField="" checked=false help="" display=true editable=true]
  <div class="radioGroup" [#if !display]style="display: none;"[/#if]>
    <h6>[#if i18nkey==""]${label}[#else][@s.text name="${i18nkey}" /][/#if]:[#if required && editable]<span class="red">*</span>[/#if]</h6>
    [#if errorField==""][@s.fielderror cssClass="fieldError" fieldName="${name}"/][#else][@s.fielderror cssClass="fieldError" fieldName="${errorfield}"/][/#if]
    <div class="radiosList">
      [#if editable]
        [#if value=="-NULL"][#assign customValue][@s.property value="${name}" /][/#assign][#else][#assign customValue]${value}[/#assign][/#if]
        [#if class==""][#assign className="${name}"][#else][#assign className="${class}"][/#if]
        [#if help!=""][#assign helpTitle][@s.text name="${help}" /][/#assign][#else][#assign helpTitle=""][/#if]
        [#if keyFieldName == ""]
          [@s.radio name="${name}" cssClass="${className}" list="${listName}" value="${customValue}" disabled="${disabled?string}" title="${helpTitle}" /]
        [#else]
          [@s.radio name="${name}" cssClass="${className}" list="${listName}" listKey="${keyFieldName}" listValue="${displayFieldName}" value="${customValue}" disabled="${disabled?string}" title="${helpTitle}" /]
        [/#if]
      [#else]
        [#assign customValue][@s.property value="${name}" /][/#assign]
        [#if customValue?trim?has_content]${customValue}[#else]<div class="select"><p>There is not an option selected yet</p></div>[/#if]
      [/#if]
    </div>
  </div>
[/#macro]

[#macro select name listName label="" keyFieldName="" displayFieldName="" value="-NULL" i18nkey="" disabled=false required=false errorField="" selected=false className="" multiple=false help="" headerKey="" headerValue="" display=true showTitle=true addButton=false stringKey=false editable=true]
  <div class="select[#if addButton] button[/#if]" [#if !display]style="display: none;"[/#if]>
    [#assign placeholderText][@s.text name="form.select.placeholder" /][/#assign]
    [#if showTitle]
      <h6>
        [#if i18nkey==""]${label} [#else][@s.text name="${i18nkey}" /]:[/#if][#if required && editable]<span class="red">*</span>[/#if]
        [#if help != ""]<img src="${baseUrl}/images/global/icon-help2.png" title="[@s.text name="${help}"/]" />[/#if]
      </h6>
    [/#if]
    [#if errorField==""][@s.fielderror cssClass="fieldError" fieldName="${name}"/][#else][@s.fielderror cssClass="fieldError" fieldName="${errorfield}"/][/#if]
    <div class="selectList">   
      [#if value=="-NULL"]
        [#assign customValue][@s.property value="${name}" /][/#assign]
      [#else]
        [#assign customValue]${value}[/#assign]
      [/#if]
      [#-- Help text --]
      [#if help!=""][#assign helpText][@s.text name="${help}" /][/#assign][#else][#assign helpText][/#assign][/#if]
      [#if editable]
        [#if keyFieldName == ""]
          [#if multiple]
            [@s.select name="${name}" list="${listName}" value="${customValue}" disabled="${disabled?string}" cssClass="${className}" multiple="true" tooltip="${helpText}" headerKey="-1" headerValue=placeholderText  /]
          [#else]
            [@s.select name="${name}" list="${listName}" value="${customValue}" disabled="${disabled?string}" cssClass="${className}" tooltip="${helpText}" headerKey="-1" headerValue="${placeholderText}"  /]
          [/#if]
        [#else]
          [#if multiple]
            [@s.select name="${name}" list="${listName}" listKey="${keyFieldName}" listValue="${displayFieldName}" value="${customValue}" disabled="${disabled?string}" cssClass="${className}" multiple="true" tooltip="${helpText}" headerKey="-1" headerValue=placeholderText /]
          [#else]
            [@s.select name="${name}" list="${listName}" listKey="${keyFieldName}" listValue="${displayFieldName}" value="${customValue}" disabled="${disabled?string}" cssClass="${className}" tooltip="${helpText}" headerKey="-1" headerValue=placeholderText /]
          [/#if]
        [/#if] 
      [#else]
        <input type="hidden" name="${name}" value="${customValue}" class="${className}"/>
        [#assign requiredText][#if required && editable]<span class="fieldError">[@s.text name="form.values.required" /]</span>[/#if][/#assign]  
        <p>  
          [#if displayFieldName == "" ]
            [#assign key][@s.property value="${name}"/][/#assign]
            [#assign customValue][#if !stringKey][@s.property value="${listName}[${key}]"/][#else][@s.property value="${listName}['${key}']"/][/#if][/#assign]
            [#if (key == "-1") || (customValue == "-1")]${requiredText}   [@s.text name="form.values.fieldEmpty" /][/#if] 
            [#if customValue?has_content]
              ${customValue}
            [#else]
              [#if !(key?has_content)]
                ${requiredText}   [@s.text name="form.values.fieldEmpty" /]
              [/#if]
            [/#if] 
          [#else]
            [#if name == ""][#--@s.property value="${listName}[${value}].${displayFieldName}"/--][/#if]
            [#assign customValue][@s.property value="${name}.${displayFieldName}"/][/#assign]  
            [#if value=="-NULL"] 
              [#if !(customValue)?has_content] 
                 ${requiredText}   [@s.text name="form.values.fieldEmpty" /]
              [#else]
                ${customValue}
              [/#if]
            [#else]
              [#if customValue?has_content]
                ${customValue}
              [#elseif value=="-1"]
                 ${requiredText}   [@s.text name="form.values.fieldEmpty" /]
              [/#if] 
            [/#if]
            </p>
          [/#if]
      [/#if]  
    </div> 
  </div>  
  [#if addButton]
     <input type="button" class="addButton [@s.text name='${i18nkey}' /]" name="" value="Add [@s.text name='${i18nkey}' /]" />
  [/#if]
[/#macro]

[#macro inputFile name template=false className="" ]
  [#assign customId][#if template]${name}-template[#else]${name}[/#if][/#assign]
  <!-- Input File ${customId} -->
  [@s.fielderror cssClass="fieldError" fieldName="${name}"/]
  [@s.file name="${name}" id="${customId}" cssClass="${className} upload" cssStyle="${template?string('display:none','')}"  /]
[/#macro] 

[#macro req required=true ][#if required]<span class="red">*</span>[/#if][/#macro]

[#macro confirmJustification action="" namespace="/" nameId="" title="" projectID=""]
  <div id="dialog-justification" title="${title}" style="display:none"> 
    <div class="dialog-content"> 
      [@s.form action="${action}" namespace="${namespace}" cssClass="pure-form"]
        [@textArea name="justification" i18nkey="saving.justification" required=true className="justification"/]
        [#if nameId != ""]
          <input name="${nameId}" type="hidden" value="-1" />
        [/#if]
        <input name="projectID" type="hidden" value="${projectID}" />
        <!-- Allow form submission with keyboard without duplicating the dialog button -->
        <input type="submit" tabindex="-1" style="position:absolute; top:-1000px">
      [/@s.form]
    </div>  
  </div>
[/#macro]

[#macro rank name disabled=false editable=true]
  [#assign score][@s.property value="${name}"/][/#assign]
  <div class="rankingBlock" style="text-align:center;">
    [#if editable]
    <input class="hover-star required" type="radio" name="${name}" value="1" [#if score == "1"]checked[/#if] [#if disabled]disabled="disabled"[/#if] title=""/>
    <input class="hover-star" type="radio" name="${name}" value="2" [#if score == "2"]checked[/#if] [#if disabled]disabled="disabled"[/#if] title=""/>
    <input class="hover-star" type="radio" name="${name}" value="3" [#if score == "3"]checked[/#if] [#if disabled]disabled="disabled"[/#if] title="" />
    <input class="hover-star" type="radio" name="${name}" value="4" [#if score == "4"]checked[/#if] [#if disabled]disabled="disabled"[/#if] title="" />
    <input class="hover-star" type="radio" name="${name}" value="5" [#if score == "5"]checked[/#if] [#if disabled]disabled="disabled"[/#if] title="" />
    <div class="hover-test" style=""></div> 
    <div class="clearfix"></div>
    [#else]
      [#if score?has_content]Rate ${score}[#else]Not rated[/#if]
    [/#if]
  </div>
[/#macro]

[#macro advancedRank name stars=5 split=1 disabled=false editable=true]
  [#assign score][@s.property value="${name}"/][/#assign]
  <div class="rankingBlock" style="text-align:center;">
    <span class="rankSplit" style="display:none">${split}</span>
    [#if editable]
      [#list 1..(stars*split) as star]
        <input class="hover-star [#if star_index = 0]required[/#if] [#if split > 1]{split:${split}}[/#if]" type="radio" name="${name}" value="${star_index+1}" [#if score?number == star_index+1]checked="checked"[/#if] [#if disabled]disabled="disabled"[/#if] title="${(star_index+1)/split}"/>
      [/#list]
      <div class="hover-test" style=""></div> 
      <div class="clearfix"></div>
    [#else]
      [#if score?has_content]Rate ${(score?number)/split}[#else]Not rated[/#if]
    [/#if]
  </div>
[/#macro]

[#macro yesNoInput name disabled=false editable=true inverse=false value=""]
  [#if value == ""]
    [#assign customValue][@s.property value="${name}"/][/#assign]
  [#else]
    [#assign customValue=value /] 
  [/#if]
  <div class="onoffswitch">
    [#if editable]
      <div class="button-wrap">
        [#-- Yes Button --]
        <input type="radio" name="${name}" id="yes-button-${name}" value="true" [#if customValue == "true"]checked[/#if] class="hidden onoffswitch-radio [#if inverse]inverse[/#if]"/>
        <label for="yes-button-${name}" class="yes-button-label button-label [#if customValue == "true"]radio-checked[/#if]">Yes</label>
        [#-- No Button --]
        <input type="radio" name="${name}" id="no-button-${name}" value="false" [#if customValue == "false"]checked[/#if] class="hidden onoffswitch-radio [#if inverse]inverse[/#if]"/>
        <label for="no-button-${name}" class="no-button-label button-label [#if customValue == "false"]radio-checked[/#if]">No</label>
      </div>
      [#if disabled] <input type="hidden" name="${name}" value="true" />[/#if] 
    [#else]
      <p style="text-align:center;">[#if customValue=="true"]Yes[#elseif customValue == "false"]No[#else]Not selected[/#if]</p>
    [/#if]
  </div>
[/#macro]

[#-- The following macros aren't tested yet. --]

[#macro radioButton name value="-NULL" i18nkey="" label="" disabled=false checked=false id="" errorField=""]
  <div class="radioList">
    [#if errorField==""][@s.fielderror cssClass="fieldError" fieldName="${name}"/][#else][@s.fielderror cssClass="fieldError" fieldName="${errorfield}"/][/#if]
    <input type="radio" id="${id}" name="${name}" value="[#if value=="-NULL"][@s.property value="${name}"/][#else]${value}[/#if]" [#if checked]checked="true"[/#if] />
    <label for="${id}"> [#if i18nkey==""]${label}[#else][@s.text name="${i18nkey}"/][/#if] </label>
  </div>
[/#macro]

[#macro addRemoveLists name id selectedList allOptionList i18nkey="" disabled=false required=false errorfield=""]
  <div id="${id}-lists">
    <div class="selectTo">
      <select name="${name}" id="${id?c}">
        [#list selectedList as item]
          <option value="${item.id?c}">${item.name}</option>
        [/#list]
      </select>
    </div>
    
    <a href="" id="btn-add">Add &raquo;</a>
    <a href="" id="btn-remove">&laquo; Remove</a>
    
    <div class="selectFrom">
      <select id="${id}">
        [#list allOptionsList as item]
          [#if test="%{ #item not in #selectedList}"]
            <option value="${item.id?c}">${item.name}</option>
          [/#if]
        [/#list]
      </select>
    </div>
  </div>
[/#macro]