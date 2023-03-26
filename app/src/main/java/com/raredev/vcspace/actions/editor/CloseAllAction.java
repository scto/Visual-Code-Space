package com.raredev.vcspace.actions.editor;

import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.ActionData;
import com.raredev.vcspace.activity.MainActivity;

public class CloseAllAction extends EditorAction {

  @Override
  public void performAction(@NonNull ActionData data) {
    var main = (MainActivity) data.get(MainActivity.class);
    main.editorManager.closeAllFiles();

    main.invalidateOptionsMenu();
  }

  @Override
  public int getTitle() {
    return R.string.close_all;
  }
}