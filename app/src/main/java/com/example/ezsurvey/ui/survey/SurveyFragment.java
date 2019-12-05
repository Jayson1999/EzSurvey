package com.example.ezsurvey.ui.survey;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.transition.Transition;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.ezsurvey.AddActivity;
import com.example.ezsurvey.HomeActivity;
import com.example.ezsurvey.Question;
import com.example.ezsurvey.R;
import com.example.ezsurvey.SurveyActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SurveyFragment extends Fragment {

    private SurveyViewModel surveyViewModel;
    private String selectedForm;
    private FirebaseFirestore db;
    private long questionCounter=0;
    private LinearLayout questionsLL;
    private ImageView loading;
    private TextView TVloading;
    private Button submit;
    private ArrayList<Question>replies;
    private ArrayList<String>filterList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        surveyViewModel =
                ViewModelProviders.of(this).get(SurveyViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_survey, container, false);

        final TextView formTitle = root.findViewById(R.id.formTitle);
        questionsLL = root.findViewById(R.id.questionsLL);
        loading = root.findViewById(R.id.loadingTV);
        TVloading = root.findViewById(R.id.loading);
        submit = root.findViewById(R.id.btn_submit);

        Glide.with(getContext()).load(R.drawable.loading).apply(new RequestOptions().override(400)).into(loading);  //Insert loading GIF with Glide
        Intent intent = getActivity().getIntent();
        selectedForm = intent.getStringExtra(HomeActivity.SELECTED_FORM);   //get Selected Form name via Intent Extra
        db = FirebaseFirestore.getInstance();

        formTitle.setText(selectedForm);
        loadQuestions();    //load questions from database
        replies = new ArrayList<>();
        filterList = new ArrayList<>();
        fillFilterList();      //load a set list of vulgar words into local arrayList

        //submit button on click
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> formReplies = new HashMap<>();
                //loop through to differentiate and store replies of common question and EditText question in a list because of different attributes
                for (int i = 0; i<questionCounter; i++){
                    if(root.findViewWithTag("respC"+i)!=null) {     //ViewWithTag C indicates common question
                        RadioGroup rg = ((RadioGroup) root.findViewWithTag("respC" + i));
                        if (rg.getCheckedRadioButtonId() != -1) {
                            int id = rg.getCheckedRadioButtonId();
                            View radioButton = rg.findViewById(id);
                            int radioIndex = rg.indexOfChild(radioButton);
                            String radioResp = radioIndex+1+"";
                            replies.add(new Question(((TextView) root.findViewWithTag("name" + i)).getText().toString(), "common", radioResp,""));
                        }
                    }
                    else if(root.findViewWithTag("respE"+i)!=null){     //ViewWithTag E indicates EditText question
                        replies.add(new Question(((TextView) root.findViewWithTag("name"+i)).getText().toString(),"field",((EditText)root.findViewWithTag("respE"+i)).getText().toString(),""));
                    }
                }
                //loop through again to check for stored list for validations before uploading to Firebase
                for(int j = 0; j<replies.size(); j++){
                    if(replies.get(j).getReply().length()>0 && !(replies.get(j).getReply().equals("0"))){   //if fields are not field or radio not selected
                        //For loop to loop through filter list to check for vulgar words and replace with ****
                        for(int k = 0; k<filterList.size(); k++){
                            if(replies.get(j).getReply().toLowerCase().contains(filterList.get(k))){
                                replies.set(j,new Question(((TextView) root.findViewWithTag("name"+j)).getText().toString(),"field",replies.get(j).getReply().replaceAll(filterList.get(k),"****"),""));
                            }
                        }
                        //insert stored list items into HashMap for Firebase insertion
                        formReplies.put("question"+j, replies.get(j).getName());
                        formReplies.put("resp"+j, replies.get(j).getReply());
                        formReplies.put("type"+j, replies.get(j).getType());
                        if(j==replies.size()-1){    //on the last loop only add to prevent unnecessary write loopings
                            formReplies.put("formName",selectedForm);
                            formReplies.put("noOfQuestions",(formReplies.size()-1)/3 );
                        }
                    }
                    else{
                        Toast.makeText(getContext(), "Please fill in all the responses!", Toast.LENGTH_SHORT).show();
                        replies.clear();
                        formReplies.clear();
                        break;
                    }
                }
                //conduct Writes to Firebase
                if(!(formReplies.isEmpty())) {
                    //upload reply with current date as document name for specification purpose
                    db.collection("Responses").document(Calendar.getInstance().getTime().toString())
                            .set(formReplies)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void documentReference) {
                                    Toast.makeText(getActivity(), "Response has been successfully submitted!", Toast.LENGTH_SHORT).show();
                                    //FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    //ft.detach(SurveyFragment.this).attach(SurveyFragment.this).commit();
                                    getActivity().finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Error adding Form" + e, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        return root;
    }

    /**
     * Function to load questions from Firebase and display in UI format on Fragment screen
     */
   public void loadQuestions(){
        db.collection("Forms").document(selectedForm).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){    //get total number of questions for looping purpose
                   questionCounter = task.getResult().getLong("noOfQuestions");
                }
                //For loop to loop and add questions views programmatically based on how many questions
                for(int i = 0;i<questionCounter;i++){
                    //Question TextView
                    TextView question = new TextView(getContext());
                    question.setTag("name"+i);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.CENTER;
                    params.setMargins(0,0,0,9);
                    question.setLayoutParams(params);
                    question.setText(i+1+"."+task.getResult().getString("question"+i));
                    questionsLL.addView(question);

                    //For common type questions
                    if(task.getResult().getString("type"+i).equals("common")){
                        final LinearLayout respLL = new LinearLayout(getContext());
                        respLL.setOrientation(LinearLayout.HORIZONTAL);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(0,15,0,0);
                        respLL.setLayoutParams(params2);
                        respLL.setGravity(Gravity.CENTER);
                        questionsLL.addView(respLL);

                        //Radio group
                        final RadioGroup respGroup = new RadioGroup(getContext());
                        respGroup.setTag("respC"+i);
                        RadioGroup.LayoutParams params6 = new RadioGroup.LayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                        respGroup.setLayoutParams(params6);
                        respGroup.setOrientation(LinearLayout.HORIZONTAL);
                        respGroup.setGravity(Gravity.CENTER);
                        respLL.addView(respGroup);

                        //Radio 1
                        final RadioButton resp1 = new RadioButton(getContext());
                        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params3.setMargins(0,0,30,0);
                        resp1.setLayoutParams(params3);
                        resp1.setGravity(Gravity.CENTER);
                        resp1.setButtonDrawable(android.R.color.transparent);
                        resp1.setBackgroundResource(R.drawable.saddest);
                        respGroup.addView(resp1);

                        //Radio 2
                        final RadioButton resp2 = new RadioButton(getContext());
                        resp2.setLayoutParams(params3);
                        resp2.setGravity(Gravity.CENTER);
                        resp2.setButtonDrawable(android.R.color.transparent);
                        resp2.setBackgroundResource(R.drawable.sad1);
                        respGroup.addView(resp2);

                        //Radio 3
                        final RadioButton resp3 = new RadioButton(getContext());
                        resp3.setLayoutParams(params3);
                        resp3.setGravity(Gravity.CENTER);
                        resp3.setButtonDrawable(android.R.color.transparent);
                        resp3.setBackgroundResource(R.drawable.poker2);
                        respGroup.addView(resp3);

                        //Radio 4
                        final RadioButton resp4 = new RadioButton(getContext());
                        resp4.setLayoutParams(params3);
                        resp4.setGravity(Gravity.CENTER);
                        resp4.setButtonDrawable(android.R.color.transparent);
                        resp4.setBackgroundResource(R.drawable.smile1);
                        respGroup.addView(resp4);

                        //Radio 5
                        final RadioButton resp5 = new RadioButton(getContext());
                        resp5.setLayoutParams(params3);
                        resp5.setGravity(Gravity.CENTER);
                        resp5.setButtonDrawable(android.R.color.transparent);
                        resp5.setBackgroundResource(R.drawable.smilest);
                        respGroup.addView(resp5);

                        //Score View LinearLayout
                        final LinearLayout respTVLL = new LinearLayout(getContext());
                        respTVLL.setOrientation(LinearLayout.HORIZONTAL);
                        LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params4.setMargins(0,0,0,90);
                        respTVLL.setLayoutParams(params4);
                        respTVLL.setGravity(Gravity.CENTER);
                        questionsLL.addView(respTVLL);

                        //Score 1
                        final TextView resp1TV = new TextView(getContext());
                        LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams(90, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params5.setMargins(0,0,30,0);
                        resp1TV.setLayoutParams(params5);
                        resp1TV.setGravity(Gravity.CENTER);
                        resp1TV.setText("1");
                        respTVLL.addView(resp1TV);

                        //Score 2
                        final TextView resp2TV = new TextView(getContext());
                        resp2TV.setLayoutParams(params5);
                        resp2TV.setGravity(Gravity.CENTER);
                        resp2TV.setText("2");
                        respTVLL.addView(resp2TV);

                        //Score 3
                        final TextView resp3TV = new TextView(getContext());
                        resp3TV.setLayoutParams(params5);
                        resp3TV.setGravity(Gravity.CENTER);
                        resp3TV.setText("3");
                        respTVLL.addView(resp3TV);

                        //Score 4
                        final TextView resp4TV = new TextView(getContext());
                        resp4TV.setLayoutParams(params5);
                        resp4TV.setGravity(Gravity.CENTER);
                        resp4TV.setText("4");
                        respTVLL.addView(resp4TV);

                        //Score 5
                        final TextView resp5TV = new TextView(getContext());
                        resp5TV.setLayoutParams(params5);
                        resp5TV.setGravity(Gravity.CENTER);
                        resp5TV.setText("5");
                        respTVLL.addView(resp5TV);

                        final ColorStateList defCol = resp1TV.getTextColors();  //get default TextView Color

                        //Radio 1 on click
                        resp1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if(isChecked){
                                    //set Radio 1 score to red and bigger
                                    resp1TV.setTextColor(Color.RED);
                                    resp1TV.setTextSize(18);
                                    resp1TV.setPadding(30,0,0,0);
                                    resp1.setVisibility(View.GONE);     //hide static image
                                    //load and display a temporary GIF haptic feedback on radio selected
                                    final ImageView img = new ImageView(getContext());
                                    Glide.with(getContext()).load(R.drawable.loudcry).apply(new RequestOptions().override(180)).into(img);
                                    respGroup.addView(img,0);
                                    //handler to set timer for the GIF
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //when timer is UP, remove GIF and replace back with static image
                                            resp1TV.setPadding(0,0,0,0);
                                            respGroup.removeView(img);
                                            resp1.setVisibility(View.VISIBLE);
                                        }
                                    }, 1000);
                                }
                                else {
                                    resp1TV.setTextSize(14);
                                    resp1TV.setTextColor(defCol);
                                }
                            }
                        });

                        //Radio 2 on click, similar implementation with Radio 1 on click
                        resp2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if(isChecked) {
                                    resp2TV.setTextColor(Color.RED);
                                    resp2TV.setTextSize(18);
                                    resp2TV.setPadding(30,0,0,0);
                                    resp2.setVisibility(View.GONE);
                                    final ImageView img = new ImageView(getContext());
                                    Glide.with(getContext()).load(R.drawable.cry).apply(new RequestOptions().override(180)).into(img);
                                    respGroup.addView(img,1);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            resp2TV.setPadding(0,0,0,0);
                                            respGroup.removeView(img);
                                            resp2.setVisibility(View.VISIBLE);
                                        }
                                    }, 1000);
                                }
                                else {
                                    resp2TV.setTextSize(14);
                                    resp2TV.setTextColor(defCol);
                                }
                            }
                        });

                        //Radio 3 on click, similar implementation with Radio 1 on click
                        resp3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if(isChecked) {
                                    resp3TV.setTextColor(Color.RED);
                                    resp3TV.setTextSize(18);
                                    resp3TV.setPadding(30,0,0,0);
                                    resp3.setVisibility(View.GONE);
                                    final ImageView img = new ImageView(getContext());
                                    Glide.with(getContext()).load(R.drawable.neutral).apply(new RequestOptions().override(180)).into(img);
                                    respGroup.addView(img,2);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            resp3TV.setPadding(0,0,0,0);
                                            respGroup.removeView(img);
                                            resp3.setVisibility(View.VISIBLE);
                                        }
                                    }, 1000);
                                }
                                else {
                                    resp3TV.setTextSize(14);
                                    resp3TV.setTextColor(defCol);
                                }
                            }
                        });

                        //Radio 4 on click, similar implementation with Radio 1 on click
                        resp4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if(isChecked) {
                                    resp4TV.setTextColor(Color.RED);
                                    resp4TV.setTextSize(18);
                                    resp4TV.setPadding(30,0,0,0);
                                    resp4.setVisibility(View.GONE);
                                    final ImageView img = new ImageView(getContext());
                                    Glide.with(getContext()).load(R.drawable.happy).apply(new RequestOptions().override(180)).into(img);
                                    respGroup.addView(img,3);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            respGroup.removeView(img);
                                            resp4.setVisibility(View.VISIBLE);
                                            resp4TV.setPadding(0,0,0,0);
                                        }
                                    }, 1000);
                                }
                                else {
                                    resp4TV.setTextSize(14);
                                    resp4TV.setTextColor(defCol);
                                }
                            }
                        });

                        //Radio 5 on click, similar implementation with Radio 1 on click
                        resp5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if(isChecked) {
                                    resp5TV.setTextColor(Color.RED);
                                    resp5TV.setTextSize(18);
                                    resp5TV.setPadding(30,0,0,0);
                                    resp5.setVisibility(View.GONE);
                                    final ImageView img = new ImageView(getContext());
                                    Glide.with(getContext()).load(R.drawable.happiest).apply(new RequestOptions().override(180)).into(img);
                                    respGroup.addView(img,4);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            respGroup.removeView(img);
                                            resp5.setVisibility(View.VISIBLE);
                                            resp5TV.setPadding(0,0,0,0);
                                        }
                                    }, 1000);
                                }
                                else {
                                    resp5TV.setTextSize(14);
                                    resp5TV.setTextColor(defCol);
                                }
                            }
                        });

                    }
                    //else for EditText field questions
                    else{
                        EditText reply = new EditText(getContext());
                        reply.setTag("respE"+i);
                        LinearLayout.LayoutParams ETparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        ETparams.setMargins(0,0,0,90);
                        reply.setHint("Answer here");
                        reply.setTextSize(14);
                        reply.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES); //input type for First capital letter on sentences
                        reply.setLayoutParams(ETparams);
                        questionsLL.addView(reply);

                    }
                }
                loading.setVisibility(View.GONE);
                TVloading.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Function to retrieve and fill up the vulgarity filter list from Firebase
     */
    public void fillFilterList(){
        db.collection("Filters").document("Vulgars").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> map = document.getData();
                        if (map != null) {
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                filterList.add(entry.getValue().toString());
                            }
                        }

                    }
                }
            }
        });

    }
}