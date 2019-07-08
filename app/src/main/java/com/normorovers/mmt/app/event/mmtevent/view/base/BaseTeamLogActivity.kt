package com.normorovers.mmt.app.event.mmtevent.view.base

import android.content.Context
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
import com.normorovers.mmt.app.event.mmtevent.view.team.TeamFragment
import kotlinx.android.synthetic.main.activity_base_team_log.*
import kotlinx.android.synthetic.main.content_base_team_log.*
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
			uid = it.getStringExtra(ARG_UID) ?: throw Error("No UID passed to activity")
		}

		val teamDb = doAsyncResult {
			return@doAsyncResult TeamRepository(application).getByUid(uid!!)
		}

		preferences = application.getSharedPreferences(application.getString(R.string.shared_preferences), Context.MODE_PRIVATE)

		val baseId: Int = preferences.getInt("base_id", -1)
		if (baseId == -1) throw Error("A base must be selected")

		val onlyPresentView: ConstraintLayout = constraint_show_present

		val team = teamDb.get()

		team!!.let {
			teamId = it.id
			name = it.name
			rego = it.registration
		}

		supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_team,
				TeamFragment.newInstance(uid!!, name!!, rego!!))?.commit()

		viewModel = BaseTeamLogViewModel(application, teamId!!)


		if (teamId == -1L) throw Error("A Team must be selected/loaded")

		(text_base_number as TextView).text = "$baseId"


		onlyPresentView.visibility = if (teamPresent) View.VISIBLE else View.INVISIBLE

		(switch_team_present as Switch).setOnCheckedChangeListener { button, isChecked ->
			if (isChecked != teamPresent) {
				teamPresent = isChecked
				if (isChecked) {
					//team has arrived at the base...
					Log.d("Log", "Team arrived")
					onlyPresentView.visibility = View.VISIBLE
					viewModel.insertArrived()
				} else {
					//team has left the base...
					Log.d("Log", "Team departing")
					onlyPresentView.visibility = View.INVISIBLE
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
		input.text = "Clues Opened"
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

}
