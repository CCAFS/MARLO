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

import org.cgiar.ccafs.marlo.data.dao.OutcomeSynthesyDAO;
import org.cgiar.ccafs.marlo.data.model.OutcomeSynthesy;

import java.util.List;

import com.google.inject.Inject;

public class OutcomeSynthesyMySQLDAO implements OutcomeSynthesyDAO {

  private StandardDAO dao;

  @Inject
  public OutcomeSynthesyMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteOutcomeSynthesy(long outcomeSynthesyId) {
    OutcomeSynthesy outcomeSynthesy = this.find(outcomeSynthesyId);
    return dao.delete(outcomeSynthesy);
  }

  @Override
  public boolean existOutcomeSynthesy(long outcomeSynthesyID) {
    OutcomeSynthesy outcomeSynthesy = this.find(outcomeSynthesyID);
    if (outcomeSynthesy == null) {
      return false;
    }
    return true;

  }

  @Override
  public OutcomeSynthesy find(long id) {
    return dao.find(OutcomeSynthesy.class, id);

  }

  @Override
  public List<OutcomeSynthesy> findAll() {
    String query = "from " + OutcomeSynthesy.class.getName() + "";
    List<OutcomeSynthesy> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(OutcomeSynthesy outcomeSynthesy) {
    if (outcomeSynthesy.getId() == null) {
      dao.save(outcomeSynthesy);
    } else {
      dao.update(outcomeSynthesy);
    }


    return outcomeSynthesy.getId();
  }


}