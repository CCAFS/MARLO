[#ftl]
[#macro indicatorInformation name list index id="indicatorID" label="" editable=true]
  <div class="form-group">
    [#local customName = "${name}[${index}]"]
    [#local element = (list[index])!{} ]
    [#-- Hidden Inputs Indicator --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
    <input type="hidden"  name="${customName}.repIndSynthesisIndicator.id" value="${(element.repIndSynthesisIndicator.id)!}"/>
    
    [#-- Data --]
    <div class="form-group margin-panel">
      [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
      [@customForm.textArea name="${customName}.data" i18nkey="${label}.${id}.data" help="${label}.${id}.data.help" paramText="${(actualPhase.year)!}" fieldEmptyText="global.prefilledByPmu" className="" helpIcon=false required=true editable=editable /]
    </div>
    
    [#-- Comments/Analysis --]
    <div class="form-group margin-panel">
      [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
      [@customForm.textArea name="${customName}.comment" i18nkey="${label}.${id}.comments" help="${label}.${id}.comments.help" fieldEmptyText="global.prefilledByPmu" className="" helpIcon=false required=true editable=editable /]
    </div>
  </div>
[/#macro]


[#macro tableFPSynthesis tableName="tableName" list=[] columns=[] crpProgramField="reportSynthesis.liaisonInstitution.crpProgram" showEmptyRows=true showTitle=true showHeader=true]
  <div class="form-group">
    [#if showTitle]
      <h4 class="simpleTitle">[@s.text name="${tableName}.title" /]</h4>
    [/#if]
    <table class="table table-bordered">
      [#if showHeader]
      <thead>
        <tr>
          <th class="col-md-1 text-center"> FP </th>
          [#list columns as column]<th class="text-center"> [@s.text name="${tableName}.column${column_index}" /] </th>[/#list]
        </tr>
      </thead>
      [/#if]
      <tbody>
        [#if list?has_content]
          [#list list as item] 
            [#-- CRP Program --]
            [#local crpProgram = (item)!{} /]
            [#list  (crpProgramField?split(".")) as level][#local crpProgram = (crpProgram[level])!{} /][/#list]
            [#-- Check if there is information available --]
            [#local isEmptyRow = true /]
            [#list columns as column][#if (item[column]?has_content)!false][#local isEmptyRow = false /][#break][/#if][/#list]
            
            [#if (isEmptyRow && showEmptyRows) || (!isEmptyRow) ]
            <tr>
              <td class="col-md-1 text-center">
                <span class="programTag" style="border-color:${(crpProgram.color)!'#fff'}">${(crpProgram.acronym)!}</span>
              </td>
              [#list columns as column]
                <td>
                  [#if (item[column]?has_content)!false] 
                    ${item[column]?replace('\n', '<br>')} 
                  [#else]
                    <i style="opacity:0.5">[@s.text name="global.prefilledByFlagship"/]</i>
                  [/#if]
                </td>
              [/#list]
            </tr>
            [/#if]
          [/#list]
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]