package com.example.myexcel

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myexcel.Database.DatabaseHelper
import com.example.myexcel.Database.SKU
import com.example.myexcel.Database.TransactionData
import com.example.myexcel.Network.ApiService
import com.example.myexcel.adapter.TransactionAdapter
import okhttp3.ResponseBody
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var apiService: ApiService
    private val context: Context = this

    // Declare UI elements
    private lateinit var truckNoEditText: EditText
    private lateinit var inDateEditText: EditText
    private lateinit var inTimeEditText: EditText
    private lateinit var outDateEditText: EditText
    private lateinit var outTimeEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var exportButton: Button // New export button
    private lateinit var btnSync: ImageButton
    private lateinit var searchableView: TextView
    private lateinit var skuName: TextView
    private lateinit var rdunloading: RadioButton
    private lateinit var rdloading: RadioButton
    private lateinit var rdWithQR: RadioButton
    private lateinit var rdWithoutQR: RadioButton
    private lateinit var add: Button// to add data in to the recycler view
    private lateinit var clear: Button
    private var skuCodes: ArrayList<String> = ArrayList()
    private var IS_SYNCED: Boolean = false
    private var SELECTED_ITEM: String = ""
    private var default_source_item: String = "Select SKU Code"
    private var IS_UNLOADING: Boolean = false
    private var IS_LOADING: Boolean = false
    private var IS_WITHQR: Boolean = false
    private var IS_WITHOUTQR: Boolean = false
    private var IS_PROCESS_CHECKED: Boolean = false
    private var IS_QR_TYPE_CHECKED: Boolean = false
    private var qrScanType: String = ""
    private var processType: String = ""
    private lateinit var quantityInput: EditText

    private lateinit var transactionList: MutableList<TransactionData>
    private lateinit var transactionAdapter: TransactionAdapter



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        // here i have introduced recycler view logic
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        // Initialize the list and adapter
        transactionList = mutableListOf()
        transactionAdapter = TransactionAdapter(transactionList) { position ->
            // Handle delete action
            transactionList.removeAt(position)
            transactionAdapter.notifyItemRemoved(position)
        }

// Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = transactionAdapter

// Form Fields
        val skuCodeField: TextView = findViewById(R.id.searchableView)
        val skuNameField: TextView = findViewById(R.id.sku_name)
        val quantityField: EditText = findViewById(R.id.quantityinput)

