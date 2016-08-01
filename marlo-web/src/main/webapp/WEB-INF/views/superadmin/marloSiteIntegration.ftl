[#ftl]
[#assign title = "MARLO Sites Integration" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["flat-flags"] /]
[#assign customJS = [ "${baseUrl}/js/superadmin/marloSLOs.js" ] /]
[#assign customCSS = [ "${baseUrl}/css/superadmin/superadmin.css" ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "siteIntegration" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"marloSiteIntegration", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
<hr />

<div class="container">
  [#include "/WEB-INF/global/pages/breadcrumb.ftl" /]
</div>
[#include "/WEB-INF/global/pages/generalMessages.ftl" /]

[#assign siteIntegrationList = [
 {"code": "BD", "name": "Bangladesh","plus": "++", "center": "IRRI and WorldFish (co-lead)", "leaders": [{"composedName":"Paul Fox (IRRI)" },{"composedName":"Craig Meisner (WorldFish)" } ] },
 {"code": "ET", "name": "Ethiopia","plus": "++", "center": "ILRI", "leaders": [{"composedName":"Boni Moyo (ILRI)" } ]},
 {"code": "NI", "name": "Nicaragua","plus": "++", "center": "CIAT", "leaders": [{"composedName":"Maya Rajasekharan (CIAT)" } ]},
 {"code": "NG", "name": "Nigeria", "plus": "++", "center": "IITA", "leaders": [{"composedName":"Alfred Dixon (IITA)" } ] },
 {"code": "TZ", "name": "Tanzania", "plus": "++", "center": "IITA", "leaders": [{"composedName":"Regina Kapinga (IITA)" } ]},
 {"code": "VN", "name": "Vietnam", "plus": "++" , "center": "CIAT (ICRAF to closely support)", "leaders": [{"composedName":"Dindo Campilan (CIAT)" } ]},
 {"code": "BF", "name": "Burkina Faso", "plus": "+", "center": "CIFOR", "leaders": [{"composedName":"Mathurin Zida (CIFOR)" } ]},
 {"code": "CM", "name": "Cameroon", "plus": "+","center": "ICRAF", "leaders": [{"composedName":"Zac Tchoundjeu (ICRAF)" } ]},
 {"code": "CD", "name": "DRC", "plus": "+","center": "IITA", "leaders": [{"composedName":"Nzola Mahungu (IITA)" } ]},
 {"code": "GH", "name": "Ghana", "plus": "+","center": "IWMI", "leaders": [{"composedName":"Funke Cofie (IWMI)" } ]},
 {"code": "IN", "name": "India", "plus": "+","center": "ICRISAT", "leaders": [{"composedName":"Peter Carberry (ICRISAT)" } ]},
 {"code": "KE", "name": "Kenya", "plus": "+","center": "ICRAF", "leaders": [{"composedName":"Jonathan Muriuki (ICRAF)" } ]},
 {"code": "MW", "name": "Malawi", "plus": "+","center": "CIP (for first two years then followed by ICRISAT)", "leaders": [{"composedName":"Paul Demo (CIP)" } ]},
 {"code": "ML", "name": "Mali", "plus": "+","center": "ICRISAT", "leaders": [{"composedName":"Ramadjita Tabo (ICRISAT)" } ]},
 {"code": "MZ", "name": "Mozambique", "plus": "+","center": "CIP", "leaders": [{"composedName":"Adiel Mbabu (CIP)" }, {"composedName":"Maria Andrade (CIP)" } ]},
 {"code": "NP", "name": "Nepal", "plus": "+","center": "IWMI and CIMMYT (co-lead)", "leaders": [{"composedName":"Arun Joshi (CIMMYT)" }, {"composedName":" Fraser Sugden (IWMI)" }, {"composedName":"Srabani Roy" } ]},
 {"code": "NE", "name": "Niger", "plus": "+","center": "ICRISAT", "leaders": [{"composedName":"Malick Ba (ICRISAT)" } ]},
 {"code": "RW", "name": "Rwanda", "plus": "+","center": "CIP (rotating leadership, starting with CIP and then CIAT)", "leaders": [ {"composedName":"Kirimi Sindi (CIP)"} ]},
 {"code": "UG", "name": "Uganda", "plus": "+","center": "CIP and Bioversity", "leaders": [ {"composedName":"Eldad Karamara (Bioversity)"} ]},
 {"code": "ZM", "name": "Zambia", "plus": "+","center": "CIMMYT", "leaders": [ {"composedName":"Peter Setimela (CIMMYT)"} ]},
 {"code": "ID", "name": "Indonesia", "plus": "","center": "CIFOR", "leaders": [ {"composedName":"Robert Nasi"} ]},
 {"code": "PH", "name": "Philippines", "plus": "","center": "IRRI", "leaders": [ {"composedName":"Bas Bourman"} ]}
]/]

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/superadmin/menu-superadmin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]
        <h4 class="sectionTitle">Site Integration</h4>
        <div class="col-md-12">
          <div class="col-md-3"><strong>Country</strong></div>
          <div class="col-md-4"><strong>Focal Point(s)</strong></div>
          <div class="col-md-5"><strong>CGIAR coordinating Center</strong></div>
        </div>
        <div class="clearfix"></div>
        <div class="idos-list">
          [#if siteIntegrationList?has_content]
            [#list siteIntegrationList as site]
              [@srfIdoMacro element=site name="siteIntegrationList[${site_index}]" index=site_index  /]
            [/#list]
          [#else]
            
          [/#if]
        </div>
        [#-- Add Outcome Button --]
        <div class="addSiteIntegration bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addSiteIntegration"/]</div>
        
        [#-- Section Buttons--]
        <div class="buttons">
          <div class="buttons-content">
            [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /][/@s.submit]
          </div>
        </div>
        
        [/@s.form]
        
      </div>
    </div>
  </div>
</section>

[#-- IDO Template --]
[@srfIdoMacro element={} name="idosList[-1]" index=-1 isTemplate=true /]

[#-- Sub-IDO Template --]
[@srfSubIdoMacro element={} name="idosList[-1].srfSubIdos[-1]" index=-1 isTemplate=true /]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro srfIdoMacro element name index isTemplate=false]
  <div id="srfIdo-${isTemplate?string('template',index)}" class="srfIdo borderBox" style="display:${isTemplate?string('none','block')}">
    [#-- Remove Button --]
    <div class="remove-element removeElement sm" title="Remove"></div>
    
    [#-- IDO Title --]
    <div class="blockTitle closed">
      <div class="row">
        <div class="col-md-3"><i class="flag-sm flag-sm-${(element.code?upper_case)!}"></i> ${(element.name)!'New Site'} <strong>${(element.plus)!}</strong></div>
        <div class="col-md-4"><span>${(element.leaders[0].composedName)!}</span> </div>
        <div class="col-md-5"><small>(${(element.center)!})</small></div>
      </div>
    </div>
    
    <div class="blockContent" style="display:none">
      <hr />
      
      [#-- Institution / Organization --]
      <div class="form-group">
      </div>
      
      [#-- Leaders --]
      <div class="form-group">
      </div>
      
    </div>
  </div>
[/#macro]

[#macro srfSubIdoMacro element name index isTemplate=false]
  <div id="srfSubIdo-${isTemplate?string('template',index)}" class="srfSubIdo form-group" style="position:relative;display:${isTemplate?string('none','block')}">
    [#-- Remove Button --]
    <div class="remove-element removeIcon" title="Remove"></div>
    [#-- Sub-IDO ID --]
    <input type="hidden" name="${name}.id" value="${(element.id)!}" />
    [#-- Sub-IDO Description --]
    [@customForm.input name="${name}.description" value="${(element.description)!}" showTitle=false type="text" className="description" required=true /]
    <div class="clearfix"></div>
  </div>
[/#macro]
