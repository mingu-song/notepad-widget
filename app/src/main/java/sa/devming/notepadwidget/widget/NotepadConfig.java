package sa.devming.notepadwidget.widget;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sa.devming.notepadwidget.MyMovementMethod;
import sa.devming.notepadwidget.R;
import sa.devming.notepadwidget.db.Notepad;
import sa.devming.notepadwidget.db.NotepadDbHelper;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class NotepadConfig extends AppCompatActivity {
    private int mAppWidgetId;
    private NotepadDbHelper mDBHelper;

    private EditText mConfigHead;
    private EditText mConfigBody;
    private Button   mConfigOK;
    private ImageView mConfigColor;
    private ImageButton mConfigTextSize;

    private int mAppWidgetIdUpdate;

    private int mColor = 0;
    public static int[] TEXT_SIZE_LIST = {18, 20, 23, 26, 29, 32, 35, 40, 15};
    public static final String WIDGET_ID_PARAM = "WIDGET_ID";
    public static final int [] CONFIG_COLOR = {
            R.drawable.widget_corner_0_yellow, R.drawable.widget_corner_1_black,
            R.drawable.widget_corner_2_white, R.drawable.widget_corner_3_red,
            R.drawable.widget_corner_4_orange, R.drawable.widget_corner_5_lime,
            R.drawable.widget_corner_6_gray, R.drawable.widget_corner_7_bluegray,
            R.drawable.widget_corner_8_liteblue };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_config_layout);

        adMob();
        initializeViews();
    }

    private void initializeViews() {
        mConfigHead = (EditText)findViewById(R.id.configHead);
        mConfigBody = (EditText)findViewById(R.id.configBody);
        mConfigOK = (Button)findViewById(R.id.configOK);
        mConfigColor = (ImageView)findViewById(R.id.configColor);
        mConfigTextSize = (ImageButton) findViewById(R.id.configTextSize);

        mConfigOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configFinish();
            }
        });
        mConfigColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectColor();
            }
        });
        mConfigTextSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int textSize = (int)v.getTag();
                if (++textSize == TEXT_SIZE_LIST.length) {
                    textSize = 0;
                }
                mConfigTextSize.setTag(textSize);
                mConfigBody.setTextSize(TEXT_SIZE_LIST[textSize]);
            }
        });


        mDBHelper = new NotepadDbHelper(this);
        mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        mAppWidgetIdUpdate = AppWidgetManager.INVALID_APPWIDGET_ID;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mAppWidgetId = bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            mAppWidgetIdUpdate = bundle.getInt(WIDGET_ID_PARAM, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && mAppWidgetIdUpdate == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        } else if (mAppWidgetIdUpdate != AppWidgetManager.INVALID_APPWIDGET_ID) {
            //업데이트를 위해 조회
            if (mDBHelper.existNote(mAppWidgetIdUpdate)) {
                Notepad notepad = mDBHelper.getNotepad(mAppWidgetIdUpdate);
                updateNotepad(notepad);
            }
        } else {
            newNotepad();
        }

        // 20171211 에디터에 클릭 기능 삭제
        /*mConfigBody.setLinksClickable(true);
        mConfigBody.setAutoLinkMask(Linkify.ALL);
        mConfigBody.setMovementMethod(MyMovementMethod.getInstance());
        //If the edit text contains previous text with potential links
        Linkify.addLinks(mConfigBody, Linkify.ALL);
        mConfigBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                Linkify.addLinks(s, Linkify.ALL);
            }
        });*/

        mConfigBody.requestFocus();
    }

    private void newNotepad() {
        mConfigColor.setBackground(ContextCompat.getDrawable(this, CONFIG_COLOR[0]));
        mColor = 0;
        mConfigTextSize.setTag(0);
        mConfigBody.setTextSize(TEXT_SIZE_LIST[0]);
    }

    private void updateNotepad(Notepad note) {
        mConfigHead.setText(note.getHead());
        mConfigBody.setText(note.getBody());
        mConfigColor.setBackground(ContextCompat.getDrawable(this, CONFIG_COLOR[note.getColorId()]));
        mColor = note.getColorId();
        mConfigTextSize.setTag(note.getTextSize());
        mConfigBody.setTextSize(TEXT_SIZE_LIST[note.getTextSize()]);
    }

    private void selectColor() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("color");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        NotepadColor colorDialog = NotepadColor.newInstance(this);
        colorDialog.show(ft, "color");
    }

    public void setConfigColor(int position) {
        mColor = position;
        mConfigColor.setBackground(ContextCompat.getDrawable(this, CONFIG_COLOR[position]));
    }

    private void configFinish() {
        if (mConfigBody.getText().toString().length() == 0) {
            Toast.makeText(this, getString(R.string.check_body_empty), Toast.LENGTH_LONG).show();
            return;
        }

        if (mConfigHead.getText().toString().replace(" ","").length() == 0) {
            SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date date = new Date();
            String strDate = dateFormat.format(date);
            mConfigHead.setText(strDate);
        }

        //DB insert or update
        if (mAppWidgetIdUpdate == AppWidgetManager.INVALID_APPWIDGET_ID) {
            long newId = mDBHelper.addNotepad(new Notepad(mColor, mAppWidgetId, mConfigHead.getText().toString(), mConfigBody.getText().toString(), (int)mConfigTextSize.getTag()));
            if (newId == -1) {
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            long updateId = mDBHelper.updateNotepad(new Notepad(mColor, mAppWidgetIdUpdate, mConfigHead.getText().toString(), mConfigBody.getText().toString(), (int)mConfigTextSize.getTag()));
            if (updateId == -1) {
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_LONG).show();
                return;
            }
        }

        //위젯 업데이트 처리
        Intent intent = new Intent(NotepadProvider.WIDGET_CLICK);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        sendBroadcast(intent);

        //config 종료
        Intent intentFinish = new Intent();
        intentFinish.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, intentFinish);
        finish();
    }

    private void adMob(){
        AdView mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}
