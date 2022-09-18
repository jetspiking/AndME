package com.dustinhendriks.andme.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.dustinhendriks.andme.MainActivity;
import com.dustinhendriks.andme.R;
import com.dustinhendriks.andme.adapters.LauncherTilesAdapter;
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
import java.util.Objects;

/**
 * Handles creating and displaying the pinned applications and handling actions.
 */
public class LauncherTilesFragment extends Fragment implements OnTileActionListener {
    private RecyclerView mTileRecycler;
    public static ArrayList<Tile> mTiles = new ArrayList<>();
    private LongPressDialogFragment mRemoveFromHomescreenDialog;
    private LauncherTilesAdapter mTileAdapter;
    private GridLayoutManager mGridLayoutManager;
    private EqualSpaceDecorator equalSpaceDecorator;
    private AppSerializableData appSerializableData;
    private int fromPos;
    private int targetPos;
    private View mLauncherView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLauncherView = inflater.inflate(R.layout.fragment_launcher_tiles, container, false);
        initTiles(mLauncherView);
        loadData();
        return mLauncherView;
    }

    private void initTiles(View view) {
        mTileRecycler = view.findViewById(R.id.fragment_launcher_tiles_rv_appgrid);
        mTileAdapter = new LauncherTilesAdapter(getContext(), mTiles, this);
        mTileRecycler.setAdapter(mTileAdapter);
        equalSpaceDecorator = new EqualSpaceDecorator(AppMiscDefaults.TILE_BORDER_MARGIN);
        mTileRecycler.addItemDecoration(equalSpaceDecorator);
        mGridLayoutManager = new GridLayoutManager(getContext(), AppMiscDefaults.TILE_SPAN_COUNT);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mTiles.get(position).getWidth();
            }
        });
        mTileRecycler.setLayoutManager(mGridLayoutManager);

        ItemTouchHelper experimentalTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                if (mRemoveFromHomescreenDialog != null)
                    mRemoveFromHomescreenDialog.dismiss();

                fromPos = viewHolder.getAdapterPosition();
                targetPos = target.getAdapterPosition();
                if ((fromPos != -1) && (targetPos != -1) && (fromPos != targetPos)) {
                    Tile fromTile = mTiles.get(fromPos);
                    mTiles.remove(fromPos);
                    mTiles.add(targetPos, fromTile);
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

            int[] coords = new int[2];
            ((LauncherTilesAdapter.ViewHolder) holder).itemView.getLocationInWindow(coords);
            int position = coords[1];
            position=0;
            mRemoveFromHomescreenDialog.show(position);
        }
    }

    public void storeData() {
        // We need to copy the arraylist with tiles to prevent mutation issues when serializing in thread.
        ArrayList<Tile> tiles = new ArrayList<Tile>(mTiles);

        new Thread(() -> {

            if (appSerializableData == null) {
                appSerializableData = new AppSerializableData();
            }
            appSerializableData.update(tiles);
            cacheAppIcons(tiles);
            SerializationUtils.serializeData(Objects.requireNonNull(getActivity()), appSerializableData);

        }).start();

    }

    public void notifyDataSetUpdate() {
        mTileAdapter.notifyDataSetChanged();
    }

    public void loadData() {
        appSerializableData = SerializationUtils.loadSerializedData(Objects.requireNonNull(getActivity()));
        if (appSerializableData != null) {
            AppMiscDefaults.RestoreFromSerializedData(appSerializableData);
            mTiles.clear();
            mTiles.addAll(appSerializableData.getTiles());
            loadAppIcons();
            MainActivity.notifyDataSetTilesUpdate();
            MainActivity.notifyDataSetAppListUpdate();
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
}
