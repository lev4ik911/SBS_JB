package by.iba.sbs.library.data.remote

object Routes {
    private const val baseRoute = "https://by-iba-sbs-api.herokuapp.com"
    private const val version = "api/v1"

    object Auth {
        const val URL_USER = "$baseRoute/$version/auth"
        const val URL_LOGIN = "$baseRoute/$version/auth/login"
        const val URL_REGISTER = "$baseRoute/$version/auth/register"
    }

    object Users {
        const val URL_USERS = "$baseRoute/$version/users/list"
        const val URL_USER_DETAILS = "$baseRoute/$version/users/item/%s"
    }

    object Guidelines {
        const val URL_GUIDELINES = "$baseRoute/$version/guidelines/list"
        const val URL_GUIDELINE_DETAILS = "$baseRoute/$version/guidelines/item/%s"
        const val URL_GUIDELINE_STEPS = "$baseRoute/$version/guidelines/item/%s/steps/list"
        const val URL_GUIDELINE_STEPS_BATCHING =
            "$baseRoute/$version/guidelines/item/%s/steps/batching"
        const val URL_GUIDELINE_STEP_DETAILS =
            "$baseRoute/$version/guidelines/item/%s/steps/item/%s"
    }

    object Ratings {
        const val URL_GUIDELINE_RATINGS = "$baseRoute/$version/guidelines/item/%s/ratings/list"
        const val URL_GUIDELINE_RATING_DETAILS =
            "$baseRoute/$version/guidelines/item/%s/ratings/item/%s"
    }
}
