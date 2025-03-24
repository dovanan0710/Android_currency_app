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
import java.util.Locale

class MainActivity : AppCompatActivity() {
    // UI components
    private lateinit var fromAmountEditText: EditText
    private lateinit var toAmountEditText: EditText
    private lateinit var fromCurrencySpinner: Spinner
    private lateinit var toCurrencySpinner: Spinner
    private lateinit var fromCurrencySymbolTextView: TextView
    private lateinit var toCurrencySymbolTextView: TextView
    private lateinit var exchangeRateTextView: TextView

    // Currency data
    private val currencies = listOf(
        Currency("USD", "United States - Dollar", "$", 1.0),
        Currency("VND", "Vietnam - Dong", "₫", 23185.0),
        Currency("EUR", "European Union - Euro", "€", 0.92),
        Currency("GBP", "United Kingdom - Pound", "£", 0.79),
        Currency("JPY", "Japan - Yen", "¥", 151.67)
    )

    // Flags to prevent infinite loops when updating text fields
    private var isFromAmountChanging = false
    private var isToAmountChanging = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        fromAmountEditText = findViewById(R.id.fromAmountEditText)
        toAmountEditText = findViewById(R.id.toAmountEditText)
        fromCurrencySpinner = findViewById(R.id.fromCurrencySpinner)
        toCurrencySpinner = findViewById(R.id.toCurrencySpinner)
        fromCurrencySymbolTextView = findViewById(R.id.fromCurrencySymbolTextView)
        toCurrencySymbolTextView = findViewById(R.id.toCurrencySymbolTextView)
        exchangeRateTextView = findViewById(R.id.exchangeRateTextView)

        // Setup currency spinners
        setupSpinners()

        // Setup text change listeners
        setupTextChangeListeners()

        // Set initial values
        fromCurrencySymbolTextView.text = currencies[0].symbol
        toCurrencySymbolTextView.text = currencies[1].symbol
        updateExchangeRateText()
    }

    private fun setupSpinners() {
        // Create adapter for currency spinners
        val currencyNames = currencies.map { it.name }.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencyNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set adapters
        fromCurrencySpinner.adapter = adapter
        toCurrencySpinner.adapter = adapter

        // Set default selections
        fromCurrencySpinner.setSelection(0) // USD
        toCurrencySpinner.setSelection(1) // VND

        // Set listeners
        fromCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                fromCurrencySymbolTextView.text = currencies[position].symbol
                updateExchangeRateText()
                convertCurrency(true)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        toCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                toCurrencySymbolTextView.text = currencies[position].symbol
                updateExchangeRateText()
                convertCurrency(true)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupTextChangeListeners() {
        fromAmountEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!isFromAmountChanging) {
                    convertCurrency(true)
                }
            }
        })

        toAmountEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!isToAmountChanging) {
                    convertCurrency(false)
                }
            }
        })
    }

    private fun convertCurrency(fromToTo: Boolean) {
        try {
            val fromCurrency = currencies[fromCurrencySpinner.selectedItemPosition]
            val toCurrency = currencies[toCurrencySpinner.selectedItemPosition]
            val formatter = DecimalFormat("#,##0.00")

            if (fromToTo) {
                val fromAmountStr = fromAmountEditText.text.toString()
                if (fromAmountStr.isEmpty()) {
                    toAmountEditText.setText("")
                    return
                }

                val fromAmount = fromAmountStr.toDoubleOrNull() ?: 0.0
                val toAmount = fromAmount * (toCurrency.rate / fromCurrency.rate)

                isToAmountChanging = true
                toAmountEditText.setText(formatter.format(toAmount))
                isToAmountChanging = false
            } else {
                val toAmountStr = toAmountEditText.text.toString()
                if (toAmountStr.isEmpty()) {
                    fromAmountEditText.setText("")
                    return
                }

                val toAmount = toAmountStr.toDoubleOrNull() ?: 0.0
                val fromAmount = toAmount * (fromCurrency.rate / toCurrency.rate)

                isFromAmountChanging = true
                fromAmountEditText.setText(formatter.format(fromAmount))
                isFromAmountChanging = false
            }
        } catch (e: Exception) {
            // Handle conversion errors
        }
    }

    private fun updateExchangeRateText() {
        val fromCurrency = currencies[fromCurrencySpinner.selectedItemPosition]
        val toCurrency = currencies[toCurrencySpinner.selectedItemPosition]
        val rate = toCurrency.rate / fromCurrency.rate
        val formatter = DecimalFormat("#,##0.00")

        exchangeRateTextView.text = "1 ${fromCurrency.code} = ${formatter.format(rate)} ${toCurrency.code}"
    }

    // Currency data class
    data class Currency(
        val code: String,
        val name: String,
        val symbol: String,
        val rate: Double
    )
}

