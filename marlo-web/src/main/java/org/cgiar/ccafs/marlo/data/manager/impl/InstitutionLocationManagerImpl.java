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


import org.cgiar.ccafs.marlo.data.dao.InstitutionLocationDAO;
import org.cgiar.ccafs.marlo.data.manager.InstitutionLocationManager;
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class InstitutionLocationManagerImpl implements InstitutionLocationManager {


  private InstitutionLocationDAO institutionLocationDAO;
  // Managers


  @Inject
  public InstitutionLocationManagerImpl(InstitutionLocationDAO institutionLocationDAO) {
    this.institutionLocationDAO = institutionLocationDAO;


  }

  @Override
  public boolean deleteInstitutionLocation(long institutionLocationId) {

    return institutionLocationDAO.deleteInstitutionLocation(institutionLocationId);
  }

  @Override
  public boolean existInstitutionLocation(long institutionLocationID) {

    return institutionLocationDAO.existInstitutionLocation(institutionLocationID);
  }

  @Override
  public List<InstitutionLocation> findAll() {

    return institutionLocationDAO.findAll();

  }

  @Override
  public InstitutionLocation findByLocation(long locationID, long institutionID) {
    return institutionLocationDAO.findByLocation(locationID, institutionID);
  }

  @Override
  public InstitutionLocation getInstitutionLocationById(long institutionLocationID) {

    return institutionLocationDAO.find(institutionLocationID);
  }

  @Override
  public long saveInstitutionLocation(InstitutionLocation institutionLocation) {

    return institutionLocationDAO.save(institutionLocation);
  }


}
