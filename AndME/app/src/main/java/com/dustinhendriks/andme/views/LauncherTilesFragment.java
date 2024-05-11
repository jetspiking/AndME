package com.dustinhendriks.andme.views;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.dustinhendriks.andme.MainActivity;
import com.dustinhendriks.andme.R;
import com.dustinhendriks.andme.adapters.LauncherTilesAdapter;
import com.dustinhendriks.andme.masks.MaskingBackgroundView;
import com.dustinhendriks.andme.interfaces.LaunchableTile;
import com.dustinhendriks.andme.interfaces.OnTileActionListener;
import com.dustinhendriks.andme.models.AppSerializableData;
import com.dustinhendriks.andme.models.AppTile;
import com.dustinhendriks.andme.models.Tile;
import com.dustinhendriks.andme.utils.AppMiscDefaults;
import com.dustinhendriks.andme.utils.EqualSpaceDecorator;
import com.dustinhendriks.andme.utils.SerializationUtils;
import com.dustinhendriks.andme.utils.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Handles creating and displaying the pinned applications and handling actions.
 */
public class LauncherTilesFragment extends Fragment implements OnTileActionListener {
    public static ArrayList<Tile> mTiles = new ArrayList<>();
    public LauncherTilesAdapter mTileAdapter;
    private RecyclerView mTileRecycler;
    private LongPressDialogFragment mRemoveFromHomescreenDialog;
    private GridLayoutManager mGridLayoutManager;
    private View mLauncherView;
    private EqualSpaceDecorator mEqualSpaceDecorator;
    private AppSerializableData mAppSerializableData;
    private int mFromPos;
    private int mTargetPos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mTiles.clear();
        mLauncherView = inflater.inflate(R.layout.fragment_launcher_tiles, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && AppMiscDefaults.SHOW_SYSTEM_WALLPAPER) {
            try {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity());
                ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.READ_EXTERNAL_STORAGE);
                Drawable wallpaperDrawable = wallpaperManager.getDrawable();
                if (wallpaperDrawable != null)
                    this.mLauncherView.setBackground(wallpaperDrawable);
                else AppMiscDefaults.SHOW_SYSTEM_WALLPAPER = false;
            } catch(Exception e) { AppMiscDefaults.SHOW_SYSTEM_WALLPAPER = false; }
        }

        mTileRecycler = mLauncherView.findViewById(R.id.fragment_launcher_tiles_rv_appgrid);
        loadData();
        initTiles();
        return mLauncherView;
    }

    private void initTiles() {
        int tileSpanCount = getTileSpanCount();

        mTileAdapter = new LauncherTilesAdapter(getContext(), mTiles, this, tileSpanCount, AppMiscDefaults.SHOW_SYSTEM_WALLPAPER);
        mTileRecycler.setAdapter(mTileAdapter);
        mEqualSpaceDecorator = new EqualSpaceDecorator(AppMiscDefaults.TILE_BORDER_MARGIN);
        mTileRecycler.addItemDecoration(mEqualSpaceDecorator);
        mGridLayoutManager = new GridLayoutManager(getContext(), tileSpanCount);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mTiles.get(position).getWidth();
            }
        });
        mTileRecycler.setLayoutManager(mGridLayoutManager);

        if (AppMiscDefaults.SHOW_SYSTEM_WALLPAPER) {
            ConstraintLayout parentLayout = (ConstraintLayout) mTileRecycler.getParent();

            MaskingBackgroundView maskingView = new MaskingBackgroundView(getActivity(), AppMiscDefaults.BACKGROUND_COLOR);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            );
            maskingView.setLayoutParams(layoutParams);
            maskingView.setBackgroundColor(Color.TRANSPARENT);

            parentLayout.addView(maskingView, 0);

            mTileRecycler.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    updateMaskingView(maskingView);
                }
            });
            mTileRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    updateMaskingView(maskingView);
                }
            });
        }

        ItemTouchHelper experimentalTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                if (mRemoveFromHomescreenDialog != null)
                    mRemoveFromHomescreenDialog.dismiss();

                mFromPos = viewHolder.getAdapterPosition();
                mTargetPos = target.getAdapterPosition();
                if ((mFromPos != -1) && (mTargetPos != -1) && (mFromPos != mTargetPos)) {
                    Tile fromTile = mTiles.get(mFromPos);
                    mTiles.remove(mFromPos);
                    mTiles.add(mTargetPos, fromTile);
                    mTileAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    MainActivity.serializeData();
                    return true;
                }
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                mTileAdapter.notifyDataSetChanged();
            }
        });

        experimentalTouchHelper.attachToRecyclerView(mTileRecycler);
    }

    @Override
    public void clickedItem(Context context, Object object, Object holder) {
        if (object instanceof LaunchableTile<?>) {
            if (object instanceof AppTile)
                if (getActivity() != null) {
                    AppTile appTile = (AppTile) object;
                    appTile.launch(getActivity());
                }
        }
    }

    @Override
    public void longClickedItem(Context context, Object object, Object holder) {
        if (object instanceof Tile) {
            mRemoveFromHomescreenDialog = new LongPressDialogFragment(context, getString(R.string.unpin_from_start));
            mRemoveFromHomescreenDialog.subscribeOption1(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTiles.remove(object);
                    mTileAdapter.notifyDataSetChanged();
                    MainActivity.serializeData();
                }
            });

            // int[] coords = new int[2];
            // ((LauncherTilesAdapter.ViewHolder) holder).itemView.getLocationInWindow(coords);
            // int position = coords[1];

            int position = 0;
            mRemoveFromHomescreenDialog.show(position);
        }
    }

    public void storeData() {
        // Copy the arraylist with tiles to prevent mutation issues when serializing in thread.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            CompletableFuture.runAsync(this::triggerSave);
        else triggerSave();
    }

    public void notifyDataSetUpdate() {
        if (mTileAdapter != null)
            mTileAdapter.notifyDataSetChanged();
    }

    public void loadData() {
        mAppSerializableData = SerializationUtils.loadSerializedData(Objects.requireNonNull(getActivity()));
        if (mAppSerializableData != null) {
            AppMiscDefaults.RestoreFromSerializedData(mAppSerializableData);
            mTiles.clear();
            mTiles.addAll(mAppSerializableData.getTiles());
            loadAppIcons();
            MainActivity.notifyDataSetTilesUpdate();
        }
    }

    private void loadAppIcons() {
        for (Tile tile : mTiles)
            if (tile instanceof AppTile) {
                AppTile appTile = (AppTile) tile;
                Bitmap bitmap = appTile.getApp().getCachedIcon();
                appTile.getApp().setAppIcon(Utilities.bitmapToDrawable(Objects.requireNonNull(getActivity()), bitmap));
            }
    }

    private void cacheAppIcons(ArrayList<Tile> tiles) {
        for (Tile tile : tiles)
            if (tile instanceof AppTile)
                ((AppTile) tile).getApp().cacheIcon(getActivity());
    }

    private int getTileSpanCount() {
        DisplayMetrics metrics = Objects.requireNonNull(getActivity()).getResources().getDisplayMetrics();

        int tileSpanCount;
        float ratio;

        if (metrics.heightPixels > metrics.widthPixels)
            ratio = ((float) metrics.heightPixels / (float) metrics.widthPixels);
        else ratio = ((float) metrics.widthPixels / (float) metrics.heightPixels);

        int orientation = MainActivity.MAIN_ACTIVITY.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            tileSpanCount = AppMiscDefaults.TILE_SPAN_COUNT;
        } else {
            tileSpanCount = (int) Math.ceil(AppMiscDefaults.TILE_SPAN_COUNT * ratio);
        }
        return tileSpanCount;
    }

    private void triggerSave() {
        ArrayList<Tile> tiles = new ArrayList<Tile>(mTiles);
        if (mAppSerializableData == null)
            mAppSerializableData = new AppSerializableData();
        mAppSerializableData.update(tiles);
        cacheAppIcons(tiles);
        SerializationUtils.serializeData(Objects.requireNonNull(getActivity()), mAppSerializableData);
    }

    private void updateMaskingView(MaskingBackgroundView maskingView) {
        List<Rect> rects = new ArrayList<>();
        for (int i = 0; i < mTileRecycler.getChildCount(); i++) {
            View tile = mTileRecycler.getChildAt(i);
            rects.add(new Rect(tile.getLeft(), tile.getTop(), tile.getRight(), tile.getBottom()));
        }
        maskingView.updateTileRects(rects);
    }
}
