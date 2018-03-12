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

package org.cgiar.ccafs.marlo.rest;

import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.rest.institutions.InstitutionTypeMapper;
import org.cgiar.ccafs.marlo.rest.institutions.dto.InstitutionTypeDTO;
import org.cgiar.ccafs.marlo.security.Permission;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller publishes Refrence data for REST clients to consume. This is a read only service and it's purpose
 * is to provide lists of common types that MARLO uses. These types may be required in other RESTfull services.
 * For example the InstitutionType is required by the Institutions rest service. MARLO clients can use this service
 * to understand what data they need to enter for the InstitutionType.
 * 
 * @author GrantL
 */
@RestController
public class ReferenceDataController {

  private static final Logger LOG = LoggerFactory.getLogger(ReferenceDataController.class);

  private final InstitutionTypeManager institutionTypeManager;

  private final InstitutionTypeMapper institutionTypeMapper;

  @Inject
  public ReferenceDataController(InstitutionTypeManager institutionTypeManager,
    InstitutionTypeMapper institutionTypeMapper) {
    super();
    this.institutionTypeManager = institutionTypeManager;
    this.institutionTypeMapper = institutionTypeMapper;
  }

  @RequiresPermissions(Permission.INSTITUTIONS_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{globalUnit}/institutionTypes", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<InstitutionTypeDTO> getAllInstitutions(@PathVariable String globalUnit) {
    LOG.debug("REST request to get all Institution Types");
    List<InstitutionType> institutionTypes = institutionTypeManager.findAll();
    List<InstitutionTypeDTO> institutionTypeDTOs = institutionTypes.stream()
      .map(institutionType -> institutionTypeMapper.institutionTypeToInstitutionTypeDTO(institutionType))
      .collect(Collectors.toList());
    return institutionTypeDTOs;
  }

}
