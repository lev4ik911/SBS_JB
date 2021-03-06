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
        const val URL_USERS = "$baseRoute/$version/users"
        const val URL_USERS_MY = "$baseRoute/$version/users/my"
        const val URL_USER_DETAILS = "$baseRoute/$version/users/%s"
        const val URL_USER_GUIDELINES = "$baseRoute/$version/users/%s/selfs/guidelines"
        const val URL_USER_FAVORITES = "$baseRoute/$version/users/%s/selfs/favourites"
    }

    object Guidelines {
        const val URL_GUIDELINES = "$baseRoute/$version/guidelines"
        const val URL_GUIDELINES_POPULAR = "$baseRoute/$version/guidelines/popular"
        const val URL_GUIDELINE_DETAILS = "$baseRoute/$version/guidelines/%s"
        const val URL_GUIDELINE_STEPS = "$baseRoute/$version/guidelines/%s/steps"
        const val URL_GUIDELINE_STEPS_BATCHING = "$baseRoute/$version/guidelines/%s/steps/batching"
        const val URL_GUIDELINE_STEP_DETAILS = "$baseRoute/$version/guidelines/%s/steps/%s"
        const val URL_GUIDELINE_FILES = "$baseRoute/$version/guidelines/%s/files"
        const val URL_GUIDELINE_FILE_DETAILS = "$baseRoute/$version/guidelines/%s/files/%s"
        const val URL_GUIDELINE_STEP_FILES = "$baseRoute/$version/guidelines/%s/steps/%s/files"
        const val URL_GUIDELINE_STEP_FILE_DETAILS = "$baseRoute/$version/guidelines/%s/steps/%s/files/%s"
    }

    object Ratings {
        const val URL_GUIDELINE_RATINGS = "$baseRoute/$version/guidelines/%s/ratings"
        const val URL_GUIDELINE_RATING_DETAILS = "$baseRoute/$version/guidelines/%s/ratings/%s"
    }

    object Favorites {
        const val URL_GUIDELINE_FAVORITES = "$baseRoute/$version/guidelines/%s/favourites"
        const val URL_GUIDELINE_FAVORITES_DETAILS = "$baseRoute/$version/guidelines/%s/favourites/%s"
    }
}
