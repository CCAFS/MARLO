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
import org.cgiar.ccafs.marlo.rest.controller.controllist.items.srflist.SrfSubIdoItem;
import org.cgiar.ccafs.marlo.rest.dto.SrfCrossCuttingIssueDTO;
import org.cgiar.ccafs.marlo.rest.dto.SrfIdoDTO;
import org.cgiar.ccafs.marlo.rest.dto.SrfSloDTO;
import org.cgiar.ccafs.marlo.rest.dto.SrfSubIdoDTO;
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
@Api(tags = "SRF Lists")
@Named
public class SrfLists {

  private static final Logger LOG = LoggerFactory.getLogger(SrfLists.class);

  private SrfSloItem<SrfLists> srfSloItem;
  private SrfIdoItem<SrfLists> srfIdoItem;
  private SrfCrossCuttingIssueItem<SrfLists> srfCrossCuttingIssueItem;
  private SrfSubIdoItem<SrfLists> srfSubIdoItem;

  @Inject
  public SrfLists(SrfSloItem<SrfLists> srfSloItem, SrfIdoItem<SrfLists> srfIdoItem,
    SrfCrossCuttingIssueItem<SrfLists> srfCrossCuttingIssueItem, SrfSubIdoItem<SrfLists> srfSubIdoItem) {
    this.srfSloItem = srfSloItem;
    this.srfIdoItem = srfIdoItem;
    this.srfCrossCuttingIssueItem = srfCrossCuttingIssueItem;
    this.srfSubIdoItem = srfSubIdoItem;
  }

  /**
   * Find a SRF-SubIdo requesting by Id
   * 
   * @param id
   * @return a SrfSubIdoDTO with the SRF-SubIdo data.
   */
  @ApiOperation(value = "Search an SRF-SubIdo with an ID", response = SrfSubIdoDTO.class)
  @RequiresPermissions(Permission.CRP_PROGRAM_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/srfSubIdos/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SrfSubIdoDTO> findSrfSubIdoById(@PathVariable Long id) {
    LOG.debug("REST request to get SRF-SubIdo : {}", id);
    return srfSubIdoItem.findSrfSubIdoById(id);
  }

  /**
   * Get All the SRF IDO items
   * 
   * @return a List of SrfIdoDTO with all SRF IDO Items.
   */
  @ApiOperation(value = "View all Srf IDO", response = SrfIdoDTO.class, responseContainer = "List", position = 1)
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
  @ApiOperation(value = "View all Srf-SLO", response = SrfSloDTO.class, responseContainer = "List", position = 2)
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
  @ApiOperation(value = "View all SRF Cross Cutting Issue", response = SrfCrossCuttingIssueDTO.class,
    responseContainer = "List")
  @RequiresPermissions(Permission.CRP_PROGRAM_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/srfCrossCuttingIssues", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<SrfCrossCuttingIssueDTO> getAllSrfSrfCrossCuttingIssues() {
    LOG.debug("REST request to get SRF Cross Cutting Issues");
    return srfCrossCuttingIssueItem.getAllSrfCrossCuttingIssues();
  }


  /**
   * Get All the SRF-SubIdo items
   * 
   * @return a List of SrfSubIdoDTO with all SRF-SubIdo Items.
   */
  @ApiOperation(value = "View all SRF-SubIdo", response = SrfSubIdoDTO.class, responseContainer = "List", position = 2)
  @RequiresPermissions(Permission.CRP_PROGRAM_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/srfSubIdos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<SrfSubIdoDTO> getAllSrfSubIdos() {
    LOG.debug("REST request to get SRF-SubIdos");
    return srfSubIdoItem.getAllSrfSubIdos();
  }

  /**
   * Find a SRF IDO requesting by Id
   * 
   * @param id
   * @return a SrfIdoDTO with the SRL IDO data.
   */
  @ApiOperation(value = "Search an SRF IDO with an ID", response = SrfIdoDTO.class)
  @RequiresPermissions(Permission.CRP_PROGRAM_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/srfIdo/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SrfIdoDTO> getSrfIdoById(@PathVariable Long id) {
    LOG.debug("REST request to get SRF-SLO : {}", id);
    return srfIdoItem.findSrfIdobyId(id);
  }

  /**
   * Find a SRF-SLO requesting by Id
   * 
   * @param id
   * @return a SrfSloDTO with the SRL-SLO data.
   */
  @ApiOperation(value = "Search an SRF-SLO with an ID", response = SrfSloDTO.class)
  @RequiresPermissions(Permission.CRP_PROGRAM_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/srfSlo/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SrfSloDTO> getSrfSloById(@PathVariable Long id) {
    LOG.debug("REST request to get SRF-SLO : {}", id);
    return srfSloItem.findSrfSlobyId(id);
  }

  /**
   * Find a SRF Cross Cutting Issue requesting by Id
   * 
   * @param id
   * @return a SrfCrossCuttingIssueDTO with the SRF Cross Cutting Issue data.
   */
  @ApiOperation(value = "Search an SRF Cross Cutting Issue with an ID", response = SrfCrossCuttingIssueDTO.class)
  @RequiresPermissions(Permission.CRP_PROGRAM_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/srfCrossCuttingIssue/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SrfCrossCuttingIssueDTO> getSrfSrfCrossCuttingIssueById(@PathVariable Long id) {
    LOG.debug("REST request to get SRF Cross Cutting Issue : {}", id);
    return srfCrossCuttingIssueItem.findSrfCrossCuttingIssuebyId(id);
  }


}
