package synowiec.application.patient;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import synowiec.application.R;
import synowiec.application.helpers.Utils;
import synowiec.application.model.Physiotherapist;

public class PhysioDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText nameET, emailET;
    private Button backBTN;
    private Physiotherapist receivedPhysiotherapist;
    CircleImageView profile_image;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physio_detail);

        initializeWidgets();
        receiveAndShowData();

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openActivity(PhysioDetailActivity.this, PatientSearchActivity.class);
                finish();
            }
        });
    }

    private void receiveAndShowData(){
        receivedPhysiotherapist = Utils.receivePhysiotherapist(getIntent(),PhysioDetailActivity.this);
        if(receivedPhysiotherapist !=null){
            nameET.setText(receivedPhysiotherapist.getName());
            emailET.setText(receivedPhysiotherapist.getEmail());
            Picasso.get().load(receivedPhysiotherapist.getPhoto())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(profile_image);

        }
    }


    @Override
    public void onClick(View v) {
        Utils.openActivity(PhysioDetailActivity.this, PatientSearchActivity.class);
    }

    private void initializeWidgets(){
        nameET = findViewById(R.id.name);
        emailET = findViewById(R.id.email);
        profile_image = findViewById(R.id.profile_image);
        backBTN = findViewById(R.id.btn_back);
    }
}
