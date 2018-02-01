package com.robotzero.counter.clock;

import com.robotzero.counter.domain.Column;
import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.domain.clock.Clocks;
import com.robotzero.counter.event.ButtonType;
import com.robotzero.counter.event.ScrollEvent;
import com.robotzero.counter.event.action.ActionType;
import com.robotzero.counter.event.ClickEvent;
import com.robotzero.counter.event.SubmitEvent;
import com.robotzero.counter.event.action.ClickAction;
import com.robotzero.counter.event.action.ScrollAction;
import com.robotzero.counter.event.result.ClickResult;
import com.robotzero.counter.event.result.Result;
import com.robotzero.counter.event.result.ScrollResult;
import com.robotzero.counter.service.DirectionService;
import com.robotzero.counter.service.Populator;
import com.robotzero.counter.service.StageController;
import com.robotzero.counter.service.TimerService;
import io.reactivex.*;
import io.reactivex.functions.Action;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.subjects.Subject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.reactfx.EventSource;
import org.reactfx.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class ClockPresenter implements Initializable {

    @FXML
    GridPane gridPane;

    @FXML
    Button startButton, resetButton;

    @FXML
    StackPane seconds;
//
//    @FXML
//    StackPane paneMinutes;
//
//    @FXML
//    StackPane paneHours;

//    @FXML
//    Text textSeconds;
//
//    @FXML
//    Text textMinutes;
//
//    @FXML
//    Text textHours;
//
//    @FXML
//    Label optionsLabel;

    @Autowired
    private Populator populator;

    @Autowired
    private Clocks clocks;

    @Autowired
    private TimerService timerService;
//
//    @Autowired
//    private ClockRepository savedTimerRepository;

    // Temp options simulating options screen
    @Autowired
    private BooleanProperty fetchFromDatabase;

    @Autowired
    @Qualifier("PlayMinutes")
    private EventSource<Void> playMinutes;

    @Autowired
    @Qualifier("PlayHours")
    private EventSource<Void> playHours;

    @Autowired
    @Qualifier("StopCountdown")
    private EventSource<Void> stopCountdown;

    @Autowired
    @Qualifier("DeltaStreamSeconds")
    private Subject<Direction> deltaStreamSeconds;

    @Autowired
    @Qualifier("DeltaStreamMinutes")
    private Subject<Direction> deltaStreamMinutes;

    @Autowired
    @Qualifier("DeltaStreamHours")
    private Subject<Direction> deltaStreamHours;

    @Autowired
    private StageController stageController;

    @Autowired
    private DirectionService directionService;

    private BooleanProperty scrollMuteProperty = new SimpleBooleanProperty(false);

    private Subscription subscribe;

    private Map<ColumnType, Column> timerColumns;

    private BooleanProperty timerMute = new SimpleBooleanProperty(true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        startButton.textProperty().bind(new When(scrollMuteProperty.isEqualTo(new SimpleBooleanProperty(false))).then("Start").otherwise("Pause"));
        timerMute.bind(startButton.armedProperty());
        this.timerColumns = this.populator.timerColumns(this.gridPane);

//        Observable<MouseEvent> mouseClickObservable = JavaFxObservable.eventsOf(startButton, MouseEvent.MOUSE_CLICKED);
//        Observable<Long> timerObservable = Observable.interval(1, 1, TimeUnit.SECONDS).skipWhile(time -> timerMute.get());
//        JavaFxObservable.valuesOf(startButton.textProperty()).startWith(startButton.textProperty().get()).skipUntil(mouseClickObservable).subscribe(
//                (val) -> System.out.println("RECEIVED"),
//                (error) -> System.out.println("ERRR"),
//                () -> System.out.println("COMPLETED")
//        );

//        Observable.create(e -> {
//           e.onNext("blah");
//           e.onComplete();
//           e.onError(new IOException());
//           e.setCancellable(() -> System.out.printf(""));
//        }).subscribe();

//        Observable.create(observable -> {
//            observable.onNext(startButton);
//            observable.onComplete();
//        }).replay().skipUntil(timerObservable).
//                subscribe(
//                (val) -> System.out.println("RECEIVED"),
//                (error) -> System.out.println("ERRR"),
//                () -> System.out.println("COMPLETED")
//        );
//        Observable.just(startButton.textProperty())
//                .skipWhile(stringProperty -> !stringProperty.get().equals("Pause"))
////                .doOnEach(textProperty -> {
////                    System.out.println("BLAH");
////                    Optional.ofNullable(textProperty.getValue()).ifPresent(stringProperty -> {
////                        startButton.setText("PAUSE");
////                        stringProperty.setValue("Pause");
////                    });
//                .subscribe(
//                        (textProperty) -> {
//                    System.out.println("BLAH");
////                    startButton.setText("Pause");
////                    textProperty.setValue("Pause");
//                },
//                        (error) -> System.out.println("ERROR"),
//                        () -> System.out.println("COMBPL"));

        Consumer<Long> timerOnNext = value -> {
//            this.timerColumns.get(ColumnType.SECONDS).play();
        };
        Consumer<Notification> timerDoOnEach = value -> this.deltaStreamSeconds.onNext(Direction.DOWN);
        Consumer<Throwable> timerDoOnError = System.out::println;
        Action timerOnComplete = () -> System.out.println("Completed");

//        Disposable disposable = mouseClickObservable.switchMap(mouseEvent -> {
//            return timerObservable;
//        }).doOnEach(timerDoOnEach::accept).subscribe(
//                timerOnNext::accept,
//                timerDoOnError::accept,
//                timerOnComplete
//        );

//        ConnectableObservable<MouseEvent> conn  = mouseClickObservable.publish();
//        //@Todo debounce or delay here!!!
//        mouseClickObservable.window(1).switchMap(mouseEvent -> {
//           return Observable.just(startButton);
//        }).doOnEach(startButton -> {
//            conn.publish();
//            if (startButton.getValue().getText().equals("Start")) {
//                startButton.getValue().setText("Pause");
//            } else {
//                startButton.getValue().setText("Start");
//            }
//        }).subscribe();

        UserManager userManager = new UserManager();

//        EventStream<MouseEvent> startClicks = EventStreams.eventsOf(startButton, MouseEvent.MOUSE_CLICKED);
//        EventStream<MouseEvent> resetClicks = EventStreams.eventsOf(reset, MouseEvent.MOUSE_CLICKED);
//        EventStream<MouseEvent> optionClicks = EventStreams.eventsOf(optionsLabel, MouseEvent.MOUSE_CLICKED);

//        Flowable<Long> ticksReact = Flowable.interval(1, TimeUnit.SECONDS).skipWhile(time -> {
//            return scrollMuteProperty.not().get();
//        });

//        Subscription.multi(playMinutes.conditionOn(scrollMuteProperty).thenIgnoreFor(Duration.ofMillis(700)).subscribe(v -> {
//            this.deltaStreamMinutes.onNext(-60);
//            timerColumns.get(ColumnType.MINUTES).play();
//        }), playHours.conditionOn(scrollMuteProperty).thenIgnoreFor(Duration.ofMillis(700)).subscribe(v -> {
//            this.deltaStreamHours.onNext(-60);
//            timerColumns.get(ColumnType.HOURS).play();
//        }));

//        reset.disableProperty().bind(scrollMuteProperty);
//        EventStream<ScrollEvent> merged = EventStreams.merge(
//                EventStreams.eventsOf(paneSeconds, ScrollEvent.SCROLL).suppressWhen(timerColumns.get(ColumnType.SECONDS).isRunning()),
//                EventStreams.eventsOf(paneMinutes, ScrollEvent.SCROLL).suppressWhen(timerColumns.get(ColumnType.MINUTES).isRunning()),
//                EventStreams.eventsOf(paneHours, ScrollEvent.SCROLL).suppressWhen(timerColumns.get(ColumnType.HOURS).isRunning())
//        );

//        stopCountdown.subscribe(v -> {
//            System.out.println("BEEP");
//        });

        // If startButton button is clicked mute scroll event until stop button is clicked.
//        StateMachine.init(scrollMuteProperty)
//                .on(startClicks).transition((wasMuted, event) -> {
//                    if (wasMuted.get()) {
//                        scrollMuteProperty.set(false);
//                        return scrollMuteProperty;
//                    }
//
//                    if (clocks.getMainClock().compareTo(LocalTime.of(0, 0, 0)) == 0) {
//                        return scrollMuteProperty;
//                    }
//
//                    scrollMuteProperty.set(true);
//                    savedTimerRepository.create("latest", clocks.getMainClock());
//                    return scrollMuteProperty;
//                })
//                .on(stopCountdown).transition((wasMuted, event) -> {
//                    scrollMuteProperty.set(false);
//                    return scrollMuteProperty;
//                })
//                .on(merged).emit((muted, t) -> muted.get() ? Optional.empty() : Optional.of(t))
//                .toEventStream().subscribe(event -> {
//                    if (((StackPane) event.getSource()).getId().contains("Seconds")) {
//                        this.deltaStreamSeconds.onNext((int)event.getDeltaY());
//                        timerColumns.get(ColumnType.SECONDS).play();
//                    }
//
//                    if (((StackPane) event.getSource()).getId().contains("Minutes")) {
//                        this.deltaStreamMinutes.onNext((int)event.getDeltaY());
//                        timerColumns.get(ColumnType.MINUTES).play();
//                    }
//                });

//        this.subscribe = ticks.subscribe(nullEvent -> {
//            this.deltaStreamSeconds.push(-60);
//            secondsColumn.play();
//        });
//        startClicks.subscribe(click -> {
//            if (scrollMuteProperty.get()) {
//                savedTimerRepository.create("latest", clocks.getMainClock());
//            }
//        });

//        optionClicks.subscribe(click -> stageController.setView());

//        resetClicks.subscribe(click -> {
//            this.fetchFromDatabase.setValue(true);
//            if (fetchFromDatabase.get()) {
//                this.clocks.initializeClocks(Optional.ofNullable(savedTimerRepository.selectLatest()).orElseGet(() -> {
////                this.clocks.initializeClocks(Optional.ofNullable(savedTimerRepository.selectLatest()).orElseGet(() -> {
//                    Clock savedTimer = new Clock();
//                    savedTimer.setSavedTimer(LocalTime.of(0, 0, 0));
//                    return savedTimer;
//                }).getSavedTimer());
//            } else {
//                this.clocks.initializeClocks(LocalTime.of(0, 0, 0));
//            }
//            this.deltaStreamSeconds.onNext(0);
//            this.deltaStreamMinutes.onNext(0);
//            this.deltaStreamHours.onNext(0);
//            timerColumns.forEach((columnType, column) -> {
//                column.setLabels();
//            });
//            timerColumns.get(ColumnType.SECONDS).setLabels();
//        });

        Observable<ClickEvent> startClickEvent = JavaFxObservable.eventsOf(startButton, MouseEvent.MOUSE_CLICKED)
                .map(ignored -> new ClickEvent(ButtonType.START));

        Observable<ClickEvent> resetClickEvent = JavaFxObservable.eventsOf(resetButton, MouseEvent.MOUSE_CLICKED)
                .map(ignored -> new ClickEvent(ButtonType.RESET));

        Observable<ScrollEvent> scrolls = JavaFxObservable.eventsOf(seconds, javafx.scene.input.ScrollEvent.SCROLL)
                .map(scrollEvent -> new com.robotzero.counter.event.ScrollEvent(ColumnType.SECONDS, scrollEvent.getDeltaY()));

        Observable<SubmitEvent> events = Observable.merge(startClickEvent, resetClickEvent, scrolls);

        ObservableTransformer<ClickEvent, com.robotzero.counter.event.action.Action> clickTransformer = e -> e.flatMap(
          clickEvent -> {
              return Observable.just(new ClickAction(ActionType.valueOf(clickEvent.getButtonType().descripton().toUpperCase())));
          });

        ObservableTransformer<ScrollEvent, com.robotzero.counter.event.action.Action> scrollTransformer = e -> e.flatMap(
                scrollEvent -> {
                    return Observable.just(new ScrollAction(directionService.calculateDirection(scrollEvent.getDelta()), scrollEvent.getColumnType()));
                });


        ObservableTransformer<SubmitEvent, com.robotzero.counter.event.action.Action> actions = e -> e.publish(shared -> {
            return Observable.merge(
              shared.ofType(ClickEvent.class).compose(clickTransformer),
              shared.ofType(ScrollEvent.class).compose(scrollTransformer)
            );
        });

        ObservableTransformer<ScrollAction, ScrollResult> scroll = act -> act.flatMap(action -> {
            return Observable.just(action);
        }).map(response -> new ScrollResult());

        ObservableTransformer<ClickAction, ClickResult> click = c -> c.flatMap(cc -> {
            return Observable.just(cc);
        }).map(response -> new ClickResult());

        Observable<Result> results = events.compose(actions).publish(ek -> {
            return Observable.merge(
                    ek.ofType(ClickAction.class).compose(click),
                    ek.ofType(ScrollAction.class).compose(scroll)
            );
        });

        Observable<SubmitUIModel> uiModels = results.scan(SubmitUIModel.idle(), (start, end) -> {
            return SubmitUIModel.idle();
        });

        uiModels.subscribe(a -> {
            System.out.println(a);
        });

        ObservableTransformer<SubmitEvent, SubmitUIModel> submit = e -> e.flatMap(clickEvent -> {
           return userManager.setI(3)
                   .map(response -> {
                       return SubmitUIModel.success(clickEvent);
                   })
                   .onErrorReturn(t -> SubmitUIModel.failure(t.toString()))
                   .observeOn(JavaFxScheduler.platform())
                   .startWith(SubmitUIModel.idle());
        });

        Flowable<Long> ticksReact = timerService.getTimer();

        ticksReact.subscribe(a -> {
            this.deltaStreamSeconds.onNext(Direction.DOWN);
            timerColumns.forEach((columnType, column) -> {
                column.play();
                column.setLabels();
            });
            timerColumns.get(ColumnType.SECONDS).setLabels();
        });

//        ObservableTransformer<SubmitEvent, SubmitUIModel> scroll = e -> e.flatMap(scrollEvent -> {
//            return userManager.setI(3)
//                    .map(response -> {
//                        return SubmitUIModel.success(scrollEvent);
//                    })
//                    .onErrorReturn(t -> SubmitUIModel.failure(t.toString()))
//                    .observeOn(JavaFxScheduler.platform())
//                    .startWith(SubmitUIModel.idle());
//        });

//        ObservableTransformer<SubmitEvent, SubmitUIModel> submitUi = e -> e.publish(shared -> Observable.merge(
//                shared.ofType(ClickEvent.class).compose(submit),
//                shared.ofType(com.robotzero.counter.event.ScrollEvent.class).compose(scroll)
//        ));

//                events.compose(submitUi).subscribe(model -> {
//                    if (model.getData() != null && model.getData().getClass().equals(ClickEvent.class)) {
//                        ClickEvent cli = (ClickEvent) model.getData();
//                        if (cli.getButtonType().equals(ActionType.START)) {
//                            if (startButton.textProperty().getValue().equals(ActionType.START.descripton())) {
//                                System.out.println("SETTING1");
//                                startButton.setText(ActionType.PAUSE.descripton());
//                                timerService.startTimer();
//                            } else {
//                                if (startButton.textProperty().getValue().equals(ActionType.PAUSE.descripton())) {
//                                    System.out.println("SETTING 2");
//                                    startButton.setText(ActionType.START.descripton());
//                                    timerService.pauseTimer();
//                                }
//                            }
//                        }
//
//                        if (click.getButtonType().equals(ActionType.RESET)) {
//                            startButton.setText(ActionType.START.descripton());
//                            timerService.stopTimer();
//                        }
//                    }
//
//        }, t -> {
//                    System.out.println(t.getMessage());
//            throw new IOException("CRASHING");
//        });
    }
}

class SubmitUIModel {
    private final boolean success;
    private final boolean failure;
    private final String errorMessage;
    protected final SubmitEvent data;

    public SubmitUIModel(boolean success, boolean failure, String errorMessage, SubmitEvent data) {
        this.success = success;
        this.failure = failure;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    static SubmitUIModel success(SubmitEvent data) {
        return new SubmitUIModel(true, false, "", data);
    }


    static SubmitUIModel failure(String errorMessage) {
        return new SubmitUIModel(false, false, errorMessage, null);
    }

    static SubmitUIModel idle() {
        return new SubmitUIModel(false, false, "", null);
    }

    public SubmitEvent getData() {
        return data;
    }
}

class UserManager {
    Integer i = 1;
    Observable get(String s) {
        return Observable.just(i);
    }

    Observable setI(Integer i) {
        this.i = i;
        return Observable.just(i);
    }

    Completable setB(Integer i) {
        this.i = i;
        return Completable.complete();
    }
}
