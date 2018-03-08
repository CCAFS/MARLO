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


import org.cgiar.ccafs.marlo.data.dao.PowbCrpStaffingCategoriesDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbCrpStaffingCategoriesManager;
import org.cgiar.ccafs.marlo.data.model.PowbCrpStaffingCategories;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class PowbCrpStaffingCategoriesManagerImpl implements PowbCrpStaffingCategoriesManager {


  private PowbCrpStaffingCategoriesDAO powbCrpStaffingCategoriesDAO;
  // Managers


  @Inject
  public PowbCrpStaffingCategoriesManagerImpl(PowbCrpStaffingCategoriesDAO powbCrpStaffingCategoriesDAO) {
    this.powbCrpStaffingCategoriesDAO = powbCrpStaffingCategoriesDAO;


  }

  @Override
  public void deletePowbCrpStaffingCategories(long powbCrpStaffingCategoriesId) {

    powbCrpStaffingCategoriesDAO.deletePowbCrpStaffingCategories(powbCrpStaffingCategoriesId);
  }

  @Override
  public boolean existPowbCrpStaffingCategories(long powbCrpStaffingCategoriesID) {

    return powbCrpStaffingCategoriesDAO.existPowbCrpStaffingCategories(powbCrpStaffingCategoriesID);
  }

  @Override
  public List<PowbCrpStaffingCategories> findAll() {

    return powbCrpStaffingCategoriesDAO.findAll();

  }

  @Override
  public PowbCrpStaffingCategories getPowbCrpStaffingCategoriesById(long powbCrpStaffingCategoriesID) {

    return powbCrpStaffingCategoriesDAO.find(powbCrpStaffingCategoriesID);
  }

  @Override
  public PowbCrpStaffingCategories savePowbCrpStaffingCategories(PowbCrpStaffingCategories powbCrpStaffingCategories) {

    return powbCrpStaffingCategoriesDAO.save(powbCrpStaffingCategories);
  }


}
