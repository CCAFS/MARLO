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


import org.cgiar.ccafs.marlo.data.dao.DeliverableIntellectualAssetDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableIntellectualAssetManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableIntellectualAsset;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableIntellectualAssetManagerImpl implements DeliverableIntellectualAssetManager {


  private DeliverableIntellectualAssetDAO deliverableIntellectualAssetDAO;
  // Managers


  @Inject
  public DeliverableIntellectualAssetManagerImpl(DeliverableIntellectualAssetDAO deliverableIntellectualAssetDAO) {
    this.deliverableIntellectualAssetDAO = deliverableIntellectualAssetDAO;


  }

  @Override
  public void deleteDeliverableIntellectualAsset(long deliverableIntellectualAssetId) {

    deliverableIntellectualAssetDAO.deleteDeliverableIntellectualAsset(deliverableIntellectualAssetId);
  }

  @Override
  public boolean existDeliverableIntellectualAsset(long deliverableIntellectualAssetID) {

    return deliverableIntellectualAssetDAO.existDeliverableIntellectualAsset(deliverableIntellectualAssetID);
  }

  @Override
  public List<DeliverableIntellectualAsset> findAll() {

    return deliverableIntellectualAssetDAO.findAll();

  }

  @Override
  public DeliverableIntellectualAsset getDeliverableIntellectualAssetById(long deliverableIntellectualAssetID) {

    return deliverableIntellectualAssetDAO.find(deliverableIntellectualAssetID);
  }

  @Override
  public DeliverableIntellectualAsset saveDeliverableIntellectualAsset(DeliverableIntellectualAsset deliverableIntellectualAsset) {

    return deliverableIntellectualAssetDAO.save(deliverableIntellectualAsset);
  }


}
