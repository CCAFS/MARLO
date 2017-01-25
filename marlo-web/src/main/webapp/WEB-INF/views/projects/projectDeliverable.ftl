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
          
          [#-- Back --]
          <small class="pull-right">
            <a href="[@s.url action='${crpSession}/deliverableList'][@s.param name="projectID" value=project.id /][/@s.url]">
              <span class="glyphicon glyphicon-circle-arrow-left"></span> Back to the project deliverables
            </a>
          </small>
          
          <div class="deliverableTabs">      
          
          [#--  Deliverable Menu  --] 
            
            <ul class="nav nav-tabs" role="tablist"> 
                <li role="presentation" class="active"><a href="#deliverable-mainInformation" aria-controls="info" role="tab" data-toggle="tab">[@s.text name="project.deliverable.generalInformation.titleTab" /]</a></li>
                [#if reportingActive]
                <li role="presentation" class=""><a href="#deliverable-disseminationMetadata" aria-controls="metadata" role="tab" data-toggle="tab">Dissemination & Metadata</a></li>
                <li role="presentation" class=""><a href="#deliverable-qualityCheck" aria-controls="quality" role="tab" data-toggle="tab">Quality check</a></li>
                <li role="presentation" class="dataSharing" style="display:${(deliverable.dissemination.alreadyDisseminated?? && deliverable.dissemination.alreadyDisseminated)?string('none','block')};"><a href="#deliverable-dataSharing" aria-controls="datasharing" role="tab" data-toggle="tab">Data Sharing</a></li>
                [/#if]
            </ul>
            
            <div class="tab-content col-md-12">
              <div id="deliverable-mainInformation" role="tabpanel" class="tab-pane fade in active row">
                
                [#-- Deliverable Information --] 
                [#include "/WEB-INF/views/projects/deliverableInfo.ftl" /]
              </div>
              
              <div id="deliverable-disseminationMetadata" role="tabpanel" class="tab-pane fade">
                
                [#-- Deliverable disseminationMetadata --] 
                [#include "/WEB-INF/views/projects/deliverableDissemination.ftl" /]
              </div>
              
              <div id="deliverable-qualityCheck" role="tabpanel" class="tab-pane fade">
               
                [#-- Deliverable qualityCheck --]
                [#include "/WEB-INF/views/projects/deliverableQualityCheck.ftl" /]
              </div>
              
              
              <div id="deliverable-dataSharing" role="tabpanel" class="tab-pane fade">
              
                [#-- Deliverable dataSharing --] 
                [#include "/WEB-INF/views/projects/deliverableDataSharing.ftl" /]
              </div>
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

[#-- deliverable Partner Template --]
[@deliverableList.deliverablePartner dp={} dp_name="" template=true dp_index=0 editable=editable /]
[@authorMacro element={} index=-1 name="author"  isTemplate=true /]
[@flagshipMacro element={} index=-1 name="deliverable.flagships"  isTemplate=true /]
  
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
      <div class="col-md-12">
        <span class="lastName"> </span>
        <span class="firstName"></span>
      </div>
    </div>
    <span><small class="orcidId"><b>orcid id:</b> not filled</small></span>
    <input type="hidden" class="lastNameInput" value="" />
    <input type="hidden" class="firstNameInput" value="" />
    <input type="hidden" class="orcidIdInput" value="" />
    <div class="clearfix"></div>
  </div>
[/#macro]

[#macro flagshipMacro element index name  isTemplate=false]
  [#assign customName = "${name}[${index}]" /]
  <div id="flagship-${isTemplate?string('template',(projectActivity.id)!)}" class="flagships  borderBox col-md-6"  style="display:${isTemplate?string('none','block')}">
    [#if editable]<div class="removeFlagship removeIcon" title="Remove flagship"></div>[/#if] 
    <input class="id" type="hidden" name="${customName}.falgship.id" value="${(element.flagship.id)!-1}" />
    <input class="idElement" type="hidden" name="${customName}.id" value="${(element.id)!-1}" />
    <span class="name">${(element.flagship.composedName)!'null'}</span>
    <div class="clearfix"></div>
  </div>
[/#macro]

[#-- Metadata Macro --]
[#macro metadataField title="" encodedName="" type="input" list="" require=false]
  [#local metadataID = (deliverable.getMetadataID(encodedName))!-1 /]
  <input type="hidden" name="deliverable.metadataElements[${deliverable.getMetadataIndex(encodedName)}].id" value="${(metadataID)!}" />
    <input type="hidden" name="deliverable.metadataElements[${deliverable.getMetadataIndex(encodedName)}].metadataElement.id" value="${(metadataID)!}" />
  [#if type == "input"]
    [@customForm.input name="deliverable.metadataElements[${deliverable.getMetadataIndex(encodedName)}].elementValue" required=require value="${(deliverable.getMetadataValue(metadataID))!}" className="${title}Metadata"  type="text" i18nkey="metadata.${title}" help="metadata.${title}.help" editable=editable/]
  [#elseif type == "textArea"]
    [@customForm.textArea name="deliverable.metadataElements[${deliverable.getMetadataIndex(encodedName)}].elementValue" required=require value="${(deliverable.getMetadataValue(metadataID))!}" className="${title}Metadata" i18nkey="metadata.${title}" help="metadata.${title}.help" editable=editable/]
  [#elseif type == "select"]
    [@customForm.select name="deliverable.metadataElements[${deliverable.getMetadataIndex(encodedName)}].elementValue" required=require value="${(deliverable.getMetadataValue(metadataID))!}" className="${title}Metadata" i18nkey="metadata.${title}" listName=list  editable=editable /]
 
  [/#if]
[/#macro]