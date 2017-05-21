package com.labs.nipamo.letseat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import static com.labs.nipamo.letseat.R.id.zipcode;
import static com.labs.nipamo.letseat.R.menu.other;

public class SettingsActivity extends AppCompatActivity {

    static final String CURRENTSAVE = "currentSave";
    static final String CUSTOMSAVE = "customSave";
    static final String ZIPCODESAVE = "zipcodeSave";
    public static final String PREFERENCES = "Prefs";
    public String ZIPCODE;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set up the app bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add the back button
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        if (sharedPreferences != null){
            boolean customSave, currentSave;
            customSave = sharedPreferences.getBoolean(CUSTOMSAVE, false);
            currentSave = sharedPreferences.getBoolean(CURRENTSAVE, false);
            ZIPCODE = sharedPreferences.getString(ZIPCODESAVE, null);
            if (customSave){
                findViewById(R.id.customLocation).performClick();
            }
            if (currentSave){
                findViewById(R.id.currentLocation).performClick();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(other, menu);
        return true;
    }

    /* Called when the user presses the "Current Location" radio button */
    public void setCurrent(View view){
        // Set local variables
        RadioButton custom = (RadioButton) findViewById(R.id.customLocation);
        TextView zipCode = (TextView) findViewById(zipcode);
        EditText zipCodeText = (EditText) findViewById(R.id.zipcodeText);

        // Hide custom location stuff
        custom.setChecked(false);
        zipCode.setVisibility(View.INVISIBLE);
        zipCodeText.setVisibility(View.INVISIBLE);

        FindLocation loc = new FindLocation();
        boolean done = false;

        //Check for permission
        if (!loc.checkForPermission(SettingsActivity.this)) {
            // Permission is not granted so request it
            while (!done) {
                done = loc.promptForPermission(SettingsActivity.this);
            }
            // Check if user pressed Deny
            if (!loc.checkForPermission(SettingsActivity.this)){
                // Permission is denied :(
                Toast toast = Toast.makeText(SettingsActivity.this,
                        "Allow permissions to use current location!", Toast.LENGTH_LONG);
                toast.show();
            }else {
                // Set variables so other activities know use current location
                ((FindLocation) getApplicationContext()).setCurrent(true);
                ((FindLocation) getApplicationContext()).setCustom(false);
            }
        }else{
            // Set variables so other activities know use current location
            ((FindLocation) getApplicationContext()).setCurrent(true);
            ((FindLocation) getApplicationContext()).setCustom(false);
        }

        SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putBoolean(CURRENTSAVE, true);
        editor.putBoolean(CUSTOMSAVE, false);
        editor.commit();
    }

    /* Called when the user presses the "Custom Location" radio button */
    public void setCustom(View view){
        // Set local variables
        RadioButton current = (RadioButton) findViewById(R.id.currentLocation);
        TextView zipCode = (TextView) findViewById(zipcode);
        EditText zipCodeText = (EditText) findViewById(R.id.zipcodeText);

        // Hide current location stuff
        current.setChecked(false);
        zipCode.setVisibility(View.VISIBLE);
        zipCodeText.setVisibility(View.VISIBLE);

        if (ZIPCODE != null){
            zipCodeText.setText(ZIPCODE);
        }

        // Set variables so other activities know use custom location
        ((FindLocation) getApplicationContext()).setCurrent(false);
        ((FindLocation) getApplicationContext()).setCustom(true);

        SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putBoolean(CURRENTSAVE, false);
        editor.putBoolean(CUSTOMSAVE, true);
        editor.commit();
    }

    /* Called when the user pressed the "Apply" button */
    public void apply(View view){
        // Set local variables
        EditText zipCodeText = (EditText) findViewById(R.id.zipcodeText);

        // Check which option to apply
        if (((FindLocation) getApplicationContext()).getCustom()){
            ZIPCODE = zipCodeText.getText().toString();
            ((FindLocation) getApplicationContext()).setZip(ZIPCODE);
            SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();
            editor.putString(ZIPCODESAVE, ZIPCODE);
            editor.commit();
        }

        // Go back to the Main Activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}