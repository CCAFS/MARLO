[#ftl]

[#assign title = "Capacity Development" /]

[#assign customCSS = ["${baseUrl}/global/css/customDataTable.css",
					  "${baseUrlMedia}/css/capDev/capacityDevelopment.css"] /]

[#assign pageLibs = ["select2","flat-flags"] /]

[#assign customJS = ["${baseUrlMedia}/js/capDev/capacityDevelopment.js", 
					 "${baseUrl}/global/js/fieldsValidation.js",
					 "${baseUrl}/global/js/autoSave.js"] /]

[#assign currentStage = "capdevIntervention" /]

[#assign breadCrumb = [
  {"label":"capdevList", "nameSpace":"/capdev", "action":"${(centerSession)!}/capdev"},
  {"label":"capdevDetail", "nameSpace":"/capdev", "action":""}
]/]

[#include "/WEB-INF/center/pages/header.ftl" /]
[#include "/WEB-INF/center/pages/main-menu.ftl" /]



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
		        	<a class="" href="[@s.url action='${centerSession}/capdev' /] "><span class="glyphicon glyphicon-circle-arrow-left"></span>[@s.text name="capdev.gotoBack" /]</a>
		        [/#if]
				 
			</div>
		
		
		<div class=" form-group "> 

			[@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
			<!-- Radio Buttons-->
			
			<div class="radio">
			  <label><input  id="individual" type="radio" hidden="true" name="capdev.category" class="radioButton" value="${(capdev.category)!}"  /></label>
			</div>
		
		
			<div class="radio">
			  <label><input id="gruops" type="radio" hidden="true" name="capdev.category" class="radioButton"  value="${(capdev.category)!}" /> </label>
			</div>	
				

			<div  class="fullForm borderBox" >
				<!-- Title-->
				<div class="form-group row " >
					<div class="col-md-12 "> 
						[@customForm.input name="capdev.title" type="text" help="capdev.help.title" i18nkey="capdev.form.title"  required=true disabled=!editable editable=editable /]
					</div>
				</div>

				<!-- type and contact Person -->
				<div class="form-group row">
						<!-- type-->
						<div class="col-md-6" > 
							[@customForm.select name="capdev.capdevType.id" listName="capdevTypes" keyFieldName="id" displayFieldName="name" help="capdev.help.type" i18nkey="capdev.form.type"  placeholder="capdev.select" required=true editable=editable disabled=!editable/]
						</div>

						<!-- Contact person -->
						<div class="col-md-6 contactField" style="display:${((capdev.category == 2)!false)?string('block','none')};">
							[@customForm.input name="contact" i18nkey="capdev.form.contactPerson" type="text" help="capdev.help.contact" className='contact'  readOnly=true   editable=editable /]
							<input class="ctFirsName" type="hidden" name="capdev.ctFirstName" value="${(capdev.ctFirstName)!}" /> 
							<input class="ctLastName" type="hidden" name="capdev.ctLastName" value="${(capdev.ctLastName)!}" /> 
							<input class="ctEmail" type="hidden" name="capdev.ctEmail" value="${(capdev.ctEmail)!}" /> 
						</div>
				</div>

				<!-- dates -->
				<div class="form-group row ">
					<!-- <div class="col-md-12 newCapdevField"> -->
						<!-- Strart date-->
						<div class="col-md-6 ">
							[@customForm.input name="capdev.startDate" i18nkey="capdev.form.startDate" type="text"  help="capdev.help.startDate" required=true  editable=editable className="capdevstartDate"/]
						</div>
						<!-- end date-->
						<div class="col-md-6 ">
							[@customForm.input name="capdev.endDate" i18nkey="capdev.form.endDate" type="text" help="capdev.help.endDate" editable=editable /]
						</div>
					<!-- </div> -->
				</div>


				<!-- Duration -->
				<div class="form-group row ">
					<div class="col-md-3">
						[@customForm.input name="capdev.duration" i18nkey="capdev.form.duration" type="text"  help="capdev.help.duration"   editable=editable className="capdevDuration"/] 
					</div>

					<div class="col-md-3 durationUnitSelect">
						<input type="hidden" name="" value="${(capdev.durationUnit)!}" class="durationUnitaInput"/>
						[@customForm.select name="capdev.durationUnit" listName="durationUnit" keyFieldName="value" value="'${(capdev.durationUnit)!}'" displayFieldName="displayName" help="capdev.help.durationUnit" i18nkey="capdev.form.durationUnit"  placeholder="capdev.select"  editable=editable className="dUnitSelect"/]
					</div>
				</div>

				


				<!-- groups participants-->
				<div style="display:${((capdev.category == 2)!false)?string('block','none')};">
					<h4 class="form-group headTitle newCapdevField grupsParticipantsForm" >Participants Information</h4>
					<div class="form-group row  simpleBox" >
						[#if capdev.capdevParticipant?has_content && capdev.capdevParticipant?size > 1] 
							<div class="capdevParticipantsTable">
								<div class="capdev-participantslist-title">List of participants</div>
								[@capdevList capdev.capdevParticipant /]
								<div class="col-md-12">
									<div class="pull-right">
										[#if editable]	
								        	<button type="button" class="" title="Delete list of participants">
								        		<a id="" class="removeCapdev" data-href=""  data-toggle="modal" data-target="#confirm-clear-participantList"> Clear
									            </a>
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

						<!-- num participants, num men and num women -->
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
				</div>

				



				<!-- induvidual participant-->
				<div style="display:${((capdev.category == 1)!false)?string('block','none')};">
					<div class="loading syncBlock" style="display:none"></div>
					<h4 class="headTitle newCapdevField individualparticipantForm">Participant Information</h4>
						<div class="note  individualparticipantForm">
							<p>Please complete the information of the trainee who received the individual capdev intervention. </p>

							<p>For visiting researchers or practicum students enter the CIAT employee number and press the green button to syncronize information</p>
						</div>
					<div class="row  individualparticipantForm simpleBox" style="display:none;">

						<input type="hidden" name="capdev.participant.id" value="${(capdev.participant.id)!}" class="genderInput"/>
						<!-- participant code -->
						<div class="form-group row">
							<div class="col-md-9">
								[@customForm.input name="capdev.participant.code" i18nkey="capdev.participant.code" type="text" required=true className="participant-code"  help="capdev.help.participant.code" editable=editable/]
							</div>
							[#if editable]
							<div class="col-md-2">
								<div id="syncBoton" class="checkButton syncParticipant" style="margin-top:21px;">[@s.text name="capdev.participant.code.sync" /]</div>
							</div>
							[/#if]
						</div>
						<!-- participant name and middle name -->
						<div class="form-group row">
							<div class="col-md-6">
								[@customForm.input name="capdev.participant.name" i18nkey="capdev.participant.firstName" type="text" className="participant-name" required=true editable=editable/]
							</div>
							<div class=" col-md-6">
								[@customForm.input name="capdev.participant.middleName" i18nkey="capdev.participant.middleName" type="text"  editable=editable/]
							</div>
						</div>
						<!-- participant last name and gender -->
						<div class="form-group row ">
							<div class="form-group col-md-6">
								[@customForm.input name="capdev.participant.lastName" i18nkey="capdev.participant.lastName" type="text" className="participant-lastname"  required=true editable=editable/]
							</div>
							<div class="form-group col-md-6 genderSelect">
								<input type="hidden" name="" value="${(participant.gender)!}" class="genderInput"/>
								[@customForm.select name="capdev.participant.gender" value="'${(capdev.participant.gender)!}'" listName="genders" keyFieldName="value" displayFieldName="displayName" help="" i18nkey="capdev.participant.gender"  placeholder="capdev.select" required=true editable=editable className=""/]
							</div>
						</div>
						<!-- participant citizenship and highest degree -->
						<div class="form-group row">
							<div class="col-md-6 pCitizenshipcountriesList">
								[@customForm.select name="capdev.participant.locElementsByCitizenship.id" listName="countryList" keyFieldName="id" displayFieldName="name" help="" i18nkey="capdev.participant.citizenship" className="" multiple=false placeholder="capdev.select" required=true editable=editable/]
							</div>
							<div class="col-md-6">
								[@customForm.select name="capdev.participant.highestDegree.id" listName="highestDegrreList" keyFieldName="id" displayFieldName="name"  i18nkey="capdev.participant.Highestdegree"  multiple=false placeholder="capdev.select"  editable=editable/]
							</div>
						</div>
						<!-- participant personal email and job email -->
						<div class="form-group row  ">
							<div class="col-md-6">
								[@customForm.input name="capdev.participant.personalEmail" i18nkey="capdev.participant.personalEmail" type="text" className="participant-pEmail"  required=true editable=editable/]
							</div>
							<div class="col-md-6">
								[@customForm.input name="capdev.participant.email" i18nkey="capdev.participant.Email" type="text" editable=editable /]
							</div>
						</div>
						<!-- intitucion and country of institution -->
						<div class="form-group row">
							<div class="col-md-6">
								[@customForm.select name="capdev.participant.institutions.id" listName="institutions" keyFieldName="id" displayFieldName="name" help="" i18nkey="capdev.participant.Institution" className="" multiple=false placeholder="capdev.select" editable=editable /]

								[#if editable]
									<div class="note participantMessage">
										<p>If you cannot find the institution you are looking for, suggest another one by clicking on the box <b>"Other"</b></p>
									</div>
								[/#if]

								<div>

									<label>Other <input type="checkbox" name="capdev.participant.otherInstitution" class="otherInstcheck"   [#if (capdev.participant.otherInstitution)??]
									[#if (capdev.participant.otherInstitution) == "1"] checked="checked" [/#if] value="${(capdev.participant.otherInstitution)!}"[/#if]  [#if !editable] disabled="true"[/#if] > </label>
									<div class="suggestInstitution" style="display: none;">[@customForm.textArea name="capdev.participant.institutionsSuggested" i18nkey="Suggest institution"  className="textarea" editable=editable /]</div>
								</div>
							

							</div>
							<div class="col-md-6 pcountryOfInstitucionList">
								[@customForm.select name="capdev.participant.locElementsByCountryOfInstitucion.id" listName="countryList" keyFieldName="id" displayFieldName="name" help="" i18nkey="capdev.participant.country" className="" multiple=false placeholder="capdev.select" editable=editable /]
							</div>
						</div>
						<!-- supervisor and funding type -->
						<div class="form-group row">
							<div class="col-md-6">
								[@customForm.input name="capdev.participant.supervisor" i18nkey="capdev.participant.Supervisor" type="text" className="participant-supervisor"  required=true editable=editable/]
							</div>
							<div class="col-md-6">
								<!-- [@customForm.input name="participant.fellowship" i18nkey="capdev.participant.Fellowship" type="text" /] -->
								[@customForm.select name="capdev.participant.fellowship.id" listName="foundingTypeList" keyFieldName="id" displayFieldName="name"  i18nkey="capdev.participant.Fellowship"  multiple=false placeholder="capdev.select" editable=editable /]
							</div>
						</div>
					</div>
				</div>


				

				<!-- Regions and countries lists -->
				<h4 class="headTitle newCapdevField">Reach</h4>	
				<div class=" newCapdevField form-group">
					<div class="simpleBox">
					
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

						
						<!-- regions-->
						<div class="form-group capdevRegional regionsBox" style="display:${((capdev.regional)!false)?string('block','none')};" >
							<div class="panel tertiary">
								<div id="capdevRegionsList">
									<div class=" panel-body" listname="capdev.regions">
										[@customForm.select name="" listName="regionsList" keyFieldName="id" displayFieldName="name" help="capdev.help.region" i18nkey="capdev.form.region" className="capdevRegionsSelect" multiple=false placeholder="capdev.select" disabled=!editable /]
										<ul class="list">
											[#if capdev.capDevRegions?has_content]
											[#list capdev.capDevRegions as region]
												<li id="" class="capdevRegion  clearfix col-md-3">
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
											<li id="" class="capdevCountry clearfix col-md-3">
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

				


				<div style="display: none;">
					[@customForm.input name="capdevID" i18nkey="capdev.id" value="${capdev.id}"  type="text" className="capdev-id" /]
					[@customForm.input name="category" i18nkey="capdev.category" value="${capdev.category}"  type="text"  /]
					[@customForm.input name="projectID" i18nkey="capdev.category" value="${projectID}"  type="text"  /]
				</div>




				<!-- buttons -->
				[#include "/WEB-INF/center/views/capDev/capdev-buttons.ftl" /]


			</div>

			[/@s.form]
		</div>
	

	</div>
	

</div>




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

<!-- region list template-->
<ul style="display:none">
  <li id="capdevRegionTemplate" class="capdevRegion clearfix col-md-4">
      <div class="removeRegion removeIcon" title="Remove region"></div>
      <input class="id" type="hidden" name="" value="" />
      <input class="rId" type="hidden" name="capdev.capDevRegions[-1].locElement.id" value="" />
      <span class="name"></span>
      <div class="clearfix"></div>
    </li>
</ul>




[#include "/WEB-INF/center/views/capDev/searchContactPerson.ftl" /]



[#include "/WEB-INF/center/pages/footer.ftl"]



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









