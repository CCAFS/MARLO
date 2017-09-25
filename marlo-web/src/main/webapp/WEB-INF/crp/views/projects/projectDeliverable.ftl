[#ftl]
[#assign title = "Deliverable information" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${deliverableID}" /]
[#assign pageLibs = ["select2","font-awesome","dropzone","blueimp-file-upload","jsUri"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/deliverables/deliverableQualityCheck.js",
  "${baseUrlMedia}/js/projects/deliverables/deliverableDataSharing.js",
  "${baseUrlMedia}/js/projects/deliverables/deliverableInfo.js",
  "${baseUrlMedia}/js/projects/deliverables/deliverableDissemination.js", 
  "${baseUrl}/global/js/autoSave.js",
  "${baseUrl}/global/js/fieldsValidation.js"
] /]
[#assign customCSS = ["${baseUrlMedia}/css/projects/projectDeliverable.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "deliverableList" /]
[#assign hideJustification = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"deliverableList", "nameSpace":"/projects", "action":"${(crpSession)!}/deliverableList" ,"param":"projectID=${projectID}"},
  {"label":"deliverableInformation", "nameSpace":"/projects", "action":""}
]/]

[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]
[#import "/WEB-INF/crp/macros/deliverableListTemplate.ftl" as deliverableList /]
[#import "/WEB-INF/global/macros/deliverableMacros.ftl" as deliverableMacros /]

[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
    <p class="col-md-10">[#if reportingActive] [@s.text name="project.deliverable.help2" /] [#else] [@s.text name="project.deliverable.help1" /] [/#if] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>
    
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/projects/messages-deliverables.ftl" /]
        
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          
          <div class="form-group">
            <br />
            [#-- Back --]
            <small >
              <a href="[@s.url action='${crpSession}/deliverableList'][@s.param name="projectID" value=project.id /][/@s.url]">
                <span class="glyphicon glyphicon-circle-arrow-left"></span> Back to the project deliverables
              </a>
            </small>

            [#-- FAIR Compliant Mini --]
            <div class="fairComplian-block" style="display:${deliverable.requeriedFair()?string('block','none')}">
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
                [#include "/WEB-INF/crp/views/projects/deliverableInfo.ftl" /]
              </div>
              [#if reportingActive]
              [#-- Deliverable disseminationMetadata --] 
              <div id="deliverable-disseminationMetadata" role="tabpanel" class="tab-pane fade [#if indexTab==2]in active[/#if]">
                [#-- Is this deliverable already disseminated? --]
                [@deliverableMacros.alreadyDisseminatedMacro /]
                
                [#-- Is this deliverable Open Access? --]
                [@deliverableMacros.isOpenaccessMacro /]
                
                [#-- Have you adopted a license?  --]
                [@deliverableMacros.deliverableLicenseMacro /]
                
                [#-- Metadata (included publications) --]
                <h3 class="headTitle">[@s.text name="project.deliverable.dissemination.metadataSubtitle" /]</h3>
                <div class="simpleBox">
                  [@deliverableMacros.deliverableMetadataMacro /]
                </div>
              </div>
              [#-- Deliverable qualityCheck --]
              <div id="deliverable-qualityCheck" role="tabpanel" class="tab-pane fade [#if indexTab==3]in active[/#if]">
                <div id="complianceCheck" style="display:[#if deliverable.deliverableType?? && (deliverable.deliverableType.id==51 || deliverable.deliverableType.id==74)]block [#else]none[/#if];">
                  [#-- Compliance check (Data products only) --]
                  [@deliverableMacros.complianceCheck /]
                </div>
                <div class="fairComplian-block" style="display:${deliverable.requeriedFair()?string('block','none')}">
                  [#-- Fair Compliant--] 
                  [@deliverableMacros.fairCompliant /]
                </div>
              </div>
              [#-- Deliverable dataSharing --] 
              <div id="deliverable-dataSharing" role="tabpanel" class="tab-pane fade [#if indexTab==4]in active[/#if]">
                [#include "/WEB-INF/crp/views/projects/deliverableDataSharing.ftl" /]
              </div>
              [/#if]
            </div>
           </div>
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/projects/buttons-deliverables.ftl" /]
          
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
  <img class="removeInput" src="${baseUrl}/global/images/icon-remove.png" alt="Remove"> 
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
    <img class="removeInput" src="${baseUrl}/global/images/icon-remove.png" alt="Remove"/>
  </li>
</ul>

[#-- Remove deliverable files modal  template --]
<div id="removeDeliverableFiles" style="display:none" title="Modal title"></div> 

[#-- Deliverable Partner Template --]
[@deliverableList.deliverablePartnerOther dp=[{}] dp_name="" template=true dp_index=0 editable=editable /]

[#-- Deliverable person template --]
[@deliverableList.deliverablePerson element={} name="deliverable.otherPartners" index=-1 checked=false isTemplate=true/]

[#-- Deliverable person template --]
[@deliverableList.deliverablePerson element={} name="deliverable.responsiblePartner" index=-1 isResponsable=true checked=false isTemplate=true/]

[@deliverableMacros.authorMacro element={} index=-1 name="deliverable.users"  isTemplate=true /]
[@deliverableMacros.flagshipMacro element={} index=-1 name="deliverable.crps"  isTemplate=true /]

[#include "/WEB-INF/crp/pages/footer.ftl"]
