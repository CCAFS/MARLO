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

import org.cgiar.ccafs.marlo.data.dao.ButtonGuideContentDAO;
import org.cgiar.ccafs.marlo.data.model.ButtonGuideContent;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ButtonGuideContentMySQLDAO extends AbstractMarloDAO<ButtonGuideContent, Long>
  implements ButtonGuideContentDAO {


  @Inject
  public ButtonGuideContentMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteButtonGuideContent(long buttonGuideContentId) {
    ButtonGuideContent buttonGuideContent = this.find(buttonGuideContentId);
    this.delete(buttonGuideContent);
  }

  @Override
  public boolean existButtonGuideContent(long buttonGuideContentID) {
    ButtonGuideContent buttonGuideContent = this.find(buttonGuideContentID);
    if (buttonGuideContent == null) {
      return false;
    }
    return true;

  }

  @Override
  public ButtonGuideContent find(long id) {
    return super.find(ButtonGuideContent.class, id);

  }


  @Override
  public List<ButtonGuideContent> findAll() {
    String query = "from " + ButtonGuideContent.class.getName();
    List<ButtonGuideContent> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ButtonGuideContent getButtonGuideContentByIdentifier(String identifier) {
    ButtonGuideContent buttonGuideContent = null;
    if (identifier != null) {
      String query = "from " + ButtonGuideContent.class.getName() + " where identifier='" + identifier + "'";
      List<ButtonGuideContent> results = super.findAll(query);

      if (!results.isEmpty()) {
        buttonGuideContent = results.get(0);
      }
    }
    return buttonGuideContent;
  }

  @Override
  public ButtonGuideContent save(ButtonGuideContent buttonGuideContent) {
    if (buttonGuideContent.getId() == null) {
      super.saveEntity(buttonGuideContent);
    } else {
      buttonGuideContent = super.update(buttonGuideContent);
    }


    return buttonGuideContent;
  }


}