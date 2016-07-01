[#ftl]
[#assign title = "MARLO Admin" /]
[#assign pageLibs = [] /]
[#assign customJS = [ "${baseUrl}/js/superadmin/marloBoard.js" ] /]
[#assign customCSS = [ "${baseUrl}/css/superadmin/marloBoard.css" ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "marloBoard" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"marloBoard", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
<hr />
<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/superadmin/menu-superadmin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]
        <h4 class="sectionTitle">Target Units</h4>
        <div class="borderBox ">
          [#-- Targets units list --]
          <div class="items-list">
            <ul>
            [#list targetUnitList as targetUnit]
              [@targetUnitMacro element=targetUnit name="targetUnitList" index=targetUnit_index /]
            [/#list]
            </ul>
            [#if !targetUnitList?has_content]<p class="text-center">There is not target units</p>[/#if]
          </div>
          <hr />
          [#-- Add target unit --]
          <div class="row">
            <div class="col-md-3 acronymBlock">[@customForm.input name="" type="text" showTitle=false placeholder="Unit acronym" className="acronym-input" required=true editable=true /]</div>
            <div class="col-md-6 nameBlock">[@customForm.input name="" type="text" showTitle=false placeholder="Unit Name" className="name-input" required=true editable=true /]</div>
            <div class="col-md-3 buttonBlock text-right"><div class="button-blue"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addTarget" /]</div></div>
          </div>
        </div>
        
        [#-- Section Buttons--]
        <div class="buttons">
          <div class="buttons-content">
            [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /][/@s.submit]
          </div>
        </div>
        
        [/@s.form]
      	
      </div>
    </div>
  </div>
</section>


[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro targetUnitMacro element name index isTemplate=false]
  <li id="targetUnit-${isTemplate?string('template',index)}" class="li-item targetUnit" style="display:${isTemplate?string('none','block')}">
    [#local customName = "${name}[${index}]"/]
    <span class="glyphicon glyphicon-scale"></span>  <span class="acronym">(${element.acronym})</span> <span class="name">${element.name}</span>
    <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
    <input type="hidden" name="${customName}.acronym" value="${(element.acronym)!}" />
    <input type="hidden" name="${customName}.name" value="${(element.name)!}" />
    [#-- Remove Button --]
    <span class="glyphicon glyphicon-remove pull-right remove-userItem" aria-hidden="true"></span>
  </li>
[/#macro]