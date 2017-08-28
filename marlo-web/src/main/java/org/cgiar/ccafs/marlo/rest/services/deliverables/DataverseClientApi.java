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

import com.ibm.icu.text.SimpleDateFormat;
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


  public DataverseClientApi() {
    xmlReaderConnectionUtil = new RestConnectionUtil();
  }

  @Override
  public MetadataModel getMetadata(String link) {
    MetadataModel metadataModel = new MetadataModel();
    JSONObject jo = new JSONObject();

    try {
      String metadata = xmlReaderConnectionUtil.getJsonRestClient(link);
      jo = new JSONObject(metadata);
      System.out.println(jo);
      jo = jo.getJSONObject("data");
      JSONObject citation = jo.getJSONObject("metadata_blocks").getJSONObject("citation");
      System.out.println(citation);
      metadataModel.setTitle(citation.get("title").toString());
      JSONObject timestamps = jo.getJSONObject("timestamps");
      SimpleDateFormat dt = new SimpleDateFormat("yyyyy-MM-dd hh:mm:ss");
      metadataModel.setPublicationDate(dt.parse(timestamps.get("publicationdate").toString()));
      JSONArray deJsonObject = citation.getJSONArray("dsDescription");
      for (Object object : deJsonObject) {
        JSONObject jsonObject = (JSONObject) object;
        metadataModel.setDescription(jsonObject.getString("dsDescriptionValue"));
      }
      StringBuilder keywords = new StringBuilder();
      JSONArray keywordArray = citation.getJSONArray("keyword");
      for (Object object : keywordArray) {
        JSONObject jsonObject = (JSONObject) object;
        if (keywords.length() == 0) {
          keywords.append(jsonObject.get("keywordValue"));
        } else {
          keywords.append(", " + jsonObject.get("keywordValue"));
        }
      }
      metadataModel.setKeywords(keywords.toString());
      List<Author> authors = new ArrayList<Author>();
      JSONArray authorsArray = citation.getJSONArray("author");
      for (Object object : authorsArray) {
        JSONObject jsonObject = (JSONObject) object;
        Author author = new Author(jsonObject.getString("authorName"));
        String names[] = author.getFirstName().split(", ");
        if (names.length == 2) {
          author.setFirstName(names[1]);
          author.setLastName(names[0]);
        }

        if (jsonObject.has("authorIdentifier")) {
          author.setOrcidId(jsonObject.getString("authorIdentifier"));
        }
        authors.add(author);
      }
      Author[] authorsArr = new Author[authors.size()];
      authorsArr = authors.toArray(authorsArr);
      metadataModel.setAuthors(authorsArr);
      metadataModel.setDoi(this.getId());
    } catch (Exception e) {
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
