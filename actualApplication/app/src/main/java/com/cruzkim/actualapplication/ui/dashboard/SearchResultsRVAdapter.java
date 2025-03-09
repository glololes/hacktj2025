package com.cruzkim.actualapplication.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cruzkim.actualapplication.R;

import java.util.ArrayList;

public class SearchResultsRVAdapter extends RecyclerView.Adapter<SearchResultsRVAdapter.ViewHolder> {

    // arraylist for storing our data and context
    private ArrayList<dataModal> dataModalArrayList;
    private Context context;

    // constructor for our variables.
    public SearchResultsRVAdapter(ArrayList<dataModal> dataModalArrayList, Context context) {
        this.dataModalArrayList = dataModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inside on create view holder method we are inflating our layout file which we created.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_results_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // inside on bind view holder method we are setting
        // data to each item of recycler view.
        dataModal modal = dataModalArrayList.get(position);

        // Set title with sustainability indicator if present
        String title = modal.getTitle();
        if (title != null) {
            if (containsSustainabilityKeyword(title)) {
                holder.titleTV.setText("♻️ " + title);
            } else {
                holder.titleTV.setText(title);
            }
        } else {
            holder.titleTV.setText("No Title Available");
        }

        // Set link and snippet
        holder.snippetTV.setText(modal.getDisplayed_link());

        // Add sustainability indicator to description
        String snippet = modal.getSnippet();
        if (snippet != null && containsSustainabilityKeyword(snippet)) {
            holder.descTV.setText("♻️ " + snippet);
        } else {
            holder.descTV.setText(snippet);
        }

        // Set click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = modal.getLink();
                if (link != null && !link.isEmpty()) {
                    // opening a link in your browser.
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(link));
                    context.startActivity(i);
                }
            }
        });
    }

    private boolean containsSustainabilityKeyword(String text) {
        if (text == null) return false;

        String lowerText = text.toLowerCase();
        String[] sustainabilityKeywords = {
                "sustainable", "eco-friendly", "green", "environmentally", "recycl",
                "biodegradable", "reusable", "renewable", "carbon", "ecological"
        };

        for (String keyword : sustainabilityKeywords) {
            if (lowerText.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return dataModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our text view.
        private TextView titleTV, descTV, snippetTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our views with their ids.
            titleTV = itemView.findViewById(R.id.idTVTitle);
            descTV = itemView.findViewById(R.id.idTVDescription);
            snippetTV = itemView.findViewById(R.id.idTVSnippet);
        }
    }
}