package com.dlsu.getbetter.getbetter.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dlsu.getbetter.getbetter.objects.Attachment;
import com.dlsu.getbetter.getbetter.objects.CaseRecord;
import com.dlsu.getbetter.getbetter.objects.HealthCenter;
import com.dlsu.getbetter.getbetter.objects.Patient;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mikedayupay on 10/01/2016.
 */
public class DataAdapter {

    protected static final String TAG = "DataAdapter";

    private int gOpenCounter;

    private final Context myContext;
    private SQLiteDatabase getBetterDb;
    private DatabaseHelper getBetterDatabaseHelper;

    private static final String USER_TABLE = "tbl_users";
    private static final String USER_TABLE_UPLOAD = "tbl_users_upload";
    private static final String CASE_RECORD_TABLE = "tbl_case_records";
    private static final String CASE_RECORD_TABLE_UPLOAD = "tbl_case_records_upload";
    private static final String CASE_RECORD_HISTORY_TABLE = "tbl_case_record_history";
    private static final String CASE_RECORD_ATTACHMENTS_TABLE_UPLOAD = "tbl_case_record_attachments_upload";
    private static final String CASE_RECORD_ATTACHMENTS_TABLE = "tbl_case_record_attachments";
    private static final String HEALTH_CENTER_TABLE = "tbl_health_centers";


    public DataAdapter(Context context) {
        this.myContext = context;
        getBetterDatabaseHelper = DatabaseHelper.getInstance(myContext);
    }

