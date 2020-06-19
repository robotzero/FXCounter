package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.DirectionType;
import java.util.Set;

public enum ChangeableState {
  FIRSTBOTTOM(
    -90d,
    Set.of(
      DirectionType.STARTDOWN,
      DirectionType.DOWN,
      DirectionType.SWITCHDOWN
    )
  ),
  FIRSTTOP(
    -90d,
    Set.of(
      DirectionType.VOID,
      DirectionType.UP,
      DirectionType.STARTUP,
      DirectionType.SWITCHUP,
      DirectionType.DOWN
    )
  ),
  LASTBOTTOM(
    270d,
    Set.of(
      DirectionType.DOWN,
      DirectionType.STARTDOWN,
      DirectionType.SWITCHDOWN
    )
  ),
  LASTTOP(
    270d,
    Set.of(
      DirectionType.VOID,
      DirectionType.STARTUP,
      DirectionType.UP,
      DirectionType.SWITCHUP
    )
  );

  private final double fromLocation;
  private final Set<DirectionType> supportedDirections;

  ChangeableState(
    final double fromLocation,
    final Set<DirectionType> supportedDirections
  ) {
    this.fromLocation = fromLocation;
    this.supportedDirections = supportedDirections;
  }

  public boolean supports(
    final double cellLocation,
    final DirectionType cellDirectionType
  ) {
    return (
      this.fromLocation == cellLocation &&
      this.supportedDirections.contains(cellDirectionType)
    );
  }
}
