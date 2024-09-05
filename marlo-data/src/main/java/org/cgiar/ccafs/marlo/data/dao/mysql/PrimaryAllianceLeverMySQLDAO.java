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

import org.cgiar.ccafs.marlo.data.dao.PrimaryAllianceLeverDAO;
import org.cgiar.ccafs.marlo.data.model.PrimaryAllianceLever;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class PrimaryAllianceLeverMySQLDAO extends AbstractMarloDAO<PrimaryAllianceLever, Long>
  implements PrimaryAllianceLeverDAO {


  @Inject
  public PrimaryAllianceLeverMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePrimaryAllianceLever(long primaryAllianceLeverId) {
    PrimaryAllianceLever primaryAllianceLever = this.find(primaryAllianceLeverId);
    primaryAllianceLever.setActive(false);
    this.update(primaryAllianceLever);
  }

  @Override
  public boolean existPrimaryAllianceLever(long primaryAllianceLeverID) {
    PrimaryAllianceLever primaryAllianceLever = this.find(primaryAllianceLeverID);
    if (primaryAllianceLever == null) {
      return false;
    }
    return true;

  }

  @Override
  public PrimaryAllianceLever find(long id) {
    return super.find(PrimaryAllianceLever.class, id);

  }

  @Override
  public List<PrimaryAllianceLever> findAll() {
    String query = "from " + PrimaryAllianceLever.class.getName() + " where is_active=1";
    List<PrimaryAllianceLever> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }


  @Override
  public List<PrimaryAllianceLever> findAllByPhase(long phaseId) {
    String query = "from " + PrimaryAllianceLever.class.getName() + " where is_active=1 and id_phase=" + phaseId;
    List<PrimaryAllianceLever> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PrimaryAllianceLever save(PrimaryAllianceLever primaryAllianceLever) {
    if (primaryAllianceLever.getId() == null) {
      super.saveEntity(primaryAllianceLever);
    } else {
      primaryAllianceLever = super.update(primaryAllianceLever);
    }


    return primaryAllianceLever;
  }


}