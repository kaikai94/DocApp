package com.example.docapp.patient.ui.appointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.example.docapp.R;
import com.example.docapp.classes.DoctorSpeciality;
import com.example.docapp.classes.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.example.docapp.databinding.ActivityAppointmentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;

public class AppointmentActivity extends AppCompatActivity {
    TextInputEditText nameText, appointmentDateText, statusText;
    MaterialAutoCompleteTextView specialityDrpDown, appointmentTimeDrpDown;
    Button update_btn, cancel_btn;

    private static String TAG = "Appointment Activity";
    private ActivityAppointmentBinding binding;
    List<DoctorSpeciality> specialityList = new ArrayList<>();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        binding = ActivityAppointmentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        nameText = binding.aaNameText;
        specialityDrpDown = binding.aaSpecialityDrpDown;
        appointmentDateText = binding.aaApointmentDatePicker;
        appointmentTimeDrpDown = binding.aaAppointmentTimeDrpDown;
        statusText = binding.aaStatusText;
        update_btn = binding.updateBtn;
        cancel_btn = binding.cancelBtn;

        //Get string array and set adapter to dropdown
        ArrayAdapter<CharSequence> aa = ArrayAdapter.createFromResource(this, R.array.timeslot, android.R.layout.simple_list_item_1);
        appointmentTimeDrpDown.setAdapter(aa);

        List<String> tempList = new ArrayList<>();
        /*Utilities.GetSpecialityList(list -> {
            specialityList = list;
            tempList.clear();
            list.forEach(cat -> tempList.add(cat.getName()));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tempList);
            specialityDrpDown.setAdapter(adapter);
        });*/

        setData();

        if(getIntent().hasExtra(getString(R.string.history)))
        {
            update_btn.setVisibility(View.GONE);
            cancel_btn.setVisibility(View.GONE);
            nameText.setEnabled(false);
            setData();
        }
        else if(getIntent().hasExtra(getString(R.string.modify)))
        {
            update_btn.setVisibility(View.VISIBLE);
            cancel_btn.setVisibility(View.VISIBLE);
            nameText.setEnabled(true);
            setData();

        }
    }

    //Get data online and bind
    private void setData()
    {
        if(getIntent().hasExtra(getString(R.string.appointment_id)))
        {
            fStore.collection(getResources().getString(R.string.collection_appointments))
                    .document(getIntent().getStringExtra(getResources().getString(R.string.appointment_id)))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                    nameText.setText(document.getString(getString(R.string.name)));
                                    //doctor_speciality_textview.setText(document.getString(getString(R.string.specialityName)));
                                    DateTime dt = new DateTime( document.getTimestamp(getResources().getString(R.string.date_time)).getSeconds() * 1000 ) ;
                                    DateTime dtMalaysia = dt.withZone(DateTimeZone.forID("Asia/Kuala_Lumpur"));
                                    //appointment_date_textview.setText(dtMalaysia.toString("yyyy-MM-dd"));
                                    //appointment_time_textview.setText(dtMalaysia.toString("h:mm a"));
                                    statusText.setText(document.getString(getString(R.string.status)));
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
        }
    }

}