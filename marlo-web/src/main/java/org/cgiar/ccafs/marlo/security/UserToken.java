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

package org.cgiar.ccafs.marlo.security;

import org.cgiar.ccafs.marlo.data.model.User;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class UserToken {

  private User user;

  private String section;


  public UserToken() {
    // TODO Auto-generated constructor stub
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    UserToken other = (UserToken) obj;
    if (section == null) {
      if (other.section != null) {
        return false;
      }
    } else if (!section.equals(other.section)) {
      return false;
    }
    if (user == null) {
      if (other.user != null) {
        return false;
      }
    } else if (!user.equals(other.user)) {
      return false;
    }
    return true;
  }


  public String getSection() {
    return section;
  }

  public User getUser() {
    return user;
  }

  public void setSection(String section) {
    this.section = section;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
