[#ftl]
[#macro text name readText=false param=""][#assign customName][#if readText]${name}.readText[#else]${name}[/#if][/#assign][@s.text name="${customName}"][@s.param]${param}[/@s.param][/@s.text][/#macro]

[#macro input name value="-NULL" type="text" i18nkey="" disabled=false required=false errorField="" help="" helpIcon=true display=true className="" paramText="" readOnly=false showTitle=true editable=true placeholder="" inputGroupText="" maxlength=""]
  <div class="feedback-flex-items"></div>
  <div class="input fieldReference ${changedField(name)}" style="display:${display?string('block','none')};">
    [#assign labelTitle][#if i18nkey==""][@s.text name="${name}"][@s.param]${paramText}[/@s.param][/@s.text][#else][@s.text name="${i18nkey}"][@s.param]${paramText}[/@s.param][/@s.text][/#if][/#assign]
    [#if showTitle]
      <label for="${name}" class="${editable?string('editable', 'readOnly')}">${labelTitle}:[@req required=required && editable /]</label>
      [#--  Help Text --]
      [@helpLabel name="${help}" paramText="${paramText}" showIcon=helpIcon editable=editable/]
    [/#if]
    [#if errorField==""][@s.fielderror cssClass="fieldError" fieldName="${name}"/][#else][@s.fielderror cssClass="fieldError" fieldName="${errorfield}"/][/#if]
    [#-- Get Custom Value --]
    [#assign customValue][#if value=="-NULL"][@s.property value="${name?string}"/][#else]${value}[/#if][/#assign]
    [#if editable]
      [#-- Condition to add input group to field --]
      [#if inputGroupText != ""]<div class="input-group"><span class="input-group-addon">${inputGroupText}</span>[/#if]
      <input type="${type}" id="${name}" name="${name}" value="${customValue}" class="form-control input-sm ${className} ${required?string('required','optional')}" [#if readOnly] readonly="readonly"[/#if] [#if disabled]disabled="disabled"[/#if] [#if placeholder?has_content]placeholder="[@s.text name=placeholder /]"[/#if]"/>
      [#if inputGroupText != ""]</div>[/#if]
      [#-- End condition --]
    [#else]
      [#local isCurrencyInput = className?contains("currencyInput") ]
      [#local requiredText][#if required && editable ]<span class="fieldError">[@s.text name="form.values.required" /]</span>[/#if][/#local] 
      [#-- Hidden input --]
      <input type="hidden" id="${name}" name="${name}" value="${customValue}" class="form-control input-sm ${className} ${required?string('required','optional')}"/>
      [#-- Show custom value --]
      <p>[#if (customValue?has_content)!false] [#if isCurrencyInput]<nobr>US$ ${((customValue)!'0')?number?string(",##0.00")}</nobr>[#else]${customValue}[/#if]  [#else]${requiredText}[@s.text name="form.values.fieldEmpty" /][/#if]</p>
    [/#if]
  </div>
  <div class="commentNumberContainer">
    <div class="numberOfCommentsBubble">
      <p></p>
    </div>
    <img src="${baseUrlCdn}/global/images/comment.png" class="qaComment" name="${name}" fieldID="" description="">
  </div>
[/#macro]

[#macro textArea name editable value="-NULL" i18nkey="" disabled=false required=false errorfield="" help="" helpIcon=true  fieldEmptyText="form.values.fieldEmpty" showTitle=true display=true className="-NULL" labelClass="" paramText="" readOnly=false editable=true placeholder="" allowTextEditor=false powbInclude=false]
  <div class="feedback-flex-items"></div>
  <div class="textArea fieldReference ${changedField(name)}" [#if !display]style="display: none;"[#else]style="width: 100%;"[/#if]> 
    [#assign customName]${(i18nkey?has_content)?string(i18nkey,name)}[/#assign]  
    [#assign customLabel][#if !editable]${customName}.readText[#else]${customName}[/#if][/#assign]
    [#-- Get Custom Value --]
    [#assign customValue][#if value=="-NULL"][@s.property value="${name?string}"/][#else]${value}[/#if][/#assign]
  	[#if showTitle]
      <label for="${name}" class="${editable?string('editable', 'readOnly')} ${labelClass} [#if powbInclude]powb-label[/#if]"> [@s.text name="${customLabel}"][@s.param]${paramText}[/@s.param][/@s.text]:[@req required=required && editable /]
        [#--  Help Text --]
        [@helpLabel name="${help}" paramText="${paramText}" showIcon=helpIcon editable=editable/]
        [#if powbInclude]
          <span class="powb-doc badge pull-right" title="[@s.text name="powb.includedField.title" /]">[@s.text name="powb.includedField" /] <span class="glyphicon glyphicon-save-file"></span></span>
        [/#if]
      </label>
    [/#if]
    [#if errorfield==""][@s.fielderror cssClass="fieldError" fieldName="${name}"/][#else][@s.fielderror cssClass="fieldError" fieldName="${errorfield}"/][/#if]
    [#if editable]
      <textarea rows="4" name="${name}" id="${name}" [#if readOnly] readonly="readonly"[/#if] [#if disabled]disabled="disabled"[/#if]  class="[#if className != "-NULL"]${className}[/#if] form-control input-sm ${required?string('required','optional')} [#if allowTextEditor]allowTextEditor[/#if]" placeholder="[@s.text name=placeholder /]" />${customValue}</textarea>
    [#else]
      [#assign requiredText][#if required && editable]<span class="fieldError">[@s.text name="form.values.required" /]</span>[/#if][/#assign] 
      [#-- Hidden input --]
      <input type="hidden" name="${name}" id="${name}" value="${customValue}" class="[#if className != "-NULL"] ${className}[/#if]  ${required?string('required','optional')}" />
      [#-- Show custom value --]
      <p class="${allowTextEditor?string('decodeHTML trumbowyg-editor', '')}">
        [#if (customValue?has_content)!false]${customValue?replace('\n', '<br>')}[#else]${requiredText}[@s.text name=fieldEmptyText /][/#if]
      </p>
    [/#if] 
  </div>
  <div class="commentNumberContainer">
    <div class="numberOfCommentsBubble">
      <p></p>
    </div>
    <img src="${baseUrlCdn}/global/images/comment.png" class="qaComment" name="${name}" fieldID="" description="">
  </div>
[/#macro]

[#macro button i18nkey class="" id="" editable=true]
  <input type="button" class="form-control ${class}" id="${id}" value="[@s.text name='${i18nkey}' /]" />
[/#macro]

[#macro checkbox name value="-NULL" label="" i18nkey="" disabled=false className="" checked=false required=false display=true help="" helpIcon=true paramText="" editable=true]
  <div class="checkbox ${changedField(name)}" [#if !display]style="display: none;"[/#if]>
    [#if editable]
      <label for="${name}" class="${editable?string('editable', 'readOnly')}">
        <input type="checkbox" id="${name}" class="${className}" name="${name}" value="${value}" [#if checked]checked="checked"[/#if] [#if disabled]disabled="disabled[/#if] />
        [#if i18nkey==""]${label}[#else][@s.text name="${i18nkey}" /][/#if] [@req required=required && editable /]
        <input type="hidden" id="__checkbox_${name}" name="__checkbox_${name}" value="${value}" />
        [#--  Help Text --]
        [@helpLabel name="${help}" paramText="${paramText}" showIcon=helpIcon editable=editable/]
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

[#macro select name listName label="" keyFieldName="" displayFieldName="" paramText="" value="-NULL" forcedValue="" valueName="" i18nkey="" disabled=false required=false errorField="" selected=false className="" multiple=false help="" helpIcon=true header=true display=true showTitle=true stringKey=false placeholder="" editable=true]
  <div class="feedback-flex-items"></div>
  <div class="select fieldReference ${changedField(name)}" [#if !display]style="display: none;"[/#if]>
    [#assign labelTitle][#if i18nkey==""][@s.text name="${name}"][@s.param]${paramText}[/@s.param][/@s.text][#else][@s.text name="${i18nkey}"][@s.param]${paramText}[/@s.param][/@s.text][/#if][/#assign]
    [#assign placeholderText][@s.text name="${(placeholder?has_content)?string(placeholder,'form.select.placeholder')}" /][/#assign]
    [#if showTitle]
    <label for="">
      [#if labelTitle != ""]${labelTitle}:[/#if][@req required=required && editable /]
      [#--  Help Text --]
      [@helpLabel name="${help}" paramText="${paramText}" showIcon=helpIcon editable=editable/]
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
              [#else]
                
               [#if key!="-1"]
                  ${key}  
               [/#if]

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
              [#if customValue?has_content] 
                ${customValue}  
              [#elseif forcedValue?has_content]
                ${forcedValue}
              [#else]
                ${requiredText} [@s.text name="form.values.fieldEmpty" /]  
              [/#if]
            [#else]
              [#if customValue?has_content]
                ${customValue}
              [#elseif value=="-1"]
                ${requiredText} [@s.text name="form.values.fieldEmpty" /]
              [#else]
                ${valueName} 
              [/#if] 
            [/#if]
            </p>
          [/#if]
      [/#if]  
    </div> 
  </div>
    <div class="commentNumberContainer">
    <div class="numberOfCommentsBubble">
      <p></p>
    </div>
    <img src="${baseUrlCdn}/global/images/comment.png" class="qaComment" name="${name}" fieldID="" description="">
  </div>
[/#macro]

[#macro selectGroup name list element  subListName="" keyFieldName="" displayFieldName="" i18nkey="" paramText="" disabled=false required=false className="" help="" helpIcon=true header=true showTitle=true placeholder="form.select.placeholder" editable=true]
  [#local valueSelected = (element[keyFieldName])!-1 /]
  [#if showTitle]
    <label for="">[@s.text name=i18nkey /]:[@req required=required && editable /]
      [#--  Help Text --]
      [@helpLabel name="${help}" paramText="${paramText}" showIcon=helpIcon editable=editable/]
    </label>
  [/#if]
  [#if editable]
    <select name="${name}" id="${name}" class="setSelect2 ${className}">
      [#if header]<option value="-1">[@s.text name=placeholder /]</option>[/#if]
      [#list list as groupsList]
        [#if groupsList[subListName]?has_content]
          <optgroup label="${groupsList[displayFieldName]}">
            [#list groupsList[subListName] as option]<option value="${option[keyFieldName]}"  [#if option[keyFieldName] == valueSelected]selected[/#if]>${option[displayFieldName]}</option>[/#list]
          </optgroup>
        [#else]
          [#if !(groupsList['parent']?has_content)]
            <option value="${groupsList[keyFieldName]}" [#if groupsList[keyFieldName] == valueSelected]selected[/#if]>${groupsList[displayFieldName]}</option>
          [/#if]
        [/#if]
      [/#list]
    </select>
  [#else]
    <p>[#if (element[keyFieldName]??)!false]${(element[displayFieldName])!(element.name)!'null'}[#else][@s.text name="form.values.fieldEmpty" /][/#if]</p>
    <input type="hidden" name="${name}" value="${valueSelected}" />
  [/#if]
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

[#macro confirmJustification action="" namespace="/" nameId="" title="" projectID="" required=true]
  <div id="dialog-justification" title="${title}" style="display:none"> 
    <div class="dialog-content"> 
      [@s.form action=action namespace="${namespace}" cssClass="pure-form"]
        [@textArea name="justification" i18nkey="saving.justification" required=required className="justification"/]
        [#if nameId != ""]
          <input class="nameId" name="${nameId}" type="hidden" value="-1" />
        [/#if]
        <input name="projectID" type="hidden" value="${projectID}" />
        <input type="hidden"  name="phaseID" value="${(actualPhase.id)!}"/>
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
        <input type="hidden"  name="phaseID" value="${(actualPhase.id)!}"/>
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
        <input type="hidden"  name="phaseID" value="${(actualPhase.id)!}"/>
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
        <input type="hidden"  name="phaseID" value="${(actualPhase.id)!}"/>
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
        <input type="hidden"  name="phaseID" value="${(actualPhase.id)!}"/>
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

[#macro yesNoInput name label="" disabled=false editable=true inverse=false value="" yesLabel="Yes" noLabel="No" cssClass="" neutral=false]
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
        <label for="yes-button-${name}" class="yes-button-label button-label [#if neutral]neutral[/#if] [#if (customValue == "true")!false]radio-checked[/#if]">${yesLabel}</label>
        [#-- No Button --]
        <label for="no-button-${name}" class="no-button-label button-label [#if neutral]neutral[/#if] [#if (customValue == "false")!false]radio-checked[/#if]">${noLabel}</label>
        [#-- Hidden Input --]
        <input type="hidden" name="${name}" id="hasCoordinates-${name}" class="onoffswitch-radio"  value="${(customValue)!-1}" />
      </div>
      [#if disabled] <input type="hidden" name="${name}" value="true" />[/#if] 
    [#else]
      <p style="text-align:center; display: inline-block"> [#if customValue=="true"]Yes[#elseif customValue == "false"]No[#else]Not selected[/#if]</p>
    [/#if]
  </div>
[/#macro]

[#macro yesNoInputDeliverable name label="" disabled=false editable=true inverse=false value="" yesLabel="Yes" noLabel="No" cssClass="" neutral=false]
  [#if value == ""]
    [#assign customValue][@s.property value="${name}"/][/#assign]
  [#else]
    [#assign customValue=value /]
  [/#if]
  <div class="feedback-flex-items"></div>
  <div class="onoffswitch fieldReference ${changedField(name)} ${cssClass}">
    [#if label?has_content]
      <label for="${name}">[@s.text name=label/]</label>
    [/#if]
    <div class="button-wrap radio-inline">  
      [#if editable]
        [#-- Yes Button --]
        <input id="${name}-yes" class="radio-input yesInput" type="radio" name="${name}" value="true" [#if (customValue == "true")!false]checked[/#if] />
        <label for="${name}-yes" class="${neutral?string('neutral', '')} yes-button-label button-label value-true [#if (customValue == "true")!false]radio-checked[/#if]"> ${yesLabel} </label>
        [#-- No Button --]
        <input id="${name}-no" class="radio-input noInput" type="radio" name="${name}" value="false" [#if (customValue == "false")!false]checked[/#if] />
        <label for="${name}-no" class="${neutral?string('neutral', '')} no-button-label button-label value-false [#if (customValue == "false")!false]radio-checked[/#if]"> ${noLabel} </label>
      [#else]
        <p style="text-align:center; display: inline-block"> [#if customValue=="true"]Yes[#elseif customValue == "false"]No[#else]Not selected[/#if]</p>
      [/#if]
    </div>
  </div>
  <div class="commentNumberContainer">
    <div class="numberOfCommentsBubble">
      <p></p>
    </div>
    <img src="${baseUrlCdn}/global/images/comment.png" class="qaComment" name="${name}" fieldID="" description="">
  </div>
[/#macro]

[#macro yesNoInputDeliverableParticipants name label="" disabled=false editable=true inverse=false value="" yesLabel="Yes" noLabel="No" cssClass="" neutral=false]
  [#if value == ""]
    [#assign customValue][@s.property value="${name}"/][/#assign]
  [#else]
    [#assign customValue=value /]
  [/#if]
  <div class="feedback-flex-items"></div>
  <div class="onoffswitch fieldReference ${changedField(name)} ${cssClass}">
    [#if label?has_content]
      <label for="${name}">[@s.text name=label/]</label>
    [/#if]
    <div class="button-wrap radio-inline">  
      [#if editable]
        [#-- Yes Button --]
        <input id="${name}-yes" class="radio-input yesInput" type="radio" name="${name}" value="true" [#if (customValue == "true")!false]checked[/#if] />
        <label for="${name}-yes" class="${neutral?string('neutral', '')} yes-button-label button-label value-true [#if (customValue == "true")!false]radio-checked[/#if]"> ${yesLabel} </label>
        [#-- No Button --]
        <input id="${name}-no" class="radio-input noInput" type="radio" name="${name}" value="false" [#if (customValue != "true")!false]checked[/#if] />
        <label for="${name}-no" class="${neutral?string('neutral', '')} no-button-label button-label value-false [#if (customValue != "true")!false]radio-checked[/#if]"> ${noLabel} </label>
      [#else]
        <p style="text-align:center; display: inline-block"> [#if customValue=="true"]Yes[#elseif customValue == "false"]No[#else]Not selected[/#if]</p>
      [/#if]
    </div>
  </div>
    <div class="commentNumberContainer">
    <div class="numberOfCommentsBubble">
      <p></p>
    </div>
    <img src="${baseUrlCdn}/global/images/comment.png" class="qaComment" name="${name}" fieldID="" description="">
  </div>
[/#macro]


[#macro radioFlat id name i18nkey="" label="" disabled=false editable=true value="" checked=true cssClass="" cssClassLabel="" inline=true columns=0]
  [#if editable]
  <div class="radioFlat [#if columns > 1]col-md-${columns}[/#if] ${inline?string('radio-inline', '')}">
    <input id="${id}" class="radio-input ${cssClass}" type="radio" name="${name}" value="${value}" [#if checked]checked[/#if] />
    <label for="${id}" class="radio-label ${cssClassLabel}">[#if i18nkey?has_content][@s.text name=i18nkey /][#else]${label}[/#if]</label>
  </div>
  [#elseif checked]
    <p>[#if i18nkey?has_content][@s.text name=i18nkey /][#else]${label}[/#if]</p>
  [/#if]
[/#macro]

[#macro checkBoxFlat id name label="" help="" paramText="" helpIcon=true disabled=false editable=true value="" checked=true cssClass="" cssClassLabel="" columns=0 ]
  <div class="inputsFlat [#if columns > 0]col-md-${columns}[/#if]">
    [#if editable]
    <input id="${id}" class="checkbox-input ${cssClass}" type="checkbox" name="${name}" value="${value}" [#if checked]checked=true[/#if] />
    <label for="${id}" class="checkbox-label ${cssClassLabel}"> [@s.text name=label /] 
      [#--  Help Text --]
      [@helpLabel name="${help}" paramText="${paramText}" showIcon=helpIcon editable=editable/]
    </label>
    [#elseif checked]
      <p>${label}</p>
    [/#if]
  </div>
[/#macro]

[#macro checkmark id name label="" i18nkey="" help="" paramText="" helpIcon=true disabled=false editable=true value="true" checked=true cssClass="" cssClassLabel="" centered=false]
  <label class="inputContainer ${cssClassLabel}"> 
    [#if editable]
      <input id="${id}" class="${cssClass}" type="checkbox" name="${name}" value="${value}" [#if checked]checked="checked"[/#if] >
      <span class="checkmark centered-${centered?string}"></span>
       [#if label?has_content || i18nkey?has_content]<label for="${id}" class="labelText ${cssClassLabel}">[#if i18nkey?has_content][@s.text name=i18nkey /][#else]${label}[/#if]</label>[/#if]
    [#else]
      <p class="checked-${checked?string}">
        [#if label?has_content || i18nkey?has_content ]<span class="${cssClassLabel}">[#if i18nkey?has_content][@s.text name=i18nkey /][#else]${label}[/#if]</span>[[/#if] 
      </p>
    [/#if]
  </label>
[/#macro]

[#macro fileUploadAjax fileDB name label="" dataUrl="" path="" required=false isEditable=true cssClass="" labelClass="" image=false imgUrl="" imgClass=""]
  [#assign hasFile = (fileDB.id??)!false /]
  <div class="fileUploadContainer ${cssClass}" >
    <label class="${labelClass}">[@customForm.text name=label readText=!isEditable /]: [@req required=required && isEditable /]</label>
    <input class="fileID" type="hidden" name="${name}" value="${(fileDB.id)!}" />
    [#if image]
    <div class="form-group">
      <img src="${baseUrlCdn}/global/images/${imgUrl}" class="${imgClass}" />
    </div>
    [/#if]
    [#-- Input File --]
    [#if isEditable]
      <div class="fileUpload" style="display:${hasFile?string('none','block')}"> <input class="upload" type="file" name="file" data-url="${dataUrl}"></div>
    [/#if]
    [#if !isEditable && !hasFile]
      <p>Prefilled if available</p>
    [/#if]
    [#-- Uploaded File --]
    <p class="fileUploaded textMessage" style="display:${hasFile?string('block','none')}">
      [#if path?has_content]<a href="${path}/${(fileDB.fileName)!('fileNotFound')}" target="_blank">[/#if]
      <span class="glyphicon glyphicon-file"></span> <span class="contentResult">${(fileDB.fileName)!('No file name')}</span> 
      [#if path?has_content]</a>[/#if]
      [#if isEditable]<span class="removeIcon"> </span>[/#if]
    </p>
    [#-- Progress Bar --]
    <div class="progress" style="height: 5px; display: none;">
      <div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%">
        <span class="sr-only"></span>
      </div>
    </div>
  </div>
[/#macro]

[#macro helpLabel name="" paramText="" showIcon=true editable=true helpMore=false]
  [#local nameValue][@s.text name="${name}"][@s.param]${paramText}[/@s.param][/@s.text][/#local]
  [#--  Help Text --]
  [#if nameValue?has_content && editable]
    [#if showIcon]
      <img src="${baseUrlCdn}/global/images/icon-help2.png" title="${nameValue}" />
      <span class="hint" style="display:none" title="${nameValue}"> [HINT] </span>
    [#else]
      <br /><i class="helpLabel">${nameValue}</i>
    [/#if]
  [/#if]
  [#if editable && helpMore]
    [@helpViewMore name="${name}" /]
  [/#if]
[/#macro]

[#function changedField name]
  [#if action.changedField(name)??]
    [#assign fieldObj = action.changedField(name)]
    [#return 'changedField changedFieldId-'+ (fieldObj.id)!]
  [/#if]
  [#return '']
[/#function]

[#macro elementsListComponent name elementType id="" elementList=[] label="" paramText="" help="" helpIcon=true listName="" keyFieldName="" displayFieldName="" maxLimit=0 indexLevel=1 required=true hasPrimary=false forceEditable=false onlyElementIDs=false i18nkey=""]
  [#attempt]
    [#local list = ((listName?eval)?sort_by((displayFieldName?split("."))))![] /] 
  [#recover]
    [#local list = [] /] 
  [/#attempt]
  
  [#local composedID = "${elementType}" /]
  [#if id?has_content]
    [#local composedID = "${elementType}-${id}" /]
  [/#if]
  <div class="feedback-flex-items"></div>
  <div class="fieldReference panel tertiary elementsListComponent" listname="${name}" style="position:relative">
    <div class="panel-head">
      <label for="">[@s.text name=label /]:[@req required=required && (editable || forceEditable) /]
        [#--  Help Text --]
        [@helpLabel name="${help}" paramText="${paramText}" showIcon=helpIcon editable=(editable || forceEditable)/]
      </label>
    </div>
    <div class="panel-body" style="min-height: 30px;">
      <div class="loading listComponentLoading" style="display:none"></div>
      <ul class="list">
        [#if elementList?has_content]
          [#if hasPrimary]<label class="primary-label">[#if editable]Set as primary [#else] Primary[/#if]</label>[/#if]
          [#list elementList as item][@listElementMacro name=name element=item type=elementType id=id index=item_index keyFieldName=keyFieldName displayFieldName=displayFieldName indexLevel=indexLevel hasPrimary=hasPrimary/][/#list]
        [/#if]
      </ul>
      [#if editable || forceEditable]
        <select name="" id="" class="setSelect2 maxLimit-${maxLimit} elementType-${composedID} indexLevel-${indexLevel}[#if (hasPrimary)!false] primarySelect[/#if]">
          <option value="-1">[@s.text name="form.select.placeholder" /]</option>
          [#list list as item]
            <option value="${(item[keyFieldName])!}">${(item[displayFieldName])!'null'}</option>
          [/#list]
        </select>
      [#else]
        [#if !(elementList?has_content)]<p class="font-italic"> No entries added yet.</p>[/#if]
      [/#if]
    </div>
    [#-- Element item Template --]
    <ul style="display:none">
      [@listElementMacro name="${name}" element={} type=elementType id=id index=-1 indexLevel=indexLevel template=true hasPrimary=hasPrimary onlyElementIDs=onlyElementIDs isEditable=(editable || forceEditable) /]
    </ul>
    <input type="hidden" name="${name}[]"/>
  </div>
    <div class="commentNumberContainer">
    <div class="numberOfCommentsBubble">
      <p></p>
    </div>
    <img src="${baseUrlCdn}/global/images/comment.png" class="qaComment" name="${name}" fieldID="" description="">
  </div>
[/#macro]

[#macro listElementMacro element name type id="" index=-1 keyFieldName="id" displayFieldName="composedName" indexLevel=1 template=false onlyElementIDs=false isEditable=true]
[#attempt]
    [#local list = ((listName?eval)?sort_by(displayFieldName))![] /] 
    [#local valueCheck = (((checkName?eval)?eval)) /] 
  [#recover]
    [#local list = [] /] 
  [/#attempt]
  
  [#local composedID = "${elementType}" /]
  [#if id?has_content]
    [#local composedID = "${elementType}-${id}" /]
  [/#if]

  <div class="panel tertiary elementsListComponent" listname="${name}" style="position:relative">
    <div class="panel-head">
      <label for="">[@s.text name=label /]:[@req required=required && editable /]
        [#--  Help Text --]
        [@helpLabel name="${help}" paramText="${paramText}" showIcon=helpIcon editable=editable/]
      </label>
    </div>
    <div class="panel-body" style="min-height: 30px;">
      <div class="loading listComponentLoading" style="display:none"></div>
      <ul class="list">
        [#if elementList?has_content]
          [#list elementList as item][@listElementMacro name=name element=item type=elementType id=id index=item_index keyFieldName=keyFieldName displayFieldName=displayFieldName indexLevel=indexLevel /][/#list]
        [/#if]
      </ul>
      [#if editable]
        <select name="" id="" class="setSelect2 maxLimit-${maxLimit} elementType-${composedID} indexLevel-${indexLevel} primarySelectorField" >
          <option value="-1">[@s.text name="form.select.placeholder" /]</option>
          [#list list as item]
            <option value="${(item[keyFieldName])!}">${(item[displayFieldName])!'null'}</option>
          [/#list]
        </select>
      [#else]
        [#if !(elementList?has_content)]<p class="font-italic"> No entries added yet.</p>[/#if]
      [/#if]
    </div>
    [#-- Element item Template --]
    <ul style="display:none">
      [@listElementMacro name="${name}" element={} type=elementType id=id index=-1 indexLevel=indexLevel template=true /]
    </ul>
    [#-- Select primary  field --]
    <div class="primarySelectorDisplayBox ${(checkName)}" style="min-height: 30px; margin-top: 10px; display:${(elementList?has_content)?string('block','none')}">
      <div class="panel-head">
        <label for="">${(((checkName?eval)?eval))} [@s.text name=labelPrimary /]:[@req required=required && editable /]
          [#--  Help Text --]
          [@helpLabel name="${help}" paramText="${paramText}" showIcon=helpIcon editable=editable/]
        </label>
      </div>
      <div class="panel-body" style="min-height: 30px;">
        <ul class="primaryRadio">
          [#if elementList?has_content]
            [#list elementList as item]
            <div class="radioFlat selectPrimary radioContentBox ID-${(item[elementType][keyFieldName])}" >
              <input id="primaryRadioButtonID${(checkName)}-${(item[elementType][keyFieldName])}" class="radio-input assesmentLevels primaryRadioButton option-${(item[elementType][keyFieldName])}" type="radio" name="${checkName}" value="${(item[elementType][keyFieldName])!'{0}'}" [#if (valueCheck == (item[elementType][keyFieldName]))!false] checked=true[/#if] />
              <label for="primaryRadioButtonID${(checkName)}-${(item[elementType][keyFieldName])}" class="radio-label">${(item[elementType][displayFieldName])!'{elementNameUndefined}'}</label>
            </div>
             [/#list]
          [/#if]  
        </ul>
      </div>
    </div>
  </div>
[/#macro]

[#macro primaryListComponent name  elementType checkName="" id="" elementList=[] label="" labelPrimary="" label="" paramText="" help="" helpIcon=true listName="" keyFieldName="" displayFieldName="" maxLimit=0 indexLevel=1 required=true checked=true ]
  [#attempt]
    [#local list = ((listName?eval)?sort_by(displayFieldName))![] /] 
    [#local valueCheck = (((checkName?eval)?eval)) /] 
  [#recover]
    [#local list = [] /] 
  [/#attempt]
  
  [#local composedID = "${elementType}" /]
  [#if id?has_content]
    [#local composedID = "${elementType}-${id}" /]
  [/#if]

  <div class="panel tertiary elementsListComponent" listname="${name}" style="position:relative">
    <div class="panel-head">
      <label for="">[@s.text name=label /]:[@req required=required && editable /]
        [#--  Help Text --]
        [@helpLabel name="${help}" paramText="${paramText}" showIcon=helpIcon editable=editable/]
      </label>
    </div>
    <div class="panel-body" style="min-height: 30px;">
      <div class="loading listComponentLoading" style="display:none"></div>
      <ul class="list">
        [#if elementList?has_content]
          [#list elementList as item][@listElementMacro name=name element=item type=elementType id=id index=item_index keyFieldName=keyFieldName displayFieldName=displayFieldName indexLevel=indexLevel /][/#list]
        [/#if]
      </ul>
      [#if editable]
        <select name="" id="" class="setSelect2 maxLimit-${maxLimit} elementType-${composedID} indexLevel-${indexLevel} primarySelectorField" >
          <option value="-1">[@s.text name="form.select.placeholder" /]</option>
          [#list list as item]
            <option value="${(item[keyFieldName])!}">${(item[displayFieldName])!'null'}</option>
          [/#list]
        </select>
      [#else]
        [#if !(elementList?has_content)]<p class="font-italic"> No entries added yet.</p>[/#if]
      [/#if]
    </div>
    [#-- Element item Template --]
    <ul style="display:none">
      [@listElementMacro name="${name}" element={} type=elementType id=id index=-1 indexLevel=indexLevel template=true /]
    </ul>
    [#-- Select primary  field --]
    <div class="primarySelectorDisplayBox ${(checkName)}" style="min-height: 30px; margin-top: 10px; display:${(elementList?has_content)?string('block','none')}">
      <div class="panel-head">
        <label for="">${(((checkName?eval)?eval))} [@s.text name=labelPrimary /]:[@req required=required && editable /]
          [#--  Help Text --]
          [@helpLabel name="${help}" paramText="${paramText}" showIcon=helpIcon editable=editable/]
        </label>
      </div>
      <div class="panel-body" style="min-height: 30px;">
        <ul class="primaryRadio">
          [#if elementList?has_content]
            [#list elementList as item]
            <div class="radioFlat selectPrimary radioContentBox ID-${(item[elementType][keyFieldName])}" >
              <input id="primaryRadioButtonID${(checkName)}-${(item[elementType][keyFieldName])}" class="radio-input assesmentLevels primaryRadioButton option-${(item[elementType][keyFieldName])}" type="radio" name="${checkName}" value="${(item[elementType][keyFieldName])!'{0}'}" [#if (valueCheck == (item[elementType][keyFieldName]))!false] checked=true[/#if] />
              <label for="primaryRadioButtonID${(checkName)}-${(item[elementType][keyFieldName])}" class="radio-label">${(item[elementType][displayFieldName])!'{elementNameUndefined}'}</label>
            </div>
             [/#list]
          [/#if]  
        </ul>
      </div>
    </div>
  </div>
[/#macro]

[#macro listElementMacro element name type id="" index=-1 keyFieldName="id" displayFieldName="composedName" indexLevel=1 template=false hasPrimary=false isEditable=true onlyElementIDs=false]
  [#local customName = "${template?string('_TEMPLATE_', '')}${name}[${index}]"]
  [#local composedID = "${type}" /]
  [#if id?has_content]
    [#local composedID = "${type}-${id}" /]
  [/#if]
  [#if hasPrimary]
    [#attempt]
     [#if template]
      [#local primaryValue = false /]
     [#else]
      [#local primaryValue = "${customName}.primary"?eval!false /]
     [/#if]
    [#recover]
      [#local primaryValue = false /]
    [/#attempt]
    <li class="[#if template]relationElement-template[/#if] relationElement indexLevel-${indexLevel}">
      [#-- Remove button --]
      [#if editable]<div class="removeElement sm removeIcon removeElementType-${composedID}" title="Remove"></div>[/#if] 
          <div class="form-group row primary-list">
          <div class="col-md-1 primary-radio">
          [#if editable]
            [@radioFlat id="${customName}.primary" name="${customName}.primary" value="true" cssClassLabel="radio-label-yes in-radio-list" editable=editable checked=(primaryValue)!false/]
            [#else]
              [#if primaryValue==true]
                <span class="primary-element glyphicon glyphicon-ok-sign"></span>
              [/#if]
          [/#if]
          </div>
          <div class="col-md-1"></div>
          <div class="col-md-10">
          [#-- Hidden Inputs --]
          <input type="hidden" class="elementID" name="${customName}.id" value="${(element.id)!}" />
          <input type="hidden" class="elementRelationID" name="${customName}.${type}.id" value="${(element[type][keyFieldName])!}" />
          [#-- Title --]
          <span class="elementName">${(element[type][displayFieldName])!'{elementNameUndefined}'}</span>
          </div>
          </div>
      </li>  
  [#else]
  <li class="[#if template]relationElement-template[/#if] relationElement indexLevel-${indexLevel}">
    [#-- Hidden Inputs --]
    <input type="hidden" class="elementID" name="${customName}.id" value="${(element.id)!}" />
    <input type="hidden" class="elementRelationID" name="${customName}.${type}.id" value="${(element[type][keyFieldName])!}" />
    [#-- Remove button --]
    [#if isEditable]<div class="removeElement sm removeIcon removeElementType-${composedID}" title="Remove"></div>[/#if] 
    [#-- Title --]
    <span class="elementName">${(element[type][displayFieldName])!'{elementNameUndefined}'}</span>
  </li>
  [/#if]
[/#macro]

[#macro helpViewMore name=""]
  [#local customName="${name}.more" /]
   <i class="helpLabel"><a id="helpViewMoreLink" class="btn-link viewMoreLinkclosed" data-toggle="collapse" data-target="#helpViewMoreBlock" aria-expanded="false" aria-controls="helpViewMoreBlock">
     [@s.text name="global.viewMore" /]
   </a></i>
   <div id="helpViewMoreBlock" class="collapse" aria-labelledby="helpViewMoreBlock" data-parent="#helpViewMoreLink">
      <i class="helpLabel">[@s.text name="${customName}" /]</i>
   </div>
[/#macro]

[#macro qaPopUp]
  <div id="qaPopup">
    <div class="closeComment"></div>
    <br>
    [@customForm.textArea name="Comment on" required=false className="limitWords-100" editable=editable /]
    <div class="commentCheckContainer">
      <div class="commentContainer">
        <div class="commentTitle"></div>
        <p class="commentReadonly"></p>
      </div>
      <div class="checkContainer">
        <img src="${baseUrlCdn}/global/images/agree.png" class="agreeComment" title="Agree">
        <img src="${baseUrlCdn}/global/images/disagree.png" class="disagreeComment" title="Disagree">
        <img src="${baseUrlCdn}/global/images/question.png" class="clarificationComment" title="Clarification needed">
      </div>
    </div>
    <div class="replyContainer">
      <br>
      [@customForm.textArea name="Reply" required=false className="limitWords-100" editable=editable /]
      <div class="replyTextContainer">
        <div class="replyTitle"></div>
        <p class="replyReadonly"></p>
      </div>
      <div id="sendReplyContainer" class="sendCommentContainer"><img src="${baseUrlCdn}/global/images/send.png" class="sendComment" title="Send"></div>
    </div>  
    <br>
    <div id="sendCommentContainer" class="sendCommentContainer"><img src="${baseUrlCdn}/global/images/send.png" class="sendComment" title="Send"></div>
    <div class="optionsContainer">
      <img id="agreeCommentBtn" src="${baseUrlCdn}/global/images/agree.png" class="qaOptions" title="Agree">
      <img id="disagreeCommentBtn" src="${baseUrlCdn}/global/images/disagree.png" class="qaOptions" title="Disagree">
      <img id="clarificationCommentBtn" src="${baseUrlCdn}/global/images/question.png" class="qaOptions" title="Clarification needed">
      <img id="replyCommentBtn" src="${baseUrlCdn}/global/images/auto-reply.png" class="qaOptions" title="Reply">
    </div>
  </div>
[/#macro]

[#macro qaPopUpMultiple fields="" name="" index=-1 canLeaveComments=false template=false]
  [#local customName = "${template?string('TEMPLATE', '')}${name}[${index}]"]
  <div id="qaPopup-${customName}" class="qaPopup">
    <div class="closeComment" name="${name}"></div>
    <br>
    [#if fields?has_content]
      [#list fields as field]
        [@qaCommentReplyBlock name=name index=field_index canLeaveComments=canLeaveComments/]
      [/#list]
    [#else]
      [@qaCommentReplyBlock name=name canLeaveComments=canLeaveComments/]
    [/#if]
  </div>
[/#macro]

[#macro qaCommentReplyBlock name="" index=0 canLeaveComments=false]
  [#if index == 0]
    [#local showTitle = true]
  [#else]
    [#local showTitle = false]
  [/#if]

  [#if editable == false]
    [#assign editable = canLeaveComments]
  [/#if]

  <div id="qaCommentReply-${name}[${index}]" class="qaCommentReplyBlock" index="${index}">
    [@customForm.textArea name="New comment" required=false className="limitWords-100" editable=editable showTitle=showTitle /]
    <div class="commentCheckContainer">
      <div class="commentContainer">
        <div class="commentTitle"></div>
        <p class="commentReadonly"></p>
      </div>
    </div>
    <div class="replyContainer">
      [@customForm.textArea name="Reply" required=false className="limitWords-100" editable=editable /]
      <div class="replyTextContainer">
        <div class="replyTitle"></div>
        <p class="replyReadonly"></p>
      </div>
      <div class="sendReplyContainer" commentId=""><img src="${baseUrlCdn}/global/images/send.png" class="sendComment" title="Send"></div>
    </div>
    <div class="sendCommentContainer"><img src="${baseUrlCdn}/global/images/send.png" class="sendComment" title="Send"></div>
    <div class="buttonsContainer">
      <div class="optionsContainer">
        <img class="agreeCommentBtn qaOptions" commentId="" src="${baseUrlCdn}/global/images/agree.png" title="Agree">
        <img class="disagreeCommentBtn qaOptions" commentId="" src="${baseUrlCdn}/global/images/disagree.png" title="Disagree">
        <img class="clarificationCommentBtn qaOptions" commentId="" src="${baseUrlCdn}/global/images/question.png" title="Clarification needed">
        <img class="replyCommentBtn qaOptions" commentId="" src="${baseUrlCdn}/global/images/auto-reply.png" title="Reply">
      </div>
      <div class="addCommentContainer" index="${index}"><img src="${baseUrlCdn}/global/images/comment.png" class="addCommentBlock" title="Add comment"></div>
    </div>
    <br>
  </div>
[/#macro]
