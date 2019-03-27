package com.movsoft.aptracker.models

class TrackedItem(val name: String) {

    override fun equals(other: Any?): Boolean {
        return if (other is TrackedItem) name == other.name else false
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}