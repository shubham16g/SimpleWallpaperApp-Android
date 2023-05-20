package com.shubhamgupta16.simplewallpaper.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.shubhamgupta16.simplewallpaper.models.CategoryPOJO;
import com.shubhamgupta16.simplewallpaper.activities.MoreWallsActivity;
import com.shubhamgupta16.simplewallpaper.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final Context context;
    private final List<CategoryPOJO> list;

    public CategoryAdapter(Context context, List<CategoryPOJO> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.category_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        int extraPadding = context.getResources().getDimensionPixelOffset(R.dimen.wall_card_space);
        if (position % 2 == 0) {
            holder.itemView.setPadding(extraPadding, 0, 0, 0);
        } else {
            holder.itemView.setPadding(0, 0, extraPadding, 0);
        }

        CategoryPOJO pojo = list.get(position);
        Glide.with(context).load(pojo.getPreview1()).transition(DrawableTransitionOptions.withCrossFade()).into(holder.image1);
                Glide.with(context).load(pojo.getPreview2()).transition(DrawableTransitionOptions.withCrossFade()).into(holder.image2);
        Glide.with(context).load(pojo.getPreview3()).transition(DrawableTransitionOptions.withCrossFade()).into(holder.image3);
        holder.title.setText(pojo.getName());
        holder.subtitle.setText(context.getResources().getQuantityString(R.plurals.wallpaper_s,
                pojo.getWallsCount(), pojo.getWallsCount()));
        holder.card.setOnClickListener(view -> {
            Intent i = new Intent(context, MoreWallsActivity.class);
            i.putExtra("category", pojo.getName());
            context.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image1, image2, image3;
        private final TextView title, subtitle;
        private final View card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subtitle = itemView.findViewById(R.id.subtitle);
            image1 = itemView.findViewById(R.id.image1);
            image2 = itemView.findViewById(R.id.image2);
            image3 = itemView.findViewById(R.id.image3);
            title = itemView.findViewById(R.id.title);
            card = itemView.findViewById(R.id.card);
        }
    }
}