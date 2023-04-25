package synowiec.application.Controller;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;

import org.junit.Before;
import org.junit.Test;

import synowiec.application.Controller.PatientActivities.PatientRegisterActivity;
import synowiec.application.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class MainActivityTest {

    @Before
    public void setUp() throws Exception {
        ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void shouldReturnRight(){
        onView(withId(R.id.btn_patient)).perform(ViewActions.click());
    }

}