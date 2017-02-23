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
        
        [#-- System Level Outcomes --]
        <h4 class="sectionTitle">[@s.text name="Users" /]</h4>
        <div class="row">
          <div class="col-md-7 form-group">
            [@customForm.input name="" i18nkey="Search by user email" value="" className="checkEmail" type="text"  required=true editable=true /]
          </div>
          <div class="col-md-5">
          <br />
            <span class="infoService"></span>
          </div>
        </div>
        
        <div class="borderBox form-group">
            <h4 style="text-align:center;">User Data</h4>
            <hr />
          <div class="row">
            <div class="col-md-2">
              [@customForm.input name="" i18nkey="Id" value="" className="userId" type="text"  required=true disabled=true editable=true /]
            </div>
            <div class="col-md-3 col-md-offset-2">
              [@customForm.input name="" i18nkey="First name" value="" className="userFirstName" type="text"  required=true disabled=true editable=true /]
            </div>
            <div class="col-md-3 form-group col-md-offset-1">
              [@customForm.input name="" i18nkey="Last name" value="" className="userLastName" type="text"  required=true disabled=true editable=true /]
            </div>
            <div class="col-md-3 ">
              [@customForm.input name="" i18nkey="Email" value="" className="userEmail" type="text"  required=true disabled=true editable=true /]
            </div>
            <div class="col-md-3 col-md-offset-1">
              [@customForm.input name="" i18nkey="Username" value="" className="userUsername" type="text"  required=true disabled=true editable=true /]
            </div>
            <div class="col-md-3 form-group col-md-offset-1">
              [@customForm.input name="" i18nkey="Password" value="" className="userPassword" type="text"  required=true disabled=true editable=true /]
            </div>
          </div>
          <h4 style="text-align:center;">Configuration</h4>
            <hr />
            
            <div class="row form-group">
            <div class="col-md-2">
              [@customForm.select header=false name="" label=""  i18nkey="CGIAR use" listName="" keyFieldName="value"  displayFieldName="name"  multiple=false required=true disabled=true  className="cgiarUser" editable=true/]
            </div>
            <div class="col-md-2 col-md-offset-2">
              [@customForm.select header=false name="" label=""  i18nkey="Is active" listName="" keyFieldName="value"  displayFieldName="name"  multiple=false required=true  className="isActive" editable=true/]
            </div>
            <div class="col-md-2 form-group col-md-offset-2">
              [@customForm.select header=false name="" label=""  i18nkey="Autosave" listName="" keyFieldName="value"  displayFieldName="name"  multiple=false required=true  className="autosave" editable=true/]
            </div>
          </div>
          
          <h4 style="text-align:center;">CRPs</h4>
            <hr />
            
          <div class="row">
            <div class="col-md-12 crpList">

            </div>
            <div id="addPartnerBlock" class="addPerson text-right">
              <div class="button-blue  addPartner"><span class="glyphicon glyphicon-plus-sign"></span> [@s.text name="Add CRP" /]</div>
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
[@crpItem element={} index=0 name="test"  isTemplate=true /]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro crpItem element index name  isTemplate=false]
  [#local customName = "${name}[${index}]" /]
  <div id="crp-${isTemplate?string('template',(element.id)!)}" class="crpItem expandableBlock borderBox"  style="display:${isTemplate?string('none','block')}">
    [#if editable] [#--&& (isTemplate) --]
     
      <div class="removeLink">
        <div id="removeCrp" class="removeCrp removeElement removeLink" title="[@s.text name='marloUsers.removeCrp' /]"></div>
      </div>
     
    [/#if]
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
