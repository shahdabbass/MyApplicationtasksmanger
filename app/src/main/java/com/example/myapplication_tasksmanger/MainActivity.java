package com.example.myapplication_tasksmanger;

import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication_tasksmanger.msgBkg.MsgListener;
import com.example.myapplication_tasksmanger.msgBkg.MySmsReceiver;
import com.example.myapplication_tasksmanger.mydata.MyTaskAdapter;
import com.example.myapplication_tasksmanger.mydata.MyTasks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity implements MsgListener {

    private static final int PERMISSION_CODE = 100;
    private FloatingActionButton Fab;
   private SearchView Sv;
    private Spinner sspnr;
    private ArrayAdapter<String> spnrSubjctAdapter;
    private ListView lstv;
    private MyTaskAdapter tasksAdapter;
    private boolean isHistoryPressed =false;
    private boolean isStarPressed =false;
    private boolean isAll=true;
    HashSet<String> subjectSet = new HashSet<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fab=findViewById(R.id.fabAdd);
        Sv=findViewById(R.id.srchV);
        sspnr=findViewById(R.id.spnr);

        //بناء الوسيط
        spnrSubjctAdapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        sspnr.setAdapter(spnrSubjctAdapter);
        sspnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                readTaskFrom_FB(spnrSubjctAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        lstv=findViewById(R.id.lstV);
        tasksAdapter=new MyTaskAdapter(this,R.layout.mytask_item_layout);
        lstv.setAdapter(tasksAdapter);
        // // adding bind listener for message receiver on below line.
        MySmsReceiver.bindListener(this);
        Fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(i);
            }
        });
