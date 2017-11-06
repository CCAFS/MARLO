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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.IpElementDAO;
import org.cgiar.ccafs.marlo.data.manager.IpElementManager;
import org.cgiar.ccafs.marlo.data.model.IpElement;
import org.cgiar.ccafs.marlo.data.model.IpProgram;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class IpElementManagerImpl implements IpElementManager {


  private IpElementDAO ipElementDAO;
  // Managers


  @Inject
  public IpElementManagerImpl(IpElementDAO ipElementDAO) {
    this.ipElementDAO = ipElementDAO;


  }

  @Override
  public void deleteIpElement(long ipElementId) {

    ipElementDAO.deleteIpElement(ipElementId);
  }

  @Override
  public boolean existIpElement(long ipElementID) {

    return ipElementDAO.existIpElement(ipElementID);
  }

  @Override
  public List<IpElement> findAll() {

    return ipElementDAO.findAll();

  }

  @Override
  public IpElement getIpElementById(long ipElementID) {

    return ipElementDAO.find(ipElementID);
  }

  @Override
  public List<IpElement> getIPElementListForOutcomeSynthesis(IpProgram program, long type) {
    return ipElementDAO.getIPElementListForOutcomeSynthesis(program.getId().longValue(), type);
  }

  @Override
  public List<IpElement> getIPElementListForSynthesis(IpProgram _program) {
    // List<Map<String, String>> ipElementDataList;
    List<IpElement> elementsList = new ArrayList<>();

    switch (_program.getId().intValue()) {
      case 1:
      case 2:
      case 3:
      case 4:
        elementsList = ipElementDAO.getIPElementByProgramIDSynthesis(_program.getId());
        break;

      default:
        /**
         * TODO
         */
        elementsList = ipElementDAO.getIPElementListForSynthesisRegion(_program.getCrpProgram().getId());
        break;
    }


    // for (Map<String, String> elementData : ipElementDataList) {
    // IpElement element = new IpElement();
    // element.setId(Integer.parseInt(elementData.get("id")));
    // element.setDescription(elementData.get("description"));
    //
    // // Set the program
    // IpProgram program = new IPProgram();
    // program.setId(Integer.parseInt(elementData.get("program_id")));
    // program.setAcronym(elementData.get("program_acronym"));
    // element.setProgram(program);
    //
    // // Set elements 'contributesTo' if exists
    // List<IPElement> elementsRelated = new ArrayList<>();
    // List<Map<String, String>> elementsData =
    // ipElementDAO.getIPElementsRelated(element.getId(), APConstants.ELEMENT_RELATION_CONTRIBUTION);
    // for (Map<String, String> parentData : elementsData) {
    // IPElement parentElement = new IPElement();
    // parentElement.setId(Integer.parseInt(parentData.get("id")));
    // parentElement.setDescription(parentData.get("description"));
    // IPProgram parentProgram = new IPProgram();
    // parentProgram.setId(Integer.parseInt(parentData.get("program_id")));
    // parentProgram.setAcronym(parentData.get("program_acronym"));
    // parentElement.setProgram(parentProgram);
    // IPElementType parentType = new IPElementType();
    // parentType.setId(Integer.parseInt(parentData.get("element_type_id")));
    // parentType.setName(parentData.get("element_type_name"));
    // parentElement.setType(parentType);
    // elementsRelated.add(parentElement);
    // }
    // element.setContributesTo(elementsRelated);
    //
    // // Set elements 'translatedOf' if exists
    // elementsRelated = new ArrayList<>();
    // elementsData = ipElementDAO.getIPElementsRelated(element.getId(), APConstants.ELEMENT_RELATION_TRANSLATION);
    // for (Map<String, String> parentData : elementsData) {
    // IPElement parentElement = new IPElement();
    // parentElement.setId(Integer.parseInt(parentData.get("id")));
    // parentElement.setDescription(parentData.get("description"));
    // IPProgram parentProgram = new IPProgram();
    // parentProgram.setId(Integer.parseInt(parentData.get("program_id")));
    // parentProgram.setAcronym(parentData.get("program_acronym"));
    // parentElement.setProgram(parentProgram);
    // IPElementType parentType = new IPElementType();
    // parentType.setId(Integer.parseInt(parentData.get("element_type_id")));
    // parentType.setName(parentData.get("element_type_name"));
    // parentElement.setType(parentType);
    // elementsRelated.add(parentElement);
    // }
    // element.setTranslatedOf(elementsRelated);
    //
    // // Set element types
    // IPElementType type = new IPElementType();
    // type.setId(Integer.parseInt(elementData.get("element_type_id")));
    // type.setName(elementData.get("element_type_name"));
    // element.setType(type);
    //
    // elementsList.add(element);
    // }

    return elementsList;
  }

  @Override
  public List<IpElement> getIPElementsByParent(IpElement parent, int relationTypeID) {

    return ipElementDAO.getIPElementsByParent(parent.getId().intValue(), relationTypeID);
  }

  @Override
  public List<IpElement> getIPElementsRelated(int ipElementID, int relationTypeID) {
    //
    return ipElementDAO.getIPElementsRelated(ipElementID, relationTypeID);
  }

  @Override
  public IpElement saveIpElement(IpElement ipElement) {

    return ipElementDAO.save(ipElement);
  }


}
