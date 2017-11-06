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


import org.cgiar.ccafs.marlo.data.dao.ICenterProgramDAO;
import org.cgiar.ccafs.marlo.data.manager.ICenterProgramManager;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterProgramManager implements ICenterProgramManager {


  private ICenterProgramDAO crpProgramDAO;

  // Managers


  @Inject
  public CenterProgramManager(ICenterProgramDAO crpProgramDAO) {
    this.crpProgramDAO = crpProgramDAO;


  }

  @Override
  public void deleteProgram(long programId) {

    crpProgramDAO.deleteProgram(programId);
  }

  @Override
  public boolean existProgram(long programID) {

    return crpProgramDAO.existProgram(programID);
  }

  @Override
  public List<CenterProgram> findAll() {

    return crpProgramDAO.findAll();

  }

  /*
   * (non-Javadoc)
   * @see org.cgiar.ccafs.marlo.data.service.ICenterProgramManager#findProgramsByResearchArea(java.lang.Long)
   */
  @Override
  public List<CenterProgram> findProgramsByResearchArea(Long researchAreaId) {

    return crpProgramDAO.findProgramsByResearchArea(researchAreaId);
  }

  @Override
  public List<CenterProgram> findProgramsByType(long id, int programType) {
    return crpProgramDAO.findProgramsByType(id, programType);
  }

  @Override
  public CenterProgram getProgramById(long programID) {

    return crpProgramDAO.find(programID);
  }


  @Override
  public CenterProgram saveProgram(CenterProgram researchProgram) {

    return crpProgramDAO.save(researchProgram);
  }

  @Override
  public CenterProgram saveProgram(CenterProgram researchProgram, String actionName, List<String> relationsName) {

    return crpProgramDAO.save(researchProgram, actionName, relationsName);
  }

}
