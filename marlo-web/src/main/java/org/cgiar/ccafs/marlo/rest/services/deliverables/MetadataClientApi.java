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


package org.cgiar.ccafs.marlo.rest.services.deliverables;

import org.json.JSONObject;

public abstract class MetadataClientApi {

  private String link;
  private String id;
  private JSONObject jsonObject;

  public String getId() {
    return id;
  }

  public JSONObject getJsonObject() {
    return jsonObject;
  }

  public String getLink() {
    return link;
  };

  public JSONObject getMetadata(String link) {
    return jsonObject;
  }

  public String parseLink() {
    return link;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setJsonObject(JSONObject jsonObject) {
    this.jsonObject = jsonObject;
  }

  public void setLink(String link) {
    this.link = link;
  }

}
