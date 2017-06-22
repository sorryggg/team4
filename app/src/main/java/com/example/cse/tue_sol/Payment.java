package com.example.cse.tue_sol;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class Payment extends ActionBarActivity {

    private ArrayList<String> mGroupList = null;
    private ArrayList<ArrayList<String>> mChildList = null;
    private ArrayList<String> mChildListContent = null;
    private static String TAG = "phptest_MainActivity";
    //php 코드상 array 의 속성 이름이다.
    private static final String TAG_JSON="Korail";
    private static final String TAG_INDEX = "index";
    private static final String TAG_SRC = "src";
    private static final String TAG_DST = "dst";
    private static final String TAG_DEP = "dep";
    private static final String TAG_ARR = "arr";
    private static final String TAG_TRAIN ="train";
    private static final String TAG_DAY = "day";
    private static final String TAG_PRICE ="price";

    TextView Point;
    private TextView mTextViewResult;
    ArrayList<HashMap<String, String>> mArrayList;
    ListView mlistView;
    ListView mlistView1;

    String mJsonString1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        mTextViewResult = (TextView) findViewById(R.id.textView_main_result);
        mlistView1 = (ListView) findViewById(R.id.listView_main_list);
        setLayout();

        mGroupList = new ArrayList<String>();
        mChildList = new ArrayList<ArrayList<String>>();
        mChildListContent = new ArrayList<String>();

        mGroupList.add("      Select Bank");

        mChildListContent.add("Uri");
        mChildListContent.add("Shinhan");
        mChildListContent.add("KB");
        mChildListContent.add("NH");
        mChildListContent.add("IBK");
        mChildListContent.add("KEB");

        mChildList.add(mChildListContent);
        mChildList.add(mChildListContent);
        mChildList.add(mChildListContent);
        mChildList.add(mChildListContent);
        mChildList.add(mChildListContent);
        mChildList.add(mChildListContent);

        mListView.setAdapter(new BaseExpandableAdapter(this, mGroupList, mChildList));

        // 그룹 클릭 했을 경우 이벤트
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                Toast.makeText(getApplicationContext(), "Select a Bank of card to use.",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // 차일드 클릭 했을 경우 이벤트
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(getApplicationContext(), mChildListContent.get(childPosition) + "  is selected.",
                        Toast.LENGTH_SHORT).show();
                mListView.collapseGroup(groupPosition);
                mGroupList.clear();
                mGroupList.add("     " + mChildListContent.get(childPosition));
                return false;
            }
        });

        // 그룹이 닫힐 경우 이벤트
        mListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                //Toast.makeText(getApplicationContext(), "g Collapse = " + groupPosition,
                //        Toast.LENGTH_SHORT).show();
            }
        });

        // 그룹이 열릴 경우 이벤트
        mListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                //Toast.makeText(getApplicationContext(), "g Expand = " + groupPosition,
                //        Toast.LENGTH_SHORT).show();
            }
        });


        //intent 값 받기
        Intent intent = getIntent();
        int indexNum = intent.getIntExtra("index", 0);
        Log.d("ooooooooooooo",""+indexNum);
        //보낸다
        GetData task = new GetData();
        task.execute("http://team4team4.esy.es/payment.php", String.valueOf(indexNum));




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_payment, menu);
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

    private ExpandableListView mListView;

    private void setLayout() {
        mListView = (ExpandableListView) findViewById(R.id.elv_list);
    }



    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Payment.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null) {

                mTextViewResult.setText(errorString);
            } else {

                mJsonString1 = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            final String data = params[1];
            String postData = "index=" + data ;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


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
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
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


            JSONObject jsonObject = new JSONObject(mJsonString1);

            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);
                String index=item.getString(TAG_INDEX);
                String src = item.getString(TAG_SRC);
                String dst = item.getString(TAG_DST);
                String day =item.getString(TAG_DAY);
                String dep=item.getString(TAG_DEP);
                String arr=item.getString(TAG_ARR);
                String address = item.getString(TAG_TRAIN);
                String price = item.getString(TAG_PRICE);

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_INDEX, index);
                hashMap.put(TAG_SRC, src);
                hashMap.put(TAG_DST, dst);
                hashMap.put(TAG_DEP, dep);
                hashMap.put(TAG_ARR, arr);
                hashMap.put(TAG_TRAIN, address);
                hashMap.put(TAG_DAY, day);
                hashMap.put(TAG_PRICE, price);

                mArrayList.add(hashMap);
            }

            ListAdapter adapter = new SimpleAdapter(

                    Payment.this, mArrayList, R.layout.list_payment,
                    new String[]{TAG_INDEX,TAG_SRC,TAG_DST,TAG_DEP,TAG_ARR, TAG_TRAIN, TAG_DAY, TAG_PRICE},
                    new int[]{R.id.textView_list_index,R.id.textView_list_id, R.id.textView_list_name,R.id.textView_list_depart,R.id.textView_list_arrive, R.id.textView_list_address, R.id.textView_list_day, R.id.textView_list_price}
            );

            mlistView1.setAdapter(adapter);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }
}

