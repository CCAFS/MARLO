[#ftl]


[#macro deliverableGeographicScope name]
  <div class="block-geographicScope geographicScopeBlock">
  [#--
    [#assign geographicScopeList = (deliverable.geographicScopes)![] ]
    [#assign isRegional =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeRegional) /]
    [#assign isMultiNational = findElementID(geographicScopeList,  action.reportingIndGeographicScopeMultiNational) /]
    [#assign isNational =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeNational) /]
    [#assign isSubNational =   findElementID(geographicScopeList,  action.reportingIndGeographicScopeSubNational) /]
    --]
    <div class="form-group">
      <div class="row">
        <div class="col-md-6">
          [#-- Geographic Scope --]
          [@customForm.elementsListComponent name="${name}.geographicScopes" elementType="repIndGeographicScope" elementList="" label="deliverable.geographicScope" listName="repIndGeographicScopes" keyFieldName="id" displayFieldName="name" required=true /]
        </div>
      </div>
      [#--  style="display:${(isRegional)?string('block','none')}"  --]
      <div class="form-group regionalBlock" >
        [#-- Regional scope --]
        [@customForm.elementsListComponent name="${name}.Regions" elementType="locElement" elementList="" label="deliverable.region"  listName="repIndRegions" keyFieldName="id" displayFieldName="composedName" required=false /]
      </div>
      [#--  style="display:${(isMultiNational || isNational || isSubNational)?string('block','none')}"  --]
      <div class="form-group nationalBlock" >
        [#-- Multinational, National and Subnational scope --]
        [@customForm.select name="${name}.countriesIds" label="" i18nkey="" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="deliverable.countriesIds" multiple=true required=true className="countriesSelect" disabled=!editable/]
      </div>
    </div>
   
  </div>
[/#macro]

[#macro macroTest]
 <h1>Hello Macro </h1>
[/#macro]