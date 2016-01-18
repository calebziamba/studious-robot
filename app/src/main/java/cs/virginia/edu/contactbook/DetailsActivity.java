package cs.virginia.edu.contactbook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class DetailsActivity extends AppCompatActivity {
    private ImageView picture;
    private Contact contact;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // retrieves the contact object from the ContactListActivity, stores in contact

        Intent i = getIntent();
        contact = (Contact) i.getSerializableExtra("info");

        // Begin a thread to retrieve the detailed info
        new DetailedInfoThread(DetailsActivity.this, contact.getDetailsURL()).execute();


        picture = (ImageView) findViewById(R.id.bigPicture);

        TextView name = (TextView) findViewById(R.id.name_Details);
        name.setText(contact.getName());
        TextView comp = (TextView) findViewById(R.id.company);
        comp.append(contact.getCompany());
        if (!contact.getPhone().getWork().equals("")) {
            TextView phoneTypes = (TextView) findViewById(R.id.phone_types);
            phoneTypes.append("work\n");
        }
        if (!contact.getPhone().getMobile().equals("")) {
            TextView phoneTypes = (TextView) findViewById(R.id.phone_types);
            phoneTypes.append("mobile\n");
        }
        if (!contact.getPhone().getHome().equals("")) {
            TextView phoneTypes = (TextView) findViewById(R.id.phone_types);
            phoneTypes.append("home\n");
        }

        TextView numbers = (TextView) findViewById(R.id.phoneList);
        numbers.setText(contact.getPhone().toString());
    }

    private class DetailedInfoThread extends AsyncTask<String, Void, Boolean> {
        private String url;
        private ProgressDialog dialog;

        public DetailedInfoThread(Activity activity, String _url) {
            this.url = _url;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Fetching extra contact info");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                parseJSON();
            } catch (MalformedURLException e) {
            } catch (IOException e) {
            }
            return true;
        }

        protected void onPostExecute(final Boolean success) {
            // now that the extra info is gotten, can place it all into the relevant views
            if (dialog.isShowing())
                dialog.dismiss();


            picture.setImageBitmap(bitmap);
            if (contact.getHomeAddr() != null) {
                TextView addrTitle = (TextView) findViewById(R.id.addr_title);
                addrTitle.setText("Address");
                TextView addr = (TextView) findViewById(R.id.addr);
                addr.setText(contact.getHomeAddr().toString());
            }
            if (contact.getBirthdate() != null) {
                TextView birthTitle = (TextView) findViewById(R.id.birth_title);
                birthTitle.setText("Birthdate");
                TextView birth = (TextView) findViewById(R.id.birth);
                birth.setText(contact.getBirthdate());
            }
            if (contact.getWebsite() != null) {
                TextView webTitle = (TextView) findViewById(R.id.web_title);
                webTitle.setText("Website");
                TextView web = (TextView) findViewById(R.id.web);
                web.setText(contact.getWebsite());
            }

        }


        private void parseJSON() throws MalformedURLException, IOException {
            JsonFactory factory = new JsonFactory();
            JsonParser parser = factory.createParser(new URL(url));
            while (!parser.isClosed()) {

                JsonToken token = parser.nextToken();
                if (JsonToken.FIELD_NAME.equals(token)) {
                    switch (parser.getCurrentName()) {
                        case "largeImageURL":
                            //access the URL and store into a bitmap
                            bitmap = BitmapFactory.decodeStream((InputStream) new URL(parser.nextTextValue()).getContent());
                            break;
                        case "email":
                            contact.setEmail(parser.nextTextValue());
                            break;
                        case "website":
                            contact.setWebsite(parser.nextTextValue());
                            break;
                        case "address":
                            // next token is start of class
                            token = parser.nextToken();
                            String street = "", city = "", state = "", country = "", zip = "";
                            int lat = 0, lon = 0;
                            while (!JsonToken.END_OBJECT.equals(token)) {
                                token = parser.nextToken();
                                switch (parser.getCurrentName()) {
                                    case "street":
                                        street = parser.nextTextValue();
                                        break;
                                    case "city":
                                        city = parser.nextTextValue();
                                        break;
                                    case "state":
                                        state = parser.nextTextValue();
                                        break;
                                    case "country":
                                        country = parser.nextTextValue();
                                        break;
                                    case "zip":
                                        zip = parser.nextTextValue();
                                        break;
                                    case "latitude":
                                        lat = parser.nextIntValue(-1);
                                        break;
                                    case "longitude":
                                        lon = parser.nextIntValue(-1);
                                        break;
                                }
                            }
                            contact.setHomeAddr(new Address(street, city, state, country, zip, lon, lat));
                            break;
                    }
                }
            }
        }
    }

    private void createCategory(String title) {
        TextView genericTitle = new TextView(DetailsActivity.this);
        genericTitle.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        genericTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        genericTitle.setText(title);
        genericTitle.setTextColor(0x000000);
        LinearLayout myLayout = (LinearLayout) findViewById(R.id.linearLay);
        myLayout.addView(genericTitle);
    }

}
