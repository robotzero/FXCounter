package com.robotzero.counter.clock;

import com.robotzero.counter.domain.Column;
import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.domain.clock.Clocks;
import com.robotzero.counter.service.Populator;
import com.robotzero.counter.service.StageController;
import io.reactivex.Flowable;
import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.subjects.Subject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.reactfx.EventSource;
import org.reactfx.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.SocketUtils;

import javax.sound.midi.Soundbank;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ClockPresenter implements Initializable {

    @FXML
    GridPane gridPane;

    @FXML
    Button startButton, reset;

//    @FXML
//    StackPane paneSeconds;
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

    private BooleanProperty scrollMuteProperty = new SimpleBooleanProperty(false);

    private Subscription subscribe;

    private Map<ColumnType, Column> timerColumns;

    private BooleanProperty timerMute = new SimpleBooleanProperty(true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        startButton.textProperty().bind(new When(scrollMuteProperty.isEqualTo(new SimpleBooleanProperty(false))).then("Start").otherwise("Pause"));
        timerMute.bind(startButton.armedProperty());
        this.timerColumns = this.populator.timerColumns(this.gridPane);

        Observable<MouseEvent> mouseClickObservable = JavaFxObservable.eventsOf(startButton, MouseEvent.MOUSE_CLICKED);
        Observable<Long> timerObservable = Observable.interval(1, 1, TimeUnit.SECONDS).skipWhile(time -> timerMute.get());
        JavaFxObservable.valuesOf(startButton.textProperty()).startWith(startButton.textProperty().get()).skipUntil(mouseClickObservable).subscribe(
                (val) -> System.out.println("RECEIVED"),
                (error) -> System.out.println("ERRR"),
                () -> System.out.println("COMPLETED")
        );

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
            System.out.println("PLAY!!!!!!!!!!!!!");
            System.out.println(value);
            this.timerColumns.get(ColumnType.SECONDS).play();
        };
        Consumer<Notification> timerDoOnEach = value -> this.deltaStreamSeconds.onNext(Direction.DOWN);
        Consumer<Throwable> timerDoOnError = System.out::println;
        Action timerOnComplete = () -> System.out.println("Completed");

        Disposable disposable = mouseClickObservable.switchMap(mouseEvent -> {
            return timerObservable;
        }).doOnEach(timerDoOnEach::accept).subscribe(
                timerOnNext::accept,
                timerDoOnError::accept,
                timerOnComplete
        );

        ConnectableObservable<MouseEvent> conn  = mouseClickObservable.publish();
        //@Todo debounce or delay here!!!
        mouseClickObservable.window(1).switchMap(mouseEvent -> {
           return Observable.just(startButton);
        }).doOnEach(startButton -> {
            conn.publish();
            if (startButton.getValue().getText().equals("Start")) {
                startButton.getValue().setText("Pause");
            } else {
                startButton.getValue().setText("Start");
            }
        }).subscribe();


//        EventStream<MouseEvent> startClicks = EventStreams.eventsOf(startButton, MouseEvent.MOUSE_CLICKED);
//        EventStream<MouseEvent> resetClicks = EventStreams.eventsOf(reset, MouseEvent.MOUSE_CLICKED);
//        EventStream<MouseEvent> optionClicks = EventStreams.eventsOf(optionsLabel, MouseEvent.MOUSE_CLICKED);

//        Flowable<Long> ticksReact = Flowable.interval(1, TimeUnit.SECONDS).skipWhile(time -> {
//            return scrollMuteProperty.not().get();
//        });
        Flowable<Long> ticksReact = Flowable.interval(1, 1, TimeUnit.SECONDS).skipWhile(time -> {
            return false;
//            return scrollMuteProperty.not().get();
        });

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
    }
}
