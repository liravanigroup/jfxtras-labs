package jfxtras.labs.icalendar.properties.recurrence.rrule.freq;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import jfxtras.labs.icalendar.DateTimeUtilities;
import jfxtras.labs.icalendar.properties.recurrence.rrule.byxxx.ByDay;
import jfxtras.labs.icalendar.properties.recurrence.rrule.byxxx.Rule;
import jfxtras.labs.icalendar.properties.recurrence.rrule.byxxx.Rule.ByRuleType;

/** Interface for frequency rule that produces a stream of LocalDateTime start times for repeatable events 
 * FREQ rule as defined in RFC 5545 iCalendar 3.3.10 p37 (i.e. Daily, Weekly, Monthly, etc.)
 * @author David Bal
 * @see FrequencyAbstract
 * @see Yearly
 * @see Monthly
 * @see Weekly
 * @see Daily
 * @see Hourly
 * @see Minutely
 * @see Secondly */
public interface Frequency {

    /** Number of frequency periods elapsed before next occurrence. Defaults to 1*/
    Integer getInterval();
    /** Number of frequency periods elapsed before next occurrence. Defaults to 1*/
    void setInterval(Integer interval);
    /** Number of frequency periods elapsed before next occurrence. Defaults to 1*/
    IntegerProperty intervalProperty();
    
    /** Collection of rules that modify frequency rule (see RFC 5545, iCalendar 3.3.10 Page 42)
     * The rules include all BYxxx rules, EXDate and RDate lists.
     * The BYxxx rules must be applied in a specific order and can only be occur once 
     * BYxxx rule parts
      are applied to the current set of evaluated occurrences in the
      following order: BYMONTH, BYWEEKNO, BYYEARDAY, BYMONTHDAY, BYDAY,
      BYHOUR, BYMINUTE, BYSECOND and BYSETPOS; then COUNT and UNTIL are
      evaluated.*/
    List<Rule> getByRules();
    /** Adds new byRule to collection and ensures that type of rule isn't already present */
    void addByRule(Rule rule);
    /** return ByRule object from byRules list by enum type.  Returns null if not present */
    default public Rule getByRuleByType(Rule.ByRuleType byRule)
    {
        Optional<Rule> rule = getByRules()
                .stream()
                .filter(a -> a.getByRuleType() == byRule)
                .findFirst();
        return (rule.isPresent()) ? rule.get() : null;
    }
    
    /** ChronoUnit of last modification to stream
     *  Enables usage of switch statement in BYxxx rules */
    ObjectProperty<ChronoUnit> getChronoUnit();
    void setChronoUnit(ObjectProperty<ChronoUnit> chronoUnit);

    /** Resulting stream of start date/times by applying Frequency temporal adjuster and all, if any,
     * Rules.
     * Starts on startDateTime, which MUST be a valid occurrence date/time, but not necessarily the
     * first date/time (DTSTART) in the sequence. A later startDateTime can be used to more efficiently
     * get to later dates in the stream.
     * 
     * @param start - starting point of stream (MUST be a valid occurrence date/time)
     * @return
     */
    Stream<Temporal> stream(Temporal start);
    
//    /**
//     * Determines if testedTemporal is a part of the recurrence set.
//     * This test does NOT consider properties from outside Frequency, such as
//     * COUNT.  Therefore, all streams are infinite.  Avoid testing values far in
//     * the future to avoid long test times.  100 years or less should be quick enough
//     * to be not noticed.
//     * 
//     * @param testedTemporal
//     * @return
//     */
//    boolean isInstance(Temporal start, Temporal testedTemporal);

    /** Which of the enum type FrenquencyType the implementing class represents */
    FrequencyType frequencyType();
        
    /** Temporal adjuster every class implementing Frequency must provide that modifies frequency dates 
     * For example, Weekly class advances the dates by INTERVAL Number of weeks. */
    TemporalAdjuster adjuster();

