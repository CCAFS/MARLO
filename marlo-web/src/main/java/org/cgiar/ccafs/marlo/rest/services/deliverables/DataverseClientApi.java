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

import org.cgiar.ccafs.marlo.utils.XMLReaderConnectionUtil;

import org.dom4j.Element;
import org.json.JSONObject;

public class DataverseClientApi extends MetadataClientApi {

  private XMLReaderConnectionUtil xmlReaderConnectionUtil = new XMLReaderConnectionUtil();

  @Override
  public JSONObject getMetadata(String link) {
    JSONObject jo = new JSONObject();
    try {
      link =
        "https://services.dataverse.harvard.edu/miniverse/metrics/v1/datasets/by-persistent-id?key=c1580888-185f-4250-8f44-b98ca5e7b01b&persistentId=doi%3A10.7910%2FDVN%2F0ZEXKC";
      Element metadata = xmlReaderConnectionUtil.getXmlRestClient(link);
      System.out.println(metadata);
    } catch (Exception e) {
      e.printStackTrace();
      jo = null;
    }

    return jo;
  }

}
