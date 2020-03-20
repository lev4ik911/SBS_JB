package model

expect class Sample {
    fun checkMe(): Int
}

expect object Platform {
    val name: String
}