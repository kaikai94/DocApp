package com.example.docapp.classes;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.docapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class Doctors extends Users
{
    private String badgeNo;
    private String specialityId;

    public Doctors()
    {

    }


    public String getBadgeNo() {
        return badgeNo;
    }

    public void setBadgeNo(String badgeNo) {
        this.badgeNo = badgeNo;
    }

    public String getSpecialityId() {
        return specialityId;
    }

    public void setSpecialityId(String specialityId) {
        this.specialityId = specialityId;
    }

    public void setData(String name, String badgeNo, String ic_no, int age, String specialityId)
    {
        setName(name);
        setIc_no(ic_no);
        setAge(age);
        setBadgeNo(badgeNo);
        setSpecialityId(specialityId);

    }
}
