[#ftl]

<div id="dialog-searchContactPerson" style="display:none">

	<form class="pure-form">
		<div class="dialog-content">
			<div class="col-md-12">
				<span class="glyphicon glyphicon-remove-circle close-dialog"></span>
				<br />
				<br />

			</div>	

			
			<div class="col-md-12 searchFields">
				<div class="searchcontact-content clearfix">
					<div class="col-md- searchcontact-input">
						[@customForm.input name="" showTitle=false type="text"  className='contact' placeholder="capdev.adUsersplaceHolder"/]
						<div class="col-md- search-loader" style="display:none;"><img src="${baseUrlMedia}/images/global/loading_2.gif"></div>
					</div>  
					<div class="col-md- search-button">[@s.text name="capdev.search" /]</div>
				</div>
				
			</div>
			

			<div class="col-md-12">
				<div class="contactList panel secondary">
					<div class="panel-head"> [@s.text name="capdev.adUsersList" /]</div>
					<div class="panel-body"> 
						<p class="userMessage" >
							If you do not find the person, please add it by clicking "Add contact person".
						</p>
						<ul></ul>
					</div>
				</div> 
			</div>

			<div class="panel-heading">
		      <h4 class="panel-title">
		        <a data-toggle="collapse" href="#collapse1">Add contact person</a>
		      </h4>
		    </div>	

			<div id="collapse1" class="panel-collapse collapse">
		      <div class="panel-body">
			      	<!-- <input class="ctFirsName" type="text" name="capdev.ctFirstName" value="${(capdev.ctFirstName)!}" /> 
					<input class="ctLastName" type="text" name="capdev.ctLastName" value="${(capdev.ctLastName)!}" /> 
					<input class="ctEmail" type="text" name="capdev.ctEmail" value="${(capdev.ctEmail)!}" />  -->
					[@customForm.input name="" i18nkey="capdev.form.contactPerson.firstName" type="text" className='ct_FirstName'  required=true  /]
					[@customForm.input name="" i18nkey="capdev.form.contactPerson.lastName" type="text" className='ct_LastName'  required=true  /]
					[@customForm.input name="" i18nkey="capdev.form.contactPerson.email" type="text" className='ct_Email'  required=true  /]

				<div class="col-md-12">
					<div class="pull-right">
						<button type="button" class="addContactPerson" aria-label="Left Align" >
							[@s.text name="form.buttons.add" /]
						</button>
					</div>
				</div>

				

			</div>

				

			</div>

		      </div>
		    </div>

		</div>

	</form>

	


	[#-- User Template --]
	<ul style="display:none"> 
		<li id="userTemplate">
			  
			<span class="idUser">{idUser}</span>
			<span class="contact firstname">{firstName}</span>  
			<span class="contact lastname">{lastName}</span>  
			<span class="contact email">  {email} </span>  
			<span class="listButton select selectADUser">[@s.text name="form.buttons.select" /]</span>
		</li> 
	</ul> 
	


</div>