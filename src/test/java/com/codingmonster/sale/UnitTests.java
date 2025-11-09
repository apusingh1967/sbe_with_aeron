package com.codingmonster.sale;

import com.codingmonster.common.sbe.trade.OrderType;
import com.codingmonster.common.sbe.trade.Side;
import java.util.*;
import org.junit.jupiter.api.Test;

public class UnitTests {

  @Test
  public void testme() {
    MatchingEngine me = new MatchingEngine();
    List<Result> results =
        me.matchNewOrder(
            new Order(123, 1233, "trader1", "AAPL", 1234, 10, Side.Sell, OrderType.Limit, 100000));
    for (Result r : results) {
      System.out.println(r);
    }
    System.out.println("**********************");
    results =
        me.matchNewOrder(
            new Order(124, 1234, "trader2", "AAPL", 1234, 10, Side.Buy, OrderType.Market, 100000));
    for (Result r : results) {
      System.out.println(r);
    }
  }
}
