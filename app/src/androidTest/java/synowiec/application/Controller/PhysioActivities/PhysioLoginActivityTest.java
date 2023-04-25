package synowiec.application.Controller.PhysioActivities;

import android.app.Activity;
import android.app.Instrumentation;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;

import org.junit.Before;
import org.junit.Test;

import synowiec.application.Controller.MainActivity;
import synowiec.application.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class PhysioLoginActivityTest {

    Instrumentation.ActivityMonitor monitor = getInstrumentation()
            .addMonitor(PhysioDashboardActivity.class.getName(), null, false);
    @Before
    public void setUp() throws Exception {
        ActivityScenario.launch(PhysioLoginActivity.class);
    }

    @Test
    public void loginCorrectly(){
        onView(withId(R.id.email)).perform(ViewActions.typeText("anna@gmail.com"));
        onView(withId(R.id.password)).perform(ViewActions.typeText("1234"));
        onView(withId(R.id.btn_login)).perform(ViewActions.click());
        Activity secondActivity = getInstrumentation()
                .waitForMonitorWithTimeout(monitor, 1000);
        assertNotNull(secondActivity);
    }

    @Test
    public void wrongEmailAndPassword(){
        onView(withId(R.id.email)).perform(ViewActions.typeText("wrongEmail"));
        onView(withId(R.id.password)).perform(ViewActions.typeText("wrongPassword"));
        onView(withId(R.id.btn_login)).perform(ViewActions.click());
        Activity secondActivity = getInstrumentation()
                .waitForMonitorWithTimeout(monitor, 1000);
        onView(withId(R.id.email)).check(matches(hasErrorText("Niepoprawny email")));
        onView(withId(R.id.password)).check(matches(hasErrorText("Niepoprawne hasło")));
        assertNull(secondActivity);
    }

    @Test
    public void wrongPasswordCorrectEmail(){
        onView(withId(R.id.email)).perform(ViewActions.typeText("anna@gmail.com"));
        onView(withId(R.id.password)).perform(ViewActions.typeText("wrongPassword"));
        onView(withId(R.id.btn_login)).perform(ViewActions.click());
        Activity secondActivity = getInstrumentation()
                .waitForMonitorWithTimeout(monitor, 1000);
        onView(withId(R.id.password)).check(matches(hasErrorText("Niepoprawne hasło")));
        assertNull(secondActivity);
    }

    @Test
    public void emptyEmailAndPassword(){
        onView(withId(R.id.btn_login)).perform(ViewActions.click());
        Activity secondActivity = getInstrumentation()
                .waitForMonitorWithTimeout(monitor, 1000);
        onView(withId(R.id.email)).check(matches(hasErrorText("Proszę wprowadź email")));
        onView(withId(R.id.password)).check(matches(hasErrorText("Proszę wprowadź hasło")));
        assertNull(secondActivity);

    }
}