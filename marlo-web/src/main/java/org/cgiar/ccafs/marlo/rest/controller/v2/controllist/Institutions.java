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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist;

import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.generallists.GlobalUnitItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.institutions.InstitutionItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.institutions.InstitutionTypeItem;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionDTO;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.PartnerRequestDTO;
import org.cgiar.ccafs.marlo.security.Permission;

import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "Institutions Lists")
public class Institutions {

	private static final Logger LOG = LoggerFactory.getLogger(Institutions.class);

	private InstitutionTypeItem<InstitutionTypeDTO> institutionTypeItem;
	private InstitutionItem<InstitutionDTO> institutionItem;
	private final UserManager userManager;

	@Inject
	public Institutions(InstitutionTypeItem<InstitutionTypeDTO> institutionTypeItem,
			InstitutionItem<InstitutionDTO> institutionItem, GlobalUnitItem<InstitutionDTO> globalUnitItem,
			UserManager userManager) {
		this.institutionTypeItem = institutionTypeItem;
		this.institutionItem = institutionItem;
		this.userManager = userManager;
	}

	@ApiOperation(value = "Create a partner request by id", response = PartnerRequestDTO.class)
	@RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
	@RequestMapping(value = "/institutions/{entityAcronym}/partner-requests", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PartnerRequestDTO> createPartnerRequest(@PathVariable String entityAcronym,
			@Valid @RequestBody InstitutionDTO institutionDTO) {
		LOG.debug("Create a new institution (Partner Request) with : {}", institutionDTO);
		return this.institutionItem.createPartnerRequest(institutionDTO, entityAcronym, this.getCurrentUser());
	}

	@ApiOperation(value = "View a list of institutions", response = InstitutionDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/institutions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<InstitutionDTO> getAllInstitutions() {
		LOG.debug("REST request to get Institutions");
		return this.institutionItem.getAllInstitutions();
	}

	@ApiOperation(value = "View a list of institution types", response = InstitutionTypeDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/institution-types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<InstitutionTypeDTO> getAllInstitutionsTypes() {
		LOG.debug("REST request to get Institution Types");
		return this.institutionTypeItem.getAllInstitutionTypes();
	}

	private User getCurrentUser() {
		Subject subject = SecurityUtils.getSubject();
		Long principal = (Long) subject.getPrincipal();
		User user = this.userManager.getUser(principal);
		return user;
	}

	@ApiOperation(value = "Search an institution with an ID", response = InstitutionDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/institution/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<InstitutionDTO> getInstitution(@PathVariable Long id) {
		LOG.debug("REST request to get Institution : {}", id);
		return this.institutionItem.findInstitutionById(id);
	}

	@ApiOperation(value = "Search a partner request by id", response = PartnerRequestDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/institutions/{entityAcronym}/partner-requests/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PartnerRequestDTO> getPartnerRequestById(@PathVariable String entityAcronym,
			@PathVariable Long id) {
		LOG.debug("Get a partner request with : {}", id);
		return this.institutionItem.getPartnerRequest(id, entityAcronym);
	}
}
