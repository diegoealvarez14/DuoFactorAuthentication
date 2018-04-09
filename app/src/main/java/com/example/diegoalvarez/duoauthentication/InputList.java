package com.example.diegoalvarez.duoauthentication;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by harmanthind on 4/9/18.
 */

public class InputList extends ArrayAdapter<UserInput> {
    private Activity context;
    private List<UserInput> userInputList;

    public InputList(Activity context, List<UserInput> userInputList){
        super(context, R.layout.list_layout, userInputList);
        this.context = context;
        this.userInputList = userInputList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);
        TextView textViewApp = (TextView) listViewItem.findViewById(R.id.textViewApp);
        TextView textViewUser = (TextView) listViewItem.findViewById(R.id.textViewUser);
        TextView textViewPass = (TextView) listViewItem.findViewById(R.id.textViewPass);

        UserInput userInput = userInputList.get(position);
        textViewApp.setText(userInput.getAppName());
        textViewUser.setText(userInput.getUserName());
        textViewPass.setText(userInput.getPassword());



        return listViewItem;

    }
}
