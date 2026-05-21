[#ftl]
[#assign title = "Timeline Management" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [] /]
[#assign customJS = [ "${baseUrlMedia}/js/admin/timelineManagement.js?20260521",  "${baseUrlCdn}/global/js/fieldsValidation.js"
 ] /]
[#assign customCSS = [ "${baseUrlMedia}/css/admin/timeline.css",  "https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/css/select2.min.css",
  "https://cdnjs.cloudflare.com/ajax/libs/select2-bootstrap-theme/0.1.0-beta.10/select2-bootstrap.min.css" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "timelineManagement" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"timelineManagement", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
<hr />

<div class="container">
  [#include "/WEB-INF/global/pages/breadcrumb.ftl" /]
</div>
[#include "/WEB-INF/global/pages/generalMessages.ftl" /]

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
        [@s.text name="timelineManagement.help" /] 
      </p>
    </div>
    <div  class="viewMoreCollapse closed"></div>
  </div>
</div>

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]
        
        [#-- System Level Outcomes --]
        <h4 class="sectionTitle">[@s.text name="timeline.title" /]</h4>
        <div class="slos-list">
        [#if timelineActivities?has_content]
          [#list timelineActivities as slo]
            [@srfSloMacro element=slo name="timelineActivities[${slo_index}]" index=slo_index  /]
          [/#list]
        [/#if]
        </div>
        [#-- Add Outcome Button --]
        <div class="addSlo bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addTimelineActivity"/]</div>
        
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
[@srfSloMacro element={} name="timelineActivities[-1]" index=-1 isTemplate=true /]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro srfSloMacro element name index isTemplate=false]
  <div id="srfSlo-${isTemplate?string('template',index)}" class="srfSlo borderBox" style="display:${isTemplate?string('none','block')}">
    [#-- Remove Button --]
    <div class="remove-element removeElement sm" title="Remove"></div>
    
    [#-- Timeline Activity Title --]
		<div class="blockTitle closed">
		  <strong>Timeline Activity ${index + 1}: </strong>
		  [#if element.description?has_content]
		    ${ element.description?substring(0, (element.description?length > 99)?then(99, element.description?length)) }...
		  [#else]
		    Timeline Activity
		  [/#if]
		</div>
    
    <div class="blockContent" style="display:none">
      <hr />
      [#-- SLO ID  --]
      <input type="hidden" name="${name}.id" value="${(element.id)!}"/>
            
      [#-- Description  --]
      <div class="form-group">
        [@customForm.textArea name="${name}.description" i18nkey="timeline.description" className="description limitWords-100" required=true /]
      </div>
      <div class="clearfix"></div>
    
      <div class="form-group row">
        [#-- Start Date --]
         <div class="col-md-6">
            [@customForm.input name="${name}.startDate" className="startDate" i18nkey="project.startDate" type="text"  /]
         </div>
        [#-- End Date --]
         <div class="col-md-6">
            [@customForm.input name="${name}.endDate" className="endDate"  i18nkey="project.endDate" type="text"  /]
         </div>
      </div>
      <br>
      <hr>
      <div class="clearfix"></div>
      <div style="width: 20%;">
        [@customForm.input name="${name}.order" type="number" i18nkey="Order" placeholder="Numeric value (optional)" required=false /]
      </div>
    
    </div>
  </div>
[/#macro]
