package com.dlsu.getbetter.getbetter.objects;

/**
 * Created by mikedayupay on 27/01/2016.
 * GetBetter 2016
 */
public class Patient {

    private long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String birthdate;
    private String age;
    private String gender;
    private String civilStatus;
    private String profileImageBytes;
    private boolean checked = false;


    public Patient(long id, String firstName, String middleName, String lastName,
                   String birthdate, String gender, String civilStatus, String profileImageBytes) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.gender = gender;
        this.civilStatus = civilStatus;
        this.profileImageBytes = profileImageBytes;
    }

    public Patient(String firstName, String middleName, String lastName, String birthdate,
                   String gender, String civilStatus, String profileImageBytes) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.gender = gender;
        this.civilStatus = civilStatus;
        this.profileImageBytes = profileImageBytes;
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getCivilStatus() {
        return civilStatus;
    }

    public String getProfileImageBytes() {
        return profileImageBytes;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void toggleChecked() {
        checked = !checked;
    }
}
