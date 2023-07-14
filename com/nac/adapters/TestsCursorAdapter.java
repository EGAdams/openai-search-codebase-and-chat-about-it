package com.nac.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.nac.R;
import com.nac.configs.MeasureConfig;
import com.nac.utils.Converter;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by andreikaralkou on 2/10/14.
 */
public class TestsCursorAdapter extends CursorAdapter {
    private LayoutInflater inflater;
    private MeasureConfig config;
    private List<Integer> checkBoxList = new ArrayList<Integer>();
    private Format dateFormat;
    private Format timeFormat;

    public TestsCursorAdapter(Context context, Cursor cursor, boolean autoRequery, MeasureConfig config) {
        super(context, cursor, autoRequery);
        this.config = config;
        dateFormat = android.text.format.DateFormat.getDateFormat(context.getApplicationContext());
        timeFormat = new SimpleDateFormat(
                context.getString(R.string.time_short_format), Locale.getDefault());
        inflater = LayoutInflater.from(context);
        for (int i = 0; i < cursor.getCount(); i++) {
            checkBoxList.add(-1);
        }
    }

    public void renewDateFormat(Format dateFormat) {
        if (! this.dateFormat.equals(dateFormat)) {
            this.dateFormat = dateFormat;
            notifyDataSetChanged();
        }
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        checkBoxList.clear();
        for (int i = 0; i < newCursor.getCount(); i++) {
            checkBoxList.add(-1);
        }
        return super.swapCursor(newCursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return inflater.inflate(R.layout.list_tests_item, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final int position = cursor.getPosition();
        final int id = cursor.getInt(0);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.check_action);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((CheckBox) view).isChecked();
                checkBoxList.set(position, checked ? id : -1);
            }
        });
        checkBox.setChecked(checkBoxList.get(position) != -1);
        TextView timeText = (TextView) view.findViewById(R.id.label_time);
        long dateInMills = cursor.getLong(1);
        TextView dateText = (TextView) view.findViewById(R.id.label_date);
        Date date = new Date(dateInMills);
        timeText.setText(timeFormat.format(date));
        dateText.setText(dateFormat.format(date));
        TextView test = (TextView) view.findViewById(R.id.label_tests);
        test.setText(String.valueOf(cursor.getInt(2)));
        TextView avg = (TextView) view.findViewById(R.id.label_avg);
        String forceUnits = config.getForceUnitsString();
        float avgCursor = cursor.getInt(3);
        float avg13cursor = cursor.getInt(4);
        float avg23cursor = cursor.getInt(5);
        float avg33cursor = cursor.getInt(6);
        TextView avg23 = (TextView) view.findViewById(R.id.label_23);
        TextView avg13 = (TextView) view.findViewById(R.id.label_13);
        TextView avg33 = (TextView) view.findViewById(R.id.label_33);
        if (config.getForceUnits() == MeasureConfig.ForceUnits.RCR) {
            avg.setText(String.format(Locale.US, "%d ", Math.round(Converter.gravityToRcr(avgCursor))) + forceUnits);
        } else {
            avg.setText(String.format(Locale.US, "%d", Math.round(avgCursor)) + forceUnits);
        }
        if (avg13cursor == 0) {
            avg13.setText("?");
        } else {
            if (config.getForceUnits() == MeasureConfig.ForceUnits.RCR) {
                avg13.setText(String.format(Locale.US, "%d ", Math.round(Converter.gravityToRcr(avg13cursor))) + forceUnits);
            } else {
                avg13.setText(String.format(Locale.US, "%d", Math.round(avg13cursor)) + forceUnits);
            }
        }

        if (avg23cursor == 0) {
            avg23.setText("?");
        } else {
            if (config.getForceUnits() == MeasureConfig.ForceUnits.RCR) {
                avg23.setText(String.format(Locale.US, "%d ", Math.round(Converter.gravityToRcr(avg23cursor))) + forceUnits);
            } else {
                avg23.setText(String.format(Locale.US, "%d", Math.round(avg23cursor)) + forceUnits);
            }
        }


        if (avg33cursor == 0) {
            avg33.setText("?");
        } else {
            if (config.getForceUnits() == MeasureConfig.ForceUnits.RCR) {
                avg33.setText(String.format(Locale.US, "%d ", Math.round(Converter.gravityToRcr(avg33cursor))) + forceUnits);
            } else {
                avg33.setText(String.format(Locale.US, "%d", Math.round(avg33cursor)) + forceUnits);
            }

        }
        TextView condition = (TextView) view.findViewById(R.id.label_condition);
        condition.setText(cursor.getString(7));
    }

    public List<Integer> getSelectedTestsIds() {
        List<Integer> checkedIds = new ArrayList<Integer>();
        for (Integer id : checkBoxList) {
            if (id != -1) {
                checkedIds.add(id);
            }
        }
        return checkedIds;
    }
}
