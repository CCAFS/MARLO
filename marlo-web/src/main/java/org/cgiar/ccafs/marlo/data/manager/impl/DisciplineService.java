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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.IDisciplineDAO;
import org.cgiar.ccafs.marlo.data.manager.IDisciplineService;
import org.cgiar.ccafs.marlo.data.model.Discipline;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class DisciplineService implements IDisciplineService {


  private IDisciplineDAO disciplineDAO;

  // Managers


  @Inject
  public DisciplineService(IDisciplineDAO disciplineDAO) {
    this.disciplineDAO = disciplineDAO;


  }

  @Override
  public boolean deleteDiscipline(long disciplineId) {

    return disciplineDAO.deleteDiscipline(disciplineId);
  }

  @Override
  public boolean existDiscipline(long disciplineID) {

    return disciplineDAO.existDiscipline(disciplineID);
  }

  @Override
  public List<Discipline> findAll() {

    return disciplineDAO.findAll();

  }

  @Override
  public Discipline getDisciplineById(long disciplineID) {

    return disciplineDAO.find(disciplineID);
  }

  @Override
  public List<Discipline> getDisciplinesByUserId(Long userId) {
    return disciplineDAO.getDisciplinesByUserId(userId);
  }

  @Override
  public long saveDiscipline(Discipline discipline) {

    return disciplineDAO.save(discipline);
  }

  @Override
  public long saveDiscipline(Discipline discipline, String actionName, List<String> relationsName) {
    return disciplineDAO.save(discipline, actionName, relationsName);
  }


}
