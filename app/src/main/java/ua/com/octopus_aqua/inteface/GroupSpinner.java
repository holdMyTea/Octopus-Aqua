package ua.com.octopus_aqua.inteface;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;

import ua.com.octopus_aqua.database.PlantBaseHandler;
import ua.com.octopus_aqua.database.PlantRow;

class GroupSpinner {
    Context context;

    Spinner spinner;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;

    GroupSpinner(Context context, Spinner spinner){
        this.spinner = spinner;
        this.context = context;

        makeSpinner();
    }

    void makeSpinner(){
        list = new ArrayList<>();
        list.add("New group");
        list.addAll(PlantBaseHandler.getInstance().groupsAsSet());
        list.add(PlantRow.NO_GROUP);

        makeAdapter();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(id == 0){
                    doNewGroup();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setSelection(list.indexOf(PlantRow.NO_GROUP));
            }
        });

        spinner.setSelection(list.indexOf(PlantRow.NO_GROUP));
    }

    private void doNewGroup(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.new_group);
        builder.setMessage(R.string.input_group);

        final EditText editText = new EditText(context);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        builder.setView(editText);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = editText.getText().toString();
                //absence of '!' took 3 hours
                if(! str.isEmpty()){
                    list.add(1,str);
                    makeAdapter();
                    spinner.setSelection(1);
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                spinner.setSelection(list.indexOf(PlantRow.NO_GROUP));
                dialog.cancel();
            }
        });

        builder.setCancelable(true);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                spinner.setSelection(list.indexOf(PlantRow.NO_GROUP));
            }
        });

        builder.show();
    }

    private void makeAdapter(){
        adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    void setSelection(String group){
        spinner.setSelection(list.indexOf(group));
    }

    String getSelection(){
        return list.get((int)spinner.getSelectedItemId());
    }

    Spinner getSpinner() {
        return spinner;
    }
}
