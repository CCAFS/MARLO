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

import org.cgiar.ccafs.marlo.data.dao.ICenterProjectPartnerPersonDAO;
import org.cgiar.ccafs.marlo.data.model.CenterProjectPartnerPerson;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CenterProjectPartnerPersonDAO extends AbstractMarloDAO<CenterProjectPartnerPerson, Long>
  implements ICenterProjectPartnerPersonDAO {


  @Inject
  public CenterProjectPartnerPersonDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectPartnerPerson(long projectPartnerPersonId) {
    CenterProjectPartnerPerson projectPartnerPerson = this.find(projectPartnerPersonId);
    projectPartnerPerson.setActive(false);
    this.save(projectPartnerPerson);
  }

  @Override
  public boolean existProjectPartnerPerson(long projectPartnerPersonID) {
    CenterProjectPartnerPerson projectPartnerPerson = this.find(projectPartnerPersonID);
    if (projectPartnerPerson == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterProjectPartnerPerson find(long id) {
    return super.find(CenterProjectPartnerPerson.class, id);

  }

  @Override
  public List<CenterProjectPartnerPerson> findAll() {
    String query = "from " + CenterProjectPartnerPerson.class.getName();
    List<CenterProjectPartnerPerson> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterProjectPartnerPerson> getProjectPartnerPersonsByUserId(long userId) {
    String query = "from " + CenterProjectPartnerPerson.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CenterProjectPartnerPerson save(CenterProjectPartnerPerson projectPartnerPerson) {
    if (projectPartnerPerson.getId() == null) {
      super.saveEntity(projectPartnerPerson);
    } else {
      projectPartnerPerson = super.update(projectPartnerPerson);
    }
    return projectPartnerPerson;
  }


}