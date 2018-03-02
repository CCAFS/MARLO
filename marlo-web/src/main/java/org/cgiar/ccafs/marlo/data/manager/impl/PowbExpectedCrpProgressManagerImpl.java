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


import org.cgiar.ccafs.marlo.data.dao.PowbExpectedCrpProgressDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbExpectedCrpProgressManager;
import org.cgiar.ccafs.marlo.data.model.PowbExpectedCrpProgress;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class PowbExpectedCrpProgressManagerImpl implements PowbExpectedCrpProgressManager {


  private PowbExpectedCrpProgressDAO powbExpectedCrpProgressDAO;
  // Managers


  @Inject
  public PowbExpectedCrpProgressManagerImpl(PowbExpectedCrpProgressDAO powbExpectedCrpProgressDAO) {
    this.powbExpectedCrpProgressDAO = powbExpectedCrpProgressDAO;


  }

  @Override
  public void deletePowbExpectedCrpProgress(long powbExpectedCrpProgressId) {

    powbExpectedCrpProgressDAO.deletePowbExpectedCrpProgress(powbExpectedCrpProgressId);
  }

  @Override
  public boolean existPowbExpectedCrpProgress(long powbExpectedCrpProgressID) {

    return powbExpectedCrpProgressDAO.existPowbExpectedCrpProgress(powbExpectedCrpProgressID);
  }

  @Override
  public List<PowbExpectedCrpProgress> findAll() {

    return powbExpectedCrpProgressDAO.findAll();

  }

  @Override
  public List<PowbExpectedCrpProgress> findByProgram(long crpProgramID) {

    return powbExpectedCrpProgressDAO.findByProgram(crpProgramID);
  }

  @Override
  public PowbExpectedCrpProgress getPowbExpectedCrpProgressById(long powbExpectedCrpProgressID) {

    return powbExpectedCrpProgressDAO.find(powbExpectedCrpProgressID);
  }

  @Override
  public PowbExpectedCrpProgress savePowbExpectedCrpProgress(PowbExpectedCrpProgress powbExpectedCrpProgress) {

    return powbExpectedCrpProgressDAO.save(powbExpectedCrpProgress);
  }


}
