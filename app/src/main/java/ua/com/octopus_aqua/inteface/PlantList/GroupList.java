package ua.com.octopus_aqua.inteface.PlantList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import ua.com.octopus_aqua.database.CustomAdapter;
import ua.com.octopus_aqua.database.PlantBaseHandler;
import ua.com.octopus_aqua.inteface.ListViewFragment;
import ua.com.octopus_aqua.inteface.PlantInfoActivity;
import ua.com.octopus_aqua.inteface.R;

class GroupList {

    static String ID = "id";

    View view;
    ListView listView;
    TextView textView;

    boolean listGone = false;

    GroupList(String group, final ListViewFragment fragment) {
        final Context context = fragment.getActivity();

        view = LayoutInflater.from(context).inflate(R.layout.list_group, null);

        textView = (TextView) view.findViewById(R.id.textGroupName);
        textView.setText(group);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listGone) {
                    listView.setVisibility(View.VISIBLE);
                    listGone = false;
                } else {
                    listView.setVisibility(View.GONE);
                    listGone = true;
                }
                listView.requestLayout();
            }
        });

        listView = (ListView) view.findViewById(R.id.listGroup);
        Cursor cursor = PlantBaseHandler.getInstance().getCursorForGroup(group);
        CustomAdapter adapter = new CustomAdapter(context, cursor);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("MY_TAG", "onItemClick: id=" + id);
                Intent intent = new Intent(context, PlantInfoActivity.class);
                intent.putExtra(ID, id);    //passing id to PlantInfo
                fragment.startActivityForResult(intent, ListViewFragment.INFO_PLANT);
            }
        });

        expandListsLength();
    }

    private void expandListsLength(){
        //without method only one row is displayed

        int totalHeight = 0;
        for(int i=0;i< listView.getCount();i++){
            View listItem = listView.getAdapter().getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams listParams = listView.getLayoutParams();
        listParams.height = totalHeight + (listView.getDividerHeight() * (listView.getCount() - 1));
        listView.setLayoutParams(listParams);
        listView.requestLayout();
    }


    View getView() {
        return view;
    }
}