checkPermission();

    }

    @Override
    protected void onResume() {
        super.onResume();
        readTaskFrom_FB("");
    }

    /**
     *  קריאת נתונים ממסד הנתונים firestore
     * @return .... רשימת הנתונים שנקראה ממסד הנתונים
     */
    public void readTaskFrom_FB(String selectedSubject) {

        //קבלת הפנייה למסד הנתונים
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //קישור לקבוצה לקבוצה שרוצים לקרוא
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").
                document(uid).
                collection("tasks").
                //הוספת מאזין לקריאת הנתונים
                        get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    /**
                     * תגובה לאירוע השלמת קריאת הנתונים
                     *
                     * @param task הנתונים שהתקבלו מענן מסד הנתונים
                     */
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {// אם בקשת הנתונים התקבלה בהצלחה
                            //מעבר על כל ה״מסמכים״= עצמים והוספתם למבנה הנתונים
                            tasksAdapter.clear();
                            spnrSubjctAdapter.clear();
                            subjectSet.clear();
                           //يمر على المهمات
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                //המרת העצם לטיפוס שלו// הוספת העצם למבנה הנתונים
                                MyTasks myTask = document.toObject(MyTasks.class);
                                if(isAll ){
                                    if (selectedSubject.length()!=0 && myTask.getSubjId().equals(selectedSubject)){
                                        tasksAdapter.add(myTask);

                                    }
                                     else if (selectedSubject.length()==0){
                                        tasksAdapter.add(myTask);

                                     }
                                    subjectSet.add(myTask.getSubjId());
                                }
                                else
                                if (isStarPressed ==true && true ==myTask.isStar  ){

                                    if (selectedSubject.length()!=0 && myTask.getSubjId().equals(selectedSubject)){
                                        tasksAdapter.add(myTask);

                                    }
                                    else if (selectedSubject.length()==0){
                                        tasksAdapter.add(myTask);

                                    }
                                    subjectSet.add(myTask.getSubjId());

                                }else
                                if (isHistoryPressed==true && true ==myTask.isCompleted ){
                                    if (selectedSubject.length()!=0 && myTask.getSubjId().equals(selectedSubject)){
                                        tasksAdapter.add(myTask);

                                    }
                                    else if (selectedSubject.length()==0){
                                        tasksAdapter.add(myTask);

                                    }
                                    subjectSet.add(myTask.getSubjId());
                                }

                            }
                            ArrayList<String> subs=new ArrayList<>();
                            subs.addAll(subjectSet);
                            int index = subs.indexOf(selectedSubject);
                            spnrSubjctAdapter.addAll(subs);
                            sspnr.setSelection(index);
                            tasksAdapter.sort(new MyComp());

                        } else {
                            Toast.makeText(MainActivity.this, "Error Reading data" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    //menu
    @Override//بناء قائمه
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override//معالجه حدث اختيار عنصر من القائمه
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.itmLogOut){
            showYesNoDialog();
        }
        if(item.getItemId()==R.id.itmHistory){
         isHistoryPressed =true;
         isAll=false;
         isStarPressed =false;
         readTaskFrom_FB("");
        }
        if(item.getItemId()==R.id.itmPlayMusic)
        {
            Toast.makeText(this,"Play music",Toast.LENGTH_SHORT).show();
            Intent serviceIntn=new Intent(getApplicationContext(),MyAudioPlayService.class);
            startService(serviceIntn);
        }

        if(item.getItemId()==R.id.itmStopMusic)
        {
            Toast.makeText(this,"Stop music",Toast.LENGTH_SHORT).show();
            Intent serviceIntn=new Intent(getApplicationContext(),MyAudioPlayService.class);
            stopService(serviceIntn);
        }
        if (item.getItemId()==R.id.itmStar){
            isStarPressed =true;
            isAll=false;
            isHistoryPressed =false;
            readTaskFrom_FB("");
        }
        if (item.getItemId()==R.id.itmAll){
            isAll=true;
            isHistoryPressed =false;
            isStarPressed =false;
            readTaskFrom_FB("");
        }

        return true;
    }
    //dialog
    public void showYesNoDialog()
    {
        //تجهيز بناء شباك حوار يتلقى بارمتر مؤشر للنشاط الحالي
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Log out");//تحديد العنوان
        builder.setMessage("Are you sure?");
        //الضغط على الزر و معالج الحدث
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //معالجه حدث للموافقه
                Toast.makeText(MainActivity.this,"Signing out",Toast.LENGTH_SHORT).show();
               FirebaseAuth.getInstance().signOut();
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //معالجه حدث للموافقه
                Toast.makeText(MainActivity.this,"Signing out",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        AlertDialog dialog=builder.create();//بناء شباك الحوار
        dialog.show();//عرض الشباك
    }

    private void checkPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//בדיקת גרסאות
            //בדיקה אם ההשאה לא אושרה בעבר
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(READ_SMS) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(RECEIVE_SMS) == PackageManager.PERMISSION_DENIED) {
                //רשימת ההרשאות שרוצים לבקש אישור
                String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE,READ_MEDIA_IMAGES,READ_SMS,RECEIVE_SMS};
                //בקשת אישור ההשאות (שולחים קוד הבקשה)
                //התשובה תתקבל בפעולה onRequestPermissionsResult
                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                //permission already granted אם יש הרשאה מקודם אז מפעילים בחירת תמונה מהטלפון
            }
        }
        else {//אם גרסה ישנה ולא צריך קבלת אישור
        }
    }
    @Override
    public void msgReceived(String phone, String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        saveMsgAsTask(phone, message);
    }
    //save a SMS as a task
    private void saveMsgAsTask(String phone, String message)
    {

        FirebaseFirestore db =FirebaseFirestore.getInstance();//مؤشر لقاعده البيانات
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();//استخراج الرقم المميز للمستعمل الذي سجل الدخول لاستعماله كاسم للؤ"دوكيومنت"
        MyTasks mytask=new MyTasks();
        mytask.setSubjId("SMS");
        mytask.setText(phone);
        mytask.setShortTitle(message);

        String id = db.collection("users").
                document(uid).
                collection("subjects").
                document(mytask.getSubjId()).
                collection("tasks").document().getId();
        mytask.setId(id);
        mytask.setUserId(uid);

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
                            Toast.makeText(MainActivity.this, "Succeeded to add SMS task", Toast.LENGTH_SHORT).show();
                            readTaskFrom_FB("");
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Failed to add SMS task", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
    //فئه تطبق واجهة interface تقارن بين كائنات بمجموعه معينه .
    public class MyComp implements Comparator<MyTasks>
    {

        @Override
        public int compare(MyTasks o1, MyTasks o2) {
            if(o1.isCompleted()==false && o2.isCompleted==false)

            return -o1.importance+o2.importance;
            else return -10+(-o1.importance+o2.importance);
        }
    }
}