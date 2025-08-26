[#ftl]
[#assign title = "Portfolios Management" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [ "${baseUrlMedia}/js/admin/portfolioManagement.js?20250826"
 ] /]
[#assign customCSS = [ "${baseUrlMedia}/css/admin/portfolioManagement.css"] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "portfolioManagement" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"marloBoard"},
  {"label":"portfolioManagement", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
<hr />

<div class="container">
  [#include "/WEB-INF/global/pages/breadcrumb.ftl" /]
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
        
        [#-- Feedback Permissions --]
        <h4 class="sectionTitle">[@s.text name="portfolioManagement.title" /]</h4>
        
        <div class="slos-list">
        [#if portfolios?has_content]
          [#list portfolios as frp]
            [@feedbackCommentFieldsMacro element=frp name="portfolios[${frp_index}]" index=frp_index  /]
          [/#list]
        [/#if]
        </div>
        [#-- Add Feedback Permission Button --]
        <div class="addSlo bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addPortfolio"/]</div>
        
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

[#-- Feedback Comments fields Template --]
[@feedbackCommentFieldsMacro element={} name="portfolios[-1]" index=-1 isTemplate=true /]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro feedbackCommentFieldsMacro element name index isTemplate=false]
[#assign isNew = (element.id)?has_content?string('','new-entry')]
<div id="srfSlo-${isTemplate?string('template',index)}"
     class="srfSlo borderBox ${isNew} ${isTemplate?string('is-template','')}"
     data-id="${(element.id)!}"
     data-permission-id="${(element.feedbackPermission.id)!}"
     style="display:${isTemplate?string('none','block')}">

    [#-- Remove Button --]
    <div class="remove-element removeElement sm" title="Remove"></div>
    
    [#-- Description --]
    <div class="blockTitle closed">
      <strong>Portfolio ${index+1}:</strong> ${(element.name)!''} 
    </div>
    
    <div class="blockContent" style="display:none">
      <hr />
      [#-- feedback role permission ID  --]
      <input type="hidden" name="${name}.id" value="${(element.id)!}"/>
      [#--  feedback role Description  --]
      <div class="form-group">
        [@customForm.input name="${name}.name" i18nkey="portfolioManagement.name" className="description" required=true /]
      </div>
      [#--  Dates  --]
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
      <div class="clearfix"></div>
        
    [#-- Phases  --]
    <div class="countriesBlock form-group" title="Select Phases clicking here">
      [#-- Phases List --]
      [@customForm.select name="${name}.selectedPhases" label=""  i18nkey="portfolioManagement.associatedPhases" listName="phases" keyFieldName="id"  displayFieldName="composedName" value="${name}.selectedPhases" multiple=true required=true  className="countriesSelect form-control input-sm" /]              
    </div>
        
    </div>
  </div>
[/#macro]
