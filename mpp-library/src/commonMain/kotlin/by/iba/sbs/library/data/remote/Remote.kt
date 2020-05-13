package by.iba.sbs.library.data.remote


data class Remote<out T>(val status: Status, val data: T?, val error: Throwable?) {

    val isSuccess = status == Status.SUCCESS
    val isNotEmpty = data != null

    companion object {
        fun <T> success(data: T?): Remote<T> {
            return Remote(
                Status.SUCCESS,
                data,
                null
            )
        }

        fun <T> error(error: Throwable, data: T? = null): Remote<T> {
            return Remote(
                Status.ERROR,
                data,
                error
            )
        }

        fun <T> unauthorized(): Remote<T> {
            return Remote(
                Status.UNAUTHORIZED,
                null,
                null
            )
        }

        fun <T> loading(data: T?): Remote<T> {
            return Remote(
                Status.LOADING,
                data,
                null
            )
        }
    }

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING,
        UNAUTHORIZED
    }
}
