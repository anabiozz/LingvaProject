package com.almexe.lingvaproject.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.almexe.lingvaproject.R;

import java.util.List;

public class SentencesListAdapter extends RecyclerView.Adapter<SentencesListAdapter.WordsViewHolder>{

    private List<String> data;

    public SentencesListAdapter(List<String> data) {
        this.data = data;
    }

    @Override
    public SentencesListAdapter.WordsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_examples, parent, false);
        return new WordsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(WordsViewHolder holder, int position) {
        holder.title.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class WordsViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView title;

        public WordsViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            cardView = (CardView)itemView.findViewById(R.id.cardViewExamples);
            title = (TextView)itemView.findViewById(R.id.title);
        }

    }
}