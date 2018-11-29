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

package org.cgiar.ccafs.marlo.rest.controller.controllist;

import org.cgiar.ccafs.marlo.rest.controller.controllist.items.powbar.CrossCuttingMarkersItem;
import org.cgiar.ccafs.marlo.rest.controller.controllist.items.powbar.InnovationTypesItem;
import org.cgiar.ccafs.marlo.rest.controller.controllist.items.powbar.ResearchPartnershipsItem;
import org.cgiar.ccafs.marlo.rest.dto.CrossCuttingMarkersDTO;
import org.cgiar.ccafs.marlo.rest.dto.InnovationTypesDTO;
import org.cgiar.ccafs.marlo.rest.dto.ResearchPartnershipsDTO;
import org.cgiar.ccafs.marlo.security.Permission;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "4. POWB/AR Lists")
@Named
public class PowbARLists {


  private static final Logger LOG = LoggerFactory.getLogger(PowbARLists.class);
  private CrossCuttingMarkersItem<PowbARLists> crossCuttingMarkersItem;
  private InnovationTypesItem<PowbARLists> innovationTypesItem;
  private ResearchPartnershipsItem<PowbARLists> researchPartnershipsItem;

  @Inject
  public PowbARLists(CrossCuttingMarkersItem<PowbARLists> crossCuttingMarkersItem,
    InnovationTypesItem<PowbARLists> innovationTypesItem,
    ResearchPartnershipsItem<PowbARLists> researchPartnershipsItem) {
    this.crossCuttingMarkersItem = crossCuttingMarkersItem;
    this.innovationTypesItem = innovationTypesItem;
    this.researchPartnershipsItem = researchPartnershipsItem;
  }

  /**
   * Get All the Cross Cutting Markers items
   * 
   * @return a List of CrossCuttingMarkersDTO with all Cross Cutting Markers Items.
   */
  @ApiOperation(value = "View all The Cross Cutting Markers", response = CrossCuttingMarkersDTO.class,
    responseContainer = "List", position = 1)
  @RequiresPermissions(Permission.CRP_PROGRAM_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/crossCuttingMarkers", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<CrossCuttingMarkersDTO> getAllCrossCuttingMarkers() {
    LOG.debug("REST request to get Cross Cutting Markers");
    return crossCuttingMarkersItem.getAllCrossCuttingMarkers();
  }

  /**
   * Get All the Innovation Types items
   * 
   * @return a List of InnovationTypesDTO with all Innovation Types Items.
   */
  @ApiOperation(value = "View all The Innovation Types", response = InnovationTypesDTO.class,
    responseContainer = "List", position = 1)
  @RequiresPermissions(Permission.CRP_PROGRAM_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/innovationTypes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<InnovationTypesDTO> getAllInnovationTypes() {
    LOG.debug("REST request to get Innovation Types");
    return innovationTypesItem.getAllInnovationTypes();
  }


  /**
   * Get All the Research Partnerships items
   * 
   * @return a List of ResearchPartnershipsDTO with all ResearchPartnerships Items.
   */
  @ApiOperation(value = "View all The Research Partnerships", response = ResearchPartnershipsDTO.class,
    responseContainer = "List", position = 1)
  @RequiresPermissions(Permission.CRP_PROGRAM_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/researchPartnerships", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ResearchPartnershipsDTO> getAllResearchPartnerships() {
    LOG.debug("REST request to get ResearchPartnerships");
    return researchPartnershipsItem.getAllResearchPartnerships();
  }


  /**
   * Find a Cross Cutting Marker requesting a MARLO id
   * 
   * @param id
   * @return a CrossCuttingMarkersDTO with the Cross Cutting Marker data.
   */
  @ApiOperation(value = "Search a Cross Cutting Marker with a MARLO ID", response = CrossCuttingMarkersDTO.class)
  @RequiresPermissions(Permission.CRP_PROGRAM_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/crossCuttingMarker/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CrossCuttingMarkersDTO> getCrossCuttingMarkerById(@PathVariable Long id) {
    LOG.debug("REST request to get Cross Cutting Marker : {}", id);
    return crossCuttingMarkersItem.findCrossCuttingMarkerById(id);
  }

  /**
   * Find a Innovation Type requesting a MARLO id
   * 
   * @param id
   * @return a InnovationTypesDTO with the Innovation Type data.
   */
  @ApiOperation(value = "Search a Innovation Type with a MARLO ID", response = InnovationTypesDTO.class)
  @RequiresPermissions(Permission.CRP_PROGRAM_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/innovationType/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InnovationTypesDTO> getInnovationTypeById(@PathVariable Long id) {
    LOG.debug("REST request to get Innovation Type : {}", id);
    return innovationTypesItem.findInnovationTypeById(id);
  }

  /**
   * Find a Research Partnership requesting a MARLO id
   * 
   * @param id
   * @return a ResearchPartnershipsDTO with the Research Partnership data.
   */
  @ApiOperation(value = "Search a Research Partnerships with a MARLO ID", response = ResearchPartnershipsDTO.class)
  @RequiresPermissions(Permission.CRP_PROGRAM_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/researchPartnership/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ResearchPartnershipsDTO> getResearchPartnershipById(@PathVariable Long id) {
    LOG.debug("REST request to get ResearchPartnership : {}", id);
    return researchPartnershipsItem.findResearchPartnershipById(id);
  }


}
