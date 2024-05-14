package com.dustinhendriks.andme.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.dustinhendriks.andme.MainActivity;
import com.dustinhendriks.andme.R;
import com.dustinhendriks.andme.interfaces.OnItemClickedListener;
import com.dustinhendriks.andme.models.App;
import com.dustinhendriks.andme.models.AppTile;
import com.dustinhendriks.andme.utils.AppMiscDefaults;
import com.dustinhendriks.andme.views.LauncherTilesFragment;
import com.dustinhendriks.andme.views.LongPressDialogFragment;

import java.util.ArrayList;

/**
 * The LauncherApplistingAdapter is responsible for showing the list of applications.
 */
public class LauncherApplistingAdapter extends RecyclerView.Adapter<LauncherApplistingAdapter.ViewHolder> {
    private ArrayList<App> mApps;
    private Context mContext;
    private OnItemClickedListener mOnItemClickedListener;

    public LauncherApplistingAdapter(Context context, ArrayList<App> apps, OnItemClickedListener onItemClickedListener) {
        this.mApps = apps;
        this.mContext = context;
        this.mOnItemClickedListener = onItemClickedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_launcher_applisting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mName.setText(mApps.get(holder.getAdapterPosition()).getName());
        holder.mName.setTextColor(AppMiscDefaults.TEXT_COLOR);
        holder.mAppIcon.setImageDrawable(mApps.get(position).getAppIcon());

        if (!AppMiscDefaults.SHOW_ICONS_IN_APPS_LIST)
            holder.mAppIcon.setVisibility(View.GONE);
        else holder.mAppIcon.setBackgroundColor(AppMiscDefaults.ACCENT_COLOR);

        holder.itemView.setOnClickListener(view -> mOnItemClickedListener.clickedItem(mContext, mApps.get(holder.getAdapterPosition()), holder));

        holder.itemView.setOnLongClickListener(v -> {
            LongPressDialogFragment longPressDialogFragment = new LongPressDialogFragment(mContext, mContext.getString(R.string.pin_to_start));
            holder.itemView.setBackgroundColor(AppMiscDefaults.ACCENT_COLOR);
            longPressDialogFragment.subscribeOption1(v1 -> {
                App app = mApps.get(holder.getAdapterPosition());
                holder.itemView.setBackgroundColor(0);
                LauncherTilesFragment.mTiles.add(new AppTile(app.getName().toString(), 1, 1, app));
                MainActivity.serializeData();
                MainActivity.notifyDataSetTilesUpdate();
            });
            longPressDialogFragment.subscribeCancelListener(dialog -> holder.itemView.setBackgroundColor(0));
            // Show dialog in the top of the screen.
            longPressDialogFragment.show(0);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mApps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private ImageView mAppIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.item_launcher_applisting_tv_appname);
            mAppIcon = itemView.findViewById(R.id.item_launcher_applisting_iv_applogo);
        }
    }
}
