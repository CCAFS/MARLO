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

package org.cgiar.ccafs.marlo.action.downloads;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.HomepageBannerManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.HomepageBanner;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.HomepageBannerImageStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serves the homepage banner image of one Global Unit. Public, like the Global Unit logo route it mirrors, and
 * deliberately without a default image: the homepage only emits the img element when a banner names a file, so a
 * request that finds nothing is a 404 rather than a placeholder.
 */
public class DownloadHomepageBannerImageAction extends BaseAction {

  private static final long serialVersionUID = 1L;
  private static final Logger LOG = LoggerFactory.getLogger(DownloadHomepageBannerImageAction.class);

  private final GlobalUnitManager globalUnitManager;
  private final HomepageBannerManager homepageBannerManager;

  private transient InputStream fileInputStream;
  private String contentType;
  private String acronym;

  @Inject
  public DownloadHomepageBannerImageAction(APConfig config, GlobalUnitManager globalUnitManager,
    HomepageBannerManager homepageBannerManager) {
    super(config);
    this.globalUnitManager = globalUnitManager;
    this.homepageBannerManager = homepageBannerManager;
  }

  @Override
  public String execute() throws Exception {
    if (StringUtils.isBlank(acronym)) {
      return ERROR;
    }

    GlobalUnit globalUnit = globalUnitManager.findGlobalUnitByAcronym(StringUtils.trim(acronym));
    if (globalUnit == null || globalUnit.getId() == null) {
      LOG.warn("homepageBannerImage: no Global Unit for acronym {}", acronym);
      return ERROR;
    }

    HomepageBanner banner = homepageBannerManager.findByGlobalUnit(globalUnit.getId());
    if (banner == null || StringUtils.isBlank(banner.getImageFileName())) {
      return ERROR;
    }

    HomepageBannerImageStore store = new HomepageBannerImageStore(config.getUploadsBaseFolder());
    File image = store.resolve(globalUnit.getAcronym(), banner.getImageFileName());
    if (image == null) {
      // The row names a file that is not on disk. That means the database and the uploads folder have drifted, which
      // is the predictable failure mode of storing images outside the database.
      LOG.warn("homepageBannerImage: {} names {} but it is not on disk", globalUnit.getAcronym(),
        banner.getImageFileName());
      return ERROR;
    }

    this.contentType = HomepageBannerImageStore.contentTypeFor(banner.getImageFileName());
    this.fileInputStream = new FileInputStream(image);
    return SUCCESS;
  }

  public String getAcronym() {
    return acronym;
  }

  public String getContentType() {
    return contentType;
  }

  public InputStream getFileInputStream() {
    return fileInputStream;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }
}
