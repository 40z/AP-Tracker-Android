package com.movsoft.aptracker.scenes.base

/**
 * Different states a view model may transition through.
 */
sealed class ViewModelState {
    class NotStarted: ViewModelState()
    class Loading: ViewModelState()
    class Placeholder(val type: ViewModelStatePlaceholder): ViewModelState()
    class Complete: ViewModelState()
}

/**
 * Different types of placeholders.
 */
enum class ViewModelStatePlaceholder {
    Error,
    NoContent,
    SettingsNotValid
}