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

import org.cgiar.ccafs.marlo.rest.services.deliverables.model.MetadataModel;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MetadataClientApi {

  /**
   * Extract parameters from a given URL
   * 
   * @param url
   * @return Map<key,List<values>>
   */
  public static Map<String, List<String>> getQueryParams(String url) {
    try {
      Map<String, List<String>> params = new HashMap<String, List<String>>();
      String[] urlParts = url.split("\\?");
      if (urlParts.length > 1) {
        String query = urlParts[1];
        for (String param : query.split("&")) {
          String[] pair = param.split("=");
          String key = URLDecoder.decode(pair[0], "UTF-8");
          String value = "";
          if (pair.length > 1) {
            value = URLDecoder.decode(pair[1], "UTF-8");
          }
          List<String> values = params.get(key);
          if (values == null) {
            values = new ArrayList<String>();
            params.put(key, values);
          }
          values.add(value);
        }
      }
      return params;
    } catch (UnsupportedEncodingException ex) {
      throw new AssertionError(ex);
    }
  }

  private String id;


  public String getId() {
    return id;
  }

  public MetadataModel getMetadata(String link) {
    return null;
  }

  public String parseLink(String link) {
    return link;
  }

  public void setId(String id) {
    this.id = id;
  }


}