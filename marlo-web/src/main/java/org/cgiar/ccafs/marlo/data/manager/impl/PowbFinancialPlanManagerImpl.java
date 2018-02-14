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


import org.cgiar.ccafs.marlo.data.dao.PowbFinancialPlanDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbFinancialPlanManager;
import org.cgiar.ccafs.marlo.data.model.PowbFinancialPlan;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class PowbFinancialPlanManagerImpl implements PowbFinancialPlanManager {


  private PowbFinancialPlanDAO powbFinancialPlanDAO;
  // Managers


  @Inject
  public PowbFinancialPlanManagerImpl(PowbFinancialPlanDAO powbFinancialPlanDAO) {
    this.powbFinancialPlanDAO = powbFinancialPlanDAO;


  }

  @Override
  public void deletePowbFinancialPlan(long powbFinancialPlanId) {

    powbFinancialPlanDAO.deletePowbFinancialPlan(powbFinancialPlanId);
  }

  @Override
  public boolean existPowbFinancialPlan(long powbFinancialPlanID) {

    return powbFinancialPlanDAO.existPowbFinancialPlan(powbFinancialPlanID);
  }

  @Override
  public List<PowbFinancialPlan> findAll() {

    return powbFinancialPlanDAO.findAll();

  }

  @Override
  public PowbFinancialPlan getPowbFinancialPlanById(long powbFinancialPlanID) {

    return powbFinancialPlanDAO.find(powbFinancialPlanID);
  }

  @Override
  public PowbFinancialPlan savePowbFinancialPlan(PowbFinancialPlan powbFinancialPlan) {

    return powbFinancialPlanDAO.save(powbFinancialPlan);
  }


}
