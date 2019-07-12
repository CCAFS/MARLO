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

import org.cgiar.ccafs.marlo.data.dao.RepIndMilestoneReasonDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndMilestoneReason;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndMilestoneReasonMySQLDAO extends AbstractMarloDAO<RepIndMilestoneReason, Long>
  implements RepIndMilestoneReasonDAO {


  @Inject
  public RepIndMilestoneReasonMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndMilestoneReason(long repIndMilestoneReasonId) {
    RepIndMilestoneReason repIndMilestoneReason = this.find(repIndMilestoneReasonId);
    this.delete(repIndMilestoneReason);
  }

  @Override
  public boolean existRepIndMilestoneReason(long repIndMilestoneReasonID) {
    RepIndMilestoneReason repIndMilestoneReason = this.find(repIndMilestoneReasonID);
    if (repIndMilestoneReason == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndMilestoneReason find(long id) {
    return super.find(RepIndMilestoneReason.class, id);

  }

  @Override
  public List<RepIndMilestoneReason> findAll() {
    String query = "from " + RepIndMilestoneReason.class.getName();
    List<RepIndMilestoneReason> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndMilestoneReason save(RepIndMilestoneReason repIndMilestoneReason) {
    if (repIndMilestoneReason.getId() == null) {
      super.saveEntity(repIndMilestoneReason);
    } else {
      repIndMilestoneReason = super.update(repIndMilestoneReason);
    }


    return repIndMilestoneReason;
  }


}