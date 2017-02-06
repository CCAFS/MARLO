[#ftl]
[#assign title = "MARLO Publication" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-${(deliverable.id)!}" /]
[#assign pageLibs = ["select2", "blueimp-file-upload"] /]
[#assign customJS = ["${baseUrl}/js/global/fieldsValidation.js","${baseUrl}/js/publications/publication.js" ] /] [#-- , "${baseUrl}/js/global/autoSave.js" --]
[#assign customCSS = [ ] /]
[#assign currentSection = "publications" /]

[#assign breadCrumb = [
  {"label":"publicationsList", "nameSpace":"/publications", "action":"publicationsList"},
  {"label":"publication", "nameSpace":"/publications", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/deliverableMacros.ftl" as deliverableMacros /]

[#assign customName = "deliverable" /]

    
<section class="container">
  <div class="col-md-1"></div>
  [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
  <div class="col-md-10">
    [#-- Messages --]
    [#--  --include "/WEB-INF/views/fundingSources/messages-fundingSource.ftl" /--]
    
    <h3 class="headTitle"> Publication Information</h3> 
    <div class="borderBox">
      [#-- Title --]
      <div class="form-group">
        [@customForm.input name="${customName}.title" i18nkey="publication.title" required=true className="" editable=editable /]
      </div>
      
      [#-- Subtype & year of completition --]
      <div class="form-group row">
        <div class="col-md-6">
          [@customForm.select name="${customName}.deliverableType.deliverableType.id" i18nkey="publication.subType" label="" listName="deliverableSubTypes" keyFieldName="id"  displayFieldName="name" required=true  className="" editable=editable/]
        </div>
        <div class="col-md-6">
          [@customForm.input name="${customName}.year" i18nkey="publication.year" required=true className="" editable=false /]
        </div>
      </div>
      
      [#-- Description --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.description" i18nkey="publication.description" required=true className="" editable=editable /]
      </div>
      
      [#-- Lead partner(s) --]
      <div class="form-group">
        <div class="panel tertiary">
         <div class="panel-head"><label for=""> [@customForm.text name="publication.leadPartners" readText=!editable /]:[@customForm.req required=editable /]</label></div>
          <div id="leadPartnerList" class="panel-body"> 
            <ul class="list">
            [#if  publication?? && deliverable.institutions?has_content]
              [#list deliverable.institutions as institutionLead]
                [@leadPartnerMacro element=institutionLead name="${customName}.institutions" index=institutionLead_index /]
              [/#list]
            [/#if]
            </ul>
            [#if editable ]
              [@customForm.select name="" label=""  showTitle=false listName="institutions" keyFieldName="id" displayFieldName="composedName" required=true className="" editable=editable /]
            [/#if] 
          </div>
        </div>
      </div>
      
      [#-- Flagships & Regions --]
      <div class="form-group row">
        [#-- Flagships --]
        <div class="col-md-6">
          <h5>[@s.text name="publication.flagships" /]:[@customForm.req required=editable/] </h5>
          <div id="" class="dottedBox">
            [#if editable]
              [@s.fielderror cssClass="fieldError" fieldName="${customName}.flagshipValue"/]
              [@s.checkboxlist name="${customName}.flagshipValue" list="programFlagships" listKey="id" listValue="composedName" cssClass="checkboxInput fpInput"  value="flagshipIds" /]
            [#else]
              <input type="hidden" name="${customName}.flagshipValue" value="${(deliverable.flagshipValue)!}"/>
              [#if publication?? && deliverable.flagships?has_content]
                [#list deliverable.flagships as element]<p class="checked">${(element.composedName)!'null'}</p>[/#list]
              [/#if]
            [/#if]
          </div>
        </div>
        [#-- Regions --] 
        <div class="col-md-6"> 
          [#if regionFlagships?has_content] 
            <h5>[@s.text name="${customName}.regions" /]:[@customForm.req required=editable /]</h5>
            <div id="" class="dottedBox">
              [#if editable]
                [@s.fielderror cssClass="fieldError" fieldName="${customName}.regionsValue"/]
                [@s.checkboxlist name="${customName}.regionsValue" list="regionFlagships" listKey="id" listValue="composedName" cssClass="checkboxInput rpInput" value="regionsIds" /]
              [#else] 
                <input type="hidden" name="${customName}.regionsValue" value="${(deliverable.regionsValue)!}"/>
                [#if publication?? && deliverable.regions?has_content]
                  [#list deliverable.regions as element]<p class="checked">${(element.composedName)!'null'}</p>[/#list]
                [/#if]
              [/#if]
            </div>
          [/#if]
        </div>
      </div>
      
      [#-- Cross cutting dimensions--]
      <div class="form-group">
        <label for="">[@customForm.text name="${customName}.crossCuttingDimensions" readText=!editable/]:[@customForm.req required=editable/]</label>
        <div class="col-md-12">
          [#if editable]
            <label class="checkbox-inline"><input type="checkbox" name="${customName}.crossCuttingGender"   id="gender"   value="true" [#if (deliverable.crossCuttingGender)!false ]checked="checked"[/#if]> Gender</label>
            <label class="checkbox-inline"><input type="checkbox" name="${customName}.crossCuttingYouth"    id="youth"    value="true" [#if (deliverable.crossCuttingYouth)!false ]checked="checked"[/#if]> Youth</label>
            <label class="checkbox-inline"><input type="checkbox" name="${customName}.crossCuttingCapacity" id="capacity" value="true" [#if (deliverable.crossCuttingCapacity)!false ]checked="checked"[/#if]> Capacity Development</label>
            <label class="checkbox-inline"><input type="checkbox" name="${customName}.crossCuttingNa"       id="na"       value="true" [#if (deliverable.crossCuttingNa)!false ]checked="checked"[/#if]> N/A</label>
          [#else]
            [#if (deliverable.crossCuttingGender)!false ] <p class="checked"> Gender</p>[/#if]
            [#if (deliverable.crossCuttingYouth)!false ] <p class="checked"> Youth</p>[/#if]
            [#if (deliverable.crossCuttingCapacity)!false ] <p class="checked"> Capacity Development</p>[/#if]
            [#if (deliverable.crossCuttingNa)!false ] <p class="checked"> N/A</p>[/#if]
          [/#if]
        </div>
        <div class="clearfix"></div>
      </div>
      [#-- If gender dimension, select with ones --]
      <div id="gender-levels" class="panel tertiary" style="display:${((deliverable.crossCuttingGender)!false)?string('block','none')}">
       <div class="panel-head"><label for=""> [@customForm.text name="${customName}.genderLevels" readText=!editable /]:[@customForm.req required=editable /]</label></div>
        <div id="genderLevelsList" class="panel-body" > 
          <ul class="list">
          [#if publication?? && deliverable.genderLevels?has_content]
            [#list deliverable.genderLevels as element]
              [@crossDimmensionMacro element=element name="${customName}.genderLevels" index=element_index /]
            [/#list]
          [#else]
            <p class="emptyText"> [@s.text name="deliverable.genderLevels.empty" /]</p> 
          [/#if]  
          </ul>
          [#if editable ][@customForm.select name="" label="" showTitle=false i18nkey="" listName="genderLevels"   required=true  className="genderLevelsSelect" editable=editable/][/#if] 
        </div>
      </div>
    </div>
    
    <h3 class="headTitle"> Publication Dissemination</h3> 
    <div class="borderBox"> 
      [#-- Dissemination channel & URL --]
      <div class="simpleBox">
        [@deliverableMacros.findableOptions /]
      </div>
      
      [#-- Is this deliverable Open Access? --]
      [@deliverableMacros.isOpenaccessMacro /]
      
      [#-- Have you adopted a license?  --]
      [@deliverableMacros.deliverableLicenseMacro/]
    </div>
    
    <h3 class="headTitle"> Publication Metadata</h3> 
    <div class="borderBox">
      [#-- Metadata (included publications) --]
      [@deliverableMacros.deliverableMetadataMacro /]
    </div> 
    
    [#-- Section Buttons & hidden inputs--]
    [#include "/WEB-INF/views/publications/buttons-publications.ftl" /]
  </div>
  [/@s.form]
  <div class="col-md-1"></div>
</section>

[#-- Author template --]
[@authorMacro element={} index="-1" name="deliverable.authors"  isTemplate=true /]


[#include "/WEB-INF/global/pages/footer.ftl"]

[#-- Metadata Macro --]
[#macro metadataField title="" encodedName="" type="input" list="" require=false]
  [#local metadataID = (deliverable.getMetadataID(encodedName))!-1 /]
  [#local metadataIndex = (deliverable.getMetadataIndex(encodedName))!-1 /]
  [#local metadataValue = (deliverable.getMetadataValue(metadataID))!'' /]
  <input type="hidden" name="${customName}.metadataElements[${metadataIndex}].id" value="${metadataID}" />
  <input type="hidden" name="${customName}.metadataElements[${metadataIndex}].metadataElement.id" value="${metadataID}" />
  [#if type == "input"]
    [@customForm.input name="${customName}.metadataElements[${metadataIndex}].elementValue" required=require value="${metadataValue}" className="${title}Metadata"  type="text" i18nkey="metadata.${title}" help="metadata.${title}.help" editable=editable/]
  [#elseif type == "textArea"]
    [@customForm.textArea name="${customName}.metadataElements[${metadataIndex}].elementValue" required=require value="${metadataValue}" className="${title}Metadata" i18nkey="metadata.${title}" help="metadata.${title}.help" editable=editable/]
  [#elseif type == "select"]
    [@customForm.select name="${customName}.metadataElements[${metadataIndex}].elementValue" required=require value="${metadataValue}" className="${title}Metadata" i18nkey="metadata.${title}" listName=list  editable=editable /]
  [/#if]
[/#macro]

[#macro authorMacro element index name  isTemplate=false]
  [#assign customName = "${name}[${index}]" /]
  <div id="author-${isTemplate?string('template',(element.id)!)}" class="author  simpleBox col-md-4"  style="display:${isTemplate?string('none','block')}">
    [#if editable] [#--&& (isTemplate) --]
      <div class="removeLink">
        <div class="removeAuthor removeIcon" title="Remove author/creator"></div>
      </div>
    [/#if]
    <div class="row">
      <div class="col-md-12"><span class="lastName">${(element.lastName)!} </span>, <span class="firstName">${(element.firstName)!} </span></div>
    </div>
    <span><small class="orcidId"> ${(element.elementId)!'<b>orcid id:</b> not filled'}</small></span>
    <input type="hidden" class="id" value="${(element.id)!}" />
    <input type="hidden" class="lastNameInput" value="${(element.lastName)!}" />
    <input type="hidden" class="firstNameInput" value="${(element.firstName)!}" />
    <input type="hidden" class="orcidIdInput" value="${(element.elementId)!}" />
    <div class="clearfix"></div>
  </div>
[/#macro]

[#macro leadPartnerMacro element name index isTemplate=false]
  <li id="leadPartner-${isTemplate?string('template', index)}" class="leadPartner" style="display:${isTemplate?string('none','block')}">
    [#local customName = "${name}[${index}]" /]
    [#-- Remove Button --]
    [#if editable]<div class="removeLeadPartner removeIcon" title="Remove Lead partner"></div>[/#if]
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}" />
    <input class="fId" type="hidden" name="${customName}.institution.id" value="${(element.institution.id)!}" />
    <span class="name">${(element.institution.composedName)!}</span>
  </li>
[/#macro]

[#macro crossDimmensionMacro element name index isTemplate=false]
  [#local customName = "${name}[${index}]" /]
  <li id="crossDimmension-${isTemplate?string('template', index)}" class="crossDimmension" style="display:${isTemplate?string('none','block')}">
    [#if editable]<div class="removeGenderLevel removeIcon" title="Remove Gender Level"></div>[/#if] 
    <input class="id" type="hidden" name="deliverable.genderLevels[${element_index}].id" value="${(element.id)!}" />
    <input class="fId" type="hidden" name="deliverable.genderLevels[${element_index}].genderLevel" value="${(element.genderLevel)!}" />
    <span title="${(element.nameGenderLevel)!'undefined'}" class="name">[@utils.wordCutter string=(element.nameGenderLevel)!"undefined" maxPos=100 substr=" "/]</span>
    <div class="clearfix"></div>
  </li>
[/#macro]
