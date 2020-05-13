package by.iba.sbs.library.data.remote

object Routes {
    private const val baseRoute = "https://by-iba-sbs-api.herokuapp.com"
    private const val version = "api/v1"

    object Users {
        const val URL_USERS = "$baseRoute/$version/users/list"
        const val URL_USER_DETAILS = "$baseRoute/$version/users/item/%s"
    }

    object Guidelines {
        const val URL_GUIDELINES = "$baseRoute/$version/guidelines/list"
        const val URL_GUIDELINE_DETAILS = "$baseRoute/$version/guidelines/item/%s"
        const val URL_GUIDELINE_STEPS_DETAILS = "$baseRoute/$version/guidelines/item/%s/steps/list"
    }
}
