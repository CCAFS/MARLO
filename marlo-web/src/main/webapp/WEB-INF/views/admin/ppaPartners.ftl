[#ftl]
[#assign title = "PPA Partners" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [ "${baseUrl}/js/admin/ppaPartners.js","${baseUrl}/js/global/fieldsValidation.js" ] /]
[#assign customCSS = [ "${baseUrl}/css/admin/ppaPartners.css" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "ppaPartners" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"ppaPartners", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/images/global/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="ppaPartners.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]
        <h4 class="sectionTitle">[@s.text name="ppaPartners.title" /]</h4>
        <div class=" borderBox formWrapper " listname="loggedCrp.crpInstitutionsPartners">
            [#-- PPA Partners --]
  	      	<div  id="partnerContent" class="" >
  	      		[#list loggedCrp.crpInstitutionsPartners as ppaPartners]
  	      			[@intitutionMacro ppaPartners=ppaPartners index=institution_index /]
  	      	  [/#list]
  	      	</div>
  	      	[#--Select an institution --]
  	      	[#if editable]
  	      	<hr />
        		<div class="form-group">
        		  <label >Select an institution:</label>
        		  [@customForm.select name="" showTitle=false listName="" keyFieldName="" displayFieldName="" header=false editable=true  /]
      			</div>
      			[/#if]
      	</div>
      	
      	[#-- Section Buttons--]
        <div class="buttons">
          <div class="buttons-content">
          [#if editable]
            <a href="[@s.url][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span> [@s.text name="form.buttons.back" /]</a>
            [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /][/@s.submit]
          [#else]
            [#if canEdit]
              <a href="[@s.url][@s.param name="edit" value="true"/][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> [@s.text name="form.buttons.edit" /]</a>
            [/#if]
          [/#if]
          </div>
        </div>
        
        [/@s.form]
      	
      </div>
    </div>
  </div>
</section>

[#-- Institutions array --]
<ul style="display:none">
[#list institutions as institution]
  <li id="institutionArray-${(institution)!}">${(institution.composedName?html)!}</li>
[/#list]
</ul>


[#-- Partner institution --]
[@intitutionMacro ppaPartners={} isTemplate=true /]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro intitutionMacro ppaPartners index=0 isTemplate=false]
	<div id="institution-${isTemplate?string('template','')}" class="institution" style="display:${isTemplate?string('none','block')}">
		[#-- Title --]
		<span class="title ">${(ppaPartners.institution.composedName)!'Null'} </span>
		[#-- Hidden inputs --]
		<input class="institutionId" type="hidden" name="loggedCrp.crpInstitutionsPartners[${index}].institution.id" value="${(ppaPartners.institution.id)!'null'}"/>
		<input class="id" type="hidden" name="loggedCrp.crpInstitutionsPartners[${index}].id" value="${(ppaPartners.id)!}"/>
		[#if editable && ppaPartners?hasContent && action.canBeDeleted(ppaPartners.id,ppaPartners.class.name) ]
		  <span class="glyphicon glyphicon-remove pull-right remove-userItem delete red" aria-hidden="true"></span>
		[/#if]
		[#if  !ppaPartners?hasContent]
		  <span class="glyphicon glyphicon-remove pull-right remove-userItem delete red" aria-hidden="true"></span>
		[/#if]
	</div>
[/#macro]

