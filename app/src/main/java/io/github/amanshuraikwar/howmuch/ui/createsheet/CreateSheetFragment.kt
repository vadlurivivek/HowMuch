package io.github.amanshuraikwar.howmuch.ui.createsheet

import android.accounts.Account
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.sheets.v4.SheetsScopes
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.ui.base.BaseFragment
import io.github.amanshuraikwar.howmuch.ui.onboarding.OnboardingScreen
import io.github.amanshuraikwar.howmuch.util.Util
import kotlinx.android.synthetic.main.fragment_create_sheet.*
import java.util.*
import javax.inject.Inject

class CreateSheetFragment @Inject constructor()
    : BaseFragment<CreateSheetContract.View, CreateSheetContract.Presenter>(), CreateSheetContract.View, OnboardingScreen {

    private val TAG = Util.getTag(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_sheet, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        createSheetBtn.setOnClickListener {
            presenter.onCreateSheetClicked()
        }
        completeSetupBtn.setOnClickListener {
            presenter.onCompleteSetupClicked()
        }
        proceedBtn.setOnClickListener {
            presenter.onProceedClicked()
        }
    }

    override fun getGoogleAccountCredential(account: Account): GoogleAccountCredential {
        return GoogleAccountCredential.usingOAuth2(activity, Arrays.asList(SheetsScopes.SPREADSHEETS))
                .setBackOff(ExponentialBackOff())
                .setSelectedAccount(account)
    }

    override fun updateLoading(message: String) {
        loadingTv.text = message
    }

    override fun showLoading() {
        loadingLl.visibility = VISIBLE
    }

    override fun hideLoading() {
        loadingLl.visibility = GONE
    }

    override fun showProceedButton() {
        proceedBtn.visibility = VISIBLE
    }

    override fun hideProceedButton() {
        proceedBtn.visibility = GONE
    }

    override fun showCreateSheetButton() {
        createSheetBtn.visibility = VISIBLE
    }

    override fun hideCreateSheetButton() {
        createSheetBtn.visibility = GONE
    }

    override fun showSnackBar(message: String) {
        Snackbar.make(parentRl, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun showId(id: String) {
        spreadSheetIdTv.visibility = VISIBLE
        spreadSheetIdTv.text = id
    }

    override fun hideId() {
        spreadSheetIdTv.visibility = INVISIBLE
    }

    override fun showName(name: String) {
        spreadSheetNameTv.visibility = VISIBLE
        spreadSheetNameTv.text = name
    }

    override fun hideName() {
        spreadSheetNameTv.visibility = INVISIBLE
    }

    override fun showCompleteSetupButton() {
        completeSetupBtn.visibility = VISIBLE
    }

    override fun hideCompleteSetupButton() {
        completeSetupBtn.visibility = GONE
    }

    override fun showIndefiniteErrorSnackbar(message: String) {
        Log.d(TAG, "showIndefiniteErrorSnackbar: called")
        Snackbar
                .make(parentRl, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry") { presenter.onIndefiniteRetryClicked() }
                .show()
    }

    override fun selected() {
        presenter.onScreenSelected()
    }
}