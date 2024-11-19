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

import org.cgiar.ccafs.marlo.data.dao.AllianceLeverDAO;
import org.cgiar.ccafs.marlo.data.model.AllianceLever;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class AllianceLeverMySQLDAO extends AbstractMarloDAO<AllianceLever, Long> implements AllianceLeverDAO {


  @Inject
  public AllianceLeverMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteAllianceLever(long allianceLeverId) {
    AllianceLever allianceLever = this.find(allianceLeverId);
    allianceLever.setActive(false);
    this.update(allianceLever);
  }

  @Override
  public boolean existAllianceLever(long allianceLeverID) {
    AllianceLever allianceLever = this.find(allianceLeverID);
    if (allianceLever == null) {
      return false;
    }
    return true;

  }

  @Override
  public AllianceLever find(long id) {
    return super.find(AllianceLever.class, id);

  }

  @Override
  public List<AllianceLever> findAll() {
    String query = "from " + AllianceLever.class.getName() + " where is_active=1";
    List<AllianceLever> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public AllianceLever save(AllianceLever allianceLever) {
    if (allianceLever.getId() == null) {
      super.saveEntity(allianceLever);
    } else {
      allianceLever = super.update(allianceLever);
    }


    return allianceLever;
  }


}