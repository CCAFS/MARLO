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


import org.cgiar.ccafs.marlo.data.dao.ProgressTargetCaseGeographicCountryDAO;
import org.cgiar.ccafs.marlo.data.manager.ProgressTargetCaseGeographicCountryManager;
import org.cgiar.ccafs.marlo.data.model.ProgressTargetCaseGeographicCountry;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProgressTargetCaseGeographicCountryManagerImpl implements ProgressTargetCaseGeographicCountryManager {


  private ProgressTargetCaseGeographicCountryDAO progressTargetCaseGeographicCountryDAO;
  // Managers


  @Inject
  public ProgressTargetCaseGeographicCountryManagerImpl(
    ProgressTargetCaseGeographicCountryDAO progressTargetCaseGeographicCountryDAO) {
    this.progressTargetCaseGeographicCountryDAO = progressTargetCaseGeographicCountryDAO;


  }

  @Override
  public void deleteProgressTargetCaseGeographicCountry(long progressTargetCaseGeographicCountryId) {

    progressTargetCaseGeographicCountryDAO
      .deleteProgressTargetCaseGeographicCountry(progressTargetCaseGeographicCountryId);
  }

  @Override
  public boolean existProgressTargetCaseGeographicCountry(long progressTargetCaseGeographicCountryID) {

    return progressTargetCaseGeographicCountryDAO
      .existProgressTargetCaseGeographicCountry(progressTargetCaseGeographicCountryID);
  }

  @Override
  public List<ProgressTargetCaseGeographicCountry> findAll() {

    return progressTargetCaseGeographicCountryDAO.findAll();

  }

  @Override
  public List<ProgressTargetCaseGeographicCountry> findGeographicCountryByTargetCase(long targetCaseID) {
    return progressTargetCaseGeographicCountryDAO.findGeographicCountryByTargetCase(targetCaseID);
  }

  @Override
  public ProgressTargetCaseGeographicCountry
    getProgressTargetCaseGeographicCountryById(long progressTargetCaseGeographicCountryID) {

    return progressTargetCaseGeographicCountryDAO.find(progressTargetCaseGeographicCountryID);
  }

  @Override
  public ProgressTargetCaseGeographicCountry
    saveProgressTargetCaseGeographicCountry(ProgressTargetCaseGeographicCountry progressTargetCaseGeographicCountry) {

    return progressTargetCaseGeographicCountryDAO.save(progressTargetCaseGeographicCountry);
  }


}
