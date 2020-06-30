package com.robotzero.counter.helper;

import com.robotzero.counter.domain.CellState;
import java.util.Comparator;
import java.util.Map.Entry;

public class InitViewComparator implements Comparator<Entry<Integer, CellState>> {

  @Override
  public int compare(Entry<Integer, CellState> entry1, Entry<Integer, CellState> entry2) {
    final var fromY1 = entry1.getValue().getCurrentLocation().getFromY();
    final var fromY2 = entry2.getValue().getCurrentLocation().getFromY();
    if (fromY1 == fromY2) {
      return 0;
    }
    return fromY1 < fromY2 ? -1 : 1;
  }
}
