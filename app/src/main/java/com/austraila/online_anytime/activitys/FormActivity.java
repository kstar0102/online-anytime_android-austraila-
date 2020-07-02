package com.austraila.online_anytime.activitys;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import com.austraila.online_anytime.Common.CustomScrollview;
import com.austraila.online_anytime.R;
import com.austraila.online_anytime.Common.AddPhotoBottomDialogFragment;
import com.austraila.online_anytime.activitys.signature.SignatureView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.austraila.online_anytime.activitys.cameraActivity.CameraActivity.Image_Capture_Code;


public class FormActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener  {
    LinearLayout linearLayout;
    LinearLayout buttonsLayout;
    DatePickerDialog picker;
    SignatureView signatureView;
    Bitmap photo;
    CustomScrollview customScrollview;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formtest);
        getSupportActionBar().hide();

        customScrollview = (CustomScrollview) findViewById(R.id.scrollmain);
        customScrollview.setEnableScrolling(true);

        linearLayout = findViewById(R.id.linear_layout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        Intent intent = getIntent();
        String camera = intent.getStringExtra("camera");
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("photoImage");
        photo = bitmap;
        if(camera != null){
            Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cInt,Image_Capture_Code);
        }

        TextView backTextView = findViewById(R.id.back_textview);
        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        SingleLineTest();
        SignatureMainLayout();
        ParagraphText();
        MultipleChoice();
        NameLint();
        TimeLint();
        PriceLint();
        SectionBreak();
        MediaLint();
        NumberLint();
        CheckBoxes();
        DropDown();
        DateLint();
        PhoneLint();
        WebSiteLint();
        fileUpload();
    }

    private void WebSiteLint() {
        //define the element
        TextView webSiteTitle = new TextView(this);
        EditText websiteEdit = new EditText(this);

        // set the property
        titleTextview(webSiteTitle);
        webSiteTitle.setText("Website Title");
        EditTextview(websiteEdit);
        websiteEdit.setText("http://");

        //add the element
        linearLayout.addView(webSiteTitle);
        linearLayout.addView(websiteEdit);
    }

    private void SignatureMainLayout() {
        //set signature title
        TextView signTitle = new TextView(this);
        titleTextview(signTitle);
        signTitle.setText("Signature");

        LinearLayout signview = new LinearLayout(this);
        LinearLayout.LayoutParams signviewParma = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        signviewParma.setMargins(0,15,0,5);
        signview.setLayoutParams(signviewParma);
        signview.setOrientation(LinearLayout.VERTICAL);
        signview.setMinimumHeight(500);

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

    // camera funtion
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                Intent intent = new Intent(FormActivity.this, FormActivity.class);
                intent.putExtra("photoImage", bp);
                startActivity(intent);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }

    // file exploer funtion
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void fileUpload() {
        //define the button.
        Button uploadbtn = new Button(this);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        btnParams.setMargins(50,20,50,10);
        uploadbtn.setLayoutParams(btnParams);
        uploadbtn.setWidth(500);
        uploadbtn.setHeight(80);
        uploadbtn.setBackground(getDrawable(R.drawable.btn_rounded));
        uploadbtn.setText("Select File");
        uploadbtn.setTextColor(getResources().getColor(R.color.white_color));
        uploadbtn.setTypeface(uploadbtn.getTypeface(), Typeface.BOLD);
        uploadbtn.setTextSize(getResources().getDimension(R.dimen.textsize_normal));
        linearLayout.addView(uploadbtn);

        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPhotoBottomDialogFragment addPhotoBottomDialogFragment =
                        AddPhotoBottomDialogFragment.newInstance();
                addPhotoBottomDialogFragment.show(getSupportFragmentManager(),
                        "add_photo_dialog_fragment");
            }
        });

        //define the Imageview
        ImageView photoImage = new ImageView(this);
        LinearLayout.LayoutParams photoImageParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        photoImageParam.setMargins(50,10,50,5);
        photoImage.setMinimumHeight(600);
        photoImage.setVisibility(View.GONE);
        photoImage.setLayoutParams(photoImageParam);

        TextView photofilepath = new TextView(this);
        titleTextview(photofilepath);
        photofilepath.setTextSize(getResources().getDimension(R.dimen.textsize_normal));
        photofilepath.setVisibility(View.GONE);
        Intent intent = getIntent();
        String getfile = intent.getStringExtra("filepath");

