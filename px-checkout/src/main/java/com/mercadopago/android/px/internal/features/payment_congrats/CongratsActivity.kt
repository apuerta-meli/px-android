package com.mercadopago.android.px.internal.features.payment_congrats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.mercadopago.android.px.R
import com.squareup.picasso.Picasso

class CongratsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_congrats)


        Picasso.with(this).load(
            "https://www.noticieros.live/noticias/wp-content/uploads/2021/07/tarjeta-metro-1280x720.jpg"
        ).into(findViewById<ImageView>(R.id.image_view_semovi))
    }
}