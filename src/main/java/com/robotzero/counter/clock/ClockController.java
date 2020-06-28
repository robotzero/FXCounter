package com.robotzero.counter.clock;

import com.google.common.collect.ImmutableSet;
import com.robotzero.counter.domain.CellState;
import com.robotzero.counter.domain.CellStatePosition;
import com.robotzero.counter.domain.ColumnStateFactory;
import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Tick;
import com.robotzero.counter.domain.TimerType;
import com.robotzero.counter.event.ButtonState;
import com.robotzero.counter.event.ButtonType;
import com.robotzero.counter.event.ClickEvent;
import com.robotzero.counter.event.CurrentViewState;
import com.robotzero.counter.event.InitEventType;
import com.robotzero.counter.event.InitViewEvent;
import com.robotzero.counter.event.MainViewEvent;
import com.robotzero.counter.event.ScrollEvent;
import com.robotzero.counter.event.TickEvent;
import com.robotzero.counter.event.action.Action;
import com.robotzero.counter.event.action.ActionType;
import com.robotzero.counter.event.action.ClickAction;
import com.robotzero.counter.event.action.InitViewAction;
import com.robotzero.counter.event.action.TickAction;
import com.robotzero.counter.event.result.ClickResult;
import com.robotzero.counter.event.result.CurrentViewData;
import com.robotzero.counter.event.result.InitViewResult;
import com.robotzero.counter.event.result.Result;
import com.robotzero.counter.event.result.TickResult;
import com.robotzero.counter.service.CellService;
import com.robotzero.counter.service.ClockService;
import com.robotzero.counter.service.ResetService;
import com.robotzero.counter.service.TimerService;
import com.robotzero.counter.view.CellsFactory;
import com.robotzero.counter.view.Column;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import java.io.IOException;
import java.net.URL;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@FxmlView("clock.fxml")
public class ClockController implements Initializable {
  @FXML
  GridPane gridPane;

  @FXML
  Button startButton, resetButton;

  @FXML
  StackPane seconds, minutes, hours;

  private final TimerService timerService;

  private final CellService cellStateService;

  private final ClockService clockService;

  private final ResetService resetService;

  private Map<ColumnType, Column> viewCells;

  public ClockController(
    final TimerService timerService,
    final CellService cellStateService,
    final ClockService clockService,
    final ResetService resetService
  ) {
    this.timerService = timerService;
    this.cellStateService = cellStateService;
    this.clockService = clockService;
    this.resetService = resetService;
  }

  private BooleanProperty timerMute = new SimpleBooleanProperty(true);

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    timerMute.bind(startButton.armedProperty());

    final var startClickEvent = JavaFxObservable
      .eventsOf(startButton, MouseEvent.MOUSE_CLICKED)
      .observeOn(Schedulers.computation())
      .map(ignored -> new ClickEvent(ButtonType.LEFT, ButtonState.valueOf(startButton.getText().toUpperCase())));

    final var resetClickEvent = JavaFxObservable
      .eventsOf(resetButton, MouseEvent.MOUSE_CLICKED)
      .observeOn(Schedulers.computation())
      .map(ignored -> new ClickEvent(ButtonType.RIGHT, ButtonState.valueOf(resetButton.getText().toUpperCase())));

    final var scrollEvent = JavaFxObservable
      .eventsOf(seconds, javafx.scene.input.ScrollEvent.SCROLL)
      .mergeWith(JavaFxObservable.eventsOf(minutes, javafx.scene.input.ScrollEvent.SCROLL))
      .mergeWith(JavaFxObservable.eventsOf(hours, javafx.scene.input.ScrollEvent.SCROLL))
      .observeOn(Schedulers.computation())
      .filter(event -> event.getDeltaY() != 0L)
      .throttleFirst(200, TimeUnit.MILLISECONDS)
      .map(
        event -> {
          return new ScrollEvent(((Node) event.getSource()).getId(), event.getDeltaY());
        }
      );

    final var initViewEvent = JavaFxObservable
      .eventsOf(gridPane, EventType.ROOT)
      .filter(c -> c.getEventType().getName().equals(InitEventType.SHOW.getName()) || c.getEventType().getName().equals(InitEventType.RESET.getName()))
      .filter(event -> !event.isConsumed())
      .map(event -> {
        event.consume();
        return new InitViewEvent();
      });

