package synowiec.application.Controller;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;

public class SessionManagerTest {

    private SessionManager sessionManager;
    private final String id = "2";
    private final String name = "Adam";
    private final String email = "nowak@gmail.com";
    private final String photo = "photo";
    private final String surname = "Nowak";
    private HashMap<String, String> user = new HashMap<>();
    private SharedPreferences getSharedPreferences() {
        return getInstrumentation().getTargetContext()
                .getSharedPreferences("LOGIN", 0);
    }

    @Before
    public void setUp() throws Exception {
        this.sessionManager = new SessionManager(getInstrumentation().getTargetContext());
    }
    @After
    public void tearDown() throws Exception {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();
    }
    @Test
    public void shouldReturnRightSetting() {
        //Given
        sessionManager.createSession(id,name,email, photo, surname);
        //When
        user=sessionManager.getUserDetail("patient");
        String actualId = user.get(sessionManager.ID);
        //Then
        assertEquals(id, actualId);
    }
    @Test
    public void shouldReturnNullForNotExistentSetting() {
        user=sessionManager.getUserDetail("patient");
        String actualId = user.get(sessionManager.ID);
        assertEquals(null, actualId);
    }
}