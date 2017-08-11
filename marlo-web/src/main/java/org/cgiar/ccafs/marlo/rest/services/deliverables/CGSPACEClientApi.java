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

import org.cgiar.ccafs.marlo.utils.RestConnectionUtil;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.json.JSONObject;

public class CGSPACEClientApi extends MetadataClientApi {

  private final String CGSPACEHANDLE = "https://cgspace.cgiar.org/rest/handle/{0}";


  private final String HANDELEURL = "http://hdl.handle.net/";
  private final String CGSPACEURL = "https://cgspace.cgiar.org/handle/";


  private RestConnectionUtil xmlReaderConnectionUtil = new RestConnectionUtil();
  private final String RESTURL = "https://cgspace.cgiar.org/rest/items/{0}/metadata";

  public CGSPACEClientApi() {
    this.setLink(RESTURL);
  }

  @Override
  public JSONObject getMetadata(String link) {
    JSONObject jo = new JSONObject();
    try {
      Element metadata = xmlReaderConnectionUtil.getXmlRestClient(this.getLink());
      List<String> authors = new ArrayList<String>();
      List<Element> elements = metadata.elements();
      for (Element element : elements) {
        Element key = element.element("key");
        Element value = element.element("value");
        String keyValue = key.getStringValue();
        keyValue = keyValue.substring(3);
        if (keyValue.equals("contributor.author")) {
          authors.add(value.getStringValue());
        } else {
          if (jo.has(keyValue)) {
            jo.put(keyValue, jo.get(keyValue) + "," + value.getStringValue());
          } else {
            jo.put(keyValue, value.getStringValue());
          }
        }


      }

      jo.put("contributor.author", authors);
    } catch (Exception e) {
      e.printStackTrace();
      jo = null;
    }

    return jo;

  }

  @Override
  public String parseLink() {

    if (this.getLink().contains(HANDELEURL)) {
      this.setId(this.getLink().replace(HANDELEURL, ""));
    }
    if (this.getLink().contains(CGSPACEURL)) {
      this.setId(this.getLink().replace(CGSPACEURL, ""));
    }

    String handleUrl = CGSPACEHANDLE.replace("{0}", this.getId());
    RestConnectionUtil connection = new RestConnectionUtil();
    Element elementHandle = connection.getXmlRestClient(handleUrl);
    this.setId(elementHandle.element("id").getStringValue());
    this.setLink(RESTURL.replace("{0}", this.getId()));
    return this.getLink();
  }
}
