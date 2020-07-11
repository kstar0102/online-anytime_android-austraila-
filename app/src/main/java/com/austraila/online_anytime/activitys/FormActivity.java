package com.austraila.online_anytime.activitys;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.austraila.online_anytime.Common.Common;
import com.austraila.online_anytime.Common.CustomScrollview;
import com.austraila.online_anytime.LocalManage.ElementDatabaseHelper;
import com.austraila.online_anytime.LocalManage.ElementOptionDatabaseHelper;
import com.austraila.online_anytime.LocalManage.ElementValueDatabaeHelper;
import com.austraila.online_anytime.R;
import com.austraila.online_anytime.Common.AddPhotoBottomDialogFragment;
import com.austraila.online_anytime.activitys.signature.SignatureView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.austraila.online_anytime.activitys.cameraActivity.CameraActivity.Image_Capture_Code;


public class FormActivity extends AppCompatActivity implements View.OnClickListener  {
    LinearLayout linearLayout;
    LinearLayout buttonsLayout;
    DatePickerDialog picker;
    SignatureView signatureView;
    Bitmap photo;
    CustomScrollview customScrollview;
    Cursor cursor;
    static public String elementCameraId;
    String  formid, formDes, formtitle, max, emailElementid, signauterElementid
            , numberElementid, singleElementid, dateElementid
            , phone1, phone2, phone3, phoneElementid
            , price1, price2, priceElemnetid
            ,firstnameElementid, secondnameElementid
            , addressElement1, addressElement2, addressElement3, addressElement4, addressElement5
            , textareaElemnet, timeElemntid, webElementid;

    private SQLiteDatabase db,ODb,VDb;
    int ss = 0;
    private SQLiteOpenHelper openHelper,ElementOptionopenHelper, ElementValueopenHeloer;
    ArrayList<String> data = new ArrayList<String>();
    public int checkpage = 1;
    TextView next_btn;

