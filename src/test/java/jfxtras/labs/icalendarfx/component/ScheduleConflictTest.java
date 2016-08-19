package jfxtras.labs.icalendarfx.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import jfxtras.labs.icalendarfx.ICalendarTestAbstract;
import jfxtras.labs.icalendarfx.VCalendar;
import jfxtras.labs.icalendarfx.components.VComponent;
import jfxtras.labs.icalendarfx.components.VEvent;
import jfxtras.labs.icalendarfx.properties.component.recurrence.rrule.FrequencyType;
import jfxtras.labs.icalendarfx.properties.component.recurrence.rrule.RecurrenceRule2;
import jfxtras.labs.icalendarfx.properties.component.recurrence.rrule.byxxx.ByDay;
import jfxtras.labs.icalendarfx.properties.component.time.TimeTransparency.TimeTransparencyType;
import jfxtras.labs.icalendarfx.utilities.DateTimeUtilities;

public class ScheduleConflictTest extends ICalendarTestAbstract
{
    @Test // starts at same time and day
    public void canDetectScheduleConflict1()
    {
        List<VEvent> list = Arrays.asList(getYearly1());
        VEvent vEvent = getDaily1();
        String conflict = DateTimeUtilities.checkScheduleConflict(vEvent, list);
        assertEquals("20151109T082900-0@jfxtras.org, 2015-11-09T10:00", conflict);
    }
    
    @Test // overlaps about a year in future
    public void canDetectScheduleConflict2()
    {
        VEvent existingVEvent = new VEvent()
            .withDateTimeStart(LocalDateTime.of(2015, 11, 9, 10, 0))
            .withDuration(Duration.ofHours(1))
            .withRecurrenceRule(new RecurrenceRule2()
                    .withFrequency(FrequencyType.YEARLY));
        List<VEvent> list = Arrays.asList(existingVEvent);
        VEvent newVEvent = new VEvent()
                .withDateTimeStart(LocalDateTime.of(2015, 11, 10, 10, 30))
                .withRecurrenceRule(new RecurrenceRule2()
                        .withFrequency(FrequencyType.DAILY));
        String conflict = DateTimeUtilities.checkScheduleConflict(newVEvent, list);
        assertEquals("2016-11-09T10:00", conflict);
    }
    
    @Test // multiple events with a TRANSPARENT one that must be ignored to be correct
    public void canDetectScheduleConflict3()
    {
        VEvent existingVEvent1 = new VEvent()
            .withDateTimeStart(LocalDateTime.of(2015, 11, 9, 10, 0))
            .withDuration(Duration.ofHours(1))
            .withRecurrenceRule(new RecurrenceRule2()
                    .withFrequency(FrequencyType.YEARLY));
        VEvent existingVEvent2 = new VEvent()
                .withDateTimeStart(LocalDateTime.of(2016, 5, 9, 9, 0))
                .withDuration(Duration.ofHours(3))
                .withRecurrenceRule(new RecurrenceRule2()
                        .withFrequency(FrequencyType.DAILY)
                        .withInterval(5));
        VEvent existingVEvent3 = new VEvent()
                .withDateTimeStart(LocalDateTime.of(2016, 5, 9, 7, 0))
                .withDuration(Duration.ofHours(1))
                .withRecurrenceRule(new RecurrenceRule2()
                        .withFrequency(FrequencyType.DAILY));
        VEvent existingVEvent4 = new VEvent()
                .withDateTimeStart(LocalDateTime.of(2016, 1, 1, 10, 0))
                .withDuration(Duration.ofHours(1))
                .withTimeTransparency(TimeTransparencyType.TRANSPARENT)
                .withRecurrenceRule(new RecurrenceRule2()
                        .withFrequency(FrequencyType.DAILY));
        List<VEvent> list = Arrays.asList(existingVEvent1, existingVEvent2, existingVEvent3, existingVEvent4);
        VEvent newVEvent = new VEvent()
                .withDateTimeStart(LocalDateTime.of(2015, 11, 27, 10, 30))
                .withRecurrenceRule(new RecurrenceRule2()
                        .withFrequency(FrequencyType.WEEKLY)
                        .withByRules(new ByDay(DayOfWeek.FRIDAY)));
        String conflict = DateTimeUtilities.checkScheduleConflict(newVEvent, list);
        assertEquals("2016-06-03T09:00", conflict);
    }
        
    @Test // starts before, ends in middle
    public void canDetectScheduleConflict5()
    {
        VEvent existingVEvent = new VEvent()
            .withDateTimeStart(LocalDateTime.of(2015, 11, 9, 10, 0))
            .withDuration(Duration.ofHours(1))
            .withRecurrenceRule(new RecurrenceRule2()
                    .withFrequency(FrequencyType.YEARLY));
        List<VEvent> list = Arrays.asList(existingVEvent);
        VEvent newVEvent = new VEvent()
                .withDateTimeStart(LocalDateTime.of(2015, 11, 10, 9, 30))
                .withDuration(Duration.ofHours(1))
                .withRecurrenceRule(new RecurrenceRule2()
                        .withFrequency(FrequencyType.DAILY));
        String conflict = DateTimeUtilities.checkScheduleConflict(newVEvent, list);
        assertEquals("2016-11-09T10:00", conflict);
    }
    
