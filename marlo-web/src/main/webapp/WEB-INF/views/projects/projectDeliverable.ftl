[#ftl]
[#assign title = "Deliverable information" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${deliverableID}" /]
[#assign pageLibs = ["select2","font-awesome","dropzone","blueimp-file-upload","jsUri"] /]
[#assign customJS = ["${baseUrl}/js/projects/deliverables/deliverableQualityCheck.js","${baseUrl}/js/projects/deliverables/deliverableDataSharing.js","${baseUrl}/js/projects/deliverables/deliverableInfo.js","${baseUrl}/js/projects/deliverables/deliverableDissemination.js", "${baseUrl}/js/global/autoSave.js","${baseUrl}/js/global/fieldsValidation.js"] /]
[#assign customCSS = ["${baseUrl}/css/projects/projectDeliverable.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "deliverableList" /]
[#assign hideJustification = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"deliverableList", "nameSpace":"/projects", "action":"${(crpSession)!}/deliverableList" ,"param":"projectID=${projectID}"},
  {"label":"deliverableInformation", "nameSpace":"/projects", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/deliverableListTemplate.ftl" as deliverableList /]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/images/global/icon-help.jpg" />
    <p class="col-md-10">[#if reportingActive] [@s.text name="project.deliverable.help2" /] [#else] [@s.text name="project.deliverable.help1" /] [/#if] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>
    
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/views/projects/messages-deliverables.ftl" /]
        
       
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          
          <div class="form-group">
            <br />
            [#-- Back --]
            <small >
              <a href="[@s.url action='${crpSession}/deliverableList'][@s.param name="projectID" value=project.id /][/@s.url]">
                <span class="glyphicon glyphicon-circle-arrow-left"></span> Back to the project deliverables
              </a>
            </small>
          
          
            
            <div class="fairComplian-block" style="display:${deliverable.requeriedFair()?string('block','none')}">
              <div class="pull-right">
                [#-- Findable --] 
                <div class="fairCompliant mini findable [#if action.isF(deliverable.id)??][#if action.isF(deliverable.id)]achieved[#else]not-achieved[/#if][/#if]">
                  <div class="sign">F</div>
                </div>
                [#-- Accessible --] 
                <div class="fairCompliant mini accessible [#if action.isA(deliverable.id)??][#if action.isA(deliverable.id)]achieved[#else]not-achieved[/#if][/#if]">
                  <div class="sign">A</div>
                </div>
                [#-- Interoperable --] 
                <div class="fairCompliant mini interoperable [#if action.isI(deliverable.id)??][#if action.isI(deliverable.id)]achieved[#else]not-achieved[/#if][/#if]">
                  <div class="sign">I</div>
                </div>
                [#-- Reusable --] 
                <div class="fairCompliant mini reusable [#if action.isR(deliverable.id)??][#if action.isR(deliverable.id)]achieved[#else]not-achieved[/#if][/#if]">
                  <div class="sign">R</div>
                </div> 
              </div>
            </div>
          </div>
             
           <input id="indexTab" name="indexTab" type="hidden" value="${(indexTab)!0}">
          <div class="deliverableTabs">
          
            
            
            
            [#--  Deliverable Menu  --] 
            <ul class="nav nav-tabs" role="tablist"> 
                <li role="presentation" class="[#if indexTab==1 || indexTab==0]active[/#if]"><a index="1" href="#deliverable-mainInformation" aria-controls="info" role="tab" data-toggle="tab">[@s.text name="project.deliverable.generalInformation.titleTab" /]</a></li>
                [#if reportingActive]
                <li role="presentation" class="[#if indexTab==2]active[/#if]"><a index="2" href="#deliverable-disseminationMetadata" aria-controls="metadata" role="tab" data-toggle="tab">Dissemination & Metadata</a></li>
                [#assign isRequiredQuality = deliverable.requeriedFair() || (deliverable.deliverableType?? && (deliverable.deliverableType.id==51 || deliverable.deliverableType.id==74)) /]
                <li role="presentation" class="[#if indexTab==3]active[/#if]" style="display:${isRequiredQuality?string('block','none')};"><a index="3" href="#deliverable-qualityCheck" aria-controls="quality" role="tab" data-toggle="tab">Quality check</a></li>
                [#assign isRequiredDataSharing = (deliverable.dissemination.alreadyDisseminated)!false /]
                <li role="presentation" class="dataSharing [#if indexTab==4]active[/#if]" style="display:${isRequiredDataSharing?string('none','block')};"><a index="4" href="#deliverable-dataSharing" aria-controls="datasharing" role="tab" data-toggle="tab">Data Sharing</a></li>
                [/#if]
            </ul>
            
            <div class="tab-content ">
              [#-- Deliverable Information --] 
              <div id="deliverable-mainInformation" role="tabpanel" class="tab-pane fade [#if indexTab==1 || indexTab==0]in active[/#if]">
                [#include "/WEB-INF/views/projects/deliverableInfo.ftl" /]
              </div>
              [#if reportingActive]
              [#-- Deliverable disseminationMetadata --] 
              <div id="deliverable-disseminationMetadata" role="tabpanel" class="tab-pane fade [#if indexTab==2]in active[/#if]">
                [#include "/WEB-INF/views/projects/deliverableDissemination.ftl" /]
              </div>
              [#-- Deliverable qualityCheck --]
              <div id="deliverable-qualityCheck" role="tabpanel" class="tab-pane fade [#if indexTab==3]in active[/#if]">
                [#include "/WEB-INF/views/projects/deliverableQualityCheck.ftl" /]
              </div>
              [#-- Deliverable dataSharing --] 
              <div id="deliverable-dataSharing" role="tabpanel" class="tab-pane fade [#if indexTab==4]in active[/#if]">
                [#include "/WEB-INF/views/projects/deliverableDataSharing.ftl" /]
              </div>
              [/#if]
            </div>
            
           </div>
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-deliverables.ftl" /]
             
         
          [/@s.form] 
      </div>
    </div>  
</section>

[#-- Funding Source list template --]
<ul style="display:none">
  <li id="fsourceTemplate" class="fundingSources clearfix" style="display:none;">
    <div class="removeFundingSource removeIcon" title="Remove funding source"></div>
    <input class="id" type="hidden" name="deliverable.fundingSources[-1].id" value="" />
    <input class="fId" type="hidden" name="deliverable.fundingSources[-1].fundingSource.id" value="" />
    <span class="name"></span>
    <div class="clearfix"></div>
  </li>
</ul>

[#-- File Input template --]
<div id="fileInputTemplate" class="fileInput" style="display:none">
  <img class="removeInput" src="${baseUrl}/images/global/icon-remove.png" alt="Remove"> 
  <input name="filesUploaded" type="file" />
</div>
 
[#-- File uploaded template --]
<ul>
  <li id="deliverableFileTemplate" class="fileUploaded" style="display:none">
    <input class="fileID" name="" type="hidden">
    <input class="fileHosted" name="" type="hidden">
    <input class="fileName" name="" type="hidden">
    <div class="fileName">filename</div>
    <div class="fileFormat">- -</div>
    <div class="fileSize">- -</div>
    <img class="removeInput" src="${baseUrl}/images/global/icon-remove.png" alt="Remove"/>
  </li>
</ul>

[#-- Remove deliverable files modal  template --]
<div id="removeDeliverableFiles" style="display:none" title="Modal title"></div> 

[#-- deliverable Partner Template --]
[@deliverableList.deliverablePartner dp={} dp_name="" template=true dp_index=0 editable=editable /]
[@authorMacro element={} index=-1 name="deliverable.users"  isTemplate=true /]
[@flagshipMacro element={} index=-1 name="deliverable.crps"  isTemplate=true /]

[#include "/WEB-INF/global/pages/footer.ftl"]

[#macro authorMacro element index name  isTemplate=false]
  [#assign customName = "${name}[${index}]" /]
  <div id="author-${isTemplate?string('template',(element.id)!)}" class="author  simpleBox col-md-4"  style="display:${isTemplate?string('none','block')}">
    [#if editable] [#--&& (isTemplate) --]
      <div class="removeLink">
        <div class="removeAuthor removeIcon" title="Remove author/creator"></div>
      </div>
    [/#if]
    <div class="row">
      <div class="col-md-12"><span class="lastName">${(element.lastName)!} </span> <span class="firstName">${(element.firstName)!} </span></div>
    </div>
    <span><small class="orcidId">[#if element.elementId?has_content][#else]<b>orcid id:</b>[/#if] ${(element.elementId)!'not filled'}</small></span>
    <input type="hidden" name="${customName}.id" class="id" value="${(element.id)!}" />
    <input type="hidden" name="${customName}.lastName"  class="lastNameInput" value="${(element.lastName)!}" />
    <input type="hidden" name="${customName}.firstName"  class="firstNameInput" value="${(element.firstName)!}" />
    <input type="hidden"name="${customName}.elementId"   class="orcidIdInput" value="${(element.elementId)!}" />
    <div class="clearfix"></div>
  </div>
[/#macro]

[#macro flagshipMacro element index name  isTemplate=false]
  [#assign customName = "${name}[${index}]" /]
  <div id="flagship-${isTemplate?string('template',(projectActivity.id)!)}" class="flagships  borderBox col-md-6"  style="display:${isTemplate?string('none','block')}">
    [#if editable]<div class="removeFlagship removeIcon" title="Remove flagship"></div>[/#if] 
    <input class="idElemento" type="hidden" name="${customName}.id" value="${(element.id)!-1}" />
    <input class="idCrp" type="hidden" name="${customName}.crpPandr.id" value="${(element.crpPandr.id)!}" />
    <input class="idFlagship" type="hidden" name="${customName}.ipProgram.id" value="${(element.ipProgram.id)!}" />
    <span class="name">${(element.crpPandr.name)!}-${(element.ipProgram.acronym)!}</span>
    <div class="clearfix"></div>
  </div>
[/#macro]

[#-- Metadata Macro --]
[#macro metadataField title="" encodedName="" type="input" list="" require=false]
  [#local metadataID = (deliverable.getMetadataID(encodedName))!-1 /]
  [#local metadataIndex = (deliverable.getMetadataIndex(encodedName))!-1 /]
  [#local ID = (deliverable.getID(metadataID))!'' /]
  [#local metadataValue = (deliverable.getMetadataValue(metadataID))!'' /]
  [#local customName = 'deliverable' /]
  <input type="hidden" name="${customName}.metadataElements[${metadataIndex}].id" value="${ID}" />
  <input type="hidden" name="${customName}.metadataElements[${metadataIndex}].metadataElement.id" value="${metadataID}" />
  [#if type == "input"]
    [@customForm.input name="${customName}.metadataElements[${metadataIndex}].elementValue" required=require value="${metadataValue}" className="${title}Metadata"  type="text" i18nkey="metadata.${title}" help="metadata.${title}.help" editable=editable/]
  [#elseif type == "textArea"]
    [@customForm.textArea name="${customName}.metadataElements[${metadataIndex}].elementValue" required=require value="${metadataValue}" className="${title}Metadata" i18nkey="metadata.${title}" help="metadata.${title}.help" editable=editable/]
  [#elseif type == "select"]
    [@customForm.select name="${customName}.metadataElements[${metadataIndex}].elementValue" required=require value="${metadataValue}" className="${title}Metadata" i18nkey="metadata.${title}" listName=list  editable=editable /]
  [/#if]
[/#macro]

[#function checkDeliverableTypes]
  [#if deliverable.deliverableType?? && deliverable.deliverableType.deliverableType.id==49]
    [#return "block"]
  [#else]
    [#return "none"]
  [/#if]
[/#function]

[#macro deliverableLicense title="" encodedName="" type="input" list="" require=false]
<div class="simpleBox">
  <div class="form-group row">
    <label class="col-md-9" for="">[@s.text name="Have you adopted a license?" /]</label>
    <div class="col-md-3">[@customForm.yesNoInput name="deliverable.adoptedLicense"  editable=editable inverse=false  cssClass="license text-center" /] </div>  
  </div>
  [#-- Deliverable type computer software --]
  <div class="radio-block licenseOptions-block" style="display:${((deliverable.adoptedLicense)!false)?string('block','none')}">
    <hr />
    [#if editable]
      <div class="licenseOptions computerLicense" style="display:[#if deliverable.deliverableType?? && deliverable.deliverableType.id==52 ]block [#else]none[/#if];">
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="MIT" [#if ((deliverable.licenseType) == "MIT")!false]checked="checked"[/#if]/> MIT License</div>
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="GNU" [#if ((deliverable.licenseType) == "GNU")!false]checked="checked"[/#if]/> GNU General Public License</div>
      </div>
      
      [#-- Deliverable type data --]
      <div class=" licenseOptions dataLicense" style="display:[#if deliverable.deliverableType?? && (deliverable.deliverableType.id==51 || deliverable.deliverableType.id==74)]block [#else]none[/#if];">
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="CC_LICENSES" [#if ((deliverable.licenseType) == "CC_LICENSES")!false]checked="checked"[/#if]/> CC licenses version 4.0</div>
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="CC_PUBLIC" [#if ((deliverable.licenseType) == "CC_PUBLIC")!false]checked="checked"[/#if]/> CC Public Domain Dedication (CC0 1.0)</div>
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="OPEN_DATA" [#if ((deliverable.licenseType) == "OPEN_DATA")!false]checked="checked"[/#if]/> Open Data Commons (ODC)</div>
      </div>
      
      [#-- Deliverable type other research types --]
      <div class=" licenseOptions" style="display:block;">
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="CC_BY" [#if ((deliverable.licenseType) == "CC_BY")!false]checked="checked"[/#if]/> CC-BY <small>(allow modifications and commercial use)</small></div>
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="CC_BY_SA" [#if ((deliverable.licenseType) == "CC_BY_SA")!false]checked="checked"[/#if]/> CC-BY-SA <small>(allow modifications as long as other share alike and commercial use)</small></div>
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="CC_BY_ND" [#if ((deliverable.licenseType) == "CC_BY_ND")!false]checked="checked"[/#if]/> CC-BY-ND <small>(allow commercial use but no modifications)</small></div>
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="CC_BY_NC" [#if ((deliverable.licenseType) == "CC_BY_NC")!false]checked="checked"[/#if]/> CC-BY-NC <small>(allow modifications but no commercial use)</small></div>
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="CC_BY_NC_SA" [#if ((deliverable.licenseType) == "CC_BY_NC_SA")!false]checked="checked"[/#if]/> CC-BY-NC-SA <small>(allow modifications as long as other share alike, but no commercial use)</small></div>
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="CC_BY_NC_ND" [#if ((deliverable.licenseType) == "CC_BY_NC_ND")!false]checked="checked"[/#if]/> CC-BY-NC-ND <small>(don't allow modifications neither commercial use)</small></div>
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="OTHER" [#if ((deliverable.licenseType) == "OTHER")!false]checked="checked"[/#if]/> Other</div>
      </div>
   
      [#-- Other --]
      <div class="licenseOptions">
        <div class="form-group row">
          <div class="col-md-6 licence-modifications" style="display:[#if (deliverable.licenseType)?? && (deliverable.licenseType)=="OTHER"]block[#else]none [/#if];" >
            [@customForm.input name="deliverable.otherLicense" showTitle=false className="" type="text" placeholder="Please specify" disabled=!editable className="otherLicense"  required=true editable=editable /]
          </div>
          <div class="col-md-6 licence-modifications" style="display:[#if (deliverable.licenseType)?? && (deliverable.licenseType)=="OTHER"]block[#else]none [/#if];" >
            <label class="col-md-6" for="">Does this license allows modifications?</label>
            <div class="col-md-6">
              [@customForm.yesNoInput name="deliverable.allowModifications"  editable=editable inverse=false cssClass="licenceModifications text-center" /] 
            </div>  
          </div>
        </div>
      </div>
      
    [#else]
      [#-- Deliverable type computer software --]
      <div class=" licenseOptions computerLicense" style="display:[#if deliverable.deliverableType?? && deliverable.deliverableType.id==52 ]block [#else]none[/#if];">
        [#if deliverable.licenseType?? && deliverable.licenseType == "MIT"]<p class="checked">MIT License</p>[/#if]
        [#if deliverable.licenseType?? && deliverable.licenseType == "GNU"]<p class="checked">GNU General Public License</p>[/#if]
      </div>
      
      [#-- Deliverable type data --]
      <div class=" licenseOptions dataLicense" style="display:[#if deliverable.deliverableType?? && (deliverable.deliverableType.id==51 || deliverable.deliverableType.id==74)]block [#else]none[/#if];">
        [#if deliverable.licenseType?? && deliverable.licenseType == "CC_LICENSES"]<p class="checked">CC licenses version 4.0</p>[/#if]
        [#if deliverable.licenseType?? && deliverable.licenseType == "CC_PUBLIC"]<p class="checked">CC Public Domain Dedication (CC0 1.0)</p>[/#if]
        [#if deliverable.licenseType?? && deliverable.licenseType == "OPEN_DATA"]<p class="checked">Open Data Commons (ODC)</p>[/#if]
      </div>
      
      [#-- Deliverable type other research types --]
      <div class="licenseOptions" style="display:block;">
        [#if deliverable.licenseType?? && deliverable.licenseType == "CC_BY"]<p class="checked">CC-BY <small>(allow modifications and commercial use)</small></p>[/#if]
        [#if deliverable.licenseType?? && deliverable.licenseType == "CC_BY_SA"]<p class="checked">CC-BY-SA <small>(allow modifications as long as other share alike and commercial use)</small></p>[/#if]
        [#if deliverable.licenseType?? && deliverable.licenseType == "CC_BY_ND"]<p class="checked">CC-BY-ND <small>(allow commercial use but no modifications)</small></p>[/#if]
        [#if deliverable.licenseType?? && deliverable.licenseType == "CC_BY_NC"]<p class="checked">CC-BY-NC <small>(allow modifications but no commercial use)</small></small></p>[/#if]
        [#if deliverable.licenseType?? && deliverable.licenseType == "CC_BY_NC_SA"]<p class="checked">CC-BY-NC-SA <small>(allow modifications as long as other share alike, but no commercial use)</small></p>[/#if]
        [#if deliverable.licenseType?? && deliverable.licenseType == "CC_BY_NC_ND"]<p class="checked">CC-BY-NC-ND <small>(don't allow modifications neither commercial use)</small></small></p>[/#if]
      </div>
      [#-- Other --]
      <div class="licenseOptions">
        [#if deliverable.licenseType?? && deliverable.licenseType == "OTHER"]<p class="checked"> Other </p>[/#if]
        
        <div class="form-group row">
          <div class="col-md-6 licence-modifications" style="display:[#if (deliverable.licenseType)?? && (deliverable.licenseType)=="OTHER"]block[#else]none [/#if];" >
            [@customForm.input name="deliverable.otherLicense" showTitle=false className="" type="text" placeholder="Please specify" disabled=!editable className="otherLicense"  required=true editable=editable /]
          </div>
          <div class="col-md-6 licence-modifications" style="display:[#if (deliverable.licenseType)?? && (deliverable.licenseType)=="OTHER"]block[#else]none [/#if];" >
            <label class="col-md-6" for="">Does this license allows modifications?</label>
            <div class="col-md-6">
              [@customForm.yesNoInput name="deliverable.allowModifications"  editable=editable inverse=false cssClass="licenceModifications text-center" /] 
            </div>  
          </div>
        </div>
      </div>
       
    [/#if]
  </div>
  
</div>
[/#macro]