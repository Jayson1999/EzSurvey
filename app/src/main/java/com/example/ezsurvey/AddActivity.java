package com.example.ezsurvey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity {

    private LinearLayout newFormLL;
    private int numberingCounter = 0;
    private Form newForm;
    private ArrayList<Question> questions = new ArrayList<>();
    private EditText formName;
    private String questionType = "common";
    private Button save;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        newFormLL = (LinearLayout)findViewById(R.id.newFormLL);
        formName = (EditText)findViewById(R.id.formName);
        save = (Button) findViewById(R.id.btn_save);
        db = FirebaseFirestore.getInstance();

        addNewQuestion();
    }

    public void addNewQuestion(){
        numberingCounter++;
        questionType = "common";
        final LinearLayout overallQ = new LinearLayout(AddActivity.this);
        overallQ.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        overallQ.setLayoutParams(params);
        newFormLL.addView(overallQ);

        final LinearLayout questionLL = new LinearLayout(AddActivity.this);
        questionLL.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.gravity = Gravity.CENTER;
        questionLL.setLayoutParams(params1);
        overallQ.addView(questionLL);

        TextView numberingTV = new TextView(AddActivity.this);
        numberingTV.setText(numberingCounter+".");
        questionLL.addView(numberingTV);

        final EditText txt_question = new EditText(AddActivity.this);
        params1.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        txt_question.setLayoutParams(params1);
        txt_question.setHint("* Enter your question here ");
        txt_question.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        questionLL.addView(txt_question);

        final ImageButton chgQ = new ImageButton(AddActivity.this);
        chgQ.setImageResource(R.drawable.change);
        chgQ.setBackgroundResource(android.R.color.transparent);
        questionLL.addView(chgQ);

        final LinearLayout respLL = new LinearLayout(AddActivity.this);
        respLL.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.setMargins(0,15,0,0);
        respLL.setLayoutParams(params2);
        respLL.setGravity(Gravity.CENTER);
        overallQ.addView(respLL);

        RadioGroup respGroup = new RadioGroup(AddActivity.this);
        RadioGroup.LayoutParams params6 = new RadioGroup.LayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
        respGroup.setLayoutParams(params6);
        respGroup.setOrientation(LinearLayout.HORIZONTAL);
        respGroup.setGravity(Gravity.CENTER);
        respLL.addView(respGroup);

        RadioButton resp1 = new RadioButton(AddActivity.this);
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params3.setMargins(0,0,30,0);
        resp1.setLayoutParams(params3);
        resp1.setGravity(Gravity.CENTER);
        resp1.setButtonDrawable(android.R.color.transparent);
        resp1.setBackgroundResource(R.drawable.saddest);
        respGroup.addView(resp1);

        RadioButton resp2 = new RadioButton(AddActivity.this);
        resp2.setLayoutParams(params3);
        resp2.setGravity(Gravity.CENTER);
        resp2.setButtonDrawable(android.R.color.transparent);
        resp2.setBackgroundResource(R.drawable.sad1);
        respGroup.addView(resp2);

        RadioButton resp3 = new RadioButton(AddActivity.this);
        resp3.setLayoutParams(params3);
        resp3.setGravity(Gravity.CENTER);
        resp3.setButtonDrawable(android.R.color.transparent);
        resp3.setBackgroundResource(R.drawable.poker2);
        respGroup.addView(resp3);

        RadioButton resp4 = new RadioButton(AddActivity.this);
        resp4.setLayoutParams(params3);
        resp4.setGravity(Gravity.CENTER);
        resp4.setButtonDrawable(android.R.color.transparent);
        resp4.setBackgroundResource(R.drawable.smile1);
        respGroup.addView(resp4);

        RadioButton resp5 = new RadioButton(AddActivity.this);
        resp5.setLayoutParams(params3);
        resp5.setGravity(Gravity.CENTER);
        resp5.setButtonDrawable(android.R.color.transparent);
        resp5.setBackgroundResource(R.drawable.smilest);
        respGroup.addView(resp5);

        final LinearLayout respTVLL = new LinearLayout(AddActivity.this);
        respTVLL.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params4.setMargins(0,0,0,45);
        respTVLL.setLayoutParams(params4);
        respTVLL.setGravity(Gravity.CENTER);
        overallQ.addView(respTVLL);

        TextView resp1TV = new TextView(AddActivity.this);
        LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams(90, LinearLayout.LayoutParams.WRAP_CONTENT);
        params5.setMargins(0,0,30,0);
        resp1TV.setLayoutParams(params5);
        resp1TV.setGravity(Gravity.CENTER);
        resp1TV.setText("1");
        respTVLL.addView(resp1TV);

        TextView resp2TV = new TextView(AddActivity.this);
        resp2TV.setLayoutParams(params5);
        resp2TV.setGravity(Gravity.CENTER);
        resp2TV.setText("2");
        respTVLL.addView(resp2TV);

        TextView resp3TV = new TextView(AddActivity.this);
        resp3TV.setLayoutParams(params5);
        resp3TV.setGravity(Gravity.CENTER);
        resp3TV.setText("3");
        respTVLL.addView(resp3TV);

        TextView resp4TV = new TextView(AddActivity.this);
        resp4TV.setLayoutParams(params5);
        resp4TV.setGravity(Gravity.CENTER);
        resp4TV.setText("4");
        respTVLL.addView(resp4TV);

        TextView resp5TV = new TextView(AddActivity.this);
        resp5TV.setLayoutParams(params5);
        resp5TV.setGravity(Gravity.CENTER);
        resp5TV.setText("5");
        respTVLL.addView(resp5TV);

        final ImageButton addQ = new ImageButton(AddActivity.this);
        addQ.setImageResource(R.drawable.add);
        addQ.setBackgroundResource(android.R.color.transparent);
        overallQ.addView(addQ);

        addQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_question.getText().toString().length()>0){
                    if(numberingCounter>0){
                        questions.add(new Question(txt_question.getText().toString(),questionType,"",""));
                    }
                    overallQ.removeView(addQ);
                    questionLL.removeView(chgQ);
                    addNewQuestion();
                }
                else{
                    Toast.makeText(AddActivity.this, "Please Enter The Question Before Adding!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        chgQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(questionType.equals("common")){
                    questionType = "field";
                    overallQ.removeView(respLL);
                    overallQ.removeView(respTVLL);
                    TextView txtField = new TextView(AddActivity.this);
                    LinearLayout.LayoutParams params10 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params10.setMargins(0,0,0,45);
                    params10.gravity = Gravity.CENTER;
                    txtField.setLayoutParams(params10);
                    txtField.setText("Text Field Question");
                    overallQ.addView(txtField);
                    overallQ.removeView(addQ);
                    overallQ.addView(addQ);
                }
                else{
                    questionType = "common";
                    overallQ.removeAllViews();
                    addNewQuestion();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(formName.getText().toString().length()>1 && txt_question.getText().toString().length()>1){
                    db.collection("Forms").document(formName.getText().toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.getLong("noOfQuestions")!=null){
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddActivity.this);
                                alertDialogBuilder.setTitle("Form Already Exist");
                                alertDialogBuilder
                                        .setMessage("Do you wish to update and replace the Form?")
                                        .setIcon(R.drawable.formtn)
                                        .setCancelable(false)
                                        .setPositiveButton("Yes",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        addToFirebase(txt_question);
                                                    }
                                                })

                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Toast.makeText(AddActivity.this, "Change another Form name", Toast.LENGTH_SHORT).show();
                                                dialog.cancel();
                                                //finish();
                                            }
                                        });

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                            else{
                                addToFirebase(txt_question);
                            }
                        }
                    });

                }
                else if(formName.getText().toString().length()<1){
                    Toast.makeText(AddActivity.this, "Please enter the Form Name!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(AddActivity.this, "Please enter the Last Question!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void addToFirebase(EditText txt_question){
        questions.add(new Question(txt_question.getText().toString(),questionType,"",""));
        newForm = new Form(formName.getText().toString(),questions);
        Map<String, Object> form = new HashMap<>();
        for(int i = 0 ; i < questions.size() ; i++){

            form.put("question"+i, newForm.getQuestions().get(i).getName());
            form.put("type"+i, newForm.getQuestions().get(i).getType());
            form.put("noOfQuestions", newForm.getQuestions().size());

            db.collection("Forms").document(newForm.getName())
                    .set(form)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void documentReference) {
                            Toast.makeText(AddActivity.this,newForm.getName()+" has been successfully added!",Toast.LENGTH_SHORT).show();
//                                        Intent i = new Intent(AddActivity.this, HomeActivity.class);
//                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                        startActivity(i);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddActivity.this,"Error adding Form"+ e,Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}
