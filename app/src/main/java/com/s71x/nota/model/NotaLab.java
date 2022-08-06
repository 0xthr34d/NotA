package com.s71x.nota.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.util.Pair;

import com.s71x.nota.R;
import com.s71x.nota.database.NotaBaseHelper;
import com.s71x.nota.database.NotaCursorWrapper;
import com.s71x.nota.database.NotaDbSchema;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class NotaLab {

	public static NotaLab sNotaLab;

	private Context mContext;
	private SQLiteDatabase mDatabase;

	private NotaLab(Context context){
		mContext = context.getApplicationContext();
		mDatabase = new NotaBaseHelper(mContext).getWritableDatabase();
	}

	public static NotaLab get(Context context){
		if(sNotaLab == null){
			sNotaLab = new NotaLab(context);
		}
		return sNotaLab;
	}

	//devuelve un objeto Storage pasandole el uuid
	public Storage getStorageByUuid(String uuid){
		Storage storage;

		NotaCursorWrapper cursor = queryStorage(NotaDbSchema.StorageTable.COLUMN_NAME_UUID + "=?",new String[]{uuid});
		cursor.moveToFirst();
		storage = cursor.getStorage();
		cursor.close();

		return storage;
	}

	//devuelve un producto pasandole el uuid
	public Prod getProdByUuid(String uuid){

		Prod prod = new Prod();
		//creo un cursor para consultas en la base de datos
		NotaCursorWrapper cursor = queryProds(NotaDbSchema.ProdTable.COLUMN_NAME_UUID + "=?",new String[]{uuid});
		try{
			//mueve el cursor a la primera posicion
			cursor.moveToFirst();
			//while que recorre las filas
			while(!cursor.isAfterLast()){

				prod = cursor.getProd();
				//el cursor se mueve a la siguiente fila
				cursor.moveToNext();
			}
		}finally {
			//se cierra el cursor
			cursor.close();
		}

		return prod;
	}

	//devuelve una lista de todos objetos Storage
	public List<Storage> getStorages(){

		List<Storage> storages = new ArrayList<>();
		//creo un cursor para consultas en la base de datos
		NotaCursorWrapper cursor = queryStorage(null,null);
		try{
			//mueve el cursor a la primera posicion
			cursor.moveToFirst();
			//while que recorre las filas
			while(!cursor.isAfterLast()){
				//añade un objeto tipo Storage la lista
				storages.add(cursor.getStorage());
				//el cursor se mueve a la siguiente fila
				cursor.moveToNext();
			}
		}finally {
			//se cierra el cursor
			cursor.close();
		}
		//devuelve la lista
		return storages;
	}

	//devuelve una lista de objetos Prod
	public List<Prod> getProdsByUuidStorage(String uuidStorage){

		List<Prod> prods = new ArrayList<>();
		//creo un cursor para consultas en la base de datos
		NotaCursorWrapper cursor = queryProds(NotaDbSchema.ProdTable.COLUMN_NAME_UUID_STORAGE + "=?",new String[]{uuidStorage});
		try{
			//mueve el cursor a la primera posicion
			cursor.moveToFirst();
			//while que recorre las filas
			while(!cursor.isAfterLast()){
				//añade un objeto tipo Prod la lista List<Prod>
				prods.add(cursor.getProd());
				//el cursor se mueve a la siguiente fila
				cursor.moveToNext();
			}
		}finally {
			//se cierra el cursor
			cursor.close();
		}
		//devuelve la lista
		return prods;
	}

	//devuelve un objeto NotaCursorWrapper
	private NotaCursorWrapper queryStorage(String selection, String[] selectionArgs){
		//consulta en la base de datos utilizando orderBy y limit
		Cursor cursor = mDatabase.query(
				NotaDbSchema.StorageTable.TABLE_NAME,
				null,
				selection,
				selectionArgs,
				null,
				null,
				null,
				null

		);
		return new NotaCursorWrapper(cursor);
	}

	private NotaCursorWrapper queryProds( String selection, String[] selectionArgs){
		//consulta en la base de datos utilizando orderBy y limit
		Cursor cursor = mDatabase.query(
				NotaDbSchema.ProdTable.TABLE_NAME,
				null,
				selection,
				selectionArgs,
				null,
				null,
				NotaDbSchema.ProdTable.COLUMN_NAME_NAME,
				null

		);
		return new NotaCursorWrapper(cursor);
	}

	//añade un objeto tipo Storage a la base de datos
	public void addStorage(Storage storage){
		ContentValues values = new ContentValues();

		values.put(NotaDbSchema.StorageTable.COLUMN_NAME_UUID,storage.getmId().toString());
		values.put(NotaDbSchema.StorageTable.COLUMN_NAME_TYPE,storage.getmType());
		values.put(NotaDbSchema.StorageTable.COLUMN_NAME_NAME,storage.getmName());

		mDatabase.insert(NotaDbSchema.StorageTable.TABLE_NAME,null,values);
	}

	//añade un objeto tipo Prod a la base de datos
	public void addProd(Prod prod){
		ContentValues values = new ContentValues();

		values.put(NotaDbSchema.ProdTable.COLUMN_NAME_UUID,prod.getmUuid().toString());
		values.put(NotaDbSchema.ProdTable.COLUMN_NAME_IMG,prod.getmImg());
		values.put(NotaDbSchema.ProdTable.COLUMN_NAME_NAME,prod.getmName());
		values.put(NotaDbSchema.ProdTable.COLUMN_NAME_DESCRIPTION,prod.getmDescription());
		values.put(NotaDbSchema.ProdTable.COLUMN_NAME_AMOUNT,prod.getmAmount());
		values.put(NotaDbSchema.ProdTable.COLUMN_NAME_UUID_STORAGE,prod.getmUuidStorage().toString());
		values.put(NotaDbSchema.ProdTable.COLUMN_NAME_DATE,prod.getmDate().getTime());

		mDatabase.insert(NotaDbSchema.ProdTable.TABLE_NAME,null,values);
	}

	//añade un objeto tipo Prod a la base de datos
	public void addProds(List<Prod> prods){
		ContentValues values = new ContentValues();
		for(Prod prod: prods) {
			values.put(NotaDbSchema.ProdTable.COLUMN_NAME_UUID, prod.getmUuid().toString());
			values.put(NotaDbSchema.ProdTable.COLUMN_NAME_IMG, prod.getmImg());
			values.put(NotaDbSchema.ProdTable.COLUMN_NAME_NAME, prod.getmName());
			values.put(NotaDbSchema.ProdTable.COLUMN_NAME_DESCRIPTION, prod.getmDescription());
			values.put(NotaDbSchema.ProdTable.COLUMN_NAME_AMOUNT, prod.getmAmount());
			values.put(NotaDbSchema.ProdTable.COLUMN_NAME_UUID_STORAGE, prod.getmUuidStorage().toString());
			values.put(NotaDbSchema.ProdTable.COLUMN_NAME_DATE, prod.getmDate().getTime());

			mDatabase.insert(NotaDbSchema.ProdTable.TABLE_NAME, null, values);
		}
	}

	//actualiza el almacen en la base de datos
	public void updateStorage(Storage storage) {
		ContentValues values = new ContentValues();
		values.put(NotaDbSchema.StorageTable.COLUMN_NAME_UUID,storage.getmId().toString());
		values.put(NotaDbSchema.StorageTable.COLUMN_NAME_TYPE,storage.getmType());
		values.put(NotaDbSchema.StorageTable.COLUMN_NAME_NAME,storage.getmName());
		//hace un update en la basede datos
		mDatabase.update(NotaDbSchema.StorageTable.TABLE_NAME,values, NotaDbSchema.StorageTable.COLUMN_NAME_UUID + " = ?", new String[]{storage.getmId().toString()});
	}

	//actualiza el producto en la base de datos
	public void updateProd(Prod prod) {
		ContentValues values = new ContentValues();
		values.put(NotaDbSchema.ProdTable.COLUMN_NAME_UUID,prod.getmUuid().toString());
		values.put(NotaDbSchema.ProdTable.COLUMN_NAME_IMG,prod.getmImg());
		values.put(NotaDbSchema.ProdTable.COLUMN_NAME_NAME,prod.getmName());
		values.put(NotaDbSchema.ProdTable.COLUMN_NAME_DESCRIPTION,prod.getmDescription());
		values.put(NotaDbSchema.ProdTable.COLUMN_NAME_AMOUNT,prod.getmAmount());
		values.put(NotaDbSchema.ProdTable.COLUMN_NAME_UUID_STORAGE,prod.getmUuidStorage().toString());
		values.put(NotaDbSchema.ProdTable.COLUMN_NAME_DATE,prod.getmDate().getTime());
		//hace un update en la base de datos
		mDatabase.update(NotaDbSchema.ProdTable.TABLE_NAME,values,NotaDbSchema.ProdTable.COLUMN_NAME_UUID + " = ?", new String[]{prod.getmUuid().toString()});
	}

	//borra un producto de la base de datos
	public void deleteProdByUuid(String uuid) {
		mDatabase.delete(NotaDbSchema.ProdTable.TABLE_NAME,NotaDbSchema.ProdTable.COLUMN_NAME_UUID + " = ?", new String[]{uuid});
	}

	public void deleteProdbyStorageUuid(String storageUuid) {
		mDatabase.delete(NotaDbSchema.ProdTable.TABLE_NAME,NotaDbSchema.ProdTable.COLUMN_NAME_UUID_STORAGE + " = ?", new String[]{storageUuid});
	}

	//borra un almacen de la base de datos
	public void deleteStoragebyUuid(String uuid) {

		mDatabase.delete(NotaDbSchema.StorageTable.TABLE_NAME, NotaDbSchema.StorageTable.COLUMN_NAME_UUID + " = ?", new String[]{uuid});
	}

	//transforma un Bitmap a un objeto byte[] para poder guardar las imagenes en la base de datos
	public static byte[] transformBitmapToArray(Bitmap bitmap){

		//crea un objeto ByteArrayOutputStream;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		//cmomprime el bitmap en formato jpeg con total calidad pasandolo al objeto stream
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		//devuelve la imagen convertida a un array de bytes
		return stream.toByteArray();
	}

	//convierte un array a un objeto de tipo Bitmap
	public static Bitmap transformByteArrayToBitmap(byte[] bitmap){
		return BitmapFactory.decodeByteArray(bitmap,0,bitmap.length);
	}

	public static Pair<Integer,Integer> imageAndType(int type){
		int resImg = 0;
		int typeTextInt = 0;

		switch(type){
			case 0:
				resImg = R.drawable.frigorifico;
				typeTextInt =  R.string.type0;//"Frigorifico"
				break;
			case 1:
				resImg = R.drawable.congelador;
				typeTextInt = R.string.type1;
				break;
			case 2:
				resImg = R.drawable.despensa;
				typeTextInt = R.string.type2;
				break;
			case 3:
				resImg = R.drawable.otro;
				typeTextInt = R.string.type3;
				break;
		}
		return new Pair<>(resImg, typeTextInt);
	}

}
