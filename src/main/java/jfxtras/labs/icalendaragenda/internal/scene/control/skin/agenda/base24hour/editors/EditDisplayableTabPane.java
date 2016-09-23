package jfxtras.labs.icalendaragenda.internal.scene.control.skin.agenda.base24hour.editors;

import java.io.IOException;
import java.net.URL;
import java.time.temporal.Temporal;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import jfxtras.labs.icalendaragenda.internal.scene.control.skin.agenda.base24hour.DeleteChoiceDialog;
import jfxtras.labs.icalendaragenda.internal.scene.control.skin.agenda.base24hour.Settings;
import jfxtras.labs.icalendaragenda.scene.control.agenda.editors.deleters.SimpleDeleterFactory;
import jfxtras.labs.icalendarfx.VCalendar;
import jfxtras.labs.icalendarfx.components.VComponent;
import jfxtras.labs.icalendarfx.components.VDisplayable;
import jfxtras.labs.icalendarfx.properties.component.descriptive.Summary;
import jfxtras.labs.icalendarfx.properties.component.recurrence.RecurrenceRule;
import jfxtras.labs.icalendarfx.properties.component.recurrence.rrule.FrequencyType;
import jfxtras.labs.icalendarfx.properties.component.recurrence.rrule.Interval;

/** 
 * Base TabPane that contains two tabs for editing descriptive properties and for editing a {@link RecurrenceRule}.
 * The first tab contains a {@link EditDescriptiveVBox }.  The second contains a {@link EditRecurrenceRuleVBox}.
 * 
 * @author David Bal
 * 
 * @param <T> subclass of {@link VDisplayable}
 * @param <U> subclass of {@link EditDescriptiveVBox} associated with the subclass of {@link VDisplayable}
 */
public abstract class EditDisplayableTabPane<T extends VDisplayable<T>, U extends EditDescriptiveVBox<T>> extends TabPane
{
    U editDescriptiveVBox;
    EditRecurrenceRuleVBox<T> recurrenceRuleVBox;

    @FXML private ResourceBundle resources; // ResourceBundle that was given to the FXMLLoader
    @FXML AnchorPane descriptiveAnchorPane;
    @FXML AnchorPane recurrenceRuleAnchorPane;
    @FXML private TabPane editDisplayableTabPane;
    @FXML private Tab descriptiveTab;
    @FXML private Tab recurrenceRuleTab;
    
    @FXML private Button cancelComponentButton;
    @FXML private Button saveComponentButton;
    @FXML private Button deleteComponentButton;
    @FXML private Button cancelRepeatButton;
    @FXML private Button saveRepeatButton;

//    ObjectProperty<Boolean> isFinished = new SimpleObjectProperty<>(false);
//    /** When property value becomes true the control should be closed
//     * (i.e. Attach a listener to this property, on changing hide the control */
//    public ObjectProperty<Boolean> isFinished() { return isFinished; }

    ObjectProperty<List<VCalendar>> iTIPMessages = new SimpleObjectProperty<>();
    public ObjectProperty<List<VCalendar>> iTIPMessagesProperty() { return iTIPMessages; }
    @Deprecated
    ObjectProperty<List<T>> newVComponents = new SimpleObjectProperty<>();
    /** This property contains a List of new components resulting from the editing.
     * When property value becomes non-null the control should be closed.
     * (i.e. Attach a listener to this property, on changing hide the control */
//    @Deprecated
//    public ObjectProperty<List<T>> newVComponentsProperty() { return newVComponents; }
    
    public EditDisplayableTabPane( )
    {
        super();
        loadFxml(EditDescriptiveVBox.class.getResource("EditDisplayable.fxml"), this);
    }
    
    @FXML    @Deprecated
    void handleSaveButton()
    {
        removeEmptyProperties();
    }

