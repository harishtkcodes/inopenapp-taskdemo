package com.example.taskdemo.core.designsystem.component

class MyBottomAppBarState(
    val hidden: Boolean
) {

    override fun toString(): String {
        return "MyBottomAppBarState[hidden=$hidden]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MyBottomAppBarState

        if (hidden != other.hidden) return false

        return true
    }

    override fun hashCode(): Int {
        return hidden.hashCode()
    }

    companion object {
        internal val Default = MyBottomAppBarState(
            hidden = true
        )
    }
}