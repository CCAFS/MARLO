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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.submissiontools;

import org.cgiar.ccafs.marlo.data.manager.SdgIndicatorsManager;
import org.cgiar.ccafs.marlo.data.manager.SdgManager;
import org.cgiar.ccafs.marlo.data.manager.SdgTargetsManager;
import org.cgiar.ccafs.marlo.data.model.Sdg;
import org.cgiar.ccafs.marlo.data.model.SdgIndicators;
import org.cgiar.ccafs.marlo.data.model.SdgTargets;
import org.cgiar.ccafs.marlo.rest.dto.SDGIndicatorDTO;
import org.cgiar.ccafs.marlo.rest.dto.SDGTargetDTO;
import org.cgiar.ccafs.marlo.rest.dto.SDGsDTO;
import org.cgiar.ccafs.marlo.rest.mappers.SdgIndicatorMapper;
import org.cgiar.ccafs.marlo.rest.mappers.SdgMapper;
import org.cgiar.ccafs.marlo.rest.mappers.SdgTargetMapper;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class SdgItem<T> {

  private SdgManager sdgManager;
  private SdgTargetsManager sdgTargetsManager;
  private SdgIndicatorsManager sdgIndicatorsManager;

  private SdgMapper sdgMapper;
  private SdgTargetMapper sdgTargetMapper;
  private SdgIndicatorMapper sdgIndicatorMapper;

  @Inject
  public SdgItem(SdgManager sdgManager, SdgTargetsManager sdgTargetsManager, SdgIndicatorsManager sdgIndicatorsManager,
    SdgMapper sdgMapper, SdgTargetMapper sdgTargetMapper, SdgIndicatorMapper sdgIndicatorMapper) {
    super();
    this.sdgManager = sdgManager;
    this.sdgTargetsManager = sdgTargetsManager;
    this.sdgIndicatorsManager = sdgIndicatorsManager;
    this.sdgMapper = sdgMapper;
    this.sdgTargetMapper = sdgTargetMapper;
    this.sdgIndicatorMapper = sdgIndicatorMapper;
  }

  public List<SDGIndicatorDTO> getAllSDGIndicators() {
    List<SdgIndicators> sdgIndicatorList = sdgIndicatorsManager.findAll();
    if (sdgIndicatorsManager.findAll() != null) {
      List<SDGIndicatorDTO> sdgIndicatorDTOs =
        sdgIndicatorList.stream().map(sdgEntity -> this.sdgIndicatorMapper.sdgIndicatorToSDGIndicatorDTO(sdgEntity))
          .collect(Collectors.toList());
      return sdgIndicatorDTOs;
    } else {
      return null;
    }
  }

  public List<SDGsDTO> getAllSDGs() {
    List<Sdg> sdgList = sdgManager.findAll();
    if (sdgManager.findAll() != null) {
      List<SDGsDTO> sdgDTO =
        sdgList.stream().map(sdgEntity -> this.sdgMapper.sdgToSdgsDTO(sdgEntity)).collect(Collectors.toList());
      return sdgDTO;
    } else {
      return null;
    }
  }

  public List<SDGTargetDTO> getAllSDGTargets() {
    List<SdgTargets> sdgTargetList = sdgTargetsManager.findAll();
    if (sdgTargetsManager.findAll() != null) {
      List<SDGTargetDTO> SDGTargetDTOs = sdgTargetList.stream()
        .map(sdgEntity -> this.sdgTargetMapper.sdgTargetToSDGTargetDTO(sdgEntity)).collect(Collectors.toList());
      return SDGTargetDTOs;
    } else {
      return null;
    }
  }
}
