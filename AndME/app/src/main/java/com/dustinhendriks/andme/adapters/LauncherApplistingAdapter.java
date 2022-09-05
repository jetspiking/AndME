package com.dustinhendriks.andme.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
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
import com.dustinhendriks.andme.utils.AppDefaults;
import com.dustinhendriks.andme.views.LauncherTilesFragment;
import com.dustinhendriks.andme.views.LongPressDialogFragment;

import java.util.ArrayList;

public class LauncherApplistingAdapter extends RecyclerView.Adapter<LauncherApplistingAdapter.ViewHolder> {
    private static final String TAG = "LauncherApplistingAdapt";

    private ArrayList<App> mApps = new ArrayList<>();
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
        ViewHolder viewHolder = new ViewHolder(view);
        view.getLayoutParams().height=125;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mName.setText(mApps.get(holder.getAdapterPosition()).getName());
        holder.mAppIcon.setImageDrawable(mApps.get(position).getAppIcon());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickedListener.clickedItem(mContext, mApps.get(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LongPressDialogFragment longPressDialogFragment = new LongPressDialogFragment(mContext, "Pin to start", "App settings");
                holder.itemView.setBackgroundColor(AppDefaults.THEME_ACCENT_COLOR);
                longPressDialogFragment.subscribeOption1(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: Adding tile.");
                        App app = mApps.get(holder.getAdapterPosition());
                        holder.itemView.setBackgroundColor(0);
                        LauncherTilesFragment.mTiles.add(new AppTile.Builder<AppTile>(
                                app.getName().toString())
                                .withBackgroundColor(-1)
                                .withWidth(1)
                                .withHeight(1)
                                .withApp(app)
                                .build());
                        MainActivity.serializeData();
                        MainActivity.notifyDataSetUpdate();
                    }
                });
                longPressDialogFragment.subscribeOption2(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.itemView.setBackgroundColor(0);
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", mApps.get(holder.getAdapterPosition()).getAppPackage().toString(), null);
                        intent.setData(uri);
                        mContext.startActivity(intent);
                    }
                });
                longPressDialogFragment.subscribeCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        holder.itemView.setBackgroundColor(0);
                    }
                });
                longPressDialogFragment.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mApps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout mConstraintLayout;
        private TextView mName;
        private ImageView mAppIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mConstraintLayout = itemView.findViewById(R.id.item_launcher_applisting_cl_constraintlayout);
            mName = itemView.findViewById(R.id.item_launcher_applisting_tv_appname);
            mAppIcon= itemView.findViewById(R.id.item_launcher_applisting_iv_applogo);
        }
    }
}
