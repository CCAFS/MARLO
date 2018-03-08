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


import org.cgiar.ccafs.marlo.data.dao.PowbSynthesisCrpStaffingCategoryDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisCrpStaffingCategoryManager;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesisCrpStaffingCategory;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class PowbSynthesisCrpStaffingCategoryManagerImpl implements PowbSynthesisCrpStaffingCategoryManager {


  private PowbSynthesisCrpStaffingCategoryDAO powbSynthesisCrpStaffingCategoryDAO;
  // Managers


  @Inject
  public PowbSynthesisCrpStaffingCategoryManagerImpl(PowbSynthesisCrpStaffingCategoryDAO powbSynthesisCrpStaffingCategoryDAO) {
    this.powbSynthesisCrpStaffingCategoryDAO = powbSynthesisCrpStaffingCategoryDAO;


  }

  @Override
  public void deletePowbSynthesisCrpStaffingCategory(long powbSynthesisCrpStaffingCategoryId) {

    powbSynthesisCrpStaffingCategoryDAO.deletePowbSynthesisCrpStaffingCategory(powbSynthesisCrpStaffingCategoryId);
  }

  @Override
  public boolean existPowbSynthesisCrpStaffingCategory(long powbSynthesisCrpStaffingCategoryID) {

    return powbSynthesisCrpStaffingCategoryDAO.existPowbSynthesisCrpStaffingCategory(powbSynthesisCrpStaffingCategoryID);
  }

  @Override
  public List<PowbSynthesisCrpStaffingCategory> findAll() {

    return powbSynthesisCrpStaffingCategoryDAO.findAll();

  }

  @Override
  public PowbSynthesisCrpStaffingCategory getPowbSynthesisCrpStaffingCategoryById(long powbSynthesisCrpStaffingCategoryID) {

    return powbSynthesisCrpStaffingCategoryDAO.find(powbSynthesisCrpStaffingCategoryID);
  }

  @Override
  public PowbSynthesisCrpStaffingCategory savePowbSynthesisCrpStaffingCategory(PowbSynthesisCrpStaffingCategory powbSynthesisCrpStaffingCategory) {

    return powbSynthesisCrpStaffingCategoryDAO.save(powbSynthesisCrpStaffingCategory);
  }


}
