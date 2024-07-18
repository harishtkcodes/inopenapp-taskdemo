package com.example.taskdemo.commons.util

import android.animation.Animator
import android.view.animation.Interpolator

fun Animator.setCustomInterpolator(
    interpolator: Interpolator
): Animator {
    this.interpolator = interpolator
    return this
}