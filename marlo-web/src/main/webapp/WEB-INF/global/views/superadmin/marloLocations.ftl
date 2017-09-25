[#ftl]
[#assign title = "MARLO Admin" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = [] /]
[#assign customJS = [ "${baseUrlMedia}/js/superadmin/marloLocations.js" ] /]
[#assign customCSS = [ "${baseUrlMedia}/css/superadmin/superadmin.css" ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "customLocations" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"customLocations"},
  {"label":"customLocations", "nameSpace":"", "action":""}
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
        [#include "/WEB-INF/global/views/superadmin/menu-superadmin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]
        <h4 class="sectionTitle"> Locations </h4>
        <div class="borderBox ">
          [#-- Targets units list --]
          <div class="items-list">
            <ul>
            [#list locElementTypeList as element]
              [@locElementTypeMacro element=element name="locElementTypeList" index=element_index /]
            [/#list]
            </ul>
            [#if !locElementTypeList?has_content]<p class="text-center">There is not target units</p>[/#if]
            <div class="clearfix"></div>
          </div>
          <hr />
          [#-- Add target unit --]
          <div class="row ">
            <div class="col-md-9">[@customForm.input name="" type="text" showTitle=false placeholder="Loaction Name" className="name-input" required=true editable=true /]</div>
            <div class="col-md-3 text-right"><div class="add-locElementType button-blue"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addLocationLevel" /]</div></div>
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
[@locElementTypeMacro element={} name="locElementTypeList" index=-1 isTemplate=true canDelete=true /]

[#include "/WEB-INF/crp/pages/footer.ftl" /]

[#macro locElementTypeMacro element name index isTemplate=false canDelete=false]
  <li id="locElementType-${isTemplate?string('template',index)}" class="li-item locElementType" style="display:${isTemplate?string('none','block')}">
    [#local customName = "${name}[${index}]"/]
    [#-- Remove Button --]
    [#if canDelete || (element?? && action.canBeDeleted((element.id)!, (element.class.name)!)!)]
      <span class="glyphicon glyphicon-remove pull-right remove-locElementType" aria-hidden="true"></span>
    [#else]
      <span class="glyphicon glyphicon-remove pull-right " style="color:#ccc" aria-hidden="true" title="Can not be deleted"></span>
    [/#if]
    [#-- Hidden Inputs --]
    <input type="hidden" class="id" name="${customName}.id" value="${(element.id)!}" />
    <input type="hidden" class="name" name="${customName}.name" value="${(element.name)!}" />
    [#-- Name --]
    <span class="glyphicon glyphicon-map-marker"></span>  <span class="composedName"> ${(element.name)!}</span>
    <br />
    <span class="crps" style="color: #9c9c9c; margin-left: 16px; font-size: 0.75em;" title="CRPs ">
      [#if element?? && element.crpLocElementTypes?has_content]
        [#list element.crpLocElementTypes as crpLocElementType]
          [#if crpLocElementType.active][${crpLocElementType.crp.acronym}] [/#if]
        [/#list] 
      [/#if]
    </span>
  </li>
[/#macro]