package com.example.myapplication_tasksmanger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication_tasksmanger.mydata.MyTasks;
import com.example.myapplication_tasksmanger.mydata.MyUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddTaskActivity extends AppCompatActivity {

    Button save;
    Button cancel;
    TextView tv;
    SeekBar sb;
    TextView TVsub;
    AutoCompleteTextView sub;
    TextInputEditText title;
    TextInputEditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        save=findViewById(R.id.btnSaveTask);
        cancel=findViewById(R.id.btnCancelTask);
        tv=findViewById(R.id.tvImpo);
        sb=findViewById(R.id.seekBar);
        TVsub=findViewById(R.id.textView);
        sub=findViewById(R.id.etSubject);
        title=findViewById(R.id.erShortTitle);
        text=findViewById(R.id.etText);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndSave();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndSave();
            }
        });

    }
    private void checkAndSave() {
        boolean isAllOk = true;
        String subj=sub.getText().toString();
        String Title = title.getText().toString();
        String Text = text.getText().toString();
        int importance= sb.getProgress();

        if (Title.length() == 0) {
            isAllOk = false;
            title.setError("must enter title");
        }
        if (Text.length() == 0) {
            isAllOk = false;
            text.setError("must enter text");
        }
        if (isAllOk) {
            Toast.makeText(this, "All OK", Toast.LENGTH_SHORT).show();
            //עצם לביצוע רישום
           saveTask_FB(Text,Title,importance,subj);

        }
        if (subj.length() == 0) {
            isAllOk = false;
            sub.setError("must enter subject");
        }

    }
    private void saveTask_FB(String text,String title,int importance,String subj)
    {
        FirebaseFirestore db =FirebaseFirestore.getInstance();//مؤشر لقاعده البيانات
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();//استخراج الرقم المميز للمستعمل الذي سجل الدخول لاستعماله كاسم للؤ"دوكيومنت"
        String id = db.collection("MyUsers").
                document(uid).
                collection("subjects").
                document(subj).
                collection("tasks").document().getId();
        //بناء الكائن الذي سيتم حفظه
        MyTasks task=new MyTasks();
        task.setKeyId(id);
        task.setUserId(uid);
        task.setSubjId(subj);
        task.setText(text);
        task.setShortTitle(title);
        task.setImportance(importance);

        //اضافه كائن "لمجموعه" المستعملين و معالج حدث لفحص نجاح المطلوب
        //معالج حدث لفحص هل تم المطلوب من قاعده البيانات
        db.collection("MyUsers").
                document(uid).
                collection("subjects").
                document(subj).
                collection("tasks").
                document(id).
                set(task).addOnCompleteListener(new OnCompleteListener<Void>()
                 {
            //داله معالج الحدث
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {//هل تم تنفيذ المطلوب بنجاح
                if(task.isSuccessful()){
                    Toast.makeText(AddTaskActivity.this, "Succeeded to add task", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(AddTaskActivity.this, "Failed to add task", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

}