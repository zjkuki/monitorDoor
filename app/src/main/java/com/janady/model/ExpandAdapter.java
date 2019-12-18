package com.janady.model;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.funsdkdemo.MyApplication;
import com.janady.view.popmenu.DropPopMenu;
import com.lkd.smartlocker.R;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.janady.RoundRect;
import com.janady.adapter.FunDeviceAdapter;
import com.janady.base.BaseRecyclerAdapter;
import com.janady.base.JBaseSegmentFragment;
import com.janady.database.model.Bluetooth;
import com.janady.device.BluetoothOperatorFragment;
import com.janady.lkd.BleLocker;
import com.janady.lkd.ClientManager;
import com.janady.setup.JBaseFragment;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.models.FunDevice;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class ExpandAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MainItemDescription> items;
    private Context context;
    private OnClickListener onClickListener;
    public DropPopMenu dropPopMenu;
    public void setOnItemClickListener(OnClickListener clickListener) {
        this.onClickListener = clickListener;
    }

    public ExpandAdapter(Context ctx, List<MainItemDescription> items) {
        context = ctx;
        this.items = items;
    }

    public void setData(List<MainItemDescription> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.jtest_list_item_layout, viewGroup, false);
        return new MainViewHolder(context, v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final MainItemDescription mainItemDescription = items.get(i);
        final MainViewHolder mainViewHolder = (MainViewHolder) viewHolder;
        List<ItemDescription> items = new ArrayList<>();
        List<Object> l = mainItemDescription.getList();

        if (l != null) {
            for (Object o : l){
                items.add((ItemDescription) o);
            }
        }
        mainViewHolder.showItems(items);
        if (items.size() > 4 && mainItemDescription.getDeviceType() != MainItemDescription.DeviceType.CAM) {
            //mainViewHolder.exBtn.setVisibility(View.VISIBLE);
            /*mainViewHolder.exBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainItemDescription.expanded = !mainItemDescription.expanded;
                    LinearLayout.LayoutParams linerarParams = (LinearLayout.LayoutParams) mainViewHolder.recyclerView.getLayoutParams();
                    final float scale = context.getResources().getDisplayMetrics().density;
                    linerarParams.height = mainItemDescription.expanded ? -1 : (int)(100 * scale);
                    mainViewHolder.recyclerView.setLayoutParams(linerarParams);
                }
            });*/

            mainViewHolder.expand.setVisibility(View.VISIBLE);
            mainViewHolder.expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainItemDescription.expanded = !mainItemDescription.expanded;
                    if(mainItemDescription.expanded){
                        rotationExpandIcon(0, 90, mainViewHolder.expand);
                    }else {
                        rotationExpandIcon(90, 0, mainViewHolder.expand);
                    }

                    LinearLayout.LayoutParams linerarParams = (LinearLayout.LayoutParams) mainViewHolder.recyclerView.getLayoutParams();
                    final float scale = context.getResources().getDisplayMetrics().density;
                    linerarParams.height = mainItemDescription.expanded ? -1 : (int)(100 * scale);
                    mainViewHolder.recyclerView.setLayoutParams(linerarParams);
                }
            });

            LinearLayout.LayoutParams linerarParams = (LinearLayout.LayoutParams) mainViewHolder.recyclerView.getLayoutParams();
            final float scale = context.getResources().getDisplayMetrics().density;
            linerarParams.height = mainItemDescription.expanded ? -1 : (int)(100 * scale);
            mainViewHolder.recyclerView.setLayoutParams(linerarParams);
        } else {
            //mainViewHolder.exBtn.setVisibility(View.GONE);
            mainViewHolder.expand.setVisibility(View.GONE);
        }

        mainViewHolder.imgView.setImageResource(mainItemDescription.getIconRes());

        mainViewHolder.nameTv.setText(mainItemDescription.getName());
        /*mainViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) onClickListener.onMainClick(itemView, mainItemDescription);
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTv;
        public RecyclerView recyclerView;
        public Button exBtn;
        public ImageView imgView;
        private GridLayoutManager layoutManager;
        private BaseItemAdapter itemAdapter;
        private List<ItemDescription> items;

        private ImageView expand;
        private ProgressBar pbSearching;

        public void showItems(List<ItemDescription> cItems) {
            items = cItems;
            itemAdapter.setData(cItems);
            recyclerView.setAdapter(itemAdapter);
        }
        public MainViewHolder(final Context context, final View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.name);
            imgView = itemView.findViewById(R.id.img);
            recyclerView = itemView.findViewById(R.id.listview);
            pbSearching = itemView.findViewById(R.id.pbSearching);
            exBtn = itemView.findViewById(R.id.expandBtn);

            expand = itemView.findViewById(R.id.expend);

            recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.set(40, 40, 40, 4);//设置itemView中内容相对边框左，上，右，下距离
                    /*RoundRect roundRect = new RoundRect(500,500,100);
                    Bitmap photo = roundRect.toRoundRect(view.getContext(),R.drawable.ic_locker);
                    Drawable bg = new BitmapDrawable(photo);
                    view.setBackground(bg);*/
                }
            });
            itemAdapter = new BaseItemAdapter(context, null);
            itemAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View itemView, int pos) {
                    if (onClickListener != null) onClickListener.onItemClick(itemView, items.get(pos) );
                }
            });

            /*if(dropPopMenu!=null){dropPopMenu.dismiss();}
            dropPopMenu = new DropPopMenu(context);
            dropPopMenu.setTriangleIndicatorViewColor(Color.WHITE);
            dropPopMenu.setBackgroundResource(R.drawable.bg_drop_pop_menu_white_shap);
            dropPopMenu.setItemTextColor(Color.BLACK);

            dropPopMenu.setOnItemClickListener(new DropPopMenu.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id, com.janady.view.popmenu.MenuItem menuItem, int currentDeviceItemsId) {
                    Toast.makeText(context, "点击了 " + menuItem.getItemId(), Toast.LENGTH_SHORT).show();
                    switch (menuItem.getItemId()) {
                        case Menu.FIRST + 0:
                            Toast.makeText(context, items.get(currentDeviceItemsId).getName(), Toast.LENGTH_SHORT).show();
                            break;
                        case Menu.FIRST + 1:

                            break;
                        default:
                            break;
                    }
                }

            });
            dropPopMenu.setMenuList(getMenuList());*/

            itemAdapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
                @Override
                public void onItemLongClick(View itemView, int pos) {
                    //final PopupMenu p = createItemPopMenu(itemView);
                    //p.show();

                    if (onClickListener != null) {
                        onClickListener.onItemLongClick(itemView, items.get(pos) );
                    }else{
                     //   itemView.showContextMenu();
                    }
                    //dropPopMenu.show(itemView, pos);

                }

            });
            layoutManager = new GridLayoutManager(context, 4);
            recyclerView.setLayoutManager(layoutManager);
        }
    }
    private List<com.janady.view.popmenu.MenuItem> getMenuList() {
        List<com.janady.view.popmenu.MenuItem> list = new ArrayList<>();
        list.add(new com.janady.view.popmenu.MenuItem(1, "修改名称"));
        list.add(new com.janady.view.popmenu.MenuItem(2, "删除设备"));
        return list;
    }
    //----------------原生弹窗
    private PopupMenu createItemPopMenu(View itemView){
        PopupMenu popupMenu = new PopupMenu(context, itemView);
        Menu menu = popupMenu.getMenu();
        menu.add(0,1,0,"修改名称");
        menu.add(0,2,0,"删除设备");

        // 监听事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case Menu.FIRST + 0:
                        Toast.makeText(context,"复制",
                                Toast.LENGTH_LONG).show();
                        break;
                    case Menu.FIRST + 1:
                        Toast.makeText(context,"粘贴",
                                Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        return popupMenu;
    }

    public interface OnClickListener {
        void onItemClick(View itemView, ItemDescription itemDescription);
        void onItemLongClick(View itemView,ItemDescription itemDescription);
        void onMainClick(View itemView,MainItemDescription mainItemDescription);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void rotationExpandIcon(float from, float to, final View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);//属性动画
            valueAnimator.setDuration(500);
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    view.setRotation((Float) valueAnimator.getAnimatedValue());
                }
            });
            valueAnimator.start();
        }
    }


}