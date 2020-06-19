package com.robotzero.counter.event.result;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.DirectionType;
import io.reactivex.rxjava3.core.Observable;

public class ScrollResult implements Result {
  private Observable<DirectionType> direction;
  private ColumnType columnType;

  public ScrollResult(
    Observable<DirectionType> direction,
    ColumnType columnType
  ) {
    this.direction = direction;
    this.columnType = columnType;
  }

  public Observable<DirectionType> getDirection() {
    return direction;
  }

  public ColumnType getColumnType() {
    return columnType;
  }
}