    final var tickEvent = Observable
      .interval(1, TimeUnit.SECONDS)
      .filter(tick -> timerService.resumed().get())
      .map(tick -> timerService.elapsedTime().addAndGet(1000))
      .map(elapsedTime -> new TickEvent(elapsedTime));

    Observable<MainViewEvent> mainViewEvents = Observable.merge(
      Observable.merge(startClickEvent, resetClickEvent, scrollEvent, tickEvent),
      initViewEvent
    );

    ObservableTransformer<ClickEvent, Action> clickEventTransformer = clickEvent ->
      clickEvent.concatMap(
        event -> {
          return resetService.getActions(event.getButtonType(), event.getButtonState(), gridPane);
        }
      );

    ObservableTransformer<ScrollEvent, Action> scrollEventTransformer = scrollMouseEvent ->
      scrollMouseEvent.concatMap(
        event -> {
          return Observable.just(
            new TickAction(
              event.getDelta(),
              Set.of(new Tick(event.getColumnType(), event.getChronoUnit(), event.getChronoField())),
              TimerType.SCROLL
            )
          );
        }
      );

    ObservableTransformer<TickEvent, TickAction> tickEventTransformer = tickEv ->
      tickEv.flatMap(
        event -> {
          return Observable.just(
            new TickAction(
              -1,
              Set.of(new Tick(ColumnType.SECONDS, ChronoUnit.SECONDS, ChronoField.SECOND_OF_MINUTE)),
              TimerType.TICK
            )
          );
        }
      );

    ObservableTransformer<InitViewEvent, InitViewAction> initViewEventTransformer = initView ->
      initView.concatMap(
        event -> {
          return Observable.just(new InitViewAction());
        }
      );

    ObservableTransformer<MainViewEvent, Action> actionTransformer = mainViewEvent ->
      mainViewEvent.publish(
        sharedObservable -> {
          return Observable.merge(
            Observable.merge(
              sharedObservable.ofType(ClickEvent.class).compose(clickEventTransformer),
              sharedObservable.ofType(ScrollEvent.class).compose(scrollEventTransformer),
              sharedObservable.ofType(TickEvent.class).compose(tickEventTransformer)
            ),
            sharedObservable.ofType(InitViewEvent.class).compose(initViewEventTransformer)
          );
        }
      );

    ObservableTransformer<InitViewAction, InitViewResult> initViewActionTransformer = initViewAction ->
      initViewAction.concatMap(
        action -> {
          return Observable.fromFuture(
            CompletableFuture
              .runAsync(
                () -> {
                  this.clockService.initializeTime();
                },
                new SimpleAsyncTaskExecutor()
              )
              .thenApplyAsync(
                ignored -> {
                  Map<ColumnType, ImmutableSet<Integer>> initialValues = this.clockService.initializeLabels();
                  return new InitViewResult(initialValues);
                },
                new SimpleAsyncTaskExecutor()
              )
          );
        }
      );

    ObservableTransformer<ClickAction, ClickResult> clickActionTransformer = clickAction ->
      clickAction
        .concatMap(
          action -> {
            return timerService.operateTimer(action);
          }
        )
        .map(response -> new ClickResult(response.getActionType(), response.getNewButtonState()));

    ObservableTransformer<TickAction, TickResult> tickActionTransformer = tickAction -> {
      return tickAction.flatMap(
        action -> {
          return clockService
            .tick(action)
            .flatMap(
              currentClockState -> {
                return Observable.just(new TickResult(currentClockState, action.getTimerType()));
              }
            );
        }
      );
    };

    Observable<Action> actions = mainViewEvents.compose(actionTransformer);
    Observable<Result> results = actions.publish(
      action -> {
        return Observable.merge(
          action.ofType(ClickAction.class).compose(clickActionTransformer),
          action.ofType(TickAction.class).compose(tickActionTransformer),
          action.ofType(InitViewAction.class).compose(initViewActionTransformer)
        );
      }
    );

