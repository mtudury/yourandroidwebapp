package fr.coding.yourandroidwebapp.ui.main;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.coding.tools.DiskCacheImageViewUrl;
import fr.coding.yourandroidwebapp.R;
import fr.coding.yourandroidwebapp.WebAppDetail;
import fr.coding.yourandroidwebapp.settings.WebApp;

public class WebAppRecyclerViewAdapter extends RecyclerView.Adapter<WebAppRecyclerViewAdapter.ViewHolder> {

    private final List<WebApp> mValues;
    private final OnListFragmentInteractionListener mListener;
    private boolean visible = false;

    public WebAppRecyclerViewAdapter(List<WebApp> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public void setVisible() {
        visible = true;
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
        if (!TextUtils.isEmpty(holder.mItem.iconUrl)) {
            new DiskCacheImageViewUrl(holder.mView.getContext(), holder.mImageView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, holder.mItem.iconUrl);
        }
        holder.mContentView.setText(holder.mItem.name);
        if (visible)
            holder.mEditButton.setVisibility(View.VISIBLE);

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
        public final ImageButton mEditButton;
        public WebApp mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = view.findViewById(R.id.img);
            mContentView = view.findViewById(R.id.content);
            mEditButton = view.findViewById(R.id.editButton);

            mEditButton.setOnClickListener(view1 -> {
                Intent detailIntent = new Intent(view1.getContext(), WebAppDetail.class);
                detailIntent.putExtra("webappid", mItem.id);
                view1.getContext().startActivity(detailIntent);
            });
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
