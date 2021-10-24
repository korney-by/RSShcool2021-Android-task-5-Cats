package com.korneysoft.rsshcool2021_android_task_5_cats.ui.extension

import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import kotlin.math.min

//TODO hamburger?
fun Toolbar.setToolbarHamburgerButton(iconResource: Int, action: () -> Unit) {
    if (iconResource == 0) {
        navigationIcon = null
    } else {
        navigationIcon = AppCompatResources.getDrawable(context, iconResource)
        setNavigationOnClickListener { action() }
    }
}

fun Toolbar.setToolBarMenu(
    menuResource: Int,
    actions: Array<() -> Unit>
) {
    menu.clear()
    if (menuResource != 0) {
        inflateMenu(menuResource)

        // set actions to menu items
        for (i in 0 until min(menu.size(), actions.size)) {
            menu.getItem(i).setOnMenuItemClickListener {
                actions[i]()
                true
            }
        }
    }
}

//TODO Naming
fun Toolbar.clearExceptTitle() {
    subtitle = ""
    clearHamburgerButton()
    clearMenu()
}

fun Toolbar.clearMenu() {
    menu.clear()
}

fun Toolbar.clearHamburgerButton() {
    setToolbarHamburgerButton(0) {}
}
