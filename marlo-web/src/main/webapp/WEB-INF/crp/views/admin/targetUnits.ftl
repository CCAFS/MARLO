[#ftl]
[#assign title = "Target units" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = [] /]
[#assign customJS = [ "${baseUrlMedia}/js/superadmin/marloBoard.js","${baseUrlMedia}/js/admin/targetUnits.js" ] /]
[#assign customCSS = [ "${baseUrlMedia}/css/superadmin/superadmin.css" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "targetUnits" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"targetUnits", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="targetUnits.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>

[#include "/WEB-INF/global/pages/generalMessages.ftl" /]

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/admin/menu-admin.ftl" /]
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
             [#if editable]
          <div class="note center">
            If you don’t find the target unit in the list, please <a class="requestPopUp" href=""> click here </a> to request it.
          </div>
           [/#if]
          [#-- Request target unit --]
        </div>
        
        [#if editable]
        [#-- Section Buttons--]
        [#include "/WEB-INF/crp/views/admin/buttons-admin.ftl" /]
        [/#if]
        
        [/@s.form]
        
      </div>
    </div>
  </div>
</section>

[#-- POPUP TO REQUEST A NEW TARGET --]
<div id="popUp"  style="display:none;" >
  <span class="glyphicon glyphicon-remove-circle close-dialog"></span>
  <h4 style="text-align:center;">Request a new Target Unit</h4>
  <hr />
  <div class="col-md-12">
    <div class="col-md-12 note center form-group">
      This request will be sent to MARLOSupport@cgiar.org
    </div>
  </div>
    <div class="col-md-12 form-group">
      [@customForm.input name="newTargetUnit" i18nkey="Write the new target unit" className="newTargetUnit" required=true editable=true /]
    </div>
    <div class="row form-group">
      <div class="col-md-5 form-group text-center pull-right">
        [@s.submit type="button" name="sendRequest"] <span class="glyphicon glyphicon-send"></span>  [@s.text name="Send" /][/@s.submit]
      </div>
    </div>
</div>

[#-- Unit Target Template --]
[@targetUnitMacro element={} name="loggedCrp.targetUnits" index=-1 isTemplate=true /]

[#include "/WEB-INF/crp/pages/footer.ftl" /]

[#macro targetUnitMacro element name index isTemplate=false]
  <li id="targetUnit-${isTemplate?string('template',index)}" class="li-item targetUnitAdmin" style="float:left; width:48%; margin-right:5px; display:${isTemplate?string('none','block')}">
    [#local customName = "${name}[${index}]"/]
    <input type="hidden" class="id" name="${customName}.targetUnit.id" value="${(element.targetUnit.id)!}" />
    <input type="hidden" class="name" name="${customName}.targetUnit.name" value="${(element.targetUnit.name)!}" />
    [#-- Check Input --]
    <span class=" pull-right" > 
     [#if editable]
      <input [#if element?? && element.targetUnit?? && action.canBeDeletedCrptargetUnit((element.targetUnit.id)!, (element.targetUnit.class.name)!)!][#else]style="opacity:0.5; cursor: not-allowed;" onclick="return false;" onkeydown="e = e || window.event; if(e.keyCode !== 9) return false;"[/#if] type="checkbox" value="true" name="${customName}.check" id="" [#if element.check?? && element.check]checked[/#if] />  
     [#else]
      [#if element.check?? && element.check]checked[/#if]
     [/#if] </span>
    [#-- Icon --]
    <span class="glyphicon glyphicon-scale"></span>  
    [#-- Name --]
    <span class="composedName"> ${(element.targetUnit.name)!}</span> <br />
    [#-- CRPs that allow this target --]
    <span class="crps" style="color: #9c9c9c; margin-left: 16px; font-size: 0.75em;" title="CRPs ">
      [#if element.targetUnit?? && element.targetUnit.crpTargetUnits?has_content]
        [#list element.targetUnit.crpTargetUnits as crpTargetUnit]
          [#if crpTargetUnit.active]
          [${crpTargetUnit.crp.acronym}] 
          [/#if]
        [/#list] 
      [/#if]
    </span>
  </li>
[/#macro]