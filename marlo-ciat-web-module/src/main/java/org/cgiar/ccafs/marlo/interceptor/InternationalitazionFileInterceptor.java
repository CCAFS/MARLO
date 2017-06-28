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


package org.cgiar.ccafs.marlo.interceptor;

import org.cgiar.ccafs.marlo.utils.APConstants;

import java.util.Locale;
import java.util.Map;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import org.apache.struts2.ServletActionContext;

public class InternationalitazionFileInterceptor extends AbstractInterceptor {

  /**
   * @author Christian David Garcia Oviedo
   */
  private static final long serialVersionUID = -3807232981762261100L;

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {


    String language = APConstants.CUSTOM_LAGUAGE;
    String pathFile = APConstants.PATH_CUSTOM_FILES;
    Map<String, Object> session = invocation.getInvocationContext().getSession();
    if (session.containsKey(APConstants.CENTER_LANGUAGE)) {
      language = (String) session.get(APConstants.CENTER_LANGUAGE);
    }
    Locale locale = new Locale(language);
    LocalizedTextUtil.reset();
    LocalizedTextUtil.addDefaultResourceBundle(APConstants.CUSTOM_FILE);
    ServletActionContext.getContext().setLocale(locale);
    if (session.containsKey(APConstants.CENTER_CUSTOM_FILE)) {
      pathFile = pathFile + session.get(APConstants.CENTER_CUSTOM_FILE);
      LocalizedTextUtil.addDefaultResourceBundle(pathFile);
    }


    return invocation.invoke();
  }

}
