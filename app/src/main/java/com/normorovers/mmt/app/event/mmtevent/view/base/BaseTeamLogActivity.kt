package com.normorovers.mmt.app.event.mmtevent.view.base

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.normorovers.mmt.app.event.mmtevent.R
import com.normorovers.mmt.app.event.mmtevent.db.Team
import com.normorovers.mmt.app.event.mmtevent.db.TeamRepository
import com.normorovers.mmt.app.event.mmtevent.qr.QRScanOnce
import com.normorovers.mmt.app.event.mmtevent.qr.code.CodeBodyInvalid
import com.normorovers.mmt.app.event.mmtevent.qr.code.CodeHeaderWrong
import com.normorovers.mmt.app.event.mmtevent.qr.code.TeamCode
import com.normorovers.mmt.app.event.mmtevent.view.team.TeamFragment
import kotlinx.android.synthetic.main.activity_base_team_log.*
import kotlinx.android.synthetic.main.content_base_team_log.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult

private const val ARG_NAME = "name"
private const val ARG_REGO = "rego"
private const val ARG_UID = "uid"

class BaseTeamLogActivity : AppCompatActivity() {
	private var team: Team? = null
	private var name: String? = null
	private var rego: String? = null
	private var uid: String? = null
	private var teamId: Long? = null

	private var teamPresent: Boolean = false

	private lateinit var preferences: SharedPreferences

