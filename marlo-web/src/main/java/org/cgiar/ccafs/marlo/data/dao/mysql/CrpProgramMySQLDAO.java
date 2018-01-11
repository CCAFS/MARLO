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

import org.cgiar.ccafs.marlo.data.dao.CrpProgramDAO;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class CrpProgramMySQLDAO extends AbstractMarloDAO<CrpProgram, Long> implements CrpProgramDAO {


  @Inject
  public CrpProgramMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCrpProgram(long crpProgramId) {
    CrpProgram crpProgram = this.find(crpProgramId);
    crpProgram.setActive(false);
    this.save(crpProgram);
  }

  @Override
  public boolean existCrpProgram(long crpProgramID) {
    CrpProgram crpProgram = this.find(crpProgramID);
    if (crpProgram == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpProgram find(long id) {
    return super.find(CrpProgram.class, id);

  }

  @Override
  public List<CrpProgram> findAll() {


    String query = "from " + CrpProgram.class.getName() + " where is_active=1";
    List<CrpProgram> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

    /*
     * Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
     * List<CrpProgram> programs = new ArrayList<CrpProgram>();
     * String query = "select * from auditlog";
     * List<Map<String, Object>> lst = super.findCustomQuery(query);
     * for (Map<String, Object> map : lst) {
     * String json = (String) map.get("Entity_json");
     * CrpProgram prog = gson.fromJson(json, CrpProgram.class);
     * programs.add(prog);
     * }
     * return programs;
     */
  }

  @Override
  public List<CrpProgram> findCrpProgramsByType(long id, int programType) {
    String query = "from " + CrpProgram.class.getName() + " where global_unit_id=" + id + " and program_type="
      + programType + " and is_active=1";
    List<CrpProgram> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public CrpProgram save(CrpProgram crpProgram) {
    if (crpProgram.getId() == null) {
      super.saveEntity(crpProgram);
    } else {
      crpProgram = super.update(crpProgram);
    }
    return crpProgram;
  }


  @Override
  public CrpProgram save(CrpProgram crpProgram, String actionName, List<String> relationsName, Phase phase) {
    if (crpProgram.getId() == null) {
      super.saveEntity(crpProgram, actionName, relationsName, phase);
    } else {
      crpProgram = super.update(crpProgram, actionName, relationsName, phase);
    }
    return crpProgram;
  }


}