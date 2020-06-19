package com.robotzero.counter.data;

public class CellWithColumnType<T, ColumnType> {
  private final T cellData;
  private final ColumnType columnType;

  public CellWithColumnType(final T cellData, final ColumnType columnType) {
    this.cellData = cellData;
    this.columnType = columnType;
  }

  public T getCell() {
    return cellData;
  }

  public ColumnType getColumnType() {
    return columnType;
  }
}
