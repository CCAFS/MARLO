/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.institutions;

import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerRequestManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.Institutions;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionDTO;
import org.cgiar.ccafs.marlo.rest.dto.PartnerRequestDTO;
import org.cgiar.ccafs.marlo.rest.mappers.InstitutionMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
@PropertySource("classpath:clarisa.properties")
public class InstitutionItem<T> {

	private static final Logger LOG = LoggerFactory.getLogger(Institutions.class);

	private InstitutionManager institutionManager;
	private LocElementManager locElementManager;
	private InstitutionMapper institutionMapper;
	private PartnerRequestManager partnerRequestManager;
	private GlobalUnitManager globalUnitManager;

	@Value("${table1.tag}")
	private String table1;

	@Inject
	public InstitutionItem(InstitutionManager institutionManager, InstitutionMapper institutionMapper,
			LocElementManager locElementManager, PartnerRequestManager partnerRequestManager,
			GlobalUnitManager globalUnitManager) {
		this.institutionManager = institutionManager;
		this.institutionMapper = institutionMapper;
		this.locElementManager = locElementManager;
		this.partnerRequestManager = partnerRequestManager;
		this.globalUnitManager = globalUnitManager;
		try {
			ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "spring-bean.xml" });
		} catch (Throwable e) {
			System.out.println(e);
		}
	}

	public ResponseEntity<PartnerRequestDTO> createPartnerRequest(InstitutionDTO institutionDTO, String entityAcronym,
			User user) {

		GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);

		// CountryDTO countryDTO = institutionDTO.getCountryDTO().get(0);
		LocElement locElement = this.locElementManager
				.getLocElementByNumericISOCode(institutionDTO.getCountryDTO().get(0).getCode());
		PartnerRequest partnerRequestParent = this.institutionMapper.institutionDTOToPartnerRequest(institutionDTO,
				globalUnitEntity, locElement, user);

		partnerRequestParent = this.partnerRequestManager.savePartnerRequest(partnerRequestParent);

		/**
		 * Need to create a parent child relationship for the partnerRequest to
		 * display. That design might need to be re-visited.
		 */
		PartnerRequest partnerRequestChild = this.institutionMapper.institutionDTOToPartnerRequest(institutionDTO,
				globalUnitEntity, locElement, user);

		partnerRequestChild.setPartnerRequest(partnerRequestParent);

		partnerRequestChild = this.partnerRequestManager.savePartnerRequest(partnerRequestChild);

		return new ResponseEntity<PartnerRequestDTO>(
				this.institutionMapper.partnerRequestToPartnerRequestDTO(partnerRequestChild), HttpStatus.CREATED);

		// TODO: SEND THE MAIL

	}

	/**
	 * Find a institution requesting a MARLO id
	 * 
	 * 
	 * @param id
	 * @return a InstitutionDTO with the Institution Type data.
	 */
	public ResponseEntity<InstitutionDTO> findInstitutionById(Long id) {
		Institution institution = this.institutionManager.getInstitutionById(id);
		LOG.debug("Titulo de la tabla1 : {}", this.table1);

		return Optional.ofNullable(institution).map(this.institutionMapper::institutionToInstitutionDTO)
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * Get All the institution *
	 * 
	 * @return a List of institutions.
	 */
	public List<InstitutionDTO> getAllInstitutions() {
		List<Institution> institutions = this.institutionManager.findAll();
		List<InstitutionDTO> institutionDTOs = institutions.stream()
				.map(institution -> this.institutionMapper.institutionToInstitutionDTO(institution))
				.collect(Collectors.toList());
		return institutionDTOs;
	}

	/**
	 * Get a partner request by an id *
	 * 
	 * @return PartnerRequestDTO founded
	 */
	public ResponseEntity<PartnerRequestDTO> getPartnerRequest(Long id, String entityAcronym) {
		PartnerRequest partnerRequest = this.partnerRequestManager.getPartnerRequestById(id);
		if (partnerRequest != null && partnerRequest.getPartnerRequest() == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return Optional.ofNullable(partnerRequest).map(this.institutionMapper::partnerRequestToPartnerRequestDTO)
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

	}

//	private Boolean sendPartnerRequestEmail(PartnerRequest partnerRequest) {
//		String institutionName, institutionAcronym, institutionTypeName, countryId, countryName, partnerWebPage;
//		String subject;
//		StringBuilder message = new StringBuilder();
//
//		// message subject
//
//		subject = this.getText("marloRequestInstitution.email.subject",
//				new String[] { this.getCrpSession().toUpperCase(), institutionName });
//		// Message content
//		message.append(this.getCurrentUser().getFirstName() + " " + this.getCurrentUser().getLastName() + " ");
//		message.append("(" + this.getCurrentUser().getEmail() + ") ");
//		message.append("is requesting to add the following partner information:");
//		message.append("</br></br>");
//		message.append("Partner Name: ");
//		message.append(institutionName);
//		message.append("</br>");
//		message.append("Acronym: ");
//		message.append(institutionAcronym);
//		message.append(" </br>");
//		message.append("Partner type: ");
//		message.append(institutionTypeName);
//		message.append(" </br>");
//
//		message.append("Headquarter country location: ");
//		message.append(countryName);
//		message.append(" </br>");
//
//		// Is there a web page?
//		if (partnerWebPage != null && !partnerWebPage.isEmpty()) {
//			message.append("Web Page: ");
//			message.append(partnerWebPage);
//			message.append(" </br>");
//		}
//		message.append(" </br>");
//
//		switch (pageRequestName) {
//		case "projects":
//			this.addProjectMessage(message, partnerRequest, partnerRequestModifications);
//			break;
//
//		case "fundingSources":
//			this.addFunginMessage(message, partnerRequest, partnerRequestModifications);
//			break;
//
//		case "studies":
//			this.addStudyMessage(message, partnerRequest, partnerRequestModifications);
//			break;
//
//		case "capdev":
//			this.addCapDevMessage(message, partnerRequest, partnerRequestModifications);
//			break;
//
//		case "powbSynthesis":
//			this.addPowbSynthesisMessage(message, partnerRequest, partnerRequestModifications);
//			break;
//
//		}
//
//		partnerRequest = this.partnerRequestManager.savePartnerRequest(partnerRequest);
//		partnerRequestModifications.setPartnerRequest(partnerRequest);
//		partnerRequestModifications.setModified(false);
//		partnerRequestModifications = this.partnerRequestManager.savePartnerRequest(partnerRequestModifications);
//
//		message.append(".</br>");
//		message.append("</br>");
//		try {
//			sendMail.send(config.getEmailNotification(), null, config.getEmailNotification(), subject,
//					message.toString(), null, null, null, true);
//		} catch (Exception e) {
//			LOG.error("unable to send mail", e);
//			/**
//			 * Original code swallows the exception and didn't even log it. Now
//			 * we at least log it, but we need to revisit to see if we should
//			 * continue processing or re-throw the exception.
//			 */
//		}
//		messageSent = true;
//
//	}

}
