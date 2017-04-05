[#ftl]
[#assign title = "Target units" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = [] /]
[#assign customJS = [ "${baseUrl}/js/superadmin/marloBoard.js" ] /]
[#assign customCSS = [ "${baseUrl}/css/superadmin/superadmin.css" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "targetUnits" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"targetUnits", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/images/global/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="targetUnits.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>

[#include "/WEB-INF/global/pages/generalMessages.ftl" /]

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]
        <h4 class="sectionTitle">Custom Target Units</h4>
        <label for="">List of target units:</label>
        <div class="borderBox ">
          [#-- Targets units list --]
          <div class="items-list">
            [#if loggedCrp.targetUnits?has_content]
            <ul>
            [#list loggedCrp.targetUnits as targetUnit]
              [@targetUnitMacro element=targetUnit name="loggedCrp.targetUnits" index=targetUnit_index /]
            [/#list]
            </ul>
            [#else]
            <p class="text-center">There is not target units</p>
            [/#if]
            <div class="clearfix"></div>
          </div>
          <hr />
          <div class="note center">
            If you don’t find the target unit in the list, please <a href=""> click here </a> to request it.
          </div>
          [#-- Request target unit --]
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
[@targetUnitMacro element={} name="loggedCrp.targetUnits" index=-1 isTemplate=true /]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro targetUnitMacro element name index isTemplate=false]
  <li id="targetUnit-${isTemplate?string('template',index)}" class="li-item targetUnitAdmin" style="float:left; width:48%; margin-right:5px; display:${isTemplate?string('none','block')}">
    [#local customName = "${name}[${index}]"/]
    <span class="glyphicon glyphicon-scale"></span>  <span class="composedName"> ${(element.targetUnit.name)!}</span>
    <input type="hidden" class="id" name="${customName}.targetUnit.id" value="${(element.targetUnit.id)!}" />
    <input type="hidden" class="name" name="${customName}.targetUnit.name" value="${(element.targetUnit.name)!}" />
    [#-- Remove Button --]
    <span class=" pull-right" > <input type="checkbox" value="true" name="${customName}.check" id="" [#if element.check?? && element.check]checked[/#if]/></span>
  </li>
[/#macro]