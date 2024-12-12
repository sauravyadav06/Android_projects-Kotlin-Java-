package com.example.myexcel.Database

import org.apache.xmlbeans.impl.inst2xsd.util.Type

data class TransactionData (
     var truckNo: String = "",
     var skuName: String = "",
     var skuCode: String = "",
     var inDate: String = "",
     var inTime: String = "",
     var outDate: String = "",
     var outTime: String = "",
     var qrScanType: String = "",
     var loadingType: String ="",
     var quantity : String=""
)

