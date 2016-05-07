package com.queen.counter.service;

import com.queen.animator.Animator;
import com.queen.counter.domain.Clocks;
import com.queen.counter.domain.UIService;

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

        if (columnName.equals("group")) {
            animator.setRunning(true);
        } else {
            animator.setMinutesRunning(true);
        }
        this.uiService.setCurrentGroupName(columnName);

        int timeShift = this.clocks.clockTick(
                columnName,
                deltaY,
                this.offsetCalculator.getCurrentOffset(deltaY, columnName.equals("group"))
        );

        this.uiService.updateLabelText(deltaY, timeShift);

        animator.animate(this.uiService.getCurrentAnimations(), deltaY);
        animator.setMinutesRunning(false);
        animator.setRunning(false);
        this.offsetCalculator.reset();
    }
}
