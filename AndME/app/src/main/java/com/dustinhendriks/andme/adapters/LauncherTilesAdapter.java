package com.dustinhendriks.andme.adapters;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dustinhendriks.andme.R;
import com.dustinhendriks.andme.interfaces.OnTileActionListener;
import com.dustinhendriks.andme.models.AppTile;
import com.dustinhendriks.andme.models.Tile;
import com.dustinhendriks.andme.utils.AppMiscDefaults;
import com.dustinhendriks.andme.utils.IconPackUtils;

import java.util.ArrayList;

/**
 * The LauncherTilesAdapter is responsible for showing the pinned applications on the homescreen.
 */
public class LauncherTilesAdapter extends RecyclerView.Adapter<LauncherTilesAdapter.ViewHolder> {

    private final ArrayList<Tile> mTiles;
    private final Context mContext;
    private final OnTileActionListener mOnItemClickedListener;
    private final int mTileCount;
    private final boolean mIsTransparent;

    /**
     * Create the tile adapter.
     *
     * @param context              Application context.
     * @param tiles                Tiles to display.
     * @param onTileActionListener Listener for tile events.
     * @param spanCount            Amount of columns to display.
     * @param isTransparent        Render tiles as transparent.
     */
    public LauncherTilesAdapter(Context context, ArrayList<Tile> tiles, OnTileActionListener onTileActionListener, int spanCount, boolean isTransparent) {
        this.mTiles = tiles;
        this.mContext = context;
        this.mOnItemClickedListener = onTileActionListener;
        this.mTileCount = spanCount;
        this.mIsTransparent = isTransparent;
    }

    /**
     * Calculate and set tile height when creating.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_launcher_tile, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.getLayoutParams().height = (parent.getWidth() / mTileCount) - (AppMiscDefaults.TILE_BORDER_MARGIN * 2);
        return viewHolder;
    }

    /**
     * Set name, background and (custom) icon for the tile.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tile selectedTile = mTiles.get(position);
        holder.mName.setText(selectedTile.getName());
        holder.mName.setTextColor(AppMiscDefaults.TEXT_COLOR);

        if (this.mIsTransparent)
            holder.mBackground.setBackgroundColor(Color.TRANSPARENT);
        else
            holder.mBackground.setBackgroundColor(AppMiscDefaults.ACCENT_COLOR);

        if (((AppTile) selectedTile).getApp().getAppIcon() != null)
            holder.mAppIcon.setImageDrawable(((AppTile) selectedTile).getApp().getAppIcon());
        Drawable customIcon = IconPackUtils.loadIconFromPack(mContext, ((AppTile) selectedTile).getApp().getAppPackage().toString(), AppMiscDefaults.APPLIED_ICON_PACK_NAME);
        if (customIcon != null) holder.mAppIcon.setImageDrawable(customIcon);
        holder.mBackground.setAlpha(1.f - (AppMiscDefaults.OPACITY / 100.f));
        holder.itemView.setOnClickListener(view -> mOnItemClickedListener.clickedItem(mContext, selectedTile, holder));
        holder.mAppIcon.setOnClickListener(view -> mOnItemClickedListener.clickedItem(mContext, selectedTile, holder));

        holder.mAppIcon.setOnLongClickListener(view -> {
            mOnItemClickedListener.longClickedItem(mContext, selectedTile, holder);
            return true; // Consume long click event
        });

        holder.itemView.setOnLongClickListener(view -> {
            mOnItemClickedListener.longClickedItem(mContext, selectedTile, holder);
            return true; // Consume long click event
        });
    }

    /**
     * Get item count (tile count) from the adapter.
     *
     * @return Amount of items (tiles).
     */
    @Override
    public int getItemCount() {
        return mTiles.size();
    }

    /**
     * Representation for displaying a tile.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mName;
        private final ImageView mAppIcon;
        private final ImageView mBackground;

        /**
         * A tile has a name, icon and background.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.item_launcher_tile_tv_name);
            mAppIcon = itemView.findViewById(R.id.item_launcher_tile_iv_icon);
            mBackground = itemView.findViewById(R.id.item_launcher_tile_iv_background);
        }
    }
}