	private lateinit var viewModel: BaseTeamLogViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_base_team_log)
		setSupportActionBar(toolbar)

		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		intent?.let {
			uid = it.getStringExtra(ARG_UID)
		}

		if (uid.isNullOrEmpty()) {
			// start the single scanner activity to get a team

			startActivityForResult(Intent(this, QRScanOnce::class.java), QRScanOnce.REQUEST_CODE)

		} else {
			initWithUid()
		}
	}

	private fun initWithUid() {
		val teamDb = doAsyncResult {
			return@doAsyncResult TeamRepository(application).getByUid(uid!!)
		}


		preferences = application.getSharedPreferences(application.getString(R.string.shared_preferences), Context.MODE_PRIVATE)

		val baseId: Int = preferences.getInt("base_id", -1)
		if (baseId == -1) throw Error("A base must be selected")

		val team = teamDb.get()

		team!!.let {
			teamId = it.id
			name = it.name
			rego = it.registration
		}

		viewModel = BaseTeamLogViewModel(application, teamId!!, baseId)

		teamPresent = viewModel.isAtBase()

		supportFragmentManager.beginTransaction().replace(R.id.fragment_team,
				TeamFragment.newInstance(uid!!, name!!, rego!!)).commit()

		viewModel = BaseTeamLogViewModel(application, teamId!!, baseId)


		if (teamId == -1L) throw Error("A Team must be selected/loaded")

		(text_base_number as TextView).text = "$baseId"

		updateDisplayPresent(teamPresent)

		(switch_team_present as Switch).isChecked = teamPresent

		(switch_team_present as Switch).setOnCheckedChangeListener { _, isChecked ->
			if (isChecked != teamPresent) {
				teamPresent = isChecked
				updateDisplayPresent(isChecked)
				if (isChecked) {
					//team has arrived at the base...
					viewModel.insertArrived()
				} else {
					//team has left the base...
					viewModel.insertDepart()
				}
			}
		}

		(button_give_points as Button).setOnClickListener {
			addPointsDialog()
		}

		(button_give_trivia as Button).setOnClickListener {
			addTriviaDialog()
		}

		(button_mark_clues as Button).setOnClickListener {
			markCluesDialog()
		}

		(button_make_comment as Button).setOnClickListener {
			makeCommentDialog()
		}

		(button_view_logs as Button).setOnClickListener {
			val i = Intent(this, ActivityLogActivity::class.java)
			i.putExtra("team", team.id)
			i.putExtra("base", baseId)
			startActivity(i)
		}
	}

	private fun updateDisplayPresent(p: Boolean) {
		(constraint_show_present as ConstraintLayout).visibility = if (p) View.VISIBLE else View.INVISIBLE
	}

	private fun addPointsDialog() {
		val builder = AlertDialog.Builder(this)
		builder.setTitle("Enter points (0.0 - 10.0)")

		val input = EditText(this)
		input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
		input.imeOptions = EditorInfo.IME_ACTION_DONE
		input.setOnEditorActionListener { _, i, _ ->
			if (i == EditorInfo.IME_ACTION_DONE) {
				addPoints(input.text.toString())
				closeKeyboard()
				true
			} else {
				false
			}
		}
		builder.setView(input)

		// Set up the buttons
		builder.setPositiveButton("Save") { _, _ -> closeKeyboard();addPoints(input.text.toString()) }
		builder.setNegativeButton("Cancel") { dialog, _ -> closeKeyboard();dialog.cancel() }

		builder.show()
		setEditTextFocused(input)
	}

	private fun addPoints(input: String) {
		val points = input.toFloat()
		viewModel.insertPoints(points)
	}

	private fun addTriviaDialog() {
		val builder = AlertDialog.Builder(this)
		builder.setTitle("Enter Trivia points (0.0 - 10.0)")

		val input = EditText(this)
		input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
		input.imeOptions = EditorInfo.IME_ACTION_DONE
		input.setOnEditorActionListener { _, i, _ ->
			if (i == EditorInfo.IME_ACTION_DONE) {
				addTrivia(input.text.toString())
				closeKeyboard()
				true
			} else {
				false
			}
		}
		builder.setView(input)

		// Set up the buttons
		builder.setPositiveButton("Save") { _, _ -> closeKeyboard();addTrivia(input.text.toString()) }
		builder.setNegativeButton("Cancel") { dialog, _ -> closeKeyboard();dialog.cancel() }

		builder.show()
		setEditTextFocused(input)
	}

	private fun addTrivia(input: String) {
		val points = input.toFloat()
		viewModel.insertTrivia(points)
	}

	private fun markCluesDialog() {
		val builder = AlertDialog.Builder(this)
		builder.setTitle("Opened the clues?")

		val input = CheckBox(this)
		input.text = getString(R.string.clues_opened)
		input.isChecked = true
		builder.setView(input)

		// Set up the buttons
		builder.setPositiveButton("Save") { _, _ -> markClues(input.isChecked) }
		builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

		builder.show()
	}

	private fun markClues(opened: Boolean) {
		viewModel.insertClues(opened)
	}

	private fun makeCommentDialog() {
		val builder = AlertDialog.Builder(this)
		builder.setTitle("Enter a comment")

		val input = EditText(this)
		input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
		input.imeOptions = EditorInfo.IME_ACTION_DONE
		input.minLines = 3
		input.setHorizontallyScrolling(false)
		input.setOnEditorActionListener { _, i, _ ->
			if (i == EditorInfo.IME_ACTION_DONE) {
				makeComment(input.text.toString())
				closeKeyboard()
				true
			} else {
				false
			}
		}
		builder.setView(input)

		// Set up the buttons
		builder.setPositiveButton("Save") { _, _ -> closeKeyboard();makeComment(input.text.toString()) }
		builder.setNegativeButton("Cancel") { dialog, _ -> closeKeyboard();dialog.cancel() }

		builder.show()
		setEditTextFocused(input)
	}

	private fun makeComment(input: String) {
		viewModel.insertComment(input)
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

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			QRScanOnce.REQUEST_CODE -> { //For opening a team
				when (resultCode) {
					RESULT_OK -> {
						val scannedData = data?.getStringExtra("data")!!
						Log.d("ScanBaseActivityLog", scannedData)

						try {
							val teamUid = TeamCode().parse(scannedData)
							doAsync {
								val team = TeamRepository(application).getByUid(teamUid)

								val i = Intent(application, BaseTeamLogActivity::class.java)
								i.putExtra("uid", team.uid)
								startActivity(i)
								finish()
							}
						} catch (e: CodeHeaderWrong) {

						} catch (e: CodeBodyInvalid) {

						}
					}
					RESULT_CANCELED -> {
						finish()
					}
				}
			}
		}
	}

}
