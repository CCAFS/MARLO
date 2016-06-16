[#ftl]
[#assign title = "PPA Partners" /]
[#assign pageLibs = ["select2","bootstrap-select"] /]
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
      
        <h4 class="text-center">PPA Partners</h4>
        <div class=" borderBox formWrapper ">
 
          [@s.form action=actionName enctype="multipart/form-data" ]  
  	      	<div class="col-md-12" id="partnerContent">
  	      		[#list crpInstitutions as crpInstitution]
  	      			[@intitutionMacro institution=institution index=institution_index /]
  	      		 [/#list]
  	      	</div>
	      	
      		
        		<div class="form-group">
        		  [@customForm.select name="" showTitle=false placeholder="Select an option..." className="selectpicker col-md-12" listName="institutions" keyFieldName="id" displayFieldName="name" editable=true  /]
        		
        			<div class="clearfix"></div>
      			</div>
  						
        		<div class="buttons">
              [@s.submit  type="button" name="save" cssClass="center-block"][@s.text name="form.buttons.save" /][/@s.submit]
            </div>
      			
			
          [/@s.form]
      	</div>
      	
      </div>
    </div>
  </div>
</section>

[@intitutionMacro institution={} isTemplate=true /]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro intitutionMacro institution index=0 isTemplate=false]
	<div id="institution-${isTemplate?string('template','')}" class="institution col-md-12" style="display:${isTemplate?string('none','block')}">
		<span class="index hidden" >${index+1}</span>
		<span class="title col-md-11">${(crpInstitution.name)!'Null'} </span>
		<span class="delete col-md-1 glyphicon glyphicon-remove red" ></span>
		
		<input type="hidden" class="id" name="institutions[${index}]" value="${(crpInstitution.id)!'-1'}"/>
	</div>
[/#macro]

