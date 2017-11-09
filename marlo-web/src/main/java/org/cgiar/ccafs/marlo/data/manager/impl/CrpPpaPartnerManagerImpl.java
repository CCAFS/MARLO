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

import org.cgiar.ccafs.marlo.data.dao.CrpPpaPartnerDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpPpaPartnerManager;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;

import java.util.List;

import com.google.inject.Inject;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CrpPpaPartnerManagerImpl implements CrpPpaPartnerManager {

  private CrpPpaPartnerDAO crpPpaPartnerDao;

  @Inject
  public CrpPpaPartnerManagerImpl(CrpPpaPartnerDAO crpPpaPartnerDao) {
    this.crpPpaPartnerDao = crpPpaPartnerDao;
  }

  @Override
  public void deleteCrpPpaPartner(long crpPpaPartnerId) {
    crpPpaPartnerDao.deleteCrpPpaPartner(crpPpaPartnerId);
  }

  @Override
  public boolean existCrpPpaPartner(long crpPpaPartnerId) {
    return crpPpaPartnerDao.existCrpPpaPartner(crpPpaPartnerId);
  }

  @Override
  public List<CrpPpaPartner> findAll() {
    return crpPpaPartnerDao.findAll();
  }

  @Override
  public CrpPpaPartner getCrpPpaPartnerById(long crpPpaPartnerId) {
    return crpPpaPartnerDao.find(crpPpaPartnerId);
  }

  @Override
  public CrpPpaPartner saveCrpPpaPartner(CrpPpaPartner crpPpaPartner) {
    return crpPpaPartnerDao.save(crpPpaPartner);
  }

}
