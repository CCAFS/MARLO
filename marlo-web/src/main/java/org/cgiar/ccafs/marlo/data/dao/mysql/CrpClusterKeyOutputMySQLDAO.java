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

import org.cgiar.ccafs.marlo.data.dao.CrpClusterKeyOutputDAO;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;

import java.util.List;

import com.google.inject.Inject;

public class CrpClusterKeyOutputMySQLDAO implements CrpClusterKeyOutputDAO {

  private StandardDAO dao;

  @Inject
  public CrpClusterKeyOutputMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCrpClusterKeyOutput(long crpClusterKeyOutputId) {
    CrpClusterKeyOutput crpClusterKeyOutput = this.find(crpClusterKeyOutputId);
    crpClusterKeyOutput.setActive(false);
    return this.save(crpClusterKeyOutput) > 0;
  }

  @Override
  public boolean existCrpClusterKeyOutput(long crpClusterKeyOutputID) {
    CrpClusterKeyOutput crpClusterKeyOutput = this.find(crpClusterKeyOutputID);
    if (crpClusterKeyOutput == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpClusterKeyOutput find(long id) {
    return dao.find(CrpClusterKeyOutput.class, id);

  }

  @Override
  public List<CrpClusterKeyOutput> findAll() {
    String query = "from " + CrpClusterKeyOutput.class.getName() + " where is_active=1";
    List<CrpClusterKeyOutput> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(CrpClusterKeyOutput crpClusterKeyOutput) {
    if (crpClusterKeyOutput.getId() == null) {
      dao.save(crpClusterKeyOutput);
    } else {
      dao.update(crpClusterKeyOutput);
    }


    return crpClusterKeyOutput.getId();
  }


}