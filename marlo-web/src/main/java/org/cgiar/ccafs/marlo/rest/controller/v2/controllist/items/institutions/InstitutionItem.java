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
import org.cgiar.ccafs.marlo.rest.dto.InstitutionRequestDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewInstitutionDTO;
import org.cgiar.ccafs.marlo.rest.mappers.InstitutionMapper;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import com.ibm.icu.text.MessageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Configuration
@PropertySource("classpath:global.properties")
@Named
public class InstitutionItem<T> {

	private static final Logger LOG = LoggerFactory.getLogger(Institutions.class);
	@Autowired
	private Environment env;
	private final SendMailS sendMail;
	private InstitutionManager institutionManager;
	private LocElementManager locElementManager;
	private InstitutionMapper institutionMapper;
	private PartnerRequestManager partnerRequestManager;
	private GlobalUnitManager globalUnitManager;
	private boolean messageSent;
	protected APConfig config;

	@Inject
	public InstitutionItem(InstitutionManager institutionManager, InstitutionMapper institutionMapper,
			LocElementManager locElementManager, PartnerRequestManager partnerRequestManager,
			GlobalUnitManager globalUnitManager, SendMailS sendMail, APConfig config) {
		this.institutionManager = institutionManager;
		this.institutionMapper = institutionMapper;
		this.locElementManager = locElementManager;
		this.partnerRequestManager = partnerRequestManager;
		this.globalUnitManager = globalUnitManager;
		this.sendMail = sendMail;
		this.config = config;

	}

	public ResponseEntity<InstitutionRequestDTO> createPartnerRequest(NewInstitutionDTO newInstitutionDTO,
			String entityAcronym, User user) {

		GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);

		if (globalUnitEntity == null) {
			return new ResponseEntity<InstitutionRequestDTO>(HttpStatus.BAD_REQUEST);
		}
		// CountryDTO countryDTO = institutionDTO.getCountryDTO().get(0);

		LocElement locElement = this.locElementManager
				.getLocElementByNumericISOCode(newInstitutionDTO.getCountryDTO().get(0).getCode());
		if (locElement == null) {
			return new ResponseEntity<InstitutionRequestDTO>(HttpStatus.BAD_REQUEST);
		}

		PartnerRequest partnerRequestParent = this.institutionMapper.institutionDTOToPartnerRequest(newInstitutionDTO,
				globalUnitEntity, locElement, user);

		partnerRequestParent = this.partnerRequestManager.savePartnerRequest(partnerRequestParent);

		/**
		 * Need to create a parent child relationship for the partnerRequest to
		 * display. That design might need to be re-visited.
		 */
		PartnerRequest partnerRequestChild = this.institutionMapper.institutionDTOToPartnerRequest(newInstitutionDTO,
				globalUnitEntity, locElement, user);

		partnerRequestChild.setPartnerRequest(partnerRequestParent);

		partnerRequestChild = this.partnerRequestManager.savePartnerRequest(partnerRequestChild);

		// SEND THE MAIL
		this.sendPartnerRequestEmail(partnerRequestChild, user, entityAcronym);

		return new ResponseEntity<InstitutionRequestDTO>(
				this.institutionMapper.partnerRequestToPartnerRequestDTO(partnerRequestChild), HttpStatus.CREATED);

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
	public ResponseEntity<InstitutionRequestDTO> getPartnerRequest(Long id, String entityAcronym) {
		PartnerRequest partnerRequest = this.partnerRequestManager.getPartnerRequestById(id);
		if (partnerRequest != null && partnerRequest.getPartnerRequest() == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return Optional.ofNullable(partnerRequest).map(this.institutionMapper::partnerRequestToPartnerRequestDTO)
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

	}

	String getText(String property, String[] params) {
		String value = this.env.getProperty(property);
		MessageFormat message = new MessageFormat(value);
		return message.format(params);

	}

	private void sendPartnerRequestEmail(PartnerRequest partnerRequest, User user, String entityAcronym) {
		String institutionName, institutionAcronym, institutionTypeName, countryName, partnerWebPage;
		institutionName = partnerRequest.getPartnerName();
		institutionAcronym = partnerRequest.getAcronym();
		institutionTypeName = partnerRequest.getInstitutionType().getName();
		countryName = partnerRequest.getLocElement().getName();
		partnerWebPage = partnerRequest.getWebPage();
		String subject;
		StringBuilder message = new StringBuilder();

		// message subject

		subject = this.getText("marloRequestInstitution.email.subject",
				new String[] { entityAcronym.toUpperCase(), institutionName });

		// Message content
		message.append(user.getFirstName() + " " + user.getLastName() + " ");
		message.append("(" + user.getEmail() + ") ");
		message.append("is requesting to add the following partner information:");
		message.append("</br></br>");
		message.append("Partner Name: ");
		message.append(institutionName);
		message.append("</br>");
		message.append("Acronym: ");
		message.append(institutionAcronym);
		message.append(" </br>");
		message.append("Partner type: ");
		message.append(institutionTypeName);
		message.append(" </br>");

		message.append("Headquarter country location: ");
		message.append(countryName);
		message.append(" </br>");

		// Is there a web page?
		if (partnerWebPage != null && !partnerWebPage.isEmpty()) {
			message.append("Web Page: ");
			message.append(partnerWebPage);
			message.append(" </br>");
		}
		message.append(" </br>");
		message.append(".</br>");
		message.append("</br>");
		try {
			this.sendMail.send(this.config.getEmailNotification(), null, this.config.getEmailNotification(), subject,
					message.toString(), null, null, null, true);
		} catch (Exception e) {
			LOG.error("unable to send mail", e);
			this.messageSent = false;
			/**
			 * Original code swallows the exception and didn't even log it. Now
			 * we at least log it, but we need to revisit to see if we should
			 * continue processing or re-throw the exception.
			 */
		}
		this.messageSent = true;

	}

}
