[#ftl]
[#assign title = "Feedback Management" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [] /]
[#assign customJS = [ "${baseUrlCdn}/global/js/superadmin/buttonGuideManagement.js",  "${baseUrlCdn}/global/js/fieldsValidation.js"
 ] /]
[#assign customCSS = [ "${baseUrlCdn}/global/css/superadmin/superadmin.css" ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "buttonGuideManagement" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"buttonGuideManagement", "nameSpace":"", "action":""}
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
        [#include "/WEB-INF/global/views/superadmin/menu-superadmin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]
        
        [#-- System Level Outcomes --]
        <h4 class="sectionTitle">[@s.text name="buttonGuideManagement.title" /]</h4>
        <div class="slos-list">
        [#if buttonGuideContents?has_content]
          [#list buttonGuideContents as slo]
            [@srfSloMacro element=slo name="buttonGuideContents[${slo_index}]" index=slo_index  /]
          [/#list]
        [/#if]
        </div>
        [#-- Add Outcome Button --]
        <div class="addSlo bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addButtonGuideContents"/]</div>
        
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
[@srfSloMacro element={} name="buttonGuideContents[-1]" index=-1 isTemplate=true /]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro srfSloMacro element name index isTemplate=false]
  <div id="srfSlo-${isTemplate?string('template',index)}" class="srfSlo borderBox" style="display:${isTemplate?string('none','block')}">
    [#-- Remove Button --]
    <div class="remove-element removeElement sm" title="Remove"></div>
    
    [#-- SLO Title --]
    <div class="blockTitle closed">
      <strong>Button Guide Content ${index+1}: </strong>${(element.sectionName)!''}
    </div>
    
    <div class="blockContent" style="display:none">
      <hr />
      [#-- SLO ID  --]
      <input type="hidden" name="${name}.id" value="${(element.id)!}"/>
      [#-- action name  --]
      <div class="form-group">
        [@customForm.input name="${name}.actionName" i18nkey="buttonGuideManagement.actionName" className="description limitWords-100" required=true /]
      </div>
      <div class="clearfix"></div>
      [#-- Section name  --]
      <div class="form-group">
        [@customForm.input name="${name}.sectionName" i18nkey="buttonGuideManagement.sectionName" className="description limitWords-100" required=true /]
      </div>
      <div class="clearfix"></div>      
      [#-- Content  --]
      <div class="form-group">
        [@customForm.input name="${name}.content" i18nkey="buttonGuideManagement.content" className="description limitWords-100" required=true /]
      </div>
      <div class="clearfix"></div>
      [#-- Identifier  --]
      <div class="form-group">
        [@customForm.input name="${name}.identifier" i18nkey="buttonGuideManagement.identifier" className="description limitWords-100" required=true /]
      </div>
      <div class="clearfix"></div>
        
    </div>
  </div>
[/#macro]
