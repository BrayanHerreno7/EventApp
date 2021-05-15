package com.example.eventapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private int RC_SIGN_IN_Google = 1;
    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();

        Button btn_no_login = (Button) findViewById(R.id.btn_no_login);
        Button btn_Google = (Button) findViewById(R.id.btn_loginGoogle);
        Button btn_Facebook = (Button) findViewById(R.id.btn_loginFacebook);


        btn_no_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loginSuccessful();
            }
        });


        btn_Google.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Configure Google Sign In
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("142736760158-tvdec6tq7gl7m7nv5r16a7u1775ng4hc.apps.googleusercontent.com")
                        .requestEmail()
                        .build();
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN_Google);
            }
        });


        btn_Facebook.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Configure Facebook Sign In
                //LoginManager.getInstance().logInWithReadPermissions((Activity) getApplicationContext(), permissionNeeds);
                LoginManager.getInstance().logInWithReadPermissions((Activity) v.getContext(), Arrays.asList("public_profile","email"));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final com.facebook.login.LoginResult loginResult) {

                        GraphRequest request =  GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject me, GraphResponse response) {

                                        if (response.getError() != null) {
                                            // handle error
                                        } else {
                                            Log.i("Autentication", "DataFacebook ---email: "+ me.optString("email") + " ---Nombre: " +me.optString("first_name") +" "+ me.optString("last_name") );
                                            handleFacebookAccessToken(loginResult,me);
                                        }
                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "last_name,first_name,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Log.i("Autentication", "AuthWithFacebook:" + "Cancelado");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.w("Autentication", "AuthWithFacebook:" + error);
                    }

                });
            }
        });

        sessionValidation(); //valid if there is an open session
    }

    private void sessionValidation(){
        SharedPreferences preferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        String user = preferences.getString("user",null);
        String typeAuthentication = preferences.getString("typeAuthentication",null);
        if(user != null && typeAuthentication != null){
            loginSuccessful();
        }
    }

    public void loginSuccessful(){
        //Create a new Intent
        Intent intent = new Intent(getApplicationContext(),Main2Activity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);//Clear activity stack
        //Start activity
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null){

        }else{

        }
    }

    private void DataUsersGoogle(GoogleSignInAccount account){
        FirebaseUser user = mAuth.getCurrentUser();
        Log.i("Autentication", "signInWithCredential:success" + " UserFirebase:" + user.getUid());
        SharedPreferences preferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  preferences.edit();
        editor.putString("user",account.getDisplayName());
        editor.putString("email",account.getEmail());
        editor.putString("idAccount",user.getUid());
        editor.putString("typeAuthentication","Google");
        editor.commit();
        Log.i("Autentication", "User: " + account.getDisplayName() + " UserID: "+ user.getUid());
    }

    private void DataUsersFacebook(JSONObject me, String idAccount){
        SharedPreferences preferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  preferences.edit();
        editor.putString("user",me.optString("first_name") +" "+ me.optString("last_name"));
        editor.putString("email",me.optString("email"));
        editor.putString("idAccount",idAccount);
        editor.putString("typeAuthentication","Facebook");
        editor.commit();
        Log.i("Autentication", "User: " + me.optString("first_name") +" "+ me.optString("last_name") + "UserID: "+ idAccount);
    }

    private void handleFacebookAccessToken(final com.facebook.login.LoginResult loginResult, JSONObject me) {
        //Log.d("Autentication", "firebaseAuthWithFacebook:" + loginResult.getAccessToken());

        AuthCredential authCredential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
        FirebaseAuth.getInstance().signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();
                    Log.i("Autentication", "signInWithCredential:success" + " UserFirebase:" + user.getUid());
                    Log.i("Autentication", "Facebook_Id: "+ loginResult.getAccessToken().getUserId().toString());
                    DataUsersFacebook(me,user.getUid());
                    loginSuccessful();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.i("Autentication", "signInWithCredential:failure", task.getException());
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN_Google) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                final GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.i("Autentication", "firebaseAuthWithGoogle:" + account.getId());

                AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                FirebaseAuth.getInstance().signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("Autentication", "signInWithCredential:success");
                            DataUsersGoogle(account);
                            loginSuccessful();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Autentication", "signInWithCredential:failure", task.getException());
                            Snackbar.make(getWindow().getDecorView().getRootView(), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();

                        }
                    }
                });
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("Autentication", "Google sign in failed", e);
                Snackbar.make(getWindow().getDecorView().getRootView(), "Authentication Error.", Snackbar.LENGTH_SHORT).show();
                // ...
            }
        }
    }
}