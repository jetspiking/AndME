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
    private LongPressDialogFragment mRemoveFromHomeScreenDialog;
    private AppSerializableData mAppSerializableData;
    private int mFromPos;
    private int mTargetPos;

    /**
     * Overridden onCreateView method called when creating the view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mTiles.clear();
        View mLauncherView = inflater.inflate(R.layout.fragment_launcher_tiles, container, false);

        if (AppMiscDefaults.SHOW_SYSTEM_WALLPAPER) {
            try {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity());
                ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.READ_EXTERNAL_STORAGE);
                Drawable wallpaperDrawable = wallpaperManager.getDrawable();
                if (wallpaperDrawable != null)
                    mLauncherView.setBackground(wallpaperDrawable);
                else AppMiscDefaults.SHOW_SYSTEM_WALLPAPER = false;
            } catch(Exception e) { AppMiscDefaults.SHOW_SYSTEM_WALLPAPER = false; }
        }

        mTileRecycler = mLauncherView.findViewById(R.id.fragment_launcher_tiles_rv_appgrid);
        loadData();
        initTiles();
        return mLauncherView;
    }

    /**
     * Initialize the tiles to be displayed and relevant events to be handled.
     */
    private void initTiles() {
        int tileSpanCount = getTileSpanCount();

        mTileAdapter = new LauncherTilesAdapter(getContext(), mTiles, this, tileSpanCount, AppMiscDefaults.SHOW_SYSTEM_WALLPAPER);
        mTileRecycler.setAdapter(mTileAdapter);
        EqualSpaceDecorator mEqualSpaceDecorator = new EqualSpaceDecorator(AppMiscDefaults.TILE_BORDER_MARGIN);
        mTileRecycler.addItemDecoration(mEqualSpaceDecorator);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getContext(), tileSpanCount);
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

            mTileRecycler.getViewTreeObserver().addOnGlobalLayoutListener(() -> updateMaskingView(maskingView));
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
                if (mRemoveFromHomeScreenDialog != null)
                    mRemoveFromHomeScreenDialog.dismiss();

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

    /**
     * When a tile is clicked the relevant application should be launched.
     */
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

    /**
     * When a tile is long clicked a prompt should appear to remove it.
     */
    @Override
    public void longClickedItem(Context context, Object object, Object holder) {
        if (object instanceof Tile) {
            mRemoveFromHomeScreenDialog = new LongPressDialogFragment(context, getString(R.string.unpin_from_start));
            mRemoveFromHomeScreenDialog.subscribeOption1(v -> {
                int index = mTiles.indexOf(object);
                mTiles.remove(object);
                mTileAdapter.notifyItemRemoved(index);
                MainActivity.serializeData();
            });

            mRemoveFromHomeScreenDialog.show(0);
        }
    }

    /**
     * Store the application data.
     */
    public void storeData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            CompletableFuture.runAsync(this::triggerSave);
        else triggerSave();
    }

    /**
     * Force update the tile adapter.
     */
    public void notifyDataSetUpdate() {
        if (mTileAdapter != null)
            mTileAdapter.notifyDataSetChanged();
    }

    /**
     * Load the application data and reset all tiles.
     */
    public void loadData() {
        mAppSerializableData = SerializationUtils.DeserializedData(Objects.requireNonNull(getActivity()));
        if (mAppSerializableData != null) {
            AppMiscDefaults.RestoreFromSerializedData(mAppSerializableData);
            mTiles.clear();
            mTiles.addAll(mAppSerializableData.getTiles());
            loadAppIcons();
            MainActivity.notifyDataSetTilesUpdate();
        }
    }

    /**
     * Load the application icons.
     */
    private void loadAppIcons() {
        for (Tile tile : mTiles)
            if (tile instanceof AppTile) {
                AppTile appTile = (AppTile) tile;
                Bitmap bitmap = appTile.getApp().getCachedIcon();
                appTile.getApp().setAppIcon(Utilities.bitmapToDrawable(Objects.requireNonNull(getActivity()), bitmap));
            }
    }

    /**
     * Cache the application icons.
     * @param tiles Tiles containing the icons to cache.
     */
    private void cacheAppIcons(ArrayList<Tile> tiles) {
        for (Tile tile : tiles)
            if (tile instanceof AppTile)
                ((AppTile) tile).getApp().cacheIcon(getActivity());
    }

    /**
     * Get the span count of tiles dependant on orientation and aspect ratio.
     * @return Span count dependant on orientation and aspect ratio.
     */
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

    /**
     * Save all tiles.
     */
    private void triggerSave() {
        // Copy the arraylist with tiles to prevent mutation issues when collection is being modified concurrently.
        ArrayList<Tile> tiles = new ArrayList<Tile>(mTiles);
        if (mAppSerializableData == null)
            mAppSerializableData = new AppSerializableData();
        mAppSerializableData.update(tiles);
        cacheAppIcons(tiles);
        SerializationUtils.serializeData(Objects.requireNonNull(getActivity()), mAppSerializableData);
    }

    /**
     * Update the masking background view (wallpaper).
     * @param maskingView maskingView to update.
     */
    private void updateMaskingView(MaskingBackgroundView maskingView) {
        List<Rect> maskingItems = new ArrayList<>();
        for (int i = 0; i < mTileRecycler.getChildCount(); i++) {
            View tile = mTileRecycler.getChildAt(i);
            maskingItems.add(new Rect(tile.getLeft(), tile.getTop(), tile.getRight(), tile.getBottom()));
        }
        maskingView.updateTileRects(maskingItems);
    }
}
