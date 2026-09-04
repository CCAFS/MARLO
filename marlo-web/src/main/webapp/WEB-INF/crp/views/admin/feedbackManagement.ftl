[#ftl]
[#assign title = "Feedback Management" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/admin/feedbackManagement.js?20260826v2",
  "//cdn.datatables.net/1.13.1/js/jquery.dataTables.min.js"
 ] /]
[#assign customCSS = [
  "//cdn.datatables.net/1.13.1/css/jquery.dataTables.min.css",
  "${baseUrlMedia}/css/admin/feedbackManagement.css?20260831"
 ] /]
[#import "/WEB-INF/crp/macros/feedbackFieldRelationsMacro.ftl" as fieldRelations /]
[#assign currentSection = "admin" /]
[#assign currentStage = "feedbackManagement" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"marloBoard"},
  {"label":"feedbackManagement", "nameSpace":"", "action":""}
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
        <h4 class="sectionTitle">[@s.text name="feedbackManagement.title" /]</h4>
        <div class="slos-list">
        [#if feedbackFields?has_content]
          [#list feedbackFields as slo]
            [@feedbackCommentFieldsMacro element=slo name="feedbackFields[${slo_index}]" index=slo_index  /]
          [/#list]
        [/#if]
        </div>
        [#-- Add Outcome Button --]
        <div class="addSlo bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addFeedbackField"/]</div>
        
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
[@feedbackCommentFieldsMacro element={} name="feedbackFields[-1]" index=-1 isTemplate=true /]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro feedbackCommentFieldsMacro element name index isTemplate=false]
  [#-- A field that already has comments cannot be deleted: feedback_qa_comments.field_id is ON DELETE RESTRICT --]
  [#local inUse = !isTemplate && (element.id)?has_content && !action.canDeleteFeedbackField(element.id) /]

  <div id="srfSlo-${isTemplate?string('template',index)}" class="srfSlo borderBox" style="display:${isTemplate?string('none','block')}">
    [#-- Remove Button: disabled while the field is in use --]
    [#if inUse]
      <div class="removeElement disable sm" title="[@s.text name="feedbackManagement.delete.disabledTitle" /]"></div>
    [#else]
      <div class="remove-element removeElement sm" title="Remove"></div>
    [/#if]

    [#-- SLO Title, with the usage counter on the right of the text --]
    <div class="blockTitle closed">
      [#if !isTemplate][@fieldRelations.feedbackFieldRelationsButton element=element labelText=true /][/#if]
      <strong>Feedback Field ${index+1}: </strong>${(element.sectionDescription)!''} - ${(element.fieldName[0..*200])!'Feeckback Fields'}
    </div>

    [#-- Where this field is being commented on. Outside the title so the accordion cannot swallow the modal. --]
    [#if !isTemplate][@fieldRelations.feedbackFieldRelationsModal element=element /][/#if]

    <div class="blockContent" style="display:none">
      <hr />
      [#-- SLO ID  --]
      <input type="hidden" name="${name}.id" value="${(element.id)!}"/>
      [#-- Section name: the slug has to be a ProjectSectionsEnum value, matching the page's
           #sectionNameToFeedback marker, so it is picked from the list instead of typed.
           The options are written here rather than through [@customForm.select] because that macro hands the
           current value to [@s.select] as an OGNL expression, which never matches a string slug, and because a
           stored slug outside the enum -- 'safeguard' for the safeguards page -- has to survive the save instead
           of being silently reset to the placeholder. --]
      [#local currentSection = (element.sectionName)!'' /]
      <div class="form-group">
        <div class="select fieldReference">
          <label for="${name}.sectionName">
            [@s.text name="feedbackManagement.sectionName" /]:[@customForm.req required=true /]
            [@customForm.helpLabel name="feedbackManagement.sectionName.help" showIcon=false /]
          </label>
          <div class="selectList">
            <select id="${name}.sectionName" name="${name}.sectionName" class="sectionName form-control input-sm">
              <option value="-1">[@s.text name="form.select.placeholder" /]</option>
              [#-- Label first, slug in parentheses: the slug is the value that is stored and the one the runtime
                   matches, so it stays visible, but on its own it is not what an administrator recognises. Only the
                   option text changes -- value is still the bare slug. --]
              [#list projectSections as section]
                <option value="${section}"[#if section == currentSection] selected="selected"[/#if]>${action.getProjectSectionLabel(section)} (${section})</option>
              [/#list]
              [#if currentSection?has_content && !projectSections?seq_contains(currentSection)]
                <option value="${currentSection}" selected="selected">${currentSection} [@s.text name="feedbackManagement.sectionName.unknown" /]</option>
              [/#if]
            </select>
          </div>
        </div>
      </div>
      <div class="clearfix"></div>
      [#-- Section description  --]
      <div class="form-group">
        [@customForm.input name="${name}.sectionDescription" i18nkey="feedbackManagement.sectionDescription" help="feedbackManagement.sectionDescription.help" helpIcon=false className="description limitWords-100" required=true /]
      </div>
      <div class="clearfix"></div>      
      [#-- field name  --]
      <div class="form-group">
        [@customForm.input name="${name}.fieldName" i18nkey="feedbackManagement.fieldName" help="feedbackManagement.fieldName.help" helpIcon=false className="description limitWords-100" required=true /]
      </div>
      <div class="clearfix"></div>
      [#-- Field description  --]
      <div class="form-group">
        [@customForm.input name="${name}.fieldDescription" i18nkey="feedbackManagement.fieldDescription" help="feedbackManagement.fieldDescription.help" helpIcon=false className="description limitWords-100" required=true /]
      </div>
      <div class="clearfix"></div>
      [#-- Parent field description  --]
      <div class="form-group">
        [@customForm.input name="${name}.parentFieldDescription" i18nkey="feedbackManagement.parentFieldDescription" help="feedbackManagement.parentFieldDescription.help" helpIcon=false className="description limitWords-100" required=true /]
      </div>
      <div class="clearfix"></div>
        
    </div>
  </div>
[/#macro]
