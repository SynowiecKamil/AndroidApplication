package synowiec.application.Controller.PatientActivities;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import synowiec.application.Controller.Helpers.MyAppointmentsAdapter;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class PatientRegisterActivityTest {

    private PatientRegisterActivity patientRegisterActivity;

    @Before
    public void setUp() throws Exception {
        ActivityScenario.launch(PatientRegisterActivity.class);
    }

    @Test
    public void shouldReturnRight(){
            final String input = "pacjent@gmail.com";
            boolean verify = patientRegisterActivity.checkEmail(input);
            assertEquals(true, verify);// Your test code goes here.
    }

}