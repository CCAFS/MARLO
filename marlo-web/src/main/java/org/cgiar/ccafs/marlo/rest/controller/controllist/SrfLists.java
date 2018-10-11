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

import org.cgiar.ccafs.marlo.rest.controller.controllist.items.srflist.SrfCrossCuttingIssueItem;
import org.cgiar.ccafs.marlo.rest.controller.controllist.items.srflist.SrfIdoItem;
import org.cgiar.ccafs.marlo.rest.controller.controllist.items.srflist.SrfSloItem;
import org.cgiar.ccafs.marlo.rest.dto.SrfCrossCuttingIssueDTO;
import org.cgiar.ccafs.marlo.rest.dto.SrfIdoDTO;
import org.cgiar.ccafs.marlo.rest.dto.SrfSloDTO;
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

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@RestController
@Api(value = "SrfLists", description = "List of the CGIAR's Strategy and Results Items")
@Named
public class SrfLists {

  private static final Logger LOG = LoggerFactory.getLogger(SrfLists.class);

  private SrfSloItem<SrfLists> srfSloItem;
  private SrfIdoItem<SrfLists> srfIdoItem;
  private SrfCrossCuttingIssueItem<SrfLists> srfCrossCuttingIssueItem;

  @Inject
  public SrfLists(SrfSloItem<SrfLists> srfSloItem, SrfIdoItem<SrfLists> srfIdoItem,
    SrfCrossCuttingIssueItem<SrfLists> srfCrossCuttingIssueItem) {
    this.srfSloItem = srfSloItem;
    this.srfIdoItem = srfIdoItem;
    this.srfCrossCuttingIssueItem = srfCrossCuttingIssueItem;
  }

  /**
   * Get All the SRF IDO items
   * 
   * @return a List of SrfIdoDTO with all SRF IDO Items.
   */
  @ApiOperation(value = "View all Srf IDO", response = Iterable.class)
  @RequiresPermissions(Permission.CRP_PROGRAM_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/srfIdos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<SrfIdoDTO> getAllSrfIdos() {
    LOG.debug("REST request to get SRF IDOs");
    return srfIdoItem.getAllSrfIdos();
  }

  /**
   * Get All the SRF-SLO items
   * 
   * @return a List of SrfSloDTO with all SRF-SLO Items.
   */
  @ApiOperation(value = "View all Srf-SLO", response = Iterable.class)
  @RequiresPermissions(Permission.CRP_PROGRAM_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/srfSlos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<SrfSloDTO> getAllSrfSlos() {
    LOG.debug("REST request to get SRF-SLOs");
    return srfSloItem.getAllSrfSlos();
  }

  /**
   * Get All the SRF Cross Cutting Issue items
   * 
   * @return a List of SrfCrossCuttingIssueDTO with all SRF Cross Cutting Issue Items.
   */
  @ApiOperation(value = "View all SRF Cross Cutting Issue", response = Iterable.class)
  @RequiresPermissions(Permission.CRP_PROGRAM_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/srfCrossCuttingIssues", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<SrfCrossCuttingIssueDTO> getAllSrfSrfCrossCuttingIssues() {
    LOG.debug("REST request to get SRF Cross Cutting Issues");
    return srfCrossCuttingIssueItem.getAllSrfCrossCuttingIssues();
  }

  /**
   * Find a SRF IDO requesting by MARLO Id
   * 
   * @param id
   * @return a SrfIdoDTO with the SRL IDO data.
   */
  @ApiOperation(value = "Search an SRF IDO with a MARLO ID", response = SrfIdoDTO.class)
  @RequiresPermissions(Permission.INSTITUTIONS_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/srfIdo/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SrfIdoDTO> getSrfIdoByMARLOId(@PathVariable Long id) {
    LOG.debug("REST request to get SRF-SLO : {}", id);
    return srfIdoItem.findSrfIdobyMARLOId(id);
  }

  /**
   * Find a SRF-SLO requesting by MARLO Id
   * 
   * @param id
   * @return a SrfSloDTO with the SRL-SLO data.
   */
  @ApiOperation(value = "Search an SRF-SLO with a MARLO ID", response = SrfSloDTO.class)
  @RequiresPermissions(Permission.INSTITUTIONS_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/srfSlo/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SrfSloDTO> getSrfSloByMARLOId(@PathVariable Long id) {
    LOG.debug("REST request to get SRF-SLO : {}", id);
    return srfSloItem.findSrfSlobyMARLOId(id);
  }


}
