package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecViewAdapter extends RecyclerView.Adapter<RecViewAdapter.ViewHolder> {
    private ArrayList<RecViewContent> contents = new ArrayList<>();
    private String[] activityNames;

    private Context context;
    public RecViewAdapter(Context context, String[] activityNames){
        this.activityNames = activityNames;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String activityName = activityNames[position];
        holder.button.setText(activityName);

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Class<?> activityClass = Class.forName(context.getPackageName() + "." + activityName);
                    Intent intent = new Intent(context, activityClass);
                    context.startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.button.setText(contents.get(position).getName());
        Glide.with(context)
                .asBitmap()
                .load(contents.get(position).getImage())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setContents(ArrayList<RecViewContent> contents) {
        this.contents = contents;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private Button button;
        private ImageView imageView;
        private RelativeLayout parent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.relatList);
            button = itemView.findViewById(R.id.button);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
