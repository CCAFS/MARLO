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

import org.cgiar.ccafs.marlo.data.dao.RepIndStageProcessDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndStageProcess;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndStageProcessMySQLDAO extends AbstractMarloDAO<RepIndStageProcess, Long>
  implements RepIndStageProcessDAO {


  @Inject
  public RepIndStageProcessMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndStageProcess(long repIndStageProcessId) {
    RepIndStageProcess repIndStageProcess = this.find(repIndStageProcessId);
    this.delete(repIndStageProcess);
  }

  @Override
  public boolean existRepIndStageProcess(long repIndStageProcessID) {
    RepIndStageProcess repIndStageProcess = this.find(repIndStageProcessID);
    if (repIndStageProcess == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndStageProcess find(long id) {
    return super.find(RepIndStageProcess.class, id);

  }

  @Override
  public List<RepIndStageProcess> findAll() {
    String query = "from " + RepIndStageProcess.class.getName();
    List<RepIndStageProcess> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndStageProcess save(RepIndStageProcess repIndStageProcess) {
    if (repIndStageProcess.getId() == null) {
      super.saveEntity(repIndStageProcess);
    } else {
      repIndStageProcess = super.update(repIndStageProcess);
    }


    return repIndStageProcess;
  }


}