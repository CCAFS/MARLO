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

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "Institutions Lists")
public class ImpactPathway {

//	private static final Logger LOG = LoggerFactory.getLogger(ImpactPathway.class);
//
//	private InstitutionTypeItem<InstitutionTypeDTO> institutionTypeItem;
//	private InstitutionItem<InstitutionDTO> institutionItem;
//	private final UserManager userManager;
//
//	@Inject
//	public ImpactPathway(InstitutionTypeItem<InstitutionTypeDTO> institutionTypeItem,
//			InstitutionItem<InstitutionDTO> institutionItem, GlobalUnitItem<InstitutionDTO> globalUnitItem,
//			UserManager userManager) {
//		this.institutionTypeItem = institutionTypeItem;
//		this.institutionItem = institutionItem;
//		this.userManager = userManager;
//	}
//
//	@ApiOperation(value = "Create a partner request by id", response = InstitutionRequestDTO.class)
//	@RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
//	@RequestMapping(value = "/institutions/{CGIAREntity}/institution-requests", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<InstitutionRequestDTO> createPartnerRequest(
//			@PathVariable(name = "CGIAR Entity Acronym") String CGIAREntity,
//			@Valid @RequestBody InstitutionDTO institutionDTO) {
//		LOG.debug("Create a new institution (Partner Request) with : {}", institutionDTO);
//		return this.institutionItem.createPartnerRequest(institutionDTO, CGIAREntity, this.getCurrentUser());
//	}
//
//	@ApiOperation(tags = { "Table 4 - CRP Innovations",
//			"Table 3 - Outcome/ Impact Case Reports" }, value = "View a list of all institutions", response = InstitutionDTO.class, responseContainer = "List")
//	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
//	@RequestMapping(value = "/institutions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	public List<InstitutionDTO> getAllInstitutions() {
//		LOG.debug("REST request to get Institutions");
//		return this.institutionItem.getAllInstitutions();
//	}
//
//	@ApiOperation(value = "View a list of institution types", response = InstitutionTypeDTO.class, responseContainer = "List")
//	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
//	@RequestMapping(value = "/institution-types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	public List<InstitutionTypeDTO> getAllInstitutionsTypes() {
//		LOG.debug("REST request to get Institution Types");
//		return this.institutionTypeItem.getAllInstitutionTypes();
//	}
//
//	private User getCurrentUser() {
//		Subject subject = SecurityUtils.getSubject();
//		Long principal = (Long) subject.getPrincipal();
//		User user = this.userManager.getUser(principal);
//		return user;
//	}
//
//	@ApiOperation(tags = { "Table 4 - CRP Innovations",
//			"Table 3 - Outcome/ Impact Case Reports" }, value = "Search an institution with an ID", response = InstitutionDTO.class)
//	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
//	@RequestMapping(value = "/institutions/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<InstitutionDTO> getInstitution(@PathVariable Long code) {
//		LOG.debug("REST request to get Institution : {}", code);
//		return this.institutionItem.findInstitutionById(code);
//	}
//
//	@ApiOperation(value = "Search an institution typet by id", response = InstitutionTypeDTO.class)
//	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
//	@RequestMapping(value = "/institution-types/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<InstitutionTypeDTO> getInstitutionTypeById(
//			@PathVariable(name = "institution type id") Long code) {
//		LOG.debug("Get a partner request with : {}", code);
//		return this.institutionTypeItem.findInstitutionTypeById(code);
//	}
//
//	@ApiOperation(value = "Search a partner request by id", response = InstitutionRequestDTO.class)
//	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
//	@RequestMapping(value = "/institutions/{CGIAREntity}/institution-requests/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<InstitutionRequestDTO> getPartnerRequestById(
//			@PathVariable(name = "CGIAR Entity Acronym") String entityAcronym,
//			@PathVariable(name = "institution request id") Long code) {
//		LOG.debug("Get a partner request with : {}", code);
//		return this.institutionItem.getPartnerRequest(code, entityAcronym);
//	}

}
