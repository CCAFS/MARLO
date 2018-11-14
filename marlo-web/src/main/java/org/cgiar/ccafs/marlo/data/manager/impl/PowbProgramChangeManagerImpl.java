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


import org.cgiar.ccafs.marlo.data.dao.PowbProgramChangeDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbProgramChangeManager;
import org.cgiar.ccafs.marlo.data.model.PowbProgramChange;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class PowbProgramChangeManagerImpl implements PowbProgramChangeManager {


  private PowbProgramChangeDAO powbProgramChangeDAO;
  // Managers


  @Inject
  public PowbProgramChangeManagerImpl(PowbProgramChangeDAO powbProgramChangeDAO) {
    this.powbProgramChangeDAO = powbProgramChangeDAO;


  }

  @Override
  public void deletePowbProgramChange(long powbProgramChangeId) {

    powbProgramChangeDAO.deletePowbProgramChange(powbProgramChangeId);
  }

  @Override
  public boolean existPowbProgramChange(long powbProgramChangeID) {

    return powbProgramChangeDAO.existPowbProgramChange(powbProgramChangeID);
  }

  @Override
  public List<PowbProgramChange> findAll() {

    return powbProgramChangeDAO.findAll();

  }

  @Override
  public PowbProgramChange getPowbProgramChangeById(long powbProgramChangeID) {

    return powbProgramChangeDAO.find(powbProgramChangeID);
  }

  @Override
  public PowbProgramChange savePowbProgramChange(PowbProgramChange powbProgramChange) {

    return powbProgramChangeDAO.save(powbProgramChange);
  }


}
