[#ftl] 
[#assign isActive=false /]

<!-- Search Users Dialog -->
<div id="dialog-searchProjects" title="Create milestone" > 
  <div class="dialog-content"> 
    <form id="fundingSourceForm" enctype="multipart/form-data" class="pure-form">
      [#--  Search a Funding Source  --]
      <div id="search-users">
        <span class="glyphicon glyphicon-remove-circle close-dialog"></span>
        <h4 class="text-center"> Create a milestone
        <br />
        </h4>
        <hr />
      </div>
      <div class="create-user clearfix">
        <div class="create-user-block">
          [#-- Loading --]
          <div class="loading" style="display:none"></div>
          [#-- Milestone Statement --]
          <div class="form-group">
            [@customForm.textArea name="statementPopup" i18nkey="outcome.milestone.index.statement" required=true className="statementPopup limitWords-50" editable=editable /]
          </div>
          <br />
          <div class="row form-group target-block">      
            [#-- Target Unit --]
            <div class="col-md-4">
              [@customForm.select name="tagetUnitPopup"  i18nkey="outcome.milestone.index.selectTargetUnit" placeholder="outcome.selectTargetUnit.placeholder" className="tagetUnitPopup" listName="targetUnitList" editable=editable  /]
              [#--  --if editable]<div class="addOtherTargetUnit text-center"><a href="#">([@s.text name = "outcomes.addNewTargetUnit" /])</a></div>[/#if--]
            </div>
            [#-- Target Value --]
            <div class="col-md-4 targetValue-block" style="display:none;">
              [@customForm.input name="targetValuePopup" type="text"  i18nkey="outcome.milestone.index.inputTargetValue" placeholder="outcome.milestone.index..placeholder" className="targetValuePopup" required=true editable=editable /]
            </div>
            [#-- Target Year --]
            <div class="col-md-4" >
              [#if editable]
                [@customForm.select name="targetYearPopup"  i18nkey="outcome.milestone.index.inputTargetYear" listName="allYears"  required=true  className=" targetYearPopup milestoneYear" header=true placeholder="Select a Year..." disabled=!editable/]
              [/#if]
            </div>
          </div>
          [#-- Save button --]
          <br />
          <div class="text-right">
            <div class="button create-button"> Create Milestone </div>
          </div>
        </div>
      </div> 
      
      <!-- Allow form submission with keyboard without duplicating the dialog button -->
      <input type="submit" tabindex="-1" style="position:absolute; top:-1000px">  
    </form>
    
    [#-- Messages for javascript --]
    <input type="hidden" id="created-message" value="[@s.text name="users.createUser.message" /]" />
    <input type="hidden" id="actionName" value="${(actionName)!}" />
  </div>  
</div>
[#--  Funding Source Popup JS --]
[#assign customJS =  [ "${baseUrlMedia}/js/global/milestonePopup.js" ]  + customJS/]
  
