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
package org.cgiar.ccafs.marlo.data.service.impl;

import org.cgiar.ccafs.marlo.data.dao.ICenterAreaDAO;
import org.cgiar.ccafs.marlo.data.model.CenterArea;
import org.cgiar.ccafs.marlo.data.service.ICenterAreaService;

import java.util.List;

import com.google.inject.Inject;


/**
 * Modified by @author nmatovu last on Oct 7, 2016
 */
public class CenterAreaService implements ICenterAreaService {

  private ICenterAreaDAO researchAreaDao;

  @Inject
  public CenterAreaService(ICenterAreaDAO researchAreaDAO) {
    this.researchAreaDao = researchAreaDAO;
  }

  /*
   * (non-Javadoc)
   * @see org.cgiar.ccafs.marlo.data.service.ICenterAreaService#deleteResearchArea(long)
   */
  @Override
  public boolean deleteResearchArea(long researchAreaId) {
    return researchAreaDao.deleteResearchArea(researchAreaId);
  }

  /*
   * (non-Javadoc)
   * @see org.cgiar.ccafs.marlo.data.service.ICenterAreaService#existResearchArea(long)
   */
  @Override
  public boolean existResearchArea(long researchAreaId) {

    return researchAreaDao.existResearchArea(researchAreaId);
  }

  /*
   * (non-Javadoc)
   * @see org.cgiar.ccafs.marlo.data.service.ICenterAreaService#find(long)
   */
  @Override
  public CenterArea find(long researchId) {

    return researchAreaDao.find(researchId);
  }

  /*
   * (non-Javadoc)
   * @see org.cgiar.ccafs.marlo.data.service.ICenterAreaService#findAll()
   */
  @Override
  public List<CenterArea> findAll() {

    return researchAreaDao.findAll();
  }

  /*
   * (non-Javadoc)
   * @see org.cgiar.ccafs.marlo.data.service.ICenterAreaService#findResearchAreaByAcronym(java.lang.String)
   */
  @Override
  public CenterArea findResearchAreaByAcronym(String acronym) {

    return researchAreaDao.findResearchAreaByAcronym(acronym);
  }

  /*
   * (non-Javadoc)
   * @see org.cgiar.ccafs.marlo.data.service.ICenterAreaService#save(org.cgiar.ccafs.marlo.data.model.CenterArea)
   */
  @Override
  public long save(CenterArea researchArea) {

    return researchAreaDao.save(researchArea);
  }

}
