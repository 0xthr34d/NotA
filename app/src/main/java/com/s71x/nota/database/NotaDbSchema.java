package com.s71x.nota.database;

import android.provider.BaseColumns;

public class NotaDbSchema {
	//se crea un constructor privado para evitar que alguien instancie la clase accidentalmente
	private NotaDbSchema(){}

	//clase interna que define el contenido de la tabla
	public static final class StorageTable implements BaseColumns {
		public static final String TABLE_NAME = "conts";
		public static final String COLUMN_NAME_UUID = "uuid";
		public static final String COLUMN_NAME_TYPE = "type";
		public static final String COLUMN_NAME_NAME = "name";
	}

	public static final class ProdTable implements BaseColumns {
		public static final String TABLE_NAME = "prods";
		public static final String COLUMN_NAME_UUID = "uuid";
		public static final String COLUMN_NAME_IMG = "img";
		public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_DESCRIPTION = "description";
		public static final String COLUMN_NAME_DATE = "date";
		public static final String COLUMN_NAME_AMOUNT = "amount";
		public static final String COLUMN_NAME_UUID_STORAGE = "uuidCont";

	}
}
