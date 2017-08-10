[#ftl]

[#assign customCSS = ["${baseUrlMedia}/css/global/customDataTable.css"] /]
[#assign customCSS = ["${baseUrlMedia}/css/capDev/capacityDevelopment.css"] /]
[#assign customJS = ["${baseUrlMedia}/js/capDev/capacityDevelopment.js"] /]


[#assign breadCrumb = [
  {"label":"capdevList", "nameSpace":"/capdev", "action":"${(centerSession)!}/capdev"},
  {"label":"capdevDetail", "nameSpace":"/capdev", "action":""}
]/]

[#include "/WEB-INF/center/global/pages/header.ftl" /]
[#include "/WEB-INF/center/global/pages/main-menu.ftl" /]







<script src="${baseUrl}/bower_components/jquery/dist/jquery.min.js"></script>
<script src="${baseUrlMedia}/js/capDev/capacityDevelopment.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.7.7/xlsx.core.min.js"></script>


<div class="container"> 

	<div class="row">
		<div class="helpMessage infoText col-md-12 capdevinfo">
				<img class="col-md-2" src="${baseUrlMedia}/images/global/icon-help.png" />
   				 <p class="col-md-10"> [@s.text name="capdev.help.detail"][/@s.text] </p>
			</div>
	</div> 

	<div class="col-md-3 capDevMenu">
		[#include "/WEB-INF/center/views/capDev/menu-capdev.ftl" /]
	</div>
	
	
	<div class="col-md-9 ">
	
		<div class="col-md-12">
			<div class="pull-right">
				<a class="" href="[@s.url action='${centerSession}/capdev' /] "><span class="glyphicon glyphicon-circle-arrow-left"></span>[@s.text name="capdev.gotoBack" /]</a> 
			</div>
		</div>
		
		<div class="col-md-12 form-group newCapdevForm"> 

			[@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
			<!-- Radio Buttons-->
			<div class="row newCapdevField" > 
				
				<div class="col-md-12">
					<div class="col-md-3">
						<div class="radio">
						  <label><input  id="individual" type="radio" hidden="true" name="capdev.category" class="radioButton" value="${capdev.category}"  /></label>
						</div>
					</div>
					<div class="col-md-3">
						<div class="radio">
						  <label><input id="gruops" type="radio" hidden="true" name="capdev.category" class="radioButton"  value="${capdev.category}" /> </label>
						</div>
					</div>
					
				</div>
			</div>

			<div  class="fullForm" >
				<!-- Title-->
				<div class="row newCapdevField" >
					<div class="col-md-12 "> 
						<div class="" > 
							[@customForm.input name="capdev.title" type="text" help="capdev.help.title" i18nkey="capdev.form.title"  required=true  /]
						</div>
					</div>
				</div>

				<!-- type and contact Person -->
				<div class="row">
					<div class="col-md-12 newCapdevField"> 
						<!-- type-->
						<div class="col-md-6 "> 
							[@customForm.select name="capdev.capdevType.id" listName="capdevTypes" keyFieldName="id" displayFieldName="name" help="capdev.help.type" i18nkey="capdev.form.type"  placeholder="capdev.select" required=true editable=true/]
						</div>

						<!-- Contact person -->
						<div class="col-md-6 contactField group individual">
							[@customForm.input name="contact" i18nkey="capdev.form.contactPerson" type="text" help="capdev.help.contact" className='contact'  required=true  /]
							<input class="ctFirsName" type="hidden" name="capdev.ctFirstName" value="${(capdev.ctFirstName)!}" /> 
							<input class="ctLastName" type="hidden" name="capdev.ctLastName" value="${(capdev.ctLastName)!}" /> 
							<input class="ctEmail" type="hidden" name="capdev.ctEmail" value="${(capdev.ctEmail)!}" /> 
						</div>
					</div>
				</div>

				<!-- dates -->
				<div class="row ">
					<div class="col-md-12 newCapdevField">
						<!-- Strart date-->
						<div class="col-md-6 ">
							[@customForm.input name="capdev.startDate" i18nkey="capdev.form.startDate" type="text"  help="capdev.help.startDate" required=true  editable=true className="capdevstartDate"/]
						</div>
						<!-- end date-->
						<div class="col-md-6 ">
							[@customForm.input name="capdev.endDate" i18nkey="capdev.form.endDate" type="text" help="capdev.help.endDate" editable=true /]
						</div>
					</div>
				</div>


				<!-- Duration -->
				<div class="row newCapdevField">
					<div class="col-md-4">
						[@customForm.input name="capdev.duration" i18nkey="capdev.form.duration" type="text"  help="capdev.help.duration" required=true  editable=true className="capdevstartDate"/] 
					</div>
				</div>

				


				<!-- groups participants-->
				<div class="row grupsParticipantsForm">
					[#if capdev.capdevParticipants?has_content && capdev.capdevParticipants?size > 1] 
						<div class="capdevParticipantsTable">
							<div class="capdev-participantslist-title">List of participants</div>
							[@capdevList capdev.capdevParticipants /]
							<div class="col-md-12">
								<div class="pull-right">	
						        	<button type="button" class="" title="Delete list of participants">
						        		<a id="" class="removeCapdev" href="[@s.url action='${centerSession}/delete_list_of_participants'][@s.param name='capdevID']${capdevID}[/@s.param][@s.param name='capdevCategory']${capdevCategory}[/@s.param] [/@s.url]" title="" > Clear
							            </a>
						        	</button>
			    				</div>
							</div>
						</div>
					[#else]
						<div class="col-md-12 newCapdevField participantsheader">
							<div class="col-md-6  participantsTitle">
								[@s.label key="capdev.form.participants" /]
							</div>
							<div class="col-md-6 ">
								<div class="pull-right">
									<button type="button" class="capdevButtons" aria-label="Left Align" title="Download template to upload the list of participants">
										<a class="downloadButton" href="[@s.url action='${centerSession}/downloadFile' /] ">[@s.text name="capdev.downloadTemplate" /]</a> 
									</button>
								</div>
							</div>
						</div>

						<div class="row newCapdevField">
							<div class="col-md-12 newCapdevField participantsBox">
								[@s.fielderror fieldName="upload_File" class="fileError" /]
								<div class="col-md-12">
									[@s.file id="uploadFile" name="uploadFile" label="Select a File to upload" size="40" class="uploadParticipants"/]
								</div>
								

								<div class="col-md-12" style="margin-top: 10px;">
								<div class="btnPreview">
									<button type="button"  id="btnDisplay" class="capdevButtons" aria-label="Left Align" data-toggle="modal" data-target="#myModa"       title="Take a look to list of participants uploaded">
										preview
									</button>
								</div>
								

								<div id="filewarning" class="warning" style="display: none; margin-top: 10px;">
								</div>
								<div class="loader" style="display:none;"><img src="${baseUrlMedia}/images/global/loading_2.gif" width="80" height="30"></div>

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
					
					
				</div>

				<!-- num participants, num men and num women -->
				<div class="row">
					<div class="col-md-12 newCapdevField">
						<div class="col-md-6 group individual">
							[@customForm.input name="capdev.numParticipants" i18nkey="capdev.form.numParticipants" type="text" help="capdev.help.numParticipants"  editable=true className="numParticipants" /]
						</div>
						<div class="col-md-3 group individual">
							[@customForm.input name="capdev.numMen" i18nkey="capdev.form.numMen" help="capdev.help.numMen" type="text" className="numMen" /]
						</div>
						<div class="col-md-3 group individual">
							[@customForm.input name="capdev.numWomen" i18nkey="capdev.form.numWomen" help="capdev.help.numMen" type="text" className="numMen" /]
						</div>
					</div>
				</div>

				<div class="row grupsParticipantsForm">
					<div class="note participantMessage">
						<p>If you don’t have a list of participants, please enter the number of participants, number of men and number of women</p>
					</div>
				</div>



				<!-- induvidual participant-->
				<div class="row newCapdevField individualparticipantForm" style="display:none;">
					<div class="col-md-12 ">
						<div class=" participantsTitle newCapdevField" >
							[@s.text name="capdev.participant"][/@s.text]
						</div>
					</div>
					<div class="col-md-12 newCapdevField">
						[@customForm.input name="participant.code" i18nkey="capdev.participant.code" type="text" required=true className="participant-code"  help="capdev.help.participant.code"/]
					</div>
					<div class="col-md-12 newCapdevField">
						<div class="col-md-6">
							[@customForm.input name="participant.name" i18nkey="capdev.participant.firstName" type="text" required=true /]
						</div>
						<div class="col-md-6">
							[@customForm.input name="participant.middleName" i18nkey="capdev.participant.middleName" type="text"  /]
						</div>
					</div>
					<div class="col-md-12 newCapdevField">
						<div class="col-md-6">
							[@customForm.input name="participant.lastName" i18nkey="capdev.participant.lastName" type="text" required=true /]
						</div>
						<div class="col-md-6 genderSelect">
							<input type="hidden" name="" value="${(participant.gender)!}" class="genderInput"/>
							[@customForm.select name="participant.gender" listName="genders" keyFieldName="value" displayFieldName="displayName" help="" i18nkey="capdev.participant.gender"  placeholder="capdev.select" required=true editable=true className=""/]
						</div>
						
					</div>
					<div class="col-md-12 newCapdevField">
						
						<div class="col-md-6 pCitizenshipcountriesList">
							
							[@customForm.select name="participant.locElementsByCitizenship.id" listName="countryList" keyFieldName="id" displayFieldName="name" help="" i18nkey="capdev.participant.citizenship" className="" multiple=false placeholder="capdev.select" required=true /]
						</div>
						<div class="col-md-6">
							<!-- [@customForm.input name="participant.highestDegree" i18nkey="capdev.participant.Highestdegree" type="text" /] -->

							[@customForm.select name="participant.highestDegree.id" listName="highestDegrreList" keyFieldName="id" displayFieldName="name"  i18nkey="capdev.participant.Highestdegree"  multiple=false placeholder="capdev.select"  /]
						</div>
					</div>
					<div class="col-md-12 newCapdevField">
						
						<div class="col-md-6">
							[@customForm.input name="participant.personalEmail" i18nkey="capdev.participant.personalEmail" type="text" required=true /]
						</div>
						<div class="col-md-6">
							[@customForm.input name="participant.email" i18nkey="capdev.participant.Email" type="text"  /]
						</div>
					</div>
					<div class="col-md-12 newCapdevField">
						<div class="col-md-6">
							
							[@customForm.select name="participant.institutions.id" listName="institutions" keyFieldName="id" displayFieldName="name" help="" i18nkey="capdev.participant.Institution" className="" multiple=false placeholder="capdev.select" required=true /]
						</div>
						<div class="col-md-6 pcountryOfInstitucionList">
							
							[@customForm.select name="participant.locElementsByCountryOfInstitucion.id" listName="countryList" keyFieldName="id" displayFieldName="name" help="" i18nkey="capdev.participant.country" className="" multiple=false placeholder="capdev.select" required=true /]
						</div>
					</div>
					<div class="col-md-12 newCapdevField">
						<div class="col-md-6">
							[@customForm.input name="participant.supervisor" i18nkey="capdev.participant.Supervisor" type="text" required=true /]
						</div>
						<div class="col-md-6">
							<!-- [@customForm.input name="participant.fellowship" i18nkey="capdev.participant.Fellowship" type="text" /] -->
							[@customForm.select name="participant.fellowship.id" listName="foundingTypeList" keyFieldName="id" displayFieldName="name"  i18nkey="capdev.participant.Fellowship"  multiple=false placeholder="capdev.select"  /]
						</div>
					</div>
					
				</div>

				

				<!-- Regions and countries lists -->
				<div class="row">
					<div class="col-md-12">
						<!-- regions-->
						<div class="col-md-5 listContainer panel ">
							<div class="newCapdevField ">
								[@customForm.select name="capdevRegions" listName="regionsList" keyFieldName="id" displayFieldName="name" help="capdev.help.region" i18nkey="capdev.form.region" className="capdevRegionsSelect" multiple=false placeholder="capdev.select"  /]
							</div>

							<div id="capdevRegionsList" class="newCapdevField regionsList">
								<div class="row">
									<div class="col-md-12 panel-body">
										
										<ul class="list">
											[#if capdev.capDevRegions?has_content]
											[#list capdev.capDevRegions as region]
												<li id="" class="capdevRegion  clearfix col-md-3">
													<div class="removeRegion-action removeRegion removeIcon" title="Remove region"></div>
													<input class="id" type="hidden" name="capdev.capDevRegions[${region_index}].id" value="${(region.id)!-1}" />
													<input class="rId" type="hidden" name="capdev.capDevRegions[${region_index}].locElement.id" value="${(region.locElement.id)!}" />
													${region.locElement.name}
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

						<!-- countries-->
						<div class="col-md-5 listContainer panel">

							<div class="newCapdevField">
								[@customForm.select name="capdevCountries" listName="countryList" keyFieldName="id" displayFieldName="name" help="capdev.help.country" i18nkey="capdev.form.country" className="capdevCountriesSelect" multiple=false placeholder="capdev.select"  /]
							</div>
							<div id="capdevCountriesList" class="newCapdevField countriesList">
								<div class="row">
									<div class="col-md-12 panel-body">
										<ul class="list">
											[#if capdev.capDevCountries?has_content]
											[#list capdev.capDevCountries as country]
											<li id="" class="capdevCountry clearfix col-md-3">
												<div class="removeCountry-action removeCountry removeIcon" title="Remove country"></div>
												<input class="id" type="hidden" name="capdev.capDevCountries[${country_index}].id" value="${(country.id)!-1}" />
												<input class="cId" type="hidden" name="capdev.capDevCountries[${country_index}].locElement.id" value="${(country.locElement.id)!}" />
												${country.locElement.name}
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
					[@customForm.input name="capdevCategory" i18nkey="capdev.category" value="${capdev.category}"  type="text"  /]
				</div>




				<!-- buttons -->
				<div class="col-md-12">
						<div class="buttons">	
				        	<div class="buttons-content">        
					          	[@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /] [/@s.submit]
					        	<div class="clearfix"></div>
				        	</div>
	    				</div>
				</div>

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
      <input class="cId" type="hidden" name="capdevCountries[-1]" value="" />
      <span class="name"></span>
      <div class="clearfix"></div>
    </li>
</ul>

<!-- region list template-->
<ul style="display:none">
  <li id="capdevRegionTemplate" class="capdevRegion clearfix col-md-4">
      <div class="removeRegion removeIcon" title="Remove region"></div>
      <input class="id" type="hidden" name="" value="" />
      <input class="rId" type="hidden" name="capdevRegions[-1]" value="" />
      <span class="name"></span>
      <div class="clearfix"></div>
    </li>
</ul>




[#include "/WEB-INF/center/views/capDev/searchContactPerson.ftl" /]



[#include "/WEB-INF/center/global/pages/footer.ftl"]



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
      </tr>
    </thead>
    <tbody>
    	[#list element as i]
    		<tr>
    			<td>${i.participant.code}</td>
    			<td>${i.participant.name}</td>
    			<td>${i.participant.lastName}</td>
    			<td>${i.participant.gender}</td>
    			<td>${i.participant.locElementsByCitizenship.name}</td>
    			<td>${i.participant.highestDegree}</td>
    			<td>${i.participant.institutions.name}</td>
    			<td>${i.participant.locElementsByCountryOfInstitucion.name}</td>
    			<td>${i.participant.email}</td>
    			<td>${i.participant.reference}</td>
    			<td>${i.participant.fellowship}</td>
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









