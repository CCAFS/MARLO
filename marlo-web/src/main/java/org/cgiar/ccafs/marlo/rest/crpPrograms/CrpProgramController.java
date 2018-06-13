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

package org.cgiar.ccafs.marlo.rest.crpPrograms;

import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.security.Permission;

import java.util.ArrayList;
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
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@RestController
public class CrpProgramController {

  private static final Logger LOG = LoggerFactory.getLogger(CrpProgramController.class);


  private final CrpProgramMapper crpProgramMapper;

  private final GlobalUnitManager globalUnitManager;

  @Inject
  public CrpProgramController(CrpProgramMapper crpProgramMapper, GlobalUnitManager globalUnitManager) {
    super();
    this.crpProgramMapper = crpProgramMapper;
    this.globalUnitManager = globalUnitManager;
  }

  @RequiresPermissions(Permission.CRP_PROGRAM_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{globalUnit}/flagships", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<CrpProgramDTO> getAllFlagships(@PathVariable String globalUnit) {
    LOG.debug("REST request to get Flagships");

    GlobalUnit globalUnitEntity = globalUnitManager.findGlobalUnitByAcronym(globalUnit);

    List<CrpProgram> flagships = new ArrayList<>(globalUnitEntity.getCrpPrograms().stream()
      .filter(fg -> fg.isActive() && fg.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList()));

    List<CrpProgramDTO> crpProgramDTOs =
      flagships.stream().map(crpProgramEntity -> crpProgramMapper.crpProgramToCrpProgramDTO(crpProgramEntity))
        .collect(Collectors.toList());
    return crpProgramDTOs;
  }


}
