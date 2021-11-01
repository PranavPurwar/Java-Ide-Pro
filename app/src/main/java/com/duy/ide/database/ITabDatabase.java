package com.duy.ide.database;

import java.util.ArrayList;

public interface ITabDatabase {
   void addFindKeyword(String var1, boolean var2);

   void addRecentFile(String var1, String var2);

   void clearFindKeywords(boolean var1);

   void clearRecentFiles();

   ArrayList<String> getFindKeywords(boolean var1);

   ArrayList<RecentFileItem> getRecentFiles();

   ArrayList<RecentFileItem> getRecentFiles(boolean var1);

   void updateRecentFile(String var1, String var2, int var3);

   void updateRecentFile(String var1, boolean var2);
}