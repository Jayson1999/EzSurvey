package com.example.ezsurvey.ui.results;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ezsurvey.HomeActivity;
import com.example.ezsurvey.Question;
import com.example.ezsurvey.R;
import com.example.ezsurvey.ResultRVAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ResultsFragment extends Fragment {

    private ResultsViewModel resultsViewModel;
    private RecyclerView mResultRecyclerView;
    private FirebaseFirestore db;
    private ArrayList<Question> responsesList;
    private ResultRVAdapter mAdapter;
    private String selectedForm;
    private LinearLayoutManager linearLayoutManager;
    private ConstraintLayout resultCL;
    private TextView loadingTv,emptyTV;
    private ImageView loadingImg;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        resultsViewModel =
                ViewModelProviders.of(this).get(ResultsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_results, container, false);
        final TextView resultTitle = root.findViewById(R.id.resultTitle);
        mResultRecyclerView = root.findViewById(R.id.resultRV);
        resultCL = root.findViewById(R.id.resultCL);
        loadingTv = root.findViewById(R.id.loadingTV);
        loadingImg = root.findViewById(R.id.loadingImg);
        emptyTV = root.findViewById(R.id.emptyTV);
        Glide.with(getContext()).load(R.drawable.loading).apply(new RequestOptions().override(400)).into(loadingImg);
        responsesList = new ArrayList<>();
        mAdapter = new ResultRVAdapter(getActivity(),responsesList);
        linearLayoutManager= new LinearLayoutManager(getActivity());
        db = FirebaseFirestore.getInstance();
        selectedForm = getActivity().getIntent().getStringExtra(HomeActivity.SELECTED_FORM);
        resultTitle.setText(selectedForm+" Responses");
        mResultRecyclerView.setLayoutManager(linearLayoutManager);
        mResultRecyclerView.setAdapter(mAdapter);

        db.collection("Responses").whereEqualTo("formName",selectedForm).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        int score = 0;
                        String review = "";
                        for(int i = 0; i<document.getLong("noOfQuestions"); i++){
                            if(document.getString("type"+i).equals("common")){
                                score = score+Integer.parseInt(document.getString("resp"+i));
                            }
                            else{
                                review = review+document.getString("resp"+i)+". ";
                            }
                        }
                        responsesList.add(new Question(review,"","Score: "+score, document.getId()));
                    }
                    if(responsesList.isEmpty())
                        emptyTV.setVisibility(View.VISIBLE);
                    mAdapter.notifyDataSetChanged();
                    resultCL.removeView(loadingImg);
                    resultCL.removeView(loadingTv);
                }
            }
        });

        return root;
    }

}