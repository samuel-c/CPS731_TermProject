package com.example.cps731_termproject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cps731_termproject.utils.NewsItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<NewsItem> dataList;
    private Activity context;

    // UI
    //TextView txt_title, txt_authorSource, txt_desc;

    public NewsAdapter(Activity context, List<NewsItem> dataList){
        this.context = context;
        this.dataList = dataList;

        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Init view
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_newsitem, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position) {
        NewsItem newsItem = dataList.get(position);
        holder.txt_title.setText(newsItem.getTitle());
        holder.txt_source.setText(newsItem.getSource() + (newsItem.getAuthor().equals("null") ? "" : " - " + newsItem.getAuthor()) );
        holder.txt_desc.setText(newsItem.getDesc());
        Picasso.get().load(newsItem.getUrlToImage()).into(holder.image_news);

        holder.image_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse(newsItem.getUrlToSource()));
                context.startActivity(viewIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_title, txt_source, txt_desc;
        private ImageView image_news;

        public ViewHolder(@NonNull View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            txt_title = view.findViewById(R.id.textNewsTitle);
            txt_source = view.findViewById(R.id.textAuthorSource);
            txt_desc = view.findViewById(R.id.textNewsDescription);
            txt_desc.setMovementMethod(new ScrollingMovementMethod());
            image_news = view.findViewById(R.id.imageNews);
        }

    }

}
