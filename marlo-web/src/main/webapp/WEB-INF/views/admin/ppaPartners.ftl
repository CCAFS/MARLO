[#ftl]
[#assign title = "PPA Partners" /]
[#assign pageLibs = ["select2","bootstrap-select"] /]
[#assign customJS = [ "${baseUrl}/js/admin/ppaPartners.js" ] /]
[#assign customCSS = [ "${baseUrl}/css/admin/ppaPartners.css" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "ppaPartners" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"siteIntegration", "nameSpace":"", "action":""}
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
      		
      		[#assign institutions = [ 
        	{
        		'name': '3R-Acacia Water',
        		'id': 1
        	},
        	{
        		'name': 'Aarhus university',
        		'id': 2
        	}
       		] 
        /]
        
	      	<div class="col-md-12" id="partnerContent">
	      		[#list institutions as institution]
	      			[@intitutionMacro institution=institution index=institution_index /]
	      		 [/#list]
	      	</div>
	      	
      		
      		<div class="form-group">
        		<select id="partnerSelect" class="selectpicker col-md-12"   data-live-search="true" data-show-subtext="true" >
        		  <option ></option>
      			  <option data-subtext="Netherlands" value="1" id="3R-Acacia Water">3R-Acacia Water</option>
      			  <option data-subtext="Denmark" value="2" id="Aarhus university">Aarhus university</option>
      			  <option data-subtext="Mali" value="3" id="Agence Nationale de la Métérologie du Mali">Agence Nationale de la Métérologie du Mali</option>
      			</select>
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
		<span class="title col-md-11">${(institution.name)!'Null'} </span>
		<span class="delete col-md-1 glyphicon glyphicon-remove red" ></span>
		
		<input type="hidden" class="id" name="institutions[${index}]" value="${(institution.id)!'-1'}"/>
	</div>
[/#macro]

