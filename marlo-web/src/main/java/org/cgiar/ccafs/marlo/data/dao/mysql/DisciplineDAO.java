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

import org.cgiar.ccafs.marlo.data.dao.IDisciplineDAO;
import org.cgiar.ccafs.marlo.data.model.Discipline;

import java.util.List;

import com.google.inject.Inject;

public class DisciplineDAO implements IDisciplineDAO {

  private final StandardDAO dao;

  @Inject
  public DisciplineDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteDiscipline(long disciplineId) {
    final Discipline discipline = this.find(disciplineId);
    // discipline.setActive(false);
    return this.save(discipline) > 0;
  }

  @Override
  public boolean existDiscipline(long disciplineID) {
    final Discipline discipline = this.find(disciplineID);
    if (discipline == null) {
      return false;
    }
    return true;

  }

  @Override
  public Discipline find(long id) {
    return dao.find(Discipline.class, id);

  }

  @Override
  public List<Discipline> findAll() {
    final String query = "from " + Discipline.class.getName();
    final List<Discipline> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<Discipline> getDisciplinesByUserId(long userId) {
    final String query = "from " + Discipline.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(Discipline discipline) {
    if (discipline.getId() == null) {
      dao.save(discipline);
    } else {
      dao.update(discipline);
    }
    return discipline.getId();
  }

  @Override
  public long save(Discipline discipline, String actionName, List<String> relationsName) {
    if (discipline.getId() == null) {
      dao.save(discipline, actionName, relationsName);
    } else {
      dao.update(discipline, actionName, relationsName);
    }
    return discipline.getId();
  }


}