package com.example.eventapp.ui.exit;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventapp.DetailProductActivity;
import com.example.eventapp.LoginActivity;
import com.example.eventapp.Main2Activity;
import com.example.eventapp.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class ExitFragment extends Fragment {

    private ExitViewModel mViewModel;
    private FirebaseAuth mAuth;

    public static ExitFragment newInstance() {
        return new ExitFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View mView =inflater.inflate(R.layout.exit_fragment, container, false);

        ClearDataUser();
        SendLoginActivity();

        return mView;
    }

    @Override
    
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ExitViewModel.class);
        // TODO: Use the ViewModel
    }

    private void ClearDataUser() {
        // close session in firebase
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        SharedPreferences preferences = this.getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        String typeAuthentication = preferences.getString("typeAuthentication",null);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        if(typeAuthentication.equals("Google")){
            // Configure Google Sign In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getContext(), gso);
            googleSignInClient.signOut(); // close session in Google
            Log.i("Autentication", "Google exit");
        }
        if(typeAuthentication.equals("Facebook")){
            // Configure Google Sign In
            LoginManager.getInstance().logOut();
            Log.i("Autentication", "Facebook exit");
        }
        Log.i("Autentication", "exit Autentication");

    }

    private void SendLoginActivity(){
        // Send Activity Login
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}