[#ftl]

[#assign customCSS = ["${baseUrl}/global/css/customDataTable.css"] /]
[#assign customCSS = ["${baseUrlMedia}/css/capDev/capacityDevelopment.css"] /]
[#assign customJS = ["${baseUrlMedia}/js/capDev/capacityDevelopment.js"] /]

[#include "/WEB-INF/center/pages/header.ftl" /]
[#include "/WEB-INF/center/pages/main-menu.ftl" /]

<div class="container">

	<div class="col-md-3">
		[#include "/WEB-INF/center/views/capDev/menu-capdev.ftl" /]
	</div>
	<div class="col-md-9">
		<div class="col-md-6">
			<div> New Deliverable</div>
		</div><div class="col-md-6">
			<div class="pull-right"> Back </div>
		</div>

		<div class="col-md-12 form-group newDeliverableForm">
			
			<div class="row newDeliverableField">
				<div class="col-md-12 "> 
					<div class=""> 
						[@customForm.input name="capdev.title" type="text" i18nkey="capdev.form.title"  required=true  /]
					</div>
				</div>
			</div>
			<div class="row newDeliverableField">
				<div class="col-md-6 ">
					[@customForm.input name="capdev.title" type="text" i18nkey="capdev.form.title"  required=true  /]
				</div>
				<div class="col-md-6 ">
					[@customForm.input name="capdev.title" type="text" i18nkey="capdev.form.title"  required=true  /]
				</div>
			</div>
			<div class="row newDeliverableField">
				<div class="col-md-12 ">
					Supporting document(s):
				</div>
				
			</div>
			<div class="row supporDocumentsContainer newDeliverableField">
				<div class="col-md-12 supportdocumentsList">
					
				</div>
				<p class="text-center inf" style="display:${(supportDocuments?has_content)?string('none','block')}">[@s.text name="capdev.supportDocuments" /]</p>
			</div>
			<div class="row newDeliverableField">
				<div class="col-md-12">
					<div class="pull-right">
						<button type="button" class="" aria-label="Left Align" >
		  					Add support document
						</button>
					</div>
				</div>
			</div>

			<div class="row newDeliverableField">
				<div class="col-md-6">
					<div class="pull-right">
						<button type="button" class="" aria-label="Left Align" >
		  					Save
						</button>
					</div>
				</div>
				<div class="col-md-6">
					<div class="pull-left">
						<button type="button" class="" aria-label="Left Align" >
		  					Cancel
						</button>
					</div>
				</div>
			</div>

		</div>
	</div>
		


</div>


[#include "/WEB-INF/center/pages/footer.ftl"]