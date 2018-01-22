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

import org.cgiar.ccafs.marlo.data.dao.CrossCuttingScoringDAO;
import org.cgiar.ccafs.marlo.data.model.CrossCuttingScoring;

import java.util.List;

import javax.inject.Named;

import com.opensymphony.xwork2.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class CrossCuttingScoringMySQLDAO extends AbstractMarloDAO<CrossCuttingScoring, Long>
  implements CrossCuttingScoringDAO {


  @Inject
  public CrossCuttingScoringMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }


  @Override
  public boolean existCrossCuttingScoring(long crossCuttingId) {
    CrossCuttingScoring crossCutting = this.find(crossCuttingId);
    if (crossCutting == null) {
      return false;
    }
    return true;
  }

  @Override
  public CrossCuttingScoring find(long crossCuttingId) {
    return super.find(CrossCuttingScoring.class, crossCuttingId);
  }

  @Override
  public List<CrossCuttingScoring> findAll() {
    String query = "from " + CrossCuttingScoring.class.getName();
    List<CrossCuttingScoring> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public CrossCuttingScoring save(CrossCuttingScoring crossCutting) {
    if (crossCutting.getId() == null) {
      crossCutting = super.saveEntity(crossCutting);
    } else {
      crossCutting = super.update(crossCutting);
    }

    return crossCutting;
  }

}
