package com.example.alocadochatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alocadochatapp.databinding.ActivitySignInBinding;
import com.example.alocadochatapp.utilities.Constants;
import com.example.alocadochatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //giriş yaptıktan sonra açılış ekranı
        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

//kayıt olma ekranı ve giriş yapma ekranı

    private void setListeners(){
        binding.textCreateNewAccount.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class)));
        binding.buttonSignIn.setOnClickListener(v -> {
            if (isValidSignInDetails()) {
                signIn();
            }
        });
    }
//Giriş yapma ekranı

    private void signIn(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        loading(false);
                        showToast("Giriş Yapılamadı");
                    }
                });
    }
//yüklenme ekranı
    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setVisibility(View.VISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
 //Yanlış giriş yapınca gözüken

    private Boolean isValidSignInDetails() {
        if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Mail Girin");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Geçerli Bir Mail Girin");
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Şifre Girin");
            return false;
        } else {
            return true;
        }
    }
}