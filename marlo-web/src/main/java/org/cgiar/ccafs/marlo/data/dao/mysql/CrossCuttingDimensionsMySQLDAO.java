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
import org.cgiar.ccafs.marlo.data.model.dto.CrossCuttingDimensionTableDTO;

import java.util.List;
import java.util.Map;

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
  public CrossCuttingDimensionTableDTO getTableC(Long liaisonInstitution, Long phaseId) {

    CrossCuttingDimensionTableDTO table = new CrossCuttingDimensionTableDTO();
    String sql = "select " + "(" + "select count(id) " + "from deliverables_info di " + "where exists( "
      + "select 'x' from deliverables d" + "where exists( " + "select 'y' from projects_info p"
      + "where p.liaison_institution_id = " + liaisonInstitution + " and p.id_phase = " + phaseId
      + " and p.id = d.project_id)" + "and di.deliverable_id = d.id) "
      + "and di.cross_cutting_score_gender is not null " + " and di.id_phase = " + phaseId + ") gender," + "( "
      + "select count(id)  num_youth " + " from deliverables_info di " + "where exists( "
      + "select 'x' from deliverables d " + "where exists( " + "select 'y' from projects_info p "
      + "where p.liaison_institution_id = " + liaisonInstitution + "and p.id_phase =" + phaseId
      + "and p.id = d.project_id) " + " and di.deliverable_id = d.id) "
      + "and di.cross_cutting_score_youth is not null " + " and di.id_phase = " + phaseId + ")youth," + "("
      + "select count(id)  num_capacity " + "from deliverables_info di " + "where exists( "
      + "select 'x' from deliverables d " + "where exists( " + "select 'y' from projects_info p "
      + "where p.liaison_institution_id = " + liaisonInstitution + "and p.id_phase = " + phaseId
      + "and p.id = d.project_id)" + "and di.deliverable_id = d.id)"
      + "and di.cross_cutting_score_capacity is not null " + " and di.id_phase = " + phaseId + ")capacity "
      + "from dual";

    List<Map<String, Object>> fieldsTableC = super.findCustomQuery(sql);

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
