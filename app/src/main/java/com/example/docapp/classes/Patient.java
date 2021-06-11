package com.example.docapp.classes;

public class Patient extends Users{
    private int appointmentCount;

    public void setData(String name, String ic_no, int age)
    {
        setIc_no(ic_no);
        setName(name);
        setAge(age);
    }
}
