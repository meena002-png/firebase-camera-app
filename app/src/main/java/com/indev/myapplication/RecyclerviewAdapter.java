package com.indev.myapplication;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.MyViewHolder> {

    private ArrayList<Member> mList;
    private Context context;

    public RecyclerviewAdapter(ArrayList<Member> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_items,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(Uri.parse(mList.get(position).getLink())).into(holder.imageView);
        holder.nameView.setText(mList.get(position).getName());
        holder.dateView.setText(mList.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView nameView,dateView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            nameView = itemView.findViewById(R.id.nameView);
            dateView = itemView.findViewById(R.id.dateView);
        }
    }


}
//        Picasso.get().load(imageInfo.get(position).link).into(holder.imageView);