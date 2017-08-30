[#ftl]

[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]

[#assign customCSS = ["${baseUrlMedia}/css/global/customDataTable.css",
					  "${baseUrlMedia}/css/capDev/capacityDevelopment.css"] /]

[#assign customJS = ["${baseUrlMedia}/js/capDev/capacityDevelopment.js", 
					 "${baseUrlMedia}/js/capDev/supportingDocuments.js"] /]


[#assign currentStage = "supportingDocuments" /]

[#assign breadCrumb = [
  {"label":"capdevList", "nameSpace":"/capdev", "action":"${(centerSession)!}/capdev"},
  {"label":"capdevSupportingDocs", "nameSpace":"/capdev", "action":""}
]/]

[#include "/WEB-INF/center/global/pages/header.ftl" /]
[#include "/WEB-INF/center/global/pages/main-menu.ftl" /]





<script src="${baseUrl}/bower_components/jquery/dist/jquery.min.js"></script>
<!-- <script src="${baseUrlMedia}/js/capDev/capacityDevelopment.js"></script>
<script src="${baseUrlMedia}/js/capDev/supportingDocuments.js"></script> -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.7.7/xlsx.core.min.js"></script>



<div class="container"> 

	<div class="row">
		<div class="col-md-12 capdevinfo">
			help text
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
		
		<div class="col-md-12">
				Supporting Documents		
		</div>
	
		<div class="col-md-12 form-group "> 

			[@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
		

			<div  class="fullForm borderBox" >
				
				<!-- supporting documents -->
				
				
					

					
					[#if capdev.capdevSupportingDocses?has_content]
						<table class="supportDocsList" id="supportingDocs">
							<thead>
								<tr class="header">
									<th>ID</th>
									<th>Title</th>
									<th>Type</th>
									<th>Publication date</th>
									<th>Remove</th>
								</tr>
							</thead>
							<tbody>
								[#list capdev.capdevSupportingDocses as supportDocs]
									[#if supportDocs.active]
										<tr>
											<td>
												<a href="[@s.url action='${centerSession}/detailSupportingDoc'][@s.param name='capdevID']${supportDocs.capacityDevelopment.id?c}[/@s.param][@s.param name='supportingDocID']${supportDocs.id?c}[/@s.param][/@s.url]">S${supportDocs.id} </a>
											</td>
											<td>
											    [#if supportDocs.title?has_content]
											    	<a href="[@s.url action='${centerSession}/detailSupportingDoc'][@s.param name='capdevID']${supportDocs.capacityDevelopment.id?c}[/@s.param][@s.param name='supportingDocID']${supportDocs.id?c}[/@s.param][/@s.url]">${supportDocs.title}</a>
											    [#else]
											    	<a href="[@s.url action='${centerSession}/detailSupportingDoc'][@s.param name='capdevID']${supportDocs.capacityDevelopment.id?c}[/@s.param][@s.param name='supportingDocID']${supportDocs.id?c}[/@s.param][/@s.url]">Not defined</a>
											    [/#if]
										    </td>
										    <td>
											    [#if supportDocs.centerDeliverableTypes??]
											    	${supportDocs.centerDeliverableTypes.name}
											    [#else]
											    	Not defined
											    [/#if]
										    </td>
										    <td>
											    [#if supportDocs.publicationDate??]
											    	${supportDocs.publicationDate}
											    [#else]
											    	Not defined
											    [/#if]
										    </td>
											<td class="removeCol">
									             <a  class="deleteDoc" href="#" data-href="[@s.url action='${centerSession}/deleteSupportingDoc'][@s.param name='capdevID']${capdev.id}[/@s.param] [@s.param name='supportingDocID']${supportDocs.id?c}[/@s.param] [/@s.url]" data-toggle="modal" data-target="#confirm-delete">
									               <img src="${baseUrlMedia}/images/global/trash.png" title="[@s.text name="capdev.removeCapdev" /]" /> 
									             </a>
								            </td>


								            
										</tr>
									[/#if]

								[/#list]

								
							</tbody>
						</table>
					[/#if]
						<p class="text-center inf" style="display:${(capdev.capdevSupportingDocses?has_content)?string('none','block')}">[@s.text name="capdev.notSupportDocuments" /]</p>	

					

					<div class="col-md-12 newCapdevField">
						<div class="pull-right">
							<div class="buttons-content">        
								<a class="addButton" href="[@s.url action='${centerSession}/addSupportingDoc'][@s.param name='supportingDocID']${supportingDocID}[/@s.param] [@s.param name='capdevID']${capdevID}[/@s.param][/@s.url]">[@s.text name="capdev.addSupportingDoc" /]</a>
								<div class="clearfix"></div>
							</div>
						</div>
					</div>
				</div>


			<div class="modal fade" id="confirm-delete" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
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
				

				




				

			</div>

		</div>
	
		[/@s.form]

	</div>
	

</div>


[#include "/WEB-INF/center/global/pages/footer.ftl"]














