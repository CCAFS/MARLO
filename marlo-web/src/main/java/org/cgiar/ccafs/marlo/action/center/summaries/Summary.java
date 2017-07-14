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
package org.cgiar.ccafs.marlo.action.center.summaries;

import java.io.InputStream;


/**
 * This interface has to be implemented for all the summary actions that will return a file (PDF, XLS, CSV) for a
 * specific report.
 * 
 * @author Hernán David Carvajal
 * @author Héctor Fabio Tobón R. CCAFS/CIAT
 */


public interface Summary {


  /**
   * This method should return the size of the file
   * to be generated.
   * 
   * @return the number of bytes of the file to return.
   */
  public int getContentLength();

  /**
   * This method should return the content type for the file that is going to be returned.
   * 
   * @return a String representing the content type. E.g. for CSV would be "text/csv".
   */
  public String getContentType();

  /**
   * This method should return the name of the file
   * to return.
   * 
   * @return a String with the file name.
   */
  public String getFileName();

  /**
   * This method should return the content of the pdf file.
   * 
   * @return an InputStream object with the content of the file.
   */
  public InputStream getInputStream();
}
