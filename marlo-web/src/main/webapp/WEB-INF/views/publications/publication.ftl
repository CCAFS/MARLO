[#ftl]
[#assign title = "MARLO Publication" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-${(publication.id)!}" /]
[#assign pageLibs = ["select2", "blueimp-file-upload"] /]
[#assign customJS = ["${baseUrl}/js/global/fieldsValidation.js","${baseUrl}/js/publications/publication.js", "${baseUrl}/js/global/autoSave.js" ] /]
[#assign customCSS = ["${baseUrl}/css/fundingSources/publication.css"] /]
[#assign currentSection = "publications" /]

[#assign breadCrumb = [
  {"label":"publicationsList", "nameSpace":"/publications", "action":"publicationsList"},
  {"label":"publication", "nameSpace":"/publications", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign editable = true /]

[#assign customName = "publication" /]

    
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
        [@customForm.textArea name="${customName}.title" required=true className="" editable=editable /]
      </div>
      
      [#-- Subtype & year of completition --]
      <div class="form-group row">
        <div class="col-md-6">
          [@customForm.select name="${customName}.subType" label="" listName="deliverableSubTypes" keyFieldName="id"  displayFieldName="name" required=true  className="" editable=editable/]
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
            [#if  publication?? && publication.institutions?has_content]
              [#list publication.institutions as institutionLead]
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
              <input type="hidden" name="${customName}.flagshipValue" value="${(publication.flagshipValue)!}"/>
              [#if publication?? && publication.flagships?has_content]
                [#list publication.flagships as element]<p class="checked">${(element.composedName)!'null'}</p>[/#list]
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
                <input type="hidden" name="${customName}.regionsValue" value="${(publication.regionsValue)!}"/>
                [#if publication?? && publication.regions?has_content]
                  [#list publication.regions as element]<p class="checked">${(element.composedName)!'null'}</p>[/#list]
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
            <label class="checkbox-inline"><input type="checkbox" name="${customName}.crossCuttingGender"   id="gender"   value="true" [#if (publication.crossCuttingGender)!false ]checked="checked"[/#if]> Gender</label>
            <label class="checkbox-inline"><input type="checkbox" name="${customName}.crossCuttingYouth"    id="youth"    value="true" [#if (publication.crossCuttingYouth)!false ]checked="checked"[/#if]> Youth</label>
            <label class="checkbox-inline"><input type="checkbox" name="${customName}.crossCuttingCapacity" id="capacity" value="true" [#if (publication.crossCuttingCapacity)!false ]checked="checked"[/#if]> Capacity Development</label>
            <label class="checkbox-inline"><input type="checkbox" name="${customName}.crossCuttingNa"       id="na"       value="true" [#if (publication.crossCuttingNa)!false ]checked="checked"[/#if]> N/A</label>
          [#else]
            [#if (publication.crossCuttingGender)!false ] <p class="checked"> Gender</p>[/#if]
            [#if (publication.crossCuttingYouth)!false ] <p class="checked"> Youth</p>[/#if]
            [#if (publication.crossCuttingCapacity)!false ] <p class="checked"> Capacity Development</p>[/#if]
            [#if (publication.crossCuttingNa)!false ] <p class="checked"> N/A</p>[/#if]
          [/#if]
        </div>
        <div class="clearfix"></div>
      </div>
      
    </div>
    <h4 class="headTitle"> Publication Dissemination</h4> 
    <div class="borderBox">
      [#-- Is open access ? --]
      <div class="form-group simpleBox row">
        <div class="col-md-9">
          <label>[@s.text name="${customName}.isOpenAccess" /] <br /> <small>(If no, please select the Open Access restriction)</small></label>
        </div>
        <div class="col-md-3">
          [@customForm.yesNoInput name="${customName}.dissemination.isOpenAccess"  editable=editable inverse=false cssClass="text-center" /]  
        </div>
        <div class="col-md-12 ">
          <hr />
          [#-- Intellectual Property Rights (confidential information) --]
          <div class="radio"><label><input type="radio" name="${customName}.dissemination.type" value="intellectualProperty" [#if ((publication.dissemination.intellectualProperty))!false]checked="checked"[/#if]>Intellectual Property Rights (confidential information)</label></div>
          [#-- Limited Exclusivity Agreements --]
          <div class="radio"><label><input type="radio" name="${customName}.dissemination.type" value="limitedExclusivity" [#if (publication.dissemination.limitedExclusivity)!false]checked="checked"[/#if]>Limited Exclusivity Agreements</label></div>
          [#-- Restricted Use Agreement - Restricted access (if so, what are these periods?) --]
          <div class="radio"><label><input type="radio" name="${customName}.dissemination.type" value="restrictedUseAgreement" [#if (publication.dissemination.restrictedUseAgreement)!false]checked="checked"[/#if]>Restricted Use Agreement - Restricted access (if so, what are these periods?)</label></div>
          [#-- Effective Date Restriction - embargoed periods (if so, what are these periods?) --]
          <div class="radio"><label><input type="radio" name="${customName}.dissemination.type" value="effectiveDateRestriction"[#if (publication.dissemination.effectiveDateRestriction)!false]checked="checked"[/#if] >Effective Date Restriction - embargoed periods (if so, what are these periods?)</label></div>
          [#-- Period --]
          <div class="row restrictionDate-block" style="display:[#if (publication.dissemination.restrictedUseAgreement)?? && (publication.dissemination.restrictedUseAgreement)||(publication.dissemination.effectiveDateRestriction)?? && (publication.dissemination.effectiveDateRestriction) ]block[#else]none [/#if];">
            <div class="col-md-5">[@customForm.input name="${customName}.dissemination.restrictedEmbargoedText" value="" type="text" i18nkey="text"  placeholder="" className="restrictionDate col-md-6" required=true editable=editable /]</div>
          </div>
        </div>
        
      </div>
      [#-- Dissemination channel & URL --]
      <div class="form-group row simpleBox">
        <p class="note col-md-12">The following list of dissemination channels are in accordance to the CGIAR Open Access Policy (i.e. adopt an Interoperability Protocol and Dublin Core Metadata Schema).</p>
        <div class="col-md-4">
        [#if editable]
          [@customForm.select name="${customName}.dissemination.disseminationChannel" value="'${(publication.dissemination.disseminationChannel)!}'"  stringKey=true label="" listName="channels" className="disseminationChannel" required=true   editable=editable/]
        [#else]
          <label for="disChannel" style="display:block;">Dissemination channel:</label>
          <p>Prefilled if available</p>
        [/#if]
        </div>
        [#-- CGSpace examples & instructions --]
        <div class="col-md-8 exampleUrl-block channel-cgspace " style="display:[#if (publication.dissemination.disseminationChannel=="cgspace")!false]block[#else]none[/#if];">
          <label for="">Example of URL:</label>
          <p><small>https://cgspace.cgiar.org/handle/10568/52163</small></p>
        </div>
        <div id="disseminationUrl" class="col-md-8" style="display:[#if (publication.dissemination.disseminationChannel=="cgspace")!false]block[#else]none[/#if];">
          [@customForm.input name="deliverable.dissemination.disseminationUrl" type="text" i18nkey="Dissemination URL"  placeholder="" className="deliverableDisseminationUrl" required=true editable=editable /]
          <div id="fillMetadata" class="checkButton" style="display:[#if (publication.dissemination.disseminationChannel=="cgspace" )!false]block[#else]none[/#if];">Search & Fill Metadata</div>
          <div class="clearfix"></div>
        </div>
      </div>
      
      [#-- License adopted --]
      <div class="form-group simpleBox row">
        <div class="col-md-9">
          <label>[@s.text name="${customName}.publicationAdoptedLicense" /]</label>
        </div>
        <div class="col-md-3">
          [@customForm.yesNoInput name="${customName}.adoptedLicense"  editable=editable inverse=false cssClass="text-center" /]  
        </div>
        <div class="col-md-12 ">
          <hr />
          <div class="form-group">
            <div><input type="radio" name="${customName}.license" id="" value="CC_BY" [#if ((publication.licenseType) == "CC_BY")!false]checked="checked"[/#if]/> CC-BY <small>(allow modifications and commercial use)</small></div>
            <div><input type="radio" name="${customName}.license" id="" value="CC_BY_SA" [#if ((publication.licenseType) == "CC_BY_SA")!false]checked="checked"[/#if]/> CC-BY-SA <small>(allow modifications as long as other share alike and commercial use)</small></div>
            <div><input type="radio" name="${customName}.license" id="" value="CC_BY_ND" [#if ((publication.licenseType) == "CC_BY_ND")!false]checked="checked"[/#if]/> CC-BY-ND <small>(allow commercial use but no modifications)</small></div>
            <div><input type="radio" name="${customName}.license" id="" value="CC_BY_NC" [#if ((publication.licenseType) == "CC_BY_NC")!false]checked="checked"[/#if]/> CC-BY-NC <small>(allow modifications but no commercial use)</small></div>
            <div><input type="radio" name="${customName}.license" id="" value="CC_BY_NC_SA" [#if ((publication.licenseType) == "CC_BY_NC_SA")!false]checked="checked"[/#if]/> CC-BY-NC-SA <small>(allow modifications as long as other share alike, but no commercial use)</small></div>
            <div><input type="radio" name="${customName}.license" id="" value="CC_BY_NC_ND" [#if ((publication.licenseType) == "CC_BY_NC_ND")!false]checked="checked"[/#if]/> CC-BY-NC-ND <small>(don't allow modifications neither commercial use)</small></div>
            <div><input type="radio" name="${customName}.license" id="" value="OTHER" [#if ((publication.licenseType) == "OTHER")!false]checked="checked"[/#if]/> Other</div>
          </div>
          
          <div class="form-group row" style="">
            <div class="col-md-5 licence-modifications">
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
      [#-- Creator / Authors --]
      <div class="form-group row">
      
      </div>
      [#-- Language & publication date --]
      <div class="form-group row">
      
      </div>
      [#-- Country & Keywords --]
      <div class="form-group row">
      
      </div>
      [#-- Citation --]
      <div class="form-group row">
      
      </div>
      [#-- Handle & DOI --]
      <div class="form-group row">
      
      </div>
      [#-- Volume, Issue & Pages --]
      <div class="form-group row">
      
      </div>
      [#-- Journal Publisher Name --]
      <div class="form-group row">
      
      </div>
      [#-- Indicators for journal articles --]
      <div class="form-group row">
      
      </div>
      [#-- Does the publication acknowledge {CRP} --]
      <div class="form-group row">
      
      </div>
    </div>

    [#-- Section Buttons & hidden inputs--]
    [#--  --include "/WEB-INF/views/fundingSources/buttons-fundingSources.ftl" /--]
  </div>
  [/@s.form]
  <div class="col-md-1"></div>
</section>



[#include "/WEB-INF/global/pages/footer.ftl"]


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
