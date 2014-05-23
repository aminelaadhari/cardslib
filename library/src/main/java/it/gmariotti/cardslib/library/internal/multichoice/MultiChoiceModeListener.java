package it.gmariotti.cardslib.library.internal.multichoice;

import android.support.v7.view.ActionMode;

public interface MultiChoiceModeListener extends android.support.v7.view.ActionMode.Callback {

    public abstract void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked);
}
