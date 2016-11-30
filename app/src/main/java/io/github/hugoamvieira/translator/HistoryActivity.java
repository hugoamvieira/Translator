package io.github.hugoamvieira.translator;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;


public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        SQLiteDatabase database = new DBHelper(this).getReadableDatabase();

        String[] dbCols = {
                DBContract.Translation._ID,
                DBContract.Translation.COLUMN_OG_TEXT,
                DBContract.Translation.COLUMN_LABEL_TRANSLATION_LANG,
                DBContract.Translation.COLUMN_TRANSLATION,
                DBContract.Translation.COLUMN_TRANSLATION_DATE
        };

        // Create cursor
        Cursor cursor = database.query(
                DBContract.Translation.TABLE_NAME,
                dbCols,
                null,
                null,
                null,
                null,
                null
        );

        LinearLayout ll = (LinearLayout) findViewById(R.id.activity_history);

        // Iterate through stuff in cursor
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String labelId = cursor.getString(cursor.getColumnIndex(DBContract.Translation._ID));
            String labelName = cursor.getString(cursor.getColumnIndex(DBContract.Translation.COLUMN_OG_TEXT));
            String translationLang = cursor.getString(cursor.getColumnIndex(DBContract.Translation.COLUMN_LABEL_TRANSLATION_LANG));
            String labelTranslation = cursor.getString(cursor.getColumnIndex(DBContract.Translation.COLUMN_TRANSLATION));
            String translationDate = cursor.getString(cursor.getColumnIndex(DBContract.Translation.COLUMN_TRANSLATION_DATE));

            TextView recordNo = new TextView(this);
            recordNo.setTextSize(24);
            recordNo.setText("Record No. " + labelId);
            ll.addView(recordNo);

            TextView tv = new TextView(this);
            tv.setTextSize(18);
            tv.setText("Original Text: " + labelName +
                    "\nTranslation Language: " + translationLang +
                    "\nTranslated Text: " + labelTranslation +
                    "\nTranslation Date: " + translationDate +
                    "\n\n");
            ll.addView(tv);
        }
    }
}
