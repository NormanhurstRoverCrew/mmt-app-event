package com.normorovers.mmt.app.event.mmtevent

import android.app.Application
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
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
import com.normorovers.mmt.app.event.mmtevent.api.Api
import com.normorovers.mmt.app.event.mmtevent.api.Teams
import com.normorovers.mmt.app.event.mmtevent.db.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import org.jetbrains.anko.doAsync
import retrofit2.Call


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
	private lateinit var credentialsManager: SecureCredentialsManager


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val account = Auth0(this)
		val authentication = AuthenticationAPIClient(account)
		val storage = SharedPreferencesStorage(this)
		credentialsManager = SecureCredentialsManager(this, authentication, storage)


		if (!credentialsManager.hasValidCredentials()) {
			WebAuthProvider.login(account)
					.withScope("openid profile email offline_access")
					.withAudience("https://admin.mmt.normorovers.com/")
					.start(this, authCallback(credentialsManager) { logout() })
		}


		setSupportActionBar(toolbar)

		val toggle = ActionBarDrawerToggle(
				this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
		drawer_layout.addDrawerListener(toggle)
		toggle.syncState()

		nav_view.setNavigationItemSelectedListener(this)

		supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()

		nav_view.getHeaderView(0).button_logout.setOnClickListener {
			logout()
		}

		doAsync {
//			AppDatabase.getInstance(application).ticketDao().insert(Ticket("ebdehadasodibodai", 1, User("Grant Perry", "0439675452", "Norman Nor Goon")))
			Api(application).retrofit {
				val teamsD: Teams = it.create(Teams::class.java)
				val call: Call<List<Team>> = teamsD.getTeams()
				val teams = call.execute().body()
				for (team: Team in teams!!.iterator()) {
					Log.d("Team", "${team.uid} ${team.name}")
					for (ticket: Ticket in team.tickets!!) {
						Log.d("Ticket", "${ticket.uid} ${ticket.user.name}")
						Log.d("Ticket", "${ticket.uid} ${ticket.user.mobile}")
						Log.d("Ticket", "${ticket.uid} ${ticket.user.crew}")
					}
				}
			}
		}
	}

	fun logout() {
		logout(application)
	}

	companion object {
		fun logout(application: Application) {
			val account = Auth0(application)
			val authentication = AuthenticationAPIClient(account)
			val storage = SharedPreferencesStorage(application)
			val credentialsManager = SecureCredentialsManager(application, authentication, storage)
			credentialsManager.clearCredentials()

			//DO NOT CLEAN DATABASES TIME SERIES DB... We might want something from them

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

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return when (item.itemId) {
			R.id.action_settings -> true
			else -> super.onOptionsItemSelected(item)
		}
	}

	override fun onNavigationItemSelected(item: MenuItem): Boolean {
		// Handle navigation view item clicks here.
		when (item.itemId) {
			R.id.nav_tickets -> {
				supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
						TicketsFragment(intent.getLongExtra("id", -1))).commit()
			}
			R.id.nav_teams -> {
				supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
						TeamsFragment()).commit()
			}
		}
		val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
		drawerLayout.closeDrawer(GravityCompat.START)
		return true
	}

	private class authCallback(val credMan: SecureCredentialsManager, val fail: () -> Unit) : AuthCallback {
		override fun onSuccess(credentials: Credentials) {
			credMan.saveCredentials(credentials)
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
}
