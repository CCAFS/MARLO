[#ftl] 
[#assign isActive=false /]
<!-- Search Users Dialog -->
<div id="dialog-searchProjects" title="Search Bilateral Co-Funded Project" style="display:none"> 
  <div class="dialog-content"> 
    <form class="pure-form">
      [#--  Search a Bilateral Co-funded Project  --]
      <div id="search-users">
        <span class="glyphicon glyphicon-remove-circle close-dialog"></span>
        <h4 class="text-center"> Search a Bilateral Co-funded Project
        <br />
        <small class="cgiarCenter"> {CGIAR Center}  </small>
        </h4>
        <hr />
      </div>
      [#-- Search Bilateral Projects --]
      <div class="accordion-block">
        <div class="search-content clearfix">
          <div class="search-input">
            [@customForm.input name="" showTitle=false type="text" i18nkey="form.buttons.searchUser" placeholder="Search by project name or project ID"/]
            <div class="search-loader" style="display:none"><img src="${baseUrl}/images/global/loading_2.gif"></div>
          </div>  
          <div class="search-button">[@s.text name="form.buttons.search" /]</div>
        </div> 
        <div class="usersList panel secondary">
          <div class="panel-head"> Projects list </div>
          <div class="panel-body"> 
            <p class="userMessage">
              If you do not find the bilateral co-funded project, please add it by <span class="link">[@s.text name="form.buttons.clickingHere" /]</span>.
            </p>
            <ul></ul>
          </div>
        </div> 
      </div>
      
      [#-- Create Bilateral Projects Form --]
      <div id="create-user" class="accordion  text-center">
        <span class="glyphicon glyphicon-plus"></span> <span class="title"> Search bilateral project </span> 
      </div>
      <div class="accordion-block create-user clearfix" style="display:none">
        <div class="create-user-block">
          <div class="loading" style="display:none"></div>
          [#-- Warning Info --]
          <p class="warning-info" style="display:none"></p> 
          
          [#-- Participating Center, CRP Lead Center --]
          <div class="pull-right">
            <label for="cofundedMode-1"><input type="radio" name="cofundedMode" id="cofundedMode-1" value="1" /> [@s.text name="projectCofunded.participatingCenter" /] </label><br />
            <label for="cofundedMode-2"><input type="radio" name="cofundedMode" id="cofundedMode-2" value="2" /> [@s.text name="projectCofunded.crpLeadCenter" /] </label>
          </div>
          
          [#-- Project title --]
          <div class="form-group">
            <div class="row">
              <div class="col-md-12">[@customForm.textArea name="title" i18nkey="projectCofunded.title"/] </div>
            </div>
          </div>
          <div class="form-group">
            <div class="row">
              <div class="col-md-4">[@customForm.input name="startDate" i18nkey="projectCofunded.startDate"/] </div>
              <div class="col-md-4">[@customForm.input name="endDate" i18nkey="projectCofunded.endDate"/] </div>
              <div class="col-md-4">[@customForm.input name="financeCode" i18nkey="projectCofunded.financeCode"/] </div>
            </div>
          </div>
          <div class="form-group">
            <div class="row">
              <div class="col-md-6">[@customForm.select name="status" i18nkey="projectCofunded.agreementStatus"  listName="status" /] </div>
              <div class="col-md-6">[@customForm.input name="budget" i18nkey="projectCofunded.budgetAgreementPeriod" className=""/]</div>
            </div>
          </div>
          <div class="form-group">
            <div class="row">
              <div class="col-md-6">[@customForm.input name="contactName" i18nkey="projectCofunded.contactName"/]</div>
              <div class="col-md-6">[@customForm.input name="contactEmail" i18nkey="projectCofunded.contactEmail"/]</div>
            </div>
          </div>
          <div class="form-group">
            <div class="row">
              <div class="col-md-12">
                [@customForm.select name="institution" i18nkey="projectCofunded.donor"  listName="institutions " keyFieldName="id"  displayFieldName="composedName" required=true /]
              </div>
            </div>
          </div>
          <div class="note">
            [@s.text name="projectCofunded.donor.disclaimer" /]
          </div>
          
          [#-- Save button --]
          <br />
          <div class="text-right">
            <div class="button create-button"> Create Project </div>
          </div>
        </div>
      </div> 
      
      <!-- Allow form submission with keyboard without duplicating the dialog button -->
      <input type="submit" tabindex="-1" style="position:absolute; top:-1000px">  
    </form>
    
    
    [#-- User Template --]
    <ul style="display:none"> 
      <li id="userTemplate">
        <div class="row">
          <div class="col-md-1">P<span class="contactId">{userId}</span></div>
          <div class="col-md-9"><span class="contact name">{composedName}</span></div>
          <div class="col-md-2"><span class="listButton select">[@s.text name="form.buttons.select" /]</span></div>
        </div>
      </li> 
    </ul>  
    
    [#-- Messages for javascript --]
    <input type="hidden" id="created-message" value="[@s.text name="users.createUser.message" /]" />
    <input type="hidden" id="actionName" value="${(actionName)!}" />
  </div>  
</div>

[#-- Project Bilateral Co-Funded  Popup JS --]
[#assign customJS =  [ "${baseUrl}/js/global/bilateralCoFundedPopup.js" ]  + customJS/]
  
