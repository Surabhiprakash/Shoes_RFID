package com.example.book_rfid;


import static org.apache.poi.ss.usermodel.DataValidationConstraint.ValidationType.FORMULA;
import static org.apache.xmlbeans.impl.piccolo.xml.Piccolo.STRING;
import static java.sql.Types.BOOLEAN;
import static java.sql.Types.NUMERIC;
import static jxl.Workbook.createWorkbook;
import static jxl.biff.Type.BLANK;


import org.apache.poi.ss.usermodel.CellType;
import java.math.BigDecimal;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LabeledIntent;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.entity.UHFTAGInfo;
import com.rscja.team.qcom.deviceapi.S;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class ReadFragment extends KeyDwonFragment {

    private static final String TAG = "UHFReadTagFragment";
    private boolean loopFlag = false;
    private int inventoryFlag = 1;
    private ArrayList<HashMap<String, String>> tagList;
    public static List<String> missingtags= new ArrayList<>();
    public static List<ProductStatus> missingtagsobj = new ArrayList<>();
     static List<String> excelTags = new ArrayList<>();
    private List<String> tempDatas = new ArrayList<>();
    public static List<String> scan = new ArrayList<>();
    public static List<String> unknown=new ArrayList<>();
    public static HashMap<String, ProductStatus> scannedStatusMap = new HashMap<>();
    List<ProductStatus> productTagList = new ArrayList<>();

    int count1=0;
   private HashMap<String,String> readedvalue=new HashMap<String,String>();
    MyAdapter adapter;
   // Adapter adapter2;
    Button BtClear;
    TextView tvTime;
    TextView tv_count;
    TextView tv_total;
    RadioGroup RgInventory;
    RadioButton RbInventorySingle;
    RadioButton RbInventoryLoop;


    ImageView imagetag;
    CardView totalCard,foundCard,missingCard,PartialCard,historyCard;
    Button BtInventory, export;
    ListView LvTags;
    private static Read mContext;
    private HashMap<String, String> map=new HashMap<>();
    private HashMap<String, model> map2 = new HashMap<>();
    private HashMap<String, String> map3 = new HashMap<>();
    private HashMap<String,String> matchkeyvalueexcel=new HashMap<>();

    private List<String> productnames=new ArrayList<>();
    private int total;
    private long time;

    private CheckBox cbFilter;
    private ViewGroup layout_filter;

    public static final String TAG_EPC = "tagEPC";
    public static final String TAG_EPC_TID = "tagEpcTID";
    public static final String TAG_COUNT = "tagCount";
    public static final String TAG_RSSI = "tagRssi";

    private CheckBox cbEPC_Tam;
    private static String fileType = "";//
    private static String extensionXLS = "XLS";//
    private static String extensionXLSX = "XLSX";//
    int index = 0;
    public static TextView mTotalTags,mFoundTags,mMissingTags,mPartialTags;


    private SimpleDateFormat mDateFormat3 = new SimpleDateFormat("dd-MM");
    Date date;

     Date previousDate=new Date();
    String datee=mDateFormat3.format(previousDate);
    TextView mTimeIn,mTimeOut;
    Date mDate,mDate2;
    long startTime;
    float endTime;
    float useTime;
   static int count_total;
    static int count_found;
    static int count_missing;
    static int count_unknown;
    static int count_clear_history;
    static List<String> history_date=new LinkedList<>();
    static List<String> history_total=new LinkedList<>();
    static List<String> history_found=new LinkedList<>();
    static List<String> history_missing=new LinkedList<>();
    static List<String> history_unknown=new LinkedList<>();
    static Set<String> dontallowduplicatedate=new LinkedHashSet<>();
    // Add this as a class-level field
    Map<String, ProductStatus> epcToProductStatusMap = new HashMap<>();
   public static Map<String, String> epcToTitleMap = new HashMap<>();


    private SharedPreferences sharedPreferences;
    private Calendar previousDatee;

    private Handler handler2=new Handler();
    private Runnable updateTimeRunnable;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            UHFTAGInfo info = (UHFTAGInfo) msg.obj;
            addDataToList(info.getEPC(), mergeTidEpc(info.getTid(), info.getEPC(), info.getUser()), info.getRssi());
            setTotalTime();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "UHFReadTagFragment.onCreateView");

        return inflater.inflate(R.layout.fragment_read, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "UHFReadTagFragment.onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        mContext = (Read) getActivity();
        mContext.currentFragment = this;
        BtClear = (Button) getView().findViewById(R.id.BtClear);
        tvTime = (TextView) getView().findViewById(R.id.tvTime);
        tvTime.setText("0s");
        tv_count = (TextView) getView().findViewById(R.id.tv_count);
        tv_total = (TextView) getView().findViewById(R.id.tv_total);
        RgInventory = (RadioGroup) getView().findViewById(R.id.RgInventory);
        RbInventorySingle = (RadioButton) getView().findViewById(R.id.RbInventorySingle);
        RbInventoryLoop = (RadioButton) getView().findViewById(R.id.RbInventoryLoop);
        tagList = new ArrayList<HashMap<String, String>>();
        BtInventory = (Button) getView().findViewById(R.id.BtInventory);
        export = getView().findViewById(R.id.export);
     //   LvTags = (ListView) getView().findViewById(R.id.LvTags);
        adapter = new MyAdapter(mContext);
      //  adapter2=new Adapter(mContext,missingtags);
        final String[] tag = {"tagUii", "tagLen", "tagCount", "tagRssi"};//
        final int[] id = {R.id.TvTagUii, R.id.TvTagLen, R.id.TvTagCount,
                R.id.TvTagRssi};//
        mTotalTags=(TextView)getView().findViewById(R.id.totalTags);
        mFoundTags=(TextView)getView().findViewById(R.id.foundTags);
        mMissingTags=(TextView)getView().findViewById(R.id.missingtags);
        mPartialTags=(TextView)getView().findViewById(R.id.PartialTags);

        totalCard=(CardView)getView().findViewById(R.id.card1);
        foundCard=(CardView)getView().findViewById(R.id.card2);
        missingCard=(CardView)getView().findViewById(R.id.card3);
        PartialCard=(CardView)getView().findViewById(R.id.card4);
        historyCard=(CardView)getView().findViewById(R.id.card5);
        imagetag=(ImageView)getView().findViewById(R.id.imagetag);
        imagetag.setImageResource(R.drawable.qqq);
        mTimeIn=(TextView)getView().findViewById(R.id.timeIn);
        mTimeOut=(TextView)getView().findViewById(R.id.timeout);

        //mDate= Calendar.getInstance().getTime();
        //mDate2= Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        startTime = System.currentTimeMillis();

        double abc=NumberTool.getPointDouble(1, useTime);
        Log.d("rrr", String.valueOf(abc));
        calendar.add(Calendar.SECOND, (int) abc);
         endTime = System.currentTimeMillis();


        // Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        // mContext.setActionBar(toolbar);

    /*    LvTags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectItem(position);
                adapter.notifyDataSetInvalidated();
            }
        });*/



        totalCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),TotalTags.class);
                startActivity(i);
            }
        });

        foundCard .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),FoundTags.class);
                startActivity(i);
            }
        });

        missingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),MissingTags.class);
                startActivity(i);
            }
        });

        PartialCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),UnknownTags.class);
                startActivity(i);
            }
        });

        historyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(getActivity(),History.class);
                startActivity(i);
            }
        });
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mContext.tagList.isEmpty()) {
                    Toast.makeText(mContext, "Please Start Scanning the Tag", Toast.LENGTH_SHORT).show();
                    Log.e("MI", String.valueOf(tagList.size()));
                } else {
                    export();

                }
                //checkPermissions();
            }
        });
     //   LvTags.setAdapter(adapter);
        BtClear.setOnClickListener(new BtClearClickListener());
        RgInventory.setOnCheckedChangeListener(new RgInventoryCheckedListener());
        if(matchkeyvalueexcel.size()==0){
            Toast.makeText(mContext,"Kindly import Excel",Toast.LENGTH_LONG).show();
        }
            BtInventory.setOnClickListener(new BtInventoryClickListener());



        initFilter(getView());

        initEPCTamperAlarm(getView());
        //clearData();
        tv_count.setText(mContext.tagList.size() + "");
        tv_total.setText(total + "");
        Log.i(TAG, "UHFReadTagFragment.EtCountOfTags=" + tv_count.getText());


    }

    private boolean hasDateChanged(Calendar currentDate) {
        if (previousDatee == null) {
            // First run or previousDate not set, so date has changed
            return true;
        }

        int previousDay = previousDatee.get(Calendar.DAY_OF_YEAR);
        int currentDay = currentDate.get(Calendar.DAY_OF_YEAR);

        // Check if the day has changed
        return previousDay != currentDay;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Start the continuous updates
        handler2.post(updateTimeRunnable);
    }
    @Override
    public void onStop() {
        super.onStop();
        handler2.removeCallbacks(updateTimeRunnable);
    }



    public static void export() {

        Date todaysdate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(todaysdate);


        String Fnamexls = date + ".xls";

        File sdCard = Environment.getExternalStorageDirectory();

        File directory = new File(sdCard.getAbsolutePath() + "/bookrfid");
        directory.mkdirs();

        File file = new File(directory, Fnamexls);

        WorkbookSettings wbSettings = new WorkbookSettings();
        //Workbook wb;
        wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook;
        try {
            int a = 1;
            Label label, label1 = null;

            workbook = createWorkbook(file, wbSettings);
             //wb= WorkbookFactory.create(file, String.valueOf(wbSettings));

            WritableSheet sheet =  workbook.createSheet("First_Sheet",0);
            List<String> scan2=new ArrayList<>(scan);
            List<String> missingtag=new ArrayList<>(missingtags);
            List<String> unknow=new ArrayList<>(unknown);
            List<String> totalTags=new ArrayList<>();
            List<String> found=new ArrayList<>();
            List<String> missingtags2=new ArrayList<>();
            List<String> unkown=new ArrayList<>();

            totalTags.add("TotalTags");
            found.add("FoundTags");
            missingtags2.add("MissingTags");
            unkown.add("UnkownTags");
            for (int b=0;b<excelTags.size();b++){
                String tag=excelTags.get(b);
                String c=tag.trim();
                totalTags.add(c);
            }

            for (int c=0;c<totalTags.size();c++){
                String temp=totalTags.get(c);
                label=new Label(0,c,temp);
                sheet.addCell(label);
            }

            for (int b=0;b<scan.size();b++){
                String tag=scan2.get(b);
                String c=tag.trim();
                found.add(c);
            }

            for (int c=0;c<found.size();c++){
                String temp=found.get(c);
                label=new Label(1,c,temp);
                sheet.addCell(label);
            }

            for (int b=0;b<missingtags.size();b++){
                String tag=missingtag.get(b);
                String c=tag.trim();
                missingtags2.add(c);
            }

            for (int c=0;c<missingtags2.size();c++){
                String temp=missingtags2.get(c);
                label=new Label(2,c,temp);
                sheet.addCell(label);
            }

            for (int b=0;b<unknown.size();b++){
                String tag=unknow.get(b);
                String c=tag.trim();
                unkown.add(c);
            }

            for (int c=0;c<unkown.size();c++){
                String temp=unkown.get(c);
                label=new Label(3,c,temp);
                sheet.addCell(label);
            }
            /*Map<String,String> missngtags3=new LinkedHashMap<>();
            missngtags3.put("EPC No","Product Name");
            for (int b=0;b<missingtags2.size();b++){
                String tag=missingtags2.get(b);
                String c=tag.trim();
             String g =c.replace("                "," ");
             Log.d("goo",g);
                String[] tag1=g.split(" ");
                Log.d("klm",tag1[0]);
                missngtags3.put(tag1[0],tag1[1]);
                Log.d("jkl", String.valueOf(missngtags3.get(tag1[0])));
                tag1=null;
            }

            int count=0;
            for(Map.Entry<String,String> mt:missngtags3.entrySet()){
                String temp1=null;

                temp1=mt.getKey();
                label=new Label(0,count,temp1);
                sheet.addCell(label);
                count++;
            }

            int count1=0;
            for(Map.Entry<String,String> mt:missngtags3.entrySet()){
                String temp2=null;

                temp2=mt.getValue();
                label=new Label(1,count1,temp2);
                sheet.addCell(label);
                count1++;
            }*/


            /*for (int i = 0; i <= missngtags3.size() - 1; i++) {
               // HashMap<String, String> temp = new HashMap<String, String>();
              //  temp = mContext.tagList.get(i);
                String temp=null;
                temp=missngtags3.get(i);
               // String tempStr = temp.get("tagEpcTID");
                String tempStr = temp;
                label = new Label(0, i, tempStr);
                Log.d("CheckArray", i + "::" + missingtags2.get(i));


                //Log.d("Mi", String.valueOf(mContext.tagList.size()));
                sheet.addCell(label);

            }*/

         /*   for (int i = 0; i <= missingtags2.size() - 1; i++) {
                // HashMap<String, String> temp = new HashMap<String, String>();
                //  temp = mContext.tagList.get(i);
                String temp=null;
                temp=missingtags2.get(i);
                // String tempStr = temp.get("tagEpcTID");
                String tempStr = temp;
                label = new Label(0, i, tempStr);
                Log.d("CheckArray", i + "::" + missingtags2.get(i));


                //Log.d("Mi", String.valueOf(mContext.tagList.size()));
                sheet.addCell(label);

            }*/

            workbook.write();
            workbook.close();

            Toast.makeText(mContext, "File has been downloaded in Internal Storage/bookrfid/" + Fnamexls, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }


    private Button btnSetFilter;

    @SuppressLint("SuspiciousIndentation")
    private void initFilter(View view) {
        layout_filter = (ViewGroup) view.findViewById(R.id.layout_filter);
        layout_filter.setVisibility(View.GONE);
        cbFilter = (CheckBox) view.findViewById(R.id.cbFilter);
        cbFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                layout_filter.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        final EditText etLen = (EditText) view.findViewById(R.id.etLen);
        final EditText etPtr = (EditText) view.findViewById(R.id.etPtr);
        final EditText etData = (EditText) view.findViewById(R.id.etData);
        final RadioButton rbEPC = (RadioButton) view.findViewById(R.id.rbEPC);
        final RadioButton rbTID = (RadioButton) view.findViewById(R.id.rbTID);
        final RadioButton rbUser = (RadioButton) view.findViewById(R.id.rbUser);
        btnSetFilter = (Button) view.findViewById(R.id.btSet);
        Log.d("DeviceAPI", "IN readTagFragment");
        btnSetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int filterBank = RFIDWithUHFUART.Bank_EPC;
                if (rbEPC.isChecked()) {
                    filterBank = RFIDWithUHFUART.Bank_EPC;
                } else if (rbTID.isChecked()) {
                    filterBank = RFIDWithUHFUART.Bank_TID;
                } else if (rbUser.isChecked()) {
                    filterBank = RFIDWithUHFUART.Bank_USER;
                }
                if (etLen.getText().toString() == null || etLen.getText().toString().isEmpty()) {
                    UIHelper.ToastMessage(mContext, "Length error!!");
                    return;
                }
                if (etPtr.getText().toString() == null || etPtr.getText().toString().isEmpty()) {
                    UIHelper.ToastMessage(mContext, "ptr error!!");
                    return;
                }
                int ptr = StringUtils.toInt(etPtr.getText().toString(), 0);
                int len = StringUtils.toInt(etLen.getText().toString(), 0);
                String data = etData.getText().toString().trim();
                if (len > 0) {
                    String rex = "[\\da-fA-F]*";
                    if (data == null || data.isEmpty() || !data.matches(rex)) {
                        UIHelper.ToastMessage(mContext, "Filtered data must be hexadecimal data");
                        return;
                    }

                    if (mContext.mReader.setFilter(filterBank, ptr, len, data)) {
                        UIHelper.ToastMessage(mContext, "Success");
                    } else {
                        UIHelper.ToastMessage(mContext, "Failure");
                    }
                } else {
                    //禁用过滤
                    String dataStr = "";
                    if (mContext.mReader.setFilter(RFIDWithUHFUART.Bank_EPC, 0, 0, dataStr)
                            && mContext.mReader.setFilter(RFIDWithUHFUART.Bank_TID, 0, 0, dataStr)
                            && mContext.mReader.setFilter(RFIDWithUHFUART.Bank_USER, 0, 0, dataStr)) {
                        UIHelper.ToastMessage(mContext, "disable success");
                    } else {
                        UIHelper.ToastMessage(mContext, "disable failure");
                    }
                }
                cbFilter.setChecked(false);

            }
        });
        CheckBox cb_filter = (CheckBox) view.findViewById(R.id.cb_filter);
        rbEPC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rbEPC.isChecked()) {
                    etPtr.setText("32");
                }
            }
        });
        rbTID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rbTID.isChecked()) {
                    etPtr.setText("0");
                }
            }
        });
        rbUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rbUser.isChecked()) {
                    etPtr.setText("0");
                }
            }
        });
    }

    private void initEPCTamperAlarm(View view) {
        cbEPC_Tam = (CheckBox) view.findViewById(R.id.cbEPC_Tam);
        cbEPC_Tam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //mContext.mReader.setEPCAndTamperAlarmMode();
                } else {
                    mContext.mReader.setEPCMode();
                }
            }
        });
    }

    @Override
    public void onPause() {
        Log.i(TAG, "UHFReadTagFragment.onPause");
        super.onPause();


        stopInventory();
    }

    private void addDataToList(String epc, String epcAndTidUser, String rssi) {
        if (StringUtils.isNotEmpty(epc)) {

            // 🔹 Log the raw hex EPC
            Log.d("addDataToList", "Raw EPC (hex): " + epc);

            // Convert EPC from hex to text
            String epcText = hexToAscii(epc);
            Log.d("addDataToList", "Converted EPC (ASCII): " + epcText);

            // Convert full EPC + TID/User data from hex to ASCII
            String epcTidUserText = hexToAscii(epcAndTidUser);

            index = checkIsExist(epcText);  // Use decoded EPC text to check uniqueness

            map = new HashMap<>();
            map.put(TAG_EPC, epcText);
            map.put(TAG_EPC_TID, epcTidUserText);
            map.put(TAG_COUNT, "1");
            map.put(TAG_RSSI, rssi);

            readedvalue.put(TAG_EPC_TID, epcTidUserText);
            String scannedKeyAscii = epcTidUserText;

            // Normalize key if it ends in -L or -R (optional — still useful for duplicates)
            String baseKey = scannedKeyAscii;
            if (scannedKeyAscii.endsWith("-L") || scannedKeyAscii.endsWith("-R")) {
                baseKey = scannedKeyAscii.substring(0, scannedKeyAscii.length() - 2);
            }

            // Just store scanned tags without matching with title
            if (!scan.contains(scannedKeyAscii)) {
                scan.add(scannedKeyAscii);
                Log.d("addDataToList", "✅ Added to scan: " + scannedKeyAscii);
            }

            // If it's truly unknown (i.e. not scanned before), track it
            if (!unknown.contains(scannedKeyAscii)) {
                unknown.add(scannedKeyAscii);
                Log.w("addDataToList", "❓ Unknown tag (just for record): " + scannedKeyAscii);
            }

            // Add to tagList if not already exists
            if (index == -1) {
                mContext.tagList.add(map);
                tempDatas.add(epcText);
                tv_count.setText(String.valueOf(adapter.getCount()));
            } else {
                int tagCount = Integer.parseInt(mContext.tagList.get(index).get(TAG_COUNT), 10) + 1;
                map.put(TAG_COUNT, String.valueOf(tagCount));
                map.put(TAG_EPC_TID, epcTidUserText);
                mContext.tagList.set(index, map);
            }

            // Update totals and refresh UI
            tv_total.setText(String.valueOf(++total));
            adapter.notifyDataSetChanged();

            mContext.bookinfo.setTempDatas(tempDatas);
            mContext.bookinfo.setTagList(mContext.tagList);
            mContext.bookinfo.setCount(total);
            mContext.bookinfo.setTagNumber(adapter.getCount());

            mFoundTags.setText(String.valueOf(scan.size()));
            mPartialTags.setText(String.valueOf(unknown.size()));
        }
    }

    private String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hexStr.length(); i += 2) {
            try {
                String str = hexStr.substring(i, i + 2);
                output.append((char) Integer.parseInt(str, 16));
            } catch (Exception e) {
                output.append('?'); // fallback for invalid hex
            }
        }
        return output.toString().trim(); // trim to remove padding
    }


    // Helper: Pad with fixed spaces (e.g., 20 total characters before title)
    private String getSpacesForAlignment(String tag) {
        int totalLengthBeforeTitle = 30; // Adjust to your required spacing
        int tagLength = tag.length();
        int spacesNeeded = totalLengthBeforeTitle - tagLength;
        if (spacesNeeded < 1) spacesNeeded = 1;

        return new String(new char[spacesNeeded]).replace('\0', ' ');
    }

    public class BtClearClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            clearData();
            selectItem = -1;
            mContext.bookinfo = new bookinfo();
        }
    }

    private void clearData() {
        tv_count.setText("0");
        tv_total.setText("0");
        tvTime.setText("0s");
        total = 0;
        mContext.tagList.clear();
        tempDatas.clear();

        adapter.notifyDataSetChanged();

        mTimeIn.setText("StartTime: ");
        mTimeOut.setText("EndTime: ");
        mTotalTags.setText(""+0);
        mFoundTags.setText(""+0);
        mMissingTags.setText(""+0);
        mPartialTags.setText(""+0);
        excelTags.clear();
        scan.clear();
        missingtags.clear();
        unknown.clear();
        matchkeyvalueexcel.clear();
      //  adapter2.notifyDataSetChanged();

    }

    public class RgInventoryCheckedListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == RbInventorySingle.getId()) {

                inventoryFlag = 0;
                cbFilter.setChecked(false);
                cbFilter.setVisibility(View.INVISIBLE);
            } else if (checkedId == RbInventoryLoop.getId()) {

                inventoryFlag = 1;
                cbFilter.setVisibility(View.VISIBLE);
            }
        }
    }


    public class BtInventoryClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

                readTag();


        }
    }

    private void readTag() {
        cbFilter.setChecked(false);
        if (BtInventory.getText().equals(mContext.getString(R.string.btInventory))) {

            switch (inventoryFlag) {
                case 0:
                    time = System.currentTimeMillis();
                    UHFTAGInfo uhftagInfo = mContext.mReader.inventorySingleTag();
                    if (uhftagInfo != null) {

                        String tid = uhftagInfo.getTid();
                        Log.d("tid",tid);
                        String epc = uhftagInfo.getEPC();
                        String user = uhftagInfo.getUser();
                        addDataToList(epc, mergeTidEpc(tid, epc, user), uhftagInfo.getRssi());
                        setTotalTime();
                        mContext.playSound(1);
                    } else {
                        UIHelper.ToastMessage(mContext, "Inventory failure");
//					mContext.playSound(2);
                    }
                    //compareTags();
                    break;
                case 1:
                    if (mContext.mReader.startInventoryTag()) {
                        scan.clear();
                        missingtags.clear();
                        unknown.clear();
                         SimpleDateFormat mDateFormat = new SimpleDateFormat("HH:mm:ss");
                        Date startDate = new Date((long) startTime);
                        mTimeIn.setText("StartTime: " +mDateFormat.format(startDate));
                       // matchkeyvalueexcel.clear();
                        SimpleDateFormat mDateFormat2 = new SimpleDateFormat("HH:mm:ss");
                        mTimeOut.setText("EndTime: " + "");

                        mFoundTags.setText(""+0);
                        mMissingTags.setText(""+0);
                        mPartialTags.setText(""+0);
                        BtInventory.setText(mContext.getString(R.string.title_stop_Inventory));

                        loopFlag = true;
                        setViewEnabled(false);
                        time = System.currentTimeMillis();
                        new TagThread().start();
                    } else {
                        stopInventory();
                        UIHelper.ToastMessage(mContext, "Open failure");
//					mContext.playSound(2);
                    }
                    break;
                default:
                    break;
            }
        } else {
            stopInventory();
            setTotalTime();
        }
    }

    private void setTotalTime() {
        useTime = (System.currentTimeMillis() - time) / 1000.0F;
        tvTime.setText(NumberTool.getPointDouble(1, useTime) + "s");
    }

    private void setViewEnabled(boolean enabled) {
        RbInventorySingle.setEnabled(enabled);
        RbInventoryLoop.setEnabled(enabled);
        cbFilter.setEnabled(enabled);
        btnSetFilter.setEnabled(enabled);
        BtClear.setEnabled(enabled);
        cbEPC_Tam.setEnabled(enabled);
    }

   /* @Override
    public void onResume() {
        super.onResume();
        Date  currentDate= getCurrentDate();
        if (hasDateChanged(currentDate)) {
            // Reset count to zero
            count_total=0;
            count_found=0;
            count_missing=0;
            count_unknown=0;
        }
        count_total=count_total+excelTags.size();
        count_found=count_found+scan.size();
        count_missing=count_missing+missingtags.size();
        count_unknown=count_unknown+unknown.size();
        previousDate= currentDate;
    }*/
    /*public ReadFragment(){
        count_total=0;
        count_found=0;
        count_missing=0;
        count_unknown=0;
        date =new Date();
    }*/
    private void stopInventory() {
        if (loopFlag) {
            loopFlag = false;
            setViewEnabled(true);
            if (mContext.mReader.stopInventory()) {
                compareTags();
                MyHistoryDBHelper hdb=new MyHistoryDBHelper(getActivity());
                sharedPreferences = mContext.getSharedPreferences("session", Context.MODE_PRIVATE);
                count_total = sharedPreferences.getInt("count", 0);
                count_found=sharedPreferences.getInt("count1",0);
                count_missing=sharedPreferences.getInt("count2",0);
                count_unknown=sharedPreferences.getInt("count3",0);
                count_clear_history=sharedPreferences.getInt("count_clear",0);
                long previousDateTimeInMillis = sharedPreferences.getLong("previousDate", 0);
                if (previousDateTimeInMillis != 0) {
                    previousDatee = Calendar.getInstance();
                    previousDatee.setTimeInMillis(previousDateTimeInMillis);
                }

                Calendar currentDate = Calendar.getInstance();
                if (hasDateChanged(currentDate)) {
                    // Reset the count to zero
                    count_total=0;
                    count_found=0;
                    count_missing=0;
                    count_unknown=0;

                    count_clear_history++;
                    Log.d("heyyy", String.valueOf(count_clear_history));
                    if (count_clear_history==7){
                        MyHistoryDBHelper myDB = new MyHistoryDBHelper(getActivity());
                        myDB.deleteAllData();
                        MyHistoryTagsDBHelper mhdb=new MyHistoryTagsDBHelper(getActivity());
                        mhdb.deleteAllData();
                        Log.d("heyyy","database refreshed");
                        count_clear_history=0;
                    }
                    Log.d("heyyy","date changed");
                }
                count_total=count_total+excelTags.size();
                count_found=count_found+scan.size();
                count_missing=count_missing+count1;
                count_unknown=count_unknown+unknown.size();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("count", count_total);
                editor.putInt("count1",count_found);
                editor.putInt("count2",count_missing);
                editor.putInt("count3",count_unknown);
                editor.putInt("count_clear",count_clear_history);
                editor.putLong("previousDate", currentDate.getTimeInMillis());
                editor.apply();
                hdb.addBook(datee,count_total,count_found,count_missing,count_unknown);

                history_date.clear();
                history_total.clear();
                history_found.clear();
                history_missing.clear();
                history_unknown.clear();


                history_date.add(datee);
                history_total.addAll(excelTags);
                history_found.addAll(scan);
                history_missing.addAll(missingtags);
                history_unknown.addAll(unknown);

                MyHistoryTagsDBHelper mthd=new MyHistoryTagsDBHelper(getActivity());
                mthd.addBookA(history_date,history_total,history_found,history_missing,history_unknown);
                updateTimeRunnable = new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        updateEndTime();

                        // Schedule the next update after 1 second
                       // handler2.postDelayed(this, 1000);
                    }
                };
                    startTimer();
                BtInventory.setText(mContext.getString(R.string.btInventory));
            } else {
                UIHelper.ToastMessage(mContext, "Stop failure");
            }
            if (scan.size()==0){
                /* SimpleDateFormat mDateFormat2 = new SimpleDateFormat("HH:mm:ss");
                recordEndTime();
                Date endDate = new Date((long) endTime);
                mTimeOut.setText("EndTime: " + mDateFormat2.format(endDate));*/

            }else{
               /*  SimpleDateFormat mDateFormat2 = new SimpleDateFormat("HH:mm:ss");
                recordEndTime();
                Date endDate = new Date((long) endTime);
                mTimeOut.setText("EndTime: " + mDateFormat2.format(endDate));*/

              // compareTags();
            }

        }
    }

    private void startTimer() {
        // Get the current system time in milliseconds
        startTime = System.currentTimeMillis();

        // Update the start time text view (assuming you have a TextView with id "startTimeTextView")
      //  startTimeTextView.setText("Start Time: " + startTimeMillis);

        // Start updating the end time
        handler2.post(updateTimeRunnable);
    }
    private void updateEndTime() {
        // Calculate the elapsed time since the start time
        long currentTimeMillis = System.currentTimeMillis();
        long elapsedTimeSeconds = (currentTimeMillis - startTime) / 1000;

        Calendar calendar = Calendar.getInstance();

// Add seconds to the current time
        SimpleDateFormat mDateFormat2 = new SimpleDateFormat("HH:mm:ss");
        calendar.add(Calendar.SECOND, (int) elapsedTimeSeconds);
        Date endDate=calendar.getTime();
        mTimeOut.setText("EndTime: " + mDateFormat2.format(endDate));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler2.removeCallbacks(updateTimeRunnable);
    }

    private String formatTime(long millis) {
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = (millis / (1000 * 60 * 60)) % 24;

        // Format the time as HH:mm:ss
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void recordEndTime() {
        SharedPreferences sharedPref = mContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Update the end time
         endTime = System.currentTimeMillis();
        editor.putLong("endTime", (long) endTime);

        // Commit the changes
        editor.apply();
    }
   /* private Date getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    private boolean hasDateChanged(Date currentDate) {
        if (previousDate == null) {
            // First run, so date has changed
            return true;
        }

        // Compare the dates using Calendar
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(previousDate);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(currentDate);

        int previousDay = calendar1.get(Calendar.DAY_OF_YEAR);
        int currentDay = calendar2.get(Calendar.DAY_OF_YEAR);

        // Check if the day has changed
        return previousDay != currentDay;
    }*/
    public int checkIsExist(String epc) {
        if (StringUtils.isEmpty(epc)) {
            return -1;
        }
        return binarySearch(tempDatas, epc);
    }


    static int binarySearch(List<String> array, String src) {
        int left = 0;
        int right = array.size() - 1;

        while (left <= right) {
            if (compareString(array.get(left), src)) {
                return left;
            } else if (left != right) {
                if (compareString(array.get(right), src))
                    return right;
            }
            left++;
            right--;
        }
        return -1;
    }

    static boolean compareString(String str1, String str2) {
        if (str1.length() != str2.length()) {
            return false;
        } else if (str1.hashCode() != str2.hashCode()) {
            return false;
        } else {
            char[] value1 = str1.toCharArray();
            char[] value2 = str2.toCharArray();
            int size = value1.length;
            for (int k = 0; k < size; k++) {
                if (value1[k] != value2[k]) {
                    return false;
                }
            }
            return true;
        }
    }

    class TagThread extends Thread {
        public void run() {
            UHFTAGInfo uhftagInfo;
            Message msg;
            while (loopFlag) {
                uhftagInfo = mContext.mReader.readTagFromBuffer();
                if (uhftagInfo != null) {
                    msg = handler.obtainMessage();
                    msg.obj = uhftagInfo;
                    handler.sendMessage(msg);
                    mContext.playSound(1);
                }
            }
        }
    }

    private String mergeTidEpc(String tid, String epc, String user) {
        String data =""+ epc;
        if (!TextUtils.isEmpty(tid) && !tid.equals("0000000000000000") && !tid.equals("000000000000000000000000")) {
            data += "\nTID:" + tid;
        }
        if (user != null && user.length() > 0) {
            data += "\nUSER:" + user;
        }
        return data;
    }

    @Override
    public void myOnKeyDwon() {
        readTag();
    }


    //-----------------------------
    private int selectItem = -1;

    public final class ViewHolder {
        public TextView tvEPCTID;
        public TextView tvTagCount;
        public TextView tvTagRssi;


    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return mContext.tagList.size();
        }

        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return mContext.tagList.get(arg0);
        }

        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.listtag_items, null);
                holder.tvEPCTID = (TextView) convertView.findViewById(R.id.TvTagUii);
                holder.tvTagCount = (TextView) convertView.findViewById(R.id.TvTagCount);
                holder.tvTagRssi = (TextView) convertView.findViewById(R.id.TvTagRssi);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String ascii = TAG_EPC_TID;
            int length = 16 - TAG_EPC_TID.length();

            String existing_string = ascii;
            StringBuilder builder1 = new StringBuilder(existing_string);


            char[] ch = builder1.toString().toCharArray();


            StringBuilder builder = new StringBuilder();

            for (char c : ch) {
                int i = (int) c;

                builder.append(Integer.toHexString(i).toUpperCase());
            }
            holder.tvEPCTID.setText((String) mContext.tagList.get(position).get(TAG_EPC_TID));
            holder.tvTagCount.setText((String) mContext.tagList.get(position).get(TAG_COUNT));
            holder.tvTagRssi.setText((String) mContext.tagList.get(position).get(TAG_RSSI));

            if (position == selectItem) {
                convertView.setBackgroundColor(mContext.getResources().getColor(R.color.lfile_colorPrimary));
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
            holder.tvEPCTID.setTextColor(Color.BLACK);
            holder.tvTagCount.setTextColor(Color.BLACK);
            holder.tvTagRssi.setTextColor(Color.BLACK);
            return convertView;
        }

        public void setSelectItem(int select) {
            if (selectItem == select) {
                selectItem = -1;
                mContext.bookinfo.setSelectItem("");
                mContext.bookinfo.setSelectIndex(selectItem);
            } else {
                selectItem = select;
                mContext.bookinfo.setSelectItem(mContext.tagList.get(select).get(TAG_EPC));
                mContext.bookinfo.setSelectIndex(selectItem);
            }

        }

        /*public void callDataToASCI() {
            if (tvEPCTID.isEmpty()) {
               // TAG_EPC_TID.("Data cannot be empty");
                Toast.makeText(mContext, "Data cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                String ascii = TAG_EPC_TID;
                int length = 16 - TAG_EPC_TID.length();

                String existing_string = ascii;
                StringBuilder builder1 = new StringBuilder(existing_string);
        *//*    for (int i = 0; i < length; i++) {
                builder1.append(" ");
            }
*//*
                // Step-1 - Convert ASCII string to char array
                char[] ch = builder1.toString().toCharArray();

                // Step-2 Iterate over char array and cast each element to Integer.
                StringBuilder builder = new StringBuilder();

                for (char c : ch) {
                    int i = (int) c;
                    // Step-3 Convert integer value to hex using toHexString() method.
                    builder.append(Integer.toHexString(i).toUpperCase());
                }

                System.out.println("ASCII = " + builder1.toString());
                System.out.println("Hex = " + builder.toString());
                Log.d("Mi","ASCII = " + builder1.toString());
               LvTags.setFilterText(builder1.toString());

            }
        }*/
    }

    @SuppressLint({"SuspiciousIndentation", "SetTextI18n"})
    public void ReadExcelFile(Context context, Uri uri) {
        try {
            Log.d("SOP", "Starting ReadExcelFile...");

            InputStream inStream;
            Workbook wb = null;

            try {
                Log.d("SOP", "Opening input stream from URI...");
                inStream = context.getContentResolver().openInputStream(uri);

                Log.d("SOP", "Checking file type...");
                if (fileType == extensionXLS) {
                    Log.d("SOP", "Detected XLS file.");
                    wb = new HSSFWorkbook(inStream);
                } else {
                    Log.d("SOP", "Detected XLSX file.");
                    wb = new XSSFWorkbook(inStream);
                }

                inStream.close();
                Log.d("SOP", "Input stream closed.");
            } catch (IOException e) {
                Log.e("SOP", "Error reading file: " + e.getMessage());
                e.printStackTrace();
            }

            if (wb == null) {
                Log.e("SOP", "Workbook is null. Exiting.");
                return;
            }

            Log.d("SOP", "Reading sheet 0...");
            Sheet sheet1 = wb.getSheetAt(0);

            productTagList = new ArrayList<>();

            for (int i = 1; i <= sheet1.getLastRowNum(); i++) {
                Row row = sheet1.getRow(i);
                if (row == null) {
                    Log.w("SOP", "Row " + i + " is null. Skipping.");
                    continue;
                }

                Log.d("SOP", "Processing row " + i + "...");

                String tag = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
                String name = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
                String boxEPC = getCellAsString(row.getCell(2));
                String leftEPC = getCellAsString(row.getCell(3));
                String rightEPC = getCellAsString(row.getCell(4));

                Log.d("SOP", "Parsed: TAG=" + tag + ", Name=" + name + ", Box=" + boxEPC + ", Left=" + leftEPC + ", Right=" + rightEPC);

                ProductStatus status = new ProductStatus(name, boxEPC, leftEPC, rightEPC);
                productTagList.add(status);

                if (!boxEPC.isEmpty()) epcToTitleMap.put(boxEPC, name);
                if (!leftEPC.isEmpty()) epcToTitleMap.put(leftEPC, name);
                if (!rightEPC.isEmpty()) epcToTitleMap.put(rightEPC, name);
            }

            Log.d("SOP", "Finished processing all rows.");
            Log.d("SOP", "productTagList size: " + productTagList.size());

            int count = productTagList.size();
            mTotalTags.setText("" + count);
            Log.d("SOP", "mTotalTags updated with count " + count);

        } catch (Exception ex) {
            Log.e("SOP", "Exception occurred during ReadExcelFile: " + ex.getMessage());
            Toast.makeText(mContext, "ReadExcelFile Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }

    }

    private String getCellAsString(Cell cell) {
        if (cell == null) return "";

        CellType cellType = CellType.forInt(cell.getCellType());

        switch (cellType) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString().trim();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue()).trim();
            case FORMULA:
                return cell.getCellFormula().trim();
            case BLANK:
            default:
                return "";
        }
    }



