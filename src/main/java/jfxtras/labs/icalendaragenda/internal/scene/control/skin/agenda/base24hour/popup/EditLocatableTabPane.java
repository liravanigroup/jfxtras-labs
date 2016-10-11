package jfxtras.labs.icalendaragenda.internal.scene.control.skin.agenda.base24hour.popup;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import jfxtras.labs.icalendaragenda.internal.scene.control.skin.agenda.base24hour.EditChoiceDialog;
import jfxtras.labs.icalendaragenda.scene.control.agenda.editors.revisors.SimpleRevisorFactory;
import jfxtras.labs.icalendarfx.VCalendar;
import jfxtras.labs.icalendarfx.components.VLocatable;
import jfxtras.labs.icalendarfx.properties.component.descriptive.Description;
import jfxtras.labs.icalendarfx.properties.component.descriptive.Location;
import jfxtras.labs.icalendarfx.properties.component.recurrence.RecurrenceRule;
import jfxtras.labs.icalendarfx.properties.component.recurrence.rrule.FrequencyType;
import jfxtras.labs.icalendarfx.properties.component.recurrence.rrule.RecurrenceRule2;

/** 
 * TabPane for editing descriptive properties and a {@link RecurrenceRule} for a {@link VComponentLocatable}.
 * 
 * @author David Bal
 * 
 * @param <T> subclass of {@link VComponentLocatable}
 * @param <U> subclass of {@link EditDescriptiveVBox} associated with the subclass of {@link VComponentLocatable}
 */
public abstract class EditLocatableTabPane<T extends VLocatable<T>> extends EditDisplayableTabPane<T, EditDescriptiveLocatableVBox<T>>
{
    public EditLocatableTabPane( )
    {
        super();
    }
        
    @Override
    @FXML void handleSaveButton()
    {
        super.handleSaveButton();
        if (vComponentCopy.getRecurrenceRule() != null)
        {
            RecurrenceRule2 rrule = vComponentCopy.getRecurrenceRule().getValue();
            if (rrule.getFrequency().getValue() == FrequencyType.WEEKLY)
            {
                if (recurrenceRuleVBox.dayOfWeekList.isEmpty())
                {
                    canNotHaveZeroDaysOfWeek();
                    return; // skip other operations, allow user to make changes and try again
                }
            }
        }
        Object[] params = new Object[] {
                EditChoiceDialog.EDIT_DIALOG_CALLBACK,
                editDescriptiveVBox.endNewRecurrence,
                editDescriptiveVBox.startOriginalRecurrence,
                editDescriptiveVBox.startRecurrenceProperty.get(),
                vComponentCopy,
                vComponentOriginal
        };
        List<VCalendar> result = SimpleRevisorFactory.newReviser(vComponentCopy, params).revise();
        System.out.println("save:" + result);
        iTIPMessagesProperty().set(result);
    }
    
    @Override
    void removeEmptyProperties()
    {
        super.removeEmptyProperties();
        if (editDescriptiveVBox.descriptionTextArea.getText().isEmpty())
        {
            vComponentCopy.setDescription((Description) null); 
        }
        if (editDescriptiveVBox.locationTextField.getText().isEmpty())
        {
            vComponentCopy.setLocation((Location) null); 
        }
    }
    
    // Displays an alert notifying at least one day of week must be present for weekly frequency
    private static void canNotHaveZeroDaysOfWeek()
    {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Invalid Modification");
        alert.setHeaderText("Please select at least one day of the week.");
        alert.setContentText("Weekly repeat must have at least one selected day");
        ButtonType buttonTypeOk = new ButtonType("OK", ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeOk);
        
        // set id for testing
        alert.getDialogPane().setId("zero_day_of_week_alert");
        alert.getDialogPane().lookupButton(buttonTypeOk).setId("zero_day_of_week_alert_button_ok");
        
        alert.showAndWait();
    }
}
