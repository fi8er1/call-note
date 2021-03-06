package com.byteshaft.callnote;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.TypedValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Helpers extends ContextWrapper {

    public Helpers(Context base) {
        super(base);
    }

    public static final String LOG_TAG = "";

    static String logTag(Class presentClass) {
        return LOG_TAG + "/" + presentClass.getSimpleName();
    }

    TelephonyManager getTelephonyManager() {
        return (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    }

    float getDensityPixels(int pixels) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, pixels, getResources().getDisplayMetrics());
    }

    private Cursor getAllContacts(ContentResolver cr) {
        return cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );
    }

    List<String> getAllContactNames() {
        List<String> contactNames = new ArrayList<>();
        Cursor cursor = getAllContacts(getContentResolver());
        while (cursor.moveToNext()) {
            String name = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            contactNames.add(name);
        }
        cursor.close();
        return contactNames;
    }

    List<String> getAllContactNumbers() {
        List<String> contactNumbers = new ArrayList<>();
        Cursor cursor = getAllContacts(getContentResolver());
        while (cursor.moveToNext()) {
            String number = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactNumbers.add(number);
        }
        cursor.close();
        return contactNumbers;
    }

    public boolean contactExists(String number, ContentResolver contentResolver) {
        Cursor phones = getAllContacts(contentResolver);
        while (phones.moveToNext()){
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(PhoneNumberUtils.compare(number, phoneNumber)){
                return true;
            }
        }
        return false;
    }

    boolean contactExistsInWhitelist(String number, String checkedContacts) {
        boolean contactExistsInWhitelist = false;
        String[] checkContactsArray = getCheckedContacts(checkedContacts);
        for(String contact : checkContactsArray) {
            if (PhoneNumberUtils.compare(contact, number)) {
                contactExistsInWhitelist = true;
            }
        }
        return contactExistsInWhitelist;
    }

    String[] getCheckedContacts(String checkedContacts) {
        return checkedContacts.split(",");
    }

//    void getCheckedContactsFromSharedPrefrence(List<String> contactNumber) {
//        String[] checkedContacts = getCheckedContacts();
//        int i = 0;
//        for (String contact : contactNumber) {
//            for (String checkedContact: checkedContacts) {
//                if (contact.equals(checkedContact)) {
//                    ContactsAdapter.mCheckStates.put(i, true);
//                }
//            }
//            i++;
//        }
//    }

    String[] getCheckedContacts() {
        String string = getPreferenceManager().getString("checkedContactsPrefs", " ");
        return string.split(",");
    }

    String getCurrentDateandTime() {
        Date formattedDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyy h:mm a zz");
        String date = sdf.format(new Date());
//        try {
//            formattedDate = sdf.parse(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        return date;
    }

    void saveServiceStateEnabled(boolean enable) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean("enabled", enable).apply();
    }

    boolean isServiceSettingEnabled() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext());
        return sharedPreferences.getBoolean("enabled", false);
    }

    SharedPreferences getPreferenceManager() {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    void putTemporaryPreferenceToPermanent() {
        SharedPreferences preferences = getPreferenceManager();
        String temp = preferences.getString("checkedContactsTemp", null);
        preferences.edit().putString("checkedContactsPrefs", temp).commit();

    }

    void putPermanentPreferenceToTemporary() {
        SharedPreferences preferences = getPreferenceManager();
        String permanent = preferences.getString("checkedContactsPrefs", null);
        preferences.edit().putString("checkedContactsTemp", permanent).commit();
    }

    void saveSpinnerState(String key, int value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putInt(key, value).apply();
    }

    int getSpinnerValue(String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext());
        return sharedPreferences.getInt(key, 0);
    }
}