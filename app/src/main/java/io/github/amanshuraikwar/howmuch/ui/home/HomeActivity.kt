package io.github.amanshuraikwar.howmuch.ui.home

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.model.DayExpense
import io.github.amanshuraikwar.howmuch.ui.addtransaction.AddTransactionActivity
import io.github.amanshuraikwar.howmuch.ui.base.BaseActivity
import io.github.amanshuraikwar.howmuch.ui.expenseday.ExpenseDayActivity
import io.github.amanshuraikwar.howmuch.ui.list.expenseday.ExpenseDayListItem
import io.github.amanshuraikwar.howmuch.ui.intro.IntroActivity
import io.github.amanshuraikwar.howmuch.ui.list.ListItem
import io.github.amanshuraikwar.howmuch.ui.list.ListItemTypeFactory
import io.github.amanshuraikwar.howmuch.ui.list.RecyclerViewAdapter
import io.github.amanshuraikwar.howmuch.ui.list.expenseday.ExpenseDayListItemOnClickListener
import io.github.amanshuraikwar.howmuch.ui.list.header.HeaderListItem
import io.github.amanshuraikwar.howmuch.util.LogUtil
import kotlinx.android.synthetic.main.activity_home.*

/**
 * Created by amanshuraikwar on 07/03/18.
 */
class HomeActivity
    : BaseActivity<HomeContract.View, HomeContract.Presenter>(), HomeContract.View {

    private val TAG = LogUtil.getLogTag(this)

    private val expenseDayOnClickListener: ExpenseDayListItemOnClickListener =
            object : ExpenseDayListItemOnClickListener{
                override fun onClick(dayExpense: DayExpense) {
                    presenter.onExpenseDayClick(dayExpense)
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initUi()
    }

    private fun initUi() {

        fab.setOnClickListener {
            presenter.onAddBtnClick()
        }

        currencySymbolTv.text = "₹"

        expenseHistoryRv.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "onResume: presenter=$presenter")
    }

    override fun startIntroActivity() {
        startActivity(Intent(this, IntroActivity::class.java))
        finish()
    }

    override fun startExpenseDayActivity(dayExpense: DayExpense) {
        with(Intent(this, ExpenseDayActivity::class.java)){
            this.putExtra(ExpenseDayActivity.KEY_DAY_EXPENSE, dayExpense)
            startActivity(this)
        }
    }

    override fun startStatsActivity() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showAddTransactionDialog() {
        startActivity(Intent(this, AddTransactionActivity::class.java))
    }

    override fun startSettingsActivity() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun displayTodaysExpense(amount: String) {
        todaysExpenseAmountTv.text = amount
    }

    override fun displayDayExpenses(listItems: List<ListItem<*>>) {
        Log.d(TAG, "displayDayExpenses:called")

        expenseHistoryRv.adapter =
                RecyclerViewAdapter(
                        this,
                        ListItemTypeFactory(),
                        listItems)
    }

    override fun getExpenseDayListItems(dayExpenses: List<DayExpense>, dailyLimit: Int)
            : List<ListItem<*>> {

        val list = mutableListOf<ListItem<*>>()
        list.add(HeaderListItem(getString(R.string.expense_history)))

        for (dayExpense in dayExpenses) {

            with(ExpenseDayListItem(dayExpense, dailyLimit)) {
                this.onClickListener = expenseDayOnClickListener
                list.add(this)
            }
        }
        return list
    }
}