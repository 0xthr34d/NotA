package com.s71x.nota.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.s71x.nota.database.NotaDbSchema.*;

import com.s71x.nota.model.Storage;
import com.s71x.nota.model.Prod;

import java.util.Date;
import java.util.UUID;

public class NotaCursorWrapper extends CursorWrapper {
	//constructor
	public NotaCursorWrapper(Cursor cursor){
		super(cursor);
	}

	//devuelve un objeto de tipo Storage obteniendo antes las propiedades de la base de datos
	public Storage getStorage(){
		//obtiene los datos y los guarda en variables
		String uuidString = getString(getColumnIndex(StorageTable.COLUMN_NAME_UUID));
		String name = getString(getColumnIndex(StorageTable.COLUMN_NAME_NAME));
		int type = getInt(getColumnIndex(StorageTable.COLUMN_NAME_TYPE));

		//crea un objeto Storage, setea las propiedades y lo devuelve
		Storage storage = new Storage();
		storage.setmId(UUID.fromString(uuidString));
		storage.setmType(type);
		storage.setmName(name);
		return storage;
	}

	//devuelve un objeto de tipo Prod obteniendo antes las propiedades de la base de datos
	public Prod getProd(){
		//obtiene los datos y los guarda en variables
		String uuidString = getString(getColumnIndex(ProdTable.COLUMN_NAME_UUID));
		byte[] img = getBlob(getColumnIndex(ProdTable.COLUMN_NAME_IMG));
		String name = getString(getColumnIndex(ProdTable.COLUMN_NAME_NAME));
		String description = getString(getColumnIndex(ProdTable.COLUMN_NAME_DESCRIPTION));
		int amount = getInt(getColumnIndex(ProdTable.COLUMN_NAME_AMOUNT));
		long date = getLong(getColumnIndex(ProdTable.COLUMN_NAME_DATE));
		String uuidStorage = getString(getColumnIndex(ProdTable.COLUMN_NAME_UUID_STORAGE));

		//crea un objeto Prod, setea las propiedades y lo devuelve
		Prod prod = new Prod();
		prod.setmUuid(UUID.fromString(uuidString));
		prod.setmImg(img);
		prod.setmName(name);
		prod.setmDescription(description);
		prod.setmAmount(amount);
		prod.setmDate(new Date(date));
		prod.setmUuidStorage(UUID.fromString(uuidStorage));
		return prod;
	}
}
