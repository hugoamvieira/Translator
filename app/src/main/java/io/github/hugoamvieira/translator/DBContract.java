package io.github.hugoamvieira.translator;

import android.provider.BaseColumns;


public class DBContract {
    private DBContract() {
    }

    // Create Translation Schema
    public static class Translation implements BaseColumns {
        public static final String TABLE_NAME = "translations";

        // Table Columns
        public static final String COLUMN_OG_TEXT = "original_text";
        public static final String COLUMN_LABEL_TRANSLATION_LANG = "translation_lang";
        public static final String COLUMN_TRANSLATION = "translated_text";
        public static final String COLUMN_TRANSLATION_DATE = "translation_date";

        // Query to create table
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_OG_TEXT + " TEXT, " +
                COLUMN_LABEL_TRANSLATION_LANG + " TEXT, " +
                COLUMN_TRANSLATION + " TEXT, " +
                COLUMN_TRANSLATION_DATE + " TEXT" +
                ")";
    }
}
