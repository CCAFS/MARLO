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

import org.cgiar.ccafs.marlo.rest.services.deliverables.model.Author;
import org.cgiar.ccafs.marlo.rest.services.deliverables.model.MetadataModel;
import org.cgiar.ccafs.marlo.utils.RestConnectionUtil;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ILRIClientAPI extends MetadataClientApi {

  private static final Logger LOG = LoggerFactory.getLogger(ILRIClientAPI.class);
  private final String ILRI_URL = "http://data.ilri.org/portal/dataset/";

  private final String ILRI_HTTPS_URL = "https://data.ilri.org/portal/dataset/";

  private final String REST_URL = "https://data.ilri.org/portal/api/3/action/package_show?id={0}";
  private RestConnectionUtil xmlReaderConnectionUtil;


  public ILRIClientAPI() {
    xmlReaderConnectionUtil = new RestConnectionUtil();
  }

  @Override
  public MetadataModel getMetadata(String link) {
    MetadataModel metadataModel = new MetadataModel();
    JSONObject jo = new JSONObject();

    try {
      String metadata = xmlReaderConnectionUtil.getJsonRestClient(link);
      jo = new JSONObject(metadata);
      jo = jo.getJSONObject("result");
      System.out.println(jo);
      JSONArray titleJsonArray = jo.getJSONArray("resources");
      metadataModel.setDescription(jo.getString("ILRI_prjabstract"));
      metadataModel.setKeywords(jo.getString("ILRI_prjspecies"));
      for (Object object : titleJsonArray) {
        JSONObject jsonObject = (JSONObject) object;
        metadataModel.setTitle(jsonObject.getString("resource_description"));
      }
      List<Author> authors = new ArrayList<Author>();
      String authorJson = jo.getString("ILRI_prjstaff");
      String authorsJson[] = authorJson.split(", ");
      for (String string : authorsJson) {
        string = string.substring(0, string.indexOf("("));
        Author author = new Author(string);
        String names[] = author.getFirstName().split(", ");
        if (names.length == 2) {
          author.setFirstName(names[0]);
          author.setLastName(names[1]);
        }
        authors.add(author);


      }
      Author[] authorsArr = new Author[authors.size()];
      authorsArr = authors.toArray(authorsArr);
      metadataModel.setAuthors(authorsArr);
    } catch (Exception e) {
      e.printStackTrace();
      LOG.error(e.getLocalizedMessage());
      jo = null;
    }

    return metadataModel;
  }

  /**
   * with the link evaluate host extract the ido and format into the rest url
   * 
   * @return the link to get the metadata
   */
  @Override
  public String parseLink(String link) {
    // if the link contains http://ebrary.ifpri.org/cdm/singleitem/collection/ we remove it from the link
    if (link.contains(ILRI_URL)) {
      link = link.replace(ILRI_URL, "");
    }
    if (link.contains(ILRI_HTTPS_URL)) {
      link = link.replace(ILRI_HTTPS_URL, "");
    }

    this.setId(link);

    String linkRest = (REST_URL.replace("{0}", this.getId()));
    return linkRest;
  }

}
