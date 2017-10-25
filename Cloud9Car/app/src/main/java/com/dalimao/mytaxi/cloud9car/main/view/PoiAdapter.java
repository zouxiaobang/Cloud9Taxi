package com.dalimao.mytaxi.cloud9car.main.view;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dalimao.mytaxi.R;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by zouxiaobang on 10/24/17.
 */

public class PoiAdapter extends ArrayAdapter {
    private LayoutInflater mInflater;
    private List<String> mData;
    private OnItemtClickListener mOnItemtClickListener;

    public PoiAdapter(@NonNull Context context, List data) {
        super(context, R.layout.poi_list_item);

        this.mData = data;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnItemtClickListener(OnItemtClickListener onItemtClickListener){
        this.mOnItemtClickListener = onItemtClickListener;
    }

    public void setData(List<String> data){
        this.mData.clear();
        this.mData.addAll(data);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Holder holder = null;
        if (convertView == null){
            holder = new Holder();
            convertView = mInflater.inflate(R.layout.poi_list_item, null);

            holder.mTextView = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (mOnItemtClickListener != null){
//                        int pos = ((Holder)view.getTag()).id;
//                        mOnItemtClickListener.onItemClick(pos);
//                    }
//                }
//            });
        } else {
            Object tag = convertView.getTag();
            if (tag == null){
                holder = new Holder();
                holder.mTextView = (TextView) convertView.findViewById(R.id.name);
                convertView.setTag(holder);
            } else {
                holder = (Holder) tag;
            }
        }

        holder.id = position;
        holder.mTextView.setText(mData.get(position));


        return convertView;
    }

    class Holder{
        int id;
        TextView mTextView;
    }

    interface OnItemtClickListener{
        void onItemClick(int position);
    }
}
