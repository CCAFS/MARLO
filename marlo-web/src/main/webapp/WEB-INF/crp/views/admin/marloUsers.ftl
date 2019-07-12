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
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="crpGuestUsers.help" /] </p>
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
