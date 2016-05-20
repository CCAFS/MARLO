[#ftl] 
[#macro searchUsers isActive=false ]
  <div id="dialog-searchUsers" title="Manage Users" style="display:none"> 
    <div class="dialog-content"> 
      <form class="pure-form">
        [#-- Search Users Form --]
        <div id="search-users" class="accordion">
          <span class="ui-icon ui-icon-triangle-1-s"></span><h6>[@s.text name="users.searchUsers" /]</h6>
        </div>
        <div class="accordion-block">
          <div class="search-content clearfix">
            <div class="search-input">
              [@customForm.input name="" showTitle=false type="text" i18nkey="form.buttons.searchUser"/]
              <div class="search-loader" style="display:none"><img src="${baseUrl}/images/global/loading_2.gif"></div>
            </div>  
            <div class="search-button">[@s.text name="form.buttons.search" /]</div>
          </div> 
          <div class="usersList panel secondary">
            <div class="panel-head"> [@s.text name="users.usersList" /]</div>
            <div class="panel-body"> 
              <p class="userMessage">
                [@s.text name="users.notUsersFound"]
                  [@s.param name="0"]<span class="link">[@s.text name="form.buttons.clickingHere" /]</span>[/@s.param]
                [/@s.text] 
              </p>
              <ul></ul>
            </div>
          </div> 
        </div>
        
        [#-- Create User Form --]    
        <div id="create-user" class="accordion active">
          <span class="ui-icon ui-icon-triangle-1-e"></span><h6>[@s.text name="users.createUser" /]</h6>
        </div>
        <div class="accordion-block create-user clearfix" style="display:none">
          <div class="loading" style="display:none"></div>
          <p class="warning-info" style="display:none"></p> 
          <div id="" class="tickBox-wrapper fullBlock">
            [@customForm.checkbox name="isCCAFS" value="" i18nkey="users.isCCAFS" /]
            <div class="tickBox-toggle">
              <br />
              <div class="halfPartBlock">[@customForm.input name="firstName" type="text" i18nkey="users.firstName"/] </div>
              <div class="halfPartBlock">[@customForm.input name="lastName" type="text" i18nkey="users.lastName"/] </div>
            </div>  
          </div> 
          <div class="fullPartBlock">[@customForm.input name="email" type="text" i18nkey="users.email"/] </div> 
          <input id="isActive" value="${isActive?string('1','0')}" type="hidden"/>
          <div class="button create-button">[@s.text name="users.createUser" /]</div> 
        </div> 
        
        <!-- Allow form submission with keyboard without duplicating the dialog button -->
        <input type="submit" tabindex="-1" style="position:absolute; top:-1000px">  
      </form>
      
      
      [#-- User Template --]
      <ul style="display:none"> 
        <li id="userTemplate">
          <span class="contact name">{composedName}</span>  
          <span class="listButton select">[@s.text name="form.buttons.select" /]</span>
          <span class="contactId" style="display:none">{userId}</span>
        </li> 
      </ul>  
      
      [#-- Messages for javascript --]
      <input type="hidden" id="created-message" value="[@s.text name="users.createUser.message" /]" />
    </div>  
  </div>
  
[/#macro]