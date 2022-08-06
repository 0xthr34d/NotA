package com.s71x.nota.viewModel;

import android.view.ActionMode;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
//esta clase la utilizo para comunicar ActionMode con el DialogFragment
public class ProdsViewModel extends ViewModel {
	private ActionMode mActionMode;

	public ActionMode getActionMode(){
		return mActionMode;
	}

	public void setActionMode(ActionMode actionMode){
		this.mActionMode = actionMode;
	}

	public void finishActionMode(){
		mActionMode.finish();
	}
}
