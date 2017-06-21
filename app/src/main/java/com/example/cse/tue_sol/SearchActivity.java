package com.example.cse.tue_sol;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.io.OutputStream;

import java.net.MalformedURLException;

public class SearchActivity extends AppCompatActivity {
    private EditText data1, data2, data3;
    private Button btn_send,btn_date,btn_time;

    private static String TAG = "phptest_SearchActivity";

    //php 코드상 array 의 속성 이름이다.
    private static final String TAG_JSON="Korail";
    private static final String TAG_INDEX = "index";
    private static final String TAG_SRC = "src";
    private static final String TAG_DST = "dst";
    private static final String TAG_DEP = "dep";
    private static final String TAG_ARR = "arr";
    private static final String TAG_TRAIN ="train";
    int year, month, day, hour, minutes;

    private TextView mTextViewResult;
    ArrayList<HashMap<String, String>> mArrayList;
    ListView mlistView;
    String mJsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        NetworkUtil.setNetworkPolicy();

        GregorianCalendar calendar = new GregorianCalendar();

        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);

        day= calendar.get(Calendar.DAY_OF_MONTH);

        hour = calendar.get(Calendar.HOUR_OF_DAY);

        minutes = calendar.get(Calendar.MINUTE);

        mTextViewResult = (TextView)findViewById(R.id.textView_main_result);
        mlistView = (ListView) findViewById(R.id.listView_main_list);
        mArrayList = new ArrayList<>();

        data1 = (EditText)findViewById(R.id.editText);
        data2 = (EditText)findViewById(R.id.editText2);
        data3 = (EditText)findViewById(R.id.editText3);
        btn_send = (Button)findViewById(R.id.btn_send);
        btn_date=(Button)findViewById(R.id.btn_date);
        btn_time=(Button)findViewById(R.id.btn_time);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GetData task = new GetData();
                task.execute("http://team4team4.esy.es/search.php",String.valueOf(data1.getText()),String.valueOf(data2.getText()),String.valueOf(data3.getText()));
                String msg = String.format("%d : %d",  hour, minutes);

                Toast.makeText(SearchActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(SearchActivity.this, dateSetListener, year, month, day).show();

            }
        });

        btn_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(SearchActivity.this, timeSetListener, hour, minutes, false).show();

            }
        });

        //원하는 승차권 클릭했을 경우 이벤트 처리
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*

                이 부분을 수정하여 원하는 열차시간표 클릭 시 결제나 다른 수단이 진행되도록 작성.
                 */
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SearchActivity.this);

        // 제목셋팅
                alertDialogBuilder.setTitle("Korail talk+");

        // AlertDialog 셋팅

                alertDialogBuilder
                        .setMessage("Do you want buy this ticket?")
                        .setCancelable(false)
                        .setPositiveButton("Yes!",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        // 프로그램을 종료한다
                                        SearchActivity.this.finish();
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        // 다이얼로그를 취소한다
                                        dialog.cancel();
                                    }
                                });

                // 다이얼로그 생성
                AlertDialog alertDialog = alertDialogBuilder.create();

                // 다이얼로그 보여주기
                alertDialog.show();
               // break;

                //default:
                //break;




                Toast.makeText(SearchActivity.this ,mArrayList.get(position).get(TAG_SRC),Toast.LENGTH_LONG).show();//list item 클릭 시 출발역 이름 toast 출력
            }
        });

    }

    private class GetData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(SearchActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null){

                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            final String data1=params[1];
            final String data2=params[2];
            final String data3=params[3];

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                String postData = "srcstation=" + data1 + "&" + "dststation=" + data2 + "&" + "train=" + data3;

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postData.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    private void showResult(){
        try {
            mArrayList.clear();//초기화
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);
                String index=item.getString(TAG_INDEX);
                String src = item.getString(TAG_SRC);
                String dst = item.getString(TAG_DST);
                String dep=item.getString(TAG_DEP);
                String arr=item.getString(TAG_ARR);
                String address = item.getString(TAG_TRAIN);

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_INDEX, index);
                hashMap.put(TAG_SRC, src);
                hashMap.put(TAG_DST, dst);
                hashMap.put(TAG_DEP, dep);
                hashMap.put(TAG_ARR, arr);
                hashMap.put(TAG_TRAIN, address);

                mArrayList.add(hashMap);
            }

            ListAdapter adapter = new SimpleAdapter(

                    SearchActivity.this, mArrayList, R.layout.item_list,
                    new String[]{TAG_INDEX,TAG_SRC,TAG_DST,TAG_DEP,TAG_ARR, TAG_TRAIN},
                    new int[]{R.id.textView_list_index,R.id.textView_list_id, R.id.textView_list_name,R.id.textView_list_depart,R.id.textView_list_arrive, R.id.textView_list_address}
            );

            mlistView.setAdapter(adapter);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {



        @Override

        public void onDateSet(DatePicker view, int years, int monthOfYear,

                              int dayOfMonth) {

            // TODO Auto-generated method stub

            String msg = String.format("%d / %d / %d", years,monthOfYear+1, dayOfMonth);
            //내가 선택한 달력날짜 고정
            year=years;
            month=monthOfYear+1;
            day=dayOfMonth;

            Toast.makeText(SearchActivity.this, msg, Toast.LENGTH_SHORT).show();

        }

    };



    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {



        @Override

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            // TODO Auto-generated method stub

            String msg = String.format("%d : %d",  hourOfDay, minute);
            //이 설정을 통해 내가 선택한 시간정보를 저장
            hour=hourOfDay;minutes=minute;
            Toast.makeText(SearchActivity.this, msg, Toast.LENGTH_SHORT).show();

        }

    };


}

