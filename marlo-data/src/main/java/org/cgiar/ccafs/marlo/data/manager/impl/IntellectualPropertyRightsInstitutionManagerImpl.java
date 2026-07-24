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


import org.cgiar.ccafs.marlo.data.dao.IntellectualPropertyRightsInstitutionDAO;
import org.cgiar.ccafs.marlo.data.manager.IntellectualPropertyRightsInstitutionManager;
import org.cgiar.ccafs.marlo.data.model.IntellectualPropertyRightsInstitution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author CCAFS
 */
@Named
public class IntellectualPropertyRightsInstitutionManagerImpl implements IntellectualPropertyRightsInstitutionManager {


  private IntellectualPropertyRightsInstitutionDAO intellectualPropertyRightsInstitutionDAO;
  // Managers


  @Inject
  public IntellectualPropertyRightsInstitutionManagerImpl(IntellectualPropertyRightsInstitutionDAO intellectualPropertyRightsInstitutionDAO) {
    this.intellectualPropertyRightsInstitutionDAO = intellectualPropertyRightsInstitutionDAO;


  }

  @Override
  @Transactional
  public void deleteIntellectualPropertyRightsInstitution(long intellectualPropertyRightsInstitutionId) {

    intellectualPropertyRightsInstitutionDAO.deleteIntellectualPropertyRightsInstitution(intellectualPropertyRightsInstitutionId);
  }

  @Override
  public boolean existIntellectualPropertyRightsInstitution(long intellectualPropertyRightsInstitutionID) {

    return intellectualPropertyRightsInstitutionDAO.existIntellectualPropertyRightsInstitution(intellectualPropertyRightsInstitutionID);
  }

  @Override
  public List<IntellectualPropertyRightsInstitution> findAll() {

    return intellectualPropertyRightsInstitutionDAO.findAll();

  }

  @Override
  public IntellectualPropertyRightsInstitution getIntellectualPropertyRightsInstitutionById(long intellectualPropertyRightsInstitutionID) {

    return intellectualPropertyRightsInstitutionDAO.find(intellectualPropertyRightsInstitutionID);
  }

  @Override
  public IntellectualPropertyRightsInstitution saveIntellectualPropertyRightsInstitution(IntellectualPropertyRightsInstitution intellectualPropertyRightsInstitution) {

    return intellectualPropertyRightsInstitutionDAO.save(intellectualPropertyRightsInstitution);
  }


}
