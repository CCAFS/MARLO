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

import org.cgiar.ccafs.marlo.data.dao.ICenterProjectOutputDAO;
import org.cgiar.ccafs.marlo.data.model.CenterProjectOutput;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CenterProjectOutputDAO extends AbstractMarloDAO implements ICenterProjectOutputDAO {


  @Inject
  public CenterProjectOutputDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteProjectOutput(long projectOutputId) {
    CenterProjectOutput projectOutput = this.find(projectOutputId);
    projectOutput.setActive(false);
    return this.save(projectOutput) > 0;
  }

  @Override
  public boolean existProjectOutput(long projectOutputID) {
    CenterProjectOutput projectOutput = this.find(projectOutputID);
    if (projectOutput == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterProjectOutput find(long id) {
    return super.find(CenterProjectOutput.class, id);

  }

  @Override
  public List<CenterProjectOutput> findAll() {
    String query = "from " + CenterProjectOutput.class.getName();
    List<CenterProjectOutput> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterProjectOutput> getProjectOutputsByUserId(long userId) {
    String query = "from " + CenterProjectOutput.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public long save(CenterProjectOutput projectOutput) {
    if (projectOutput.getId() == null) {
      super.save(projectOutput);
    } else {
      super.update(projectOutput);
    }
    return projectOutput.getId();
  }


}