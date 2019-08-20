package com.normorovers.mmt.app.event.mmtevent

import android.Manifest
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.auth0.android.provider.AuthCallback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.google.android.material.navigation.NavigationView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.normorovers.mmt.app.event.mmtevent.api.Api
import com.normorovers.mmt.app.event.mmtevent.qr.QRScanMulti
import com.normorovers.mmt.app.event.mmtevent.qr.QRScanOnce
import com.normorovers.mmt.app.event.mmtevent.qr.code.CodeBodyInvalid
import com.normorovers.mmt.app.event.mmtevent.qr.code.CodeHeaderWrong
import com.normorovers.mmt.app.event.mmtevent.qr.code.TicketCode
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
	private val CAMERA_REQUEST_CODE: Int = 9042
	private lateinit var credentialsManager: SecureCredentialsManager
	private val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

	private lateinit var preferences: SharedPreferences

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		preferences = getSharedPreferences(application.getString(R.string.shared_preferences), Context.MODE_PRIVATE)

		firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults)

		firebaseRemoteConfig.fetch(0)
				.addOnCompleteListener { task ->
					if (task.isSuccessful) {
						firebaseRemoteConfig.activate()
						Toast.makeText(this, "Config Refreshed", Toast.LENGTH_SHORT).show()
					} else {
						Toast.makeText(this, "Not Refreshed", Toast.LENGTH_SHORT).show()
					}
				}
				.addOnSuccessListener {
					firebaseRemoteConfig.activate()
				}
				.addOnFailureListener { Log.d("FirebaseRemoteConfig", "FAILED") }

		val account = Auth0(this)
		val authentication = AuthenticationAPIClient(account)
		val storage = SharedPreferencesStorage(this)
		credentialsManager = SecureCredentialsManager(this, authentication, storage)


		if (!credentialsManager.hasValidCredentials()) {
			WebAuthProvider.login(account)
					.withScope("openid profile email offline_access")
					.withAudience("https://admin.mmt.normorovers.com/")
					.start(this, authCallback(application, credentialsManager) { logout() })
		} else {
			setupPermissions()
		}

		setSupportActionBar(toolbar)

		val toggle = ActionBarDrawerToggle(
				this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
		drawer_layout.addDrawerListener(toggle)
		toggle.syncState()

		nav_view.setNavigationItemSelectedListener(this)

		when (firebaseRemoteConfig.getString("landingPage")) {
			"checkin" -> {
				supportFragmentManager.beginTransaction().replace(R.id.fragment_container, CheckInFragment()).commit()
			}
			"base" -> {
				supportFragmentManager.beginTransaction().replace(R.id.fragment_container, BaseFragment()).commit()
			}
			else -> {
				supportFragmentManager.beginTransaction().replace(R.id.fragment_container, CheckInFragment()).commit()
			}
		}


		nav_view.getHeaderView(0).button_logout.setOnClickListener {
			logout()
		}

		nav_view.getHeaderView(0).button_base.setOnClickListener {
			editBase()
		}

		val authId = if (credentialsManager.hasValidCredentials()) {
			Api(application).getUser().get(5, TimeUnit.SECONDS).id ?: null
		} else null

		preferences.edit().apply {
			if (!authId.isNullOrEmpty()) {
				putString("auth_id", authId)
			}
			apply()
		}

		if (preferences.getInt("base_id", -1) == -1) {
			editBase()
		}
	}

	fun editBase() {
		val builder = AlertDialog.Builder(this)
		builder.setTitle("Which base [1-3]")

		// Set up the input
		val input = EditText(this)
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.inputType = InputType.TYPE_CLASS_NUMBER
		input.text = Editable.Factory().newEditable("")
		input.imeOptions = EditorInfo.IME_ACTION_DONE
		input.setOnEditorActionListener { _, i, _ ->
			if (i == EditorInfo.IME_ACTION_DONE) {
				closeKeyboard()
				updateBaseId(input.text.toString())
				true
			} else {
				false
			}
		}
		builder.setView(input)

		// Set up the buttons
		builder.setPositiveButton("Save") { _, _ -> closeKeyboard();updateBaseId(input.text.toString()) }
		builder.setNegativeButton("Cancel") { dialog, _ -> closeKeyboard();dialog.cancel() }

		builder.show()
		setEditTextFocused(input)
	}

	private fun updateBaseId(baseId: String) {
		val base = Integer.valueOf(baseId)
		if ((base >= 1) and (base <= 3)) {
			preferences.edit().apply {
				putInt("base_id", base)
				apply()
			}
		} else {
			Toast.makeText(this, "Enter base between 1-3", Toast.LENGTH_LONG).show()
		}
	}

	private fun setEditTextFocused(input: EditText) {
		val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
		inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
		input.requestFocus()
	}

	private fun closeKeyboard() {
		val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
		inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
	}

	fun logout() {
		logout(application)
	}

	private fun setupPermissions() {
		val permission = ContextCompat.checkSelfPermission(this,
				Manifest.permission.CAMERA)

		if (permission != PackageManager.PERMISSION_GRANTED) {
			Log.i("MainActivity", "Permission to use camera denied")
			makeRequest()
		}
	}

	private fun makeRequest() {
		ActivityCompat.requestPermissions(this,
				arrayOf(Manifest.permission.CAMERA),
				CAMERA_REQUEST_CODE)
	}

	override fun onRequestPermissionsResult(requestCode: Int,
											permissions: Array<String>, grantResults: IntArray) {
		when (requestCode) {
			CAMERA_REQUEST_CODE -> {

				if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

					Log.i("MainActivity", "Permission has been denied by user")
				} else {
					Log.i("MainActivity", "Permission has been granted by user")
				}
			}
		}
	}

	companion object {
		fun logout(application: Application) {
			val account = Auth0(application)
			val authentication = AuthenticationAPIClient(account)
			val storage = SharedPreferencesStorage(application)
			val credentialsManager = SecureCredentialsManager(application, authentication, storage)
			credentialsManager.clearCredentials()

			//DO NOT CLEAN DATABASES TIME SERIES DB... We might want something from them

			restartMainActivity(application)
		}

		fun restartMainActivity(application: Application) {
			val i = Intent(application, MainActivity::class.java)
			i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			application.startActivity(i)
		}
	}


	override fun onBackPressed() {
		val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
		if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
			drawerLayout.closeDrawer(GravityCompat.START)
		} else {
			super.onBackPressed()
		}
	}

