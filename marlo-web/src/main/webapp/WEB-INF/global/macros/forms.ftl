[#ftl]
[#macro text name readText=false param="" ][#assign customName][#if readText]${name}.readText[#else]${name}[/#if][/#assign][@s.text name="${customName}"][@s.param]${param}[/@s.param][/@s.text][/#macro]

[#macro input name value="-NULL" type="text" i18nkey="" disabled=false required=false errorField="" help="" display=true className="" paramText="" readOnly=false showTitle=true editable=true placeholder=""]
  <div class="input ${changedField(name)}" style="display:${display?string('block','none')};">
    [#assign labelTitle][#if i18nkey==""][@s.text name="${name}"][@s.param]${paramText}[/@s.param][/@s.text][#else][@s.text name="${i18nkey}"][@s.param]${paramText}[/@s.param][/@s.text][/#if][/#assign]
    [#if showTitle]
      <label for="${name}" class="${editable?string('editable', 'readOnly')}">${labelTitle}:[@req required=required && editable /]</label>
      [#if help != ""]<img src="${baseUrl}/global/images/icon-help2.png" title="[@s.text name="${help}"/]" />[/#if]
    [/#if]
    [#if errorField==""][@s.fielderror cssClass="fieldError" fieldName="${name}"/][#else][@s.fielderror cssClass="fieldError" fieldName="${errorfield}"/][/#if]
    [#if editable]
      <input type="${type}" id="${name}" name="${name}" value="[#if value=="-NULL"][@s.property value="${name?string}"/][#else]${value}[/#if]"  class="form-control input-sm ${className} ${required?string('required','optional')}" [#if readOnly] readonly="readonly"[/#if] [#if disabled]disabled="disabled"[/#if] [#if placeholder?has_content]placeholder="[@s.text name=placeholder /]"[/#if]/>
    [#else]
      <input type="hidden" id="${name}" name="${name}" value="[#if value=="-NULL"][@s.property value="${name?string}"/][#else]${value}[/#if]" class="form-control input-sm ${className} ${required?string('required','optional')}"/>
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

[#macro textArea name editable value="-NULL" i18nkey="" disabled=false required=false errorfield="" help="" showTitle=true display=true className="-NULL" paramText="" readOnly=false editable=true placeholder=""]
  <div class="textArea ${changedField(name)}" [#if !display]style="display: none;"[/#if]> 
    [#assign customName]${(i18nkey?has_content)?string(i18nkey,name)}[/#assign]  
    [#assign customLabel][#if !editable]${customName}.readText[#else]${customName}[/#if][/#assign]
    [#assign customValue][#if value=="-NULL"][@s.property value="${name}"/][#else]${value}[/#if][/#assign]
  	[#if showTitle]
      <label for="${name}" class="${editable?string('editable', 'readOnly')}"> [@s.text name="${customLabel}"][@s.param]${paramText}[/@s.param][/@s.text]:[@req required=required && editable /]
        [#if help != ""]
          <img  class="hint-img" src="${baseUrl}/global/images/icon-help2.png" title="[@s.text name="${help}"/]" style="display:inline-block" />
          <span class="hint" style="display:none" title="[@s.text name="${help}"/]"> [HINT] </span>
        [/#if]
      </label>
    [/#if]
    [#if errorfield==""][@s.fielderror cssClass="fieldError" fieldName="${name}"/][#else][@s.fielderror cssClass="fieldError" fieldName="${errorfield}"/][/#if]
    [#if editable]
      <textarea rows="4" name="${name}" id="${name}" [#if readOnly] readonly="readonly"[/#if] [#if disabled]disabled="disabled"[/#if]  class="[#if className != "-NULL"]${className}[/#if] form-control input-sm ${required?string('required','optional')}" placeholder="[@s.text name=placeholder /]" />${customValue}</textarea>
    [#else]
      <input type="hidden" name="${name}" id="${name}" value="${customValue}" class="[#if className != "-NULL"] ${className}[/#if]  ${required?string('required','optional')}" />
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
[/#macro]

[#macro button i18nkey class="" id="" editable=true]
  <input type="button" class="form-control ${class}" id="${id}" value="[@s.text name='${i18nkey}' /]" />
[/#macro]

[#macro checkbox name value="-NULL" label="" i18nkey="" disabled=false className="" checked=false required=false display=true help="" editable=true]
  <div class="checkbox ${changedField(name)}" [#if !display]style="display: none;"[/#if]>
    [#if editable]
      <label for="${name}" class="${editable?string('editable', 'readOnly')}">
        <input type="checkbox" id="${name}" class="${className}" name="${name}" value="${value}" [#if checked]checked="checked"[/#if] [#if disabled]disabled="disabled[/#if] />
        [#if i18nkey==""]${label}[#else][@s.text name="${i18nkey}" /][/#if] [@req required=required && editable /]
        <input type="hidden" id="__checkbox_${name}" name="__checkbox_${name}" value="${value}" />
        [#if help != ""]<img src="${baseUrl}/global/images/icon-help2.png" title="[@s.text name="${help}"/]" />[/#if]
      </label>
    [#else]
      [#if checked]<p class="checked">[#if i18nkey==""]${label}[#else][@s.text name="${i18nkey}.readText" /][/#if]</p>[/#if]
    [/#if]
  </div>
[/#macro]

[#macro checkboxGroup label name listName displayFieldName="" keyFieldName="" value="-NULL" i18nkey="" disabled=false required=false errorField="" checked=false display=true editable=true]
  <div class="checkbox ${changedField(name)}" [#if !display]style="display: none;"[/#if]>
    [#if i18nkey==""]${label}[#else][@s.text name="${i18nkey}" /][/#if]:[@req required=required && editable /]
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
  <div class="radioGroup ${changedField(name)}" [#if !display]style="display: none;"[/#if]>
    [#if i18nkey==""]${label}[#else][@s.text name="${i18nkey}" /][/#if]:[@req required=required && editable /]
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

[#macro select name listName label="" keyFieldName="" displayFieldName="" paramText="" value="-NULL" i18nkey="" disabled=false required=false errorField="" selected=false className="" multiple=false help="" header=true display=true showTitle=true stringKey=false placeholder="" editable=true]
  <div class="select ${changedField(name)}" [#if !display]style="display: none;"[/#if]>
    [#assign labelTitle][#if i18nkey==""][@s.text name="${name}"][@s.param]${paramText}[/@s.param][/@s.text][#else][@s.text name="${i18nkey}"][@s.param]${paramText}[/@s.param][/@s.text][/#if][/#assign]
    [#assign placeholderText][@s.text name="${(placeholder?has_content)?string(placeholder,'form.select.placeholder')}" /][/#assign]
    [#if showTitle]
    <label for="">
        [#if labelTitle != ""]${labelTitle}:[/#if][@req required=required && editable /]
        [#if help != ""]<img src="${baseUrl}/global/images/icon-help2.png" title="[@s.text name="${help}"/]" />[/#if]
    </label>
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
            [@s.select name="${name}" list="${listName}" value="${customValue}" disabled="${disabled?string}" cssClass="${className} form-control input-sm" multiple="true" tooltip="${helpText}"   /]
          [#else]
            [#if header]
              [@s.select name="${name}" list="${listName}" value="${customValue}" disabled="${disabled?string}" cssClass="${className} form-control input-sm" tooltip="${helpText}" headerKey="-1" headerValue=placeholderText /]
            [#else]
              [@s.select name="${name}" list="${listName}" value="${customValue}" disabled="${disabled?string}" cssClass="${className} form-control input-sm" tooltip="${helpText}" /]
            [/#if]
          [/#if]
        [#else]
          [#if multiple]
            [@s.select name="${name}" list="${listName}" listKey="${keyFieldName}" listValue="${displayFieldName}" value="${customValue}" disabled="${disabled?string}" cssClass="${className} form-control input-sm" multiple="true" tooltip="${helpText}" /]
          [#else]
            [#if header]
              [@s.select name="${name}" list="${listName}" listKey="${keyFieldName}" listValue="${displayFieldName}" value="${customValue}" disabled="${disabled?string}" cssClass="${className} form-control input-sm" tooltip="${helpText}" headerKey="-1" headerValue=placeholderText /]
            [#else]
              [@s.select name="${name}" list="${listName}" listKey="${keyFieldName}" listValue="${displayFieldName}" value="${customValue}" disabled="${disabled?string}" cssClass="${className} form-control input-sm" tooltip="${helpText}" /]
            [/#if]
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
            [#if name?contains(".id")]
              [#assign customName]${name?replace('.id','')}[/#assign]
            [#else]
              [#assign customName]${name}[/#assign]
            [/#if]
            [#assign customValue][@s.property value="${customName}.${displayFieldName}"/][/#assign]
            [#if value=="-NULL"] 
              [#if !(customValue)?has_content] ${requiredText}   [@s.text name="form.values.fieldEmpty" /]
              [#else]${customValue}
              [/#if]
            [#else]
              [#if customValue?has_content]${customValue}
              [#elseif value=="-1"]${requiredText}   [@s.text name="form.values.fieldEmpty" /]
              [/#if] 
            [/#if]
            </p>
          [/#if]
      [/#if]  
    </div> 
  </div>
[/#macro]

[#macro inputFile name template=false className="" fileUrl="" fileName="" editable=true]
  [#local customId][#if template]${name}-template[#else]${name}[/#if][/#local]
  [#local customFileName][@s.property value="${fileName}"/][/#local]
  <div id="${customId}" class="${className} ${changedField(name)} form-group" style="${template?string('display:none','block')}">
    [#if customFileName?has_content && !template]
      <p class="inputFile"> 
        <span class="file"></span> <a href="${fileUrl}${customFileName}">${customFileName}</a>  
        [#if editable]<span id="remove-${name}" class="remove"></span>[/#if] 
      </p>
    [#else]
      [#if editable]
       [@s.fielderror cssClass="fieldError" fieldName="${name}"/]
       [@s.file name="${name}" id="" cssClass="upload" cssStyle=""  /]
      [#else]
        <p>[@s.text name="form.values.notFileUploaded" /]</p> 
      [/#if]
    [/#if]
    
    [#if template]
      <input type="hidden" name="${fileName}"  /> 
    [#else]
      <input type="hidden" name="${fileName}" value="${(customFileName)!}" /> 
    [/#if]
  </div>
  
[/#macro] 
 
[#macro req required=true ]<span class="red requiredTag" style="display:${required?string('inline','none')};">*</span>[/#macro]

[#macro confirmJustification action="" namespace="/" nameId="" title="" projectID=""]
  <div id="dialog-justification" title="${title}" style="display:none"> 
    <div class="dialog-content"> 
      [@s.form action=action namespace="${namespace}" cssClass="pure-form"]
        [@textArea name="justification" i18nkey="saving.justification" required=true className="justification"/]
        [#if nameId != ""]
          <input class="nameId" name="${nameId}" type="hidden" value="-1" />
        [/#if]
        <input name="projectID" type="hidden" value="${projectID}" />
        <!-- Allow form submission with keyboard without duplicating the dialog button -->
        <input type="submit" tabindex="-1" style="position:absolute; top:-1000px">
      [/@s.form]
    </div>  
  </div>
[/#macro]

[#macro confirmJustificationOutcome action="" namespace="/" nameId="" title="" outcomeID=""]
  <div id="dialog-justification" title="${title}" style="display:none"> 
    <div class="dialog-content"> 
      [@s.form action=action namespace="${namespace}" cssClass="pure-form"]
        [@textArea name="justification" i18nkey="saving.justification.outcome" required=true className="justification"/]
        [#if nameId != ""]
          <input name="${nameId}" type="hidden" value="-1" />
        [/#if]
        <input name="outcomeID" type="hidden" value="${outcomeID}" />
        <!-- Allow form submission with keyboard without duplicating the dialog button -->
        <input type="submit" tabindex="-1" style="position:absolute; top:-1000px">
      [/@s.form]
    </div>  
  </div>
[/#macro]


[#macro confirmJustificationOut action="" namespace="/" nameId="" title="" outputID=""]
  <div id="dialog-justification" title="${title}" style="display:none"> 
    <div class="dialog-content"> 
      [@s.form action=action namespace="${namespace}" cssClass="pure-form"]
        [@textArea name="justification" i18nkey="saving.justification.output" required=true className="justification"/]
        [#if nameId != ""]
          <input name="${nameId}" type="hidden" value="-1" />
        [/#if]
        <input name="outputID" type="hidden" value="${outputID}" />
        <!-- Allow form submission with keyboard without duplicating the dialog button -->
        <input type="submit" tabindex="-1" style="position:absolute; top:-1000px">
      [/@s.form]
    </div>  
  </div>
[/#macro]

[#macro confirmJustificationProject action="" namespace="/" nameId="" title="" projectID=""]
  <div id="dialog-justification" title="${title}" style="display:none"> 
    <div class="dialog-content"> 
      [@s.form action=action namespace="${namespace}" cssClass="pure-form"]
        [@textArea name="justification" i18nkey="saving.justification.project" required=true className="justification"/]
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

[#macro confirmJustificationDeliverable action="" namespace="/" nameId="" title="" deliverableID=""]
  <div id="dialog-justification" title="${title}" style="display:none"> 
    <div class="dialog-content"> 
      [@s.form action=action namespace="${namespace}" cssClass="pure-form"]
        [@textArea name="justification" i18nkey="saving.justification.deliverable" required=true className="justification"/]
        [#if nameId != ""]
          <input name="${nameId}" type="hidden" value="-1" />
        [/#if]
        <input name="deliverableID" type="hidden" value="${deliverableID}" />
        <!-- Allow form submission with keyboard without duplicating the dialog button -->
        <input type="submit" tabindex="-1" style="position:absolute; top:-1000px">
      [/@s.form]
    </div>  
  </div>
[/#macro]

[#macro rank name disabled=false editable=true]
  [#assign score][@s.property value="${name}"/][/#assign]
  <div class="rankingBlock ${changedField(name)}" style="text-align:center;">
    [#if editable]
    <fieldset class="rating">
    <input type="radio" id="star5" name="rating" value="5" /><label class = "full" for="star5" title="5 stars"></label>
    <input type="radio" id="star4half" name="rating" value="4.5" /><label class="half" for="star4half" title="4.5 stars"></label>
    <input type="radio" id="star4" name="rating" value="4" /><label class = "full" for="star4" title="4 stars"></label>
    <input type="radio" id="star3half" name="rating" value="3.5" /><label class="half" for="star3half" title="3.5 stars"></label>
    <input type="radio" id="star3" name="rating" value="3" /><label class = "full" for="star3" title="3 stars"></label>
    <input type="radio" id="star2half" name="rating" value="2.5" /><label class="half" for="star2half" title="2.5 stars"></label>
    <input type="radio" id="star2" name="rating" value="2" /><label class = "full" for="star2" title="2 stars"></label>
    <input type="radio" id="star1half" name="rating" value="1.5" /><label class="half" for="star1half" title="1.5 stars"></label>
    <input type="radio" id="star1" name="rating" value="1" /><label class = "full" for="star1" title="1 star"></label>
    <input type="radio" id="starhalf" name="rating" value="0.5" /><label class="half" for="starhalf" title="0.5 stars"></label>
    </fieldset>
    [#else]
      [#if score?has_content]Rate ${score}[#else]Not rated[/#if]
    [/#if]
  </div>
[/#macro]

[#macro advancedRank name stars=5 split=1 disabled=false editable=true]
  [#assign score][@s.property value="${name}"/][/#assign]
  <div class="rankingBlock ${changedField(name)}" style="text-align:center;">
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

[#macro yesNoInput name label="" disabled=false editable=true inverse=false value="" yesLabel="Yes" noLabel="No" cssClass=""]
  [#if value == ""]
    [#assign customValue][@s.property value="${name}"/][/#assign]
  [#else]
    [#assign customValue=value /]
  [/#if]
  <div class="onoffswitch ${changedField(name)} ${cssClass}">
    [#if label?has_content]
      <label for="${name}">[@s.text name=label/]</label>
    [/#if]
    [#if editable]
      <div class="button-wrap">
        [#-- Yes Button --]
        <label for="yes-button-${name}" class="yes-button-label button-label [#if customValue == "true"]radio-checked[/#if]">${yesLabel}</label>
        [#-- No Button --]
        <label for="no-button-${name}" class="no-button-label button-label [#if customValue == "false" || !(customValue?has_content)]radio-checked[/#if]">${noLabel}</label>
        <input type="hidden" name="${name}" id="hasCoordinates-${name}" class="onoffswitch-radio"  [#if !(customValue?has_content)]value="false"[/#if] [#if customValue == "false"]value="false"[#elseif customValue == "true"]value="true"[/#if] />
      </div>
      [#if disabled] <input type="hidden" name="${name}" value="true" />[/#if] 
    [#else]
      <p style="text-align:center; display: inline-block"> [#if customValue=="true"]Yes[#elseif customValue == "false"]No[#else]Not selected[/#if]</p>
    [/#if]
  </div>
[/#macro]

[#macro radioFlat id name label="" disabled=false editable=true value="" checked=true cssClass=""]
  <div class="radioFlat radio-inline">
    <input id="${id}" class="radio-input ${cssClass}" type="radio" name="${name}" value="${value}" [#if checked]checked[/#if] />
    <label for="${id}" class="radio-label"> ${label} </label>
  </div>
[/#macro]


[#function changedField name]
  [#if action.changedField(name)??]
    [#assign fieldObj = action.changedField(name)]
    [#return 'changedField changedFieldId-'+ (fieldObj.id)!]
  [/#if]
  [#return '']
[/#function]


