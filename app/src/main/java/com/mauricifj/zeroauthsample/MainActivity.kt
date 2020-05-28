package com.mauricifj.zeroauthsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import br.com.cielo.zeroauth.ZeroAuth
import br.com.cielo.zeroauth.models.*
import br.com.cielo.zeroauth.Environment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var zeroAuth: ZeroAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        zeroAuth = ZeroAuth(
            merchantId = "MERCHANT-ID",
            clientId = "CLIENT-ID",
            clientSecret = "CLIENT-SECRET",
            environment = Environment.SANDBOX
        )

        card_expiration_month.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, listOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")))
        card_expiration_year.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, listOf("2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030")))
        card_brand.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, listOf("Visa", "Master", "Elo")))

        verify.setOnClickListener {
            form.visibility = View.INVISIBLE
            loading.visibility = View.VISIBLE

            zeroAuth.validate(
                zeroAuthRequest = ZeroAuthRequest(
                    cardType = CardType.CREDIT_CARD,
                    cardNumber = card_number.text.toString(),
                    holder = holder.text.toString(),
                    expirationDate = "${card_expiration_month.text}/${card_expiration_year.text}",
                    securityCode = card_security_code.text.toString(),
                    saveCard = false,
                    brand = card_brand.text.toString(),
                    cardOnFile = CardOnFile(
                        usage = Usage.FIRST,
                        reason = Reason.RECURRING
                    )
                )
            ) {
                Log.d("ZERO_AUTH_TAG", it.toString())

                if (it.result != null) {
                    with (it.result!!) {
                        result_valid.text = valid.toString()
                        result_return_code.text = returnCode
                        result_return_message.text = returnMessage
                        result_issuer_transaction_id.text = issuerTransactionId
                    }
                } else {
                    result_valid.text = null
                    result_return_code.text = null
                    result_return_message.text = null
                    result_issuer_transaction_id.text = null
                }

                if (!it.errors.isEmpty()) {
                    errors_list.adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        it.errors
                    )
                } else {
                    errors_list.adapter = null
                }
                loading.visibility = View.INVISIBLE
                result.visibility = View.VISIBLE
            }
        }

        backToForm.setOnClickListener {
            holder.text?.clear()
            card_number.text?.clear()
            card_expiration_month.text.clear()
            card_expiration_year.text.clear()
            card_security_code.text?.clear()
            card_brand.text.clear()
            result.visibility = View.INVISIBLE
            form.visibility = View.VISIBLE
        }
    }
}
