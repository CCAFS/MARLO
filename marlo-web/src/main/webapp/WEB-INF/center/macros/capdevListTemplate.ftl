[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]
[#macro capdevList capdevs={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="description"]
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
		  	[#if capdevs?has_content]
			  	[#list capdevs as i]
				  	[#local capdevUrl][@s.url namespace=namespace action=defaultAction][@s.param name='capdevID']${i.id?c}[/@s.param][@s.param name='projectID']${projectID?c}[/@s.param][@s.param name='edit' value="true" /][/@s.url][/#local]
				  	[#if i.active]


					  <tr>
					    <td><a href="${capdevUrl}">C${i.id}</a></td>
					     
					    <td>
						    [#if i.title?has_content]
						    	<a href="${capdevUrl}">${i.title}</a>
						    [#else]
						    	<a href="${capdevUrl}">Not defined</a>
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
					    		
				    			<img src="${baseUrl}/center/images/capdev/participants.png" class="[#if i.capdevParticipant?has_content && i.capdevParticipant?size  > 1]capDevIconEnable [#else]capDevIconDisable[/#if]" title="List of participants" />
					    		
						    	
						    	<img src="${baseUrl}/center/images/capdev/deliverable.png" class="[#if i.deliverables?has_content] capDevIconEnable [#else]capDevIconDisable[/#if]" title="Supporting documents" />
					    	</div>

				    		
					    </td>
					    <td class="removeCol">
					    	[#if action.centerCanBeDeleted(i.id, i.class.name)!false]
					    	<a id="removeCapdev-${i.id}" class="removeCapdev" href="#" data-href="[@s.url namespace='/capdev' action='${centerSession}/deleteCapdev'][@s.param name='projectID']${projectID}[/@s.param][@s.param name='capdevID']${i.id}[/@s.param] [/@s.url]" data-toggle="modal" data-target="#confirm-delete-capdev">
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
		          	<a class="addButton" href="[@s.url namespace='/capdev' action='${centerSession}/addCapdev'][@s.param name='projectID']${projectID}[/@s.param][@s.param name='capdevID']${capdevID}[/@s.param][@s.param name='capdevCategory']2[/@s.param] [@s.param name='edit' value="true" /][/@s.url]" title="Add a capacity development intervention destinated  at a group of people ">[@s.text name="capdev.addgroupItem" /]</a>
	        	</div>
	        	<div class="buttons-content addindividualItem" >        
		          	<a class="addButton" href="[@s.url namespace='/capdev' action='${centerSession}/addCapdev' ][@s.param name='projectID']${projectID}[/@s.param][@s.param name='capdevID']${capdevID}[/@s.param][@s.param name='capdevCategory']1[/@s.param][@s.param name='edit' value="true" /][/@s.url]" title="Add a capacity development intervention destinated  at one person">[@s.text name="capdev.addindividualItem" /]</a>
	        	</div>
			</div>
		</div>

		<div class="row conventionContainer">
			<div class="col-md-12 itemName">
				<div class="col-md-1"> Annexes:</div>

				<div class="col-md-2">
					<img src="${baseUrl}/center/images/capdev/participants.png" class="capDevIconConvention" />
					<div>Lista of participants</div>
				</div>

				<div class="col-md-2">
					<img src="${baseUrl}/center/images/capdev/deliverable.png" class="capDevIconConvention" />
					<div>Supporting documents</div>
				</div>

				<div class="col-md-7">
					If an icon is highlighted, it means that the capacity development intervention has any of those: list of participants or supporting documents, or has both.
				</div>
				
			</div>
		</div>


[/#macro]