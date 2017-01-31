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

import org.cgiar.ccafs.marlo.data.dao.MogSynthesyDAO;
import org.cgiar.ccafs.marlo.data.model.MogSynthesy;

import java.util.List;

import com.google.inject.Inject;

public class MogSynthesyMySQLDAO implements MogSynthesyDAO {

  private StandardDAO dao;

  @Inject
  public MogSynthesyMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteMogSynthesy(long mogSynthesyId) {
    MogSynthesy mogSynthesy = this.find(mogSynthesyId);
    return dao.delete(mogSynthesy);
  }

  @Override
  public boolean existMogSynthesy(long mogSynthesyID) {
    MogSynthesy mogSynthesy = this.find(mogSynthesyID);
    if (mogSynthesy == null) {
      return false;
    }
    return true;

  }

  @Override
  public MogSynthesy find(long id) {
    return dao.find(MogSynthesy.class, id);

  }

  @Override
  public List<MogSynthesy> findAll() {
    String query = "from " + MogSynthesy.class.getName() + " where is_active=1";
    List<MogSynthesy> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(MogSynthesy mogSynthesy) {
    if (mogSynthesy.getId() == null) {
      dao.save(mogSynthesy);
    } else {
      dao.update(mogSynthesy);
    }


    return mogSynthesy.getId();
  }


}