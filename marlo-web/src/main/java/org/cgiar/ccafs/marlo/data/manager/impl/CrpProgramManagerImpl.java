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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.CrpProgramDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CrpProgramManagerImpl implements CrpProgramManager {


  private CrpProgramDAO crpProgramDAO;
  // Managers


  @Inject
  public CrpProgramManagerImpl(CrpProgramDAO crpProgramDAO) {
    this.crpProgramDAO = crpProgramDAO;


  }

  @Override
  public boolean deleteCrpProgram(long crpProgramId) {

    return crpProgramDAO.deleteCrpProgram(crpProgramId);
  }

  @Override
  public boolean existCrpProgram(long crpProgramID) {

    return crpProgramDAO.existCrpProgram(crpProgramID);
  }

  @Override
  public List<CrpProgram> findAll() {

    return crpProgramDAO.findAll();

  }

  @Override
  public List<CrpProgram> findCrpProgramsByType(long id, int programType) {
    return crpProgramDAO.findCrpProgramsByType(id, programType);
  }

  @Override
  public CrpProgram getCrpProgramById(long crpProgramID) {

    return crpProgramDAO.find(crpProgramID);
  }

  @Override
  public long saveCrpProgram(CrpProgram crpProgram) {

    return crpProgramDAO.save(crpProgram);
  }


  @Override
  public long saveCrpProgram(CrpProgram crpProgram, String actionName, List<String> relationsName) {

    return crpProgramDAO.save(crpProgram, actionName, relationsName);
  }

  @Override
  public long saveCrpProgram(CrpProgram crpProgram, String actionName, List<String> relationsName, Phase phase) {

    return crpProgramDAO.save(crpProgram, actionName, relationsName, phase);
  }
}
