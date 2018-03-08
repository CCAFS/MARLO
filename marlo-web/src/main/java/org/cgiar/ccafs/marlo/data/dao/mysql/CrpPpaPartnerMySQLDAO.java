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

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class CrpPpaPartnerMySQLDAO extends AbstractMarloDAO<CrpPpaPartner, Long> implements CrpPpaPartnerDAO {


  @Inject
  public CrpPpaPartnerMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCrpPpaPartner(long crpPpaPartnerId) {
    CrpPpaPartner crpPpaPartner = this.find(crpPpaPartnerId);
    crpPpaPartner.setActive(false);
    super.update(crpPpaPartner);
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
    return super.find(CrpPpaPartner.class, id);
  }

  @Override
  public List<CrpPpaPartner> findAll() {
    String query = "from " + CrpPpaPartner.class.getName() + " where is_active=1";
    List<CrpPpaPartner> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public CrpPpaPartner save(CrpPpaPartner crpPpaPartner) {
    if (crpPpaPartner.getId() == null) {
      super.saveEntity(crpPpaPartner);
    } else {
      crpPpaPartner = super.update(crpPpaPartner);
    }
    return crpPpaPartner;
  }

}
