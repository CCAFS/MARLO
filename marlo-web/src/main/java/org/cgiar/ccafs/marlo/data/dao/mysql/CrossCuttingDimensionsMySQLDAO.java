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

import org.cgiar.ccafs.marlo.data.dao.CrossCuttingDimensionsDAO;
import org.cgiar.ccafs.marlo.data.model.CrossCuttingDimensions;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class CrossCuttingDimensionsMySQLDAO extends AbstractMarloDAO<CrossCuttingDimensions, Long>
  implements CrossCuttingDimensionsDAO {

  @Inject
  public CrossCuttingDimensionsMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public CrossCuttingDimensions find(Long id) {
    // TODO Auto-generated method stub
    return super.find(CrossCuttingDimensions.class, id);
  }

  @Override
  public CrossCuttingDimensions findCrossCutting(Long liaisonInstitutionId, Long phaseId) {
    // TODO Auto-generated method stub
    String query = "from " + CrossCuttingDimensions.class.getName() + " where is_active=1 and id_phase= " + phaseId
      + " and liaison_institution_id= " + liaisonInstitutionId;
    List<CrossCuttingDimensions> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;


  }

  @Override
  public CrossCuttingDimensions save(CrossCuttingDimensions crossCutting) {
    if (crossCutting.getId() == null) {
      super.saveEntity(crossCutting);
    } else {
      crossCutting = super.update(crossCutting);
    }

    return crossCutting;
  }

}
