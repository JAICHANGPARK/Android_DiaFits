package com.dreamwalker.diabetesfits.widget;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.supercalendar.component.State;
import com.dreamwalker.supercalendar.interf.IDayRenderer;
import com.dreamwalker.supercalendar.model.CalendarDate;
import com.dreamwalker.supercalendar.view.DayView;


/**
 * Created by ldf on 17/6/27.
 */

public class ThemeDayView extends DayView {
    private TextView dateTv;
    private ImageView marker;
    private View selectedBackground;
    private View todayBackground;
    private final CalendarDate today = new CalendarDate();

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public ThemeDayView(Context context, int layoutResource) {
        super(context, layoutResource);
        dateTv = (TextView) findViewById(R.id.date);
        marker = (ImageView) findViewById(R.id.maker);
        selectedBackground = findViewById(R.id.selected_background);
        selectedBackground = findViewById(R.id.selected_background);
        todayBackground = findViewById(R.id.today_background);
    }

    @Override
    public void refreshContent() {
        CalendarDate date = day.getDate();
        State state = day.getState();
        if (date != null) {
            if (date.equals(today)) {
                dateTv.setText(getContext().getString(R.string.diary_now));
                todayBackground.setVisibility(VISIBLE);
            } else {
                dateTv.setText(date.day + "");
                todayBackground.setVisibility(GONE);
            }
        }
        if (state == State.SELECT) {
            selectedBackground.setVisibility(VISIBLE);
        } else {
            selectedBackground.setVisibility(GONE);
        }
        super.refreshContent();
    }

    @Override
    public IDayRenderer copy() {
        return new ThemeDayView(context, layoutResource);
    }
}
