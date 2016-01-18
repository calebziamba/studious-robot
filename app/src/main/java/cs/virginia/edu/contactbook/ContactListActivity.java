package cs.virginia.edu.contactbook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {
    private MyCustomAdapter adapter;
    ListView listView;
    ArrayList<Contact> listOfContacts;
    Bitmap bitmap;
    ProgressDialog dialog;
    SparseArray<Bitmap> imageStore = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // a worker thread to retrieve all of the primary info for the contacts
        new ProgressTask(ContactListActivity.this).execute();

        listOfContacts = new ArrayList<Contact>();

        listView = (ListView) findViewById(R.id.theListView);

        /* Here, the click listener will respond to the specific list items, allowing to send
        * item-specific information to the details activity
        */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact selectedEntry = adapter.get(position);
                Intent beginDetailsActivity = new Intent(ContactListActivity.this, DetailsActivity.class);
                // Because Contact and all of its fields are serializable or primitive its
                // data can be sent via one intents
                beginDetailsActivity.putExtra("info", selectedEntry);
                startActivity(beginDetailsActivity);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
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

    private class ProgressTask extends AsyncTask<String, Void, Boolean> {
        private Context context;

        public ProgressTask(Activity act) {
            context = act;
            dialog = new ProgressDialog(context);
        }


        @Override
        protected Boolean doInBackground(String... params) {
            String url = "https://solstice.applauncher.com/external/contacts.json";
            try {
                parseJSON(url);
            } catch (MalformedURLException e) {
            } catch (IOException e) {
            }
            return true;
        }

        // This uses the Jackson package
        private void parseJSON(String url) throws MalformedURLException, IOException {
            JsonFactory factory = new JsonFactory();
            JsonParser parser = factory.createParser(new URL(url));
            Contact contact = new Contact();
            while (!parser.isClosed()) {
                JsonToken token = parser.nextToken();
                // if token in null, then at end of file
                if (token == null)
                    break;

                /* The following if statement and switch parse the data by scanning for
                *  every potential field and applying the appropriate value to the contact
                *  until the end of class token is found, at which point the contact is added
                *  to the ArrayList of contacts
                */
                if (JsonToken.FIELD_NAME.equals(token)) {
                    // the parser found a field, identify the field then save its value
                    switch (parser.getCurrentName()) {
                        case "employeeId":
                            contact.setId(parser.nextIntValue(1));
                            break;
                        case "name":
                            contact.setName(parser.nextTextValue());
                            break;
                        case "company":
                            contact.setCompany(parser.nextTextValue());
                            break;
                        case "detailsURL":
                            contact.setDetailsURL(parser.nextTextValue());
                            break;
                        case "smallImageURL":
                            // instead of saving the URL we simply read and decode it into a bitmap
                            // and store it in the imageStore list, which will be used in onPostExecute
                            // in order to match the image with appropriate contact
                            try {
                                bitmap = BitmapFactory.decodeStream((InputStream) new URL(parser.nextTextValue()).getContent());
                                imageStore.put(contact.getId(), bitmap);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            break;
                        case "birthdate":
                            contact.setBirthdate(Long.parseLong(parser.nextTextValue()));
                            break;
                        case "phone":
                            // following phone is the start of an object, so bypass that to get to #s
                            token = parser.nextToken();
                            while (!JsonToken.END_OBJECT.equals(token)) {
                                token = parser.nextToken();
                                switch (parser.getCurrentName()) {
                                    case "work":
                                        contact.getPhone().setWork(parser.nextTextValue());
                                        break;
                                    case "home":
                                        contact.getPhone().setHome(parser.nextTextValue());
                                        break;
                                    case "mobile":
                                        contact.getPhone().setMobile(parser.nextTextValue());
                                        break;
                                }
                            }
                    }
                } else if (JsonToken.END_OBJECT.equals(token)) {
                    listOfContacts.add(contact);
                    contact = new Contact();
                }
            }

        }

        // causes a loading message to appear
        protected void onPreExecute() {
            dialog.setMessage("Retrieving Contact Info");
            dialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Collections.sort(listOfContacts);
            adapter = new MyCustomAdapter(ContactListActivity.this, listOfContacts);
            listView.setAdapter(adapter);
        }


    }

    public class MyCustomAdapter extends ArrayAdapter<Contact> {
        Context myContext;
        List<Contact> contactList;

        public MyCustomAdapter(Context context, List<Contact> theList) {
            super(context, R.layout.contact_list_item, theList);
            this.myContext = context;
            this.contactList = theList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            // LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Contact entry = contactList.get(position);

            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.contact_list_item, parent, false);
            }

            //Sets the Name view of the contact
            TextView textView = (TextView) row.findViewById(R.id.text_Name);
            textView.setText(entry.getName());

            //Sets the Phone # view of the contact
            textView = (TextView) row.findViewById(R.id.text_Phone1);
            textView.setText(entry.getPhone().getHome());

            //Sets the small image view of the contact
            ImageView imgView = (ImageView) row.findViewById(R.id.img);
            imgView.setImageBitmap(imageStore.get(entry.getId()));

            return row;
        }

        public Contact get(int position) {
            return contactList.get(position);
        }
    }
}
