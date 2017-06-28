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
package org.cgiar.ccafs.marlo.data.service.impl;


import org.cgiar.ccafs.marlo.data.dao.IInstitutionDAO;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.service.IInstitutionService;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class InstitutionService implements IInstitutionService {


  private IInstitutionDAO institutionDAO;

  // Managers


  @Inject
  public InstitutionService(IInstitutionDAO institutionDAO) {
    this.institutionDAO = institutionDAO;


  }

  @Override
  public boolean deleteInstitution(long institutionId) {

    return institutionDAO.deleteInstitution(institutionId);
  }

  @Override
  public boolean existInstitution(long institutionID) {

    return institutionDAO.existInstitution(institutionID);
  }

  @Override
  public List<Institution> findAll() {

    return institutionDAO.findAll();

  }

  @Override
  public Institution getInstitutionById(long institutionID) {

    return institutionDAO.find(institutionID);
  }

  @Override
  public List<Institution> getInstitutionsByUserId(Long userId) {
    return institutionDAO.getInstitutionsByUserId(userId);
  }

  @Override
  public long saveInstitution(Institution institution) {

    return institutionDAO.save(institution);
  }


}