    void removeEmptyProperties()
    {
        if (vComponent.getRecurrenceRule() != null)
        {
            if (recurrenceRuleVBox.frequencyComboBox.getValue() == FrequencyType.WEEKLY && recurrenceRuleVBox.dayOfWeekList.isEmpty())
            {
                canNotHaveZeroDaysOfWeek();
            } else if (! vComponent.getRecurrenceRule().isValid())
            {
                throw new RuntimeException("Unhandled component error" + System.lineSeparator() + vComponent.errors());
            }
        }
        
        if (editDescriptiveVBox.summaryTextField.getText().isEmpty())
        {
            vComponent.setSummary((Summary) null); 
        }

       // nullify Interval if value equals default (avoid unnecessary content output)
        if ((vComponent.getRecurrenceRule() != null) && (recurrenceRuleVBox.intervalSpinner.getValue() == Interval.DEFAULT_INTERVAL))
        {
            vComponent.getRecurrenceRule().getValue().setInterval((Interval) null); 
        }
    }
    
    @FXML private void handleCancelButton()
    {
        // TODO - WITH PUBLISH IN MIND, I THINK THIS SHOULD RETURN NULL (MUST BE EDITING A COPY OF ORIGINAL THAT IS ABANDONED)
//        vComponents.remove(vComponent);
//        vComponents.add(vComponentOriginalCopy);
        iTIPMessagesProperty().set(Collections.emptyList());
//        newVComponentsProperty().set(Arrays.asList(vComponentOriginalCopy)); // indicates control should be hidden
//        isFinished.set(true);
    }
    
    @FXML private void handleDeleteButton()
    {
        removeEmptyProperties();
        Object[] params = new Object[] {
                DeleteChoiceDialog.DELETE_DIALOG_CALLBACK,
                editDescriptiveVBox.startOriginalRecurrence
//                vComponents
        };
        List<VCalendar> result = SimpleDeleterFactory.newDeleter(vComponent, params).delete();
        iTIPMessagesProperty().set(result);

        
//        T result = (T) SimpleDeleterFactory.newDeleter(vComponent, params).delete();
//        newVComponentsProperty().set(Arrays.asList(result)); // indicates control should be hidden
//        isFinished.set(result);
    }
    
    @FXML private void handlePressEnter(KeyEvent e)
    {
        if (e.getCode().equals(KeyCode.ENTER))
        {
            handleSaveButton();
        }
    }
    
    T vComponent;
    T vComponentOriginalCopy;
//    List<T> vComponents;

    /**
     * Provide necessary data to setup
     * 
     * @param vComponent - component to be edited
     * @param vComponents - List of {@link VComponent} that the vComponent parameter is a member
     * @param startRecurrence - start of selected recurrence
     * @param endRecurrence - end of selected recurrence
     * @param categories - list of category names
     */
    public void setupData(
            T vComponent,
//            List<T> vComponents,
            Temporal startRecurrence,
            Temporal endRecurrence,
            List<String> categories
            )
    {
        this.vComponent = vComponent;
//        this.vComponents = vComponents;
        editDescriptiveVBox.setupData(vComponent, startRecurrence, endRecurrence, categories);
        
        /* 
         * Shut off repeat tab if vComponent is not a parent
         * Components with RECURRENCE-ID can't add repeat rules (only parent can have repeat rules)
         */
        if (vComponent.getRecurrenceId() != null)
        {
            recurrenceRuleTab.setDisable(true);
            recurrenceRuleTab.setTooltip(new Tooltip(resources.getString("repeat.tab.unavailable")));
        }
        recurrenceRuleVBox.setupData(vComponent, editDescriptiveVBox.startRecurrenceProperty);
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
    
    protected static void loadFxml(URL fxmlFile, Object rootController)
    {
        FXMLLoader loader = new FXMLLoader(fxmlFile);
        loader.setController(rootController);
        loader.setRoot(rootController);
        loader.setResources(Settings.resources);
        try {
            loader.load();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
 
