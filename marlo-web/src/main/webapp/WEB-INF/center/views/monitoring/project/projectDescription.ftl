[#ftl]
[#assign title = "Project Description" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2","flat-flags"] /]
[#assign customJS = [
  "${baseUrl}/global/js/fieldsValidation.js",
  "${baseUrl}/global/js/usersManagement.js", 
  "${baseUrlMedia}/js/monitoring/projects/projectDescription.js",
  "${baseUrlMedia}/js/monitoring/projects/projectSync.js",
  "${baseUrl}/global/js/autoSave.js"
  ] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "description" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/monitoring", "action":"${(centerSession)!}/projectList"},
  {"label":"projectDescription", "nameSpace":"/monitoring", "action":""}
] /]


[#include "/WEB-INF/center/pages/header.ftl" /]
[#include "/WEB-INF/center/pages/main-menu.ftl" /]
[#-- Search users Interface --]
[#import "/WEB-INF/global/macros/usersPopup.ftl" as usersForm/]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]

[#-- Help text --]
<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.png" />
    <p class="col-md-10"> [@s.text name="projectDescription.help"][/@s.text] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>
    
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/center/views/monitoring/project/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/center/views/monitoring/project/messages-projects.ftl" /]
        <br />

        [#assign projectSync = (project.sync)!false /]
     
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
           
          [#-- Back --]
        <div class="pull-right">
          <a href="[@s.url action='${centerSession}/projectList'][@s.param name="programID" value=programID /][@s.param name="projectID" value=projectID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
            <span class="glyphicon glyphicon-circle-arrow-left"></span> Back to the project list
          </a>
        </div>
            
            <h3 class="headTitle">${selectedProgram.name} - [@s.text name="projectDescription.title" /]</h3>  
            <div id="projectDescription" class="borderBox">
            
            [#-- Project Title --]
            <div class="form-group metadataElement-description">
              [@customForm.input name="project.name" i18nkey="projectDescription.name" required=true className="project-title metadataValue" readOnly=projectSync editable=editable && action.hasPermission("title") /]
            </div>
            [#-- Project Suggested Title --]
            <div class="form-group">
              [@customForm.input name="project.suggestedName" i18nkey="projectDescription.suggestedName" required=false className="project-title" required=true editable=editable && action.hasPermission("title") /]
            </div>
            [#-- Project Description --]
            <div class="form-group metadataElement-objectives">
              [@customForm.textArea name="project.description" i18nkey="projectDescription.description" required=true className="metadataValue" readOnly=projectSync editable=editable && action.hasPermission("title") /]
            </div> 
          
           
            <div class="form-group row">              
            [#-- Project Status --]  
              <div class="col-md-3">
                [@customForm.select name="project.projectStatus.id" label=""  i18nkey="projectsList.status" listName="status" keyFieldName="id"  displayFieldName="name"  multiple=false required=true header=false className="" editable=editable/]
              </div>  
   
            </div>   
                       
            <div class="form-group row">  
              [#-- Start Date --]
              <div class="col-md-4 metadataElement-startDate">
                [@customForm.input name="project.startDate" i18nkey="projectDescription.startDate" className="metadataValue" type="text" disabled=!editable  required=true editable=editable /]
              </div> 
              [#-- End Date --]
              <div class="col-md-4 metadataElement-endDate">
                [@customForm.input name="project.endDate" i18nkey="projectDescription.endDate" className="metadataValue" type="text" disabled=!editable required=false editable=editable /]
              </div>
            </div>
            
            
            [#-- Principal Investigator --]
            <!--
              <div class="col-md-12">
                [@customForm.input name="principalInvestigator" i18nkey="projectDescription.pl" type="text" disabled=!editable  required=true editable=false /]
              </div>
              -->
            
            <div class="form-group metadataElement-pInvestigator">
              [#-- Project contact --]
              <div class="partnerPerson-email userField" style="position: relative;">
                <input type="hidden" class="canEditEmail" value="" />
                [@customForm.input name="project.projectLeader.composedName" className='userName' type="text"  i18nkey="projectDescription.contactPerson" required=true readOnly=true editable=editable /]
                <input class="userId" type="hidden" name="project.projectLeader.id" value="${(project.projectLeader.id)!}" />   
                [#if editable]<div class="searchUser button-blue button-float">[@s.text name="form.buttons.searchUser" /]</div>[/#if]
              </div>
              <span class="text-warning metadataSuggested"></span><br />
            </div>
            
           
           
            [#-- CRP Project Contributions List --]
            <div class="form-group">
              <label>[@s.text name="projectDescription.crpCont" /]:</label>
              <div class="fundingSourceList" listname="project.fundingSources">
                [#if project.fundingSources?has_content]
                  [#list project.fundingSources as fundingSource]
                    [@fundingSourceMacro element=fundingSource name="project.fundingSources"  index=fundingSource_index /]
                  [/#list]
                [/#if]
              </div>
              [#-- Add contribution Button --]
              [#if editable]
                <div class="text-center"><div class="addFundingSource bigAddButton"><span class="glyphicon glyphicon-plus-sign"></span>[@s.text name="Add Contribution" /]</div></div>
              [/#if]
            </div>  
            
            [#-- LOCATION INFORMATION --]
            <div class="form-group">
              <h4 class="headTitle">Location information</h4> 
              <div class="informationWrapper simpleBox">
                [#-- YES/NO Global Dimension --]
                [#if editable] 
                <div class="form-group projectsGlobal">[@customForm.yesNoInput  label="projectDescription.globalDimensionQuestion" name="project.sGlobal" editable=editable inverse=false  cssClass="isGlobal" /] </div>
                <hr />
                [#else]
                <div class="form-group"><label for="">[@s.text name="projectDescription.globalDimension${((project.sGlobal == 'true')!false)?string('Yes','No')}" /]</label></div>
                [/#if]
                
                [#-- YES/NO Regional Dimension --]
                [#if editable]
                <div class="form-group projectsRegion">[@customForm.yesNoInput  label="projectDescription.regionalDimensionQuestion" name="project.sRegion" editable=editable inverse=false  cssClass="isRegional" /]</div>
                <hr />
                [#else]
                <div class="form-group"><label for="">[@s.text name="projectDescription.regionallDimension${((project.sRegion == 'true')!false)?string('Yes','No')}" /]</label></div>
                [/#if]
                
                [#-- REGIONAL SELECT --]
                <div class="regionsBox form-group" style="display:[#if project.sRegion??][#if project.sRegion=="true"]block[#else]none[/#if][#else]none[/#if]">
                  <div class="panel tertiary">
                    <div class="panel-head">
                      <label for=""> [@customForm.text name="projectDescription.selectRegions" readText=!editable /]:[@customForm.req required=editable /]</label><br />
                      <small style="color: #337ab7;">(Standart regions are defined by United Nations)</small>
                    </div>
                    <div id="regionList" class="panel-body" listname="project.regions"> 
                      <ul class="list">
                      [#if project.projectRegions?has_content]
                        [#list project.projectRegions as region]
                            <li id="" class="region clearfix col-md-3">
                            [#if editable ]
                              <div class="removeRegion removeIcon" title="Remove region"></div>
                            [/#if]
                              <input class="id" type="hidden" name="project.projectRegions[${region_index}].id" value="${region.id}" />
                              <input class="rId" type="hidden" name="project.projectRegions[${region_index}].locElement.id" value="${(region.locElement.id)!}" />
                              <span class="name">[@utilities.wordCutter string=(region.locElement.name)! maxPos=20 /]</span>
                              <div class="clearfix"></div>
                            </li>
                        [/#list]
                        [#else]
                        <p class="emptyText"> [@s.text name="No regions added yet." /]</p> 
                      [/#if]
                      </ul>
                      [#if editable ]
                        [@customForm.select name="" label=""  showTitle=false  i18nkey="" listName="regionLists" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className="regionSelect" editable=editable /]
                      [/#if] 
                    </div>
                  </div>
                </div>
                
                [#-- SELECT COUNTRIES --]
              <div class="countriesBox form-group" style="display:block">
                <div class="panel tertiary">
                  <div class="panel-head"><label for=""> [@customForm.text name="projectDescription.listCountries" readText=!editable /]:[@customForm.req required=editable/]</label></div>
                  <div id="countryList" class="panel-body" listname="project.countries"> 
                    <ul class="list">
                    [#if project.projectCountries?has_content]
                      [#list project.projectCountries as country]
                          <li id="" class="country clearfix col-md-3">
                          [#if editable ]
                            <div class="removeCountry removeIcon" title="Remove country"></div>
                          [/#if]
                            <input class="id" type="hidden" name="project.projectCountries[${country_index}].id" value="${(country.id)!-1}" />
                            <input class="cId" type="hidden" name="project.projectCountries[${country_index}].locElement.isoAlpha2" value="${(country.locElement.isoAlpha2)!}" />
                            <span class="name"><span> <i class="flag-sm flag-sm-${(country.locElement.isoAlpha2)!}"></i> [@utilities.wordCutter string=(country.locElement.name)! maxPos=20 /]</span></span>
                            <div class="clearfix"></div>
                          </li>
                      [/#list]
                      [#else]
                      <p class="emptyText"> [@s.text name="No countries added yet." /]</p> 
                    [/#if]
                    </ul>
                    [#if editable ]
                      [@customForm.select name="" label=""  showTitle=false  i18nkey="" listName="countryLists" keyFieldName="isoAlpha2"  displayFieldName="name"  multiple=false required=true  className="countriesSelect" editable=editable /]
                    [/#if] 
                    </div>
                  </div>
                </div>
                
              </div>
            </div>
            
          </div>
            
          [#-- Select the cross-cutting dimension(s) to this project? --]
          <div class="form-group">
            <label for="">[@customForm.text name="projectDescription.crossCuttingDimensions" readText=!editable/] [@customForm.req required=editable/]</label>
            [#assign crossCutingName = "project.projectCrosscutingTheme" /]
            <div class="checkbox crossCutting">
            [#if editable]
              <label class="col-md-3"><input type="checkbox" name="${crossCutingName}.climateChange"          class="optionable"  value="true" [#if (project.projectCrosscutingTheme.climateChange)!false ]checked="checked"[/#if] > Climate Change</label>
              <label class="col-md-3"><input type="checkbox" name="${crossCutingName}.gender"                 class="optionable"  value="true" [#if (project.projectCrosscutingTheme.gender)!false ]checked="checked"[/#if] > Gender</label>
              <label class="col-md-3"><input type="checkbox" name="${crossCutingName}.youth"                  class="optionable"  value="true" [#if (project.projectCrosscutingTheme.youth)!false ]checked="checked"[/#if] > Youth</label>
              <label class="col-md-3"><input type="checkbox" name="${crossCutingName}.policiesInstitutions"   class="optionable"  value="true" [#if (project.projectCrosscutingTheme.policiesInstitutions)!false ]checked="checked"[/#if] > Policies and Institutions</label>
              <label class="col-md-3"><input type="checkbox" name="${crossCutingName}.capacityDevelopment"    class="optionable"  value="true" [#if (project.projectCrosscutingTheme.capacityDevelopment)!false ]checked="checked"[/#if] > Capacity Development</label>
              <label class="col-md-3"><input type="checkbox" name="${crossCutingName}.bigData"                class="optionable"  value="true" [#if (project.projectCrosscutingTheme.bigData)!false ]checked="checked"[/#if] > Big Data</label>
              <label class="col-md-3"><input type="checkbox" name="${crossCutingName}.impactAssessment"       class="optionable"  value="true" [#if (project.projectCrosscutingTheme.impactAssessment)!false ]checked="checked"[/#if] > Impact Assessment</label>
              <label class="col-md-3"><input type="checkbox" name="${crossCutingName}.na"                     class="na"          value="true" [#if (project.projectCrosscutingTheme.na)!false ]checked="checked"[/#if] > N/A</label>
              <div class="clearfix"></div>
            [#else]
              [#if (project.projectCrosscutingTheme.climateChange)!false ]<p class="checked"> Climate Change</p>[/#if]
              [#if (project.projectCrosscutingTheme.gender)!false ]<p class="checked"> Gender</p>[/#if]
              [#if (project.projectCrosscutingTheme.youth)!false ]<p class="checked"> Youth</p>[/#if]
              [#if (project.projectCrosscutingTheme.policiesInstitutions)!false ]<p class="checked"> Policies and Institutions</p>[/#if]
              [#if (project.projectCrosscutingTheme.capacityDevelopment)!false ]<p class="checked"> Capacity Development</p>[/#if]
              [#if (project.projectCrosscutingTheme.bigData)!false ]<p class="checked"> Big Data</p>[/#if]
              [#if (project.projectCrosscutingTheme.impactAssessment)!false ]<p class="checked"> Impact Assessment</p>[/#if]
              [#if (project.projectCrosscutingTheme.na)!false ]<p class="checked"> N/A</p>[/#if]                     
            [/#if]
            </div>
          </div>
           
          [#-- Outputs --]
          <div class="form-group">      
            <div class="output panel tertiary">
              <div class="panel-head" ><label for="">[@customForm.text name="projectDescription.outputs" readText=!editable /]<span class="red">*</span></label></div> 
              <div class="panel-body" listname="project.outputs"> 
                <ul id="outputsBlock" class="list outputList">
                [#if  project.outputs?has_content]  
                  [#list project.outputs as output]
                     [@outputMacro element=output name="project.outputs" index=output_index isTemplate=false/]
                  [/#list] 
                [#else]
                  <p class="text-center outputInf"> [@s.text name="projectDescription.notOutputs" /] </p>  
                [/#if]  
                </ul>
                [#if editable]
                  <select name="" class="outputSelect">
                    <option value="-1">Select an option...</option>
                    [#list outputs as output]
                      <optgroup  label="${(output.outcome.composedName)!}">
                        [#list output.outputs as op]<option value="${(op.id)!}">${(op.composedName)!}</option>[/#list]
                      </optgroup>
                    [/#list]
                  </select>
                [/#if] 
              </div>
              [#-- Note --]
              [#if editable]
              <hr />
              <div class="note">
                If you don't find the <strong>output</strong> you are looking for, request to have it added by
                <a href="#" class="" data-toggle="modal" data-target="#requestModal">clicking here</a>
              </div>
              [/#if]
            </div>
          </div>
          
          
          
          
        </div>
      </div>
      [#-- Section Buttons & hidden inputs--]
      [#include "/WEB-INF/center/views/monitoring/project/buttons-projects.ftl" /]
         
      [/@s.form] 
    </div>  
</section>


[@fundingSourceMacro element={} name="project.fundingSources"  index=-1 isTemplate=true /]
[@outputMacro element={} name="project.outputs"  index=-1 isTemplate=true /]
[@usersForm.searchUsers/]

[#-- Request outputs popup --]
<div class="modal fade" id="requestModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="loading" style="display:none"></div>
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="exampleModalLabel">Request an Output</h4>
      </div>
      <div class="modal-body">
        <form>
          <div class="form-group">
            [@customForm.textArea name="outputName" i18nkey="Output Statement" required=true /]
          </div>
          <div class="form-group">
            <label for="outcomeID" >Outcome:<span class="red">*</span></label>
            <select id="outcomeID" name="outcomeID" class="">
              <option value="-1">Select an outcome...</option>
              [#list topicOutcomes as topicOutcome]
                <optgroup  label="${(topicOutcome.topic.researchTopic)!'No Research topic'}">
                  [#list topicOutcome.outcomes as outcome]<option value="${(outcome.id)!}">${(outcome.composedName)!}</option>[/#list]
                </optgroup>
              [/#list]
            </select>
          </div>
          <input type="hidden" name="programID" value="${(programID )!}"/> 
          <input type="hidden" name="projectID" value="${(projectID )!}"/> 
        </form>
        
        <div class="messageBlock" style="display:none">
          <div class="notyMessage">
            <h1 class="text-center brand-success"><span class="glyphicon glyphicon-ok-sign"></span></h1>
            <p  class="text-center col-md-12">
              [@s.text name="output.request.message" /]
            </p>
            <br />
            [#-- Buttons --]
            <div class="text-center">
              <button class="btn btn-danger" type="button" class="close" data-dismiss="modal" aria-label="Close">Close</button>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-footer"> 
        <button type="button" class="requestButton btn btn-primary"> <span class="glyphicon glyphicon-send"></span> Request</button>
      </div>
    </div>
  </div>
</div>

[#-- Region element template --]
<ul style="display:none">
  <li id="regionTemplate" class="region clearfix col-md-3">
      <div class="removeRegion removeIcon" title="Remove region"></div>
      <input class="id" type="hidden" name="project.projectRegions[-1].id" value="" />
      <input class="rId" type="hidden" name="project.projectRegions[-1].locElement.id" value="" />
      <span class="name"></span>
      <div class="clearfix"></div>
    </li>
</ul>

[#-- Country element template --]
<ul style="display:none">
  <li id="countryTemplate" class="country clearfix col-md-3">
      <div class="removeCountry removeIcon" title="Remove country"></div>
      <input class="id" type="hidden" name="project.projectCountries[-1].id" value="" />
      <input class="cId" type="hidden" name="project.projectCountries[-1].locElement.isoAlpha2" value="" />
      <span class="name"></span>
      <div class="clearfix"></div>
    </li>
</ul>
  
[#include "/WEB-INF/center/pages/footer.ftl"]


[#macro fundingSourceMacro element name index=-1 isTemplate=false]  
  [#local customName = "${name}[${index}]" /]
  [#local isSynced = (element.sync)!false ]
  [#local isAutofill = (element.autoFill)!false ]
  [#local isOCS = (element.centerFundingSyncType.id == 1)!true ]
  <div id="fundingSource-${isTemplate?string('template',(element.id)!)}" class="fundingSources fsSync simpleBox"  style="display:${isTemplate?string('none','block')}">
    [#-- Loading block --]
    <div class="loading syncBlock" style="display:none"></div>
    
    [#-- Remove Button --]
    [#if editable]
      [#if isAutofill]
        <div class="removeFundingSource"></div>
      [#else]
        <div class="removeFundingSource removeElement sm" title="Remove Funding Source"></div>        
      [/#if]       
    [/#if]
    
    [#-- Index --]
    <div class="leftHead sm">
      <span class="index">${index+1}</span>
      <span class="elementId"> Funding Source</span>
    </div>
    <br />

    [#-- Hidden inputs --]
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!-1}" />    
    
    [#-- Finance code --]
    <div class="form-group row">
      <div class="col-md-6">
        <div class="syncedBlock unsyncVisibles" style="display:${isSynced?string('block','none')};"></div>
        <label for="">The project contains information from:[@customForm.req required=editable /]</label>
        <div class="form-group">
        [#if syncTypes??]
          [#list syncTypes as syncType]
            [#assign syncCustomID = "radio-${syncType.id}-${index}"]
            <div class="radioFlat radio-inline">
              <input id="${syncCustomID}" class="radio-input radioSyncType" type="radio" name="${customName}.centerFundingSyncType.id" value="${syncType.id}" [#if element.centerFundingSyncType??][#if (element.centerFundingSyncType.id == syncType.id)!false]checked[/#if][#else][#if syncType_index = 0]checked[/#if][/#if] />
              <label for="${syncCustomID}" class="radio-label"> ${(syncType.syncName)!'{sync_name}'} </label>
            </div>
          [/#list]
        [/#if]
        </div>
      </div>
      <div id="disseminationUrl" class="col-md-6">
        <div class="url-field">
          [@customForm.input name="${customName}.code" i18nkey="Code" className="financeCode" type="text" disabled=!editable  required=true readOnly=isSynced  editable=editable /]
          <span class="financeCode-message"></span>
        </div>
        <div class="buttons-field">
          [#if editable]
            <div class="fillMetadata">
              <input type="hidden" name="${customName}.sync" value="${isSynced?string}" />
              [#-- Sync Button --]
              <div class="checkButton" style="display:${isSynced?string('none','block')};">[@s.text name="form.buttons.sync" /]</div>
              <div class="unSyncBlock" style="display:${isSynced?string('block','none')};">
                [#-- Update Button --]
                <div class="updateButton">[@s.text name="form.buttons.update" /]</div>
                [#-- Unsync Button --]
                <div class="uncheckButton">[@s.text name="form.buttons.unsync" /]</div>
              </div>
            </div>
          [/#if]
        </div>
      </div>
      <div class="metadata-output"></div>
    </div>
    
    
    <div class="form-group row">
      <div class="col-md-4 metadataElement-fundingTypeId isOCSBlock" style="display:${isOCS?string('block','none')}">
        <div class="syncedBlock unsyncVisibles" style="display:${isSynced?string('block','none')};"></div>
        [@customForm.select name="${customName}.centerFundingSourceType.id" label=""  i18nkey="Funding source type" listName="fundingSourceTypes" keyFieldName="id"  displayFieldName="name"  multiple=false required=true header=false className="metadataValue" editable=editable/]
      </div>
      <div class="col-md-4 metadataElement-crp isCRPProjectBlock" style="display:${isOCS?string('none','block')}">
        <div class="syncedBlock unsyncVisibles" style="display:${isSynced?string('block','none')};"></div>
        [@customForm.select name="${customName}.crp.id" label=""  i18nkey="CRP" listName="crps" keyFieldName="id"  displayFieldName="crpAcronymOrName"  multiple=false required=true header=false className="metadataValue"  editable=editable/]
      </div>
    </div>
    <div class="form-group metadataElement-description">
      [@customForm.input name="${customName}.title" i18nkey="Project Title" className="metadataValue" disabled=!editable required=false readOnly=isSynced editable=editable /]
    </div>
    <div class="form-group metadataElement-objectives">
      [@customForm.textArea name="${customName}.description" i18nkey="Project Description" className="metadataValue" disabled=!editable required=false readOnly=isSynced editable=editable /]
    </div>
    <div class="form-group row">
      <div class="col-md-4 metadataElement-startDate">
        [@customForm.input name="${customName}.startDate" i18nkey="Start Date" className="metadataValue"  disabled=!editable  required=true readOnly=isSynced editable=editable /]
      </div> 
      <div class="col-md-4 metadataElement-endDate">
        [@customForm.input name="${customName}.endDate" i18nkey="End Date" className="metadataValue" disabled=!editable required=false readOnly=isSynced editable=editable /]
      </div>
      <div class="col-md-4 metadataElement-extensionDate isOCSBlock" style="display:${isOCS?string('block','none')}">
        [@customForm.input name="${customName}.extensionDate" i18nkey="Extension Date" className="metadataValue"  disabled=!editable required=false readOnly=isSynced editable=editable /]
      </div>
    </div>
    <div class="form-group isOCSBlock metadataElement-originalDonorName" style="display:${isOCS?string('block','none')}">
      [@customForm.input name="${customName}.originalDonor" i18nkey="Original Donor" className="metadataValue" disabled=!editable required=false readOnly=isSynced editable=editable /]
    </div>
    <div class="form-group isOCSBlock metadataElement-directDonorName" style="display:${isOCS?string('block','none')}">
      [@customForm.input name="${customName}.directDonor" i18nkey="Direct Donor" className="metadataValue"  disabled=!editable required=false readOnly=isSynced editable=editable /]
    </div>
    
    <div class="form-group row isOCSBlock" style="display:${isOCS?string('block','none')}">
      <div class="col-md-6 metadataElement-grantAmount"> 
        [@customForm.input name="${customName}.totalAmount" i18nkey="Total amount (USD)" className="metadataValue currencyInput"  disabled=!editable required=false readOnly=isSynced editable=editable /]
      </div>
    </div>
    
  </div>
[/#macro]

[#macro outputMacro element name index=-1 isTemplate=false]  
  [#assign outputCustomName = "${name}[${index}]" /]
  <li id="output-${isTemplate?string('template',(element.id)!)}" class="outputs  borderBox expandableBlock row "  style="display:${isTemplate?string('none','block')}">
  <input type="hidden" name="${outputCustomName}.id" value="${(element.id)!}"/>
  <input type="hidden" class="outputId" name="${outputCustomName}.researchOutput.id" value="${(element.researchOutput.id)!}"/>
    [#if editable] [#--&& (isTemplate) --]
      <div class="removeLink">
        <div id="" class="removeOutput removeElement removeLink" title="[@s.text name='projectDecription.removeOutput' /]"></div>
      </div>
    [/#if]
    <div class="leftHead">
      <span class="index">O${(element.researchOutput.id)!}</span>
    </div>
    [#-- Output Title --]
    <div class="blockTitle closed form-group" style="margin-top:30px;">
      <label for="">Output statement:</label>
      <div class="oStatement">
        [#if element.researchOutput?? && element.researchOutput.title?has_content]
        ${(element.researchOutput.title)!'New output'}
        [#else]
        No Output
        [/#if]
      </div>
        
      <div class="clearfix"></div>
    </div>
    
    <div class="blockContent " style="display:none">
      <div class="form-group">
        <label for="">Research topic:</label>
        <div class="rTopic">${(element.researchOutput.researchOutcome.researchTopic.researchTopic)!}</div>
      </div>
      <div class="form-group">
        <label for="">Outcome:</label>
        <div class="outcome">${(element.researchOutput.researchOutcome.description)!}</div>
      </div>
    </div>
  </li>
[/#macro]