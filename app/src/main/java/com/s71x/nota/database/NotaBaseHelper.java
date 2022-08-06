package com.s71x.nota.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotaBaseHelper extends SQLiteOpenHelper {
	//version de la base de datos
	private static final int VERSION = 1;
	//nombre de la base de datos
	private static final String DATABASE_NAME = "notaBase.db";
	//contexto de la base de datos
	private Context mContext;

	//sentencia sql que crea la tabla
	private static final  String SQL_CREATE_ENTRIES_STORAGE = "CREATE TABLE " + NotaDbSchema.StorageTable.TABLE_NAME + " (" +
			NotaDbSchema.StorageTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			NotaDbSchema.StorageTable.COLUMN_NAME_UUID + " TEXT," +
			NotaDbSchema.StorageTable.COLUMN_NAME_TYPE + " INTEGER," +
			NotaDbSchema.StorageTable.COLUMN_NAME_NAME + " TEXT)";

	private static final  String SQL_CREATE_ENTRIES_PROD = "CREATE TABLE " + NotaDbSchema.ProdTable.TABLE_NAME + " (" +
			NotaDbSchema.ProdTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			NotaDbSchema.ProdTable.COLUMN_NAME_UUID + " TEXT," +
			NotaDbSchema.ProdTable.COLUMN_NAME_IMG + " BLOB," +
			NotaDbSchema.ProdTable.COLUMN_NAME_NAME + " TEXT," +
			NotaDbSchema.ProdTable.COLUMN_NAME_DESCRIPTION + " TEXT," +
			NotaDbSchema.ProdTable.COLUMN_NAME_AMOUNT + " INTEGER," +
			NotaDbSchema.ProdTable.COLUMN_NAME_DATE + " INTEGER," +
			NotaDbSchema.ProdTable.COLUMN_NAME_UUID_STORAGE + " TEXT)";

	//constructor
	public NotaBaseHelper(Context context){
		super(context,DATABASE_NAME,null,VERSION);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//ejecuto la sentencia que crea las tabla
		db.execSQL(SQL_CREATE_ENTRIES_STORAGE);
		db.execSQL(SQL_CREATE_ENTRIES_PROD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
