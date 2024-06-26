package com.example.myapplication_tasksmanger.mydata;

import static android.Manifest.permission.CALL_PHONE;
import static android.app.ProgressDialog.show;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.PermissionChecker;

import com.example.myapplication_tasksmanger.AddTaskActivity;
import com.example.myapplication_tasksmanger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MyTaskAdapter  extends ArrayAdapter<MyTasks> {
    //המזהה של קובץ עיצוב הפריט
    private final int itemLayout;

    /**
     * פעולה בונה מתאם
     *
     * @param context  קישור להקשר (מסך- אקטיביטי)
     * @param resource עיצוב של פריט שיציג הנתונים של העצם
     */

    public MyTaskAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.itemLayout = resource;

    }
/**
    /**
     * בונה פריט גרפי אחד בהתאם לעיצוב והצגת נתוני העצם עליו
     *
     * @param position    מיקום הפריט החל מ 0
     * @param convertView
     * @param parent      רכיב האוסף שיכיל את הפריטים כמו listview
     * @return . פריט גרפי שמציג נתוני עצם אחד
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //בניית הפריט הגרפי מתו קובץ העיצוב
        View vitem = convertView;
        if (vitem == null)
            vitem = LayoutInflater.from(getContext()).inflate(itemLayout, parent, false);
        //קבלת הפניות לרכיבים בקובץ העיצוב
        ImageView imageView = vitem.findViewById(R.id.imageVitm);
        TextView tvTitle = vitem.findViewById(R.id.tvItmTitle);
        TextView tvText = vitem.findViewById(R.id.tvItmText);
        TextView tvImportance = vitem.findViewById(R.id.tvItmImportance);
        ImageView imgSend = vitem.findViewById(R.id.imgVsend);
        ImageView imgCall = vitem.findViewById(R.id.imgVcall);
        ImageView imgEdit = vitem.findViewById(R.id.imgVedit);
        ImageView imgStar = vitem.findViewById(R.id.imgVimp);
        CheckBox checkBox=vitem.findViewById(R.id.checkBox);


        //קבלת הנתון (עצם) הנוכחי
        MyTasks current = getItem(position);
        //הצגת הנתונים על שדות הרכיב הגרפי
        tvTitle.setText(current.getShortTitle());
        tvText.setText(current.getText());
        tvImportance.setText("Importance:" + current.getImportance());
        checkBox.setChecked(current.isCompleted);
        if(current.isStar()){
            imgStar.setImageResource(android.R.drawable.star_big_on);
        }
        else {
            imgStar.setImageResource(android.R.drawable.star_big_off);

        }
        downloadImageUsingPicasso(current.getImg(), imageView);
        //  return vitem;


        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSendSmsApp(current.getText(), "");

            }
        });

        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAPhoneNymber(current.getText());
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(getContext(), "Checked", Toast.LENGTH_SHORT).show();
                current.setCompleted(isChecked);
                updatetask(current);
            }
        });
        imgStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current.isStar()==false){
                    imgStar.setImageResource(android.R.drawable.star_big_on);
                    current.setStar(true);
                }
                else {
                    imgStar.setImageResource(android.R.drawable.star_big_off);
                    current.setStar(false);

                }
                updatetask(current);


            }
        });
       //
        return vitem;//????
    }

    private void updatetask(MyTasks current) {
        FirebaseFirestore db =FirebaseFirestore.getInstance();//مؤشر لقاعده البيانات

        //اضافه كائن "لمجموعه" المستعملين و معالج حدث لفحص نجاح المطلوب
        //معالج حدث لفحص هل تم المطلوب من قاعده البيانات
        db.collection("users").
                document(current.getUserId()).
                collection("tasks").
                document(current.getId()).
                set(current).addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    //داله معالج الحدث
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {//هل تم تنفيذ المطلوب بنجاح
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), " task updated", Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(getContext(), "fail to update task", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }


    /**
     * הצגת תמונה ישירות מהענן בעזרת המחלקה ״פיקאסו״
     *
     * @param imageUrL כתובת התמונה בענן/שרת
     * @param toView   רכיב תמונה המיועד להצגת התמונה אחרי ההורדה
     */
    private void downloadImageUsingPicasso(String imageUrL, ImageView toView) {
        // אם אין תמונה= כתובת ריקה אז לא עושים כלום מפסיקים את הפעולה
        if (imageUrL == null) return;
        //todo: add dependency to module gradle:
        //    implementation 'com.squareup.picasso:picasso:2.5.2'
        Picasso.with(getContext())
                .load(imageUrL)//הורדת התמונה לפי כתובת
                .centerCrop()
                .error(R.mipmap.my_logo)//התמונה שמוצגת אם יש בעיה בהורדת התמונה
                .resize(90, 90)//שינוי גודל התמונה
                .into(toView);// להציג בריכיב התמונה המיועד לתמונה זו
    }

    /**
     * פתיחת אפליקצית שליחת sms
     *
     * @param msg   .. ההודעה שרוצים לשלוח
     * @param phone
     */
    public void openSendSmsApp(String msg, String phone) {
        //אינטנט מרומז לפתיחת אפליקצית ההודות סמס
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        //מעבירים מספר הטלפון
        smsIntent.setData(Uri.parse("smsto:" + phone));
        //ההודעה שנרצה שתופיע באפליקצית ה סמס
        smsIntent.putExtra("sms_body", msg);
        smsIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
        //פתיחת אפליקציית ה סמס
        getContext().startActivity(smsIntent);
    }


    /**
     * פתיחת אפליקצית שליחת whatsapp
     *
     * @param msg   .. ההודעה שרוצים לשלוח
     * @param phone
     */
    public void openSendWhatsAppV2(String msg, String phone) {
        //אינטנט מרומז לפתיחת אפליקצית ההודות סמס
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        ;
        String url = null;
        try {
            url = "https://api.whatsapp.com/send?phone=" + phone + "&text=" + URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
            Toast.makeText(getContext(), "there is no whatsapp!!", Toast.LENGTH_SHORT).show();
        }
        sendIntent.setData(Uri.parse(url));
        sendIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        sendIntent.addCategory(Intent.CATEGORY_DEFAULT);
        //פתיחת אפליקציית ה סמס
        getContext().startActivity(sendIntent);
    }

    //ביצוע שיחה למפסר טלפון
    private void callAPhoneNymber(String phone) {
        //בדיקה אם יש הרשאה לביצוע שיחה
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//בדיקת גרסאות
            //בדיקה אם ההרשאה לא אושרה בעבר
            if (checkSelfPermission(getContext(), CALL_PHONE) == PermissionChecker.PERMISSION_DENIED) {
                //רשימת ההרשאות שרוצים לבקש אישור
                String[] permissions = {CALL_PHONE};
                //בקשת אישור הרשאות (שולחים קוד הבקשה)
                //התשובה תתקבל בפעולה onRequestPermissionsResult


                requestPermissions((Activity) getContext(), permissions, 100);

            } else {
                //אינטנט מרומז לפתיחת אפליקצית ההודות סמס
                Intent phone_intent = new Intent(Intent.ACTION_CALL);
                phone_intent.setData(Uri.parse("tel:" + phone));
                getContext().startActivity(phone_intent);

            }

        }
    }
    /**
     * מחיקת פריט כולל התמונה מבסיס הנתונים
     * @param myTask הפריט שמוחקים
     */
    private void delMyTaskFromDB_FB(MyTasks myTask)
    {
        //הפנייה/כתובת  הפריט שרוצים למחוק
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("users").
                document(myTask.getUserId()).
                collection("tasks").document(myTask.getId()).
                delete().//מאזין אם המחיקה בוצעה
                addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    remove(myTask);// מוחקים מהמתאם
                    deleteFile(myTask.getImg());// מחיקת הקובץ
                    Toast.makeText(getContext(), "deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     * מחיקת קובץ האיחסון הענן
     * @param fileURL כתובת הקובץ המיועד למחיקה
     */
    private void deleteFile(String fileURL) {
        // אם אין תמונה= כתובת ריקה אז לא עושים כלום מפסיקים את הפעולה
        if(fileURL==null){
            Toast.makeText(getContext(), "Theres no file to delete!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        // הפניה למיקום הקובץ באיחסון
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(fileURL);
        //מחיקת הקובץ והוספת מאזין שבודק אם ההורדה הצליחה או לא
        storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(getContext(), "file deleted", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), "onFailure: did not delete file "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //dialog
    public void showYesNoDialog()
    {
        //تجهيز بناء شباك حوار يتلقى بارمتر مؤشر للنشاط الحالي
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Delet Task");//تحديد العنوان
        builder.setMessage("Are you sure?");
        //الضغط على الزر و معالج الحدث
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //معالجه حدث للموافقه
                Toast.makeText(getContext(),"Delet",Toast.LENGTH_SHORT).show();

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //معالجه حدث للموافقه
                Toast.makeText(getContext(),"Signing out",Toast.LENGTH_SHORT).show();

            }
        });

        AlertDialog dialog=builder.create();//بناء شباك الحوار
        dialog.show();//عرض الشباك
    }


}