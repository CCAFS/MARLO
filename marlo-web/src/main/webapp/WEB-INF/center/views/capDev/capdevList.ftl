[#ftl]

[#assign customCSS = ["${baseUrlMedia}/css/global/customDataTable.css"] /]
[#assign customCSS = ["${baseUrlMedia}/css/capDev/capacityDevelopment.css"] /]
[#assign customJS = [
	"${baseUrlMedia}/js/capDev/capacityDevelopment.js",
 	"${baseUrlMedia}/js/capDev/capdevList.js"] 
/]


[#assign breadCrumb = [
  {"label":"capdevList", "nameSpace":"/capdev", "action":"${(centerSession)!}/capdev"}
]/]


[#include "/WEB-INF/center/global/pages/header.ftl" /]
[#include "/WEB-INF/center/global/pages/main-menu.ftl" /]


<script src="${baseUrl}/bower_components/jquery/dist/jquery.min.js"></script>
<script src="${baseUrlMedia}/js/capDev/capacityDevelopment.js"></script>
<script src="${baseUrlMedia}/js/capDev/capdevList.js"></script>


	<div class="container">
		<div class="row titleContainer">
			<div class="col-md-12">
				<p>Capacity Development Tracking Tool</p>
			</div>
			
		
		</div>

		<div class="row">
			<div class="helpMessage infoText col-md-12 capdevinfo">
				<img class="col-md-2" src="${baseUrlMedia}/images/global/icon-help.png" />
   				 <p class="col-md-10"> [@s.text name="capdev.help.list"][/@s.text] </p>
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
					    <th style="width: 7%">Remove</th>
					  </tr>
				  	</thead>
				  	<tbody id="capdevTbody">
					  [#if capDevs?has_content]
					  [#list capDevs as i]
					  [#if i.active]


					  <tr >
					    <td><a href="[@s.url action='${centerSession}/detailCapdev'][@s.param name='capdevID']${i.id?c}[/@s.param][/@s.url]">C${i.id}</a></td>
					     
					    <td>
						    [#if i.title?has_content]
						    	<a href="[@s.url action='${centerSession}/detailCapdev'][@s.param name='capdevID']${i.id?c}[/@s.param][/@s.url]">${i.title}</a>
						    [#else]
						    	<a href="[@s.url action='${centerSession}/detailCapdev'][@s.param name='capdevID']${i.id?c}[/@s.param][/@s.url]">Not defined</a>
						    [/#if]
					    </td> 
					    <td>
						    [#if i.capdevType??]
						    	${i.capdevType.name}
						    [#else]
						    	Not defined
						    [/#if]
					    </td>
					     <td>
						    [#if i.startDate??]
						    	${i.startDate}
						    [#else]
						    	Not defined
						    [/#if]
					    </td>
					    
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
					    <td>
					    	<a id="removeCapdev-${i.id}" class="removeCapdev" href="#" data-href="[@s.url action='${centerSession}/deleteCapdev'][@s.param name='capdevID']${i.id}[/@s.param] [/@s.url]" data-toggle="modal" data-target="#confirm-delete-capdev">
				               <img src="${baseUrlMedia}/images/global/trash.png" title="[@s.text name="capdev.removeCapdev" /]" /> 
				             </a>
					    </td>

					     
					  </tr>
					   <div class="modal fade" id="confirm-delete-capdev" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
					        <div class="modal-dialog">
					            <div class="modal-content">
					            
					                <div class="modal-header">
					                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					                    <h4 class="modal-title" id="myModalLabel">Confirm Delete</h4>
					                </div>
					            
					                <div class="modal-body">
					                    <p>You are about to delete one track, this procedure is irreversible.</p>
					                    <p>Do you want to proceed?</p>
					                    <p class="debug-url"></p>
					                </div>
					                
					                <div class="modal-footer">
					                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					                    <a class="btn btn-danger btn-ok">Delete</a>
					                </div>
					            </div>
					        </div>
					    </div>
					  [/#if]
					 
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