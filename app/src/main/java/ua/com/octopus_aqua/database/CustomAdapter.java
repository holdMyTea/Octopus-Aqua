package ua.com.octopus_aqua.database;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ua.com.octopus_aqua.inteface.R;
import ua.com.octopus_aqua.inteface.image_making.ImageMakerActivity;

public class CustomAdapter extends CursorAdapter {
    //the only reason for using this adapter instead of SimpleCursorAdapter is to supply def pic's
    //'cause ImageMakerActivity.getBitmap(picPath)

    public CustomAdapter(Context context, Cursor c){
        super(context,c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View v, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) v.findViewById(R.id.imageView);

        TextView textName = (TextView) v.findViewById(R.id.textName);
        TextView textType = (TextView) v.findViewById(R.id.textType);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(PlantBaseHandler.NAME_COLUMN));
        String type = cursor.getString(cursor.getColumnIndexOrThrow(PlantBaseHandler.TYPE_COLUMN));
        String picPath = cursor.getString(cursor.getColumnIndexOrThrow(PlantBaseHandler.PIC_COLUMN));

        Log.d("MY_TAG", "ADAPTER GOT: " + name + " " + type + " " + picPath);
        Log.d("MY_TAG", cursor.getString(cursor.getColumnIndexOrThrow(PlantBaseHandler.GROUP_COLUMN))+" has rows: "+cursor.getCount());

        textName.setText(name);
        textType.setText(type);
        imageView.setImageBitmap(ImageMakerActivity.getBitmap(picPath));
        cursor.moveToNext();
    }
}