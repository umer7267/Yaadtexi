package com.travel.taxi.Adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.travel.taxi.ApiResponse.Service.Service;
import com.travel.taxi.Map.DashBoard;
import com.travel.taxi.R;

import java.util.List;


public class TaxiSlection extends RecyclerView.Adapter<TaxiSlection.MyViewHolder> {

    public List<Service> mDataset;
    public Context context;

    private TextView previousTextView;
    private Service selected;

    // Provide a suitable constructor (depends on the kind of dataset)
    public TaxiSlection(List<Service> myCarsList, Context context) {
        this.mDataset = myCarsList;
        this.context = context;
    }

    public void update(List<Service> names) {
        this.mDataset = names;
        notifyDataSetChanged();

    }

    // Create new views (invoked by the layout manager)
    @Override
    public TaxiSlection.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.car_layout, parent, false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (mDataset != null) {
            holder.mTextView.setText(mDataset.get(position).getName());
            Glide.with(context).load(mDataset.get(position).getImage()).into(holder.mCar);

            holder.parent.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View view) {
                    if (previousTextView == null) {

                        previousTextView = holder.mTextView;
                        holder.mTextView.setBackground(ContextCompat.getDrawable(context, R.drawable.background_selected_car));
                        holder.mTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
                        selected = mDataset.get(position);
                        ((DashBoard) (context)).slected = selected;
                    }
                    else {
                        if (previousTextView.getText().toString().equals(holder.mTextView.getText().toString())) {
                            ((DashBoard) (context)).handleSecondClickOnTheServiceItem(mDataset.get(position));
                        } else {
                            previousTextView.setBackground(ContextCompat.getDrawable(context, R.drawable.background_unselected_car));
                            previousTextView.setTextColor(ContextCompat.getColor(context, R.color.back));
                            previousTextView = holder.mTextView;
                            holder.mTextView.setBackground(ContextCompat.getDrawable(context, R.drawable.background_selected_car));
                            holder.mTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
                            selected = mDataset.get(position);
                            ((DashBoard) (context)).slected = selected;
                        }
                    }

                }
            });
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mDataset != null)
            return mDataset.size();
        else
            return 0;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageView mCar;
        public LinearLayout parent;


        public MyViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.seating_capacity);
            mCar = v.findViewById(R.id.car);
            parent = v.findViewById(R.id.layout);


        }
    }
}
