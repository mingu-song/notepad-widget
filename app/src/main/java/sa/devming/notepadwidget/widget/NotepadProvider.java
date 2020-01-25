package sa.devming.notepadwidget.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import sa.devming.notepadwidget.R;
import sa.devming.notepadwidget.db.Notepad;
import sa.devming.notepadwidget.db.NotepadDbHelper;

public class NotepadProvider extends AppWidgetProvider {
    public static NotepadDbHelper mDBHelper;
    public final static int [] WIDGET_BACKGROUND
            = { R.drawable.widget_corner_0_yellow, R.drawable.widget_corner_1_black,
            R.drawable.widget_corner_2_white, R.drawable.widget_corner_3_red,
            R.drawable.widget_corner_4_orange, R.drawable.widget_corner_5_lime,
            R.drawable.widget_corner_6_gray, R.drawable.widget_corner_7_bluegray,
            R.drawable.widget_corner_8_liteblue };
    public final static int [] WIDGET_TEXT_COLOR
            = { Color.BLACK, Color.WHITE, Color.BLACK, Color.WHITE,
            Color.BLACK, Color.BLACK, Color.BLACK, Color.WHITE, Color.WHITE };

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        mDBHelper = new NotepadDbHelper(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int i : appWidgetIds) {
            updateNotepad(context, appWidgetManager, i);
        }
    }

    private void updateNotepad(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        //데이터 조회
        if (mDBHelper == null) {
            mDBHelper = new NotepadDbHelper(context);
        }
        if (mDBHelper.existNote(appWidgetId)) {
            Notepad notepad = mDBHelper.getNotepad(appWidgetId);

            // 위젯셋업
            RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            updateViews.setTextViewText(R.id.widgetID, String.valueOf(notepad.getWidgetId()));
            updateViews.setTextViewText(R.id.widgetBody, notepad.getBody());
            updateViews.setTextColor(R.id.widgetBody, WIDGET_TEXT_COLOR[notepad.getColorId()]);
            updateViews.setInt(R.id.widgetBody, "setBackgroundResource", WIDGET_BACKGROUND[notepad.getColorId()]);
            updateViews.setFloat(R.id.widgetBody, "setTextSize", NotepadConfig.TEXT_SIZE_LIST[notepad.getTextSize()]);

            //클릭 이벤트 생성 : config 이동 with ID
            Intent intent = new Intent(context, NotepadConfig.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.widgetBody, pendingIntent);

            //위젯 새로고침
            appWidgetManager.updateAppWidget(appWidgetId, updateViews);
        } else {
            return;
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int i : appWidgetIds) {
            try {
                if (mDBHelper == null) {
                    mDBHelper = new NotepadDbHelper(context);
                }
                mDBHelper.deleteNotepad(i);
            } catch (Exception e) {
                Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
}
