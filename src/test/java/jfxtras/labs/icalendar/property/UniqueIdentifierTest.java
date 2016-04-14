package jfxtras.labs.icalendar.property;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import jfxtras.labs.icalendar.properties.component.relationship.UniqueIdentifier;

public class UniqueIdentifierTest
{
    @Test
    public void canParseUniqueIdentifier()
    {
        String expectedContentLine = "UID:19960401T080045Z-4000F192713-0052@example.com";
        UniqueIdentifier property = new UniqueIdentifier(expectedContentLine);
        String madeContentLine = property.toContentLine();
        assertEquals(expectedContentLine, madeContentLine);
        assertEquals("19960401T080045Z-4000F192713-0052@example.com", property.getValue());
    }
}
