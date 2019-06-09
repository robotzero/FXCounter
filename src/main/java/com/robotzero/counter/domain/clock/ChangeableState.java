package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.CellState;
import com.robotzero.counter.domain.DirectionType;

import java.util.ArrayDeque;
import java.util.List;
import java.util.function.Function;

public enum ChangeableState {
    FIRSTBOTTOM(-90d, List.of(DirectionType.STARTDOWN, DirectionType.DOWN, DirectionType.SWITCHDOWN)) {
        @Override
        public Function<CellState, Function<ArrayDeque<CellState>, CellState>> getChangeable() {
            return (cellState -> {
                System.out.println("WEEE1");
               return (currentCellStates) -> {
                   currentCellStates.removeLast();
                   currentCellStates.offerFirst(cellState);
                   return cellState;
               };
            });
        }
    },

    FIRSTTOP(-90d, List.of(DirectionType.VOID, DirectionType.UP, DirectionType.STARTUP, DirectionType.SWITCHUP, DirectionType.DOWN)) {
        @Override
        public Function<CellState, Function<ArrayDeque<CellState>, CellState>> getChangeable() {
            return (cellState -> {
                System.out.println("WEEE2");
                return (currentCellStates) -> {
                    return cellState;
                };
            });
        }
    },

    LASTBOTTOM(270d, List.of(DirectionType.DOWN, DirectionType.STARTDOWN, DirectionType.SWITCHDOWN)) {
        @Override
        public Function<CellState, Function<ArrayDeque<CellState>, CellState>> getChangeable() {
            return (cellState -> {
                System.out.println("WEEE3");
                return (currentCellStates) -> {
                    return cellState;
                };
            });
        }
    },

    LASTTOP(270d, List.of(DirectionType.VOID, DirectionType.STARTUP, DirectionType.UP, DirectionType.SWITCHUP)) {
        @Override
        public Function<CellState, Function<ArrayDeque<CellState>, CellState>> getChangeable() {
            return (cellState -> {
                System.out.println("WEEE4");
                return (currentCellStates) -> {
                    currentCellStates.removeFirst();
                    currentCellStates.addLast(cellState);
                    return cellState;
                };
            });
        }
    };

    private final double fromLocation;
    private final List<DirectionType> supportedDirections;

    ChangeableState(double fromLocation, List<DirectionType> supportedDirections) {
        this.fromLocation = fromLocation;
        this.supportedDirections = supportedDirections;
    }

    public double getFromLocation() {
        return fromLocation;
    }

    public List<DirectionType> getSupportedDirections() {
        return supportedDirections;
    }

    public abstract Function<CellState, Function<ArrayDeque<CellState>, CellState>> getChangeable();

    public boolean supports(double cellLocation, DirectionType cellDirectionType) {
        return this.getFromLocation() == cellLocation && this.getSupportedDirections().contains(cellDirectionType);
    }
}
