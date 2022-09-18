package com.dustinhendriks.andme.adapters;

import android.content.Context;
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

import java.util.ArrayList;

/**
 * The LauncherApplistingAdapter is responsible for showing the pinned applications on the homescreen.
 */
public class LauncherTilesAdapter extends RecyclerView.Adapter<LauncherTilesAdapter.ViewHolder> {

    private ArrayList<Tile> mTiles;
    private Context mContext;
    private OnTileActionListener mOnItemClickedListener;

    public LauncherTilesAdapter(Context context, ArrayList<Tile> tiles, OnTileActionListener onTileActionListener) {
        this.mTiles = tiles;
        this.mContext = context;
        this.mOnItemClickedListener = onTileActionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_launcher_tile, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.getLayoutParams().height = (parent.getWidth() / AppMiscDefaults.TILE_SPAN_COUNT) - (AppMiscDefaults.TILE_BORDER_MARGIN * 2);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tile selectedTile = mTiles.get(position);
        holder.mName.setText(selectedTile.getName());
        holder.mName.setTextColor(AppMiscDefaults.TEXT_COLOR);
        holder.mBackground.setBackgroundColor(AppMiscDefaults.ACCENT_COLOR);
        if ((selectedTile instanceof AppTile)) {
            if (((AppTile)selectedTile).getApp().getAppIcon()!=null)
                holder.mAppIcon.setImageDrawable(((AppTile)selectedTile).getApp().getAppIcon());
        } else
            holder.mAppIcon.setImageResource(selectedTile.getIconResource());
        holder.mBackground.setAlpha(1.f-(AppMiscDefaults.OPACITY/100.f));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickedListener.clickedItem(mContext, selectedTile, holder);
            }
        });
        holder.mAppIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickedListener.clickedItem(mContext, selectedTile, holder);
            }
        });

        holder.mAppIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mOnItemClickedListener.longClickedItem(mContext, selectedTile, holder);
                return true; // Consume long click event
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mOnItemClickedListener.longClickedItem(mContext, selectedTile, holder);
                return true; // Consume long click event
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private ImageView mAppIcon;
        private ImageView mBackground;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.item_launcher_tile_tv_name);
            mAppIcon = itemView.findViewById(R.id.item_launcher_tile_iv_icon);
            mBackground = itemView.findViewById(R.id.item_launcher_tile_iv_background);
        }
    }
}
