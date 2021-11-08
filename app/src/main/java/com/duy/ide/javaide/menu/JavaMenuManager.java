package com.duy.ide.javaide.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.SubMenu;
import com.jecelyin.editor.v2.manager.MenuManager;
import java.util.ArrayList;
import java.util.Iterator;

public class JavaMenuManager {
   private final Context mContext;

   public JavaMenuManager(Context var1) {
      this.mContext = var1;
   }

   private void addToMenu(SubMenu var1, int var2, int var3, int[] var4) {
      var1 = var1.addSubMenu(0, 0, 0, var3);
      var1.getItem().setIcon(MenuManager.makeMenuNormalIcon(this.mContext, var2)).setShowAsAction(2);

      for(var2 = 0; var2 < var4.length / 3; ++var2) {
         Context var5 = this.mContext;
         var3 = var2 * 3;
         Drawable var6 = MenuManager.makeMenuNormalIcon(var5, var4[var3 + 2]);
         var1.add(0, var4[var3], 0, var4[var3 + 1]).setIcon(var6);
      }

   }

   public void createFileMenu(SubMenu var1) {
      var1.removeItem(2131296304);
      var1.removeItem(2131296306);
      ArrayList var2 = new ArrayList();

      for(int var3 = 0; var3 < var1.size(); ++var3) {
         var2.add(var1.getItem(var3));
      }

      var1.clear();
      this.addToMenu(var1, 2131230985, 2131690018, new int[]{2131296305, 2131689800, 2131230985, 2131296302, 2131689795, 2131230985, 2131296303, 2131689796, 2131230985, 2131296304, 2131689797, 2131230996});
      this.addToMenu(var1, 2131230857, 2131690019, new int[]{2131296308, 2131689818, 2131231001, 2131296307, 2131689815, 2131231001});
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         MenuItem var5 = (MenuItem)var4.next();
         var1.add(var5.getGroupId(), var5.getItemId(), var5.getOrder(), var5.getTitle()).setIcon(var5.getIcon());
      }

   }
}