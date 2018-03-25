package com.example.urvash.shehrotees;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private final int RC_SIGN_IN = 123;
    private final double version_code = 1.0;
    private final String EULA_KEY = "Hion Events" + String.valueOf(version_code);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

/*        final Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                final SharedPreferences pref1 = PreferenceManager
//                        .getDefaultSharedPreferences(getBaseContext());
//
//                //  Create a new boolean and preference and set it to true
//                boolean isFirstStart = pref1.getBoolean("firstStart", true);
//
//                //  If the activity has never started before...
//                if (isFirstStart) {
//
//                    //  Launch app intro
//                    final Intent i = new Intent(LoginActivity.this, IntroActivity.class);
//
//                    runOnUiThread(new Runnable() {
//                        @Override public void run() {
//                            startActivity(i);
//                        }
//                    });
//
//                    //  Make a new preferences editor
//                    SharedPreferences.Editor e = pref1.edit();
//
//                    //  Edit preference to make it false because we don't want this to run again
//                    e.putBoolean("firstStart", false);
//
//                    //  Apply changes
//                    e.apply();
//                }
//            }
        });
*/

        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null){
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(
                                            Arrays.asList(
                                                    new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()
                                    ))
                                    .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                                    .setLogo(R.drawable.edc)
                                    .setTheme(R.style.AppTheme)
                                    .build(),
                            RC_SIGN_IN);
                }
                else{
                    showEULA();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == ResultCodes.OK) {
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, "Pressed Back Button", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "Unknown Error", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    private void showEULA() {
        Log.d("showEULA","Entering the function");

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isAccepted = pref.getBoolean(EULA_KEY,false);

        if(!isAccepted){
            String title = "Terms and Conditions";

            String eula = "Order Shahrotees";

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title)
                    .setMessage(eula)
                    .setCancelable(false)
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putBoolean(EULA_KEY,true);
                            editor.apply();

                            dialog.dismiss();
                            startActivity(new Intent(LoginActivity.this,TestActivity.class));
                        }
                    })
                    .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(LoginActivity.this,
                                    "Terms and Conditions must be accepted",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
            builder.create().show();
        }else{
            startActivity(new Intent(LoginActivity.this,TestActivity.class));
        }
    }
}
