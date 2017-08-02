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

/**
 * 
 */
package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.ICenterAreaDAO;
import org.cgiar.ccafs.marlo.data.model.CenterArea;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;


/**
 * Modified by @author nmatovu last on Oct 7, 2016
 */
public class CenterAreaDAO extends AbstractMarloDAO implements ICenterAreaDAO {


  @Inject
  public CenterAreaDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  /*
   * (non-Javadoc)
   * @see org.cgiar.ccafs.marlo.data.dao.ICenterAreaDAO#deleteResearchArea(long)
   */
  @Override
  public boolean deleteResearchArea(long researchAreaId) {
    CenterArea researchArea = this.find(researchAreaId);
    researchArea.setActive(false);
    return this.save(researchArea) > 0;
  }

  /*
   * (non-Javadoc)
   * @see org.cgiar.ccafs.marlo.data.dao.ICenterAreaDAO#existResearchArea(long)
   */
  @Override
  public boolean existResearchArea(long researchAreaId) {
    CenterArea researchArea = this.find(researchAreaId);
    if (researchArea == null) {
      return false;
    }
    return true;

  }

  /*
   * (non-Javadoc)
   * @see org.cgiar.ccafs.marlo.data.dao.ICenterAreaDAO#find(long)
   */
  @Override
  public CenterArea find(long researchId) {
    return super.find(CenterArea.class, researchId);
  }

  /*
   * (non-Javadoc)
   * @see org.cgiar.ccafs.marlo.data.dao.ICenterAreaDAO#findAll()
   */
  @Override
  public List<CenterArea> findAll() {
    String query = "from " + CenterArea.class.getName();
    List<CenterArea> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.cgiar.ccafs.marlo.data.dao.ICenterAreaDAO#findResearchAreaByAcronym(java.lang.String)
   */
  @Override
  public CenterArea findResearchAreaByAcronym(String acronym) {
    String query = "from " + CenterArea.class.getName() + " where acronym='" + acronym + "'";
    List<CenterArea> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.cgiar.ccafs.marlo.data.dao.ICenterAreaDAO#save(org.cgiar.ccafs.marlo.data.model.CenterArea)
   */
  @Override
  public long save(CenterArea researchArea) {
    if (researchArea.getId() == null) {
      super.save(researchArea);
    } else {
      super.update(researchArea);
    }
    return researchArea.getId();
  }

}
