package com.cdevtech.getcontacts;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // The URL used to target the content provider
    static final Uri CONTENT_URL =
            Uri.parse("content://com.cdevtech.contactprovider.ContactProvider/cpcontacts");

    TextView contactsTextView;
    EditText deleteIdEditText, findIdEditText, addContactEditText;
    CursorLoader cursorLoader;

    // Provides access to other applications Content Providers
    ContentResolver resolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contactsTextView = (TextView) findViewById(R.id.contacts_text_view);
        
        deleteIdEditText = (EditText) findViewById(R.id.delete_id_edit_text);

        // As an example, for the Delete ID button, check for values in text field
        // as opposed to enable/disable
        /*
        final Button deleteIdButton = (Button) findViewById(R.id.delete_id_button);
        deleteIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                deleteIdButton.setEnabled(deleteIdEditText.getText().toString().trim().length() > 0);
            }
        });
        */
        
        findIdEditText = (EditText) findViewById(R.id.find_id_edit_text);
        final Button findIdButton = (Button) findViewById(R.id.find_id_button);
        findIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                findIdButton.setEnabled(findIdEditText.getText().toString().trim().length() > 0);
            }
        });

        addContactEditText = (EditText) findViewById(R.id.add_contact_edit_text);
        final Button addContactButton = (Button) findViewById(R.id.add_contact_button);
        addContactEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                addContactButton.setEnabled(addContactEditText.getText().toString().trim().length() > 0);
            }
        });

        resolver = getContentResolver();

        getContacts();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onShowContacts(View view) {
        getContacts();
    }

    public void onDeleteContact(View view) {
        // Get the ID of the contact to delete
        String idToDelete = deleteIdEditText.getText().toString();

        // Check for a value in the field as opposed to enable/disable of button
        if(TextUtils.isEmpty(idToDelete)) {
            deleteIdEditText.setError(getString(R.string.detele_id_missing_text));
            return;
        }

        // Use the resolver to delete ids by passing the content provider url
        // what you are targeting with the where and the string that replaces
        // the ? in the where clause
        long idDeleted = resolver.delete(
                CONTENT_URL,                    // URI: URL
                "id = ? ",                      // String: Where clause
                new String[]{idToDelete}        // String[]: Selection Arguments
        );

        if (idDeleted > 0) {
            deleteIdEditText.getText().clear();
            getContacts();
        } else {
            Toast.makeText(this, R.string.contact_not_deleted_text, Toast.LENGTH_SHORT).show();
        }
    }

    public void onFindContact(View view) {
        // The id we want to search for
        String idToFind = findIdEditText.getText().toString();

        // Holds the column data we want to retrieve
        String[] projection = new String[]{"id", "name"};

        // Pass the URL for Content Provider, the projection,
        // the where clause followed by the matches in an array for the ?
        // null is for sort order
        Cursor cursor = resolver.query(
                CONTENT_URL,                    // URI: URL
                projection, "id = ? ",          // String: Where clause
                new String[]{idToFind},         // String[]: Selection Arguments
                null);                          // String: The sort order for the returned rows

        String contact = "";

        // Cycle through our one result or print error
        if(cursor.moveToFirst()){

            String id = cursor.getString(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));

            contact = contact + id + " : " + name + "\n";

            findIdEditText.getText().clear();

        } else {
            Toast.makeText(this, "Contact Not Found", Toast.LENGTH_SHORT).show();
        }

        contactsTextView.setText(contact);
    }

    public void onAddContact(View view) {
        // Get the name to add
        String nameToAdd = addContactEditText.getText().toString();

        // Put in the column name and the value
        ContentValues values = new ContentValues();
        values.put("name", nameToAdd);

        // Insert the value into the Content Provider
        Uri result = resolver.insert(
            CONTENT_URL,            // URI: URL
            values                  // ContentValues: Initial value for new row
        );

        if (result != null) {
            addContactEditText.getText().clear();
            Toast.makeText(this, "Contact added", Toast.LENGTH_SHORT).show();
        }

        getContacts();
    }

    private void getContacts(){
        // Projection contains the columns we want
        String[] projection = new String[]{"id", "name"};

        // Pass the URL, projection and I'll cover the other options below
        Cursor cursor = resolver.query(
                CONTENT_URL,        // URI: The content URI of the cpcontacts table
                projection,         // String[]: The columns to return for each row
                null,               // String: Selection criteria clause, e.g. where
                null,               // String[]: Selection criteria arguments
                null                // String: The sort order for the returned rows
        );

        String contactList = "";

        // Cycle through and display every row of data
        if(cursor.moveToFirst()){

            do{
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));

                contactList += id + " : " + name + "\n";

            }while (cursor.moveToNext());
        }

        contactsTextView.setText(contactList);
    }

}
