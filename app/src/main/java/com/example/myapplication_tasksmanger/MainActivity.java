package com.example.myapplication_tasksmanger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton Fab;
   private SearchView Sv;
    private Spinner sspnr;
    private ArrayAdapter<String> spnrSubjctAdapter;
    private ListView lstv;
    private MyTaskAdapter tasksAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fab=findViewById(R.id.fabAdd);
        Sv=findViewById(R.id.srchV);
        sspnr=findViewById(R.id.spnr);
        spnrSubjctAdapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        sspnr.setAdapter(spnrSubjctAdapter);
        lstv=findViewById(R.id.lstV);
        tasksAdapter=new MyTaskAdapter(this,R.layout.mytask_item_layout);
        lstv.setAdapter(tasksAdapter);
        Fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(i);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        readTaskFrom_FB();
    }

    /**
     *  קריאת נתונים ממסד הנתונים firestore
     * @return .... רשימת הנתונים שנקראה ממסד הנתונים
     */
    public void readTaskFrom_FB() {

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
                            HashSet<String> subjects = new HashSet<>();
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                //המרת העצם לטיפוס שלו// הוספת העצם למבנה הנתונים
                                MyTasks myTask = document.toObject(MyTasks.class);

                                tasksAdapter.add(myTask);
                                subjects.add(myTask.getSubjId());
                            }
                            spnrSubjctAdapter.addAll(subjects);
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



}