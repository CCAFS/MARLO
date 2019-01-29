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
import org.cgiar.ccafs.marlo.rest.dto.InstitutionDTO;
import org.cgiar.ccafs.marlo.rest.dto.PartnerRequestDTO;
import org.cgiar.ccafs.marlo.rest.mappers.InstitutionMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class InstitutionItem<T> {

	private InstitutionManager institutionManager;
	private LocElementManager locElementManager;
	private InstitutionMapper institutionMapper;
	private PartnerRequestManager partnerRequestManager;
	private GlobalUnitManager globalUnitManager;

	@Inject
	public InstitutionItem(InstitutionManager institutionManager, InstitutionMapper institutionMapper,
			LocElementManager locElementManager, PartnerRequestManager partnerRequestManager,
			GlobalUnitManager globalUnitManager) {
		this.institutionManager = institutionManager;
		this.institutionMapper = institutionMapper;
		this.locElementManager = locElementManager;
		this.partnerRequestManager = partnerRequestManager;
		this.globalUnitManager = globalUnitManager;
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
//
//		// Return an institutionDTO with a blank id - so that the user doesn't
//		// try and look up the institution straight
//		// away.
//		return new ResponseEntity<InstitutionDTO>(
//				this.institutionMapper.partnerRequestToInstitutionDTO(partnerRequestParent), HttpStatus.CREATED);

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

}
