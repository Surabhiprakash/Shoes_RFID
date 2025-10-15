package com.example.book_rfid;

import static com.example.book_rfid.ReadFragment.scan;
import static com.example.book_rfid.ReadFragment.epcToTitleMap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FoundTags extends AppCompatActivity {

    Adapter4 adapter4;
    List<ProductStatus> productStatusList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_tags);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ListView listView = findViewById(R.id.LvTags2);

        productStatusList = new ArrayList<>();
        HashMap<String, ProductStatus> productMap = new HashMap<>();
        Log.d("FoundTags", "scan " + scan);
        for (String tag : scan) {
            boolean isLeft = tag.endsWith("L");
            boolean isRight = tag.endsWith("R");

            // Remove L/R if present
            String numericPart = isLeft || isRight ? tag.substring(0, tag.length() - 1) : tag;

            // Keep only digits
            numericPart = numericPart.replaceAll("[^0-9]", "");

            // Now numericPart should match exactly Box EPC in epcToTitleMap
            String baseEpc = numericPart;

            Log.d("FoundTags", "Normalized baseEpc: " + baseEpc);

            // Get product title using base EPC
            String productTitle = epcToTitleMap.get(baseEpc);
            if (productTitle == null) {
                Log.d("FoundTags", "⚠️ No product title found for EPC: " + baseEpc);
                continue;
            }

            ProductStatus status = productMap.get(baseEpc);
            if (status == null) {
                status = new ProductStatus(productTitle, false, false, false);
            }

            if (isLeft) status.isLeftFound = true;
            else if (isRight) status.isRightFound = true;
            else status.isBoxFound = true;

            productMap.put(baseEpc, status);
        }
        Log.d("ProductStatus","hi outside");
        // ✅ Only add entries where all 3 parts (Box, Left, Right) were found
        for (ProductStatus status : productMap.values()) {
            Log.d("ProductStatus","hi inside");
            if (status.isBoxFound && status.isLeftFound && status.isRightFound) {
                productStatusList.add(status);
            }
        }
        Log.d("ProductStatus", "✅ Fully matched products: " + productStatusList);

        Log.d("ProductStatus", "✅ Fully matched products: " + productStatusList.size());

        adapter4 = new Adapter4(this, productStatusList);
        listView.setAdapter(adapter4);

//        if (ReadFragment.mFoundTags != null) {
//            ReadFragment.mFoundTags.setText(productStatusList.size());
//        }

    }
}
