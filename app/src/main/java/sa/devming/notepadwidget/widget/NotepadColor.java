package sa.devming.notepadwidget.widget;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import sa.devming.notepadwidget.R;

public class NotepadColor extends DialogFragment {
    private static Context mContext;

    public static NotepadColor newInstance(Context context) {
        mContext = context;
        NotepadColor f = new NotepadColor();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog() == null) {
            return;
        }
        int width = getResources().getDimensionPixelSize(R.dimen.dialog_x);
        int height = getResources().getDimensionPixelSize(R.dimen.dialog_y);

        getDialog().getWindow().setLayout(width, height);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.widget_config_color_layout, container, false);
        ListView colorLV = v.findViewById(R.id.colorLV);
        String[] sample = new String[] { "Sample", "Sample", "Sample", "Sample", "Sample", "Sample", "Sample", "Sample", "Sample"};
        ArrayList<String> arrayList = new ArrayList();
        arrayList.addAll(Arrays.asList(sample));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(mContext, R.layout.widget_config_color_list_item, arrayList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = view.findViewById(R.id.colorItem);
                tv.setBackground(ContextCompat.getDrawable(mContext, NotepadProvider.WIDGET_BACKGROUND[position]));
                tv.setTextColor(NotepadProvider.WIDGET_TEXT_COLOR[position]);
                return view;
            }
        };
        colorLV.setAdapter(arrayAdapter);

        colorLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotepadConfig callActivity =  (NotepadConfig)getActivity();
                callActivity.setConfigColor(position);
                dismiss();
            }
        });

        return v;
    }
}
