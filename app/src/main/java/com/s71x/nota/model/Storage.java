package com.s71x.nota.model;

import java.util.UUID;

public class Storage {
	private UUID mId;
	private int mType;
	private String mName;

	public Storage(){
		this(UUID.randomUUID());
		this.mType = 0;
		this.mName = "";
	}

	public Storage(UUID uuid){
		mId = uuid;
	}

	public void setmId(UUID mId) {
		this.mId = mId;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public UUID getmId() {
		return mId;
	}

	public String getmName() {
		return mName;
	}


	public int getmType() {
		return mType;
	}

	public void setmType(int mType) {
		this.mType = mType;
	}
}
