[#ftl]


[#macro deliverableGeographicScope name element]
  <div class="block-geographicScope geographicScopeBlock">
  
    [#assign geographicScopeList = (element.geographicScopes)![] ]
    [#assign isRegional =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeRegional) /]
    [#assign isMultiNational = findElementID(geographicScopeList,  action.reportingIndGeographicScopeMultiNational) /]
    [#assign isNational =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeNational) /]
    [#assign isSubNational =   findElementID(geographicScopeList,  action.reportingIndGeographicScopeSubNational) /]

    <div class="form-group">
      <div class="row">
        <div class="col-md-6">
          [#-- Geographic Scope --]
          [@customForm.elementsListComponent name="${name}.geographicScopes" indexLevel=3 elementType="repIndGeographicScope" elementList=element.geographicScopes label="deliverable.geographicScope" listName="repIndGeographicScopes" keyFieldName="id" displayFieldName="name" required=true /]
        </div>
      </div>

      <div class="form-group regionalBlock" style="display:${(isRegional)?string('block','none')}">
        [#-- Regional scope --]
        [@customForm.elementsListComponent name="${name}.geographicRegions" indexLevel=3  elementType="locElement" elementList="element.geographicRegions" label="deliverable.region"  listName="repIndRegions" keyFieldName="id" displayFieldName="composedName" required=false /]
      </div>
    
      <div class="form-group nationalBlock" style="display:${(isMultiNational || isNational || isSubNational)?string('block','none')}">
        [#-- Multinational, National and Subnational scope --]
        [@customForm.select name="${name}.geographicCountries"  label="" i18nkey="" listName="element.geographicCountries" keyFieldName="isoAlpha2"  displayFieldName="name" value="deliverable.countriesIds" multiple=true required=true className="countriesSelect" disabled=!editable/]
      </div>
    </div>
   
  </div>
[/#macro]


[#function findElementID list id]
  [#list (list)![] as item]
    [#if (item.repIndGeographicScope.id == id)!false][#return true][/#if]
  [/#list]
  [#return false]
[/#function]

[#macro macroTest]
 <h1>Hello Macro </h1>
[/#macro]