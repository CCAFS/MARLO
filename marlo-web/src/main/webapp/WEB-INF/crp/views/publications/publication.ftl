[#ftl]
[#assign title = "MARLO Publication" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-${(deliverable.id)!}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2","font-awesome","blueimp-file-upload","jsUri", "flag-icon-css", "pickadate", "vue"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/publications/publication.js?20190228",
  "${baseUrlMedia}/js/projects/deliverables/deliverableQualityCheck.js?20190228", 
  "${baseUrlMedia}/js/projects/deliverables/deliverableInfo.js?20230417",
  "${baseUrlMedia}/js/projects/deliverables/deliverableDissemination.js?20230630",
  [#-- "${baseUrlCdn}/global/js/autoSave.js",--]
  "${baseUrlCdn}/global/js/fieldsValidation.js?20180529"
] /]
[#assign customCSS = [ "${baseUrlMedia}/css/publications/publication.css", "${baseUrlMedia}/css/projects/projectDeliverable.css" ] /]
[#assign currentSection = "additionalReporting" /]

[#assign breadCrumb = [
  {"label":"publicationsList", "nameSpace":"publications", "action":"${crpSession}/publicationsList"},
  {"label":"publication", "nameSpace":"publications", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/deliverableMacros.ftl" as deliverableMacros /]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]
[#assign customName = "deliverable" /]

    
<section class="container">
  <div class="col-md-1"></div>
  [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
  <div class="col-md-10">
    [#-- Messages --]
    [#include "/WEB-INF/crp/views/publications/messages-publications.ftl" /]
    
    [#-- Back --]
    <small class="pull-left">
      <a href="[@s.url action='${crpSession}/publicationsList'][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
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

    




    <div class="deliverableTabs">
      [#--  Deliverable Menu  --] 
      <ul class="nav nav-tabs" role="tablist"> 
          <li role="presentation" class="active"><a index="1" href="#deliverable-mainInformation" aria-controls="info" role="tab" data-toggle="tab">[@s.text name="project.deliverable.generalInformation.titleTab" /]</a></li>
          
          [#if (reportingActive || actualPhase.upkeep) && action.hasSpecificities("crp_has_disemination") ]
          <li role="presentation" class=""><a index="2" href="#deliverable-disseminationMetadata" aria-controls="metadata" role="tab" data-toggle="tab">Dissemination & Metadata</a></li>
          
          [#--  <li role="presentation" class="" ><a index="3" href="#deliverable-qualityCheck" aria-controls="quality" role="tab" data-toggle="tab">Quality check</a></li>--]
          
          [#--  
          [#assign isRequiredDataSharing = (deliverable.dissemination.alreadyDisseminated)!false /]
          <li role="presentation" class="dataSharing " style="display:${isRequiredDataSharing?string('none','block')};"><a index="4" href="#deliverable-dataSharing" aria-controls="datasharing" role="tab" data-toggle="tab">Data Sharing</a></li>
           --]
          [/#if]
         
      </ul>
      <div class="tab-content ">
        [#-- Deliverable Information --] 
        <div id="deliverable-mainInformation" role="tabpanel" class="tab-pane fade in active">
          [#--  <h3 class="headTitle"> Publication Information</h3> --]  
          <div class="borderBox">
            [#-- Title --]
            <div class="form-group">
              [@customForm.input name="${customName}.deliverableInfo.title" i18nkey="publication.title" required=true className="" editable=editable /]
            </div>
            
            [#-- Description --] 
            <div class="form-group">
              [@customForm.textArea name="${customName}.deliverableInfo.description" value="${(deliverable.deliverableInfo.description)!}" i18nkey="project.deliverable.generalInformation.description"  placeholder="" className="limitWords-50" required=true editable=editable /]
            </div>
            
            [#-- Subtype & year of completition --]
            <div class="form-group row">
              <div class="col-md-6">
                [@customForm.select name="${customName}.deliverableInfo.deliverableType.id" i18nkey="publication.subType" label="" listName="deliverableSubTypes" keyFieldName="id"  displayFieldName="name" required=true  className="" editable=editable/]
              </div>
              <div class="col-md-6">
                [@customForm.input name="${customName}.deliverableInfo.year" i18nkey="publication.year" required=true className="" editable=false /]
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
                    [@customForm.select name="" label="" keyFieldName="id"  displayFieldName="composedName"  showTitle=false listName="institutions"  required=true className="leadPartnersSelect" editable=editable /]
                  [/#if] 
                </div>
              </div>
            </div>
            
            [#-- Flagships & Regions --]
            <div class="form-group row">
              [#-- Flagships --]
              [#if flagshipsList??]
                <div class="col-md-6">
                  <h5>[@s.text name="publication.flagships" /]:[@customForm.req required=editable/] </h5>
                  <div id="" class="dottedBox">
                    [#if editable]
                      [#list flagshipsList as element]
                        [@customForm.checkBoxFlat id="flagship-${element.id}" name="${customName}.flagshipValue" label="${element.composedName}" value="${element.id}" editable=editable checked=((flagshipIds?seq_contains('${element.id}'))!false) cssClass="checkboxInput fpInput" cssClassLabel="font-normal" /]
                      [/#list]
                    [#else]
                      <input type="hidden" name="${customName}.flagshipValue" value="${(deliverable.flagshipValue)!}"/>
                      [#if deliverable.programs?has_content]
                        [#list deliverable.programs as element]<p class="checked">${(element.crpProgram.composedName)!'null'}</p>[/#list]
                      [/#if]
                    [/#if]
                  </div>
                </div>
              [/#if]
              
              [#-- Regions --] 
              [#if regionsList?has_content] 
                <div class="col-md-6"> 
                  <h5>[@s.text name="publication.regions" /]:[@customForm.req required=editable /]</h5>
                  <div id="" class="dottedBox">
                    [#if editable]
                      [#assign noRegionalLabel][@s.text name="project.noRegional" /][/#assign]                
                      [@customForm.checkBoxFlat id="noRegional" name="deliverable.deliverableInfo.isLocationGlobal" label="${noRegionalLabel}" disabled=false editable=editable value="true" checked=((deliverable.deliverableInfo.isLocationGlobal)!false) cssClass="" cssClassLabel="font-italic" /]
                      [#list regionsList as element]
                        [@customForm.checkBoxFlat id="region-${element.id}" name="${customName}.regionsValue" label="${element.composedName}" value="${element.id}" editable=editable checked=((regionsIds?seq_contains('${element.id}'))!false) cssClass="checkboxInput rpInput" /]
                      [/#list]
                    [#else] 
                      <input type="hidden" name="${customName}.regionsValue" value="${(deliverable.regionsValue)!}"/>
                      [#if  deliverable.regions?has_content]
                        [#list deliverable.regions as element]<p class="checked">${(element.crpProgram.composedName)!'null'}</p>[/#list]
                      [/#if]
                    [/#if]
                  </div>
                </div>
              [/#if]
            </div>
            
            [#-- Key Outputs select --]
            <div class="form-group">
              [@customForm.select name="deliverable.deliverableInfo.crpClusterKeyOutput.id" label=""  i18nkey="project.deliverable.generalInformation.keyOutput" listName="keyOutputs" keyFieldName="id"  displayFieldName="composedName"  multiple=false required=true  className="keyOutput" editable=editable/]
            </div>
            
            [#-- Funding Source --]
            [#if !phaseOne]
            <div class="panel tertiary">
             <div class="panel-head"><label for=""> [@customForm.text name="project.deliverable.fundingSource" readText=!editable /]:[@customForm.req required=editable /]</label></div>
              <div id="fundingSourceList" class="panel-body" listname="deliverable.fundingSources"> 
                <ul class="list">
                [#if deliverable.fundingSources?has_content]
                  [#list deliverable.fundingSources as element]
                    <li class="fundingSources clearfix">
                      [#if editable]<div class="removeFundingSource removeIcon" title="Remove funding source"></div>[/#if] 
                      <input class="id" type="hidden" name="deliverable.fundingSources[${element_index}].id" value="${(element.id)!}" />
                      <input class="fId" type="hidden" name="deliverable.fundingSources[${element_index}].fundingSource.id" value="${(element.fundingSource.id)!}" />
                      <span class="name">
                        <strong>FS${(element.fundingSource.id)!} - ${(element.fundingSource.fundingSourceInfo.budgetType.name)!} [#if (element.fundingSource.fundingSourceInfo.w1w2)!false] (Co-Financing)[/#if]</strong> <br />
                        <span class="description">${(element.fundingSource.fundingSourceInfo.title)!}</span><br />
                      </span>
                      <div class="clearfix"></div>
                    </li>
                  [/#list]
                  <p style="display:none;" class="emptyText"> [@s.text name="project.deliverable.fundingSource.empty" /]</p>   
                [#else]
                  <p class="emptyText"> [@s.text name="project.deliverable.fundingSource.empty" /]</p> 
                [/#if]
                </ul>
                [#if editable ]
                  [@customForm.select name="" label="" showTitle=false i18nkey="" listName="fundingSources" keyFieldName="id"  displayFieldName="composedName"  header=true required=true  className="fundingSource" editable=editable/]
                [/#if]
              </div>
            </div>
            [#-- Funding source List --]
            <div style="display:none">
              [#if fundingSources?has_content]
                [#list fundingSources as element]
                  <span id="fundingSource-${(element.id)!}">
                    <strong>FS${(element.id)!} - ${(element.fundingSourceInfo.budgetType.name)!} [#if (element.w1w2)!false] (Co-Financing) [/#if]</strong> <br />
                    <span class="description">${(element.fundingSourceInfo.title)!}</span><br />
                  </span>
                [/#list]
              [/#if]
            </div>
            [/#if]
            
            [#-- Geographic Scope  --]
            <div class="form-group simpleBox">
              [@deliverableMacros.deliverableGeographicScope /]
            </div>
            
            [#--  Cross Cutting --]
            <div class="form-group simpleBox">
              [@deliverableMacros.deliverableCrossCuttingMacro label="publication.crossCuttingDimensions" /]
            </div>
            
          </div>
        </div>
       
        [#-- Deliverable disseminationMetadata --] 
        <div id="deliverable-disseminationMetadata" role="tabpanel" class="tab-pane fade ">

          [#if action.hasSpecificities("crp_has_disemination")]
          <h3 class="headTitle"> Publication Dissemination</h3> 
          <div class="borderBox"> 
            [#-- Dissemination channel & URL --]
            <div class="simpleBox">
              <div class="findable"><input type="hidden" name="${customName}.dissemination.alreadyDisseminated" value="true"/></div>
              [@deliverableMacros.findableOptions /]
            </div>
            
            [#--  Does this deliverable involve Participants and Trainees? --]
            [@deliverableMacros.deliverableParticipantsMacro /]
            
            [#-- Is this deliverable Open Access? --]
            [@deliverableMacros.isOpenaccessMacro /]
            
            [#-- Have you adopted a license?  --]
            [@deliverableMacros.deliverableLicenseMacro/]
          </div>
          
          <h3 class="headTitle"> Publication Metadata</h3> 
          <div class="borderBox">
            [#-- Metadata (included publications) --]
            [@deliverableMacros.deliverableMetadataMacro flagshipslistName="flagshipsList" allowFlagships=false /]
          </div>
        [/#if] 

        </div>
        [#-- Deliverable qualityCheck 
        <div id="deliverable-qualityCheck" role="tabpanel" class="tab-pane fade">


        </div>
        --]
     </div>

    [#-- Section Buttons & hidden inputs--]
    [#include "/WEB-INF/crp/views/publications/buttons-publications.ftl" /]
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
