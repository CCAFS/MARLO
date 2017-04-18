[#ftl]
[#assign title = "MARLO Users" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [ "${baseUrl}/js/superadmin/marloUsers.js" ] /]
[#assign customCSS = [ "${baseUrl}/css/superadmin/superadmin.css" ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "users" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"marloUsers", "nameSpace":"", "action":""}
]/]

 
[#include "/WEB-INF/global/pages/header.ftl" /]
<hr />

<div class="container">
  [#include "/WEB-INF/global/pages/breadcrumb.ftl" /]
</div>
[#include "/WEB-INF/global/pages/generalMessages.ftl" /]

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/superadmin/menu-superadmin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]
        
        <input type="hidden" class="isNewUser" name="isNewUser" value=""/>
        
        [#-- System Level Outcomes --]
        <h4 class="sectionTitle">[@s.text name="Users" /]</h4>
        <div class="row">
          <div class="col-md-7 form-group">
            [@customForm.input name="user.email" i18nkey="Search by user email"  className="checkEmail" type="text"  required=true editable=true /]
          </div>
          <div class="col-md-5">
          <br />
            <span class="infoService"></span>
          </div>
        </div>
        
        <div class="borderBox form-group">
            <h4 style="text-align:center;">User Data</h4>
          <div class="row">
            <div class="col-md-2">
              [@customForm.input name="user.id" i18nkey="Id" value="" className="userId" type="text"  required=true readOnly=true editable=true /]
            </div>
            <div class="col-md-3 col-md-offset-2">
              [@customForm.input name="user.firstName" i18nkey="First name" value="" className="userFirstName" type="text"  required=true readOnly=true editable=true /]
            </div>
            <div class="col-md-3 form-group col-md-offset-1">
              [@customForm.input name="user.lastName" i18nkey="Last name" value="" className="userLastName" type="text"  required=true readOnly=true editable=true /]
            </div>
            <div class="col-md-3 ">
              [@customForm.input name="user.email" i18nkey="Email" value="" className="userEmail" type="text"  required=true readOnly=true editable=true /]
            </div>
            <div class="col-md-3 col-md-offset-1">
              [@customForm.input name="user.username" i18nkey="Username" value="" className="userUsername" type="text"  required=true readOnly=true editable=true /]
            </div>
            <div class="col-md-3 form-group col-md-offset-1">
              [@customForm.input name="user.password" i18nkey="Password" value="" className="userPassword" type="text"  required=true readOnly=true editable=true /]
            </div>
          </div>
            <hr />
          <h4 style="text-align:center;">Configuration</h4>
            
            <div class="row form-group">
            <div class="col-md-2">
              [@customForm.select header=false name="user.cgiarUser" label=""  i18nkey="CGIAR user" listName="" keyFieldName="value"  displayFieldName="name"  multiple=false required=true disabled=true  className="cgiarUser" editable=true/]
            </div>
            <div class="col-md-2 col-md-offset-2">
              [@customForm.select header=false name="user.active" label=""  i18nkey="Is active" listName="" keyFieldName="value"  displayFieldName="name"  multiple=false required=true  className="isActive" editable=true/]
            </div>
            <div class="col-md-2 form-group col-md-offset-2">
              [@customForm.select header=false name="user.autoSave" label=""  i18nkey="Autosave" listName="" keyFieldName="value"  displayFieldName="name"  multiple=false required=true  className="autosave" editable=true/]
            </div>
          </div>
          
            <hr />
          <h4 style="text-align:center;">CRPs</h4>
          <div class="row">
            <div class="col-md-12 crpList">
            </div>
            <div class="col-md-12 form-group">
              [@customForm.select name="user.crps" label=""  i18nkey="Select to add a crp" listName="crps" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className="crpSelect" editable=true disabled=true/]
            </div>
          </div>
        </div>
        
        [#-- Add Outcome Button --]
        
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


[#-- Key output Template --]
[@crpItem element={} index=-1 name="user.crpUser"  isTemplate=true /]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro crpItem element index name  isTemplate=false]
  [#local customName = "${name}[${index}]" /]
  <div id="crp-${isTemplate?string('template',(element.id)!)}" class="crpItem expandableBlock borderBox"  style="display:${isTemplate?string('none','block')}">
    [#if editable] [#--&& (isTemplate) --]
     
      <div class="removeLink">
        <div id="removeCrp" class="removeCrp removeElement removeLink" title="[@s.text name='marloUsers.removeCrp' /]"></div>
      </div>
     
    [/#if]
    <input type="hidden" class="crpUserId" name="${customName}.id" value=""/>
    <input type="hidden" class="crpUserCrpId" name="${customName}.crp.id" value="" />
    [#-- crp Title --]
    <div class="blockTitle closed">
       <span title="" class="crpTitle col-md-9">[#if element.crp?has_content][@utils.wordCutter string=(element.crp) maxPos=70 substr=" "/][#else]CCAFS[/#if]</span>
    <div class="clearfix"></div>
    </div>
    
    <div class="blockContent" style="display:none">
      <hr />
      [#-- ROLES --]
      <div id="roles" class="roles simpleBox"  >
        <h5 class="sectionSubTitle">Roles</h5>
        <div class="rolesList">
        </div>
      
        </div>
    </div>
  
  </div>

[/#macro]
