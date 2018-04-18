[#ftl]
<div class="simpleBox">
  [#-- Title --] 
  <div class="form-group">
    [@customForm.input name="" value="${(title)!}" type="text" i18nkey="projectInnovations.title"  placeholder="" className="limitWords-20" required=true editable=editable /]
  </div>

  [#-- Narrative --] 
  <div class="form-group">
    [@customForm.textArea name="" value="${(narrative)!}" i18nkey="projectInnovations.narrative"  placeholder="" className="limitWords-50" required=true editable=editable /]
  </div>

  [#-- Phase of research and Stage of innovation --] 
  <div class="form-group row">
    <div class="col-md-6 ">
      [@customForm.select name="" label=""  i18nkey="projectInnovations.phase" listName="deliverableTypeParent" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className=" form-control input-sm" editable=editable/]
    </div>
    <div class="col-md-6 ">
      [@customForm.select name="" label=""  i18nkey="projectInnovations.stage" listName="deliverableSubTypes" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className=" form-control input-sm" editable=editable/]
    </div>
  </div>

  [#-- Geographic scope and innovation type --] 
  <div class="form-group row">
    <div class="col-md-6 ">
      [@customForm.select name="" label=""  i18nkey="projectInnovations.geographicScope" listName="deliverableTypeParent" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className=" form-control input-sm" editable=editable/]
    </div>
    <div class="col-md-6 ">
      [@customForm.select name="" label=""  i18nkey="projectInnovations.innovationType" listName="deliverableSubTypes" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className=" form-control input-sm" editable=editable/]
    </div>
  </div>

  [#-- Region (if scope is Region) --] 
  <div class="form-group row">
    <div class="col-md-6 ">
      [@customForm.select name="" label=""  i18nkey="projectInnovations.region" listName="deliverableTypeParent" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className=" form-control input-sm" editable=editable/]
    </div>
    <div class="col-md-6 ">
    </div>
  </div>

  [#-- FIX FORMAT--]
  [#-- Country(ies) (if scope is Multi-national, National or Sub-National)  --] 
      [#-- <h5 class="sectionSubTitle">[@s.text name="projectPartners.countryOffices${action.hasSpecificities('crp_partners_office')?string('.required','')}" /] <small>[@customForm.req required=action.hasSpecificities('crp_partners_office') /]</small></h5> --]
      <label for="">[@s.text name="projectInnovations.countries" /]:</label>
      <div class="countries-list items-list simpleBox" listname="selectedLocations">
        <ul class="">
          [#if (element.selectedLocations?has_content)!false]
            [#list element.selectedLocations as locElement]
              [@locElementMacro element=locElement!{} name="selectedLocations" index=locElement_index /]
            [/#list]
          [#else] 
            <p class="message text-center">No country added</p>
          [/#if]
        </ul>
        <div class="clearfix"></div> 
        [#-- Add Location Element --]
        [#if editable]
          <hr />
          <div class="form-group">
            [@customForm.select name="" showTitle=false i18nkey="location.select.country" listName="${name}.institution.locations" header=true keyFieldName="locElement.isoAlpha2" displayFieldName="composedName" value="id" placeholder="Select a country..." className="countriesList"/]
          </div>
        [/#if]
      </div>

  [#-- Specify next user organizational type (Only if stage 4) --]
  <div class="panel tertiary">
   <div class="panel-head"><label for=""> [@customForm.text name="projectInnovations.nextUserOrganizationalType" readText=!editable /]:[@customForm.req required=editable /]</label></div>
    <div id="fundingSourceList" class="panel-body" listname="deliverable.fundingSources"> 
      <ul class="list">
      [#if deliverable.fundingSources?has_content]
        [#list deliverable.fundingSources as element]
          <li class="fundingSources clearfix">
            [#if editable]<div class="removeFundingSource removeIcon" title="Remove funding source"></div>[/#if] 
            <input class="id" type="hidden" name="deliverable.fundingSources[${element_index}].id" value="${(element.id)!}" />
            <span class="name">
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
        [@customForm.select name="deliverable.fundingSource.id" label=""  showTitle=false  i18nkey="" listName="fundingSources" keyFieldName="id"  displayFieldName="composedName"  header=true required=true  className="fundingSource" editable=editable/]
      [/#if] 
    </div>
  </div>
  
  [#-- Selected options List --]
  <div style="display:none">
    [#if fundingSources?has_content]
      [#list fundingSources as element]
        <span id="">
          <span class="description">${(element.fundingSourceInfo.title)!}</span><br />
        </span>
      [/#list]
    [/#if]
  </div>

  [#-- Specify an Outcome Case Study (Only if stage 4) --]
  <div class="form-group">
  [@customForm.select name="" label=""  i18nkey="projectInnovations.outcomeCaseStudy" listName="keyOutputs" keyFieldName="id"  displayFieldName="composedName"  multiple=false required=true  className="keyOutput" editable=editable/]
  </div>

  [#-- Novel or adaptative research --] 
  <div class="form-group">
    [@customForm.textArea name="" value="${(narrative)!}" i18nkey="projectInnovations.novelOrAdaptative"  placeholder="" className="limitWords-100" required=true editable=editable /]
  </div>

  [#-- Evidence Link --] 
  <div class="form-group">
    [@customForm.input name="" value="${(title)!}" type="text" i18nkey="projectInnovations.evidenceLink"  placeholder="https://example.com" className="" required=false editable=editable /]
  </div>

  [#-- Or Deliverable ID (optional) --]
  <div class="panel tertiary">
   <div class="panel-head"><label for=""> [@customForm.text name="projectInnovations.nextUserOrganizationalType" readText=!editable /]:[@customForm.req required=editable /]</label></div>
    <div id="fundingSourceList" class="panel-body" listname="deliverable.fundingSources"> 
      <ul class="list">
      [#if deliverable.fundingSources?has_content]
        [#list deliverable.fundingSources as element]
          <li class="fundingSources clearfix">
            [#if editable]<div class="removeFundingSource removeIcon" title="Remove funding source"></div>[/#if] 
            <input class="id" type="hidden" name="deliverable.fundingSources[${element_index}].id" value="${(element.id)!}" />
            <span class="name">
                <span>${(element.fundingSourceInfo.id)!} - </span>
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
        [@customForm.select name="deliverable.fundingSource.id" label=""  showTitle=false  i18nkey="" listName="fundingSources" keyFieldName="id"  displayFieldName="composedName"  header=true required=true  className="fundingSource" editable=editable/]
      [/#if] 
    </div>
  </div>
  
  [#-- Selected options List --]
  <div style="display:none">
    [#if fundingSources?has_content]
      [#list fundingSources as element]
        <span id="">
          <span>${(element.fundingSourceInfo.id)!} - </span>
          <span class="description">${(element.fundingSourceInfo.title)!}</span><br />
        </span>
      [/#list]
    [/#if]
  </div>

  [#-- Contributing CRPs/Platforms --]
  <div class="panel tertiary">
   <div class="panel-head"><label for=""> [@customForm.text name="projectInnovations.contributing" readText=!editable /]:[@customForm.req required=editable /]</label></div>
    <div id="fundingSourceList" class="panel-body" listname="deliverable.fundingSources"> 
      <ul class="list">
      [#if deliverable.fundingSources?has_content]
        [#list deliverable.fundingSources as element]
          <li class="fundingSources clearfix">
            [#if editable]<div class="removeFundingSource removeIcon" title="Remove funding source"></div>[/#if] 
            <input class="id" type="hidden" name="deliverable.fundingSources[${element_index}].id" value="${(element.id)!}" />
            <span class="name">
                <span>${(element.fundingSourceInfo.id)!} - </span>
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
        [@customForm.select name="deliverable.fundingSource.id" label=""  showTitle=false  i18nkey="" listName="fundingSources" keyFieldName="id"  displayFieldName="composedName"  header=true required=true  className="fundingSource" editable=editable/]
      [/#if] 
    </div>
  </div>
  
  [#-- Selected options List --]
  <div class="row" style="display:none">
    [#if fundingSources?has_content]
      [#list fundingSources as element]
      <div class="col-md-3">
        <span id="">${name!}</span>
      </div>
      [/#list]
    [/#if]
  </div>

  [#-- Gender Relevance --]
  <div class="relevance-container">
    <ul>
      <li>
        [@customForm.radioButtonGroup label="projectInnovations.genderRelevance" name=""  listName="" i18nkey="projectInnovations.genderRelevance.notTargeted" value="" required=true checked="" editable=editable]
      </li>
    </ul>

    [#-- brief explanation and evidence --] 
    <div class="form-group">
      [@customForm.textArea name="" value="${(narrative)!}" i18nkey="projectInnovations.genderRelevance.explanation"  placeholder="" className="limitWords-100" required=true editable=editable /]
    </div>
  </div>

</div>

[#macro locElementMacro element name index isTemplate=false ]
  <li id="locElement-${isTemplate?string('template', index)}" class="locElement userItem" style="display:${isTemplate?string('none','block')}">
    [#assign locElementName = "${name}[${index}]" ]
    [#-- Remove Button --]
    [#if editable]<div class="removeLocElement removeIcon" title="Remove Location"></div>[/#if] 
    
    [#-- Location Name --]
    <span class="flag-icon"><i class="flag-sm flag-sm-${(element.locElement.isoAlpha2?upper_case)!}"></i></span> <span class="name">${(element.composedName)!'{name}'}</span><br />
    
    [#-- Hidden inputs --]
    <input type="hidden" class="locElementCountry" name="${locElementName}.locElement.isoAlpha2" value="${(element.locElement.isoAlpha2)!}" /> 
  </li>
[/#macro]