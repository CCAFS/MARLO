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

import org.cgiar.ccafs.marlo.data.dao.AgreementDAO;
import org.cgiar.ccafs.marlo.data.model.Agreement;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;


@Named
public class AgreementMySQLDAO extends AbstractMarloDAO<Agreement, String> implements AgreementDAO {

  @Inject
  public AgreementMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public Agreement find(String id) {
    return super.find(Agreement.class, id);
  }

  @Override
  public Agreement save(Agreement agreement) {
    // dao.save(agreement);
    return null;
  }


  @Override
  public Agreement update(Agreement agreement) {
    // dao.update(agreement);
    return null;
  }


}
