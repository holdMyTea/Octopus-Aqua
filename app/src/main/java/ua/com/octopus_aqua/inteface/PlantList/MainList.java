package ua.com.octopus_aqua.inteface.PlantList;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import ua.com.octopus_aqua.database.PlantBaseHandler;
import ua.com.octopus_aqua.inteface.ListViewFragment;

public class MainList {

    GroupList[] lists;
    ListView listView;
    ArrayAdapter adapter;
    ListViewFragment fragment;

    public MainList(ListViewFragment fragment){
        this.fragment = fragment;
        updateList();
    }

    public void updateList(){
        listsFromBase(fragment);
        adapter = new MainAdapter(fragment.getActivity(),new ArrayList<>(Arrays.asList(lists)));
        fragment.getListView().setAdapter(adapter);
    }

    void listsFromBase(ListViewFragment fragment) {
        Set set = PlantBaseHandler.getInstance().groupsAsSet();

        String[] groups = (String[]) set.toArray(new String[set.size()]);
        lists = new GroupList[groups.length];
        String log = "";
        for (int i = 0; i < groups.length; i++) {
            lists[i] = new GroupList(groups[i],fragment);
            log = log + groups[i] + ", ";
        }
        Log.d("MY_TAG", "Groups: "+log);
    }

    public ListView getListView() {
        return listView;
    }

    private class MainAdapter extends ArrayAdapter<GroupList>{
        MainAdapter(Context context, ArrayList<GroupList> arr){
            super(context,0,arr);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItem(position).getView();
        }
    }
}
