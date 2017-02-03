[#ftl]
[#assign title = "MARLO Publication" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-${(deliverable.id)!}" /]
[#assign pageLibs = ["select2", "blueimp-file-upload"] /]
[#assign customJS = ["${baseUrl}/js/global/fieldsValidation.js","${baseUrl}/js/publications/deliverable.js", "${baseUrl}/js/global/autoSave.js" ] /]
[#assign customCSS = [ ] /]
[#assign currentSection = "publications" /]

[#assign breadCrumb = [
  {"label":"publicationsList", "nameSpace":"/publications", "action":"publicationsList"},
  {"label":"publication", "nameSpace":"/publications", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign editable = true /]

[#assign customName = "deliverable" /]

    
<section class="container">
  <div class="col-md-1"></div>
  [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
  <div class="col-md-10">
    [#-- Messages --]
    [#--  --include "/WEB-INF/views/fundingSources/messages-fundingSource.ftl" /--]
    
    <h4 class="headTitle"> Publication Information</h4> 
    <div class="borderBox">
      [#-- Title --]
      <div class="form-group">
        [@customForm.input name="${customName}.title" required=true className="" editable=editable /]
      </div>
      
      [#-- Subtype & year of completition --]
      <div class="form-group row">
        <div class="col-md-6">
          [@customForm.select name="${customName}.deliverableType.id" label="" listName="deliverableSubTypes" keyFieldName="id"  displayFieldName="name" required=true  className="" editable=editable/]
        </div>
        <div class="col-md-6">
          [@customForm.input name="${customName}.year" required=true className="" editable=editable /]
        </div>
      </div>
      
      [#-- Description --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.description" required=true className="" editable=editable /]
      </div>
      
      [#-- Lead partner(s) --]
      <div class="form-group">
        <div class="panel tertiary">
         <div class="panel-head"><label for=""> [@customForm.text name="${customName}.leadPartners" readText=!editable /]:[@customForm.req required=editable /]</label></div>
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
          <h5>[@s.text name="${customName}.flagships" /]:[@customForm.req required=editable/] </h5>
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
    
    <h4 class="headTitle"> Publication Dissemination</h4> 
    <div class="borderBox">
      [#-- Is open access ? --]
      <div class="form-group simpleBox row">
        <div class="col-md-9">
          <label>[@s.text name="${customName}.isPublicationOpenAccess" /] <br /> <small>([@s.text name="${customName}.isOpenAccess.restriction" /])</small></label>
        </div>
        <div class="col-md-3">
          [@customForm.yesNoInput name="${customName}.dissemination.isOpenAccess" value="${((deliverable.dissemination.isOpenAccess)!false)?string}"  editable=editable inverse=false cssClass="isOpenAccessQuestion text-center" /]  
        </div>
        <div class="col-md-12 openAccessOptions">
          <hr />
          [#-- Intellectual Property Rights (confidential information) --]
          <div class="radio"><label><input type="radio" class="openAccessRestrictionRadio" name="${customName}.dissemination.type" value="intellectualProperty" [#if ((deliverable.dissemination.intellectualProperty))!false]checked="checked"[/#if]>Intellectual Property Rights (confidential information)</label></div>
          [#-- Limited Exclusivity Agreements --]
          <div class="radio"><label><input type="radio" class="openAccessRestrictionRadio" name="${customName}.dissemination.type" value="limitedExclusivity" [#if (deliverable.dissemination.limitedExclusivity)!false]checked="checked"[/#if]>Limited Exclusivity Agreements</label></div>
          [#-- Restricted Use Agreement - Restricted access (if so, what are these periods?) --]
          <div class="radio"><label><input type="radio" class="openAccessRestrictionRadio" name="${customName}.dissemination.type" value="restrictedUseAgreement" [#if (deliverable.dissemination.restrictedUseAgreement)!false]checked="checked"[/#if]>Restricted Use Agreement - Restricted access (if so, what are these periods?)</label></div>
          [#-- Effective Date Restriction - embargoed periods (if so, what are these periods?) --]
          <div class="radio"><label><input type="radio" class="openAccessRestrictionRadio" name="${customName}.dissemination.type" value="effectiveDateRestriction"[#if (deliverable.dissemination.effectiveDateRestriction)!false]checked="checked"[/#if] >Effective Date Restriction - embargoed periods (if so, what are these periods?)</label></div>
          [#-- Period --]
          <div class="row restrictionDate-block" style="display:[#if (deliverable.dissemination.restrictedUseAgreement)?? && (deliverable.dissemination.restrictedUseAgreement)||(deliverable.dissemination.effectiveDateRestriction)?? && (deliverable.dissemination.effectiveDateRestriction) ]block[#else]none [/#if];">
            <div class="col-md-5">[@customForm.input name="${customName}.dissemination.restrictedEmbargoedText" value="" type="text" i18nkey="text"  placeholder="" className="restrictionDate col-md-6" required=true editable=editable /]</div>
          </div>
        </div>
        
      </div>
      [#-- Dissemination channel & URL --]
      <div class="form-group row simpleBox">
        <p class="note col-md-12">The following list of dissemination channels are in accordance to the CGIAR Open Access Policy (i.e. adopt an Interoperability Protocol and Dublin Core Metadata Schema).</p>
        <div class="col-md-4">
        [#if editable]
          [@customForm.select name="${customName}.dissemination.disseminationChannel" value="'${(deliverable.dissemination.disseminationChannel)!}'"  stringKey=true label="" listName="channels" className="disseminationChannel" required=true   editable=editable/]
        [#else]
          <label for="disChannel" style="display:block;">Dissemination channel:</label>
          <p>Prefilled if available</p>
        [/#if]
        </div>
        [#-- CGSpace examples & instructions --]
        <div class="col-md-8 exampleUrl-block channel-cgspace " style="display:[#if (deliverable.dissemination.disseminationChannel=="cgspace")!false]block[#else]none[/#if];">
          <label for="">Example of URL:</label>
          <p><small>https://cgspace.cgiar.org/handle/10568/52163</small></p>
        </div>
        <div id="disseminationUrl" class="col-md-8" style="display:[#if (deliverable.dissemination.disseminationChannel=="cgspace")!false]block[#else]none[/#if];">
          [@customForm.input name="deliverable.dissemination.disseminationUrl" type="text" i18nkey="Dissemination URL"  placeholder="" className="deliverableDisseminationUrl" required=true editable=editable /]
          <div id="fillMetadata" class="checkButton" style="display:[#if (deliverable.dissemination.disseminationChannel=="cgspace" )!false]block[#else]none[/#if];">Search & Fill Metadata</div>
          <div class="clearfix"></div>
        </div>
      </div>
      
      [#-- License adopted --]
      <div class="form-group simpleBox row">
        <div class="col-md-9">
          <label>[@s.text name="${customName}.publicationAdoptedLicense" /]</label>
        </div>
        <div class="col-md-3">
          [@customForm.yesNoInput name="${customName}.adoptedLicense" value="${((deliverable.adoptedLicense)!false)?string}"  editable=editable inverse=false cssClass="adoptedLicense text-center" /]  
        </div>
        <div class="col-md-12 adoptedLicenseOptions" style="display:${((deliverable.adoptedLicense)!false)?string('block','none')}">
          <hr />
          <div class="form-group col-md-12">
            <div class="radio"><input type="radio" class="licenseRadio" name="${customName}.license" id="" value="CC_BY" [#if ((deliverable.licenseType) == "CC_BY")!false]checked="checked"[/#if]/> CC-BY <small>(allow modifications and commercial use)</small></div>
            <div class="radio"><input type="radio" class="licenseRadio" name="${customName}.license" id="" value="CC_BY_SA" [#if ((deliverable.licenseType) == "CC_BY_SA")!false]checked="checked"[/#if]/> CC-BY-SA <small>(allow modifications as long as other share alike and commercial use)</small></div>
            <div class="radio"><input type="radio" class="licenseRadio" name="${customName}.license" id="" value="CC_BY_ND" [#if ((deliverable.licenseType) == "CC_BY_ND")!false]checked="checked"[/#if]/> CC-BY-ND <small>(allow commercial use but no modifications)</small></div>
            <div class="radio"><input type="radio" class="licenseRadio" name="${customName}.license" id="" value="CC_BY_NC" [#if ((deliverable.licenseType) == "CC_BY_NC")!false]checked="checked"[/#if]/> CC-BY-NC <small>(allow modifications but no commercial use)</small></div>
            <div class="radio"><input type="radio" class="licenseRadio" name="${customName}.license" id="" value="CC_BY_NC_SA" [#if ((deliverable.licenseType) == "CC_BY_NC_SA")!false]checked="checked"[/#if]/> CC-BY-NC-SA <small>(allow modifications as long as other share alike, but no commercial use)</small></div>
            <div class="radio"><input type="radio" class="licenseRadio" name="${customName}.license" id="" value="CC_BY_NC_ND" [#if ((deliverable.licenseType) == "CC_BY_NC_ND")!false]checked="checked"[/#if]/> CC-BY-NC-ND <small>(don't allow modifications neither commercial use)</small></div>
            <div class="radio"><input type="radio" class="licenseRadio" name="${customName}.license" id="" value="OTHER" [#if ((deliverable.licenseType) == "OTHER")!false]checked="checked"[/#if]/> Other</div>
          </div>
          
          <div class="form-group row licence-modifications" style="display:[#if ((deliverable.licenseType) == "OTHER")!false]block[#else]none[/#if]">
            <div class="col-md-5">
              [@customForm.input name="${customName}.otherLicense" showTitle=false className="" type="text" placeholder="Please specify" disabled=!editable className="otherLicense"  required=true editable=editable /]
            </div>
            <div class="col-md-7">
              <label class="pull-left">[@s.text name="${customName}.publicationAllowModifications" /]</label>
              [@customForm.yesNoInput name="${customName}.allowModifications"  editable=editable inverse=false value="true" cssClass="licenceModifications pull-right" /] 
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <h4 class="headTitle"> Publication Metadata</h4> 
    <div class="borderBox">
      [#-- Language & publication date --]
      <div class="form-group row">
        <div class="col-md-6">[@metadataField title="language" encodedName="dc.language" type="input" require=true/]</div>
        <div class="col-md-6">[@metadataField title="date" encodedName="dc.date" type="input" require=false/]</div>
      </div>
      [#-- Country & Keywords --]
      <div class="form-group row">
        <div class="col-md-6">[@metadataField title="country" encodedName="cg:coverage.country" type="input" require=false/]</div>
        <div class="col-md-6">[@metadataField title="keywords" encodedName="marlo.keywords" type="input" require=true/]</div>
      </div>
      [#-- Citation --]
      <div class="form-group">
        [@metadataField title="citation" encodedName="dc.identifier.citation" type="textArea" require=false/]
      </div>
      [#-- Handle & DOI --]
      <div class="form-group row">
        <div class="col-md-6">[@metadataField title="Handle" encodedName="marlo.handle" type="input" require=false/]</div>
        <div class="col-md-6">[@metadataField title="DOI" encodedName="marlo.doi" type="input" require=false/]</div>
      </div>
      
      <hr />
      
      [#-- Volume, Issue & Pages --]
      <div class="form-group row">
        <input type="hidden" name="${customName}.deliverable.id" value="${(deliverable.publication.id)!}"/>
        <div class="col-md-4">[@customForm.input name="${customName}.publication.volume" i18nkey="Volume" className="" type="text" disabled=!editable  required=true editable=editable /]</div>
        <div class="col-md-4">[@customForm.input name="${customName}.publication.issue" i18nkey="Issue" className="" type="text" disabled=!editable  required=true editable=editable /]</div>
        <div class="col-md-4">[@customForm.input name="${customName}.publication.pages" i18nkey="Pages" className="" type="text" disabled=!editable  required=true editable=editable /]</div>
      </div>
      [#-- Journal Publisher Name --]
      <div class="form-group">
        [@customForm.input name="${customName}.publication.journal" i18nkey="Journal/Publisher name" className="" type="text" disabled=!editable  required=true editable=editable /]
      </div>
      [#-- Indicators for journal articles --]
      <div class="form-group">
        <label for="">[@s.text name="${customName}.indicatorsJournalArticles" /]:</label>
        [@s.checkboxlist name="${customName}.indicatorsJournal" list="" listKey="id" listValue="composedName" cssClass="checkboxInput fpInput"  value="" /]
      </div>
      [#-- Does the publication acknowledge {CRP} --]
      <div class="form-group row">
        <label class="col-md-9" for="">Does the publication acknowledge?</label>
        <div class="col-md-3">
          [@customForm.yesNoInput name="${customName}.acknowledge"  editable=editable inverse=false value="true" cssClass="acknowledge text-center" /] 
        </div>
      </div>
      
      <hr />
      
      [#-- Creator / Authors --]
      <div class="form-group">
        <label for="">Creator/Authors:</label>
        <div class="authorsList simpleBox col-md-12">
          [#if publication?? && deliverable.authors?has_content]
            [#list deliverable.authors as author]
              [@authorMacro element=author index=author_index name="deliverable.authors"  /]
            [/#list]
          [#else]
            <p class="emptyText text-center "> [@s.text name="No Creator/Authors added yet." /]</p>
          [/#if]
        </div>
        [#if editable]
        <div class="col-md-12">
          <div class="col-md-3">
            <input class="form-control input-sm lName" placeholder="Last Name" type="text" /> 
          </div>
          <div class="col-md-3">
            <input class="form-control input-sm fName" placeholder="First Name" type="text" /> 
          </div>
          <div class="col-md-3">
            <input class="form-control input-sm oId" placeholder="Orcid Id" type="text" /> 
           </div>
           <div class="col-md-3">
             <div id="" class="addAuthor text-right">
              <div class="button-blue "><span class="glyphicon glyphicon-plus-sign"></span> [@s.text name="Add author" /]</div>
            </div>
           </div>
        </div>
        [/#if] 
      </div>
      
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
