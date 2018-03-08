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


import org.cgiar.ccafs.marlo.data.dao.PowbFinancialExpenditureDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbFinancialExpenditureManager;
import org.cgiar.ccafs.marlo.data.model.PowbFinancialExpenditure;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class PowbFinancialExpenditureManagerImpl implements PowbFinancialExpenditureManager {


  private PowbFinancialExpenditureDAO powbFinancialExpenditureDAO;
  // Managers


  @Inject
  public PowbFinancialExpenditureManagerImpl(PowbFinancialExpenditureDAO powbFinancialExpenditureDAO) {
    this.powbFinancialExpenditureDAO = powbFinancialExpenditureDAO;


  }

  @Override
  public void deletePowbFinancialExpenditure(long powbFinancialExpenditureId) {

    powbFinancialExpenditureDAO.deletePowbFinancialExpenditure(powbFinancialExpenditureId);
  }

  @Override
  public boolean existPowbFinancialExpenditure(long powbFinancialExpenditureID) {

    return powbFinancialExpenditureDAO.existPowbFinancialExpenditure(powbFinancialExpenditureID);
  }

  @Override
  public List<PowbFinancialExpenditure> findAll() {

    return powbFinancialExpenditureDAO.findAll();

  }

  @Override
  public PowbFinancialExpenditure getPowbFinancialExpenditureById(long powbFinancialExpenditureID) {

    return powbFinancialExpenditureDAO.find(powbFinancialExpenditureID);
  }

  @Override
  public PowbFinancialExpenditure savePowbFinancialExpenditure(PowbFinancialExpenditure powbFinancialExpenditure) {

    return powbFinancialExpenditureDAO.save(powbFinancialExpenditure);
  }


}
