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

import org.cgiar.ccafs.marlo.data.dao.RepIndGenderYouthFocusLevelDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndGenderYouthFocusLevelMySQLDAO extends AbstractMarloDAO<RepIndGenderYouthFocusLevel, Long>
  implements RepIndGenderYouthFocusLevelDAO {


  @Inject
  public RepIndGenderYouthFocusLevelMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndGenderYouthFocusLevel(long repIndGenderYouthFocusLevelId) {
    RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel = this.find(repIndGenderYouthFocusLevelId);
    this.delete(repIndGenderYouthFocusLevel);
  }

  @Override
  public boolean existRepIndGenderYouthFocusLevel(long repIndGenderYouthFocusLevelID) {
    RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel = this.find(repIndGenderYouthFocusLevelID);
    if (repIndGenderYouthFocusLevel == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndGenderYouthFocusLevel find(long id) {
    return super.find(RepIndGenderYouthFocusLevel.class, id);

  }

  @Override
  public List<RepIndGenderYouthFocusLevel> findAll() {
    String query = "from " + RepIndGenderYouthFocusLevel.class.getName();
    List<RepIndGenderYouthFocusLevel> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndGenderYouthFocusLevel save(RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel) {
    if (repIndGenderYouthFocusLevel.getId() == null) {
      super.saveEntity(repIndGenderYouthFocusLevel);
    } else {
      repIndGenderYouthFocusLevel = super.update(repIndGenderYouthFocusLevel);
    }


    return repIndGenderYouthFocusLevel;
  }


}