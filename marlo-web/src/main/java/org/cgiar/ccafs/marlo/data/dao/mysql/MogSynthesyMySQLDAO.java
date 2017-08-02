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
import org.hibernate.SessionFactory;

public class MogSynthesyMySQLDAO extends AbstractMarloDAO implements MogSynthesyDAO {


  @Inject
  public MogSynthesyMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteMogSynthesy(long mogSynthesyId) {
    MogSynthesy mogSynthesy = this.find(mogSynthesyId);
    return super.delete(mogSynthesy);
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
    return super.find(MogSynthesy.class, id);

  }

  @Override
  public List<MogSynthesy> findAll() {
    String query = "from " + MogSynthesy.class.getName() + " where is_active=1";
    List<MogSynthesy> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<MogSynthesy> findMogSynthesis(long programId) {
    String sql = "from " + MogSynthesy.class.getName() + " where program_id=" + programId;
    List<MogSynthesy> list = super.findAll(sql);
    return list;
  }

  @Override
  public List<MogSynthesy> findMogSynthesisRegion(long midoutcome, int year) {
    String sql = "from " + MogSynthesy.class.getName() + " where mog_id=" + midoutcome + " and year=" + year
      + " and program_id not in (1,2,3,4)";
    List<MogSynthesy> list = super.findAll(sql);
    return list;
  }

  @Override
  public long save(MogSynthesy mogSynthesy) {
    if (mogSynthesy.getId() == null) {
      super.save(mogSynthesy);
    } else {
      super.update(mogSynthesy);
    }


    return mogSynthesy.getId();
  }


}