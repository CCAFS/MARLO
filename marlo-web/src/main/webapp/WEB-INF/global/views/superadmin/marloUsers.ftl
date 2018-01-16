[#ftl]
[#assign title = "MARLO Users" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "datatables.net", "datatables.net-bs"] /]
[#assign customJS = [ "${baseUrl}/global/js/superadmin/marloUsers.js" ] /]
[#assign customCSS = [ "${baseUrl}/global/css/superadmin/superadmin.css" ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "users" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"marloUsers", "nameSpace":"", "action":""}
]/]

 
[#include "/WEB-INF/crp/pages/header.ftl" /]
<hr />

<div class="container">
  [#include "/WEB-INF/global/pages/breadcrumb.ftl" /]
</div>
[#include "/WEB-INF/global/pages/generalMessages.ftl" /]

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/global/views/superadmin/menu-superadmin.ftl" /]
      </div>
      <div class="col-md-9">
        
        [#-- Add New User Button --]
        <br /><a href="#" id="addNewUser" class="btn btn-danger pull-right"> <span class="glyphicon glyphicon-plus"></span>  Add a new user</a>
        
        [#--Users tables --]
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
        
        <!-- Modal -->
        <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
          <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
              [@s.form action=actionName enctype="multipart/form-data" ]
              <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel">Edit Users</h4>
              </div>
              <div class="modal-body">
        
                <input type="hidden" class="isNewUser" name="isNewUser" value=""/>
                
                [#--User Form --]
                <div class="row hide">
                  <div class="col-md-7 form-group">
                    [@customForm.input name="" i18nkey="Search by user email"  className="checkEmail" type="text"  required=true editable=true /]
                  </div>
                  <div class="col-md-5">
                  <br />
                    <span class="infoService"></span>
                  </div>
                </div>
                
                <div class="simpleBox form-group">
                  <h4>User Information</h4>
                  <div class="row">
                    
                    <input type="hidden" class="userId" name="user.id" />
                   
                    <div class="col-md-4 ">
                      [@customForm.input name="user.email" i18nkey="Email" value="" className="userEmail" type="text"  required=true readOnly=true editable=true /]
                    </div>
                    <div class="col-md-4 ">
                      [@customForm.input name="user.username" i18nkey="Username" value="" className="userUsername" type="text"   editable=true /]
                    </div>
                    <div class="col-md-4">
                      [@customForm.input name="user.password" i18nkey="Password" value="" className="userPassword" placeholder="Change password" type="password"  required=true  editable=true /]
                    </div>
                    <div class="col-md-6 ">
                      [@customForm.input name="user.firstName" i18nkey="First name" value="" className="userFirstName" type="text"  required=true  editable=true /]
                    </div>
                    <div class="col-md-6">
                      [@customForm.input name="user.lastName" i18nkey="Last name" value="" className="userLastName" type="text"  required=true  editable=true /]
                    </div>
                  </div>
                  <hr />
                  
                  <h4>User Settings</h4>
                  <div class="row form-group">
                    <div class="col-md-4">
                      [@customForm.select header=false name="user.cgiarUser" label=""  i18nkey="CGIAR user" listName="" keyFieldName="value"  displayFieldName="name"  multiple=false required=true disabled=true  className="cgiarUser" editable=true/]
                    </div>
                    <div class="col-md-4 ">
                      [@customForm.select header=false name="user.active" label=""  i18nkey="Is active" listName="" keyFieldName="value"  displayFieldName="name"  multiple=false required=true  className="isActive" editable=true/]
                    </div>
                    <div class="col-md-4 ">
                      [@customForm.select header=false name="user.autoSave" label=""  i18nkey="Autosave" listName="" keyFieldName="value"  displayFieldName="name"  multiple=false required=true  className="autosave" editable=true/]
                    </div>
                  </div>
                  
                  <hr />
                  <h4>CRPs</h4>
                  <div class="row">
                    <div class="col-md-12 crpList">
                    </div>
                    <div class="col-md-12 form-group">
                      [@customForm.select name="user.crps" label=""  i18nkey="Select to add a crp" listName="crps" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className="crpSelect" editable=true disabled=true/]
                    </div>
                  </div>
                </div>

              </div>
              <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                [@s.submit type="button" name="save" cssClass="btn btn-primary"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /][/@s.submit]
              </div>
              [/@s.form]
            </div>
          </div>
        </div>
        
        
        
      </div>
    </div>
  </div>
</section>


[#-- Key output Template --]
[@crpItem element={} index=-1 name="user.crpUser"  isTemplate=true /]

[#include "/WEB-INF/crp/pages/footer.ftl" /]

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