    Map<String, String> element_data = new HashMap<String, String>();
    static Map<String, Bitmap> elementPhotos = new HashMap<String, Bitmap>();

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formtest);

        getSupportActionBar().hide();

        //Local database define.
        openHelper = new ElementDatabaseHelper(this);
        ElementOptionopenHelper = new ElementOptionDatabaseHelper(this);
        ElementValueopenHeloer = new ElementValueDatabaeHelper(this);
        db = openHelper.getReadableDatabase();
        ODb = ElementOptionopenHelper.getReadableDatabase();
        VDb = ElementValueopenHeloer.getWritableDatabase();

        //define the custom Scroll.
        customScrollview = (CustomScrollview) findViewById(R.id.scrollmain);
        customScrollview.setEnableScrolling(true);

        next_btn = findViewById(R.id.next_textBtn);

        //define the main Layout
        linearLayout = findViewById(R.id.linear_layout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        //get value from other activity
        Intent intent = getIntent();
        formid = getIntent().getStringExtra("id");
        formDes = getIntent().getStringExtra("des");
        formtitle = getIntent().getStringExtra("title");
        String camera = intent.getStringExtra("camera");
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("photoImage");

        //get camera data.
        if (elementCameraId != null){
            elementPhotos.put(elementCameraId, bitmap);
        }

        if(camera != null){
            Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cInt,Image_Capture_Code);
        }

        //go back button
        TextView backTextView = findViewById(R.id.back_textview);
        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //get max pagenumber.
        ArrayList<String> groupkeyList = new ArrayList<String>();
        cursor = db.rawQuery("SELECT *FROM " + ElementDatabaseHelper.ElEMENTTABLE_NAME + " WHERE " + ElementDatabaseHelper.ECOL_11 + "=?", new String[]{formid});
        if (cursor.moveToFirst()){
            do{
                String keydate = cursor.getString(cursor.getColumnIndex("element_page_number"));
                if(!groupkeyList.contains(keydate)){
                    groupkeyList.add(keydate);
                }else{
                    continue;
                }
            }while(cursor.moveToNext());
        }
        cursor.close();

        max = groupkeyList.get(0);
        for (int i = 1; i < groupkeyList.size(); i++) {
            if (Integer.parseInt(groupkeyList.get(i)) > Integer.parseInt(max)) {
                max = groupkeyList.get(i);
            }
        }

        //show the element.
        showElement(checkpage);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showElement(int i) {
        //Go top page on scrollview
        customScrollview.fullScroll(CustomScrollview.FOCUS_UP);

        //make the Page title
        setTextTitle();

        // get value selected page from local database.
        cursor = db.rawQuery("SELECT *FROM " + ElementDatabaseHelper.ElEMENTTABLE_NAME + " WHERE " + ElementDatabaseHelper.ECOL_11 + "=? AND " + ElementDatabaseHelper.ECOL_7 + "=?", new String[]{formid, String.valueOf(i)});

        //UI dynamic generate
        if (cursor.moveToFirst()){
            do{
                data.add(cursor.getString(cursor.getColumnIndex("element_type")));
                switch (cursor.getString(cursor.getColumnIndex("element_type"))){
                    case "number":
                        NumberLint(cursor.getString(cursor.getColumnIndex("element_title")),cursor.getString(cursor.getColumnIndex("element_id")));
                        break;
                    case "europe_date":
                        DateLint(cursor.getString(cursor.getColumnIndex("element_title")),cursor.getString(cursor.getColumnIndex("element_id")));
                        break;
                    case "file":
                        fileUpload(cursor.getString(cursor.getColumnIndex("element_title")),cursor.getString(cursor.getColumnIndex("element_id")));
                        break;
                    case "email":
                        emailLine(cursor.getString(cursor.getColumnIndex("element_title")),cursor.getString(cursor.getColumnIndex("element_id")));
                        break;
                    case "money":
                        PriceLint(cursor.getString(cursor.getColumnIndex("element_title")),cursor.getString(cursor.getColumnIndex("element_id")));
                        break;
                    case "text":
                        SingleLineTest(cursor.getString(cursor.getColumnIndex("element_title")),cursor.getString(cursor.getColumnIndex("element_id")));
                        break;
                    case "signature":
                        SignatureMainLayout(cursor.getString(cursor.getColumnIndex("element_title")),cursor.getString(cursor.getColumnIndex("element_id")));
                        break;
                    case "simple_name":
                        NameLint(cursor.getString(cursor.getColumnIndex("element_title")),cursor.getString(cursor.getColumnIndex("element_id")));
                        break;
                    case "media":
                        MediaLint(cursor.getString(cursor.getColumnIndex("element_title")),cursor.getString(cursor.getColumnIndex("element_id")));
                        break;
                    case "phone":
                        PhoneLint(cursor.getString(cursor.getColumnIndex("element_title")),cursor.getString(cursor.getColumnIndex("element_id")));
                        break;
                    case "date":
                        DateLint(cursor.getString(cursor.getColumnIndex("element_title")),cursor.getString(cursor.getColumnIndex("element_id")));
                        break;
                    case "select":
                        DropDown(cursor.getString(cursor.getColumnIndex("element_title")), cursor.getString(cursor.getColumnIndex("element_id")));
                        break;
                    case "checkbox":
                        CheckBoxes(cursor.getString(cursor.getColumnIndex("element_title")), cursor.getString(cursor.getColumnIndex("element_id")));
                        break;
                    case "radio":
                        MultipleChoice(cursor.getString(cursor.getColumnIndex("element_title")), cursor.getString(cursor.getColumnIndex("element_id")));
                        break;
                    case "time":
                        TimeLint(cursor.getString(cursor.getColumnIndex("element_title")),cursor.getString(cursor.getColumnIndex("element_id")));
                        break;
                    case "url":
                        WebSiteLint(cursor.getString(cursor.getColumnIndex("element_title")),cursor.getString(cursor.getColumnIndex("element_id")));
                        break;
                    case "textarea":
                        ParagraphText(cursor.getString(cursor.getColumnIndex("element_title")),cursor.getString(cursor.getColumnIndex("element_id")));
                        break;
                    case "page_break":
                        page_break(i,cursor.getString(cursor.getColumnIndex("element_id")));
                        break;
                    case "address":
                        AddressLint(cursor.getString(cursor.getColumnIndex("element_title")), cursor.getInt(cursor.getColumnIndex("element_address_hideline2")),cursor.getString(cursor.getColumnIndex("element_id")));
                        break;
                    case "matrix":
                        matrixLint(cursor.getString(cursor.getColumnIndex("element_title")), cursor.getString(cursor.getColumnIndex("element_guidelines")), cursor.getString(cursor.getColumnIndex("element_id")));
                        break;
                    case "section":
                        SectionBreak(cursor.getString(cursor.getColumnIndex("element_title")), cursor.getString(cursor.getColumnIndex("element_guidelines")));
                }
            }while(cursor.moveToNext());
        }
        cursor.close();

        // get end page. submit button.
        if(i == Integer.parseInt(max)){
            submitButton();
            next_btn.setText("Submit");
            next_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FormActivity.this, SuccessActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void matrixLint(String title, String guidelines, final String id) {
        //get value for matrix from local database
        ArrayList<String> matrixList = new ArrayList<String>();
        Cursor cursor = ODb.rawQuery("SELECT *FROM " + ElementOptionDatabaseHelper.OPTIONTABLE_NAME + " WHERE " + ElementOptionDatabaseHelper.OCOL_2 + "=? AND " + ElementOptionDatabaseHelper.OCOL_3 + "=?" , new String[]{formid, id});
        if(cursor.moveToFirst()){
            do{
                String data = cursor.getString(cursor.getColumnIndex("OOption"));
                matrixList.add(data);
            }while (cursor.moveToNext());

        }
        cursor.close();

        //define Layout for matrix
        LinearLayout.LayoutParams matrixParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        matrixParams.setMargins(50,20,50,5);
        LinearLayout matrixLayout = new LinearLayout(this);
        matrixLayout.setOrientation(LinearLayout.HORIZONTAL);
        matrixLayout.setLayoutParams(matrixParams);

        // define the title field on matrix
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(500, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.setMargins(0,0,10,0);
        LinearLayout titlelayout = new LinearLayout(this);
        titlelayout.setOrientation(LinearLayout.VERTICAL);
        titlelayout.setWeightSum(1);
        titlelayout.setLayoutParams(titleParams);

        //define the radio group on matrix
        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(550, LinearLayout.LayoutParams.WRAP_CONTENT);
        itemParams.setMargins(10,0,0,0);
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setLayoutParams(itemParams);
        itemLayout.setWeightSum(1);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);

        matrixLayout.addView(titlelayout);
        matrixLayout.addView(itemLayout);

        TextView matrixTitle = new TextView(this);
        titleTextview(matrixTitle);

        //define the header department on matrix
        LinearLayout headLayout = new LinearLayout(this);
        LinearLayout.LayoutParams headLayoutParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        headLayoutParam.setMargins(50,20,50,10);
        headLayout.setLayoutParams(headLayoutParam);
        headLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout empty = new LinearLayout(this);
        LinearLayout headtitle = new LinearLayout(this);
        LinearLayout.LayoutParams emptyParam = new LinearLayout.LayoutParams(
                450, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        emptyParam.setMargins(10,0,0,0);
        empty.setLayoutParams(emptyParam);
        headLayout.addView(empty);
        headLayout.addView(headtitle);


        if(guidelines.isEmpty()){
            matrixTitle.setVisibility(View.GONE);
        }else {
            matrixTitle.setText(guidelines);
            linearLayout.addView(matrixTitle);
            linearLayout.addView(headLayout);

            for(int i = 0; i < matrixList.size(); i ++){
                TextView itemtext = new TextView(this);
                titleTextview(itemtext);
                itemtext.setTextSize(getResources().getDimension(R.dimen.textsize_normal));
                itemtext.setWidth(140);
                itemtext.setText(matrixList.get(i));
                headtitle.addView(itemtext);
            }
        }

        TextView titleText = new TextView(this);
        titleTextview(titleText);
        titleText.setTextSize(getResources().getDimension(R.dimen.textsize_normal));
        titleText.setText(title);
        titlelayout.addView(titleText);

        // set the radio group
        final RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams radiogroupparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        radiogroupparams.setMargins(50,5,10, 0);

        for (int i = 0; i < matrixList.size(); i ++){
            final RadioButton radioButtonView = new RadioButton(this);
            radioGroup.addView(radioButtonView, radiogroupparams);

            radioButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int idx = radioGroup.indexOfChild(radioButtonView);
                    element_data.put("element_" + id, String.valueOf(idx));
                    Toast.makeText(FormActivity.this, radioButtonView.getText().toString(), Toast.LENGTH_LONG).show();
                }
            });
        }

        itemLayout.addView(radioGroup);
        linearLayout.addView(matrixLayout);
    }

    private void AddressLint(String title, int address, String id) {
        //define the element id.
        final String addressid = id;
        TextView addressTitle = new TextView(this);

        //set the address title.
        titleTextview(addressTitle);
        addressTitle.setText(title);
        linearLayout.addView(addressTitle);

        //set the street Address.
        LinearLayout.LayoutParams streetaddressparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        streetaddressparams.setMargins(50,30,50,5);
        EditText streetEdit = new EditText(this);
        //set tag and check the value
        streetEdit.setTag("element_" + id + "_1");
        addressElement1 = "element_" + id + "_1";
        String addstr = element_data.get(addressElement1);
        if(addstr != null){
            streetEdit.setText(addstr);
        }
        EditTextview(streetEdit);

        TextView streettitle = new TextView(this);
        titleTextview(streettitle);
        streettitle.setTextSize(getResources().getDimension(R.dimen.textsize_normal));
        streettitle.setText("Street Address");

        LinearLayout stressaddress = new LinearLayout(this);
        stressaddress.setOrientation(LinearLayout.VERTICAL);
        stressaddress.addView(streetEdit);
        stressaddress.addView(streettitle);

        //set the line2 Address
        LinearLayout line2 = new LinearLayout(this);
        line2.setOrientation(LinearLayout.VERTICAL);

        EditText lineEdit = new EditText(this);
        EditTextview(lineEdit);

        TextView lineText = new TextView(this);
        titleTextview(lineText);
        lineText.setTextSize(getResources().getDimension(R.dimen.textsize_normal));
        lineText.setText("Address Line2");

        line2.addView(lineEdit);
        line2.addView(lineText);

        if(address == 1){
            linearLayout.addView(stressaddress);
        }else {
            lineEdit.setTag("element_" + id + "_2");
            addressElement2 = "element_" + id + "_2";
            String addlin = element_data.get(addressElement2);
            if(addlin != null){
                lineEdit.setText(addlin);
            }
            linearLayout.addView(stressaddress);
            linearLayout.addView(line2);
        }

        //set the city and state address
        LinearLayout.LayoutParams CityStateparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        CityStateparams.setMargins(0,15,10,5);
        LinearLayout CityState = new LinearLayout(this);
        CityState.setOrientation(LinearLayout.HORIZONTAL);
        CityState.setLayoutParams(CityStateparams);

        LinearLayout addresscity = new LinearLayout(this);
        LinearLayout addressstate = new LinearLayout(this);
        addressstate.setWeightSum(1);
        addresscity.setWeightSum(1);

        CityState.addView(addresscity);
        CityState.addView(addressstate);

        addresscity.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams cityparams = new LinearLayout.LayoutParams(
                530,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cityparams.setMargins(0,0,10,0);
        addresscity.setLayoutParams(cityparams);

        //set the city address
        EditText cityEdit = new EditText(this);
        EditTextview(cityEdit);
        cityEdit.setTag("element_" + id + "_3");
        addressElement3 = "element_" + id + "_3";
        String cityL = element_data.get(addressElement3);
        if(cityL != null){
            cityEdit.setText(cityL);
        }

        TextView cityText = new TextView(this);
        titleTextview(cityText);
        cityText.setTextSize(getResources().getDimension(R.dimen.textsize_normal));
        cityText.setText("City");

        addressstate.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams stateparma = new LinearLayout.LayoutParams(
                530,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        stateparma.setMargins(0,0,10,0);
        addressstate.setLayoutParams(stateparma);

        EditText stateEdit = new EditText(this);
        EditTextview(stateEdit);
        stateEdit.setTag("element_" + id + "_4");
        addressElement4 = "element_" + id + "_4";
        String stateL = element_data.get(addressElement4);
        if(stateL != null){
            stateEdit.setText(stateL);
        }

        TextView stateText = new TextView(this);
        titleTextview(stateText);
        stateText.setTextSize(getResources().getDimension(R.dimen.textsize_normal));
        stateText.setText("State/Province/Region");

        addresscity.addView(cityEdit);
        addresscity.addView(cityText);
        addressstate.addView(stateEdit);
        addressstate.addView(stateText);

        linearLayout.addView(CityState);

        LinearLayout postalCountry = new LinearLayout(this);
        postalCountry.setOrientation(LinearLayout.HORIZONTAL);
        postalCountry.setLayoutParams(CityStateparams);

        LinearLayout postal = new LinearLayout(this);
        postal.setOrientation(LinearLayout.VERTICAL);
        postal.setWeightSum(1);
        postal.setLayoutParams(cityparams);
        LinearLayout country = new LinearLayout(this);
        country.setOrientation(LinearLayout.VERTICAL);
        country.setWeightSum(1);
        country.setLayoutParams(stateparma);

        postalCountry.addView(postal);
        postalCountry.addView(country);

        EditText postalEdit = new EditText(this);
        TextView postalText = new TextView(this);
        EditTextview(postalEdit);
        postalEdit.setTag("element_" + id + "_5");
        addressElement5 = "element_" + id + "_5";
        String postL = element_data.get(addressElement5);
        if(postL != null){
            postalEdit.setText(postL);
        }
        titleTextview(postalText);
        postalText.setTextSize(getResources().getDimension(R.dimen.textsize_normal));
        postalText.setText("Postal/Zip Code");

        postal.addView(postalEdit);
        postal.addView(postalText);

        Common common = new Common();
        LinearLayout.LayoutParams dropdownParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        final Spinner countrydrop = new Spinner(this);
        dropdownParams.setMargins(50,0,50,0);
        countrydrop.setLayoutParams(dropdownParams);
        countrydrop.setBackground(getResources().getDrawable(R.drawable.editview_border));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_item, common.countryArray);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        countrydrop.setAdapter(adapter);
        countrydrop.setSelection(3);

        countrydrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // Notify the selected item text
                element_data.put("element_" + addressid + "_6", selectedItemText);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        TextView countryText = new TextView(this);
        titleTextview(countryText);
        countryText.setText("Country");
        countryText.setTextSize(getResources().getDimension(R.dimen.textsize_normal));
        country.addView(countrydrop);
        country.addView(countryText);

        linearLayout.addView(postalCountry);
    }

    private void submitButton() {
        Button submitBtn =new Button(this);
        LinearLayout.LayoutParams btnparams = new LinearLayout.LayoutParams(300,110);
        btnparams.setMargins(50,20,10,5);
        submitBtn.setBackground(getResources().getDrawable(R.drawable.btn_submit));
        submitBtn.setText("Submit");
        submitBtn.setTextColor(getResources().getColor(R.color.white_color));
        submitBtn.setLayoutParams(btnparams);
        linearLayout.addView(submitBtn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetElementValue();
                System.out.println(element_data.size());
                System.out.println(element_data);

        Intent intent = new Intent(FormActivity.this, SuccessActivity.class);
        intent.putExtra("FormId", formid);
        intent.putExtra("elementData", (Serializable) element_data);
        startActivity(intent);
            }
        });
    }

    private void setTextTitle() {
        TextView TitleTextvew = new TextView(this);
        TextView desTextview = new TextView(this);

        titleTextview(TitleTextvew);
        TitleTextvew.setTextSize(getResources().getDimension(R.dimen.textsize_title));


        LinearLayout.LayoutParams breakdesparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        breakdesparams.setMargins(50,0,50,0);
        titleTextview(desTextview);

        desTextview.setLayoutParams(breakdesparams);
        desTextview.setTextSize(getResources().getDimension(R.dimen.textsize_normal));

        linearLayout.addView(TitleTextvew);
        linearLayout.addView(desTextview);

        TitleTextvew.setText(Html.fromHtml(formtitle));
        desTextview.setText(Html.fromHtml(formDes));
    }

    private void WebSiteLint(String title, String id) {
        //define the element
        TextView webSiteTitle = new TextView(this);
        EditText websiteEdit = new EditText(this);

        // set the property
        titleTextview(webSiteTitle);
        webSiteTitle.setText(Html.fromHtml(title));
        EditTextview(websiteEdit);
        websiteEdit.setText("http://");
        websiteEdit.setTag("element_" + id);
        webElementid = "element_" + id;
        String webL = element_data.get(webElementid);
        if(webL != null){
            websiteEdit.setText(webL);
        }

        //add the element
        linearLayout.addView(webSiteTitle);
        linearLayout.addView(websiteEdit);
    }

    private void SignatureMainLayout(String title, String id) {
        //set signature title
        TextView signTitle = new TextView(this);
        titleTextview(signTitle);
        signTitle.setText(Html.fromHtml(title));

        LinearLayout signview = new LinearLayout(this);
        LinearLayout.LayoutParams signviewParma = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        signviewParma.setMargins(0,15,0,5);
        signview.setLayoutParams(signviewParma);
        signview.setOrientation(LinearLayout.VERTICAL);
        signview.setMinimumHeight(500);
        signview.setTag("element_" + id);
        signauterElementid = "element_" + id;


        this.buttonsLayout = this.buttonsLayout();
        this.signatureView = new SignatureView(this);
        signatureView.setBackground(getResources().getDrawable(R.drawable.editview_border));

        signatureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //The following code is to disable  scroll view on gesture touchListener.
                customScrollview.setEnableScrolling(false);
                return false;
            }});

        LinearLayout.LayoutParams signatureViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        signatureViewParams.setMargins(50,15,50,5);
        signatureView.setMinimumHeight(300);
        signatureView.setLayoutParams(signatureViewParams);
        signview.addView(buttonsLayout);
        signview.addView(signatureView);

        linearLayout.addView(signTitle);
        linearLayout.addView(signview);
    }

    private LinearLayout buttonsLayout() {
        // create the UI programatically
        LinearLayout linearLayout = new LinearLayout(this);
        Button saveBtn = new Button(this);
        Button clearBtn = new Button(this);

        // set orientation
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams buttonlayoutParm = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonlayoutParm.setMargins(50,0,10,0);
        linearLayout.setLayoutParams(buttonlayoutParm);

        // set texts, tags and OnClickListener
        saveBtn.setText("Save");
        saveBtn.setTag("Save");
        saveBtn.setOnClickListener(this);

        clearBtn.setText("Clear");
        clearBtn.setTag("Clear");
        clearBtn.setOnClickListener(this);

        linearLayout.addView(saveBtn);
        linearLayout.addView(clearBtn);

        // return the whoe layout
        return linearLayout;
    }

    @Override
    public void onClick(View v) {
        String tag = v.getTag().toString().trim();

        // save the signature
        if (tag.equalsIgnoreCase("save")) {
            elementPhotos.put(signauterElementid, this.signatureView.getSignature());
            this.saveImage(this.signatureView.getSignature());
            customScrollview.setEnableScrolling(true);
        }

        // empty the canvas
        else {
            this.signatureView.clearSignature();
            customScrollview.setEnableScrolling(true);
        }
    }

    final void saveImage(Bitmap signature) {

        String root = Environment.getExternalStorageDirectory().toString();

        // the directory where the signature will be saved
        File myDir = new File(root + "/saved_signature");

        // make the directory if it does not exist yet
        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        // set the file name of your choice
        String fname = "signature.png";

        // in our case, we delete the previous file, you can remove this
        File file = new File(myDir, fname);
        if (file.exists()) {
            file.delete();
        }

        try {

            // save the signature
            FileOutputStream out = new FileOutputStream(file);
            signature.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();

            Toast.makeText(FormActivity.this, "Signature saved.", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                Intent intent = new Intent(FormActivity.this, FormActivity.class);
                intent.putExtra("photoImage", bp);
                intent.putExtra("id", formid);
                intent.putExtra("des", formDes);
                intent.putExtra("title", formtitle);
                startActivity(intent);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(FormActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }

    // file exploer funtion
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void fileUpload(String title, final String id) {

        TextView filetitle = new TextView(this);
        titleTextview(filetitle);
        filetitle.setText(Html.fromHtml(title));

        //define the button.
        Button uploadbtn = new Button(this);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(500, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(50,25,50,10);
        uploadbtn.setLayoutParams(btnParams);
        uploadbtn.setBackground(getDrawable(R.drawable.btn_rounded));
        uploadbtn.setText("Select File");
        uploadbtn.setTextColor(getResources().getColor(R.color.white_color));
        uploadbtn.setTypeface(uploadbtn.getTypeface(), Typeface.BOLD);
        uploadbtn.setTextSize(getResources().getDimension(R.dimen.textsize_normal));
        linearLayout.addView(filetitle);
        linearLayout.addView(uploadbtn);

        //define the Imageview
        ImageView photoImage = new ImageView(this);
        LinearLayout.LayoutParams photoImageParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        photoImageParam.setMargins(50,10,50,5);
        photoImage.setMinimumHeight(500);
        photoImage.setLayoutParams(photoImageParam);

        photoImage.setTag("element_" + id);

        TextView photofilepath = new TextView(this);
        titleTextview(photofilepath);
        photofilepath.setTextSize(getResources().getDimension(R.dimen.textsize_normal));
        photofilepath.setVisibility(View.GONE);

        linearLayout.addView(photoImage);
        linearLayout.addView(photofilepath);

        photo = elementPhotos.get("element_" + id);
        if(photo != null){
            photoImage.setImageBitmap(photo);
        }

        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("id", formid);
                bundle.putString("formDes", formDes);
                bundle.putString("formtitle", formtitle);
                elementCameraId = "element_" +  id;
                AddPhotoBottomDialogFragment addPhotoBottomDialogFragment = AddPhotoBottomDialogFragment.newInstance();
                addPhotoBottomDialogFragment.setArguments(bundle);
                addPhotoBottomDialogFragment.show(getSupportFragmentManager(),"add_photo_dialog_fragment");
            }
        });

        Intent intent = getIntent();
        String getfile = intent.getStringExtra("filepath");

        if(getfile != null){
            photofilepath.setVisibility(View.VISIBLE);
            File file = new File(getfile);
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            photofilepath.setText(getfile);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void DateLint(String title, String id) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dateTime = dateFormat.format(date);

        final Calendar cldr = Calendar.getInstance();
        final int day = cldr.get(Calendar.DAY_OF_MONTH);
        final int month = cldr.get(Calendar.MONTH);
        final int year = cldr.get(Calendar.YEAR);

        //define the dateTitle
        TextView dateTitle = new TextView(this);
        dateTitle.setText(Html.fromHtml(title));
        titleTextview(dateTitle);
        linearLayout.addView(dateTitle);

        //define the date picker
        final EditText dateEditText = new EditText(this);
        EditTextview(dateEditText);
        dateEditText.setText(dateTime);
        dateEditText.setTag("element_" + id);
        dateElementid = "element_" + id;
        String dateL = element_data.get(dateElementid);
        if(dateL != null){
            dateEditText.setText(dateL);
        }

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker = new DatePickerDialog(FormActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dateEditText.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
        linearLayout.addView(dateEditText);
    }

    private void PhoneLint(String title, String id) {
        phoneElementid = "element_" + id;
        //define the phone title
        TextView phoneTitle = new TextView(this);
        titleTextview(phoneTitle);
        phoneTitle.setText(Html.fromHtml(title));
        linearLayout.addView(phoneTitle);

        //define the phone number LinearLayout edit
        LinearLayout phoneLinerLayout = new LinearLayout(this);
        phoneLinerLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams phoneLinerLayoutParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        phoneLinerLayoutParam.setMargins(55,5,50,0);
        phoneLinerLayout.setLayoutParams(phoneLinerLayoutParam);
        linearLayout.addView(phoneLinerLayout);

        // define the EditText and textview
        TextView lineText = new TextView(this);
        TextView lineText1 = new TextView(this);
        LinearLayout.LayoutParams lineParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lineParam.setMargins(1,5,1,0);
        lineText.setLayoutParams(lineParam);
        lineText1.setLayoutParams(lineParam);
        lineText.setText("-");
        lineText1.setText("-");
        lineText.setTextSize(getResources().getDimension(R.dimen.textsize_normal));
        lineText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        lineText1.setTextSize(getResources().getDimension(R.dimen.textsize_normal));
        lineText1.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

        //define the edittext
        EditText phoneNum1 = new EditText(this);
        EditText phoneNum2 = new EditText(this);
        EditText phoneNum3 = new EditText(this);
        LinearLayout.LayoutParams param3Num = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        param3Num.setMargins(10,0,10,0);

        EditTextview(phoneNum1);
        phoneNum1.setHint("###");
        phoneNum1.setLayoutParams(param3Num);
        phoneNum1.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(3);
        phoneNum1.setFilters(FilterArray);
        phoneNum1.setWidth(130);
        phoneNum1.setTag("element_" + id + "_1");
        phone1 = "element_" + id + "_1";
        String ph1L = element_data.get(phone1);
        if(ph1L != null){
            phoneNum1.setText(ph1L);
        }
        phoneLinerLayout.addView(phoneNum1);
        phoneLinerLayout.addView(lineText);

        EditTextview(phoneNum2);
        phoneNum2.setHint("###");
        phoneNum2.setLayoutParams(param3Num);
        phoneNum2.setInputType(InputType.TYPE_CLASS_NUMBER);
        phoneNum2.setFilters(FilterArray);
        phoneNum2.setWidth(130);
        phoneNum2.setTag("element_" + id + "_2");
        phone2 = "element_" + id + "_2";
        String ph2L = element_data.get(phone2);
        if(ph2L != null){
            phoneNum2.setText(ph2L);
        }
        phoneLinerLayout.addView(phoneNum2);

        phoneLinerLayout.addView(lineText1);
        EditTextview(phoneNum3);
        phoneNum3.setHint("####");
        phoneNum3.setTag("element_" + id + "_3");
        phone3 = "element_" + id + "_3";
        String ph3L = element_data.get(phone3);
        if(ph3L != null){
            phoneNum3.setText(ph3L);
        }
        phoneNum3.setLayoutParams(param3Num);
        phoneNum3.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputFilter[] FilterArray1 = new InputFilter[1];
        FilterArray1[0] = new InputFilter.LengthFilter(4);
        phoneNum3.setFilters(FilterArray1);
        phoneNum3.setWidth(180);
        phoneLinerLayout.addView(phoneNum3);

    }

    private void DropDown(String title, String id) {
        final String dropid = id;
        ArrayList<String> dropList = new ArrayList<String>();
        Cursor cursor = ODb.rawQuery("SELECT *FROM " + ElementOptionDatabaseHelper.OPTIONTABLE_NAME + " WHERE " + ElementOptionDatabaseHelper.OCOL_2 + "=? AND " + ElementOptionDatabaseHelper.OCOL_3 + "=?" , new String[]{formid, id});

        if(cursor.moveToFirst()){
            do{
                String data = cursor.getString(cursor.getColumnIndex("OOption"));
                dropList.add(data);
            }while (cursor.moveToNext());

        }
        cursor.close();

        //define the dropdown title
        TextView dropTitle = new TextView(this);
        titleTextview(dropTitle);
        dropTitle.setText(Html.fromHtml(title));
        linearLayout.addView(dropTitle);

        //define the spinner
        LinearLayout.LayoutParams dropdownParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        dropdownParams.setMargins(60,10,50,0);
        Spinner dropdown = new Spinner(this);
        dropdown.setLayoutParams(dropdownParams);
        dropdown.setBackground(getResources().getDrawable(R.drawable.editview_border));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_item, dropList);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        linearLayout.addView(dropdown);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                element_data.put("element_" + dropid, String.valueOf(position));
                // Notify the selected item text
//                Toast.makeText(getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void CheckBoxes(String title, final String id) {
        ArrayList<String> mylist = new ArrayList<String>();

        Cursor cursor = ODb.rawQuery("SELECT *FROM " + ElementOptionDatabaseHelper.OPTIONTABLE_NAME + " WHERE " + ElementOptionDatabaseHelper.OCOL_2 + "=? AND " + ElementOptionDatabaseHelper.OCOL_3 + "=?" , new String[]{formid, id});

        if(cursor.moveToFirst()){
            do{
                String data = cursor.getString(cursor.getColumnIndex("OOption"));
                mylist.add(data);
            }while (cursor.moveToNext());

        }
        cursor.close();

        //define the checkboxesTitle
        TextView checkboxesTitle = new TextView(this);
        titleTextview(checkboxesTitle);
        checkboxesTitle.setText(Html.fromHtml(title));
        linearLayout.addView(checkboxesTitle);

        LinearLayout.LayoutParams ParmsDescription = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        ParmsDescription.setMargins(50,10,50,0);

        for(int i = 0; i < mylist.size(); i ++){
            element_data.put("element_" + id + "_" + String.valueOf(i+1), "0");
            final CheckBox checkBox = new CheckBox(this);
            checkBox.setText(mylist.get(i));
            checkBox.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            checkBox.setTextSize(getResources().getDimension(R.dimen.textsize_normal));
            checkBox.setLayoutParams(ParmsDescription);
            final int finalI = i;
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    element_data.put("element_" + id + "_" + String.valueOf(finalI+1), "1");
                    Toast.makeText(FormActivity.this, checkBox.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
            linearLayout.addView(checkBox);
            //doi se. il hal lae.haw/na seng nat e.o. ne mu ja ju seng nae ni kka i jen mu sep ji ana.mam dae lo hae.sure.bye.
        }
    }

    private void NumberLint(String title, String id) {
        //define the element
        TextView numberTitle = new TextView(this);
        EditText numberEdit = new EditText(this);

        //set property the numberTitle
        titleTextview(numberTitle);
        numberTitle.setText(Html.fromHtml(title));
        linearLayout.addView(numberTitle);

        //set property the numberEdit
        EditTextview(numberEdit);
        numberEdit.setHint("Only number");
        numberEdit.setId(Integer.parseInt(id));
        numberEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        numberEdit.setTag("element_" + id);
        numberElementid = "element_" + id;
        String num = element_data.get(numberElementid);
        if(num != null){
            numberEdit.setText(num);
        }
        linearLayout.addView(numberEdit);
    }

     private void MediaLint(String title, String id) {
        //define the element
        TextView mediaTitle = new TextView(this);
        ImageView mediaImage = new ImageView(this);

        //set property the mediatitle
        mediaTitle.setText(Html.fromHtml(title));
        titleTextview(mediaTitle);

        linearLayout.addView(mediaTitle);

        //set property the media imageview
        LinearLayout.LayoutParams ParmsDescription = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        ParmsDescription.setMargins(50,0,50,5);
        mediaImage.setImageResource(R.drawable.app_logo);
        mediaImage.setLayoutParams(ParmsDescription);
        linearLayout.addView(mediaImage);
    }

    private void SectionBreak(String title, String des) {
        //define the element
        TextView breakTitle = new TextView(this);
        TextView breakdes = new TextView(this);

        //set property the breakTitle
        titleTextview(breakTitle);
        breakTitle.setText(Html.fromHtml(title));

        //set property the breakdes
        LinearLayout.LayoutParams breakdesparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        breakdesparams.setMargins(50,0,50,0);
        titleTextview(breakdes);
        breakdes.setText(Html.fromHtml(des));

        breakdes.setLayoutParams(breakdesparams);
        breakdes.setTextSize(getResources().getDimension(R.dimen.textsize_normal));

        linearLayout.addView(breakTitle);
        linearLayout.addView(breakdes);
    }

    private void PriceLint(String title, String id) {
        priceElemnetid = "element_" + id;
        //define elements
        TextView priceTitle = new TextView(this);
        TextView label1 = new TextView(this);
        EditText dollarsEditText = new EditText(this);
        EditText centEditText = new EditText(this);
        TextView label2 = new TextView(this);
        LinearLayout priceLinerlayout = new LinearLayout(this);

        //set property the priceLinerlayout
        LinearLayout.LayoutParams pricelinerlayoutparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        pricelinerlayoutparams.setMargins(50,0,0,0);
        priceLinerlayout.setOrientation(LinearLayout.HORIZONTAL);

        // set property the label1 and add to priceLinearLayout
        LinearLayout.LayoutParams label1params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        label1params.setMargins(50,0,5,0);
        label1.setText("$");
        label1.setTextSize(getResources().getDimension(R.dimen.textsize_normal));
        label1.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        label1.setLayoutParams(label1params);
        priceLinerlayout.addView(label1);

        //set property the dollerEditText and add to the priceLinearLayout
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10,0,10,0);
        EditTextview(dollarsEditText);
        dollarsEditText.setWidth(300);
        dollarsEditText.setHint("Dollars");
        dollarsEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        dollarsEditText.setLayoutParams(params);
        dollarsEditText.setTag("element_" + id + "_1");
        price1 = "element_" + id + "_1";
        String dol = element_data.get(price1);
        if(dol != null){
            dollarsEditText.setText(dol);
        }
        priceLinerlayout.addView(dollarsEditText);

        //set property the lable2 and add to priceLineatLayout
        LinearLayout.LayoutParams label2LayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        label2LayoutParams.setMargins(10,20,10,0);
        label2.setText(".");
        label2.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        label2.setTypeface(label2.getTypeface(), Typeface.BOLD);
        label2.setTextSize(getResources().getDimension(R.dimen.textsize_header));
        label2.setLayoutParams(label2LayoutParams);
        priceLinerlayout.addView(label2);

        //set property the centsEditText and add to priceLinerLayout
        EditTextview(centEditText);
        centEditText.setHint("Cents");
        centEditText.setWidth(250);
        centEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(2);
        centEditText.setFilters(FilterArray);
        centEditText.setLayoutParams(params);
        centEditText.setTag("element_" + id + "_2");
        price2 = "element_" + id + "_2";
        String cen = element_data.get(price2);
        if(cen != null){
            centEditText.setText(cen);
        }
        priceLinerlayout.addView(centEditText);

        //set property the price Title
        priceTitle.setText(Html.fromHtml(title));
        titleTextview(priceTitle);

        // add the element
        linearLayout.addView(priceTitle);
        linearLayout.addView(priceLinerlayout);
    }

    private void page_break(final int showcheckbtn, final String id){
        LinearLayout btnlinearLayout =new LinearLayout(this);
        LinearLayout.LayoutParams btnLinerParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        btnLinerParam.setMargins(40,5,40,5);
        btnlinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        btnlinearLayout.setLayoutParams(btnLinerParam);


        Button nextbutton = new Button(this);
        Button prebutton =new Button(this);
        LinearLayout.LayoutParams btnparams = new LinearLayout.LayoutParams(300, 120);
        btnparams.setMargins(10,20,10,5);

        nextbutton.setText("Continue");
        nextbutton.setLayoutParams(btnparams);


        prebutton.setText("Previous");
        prebutton.setLayoutParams(btnparams);

        btnlinearLayout.addView(nextbutton);
        btnlinearLayout.addView(prebutton);

        if(showcheckbtn < 2 ){
            prebutton.setVisibility(View.GONE);
        }

        next_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                linearLayout.removeAllViewsInLayout();
                showElement(showcheckbtn +1 );
            }
        });

        nextbutton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                GetElementValue();
                linearLayout.removeAllViewsInLayout();
                showElement(showcheckbtn +1 );
                System.out.println(elementPhotos);
            }

        });

        prebutton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                linearLayout.removeAllViewsInLayout();
                showElement(showcheckbtn - 1 );
            }
        });
        linearLayout.addView(btnlinearLayout);
    }

    private void TimeLint(String title, String id) {
        //define the timepicker and timetitle
        TimePicker timePicker = new TimePicker(this);
        TextView timeTitle = new TextView(this);
        final EditText editText = new EditText(this);
        EditTextview(editText);
        editText.setTag("element_" + id);
        timeElemntid = "element_" + id;

        String timeL = element_data.get(timeElemntid);
        if(timeL != null){
            editText.setText(timeL);
        }

        editText.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                final Calendar myCalender = Calendar.getInstance();
                int hour = myCalender.get(Calendar.HOUR_OF_DAY);
                int minute = myCalender.get(Calendar.MINUTE);


                TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (view.isShown()) {
                            myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            myCalender.set(Calendar.MINUTE, minute);

                        }
                        editText.setText(hourOfDay + ":" + minute);
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(FormActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, false);
                timePickerDialog.setTitle("Choose hour:");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
            }
        });

        // set the propert of the timeTitle
        timeTitle.setText(Html.fromHtml(title));
        titleTextview(timeTitle);

        // set the property of the timepicker
        LinearLayout.LayoutParams timepickerparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        timepickerparams.setMargins(50,5,50,0);
        timePicker.setLayoutParams(timepickerparams);
        timePicker.setMinimumHeight(300);

        // add the element
        linearLayout.addView(timeTitle);
