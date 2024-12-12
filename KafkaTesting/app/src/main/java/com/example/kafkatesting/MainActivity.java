package com.example.kafkatesting;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.kafkatesting.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private KafkaProducerExample kafkaProducer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        kafkaProducer = new KafkaProducerExample();
        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
        binding.btn.setOnClickListener(v -> produceMessage());
    }
    public void produceMessage() {

        new CollectInventoryData().execute("ABC");
    }
        public class CollectInventoryData extends AsyncTask<String, String, JSONObject> {

            @Override
            protected JSONObject doInBackground(String... strings) {
                kafkaProducer = new KafkaProducerExample();
                JSONObject shipmentData = new JSONObject();
                try {
                    shipmentData.put("shipment_id", 123);
                    JSONArray items = new JSONArray();
                    JSONObject item = new JSONObject();
                    item.put("item_id", 1);
                    item.put("quantity", 10);
                    items.put(item);
                    shipmentData.put("items", items);
                    return shipmentData;
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                if(jsonObject!= null){
                    try{
                        Log.e("JSON", jsonObject.toString());
                        kafkaProducer.sendMessage( jsonObject);
                    }
                    catch (Exception ex){
                        //should not be here
                        ex.printStackTrace();
                    }
                }
            }
        }

    }

