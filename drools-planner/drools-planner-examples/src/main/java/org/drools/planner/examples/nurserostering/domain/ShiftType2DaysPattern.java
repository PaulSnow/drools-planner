package org.drools.planner.examples.nurserostering.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author Geoffrey De Smet
 */
@XStreamAlias("ShiftType2DaysPattern")
public class ShiftType2DaysPattern extends Pattern {

    private DayOfWeek startDayOfWeek; // null means any

    private ShiftType dayIndex0ShiftType;
    private ShiftType dayIndex1ShiftType;

    public DayOfWeek getStartDayOfWeek() {
        return startDayOfWeek;
    }

    public void setStartDayOfWeek(DayOfWeek startDayOfWeek) {
        this.startDayOfWeek = startDayOfWeek;
    }

    public ShiftType getDayIndex0ShiftType() {
        return dayIndex0ShiftType;
    }

    public void setDayIndex0ShiftType(ShiftType dayIndex0ShiftType) {
        this.dayIndex0ShiftType = dayIndex0ShiftType;
    }

    public ShiftType getDayIndex1ShiftType() {
        return dayIndex1ShiftType;
    }

    public void setDayIndex1ShiftType(ShiftType dayIndex1ShiftType) {
        this.dayIndex1ShiftType = dayIndex1ShiftType;
    }

    @Override
    public String toString() {
        return "Work starting on " + startDayOfWeek + " in sequence: "
                + dayIndex0ShiftType + ", " + dayIndex1ShiftType;
    }

}