//        linearLayout.addView(timePicker);
        linearLayout.addView(editText);
    }

    public void SingleLineTest (String title, String id){
        // define the textview and Edittext
        TextView textView =  new TextView(this);
        EditText editText = new EditText(this);

        // set text in textview.
        textView.setText(Html.fromHtml(title));

        //set property
        titleTextview(textView);
        EditTextview(editText);
        editText.setTag("element_" + id);
        singleElementid = "element_" + id;
        String single = element_data.get(singleElementid);
        if(single != null){
            editText.setText(single);
        }

        // add the element
        linearLayout.addView(textView);
        linearLayout.addView(editText);
    }

    public void emailLine (String title, String id){
        // define the textview and Edittext
        TextView textView =  new TextView(this);
        EditText editText = new EditText(this);

        // set text in textview.
        textView.setText(Html.fromHtml(title));

        //set property
        titleTextview(textView);
        EditTextview(editText);
        editText.setTag("element_" + id);
        emailElementid = "element_" + id;
        String email = element_data.get(emailElementid);
        if(email != null){
            editText.setText(email);
        }

        // add the element
        linearLayout.addView(textView);
        linearLayout.addView(editText);
    }

    private void ParagraphText(String title, String id){

        // define the text title and edittext(muitline)
        TextView paragraphTitle =  new TextView(this);
        EditText textArea = new EditText(this);

        // set property of element
        paragraphTitle.setText(Html.fromHtml(title));
        titleTextview(paragraphTitle);
        EditTextview(textArea);
        textArea.setSingleLine(false);
        textArea.setLines(5);
        textArea.setTag("element_" + id);
        textareaElemnet = "element_" + id;
        String area = element_data.get(textareaElemnet);
        if(area != null){
            textArea.setText(area);
        }
        textArea.setGravity(View.TEXT_ALIGNMENT_TEXT_START);

        // add the element
        linearLayout.addView(paragraphTitle);
        linearLayout.addView(textArea);
    }

    private void MultipleChoice(String title, final String id) {
        ArrayList<String> mylist = new ArrayList<String>();

        Cursor cursor = ODb.rawQuery("SELECT *FROM " + ElementOptionDatabaseHelper.OPTIONTABLE_NAME + " WHERE " + ElementOptionDatabaseHelper.OCOL_2 + "=? AND " + ElementOptionDatabaseHelper.OCOL_3 + "=?" , new String[]{formid, id});

        if(cursor.moveToFirst()){
            do{
                String data = cursor.getString(cursor.getColumnIndex("OOption"));
                mylist.add(data);
            }while (cursor.moveToNext());

        }
        cursor.close();

        // define the radio group and title
        TextView radiotitle = new TextView(this);
        final RadioGroup radioGroup = new RadioGroup(this);

        //set property the radiotile
        radiotitle.setText(Html.fromHtml(title));
        titleTextview(radiotitle);

        // set property the radiogroup
        radioGroup.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams radiogroupparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        radiogroupparams.setMargins(50,5,50, 0);

        //radio button add
        for (int i = 0; i < mylist.size(); i ++){
            final RadioButton radioButtonView = new RadioButton(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                radioButtonView.setId(View.generateViewId());
            }
            radioButtonView.setText(mylist.get(i));
            radioButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                int idx = radioGroup.indexOfChild(radioButtonView);
                element_data.put("element_" + id, String.valueOf(idx));
                Toast.makeText(FormActivity.this, radioButtonView.getText().toString(), Toast.LENGTH_LONG).show();
                }
            });
            radioGroup.addView(radioButtonView, radiogroupparams);
        }

        //add the title and radiogroup
        linearLayout.addView(radiotitle);
        linearLayout.addView(radioGroup);
    }

    private void  NameLint(String title, String id) {
        // define the name title and frist, last edittext
        TextView nametitle = new TextView(this);
        EditText firstname = new EditText(this);
        EditText lastname = new EditText(this);

        titleTextview(nametitle);
        nametitle.setText(Html.fromHtml(title));

        // define the name LinearLayout
        LinearLayout namelinearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams linerlayoutparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.HORIZONTAL
        );
        linerlayoutparams.setMargins(50,0,50,0);
        namelinearLayout.setLayoutParams(linerlayoutparams);

        //set porperty first EditText
        LinearLayout.LayoutParams firstnameparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        firstnameparams.setMargins(10,0,10,0);
        EditTextview(firstname);
        firstname.setWidth(350);
        firstname.setHint("First Name");
        firstname.setTag("element_" + id + "_1");
        firstnameElementid = "element_" + id + "_1";
        String first = element_data.get(firstnameElementid);
        if(first != null){
            firstname.setText(first);
        }
        firstname.setLayoutParams(firstnameparams);

        // set property the last name
        LinearLayout.LayoutParams lastnameparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lastnameparams.setMargins(10,0,10,0);
        EditTextview(lastname);
        lastname.setWidth(500);
        lastname.setHint("Last Name");
        lastname.setTag("element_" + id + "_2");
        secondnameElementid = "element_" + id + "_2";
        String second = element_data.get(secondnameElementid);
        if(second != null){
            lastname.setText(second);
        }
        lastname.setLayoutParams(lastnameparams);

        // add the first and last name in namelineatlayout
        namelinearLayout.addView(firstname);
        namelinearLayout.addView(lastname);

        //add the element
        linearLayout.addView(nametitle);
        linearLayout.addView(namelinearLayout);
    }

    private void titleTextview(TextView textView){
        LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textView.setTextSize(getResources().getDimension(R.dimen.textsize_header));
        textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

        // set margin in textview
        textparams.setMargins(50, 15, 10, 5);
        textView.setLayoutParams(textparams);
    }

    private void EditTextview(EditText editText){
        LinearLayout.LayoutParams editparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        editText.setTextSize(getResources().getDimension(R.dimen.textsize_normal));
        editText.setHint("Please write");

        editText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        editText.setBackground(getResources().getDrawable(R.drawable.editview_border));
        editText.setPadding(20,20,20,20);

        // set margin and height and width
        editparams.setMargins(50, 0, 50, 5);
        editText.setLayoutParams(editparams);
    }

    private void GetElementValue() {
        if(numberElementid != null){
            EditText numberedit = linearLayout.findViewWithTag(numberElementid);
            element_data.put(numberElementid, numberedit.getText().toString());
            linearLayout.removeView(numberedit);
            numberElementid =null;
        }

        if(singleElementid != null){
            EditText editText = linearLayout.findViewWithTag(singleElementid);
            element_data.put(singleElementid, editText.getText().toString());
            linearLayout.removeView(editText);
            singleElementid = null;
        }

        if(emailElementid != null){
            EditText editText = linearLayout.findViewWithTag(emailElementid);
            element_data.put(emailElementid, editText.getText().toString());
            linearLayout.removeView(editText);
            emailElementid = null;
        }

        if(dateElementid != null){
            EditText editText = linearLayout.findViewWithTag(dateElementid);
            element_data.put(dateElementid, editText.getText().toString());
            linearLayout.removeView(editText);
            dateElementid = null;
        }

        if(phone1 != null || phone2 != null || phone3 != null){
            EditText ph1 = linearLayout.findViewWithTag(phone1);
            EditText ph2 = linearLayout.findViewWithTag(phone2);
            EditText ph3 = linearLayout.findViewWithTag(phone3);
            element_data.put(phoneElementid, ph1.getText().toString() + ph2.getText().toString() + ph3.getText().toString());
            linearLayout.removeView(ph1);
            linearLayout.removeView(ph2);
            linearLayout.removeView(ph3);
            phone1 = null;
            phone2 = null;
            phone3 = null;
        }

        if(price1 != null || price2 != null){
            EditText pric1 = linearLayout.findViewWithTag(price1);
            EditText pric2 = linearLayout.findViewWithTag(price2);
            element_data.put(priceElemnetid, pric1.getText().toString() + "." + pric2.getText().toString());
            linearLayout.removeView(pric1);
            linearLayout.removeView(pric1);
            price1 = null;
            price2 = null;
        }

        if(firstnameElementid != null || secondnameElementid != null){
            EditText firstname = linearLayout.findViewWithTag(firstnameElementid);
            EditText lastname = linearLayout.findViewWithTag(secondnameElementid);
            element_data.put(firstnameElementid, firstname.getText().toString());
            element_data.put(secondnameElementid, lastname.getText().toString());
            linearLayout.removeView(firstname);
            linearLayout.removeView(lastname);
            firstnameElementid = null;
            secondnameElementid = null;

        }

        if(addressElement1 != null || addressElement3 != null){
            EditText ad1 = linearLayout.findViewWithTag(addressElement1);
            if(addressElement2 != null){
                EditText ad2 = linearLayout.findViewWithTag(addressElement2);
                if(ad2.getText().toString().equals("")){
                    Toast.makeText(FormActivity.this, "Please enter a Address Line 2.", Toast.LENGTH_LONG).show();
                    return;
                }
                element_data.put(addressElement2, ad2.getText().toString());
                linearLayout.removeView(ad2);
                addressElement2 = null;
            }
            EditText ad3 = linearLayout.findViewWithTag(addressElement3);
            EditText ad4 = linearLayout.findViewWithTag(addressElement4);
            EditText ad5 = linearLayout.findViewWithTag(addressElement5);
            element_data.put(addressElement1, ad1.getText().toString());
            element_data.put(addressElement3, ad3.getText().toString());
            element_data.put(addressElement4, ad4.getText().toString());
            element_data.put(addressElement5, ad5.getText().toString());
            linearLayout.removeView(ad1);
            linearLayout.removeView(ad3);
            linearLayout.removeView(ad4);
            linearLayout.removeView(ad5);
            addressElement1 = null;
            addressElement3 = null;
            addressElement4 = null;
            addressElement5 = null;
        }

        if(textareaElemnet != null){
            EditText editText = linearLayout.findViewWithTag(textareaElemnet);
            element_data.put(textareaElemnet, editText.getText().toString());
            linearLayout.removeView(editText);
            textareaElemnet = null;
        }

        if(timeElemntid != null){
            EditText timeeditText = linearLayout.findViewWithTag(timeElemntid);
            element_data.put(timeElemntid, timeeditText.getText().toString());
            linearLayout.removeView(timeeditText);
            timeElemntid = null;
        }

        if(webElementid != null){
            EditText editText = linearLayout.findViewWithTag(webElementid);
            element_data.put(webElementid,editText.getText().toString());
            linearLayout.removeView(editText);
            webElementid = null;
        }
    }


}



