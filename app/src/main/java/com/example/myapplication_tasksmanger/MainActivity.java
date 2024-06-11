package com.example.myapplication_tasksmanger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication_tasksmanger.mydata.MyTaskAdapter;
import com.example.myapplication_tasksmanger.mydata.MyTasks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton Fab;
   private SearchView Sv;
    private Spinner sspnr;
    private ListView lstv;
    private MyTaskAdapter tasksAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fab=findViewById(R.id.fabAdd);
        Sv=findViewById(R.id.srchV);
        sspnr=findViewById(R.id.spnr);
        lstv=findViewById(R.id.lstV);
        tasksAdapter=new MyTaskAdapter(this,R.layout.mytask_item_layout);
        lstv.setAdapter(tasksAdapter);

    }


    /**
     *  קריאת נתונים ממסד הנתונים firestore
     * @return .... רשימת הנתונים שנקראה ממסד הנתונים
     */
    public ArrayList<MyTasks> readTaskFrom_FB()
    {
        //בניית רשימה ריקה
        ArrayList<MyTasks> arrayList =new ArrayList<>();
        //קבלת הפנייה למסד הנתונים
        FirebaseFirestore ffRef = FirebaseFirestore.getInstance();
        //קישור לקבוצה לקבוצה שרוצים לקרוא

        ffRef.collection("MyUsers").
                document(FirebaseAuth.getInstance().getUid()).
                collection("subjects").
                document(sspnr.getSelectedItem().toString()).
                //הוספת מאזין לקריאת הנתונים
                        collection("Tasks").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    /**
                     * תגובה לאירוע השלמת קריאת הנתונים
                     * @param task הנתונים שהתקבלו מענן מסד הנתונים
                     */
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())// אם בקשת הנתונים התקבלה בהצלחה
                            //מעבר על כל ה״מסמכים״= עצמים והוספתם למבנה הנתונים
                            for (DocumentSnapshot document : task.getResult().getDocuments())
                            {
                                //המרת העצם לטיפוס שלו// הוספת העצם למבנה הנתונים
                                arrayList.add(document.toObject(MyTasks.class));
                            }
                        else{
                            Toast.makeText(MainActivity.this, "Error Reading data"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        return arrayList;
    }



}