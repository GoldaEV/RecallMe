package com.golda.recallme.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.golda.recallme.R;
import com.golda.recallme.alarm.AlarmClockBuilder;
import com.golda.recallme.alarm.AlarmClockLab;
import com.golda.recallme.alarm.db.AlarmDBUtils;
import com.golda.recallme.models.alarm.AlarmModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Evgeniy on 27.03.2019.
 */
public class RingActivity extends AppCompatActivity {

    public static final String EXTRA_REPEAT = "EXTRA_REPEAT";

    @BindView(R.id.ring_list)
    RecyclerView rv_ringList;

    private List<String> ringList;
    private AlarmModel alarmClockLab;

    public static Intent newIntent(Context context, int id) {
        Intent intent = new Intent(context, RingActivity.class);
        intent.putExtra(EXTRA_REPEAT, id);
        return intent;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);
        ButterKnife.bind(this);

        int id = getIntent().getIntExtra(EXTRA_REPEAT, 0);
        alarmClockLab = AlarmDBUtils.getAlarmModel(id);

        ringList = showRingList(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.scrollToPositionWithOffset(alarmClockLab.ringPosition, 100);
        rv_ringList.setLayoutManager(linearLayoutManager);
        rv_ringList.setAdapter(new RingAdapter());
    }


    private List<String> showRingList(Context context) {
        List<String> ringNameList = new ArrayList<>();
        RingtoneManager ringtoneManager = new RingtoneManager(context);
        ringtoneManager.setType(RingtoneManager.TYPE_ALARM);
        Cursor cursor = ringtoneManager.getCursor();
        while (cursor.moveToNext()) {
            String ringName = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            ringNameList.add(ringName);
        }
        return ringNameList;
    }

    private class RingAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(RingActivity.this).inflate(R.layout.ring_list_item, parent, false);
            return new RingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((RingViewHolder) holder).initView(position);
        }

        @Override
        public int getItemCount() {
            return ringList.size();
        }
    }

    private class RingViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout ringView;
        private TextView ringText;

        public RingViewHolder(View itemView) {
            super(itemView);
            ringView = itemView.findViewById(R.id.id_ring_item);
            ringText = itemView.findViewById(R.id.ring_list_text);
        }

        public void initView(final int position) {
            String ring = ringList.get(position);
            ringText.setText(ring);
            ringText.setTextColor(ring.equals(alarmClockLab.ring) ? ContextCompat.getColor
                    (RingActivity.this, R.color.colorRed_500) : ContextCompat.getColor(RingActivity.this, R.color.colorWhite));

            ringView.setOnClickListener(view -> {
                alarmClockLab.setRingPosition(position);
                alarmClockLab.setRing(ringList.get(position));
                finish();
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlarmDBUtils.updateLiveAlarmClock(alarmClockLab);
    }
}
