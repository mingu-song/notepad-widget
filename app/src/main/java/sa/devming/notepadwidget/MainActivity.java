package sa.devming.notepadwidget;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import sa.devming.notepadwidget.db.Notepad;
import sa.devming.notepadwidget.db.NotepadDbHelper;
import sa.devming.notepadwidget.widget.NotepadConfig;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {
    private TextView mEmpty;
    private ListView mWidgetList;
    private NotepadDbHelper mDBHelper;
    private MainListAdapter mAdapter;
    private ArrayList<Notepad> mArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmpty = findViewById(R.id.emptyList);
        mWidgetList = findViewById(R.id.widgetList);
        mWidgetList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notepad notepad = (Notepad)parent.getItemAtPosition(position);

                Intent intent = new Intent(getApplicationContext(), NotepadConfig.class);
                Bundle bundle = new Bundle();
                bundle.putInt(NotepadConfig.WIDGET_ID_PARAM, notepad.getWidgetId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mDBHelper = new NotepadDbHelper(this);
        mAdapter = new MainListAdapter(this);

        adMob();
        loadList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadList();
    }

    private void loadList() {
        if (mDBHelper.getNotepadCnt() > 0) {
            mEmpty.setVisibility(View.GONE);
            if (mArrayList != null) {
                mArrayList.clear();
            }
            mArrayList = mDBHelper.getAllNotepad();
            mAdapter.setNotepadList(mArrayList);
            mWidgetList.setAdapter(mAdapter);
            mWidgetList.smoothScrollByOffset(0);
        } else {
            if (mArrayList != null) {
                mArrayList.clear();
                mAdapter.notifyDataSetChanged();
            }
            mEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void adMob(){
        AdView mAdView = findViewById(R.id.adView);
        Bundle extras = new Bundle();
        extras.putString("max_ad_content_rating", "G");
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .build();
        mAdView.loadAd(adRequest);
    }
}
