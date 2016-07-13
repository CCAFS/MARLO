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

package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.CrpPpaPartnerDAO;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;

import java.util.List;

import com.google.inject.Inject;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CrpPpaPartnerMySQLDAO implements CrpPpaPartnerDAO {

  private StandardDAO dao;

  @Inject
  public CrpPpaPartnerMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCrpPpaPartner(long crpPpaPartnerId) {
    CrpPpaPartner crpPpaPartner = this.find(crpPpaPartnerId);
    crpPpaPartner.setActive(false);
    return this.save(crpPpaPartner) > 0;
  }

  @Override
  public boolean existCrpPpaPartner(long crpPpaPartnerId) {
    CrpPpaPartner crpPpaPartner = this.find(crpPpaPartnerId);
    if (crpPpaPartner == null) {
      return false;
    }
    return true;
  }

  @Override
  public CrpPpaPartner find(long id) {
    return dao.find(CrpPpaPartner.class, id);
  }

  @Override
  public List<CrpPpaPartner> findAll() {
    String query = "from " + CrpPpaPartner.class.getName() + " where is_active=1";
    List<CrpPpaPartner> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public long save(CrpPpaPartner crpPpaPartner) {
    if (crpPpaPartner.getId() == null) {
      dao.save(crpPpaPartner);
    } else {
      dao.update(crpPpaPartner);
    }
    return crpPpaPartner.getId();
  }

}
