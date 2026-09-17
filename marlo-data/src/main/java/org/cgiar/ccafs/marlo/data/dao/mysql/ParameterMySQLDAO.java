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

import org.cgiar.ccafs.marlo.data.dao.ParameterDAO;
import org.cgiar.ccafs.marlo.data.model.Parameter;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

@Named
public class ParameterMySQLDAO extends AbstractMarloDAO<Parameter, Long> implements ParameterDAO {


  @Inject
  public ParameterMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteParameter(long parameterId) {
    Parameter parameter = this.find(parameterId);

    super.delete(parameter);
  }

  @Override
  public boolean existParameter(long parameterID) {
    Parameter parameter = this.find(parameterID);
    if (parameter == null) {
      return false;
    }
    return true;

  }

  @Override
  public Parameter find(long id) {
    return super.find(Parameter.class, id);

  }

  @Override
  public List<Parameter> findAll() {
    String query = "from " + Parameter.class.getName() + "";
    List<Parameter> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  /**
   * <b>Repaired 2026-08-31 during CHG-COGNITO-AUTH-001-T10 — the previous query could not execute.</b>
   * <p>
   * It read {@code "from Parameter where key='…' and global_unit_type_id=…"} and went through
   * {@code AbstractMarloDAO.findAll(String)}, which calls {@code createQuery} — <b>HQL, not native SQL</b>.
   * {@code global_unit_type_id} is the <i>column</i>; {@code Parameters.hbm.xml} maps the association as the
   * property {@code globalUnitType}, so Hibernate rejected this at parse time on every call.
   * <p>
   * T10 made the method load-bearing — {@code CognitoAuthSpecificity} resolves every Global Unit's
   * {@code cognito_auth_active} through it, and the T02 migration seeds no {@code custom_parameters}, so
   * <b>every</b> unit reaches this catalog branch. Left broken it emptied {@code crps[]} inside
   * {@code CrpByUserEmailAction}'s per-entry catch and rendered as "email not found" for every user.
   * <p>
   * <b>Consequence beyond the defect, recorded rather than shipped quietly:</b> the only pre-existing caller,
   * {@code CrpAdminManagmentAction:1018-1032}, sits in a {@code parameters.size() == 0} branch that has
   * therefore always thrown. Repairing this method changes that branch from "always 500" to "performs a
   * {@code custom_parameters} INSERT" — an untested save path in a section this task never declared. See
   * {@code execution.md} PS-19.
   */
  @Override
  public Parameter getParameterByKey(String key, long globalUnitTypeId) {
    String queryString = "SELECT p FROM Parameter p WHERE p.key = :key AND p.globalUnitType.id = :typeId";
    Query<Parameter> query = this.getSessionFactory().getCurrentSession().createQuery(queryString, Parameter.class);
    query.setParameter("key", key).setParameter("typeId", globalUnitTypeId);
    List<Parameter> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public Parameter save(Parameter parameter) {
    if (parameter.getId() == null) {
      super.saveEntity(parameter);
    } else {
      parameter = super.update(parameter);
    }


    return parameter;
  }


}