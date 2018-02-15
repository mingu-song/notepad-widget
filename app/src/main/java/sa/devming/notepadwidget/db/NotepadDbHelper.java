package sa.devming.notepadwidget.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class NotepadDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "notepad.db";

    public static class NotepadDB /*implements BaseColumns*/ {
        public static final String TABLE_NAME = "notepad";
        public static final String COLUMN_NAME_HEAD = "head";
        public static final String COLUMN_NAME_BODY = "body";
        public static final String COLUMN_NAME_WIDGET_ID = "widget_id";
        public static final String COLUMN_NAME_COLOR_ID = "color_id";
        public static final String COLUMN_NAME_TEXT_SIZE = "test_size";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + NotepadDB.TABLE_NAME + " (" +
                    NotepadDB.COLUMN_NAME_WIDGET_ID + INTEGER_TYPE +" PRIMARY KEY," +
                    NotepadDB.COLUMN_NAME_COLOR_ID + INTEGER_TYPE + COMMA_SEP +
                    NotepadDB.COLUMN_NAME_HEAD + TEXT_TYPE + COMMA_SEP +
                    NotepadDB.COLUMN_NAME_BODY + TEXT_TYPE + COMMA_SEP +
                    NotepadDB.COLUMN_NAME_TEXT_SIZE+ INTEGER_TYPE + " DEFAULT 14 )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + NotepadDB.TABLE_NAME;

    public NotepadDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            StringBuilder sb = new StringBuilder();
            sb.append(" ALTER TABLE ")
                    .append(NotepadDB.TABLE_NAME)
                    .append(" ADD ")
                    .append(NotepadDB.COLUMN_NAME_TEXT_SIZE)
                    .append(INTEGER_TYPE)
                    .append(" DEFAULT 0 ");
            db.execSQL(sb.toString());
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long addNotepad(Notepad notepad) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NotepadDB.COLUMN_NAME_HEAD, notepad.getHead());
        values.put(NotepadDB.COLUMN_NAME_BODY, notepad.getBody());
        values.put(NotepadDB.COLUMN_NAME_WIDGET_ID, notepad.getWidgetId());
        values.put(NotepadDB.COLUMN_NAME_COLOR_ID, notepad.getColorId());
        values.put(NotepadDB.COLUMN_NAME_TEXT_SIZE, notepad.getTextSize());

        return db.insert(NotepadDB.TABLE_NAME, null, values);
    }

    public Notepad getNotepad(int widgetId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                NotepadDB.TABLE_NAME,                      // The table to query
                new String[] {NotepadDB.COLUMN_NAME_WIDGET_ID,
                        NotepadDB.COLUMN_NAME_COLOR_ID,
                        NotepadDB.COLUMN_NAME_HEAD,
                        NotepadDB.COLUMN_NAME_BODY,
                        NotepadDB.COLUMN_NAME_TEXT_SIZE},       // The columns to return
                NotepadDB.COLUMN_NAME_WIDGET_ID + " = ?",  // The columns for the WHERE clause
                new String[] {String.valueOf(widgetId)},   // The values for the WHERE clause
                null,                                      // group by
                null,                                      // having
                null,                                      // order by
                null                                       // limit
                );

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Notepad notepad = new Notepad(
                cursor.getInt(cursor.getColumnIndexOrThrow(NotepadDB.COLUMN_NAME_COLOR_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(NotepadDB.COLUMN_NAME_WIDGET_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(NotepadDB.COLUMN_NAME_HEAD)),
                cursor.getString(cursor.getColumnIndexOrThrow(NotepadDB.COLUMN_NAME_BODY)),
                cursor.getInt(cursor.getColumnIndexOrThrow(NotepadDB.COLUMN_NAME_TEXT_SIZE)));

        return  notepad;
    }

    public ArrayList<Notepad> getAllNotepad() {
        ArrayList<Notepad> notepads = new ArrayList();
        String selectQuery = "SELECT * FROM " + NotepadDB.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Notepad note = new Notepad();
                note.setColorId(cursor.getInt(cursor.getColumnIndexOrThrow(NotepadDB.COLUMN_NAME_COLOR_ID)));
                note.setWidgetId(cursor.getInt(cursor.getColumnIndexOrThrow(NotepadDB.COLUMN_NAME_WIDGET_ID)));
                note.setHead(cursor.getString(cursor.getColumnIndexOrThrow(NotepadDB.COLUMN_NAME_HEAD)));
                note.setBody(cursor.getString(cursor.getColumnIndexOrThrow(NotepadDB.COLUMN_NAME_BODY)));
                note.setTextSize(cursor.getInt(cursor.getColumnIndexOrThrow(NotepadDB.COLUMN_NAME_TEXT_SIZE)));
                notepads.add(note);
            } while (cursor.moveToNext());
        }
        return notepads;
    }

    public int getNotepadCnt() {
        String selectQuery = "SELECT * FROM " + NotepadDB.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        return (cursor == null)? 0 : cursor.getCount();
    }

    public boolean existNote(int widgetId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(NotepadDB.TABLE_NAME,                      // The table to query
                                new String[] {NotepadDB.COLUMN_NAME_WIDGET_ID},       // The columns to return
                                NotepadDB.COLUMN_NAME_WIDGET_ID + " = ?",  // The columns for the WHERE clause
                                new String[] {String.valueOf(widgetId)},   // The values for the WHERE clause
                                null,                                      // group by
                                null,                                      // having
                                null                                       // order by
                                );
        if (cursor == null) {
            return false;
        } else {
            return cursor.getCount() > 0;
        }
    }

    public int updateNotepad(Notepad notepad) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NotepadDB.COLUMN_NAME_HEAD, notepad.getHead());
        values.put(NotepadDB.COLUMN_NAME_BODY, notepad.getBody());
        values.put(NotepadDB.COLUMN_NAME_COLOR_ID, notepad.getColorId());
        values.put(NotepadDB.COLUMN_NAME_TEXT_SIZE, notepad.getTextSize());

        return db.update(NotepadDB.TABLE_NAME,
                values,
                NotepadDB.COLUMN_NAME_WIDGET_ID+ " = ?",
                new String[] {String.valueOf(notepad.getWidgetId())}
                );
    }

    public void deleteNotepad(int widgetId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NotepadDB.TABLE_NAME,
                NotepadDB.COLUMN_NAME_WIDGET_ID + " = ?",
                new String[] {String.valueOf(widgetId)});
    }
}