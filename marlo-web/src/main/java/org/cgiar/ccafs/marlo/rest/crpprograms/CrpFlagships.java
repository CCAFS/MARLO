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

package org.cgiar.ccafs.marlo.rest.crpprograms;

import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.CrpProgramDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewFlagshipDTO;
import org.cgiar.ccafs.marlo.security.Permission;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@RestController
@Api(value = "FlagshipService", description = "Service pertaining to CRP Flagship programs.")
public class CrpFlagships {

  private static final Logger LOG = LoggerFactory.getLogger(CrpFlagships.class);


  private final CrpProgramMapper crpProgramMapper;

  private final CrpProgramManager crpProgramManager;

  private final GlobalUnitManager globalUnitManager;

  private UserManager userManager;

  @Inject
  public CrpFlagships(CrpProgramMapper crpProgramMapper, GlobalUnitManager globalUnitManager,
    CrpProgramManager crpProgramManager, UserManager userManager) {
    super();
    this.crpProgramMapper = crpProgramMapper;
    this.globalUnitManager = globalUnitManager;
    this.crpProgramManager = crpProgramManager;
    this.userManager = userManager;
  }

  /**
   * Create a CGIAR Structure Flagship
   * 
   * @param CGIARStructure
   * @param crpProgramDTO
   * @return
   */
  @ApiOperation(value = "Add a CRP Flagship program", response = CrpProgramDTO.class)
  @RequiresPermissions(Permission.CRP_PROGRAM_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIARStructure}/setFlagship", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CrpProgramDTO> createFlagship(@PathVariable String CGIARStructure,
    @Valid @RequestBody NewFlagshipDTO newFlagshipDTO) {
    LOG.debug("Create a new Crp Program (Flagship) with : {}", newFlagshipDTO);

    GlobalUnit globalUnitEntity = globalUnitManager.findGlobalUnitByAcronym(CGIARStructure);

    CrpProgram crpProgram = crpProgramMapper.newFlagshipDTOToCrpProgram(newFlagshipDTO);

    crpProgram.setActive(true);
    crpProgram.setCreatedBy(this.getCurrentUser());
    crpProgram.setModifiedBy(this.getCurrentUser());
    crpProgram.setActiveSince(new Date());
    crpProgram.setModificationJustification("");

    crpProgram.setProgramType(ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue());
    crpProgram.setCrp(globalUnitEntity);

    crpProgram = crpProgramManager.saveCrpProgram(crpProgram);


    // Return an institutionDTO with a blank id - so that the user doesn't try and look up the institution straight
    // away.
    return new ResponseEntity<CrpProgramDTO>(crpProgramMapper.crpProgramToCrpProgramDTO(crpProgram),
      HttpStatus.CREATED);
  }

  /**
   * Delete a CGIAR Structure Flagship
   * 
   * @param CGIARStructure
   * @param id
   * @return
   */
  @ApiOperation(value = "Delete a CRP Flagship program")
  @RequiresPermissions(Permission.CRP_PROGRAM_DELETE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIARStructure}/deleteFlagship/{id}", method = RequestMethod.DELETE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> deleteFlagship(@PathVariable String CGIARStructure, @PathVariable Long id) {
    LOG.debug("Delete Flagship with id: {}", id);

    GlobalUnit globalUnitEntity = globalUnitManager.findGlobalUnitByAcronym(CGIARStructure);
    CrpProgram crpProgram = crpProgramManager.getCrpProgramById(id);

    // If The program is not a Flagship
    if (crpProgram.getProgramType() != ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
      return ResponseEntity.notFound().build();
    }

    // If the program does not belong the CGIAR Structure
    if (!crpProgram.getCrp().getId().equals(globalUnitEntity.getId())) {
      return ResponseEntity.notFound().build();
    }

    crpProgramManager.deleteCrpProgram(crpProgram.getId());
    return ResponseEntity.ok().build();
  }


  /**
   * Get all The flagships with specific CGIAR Structure (Crp/Platform)
   * 
   * @param CGIARStructure
   * @return
   */
  @ApiOperation(value = "View a CRP Flagship programs", response = Iterable.class)
  @RequiresPermissions(Permission.CRP_PROGRAM_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIARStructure}/flagships", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<CrpProgramDTO> getAllFlagships(@PathVariable String CGIARStructure) {
    LOG.debug("REST request to get Flagships");

    GlobalUnit globalUnitEntity = globalUnitManager.findGlobalUnitByAcronym(CGIARStructure);

    List<CrpProgram> flagships = new ArrayList<>(globalUnitEntity.getCrpPrograms().stream()
      .filter(fg -> fg.isActive() && fg.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList()));

    List<CrpProgramDTO> crpProgramDTOs =
      flagships.stream().map(crpProgramEntity -> crpProgramMapper.crpProgramToCrpProgramDTO(crpProgramEntity))
        .collect(Collectors.toList());
    return crpProgramDTOs;
  }

  private User getCurrentUser() {
    Subject subject = SecurityUtils.getSubject();
    Long principal = (Long) subject.getPrincipal();
    User user = userManager.getUser(principal);
    return user;
  }


}
