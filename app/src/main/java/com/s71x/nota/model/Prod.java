package com.s71x.nota.model;

import java.util.Date;
import java.util.UUID;

public class Prod {
	private UUID mUuid;
	private byte[] mImg;
	private String mName;
	private String mDescription;
	private int mAmount;

	public int getmAmount() {
		return mAmount;
	}

	public void setmAmount(int mAmount) {
		this.mAmount = mAmount;
	}

	private Date mDate;
	private UUID mUuidStorage;

	public Prod(){
		this(UUID.randomUUID());
	}

	public Prod(Date date, UUID uuidStorage){
		this(UUID.randomUUID());
		mImg = null;
		mName = "";
		mDescription = "";
		mDate = date;
		mUuidStorage = uuidStorage;
		mAmount = 1;
	}

	public Prod(UUID uuid){
		mUuid = uuid;
	}

	public UUID getmUuid() {
		return mUuid;
	}

	public void setmUuid(UUID mUuid) {
		this.mUuid = mUuid;
	}

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public Date getmDate() {
		return mDate;
	}

	public void setmDate(Date mDate) {
		this.mDate = mDate;
	}

	public byte[] getmImg() {
		return mImg;
	}

	public void setmImg(byte[] mImg) {
		this.mImg = mImg;
	}

	public String getmDescription() {
		return mDescription;
	}

	public void setmDescription(String mDescription) {
		this.mDescription = mDescription;
	}

	public UUID getmUuidStorage() {
		return mUuidStorage;
	}

	public void setmUuidStorage(UUID mUuidStorage) {
		this.mUuidStorage = mUuidStorage;
	}
}
