package fr.coding.yourandroidwebapp;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.coding.tools.DiskCacheImageViewUrl;
import fr.coding.tools.ImageViewUrl;
import fr.coding.yourandroidwebapp.settings.WebApp;

public class MyWebAppRecyclerViewAdapter extends RecyclerView.Adapter<MyWebAppRecyclerViewAdapter.ViewHolder> {

    private final List<WebApp> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyWebAppRecyclerViewAdapter(List<WebApp> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_startup_list_webapp_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        if (!TextUtils.isEmpty(holder.mItem.iconUrl))
            new DiskCacheImageViewUrl(holder.mView.getContext() ,holder.mImageView).execute(holder.mItem.iconUrl);
        holder.mContentView.setText(holder.mItem.name);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mContentView;
        public WebApp mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.img);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(WebApp item);
    }
}
