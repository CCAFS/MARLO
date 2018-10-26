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

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HarvardDataverseClientApi extends DataverseClientApi {

  private static final Logger LOG = LoggerFactory.getLogger(HarvardDataverseClientApi.class);
  private final String REST_URL = "https://dataverse.harvard.edu/api/datasets/:persistentId/?persistentId={0}";

  /**
   * with the link get the id and make a connection to get the Metadata id connnection and format into the rest url
   * 
   * @return the link to get the metadata
   */
  @Override
  public String parseLink(String link) {
    Map<String, List<String>> map = CIMMYTDataverseClientAPI.getQueryParams(link);
    if (map.containsKey("persistentId")) {
      List<String> handles = map.get("persistentId");
      for (String handle : handles) {
        if (handle.contains("doi")) {
          this.setId(handle);
        }
      }
      String handleURL = REST_URL.replace("{0}", this.getId());
      return handleURL;
    }
    return null;
  }

}