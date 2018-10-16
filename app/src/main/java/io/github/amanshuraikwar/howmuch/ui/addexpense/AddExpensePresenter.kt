package io.github.amanshuraikwar.howmuch.ui.addexpense

import android.accounts.Account
import io.github.amanshuraikwar.howmuch.bus.AppBus
import io.github.amanshuraikwar.howmuch.data.DataManager
import io.github.amanshuraikwar.howmuch.data.network.sheets.AuthenticationManager
import io.github.amanshuraikwar.howmuch.data.network.sheets.SheetsDataSource
import io.github.amanshuraikwar.howmuch.model.Expense
import io.github.amanshuraikwar.howmuch.ui.base.AccountPresenter
import io.github.amanshuraikwar.howmuch.ui.base.BasePresenterImpl
import io.github.amanshuraikwar.howmuch.util.Util
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AddExpensePresenter
    @Inject constructor(appBus: AppBus,
                        dataManager: DataManager)
    : AccountPresenter<AddExpenseContract.View>(appBus, dataManager), AddExpenseContract.Presenter {

    @Suppress("PrivatePropertyName", "unused")
    private val TAG = Util.getTag(this)

    override fun onAttach(wasViewRecreated: Boolean) {
        super.onAttach(wasViewRecreated)

        if (wasViewRecreated) {
            getCategories(getAccount()!!, getEmail())
        }
    }

    private fun getCategories(account: Account, email: String) {

        getDataManager().let {
            dm ->
            dm
                    .getCategories()
                    .flatMap {
                        categoriesSet ->
                        if (categoriesSet.isEmpty()) {
                            dm
                                    .getSpreadsheetIdForYearAndMonthAndEmail(
                                            Util.getCurYearNumber(),
                                            Util.getCurMonthNumber(),
                                            email
                                    )
                                    .flatMap {
                                        id ->
                                        dm
                                                .readSpreadSheet(
                                                        id,
                                                        Util.getDefaultCategoriesSpreadSheetRange(),
                                                        getView()!!.getGoogleAccountCredential(account)
                                                )
                                    }
                                    .map { convertToCategoriesArray(it) }
                                    .flatMap {
                                        categoriesList ->
                                        dm
                                                .setCategories(categoriesList.toSet())
                                                .toSingleDefault(categoriesList)
                                                .toObservable()
                                    }
                        } else {
                            Observable.just(categoriesSet.toList())
                        }
                    }
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                getView()?.populateCategories(it)
                                getView()?.hideLoadingOverlay()
                            },
                            {
                                getView()?.showErrorOverlay()
                            },
                            {

                            },
                            {
                                getView()?.showLoadingOverlay()
                            })
        }
    }

    private fun convertToCategoriesArray(input: MutableList<MutableList<Any>>): List<String> {
        val categories = mutableListOf<String>()
        input.forEach {
            categories.add(it[0].toString())
        }
        return categories
    }

    override fun onSubmitClicked(expense: Expense) {

        if (expense.amount == "") {
            getView()?.showAmountError("Can't be empty!")
            return
        }

        if (expense.description == "") {
            getView()?.showDescriptionError("Can't be empty!")
            return
        }

        addExpense(expense, getAccount()!!, getEmail())
    }

    private fun addExpense(expense: Expense, account: Account, email:String) {

        getDataManager().let {
            dm ->
            dm
                    .getSpreadsheetIdForYearAndMonthAndEmail(
                            Util.getCurYearNumber(),
                            Util.getCurMonthNumber(),
                            email
                    )
                    .flatMap {
                        id ->
                        dm
                                .appendToSpreadSheet(
                                        id,
                                        Util.getDefaultTransactionsSpreadSheetRange(),
                                        SheetsDataSource.VALUE_INPUT_OPTION,
                                        listOf(
                                                listOf(
                                                        expense.date,
                                                        expense.time,
                                                        expense.amount,
                                                        expense.description,
                                                        expense.category
                                                )
                                        ),
                                        getView()!!.getGoogleAccountCredential(account)
                                )
                    }
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                if (it != null) {
                                    getView()?.showSnackBar("Added successfully!")
                                    getView()?.resetInputFields()
                                } else {
                                    getView()?.showSnackBar("Could not add expense!")
                                }
                            },
                            {
                                getView()?.run {
                                    showSnackBar("Could not add expense!")
                                    enableSubmitBtn()
                                    setSubmitBtnText("save")
                                }
                            },
                            {
                                getView()?.run {
                                    enableSubmitBtn()
                                    setSubmitBtnText("save")
                                }
                            },
                            {
                                getView()?.run {
                                    disableSubmitBtn()
                                    setSubmitBtnText("saving...")
                                }
                            })
        }
    }

    override fun onLoadingRetryClicked() {
        getCategories(getAccount()!!, getEmail())
    }
}