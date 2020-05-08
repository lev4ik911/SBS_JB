package by.iba.sbs.binding

import by.iba.sbs.library.model.Step

class StepBinding(val value: Step) {
    val name = value.name
    val description = value.description
    val stepNo = value.stepId.toString()
}