package com.example.diegoalvarez.duoauthentication;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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

        Encryption decrypt = new Encryption();
        byte[] decryptedPasswordByteArray = Base64.decode(userInput.getPassword(), 1);
        String decryptedPassword;
        byte[] decryptedAppByteArray = Base64.decode(userInput.getAppName(), 1);
        String decryptedApp;
        byte[] decryptedUserNameByteArray = Base64.decode(userInput.getUserName(), 1);
        String decryptedUserName;
        try {
            decryptedPassword = decrypt.decryptAES(decryptedPasswordByteArray);
            decryptedApp = decrypt.decryptAES(decryptedAppByteArray);
            decryptedUserName = decrypt.decryptAES(decryptedUserNameByteArray);

            textViewApp.setText(decryptedApp);
            textViewUser.setText(decryptedUserName);
            textViewPass.setText(decryptedPassword);
            return listViewItem;
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException | NoSuchProviderException | KeyStoreException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IOException | InvalidAlgorithmParameterException | IllegalBlockSizeException | CertificateException e) {
            e.printStackTrace();
        }

        return listViewItem;

    }
}
