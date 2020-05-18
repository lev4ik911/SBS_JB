package by.iba.sbs.library.data.remote


data class Response<out T>(val status: Status, val data: T?, val error: Throwable?) {

    val isSuccess = status == Status.SUCCESS
    val isNotEmpty = data != null

    companion object {
        fun <T> success(data: T?): Response<T> {
            return Response(
                Status.SUCCESS,
                data,
                null
            )
        }

        fun <T> error(error: Throwable, data: T? = null): Response<T> {
            return Response(
                Status.ERROR,
                data,
                error
            )
        }

        fun <T> unauthorized(): Response<T> {
            return Response(
                Status.UNAUTHORIZED,
                null,
                null
            )
        }

        fun <T> loading(data: T?): Response<T> {
            return Response(
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