    /**
     * Find previous occurrence date to start the stream
     * 
     * @param dateTimeStart - DTSTART
     * @param start
     * @return
     */
    // TODO - FIX THIS - NOT WORKING FOR RANDOM DATES
    @Deprecated // may not be needed anymore
    default Temporal makeFrequencyOccurrence(Temporal dateTimeStart, Temporal start)
    {
        if (DateTimeUtilities.isBefore(start, dateTimeStart)) return dateTimeStart;
        Iterator<Temporal> i = Stream.iterate(start, a -> a.with(adjuster())).iterator();
        Temporal last = null;
        while (i.hasNext())
        {
            Temporal current = i.next();
            if (DateTimeUtilities.isAfter(current, start)) return last;
            last = current;
        }
        return null; // should never get here
    }
    /**
     * Checks to see if object contains required properties.  Returns empty string if it is
     * valid.  Returns string of errors if not valid.
     */
    default String makeErrorString()
    {
        StringBuilder builder = new StringBuilder();
        if (getInterval() < 1) builder.append(System.lineSeparator() + "Invalid RRule.  INTERVAL must be greater than or equal to 1.");
        switch (frequencyType())
        {
        case DAILY:
            if (getByRuleByType(ByRuleType.BYWEEKNO) != null) builder.append(System.lineSeparator() + "Invalid RRule. BYWEEKNO not available when FREQ is " + frequencyType());
            if (getByRuleByType(ByRuleType.BYYEARDAY) != null) builder.append(System.lineSeparator() + "Invalid RRule. BYYEARDAY not available when FREQ is " + frequencyType());
            break;
        case MONTHLY:
            if (getByRuleByType(ByRuleType.BYWEEKNO) != null) builder.append(System.lineSeparator() + "Invalid RRule. BYWEEKNO not available when FREQ is " + frequencyType());
            if (getByRuleByType(ByRuleType.BYYEARDAY) != null) builder.append(System.lineSeparator() + "Invalid RRule. BYYEARDAY not available when FREQ is " + frequencyType());
            break;
        case WEEKLY:
            if (getByRuleByType(ByRuleType.BYWEEKNO) != null) builder.append(System.lineSeparator() + "Invalid RRule. BYWEEKNO not available when FREQ is " + frequencyType());
            if (getByRuleByType(ByRuleType.BYYEARDAY) != null) builder.append(System.lineSeparator() + "Invalid RRule. BYYEARDAY not available when FREQ is " + frequencyType());
            if (getByRuleByType(ByRuleType.BYMONTHDAY) != null) builder.append(System.lineSeparator() + "Invalid RRule. BYMONTHDAY not available when FREQ is " + frequencyType());
            break;
        case YEARLY:
            if ((getByRuleByType(ByRuleType.BYWEEKNO) != null) && (getByRuleByType(ByRuleType.BYDAY) != null))
            {
                ByDay byDay = (ByDay) getByRuleByType(ByRuleType.BYDAY);
                if (byDay.hasOrdinals()) builder.append(System.lineSeparator()
                        + "Invalid RRule. The BYDAY rule part MUST NOT be specified with a numeric value with the FREQ rule part set to YEARLY when the BYWEEKNO rule part is specified");
            }
            break;
        case HOURLY:
        case MINUTELY:
        case SECONDLY:
            builder.append(System.lineSeparator() + "Invalid RRule. " + frequencyType() + " not implemented.");
            break;
        default:
            builder.append(System.lineSeparator() + "Invalid RRule. " + frequencyType() + " unknown.");
            break;
        
        }
        return builder.toString();
    }
    
    /** Enumeration of FREQ rules 
     * Is used to make new instances of the different Frequencies by matching FREQ property
     * to its matching class */
    public static enum FrequencyType
    {
        YEARLY (Yearly.class) ,
        MONTHLY (Monthly.class) ,
        WEEKLY (Weekly.class) ,
        DAILY (Daily.class) ,
        HOURLY (Hourly.class) ,
        MINUTELY (Minutely.class) ,
        SECONDLY (Secondly.class);
      
        private Class<? extends Frequency> clazz;
          
        FrequencyType(Class<? extends Frequency> clazz)
        {
            this.clazz = clazz;
        }

        public Frequency newInstance()
        {
            try {
                return clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

        /** return array of implemented FrequencyTypes */
        public static FrequencyType[] implementedValues()
        {
            return new FrequencyType[] { DAILY, WEEKLY, MONTHLY, YEARLY };
        }
    }

    /** Deep copy all fields from source to destination */
    static void copy(Frequency source, Frequency destination)
    {
        destination.setChronoUnit(source.getChronoUnit());
        if (source.getInterval() != null) destination.setInterval(source.getInterval());
        if (source.getByRules() != null)
        {
            source.getByRules().stream().forEach(r ->
            {
                try {
                    Rule newRule = r.getClass().newInstance();
                    Rule.copy(r, newRule);
                    destination.addByRule(newRule);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        }
    }
    
}