//        File imgFile = new  File("/sdcard/Images/test_image.jpg");
//        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());


        if(getfile != null){
            photofilepath.setVisibility(View.VISIBLE);
            File file = new File(getfile);
            Log.e("adfasdfasdf", getfile );
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            photofilepath.setText(getfile);
        }
        if(photo != null){
            photoImage.setVisibility(View.VISIBLE);
            photoImage.setImageBitmap(photo);
        }

        linearLayout.addView(photoImage);
        linearLayout.addView(photofilepath);

    }

    //spinner select function
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void DateLint() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dateTime = dateFormat.format(date);

        final Calendar cldr = Calendar.getInstance();
        final int day = cldr.get(Calendar.DAY_OF_MONTH);
        final int month = cldr.get(Calendar.MONTH);
        final int year = cldr.get(Calendar.YEAR);

        //define the dateTitle
        TextView dateTitle = new TextView(this);
        dateTitle.setText("Date");
        titleTextview(dateTitle);
        linearLayout.addView(dateTitle);

        //define the date picker
        final EditText dateEditText = new EditText(this);
        EditTextview(dateEditText);
        dateEditText.setText(dateTime);
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

    private void PhoneLint() {
        //define the phone title
        TextView phoneTitle = new TextView(this);
        titleTextview(phoneTitle);
        phoneTitle.setText("Phone");
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
        phoneLinerLayout.addView(phoneNum1);
        phoneLinerLayout.addView(lineText);
        EditTextview(phoneNum2);
        phoneNum2.setHint("###");
        phoneNum2.setLayoutParams(param3Num);
        phoneNum2.setInputType(InputType.TYPE_CLASS_NUMBER);
        phoneNum2.setFilters(FilterArray);
        phoneNum2.setWidth(130);
        phoneLinerLayout.addView(phoneNum2);
        phoneLinerLayout.addView(lineText1);
        EditTextview(phoneNum3);
        phoneNum3.setHint("####");
        phoneNum3.setLayoutParams(param3Num);
        phoneNum3.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputFilter[] FilterArray1 = new InputFilter[1];
        FilterArray1[0] = new InputFilter.LengthFilter(4);
        phoneNum3.setFilters(FilterArray1);
        phoneNum3.setWidth(180);
        phoneLinerLayout.addView(phoneNum3);

    }

    private void DropDown() {
        //define the dropdown title
        TextView dropTitle = new TextView(this);
        titleTextview(dropTitle);
        dropTitle.setText("DropDowm");
        linearLayout.addView(dropTitle);

        String[] users = {"Suresh Dasari", "Trishika Dasari", "Rohini Alavala", "Praveen Kumar", "Madhav Sai"};
        //define the spinner
        LinearLayout.LayoutParams dropdownParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        dropdownParams.setMargins(60,10,50,0);
        Spinner dropdown = new Spinner(this);
        dropdown.setLayoutParams(dropdownParams);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_item, users);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);
        linearLayout.addView(dropdown);
    }

    private void CheckBoxes() {
        //define the checkboxesTitle
        TextView checkboxesTitle = new TextView(this);
        titleTextview(checkboxesTitle);
        checkboxesTitle.setText("Checkboxes");
        linearLayout.addView(checkboxesTitle);

        String[] ab ={"CheckBox1","CheckBox2"};
        LinearLayout.LayoutParams ParmsDescription = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        ParmsDescription.setMargins(50,10,50,0);
        for(int i = 0; i < ab.length; i ++){
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(ab[i]);
            checkBox.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            checkBox.setTextSize(getResources().getDimension(R.dimen.textsize_normal));
            checkBox.setLayoutParams(ParmsDescription);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String msg = "You have " + (isChecked ? "checked" : "unchecked") + " this Check it Checkbox.";
                    Toast.makeText(FormActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
            linearLayout.addView(checkBox);
        }
    }

    private void NumberLint() {
        //define the element
        TextView numberTitle = new TextView(this);
        EditText numberEdit = new EditText(this);

        //set property the numberTitle
        titleTextview(numberTitle);
        numberTitle.setText("Number");
        linearLayout.addView(numberTitle);

        //set property the numberEdit
        EditTextview(numberEdit);
        numberEdit.setHint("Only number");
        numberEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        linearLayout.addView(numberEdit);
    }

    private void MediaLint() {
        //define the element
        TextView mediaTitle = new TextView(this);
        ImageView mediaImage = new ImageView(this);

        //set property the mediatitle
        mediaTitle.setText("Media");
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

    private void SectionBreak() {
        //define the element
        TextView breakTitle = new TextView(this);
        TextView breakdes = new TextView(this);

        //set property the breakTitle
        titleTextview(breakTitle);
        breakTitle.setText("Section Break");

        //set property the breakdes
        LinearLayout.LayoutParams breakdesparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        breakdesparams.setMargins(50,0,50,0);
        titleTextview(breakdes);
        breakdes.setText("aaaaa");
        breakdes.setLayoutParams(breakdesparams);
        breakdes.setTextSize(getResources().getDimension(R.dimen.textsize_normal));

        linearLayout.addView(breakTitle);
        linearLayout.addView(breakdes);
    }

    private void PriceLint() {
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
        priceLinerlayout.addView(centEditText);

        //set property the price Title
        priceTitle.setText("Price");
        titleTextview(priceTitle);

        // add the element
        linearLayout.addView(priceTitle);
        linearLayout.addView(priceLinerlayout);
    }

    private void TimeLint() {
        //define the timepicker and timetitle
        TimePicker timePicker = new TimePicker(this);
        TextView timeTitle = new TextView(this);
        final EditText editText = new EditText(this);
        EditTextview(editText);
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
        timeTitle.setText("Time Picker");
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

    public void SingleLineTest (){
        // define the textview and Edittext
        TextView textView =  new TextView(this);
        EditText editText = new EditText(this);

        // set text in textview.
        textView.setText("Text");

        //set property
        titleTextview(textView);
        EditTextview(editText);

        // add the element
        linearLayout.addView(textView);
        linearLayout.addView(editText);
    }

    private void ParagraphText(){
        // define the text title and edittext(muitline)
        TextView paragraphTitle =  new TextView(this);
        EditText textArea = new EditText(this);

        // set property of element
        paragraphTitle.setText("Paragraph");
        titleTextview(paragraphTitle);
        EditTextview(textArea);
        textArea.setSingleLine(false);
        textArea.setLines(5);
        textArea.setGravity(View.TEXT_ALIGNMENT_TEXT_START);

        // add the element
        linearLayout.addView(paragraphTitle);
        linearLayout.addView(textArea);
    }

    private void MultipleChoice() {
        // define the radio group and title
        TextView radiotitle = new TextView(this);
        RadioGroup radioGroup = new RadioGroup(this);

        //set property the radiotile
        radiotitle.setText("radio title");
        titleTextview(radiotitle);

        // set property the radiogroup
        radioGroup.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams radiogroupparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        radiogroupparams.setMargins(50,5,50, 0);

        String[] ab ={"radio1","radio2"};
        //radio button add
        for (int i = 0; i < ab.length; i ++){
            RadioButton radioButtonView = new RadioButton(this);
            radioButtonView.setText(ab[i]);
            radioGroup.addView(radioButtonView, radiogroupparams);
        }

        //add the title and radiogroup
        linearLayout.addView(radiotitle);
        linearLayout.addView(radioGroup);
    }

    private void  NameLint() {
        // define the name title and frist, last edittext
        TextView nametitle = new TextView(this);
        EditText firstname = new EditText(this);
        EditText lastname = new EditText(this);

        titleTextview(nametitle);
        nametitle.setText("Name");

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


}



