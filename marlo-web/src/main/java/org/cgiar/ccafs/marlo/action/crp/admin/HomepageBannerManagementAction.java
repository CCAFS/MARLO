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

package org.cgiar.ccafs.marlo.action.crp.admin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.HomepageBannerManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.HomepageBanner;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.HomepageBannerImageStore;
import org.cgiar.ccafs.marlo.utils.HomepageBannerImageStore.StoreOutcome;
import org.cgiar.ccafs.marlo.validation.superadmin.HomepageBannerManagementValidator;

import java.io.File;
import java.util.Date;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Homepage banner content for the current Global Unit: title, description and image. Exactly one banner per Global
 * Unit, so this section is an upsert on a single row rather than a list.
 *
 * Nothing here is required. An administrator who clears all three fields hides the banner from the homepage, which is
 * the intended way to turn it off without losing the specificity as a separate lever.
 */
public class HomepageBannerManagementAction extends BaseAction {

  private static final long serialVersionUID = -8004501913061845104L;
  private static final Logger LOG = LoggerFactory.getLogger(HomepageBannerManagementAction.class);

  private final HomepageBannerManager homepageBannerManager;
  private final HomepageBannerManagementValidator validator;

  private HomepageBanner homepageBanner;

  // Populated by the file-upload interceptor from the "image" field of the multipart form.
  private File image;
  private String imageContentType;
  private String imageFileName;
  private boolean removeImage;

  @Inject
  public HomepageBannerManagementAction(APConfig config, HomepageBannerManager homepageBannerManager,
    HomepageBannerManagementValidator validator) {
    super(config);
    this.homepageBannerManager = homepageBannerManager;
    this.validator = validator;
  }

  public HomepageBanner getHomepageBanner() {
    return homepageBanner;
  }

  public File getImage() {
    return image;
  }

  public String getImageContentType() {
    return imageContentType;
  }

  public String getImageFileName() {
    return imageFileName;
  }

  public boolean isRemoveImage() {
    return removeImage;
  }

  private HomepageBannerImageStore imageStore() {
    return new HomepageBannerImageStore(config.getUploadsBaseFolder());
  }

  /**
   * Loads the stored banner so the form shows what the homepage is rendering. The params interceptor runs after this,
   * so the title and description a POST carries overwrite what was loaded, while the stored image file name — which
   * the form has no input for — survives.
   */
  @Override
  public void prepare() throws Exception {
    GlobalUnit globalUnit = this.getCurrentGlobalUnit();
    if (globalUnit != null && globalUnit.getId() != null) {
      homepageBanner = homepageBannerManager.findByGlobalUnit(globalUnit.getId());
    }
    if (homepageBanner == null) {
      homepageBanner = new HomepageBanner();
      homepageBanner.setGlobalUnit(globalUnit);
    }
  }

  @Override
  public String save() {
    if (!this.hasPermission("*")) {
      return NOT_AUTHORIZED;
    }

    GlobalUnit globalUnit = this.getCurrentGlobalUnit();
    HomepageBanner bannerToSave = homepageBannerManager.findByGlobalUnit(globalUnit.getId());
    boolean isNew = bannerToSave == null;
    if (isNew) {
      bannerToSave = new HomepageBanner();
      bannerToSave.setGlobalUnit(globalUnit);
      bannerToSave.setActive(true);
      bannerToSave.setActiveSince(new Date());
      bannerToSave.setCreatedBy(this.getCurrentUser());
    }

    bannerToSave.setTitle(StringUtils.trimToNull(homepageBanner.getTitle()));
    bannerToSave.setDescription(StringUtils.trimToNull(homepageBanner.getDescription()));

    // The image is a filesystem side effect, so it is applied here and not in validate(): when the uploads folder is
    // unusable the administrator must still keep the text they typed, with an explicit error explaining the rest.
    this.applyImageChange(bannerToSave, globalUnit.getAcronym());

    // A Global Unit that never had a banner and is saved with nothing in it needs no row at all: an absent row and an
    // all-empty row mean exactly the same thing to the homepage, so inserting one would leave a junk row behind for
    // every administrator who opens this section and presses save without typing anything.
    if (isNew && bannerToSave.isEmpty()) {
      LOG.debug("homepageBannerManagement: nothing to store for {}, no row inserted", globalUnit.getAcronym());
    } else {
      bannerToSave.setModifiedBy(this.getCurrentUser());
      bannerToSave.setModificationJustification("");
      homepageBannerManager.saveHomepageBanner(bannerToSave);
    }

    if (this.getUrl() == null || this.getUrl().isEmpty()) {
      if (!this.getActionErrors().isEmpty()) {
        // Keep the errors already added by applyImageChange; the text side did save.
        return INPUT;
      }
      this.addActionMessage("message:" + this.getText("saving.saved"));
      return SUCCESS;
    }
    this.addActionMessage("");
    this.setActionMessages(null);
    return REDIRECT;
  }

  /**
   * An upload wins over the remove checkbox: an administrator who ticks remove and also picks a file wants the new
   * image, not an empty banner.
   */
  private void applyImageChange(HomepageBanner bannerToSave, String acronym) {
    HomepageBannerImageStore store = this.imageStore();

    if (image != null && image.exists()) {
      StoreOutcome outcome = store.store(acronym, image);
      if (outcome.isStored()) {
        bannerToSave.setImageFileName(outcome.getFileName());
      } else {
        this.addActionError(this.getText(this.messageKeyFor(outcome)));
        LOG.error("homepageBannerManagement: could not store the image for {}, status {}", acronym,
          outcome.getStatus());
      }
      return;
    }

    if (removeImage && StringUtils.isNotBlank(bannerToSave.getImageFileName())) {
      if (store.delete(acronym, bannerToSave.getImageFileName())) {
        bannerToSave.setImageFileName(null);
      } else {
        this.addActionError(this.getText("homepageBannerManagement.error.imageDeleteFailed"));
      }
    }
  }

  private String messageKeyFor(StoreOutcome outcome) {
    switch (outcome.getStatus()) {
      case TOO_LARGE:
        return "homepageBannerManagement.error.imageTooLarge";
      case INVALID_FORMAT:
        return "homepageBannerManagement.error.invalidImageFormat";
      case UPLOADS_NOT_CONFIGURED:
        return "homepageBannerManagement.error.uploadsNotConfigured";
      case UPLOADS_NOT_WRITABLE:
        return "homepageBannerManagement.error.uploadsNotWritable";
      default:
        return "homepageBannerManagement.error.imageWriteFailed";
    }
  }

  public void setHomepageBanner(HomepageBanner homepageBanner) {
    this.homepageBanner = homepageBanner;
  }

  public void setImage(File image) {
    this.image = image;
  }

  public void setImageContentType(String imageContentType) {
    this.imageContentType = imageContentType;
  }

  public void setImageFileName(String imageFileName) {
    this.imageFileName = imageFileName;
  }

  public void setRemoveImage(boolean removeImage) {
    this.removeImage = removeImage;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, homepageBanner != null ? homepageBanner.getTitle() : null, image);
    }
  }
}
