package com.queen.counter.service;

import com.queen.animator.Animator;
import com.queen.counter.domain.Clocks;
import com.queen.counter.domain.UIService;
import javafx.scene.text.Text;

public class Scroller {

    private final Animator animator;
    private final Clocks clocks;
    private final UIService uiService;
    private final OffsetCalculator offsetCalculator;

    public Scroller(
            final Animator animator,
            final Clocks clocks,
            final UIService uiService,
            final OffsetCalculator offsetCalculator
    ) {
        this.animator = animator;
        this.clocks = clocks;
        this.uiService = uiService;
        this.offsetCalculator = offsetCalculator;
    }

    public void scroll(final String columnName, double deltaY) {

        this.offsetCalculator.setCurrentDelta((int) deltaY);
        this.offsetCalculator.setCurrentLabel(columnName.equals("group"));

        this.uiService.setDelta(deltaY);
        this.uiService.setCurrentGroupName(columnName);
        this.offsetCalculator.setFoundEdgeRectangle();

        int timeShift = this.clocks.clockTick(columnName, deltaY, this.offsetCalculator.getCurrentOffset());

        if (this.uiService.getEdgeRectangleId() != null) {
            this.uiService.getCurrentLabelsStream().get().filter(lbl -> lbl.getId()
                    .equals(this.uiService.getEdgeRectangleId()))
                    .findFirst()
                    .ifPresent(lbl -> ((Text) lbl).setText(timeShift + ""));
        }

        animator.animate(this.uiService.getCurrentAnimations(), deltaY);
        this.offsetCalculator.resetFoundRectangle();
    }
}
