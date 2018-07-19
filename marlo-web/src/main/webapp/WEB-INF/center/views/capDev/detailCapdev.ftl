[#ftl]
[#assign title = "Capacity Development" /]
[#assign pageLibs = ["select2","flat-flags", "pickadate"] /]
[#assign customCSS = [
  "${baseUrl}/global/css/customDataTable.css",
  "${baseUrlMedia}/css/capDev/capacityDevelopment.css"
  ] 
/]
[#assign customJS = [
  "${baseUrl}/global/js/usersManagement.js", 
  "${baseUrlMedia}/js/capDev/capacityDevelopment.js",
  "${baseUrlMedia}/js/capDev/syncParticipants.js",
  "${baseUrl}/global/js/fieldsValidation.js",
  "${baseUrl}/global/js/autoSave.js"
  ] 
/]

[#assign currentSection = "capdev" /]
[#assign currentStage = "capdevIntervention" /]

[#assign breadCrumb = [
  {"label":"capdevList", "nameSpace":"/capdev", "action":"${(centerSession)!}/capdev"},
  {"label":"capdevDetail", "nameSpace":"/capdev", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container"> 
	<div class="row">
		<div class="helpMessage infoText col-md-12 capdevinfo">
			<img class="col-md-2" src="${baseUrl}/global/images/icon-help.png" />
   			[@s.text name="capdev.help.detail"][/@s.text] 
		</div>
	</div> 

	<div class="col-md-3 capDevMenu">
		[#include "/WEB-INF/center/views/capDev/menu-capdev.ftl" /]
	</div>
	
	<div class="col-md-9 ">
    [#-- Section Messages --]
    [#include "/WEB-INF/center/views/capDev/messages-capdev.ftl" /]
    <br />

    <div class="pull-right">
      [#if projectID > 0]
      	<a class="" href="[@s.url namespace='/monitoring' action='${centerSession}/projectCapdev'] [@s.param name='projectID']${projectID?c}[/@s.param][@s.param name='edit' value="true" /] [/@s.url] "><span class="glyphicon glyphicon-circle-arrow-left"></span>[@s.text name="capdev.gotoBackProjects" /]</a>
      [#else]
      	<a class="" href="[@s.url action='${centerSession}/capdev' ][@s.param name='edit' value="true" /] [/@s.url] "><span class="glyphicon glyphicon-circle-arrow-left"></span>[@s.text name="capdev.gotoBack" /]</a>
      [/#if]
    </div>
    [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
      [#-- Intervencion Information --]
      <h4 class="form-group headTitle" >[@s.text name="capdev.interventionTitle" /]</h4>
      <div  class="fullForm borderBox" >
        [@interventionInfo /]
      </div>
      
      [#-- Participant(s) --]
      <h4 class="form-group headTitle newCapdevField grupsParticipantsForm" >Participant(s) Information</h4>
      <div  class="fullForm borderBox" >
        [#if ((capdev.category == 2)!false)]
          [#-- Groups --]
          [@groupsParticipants /]
        [#elseif ((capdev.category == 1)!false)]
          [#-- Induvidual--]
          [@induvidualParticipant /]
        [/#if]
      </div>
      
      [#-- Regions and countries lists  --]
      <h4 class="headTitle newCapdevField">Reach</h4>
      <div class="borderBox">
        [@reachLocation /]
      </div>
      
      [#-- Buttons --]
      [#include "/WEB-INF/center/views/capDev/capdev-buttons.ftl" /]
    [/@s.form]

  </div>
  
</div>

[#-- Search users Interface --]
[#import "/WEB-INF/global/macros/usersPopup.ftl" as usersForm/]
[@usersForm.searchUsers/]


<!-- country list template-->
<ul style="display:none">
  <li id="capdevCountryTemplate" class="capdevCountry clearfix col-md-4">
      <div class="removeCountry removeIcon" title="Remove country"></div>
      <input class="id" type="hidden" name="" value="" />
      <input class="cId" type="hidden" name="capdev.capDevCountries[-1].locElement.id" value="" />
      <span class="name"></span>
      <div class="clearfix"></div>
    </li>
</ul>

<!-- Region list template-->
<ul style="display:none">
  <li id="capdevRegionTemplate" class="capdevRegion clearfix col-md-4">
      <div class="removeRegion removeIcon" title="Remove region"></div>
      <input class="id" type="hidden" name="" value="" />
      <input class="rId" type="hidden" name="capdev.capDevRegions[-1].locElement.id" value="" />
      <span class="name"></span>
      <div class="clearfix"></div>
    </li>
</ul>


[#include "/WEB-INF/global/pages/footer.ftl"]

[#macro interventionInfo]
  [#-- Radio Buttons --]
  <div class="radio" style="display:none">
    <label><input  id="individual" type="radio" hidden="true" name="capdev.category" class="radioButton" value="${(capdev.category)!}"  /></label>
  </div>
  <div class="radio" style="display:none">
    <label><input id="gruops" type="radio" hidden="true" name="capdev.category" class="radioButton"  value="${(capdev.category)!}" /> </label>
  </div>
  [#-- Title--]
  <div class="form-group row " >
    <div class="col-md-12 "> 
      [@customForm.input name="capdev.title" type="text" help="capdev.help.title" i18nkey="capdev.form.title" helpIcon=false required=true disabled=!editable editable=editable /]
    </div>
  </div>
  [#-- type and contact Person --]
  <div class="form-group row">
      [#-- type--]
      <div class="col-md-6" > 
        [@customForm.select name="capdev.capdevType.id" listName="capdevTypes" keyFieldName="id" displayFieldName="name" help="capdev.help.type" i18nkey="capdev.form.type"  placeholder="capdev.select" required=true editable=editable disabled=!editable/]
      </div>
      [#-- Contact person --]
      <div class="col-md-6 contactField userField" style="display:${((capdev.category == 2)!false)?string('block','none')};">
        [@customForm.input name="contact" value="${(capdev.ctFirstName)!} ${(capdev.ctLastName)!}" className='userName' type="text" disabled=!canEdit i18nkey="capdev.form.contactPerson" required=true readOnly=true editable=editable /]
        <input class="userId"     type="hidden" name="capdev.user.id"     value="${(capdev.user.id)!}" />   
        <input class="ctFirsName" type="hidden" name="capdev.ctFirstName" value="${(capdev.ctFirstName)!}" /> 
        <input class="ctLastName" type="hidden" name="capdev.ctLastName"  value="${(capdev.ctLastName)!}" /> 
        <input class="ctEmail"    type="hidden" name="capdev.ctEmail"     value="${(capdev.ctEmail)!}" />
        [#if editable]<div class="searchUser button-blue button-float">[@s.text name="form.buttons.searchUser" /]</div>[/#if]
      </div>
  </div>
  [#-- Dates --]
  <div class="form-group row datePickersBlock">
    [#-- Start date--]
    <div class="col-md-6 ">
      [@customForm.input name="capdev.startDate" value="${(capdev.startDate?string.medium)!}" i18nkey="capdev.form.startDate" type="text"  help="capdev.help.startDate" editable=editable required=true className="startDate datePicker"/]
    </div>
    [#-- End date
    <div class="col-md-6 ">
      [@customForm.input name="capdev.endDate" value="${(capdev.endDate?string.medium)!}" i18nkey="capdev.form.endDate" type="text" help="capdev.help.endDate"  editable=editable className="endDate datePicker" /]
    </div>
    --]
  </div>
  
  [#-- Duration --]
  <div class="form-group row ">
    <i class="col-md-12"><small>if your training was not full time, what was the real duration?</small></i>
    <div class="col-md-3">
      [@customForm.input name="capdev.duration" i18nkey="capdev.form.duration" type="text"  help="capdev.help.duration" required=true  editable=editable className="capdevDuration"/] 
    </div>
    <div class="col-md-3 durationUnitSelect">
      <input type="hidden" name="" value="${(capdev.durationUnit)!}" class="durationUnitaInput"/>
      [@customForm.select name="capdev.durationUnit" listName="durationUnit" keyFieldName="value" value="'${(capdev.durationUnit)!}'" displayFieldName="displayName" help="capdev.help.durationUnit" i18nkey="capdev.form.durationUnit"  placeholder="capdev.select" required=true editable=editable className="dUnitSelect"/]
    </div>
  </div>
[/#macro]

[#macro groupsParticipants]
<div class="form-group row" >
  [#if capdev.capdevParticipant?has_content && capdev.capdevParticipant?size > 1] 
    <div class="capdevParticipantsTable">
      <div class="capdev-participantslist-title">List of participants</div>
      [@capdevList capdev.capdevParticipant /]
      <div class="col-md-12">
        <div class="pull-right">
          [#if editable]  
            <button type="button" class="" title="Delete list of participants">
              <a id="" class="removeCapdev" data-href=""  data-toggle="modal" data-target="#confirm-clear-participantList"> Clear</a>
            </button>
            <div class="modal fade" id="confirm-clear-participantList" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
              <div class="modal-dialog">
                <div class="modal-content">
                  <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="myModalLabel">Confirm Clear</h4>
                  </div>
                  <div class="modal-body">
                    <p>You are about to clear the list of participants, this procedure is irreversible.</p>
                    <p>Do you want to proceed?</p>
                    <p class="debug-url"></p>
                  </div>
                  <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    <a class="btn btn-danger btn-ok" href="[@s.url action='${centerSession}/delete_list_of_participants'][@s.param name='capdevID']${capdevID}[/@s.param][@s.param name='projectID']${projectID?c}[/@s.param][@s.param name='category']${capdev.category}[/@s.param] [@s.param name='edit' value='true' /][/@s.url]">Clear</a>
                  </div>
                </div>
              </div>
            </div>
          [/#if]
        </div>
      </div>
    </div>
  [#else]
    [#if editable]
      <div class="form-group col-md-12 newCapdevField participantsheader">
        <div class="pull-right">
          <button type="button" class="capdevButtons" aria-label="Left Align" title="Download template to upload the list of participants">
            <a class="downloadButton" href="[@s.url action='${centerSession}/downloadFile' /] ">[@s.text name="capdev.downloadTemplate" /]</a>
          </button>
        </div>
      </div>
      <div class="form-group row ">
        <div class="col-md-12  participantsBox " listname="capdev.uploadFile">
          <!-- [@s.fielderror fieldName="upload_File" class="fileError" /] -->
          <div class="col-md-12">
            [@s.file id="uploadFile" name="uploadFile" label="Select a File to upload" size="40" class="uploadParticipants" editable=editable/]
          </div>
          <div class="col-md-12" style="margin-top: 10px;">
          <div class="btnPreview">
            <button type="button"  id="btnDisplay" class="capdevButtons" aria-label="Left Align" data-toggle="modal" data-target="#myModa"       title="Take a look to list of participants uploaded" >
              preview 
            </button>
          </div>
          <div id="filewarning" class="warning" style="display: none; margin-top: 10px;">
          </div>
          <div class="loader" style="display:none;"><img src="${baseUrl}/global/images/loading_2.gif" width="80" height="30"></div>

          <div class="modal fade bd-example-modal-lg" id="myModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg" role="document">
              <div class="modal-content highlight">
                <div class="modal-header">
                  <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                  </button>
                  <h5 class="modal-title" id="exampleModalLabel">Preview participants file</h5>
                  
                </div>
                <div id="participantsTable"  class="modal-body">
                  
                </div>
              </div>
            </div>
          </div>
        </div>
        </div>
      </div>
    [/#if]
  [/#if]

  [#-- num participants, num men and num women --]
  <div class="form-group row">
    <div class="col-md-12 ">
      <div class="col-md-4 group individual">
        [@customForm.input name="capdev.numParticipants" i18nkey="capdev.form.numParticipants" type="text" help="capdev.help.numParticipants"  editable=editable className="numParticipants" /]
      </div>
      <div class="col-md-3 group individual">
        [@customForm.input name="capdev.numMen" i18nkey="capdev.form.numMen" help="capdev.help.numMen" type="text" className="numMen" editable=editable/]
      </div>
      <div class="col-md-3 group individual">
        [@customForm.input name="capdev.numWomen" i18nkey="capdev.form.numWomen" help="capdev.help.numWomen" type="text" className="numWomen" editable=editable/]
      </div>
      <div class="col-md-2 group individual">
        [@customForm.input name="capdev.numOther" i18nkey="capdev.form.numOther" help="capdev.help.numOther" type="text" className="numOhter" editable=editable/]
      </div>
    </div>
  </div>
  <div class="row grupsParticipantsForm">
    <div class="note participantMessage">
      <p>If you don’t have a list of participants, please enter the number of participants, specifying number of men,  number of women, or other</p>
    </div>
  </div>
</div>
[/#macro]

[#macro induvidualParticipant]
  <div class="loading syncBlock" style="display:none"></div>
  <div class="note  individualparticipantForm">
    <p><small>[@s.text name="capdev.help.participant.helpText" /]</small></p>
  </div>
  <div class="individualparticipantForm" style="display:none;">
    <input type="hidden" name="capdev.participant.id" value="${(capdev.participant.id)!}" class="genderInput"/>
    
    [#-- Finance code module --]
    [#if capdev.participant?has_content]
      [#assign isSynced = (capdev.participant.sync)!false ]
    [#else]
      [#assign isSynced =false ]
    [/#if]
   
    <label for="" class="editable">[@s.text name="capdev.participant"/]: <span class="red requiredTag" style="display:none;">*</span></label>
    [@customForm.helpLabel name="capdev.help.participant.code" paramText="" showIcon=false editable=editable/]
    <div class="form-group row">
      <div class="col-md-6">
        <div class="url-field">
          <div class="">
            [#if editable]
              [#-- Finance Input --]
              <input type="text" name="capdev.participant.code" value="${(capdev.participant.code)!}" class="form-control input-sm financeCode participant-code optional" [#if isSynced]readonly="readonly"[/#if] placeholder="">
            [#else]
              <input type="hidden" class="financeCode" name="capdev.participant.code" value="${(capdev.participant.code)!}"/>
              <small >${(capdev.participant.code)!} </small>
            [/#if]
          </div>
          <span class="financeCode-message"></span>
        </div>
        <div class="buttons-field">
          <input type="hidden" id="isSynced" name="capdev.participant.sync" value="${isSynced?string}" />
          [#if editable]
            <div id="fillMetadata">
              [#-- Sync Button --]
              <div class="checkButton" style="display:${isSynced?string('none','block')};">[@s.text name="project.deliverable.dissemination.sync" /]</div>
              <div class="unSyncBlock" style="display:${isSynced?string('block','none')};">
                [#-- Update Button --]
                <div class="updateButton">[@s.text name="project.deliverable.dissemination.update" /]</div>
                [#-- Unsync Button --]
                <div class="uncheckButton">[@s.text name="project.deliverable.dissemination.unsync" /]</div>
              </div>
            </div>
          [/#if]
        </div>
        <div id="metadata-output row">
          <p class="lastDaySync" style="display:${(!isSynced)?string('none', 'block')}">Last sync was made on <span>${(capdev.participant.syncedDate?date)!}</span></p>
        </div>
        <input type="hidden" class="fundingSourceSyncedDate" name="capdev.participant.syncedDate" value="${(capdev.participant.syncedDate?string["yyyy-MM-dd"])!'2017-06-30'}" />
      </div>
    </div>
    
    
    [#-- Participant name and middle name --]
    <div class="form-group row">
      <div class="col-md-6 metadataElement-firstName">
        [@customForm.input name="capdev.participant.name" i18nkey="capdev.participant.firstName" type="text" className="participant-name metadataValue" required=true readOnly=isSynced editable=editable/]
      </div>
      <div class=" col-md-6 ">
        [@customForm.input name="capdev.participant.middleName" i18nkey="capdev.participant.middleName" type="text"  editable=editable/]
      </div>
    </div>
    [#-- participant last name and gender --]
    <div class="form-group row ">
      <div class="form-group col-md-6 metadataElement-lastName">
        [@customForm.input name="capdev.participant.lastName" i18nkey="capdev.participant.lastName" type="text" className="participant-lastname metadataValue" required=true readOnly=isSynced editable=editable/]
      </div>
      <div class="form-group col-md-6 genderSelect metadataElement-gender">
        <input type="hidden" name="" value="${(participant.gender)!}" class="genderInput"/>
        [@customForm.select name="capdev.participant.gender" value="'${(capdev.participant.gender)!}'" listName="genders" keyFieldName="value" displayFieldName="displayName" help="" i18nkey="capdev.participant.gender"  placeholder="capdev.select" disabled=isSynced required=true editable=editable className="metadataValue"/]
        [#if isSynced && editable]<input type="hidden" class="selectHiddenInput" name="capdev.participant.gender" value="${(capdev.participant.gender)!}" />[/#if]
      </div>
    </div>

    [#-- Age --]
    <div class="form-group row">
      <div class="col-md-6">
        [@customForm.select name="capdev.participant.age.id"  listName="rangeAgeList" keyFieldName="id" displayFieldName="range" help="" i18nkey="Range age"  placeholder="capdev.select" required=true editable=editable className=""/]
      </div>
    </div>
    
    [#-- Participant citizenship    --]
    <div class="form-group row">
      <div class="col-md-6 pCitizenshipcountriesList metadataElement-countryID">
        [@customForm.select name="capdev.participant.locElementsByCitizenship.id" listName="countryList" keyFieldName="id" displayFieldName="name" help="" i18nkey="capdev.participant.citizenship" className="countrieSelect metadataValue" disabled=isSynced  placeholder="capdev.select" required=true editable=editable/]
        [#if isSynced && editable]<input type="hidden" class="selectHiddenInput" name="capdev.participant.locElementsByCitizenship.id" value="${(capdev.participant.locElementsByCitizenship.id)!}" />[/#if]
      </div> 
    </div>
    [#-- Participant personal email and job email --]
    <div class="form-group row  ">
      <div class="col-md-6 metadataElement-email">
        [@customForm.input name="capdev.participant.personalEmail" i18nkey="capdev.participant.personalEmail" type="text" className="participant-pEmail metadataValue"  required=true readOnly=isSynced editable=editable/]
      </div>
      <div class="col-md-6">
        [@customForm.input name="capdev.participant.email" i18nkey="capdev.participant.Email" type="text" editable=editable /]
      </div>
    </div>
    [#-- intitucion and country of institution  --]
    <div class="form-group row">
      <div class="col-md-6 metadataElement-institucion">
        [@customForm.select name="capdev.participant.institutions.id" listName="institutions" keyFieldName="id" displayFieldName="composedName" help="" i18nkey="capdev.participant.Institution" className="" multiple=false placeholder="capdev.select" editable=editable /]
        <span class="text-warning metadataSuggested"></span> 
           
        [#-- Request partner adition --]
        [#if editable]
        <p id="addPartnerText" class="helpMessage">
          <small>If you cannot find the institution you are looking for, please 
          <a class="popup" href="[@s.url action='${crpSession}/partnerSave' namespace="/projects"][@s.param name='capdevID']${(capdevID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
            click here to [@s.text name="projectPartners.addPartnerMessage.second" /]
          </a>
          </small>
        </p> 
        [/#if]
        [#-- 
        <div>
          <label>Other <input type="checkbox" name="capdev.participant.otherInstitution" class="otherInstcheck"   [#if (capdev.participant.otherInstitution)??]
          [#if (capdev.participant.otherInstitution) == "1"] checked="checked" [/#if] value="${(capdev.participant.otherInstitution)!}"[/#if]  [#if !editable] disabled="true"[/#if] > </label>
          <div class="suggestInstitution" style="display: none;">[@customForm.textArea name="capdev.participant.institutionsSuggested" i18nkey="Suggest institution"  className="textarea" editable=editable /]</div>
        </div>
         --]
      </div>
      <div class="col-md-6 pcountryOfInstitucionList ">
        [@customForm.select name="capdev.participant.locElementsByCountryOfInstitucion.id" listName="countryList" keyFieldName="id" displayFieldName="name" help="" i18nkey="capdev.participant.country" className="" multiple=false placeholder="capdev.select" editable=editable /]
        
      </div>
    </div>
    [#-- Supervisor and funding type --]
    <div class="form-group row">
      <div class="col-md-6 metadataElement-supervisor1">
        [@customForm.input name="capdev.participant.supervisor" i18nkey="capdev.participant.Supervisor" type="text" className="participant-supervisor metadataValue"  required=true readOnly=isSynced editable=editable/]
      </div>
      <div class="col-md-6">
        [@customForm.select name="capdev.participant.fellowship.id" listName="foundingTypeList" keyFieldName="id" displayFieldName="name"  i18nkey="capdev.participant.Fellowship"  multiple=false placeholder="capdev.select" editable=editable className="fundingType" /]
      </div>
    </div>
    <div class="form-group fundingProvidedBy" style="display:${((capdev.participant.fellowship.id == 4)!false)?string('block','none')}">
      [@customForm.input name="capdev.participant.otherFunding" i18nkey="capdev.participant.otherFunding" type="text" /]
    </div>
  </div>
[/#macro]

[#macro reachLocation]
<div class=" newCapdevField form-group">
  <div class="">
  
    <div class="form-roup capdevDimension" >
      <div listname="capdev.globalReach">
        [@customForm.yesNoInput  label="capdev.globalDimensionQuestion" name="capdev.sGlobal" value="${(capdev.sGlobal)!'-1'}" inverse=false  cssClass="global" editable=editable/]
      </div>
    </div>

    <div class="form-group capdevDimension" >
      <div listname="capdev.regionReach">
        [@customForm.yesNoInput  label="capdev.regionDimensionQuestion" name="capdev.sRegional" value="${(capdev.sRegional)!'-1'}" inverse=false  cssClass="regional" editable=editable/] 
      </div>
    </div>

    
    <!-- Regions-->
    <div class="form-group capdevRegional regionsBox" style="display:${((capdev.regional)!false)?string('block','none')};" >
      <div class="panel tertiary">
        <div id="capdevRegionsList">
          <div class=" panel-body" listname="capdev.regions">
            [@customForm.select name="" listName="regionsList" keyFieldName="id" displayFieldName="name" help="capdev.help.region" i18nkey="capdev.form.region" className="capdevRegionsSelect" multiple=false placeholder="capdev.select" disabled=!editable /]
            <ul class="list">
              [#if capdev.capDevRegions?has_content]
                [#list capdev.capDevRegions as region]
                  <li id="" class="capdevRegion  clearfix col-md-4">
                    [#if editable]
                      <a class="removeCountry removeIcon" title="Remove country" href="[@s.url action='${centerSession}/deleteRegion'][@s.param name="capdevID" value=capdevID /][@s.param name="projectID" value=projectID /][@s.param name="edit" value=true /][@s.param name="capdevRegion" value=region.id /][/@s.url]"></a>
                    [/#if]
                    <input class="id" type="hidden" name="capdev.capDevRegions[${region_index}].id" value="${(region.id)!}" />
                    <input class="rId" type="hidden" name="capdev.capDevRegions[${region_index}].locElement.id" value="${(region.locElement.id)!}" />
                    ${(region.locElement.name)!}
                    <div class="clearfix"></div>
                  </li>
                [/#list] 
              [#else]
                <p class="emptyText"> [@s.text name="capdev.notRegions" /]</p> 
              [/#if]
            </ul>
          </div>
        </div>
      </div>
    </div>


    <div class="capdevDimension"><label>If the capacity development intervention focuses on specific countries, please list these countries:</label></div>
    <!-- countries-->
    <div class="form-group  countriesBox">
      <div class="panel tertiary">
        <div id="capdevCountriesList">
          <div class="panel-body" listname="capdev.countries">
            [@customForm.select name="" listName="countryList" keyFieldName="id" displayFieldName="name" help="capdev.help.country" i18nkey="capdev.form.country" className="capdevCountriesSelect" multiple=false placeholder="capdev.select"  disabled=!editable/]
            <ul class="list">
              [#if capdev.capDevCountries?has_content]
              [#list capdev.capDevCountries as country]
              <li id="" class="capdevCountry clearfix col-md-4">
                [#if editable]
                  <a class=" removeCountry removeIcon" title="Remove country" href="[@s.url action='${centerSession}/deleteCountry'][@s.param name="capdevID" value=capdevID /][@s.param name="projectID" value=projectID /][@s.param name="edit" value=true /][@s.param name="capdevCountry" value=country.id /][/@s.url]"></a>
                [/#if]
                <input class="id" type="hidden" name="capdev.capDevCountries[${country_index}].id" value="${(country.id)!}" />
                <input class="cId" type="hidden" name="capdev.capDevCountries[${country_index}].locElement.id" value="${(country.locElement.id)!}" />
                ${(country.locElement.name)!}
                <div class="clearfix"></div>
              </li>
              [/#list]
              [#else]
              <p class="emptyText"> [@s.text name="capdev.notCountries" /]</p> 
              [/#if]
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
[/#macro]

[#macro capdevList element={} ]
	
	<table class="table table-bordered ">
    <thead class="thead-default">
      <tr class="header">
        <th>Code</th>
        <th>Name</th>
        <th>Last Name</th>
        <th>Gender</th>
        <th>Citizenship</th>
        <th>Highest degree</th>
        <th>Institution</th>
        <th>Country of institucion</th>
        <th>Email</th>
        <th>Reference</th>
        <th>Fellowship</th>
        <th>Institution Suggested</th>
      </tr>
    </thead>
    <tbody>
    	[#list element as i]
    		<tr>
    			[#if i.participant.code??]
    				<td>${i.participant.code}</td>
    			[#else]
    				<td>Not defined</td>
    			[/#if]
    			[#if i.participant.name??]
    				<td>${i.participant.name}</td>
    			[#else]
    				<td>Not defined</td>
    			[/#if]
    			[#if i.participant.lastName??]
    				<td>${i.participant.lastName}</td>
    			[#else]
    				<td>Not defined</td>
    			[/#if]
    			[#if i.participant.gender??]
    				<td>${i.participant.gender}</td>
    			[#else]
    				<td>Not defined</td>
    			[/#if]
    			[#if i.participant.locElementsByCitizenship??]
    				<td>${i.participant.locElementsByCitizenship.name}</td>
    			[#else]
    				<td>Not defined</td>
    			[/#if]
    			[#if i.participant.highestDegree??]
    				<td>${i.participant.highestDegree.name}</td>
    			[#else]
    				<td>Not defined</td>
    			[/#if]
    			[#if i.participant.institutions??]
    				<td>${i.participant.institutions.name}</td>
    			[#else]
    				<td>Not defined</td>
    			[/#if]
    			[#if i.participant.locElementsByCountryOfInstitucion??]
    				<td>${i.participant.locElementsByCountryOfInstitucion.name}</td>
    			[#else]
    				<td>Not defined</td>
    			[/#if]
    			[#if i.participant.email??]
    				<td>${i.participant.email}</td>
    			[#else]
    				<td>Not defined</td>
    			[/#if]
    			[#if i.participant.reference??]
    				<td>${i.participant.reference}</td>
    			[#else]
    				<td>Not defined</td>
    			[/#if]
    			[#if i.participant.fellowship??]
    				<td>${i.participant.fellowship.name}</td>
    			[#else]
    				<td>Not defined</td>
    			[/#if]
    			[#if i.participant.institutionsSuggested??]
    				<td>${i.participant.institutionsSuggested}</td>
    			[#else]
    				<td></td>
    			[/#if]
    			
    		</tr>
    	[/#list]
   
    </tbody>
  </table>
	
[/#macro]





[#macro outComeMacro element isTemplate=false]
	<div id="outcome-${isTemplate?string('template',(element)!)}" class="outcome  borderBox col-md-4 " style="display:${isTemplate?string('none','block')}" >
		<div class="removeOutCome removeIcon" title="Remove outcome"></div>
		<div class="col-md-4">
			 [@s.text name="element" /]
		</div>
	</div>
[/#macro]