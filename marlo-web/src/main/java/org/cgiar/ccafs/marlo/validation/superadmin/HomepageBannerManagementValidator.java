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

package org.cgiar.ccafs.marlo.validation.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.utils.HomepageBannerImageStore;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.io.File;
import java.util.HashMap;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

/**
 * Homepage banner rules. Note what is deliberately absent: nothing is required. A banner with no title, no
 * description and no image is a legal state — it is how an administrator hides the banner from the homepage
 * (ENH-HOMEPAGE-BANNER-001 FN-004) — so this validator only rejects content that could not be stored or rendered.
 */
@Named
public class HomepageBannerManagementValidator extends BaseValidator {

  /** Matches the width of homepage_banners.title. */
  public static final int MAX_TITLE_LENGTH = 500;

  public HomepageBannerManagementValidator() {
  }

  public boolean isTitleTooLong(String title) {
    return StringUtils.length(StringUtils.trimToEmpty(title)) > MAX_TITLE_LENGTH;
  }

  /**
   * Delegates to the store so the form refuses exactly what the filesystem would refuse, instead of keeping a second
   * copy of the whitelist that could drift from it.
   */
  public boolean isImageFormatInvalid(File image) {
    if (image == null || !image.exists()) {
      return false;
    }
    return HomepageBannerImageStore.extensionFor(image) == null;
  }

  public boolean isImageTooLarge(File image) {
    return HomepageBannerImageStore.isTooLarge(image);
  }

  public void validate(BaseAction action, String title, File image) {
    action.setInvalidFields(new HashMap<>());

    if (this.isTitleTooLong(title)) {
      action.addMessage(action.getText("homepageBannerManagement.field.title"));
      action.getInvalidFields().put("input-homepageBanner.title", InvalidFieldsMessages.WRONGVALUE);
      action.addActionError(action.getText("homepageBannerManagement.error.titleTooLong",
        new String[] {String.valueOf(MAX_TITLE_LENGTH)}));
    }

    // Size is checked before format: a file too big to accept should say so, rather than complain about a format
    // nobody asked us to read.
    if (this.isImageTooLarge(image)) {
      action.getInvalidFields().put("input-image", InvalidFieldsMessages.FILE_SIZE);
      action.addActionError(action.getText("homepageBannerManagement.error.imageTooLarge"));
    } else if (this.isImageFormatInvalid(image)) {
      action.getInvalidFields().put("input-image", InvalidFieldsMessages.INVALID_FORMAT);
      action.addActionError(action.getText("homepageBannerManagement.error.invalidImageFormat"));
    }
  }
}
