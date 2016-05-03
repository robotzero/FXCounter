package com.queen.counter.service;

import com.queen.animator.Animator;
import com.queen.counter.cache.InMemoryCachedServiceLocator;
import com.queen.counter.domain.AnimationMetadata;
import com.queen.counter.domain.Clocks;
import com.queen.counter.domain.UIService;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.reactfx.EventSource;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.util.Tuple3;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Scroller {

    private final Animator animator;
    private final InMemoryCachedServiceLocator cache;
    private final Clocks clocks;
    private final UIService uiService;
    private final EventSource eventSource;

    private BooleanProperty deltaB = new SimpleBooleanProperty(false);
    private BooleanProperty label = new SimpleBooleanProperty(false);
    private BooleanProperty f = new SimpleBooleanProperty(false);

    private IntegerProperty offset = new SimpleIntegerProperty(1);
    private IntegerProperty seconds = new SimpleIntegerProperty();
    private IntegerProperty minutes = new SimpleIntegerProperty();
    private Predicate<Node> rectanglePredicate = r -> r.getClass().equals(Rectangle.class);
    private Predicate<Node> labelPredicate = l -> l.getClass().equals(Text.class);
    private IntegerProperty delta = new SimpleIntegerProperty();
    private NumberBinding changeRectangle = Bindings.createIntegerBinding(() -> delta.getValue() > 0 ? 0 : 240);
    private NumberBinding compareRectangle = Bindings.createIntegerBinding(() -> delta.getValue() < 0 ? 0 : 240);

    public Scroller(final Animator animator, final InMemoryCachedServiceLocator cache, final Clocks clocks, final UIService uiService, final EventSource eventSource) {
        this.animator = animator;
        this.cache = cache;
        this.clocks = clocks;
        this.uiService = uiService;
        this.eventSource = eventSource;

        EventStream lab = EventStreams.valuesOf(label);
        EventStream del = EventStreams.valuesOf(deltaB);
        EventStream ff = EventStreams.valuesOf(f);

        EventStream<Tuple3<Boolean, Boolean, Boolean>> combo = EventStreams.combine(del, ff, lab);

        combo.map(change -> {
            Boolean delta = change.get1();
            //System.out.println(delta);
            Boolean found = change.get2();
            //System.out.println(found);
            Boolean labl = change.get3();
            if ((delta && found) || (labl && found)) {
                return 2;
            }

            return 1;
        }).feedTo(offset);
    }

    public void scroll(final String columnName, final String rectangleId, double deltaY) {

        delta.setValue((int)deltaY);
//        final int found = deltaY > 0 ? 0 : 240;
//        final int compare = deltaY < 0 ? 0 : 240;
        System.out.println(changeRectangle);
        this.uiService.getStream(g -> g.getId()
                .contains(rectangleId), rectanglePredicate)
                .get()
                .filter(r -> r.getTranslateY() == changeRectangle.getValue().intValue())
                .findAny()
                .ifPresent(r -> f.set(true));

        deltaB.set(deltaY < 0);
        this.label.set(columnName.equals("group"));

        int offsetNumber = offset.getValue();

        if (label.get()) {
            animator.setRunning(true);
        } else {
            animator.setMinutesRunning(true);
        }

        int timeShift = this.clocks.clockTick(columnName, deltaY, offsetNumber);

        if (label.get()) {
            seconds.set(timeShift);
        } else {
            minutes.set(timeShift);
        }

        List<AnimationMetadata> l = this.uiService.getStream(g -> g.getId()
                .contains(rectangleId), rectanglePredicate)
                .get()
                .map(n -> (AnimationMetadata) cache.get(AnimationMetadata.class, n))
                .collect(Collectors.toList());

        this.uiService.getStream(g -> g.getId().contains(rectangleId), rectanglePredicate).get()
                .filter(r -> r.getTranslateY() == compareRectangle.getValue().intValue())
                .findAny()
                .ifPresent(r -> this.uiService.getStream(g -> g.getId().contains(rectangleId), labelPredicate)
                        .get().filter(lbl -> lbl.getId()
                        .equals(r.getId()))
                        .findFirst()
                        .ifPresent(lbl -> {
                            if (label.get()) {
                                ((Text) lbl).setText(seconds.get() + "");
                            } else {
                                ((Text) lbl).setText(minutes.get() + "");
                            }
                        }));

        animator.animate(l, deltaY, cache);
        offset.set(1);
        f.set(false);
    }
}
