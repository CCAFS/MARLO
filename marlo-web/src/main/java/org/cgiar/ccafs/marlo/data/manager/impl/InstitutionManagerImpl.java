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

import org.cgiar.ccafs.marlo.data.dao.InstitutionDAO;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.Institution;

import java.util.List;

import com.google.inject.Inject;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class InstitutionManagerImpl implements InstitutionManager {

  private InstitutionDAO institutionDao;

  @Inject
  public InstitutionManagerImpl(InstitutionDAO institutionDao) {
    this.institutionDao = institutionDao;
  }

  @Override
  public boolean deleteInstitution(long institutionId) {
    return institutionDao.deleteInstitution(institutionId);
  }

  @Override
  public boolean existInstitution(long institutionId) {
    return institutionDao.existInstitution(institutionId);
  }

  @Override
  public List<Institution> findAll() {
    return institutionDao.findAll();
  }

  @Override
  public Institution getInstitutionById(long institutionId) {
    return institutionDao.find(institutionId);
  }

  @Override
  public long saveInstitution(Institution institution) {
    return institutionDao.save(institution);
  }

  @Override
  public List<Institution> searchInstitution(String searchValue, int ppaPartner, int onlyPPA, long crpID) {
    return institutionDao.searchInstitution(searchValue, ppaPartner, onlyPPA, crpID);
  }

}