    Observable<CurrentViewState> uiModels = results.scan(
      CurrentViewState.idle(),
      (startingState, intermediateState) -> {
        if (intermediateState.getClass().equals(ClickResult.class)) {
          ClickResult clickResult = (ClickResult) intermediateState;
          if (clickResult.getActionType().equals(ActionType.LEFT)) {
            if (clickResult.getButtonState().equals(ButtonState.START)) {
              return CurrentViewState.pause(new CurrentViewData(clickResult));
            }

            if (clickResult.getButtonState().equals(ButtonState.PAUSE)) {
              return CurrentViewState.start(new CurrentViewData(clickResult));
            }

            if (clickResult.getButtonState().equals(ButtonState.STOP)) {
              return CurrentViewState.stop(new CurrentViewData(clickResult));
            }
          }

          if (clickResult.getActionType().equals(ActionType.RIGHT)) {
            return CurrentViewState.reset(new CurrentViewData(clickResult));
          }
        }

        if (intermediateState.getClass().equals(TickResult.class)) {
          TickResult tickResult = (TickResult) intermediateState;
          return CurrentViewState.tick(new CurrentViewData(tickResult));
        }

        if (intermediateState.getClass().equals(InitViewResult.class)) {
          InitViewResult initViewResult = (InitViewResult) intermediateState;
          return CurrentViewState.init(new CurrentViewData(initViewResult));
        }

        return CurrentViewState.idle();
      }
    );

    uiModels
      .observeOn(JavaFxScheduler.platform())
      .subscribe(
        currentViewState -> {
          Optional
            .of(currentViewState)
            .filter(CurrentViewState::isClick)
            .ifPresent(
              state -> {
                startButton
                  .textProperty()
                  .setValue(((ClickResult) state.getData().getResult()).getButtonState().getDescription());
                Optional
                  .of(state)
                  .filter(CurrentViewState::isStart)
                  .ifPresent(
                    s -> {
                      resetButton.disableProperty().setValue(true);
                    }
                  );
                Optional
                  .of(state)
                  .filter(CurrentViewState::isPause)
                  .ifPresent(
                    s -> {
                      resetButton.disableProperty().setValue(false);
                    }
                  );
                Optional
                  .of(state)
                  .filter(CurrentViewState::isStop)
                  .ifPresent(
                    s -> {
                      resetButton.disableProperty().setValue(false);
                    }
                  );
                Optional
                  .of(state)
                  .filter(CurrentViewState::isReset)
                  .ifPresent(
                    s -> {
                      startButton.textProperty().setValue("Start");
                      resetButton.disableProperty().setValue(false);
                    }
                  );
              }
            );

          Optional
            .of(currentViewState)
            .filter(CurrentViewState::isTick)
            .ifPresent(
              state -> {
                TickResult tickResult = (TickResult) currentViewState.getData().getResult();
                List<CellState> cellStates = tickResult.getLabels().getCellStates();
                cellStates.forEach(
                  cellState -> {
                    int vboxId = cellState.getId();
                    this.viewCells.entrySet()
                      .stream()
                      .filter(
                        columnTypeColumnEntry -> {
                          return columnTypeColumnEntry.getValue().getCells().get(cellState.getId()) != null;
                        }
                      )
                      .findFirst()
                      .ifPresent(
                        columnTypeColumnEntry -> {
                          final var column = columnTypeColumnEntry.getValue();
                          Optional
                            .of(cellState.getCellStatePosition())
                            .filter(cellStatePosition -> cellStatePosition == CellStatePosition.CHANGEABLE)
                            .ifPresent(
                              cellStatePosition -> {
                                column.setLabel(vboxId, cellState.getTimerValue());
                              }
                            );
                          column.play(tickResult.getDuration(), cellState);
                        }
                      );
                  }
                );
              }
            );

          Optional
            .of(currentViewState)
            .filter(CurrentViewState::isInit)
            .ifPresent(
              state -> {
                InitViewResult initResult = (InitViewResult) currentViewState.getData().getResult();
                CellsFactory.build();
                this.viewCells = CellsFactory.build();
                this.cellStateService.initialize(ColumnStateFactory.build());
                initResult
                  .getInitialValues()
                  .forEach(
                    (columnType, timeValuesForEachCell) -> {
                      final var iterator = timeValuesForEachCell.iterator();
                      this.viewCells.get(columnType)
                        .getCells()
                        .forEach(
                          (cellId_ignored, cell) -> {
                            if (iterator.hasNext()) {
                              cell.setLabel(iterator.next());
                            }
                          }
                        );
                    }
                  );
              }
            );
        },
        error -> {
          System.out.println(error.getMessage());
          throw new IOException("CRASHING THE APP...");
        }
      );
  }
}
