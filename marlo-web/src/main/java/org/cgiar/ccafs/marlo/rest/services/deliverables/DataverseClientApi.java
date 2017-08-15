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
import org.cgiar.ccafs.marlo.utils.DateTypeAdapter;
import org.cgiar.ccafs.marlo.utils.RestConnectionUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataverseClientApi extends MetadataClientApi {

  private static final Logger LOG = LoggerFactory.getLogger(DataverseClientApi.class);
  private final String DATAVERSE_URL = "https://dataverse.harvard.edu/dataset.xhtml?persistentId=doi:";
  private final String DX_URL = "http://dx.doi.org/";
  private final String REST_URL =
    "https://services.dataverse.harvard.edu/miniverse/metrics/v1/datasets/by-persistent-id?key=c1580888-185f-4250-8f44-b98ca5e7b01b&persistentId=doi:{0}";
  private RestConnectionUtil xmlReaderConnectionUtil;
  private Map<String, String> coverterAtrributes;

  public DataverseClientApi() {
    xmlReaderConnectionUtil = new RestConnectionUtil();
    coverterAtrributes = new HashMap<String, String>();
  }

  /**
   * TODO implement method
   */
  @Override
  public MetadataModel getMetadata(String link) {
    MetadataModel metadataModel = null;
    JSONObject jo = new JSONObject();

    try {
      String metadata = xmlReaderConnectionUtil.getJsonRestClient(link);
      jo = new JSONObject(metadata);
      System.out.println(jo);
      JSONObject object2 = jo.getJSONObject("dsDescription");

      jo.put("description", jo.getJSONObject("dsDescription").get("dsDescriptionValue"));
      StringBuilder keywords = new StringBuilder();

      JSONArray keywordArray = jo.getJSONArray("keyword");
      for (Object object : keywordArray) {
        JSONObject jsonObject = (JSONObject) object;
        if (keywords.length() == 0) {
          keywords.append(jsonObject.get("keywordValue"));
        } else {
          keywords.append(", " + jsonObject.get("keywordValue"));
        }
      }
      jo.put("keywords", keywords.toString());
      List<Author> authors = new ArrayList<Author>();
      JSONArray authorsArray = jo.getJSONArray("author");
      for (Object object : authorsArray) {
        JSONObject jsonObject = (JSONObject) object;
        Author author = new Author(jsonObject.getString("authorName"));
        author.setOrcidId(jsonObject.getString("authorIdentifier"));
        authors.add(author);
      }
      jo.put("author", authors);
      jo.put("doi", this.getId());
      GsonBuilder gsonBuilder = new GsonBuilder();
      gsonBuilder.registerTypeAdapter(Date.class, new DateTypeAdapter());
      Gson gson = gsonBuilder.create();
      String data = jo.toString();
      for (String key : coverterAtrributes.keySet()) {
        data = data.replace(key, coverterAtrributes.get(key));
      }
      metadataModel = gson.fromJson(data, MetadataModel.class);
    } catch (Exception e) {
      e.printStackTrace();
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
    // if the link contains https://dataverse.harvard.edu/dataset.xhtml?persistentId=doi: we remove it from the link
    if (link.contains(DATAVERSE_URL)) {
      this.setId(link.replace(DATAVERSE_URL, ""));
    }
    // if the link http://dx.doi.org/ we remove it from the link
    if (link.contains(DX_URL)) {
      this.setId(link.replace(DX_URL, ""));
    }
    String linkRest = (REST_URL.replace("{0}", this.getId()));
    return linkRest;
  }

}
