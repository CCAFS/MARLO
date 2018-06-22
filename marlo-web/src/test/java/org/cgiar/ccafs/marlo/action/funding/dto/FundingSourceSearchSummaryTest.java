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

package org.cgiar.ccafs.marlo.action.funding.dto;

import java.math.BigInteger;
import java.util.Map;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class FundingSourceSearchSummaryTest {

  @Test
  public void testFundingSearchSummaryConvertsToMap() throws Exception {

    FundingSourceSearchSummary summary = new FundingSourceSearchSummary();

    summary.setId(new BigInteger("5"));
    summary.setUsedAmount(110000D);
    summary.setBudget(180000D);
    summary.setCanSelect(false);
    summary.setFinanceCode("ABCD1234");
    summary.setName("Test");
    summary.setType("Bilateral");
    summary.setTypeId(new BigInteger("3"));
    summary.setW1w2(false);


    assertThat(summary.convertToMap(), notNullValue());

    Map<String, Object> summaryMap = summary.convertToMap();

    assertThat(summaryMap.size(), equalTo(10));

    assertThat(summaryMap.get("name"), equalTo("Test"));

    assertThat(summaryMap.get("amount"), equalTo(70000D));


  }


  @Test
  public void testFundingSearchSummaryHandlesNullBudget() throws Exception {

    FundingSourceSearchSummary summary = new FundingSourceSearchSummary();

    summary.setId(new BigInteger("5"));
    summary.setUsedAmount(110000D);
    summary.setBudget(null);
    summary.setCanSelect(false);
    summary.setFinanceCode("ABCD1234");
    summary.setName("Test");
    summary.setType("Bilateral");
    summary.setTypeId(new BigInteger("3"));
    summary.setW1w2(false);


    assertThat(summary.convertToMap(), notNullValue());

    Map<String, Object> summaryMap = summary.convertToMap();

    assertThat(summaryMap.get("amount"), equalTo(-110000D));


  }

  @Test
  public void testFundingSearchSummaryHandlesNullBudgetAndNullUsedAmount() throws Exception {

    FundingSourceSearchSummary summary = new FundingSourceSearchSummary();

    summary.setId(new BigInteger("5"));
    summary.setUsedAmount(null);
    summary.setBudget(null);
    summary.setCanSelect(false);
    summary.setFinanceCode("ABCD1234");
    summary.setName("Test");
    summary.setType("Bilateral");
    summary.setTypeId(new BigInteger("3"));
    summary.setW1w2(false);


    assertThat(summary.convertToMap(), notNullValue());

    Map<String, Object> summaryMap = summary.convertToMap();

    assertThat(summaryMap.get("amount"), equalTo(0.0D));


  }

  @Test
  public void testFundingSearchSummaryHandlesNullUsedAmount() throws Exception {

    FundingSourceSearchSummary summary = new FundingSourceSearchSummary();

    summary.setId(new BigInteger("5"));
    summary.setUsedAmount(null);
    summary.setBudget(50000D);
    summary.setCanSelect(false);
    summary.setFinanceCode("ABCD1234");
    summary.setName("Test");
    summary.setType("Bilateral");
    summary.setTypeId(new BigInteger("3"));
    summary.setW1w2(false);


    assertThat(summary.convertToMap(), notNullValue());

    Map<String, Object> summaryMap = summary.convertToMap();

    assertThat(summaryMap.get("amount"), equalTo(50000D));

  }

}
