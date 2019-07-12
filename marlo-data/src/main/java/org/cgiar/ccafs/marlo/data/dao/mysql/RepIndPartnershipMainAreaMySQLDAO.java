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

import org.cgiar.ccafs.marlo.data.dao.RepIndPartnershipMainAreaDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndPartnershipMainArea;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndPartnershipMainAreaMySQLDAO extends AbstractMarloDAO<RepIndPartnershipMainArea, Long>
  implements RepIndPartnershipMainAreaDAO {


  @Inject
  public RepIndPartnershipMainAreaMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndPartnershipMainArea(long repIndPartnershipMainAreaId) {
    RepIndPartnershipMainArea repIndPartnershipMainArea = this.find(repIndPartnershipMainAreaId);
    this.delete(repIndPartnershipMainArea);
  }

  @Override
  public boolean existRepIndPartnershipMainArea(long repIndPartnershipMainAreaID) {
    RepIndPartnershipMainArea repIndPartnershipMainArea = this.find(repIndPartnershipMainAreaID);
    if (repIndPartnershipMainArea == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndPartnershipMainArea find(long id) {
    return super.find(RepIndPartnershipMainArea.class, id);

  }

  @Override
  public List<RepIndPartnershipMainArea> findAll() {
    String query = "from " + RepIndPartnershipMainArea.class.getName();
    List<RepIndPartnershipMainArea> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndPartnershipMainArea save(RepIndPartnershipMainArea repIndPartnershipMainArea) {
    if (repIndPartnershipMainArea.getId() == null) {
      super.saveEntity(repIndPartnershipMainArea);
    } else {
      repIndPartnershipMainArea = super.update(repIndPartnershipMainArea);
    }


    return repIndPartnershipMainArea;
  }


}