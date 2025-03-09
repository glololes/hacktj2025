package com.example.hacktj20.ui.dashboard;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hacktj20.R;

import java.util.List;

public class SavedListAdapter extends RecyclerView.Adapter<SavedListAdapter.ViewHolder> {

    private List<SavedItem> savedItems;

    public SavedListAdapter(List<SavedItem> savedItems) {
        this.savedItems = savedItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavedItem item = savedItems.get(position);
        holder.brand.setText(item.getBrand());
        holder.productName.setText(item.getProductName());
        holder.priceScore.setText(item.getPriceScore());
    }

    @Override
    public int getItemCount() {
        return savedItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView brand, productName, priceScore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            brand = itemView.findViewById(R.id.brand);
            productName = itemView.findViewById(R.id.productName);
            priceScore = itemView.findViewById(R.id.priceScore);
        }
    }
}
