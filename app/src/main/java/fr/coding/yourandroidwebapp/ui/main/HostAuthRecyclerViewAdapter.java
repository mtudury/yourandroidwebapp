package fr.coding.yourandroidwebapp.ui.main;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import fr.coding.tools.model.HostAuth;
import fr.coding.yourandroidwebapp.HostAuthActivity;
import fr.coding.yourandroidwebapp.R;
import fr.coding.yourandroidwebapp.WebAppDetail;
import fr.coding.yourandroidwebapp.settings.WebApp;

public class HostAuthRecyclerViewAdapter extends RecyclerView.Adapter<HostAuthRecyclerViewAdapter.ViewHolder> {

    private final List<HostAuth> mValues;
    private final OnListFragmentInteractionListener mListener;

    public HostAuthRecyclerViewAdapter(List<HostAuth> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_hostauth_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText("Host : "+holder.mItem.Host + ", Login : "+holder.mItem.Login);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final ImageButton mDeleteButton;
        public HostAuth mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.hostauth_content);
            mDeleteButton = view.findViewById(R.id.hostauth_deleteButton);

            mDeleteButton.setOnClickListener(view1 -> {
                ((HostAuthActivity)view1.getContext()).DeleteHostAuth(mItem.id);
                notifyDataSetChanged();
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(HostAuth item);
    }
}
