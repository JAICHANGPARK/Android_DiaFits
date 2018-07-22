package com.dreamwalker.diabetesfits.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.adapter.ShopAdapter;
import com.dreamwalker.diabetesfits.model.categories.Item;
import com.dreamwalker.diabetesfits.model.categories.Shop;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceChooseActivity extends AppCompatActivity implements DiscreteScrollView.OnItemChangedListener, View.OnClickListener {
    private static final String TAG = "DeviceChooseActivity";

    @BindView(R.id.item_name)
    TextView currentItemName;

    @BindView(R.id.item_price)
    TextView currentItemPrice;

    @BindView(R.id.item_btn_rate)
    ImageView rateItemButton;

    @BindView(R.id.item_picker)
    DiscreteScrollView itemPicker;

    private List<Item> data;
    private Shop shop;
    private InfiniteScrollAdapter infiniteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_choose);
        ButterKnife.bind(this);


        shop = Shop.get();
        data = shop.getData();
        itemPicker.setOrientation(DSVOrientation.HORIZONTAL);
        itemPicker.addOnItemChangedListener(this);
        infiniteAdapter = InfiniteScrollAdapter.wrap(new ShopAdapter(data));
        itemPicker.setAdapter(infiniteAdapter);
        itemPicker.setItemTransitionTimeMillis(320);
        itemPicker.setItemTransformer(new ScaleTransformer.Builder().setMinScale(0.8f).build());

        onItemChanged(data.get(0));

        findViewById(R.id.item_btn_rate).setOnClickListener(this);
        findViewById(R.id.item_btn_buy).setOnClickListener(this);
        findViewById(R.id.item_btn_comment).setOnClickListener(this);

        findViewById(R.id.home).setOnClickListener(this);
//        findViewById(R.id.btn_smooth_scroll).setOnClickListener(this);
//        findViewById(R.id.btn_transition_time).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DeviceChooseActivity.this);
        switch (v.getId()) {
            case R.id.item_btn_rate:
                int realPosition = infiniteAdapter.getRealPosition(itemPicker.getCurrentItem());
                Item current = data.get(realPosition);
                shop.setRated(current.getId(), !shop.isRated(current.getId()));
                changeRateButtonState(current);
                break;
            case R.id.home:
                finish();
                break;
            case R.id.item_btn_buy:
                int position = infiniteAdapter.getRealPosition(itemPicker.getCurrentItem());
                Item item = data.get(position);
                //Log.e(TAG, "onClick: " + item.getName() + ", " + item.getId());

                switch (item.getId()){
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        startActivity(new Intent(DeviceChooseActivity.this, BSMScanActivity.class));
                        finish();
                        break;
                }


                break;
            case R.id.item_btn_comment:
                int commentPosition = infiniteAdapter.getRealPosition(itemPicker.getCurrentItem());
                Item commentItem = data.get(commentPosition);
                //Log.e(TAG, "onClick: " + commentItem.getName() + ", " + commentItem.getId());
                switch (commentItem.getId()){
                    case 1:
                        builder.setTitle(commentItem.getName());
                        builder.setMessage(getString(R.string.device_choose_treadmill));
                        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            dialog.dismiss();
                        });
                        builder.show();
                        break;
                    case 2:
                        builder.setTitle(commentItem.getName());
                        builder.setMessage(getString(R.string.device_choose_ergometer));
                        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            dialog.dismiss();
                        });
                        builder.show();
                        break;
                    case 3:
                        builder.setTitle(commentItem.getName());
                        builder.setMessage(getString(R.string.device_choose_bsm));
                        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            dialog.dismiss();
                        });
                        builder.show();
                        break;
                }
                break;
//            case R.id.btn_transition_time:
//                DiscreteScrollViewOptions.configureTransitionTime(itemPicker);
//                break;
//            case R.id.btn_smooth_scroll:
//                DiscreteScrollViewOptions.smoothScrollToUserSelectedPosition(itemPicker, v);
//                break;
            default:
                showUnsupportedSnackBar();
                break;
        }
    }

    private void onItemChanged(Item item) {
        currentItemName.setText(item.getName());
        currentItemPrice.setText(item.getPrice());
        changeRateButtonState(item);
    }

    private void changeRateButtonState(Item item) {
        if (shop.isRated(item.getId())) {
            //rateItemButton.setImageResource(R.drawable.ic_star_black_24dp);
            rateItemButton.setColorFilter(ContextCompat.getColor(this, R.color.shopRatedStar));
        } else {
            rateItemButton.setImageResource(R.drawable.ic_star_border_black_24dp);
            rateItemButton.setColorFilter(ContextCompat.getColor(this, R.color.shopSecondary));
        }
    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
        int positionInDataSet = infiniteAdapter.getRealPosition(adapterPosition);
        onItemChanged(data.get(positionInDataSet));
    }

    private void showUnsupportedSnackBar() {
        Snackbar.make(itemPicker, R.string.msg_unsupported_op, Snackbar.LENGTH_SHORT).show();
    }
}
