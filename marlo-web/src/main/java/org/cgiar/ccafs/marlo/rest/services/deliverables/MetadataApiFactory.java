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


public class MetadataApiFactory {

  /**
   * Api is created depending on the page we are looking for, if you are going to add a new repository you must create
   * the class and write the methods and add it here in the factory
   * 
   * @param clientType the id of the page we want to search
   * @return the api of the page
   */
  public static MetadataClientApi getMetadataClientApi(String clientType) {
    if (clientType == null) {
      return null;
    }
    if (clientType.equalsIgnoreCase("cgspace")) {
      return new CGSpaceClientAPI();
    }
    if (clientType.equalsIgnoreCase("harvardDataverse")) {
      return new HarvardDataverseClientApi();
    }
    if (clientType.equalsIgnoreCase("ifpriEbrary")) {
      return new IFPRIEBraryClientAPI();
    }
    if (clientType.equalsIgnoreCase("ilriDatasets")) {
      return new ILRIDatasetsClientAPI();
    }
    if (clientType.equalsIgnoreCase("cimmytDataverse")) {
      return new CIMMYTDataverseClientAPI();
    }
    if (clientType.equalsIgnoreCase("cimmytDspace")) {
      return new CIMMYTDspaceClientAPI();
    }
    if (clientType.equalsIgnoreCase("melDspace")) {
      return new MELDSpaceClientAPI();
    }
    if (clientType.equalsIgnoreCase("melDataverse")) {
      return new MELDataverseClientAPI();
    }
    return null;
  }

}