package com.example.ezsurvey.ui.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class StatisticsFragment extends Fragment {

    private StatisticsViewModel statisticsViewModel;
    private String selectedForm;
    private FirebaseFirestore db;
    private ArrayList<Integer>qScoreList;
    private ArrayList<String>questions;
    private GraphView graph,graph2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticsViewModel =
                ViewModelProviders.of(this).get(StatisticsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistics, container, false);
        selectedForm = getActivity().getIntent().getStringExtra(HomeActivity.SELECTED_FORM);
        final TextView graphTitle = root.findViewById(R.id.graphTitle);
        graphTitle.setText(selectedForm + " Overall Statistics");
        db = FirebaseFirestore.getInstance();
        qScoreList = new ArrayList<>();
        questions = new ArrayList<>();
        graph = (GraphView) root.findViewById(R.id.graph);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Recent Responses");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Score");
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(15);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxY(60);
        graph.getViewport().setMaxX(10);
        graph.getViewport().setScrollable(true);

        graph2 = (GraphView) root.findViewById(R.id.graph2);
        graph2.getGridLabelRenderer().setVerticalAxisTitle("Score");
        graph2.getGridLabelRenderer().setVerticalAxisTitleTextSize(15);
        graph2.getViewport().setMaxY(60);
        graph2.getViewport().setYAxisBoundsManual(true);
        graph2.getViewport().setScrollable(true);

        db.collection("Responses").whereEqualTo("formName",selectedForm).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    DataPoint[] dp = new DataPoint[task.getResult().size()];
                    int index = 0;
                    for(QueryDocumentSnapshot document:task.getResult()){
                        int totalScorePerResp=0;
                        int qScoreIndex = -1;
                        for(int i = 0; i<document.getLong("noOfQuestions");i++){
                            if(document.getString("type"+i).equals("common")){
                                qScoreIndex++;
                                if(!(questions.contains("Question "+(i+1)))){
                                    questions.add("Question "+(i+1));
                                }
                                totalScorePerResp = totalScorePerResp + Integer.parseInt(document.getString("resp"+i));
                                if(index==0){
                                    qScoreList.add(Integer.parseInt(document.getString("resp"+i)));
                                }
                                else{
                                    qScoreList.set(qScoreIndex,qScoreList.get(qScoreIndex)+Integer.parseInt(document.getString("resp"+i)));
                                }
                            }
                        }
                        dp[index]=new DataPoint(index,totalScorePerResp);
                        index++;
                    }
                    if(dp.length>0) {
                        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dp);
                        graph.addSeries(series);
                    }
                    else{
                        graph.setTitle("Not enough data collected");
                    }

                    StaticLabelsFormatter slf = new StaticLabelsFormatter(graph2);
                    String[] questionNames = new String[questions.size()];
                    for(int i = 0;i<questions.size();i++){
                        questionNames[i] = questions.get(i);
                    }
                    if(questionNames.length>1) {
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
                }
            }
        });
        return root;
    }
}