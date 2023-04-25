package synowiec.application.Controller.Helpers;

import android.content.Context;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import synowiec.application.Controller.SessionManager;
import synowiec.application.Model.Appointment;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class MyAppointmentsAdapterTest {

    private Context context;
    private MyAppointmentsAdapter adatper;
    private List<Appointment> appointmentList = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        this.context = getInstrumentation().getContext();
        this.adatper = new MyAppointmentsAdapter(appointmentList, "patient", context);
    }

    @Test
    public void shouldReturnRight(){
        LatLng input = adatper.getLatLng("Katowice Chłodna 17", context);
        LatLng output = new LatLng(50.2759, 19.0796);
        assertEquals(output, input);
    }
    @Test
    public void shouldReturnNullForEmptyInput(){
        LatLng input = adatper.getLatLng("", context);
        assertEquals(null, input);
    }
    @Test
    public void shouldReturnNullForWrongInput(){
        LatLng input = adatper.getLatLng("wrong defined address", context);
        assertEquals(null, input);
    }
}

