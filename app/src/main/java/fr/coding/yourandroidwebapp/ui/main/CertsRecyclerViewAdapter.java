package fr.coding.yourandroidwebapp.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import fr.coding.tools.model.HostAuth;
import fr.coding.tools.model.SslByPass;
import fr.coding.yourandroidwebapp.CertsActivity;
import fr.coding.yourandroidwebapp.R;

public class CertsRecyclerViewAdapter extends RecyclerView.Adapter<CertsRecyclerViewAdapter.ViewHolder> {

    private final List<SslByPass> mValues;
    private final OnListFragmentInteractionListener mListener;

    public CertsRecyclerViewAdapter(List<SslByPass> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_cert_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText("Host : "+holder.mItem.Host + ", DtExpire : "+holder.mItem.ValidNotAfter);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final ImageButton mDeleteButton;
        public SslByPass mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.hostauth_content);
            mDeleteButton = view.findViewById(R.id.hostauth_deleteButton);

            mDeleteButton.setOnClickListener(view1 -> {
                ((CertsActivity)view1.getContext()).DeleteCert(mItem.id);
                notifyDataSetChanged();
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(SslByPass item);
    }
}
