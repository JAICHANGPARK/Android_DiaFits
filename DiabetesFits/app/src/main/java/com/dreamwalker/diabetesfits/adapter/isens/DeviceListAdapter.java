package com.dreamwalker.diabetesfits.adapter.isens;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.fragment.isens.ScannerFragment;
import com.dreamwalker.diabetesfits.model.isens.ExtendedDevice;

import java.util.ArrayList;

/**
 * Created by isens on 2015. 10. 13..
 */
public class DeviceListAdapter extends BaseAdapter {
    private static final int TYPE_TITLE = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_EMPTY = 2;

    private final ArrayList<ExtendedDevice> mListBondedValues = new ArrayList<ExtendedDevice>();
    private final ArrayList<ExtendedDevice> mListValues = new ArrayList<ExtendedDevice>();

    private final Context mContext;
    private final ExtendedDevice.AddressComparator comparator = new ExtendedDevice.AddressComparator();

    public DeviceListAdapter(Context context) {
        mContext = context;
    }

    public void addBondedDevice(ExtendedDevice device) {
        mListBondedValues.add(device);
        notifyDataSetChanged();
    }

    /**
     * Looks for the device with the same address as given one in the list of bonded devices. If the device has been found it updates its RSSI value.
     *
     * @param address
     *            the device address
     * @param rssi
     *            the RSSI of the scanned device
     */
    public void updateRssiOfBondedDevice(String address, int rssi) {
        comparator.address = address;
        final int indexInBonded = mListBondedValues.indexOf(comparator);
        if (indexInBonded >= 0) {
            ExtendedDevice previousDevice = mListBondedValues.get(indexInBonded);
            previousDevice.rssi = rssi;
            notifyDataSetChanged();
        }
    }

    /**
     * If such device exists on the bonded device list, this method does nothing. If not then the device is updated (rssi value) or added.
     *
     * @param device
     *            the device to be added or updated
     */
    public void addOrUpdateDevice(ExtendedDevice device) {
        final boolean indexInBonded = mListBondedValues.contains(device);
        if (indexInBonded) {
            return;
        }

        final int indexInNotBonded = mListValues.indexOf(device);
        if (indexInNotBonded >= 0) {
            ExtendedDevice previousDevice = mListValues.get(indexInNotBonded);
            previousDevice.rssi = device.rssi;
            notifyDataSetChanged();
            return;
        }
        mListValues.add(device);
        notifyDataSetChanged();
    }

    public void clearDevices() {
        mListValues.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        final int bondedCount = mListBondedValues.size() + 1; // 1 for the title
        final int availableCount = mListValues.isEmpty() ? 2 : mListValues.size() + 1; // 1 for title, 1 for empty text
        if (bondedCount == 1)
            return availableCount;
        return bondedCount + availableCount;
    }

    @Override
    public Object getItem(int position) {
        final int bondedCount = mListBondedValues.size() + 1; // 1 for the title
        if (mListBondedValues.isEmpty()) {
            if (position == 0)
                return R.string.scanner_subtitle__not_bonded;
            else
                return mListValues.get(position - 1);
        } else {
            if (position == 0)
                return R.string.scanner_subtitle_bonded;
            if (position < bondedCount)
                return mListBondedValues.get(position - 1);
            if (position == bondedCount)
                return R.string.scanner_subtitle__not_bonded;
            return mListValues.get(position - bondedCount - 1);
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == TYPE_ITEM;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_TITLE;

        if (!mListBondedValues.isEmpty() && position == mListBondedValues.size() + 1)
            return TYPE_TITLE;

        if (position == getCount() - 1 && mListValues.isEmpty())
            return TYPE_EMPTY;

        return TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View oldView, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        final int type = getItemViewType(position);

        View view = oldView;
        switch (type) {
            case TYPE_EMPTY:
                if (view == null) {
                    view = inflater.inflate(R.layout.device_list_empty, parent, false);
                }
                break;
            case TYPE_TITLE:
                if (view == null) {
                    view = inflater.inflate(R.layout.device_list_title, parent, false);
                }
                final TextView title = (TextView) view;
                title.setText((Integer) getItem(position));
                break;
            default:
                if (view == null) {
                    view = inflater.inflate(R.layout.device_list_row, parent, false);
                    final ViewHolder holder = new ViewHolder();
                    holder.name = (TextView) view.findViewById(R.id.name);
                    holder.address = (TextView) view.findViewById(R.id.address);
                    holder.rssi = (ImageView) view.findViewById(R.id.rssi);
                    view.setTag(holder);
                }

                final ExtendedDevice device = (ExtendedDevice) getItem(position);
                final ViewHolder holder = (ViewHolder) view.getTag();
                holder.name.setText(device.device.getName());
                holder.address.setText(device.device.getAddress());

                if (!device.isBonded || device.rssi != ScannerFragment.NO_RSSI) {
                    final int rssiPercent = (int) (100.0f * (127.0f + device.rssi) / (127.0f + 20.0f));
                    holder.rssi.setImageLevel(rssiPercent);
                    holder.rssi.setVisibility(View.VISIBLE);
                } else {
                    holder.rssi.setVisibility(View.GONE);
                }
                break;
        }

        return view;
    }

    private class ViewHolder {
        private TextView name;
        private TextView address;
        private ImageView rssi;
    }
}
