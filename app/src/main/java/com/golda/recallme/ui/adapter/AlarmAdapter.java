package com.golda.recallme.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.golda.recallme.R;
import com.golda.recallme.alarm.AlarmManagerHelper;
import com.golda.recallme.alarm.db.AlarmDBUtils;
import com.golda.recallme.models.alarm.AlarmModel;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.List;

/**
 * Created by Evgeniy on 11.04.2019.
 */

public class AlarmAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<AlarmModel> mList;

    public interface AlarmAdapterInterface {
        void onItemClick(AlarmModel model);

        void onLongClick(int id);
    }

    private AlarmAdapterInterface mListener;

    public AlarmAdapter(AlarmAdapterInterface listener) {
        mListener = listener;
        mContext = (Context) listener;
    }

    public void setData(List<AlarmModel> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_list, parent, false);
        return new AlarmViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final AlarmViewHolder alarmViewHolder = (AlarmViewHolder) holder;
        alarmViewHolder.initData(holder.getAdapterPosition());

        alarmViewHolder.alarmCardView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(mList.get(position));
            }
        });

        alarmViewHolder.alarmCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mListener != null) {
                    mListener.onLongClick(mList.get(position).id);
                }
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }


    public class AlarmViewHolder extends RecyclerView.ViewHolder {

        CardView alarmCardView;
        TextView tvTime;
        TextView tvSun, tvMon, tvTue, tvWed, tvThu, tvFri, tvSat;
        SwitchCompat switchCompat;
        MaterialIconView ivWeather;

        AlarmViewHolder(View realContentView) {
            super(realContentView);
            alarmCardView = realContentView.findViewById(R.id.alarmlist_cView);
            tvTime = realContentView.findViewById(R.id.alarmlist_time);
            tvSun = realContentView.findViewById(R.id.alarmlist_sun);
            tvMon = realContentView.findViewById(R.id.alarmlist_mon);
            tvTue = realContentView.findViewById(R.id.alarmlist_tue);
            tvWed = realContentView.findViewById(R.id.alarmlist_wed);
            tvThu = realContentView.findViewById(R.id.alarmlist_thu);
            tvFri = realContentView.findViewById(R.id.alarmlist_fri);
            tvSat = realContentView.findViewById(R.id.alarmlist_sat);
            switchCompat = realContentView.findViewById(R.id.alarmlist_switch);
            ivWeather = realContentView.findViewById(R.id.alarmlist_weather_icon);
        }

        void initData(final int adapterPosition) {
            final AlarmModel alarm = mList.get(adapterPosition);
            switchCompat.setOnCheckedChangeListener(null);
            switchCompat.setChecked(alarm.enable);

            setViewVisible(tvSun, alarm.sunday);
            setViewVisible(tvMon, alarm.monday);
            setViewVisible(tvTue, alarm.tuesday);
            setViewVisible(tvWed, alarm.wednesday);
            setViewVisible(tvThu, alarm.thursday);
            setViewVisible(tvFri, alarm.friday);
            setViewVisible(tvSat, alarm.saturday);
            setViewVisible(ivWeather, alarm.weather);

            String hour = (alarm.hour >= 10 ? alarm.hour + "" : "0" + alarm.hour);
            String minute = alarm.minute >= 10 ? alarm.minute + "" : "0" + alarm.minute;
            tvTime.setText(hour + ":" + minute);

            if (alarm.enable) {
                enableTextColor();
            } else {
                disableTextColor();
            }

            switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
                alarm.setEnable(isChecked);
                notifyItemChanged(adapterPosition);

                if (isChecked) {
                    enableTextColor();
                    AlarmManagerHelper.startAlarmClock(mContext, alarm.id);
                } else {
                    disableTextColor();
                    AlarmManagerHelper.cancelAlarmClock(mContext, alarm.id);
                }
                AlarmDBUtils.updateLiveAlarmClock(alarm);
            });
        }

        private void enableTextColor() {
            tvTime.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            tvSun.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            tvMon.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            tvTue.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            tvWed.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            tvThu.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            tvFri.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            tvSat.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            ivWeather.setColor(ContextCompat.getColor(mContext, R.color.colorRed_500));
        }

        private void disableTextColor() {
            tvTime.setTextColor(ContextCompat.getColor(mContext, R.color.colorGrey_800));
            tvSun.setTextColor(ContextCompat.getColor(mContext, R.color.colorGrey_800));
            tvMon.setTextColor(ContextCompat.getColor(mContext, R.color.colorGrey_800));
            tvTue.setTextColor(ContextCompat.getColor(mContext, R.color.colorGrey_800));
            tvWed.setTextColor(ContextCompat.getColor(mContext, R.color.colorGrey_800));
            tvThu.setTextColor(ContextCompat.getColor(mContext, R.color.colorGrey_800));
            tvFri.setTextColor(ContextCompat.getColor(mContext, R.color.colorGrey_800));
            tvSat.setTextColor(ContextCompat.getColor(mContext, R.color.colorGrey_800));
            ivWeather.setColor(ContextCompat.getColor(mContext, R.color.colorGrey_800));
        }

        private void setViewVisible(View view, boolean isVisible) {
            view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }
}