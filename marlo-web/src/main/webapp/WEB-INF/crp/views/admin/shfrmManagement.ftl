[#ftl]
[#assign title = "SHFRM Management" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [] /]
[#assign customJS = [ 
  "${baseUrlMedia}/js/admin/shfrmManagement.js",
  "${baseUrlCdn}/global/js/fieldsValidation.js" 
  ] 
/]
[#assign customCSS = [ "${baseUrlMedia}/css/admin/crpPhases.css" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "shfrmManagement" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"adminManagement"},
  {"label":"shfrmManagement", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
<hr />

<div class="container">
  [#include "/WEB-INF/global/pages/breadcrumb.ftl" /]
</div>
[#include "/WEB-INF/global/pages/generalMessages.ftl" /]

[#-- 
<div class="animated flipInX container  viewMore-block containerAlertMargin">
  <div class=" containerAlert  alert-leftovers alertColorBackgroundInfo"  id="containerAlert">
    <div class="containerLine alertColorInfo"></div>
    <div class="containerIcon">
      <div class="containerIcon alertColorInfo">
        <img src="${baseUrlCdn}/global/images/icon-exclamation.png" />      
      </div>
    </div>
   <div class="containerText col-md-12">
      <p class="alertText">
        [@s.text name="Please note that activities are displayed on the homepage timeline component in the order they are entered or in the order defined by the 'order' field, if its filled. By default, the dates of the activities do not determine their order." /] 
      </p>
    </div>   
    <div class="viewMoreCollapse closed"></div>  
  </div>
</div>
 --]
 
<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]
        
        [#-- System Level Outcomes --]
        <h4 class="sectionTitle">[@s.text name="shfrmManagement.title" /]</h4>
        <div class="slos-list">
        [#if priorityActions?has_content]
          [#list priorityActions as slo]
            [@srfSloMacro element=slo name="priorityActions[${slo_index}]" index=slo_index  /]
          [/#list]
        [/#if]
        </div>
        [#-- Add Outcome Button --]
        <div class="addSlo bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="shfrmManagement.priorityActions.add"/]</div>
        
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

[#-- SLO Template --]
[@srfSloMacro element={} name="priorityActions[-1]" index=-1 isTemplate=true /]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro srfSloMacro element name index isTemplate=false]
  <div id="srfSlo-${isTemplate?string('template',index)}" class="srfSlo borderBox" style="display:${isTemplate?string('none','block')}">
    [#-- Remove Button --]
    <div class="remove-element removeElement sm" title="Remove"></div>
    
    [#-- SLO Title --]
    <div class="blockTitle closed">
      <strong>${(element.name)!'Shfrm Priority Action'} ${index+1}: </strong>${(element.description)!'Shfrm Priority Action'}
    </div>
    
    <div class="blockContent" style="display:none">
      <hr />
      [#-- SLO ID  --]
      <input type="hidden" name="${name}.id" value="${(element.id)!}"/>
      [#-- Section name  --]
      <div class="form-group">
        [@customForm.input name="${name}.name" i18nkey="shfrmManagement.subActions.title" className="description limitWords-100" required=true /]
      </div>
      <div class="clearfix"></div>
      [#-- Section description  --]
      <div class="form-group">
        [@customForm.input name="${name}.description" i18nkey="shfrmManagement.subActions.description" className="description limitWords-100" required=true /]
      </div>
      <div class="clearfix"></div>      
        
    </div>
  </div>
[/#macro]
