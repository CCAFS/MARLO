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
import org.dom4j.Element;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CGSpaceClientAPI extends MetadataClientApi {

  private static final Logger LOG = LoggerFactory.getLogger(CGSpaceClientAPI.class);
  private final String CGSPACE_HANDLE = "https://cgspace.cgiar.org/rest/handle/{0}";
  private final String HANDLE_URL = "http://hdl.handle.net/";
  private final String CGSPACE_URL = "https://cgspace.cgiar.org/handle/";
  private final String REST_URL = "https://cgspace.cgiar.org/rest/items/{0}/metadata";
  private RestConnectionUtil xmlReaderConnectionUtil;
  private Map<String, String> coverterAtrributes;

  public CGSpaceClientAPI() {
    xmlReaderConnectionUtil = new RestConnectionUtil();
    coverterAtrributes = new HashMap<String, String>();
    coverterAtrributes.put("description.abstract", "description");
    coverterAtrributes.put("date.issued", "publicationDate");
    coverterAtrributes.put("language.iso", "language");
    coverterAtrributes.put("subject", "keywords");
    coverterAtrributes.put("identifier.citation", "citation");
    coverterAtrributes.put("identifier.uri", "handle");
    coverterAtrributes.put("identifier.doi", "doi");
  }

  @Override
  public MetadataModel getMetadata(String link) {
    MetadataModel metadataModel = null;
    JSONObject jo = new JSONObject();
    try {
      Element metadata = xmlReaderConnectionUtil.getXmlRestClient(link);
      List<Author> authors = new ArrayList<Author>();
      List<Element> elements = metadata.elements();
      for (Element element : elements) {
        Element key = element.element("key");
        Element value = element.element("value");
        String keyValue = key.getStringValue();
        keyValue = keyValue.substring(3);
        if (keyValue.equals("contributor.author")) {
          Author author = new Author(value.getStringValue());
          String names[] = author.getFirstName().split(", ");
          if (names.length == 2) {
            author.setFirstName(names[1]);
            author.setLastName(names[0]);
          }
          authors.add(author);
        } else {
          if (keyValue.equals("identifier.status")) {
            if (value.getStringValue().equals("Open Access")) {
              jo.put("openAccess", "true");
            } else {
              jo.put("openAccess", "false");
            }
          }
          if (jo.has(keyValue)) {
            jo.put(keyValue, jo.get(keyValue) + "," + value.getStringValue());
          } else {
            jo.put(keyValue, value.getStringValue());
          }
        }


      }


      GsonBuilder gsonBuilder = new GsonBuilder();
      gsonBuilder.registerTypeAdapter(Date.class, new DateTypeAdapter());
      Gson gson = gsonBuilder.create();
      String data = jo.toString();
      for (String key : coverterAtrributes.keySet()) {
        data = data.replace(key, coverterAtrributes.get(key));
      }
      metadataModel = gson.fromJson(data, MetadataModel.class);
      System.out.println(metadataModel.getPublicationDate());
      Author[] authorsArr = new Author[authors.size()];
      authorsArr = authors.toArray(authorsArr);
      metadataModel.setAuthors(authorsArr);

    } catch (Exception e) {
      e.printStackTrace();
      LOG.error(e.getLocalizedMessage());

    }

    return metadataModel;

  }

  /**
   * with the link get the id and make a connection to get the Metadata id connnection and format into the rest url
   * 
   * @return the link to get the metadata
   */
  @Override
  public String parseLink(String link) {

    // if the link contains http://hdl.handle.net/ we remove it from the link
    if (link.contains(HANDLE_URL)) {
      this.setId(link.replace(HANDLE_URL, ""));
    }
    // if the link https://cgspace.cgiar.org/handle/ we remove it from the link
    if (link.contains(CGSPACE_URL)) {
      this.setId(link.replace(CGSPACE_URL, ""));
    }

    String handleUrl = CGSPACE_HANDLE.replace("{0}", this.getId());
    RestConnectionUtil connection = new RestConnectionUtil();
    Element elementHandle = connection.getXmlRestClient(handleUrl);
    this.setId(elementHandle.element("id").getStringValue());
    String linkRest = (REST_URL.replace("{0}", this.getId()));
    return linkRest;
  }
}
