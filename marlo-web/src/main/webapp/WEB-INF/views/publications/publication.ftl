[#ftl]
[#assign title = "MARLO Publication" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-${(deliverable.id)!}" /]
[#assign pageLibs = ["select2","font-awesome","blueimp-file-upload","jsUri"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/publications/publication.js",
  "${baseUrlMedia}/js/projects/deliverables/deliverableQualityCheck.js", 
  "${baseUrlMedia}/js/projects/deliverables/deliverableInfo.js",
  "${baseUrlMedia}/js/projects/deliverables/deliverableDissemination.js", 
  "${baseUrlMedia}/js/global/autoSave.js",
  "${baseUrlMedia}/js/global/fieldsValidation.js"
] /]
[#assign customCSS = [ "${baseUrlMedia}/css/publications/publication.css", "${baseUrlMedia}/css/projects/projectDeliverable.css" ] /]
[#assign currentSection = "publications" /]

[#assign breadCrumb = [
  {"label":"publicationsList", "nameSpace":"publications", "action":"${crpSession}/publicationsList"},
  {"label":"publication", "nameSpace":"publications", "action":""}
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
    [#include "/WEB-INF/views/publications/messages-publications.ftl" /]
    
    [#-- Back --]
    <small class="pull-left">
      <a href="[@s.url action='${crpSession}/publicationsList'][/@s.url]">
        <span class="glyphicon glyphicon-circle-arrow-left"></span> Back to publications
      </a>
    </small>
    
    [#-- FAIR Compliant Mini --]
    <div class="fairComplian-block" style="">
      <div class="pull-right">
        [#-- Findable --] 
        <div class="fairCompliant mini findable [#attempt][#if action.isF(deliverable.id)??][#if action.isF(deliverable.id)] achieved [#else] not-achieved [/#if][/#if][#recover][/#attempt]"><div class="sign">F</div></div>
        [#-- Accessible --] 
        <div class="fairCompliant mini accessible [#attempt][#if action.isA(deliverable.id)??][#if action.isA(deliverable.id)] achieved [#else] not-achieved [/#if][/#if][#recover][/#attempt]"><div class="sign">A</div></div>
        [#-- Interoperable --] 
        <div class="fairCompliant mini interoperable [#attempt][#if action.isI(deliverable.id)??][#if action.isI(deliverable.id)] achieved [#else] not-achieved [/#if][/#if][#recover][/#attempt]"><div class="sign">I</div></div>
        [#-- Reusable --] 
        <div class="fairCompliant mini reusable [#attempt][#if action.isR(deliverable.id)??][#if action.isR(deliverable.id)] achieved [#else] not-achieved [/#if][/#if][#recover][/#attempt]"><div class="sign">R</div></div> 
      </div>
    </div>
    
    <br />
    
    <h3 class="headTitle"> Publication Information</h3> 
    <div class="borderBox">
      [#-- Title --]
      <div class="form-group">
        [@customForm.input name="${customName}.title" i18nkey="publication.title" required=true className="" editable=editable /]
      </div>
      
      [#-- Subtype & year of completition --]
      <div class="form-group row">
        <div class="col-md-6">
          [@customForm.select name="${customName}.deliverableType.id" i18nkey="publication.subType" label="" listName="deliverableSubTypes" keyFieldName="id"  displayFieldName="name" required=true  className="" editable=editable/]
        </div>
        <div class="col-md-6">
          [@customForm.input name="${customName}.year" i18nkey="publication.year" required=true className="" editable=false /]
        </div>
      </div>
      
      [#-- Lead partner(s) --]
      <div class="form-group">
        <div class="panel tertiary">
         <div class="panel-head"><label for=""> [@customForm.text name="publication.leadPartners" readText=!editable /]:[@customForm.req required=editable /]</label></div>
          <div id="leadPartnerList" class="panel-body"  listname="deliverable.leaders" style="position: relative;"> 
            <ul class="list">
            [#if  deliverable?? && deliverable.leaders?has_content]
              [#list deliverable.leaders as institutionLead]
                [@leadPartnerMacro element=institutionLead name="${customName}.leaders" index=institutionLead_index /]
              [/#list]
            [/#if]
            </ul>
            [#if editable ]
              [@customForm.select name="" label=""  showTitle=false listName="institutions"  required=true className="leadPartnersSelect" editable=editable /]
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
              [@s.checkboxlist name="${customName}.flagshipValue" list="programs" cssClass="checkboxInput fpInput"  value="flagshipIds" /]
            [#else]
              <input type="hidden" name="${customName}.flagshipValue" value="${(deliverable.flagshipValue)!}"/>
              [#if deliverable.programs?has_content]
                [#list deliverable.programs as element]<p class="checked">${(element.ipProgram.composedName)!'null'}</p>[/#list]
              [/#if]
            [/#if]
          </div>
        </div>
        [#-- Regions --] 
        <div class="col-md-6"> 
          [#if regions?has_content] 
            <h5>[@s.text name="publication.regions" /]:[@customForm.req required=editable /]</h5>
            <div id="" class="dottedBox">
              [#if editable]
                [@s.fielderror cssClass="fieldError" fieldName="${customName}.regionsValue"/]
                [@s.checkboxlist name="${customName}.regionsValue" list="regions" cssClass="checkboxInput rpInput" value="regionsIds" /]
              [#else] 
                <input type="hidden" name="${customName}.regionsValue" value="${(deliverable.regionsValue)!}"/>
                [#if  deliverable.regions?has_content]
                  [#list deliverable.regions as element]<p class="checked">${(element.ipProgram.composedName)!'null'}</p>[/#list]
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
        <div id="genderLevelsList" class="panel-body"  listname="deliverable.genderLevels" style="position: relative;" > 
          <ul class="list">
          [#if deliverable?? && deliverable.genderLevels?has_content]
            [#list deliverable.genderLevels as element]
              [@genderLevelMacro element=element name="${customName}.genderLevels" index=element_index /]
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
        <div class="findable"><input type="hidden" name="${customName}.dissemination.alreadyDisseminated" value="true"/></div>
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

[#-- Lead parter template --]
[@leadPartnerMacro element={} name="${customName}.leaders" index=-1 isTemplate=true /]

[#-- Gender list template --]
[@genderLevelMacro element={} name="${customName}.genderLevels" index=-1 isTemplate=true /]

[#-- CRP & Flagships template --]
[@deliverableMacros.flagshipMacro element={} index=-1 name="${customName}.crps"  isTemplate=true /]

[#-- Author template --]
[@deliverableMacros.authorMacro element={} index="-1" name="${customName}.users"  isTemplate=true /]


[#include "/WEB-INF/global/pages/footer.ftl"]

[#macro leadPartnerMacro element name index isTemplate=false]
  <li id="leadPartner-${isTemplate?string('template', index)}" class="leadPartner" style="display:${isTemplate?string('none','block')}">
    [#local customName = "${name}[${index}]" /]
    [#-- Remove Button --]
    [#if editable]<div class="removeLeadPartner removeIcon" title="Remove Lead partner"></div>[/#if]
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}" />
    <input class="fId" type="hidden" name="${customName}.institution.id" value="${(element.institution.id)!}" />
    <span class="name">${(element.institution.composedName)!'undefined'}</span>
    <div class="clearfix"></div>
  </li>
[/#macro]

[#macro genderLevelMacro element name index isTemplate=false]
  [#local customName = "${name}[${index}]" /]
  <li id="genderLevel-${isTemplate?string('template', index)}" class="genderLevel" style="display:${isTemplate?string('none','block')}">
    [#if editable]<div class="removeGenderLevel removeIcon" title="Remove Gender Level"></div>[/#if] 
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}" />
    <input class="fId" type="hidden" name="${customName}.genderLevel" value="${(element.genderLevel)!}" />
    <span title="${(element.nameGenderLevel)!'undefined'}" class="name">${(element.nameGenderLevel)!'undefined'}</span>
    <div class="clearfix"></div>
  </li>
[/#macro]
