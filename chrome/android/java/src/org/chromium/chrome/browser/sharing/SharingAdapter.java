// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chrome.browser.sharing;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.chromium.chrome.R;
import org.chromium.chrome.browser.sharing.SharingServiceProxy.DeviceInfo;
import org.chromium.ui.widget.ChromeImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Adapter to populate the Sharing Device Picker sheet.
 */
public class SharingAdapter extends BaseAdapter {
    private final ArrayList<DeviceInfo> mTargetDevices;

    public SharingAdapter(int capabilities) {
        mTargetDevices = SharingServiceProxy.getInstance().getDeviceCandidates(capabilities);
    }

    @Override
    public int getCount() {
        return mTargetDevices.size();
    }

    @Override
    public DeviceInfo getItem(int position) {
        return mTargetDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final Context context = parent.getContext();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.send_tab_to_self_device_picker_item, parent, false);

            DeviceInfo deviceInfo = getItem(position);
            ChromeImageView deviceIcon = convertView.findViewById(R.id.device_icon);
            deviceIcon.setImageDrawable(getDrawableForDeviceType(context, deviceInfo));
            deviceIcon.setVisibility(View.VISIBLE);

            TextView deviceName = convertView.findViewById(R.id.device_name);
            deviceName.setText(deviceInfo.clientName);

            TextView lastActive = convertView.findViewById(R.id.last_active);

            long numDaysDeviceActive =
                    TimeUnit.MILLISECONDS.toDays(Calendar.getInstance().getTimeInMillis()
                            - deviceInfo.lastUpdatedTimestampMilliseconds);
            lastActive.setText(getLastActiveMessage(context.getResources(), numDaysDeviceActive));
        }
        return convertView;
    }

    private static String getLastActiveMessage(Resources resources, long numDays) {
        if (numDays < 1) {
            return resources.getString(R.string.send_tab_to_self_device_last_active_today);
        } else if (numDays == 1) {
            return resources.getString(R.string.send_tab_to_self_device_last_active_one_day_ago);
        } else {
            return resources.getString(
                    R.string.send_tab_to_self_device_last_active_more_than_one_day, numDays);
        }
    }

    private static Drawable getDrawableForDeviceType(Context context, DeviceInfo targetDevice) {
        switch (targetDevice.deviceType) {
            case TYPE_CROS:
            case TYPE_LINUX:
            case TYPE_MAC:
            case TYPE_WIN: {
                return AppCompatResources.getDrawable(context, R.drawable.computer_black_24dp);
            }
            case TYPE_PHONE: {
                return AppCompatResources.getDrawable(context, R.drawable.smartphone_black_24dp);
            }
            default:
                return AppCompatResources.getDrawable(context, R.drawable.devices_black_24dp);
        }
    }
}
