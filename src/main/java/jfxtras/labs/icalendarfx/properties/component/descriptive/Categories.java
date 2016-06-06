package jfxtras.labs.icalendarfx.properties.component.descriptive;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.util.StringConverter;
import jfxtras.labs.icalendarfx.components.VEvent;
import jfxtras.labs.icalendarfx.components.VJournal;
import jfxtras.labs.icalendarfx.components.VTodo;
import jfxtras.labs.icalendarfx.parameters.ValueType;
import jfxtras.labs.icalendarfx.properties.PropertyBaseLanguage;

/**
 * CATEGORIES
 * RFC 5545, 3.8.1.2, page 81
 * 
 * This property defines the categories for a calendar component.
 * 
 * Examples:
 * CATEGORIES:APPOINTMENT,EDUCATION
 * CATEGORIES:MEETING
 * 
 * @author David Bal
 * 
 * The property can be specified in following components:
 * @see VEvent
 * @see VTodo
 * @see VJournal
 */
public class Categories extends PropertyBaseLanguage<List<SimpleStringProperty>, Categories>
{
    private final static StringConverter<List<SimpleStringProperty>> CONVERTER = new StringConverter<List<SimpleStringProperty>>()
    {
        @Override
        public String toString(List<SimpleStringProperty> object)
        {
            return object.stream()
                    .map(p -> p.get())
                    .map(v -> ValueType.TEXT.getConverter().toString(v)) // escape special characters
                    .collect(Collectors.joining(","));
        }

        @Override
        public List<SimpleStringProperty> fromString(String string)
        {
            return Arrays.stream(string.replace("\\,", "~~").split(",")) // change comma escape sequence to avoid splitting by it
                    .map(s -> s.replace("~~", "\\,"))
                    .map(v -> (String) ValueType.TEXT.getConverter().fromString(v)) // unescape special characters
                    .map(s -> new SimpleStringProperty(s))
                    .collect(Collectors.toList());
        }
    };
    
//    public Categories(CharSequence contentLine)
//    {
//        super();
//        setConverter(CONVERTER);
//        parseContent(contentLine);
//    }
    
    public Categories(List<SimpleStringProperty> values)
    {
        this();
        setValue(values);
    }
    
    /** Constructor with varargs of property values 
     * Note: Do not use to parse the content line.  Use static parse method instead.*/
    public Categories(String...values)
    {
        this();
        List<SimpleStringProperty> value = Arrays.stream(values)
                .map(s -> new SimpleStringProperty(s))
                .collect(Collectors.toList());
        setValue(value);
    }
    
    public Categories(Categories source)
    {
        super(source);
    }
    
    public Categories()
    {
        super();
        setConverter(CONVERTER);
    }

    public static Categories parse(String propertyContent)
    {
        Categories property = new Categories();
        property.parseContent(propertyContent);
        return property;
    }
}
