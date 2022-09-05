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
import com.dustinhendriks.andme.utils.AppDefaults;

import java.util.ArrayList;

public class LauncherTilesAdapter extends RecyclerView.Adapter<LauncherTilesAdapter.ViewHolder> {
    private static final String TAG = "LauncherTilesAdapter";

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
        view.getLayoutParams().height = (parent.getWidth() / AppDefaults.TILE_SPAN_COUNT) - (AppDefaults.TILE_BORDER_MARGIN * 2);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mName.setText(mTiles.get(holder.getAdapterPosition()).getName());
        if (mTiles.get(holder.getAdapterPosition()).getBackgroundColor()!=-1)
            holder.mBackground.setBackgroundColor(mTiles.get(holder.getAdapterPosition()).getBackgroundColor());
        else holder.mBackground.setBackgroundColor(AppDefaults.THEME_ACCENT_COLOR);
        if ((mTiles.get(holder.getAdapterPosition()) instanceof AppTile)) {
            if (((AppTile)mTiles.get(holder.getAdapterPosition())).getApp().getAppIcon()!=null)
                holder.mAppIcon.setImageDrawable(((AppTile)mTiles.get(holder.getAdapterPosition())).getApp().getAppIcon());
        } else
            holder.mAppIcon.setImageResource(mTiles.get(holder.getAdapterPosition()).getIconResource());
        holder.mAlertIcon.setImageResource(mTiles.get(holder.getAdapterPosition()).getAlertResource()); //Currently no alert resource bell!
        holder.mDragging.setVisibility(View.INVISIBLE);
        holder.mBackground.setAlpha(1.f-(AppDefaults.OPACITY/100.f));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickedListener.clickedItem(mContext, mTiles.get(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
        holder.mAppIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickedListener.clickedItem(mContext, mTiles.get(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });

        holder.mAppIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mOnItemClickedListener.longClickedItem(mContext, mTiles.get(holder.getAdapterPosition()), holder.getAdapterPosition());
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
        private ImageView mAlertIcon;
        private ImageView mBackground;
        private ImageView mDragging;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.item_launcher_tile_tv_name);
            mAppIcon = itemView.findViewById(R.id.item_launcher_tile_iv_icon);
            mAlertIcon = itemView.findViewById(R.id.item_launcher_tile_iv_alert);
            mBackground = itemView.findViewById(R.id.item_launcher_tile_iv_background);
            mDragging = itemView.findViewById(R.id.item_launcher_tile_iv_dragging);
        }
    }
}
