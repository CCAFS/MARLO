[#ftl]
[#assign title = "MARLO Admin" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = [] /]
[#assign customJS = [ "${baseUrlMedia}/js/superadmin/marloBoard.js" ] /]
[#assign customCSS = [ "${baseUrlMedia}/css/superadmin/superadmin.css" ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "marloBoard" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"marloBoard", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/center//global/pages/header.ftl" /]
<hr />

<div class="container">
  [#include "/WEB-INF/center//global/pages/breadcrumb.ftl" /]
</div>
[#include "/WEB-INF/center//global/pages/generalMessages.ftl" /]

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/center//views/superadmin/menu-superadmin.ftl" /]
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
            <div class="clearfix"></div>
          </div>
          <hr />
          [#-- Add target unit --]
          <div class="row ">
            <div class="col-md-9">[@customForm.input name="" type="text" showTitle=false placeholder="Target Unit Name" className="name-input" required=true editable=true /]</div>
            <div class="col-md-3 text-right"><div class="addTargetUnit button-blue"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addTarget" /]</div></div>
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


[#-- Unit Target Template --]
[@targetUnitMacro element={} name="targetUnitList" index=-1 isTemplate=true /]

[#include "/WEB-INF/center//global/pages/footer.ftl" /]

[#macro targetUnitMacro element name index isTemplate=false]
  <li id="targetUnit-${isTemplate?string('template',index)}" class="li-item targetUnit" style="display:${isTemplate?string('none','block')}">
    [#local customName = "${name}[${index}]"/]
    <span class="glyphicon glyphicon-scale"></span>  <span class="composedName"> ${(element.name)!}</span>
    <input type="hidden" class="id" name="${customName}.id" value="${(element.id)!}" />
    <input type="hidden" class="acronym" name="${customName}.acronym" value="${(element.acronym)!}" />
    <input type="hidden" class="name" name="${customName}.name" value="${(element.name)!}" />
    [#-- Remove Button --]
    <span class="glyphicon glyphicon-remove pull-right remove-targetUnit" aria-hidden="true"></span>
  </li>
[/#macro]