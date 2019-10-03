package com.luvapay.bsigner.activities

import android.os.Bundle
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.fragments.HomeFragment
import com.luvapay.bsigner.utils.getColorCompat
import com.luvapay.bsigner.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.android.synthetic.main.activity_home.activityHome_menuBtn as menuBtn

class HomeActivity : BaseActivity() {

    private val vm: HomeViewModel by viewModel()

    private val homeFrag: HomeFragment by lazy { HomeFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportFragmentManager.commit {
            replace(R.id.activityHome_fragmentContainer, homeFrag)
        }

        menuBtn.setOnClickListener {
            vm.canModify.value = !(vm.canModify.value ?: true)
        }

        vm.canModify.observe(this, Observer {
            updateBtnUI(it)
        })

    }

    private fun updateBtnUI(canModify: Boolean) {
        if (canModify) {
            menuBtn.apply {
                setBackgroundColor(getColorCompat(R.color.colorPrimary))
                icon = getDrawable(R.drawable.ic_check)
            }
        } else {
            menuBtn.apply {
                setBackgroundColor(getColorCompat(R.color.colorAccent))
                icon = getDrawable(R.drawable.ic_edit)
            }
        }
    }

}