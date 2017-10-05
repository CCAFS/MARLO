[#ftl]

[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customCSS = ["${baseUrl}/global/css/customDataTable.css", 
					  					"${baseUrlMedia}/css/capDev/capacityDevelopment.css"] /]


[#assign customJS = [
 	"${baseUrlMedia}/js/capDev/capdevList.js"] 
/]

[#assign currentSection = "capdev" /]


[#assign breadCrumb = [
  {"label":"capdevList", "nameSpace":"/capdev", "action":"${(centerSession)!}/capdev"}
]/]


[#include "/WEB-INF/center/pages/header.ftl" /]
[#include "/WEB-INF/center/pages/main-menu.ftl" /]


<!--<script src="${baseUrl}/bower_components/jquery/dist/jquery.min.js"></script>-->



	<div class="container">
		<div class="row titleContainer">
			<div class="col-md-12">
				<p>Capacity Development Tracking Tool</p>
			</div>
			
		
		</div>

		<div class="row">
			<div class="helpMessage infoText col-md-12 capdevinfo">
				<img class="col-md-2" src="${baseUrl}/global/images/icon-help.png" />
   				 <p class="col-md-10"> [@s.text name="capdev.help.list"][/@s.text] </p>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12 titleContainer">
				Latest CAPDEV Interventions
			</div>
		</div>
		
		
		
			
				<table class="capDevList" id="capdevs">
					<thead >
					  <tr class="header">
					    <th style="width: 3%">ID</th>
					    <th style="width: 21%">Title</th> 
					    <th style="width: 7%">Type</th>
					    <th style="width: 7%">Start date</th>
					    <th style="width: 7%">End date</th>
					    <th style="width: 7%">Research Area</th>
					    <th style="width: 7%">Research Program</th>
					    <th style="width: 7%">Annexes</th>
					    <th style="width: 1%">Remove</th>
					  </tr>
				  	</thead>
				  	<tbody >
					  [#if capDevs?has_content]
					  [#list capDevs as i]
					  [#if i.active]


					  <tr >
					    <td><a href="[@s.url action='${centerSession}/detailCapdev'][@s.param name='capdevID']${i.id?c}[/@s.param][@s.param name='edit' value="true" /][/@s.url]">C${i.id}</a></td>
					     
					    <td>
						    [#if i.title?has_content]
						    	<a href="[@s.url action='${centerSession}/detailCapdev'][@s.param name='capdevID']${i.id?c}[/@s.param][@s.param name='edit' value="true" /][/@s.url]">${i.title}</a>
						    [#else]
						    	<a href="[@s.url action='${centerSession}/detailCapdev'][@s.param name='capdevID']${i.id?c}[/@s.param][@s.param name='edit' value="true" /][/@s.url]">Not defined</a>
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
					    	<div class="icon">
					    		
				    			<img src="${baseUrlMedia}/images/global/participants.png" class="[#if i.capdevParticipant?has_content && i.capdevParticipant?size  > 1]capDevIconEnable [#else]capDevIconDisable[/#if]" title="List of participants" />
					    		
						    	
						    	<img src="${baseUrlMedia}/images/global/deliverable.png" class="[#if i.capdevSupportingDocs?has_content] capDevIconEnable [#else]capDevIconDisable[/#if]" title="Supporting documents" />
					    	</div>

				    		
					    </td>
					    <td class="removeCol">
					    	[#if action.centerCanBeDeleted(i.id, i.class.name)!false]
					    	<a id="removeCapdev-${i.id}" class="removeCapdev" href="#" data-href="[@s.url action='${centerSession}/deleteCapdev'][@s.param name='capdevID']${i.id}[/@s.param] [/@s.url]" data-toggle="modal" data-target="#confirm-delete-capdev">
				               <img src="${baseUrl}/global/images/trash.png" title="[@s.text name="capdev.removeCapdev" /]" /> 
				            </a>
				            [#else]
				            
				            	<img src="${baseUrl}/global/images/trash_disable.png" title="[@s.text name="capdev.removeCapdev" /]" /> 
				            [/#if]
					    	
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
			
		<div class="row ">
		

			<div class="addButtons botones">
				
	        	<div class="buttons-content addgroupItem" >        
		          	<a class="addButton" href="[@s.url action='${centerSession}/addCapdev'][@s.param name='capdevID']${capdevID}[/@s.param][@s.param name='capdevCategory']2[/@s.param] [@s.param name='edit' value="true" /][/@s.url]" title="Add a capacity development intervention destinated  at a group of people ">[@s.text name="capdev.addgroupItem" /]</a>
		          	
	        	</div>
	        	<div class="buttons-content addindividualItem" >        
		          	<a class="addButton" href="[@s.url action='${centerSession}/addCapdev' ][@s.param name='capdevID']${capdevID}[/@s.param][@s.param name='capdevCategory']1[/@s.param][@s.param name='edit' value="true" /][/@s.url]" title="Add a capacity development intervention destinated  at one person">[@s.text name="capdev.addindividualItem" /]</a>

	        	</div>
			</div>
	    		
		
		</div>

		<div class="row conventionContainer">
			<div class="col-md-12 itemName">
				<div class="col-md-1"> Annexes:</div>

				<div class="col-md-2">
					<img src="${baseUrlMedia}/images/global/participants.png" class="capDevIconConvention" />
					<div>Lista of participants</div>
					
				</div>

				<div class="col-md-2">
					<img src="${baseUrlMedia}/images/global/deliverable.png" class="capDevIconConvention" />
					<div>Supporting documents</div>
				</div>

				<div class="col-md-7">
					If any icon is highlighted it mean that the capacity development intervention has any of those: list of participants or supporting documents, or has both.
				</div>
				
			</div>
		</div>
	 </div>



[#include "/WEB-INF/center/pages/footer.ftl"]