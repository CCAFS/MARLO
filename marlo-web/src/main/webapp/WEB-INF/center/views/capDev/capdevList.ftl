[#ftl]

[#assign customCSS = ["${baseUrlMedia}/css/global/customDataTable.css"] /]
[#assign customCSS = ["${baseUrlMedia}/css/capDev/capacityDevelopment.css"] /]
[#assign customJS = ["${baseUrlMedia}/js/capDev/capacityDevelopment.js", "${baseUrlMedia}/js/capDev/capdevList.js"] /]

[#include "/WEB-INF/center/global/pages/header.ftl" /]
[#include "/WEB-INF/center/global/pages/main-menu.ftl" /]


<script src="${baseUrl}/bower_components/jquery/dist/jquery.min.js"></script>
<script src="${baseUrlMedia}/js/capDev/capacityDevelopment.js"></script>

	<div class="container">
		<div class="row titleContainer">
			<div class="col-md-12">
				<p>Capacity Development Tracking Tool</p>
			</div>
			
		
		</div>

		<div class="row">
			<div class="col-md-12 capdevinfo">
				introduction text
			</div>
		</div>
		<div class="row">
			<div class="col-md-12 titleContainer">
				Latest CAPDEV Interventions
			</div>
		</div>
		
		
		<div class="row searchArea">
			<div class="col-md-12">
				<div class="pull-right">
	  				<img src="${baseUrlMedia}/images/global/search.png" class="searchIcon" />
				</div>
				<div class="pull-right ">
						<input id="capdevSearchInput" type="search" name="search" class="form-control input-sm searchInput" aria-controls="projects"><br>
				</div> 
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<table id="capdevs" class="table table-bordered capdevTable">
					<thead>
					  <tr>
					    <th style="width: 3%">ID</th>
					    <th style="width: 15%">Title</th> 
					    <th style="width: 7%">Type</th>
					    <th style="width: 7%">Start date</th>
					    <th style="width: 7%">End date</th>
					    <th style="width: 7%">Research Area</th>
					    <th style="width: 7%">Research Program</th>
					    <th style="width: 7%">Annexes</th>
					  </tr>
				  	</thead>
				  	<tbody id="capdevTbody">
					  [#if capDevs?has_content]
					  [#list capDevs as i]

					  <tr >
					    <td>C${i.id}</td>
					    [#if i.title??]
					    <td>
					    	<a href="[@s.url action='${centerSession}/detailCapdev'][@s.param name='capdevID']${i.id?c}[/@s.param][/@s.url]">${i.title}</a>
					    </td> 
					    <td>${i.capdevType.name}</td>
					    <td>${i.startDate}</td>
					    [#else]
					    <td>
					    	<a href="[@s.url action='${centerSession}/detailCapdev'][@s.param name='capdevID']${i.id?c}[/@s.param][/@s.url]">Not defined</a>
					    </td> 
					    <td>Not defined</td>
					    <td>Not defined</td>
					    [/#if]
					    <td>
					    [#if i.endDate??]
					    	${i.endDate}
					    [#else]
					    	Not defined
					    [/#if]
					    </td>
					    <td>
					    [#if i.researchArea??] 
					    	${i.researchArea.name}
					    [#else]
					    	Not defined
					    [/#if]
					    </td>
					    <td>
					    [#if i.researchProgram??]
					    	${i.researchProgram.name}
					    [#else]
					    	Not defined
					    [/#if]
					    </td>
					    <td>
					    [#if i.capdevParticipants?has_content]
					    [#if i.capdevParticipants?size  > 1 ]
				    		<div class=" iconContentBox">
				    			<img src="${baseUrlMedia}/images/global/participants.png" class="capDevIcon" title="List of participants" />
				    		</div>
				    	[#else]
				    	[/#if]
				    	[#else]
				    	[/#if]

				    		<div class=" iconContentBox">
				    			<img src="${baseUrlMedia}/images/global/deliverable.png" class="capDevIcon" title="Supporting documents" />
				    		</div>
					    </td>
					  </tr>
					  [/#list]

					  [/#if]
				  	</tbody>
				</table>
			</div>
		</div>
		<div class="row">
			<div class="pull-right">
				<div class="col-md-12">
						<div class="buttons">	
				        	<div class="buttons-content ">        
					          	<!-- <a class="addButton" href="[@s.url action='${centerSession}/addCapdev' ][@s.param name='capdeID']${capdevID}[/@s.param][@s.param name='capdevCategory']2[/@s.param][/@s.url]">[@s.text name="capdev.addgroupItem" /]</a> -->
					          	<a class="addButton" href="[@s.url action='${centerSession}/addCapdev'][@s.param name='capdevID']${capdevID}[/@s.param][@s.param name='capdevCategory']2[/@s.param][/@s.url]">[@s.text name="capdev.addgroupItem" /]</a>
					        	<!-- <div class="clearfix"></div> -->
				        	</div>
				        	<div class="buttons-content ">        
					          	<a class="addButton" href="[@s.url action='${centerSession}/addCapdev' ][@s.param name='capdevID']${capdevID}[/@s.param][@s.param name='capdevCategory']1[/@s.param][/@s.url]">[@s.text name="capdev.addindividualItem" /]</a>
					        	<!-- <div class="clearfix"></div> -->
				        	</div>
	    				</div>
	    		</div>
			</div>
		</div>

		<div class="row conventionContainer">
			<div class="col-md-12 itemName">
				<div class="col-md-2"> Annexes:</div>

				<div class="col-md-2">
					<img src="${baseUrlMedia}/images/global/participants.png" class="capDevIconConvention" />
					<div>Lista of participants</div>
					
				</div>

				<div class="col-md-2">
					<img src="${baseUrlMedia}/images/global/deliverable.png" class="capDevIconConvention" />
					<div>Supporting documents</div>
				</div>
				
			</div>
		</div>
	 </div>




	 






[#include "/WEB-INF/center/global/pages/footer.ftl"]