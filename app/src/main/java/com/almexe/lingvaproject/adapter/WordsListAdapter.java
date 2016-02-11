package com.almexe.lingvaproject.adapter;

import android.graphics.Color;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.almexe.lingvaproject.R;

import java.util.ArrayList;
import java.util.List;

public class WordsListAdapter extends RecyclerView.Adapter<WordsListAdapter.WordsViewHolder> {

    private List<String> data;
    private List<String> nativeData;
    private SparseBooleanArray selectedItems;

    public WordsListAdapter(List<String> data, List<String> nativeData) {
        this.data = data;
        this.nativeData = nativeData;
    }

    @Override
    public WordsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false);
        return new WordsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(WordsViewHolder holder, int position) {
        holder.title.setText(data.get(position));
        holder.nativeTitle.setText(nativeData.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class WordsViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView title;
        TextView nativeTitle;

        public WordsViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            cardView = (CardView)itemView.findViewById(R.id.cardView);
            title = (TextView)itemView.findViewById(R.id.title);
            nativeTitle = (TextView)itemView.findViewById(R.id.nativeTitle);
        }

    }

}
