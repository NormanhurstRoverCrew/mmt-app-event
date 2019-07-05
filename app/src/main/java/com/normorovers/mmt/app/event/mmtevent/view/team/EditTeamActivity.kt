package com.normorovers.mmt.app.event.mmtevent.view.team

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.normorovers.mmt.app.event.mmtevent.*
import com.normorovers.mmt.app.event.mmtevent.db.Team
import com.normorovers.mmt.app.event.mmtevent.db.TeamRepository
import com.normorovers.mmt.app.event.mmtevent.view.ticket.TicketsFragment
import kotlinx.android.synthetic.main.activity_edit_team.*
import kotlinx.android.synthetic.main.content_edit_team.*
import org.jetbrains.anko.doAsync


class EditTeamActivity : AppCompatActivity() {

	private var teamId: Long = -1

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_edit_team)
		setSupportActionBar(toolbar)

		teamId = intent.getLongExtra("id", -1)

		Log.d("EditTeam", teamId.toString())

		if (teamId == -1L) {
			// start the single scanner activity to get a team

			Log.d("Edit Team", "Will open scanner to get team")

			startActivityForResult(Intent(this, QRScanOnce::class.java), QRScanOnce.REQUEST_CODE)

		} else {
			initWithId()
		}
	}

	private fun initWithId() {
		Log.d("Edit Team", "Have id. will load tickets")
		val factory: TeamViewModel.Factory = TeamViewModel.Factory(application, teamId)

		val teamViewModel: TeamViewModel = ViewModelProviders.of(this, factory).get(TeamViewModel::class.java)
		teamViewModel.get()
				.observe(this, Observer { team: Team ->
					val tn = (team_name as TextView)
					if (team.name.isNotEmpty()) {
						tn.text = team.name
						tn.setTextColor(Color.BLACK)
					} else {
						tn.text = getString(R.string.empty_team_name)
						tn.setTextColor(Color.RED)
					}

					val tr = (team_registration as TextView)
					if (team.registration.isNotEmpty()) {
						tr.text = team.registration
						tr.setTextColor(Color.BLACK)
					} else {
						tr.text = getString(R.string.empty_team_registration)
						tr.setTextColor(Color.RED)
					}

					(team_name as TextView).setOnClickListener { editTeamNameDialogue(team.name) }
					(team_registration as TextView).setOnClickListener { editTeamRegoDialogue(team.registration) }
				})

		supportFragmentManager.beginTransaction().replace(
				R.id.fragment,
				TicketsFragment(teamId)
		).commit()

		(button_add_ticket as FloatingActionButton).setOnClickListener {
			startActivityForResult(Intent(application, QRMultiScan::class.java), QRMultiScan.REQUEST_CODE)
		}
	}

	private fun editTeamNameDialogue(name: String) {
		val builder = AlertDialog.Builder(this)
		builder.setTitle("Edit Team Name")

		// Set up the input
		val input = EditText(this)
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.inputType = InputType.TYPE_CLASS_TEXT
		input.text = Editable.Factory().newEditable(name)
		input.imeOptions = EditorInfo.IME_ACTION_DONE
		input.setOnEditorActionListener { _, i, _ ->
			if (i == EditorInfo.IME_ACTION_DONE) {
				closeKeyboard()
				updateTeamName(input.text.toString())
				true
			} else {
				false
			}
		}
		builder.setView(input)

		// Set up the buttons
		builder.setPositiveButton("Save") { _, _ -> closeKeyboard();updateTeamName(input.text.toString()) }
		builder.setNegativeButton("Cancel") { dialog, _ -> closeKeyboard();dialog.cancel() }

		builder.show()
		setEditTextFocused(input)
	}

	private fun editTeamRegoDialogue(rego: String) {
		val builder = AlertDialog.Builder(this)
		builder.setTitle("Edit Team Name")

		// Set up the input
		val input = EditText(this)
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.inputType = InputType.TYPE_CLASS_TEXT
		input.text = Editable.Factory().newEditable(rego)
		input.imeOptions = EditorInfo.IME_ACTION_DONE
		input.setOnEditorActionListener { _, i, _ ->
			if (i == EditorInfo.IME_ACTION_DONE) {
				closeKeyboard()
				updateTeamRego(input.text.toString())
				true
			} else {
				false
			}
		}
		builder.setView(input)

		// Set up the buttons
		builder.setPositiveButton("Save") { _, _ -> closeKeyboard();updateTeamRego(input.text.toString()) }
		builder.setNegativeButton("Cancel") { dialog, _ -> closeKeyboard();dialog.cancel() }

		builder.show()
		setEditTextFocused(input)
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

	private fun updateTeamName(name: String) {
		doAsync {
			val repo = TeamRepository(application)
			val team = repo.getOnly(teamId)

			team.name = name

			repo.update(team)
		}
	}

	private fun updateTeamRego(rego: String) {
		doAsync {
			val repo = TeamRepository(application)
			val team = repo.getOnly(teamId)

			team.registration = rego

			repo.update(team)
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			QRMultiScan.REQUEST_CODE -> { //For adding tickets to our team
				when (resultCode) {
					RESULT_OK -> {
						val scannedData = data?.getStringArrayListExtra("data")!!
						for (i in scannedData) {
							try {
								val ticket = TicketCode().parse(i)
								Log.d("REG", ticket)

								TeamViewModel(application, teamId).addTicketbyUid(ticket)
							} catch (e: CodeHeaderWrong) {

							} catch (e: CodeBodyInvalid) {

							}
						}

					}
				}
			}
			QRScanOnce.REQUEST_CODE -> { //For opening a team
				when (resultCode) {
					RESULT_OK -> {
						val scannedData = data?.getStringExtra("data")!!
						Log.d("EditTeam", scannedData)

						try {
							val teamUid = TeamCode().parse(scannedData)
							doAsync {
								val team = TeamRepository(application).getByUid(teamUid)

								val i = Intent(application, EditTeamActivity::class.java)
								i.putExtra("id", team.id)
								startActivity(i)
								finish()
							}
						} catch (e: CodeHeaderWrong) {

						} catch (e: CodeBodyInvalid) {

						}
					}
				}
			}
		}
	}
}
