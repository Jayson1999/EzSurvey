package com.example.ezsurvey.ui.statistics;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.ezsurvey.HomeActivity;
import com.example.ezsurvey.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class StatisticsFragment extends Fragment {

    private String selectedForm;
    private FirebaseFirestore db;
    private ArrayList<Integer>qScoreList;
    private ArrayList<String>questions;
    private GraphView graph,graph2;
    private LinearLayout graphLL;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_statistics, container, false);

        selectedForm = getActivity().getIntent().getStringExtra(HomeActivity.SELECTED_FORM);    //get Intent Extra on Selected Form
        final TextView graphTitle = root.findViewById(R.id.graphTitle);
        graphLL = (LinearLayout) root.findViewById(R.id.graphLL);
        graphTitle.setText(selectedForm + " Overall Statistics");
        db = FirebaseFirestore.getInstance();
        qScoreList = new ArrayList<>();
        questions = new ArrayList<>();

        //First Graph View settings
        graph = (GraphView) root.findViewById(R.id.graph);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Recent Responses");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Total Scores");
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(15);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxY(60);
        graph.getViewport().setMaxX(10);
        graph.getViewport().setScrollable(true);

        //Second Graph View settings
        graph2 = (GraphView) root.findViewById(R.id.graph2);
        graph2.getGridLabelRenderer().setVerticalAxisTitle("Total Scores");
        graph2.getGridLabelRenderer().setVerticalAxisTitleTextSize(15);
        graph2.getViewport().setMaxY(60);
        graph2.getViewport().setYAxisBoundsManual(true);
        graph2.getViewport().setScrollable(true);

        //Retrieving Responses data from Firebase
        db.collection("Responses").whereEqualTo("formName",selectedForm).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    //set DataPoint for graphs plotting value
                    DataPoint[] dp = new DataPoint[task.getResult().size()];
                    int index = 0;  //index counter to track below foreach loop of QueryDocumentSnapshot for Graph 1
                    for(QueryDocumentSnapshot document:task.getResult()){
                        int totalScorePerResp=0;    //accumulate total score per response/person for Graph 1
                        int qScoreIndex = -1;   //question score list index to track score accumulated base on Question No.
                        for(int i = 0; i<document.getLong("noOfQuestions");i++){
                            //if common type question with Scores
                            if(document.getString("type"+i).equals("common")){
                                qScoreIndex++;
                                //Adding Question string for Graph 2
                                if(!(questions.contains("Question "+(i+1)))){
                                    questions.add("Question "+(i+1));
                                }
                                totalScorePerResp = totalScorePerResp + Integer.parseInt(document.getString("resp"+i)); //accumulate score per response/person for Graph 1
                                if(index==0){   //Add into list on first loop
                                    qScoreList.add(Integer.parseInt(document.getString("resp"+i)));
                                }
                                else{   //After that, replace by adding subsequent score for Graph 2
                                    qScoreList.set(qScoreIndex,qScoreList.get(qScoreIndex)+Integer.parseInt(document.getString("resp"+i)));
                                }
                            }
                        }
                        dp[index]=new DataPoint(index,totalScorePerResp);   //add new datapoint for each response indicate tracking score for each response
                        index++;
                    }
                    //Validate if there's any response
                    if(dp.length>1) {
                        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dp);
                        graph.addSeries(series);
                    }
                    else{
                        graph.setTitle("Not enough data collected");
                    }
                    //Graph 2 Label Formatter settings
                    StaticLabelsFormatter slf = new StaticLabelsFormatter(graph2);
                    String[] questionNames = new String[questions.size()];
                    for(int i = 0;i<questions.size();i++){  //Passing arrayList into Array for DataPoint usage
                        questionNames[i] = questions.get(i);
                    }
                    //if passed arrayList is not empty
                    if(questionNames.length>1) {    //conduct Graph 2 value and label settings
                        slf.setHorizontalLabels(questionNames);
                        graph2.getGridLabelRenderer().setLabelFormatter(slf);
                        DataPoint[] dp2 = new DataPoint[qScoreList.size()];
                        for (int j = 0; j < qScoreList.size(); j++) {
                            dp2[j] = new DataPoint(j, qScoreList.get(j));
                        }
                        BarGraphSeries<DataPoint> seriesBar = new BarGraphSeries<>(dp2);
                        seriesBar.setSpacing(10);
                        graph2.addSeries(seriesBar);
                    }
                    else {
                        graph2.setTitle("Not enough data collected");
                    }

                    if(!(task.getResult().isEmpty())) {
                        //Continue Creating graphs depends on how many questions exist
                        //for each question
                        int questionNamesCounter = 0;     //counter to track question name and show as graph title
                        //for each question
                        for (int questionCounter = 0; questionCounter < task.getResult().getDocuments().get(0).getLong("noOfQuestions"); questionCounter++) {
                            int scoreArray[] = {0, 0, 0, 0, 0};     //to store counters based on 5 different scores
                            //for each response
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //for Common type questions
                                if (document.getString("type" + questionCounter).equals("common")) {
                                    //for each score
                                    for (int scoreCounter = 1; scoreCounter <= 5; scoreCounter++) {
                                        if (Integer.parseInt(document.getString("resp" + questionCounter)) == scoreCounter) {
                                            scoreArray[scoreCounter - 1]++;
                                        }
                                    }
                                }
                            }
                            //empty check to check if it's Common type question
                            boolean emptyCheck = true;
                            for (int checkCounter = 0; checkCounter < 5; checkCounter++) {
                                if (scoreArray[checkCounter] != 0) {
                                    emptyCheck = false;     //if it's not empty, it's Common type question
                                    break;
                                }
                            }
                            //Only create graphs for Common Type Questions
                            if (!emptyCheck) {
                                //create Graph View
                                GraphView newGraphView = new GraphView(getContext());
                                //Graph View settings
                                DisplayMetrics displayMetrics = new DisplayMetrics();   //get screen width for layout settings
                                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                LinearLayout.LayoutParams newParams = new LinearLayout.LayoutParams(displayMetrics.widthPixels-180, 600);
                                newParams.setMargins(0, 90, 0, 0);
                                newParams.gravity = Gravity.CENTER;
                                newGraphView.setLayoutParams(newParams);
                                graphLL.addView(newGraphView);

                                //create Bar Graph series
                                DataPoint[] newDP = new DataPoint[5];
                                for (int dpCounter = 0; dpCounter < 5; dpCounter++) {
                                    newDP[dpCounter] = new DataPoint(dpCounter + 1, scoreArray[dpCounter]);
                                }
                                BarGraphSeries<DataPoint> newGraphSeries = new BarGraphSeries<>(newDP);
                                newGraphSeries.setSpacing(10);

                                //Graph settings
                                newGraphView.addSeries(newGraphSeries);
                                newGraphView.setTitle(questionNames[questionNamesCounter]);     //set title with questionNames stored previously
                                newGraphView.getGridLabelRenderer().setHorizontalAxisTitle("Score");

                                questionNamesCounter++; //increment counter to proceed to next name for next graph
                            }
                        }
                    }
                }
            }
        });
        return root;
    }
}