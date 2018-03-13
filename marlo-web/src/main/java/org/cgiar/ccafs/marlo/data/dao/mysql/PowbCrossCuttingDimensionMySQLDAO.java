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

import org.cgiar.ccafs.marlo.data.dao.PowbCrossCuttingDimensionDAO;
import org.cgiar.ccafs.marlo.data.model.CrossCuttingDimensionTableDTO;
import org.cgiar.ccafs.marlo.data.model.PowbCrossCuttingDimension;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class PowbCrossCuttingDimensionMySQLDAO extends AbstractMarloDAO<PowbCrossCuttingDimension, Long>
  implements PowbCrossCuttingDimensionDAO {


  @Inject
  public PowbCrossCuttingDimensionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePowbCrossCuttingDimension(long powbCrossCuttingDimensionId) {
    PowbCrossCuttingDimension powbCrossCuttingDimension = this.find(powbCrossCuttingDimensionId);
    powbCrossCuttingDimension.setActive(false);
    this.save(powbCrossCuttingDimension);
  }

  @Override
  public boolean existPowbCrossCuttingDimension(long powbCrossCuttingDimensionID) {
    PowbCrossCuttingDimension powbCrossCuttingDimension = this.find(powbCrossCuttingDimensionID);
    if (powbCrossCuttingDimension == null) {
      return false;
    }
    return true;

  }

  @Override
  public PowbCrossCuttingDimension find(long id) {
    return super.find(PowbCrossCuttingDimension.class, id);

  }

  @Override
  public List<PowbCrossCuttingDimension> findAll() {
    String query = "from " + PowbCrossCuttingDimension.class.getName() + " where is_active=1";
    List<PowbCrossCuttingDimension> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public CrossCuttingDimensionTableDTO getTableC(Long liaisonInstitution, Long phaseId) {

    CrossCuttingDimensionTableDTO table = new CrossCuttingDimensionTableDTO();
    String sql = "select " + "(" + "select count(id) " + " from deliverables_info di " + "where exists(  "
      + " select 'x' from deliverables d " + " where exists( " + " select 'y' from projects_info p "
      + " where p.liaison_institution_id =  " + liaisonInstitution + " and p.id_phase = " + phaseId
      + " and p.project_id = d.project_id) " + " and di.deliverable_id = d.id) "
      + " and di.cross_cutting_score_gender is not null " + " and di.cross_cutting_score_gender = 2 "
      + " and di.id_phase = " + phaseId + ") genderPrincipal, " + " (select count(id)  " + " from deliverables_info di "
      + " where exists( " + " select 'x' from deliverables d " + " where exists( " + " select 'y' from projects_info p "
      + " where p.liaison_institution_id = " + liaisonInstitution + " and p.id_phase = " + phaseId
      + " and p.project_id = d.project_id)" + " and di.deliverable_id = d.id) "
      + " and di.cross_cutting_score_gender is not null " + " and di.cross_cutting_score_gender = 1 "
      + " and di.id_phase = " + phaseId + ") genderSignificant, " + " (select count(id)  "
      + " from deliverables_info di " + " where exists( " + " select 'x' from deliverables d " + " where exists( "
      + " select 'y' from projects_info p " + " where p.liaison_institution_id = " + liaisonInstitution
      + " and p.id_phase = " + phaseId + " and p.project_id = d.project_id)" + " and di.deliverable_id = d.id) "
      + " and di.cross_cutting_score_gender is not null " + " and di.cross_cutting_score_gender = 0 "
      + " and di.id_phase = " + phaseId + ") genderNotTargered, " + "(" + " select count(id)  num_youth "
      + " from deliverables_info di " + " where exists( " + " select 'x' from deliverables d " + "where exists( "
      + " select 'y' from projects_info p " + " where p.liaison_institution_id =  " + liaisonInstitution
      + " and p.id_phase = " + phaseId + " and p.project_id = d.project_id) " + " and di.deliverable_id = d.id) "
      + " and di.cross_cutting_score_youth is not null " + " and di.cross_cutting_score_youth = 2 "
      + " and di.id_phase = " + phaseId + ")youthPrincipal, " + "(" + "select count(id)  num_youth "
      + " from deliverables_info di " + " where exists(" + " select 'x' from deliverables d " + " where exists("
      + " select 'y' from projects_info p " + " where p.liaison_institution_id = " + liaisonInstitution
      + " and p.id_phase = " + phaseId + " and p.project_id = d.project_id) " + " and di.deliverable_id = d.id) "
      + " and di.cross_cutting_score_youth is not null " + " and di.cross_cutting_score_youth = 1 "
      + " and di.id_phase = " + phaseId + ")youthSignificant," + "(" + " select count(id)  num_youth "
      + " from deliverables_info di " + " where exists(" + " select 'x' from deliverables d" + " where exists("
      + " select 'y' from projects_info p" + " where p.liaison_institution_id = " + liaisonInstitution
      + " and p.id_phase = " + phaseId + " and p.project_id = d.project_id)" + " and di.deliverable_id = d.id)"
      + " and di.cross_cutting_score_youth is not null " + " and di.cross_cutting_score_youth = 0 "
      + " and di.id_phase = " + phaseId + ")youthNotTargered, " + "(" + " select count(id)  num_capacity "
      + " from deliverables_info di" + " where exists(" + " select 'x' from deliverables d " + " where exists( "
      + " select 'y' from projects_info p " + " where p.liaison_institution_id = " + liaisonInstitution
      + " and p.id_phase = " + phaseId + " and p.project_id = d.project_id) " + " and di.deliverable_id = d.id)"
      + " and di.cross_cutting_score_capacity is not null " + " and di.cross_cutting_score_capacity = 2 "
      + " and di.id_phase =  " + phaseId + ")capacityPrincipal, " + "(" + "select count(id)  num_capacity "
      + " from deliverables_info di " + " where exists( " + "select 'x' from deliverables d " + "where exists( "
      + " select 'y' from projects_info p " + " where p.liaison_institution_id = " + liaisonInstitution
      + " and p.id_phase = " + phaseId + " and p.project_id = d.project_id) " + " and di.deliverable_id = d.id) "
      + " and di.cross_cutting_score_capacity is not null " + " and di.cross_cutting_score_capacity = 1 "
      + " and di.id_phase = " + phaseId + ")capacitySignificant, " + "(" + "select count(id)  num_capacity "
      + " from deliverables_info di" + " where exists(" + "select 'x' from deliverables d" + " where exists("
      + " select 'y' from projects_info p " + " where p.liaison_institution_id = " + liaisonInstitution
      + " and p.id_phase = " + phaseId + " and p.project_id = d.project_id)" + " and di.deliverable_id = d.id)"
      + " and di.cross_cutting_score_capacity is not null " + " and di.cross_cutting_score_capacity = 0 "
      + " and di.id_phase = " + phaseId + ")capacityNotScored";


    List<Map<String, Object>> fieldsTableC = super.findCustomQuery(sql);

    // map the values of table C
    for (Map<String, Object> field : fieldsTableC) {

      table.setGenderPrincipal(Integer.parseInt(field.get("genderPrincipal").toString()));
      table.setGenderScored(Integer.parseInt(field.get("genderNotTargered").toString()));
      table.setGenderSignificant(Integer.parseInt(field.get("genderSignificant").toString()));

      table.setYouthPrincipal(Integer.parseInt(field.get("youthPrincipal").toString()));
      table.setYouthSignificant(Integer.parseInt(field.get("youthSignificant").toString()));
      table.setYouthScored(Integer.parseInt(field.get("youthNotTargered").toString()));

      table.setCapDevPrincipal(Integer.parseInt(field.get("capacityPrincipal").toString()));
      table.setCapDevSignificant(Integer.parseInt(field.get("capacitySignificant").toString()));
      table.setCapDevScored(Integer.parseInt(field.get("capacityNotScored").toString()));

    }

    return table;
  }

  @Override
  public PowbCrossCuttingDimension save(PowbCrossCuttingDimension powbCrossCuttingDimension) {
    if (powbCrossCuttingDimension.getId() == null) {
      super.saveEntity(powbCrossCuttingDimension);
    } else {
      powbCrossCuttingDimension = super.update(powbCrossCuttingDimension);
    }


    return powbCrossCuttingDimension;
  }


}