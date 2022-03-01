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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.manager.impl;

import org.cgiar.ccafs.marlo.data.dao.ImpactAreasDAO;
import org.cgiar.ccafs.marlo.data.manager.ImpactAreasManager;
import org.cgiar.ccafs.marlo.data.model.ImpactArea;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ImpactAreasManagerImpl implements ImpactAreasManager {

  private ImpactAreasDAO impactAreasDAO;

  @Inject
  public ImpactAreasManagerImpl(ImpactAreasDAO impactAreasDAO) {
    super();
    this.impactAreasDAO = impactAreasDAO;
  }

  @Override
  public void deleteImpactArea(Long impactAreaId) {
    this.impactAreasDAO.deleteImpactArea(impactAreaId.longValue());
  }

  @Override
  public boolean existImpactArea(Long impactAreaID) {
    return this.impactAreasDAO.existImpactArea(impactAreaID.longValue());
  }

  @Override
  public List<ImpactArea> getAll() {
    return this.impactAreasDAO.getAll();
  }

  @Override
  public ImpactArea getImpactAreaByFinancialCode(String financialCode) {
    return this.impactAreasDAO.getImpactAreaByFinancialCode(financialCode);
  }

  @Override
  public ImpactArea getImpactAreaById(Long id) {
    return this.impactAreasDAO.getImpactAreaById(id.longValue());
  }

  @Override
  public ImpactArea save(ImpactArea impactArea) {
    return this.impactAreasDAO.save(impactArea);
  }

}
