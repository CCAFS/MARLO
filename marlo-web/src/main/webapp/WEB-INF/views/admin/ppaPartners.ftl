[#ftl]
[#assign title = "PPA Partners" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["bootstrap-select"] /]
[#assign customJS = [ "${baseUrl}/js/admin/ppaPartners.js" ] /]
[#assign customCSS = [ "${baseUrl}/css/admin/ppaPartners.css" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "ppaPartners" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"ppaPartners", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]
        <h4 class="text-center">[@s.text name="ppaPartners.title" /]</h4>
        <div class=" borderBox formWrapper ">
            [#-- PPA Partners --]
  	      	<div  id="partnerContent" class="" >
  	      		[#list loggedCrp.crpInstitutionsPartners as ppaPartners]
  	      			[@intitutionMacro ppaPartners=ppaPartners index=institution_index /]
  	      		 [/#list]
  	      	</div>
  	      	[#--Select an institution --]
  	      	[#if editable]  	      	
        		<div class="form-group">
        		<div class="clearfix"></div>
        		<label >Select an institution:</label>
        		  [@customForm.select name="" showTitle=false placeholder="form.select.placeholder" className="selectpicker col-md-12" listName="institutions" keyFieldName="id" displayFieldName="composedName" editable=true  /]
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

[@intitutionMacro ppaPartners={} isTemplate=true /]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro intitutionMacro ppaPartners index=0 isTemplate=false]
	<div id="institution-${isTemplate?string('template','')}" class="institution col-md-12" style="display:${isTemplate?string('none','block')}">
		<span class="index hidden" >${index+1}</span>
		<span class="title col-md-11">${(ppaPartners.institution.composedName)!'Null'} </span>
		<input class="institutionId" type="hidden" name="loggedCrp.crpInstitutionsPartners[${index}].institution.id" value="${(ppaPartners.institution.id)!'null'}"/>
		<input class="id" type="hidden" name="loggedCrp.crpInstitutionsPartners[${index}].id" value="${(ppaPartners.id)!}"/>
		[#if editable]
		<span class="delete col-md-1 glyphicon glyphicon-remove red" ></span>
		[/#if]
	</div>
[/#macro]