// Add Button to add data in recyceler view
        val addButton: Button = findViewById(R.id.addtoview)
        addButton.setOnClickListener {
            val skuCode = skuCodeField.text.toString().trim()
            val skuName = skuNameField.text.toString().trim()
            val quantity = quantityField.text.toString().trim()

            // Validate fields
            if (skuCode.isNotEmpty() && skuName.isNotEmpty() && quantity.isNotEmpty()) {
                // Add data to the list
                transactionList.add(TransactionData(skuCode = skuCode, skuName = skuName, quantity = quantity))
                transactionAdapter.notifyItemInserted(transactionList.size - 1)

                // Clear fields after adding
                skuCodeField.text = "" // Assuming you want to clear TextView
                skuNameField.text = "" // Assuming you want to clear TextView
                quantityField.text.clear()
            } else {
                // Show error message
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Initialize DatabaseHelper
        databaseHelper = DatabaseHelper(this)
        skuCodes = databaseHelper.getAllSKUCode() as ArrayList<String>
        Log.e("sku", (skuCodes as ArrayList<String>).toString())

        // Bind UI elements
        truckNoEditText = findViewById(R.id.truck_no)
        skuName = findViewById(R.id.sku_name)
        searchableView = findViewById(R.id.searchableView)
        inDateEditText = findViewById(R.id.in_date)
        inTimeEditText = findViewById(R.id.in_time)
        outDateEditText = findViewById(R.id.out_date)
        outTimeEditText = findViewById(R.id.out_time)
        saveButton = findViewById(R.id.save_button)
        exportButton = findViewById(R.id.export_button)
        btnSync = findViewById(R.id.btnSync)
        rdunloading = findViewById(R.id.rdul)
        rdloading = findViewById(R.id.rdl)
        rdWithQR = findViewById(R.id.rdQR)
        rdWithoutQR = findViewById(R.id.rdwithoutQR)
        quantityInput = findViewById(R.id.quantityinput)
        add= findViewById(R.id.addtoview)
        clear=findViewById(R.id.clear_button)


//        val skuNameInput = findViewById<EditText>(R.id.tvSkuName)
//        val skuCodeInput = findViewById<EditText>(R.id.tvSkuCode)
//        val quantityInput = findViewById<EditText>(R.id.tvQuantity)
//        val listView = findViewById<ListView>(R.id.LvTags)




        if (skuCodes.size > 0) {
            IS_SYNCED = true
            btnSync.visibility = View.GONE
        } else {
            IS_SYNCED = false
            btnSync.visibility = View.VISIBLE
        }

        btnSync.setOnClickListener {
            //0getSKUMaster()
            readExcelAndStoreSKUMaster(context)

        }


        searchableView.setOnClickListener {
            // Initialize dialog
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_searchable_spinner)
            dialog.window?.setLayout(1000, 800)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            // Initialize and assign variables
            val editText = dialog.findViewById<EditText>(R.id.edit_text)
            val listView = dialog.findViewById<ListView>(R.id.list_view)

            // Initialize array adapter
            val searchableAdapter = SearchableAdapter(this, skuCodes)
            listView.adapter = searchableAdapter

            // Add text change listener to filter items
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    searchableAdapter.filter.filter(s)
                }

                override fun afterTextChanged(s: Editable?) {}
            })




            // Set item click listener
            listView.setOnItemClickListener { _, _, position, _ ->
                if (!truckNoEditText.text.toString().equals("")) {
                    if (IS_SYNCED) {
                        dialog.dismiss()
                        SELECTED_ITEM = searchableAdapter.getItem(position) as String
                        searchableView.text = SELECTED_ITEM

                        if (SELECTED_ITEM.equals(
                                default_source_item,
                                ignoreCase = true
                            ) || SELECTED_ITEM.isEmpty()
                        ) {
                            SELECTED_ITEM = ""
                        } else {
                            skuName.text = databaseHelper.getSKUNameBySKUCode(SELECTED_ITEM)
                        }
                    } else {
                        Toast.makeText(this, "Please sync SKUs", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Please enter Truck No", Toast.LENGTH_SHORT).show()
                }

            }
        }
        rdunloading.setOnClickListener { view ->
            handleRadioButtonClick(view, "Process")
        }
        rdloading.setOnClickListener { view ->
            handleRadioButtonClick(view, "Process")
        }
        rdWithQR.setOnClickListener { view ->
            handleRadioButtonClick(view, "QRType")
        }
        rdWithoutQR.setOnClickListener { view ->
            handleRadioButtonClick(view, "QRType")
        }

        // Date picker dialog for "In Date" and "Out Date"
        inDateEditText.setOnClickListener { showDatePickerDialog(inDateEditText) }
        outDateEditText.setOnClickListener { showDatePickerDialog(outDateEditText) }

        // Time picker dialog for "In Time" and "Out Time" (Analog Clock Style)
        inTimeEditText.setOnClickListener { showTimePickerDialog(inTimeEditText) }
        outTimeEditText.setOnClickListener { showTimePickerDialog(outTimeEditText) }

        // Save button click listener
        saveButton.setOnClickListener {
            // Show confirmation dialog
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmation")
            builder.setMessage("Do you want to save?")

            // Set "Yes" button
            builder.setPositiveButton("Yes") { dialog, _ ->
                // Proceed with saving if the user confirms
                saveTransactionData()
                dialog.dismiss() // Close the dialog after action
            }

            // Set "No" button
            builder.setNegativeButton("No") { dialog, _ ->
                // Just dismiss the dialog, do nothing
                dialog.dismiss()
            }

            // Show the dialog
            builder.show()
        }


        clear.setOnClickListener {
            // Show confirmation dialog
            AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to clear all fields?")
                .setPositiveButton("Yes") { _, _ ->
                    // Clear all fields if the user confirms
                    skuName.text = ""
                    searchableView.text = default_source_item
                    inDateEditText.text.clear()
                    inTimeEditText.text.clear()
                    outDateEditText.text.clear()
                    outTimeEditText.text.clear()
                    quantityInput.text.clear()

                    // Reset radio buttons
                    rdloading.isChecked = false
                    rdunloading.isChecked = false
                    rdWithQR.isChecked = false
                    rdWithoutQR.isChecked = false

                    // Reset flags
                    SELECTED_ITEM = ""
                    IS_PROCESS_CHECKED = false
                    IS_QR_TYPE_CHECKED = false
                    IS_WITHQR = false
                    IS_WITHOUTQR = false
                    IS_LOADING = false
                    IS_UNLOADING = false

                    Toast.makeText(this, "Fields Cleared", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No") { dialog, _ ->
                    // Dismiss the dialog if the user cancels
                    dialog.dismiss()
                }
                .create()
                .show()
        }


        // Export button click listener
        exportButton.setOnClickListener {
            // Show confirmation dialog
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmation")
            builder.setMessage("Do you want to export the data?")

            // Set "Yes" button
            builder.setPositiveButton("Yes") { dialog, _ ->
                val transactions: List<TransactionData> = databaseHelper.getAllTransactions()
                Log.e("DBTrans", transactions.toString())
                if (transactions.isNotEmpty()) {
                    exportToExcel(transactions)
                } else {
                    Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss() // Close the dialog after action
            }

            // Set "No" button
            builder.setNegativeButton("No") { dialog, _ ->
                // Just dismiss the dialog, do nothing
                dialog.dismiss()
            }

            // Show the dialog
            builder.show()
        }


    }


    // Function to show DatePickerDialog
    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                editText.setText(formattedDate)
            }, year, month, day)

        datePickerDialog.show()
    }

    // Function to show TimePickerDialog
    private fun showTimePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val amPm = if (selectedHour < 12) "AM" else "PM"
            val formattedHour =
                if (selectedHour > 12) selectedHour - 12 else if (selectedHour == 0) 12 else selectedHour
            val formattedTime = String.format("%02d:%02d %s", formattedHour, selectedMinute, amPm)
            editText.setText(formattedTime)
        }, hour, minute, false)

        timePickerDialog.show()
    }

    private fun saveTransactionData() {
        // Reset field backgrounds
        resetFieldBackgrounds()

        // Check if the RecyclerView has data
        if (transactionList.isEmpty()) {
            Toast.makeText(this, "No transactions to save", Toast.LENGTH_SHORT).show()
            return
        }

        // Get input field values
        val truckNo = truckNoEditText.text.toString().trim()
        val inDate = inDateEditText.text.toString().trim()
        val inTime = inTimeEditText.text.toString().trim()
        val outDate = outDateEditText.text.toString().trim()
        val outTime = outTimeEditText.text.toString().trim()

        var isValid = true

        // Validate fields and visually highlight errors
        if (truckNo.isEmpty()) {
            showFieldError(truckNoEditText)
            isValid = false
        }
        if (inDate.isEmpty()) {
            showFieldError(inDateEditText)
            isValid = false
        }
        if (inTime.isEmpty()) {
            showFieldError(inTimeEditText)
            isValid = false
        }
        if (outDate.isEmpty()) {
            showFieldError(outDateEditText)
            isValid = false
        }
        if (outTime.isEmpty()) {
            showFieldError(outTimeEditText)
            isValid = false
        }
        if (!IS_PROCESS_CHECKED) {
            Toast.makeText(this, "Please select a process type", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        if (!IS_QR_TYPE_CHECKED) {
            Toast.makeText(this, "Please select a QR type", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (!isValid) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Save transactions if all fields are valid
        for (transaction in transactionList) {
            val transactionToSave = transaction.copy(
                truckNo = truckNo,
                inDate = inDate,
                inTime = inTime,
                outDate = outDate,
                outTime = outTime,
                loadingType = processType,
                qrScanType = qrScanType
            )

            val isSaved = databaseHelper.insertData(transactionToSave)
            if (!isSaved) {
                Toast.makeText(this, "Failed to save some data", Toast.LENGTH_SHORT).show()
                return
            }
        }

        Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()

        // Clear the RecyclerView data
        transactionList.clear()
        transactionAdapter.notifyDataSetChanged()

        // Clear all input fields
        clearFields()
    }

//This fn will clear all the fields after saving
    private fun clearFields() {
        // Clear text fields
        truckNoEditText.text.clear()
        inDateEditText.text.clear()
        inTimeEditText.text.clear()
        outDateEditText.text.clear()
        outTimeEditText.text.clear()

        // Reset radio button selections
        rdunloading.isChecked = false
        rdloading.isChecked = false
        rdWithQR.isChecked = false
        rdWithoutQR.isChecked = false

        IS_PROCESS_CHECKED = false
        IS_QR_TYPE_CHECKED = false
    }



    // Reset field backgrounds to default
    private fun resetFieldBackgrounds() {
        truckNoEditText.setBackgroundResource(R.drawable.default_background)
        inDateEditText.setBackgroundResource(R.drawable.default_background)
        inTimeEditText.setBackgroundResource(R.drawable.default_background)
        outDateEditText.setBackgroundResource(R.drawable.default_background)
        outTimeEditText.setBackgroundResource(R.drawable.default_background)
    }

    // Show visual error for missing fields
    private fun showFieldError(field: EditText) {
        field.setBackgroundResource(R.drawable.error_background)
    }





    // this function will highlight the entire field in red colour
//    private fun highlightField(editText: EditText, highlight: Boolean) {
//        if (highlight) {
//            // Set background color to red or any other color to indicate an error
//            editText.setBackgroundColor(Color.parseColor("#FFCDD2")) // Light Red
//        } else {
//            // Reset background to default
//            editText.setBackgroundColor(Color.TRANSPARENT)
//        }
//    }




//    // Function to save transaction data
//    private fun saveTransactionData() {
//        // Get data from input fields
//        val truckNo = truckNoEditText.text.toString().trim()
//        val skuName = skuName.text.toString().trim()
//        val skuCode = searchableView.text.toString().trim()
//        val inDate = inDateEditText.text.toString().trim()
//        val inTime = inTimeEditText.text.toString().trim()
//        val outDate = outDateEditText.text.toString().trim()
//        val outTime = outTimeEditText.text.toString().trim()
//        val quantity= quantityInput.text.toString().trim()
//
//
//        //Data to check whether all the data is filled or not
//        if (IS_PROCESS_CHECKED) {
//            if (truckNo.isNotEmpty()) {
//                if (skuCode.isNotEmpty() || skuName.isNotEmpty()) {
//                    if (inDate.isNotEmpty()) {
//                        if (inTime.isNotEmpty()) {
//                            if (outDate.isNotEmpty()) {
//                                if (outTime.isNotEmpty()) {
//                                    if (IS_QR_TYPE_CHECKED) {
//                                        // Check if quantity is not empty
//                                        if (quantity.isNotEmpty()) {
//                                            // Create TransactionData object
//                                            val transaction = TransactionData(
//                                                truckNo = truckNo,
//                                                skuName = skuName,
//                                                skuCode = skuCode,
//                                                inDate = inDate,
//                                                inTime = inTime,
//                                                outDate = outDate,
//                                                outTime = outTime,
//                                                quantity = quantity,
//                                                qrScanType = qrScanType,
//                                                loadingType = processType
//                                            )
//                                            Log.e("Transaction", transaction.toString())
//
//                                            // Insert into database
//                                            val isSaved = databaseHelper.insertData(transaction)
//                                            if (isSaved) {
//                                                Toast.makeText(
//                                                    this,
//                                                    "Data saved successfully",
//                                                    Toast.LENGTH_SHORT
//                                                ).show()
//                                                clearFields()
//                                            } else {
//                                                Toast.makeText(
//                                                    this,
//                                                    "Failed to save data",
//                                                    Toast.LENGTH_SHORT
//                                                ).show()
//                                            }
//                                        } else {
//                                            // Quantity is empty
//                                            Toast.makeText(this, "Please enter a quantity", Toast.LENGTH_SHORT).show()
//                                        }
//                                    } else {
//                                        Toast.makeText(this, "Please select QR type", Toast.LENGTH_SHORT).show()
//                                    }
//                                } else {
//                                    Toast.makeText(this, "Please select out time", Toast.LENGTH_SHORT).show()
//                                }
//                            } else {
//                                Toast.makeText(this, "Please select out date", Toast.LENGTH_SHORT).show()
//                            }
//                        } else {
//                            Toast.makeText(this, "Please select in time", Toast.LENGTH_SHORT).show()
//                        }
//                    } else {
//                        Toast.makeText(this, "Please select in date", Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    Toast.makeText(this, "Please select SKU code", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                Toast.makeText(this, "Please enter truck no", Toast.LENGTH_SHORT).show()
//            }
//        } else {
//            Toast.makeText(this, "Please select a process type", Toast.LENGTH_SHORT).show()
//        }
//    }



    // Function to clear input fields after saving
//    private fun clearFields() {
//        //truckNoEditText.text.clear()
//        skuName.text = ""
//        searchableView.text = default_source_item
//        inDateEditText.text.clear()
//        inTimeEditText.text.clear()
//        outDateEditText.text.clear()
//        outTimeEditText.text.clear()
//        SELECTED_ITEM = ""
//        IS_PROCESS_CHECKED = false
//        IS_QR_TYPE_CHECKED = false
//        IS_WITHQR = false
//        IS_WITHOUTQR = false
//        IS_LOADING = false
//        IS_UNLOADING = false
//        rdloading.isChecked = false
//        rdunloading.isChecked = false
//        rdWithQR.isChecked = false
//        rdWithoutQR.isChecked = false
//        quantityInput.text.clear()
//    }


//this is the method for calling get api
    private fun getSKUMaster() {
        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.113/GRB14/") // Replace with actual base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
        val call: Call<ResponseBody> = apiService.getSkuData()
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.isSuccessful && response.body() != null) {
                    try {
                        val json = response.body()!!.string()
                        val jsonObject = JSONObject(json)
                        Log.e("SKUResponse", jsonObject.toString())
                        if (jsonObject.has("status")) {
                            val status: Boolean = jsonObject.getBoolean("status")
                            if (status) {
                                if (jsonObject.has("data")) {
                                    val jsonArray: JSONArray = jsonObject.getJSONArray("data")
                                    if (jsonArray.length() > 0) {
                                        val skuList: MutableList<SKU> = mutableListOf()
                                        for (i in 0 until jsonArray.length()) {
                                            val sku: SKU = SKU()
                                            val obj: JSONObject = jsonArray.getJSONObject(i)
                                            if (obj.has("ItemName")) {
                                                sku.skuName = obj.get("ItemName").toString()
                                            }
                                            if (obj.has("ItemCode")) {
                                                sku.skuCode = obj.get("ItemCode").toString()
                                            }
                                            skuList.add(sku)
                                        }
                                        if (skuList.isNotEmpty()) {
                                            databaseHelper.deleteSKUMaster()
                                            databaseHelper.storeSKUMaster(skuList)
                                            skuCodes =
                                                databaseHelper.getAllSKUCode() as ArrayList<String>
                                        }
                                    }
                                }
                                Toast.makeText(
                                    this@MainActivity,
                                    "Successfully synced SKU Data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    jsonObject.getString("message"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@MainActivity,
                            "Something went wrong",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                // Handle failure
                Toast.makeText(this@MainActivity, call.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }



    // fun to export into excel
    private fun exportToExcel(transactionData: List<TransactionData>) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Transactions")

        // Create header row
        val headerRow: Row = sheet.createRow(0)

        // Create a CellStyle for header with background color and bold text
        val headerCellStyle = workbook.createCellStyle()
        val headerFont = workbook.createFont().apply {
            color = IndexedColors.WHITE.index // White font color
            bold = true // Bold font
        }
        headerCellStyle.setFont(headerFont)

        // Set background color for header row
        headerCellStyle.fillForegroundColor = IndexedColors.BLUE.index // Blue background
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND) // Set fill pattern correctly

        // Set header cell values
        headerRow.createCell(0).setCellValue("Truck No")
        headerRow.createCell(1).setCellValue("SKU Name")
        headerRow.createCell(2).setCellValue("SKU Code")
        headerRow.createCell(3).setCellValue("In Date")
        headerRow.createCell(4).setCellValue("In Time")
        headerRow.createCell(5).setCellValue("Out Date")
        headerRow.createCell(6).setCellValue("Out Time")
        headerRow.createCell(7).setCellValue("QR Scan Type")
        headerRow.createCell(8).setCellValue("Process Type")
        headerRow.createCell(9).setCellValue("quantity")

        // Apply the header style to each cell in the header row
        for (i in 0 until 10) {
            headerRow.getCell(i).cellStyle = headerCellStyle
        }

        // Fill the data rows
        for (i in transactionData.indices) {
            val transaction = transactionData[i]
            val row: Row = sheet.createRow(i + 1)
            row.createCell(0).setCellValue(transaction.truckNo)
            row.createCell(1).setCellValue(transaction.skuName)
            row.createCell(2).setCellValue(transaction.skuCode)
            row.createCell(3).setCellValue(transaction.inDate)
            row.createCell(4).setCellValue(transaction.inTime)
            row.createCell(5).setCellValue(transaction.outDate)
            row.createCell(6).setCellValue(transaction.outTime)
            row.createCell(7).setCellValue(transaction.qrScanType)
            row.createCell(8).setCellValue(transaction.loadingType)
            row.createCell(9).setCellValue(transaction.quantity)
        }

        // here it will take the uploaded excel file ie in asset folder and save it to my database
        val contentResolver = contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "Transactions.xlsx")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/")  // Save in Downloads folder
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 and above, use MediaStore (no need for WRITE_EXTERNAL_STORAGE permission)
            val uri: Uri? = contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
            uri?.let {
                try {
                    contentResolver.openOutputStream(it)?.use { outputStream ->
                        workbook.write(outputStream)
                        databaseHelper.deleteTransactionData()  // Optionally clear transaction data after export
                        Toast.makeText(this, "Excel file exported successfully to Downloads folder", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed to export Excel file", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // For Android 9 and below, use direct file path (WRITE_EXTERNAL_STORAGE permission required)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Transactions.xlsx")
                var fileOutputStream: FileOutputStream? = null
                try {
                    fileOutputStream = FileOutputStream(file)
                    workbook .write(fileOutputStream)
                    Toast.makeText(this, "Excel file exported successfully to Downloads folder", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed to export Excel file", Toast.LENGTH_SHORT).show()
                } finally {
                    fileOutputStream?.close()
                }
            } else {
                // Request permission if not granted
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            }
        }
    }

    private fun handleRadioButtonClick(view: View, action: String) {
        val checked: Boolean = (view as RadioButton).isChecked
        if (action.equals("Process")) {
            IS_PROCESS_CHECKED = true
            if (view.id == R.id.rdul) {
                if (checked) {
                    IS_UNLOADING = true
                    IS_LOADING = false
                    rdloading.isChecked = false
                    processType = "Unloading"
                }
            } else if (view.id == R.id.rdl) {
                if (checked) {
                    IS_UNLOADING = false
                    IS_LOADING = true
                    rdunloading.isChecked = false
                    processType = "Loading"
                }
            }
        } else if (action.equals("QRType")) {
            IS_QR_TYPE_CHECKED = true
            if (view.id == R.id.rdQR) {
                if (checked) {
                    IS_WITHQR = true
                    IS_WITHOUTQR = false
                    rdWithoutQR.isChecked = false
                    qrScanType = "With QR"
                }
            } else if (view.id == R.id.rdwithoutQR) {
                if (checked) {
                    IS_WITHQR = false
                    IS_WITHOUTQR = true
                    rdWithQR.isChecked = false
                    qrScanType = "Without QR"
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        databaseHelper.close()
    }

    override fun onBackPressed() {
        databaseHelper.close()
        super.onBackPressed()
    }

    fun readExcelAndStoreSKUMaster(context: Context) {
        try {
            // Open the Excel file from assets
            val inputStream: InputStream = context.assets.open("skudata.xlsx")
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0) // Access the first sheet

            val skuList = mutableListOf<SKU>() // Create a list to hold SKU objects

            // Loop through each row in the Excel sheet
            for (row in sheet) {
                if (row.rowNum == 0) continue // Skip the header row

                val skuCode = row.getCell(0)?.toString()?.trim() ?: "" // Read SKU Code
                val skuName = row.getCell(1)?.toString()?.trim() ?: "" // Read SKU Name
                val skus : SKU = SKU()
                if (skuCode.isNotEmpty() && skuName.isNotEmpty()) {
                    skus.skuCode = skuCode
                    skus.skuName = skuName
                }
                skuList.add(skus)
            }
            Log.e("sksuList", skuList.toString())
            databaseHelper.deleteSKUMaster()
            databaseHelper.storeSKUMaster(skuList)
            skuCodes = databaseHelper.getAllSKUCode() as ArrayList<String>
            Log.e("sku1", skuCodes .toString())
            if(skuCodes.size>0){
                IS_SYNCED=true
                btnSync.visibility = View.GONE
                Toast.makeText(context, "Successfully Synced", Toast.LENGTH_SHORT).show()
            } else{
                IS_SYNCED=false
                btnSync.visibility = View.VISIBLE
                Toast.makeText(context, "Failed to sync data", Toast.LENGTH_SHORT).show()
            }

            // Confirm success
            println("SKU data imported successfully")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to import SKU data: ${e.message}")
        }
    }

}