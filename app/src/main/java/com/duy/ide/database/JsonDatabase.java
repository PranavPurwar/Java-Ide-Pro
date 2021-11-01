package com.duy.ide.database;

import android.content.Context;
import android.text.TextUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonDatabase implements ITabDatabase {
   private JsonDatabase.RecentFileJsonHelper mHelper;
   private Context mContext;
   private String RECENT_FILES_DATABASE_NAME = "recent_files.json";
   private String KEYWORDS_DATABASE_NAME = "keywords.json";

   public JsonDatabase(Context context) {
      this.mContext = context;
      this.mHelper = new JsonDatabase.RecentFileJsonHelper();
   }

   public void addRecentFile(String path, String encoding) {
      if (!TextUtils.isEmpty(path)) {
         try {
            JSONObject database = this.getRecentFileDatabase();
            JSONObject jsonItem;
            RecentFileItem recentFile;
            if (database.has(path)) {
               jsonItem = database.getJSONObject(path);
               recentFile = this.mHelper.read(jsonItem);
               recentFile.setPath(path);
               recentFile.setLastOpen(true);
            } else {
               jsonItem = new JSONObject();
               recentFile = new RecentFileItem();
               recentFile.setPath(path);
               recentFile.setEncoding(encoding);
               recentFile.setLastOpen(true);
               database.put(path, jsonItem);
            }

            this.mHelper.write(jsonItem, recentFile);
            this.saveRecentFileDatabase(database);
         } catch (JSONException var6) {
            var6.printStackTrace();
         }

      }
   }

   public void updateRecentFile(String path, boolean lastOpen) {
      try {
         JSONObject database = this.getRecentFileDatabase();
         if (database.has(path)) {
            JSONObject jsonItem = database.getJSONObject(path);
            RecentFileItem recentFile = this.mHelper.read(jsonItem);
            recentFile.setPath(path);
            recentFile.setLastOpen(lastOpen);
            this.mHelper.write(jsonItem, recentFile);
            this.saveRecentFileDatabase(database);
         }
      } catch (JSONException var6) {
         var6.printStackTrace();
      }

   }

   public void updateRecentFile(String path, String encoding, int offset) {
      try {
         JSONObject database = this.getRecentFileDatabase();
         if (database.has(path)) {
            JSONObject jsonItem = database.getJSONObject(path);
            RecentFileItem recentFile = this.mHelper.read(jsonItem);
            recentFile.setPath(path);
            recentFile.setOffset(offset);
            this.mHelper.write(jsonItem, recentFile);
            this.saveRecentFileDatabase(database);
         } else {
            this.addRecentFile(path, encoding);
         }
      } catch (JSONException var7) {
         var7.printStackTrace();
      }

   }

   public ArrayList<RecentFileItem> getRecentFiles() {
      return this.getRecentFiles(false);
   }

   public ArrayList<RecentFileItem> getRecentFiles(boolean lastOpenFiles) {
      ArrayList<RecentFileItem> list = new ArrayList(30);
      JSONObject db = this.getRecentFileDatabase();
      Iterator keys = db.keys();

      while(keys.hasNext()) {
         String key = (String)keys.next();

         try {
            JSONObject jsonObject = db.getJSONObject(key);
            RecentFileItem file = this.mHelper.read(jsonObject);
            if (file.isLastOpen() == lastOpenFiles) {
               list.add(file);
            }
         } catch (JSONException var8) {
            var8.printStackTrace();
         }
      }

      return list;
   }

   public void clearRecentFiles() {
      this.saveRecentFileDatabase(new JSONObject());
   }

   public void clearFindKeywords(boolean isReplace) {
      try {
         JSONObject database = this.getKeywordsDatabase();
         database.put("keywords", new JSONArray());
         this.saveKeywordsDatabase(database);
      } catch (JSONException var3) {
         var3.printStackTrace();
      }

   }

   public void addFindKeyword(String keyword, boolean isReplace) {
      try {
         JSONObject database = this.getKeywordsDatabase();
         JSONArray jsonArray;
         if (database.has("keywords")) {
            jsonArray = database.getJSONArray("keywords");
         } else {
            jsonArray = new JSONArray();
            database.put("keywords", jsonArray);
         }

         JSONObject value = new JSONObject();
         value.put("keyword", keyword);
         value.put("isReplace", isReplace);
         jsonArray.put(value);
         this.saveKeywordsDatabase(database);
      } catch (JSONException var6) {
         var6.printStackTrace();
      }

   }

   public ArrayList<String> getFindKeywords(boolean isReplace) {
      ArrayList<String> list = new ArrayList();
      JSONObject database = this.getKeywordsDatabase();
      if (!database.has("keywords")) {
         try {
            JSONArray array = database.getJSONArray("keywords");

            for(int i = 0; i < array.length(); ++i) {
               JSONObject item = array.getJSONObject(i);
               if (item.getBoolean("isReplace") == isReplace) {
                  list.add(item.getString("keyword"));
               }
            }
         } catch (JSONException var7) {
            var7.printStackTrace();
         }
      }

      if (list.isEmpty()) {
         list.add("");
      }

      return list;
   }

   private JSONObject getKeywordsDatabase() {
      return this.readFromFile(this.KEYWORDS_DATABASE_NAME);
   }

   private void saveKeywordsDatabase(JSONObject database) {
      this.writeJsonToFile(database, this.KEYWORDS_DATABASE_NAME);
   }

   private void writeJsonToFile(JSONObject jsonObject, String fileName) {
      try {
         File file = new File(this.mContext.getFilesDir(), "database" + File.separator + fileName);
         file.getParentFile().mkdirs();
         FileOutputStream output = new FileOutputStream(file);
         IOUtils.write(jsonObject.toString(), output);
         output.close();
      } catch (IOException var5) {
         var5.printStackTrace();
      }

   }

   private void saveRecentFileDatabase(JSONObject database) {
      this.writeJsonToFile(database, this.RECENT_FILES_DATABASE_NAME);
   }

   public JSONObject readFromFile(String fileName) {
      try {
         File jsonDatabase = new File(this.mContext.getFilesDir(), "database" + File.separator + fileName);
         jsonDatabase.getParentFile().mkdirs();
         FileInputStream input = new FileInputStream(jsonDatabase);
         String content = IOUtils.toString(input);
         input.close();
         return new JSONObject(content);
      } catch (IOException var5) {
         var5.printStackTrace();
      } catch (JSONException var6) {
         var6.printStackTrace();
      }

      return new JSONObject();
   }

   private JSONObject getRecentFileDatabase() {
      return this.readFromFile(this.RECENT_FILES_DATABASE_NAME);
   }

   private class RecentFileJsonHelper {
      private RecentFileJsonHelper() {
      }

      public void write(JSONObject json, RecentFileItem item) throws JSONException {
         json.put("time", item.time);
         json.put("path", item.path);
         json.put("encoding", item.encoding);
         json.put("offset", item.offset);
         json.put("isLastOpen", item.isLastOpen);
      }

      public RecentFileItem read(JSONObject json) throws JSONException {
         RecentFileItem item = new RecentFileItem();
         if (json.has("time")) {
            item.time = (long)json.getInt("time");
         }

         if (json.has("path")) {
            item.path = json.getString("path");
         }

         if (json.has("encoding")) {
            item.encoding = json.getString("encoding");
         }

         if (json.has("offset")) {
            item.offset = json.getInt("offset");
         }

         if (json.has("isLastOpen")) {
            item.isLastOpen = json.getBoolean("isLastOpen");
         }

         return item;
      }

      // $FF: synthetic method
      RecentFileJsonHelper(Object x1) {
         this();
      }
   }
}