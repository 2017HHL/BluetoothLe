package com.example.bluetooth.le;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wyw on 2017/6/20.
 */

public class FileShowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List list=new ArrayList();
    private Context context;
    private LayoutInflater mLayoutInflater;
    View headerView;
    public FileShowAdapter(Context context,List list) {
        this.context = context;
        this.list = list;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public List getDates() {
        return list;
    }

    public void setmDatas(List list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void addAll(List list) {
        if (this.list == null) {
            this.list = list;
        } else {
            this.list.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListviewViewHolder(mLayoutInflater.inflate(R.layout.device_bcfh_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ListviewViewHolder) {
//            ((ListviewViewHolder) holder).cj.setText("1111");
//            ((ListviewViewHolder) holder).jd.setText("1111");
//            ((ListviewViewHolder) holder).fw.setText("1111");
//            ((ListviewViewHolder) holder).gps.setText("1111");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class ListviewViewHolder extends RecyclerView.ViewHolder {
        public TextView cj;
        public TextView jd;
        public TextView fw;
        public TextView gps;

        private ListviewViewHolder(View view) {
            super(view);
            cj = (TextView) view.findViewById(R.id.cj);
            jd = (TextView) view.findViewById(R.id.jd);
            fw = (TextView) view.findViewById(R.id.fw);
            gps = (TextView) view.findViewById(R.id.gps);
        }
    }
}
