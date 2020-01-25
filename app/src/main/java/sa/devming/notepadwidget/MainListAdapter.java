package sa.devming.notepadwidget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import sa.devming.notepadwidget.db.Notepad;
import sa.devming.notepadwidget.widget.NotepadConfig;

public class MainListAdapter extends BaseAdapter {
    private Context mContext;
    private List<Notepad> mNotepadList;
    private LayoutInflater mLayoutInflater;

    public MainListAdapter(Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void setNotepadList(List<Notepad> list) {
        this.mNotepadList = list;
    }

    @Override
    public int getCount() {
        return (mNotepadList==null)? 0 : mNotepadList.size();
    }

    @Override
    public Object getItem(int position) {
        return (mNotepadList != null && mNotepadList.size() > position) ? mNotepadList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        if (convertView != null) {
            mHolder = (ViewHolder) convertView.getTag();
        } else {
            mHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.main_list_item, null);
            mHolder.itemID = convertView.findViewById(R.id.itemID);
            mHolder.itemColor = convertView.findViewById(R.id.itemColor);
            mHolder.itemBody = convertView.findViewById(R.id.itemBody);
            mHolder.itemColorImg = convertView.findViewById(R.id.itemColorImg);
            convertView.setTag(mHolder);
        }

        Notepad notepad = mNotepadList.get(position);
        mHolder.itemID.setText(String.valueOf(notepad.getWidgetId()));
        mHolder.itemColor.setText(String.valueOf(notepad.getColorId()));
        mHolder.itemBody.setText(notepad.getBody());
        mHolder.itemColorImg.setBackground(ContextCompat.getDrawable(mContext, NotepadConfig.CONFIG_COLOR[notepad.getColorId()]));

        return convertView;
    }

    private class ViewHolder {
        private TextView itemID;
        private TextView itemColor;
        private TextView itemBody;
        private ImageView itemColorImg;
    }
}
