/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


package org.cgiar.ccafs.marlo.interceptor;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;

import java.util.Locale;
import java.util.Map;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternationalitazionFileInterceptor extends AbstractInterceptor {

  /**
   * 
   */
  private static final long serialVersionUID = -3807232981762261100L;
  private static final Logger LOG = LoggerFactory.getLogger(RequireUserInterceptor.class);

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {


    String language = APConstants.CUSTOM_LAGUAGE;
    String pathFile = APConstants.PATH_CUSTOM_FILES;
    Map<String, Object> session = invocation.getInvocationContext().getSession();
    if (session.containsKey(APConstants.CRP_LANGUAGE)) {
      language = (String) session.get(APConstants.CRP_LANGUAGE);
    }
    Locale locale = new Locale(language);
    LocalizedTextUtil.reset();
    LocalizedTextUtil.addDefaultResourceBundle(APConstants.CUSTOM_FILE);
    ServletActionContext.getContext().setLocale(locale);
    if (session.containsKey(APConstants.CRP_CUSTOM_FILE)) {
      pathFile = pathFile + session.get(APConstants.CRP_CUSTOM_FILE);
      LocalizedTextUtil.addDefaultResourceBundle(pathFile);
    }


    return BaseAction.INPUT;
  }

}
