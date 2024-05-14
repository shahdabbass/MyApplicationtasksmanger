package com.example.myapplication_tasksmanger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    Button signIn;
    Button signUp;
    TextInputEditText tEmail;
    TextInputEditText tPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        signIn = findViewById(R.id.btnSignIn);
        signUp = findViewById(R.id.btnSignUp);
        tEmail = findViewById(R.id.etEmail);
        tPass = findViewById(R.id.etPassword);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndSignIn();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });

    }

    private void checkAndSignIn() {
        //يحوي نتيجه فحص الحقول ان كانت سليمه
        boolean isAllOK = true;
        //استخراج النص من حقل الايميل
        String email = tEmail.getText().toString();
        //استخراج نص كلمة المرور
        String password = tPass.getText().toString();
        //فحص الايميل ان كان طوله اقل من 6 او لا يحوي @ فهو خطأ
        if (email.length() < 6 || email.contains("@") == false) {
            //تعديل المتغير ليدل على ان الفحص يعطي نتيجه خاطئه
            isAllOK = false;
            //عرض ملاحظه خطأ على الشاشه داخل حقل البريد
            tEmail.setError("Wrong Email");
        }
        if (password.length() < 8 || email.contains(" ") == true) {
            isAllOK = false;
            tPass.setError("Wrong Password");
        }
        if(isAllOK) {
            Toast.makeText(this, "ALL OK", Toast.LENGTH_SHORT).show();
            //עצם לביצוע רישום
            FirebaseAuth auth=FirebaseAuth.getInstance();
            //כניסה לחשבון בעזרת מיל וסיסמא
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override//התגובה שמתקבל מהענן מניסיון הכניסה בענן
                public void onComplete(@NonNull Task<AuthResult> task) {//הפרמטר מכיל מידע מהשרת על תוצאת הבקשה לרישום
                    if(task.isSuccessful()){//אם הפעולה הצליחה
                        Toast.makeText(SignInActivity.this, "Signing in Succeeded", Toast.LENGTH_SHORT).show();
                        //מעבר למסך הראשי
                    }
                    else {
                        Toast.makeText(SignInActivity.this, "Signing in Failed", Toast.LENGTH_SHORT).show();
                        tEmail.setError(task.getException().getMessage());//הצגת הודעת השגיאה שהקבלה מהענן
                    }
                }
            });
        }
    }
}




