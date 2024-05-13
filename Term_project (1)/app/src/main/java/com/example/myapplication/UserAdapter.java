package com.example.myapplication;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

     List<User> mUsers;

     UserAdapter(List<User> userList) {
        mUsers = userList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setUsers(List<User> users) {
        mUsers = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return mUsers != null ? mUsers.size() : 0;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
         final TextView displayNameTextView;
         final TextView emailTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            displayNameTextView = itemView.findViewById(R.id.et_display_name);
            emailTextView = itemView.findViewById(R.id.et_email);
        }

        public void bind(User user) {
            displayNameTextView.setText(user.getDisplayName());
            emailTextView.setText(user.getEmail());
        }
    }
}