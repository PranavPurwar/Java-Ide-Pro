package com.duy.ide.database;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import java.util.ArrayList;

public class SQLHelper extends SQLiteOpenHelper implements ITabDatabase {
    private static final String DATABASE_NAME = "920-text-editor.db";
    private static final int DATABASE_VERSION = 4;

    public SQLHelper(Context context) {
        this(context, DATABASE_NAME, null, 4);
    }

    public SQLHelper(Context context, String name, SQLiteDatabase.CursorFactory cursorFactory, int i) {
        this(context, name, cursorFactory, i, new DatabaseErrorHandler() {
            public void onCorruption(SQLiteDatabase database) {
            }
        });
    }

    public SQLHelper(Context context, String name, SQLiteDatabase.CursorFactory cursorFactory, int i, DatabaseErrorHandler databaseErrorHandler) {
        super(context, name, cursorFactory, i, databaseErrorHandler);
    }

    public static ITabDatabase getInstance(Context context) {
        return new JsonDatabase(context.getApplicationContext());
    }

    private void upgradeTo2(SQLiteDatabase database) {
        database.execSQL("alter table recent_files ADD COLUMN encoding TEXT");
    }

    private void upgradeTo3(SQLiteDatabase database) {
        database.execSQL("alter table recent_files ADD COLUMN offset integer");
        database.execSQL("alter table recent_files ADD COLUMN last_open integer");
    }

    private void upgradeTo4(SQLiteDatabase database) {
        createFindKeywordsTable(database);
    }

    public void addFindKeyword(String str, boolean z) {
        if (!TextUtils.isEmpty(str)) {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            writableDatabase.execSQL("REPLACE INTO find_keywords VALUES (?, ?, ?)", new Object[]{str, Integer.valueOf(z ? 1 : 0), Long.valueOf(System.currentTimeMillis())});
            writableDatabase.close();
        }
    }

    public void addRecentFile(String str, String str2) {
        if (!TextUtils.isEmpty(str)) {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            writableDatabase.execSQL("REPLACE INTO recent_files VALUES (?, ?, ?, ?, ?)", new Object[]{str, Long.valueOf(System.currentTimeMillis()), str2, 0, 1});
            writableDatabase.close();
        }
    }

    public void clearFindKeywords(boolean z) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        StringBuilder sb = new StringBuilder();
        sb.append("delete from find_keywords where is_replace=");
        sb.append(z ? "1" : "0");
        writableDatabase.execSQL(sb.toString());
        writableDatabase.close();
    }

    public void clearRecentFiles() {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.execSQL("delete from recent_files");
        writableDatabase.close();
    }

    public void createFindKeywordsTable(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE \"find_keywords\" (\n\t \"keyword\" TEXT NOT NULL,\n\t \"is_replace\" integer,\n\t \"ctime\" integer,\n\tPRIMARY KEY(\"keyword\", \"is_replace\")\n)");
        database.execSQL("CREATE INDEX \"ctime\" ON find_keywords (\"ctime\" DESC)");
    }

    public ArrayList<String> getFindKeywords(boolean z) {
        ArrayList<String> arrayList = new ArrayList<>();
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor query = readableDatabase.query("find_keywords", new String[]{"keyword"}, "is_replace=?", new String[]{z ? "1" : "0"}, (String) null, (String) null, "ctime desc", "100");
        while (query.moveToNext()) {
            arrayList.add(query.getString(0));
        }
        query.close();
        readableDatabase.close();
        return arrayList;
    }

    public ArrayList<RecentFileItem> getRecentFiles() {
        return getRecentFiles(false);
    }

    public ArrayList<RecentFileItem> getRecentFiles(boolean z) {
        ArrayList<RecentFileItem> arrayList = new ArrayList<>(30);
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor query = readableDatabase.query("recent_files", (String[]) null, (String) null, (String[]) null, (String) null, (String) null, z ? "open_time asc" : "open_time desc", "30");
        while (query.moveToNext()) {
            boolean z2 = query.getInt(4) == 1;
            if (!z || z2) {
                RecentFileItem recentFileItem = new RecentFileItem();
                recentFileItem.path = query.getString(0);
                recentFileItem.time = query.getLong(1);
                recentFileItem.encoding = query.getString(2);
                recentFileItem.offset = query.getInt(3);
                recentFileItem.isLastOpen = z2;
                arrayList.add(recentFileItem);
            }
        }
        query.close();
        readableDatabase.close();
        return arrayList;
    }

    public void onCreate(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE \"recent_files\" (\n\t \"path\" TEXT NOT NULL,\n\t \"open_time\" integer,\n\t \"encoding\" TEXT,\n\t \"offset\" integer,\n\t \"last_open\" integer,\n\tPRIMARY KEY(\"path\")\n)");
        database.execSQL("CREATE INDEX \"open_time\" ON recent_files (\"open_time\" DESC)");
        createFindKeywordsTable(database);
    }

    public void onUpgrade(SQLiteDatabase database, int i, int i2) {
        while (i < i2) {
            switch (i) {
                case 1:
                    upgradeTo2(database);
                    break;
                case 2:
                    upgradeTo3(database);
                    break;
                case 3:
                    upgradeTo4(database);
                    break;
            }
            i++;
        }
    }

    public void updateRecentFile(String str, String str2, int i) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        if (i >= 0) {
            writableDatabase.execSQL("UPDATE recent_files SET encoding = ?, offset = ? WHERE path = ?", new Object[]{str2, Integer.valueOf(i), str});
        } else {
            writableDatabase.execSQL("UPDATE recent_files SET encoding = ? WHERE path = ?", new Object[]{str2, str});
        }
        writableDatabase.close();
    }

    public void updateRecentFile(String str, boolean z) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.execSQL("UPDATE recent_files SET last_open = ? WHERE path = ?", new Object[]{Integer.valueOf(z ? 1 : 0), str});
        writableDatabase.close();
    }
}
