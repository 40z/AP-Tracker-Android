package com.movsoft.aptracker.scenes.base

/**
 * Different states a view model may transition through.
 */
sealed class ViewModelState {
    object NotStarted : ViewModelState()
    object Loading : ViewModelState()
    class Placeholder(val type: ViewModelStatePlaceholder): ViewModelState()
    object Complete : ViewModelState()
}

/**
 * Different types of placeholders.
 */
enum class ViewModelStatePlaceholder {
    Error,
    NoContent,
    SettingsNotValid
}