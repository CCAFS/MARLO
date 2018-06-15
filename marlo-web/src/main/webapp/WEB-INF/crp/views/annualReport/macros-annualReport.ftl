[#ftl]
[#macro indicatorInformation name list index id="indicatorID" label="" editable=true]
  [#local customName = "${name}[${index}]"]
  [#local element = (list[index])!{} ]
  [#-- Hidden Inputs Indicator --]
  <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
  <input type="hidden"  name="${customName}.repIndSynthesisIndicator.id" value="${(element.repIndSynthesisIndicator.id)!}"/>
  
  [#-- Data --]
  <div class="form-group margin-panel">
    [@customForm.textArea name="${customName}.data" i18nkey="${label}.${id}.data" help="${label}.${id}.data.help" paramText="${(actualPhase.year)!}" className="" helpIcon=false required=true editable=editable /]
  </div>
  
  [#-- Comments/Analysis --]
  <div class="form-group margin-panel">
    [@customForm.textArea name="${customName}.comment" i18nkey="${label}.${id}.comments" help="${label}.${id}.comments.help" className="" helpIcon=false required=true editable=editable /]
  </div>
[/#macro]
