[#ftl]
[#assign title = "PPA Partners" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [
  "${baseUrl}/global/js/usersManagement.js", 
  "${baseUrlMedia}/js/admin/ppaPartners.js",
  "${baseUrl}/global/js/fieldsValidation.js" 
  ] 
/]
[#assign customCSS = [ "${baseUrlMedia}/css/admin/ppaPartners.css" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "ppaPartners" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"ppaPartners", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="ppaPartners.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]
        <h4 class="sectionTitle">[@s.text name="ppaPartners.title" /]</h4>
        <div class=" formWrapper " listname="loggedCrp.crpInstitutionsPartners">
            [#-- PPA Partners --]
  	      	<div  id="partnerContent" class="" >
  	      		[#list loggedCrp.crpInstitutionsPartners as ppaPartner]
  	      			[@intitutionMacro ppaPartner=ppaPartner index=institution_index /]
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

[#-- Search users Interface --]
[#import "/WEB-INF/global/macros/usersPopup.ftl" as usersForm/]
[@usersForm.searchUsers/]

<ul style="display:none">
  [#-- User template --]
  [@userItem element={} index=-1 name="loggedCrp.crpInstitutionsPartners[-1].contactPoints" template=true /]
</ul>

[#-- Institutions array --]
<ul style="display:none">
[#list institutions as institution]
  <li id="institutionArray-${(institution)!}">${(institution.composedName?html)!}</li>
[/#list]
</ul>


[#-- Partner institution --]
[@intitutionMacro ppaPartner={} isTemplate=true /]

[#include "/WEB-INF/crp/pages/footer.ftl" /]

[#macro intitutionMacro ppaPartner index=0 isTemplate=false]
  [#local customName = "loggedCrp.crpInstitutionsPartners[${index}]"]
  <div id="institution-${isTemplate?string('template',index)}" class="institution ppaPartner borderBox" style="display:${isTemplate?string('none','block')}">
    [#-- Hidden inputs --]
    <input class="institutionId" type="hidden" name="${customName}.institution.id" value="${(ppaPartner.institution.id)!'null'}"/>
    <input class="id" type="hidden" name="${customName}.id" value="${(ppaPartner.id)!}"/>
    [#-- Remove --]
    [#if (ppaPartner?hasContent && action.canBeDeleted(ppaPartner.id,ppaPartner.class.name)) || !ppaPartner?hasContent ]
      <div class="removeLink"><div class="delete removeElement removeLink" title="Remove"></div></div>
    [#else]
      <div class="removeLink"><div class="removeElement removeLink disable" title="Cannot be removed"></div></div>
    [/#if]
    
    [#-- Title --]
    <h5 class="title sectionSubTitle">${(ppaPartner.institution.composedName)!'Null'} </h5>
    [#-- Show contact points --]
    [#if cpRole??]
    [#-- Contact Points --]
    <label for="">Contact Point(s):</label>
    <div class="usersBlock leaders form-group simpleBox" listname="${customName}.contactPoints">
      [#-- List --]
      <div class="items-list" listname="${customName}.contactPoints" >
        <ul>
        [#if ppaPartner.contactPoints??]
          [#list ppaPartner.contactPoints as contactPoint]
            [@userItem element=contactPoint index=contactPoint_index name="${customName}.contactPoints"/]
          [/#list]
        [/#if]
        </ul>
      </div>
      [#-- Add person Button --]
      [#if editable]
      <div class="text-center">
        <div class="searchUser button-green"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addPerson" /]</div>
      </div>
      [/#if]
    </div>
    [/#if]
    [#-- End show contact points --]
  </div>
[/#macro]

[#macro userItem element index name template=false]
  [#assign userCustomName = "${name}[${index}]" /]
  <li id="user-${template?string('template',index)}" class="user userItem" style="display:${template?string('none','block')}">
    [#-- User Name --]
    <span class="glyphicon glyphicon-user" aria-hidden="true"></span> <span class="name"> ${(element.user.getComposedName()?html)!'Unknown user'}</span>
    [#-- Hidden inputs --]
    <input class="user" type="hidden" name="${userCustomName}.user.id" value="${(element.user.id)!}"/>
    <input class="id" type="hidden" name="${userCustomName}.id" value="${(element.id)!}"/>
    [#-- Remove Button --]
    [#if editable]
      [#if template || action.canBeDeleted(element.id, element.class.name)!false]
        <span class="glyphicon glyphicon-remove pull-right remove-userItem" aria-hidden="true"></span>
      [#else]
        <span class="glyphicon glyphicon-remove pull-right" style="color:#ccc" aria-hidden="true" title="Can not be deleted"></span>
      [/#if]
    [/#if]
  </li>
[/#macro]