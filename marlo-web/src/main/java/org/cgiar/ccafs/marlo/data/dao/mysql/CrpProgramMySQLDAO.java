/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.CrpProgramDAO;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;

import java.util.List;

import com.google.inject.Inject;

public class CrpProgramMySQLDAO implements CrpProgramDAO {

  private StandardDAO dao;

  @Inject
  public CrpProgramMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCrpProgram(long crpProgramId) {
    CrpProgram crpProgram = this.find(crpProgramId);
    crpProgram.setActive(false);
    return this.save(crpProgram) > 0;
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
    return dao.find(CrpProgram.class, id);

  }

  @Override
  public List<CrpProgram> findAll() {


    String query = "from " + CrpProgram.class.getName() + " where is_active=1";
    List<CrpProgram> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

    /*
     * Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
     * List<CrpProgram> programs = new ArrayList<CrpProgram>();
     * String query = "select * from auditlog";
     * List<Map<String, Object>> lst = dao.findCustomQuery(query);
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
    String query = "from " + CrpProgram.class.getName() + " where crp_id=" + id + " and program_type=" + programType
      + " and is_active=1";
    List<CrpProgram> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public long save(CrpProgram crpProgram) {
    if (crpProgram.getId() == null) {
      dao.save(crpProgram);
    } else {
      dao.update(crpProgram);
    }
    return crpProgram.getId();
  }


}