package com.normorovers.mmt.app.event.mmtevent.view.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.normorovers.mmt.app.event.mmtevent.R
import kotlinx.android.synthetic.main.fragment_team.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_NAME = "name"
private const val ARG_REGO = "rego"
private const val ARG_UID = "uid"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TeamFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [TeamFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class TeamFragment : Fragment() {
	private var name: String? = null
	private var rego: String? = null
	private var uid: String? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			name = it.getString(ARG_NAME)
			rego = it.getString(ARG_REGO)
			uid = it.getString(ARG_UID)
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_team, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		(text_team_name as TextView).text = name.toString()
		(text_team_rego as TextView).text = rego.toString()
	}

	companion object {
		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.
		 *
		 * @param uid Team Unique IDentifier.
		 * @param name Team Name.
		 * @param registration Team Vehicle Registration.
		 * @return A new instance of fragment TeamFragment.
		 */
		@JvmStatic
		fun newInstance(uid: String, name: String, registration: String) =
				TeamFragment().apply {
					arguments = Bundle().apply {
						putString(ARG_NAME, name)
						putString(ARG_REGO, registration)
						putString(ARG_UID, uid)
					}
				}
	}
}
