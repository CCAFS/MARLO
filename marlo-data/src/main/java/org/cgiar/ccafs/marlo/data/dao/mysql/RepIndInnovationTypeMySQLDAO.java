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

import org.cgiar.ccafs.marlo.data.dao.RepIndInnovationTypeDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndInnovationType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndInnovationTypeMySQLDAO extends AbstractMarloDAO<RepIndInnovationType, Long>
  implements RepIndInnovationTypeDAO {


  @Inject
  public RepIndInnovationTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndInnovationType(long repIndInnovationTypeId) {
    RepIndInnovationType repIndInnovationType = this.find(repIndInnovationTypeId);
    this.delete(repIndInnovationType);
  }

  @Override
  public boolean existRepIndInnovationType(long repIndInnovationTypeID) {
    RepIndInnovationType repIndInnovationType = this.find(repIndInnovationTypeID);
    if (repIndInnovationType == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndInnovationType find(long id) {
    return super.find(RepIndInnovationType.class, id);

  }

  @Override
  public List<RepIndInnovationType> findAll() {
    String query = "from " + RepIndInnovationType.class.getName() + " WHERE is_marlo=1";
    List<RepIndInnovationType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<RepIndInnovationType> oneCGIARFindAll() {
    String query = "from " + RepIndInnovationType.class.getName() + " WHERE is_onecgiar=1";
    List<RepIndInnovationType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndInnovationType save(RepIndInnovationType repIndInnovationType) {
    if (repIndInnovationType.getId() == null) {
      super.saveEntity(repIndInnovationType);
    } else {
      repIndInnovationType = super.update(repIndInnovationType);
    }


    return repIndInnovationType;
  }


}