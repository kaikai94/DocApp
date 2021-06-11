package com.example.docapp.doctor;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.docapp.R;
import com.example.docapp.classes.DoctorSpeciality;
import com.example.docapp.classes.Doctors;
import com.example.docapp.classes.Utilities;
import com.example.docapp.databinding.FragmentDoctorProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static String TAG = "Doctor profile";
    private FragmentDoctorProfileBinding binding;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    Doctors doc;
    TextInputEditText doctor_name_text, doctor_badge_text, age_text, ic_text;
    MaterialAutoCompleteTextView specialityDrpdown;
    Button updateBtn, cancelBtn;
    List<DoctorSpeciality> specialityList = new ArrayList<>();
    int specialityIdPosition;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDoctorProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        doctor_name_text = binding.fdpDoctorNameText;
        doctor_badge_text = binding.fdpDoctorBadgeText;
        age_text = binding.fdpAgeText;
        ic_text = binding.fdpIdentityText;
        specialityDrpdown = binding.fdpSpecialityDropdown;

        updateBtn = binding.updateBtn;
        cancelBtn = binding.cancelBtn;

        List<String> tempList = new ArrayList<>();

        Utilities.getSpecialityList(list -> {
            specialityList = list;
            tempList.clear();
            list.forEach(cat -> tempList.add(cat.getName()));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, tempList);
            specialityDrpdown.setAdapter(adapter);

            DocumentReference userRef = fStore.collection(getResources().getString(R.string.collection_accounts)).document(fAuth.getUid());
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot userDocument = task.getResult();
                    if (userDocument.exists()) {

                        DocumentReference docRef = fStore.collection(getResources().getString(R.string.collection_doctors)).document(fAuth.getUid());
                        docRef.get().addOnCompleteListener(docTask -> {
                            if (docTask.isSuccessful()) {
                                DocumentSnapshot doctorDocument = docTask.getResult();
                                if (doctorDocument.exists()) {
                                    doc = doctorDocument.toObject(Doctors.class);

                                    DocumentReference speRef = fStore.collection(getResources().getString(R.string.collection_doctors_speciality)).document(doc.getSpecialityId());
                                    speRef.get().addOnCompleteListener(speTask -> {
                                        if (speTask.isSuccessful()) {
                                            DocumentSnapshot speDocument = speTask.getResult();
                                            if (speDocument.exists()) {
                                                //doc.setSpecialityName(speDocument.getString(getResources().getString(R.string.name)));
                                                doc.setSpecialityId(speDocument.getId());

                                                doctor_name_text.setText(doc.getName());
                                                doctor_badge_text.setText(doc.getBadgeNo());
                                                age_text.setText(String.valueOf(doc.getAge()));
                                                ic_text.setText(doc.getIc_no());
                                                specialityDrpdown.setText(adapter.getItem(adapter.getPosition(speDocument.getString(getResources().getString(R.string.name)))),false);
                                            }
                                        }
                                    });
                                }
                            }
                        });

                    }
                }
            });
        });

        updateBtn.setOnClickListener(v -> {
            DocumentReference userRef = fStore.collection(getResources().getString(R.string.collection_doctors)).document(fAuth.getUid());

            doc.setData(
                    doctor_name_text.getText().toString(),
                    doctor_badge_text.getText().toString(),
                    ic_text.getText().toString(),
                    Integer.parseInt(age_text.getText().toString()),
                    specialityList.get(specialityIdPosition).getDocumentId()
            );

            userRef
                    .set(doc)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Successfully update profile!", Toast.LENGTH_SHORT);
                            //Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Fail to update profile!", Toast.LENGTH_SHORT);
                            //Log.w(TAG, "Error writing document", e);
                        }
                    });

        });

        specialityDrpdown.setOnItemClickListener((parent, view, position, id) -> {
            specialityIdPosition = position;
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
