package jfxtras.labs.repeatagenda.scene.control.repeatagenda.icalendar.rrule.byxxx;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import jfxtras.labs.repeatagenda.scene.control.repeatagenda.icalendar.rrule.freq.Frequency;

public class BySetPos extends ByRuleAbstract
{
    public BySetPos(Frequency frequency)
    {
        super(frequency);
        setSortOrder(80);
        throw new RuntimeException("not implemented");
    }
    
    @Override
    public Stream<LocalDateTime> stream(Stream<LocalDateTime> inStream,
            LocalDateTime startDateTime) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void copyTo(Rule destination) {
        // TODO Auto-generated method stub
        
    }

}
