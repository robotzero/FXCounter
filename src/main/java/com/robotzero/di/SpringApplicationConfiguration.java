package com.robotzero.di;

import com.airhacks.afterburner.views.FXMLView;
import com.robotzero.configuration.SceneConfiguration;
import com.robotzero.counter.clock.ClockPresenter;
import com.robotzero.counter.clock.ClockView;
import com.robotzero.counter.clock.options.OptionsPresenter;
import com.robotzero.counter.domain.*;
import com.robotzero.counter.clock.options.OptionsView;
import com.robotzero.counter.repository.SavedTimerRepository;
import com.robotzero.counter.repository.SavedTimerSqlliteJdbcRepository;
import com.robotzero.counter.service.Populator;
import com.robotzero.counter.service.StageController;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.reactfx.EventSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class SpringApplicationConfiguration {

    @FXML
    StackPane paneSeconds;

    @Bean
    public FXMLView clockView() {
        return new ClockView();
    }

    @Bean
    public FXMLView optionsView() { return new OptionsView(); }

    @Bean
    public StageController stageController() {
        return new StageController(sceneConfiguration(), clockView(), optionsView());
    }

    @Bean
    public ClockPresenter clockPresenter() {
        return new ClockPresenter();
    }

    @Bean
    public OptionsPresenter optionPresenter() {
        return new OptionsPresenter();
    }

    @Bean
    public SceneConfiguration sceneConfiguration() {
        return new SceneConfiguration();
    }

    @Bean
    public EventSource seconds() {
        return new EventSource();
    }

    @Bean
    public EventSource minutes() {
        return new EventSource();
    }

    @Bean
    public EventSource hours() {
        return new EventSource();
    }

    @Bean
    public EventSource<Void> PlayMinutes() {
        return new EventSource<>();
    }

    @Bean
    public EventSource<Void> PlayHours() {
        return new EventSource<>();
    }

    @Bean
    public EventSource<Void> StopCountdown() {
        return new EventSource<>();
    }

    @Bean
    public Subject<Integer> DeltaStreamSeconds() {
        return BehaviorSubject.create();
    }

    @Bean
    public Subject<Integer> DeltaStreamMinutes() {
        return BehaviorSubject.create();
    }

    @Bean
    public Subject<Integer> DeltaStreamHours() {
        return BehaviorSubject.create();
    }

    @Bean
    public Populator populator() {
        List<Subject<Integer>> deltaStreams = new ArrayList<>();
        deltaStreams.add(DeltaStreamSeconds());
        deltaStreams.add(DeltaStreamMinutes());
        deltaStreams.add(DeltaStreamHours());
        return new Populator(configureClocks(), deltaStreams, seconds(), minutes(), hours());
    }

    @Bean
    public DataSource jdbcDataSource() {
        SingleConnectionDataSource ds = new SingleConnectionDataSource();
        ds.setDriverClassName("org.sqlite.JDBC");
        ds.setUrl("jdbc:sqlite:clocks.db");
        return ds;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public SavedTimerRepository savedTimerRepository(JdbcTemplate jdbcTemplate) {
        return new SavedTimerSqlliteJdbcRepository(jdbcTemplate);
    }

    @Bean
    BooleanProperty fetchFromDatabase() {
        return new SimpleBooleanProperty(false);
    }

    @Bean
    public Clocks configureClocks() {
        List<EventSource<Void>> plays = new ArrayList<>();
        plays.add(PlayMinutes());
        plays.add(PlayHours());
        plays.add(StopCountdown());

        List<Subject<Integer>> deltaStreams = new ArrayList<>();
        deltaStreams.add(DeltaStreamSeconds());
        deltaStreams.add(DeltaStreamMinutes());
        deltaStreams.add(DeltaStreamHours());
        return new Clocks(plays, deltaStreams, seconds(), minutes(), hours());
    }

    @Bean
    public Map<ColumnType, Column> timerColumns(ClockView clockView, Clocks clocks) {
        List<Cell> seconds = clockView.getView().getChildrenUnmodifiable()
                .filtered(node -> node.getClass().equals(StackPane.class) && node.getId().equals("seconds"))
                .stream()
                .flatMap(stackPane -> ((StackPane) stackPane).getChildren().stream())
                .filter(node -> node.getClass().equals(VBox.class))
                .map(node -> {
                    TranslateTransition translateTransition = new TranslateTransition();
                    VBox vbox = (VBox) node;
                    translateTransition.setNode(vbox);
                    return new Cell(vbox, new Location(), ((Text) vbox.getChildren().get(0)), translateTransition, DeltaStreamSeconds(), new SimpleIntegerProperty(90));
                }).collect(Collectors.toList());

        List<Cell> minutes = clockView.getView().getChildrenUnmodifiable()
                .filtered(node -> node.getClass().equals(StackPane.class) && node.getId().equals("minutes"))
                .stream()
                .flatMap(stackPane -> ((StackPane) stackPane).getChildren().stream())
                .filter(node -> node.getClass().equals(VBox.class))
                .map(node -> {
                    TranslateTransition translateTransition = new TranslateTransition();
                    VBox vbox = (VBox) node;
                    translateTransition.setNode(vbox);
                    return new Cell(vbox, new Location(), ((Text) vbox.getChildren().get(0)), translateTransition, DeltaStreamSeconds(), new SimpleIntegerProperty(90));
                }).collect(Collectors.toList());

        List<Cell> hours = clockView.getView().getChildrenUnmodifiable()
                .filtered(node -> node.getClass().equals(StackPane.class) && node.getId().equals("hours"))
                .stream()
                .flatMap(stackPane -> ((StackPane) stackPane).getChildren().stream())
                .filter(node -> node.getClass().equals(VBox.class))
                .map(node -> {
                    TranslateTransition translateTransition = new TranslateTransition();
                    VBox vbox = (VBox) node;
                    translateTransition.setNode(vbox);
                    return new Cell(vbox, new Location(), ((Text) vbox.getChildren().get(0)), translateTransition, DeltaStreamSeconds(), new SimpleIntegerProperty(90));
                }).collect(Collectors.toList());

        Map<ColumnType, Column> timerColumns = new HashMap<>();
        timerColumns.put(ColumnType.SECONDS, new Column(seconds, clocks, ColumnType.SECONDS, seconds()));
        timerColumns.put(ColumnType.MINUTES, new Column(minutes, clocks, ColumnType.MINUTES, minutes()));
        timerColumns.put(ColumnType.HOURS, new Column(hours, clocks, ColumnType.HOURS, hours()));
        return timerColumns;
    }
}