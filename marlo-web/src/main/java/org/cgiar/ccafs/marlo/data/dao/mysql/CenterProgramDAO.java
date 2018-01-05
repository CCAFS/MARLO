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

import org.cgiar.ccafs.marlo.data.dao.ICenterProgramDAO;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class CenterProgramDAO extends AbstractMarloDAO<CenterProgram, Long> implements ICenterProgramDAO {


  @Inject
  public CenterProgramDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProgram(long programId) {
    CenterProgram researchProgram = this.find(programId);
    researchProgram.setActive(false);
    this.save(researchProgram);
  }

  @Override
  public boolean existProgram(long programID) {
    CenterProgram researchProgram = this.find(programID);
    if (researchProgram == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterProgram find(long id) {
    return super.find(CenterProgram.class, id);

  }

  @Override
  public List<CenterProgram> findAll() {
    String query = "from " + CenterProgram.class.getName() + " where is_active=1";
    List<CenterProgram> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.cgiar.ccafs.marlo.data.dao.ICenterProgramDAO#findProgramsByResearchArea(java.lang.Long)
   */
  @Override
  public List<CenterProgram> findProgramsByResearchArea(Long researchAreaId) {
    String query =
      "from " + CenterProgram.class.getName() + " where research_area_id=" + researchAreaId + " and is_active=1";
    List<CenterProgram> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public List<CenterProgram> findProgramsByType(long id, int programType) {
    String query = "from " + CenterProgram.class.getName() + " where research_area_id=" + id + " and program_type="
      + programType + " and is_active=1";
    List<CenterProgram> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public CenterProgram save(CenterProgram researchProgram) {
    if (researchProgram.getId() == null) {
      super.saveEntity(researchProgram);
    } else {
      researchProgram = super.update(researchProgram);
    }
    return researchProgram;
  }

  @Override
  public CenterProgram save(CenterProgram researchProgram, String actionName, List<String> relationsName) {
    if (researchProgram.getId() == null) {
      super.saveEntity(researchProgram, actionName, relationsName);
    } else {
      researchProgram = super.update(researchProgram, actionName, relationsName);
    }
    return researchProgram;
  }


}