// create map only with epic

    private void compareTags() {
        int foundCount = 0;
        int partialCount = 0;
        int missingCount = 0;

        Set<String> knownEPCs = new HashSet<>();
        for (ProductStatus product : productTagList) {
            knownEPCs.add(product.boxEPC);
            knownEPCs.add(product.leftEPC);
            knownEPCs.add(product.rightEPC);
        }
        Log.d("compareTags", "knownEPCs: " + knownEPCs);

        Set<String> scannedSet = new HashSet<>();
        for (String tag : scan) {
            if (knownEPCs.contains(tag)) {
                scannedSet.add(tag);
            }
        }

        Log.d("compareTags", "scannedSet: " + scannedSet);

        missingtags.clear();
        missingtagsobj.clear();

        for (ProductStatus product : productTagList) {
            boolean isBoxFound = scannedSet.contains(product.boxEPC);
            boolean isLeftFound = scannedSet.contains(product.leftEPC);
            boolean isRightFound = scannedSet.contains(product.rightEPC);

            Log.d("compareTags", "🔎 Checking: " + product.productTitle);
            Log.d("compareTags", "Box EPC: " + product.boxEPC + " -> " + isBoxFound);
            Log.d("compareTags", "Left EPC: " + product.leftEPC + " -> " + isLeftFound);
            Log.d("compareTags", "Right EPC: " + product.rightEPC + " -> " + isRightFound);

            if (isBoxFound && isLeftFound && isRightFound) {
                foundCount++;
                Log.d("compareTags", "foundCount: " + foundCount);
            } else {
                ProductStatus status = new ProductStatus(
                        product.productTitle, isBoxFound, isLeftFound, isRightFound
                );
                status.boxEPC = product.boxEPC;
                status.leftEPC = product.leftEPC;
                status.rightEPC = product.rightEPC;

                if (!isBoxFound && !isLeftFound && !isRightFound) {
                    missingtagsobj.add(status);
                    Log.d("compareTags", "➕ Added to missingtagsobj: " + product.productTitle);
                    missingCount++;
                    Log.d("compareTags", "missingCount: " + missingCount);
                } else {
                    partialCount++;
                    Log.d("compareTags", "partialCount: " + partialCount);
                }
            }
        }

        Set<String> unknownTags = new HashSet<>(scan);
        unknownTags.removeAll(knownEPCs); // Remove known tags

        Log.d("compareTags", "🧩 Unknown EPCs count: " + unknownTags.size());
        for (String unknown : unknownTags) {
            Log.d("compareTags", "❓ Unknown Tag: " + unknown);
        }

        // Optional: show unknown tag count in UI (if you have a TextView)
        // mUnknownTags.setText(String.valueOf(unknownTags.size()));

        // Show known results in UI
        mFoundTags.setText(String.valueOf(foundCount));
        mMissingTags.setText(String.valueOf(missingCount));
        mPartialTags.setText(String.valueOf(partialCount));
    }
}