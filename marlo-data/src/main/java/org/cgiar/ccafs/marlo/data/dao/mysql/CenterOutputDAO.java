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

import org.cgiar.ccafs.marlo.data.dao.ICenterOutputDAO;
import org.cgiar.ccafs.marlo.data.model.CenterOutput;

import java.util.List;

import com.google.inject.Inject;

public class CenterOutputDAO implements ICenterOutputDAO {

  private StandardDAO dao;

  @Inject
  public CenterOutputDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteResearchOutput(long researchOutputId) {
    CenterOutput researchOutput = this.find(researchOutputId);
    researchOutput.setActive(false);
    return this.save(researchOutput) > 0;
  }

  @Override
  public boolean existResearchOutput(long researchOutputID) {
    CenterOutput researchOutput = this.find(researchOutputID);
    if (researchOutput == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterOutput find(long id) {
    return dao.find(CenterOutput.class, id);

  }

  @Override
  public List<CenterOutput> findAll() {
    String query = "from " + CenterOutput.class.getName();
    List<CenterOutput> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterOutput> getResearchOutputsByUserId(long userId) {
    String query = "from " + CenterOutput.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CenterOutput researchOutput) {
    if (researchOutput.getId() == null) {
      dao.save(researchOutput);
    } else {
      dao.update(researchOutput);
    }
    return researchOutput.getId();
  }

  @Override
  public long save(CenterOutput output, String actionName, List<String> relationsName) {
    if (output.getId() == null) {
      dao.save(output, actionName, relationsName);
    } else {
      dao.update(output, actionName, relationsName);
    }
    return output.getId();
  }


}