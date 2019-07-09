[#ftl]
[#assign title = "MARLO Users" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "datatables.net", "datatables.net-bs"] /]
[#assign customJS = [ "${baseUrl}/global/js/superadmin/marloUsers.js", 
  "${baseUrl}/global/js/fieldsValidation.js" ] /]
[#assign customCSS = [ "${baseUrl}/global/css/superadmin/superadmin.css" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "guestUsers" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"marloBoard"},
  {"label":"guestUsers", "nameSpace":"", "action":""}
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
        [#include "/WEB-INF/crp/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
     
        [#-- Create User Guest --]
        <h4 class="sectionTitle">Create Guest User</h4>
        <div class="borderBox">        
          [@s.form action=actionName enctype="multipart/form-data" ]
          
          <input type="hidden" class="userId" name="user.id" />
          <div class="form-group">
            <div class="form-group row">
              <div class="col-md-8">
                [@customForm.input name="user.email" i18nkey="guestUsers.email" value="${(user.email)!}" className="userEmail" type="text"  required=true editable=true /]
              </div>
              <div class="col-md-4">
                 [@customForm.select name="selectedGlobalUnitID" className="" i18nkey="guestUsers.globalUnit" listName="crps" keyFieldName="id"  displayFieldName="acronym" required=true editable=true/]
              </div>
            </div>
            <div class="form-group row">
              <div class="col-md-6 ">
                [@customForm.input name="user.firstName" help="Not required for CGIAR emails"  i18nkey="guestUsers.firstName" value="${(user.firstName)!}" className="userFirstName" type="text"  required=(isCgiarUser)!true editable=true /]
              </div>
              <div class="col-md-6">
                [@customForm.input name="user.lastName" help="Not required for CGIAR emails" i18nkey="guestUsers.lastName" value="${(user.lastName)!}" className="userLastName" type="text"  required=(isCGIARUser)!true  editable=true /]
              </div>
            </div>
            </br>
            [#-- SEND EMAIL --]         
            <div class="form-group col-md-12">
              [@customForm.checkmark  id="sendEmail"  i18nkey="guestUsers.sendEmail" name="isEmailSend" value="true" checked=true editable=true /]
            </div>
            <br />          
          </div>
          <br />
          
          <div class="buttons">
            <div class="buttons-content">
              [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add User [/@s.submit]
            </div>
          </div>
          [/@s.form]
        </div>
        
        [#--Users table --]
        <h4 class="sectionTitle">MARLO Users</h4>
        <div class="borderBox">
          <table id="marloUsersTable" class="display table table-striped table-hover" width="100%">
            <thead>
              <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Username</th>
                  <th>Email</th>
                  <th>Active</th>
                  <th>AutoSave</th>
                  <th>Last Login</th>
              </tr>
            </thead> 
          </table>
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
      <div id="roles" class="roles">
        <h5 class="sectionSubTitle">Roles</h5>
        <div class="rolesList">
        </div>
      
        </div>
    </div>
  
  </div>

[/#macro]
