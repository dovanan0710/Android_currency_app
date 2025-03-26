package com.example.doi_tien_app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.doi_tien_app.R
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var editTextFromAmount: EditText// lateinit biến được khai báo sẽ được khởi tạo sau
    private lateinit var editTextToAmount: EditText
    private lateinit var spinnerFromCurrency: Spinner
    private lateinit var spinnerToCurrency: Spinner
    private lateinit var textViewExchangeRate: TextView
    private lateinit var textViewFromSymbol: TextView
    private lateinit var textViewToSymbol: TextView

    // Danh sách các đồng tiền
    private val currencies = arrayOf("United States - Dollar", "Vietnam - Dong", "European Union - Euro", "Japan - Yen", "United Kingdom - Pound")

    // Danh sách ký hiệu tiền tệ
    private val currencySymbols = mapOf(
        "United States - Dollar" to "$",
        "Vietnam - Dong" to "₫",
        "European Union - Euro" to "€",
        "Japan - Yen" to "¥",
        "United Kingdom - Pound" to "£"
    )

    // Tỷ giá cố định so với USD
    private val exchangeRates = mapOf(
        "United States - Dollar" to 1.0,
        "Vietnam - Dong" to 23185.0,
        "European Union - Euro" to 0.91,
        "Japan - Yen" to 108.85,
        "United Kingdom - Pound" to 0.77
    )
    // Danh sách mã tiền tệ viết tắt
    private val currencyCodes = mapOf(
        "United States - Dollar" to "USD",
        "Vietnam - Dong" to "VND",
        "European Union - Euro" to "EUR",
        "Japan - Yen" to "JPY",
        "United Kingdom - Pound" to "GBP"
    )

    private var isUpdating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextFromAmount = findViewById(R.id.editTextFromAmount)
        editTextToAmount = findViewById(R.id.editTextToAmount)
        spinnerFromCurrency = findViewById(R.id.spinnerFromCurrency)
        spinnerToCurrency = findViewById(R.id.spinnerToCurrency)
        textViewExchangeRate = findViewById(R.id.textViewExchangeRate)
        textViewFromSymbol = findViewById(R.id.textViewFromSymbol)
        textViewToSymbol = findViewById(R.id.textViewToSymbol)

        // Thiết lập adapter cho các spinner
        val adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerFromCurrency.adapter = adapter
        spinnerToCurrency.adapter = adapter

        // Thiết lập giá trị mặc định
        spinnerFromCurrency.setSelection(0) // USD
        spinnerToCurrency.setSelection(1) // VND

        // Thiết lập sự kiện khi thay đổi giá trị trên EditText
        editTextFromAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating) {
                    convertCurrency()
                }
            }
        })

        // Thiết lập sự kiện khi thay đổi đồng tiền nguồn
        spinnerFromCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val fromCurrency = currencies[position]
                textViewFromSymbol.text = currencySymbols[fromCurrency]
                convertCurrency()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Thiết lập sự kiện khi thay đổi đồng tiền đích
        spinnerToCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val toCurrency = currencies[position]
                textViewToSymbol.text = currencySymbols[toCurrency]
                convertCurrency()
            }
//ok
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // Phương thức chuyển đổi tiền tệ
    private fun convertCurrency() {
        isUpdating = true

        try {
            // Lấy giá trị nhập vào
            val amountStr = editTextFromAmount.text.toString()
            if (amountStr.isEmpty()) {
                editTextToAmount.setText("0")
                updateExchangeRateInfo()
                isUpdating = false
                return
            }

            val amount = amountStr.toDouble()

            // Lấy đồng tiền nguồn và đích
            val fromCurrency = currencies[spinnerFromCurrency.selectedItemPosition]
            val toCurrency = currencies[spinnerToCurrency.selectedItemPosition]

            // Tính toán
            val fromRate = exchangeRates[fromCurrency] ?: 1.0
            val toRate = exchangeRates[toCurrency] ?: 1.0

            // Chuyển đổi sang USD làm trung gian
            val amountInUSD = amount / fromRate
            val result = amountInUSD * toRate

            // Hiển thị kết quả
            val formatter = DecimalFormat("#,##0.00")
            editTextToAmount.setText(formatter.format(result))

            // Cập nhật thông tin tỷ giá
            updateExchangeRateInfo()
        } catch (e: NumberFormatException) {
            editTextToAmount.setText("0")
        }

        isUpdating = false
    }

    // Cập nhật thông tin tỷ giá hiển thị
    private fun updateExchangeRateInfo() {
        val fromCurrency = currencies[spinnerFromCurrency.selectedItemPosition]
        val toCurrency = currencies[spinnerToCurrency.selectedItemPosition]

        val fromRate = exchangeRates[fromCurrency] ?: 1.0
        val toRate = exchangeRates[toCurrency] ?: 1.0

        val rate = toRate / fromRate

        val formatter = DecimalFormat("#,##0.00")
        // lấy mã tiền tệ để hiển
        val fromCode=currencyCodes[fromCurrency]?:fromCurrency
        val toCode=currencyCodes[toCurrency]?:toCurrency
        val rateText = "1 $fromCode = ${formatter.format(rate)} $toCode"

        textViewExchangeRate.text = rateText
    }
}