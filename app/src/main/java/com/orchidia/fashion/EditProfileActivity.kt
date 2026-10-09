package com.orchidia.fashion

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnSaveProfile).setOnClickListener {
            val name = findViewById<EditText>(R.id.etProfileName)
                .text.toString().trim()

            val email = findViewById<EditText>(R.id.etProfileEmail)
                .text.toString().trim()

            if (name.isBlank() || email.isBlank()) {
                Toast.makeText(
                    this,
                    "Nama dan email harus diisi",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            getSharedPreferences("orchidia_profile", MODE_PRIVATE)
                .edit()
                .putString("name", name)
                .putString("email", email)
                .apply()

            Toast.makeText(
                this,
                "Profile saved ♡",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }
    }
}