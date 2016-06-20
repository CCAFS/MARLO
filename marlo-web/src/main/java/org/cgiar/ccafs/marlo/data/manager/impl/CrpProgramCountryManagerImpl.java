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


import org.cgiar.ccafs.marlo.data.dao.CrpProgramCountryDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramCountryManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgramCountry;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CrpProgramCountryManagerImpl implements CrpProgramCountryManager {


  private CrpProgramCountryDAO crpProgramCountryDAO;
  // Managers


  @Inject
  public CrpProgramCountryManagerImpl(CrpProgramCountryDAO crpProgramCountryDAO) {
    this.crpProgramCountryDAO = crpProgramCountryDAO;


  }

  @Override
  public boolean deleteCrpProgramCountry(long crpProgramCountryId) {

    return crpProgramCountryDAO.deleteCrpProgramCountry(crpProgramCountryId);
  }

  @Override
  public boolean existCrpProgramCountry(long crpProgramCountryID) {

    return crpProgramCountryDAO.existCrpProgramCountry(crpProgramCountryID);
  }

  @Override
  public List<CrpProgramCountry> findAll() {

    return crpProgramCountryDAO.findAll();

  }

  @Override
  public CrpProgramCountry getCrpProgramCountryById(long crpProgramCountryID) {

    return crpProgramCountryDAO.find(crpProgramCountryID);
  }

  @Override
  public long saveCrpProgramCountry(CrpProgramCountry crpProgramCountry) {

    return crpProgramCountryDAO.save(crpProgramCountry);
  }


}
