package com.robotzero.counter.helper;

import com.robotzero.counter.domain.ColumnState;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InitViewCellStateSorter {
  private static final InitViewComparator initViewComparator = new InitViewComparator();

  public static Iterator<Integer> sortAndGetIterator(ColumnState columnState, List<Integer> defaultCellStateSort) {
    return Optional
      .of(
        Optional
          .of(
            columnState
              .getCellStates()
              .entrySet()
              .stream()
              .sorted(initViewComparator)
              .collect(Collectors.toUnmodifiableList())
          )
          .filter(entry -> !entry.isEmpty())
          .stream()
          .flatMap(b -> b.stream())
          .map(a -> a.getKey())
          .collect(Collectors.toList())
      )
      .filter(list -> !list.isEmpty())
      .orElseGet(
        () -> {
          return defaultCellStateSort;
        }
      )
      .iterator();
  }
}
