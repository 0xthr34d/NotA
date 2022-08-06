package com.s71x.nota.model;

import com.s71x.nota.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StoragesView {
	private int mImage;
	private int mTypeText;
	private boolean mChecked;

	public StoragesView(int res, int typeText){
		this.mImage = res;
		this.mTypeText = typeText;
		this.mChecked = false;
	}

	public int getmImage() {
		return mImage;
	}

	public int getTypeText() {
		return mTypeText;
	}


	public boolean ismChecked() {
		return mChecked;
	}

	public void setmChecked(boolean mChecked) {
		this.mChecked = mChecked;
	}

	public static List<StoragesView> getSelectStorage(){
		return new ArrayList<>(Arrays.asList(
				new StoragesView(R.drawable.frigorifico,R.string.type0),
				new StoragesView(R.drawable.congelador,R.string.type1),
				new StoragesView(R.drawable.despensa,R.string.type2),
				new StoragesView(R.drawable.otro,R.string.type3)
		));
	}
}
