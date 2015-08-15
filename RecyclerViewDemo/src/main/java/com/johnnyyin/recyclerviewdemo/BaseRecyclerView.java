package com.johnnyyin.recyclerviewdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

public class BaseRecyclerView extends RecyclerView {
    public abstract static class BaseViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        protected int mPosition;
        protected View mRootView;
        protected BaseRecyclerView mRecyclerView;

        public BaseViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView;
        }

        @Override
        public void onClick(View v) {
        }
    }

    public abstract static class BaseAdapter<VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {
        protected BaseRecyclerView mRecyclerView;

        public BaseAdapter(BaseRecyclerView recyclerView) {
            mRecyclerView = recyclerView;
        }

        @Override
        public void onBindViewHolder(VH vh, int i) {
            vh.mPosition = i;
            if (!vh.mRootView.hasOnClickListeners()) {
                vh.mRootView.setOnClickListener(vh);
            }
        }
    }

    AdapterView.OnItemClickListener mOnItemClickListener;

    public BaseRecyclerView(Context context) {
        super(context);
    }

    public BaseRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

}
