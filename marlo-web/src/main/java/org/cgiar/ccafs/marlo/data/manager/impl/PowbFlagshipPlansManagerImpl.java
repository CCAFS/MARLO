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


import org.cgiar.ccafs.marlo.data.dao.PowbFlagshipPlansDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbFlagshipPlansManager;
import org.cgiar.ccafs.marlo.data.model.PowbFlagshipPlans;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class PowbFlagshipPlansManagerImpl implements PowbFlagshipPlansManager {


  private PowbFlagshipPlansDAO powbFlagshipPlansDAO;
  // Managers


  @Inject
  public PowbFlagshipPlansManagerImpl(PowbFlagshipPlansDAO powbFlagshipPlansDAO) {
    this.powbFlagshipPlansDAO = powbFlagshipPlansDAO;


  }

  @Override
  public void deletePowbFlagshipPlans(long powbFlagshipPlansId) {

    powbFlagshipPlansDAO.deletePowbFlagshipPlans(powbFlagshipPlansId);
  }

  @Override
  public boolean existPowbFlagshipPlans(long powbFlagshipPlansID) {

    return powbFlagshipPlansDAO.existPowbFlagshipPlans(powbFlagshipPlansID);
  }

  @Override
  public List<PowbFlagshipPlans> findAll() {

    return powbFlagshipPlansDAO.findAll();

  }

  @Override
  public PowbFlagshipPlans getPowbFlagshipPlansById(long powbFlagshipPlansID) {

    return powbFlagshipPlansDAO.find(powbFlagshipPlansID);
  }

  @Override
  public PowbFlagshipPlans savePowbFlagshipPlans(PowbFlagshipPlans powbFlagshipPlans) {

    return powbFlagshipPlansDAO.save(powbFlagshipPlans);
  }


}
