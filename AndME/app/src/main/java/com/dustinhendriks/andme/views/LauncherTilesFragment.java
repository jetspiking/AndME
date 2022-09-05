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
import com.dustinhendriks.andme.utils.AppDefaults;
import com.dustinhendriks.andme.utils.EqualSpaceDecorator;
import com.dustinhendriks.andme.utils.SerializationUtils;
import com.dustinhendriks.andme.utils.Utilities;
import java.util.ArrayList;
import java.util.Objects;

public class LauncherTilesFragment extends Fragment implements OnTileActionListener {
    private static final String TAG = "LauncherTilesFragment";
    private RecyclerView mTileRecycler;
    public static ArrayList<Tile> mTiles = new ArrayList<>();
    private LauncherTilesAdapter mTileAdapter;
    private GridLayoutManager mGridLayoutManager;
    private EqualSpaceDecorator equalSpaceDecorator;
    private AppSerializableData appSerializableData;
    private int fromPos;
    private int targetPos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_launcher_tiles, container, false);
        initTiles(view);
        initSerializer();
        loadStoredData();
        return view;
    }

    private void initSerializer() {
        appSerializableData = new AppSerializableData();
    }

    private void initTiles(View view) {
        mTileRecycler = view.findViewById(R.id.fragment_launcher_tiles_rv_appgrid);
        mTileAdapter = new LauncherTilesAdapter(getContext(), mTiles, this);
        mTileRecycler.setAdapter(mTileAdapter);
        equalSpaceDecorator = new EqualSpaceDecorator(AppDefaults.TILE_BORDER_MARGIN);
        mTileRecycler.addItemDecoration(equalSpaceDecorator);
        mGridLayoutManager = new GridLayoutManager(getContext(), AppDefaults.TILE_SPAN_COUNT);
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
    public void clickedItem(Context context, Object object, int position) {
        if (object instanceof LaunchableTile<?>) {
            if (object instanceof AppTile)
                if (getActivity() != null) {
                    AppTile appTile = (AppTile) object;
                    appTile.launch(getActivity());
                }
        }
    }

    @Override
    public void longClickedItem(Context context, Object object, int position) {
        if (object instanceof Tile) {
            Log.d(TAG, "longClickedItem: POS " + position);
            mTiles.remove(object);
            mTileAdapter.notifyDataSetChanged();
            storeData();
        }
    }

    @Override
    public void clickedDragger(Context context, Object object, int position) {
        if (object instanceof Tile) {
            mTileAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void clickedUnpin(Context context, Object object, int position) {

    }

    @Override
    public void clickedResize(Context context, Object object, int position) {

    }

    public void storeData() {
        if (appSerializableData == null) {
            initSerializer();
        }
        appSerializableData.update(mTiles);
        cacheAppIcons();
        SerializationUtils.serializeData(Objects.requireNonNull(getActivity()), appSerializableData);
    }

    public void notifyDataSetUpdate() {
        mTileAdapter.notifyDataSetChanged();
    }

    private void loadStoredData() {
        appSerializableData = SerializationUtils.loadSerializedData(getActivity());
        Log.d(TAG, "loadStoredData: LOCAl VALUES --> \n" + mTiles.toString());
        if (appSerializableData != null) {
            Log.d(TAG, "loadStoredData: FROM STORED VALUES --> \n" + appSerializableData.getTiles().toString());
            mTiles.clear();
            mTiles.addAll(appSerializableData.getTiles());
            loadAppIcons();
            mTileAdapter.notifyDataSetChanged();
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

    private void cacheAppIcons() {
        for (Tile tile : mTiles)
            if (tile instanceof AppTile)
                ((AppTile) tile).getApp().cacheIcon(getActivity());
    }
}
