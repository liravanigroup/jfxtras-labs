package jfxtras.labs.repeatagenda;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.temporal.Temporal;

import org.junit.Test;

import jfxtras.labs.repeatagenda.scene.control.repeatagenda.VEventImpl;
import jfxtras.labs.repeatagenda.scene.control.repeatagenda.icalendar.VComponent;

public class ICalendarToStringTest extends ICalendarTestAbstract
{
    /** Tests FREQ=YEARLY */
    @Test
    public void yearly1ToString()
    {
        VEventImpl e = getYearly1();
        String madeString = e.toString();
        String expectedString = "BEGIN:VEVENT" + System.lineSeparator()
                              + "CATEGORIES:group13" + System.lineSeparator()
                              + "CREATED:20151109T082900" + System.lineSeparator()
                              + "DESCRIPTION:Yearly1 Description" + System.lineSeparator()
                              + "DTSTAMP:20151109T083000" + System.lineSeparator()
                              + "DTSTART:20151109T100000" + System.lineSeparator()
                              + "DURATION:PT1H" + System.lineSeparator()
                              + "LAST-MODIFIED:20151110T183000" + System.lineSeparator()
                              + "RRULE:FREQ=YEARLY" + System.lineSeparator()
                              + "SUMMARY:Yearly1 Summary" + System.lineSeparator()
                              + "UID:20151109T082900-0@jfxtras.org" + System.lineSeparator()
                              + "END:VEVENT";
        assertEquals(expectedString, madeString);
    }

    /** Tests FREQ=MONTHLY;BYDAY=FR;BYMONTHDAY=13 */
    @Test
    public void monthly5ToString()
    {

    VEventImpl e = getMonthly5();
    String madeString = e.toString();
    String expectedString = "BEGIN:VEVENT" + System.lineSeparator()
                          + "DTSTAMP:19970901T083000" + System.lineSeparator()
                          + "DTSTART:19970902T100000" + System.lineSeparator()
                          + "DURATION:PT1H" + System.lineSeparator()
                          + "RRULE:FREQ=MONTHLY;BYMONTHDAY=13;BYDAY=FR" + System.lineSeparator()
                          + "UID:19970901T083000-0@jfxtras.org" + System.lineSeparator()
                          + "END:VEVENT";
    assertEquals(expectedString, madeString);
    }
    
    /** Tests FREQ=MONTHLY;BYDAY=FR;BYMONTHDAY=13 */
    @Test
    public void monthly6ToString()
    {

    VEventImpl e = getMonthly6();
    String madeString = e.toString();
    String expectedString = "BEGIN:VEVENT" + System.lineSeparator()
                          + "DTSTAMP:20150110T080000" + System.lineSeparator()
                          + "DTSTART:20150110T100000" + System.lineSeparator()
                          + "DURATION:PT1H30M" + System.lineSeparator()
                          + "RRULE:FREQ=MONTHLY;BYMONTH=11,12;BYDAY=TU,WE,FR" + System.lineSeparator()
                          + "UID:20150110T080000-0@jfxtras.org" + System.lineSeparator()
                          + "END:VEVENT";
    assertEquals(expectedString, madeString);
    }
    
    /** FREQ=DAILY;INVERVAL=3;COUNT=6
     *  EXDATE=20151112T100000,20151115T100000 */
    @Test
    public void dailyWithException1ToString()
    {

    VEventImpl e = getDailyWithException1();
    String madeString = e.toString();
    String expectedString = "BEGIN:VEVENT" + System.lineSeparator()
                          + "CATEGORIES:group03" + System.lineSeparator()
                          + "DESCRIPTION:Daily2 Description" + System.lineSeparator()
                          + "DTSTAMP:20150110T080000" + System.lineSeparator()
                          + "DTSTART:20151109T100000" + System.lineSeparator()
                          + "DURATION:PT1H30M" + System.lineSeparator()
                          + "EXDATE:20151112T100000,20151115T100000" + System.lineSeparator()
                          + "RRULE:FREQ=DAILY;INTERVAL=3;COUNT=6" + System.lineSeparator()
                          + "SUMMARY:Daily2 Summary" + System.lineSeparator()
                          + "UID:20150110T080000-0@jfxtras.org" + System.lineSeparator()
                          + "END:VEVENT";
    assertEquals(expectedString, madeString);
    }

    @Test
    public void canConvertWholeDay1()
    {
        Temporal date = LocalDate.of(2015, 11, 12);
        String expectedString = "VALUE=DATE:20151112";
        String madeString = VComponent.temporalToString(date);
        assertEquals(expectedString, madeString);
    }

    @Test
    public void canConvertWholeDay2()
    {
    VEventImpl vEvent = getWholeDayDaily1();
    String madeString = vEvent.toString();
    String expectedString = "BEGIN:VEVENT" + System.lineSeparator()
                          + "DTEND:VALUE=DATE:20151112" + System.lineSeparator()
                          + "DTSTAMP:20150110T080000" + System.lineSeparator()
                          + "DTSTART:VALUE=DATE:20151109" + System.lineSeparator()
                          + "UID:20150110T080000-0@jfxtras.org" + System.lineSeparator()
                          + "END:VEVENT";
    assertEquals(expectedString, madeString);
    }

}