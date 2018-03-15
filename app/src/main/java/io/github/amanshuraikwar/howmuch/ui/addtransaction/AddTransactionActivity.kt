package io.github.amanshuraikwar.howmuch.ui.addtransaction

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.ui.base.BaseActivity
import io.github.amanshuraikwar.howmuch.util.LogUtil
import kotlinx.android.synthetic.main.activity_add_transaction.*

/**
 * Created by amanshuraikwar on 09/03/18.
 */
class AddTransactionActivity
    : BaseActivity<AddTransactionContract.View, AddTransactionContract.Presenter>(),
        AddTransactionContract.View {

    private val TAG = LogUtil.getLogTag(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        initUi()
    }

    private fun initUi() {

        addBtn.setOnClickListener {
            presenter.onAddBtnClick(amountTv.text.toString())
        }
    }

    override fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }

    override fun closeActivity() {
        finishAfterTransition()
    }
}