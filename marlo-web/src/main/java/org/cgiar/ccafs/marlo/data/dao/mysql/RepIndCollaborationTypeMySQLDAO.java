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

import org.cgiar.ccafs.marlo.data.dao.RepIndCollaborationTypeDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndCollaborationType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndCollaborationTypeMySQLDAO extends AbstractMarloDAO<RepIndCollaborationType, Long>
  implements RepIndCollaborationTypeDAO {


  @Inject
  public RepIndCollaborationTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndCollaborationType(long repIndCollaborationTypeId) {
    RepIndCollaborationType repIndCollaborationType = this.find(repIndCollaborationTypeId);
    this.delete(repIndCollaborationType);
  }

  @Override
  public boolean existRepIndCollaborationType(long repIndCollaborationTypeID) {
    RepIndCollaborationType repIndCollaborationType = this.find(repIndCollaborationTypeID);
    if (repIndCollaborationType == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndCollaborationType find(long id) {
    return super.find(RepIndCollaborationType.class, id);

  }

  @Override
  public List<RepIndCollaborationType> findAll() {
    String query = "from " + RepIndCollaborationType.class.getName();
    List<RepIndCollaborationType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndCollaborationType save(RepIndCollaborationType repIndCollaborationType) {
    if (repIndCollaborationType.getId() == null) {
      super.saveEntity(repIndCollaborationType);
    } else {
      repIndCollaborationType = super.update(repIndCollaborationType);
    }


    return repIndCollaborationType;
  }


}