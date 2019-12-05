package com.example.ezsurvey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Result extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout resLL;
    private TextView resTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        db = FirebaseFirestore.getInstance();
        resLL = findViewById(R.id.resLL);
        resTV = findViewById(R.id.resTV);

        //Reading results responses from Firebase based on clicked Response on the specific time
        db.collection("Responses").document(getIntent().getStringExtra("RESPONSE")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                resTV.setText("Result of \n"+task.getResult().getId());
                for(int i = 0; i<task.getResult().getLong("noOfQuestions");i++){
                    TextView question = new TextView(Result.this);
                    question.setTextSize(14);
                    question.setText(task.getResult().getString("question"+i));
                    TextView resp = new TextView(Result.this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.CENTER;
                    params.setMargins(0,15,0,60);
                    resp.setText(task.getResult().getString("resp"+i));
                    resp.setTextSize(18);
                    resp.setTypeface(Typeface.DEFAULT_BOLD);
                    resp.setLayoutParams(params);
                    resLL.addView(question);
                    resLL.addView(resp);
                }

            }
        });

    }

    public void doneOnClick(View view) {
        finish();
    }
}
