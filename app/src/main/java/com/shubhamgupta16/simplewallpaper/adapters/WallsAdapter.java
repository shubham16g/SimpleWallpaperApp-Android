package com.shubhamgupta16.simplewallpaper.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.shubhamgupta16.simplewallpaper.R;
import com.shubhamgupta16.simplewallpaper.data_source.DataService;
import com.shubhamgupta16.simplewallpaper.activities.WallpaperActivity;
import com.shubhamgupta16.simplewallpaper.models.WallsPOJO;

import java.util.List;
import java.util.Objects;

public class WallsAdapter extends RecyclerView.Adapter<WallsAdapter.ViewHolder> {

    private final Context context;
    private final List<WallsPOJO> list;
    private final DataService dataService;
    private DataService.QueryType type;

    public WallsAdapter(Context context, DataService dataService, List<WallsPOJO> list, DataService.QueryType type) {
        this.context = context;
        this.list = list;
        this.type = type;
        this.dataService = dataService;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        if (viewType == 1)
            view = inflater.inflate(R.layout.walls_layout, parent, false);

        else if (viewType == 2)
            view = inflater.inflate(R.layout.ad_layout, parent, false);
        else
            view = inflater.inflate(R.layout.loading_layout, parent, false);
        return new ViewHolder(view, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getId() == -1)
            return 0;
        else if (list.get(position).getId() == -2)
            return 2;
        else return 1;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WallsPOJO pojo = list.get(position);

        if (getItemViewType(position) == 0)
            return;
        if (getItemViewType(position) == 2) {
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);

            NativeAd ad = pojo.getNativeAd();
            if (ad != null) {
                NativeAdView unifiedNativeAdView = holder.adView;
                unifiedNativeAdView.setVisibility(View.VISIBLE);
                mapUnifiedNativeAdToLayout(pojo.getNativeAd(), unifiedNativeAdView);
            }
            return;
        }

        holder.premiumImage.setVisibility(pojo.isPremium() ? View.VISIBLE : View.GONE);
        holder.heartImage.setVisibility(pojo.isPremium() ? View.GONE : View.VISIBLE);
        Glide.with(context).load(pojo.getPreviewUrl()).into(holder.imageView);
        holder.title.setText(pojo.getName());
        holder.card.setOnClickListener(view -> {
            Intent i = new Intent(context, WallpaperActivity.class);
            i.putExtra("pojo", pojo);
            context.startActivity(i);
        });

        handleHeart(position, pojo.getId(), holder.heartImage);
    }

    private void handleHeart(final int position, final int id, final ImageView heartImage) {
        if (dataService.isFavorite(id)) {
            heartImage.setImageResource(R.drawable.ic_baseline_favorite_24);
        } else {
            heartImage.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }
        heartImage.setOnClickListener(view -> {
            if (dataService.isFavorite(id)) {
                dataService.toggleFavorite(list.get(position), false);
                if (type == DataService.QueryType.FAVORITE) {
                    list.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, list.size());
                    if (onRemoveFromFavSection != null)
                        onRemoveFromFavSection.onRemove();
                } else {
                    heartImage.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                }
            } else {
                dataService.toggleFavorite(list.get(position), true);
                heartImage.setImageResource(R.drawable.ic_baseline_favorite_24);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setType(DataService.QueryType type) {
        this.type = type;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView, heartImage, premiumImage;
        private TextView title;
        private View card;
        private NativeAdView adView;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            if (viewType == 1) {
                heartImage = itemView.findViewById(R.id.heartImage);
                premiumImage = itemView.findViewById(R.id.premiumImage);
                imageView = itemView.findViewById(R.id.image);
                title = itemView.findViewById(R.id.title);
                card = itemView.findViewById(R.id.card);
            } else if (viewType == 2) {
                adView = itemView.findViewById(R.id.unifiedNativeAd);
            }
        }
    }
    private OnRemoveFromFavSection onRemoveFromFavSection;

    public void setOnRemoveFromFavSection(OnRemoveFromFavSection onRemoveFromFavSection) {
        this.onRemoveFromFavSection = onRemoveFromFavSection;
    }

    public interface OnRemoveFromFavSection {
        void onRemove();
    }

    public void mapUnifiedNativeAdToLayout(NativeAd adFromGoogle, NativeAdView myAdView) {
        MediaView mediaView = myAdView.findViewById(R.id.ad_media);
        myAdView.setMediaView(mediaView);

        myAdView.setHeadlineView(myAdView.findViewById(R.id.ad_headline));
        myAdView.setBodyView(myAdView.findViewById(R.id.ad_body));
        myAdView.setCallToActionView(myAdView.findViewById(R.id.ad_call_to_action));
        myAdView.setIconView(myAdView.findViewById(R.id.ad_icon));
        myAdView.setPriceView(myAdView.findViewById(R.id.ad_price));
        myAdView.setStarRatingView(myAdView.findViewById(R.id.ad_rating));
        myAdView.setStoreView(myAdView.findViewById(R.id.ad_store));
        myAdView.setAdvertiserView(myAdView.findViewById(R.id.ad_advertiser));

        ((TextView) Objects.requireNonNull(myAdView.getHeadlineView())).setText(adFromGoogle.getHeadline());

        if (myAdView.getBodyView() != null){
            if (adFromGoogle.getBody() == null) {
                myAdView.getBodyView().setVisibility(View.GONE);
            } else {
                ((TextView) myAdView.getBodyView()).setText(adFromGoogle.getBody());
            }
        }

        if (myAdView.getCallToActionView() != null) {
            if (adFromGoogle.getCallToAction() == null) {
                myAdView.getCallToActionView().setVisibility(View.GONE);
            } else {
                ((Button) myAdView.getCallToActionView()).setText(adFromGoogle.getCallToAction());
            }
        }

        if (myAdView.getIconView() != null) {
            if (adFromGoogle.getIcon() == null) {
                myAdView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) myAdView.getIconView()).setImageDrawable(adFromGoogle.getIcon().getDrawable());
            }
        }

        if (myAdView.getPriceView() != null) {
            if (adFromGoogle.getPrice() == null) {
                myAdView.getPriceView().setVisibility(View.GONE);
            } else {
                ((TextView) myAdView.getPriceView()).setText(adFromGoogle.getPrice());
            }
        }

        if (myAdView.getStarRatingView() != null) {
            if (adFromGoogle.getStarRating() == null) {
                myAdView.getStarRatingView().setVisibility(View.GONE);
            } else {
                ((RatingBar) myAdView.getStarRatingView()).setRating(adFromGoogle.getStarRating().floatValue());
            }
        }

        if (myAdView.getStoreView() != null) {
            if (adFromGoogle.getStore() == null) {
                myAdView.getStoreView().setVisibility(View.GONE);
            } else {
                ((TextView) myAdView.getStoreView()).setText(adFromGoogle.getStore());
            }
        }

        if (myAdView.getAdvertiserView() != null) {
            if (adFromGoogle.getAdvertiser() == null) {
                myAdView.getAdvertiserView().setVisibility(View.GONE);
            } else {
                ((TextView) myAdView.getAdvertiserView()).setText(adFromGoogle.getAdvertiser());
            }
        }

        myAdView.setNativeAd(adFromGoogle);
    }
}