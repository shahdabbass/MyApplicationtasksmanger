package com.example.myapplication_tasksmanger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication_tasksmanger.mydata.MyTasks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddTaskActivity extends AppCompatActivity {

    //upload: 1 add Xml image view or button and upload button
//upload: 2 add next fileds
    private final int IMAGE_PICK_CODE=100;// קוד מזהה לבקשת בחירת תמונה
    private final int PERMISSION_CODE=101;//קוד מזהה לבחירת הרשאת גישה לקבצים
    private ImageView imgBtnl;//כפתור/ לחצן לבחירת תמונה והצגתה
    private Uri toUploadimageUri;// כתוב הקובץ(תמונה) שרוצים להעלות
    private Uri downladuri;//כתובת הקוץ בענן אחרי ההעלאה
    private MyTasks mytask =new MyTasks();//עצם/נתון שרוצים לשמור


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
        imgBtnl=findViewById(R.id.imgV);
        imgBtnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();

            }
        });


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
        if (subj.length() == 0) {
            isAllOk = false;
            sub.setError("must enter subject");
        }
        if (isAllOk) {
            Toast.makeText(this, "All OK", Toast.LENGTH_SHORT).show();
            //بناء الكائن الذي سيتم حفظه


            mytask.setSubjId(subj);
            mytask.setText(Text);
            mytask.setShortTitle(Title);
            mytask.setImportance(importance);
            uploadImage(toUploadimageUri);

        }


    }
    private void saveTask_FB()
    {
        FirebaseFirestore db =FirebaseFirestore.getInstance();//مؤشر لقاعده البيانات
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();//استخراج الرقم المميز للمستعمل الذي سجل الدخول لاستعماله كاسم للؤ"دوكيومنت"
        String id = db.collection("users").
                document(uid).
                collection("subjects").
                document(mytask.getSubjId()).
                collection("tasks").document().getId();


        //اضافه كائن "لمجموعه" المستعملين و معالج حدث لفحص نجاح المطلوب
        //معالج حدث لفحص هل تم المطلوب من قاعده البيانات
        db.collection("users").
                document(uid).
                collection("tasks").
                document(id).
                set(mytask).addOnCompleteListener(new OnCompleteListener<Void>()
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

    private void pickImageFromGallery(){
        //implicit intent (מרומז) to pick image
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_CODE);//הפעלתה האינטנט עם קוד הבקשה
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        //אם נבחר משהו ואם זה קוד בקשת התמונה
        if (resultCode==RESULT_OK && requestCode== IMAGE_PICK_CODE){
            //a עידכון תכונת כתובת התמונה
            toUploadimageUri = data.getData();//קבלת כתובת התמונה הנתונים שניבחרו
            imgBtnl.setImageURI(toUploadimageUri);// הצגת התמונה שנבחרה על רכיב התמונה
        }
    }
    /**
     * בדיקה האם יש הרשאה לגישה לקבצים בטלפון
     */
    private void checkPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//בדיקת גרסאות
            //בדיקה אם ההשאה לא אושרה בעבר
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                //רשימת ההרשאות שרוצים לבקש אישור
                String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE};
                //בקשת אישור ההשאות (שולחים קוד הבקשה)
                //התשובה תתקבל בפעולה onRequestPermissionsResult
                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                //permission already granted אם יש הרשאה מקודם אז מפעילים בחירת תמונה מהטלפון
                pickImageFromGallery();
            }
        }
        else {//אם גרסה ישנה ולא צריך קבלת אישור
            pickImageFromGallery();
        }
    }
    /**
     * @param requestCode The request code passed in מספר בקשת ההרשאה
     * @param permissions The requested permissions. Never null. רשימת ההרשאות לאישור
     * @param grantResults The grant results for the corresponding permissions תוצאה עבור כל הרשאה
     *   PERMISSION_GRANTED אושר or PERMISSION_DENIED נדחה . Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PERMISSION_CODE) {//בדיקת קוד בקשת ההרשאה
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission was granted אם יש אישור
                pickImageFromGallery();
            } else {
                //permission was denied אם אין אישור
                Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void uploadImage(Uri filePath) {
        if (filePath != null) {
            //יצירת דיאלוג התקדמות
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();//הצגת הדיאלוג
            //קבלת כתובת האחסון בענן
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();
            //יצירת תיקיה ושם גלובלי לקובץ
            final StorageReference ref = storageReference.child("mytaskpics/" + UUID.randomUUID().toString());
            // יצירת ״תהליך מקביל״ להעלאת תמונה
            ref.putFile(filePath)
                    //הוספת מאזין למצב ההעלאה
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();// הסתרת הדיאלוג
                                //קבלת כתובת הקובץ שהועלה
                                ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        downladuri = task.getResult();
                                        Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                                        mytask.setImg(downladuri.toString());//עדכון כתובת התמונה שהועלתה
                                        saveTask_FB();
                                    }
                                });
                            } else {
                                progressDialog.dismiss();//הסתרת הדיאלוג
                                Toast.makeText(getApplicationContext(), "Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    //הוספת מאזין שמציג מהו אחוז ההעלאה
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //חישוב מה הגודל שהועלה
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()/ taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        } else {
            saveTask_FB();
        }
    }






}