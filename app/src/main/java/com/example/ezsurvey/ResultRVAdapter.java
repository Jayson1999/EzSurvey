package com.example.ezsurvey;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ResultRVAdapter extends RecyclerView.Adapter<ResultRVAdapter.ViewHolder> {

    Context context;
    ArrayList<Question> resultList = new ArrayList<>();

    public ResultRVAdapter(Context context,
                         ArrayList<Question> resultList) {
        this.resultList = resultList;
        this.context = context;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView resDate,resSub,resReply;
        ConstraintLayout RVCL;
        FirebaseFirestore db;

        public ViewHolder(View itemView) {
            super(itemView);
            resDate = itemView.findViewById(R.id.resDate);
            resSub = itemView.findViewById(R.id.resSub);
            resReply = itemView.findViewById(R.id.resReply);
            RVCL = itemView.findViewById(R.id.RVCL);
            db = FirebaseFirestore.getInstance();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_result, parent, false);

        ResultRVAdapter.ViewHolder vh = new ResultRVAdapter.ViewHolder(v);

        return vh;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Question result = resultList.get(position);

        holder.resDate.setText(result.getDate());
        holder.resSub.setText(result.getReply());
        holder.resReply.setText(result.getName());
        holder.RVCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.resDate.setTextColor(Color.parseColor("#FCB50E"));
                holder.RVCL.setBackgroundColor(Color.WHITE);
                Intent i = new Intent(holder.itemView.getContext(),Result.class);
                i.putExtra("RESPONSE",result.getDate());
                holder.itemView.getContext().startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {

        return resultList.size();
    }

}
