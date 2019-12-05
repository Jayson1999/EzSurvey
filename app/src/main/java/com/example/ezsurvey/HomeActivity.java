package com.example.ezsurvey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private FloatingActionButton add;
    private GridView formGV;
    private ArrayList<Form> forms;
    private ArrayList<Question> questions;
    private FormAdapter formAdapter;
    private FirebaseFirestore db;
    private ConstraintLayout formCL;
    private ImageView loading;
    private TextView loadTV,tv;
    public static final String SELECTED_FORM="com.example.ezsurvey.SELECTED_FORM";
    public static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setIcon(R.drawable.logo);
        setContentView(R.layout.activity_home);

        formCL = (ConstraintLayout)findViewById(R.id.formCL);
        loading = (ImageView) findViewById(R.id.loading);
        loadTV = (TextView) findViewById(R.id.loadTV);
        tv = (TextView) findViewById(R.id.tv);
        formGV = (GridView)findViewById(R.id.formGV);
        db = FirebaseFirestore.getInstance();
        questions = new ArrayList<>();
        forms  = new ArrayList<>();
        formAdapter = new FormAdapter(this,forms);
        add = (FloatingActionButton)findViewById(R.id.btn_add);
        Glide.with(HomeActivity.this).load(R.drawable.loading).apply(new RequestOptions().override(400)).into(loading);

        db.collection("Forms").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        forms.add(new Form(document.getId(),questions));
                    }
                    if(forms.isEmpty()){
                        tv.setVisibility(View.VISIBLE);
                    }
                    formGV.setAdapter(formAdapter);
                    formCL.removeView(loading);
                    formCL.removeView(loadTV);
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,AddActivity.class));
            }
        });

        formGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(HomeActivity.this,SurveyActivity.class);
                i.putExtra(SELECTED_FORM,forms.get(position).getName());
                startActivityForResult(i,REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        Intent i = getIntent();
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    public void exitApp(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
        alertDialogBuilder.setTitle("Exit Application?");
        alertDialogBuilder
                .setMessage("Exit App")
                .setIcon(R.drawable.exit)
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
