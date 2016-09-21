package com.geeky7.rohit.location.adapter;

/**
 * Created by Rohit on 16/09/2016.
 */

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.geeky7.rohit.location.DataObject;
import com.geeky7.rohit.location.R;

import java.util.ArrayList;


public class SemiAutomaticRecyclerViewAdapter extends RecyclerView
        .Adapter<SemiAutomaticRecyclerViewAdapter
        .AutomaticDataObjectHolder> {
    private static String LOG_TAG = "SemiAutomaticRecyclerViewAdapter";
    private ArrayList<DataObject> mDataset;
    private static MyClickListener myClickListener;
    public SemiAutomaticRecyclerViewAdapter(){}
    public static class AutomaticDataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView label;
        TextView dateTime;
        ImageView icon;
        CardView cardView;

        public AutomaticDataObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.textView);
            dateTime = (TextView) itemView.findViewById(R.id.textView2);
            icon = (ImageView)itemView.findViewById(R.id.imageView);
            cardView = (CardView)itemView.findViewById(R.id.card_view);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public SemiAutomaticRecyclerViewAdapter(ArrayList<DataObject> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public AutomaticDataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.semiautomatic_card_view, parent, false);
        AutomaticDataObjectHolder AutomaticDataObjectHolder = new AutomaticDataObjectHolder(view);
        return AutomaticDataObjectHolder;
    }

    @Override
    public void onBindViewHolder(AutomaticDataObjectHolder holder, int position) {
        holder.label.setText(mDataset.get(position).getmText1());
        holder.dateTime.setText(mDataset.get(position).getmText2());
        holder.icon.setImageDrawable(mDataset.get(position).getmImage());
//        if (position==2)
//            holder.cardView.setCardBackgroundColor(Color.GREEN);
//        if (position==0||position==1||position==3)
            holder.cardView.setEnabled(false);
//        holder.cardView.
    }

    public void addItem(DataObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
