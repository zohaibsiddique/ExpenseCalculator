package com.example.zohaibsiddique.expensecalculator;


import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zohaib Siddique on 12/08/2016.
 */
public class TypeFragmentRight extends ListFragment implements AdapterView.OnItemClickListener{

    ArrayList<String> arrayList;
    DB db;
    getDataFromTypeFragment getData;
    View view;
    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> selectedItems;
    SessionManager sessionManager;
    List<String> list = new ArrayList<>();
    List<String> positionsList;
    final String PREFERENCES_FILTER = "filter";
    final String KEY_PREFERENCES = "arrayList";

    public interface getDataFromTypeFragment{
        void getDataFromTypeFragment(ArrayList<String> data);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            getData = (getDataFromTypeFragment) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.type_list_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        arrayList = new ArrayList<>();
        db = new DB(getActivity());
        listView = (ListView) view.findViewById(android.R.id.list);
        sessionManager = new SessionManager();

        viewTypes();

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, arrayList);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);

        SharedPreferences editor = getActivity().getSharedPreferences(PREFERENCES_FILTER, Context.MODE_PRIVATE);
        if(editor.contains(KEY_PREFERENCES)) {
            if(getState().isEmpty()) {
                Utility.shortToast(getActivity(), String.valueOf("state empty"));
            } else {
                list = getState();
                if(list.isEmpty()) {

                } else {
                    for(int j = 0; j<list.size(); j++) {
                        listView.setItemChecked(Integer.valueOf(list.get(j)), true);
                    }
                }
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        positionsList = new ArrayList<>();
        selectedItems = new ArrayList<>();
        int position;
        for (int j = 0; j < checked.size(); j++) {
            position = checked.keyAt(j);
            if (checked.valueAt(j)) {
                selectedItems.add(adapter.getItem(position));
                positionsList.add(String.valueOf(position));
            }

        }
        getData.getDataFromTypeFragment(selectedItems);

        saveState();

    }



    private void viewTypes() {
        try {
            Cursor cursor = db.selectMainType();
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {
                Toast.makeText(getActivity(), "Empty list", Toast.LENGTH_SHORT).show();
            } else {
                for (int i = 0; i < cursor.getCount(); i++) {
                    arrayList.add(cursor.getString(cursor.getColumnIndex(db.NAME_MAIN_TYPE)));
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.d("viewTypes", " failed " + e.getMessage());
        }
    }

    public void saveState() {
        sessionManager.setPreferences(getActivity(), PREFERENCES_FILTER, KEY_PREFERENCES, positionsList);
    }

    public List<String> getState() {
        return sessionManager.getPreferences(getActivity(), PREFERENCES_FILTER, KEY_PREFERENCES);
    }
}

