package com.example.myapplication_tasksmanger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    TextInputEditText tName;
    TextInputEditText tEmail;
    TextInputEditText tPhone;
    TextInputEditText tPass;
    TextInputEditText tRePass;
    Button btSave;
    Button btCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        tName = findViewById(R.id.etName);
        tEmail = findViewById(R.id.etEmail);
        tPhone = findViewById(R.id.etPhone);
        tPass = findViewById(R.id.etPassword);
        tRePass = findViewById(R.id.etRePassword);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSignUp();
            }
        });

    }

    private void checkSignUp() {
        boolean isAllOk = true;

        String email = tEmail.getText().toString();
        String name = tName.getText().toString();
        String phone = tPhone.getText().toString();
        String password = tPass.getText().toString();
        String Repassword = tRePass.getText().toString();

        if (phone.length() < 10) {
            isAllOk = false;
            tPhone.setError("wrong phone number");
        }
        if (name.length() < 1) {
            isAllOk = false;
            tName.setError("must enter your name");
        }
        if (email.length() < 6 || email.contains("@") == false) {
            isAllOk = false;
            tEmail.setError("Wrong Email");
        }
        if (password.length() < 8 || password.contains(" ") == true) {
            isAllOk = false;
            tPass.setError("Wrong Password");
        }
        if (Repassword.length() < 8 || Repassword.contains(" ") == true) {
            isAllOk = false;
            tRePass.setError("Wrong Password");
        }
        if (isAllOk) {
            Toast.makeText(this, "All OK", Toast.LENGTH_SHORT).show();
            //עצם לביצוע רישום
            FirebaseAuth auth=FirebaseAuth.getInstance();
            //יצירת חשבון בעזרת מיל וסיסמא
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override//התגובה שמתקבל הניסיון הרישום בענן
                public void onComplete(@NonNull Task<AuthResult> task) {//הפרמטר מכיל מידע מהשרצ על תוצאת הבקשה לרישום
                  if(task.isSuccessful()){//אם הפעולה הצליחה
                      Toast.makeText(SignUpActivity.this,"Signing up Succeeded",Toast.LENGTH_SHORT).show();
                      finish();
                      //todo invoke save
                  }
                  else{
                      Toast.makeText(SignUpActivity.this, "Signing up Failed", Toast.LENGTH_SHORT).show();
                      tEmail.setError(task.getException().getMessage());//הצגת הודעה השגיאה שהקלה ההענן

                  }
                }
            });
        }

    }
    //todo save
}