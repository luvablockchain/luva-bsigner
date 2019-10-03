package com.luvapay.bsigner.activities

import android.os.Bundle
import androidx.fragment.app.commit
import com.luvapay.bsigner.R
import com.luvapay.bsigner.activities.account.AccountDetailActivity
import com.luvapay.bsigner.activities.account.BackupWarningActivity
import com.luvapay.bsigner.activities.account.RecoverAccountActivity
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.entities.StellarAccount
import com.luvapay.bsigner.fragments.AccountFragment
import com.luvapay.bsigner.fragments.HomeFragment
import com.luvapay.bsigner.utils.showPopupMenu
import com.luvapay.bsigner.viewmodel.HomeViewModel
import com.orhanobut.logger.Logger
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.android.synthetic.main.activity_home.activityHome_menuBtn as menuBtn

class HomeActivity : BaseActivity() {

    private val vm: HomeViewModel by viewModel()

    private var modifyMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportFragmentManager.commit {
            replace(R.id.activityHome_fragmentContainer, HomeFragment())
        }

        menuBtn.setOnClickListener {

            modifyMode = !modifyMode

            menuBtn.icon = if (modifyMode) getDrawable(R.drawable.ic_check) else getDrawable(R.drawable.ic_edit)

            showPopupMenu(it, R.menu.menu_home) { menuItem ->
                when (menuItem.itemId) {
                    R.id.home_create_account -> startActivity<BackupWarningActivity>()
                    R.id.home_restore_account -> startActivity<RecoverAccountActivity>()
                }
            }
        }

    }


}