    public DataAdapter createDatabase() throws SQLException {

        try {
            getBetterDatabaseHelper.createDatabase();
        }catch (IOException ioe) {
            Log.e(TAG, ioe.toString() + "UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public synchronized DataAdapter openDatabase() throws SQLException {

        gOpenCounter++;

        if(gOpenCounter == 1) {

            try {
                getBetterDatabaseHelper.openDatabase();
                getBetterDatabaseHelper.close();
                getBetterDb = getBetterDatabaseHelper.getWritableDatabase();
            }catch (SQLException sqle) {
                Log.e(TAG, "open >>" +sqle.toString());
                throw sqle;
            }

        }

        return this;
    }

    public synchronized void closeDatabase() {

        gOpenCounter--;

        if(gOpenCounter == 0) {

            getBetterDb.close();
        }
    }

    public boolean checkLogin (String username, String password) {

        String sql = "SELECT * FROM tbl_users WHERE email = '" + username + "' AND pass = '" + password + "'";

        Cursor c = getBetterDb.rawQuery(sql, null);

        if(c.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public long insertPatientInfo(String firstName, String middleName, String lastName,
                                  String birthDate, String gender, String civilStatus,
                                  String profileImage) {

        int genderId;
        int civilId;
        long rowId;

        String genderSql = "SELECT _id FROM tbl_genders WHERE gender_name = '" + gender + "'";
        Cursor cGender = getBetterDb.rawQuery(genderSql, null);
        cGender.moveToFirst();
        genderId = cGender.getInt(cGender.getColumnIndex("_id"));
        cGender.close();

        String civilSql = "SELECT _id FROM tbl_civil_statuses WHERE civil_status_name = '" + civilStatus + "'";
        Cursor cCivil = getBetterDb.rawQuery(civilSql, null);
        cCivil.moveToFirst();
        civilId = cCivil.getInt(cCivil.getColumnIndex("_id"));
        cCivil.close();

        ContentValues cv = new ContentValues();
        cv.put("first_name", firstName);
        cv.put("middle_name", middleName);
        cv.put("last_name", lastName);
        cv.put("birthdate", birthDate);
        cv.put("gender_id", genderId);
        cv.put("civil_status_id", civilId);
        cv.put("profile_url", profileImage);
        cv.put("role_id", 6);

        rowId = getBetterDb.insert(USER_TABLE, null, cv);

        cv.put("_id", rowId);

        getBetterDb.insert(USER_TABLE_UPLOAD, null, cv);

        return rowId;
    }

    public long insertCaseRecord (int caseRecordId, long userId, int healthCenterId, String complaint,
                                  String controlNumber) {

        long rowId;

        ContentValues cv = new ContentValues();
        cv.put("_id", caseRecordId);
        cv.put("user_id", userId);
        cv.put("health_center_id", healthCenterId);
        cv.put("complaint", complaint);
        cv.put("control_number", controlNumber);

        rowId = getBetterDb.insert(CASE_RECORD_TABLE, "case_id", cv);
        getBetterDb.insert(CASE_RECORD_TABLE_UPLOAD, "case_id", cv);

        return rowId;
    }

    public long insertCaseRecordHistory(int caseRecordId, int recordStatusId, String midwifeName,
                                        String dateUpdated) {

        long rowId;

        ContentValues cv = new ContentValues();
        cv.put("_id", caseRecordId);
        cv.put("record_status_id", recordStatusId);
        cv.put("updated_by", midwifeName);
        cv.put("updated_on", dateUpdated);

        rowId = getBetterDb.insert(CASE_RECORD_HISTORY_TABLE, null, cv);

        return rowId;
    }

    public long insertCaseRecordAttachments(int caseRecordId, String description, String caseFileUrl,
                                            int caseRecordAttachmentTypeId, String uploadedDate) {

        long rowId;

        ContentValues cv = new ContentValues();
        cv.put("case_record_id", caseRecordId);
        cv.put("description", description);
        cv.put("case_file_url", caseFileUrl);
        cv.put("case_record_attachment_type_id", caseRecordAttachmentTypeId);
        cv.put("uploaded_on", uploadedDate);

        rowId = getBetterDb.insert(CASE_RECORD_ATTACHMENTS_TABLE, null, cv);
        getBetterDb.insert(CASE_RECORD_ATTACHMENTS_TABLE_UPLOAD, null, cv);

        return rowId;
    }

    public ArrayList<Patient> getPatients (int healthCenterId) {

        ArrayList<Patient> results = new ArrayList<>();

        String sql = "SELECT u._id AS id, u.first_name AS first_name, u.middle_name AS middle_name, " +
                "u.last_name AS last_name, u.birthdate AS birthdate, g.gender_name AS gender, " +
                "c.civil_status_name AS civil_status, u.profile_url AS image " +
                "FROM tbl_users AS u, tbl_genders AS g, tbl_civil_statuses AS c " +
                "WHERE u.gender_id = g._id AND u.civil_status_id = c._id AND u.role_id = 6";

        Cursor c = getBetterDb.rawQuery(sql, null);

        Log.e("cursor", c.getCount() + "");
        while (c.moveToNext()) {
            Patient patient = new Patient(c.getLong(c.getColumnIndexOrThrow("id")),
                    c.getString(c.getColumnIndexOrThrow("first_name")),
                    c.getString(c.getColumnIndexOrThrow("middle_name")),
                    c.getString(c.getColumnIndexOrThrow("last_name")),
                    c.getString(c.getColumnIndexOrThrow("birthdate")),
                    c.getString(c.getColumnIndexOrThrow("gender")),
                    c.getString(c.getColumnIndexOrThrow("civil_status")),
                    c.getString(c.getColumnIndexOrThrow("image")));

            results.add(patient);
        }

        Log.e("data patient", results.size() + "");
        c.close();
        return results;
    }

    public ArrayList<Patient> getPatientsUpload (int healthCenterId) {

        ArrayList<Patient> results = new ArrayList<>();

        String sql = "SELECT u._id AS id, u.first_name AS first_name, u.middle_name AS middle_name, " +
                "u.last_name AS last_name, u.birthdate AS birthdate, g.gender_name AS gender, " +
                "c.civil_status_name AS civil_status, u.profile_url AS image " +
                "FROM tbl_users_upload AS u, tbl_genders AS g, tbl_civil_statuses AS c " +
                "WHERE u.gender_id = g._id AND u.civil_status_id = c._id AND u.role_id = 6";

        Cursor c = getBetterDb.rawQuery(sql, null);

        Log.e("cursor", c.getCount() + "");
        while (c.moveToNext()) {
            Patient patient = new Patient(c.getLong(c.getColumnIndexOrThrow("id")),
                    c.getString(c.getColumnIndexOrThrow("first_name")),
                    c.getString(c.getColumnIndexOrThrow("middle_name")),
                    c.getString(c.getColumnIndexOrThrow("last_name")),
                    c.getString(c.getColumnIndexOrThrow("birthdate")),
                    c.getString(c.getColumnIndexOrThrow("gender")),
                    c.getString(c.getColumnIndexOrThrow("civil_status")),
                    c.getString(c.getColumnIndexOrThrow("image")));

            results.add(patient);
        }

        c.close();
        return results;
    }

    public Patient getPatient (long patientId) {

        String sql = "SELECT u._id AS id, u.first_name AS first_name, u.middle_name AS middle_name, " +
                "u.last_name AS last_name, u.birthdate AS birthdate, g.gender_name AS gender, " +
                "c.civil_status_name AS civil_status, u.profile_url AS image " +
                "FROM tbl_users AS u, tbl_genders AS g, tbl_civil_statuses AS c " +
                "WHERE u.gender_id = g._id AND u.civil_status_id = c._id AND u._id = " + patientId;

        Cursor c = getBetterDb.rawQuery(sql, null);

        Log.e("cursor", c.getCount() + "");

        c.moveToFirst();
        Patient patient = new Patient(c.getLong(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("first_name")),
                c.getString(c.getColumnIndexOrThrow("middle_name")),
                c.getString(c.getColumnIndexOrThrow("last_name")),
                c.getString(c.getColumnIndexOrThrow("birthdate")),
                c.getString(c.getColumnIndexOrThrow("gender")),
                c.getString(c.getColumnIndexOrThrow("civil_status")),
                c.getString(c.getColumnIndexOrThrow("image")));

        c.close();
        return patient;

    }

    public void removePatientUpload (int userId) {

        getBetterDb.delete(USER_TABLE_UPLOAD, "_id = " + userId, null);

    }

    public ArrayList<HealthCenter> getHealthCenters() {

        ArrayList<HealthCenter> results = new ArrayList<>();

        String sql = "SELECT _id, health_center_name FROM " + HEALTH_CENTER_TABLE;

        Cursor c = getBetterDb.rawQuery(sql, null);

        while (c.moveToNext()) {
            HealthCenter hc = new HealthCenter(c.getInt(c.getColumnIndexOrThrow("_id")),
                    c.getString(c.getColumnIndexOrThrow("health_center_name")));

            results.add(hc);
        }
        c.close();
        return results;
    }

    public int getHealthCenterId(String healthCenter) {

        int result;
        String sql = "SELECT _id FROM tbl_health_centers WHERE health_center_name = '" + healthCenter + "'";

        Cursor c = getBetterDb.rawQuery(sql, null);

        c.moveToFirst();
        result = c.getInt(c.getColumnIndex("_id"));

        c.close();
        return result;
    }

    public ArrayList<Integer> getCaseRecordIds () {

        ArrayList<Integer> ids = new ArrayList<>();

        String sql = "SELECT _id FROM tbl_case_records";
        Cursor c = getBetterDb.rawQuery(sql, null);

        while(c.moveToNext()) {
            ids.add(c.getInt(c.getColumnIndexOrThrow("_id")));
        }

        c.close();
        return ids;
    }

    public int getAttachmentTypeId (String attachmentType) {

        int result = 0;
        String sql = "SELECT _id FROM tbl_case_record_attachment_types WHERE case_record_attachment_type_name = '"
                + attachmentType + "'";

        Cursor c = getBetterDb.rawQuery(sql, null);

        c.moveToFirst();
        result = c.getInt(c.getColumnIndexOrThrow("_id"));
        c.close();
        return result;
    }

    public ArrayList<CaseRecord> getCaseRecords (int patientId) {

        ArrayList<CaseRecord> results = new ArrayList<>();

        String sql = "SELECT * FROM tbl_case_records WHERE user_id = " + patientId;
        Cursor c = getBetterDb.rawQuery(sql, null);

        while(c.moveToNext()) {
            CaseRecord caseRecord = new CaseRecord(c.getInt(c.getColumnIndexOrThrow("_id")),
                    c.getString(c.getColumnIndexOrThrow("complaint")),
                    c.getString(c.getColumnIndexOrThrow("control_number")));

            results.add(caseRecord);
        }
        c.close();
        return results;
    }

    public ArrayList<Attachment> getCaseRecordAttachments (int caseRecordId) {

        ArrayList<Attachment> attachments = new ArrayList<>();
        String sql = "SELECT * FROM tbl_case_record_attachments WHERE case_record_id = " + caseRecordId;

        Cursor c = getBetterDb.rawQuery(sql, null);

        while (c.moveToNext()) {

            Attachment attachment = new Attachment(c.getString(c.getColumnIndexOrThrow("case_file_url")),
                    c.getString(c.getColumnIndexOrThrow("description")));

            attachments.add(attachment);

        }

        return attachments;
    }

    public void updatePatientInfo (Patient patient, long patientId) {

        int genderId;
        int civilId;

        Log.e("gender", patient.getGender());

        String genderSql = "SELECT _id FROM tbl_genders WHERE gender_name = '" + patient.getGender() + "'";
        Cursor cGender = getBetterDb.rawQuery(genderSql, null);
        cGender.moveToFirst();
        genderId = cGender.getInt(cGender.getColumnIndex("_id"));
        cGender.close();

        String civilSql = "SELECT _id FROM tbl_civil_statuses WHERE civil_status_name = '" + patient.getCivilStatus() + "'";
        Cursor cCivil = getBetterDb.rawQuery(civilSql, null);
        cCivil.moveToFirst();
        civilId = cCivil.getInt(cCivil.getColumnIndex("_id"));
        cCivil.close();

        ContentValues cv = new ContentValues();
        cv.put("first_name", patient.getFirstName());
        cv.put("middle_name", patient.getMiddleName());
        cv.put("last_name", patient.getLastName());
        cv.put("birthdate", patient.getBirthdate());
        cv.put("gender_id", genderId);
        cv.put("civil_status_id", civilId);
        cv.put("profile_url", patient.getProfileImageBytes());

        getBetterDb.update(USER_TABLE, cv, "_id = " + patientId, null);

    }

}
