[#ftl] 
[#assign isActive=false /]

<!-- Search Users Dialog -->
<div id="dialog-searchProjects" title="Search Funding Source" style="display:none"> 
  <div class="dialog-content"> 
    <form id="fundingSourceForm" enctype="multipart/form-data" class="pure-form">
      [#--  Search a Funding Source  --]
      <div id="search-users">
        <span class="glyphicon glyphicon-remove-circle close-dialog"></span>
        <h4 class="text-center"> Search a Funding Source
        <br />
        <small class="cgiarCenter"> {CGIAR Center}  </small>
        </h4>
        <hr />
      </div>
      [#-- Search Funding Source --]
      <div class="accordion-block">
        <div class="search-content clearfix">
          <div class="search-input">
            [@customForm.input name="" showTitle=false type="text" i18nkey="form.buttons.searchUser" placeholder="Search by funding source name or ID"/]
            <div class="search-loader" style="display:none"><img src="${baseUrl}/images/global/loading_2.gif"></div>
          </div>  
          <div class="search-button">[@s.text name="form.buttons.search" /]</div>
        </div> 
        <div class="usersList panel secondary">
          <div class="panel-head"> Funding sources list </div>
          <div class="panel-body"> 
            <p class="userMessage">
              If you do not find the Funding Source, please add it by <span class="link">[@s.text name="form.buttons.clickingHere" /]</span>.
            </p>
            <ul></ul>
          </div>
        </div> 
      </div>
      
      [#-- Create Funding Sources Form --]
      <div id="create-user" class="accordion  text-center">
        <span class="glyphicon glyphicon-plus"></span> <span class="title"> Create Funding Source </span> 
      </div>
      <div class="accordion-block create-user clearfix" style="display:none">
        <div class="create-user-block">
          [#-- Loading --]
          <div class="loading" style="display:none"></div>
          [#-- Warning Info --]
          <p class="warning-info" style="display:none"></p> 
          
          [#-- Title --]
          <div class="form-group">
            <div class="row">
              <div class="col-md-12">[@customForm.input name="title" i18nkey="projectCofunded.title" required=true/] </div>
            </div>
          </div>
          [#-- Description --]
          <div class="form-group">
            <div class="row">
              <div class="col-md-12">[@customForm.textArea name="description" i18nkey="projectCofunded.description" className="limitWords-150" required=false/] </div>
            </div>
          </div>
          
           
          [#-- Upload bilateral contract --]
          [#-- 
          <div class="form-group fileUploadContainer">
            <label>[@customForm.text name="fundingSource.uploadContract" readText=!editable /]:</label>
            [#-- Input File  
            <div class="fileUpload"> <input class="upload" type="file" name="file" data-url="${baseUrl}/uploadFundingSource.do"></div>
            [#-- Uploaded File  
            <p class="fileUploaded textMessage checked" style="display:none"><span class="contentResult">{{contentResult}}</span> <span class="removeIcon"> </span> </p>
          </div>
          --]
          
          <div class="form-group">
            <div class="row">
              <div class="col-md-4">[@customForm.input name="startDate" i18nkey="projectCofunded.startDate" required=true/] </div>
              <div class="col-md-4">[@customForm.input name="endDate" i18nkey="projectCofunded.endDate" required=true/] </div>
              <div class="col-md-4">[@customForm.input name="financeCode" i18nkey="projectCofunded.financeCode" placeholder="projectCofunded.financeCode.placeholder" /] </div>
            </div>
          </div>
          
          <div class="form-group">
            <div class="row">
              <div class="col-md-6">[@customForm.select name="status" i18nkey="projectCofunded.agreementStatus"  listName="status" header=false required=true /] </div>
              <div class="col-md-6">
                [@customForm.select name="budgetType"   i18nkey="projectCofunded.type" className="type" listName="budgetTypes" header=false required=true /]
              </div>
            </div>
            [#-- W1W2 Tag --]
            <div class="w1w2-tag">
              <div class="checkbox dottedBox">
                <label for="w1w2-tag-input"><input type="checkbox" name="w1w2" value="true" id="w1w2-tag-input"/> <small>[@customForm.text name="fundingSource.w1w2Tag" readText=!editable /]</small></label>
              </div>
            </div>
          </div>
          
          [#-- Budget Types Description --]
          <ul style="display:none">
            [#list budgetTypesList as budgetType]
              <li class="budgetTypeDescription-${budgetType.id}">${(budgetType.description)!}</li>
            [/#list]
          </ul>
          
          <div class="form-group">
            <div class="row">
              <div class="col-md-12">
                [@customForm.select name="institution" i18nkey="projectCofunded.donor" value="${action.getCGIARInsitution()}"  listName="institutions " keyFieldName="id"  displayFieldName="composedNameLoc" required=true /]
              </div>
            </div>
          </div>
          <div class="form-group"> 
            [#assign canSeePIEmail = action.hasSpecificities('crp_email_funding_source')]
            <div class="row">
              <div class="col-md-6">[@customForm.input name="contactName" i18nkey="projectCofunded.contactName" className="contactName" required=true/]</div>
              <div class="col-md-6" style="display:${canSeePIEmail?string('block','none')}">[@customForm.input name="contactEmail" i18nkey="projectCofunded.contactEmail" className="contactEmail validate-${canSeePIEmail?string}" required=true/]</div>
            </div>
          </div>
          [#--  
          <div class="note">
            [@s.text name="projectCofunded.donor.disclaimer" /]
          </div>
          --]
          [#-- Budget --]
          <div class="form-group" style="display:${(action.canEditFundingSourceBudget())?string('block','none')}">
            <div class="budgetByYears">
                <strong class="pull-right">Entire funding budget $US <span class="fundingTotalAmount">0.00</span></strong>
                <ul class="nav nav-tabs" role="tablist">
                </ul>
                <div class="tab-content">
                </div>
            </div>
          </div>
          
          
          [#-- Save button --]
          <br />
          <div class="text-right">
            <div class="button create-button"> Create Funding Source </div>
          </div>
        </div>
      </div> 
      
      <!-- Allow form submission with keyboard without duplicating the dialog button -->
      <input type="submit" tabindex="-1" style="position:absolute; top:-1000px">  
    </form>
    
    
    [#-- Funding Source item search Template --]
    <ul style="display:none"> 
      <li id="userTemplate">
        <div class="row">
          <div class="col-md-1">
            FS<span class="contactId">{userId}</span>
          </div>
          <div class="col-md-9">
            <small><span class="red noBudgetMessage pull-left glyphicon glyphicon-exclamation-sign" style="display:none" title="Insufficient funds for {year}"></span></small>
            <span class="contact name">{composedName}</span>
            <span class="currentBudget">{budget}</span>
          </div>
          <div class="col-md-2">
            <span class="listButton select">[@s.text name="form.buttons.select" /]</span>
            <span class="glyphicon glyphicon-new-window linkIcon"></span>
          </div>
          [#-- Hidden parameters --]
          <span style="display:none" class="budget">{budget}</span>
          <span style="display:none" class="budgetTypeName">{budget}</span>
          <span style="display:none" class="budgetTypeId">{budget}</span>
        </div>
      </li> 
    </ul>  
    
    [#-- Messages for javascript --]
    <input type="hidden" id="created-message" value="[@s.text name="users.createUser.message" /]" />
    <input type="hidden" id="actionName" value="${(actionName)!}" />
  </div>  
<span class="hidden cgiarConsortium">${action.getCGIARInsitution()}</span>
</div>
[#--  Funding Source Popup JS --]
[#assign customJS =  [ "${baseUrl}/js/global/fundingSourcesPopup.js" ]  + customJS/]
  
