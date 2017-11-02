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


package org.cgiar.ccafs.marlo.ocs.ws;

import org.cgiar.ccafs.marlo.ocs.model.AgreementOCS;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MarloOcsClient {

  public static Logger LOG = LoggerFactory.getLogger(MarloOcsClient.class);
  private static final int MYTHREADS = 4;


  private APConfig apConfig;

  @Inject
  public MarloOcsClient(APConfig apConfig) {
    this.apConfig = apConfig;

  }

  /**
   * @param agreementID the id to search
   * @return AgreementOCS object with all the info
   */
  public AgreementOCS getagreement(String agreementID) {
    AgreementOCS agreementOCS = new AgreementOCS();
    ExecutorService executor = Executors.newFixedThreadPool(MYTHREADS);
    // Run the services on parallel
    for (int i = 1; i <= 4; i++) {
      Runnable worker = new WsThread(apConfig, i, agreementID, agreementOCS);
      executor.execute(worker);
    }
    executor.shutdownNow();
    // Wait until all threads are finish
    while (!executor.isTerminated()) {
      // LOG.info("Ws OCS waiting");
    }

    if (agreementOCS.getId() == null) {
      return null;
    } else {
      System.out.println(agreementOCS.getDescription());
      return agreementOCS;
    }
  }


}