    @Test // starts in middle, ends in middle
    public void canDetectScheduleConflict6()
    {
        VEvent existingVEvent = new VEvent()
            .withDateTimeStart(LocalDateTime.of(2015, 11, 9, 10, 0))
            .withDuration(Duration.ofHours(1))
            .withRecurrenceRule(new RecurrenceRule2()
                    .withFrequency(FrequencyType.YEARLY));
        List<VEvent> list = Arrays.asList(existingVEvent);
        VEvent newVEvent = new VEvent()
                .withDateTimeStart(LocalDateTime.of(2015, 11, 10, 10, 30))
                .withDuration(Duration.ofMinutes(20))
                .withRecurrenceRule(new RecurrenceRule2()
                        .withFrequency(FrequencyType.DAILY));
        String conflict = DateTimeUtilities.checkScheduleConflict(newVEvent, list);
        assertEquals("2016-11-09T10:00", conflict);
    }
    
    @Test // starts in middle, ends outside
    public void canDetectScheduleConflict7()
    {
        VEvent existingVEvent = new VEvent()
            .withDateTimeStart(LocalDateTime.of(2015, 11, 9, 10, 0))
            .withDuration(Duration.ofHours(1))
            .withRecurrenceRule(new RecurrenceRule2()
                    .withFrequency(FrequencyType.YEARLY));
        List<VEvent> list = Arrays.asList(existingVEvent);
        VEvent newVEvent = new VEvent()
                .withDateTimeStart(LocalDateTime.of(2015, 11, 10, 10, 30))
                .withDuration(Duration.ofHours(1))
                .withRecurrenceRule(new RecurrenceRule2()
                        .withFrequency(FrequencyType.DAILY));
        String conflict = DateTimeUtilities.checkScheduleConflict(newVEvent, list);
        assertEquals("2016-11-09T10:00", conflict);
    }
    
    @Test // test individual conflict
    public void canDetectScheduleConflict8()
    {
        VEvent existingVEvent = new VEvent()
            .withDateTimeStart(LocalDateTime.of(2015, 11, 9, 10, 0))
            .withDuration(Duration.ofHours(1))
            .withRecurrenceRule(new RecurrenceRule2()
                    .withFrequency(FrequencyType.DAILY));
        List<VEvent> list = Arrays.asList(existingVEvent);
        VEvent newVEvent = new VEvent()
                .withDateTimeStart(LocalDateTime.of(2015, 12, 1, 10, 30))
                .withDuration(Duration.ofHours(1));
        String conflict = DateTimeUtilities.checkScheduleConflict(newVEvent, list);
        assertEquals("2015-12-01T10:00", conflict);
    }

    
    @Test // starts before, ends outside
    public void canDetectScheduleConflict9()
    {
        VEvent existingVEvent = new VEvent()
            .withDateTimeStart(LocalDateTime.of(2015, 11, 9, 10, 0))
            .withDuration(Duration.ofHours(1))
            .withRecurrenceRule(new RecurrenceRule2()
                    .withFrequency(FrequencyType.YEARLY));
        List<VEvent> list = Arrays.asList(existingVEvent);
        VEvent newVEvent = new VEvent()
                .withDateTimeStart(LocalDateTime.of(2015, 11, 10, 9, 30))
                .withDuration(Duration.ofHours(2))
                .withRecurrenceRule(new RecurrenceRule2()
                        .withFrequency(FrequencyType.DAILY));
        String conflict = DateTimeUtilities.checkScheduleConflict(newVEvent, list);
        assertEquals("2016-11-09T10:00", conflict);
    }
    
    @Test // return null when no conflict
    public void canDetectNoScheduleConflict()
    {
        VEvent existingVEvent = new VEvent()
            .withDateTimeStart(LocalDateTime.of(2015, 11, 9, 10, 0))
            .withDuration(Duration.ofHours(1))
            .withRecurrenceRule(new RecurrenceRule2()
                    .withFrequency(FrequencyType.YEARLY));
        List<VEvent> list = Arrays.asList(existingVEvent);
        VEvent newVEvent = new VEvent()
                .withDateTimeStart(LocalDateTime.of(2015, 11, 10, 7, 30))
                .withDuration(Duration.ofHours(1))
                .withRecurrenceRule(new RecurrenceRule2()
                        .withFrequency(FrequencyType.DAILY));
        String conflict = DateTimeUtilities.checkScheduleConflict(newVEvent, list);
        assertNull(conflict);
    }
    
    @Test // end when existing starts
    public void canDetectNoScheduleConflict2()
    {
        VEvent existingVEvent = new VEvent()
            .withDateTimeStart(LocalDateTime.of(2015, 11, 9, 10, 0))
            .withDuration(Duration.ofHours(1))
            .withRecurrenceRule(new RecurrenceRule2()
                    .withFrequency(FrequencyType.YEARLY));
        List<VEvent> list = Arrays.asList(existingVEvent);
        VEvent newVEvent = new VEvent()
                .withDateTimeStart(LocalDateTime.of(2015, 11, 10, 9, 0))
                .withDuration(Duration.ofHours(1))
                .withRecurrenceRule(new RecurrenceRule2()
                        .withFrequency(FrequencyType.DAILY));
        String conflict = DateTimeUtilities.checkScheduleConflict(newVEvent, list);
        assertNull(conflict);
    }

    
    @Test
    public void canDetectScheduleConflictDuringImport()
    {
        VCalendar c = new VCalendar();
        c.addVComponent(getDaily1());
        String content = 
                "BEGIN:VEVENT" + System.lineSeparator() +
                "DTSTART:20160406T100000" + System.lineSeparator() +
                "DTEND:20160406T123000" + System.lineSeparator() +
                "END:VEVENT";
        VEvent v = VEvent.parse(content);
        v.               withRecurrenceRule(new RecurrenceRule2()
                .withFrequency(FrequencyType.DAILY));
        String conflict = DateTimeUtilities.checkScheduleConflict(v, c.getVEvents());
        System.out.println(conflict);
        VComponent newVComponent = c.importVComponent(content);
        System.out.println(newVComponent);
    }
}