//	override fun onCreateOptionsMenu(menu: Menu): Boolean {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		menuInflater.inflate(R.menu.menu, menu)
//		return true
//	}
//
//	override fun onOptionsItemSelected(item: MenuItem): Boolean {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		return when (item.itemId) {
//			R.id.action_settings -> true
//			else -> super.onOptionsItemSelected(item)
//		}
//	}

	override fun onNavigationItemSelected(item: MenuItem): Boolean {
		// Handle navigation view item clicks here.
		when (item.itemId) {
			R.id.nav_checkin -> {
				supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
						CheckInFragment()).commit()
			}
			R.id.nav_base -> {
				supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
						BaseFragment()).commit()
			}
		}
		val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
		drawerLayout.closeDrawer(GravityCompat.START)
		return true
	}

	private class authCallback(
			private val application: Application,
			private val credMan: SecureCredentialsManager,
			private val fail: () -> Unit) : AuthCallback {
		override fun onSuccess(credentials: Credentials) {
			credMan.saveCredentials(credentials)
			restartMainActivity(application)
		}

		override fun onFailure(dialog: Dialog) {
			credMan.clearCredentials()
			fail()
		}

		override fun onFailure(exception: AuthenticationException?) {
			credMan.clearCredentials()
			fail()
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			QRScanOnce.REQUEST_CODE -> {
				when (resultCode) {
					RESULT_OK -> {
						val scannedData = data?.getStringExtra("data")!!
						Log.d("MainActivity", scannedData)

						try {
							Log.d("REG", TicketCode().parse(scannedData))
						} catch (e: CodeHeaderWrong) {

						} catch (e: CodeBodyInvalid) {

						}
					}
				}
			}
			QRScanMulti.REQUEST_CODE -> {
				when (resultCode) {
					RESULT_OK -> {
						val scannedData = data?.getStringExtra("data")!!
						Log.d("MainActivity", scannedData)

						try {
							Log.d("REG", TicketCode().parse(scannedData))
						} catch (e: CodeHeaderWrong) {

						} catch (e: CodeBodyInvalid) {

						}
					}
				}
			}
		}
	}
}
