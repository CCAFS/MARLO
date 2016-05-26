/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.CrpProgramDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;

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
  public CrpProgram getCrpProgramById(long crpProgramID) {

    return crpProgramDAO.find(crpProgramID);
  }

  @Override
  public long saveCrpProgram(CrpProgram crpProgram) {

    return crpProgramDAO.save(crpProgram);
  }


}
