package auth

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import org.pac4j.core.authorization.authorizer.RequireAllRolesAuthorizer
import org.pac4j.core.authorization.generator.AuthorizationGenerator
import org.pac4j.core.client.Clients
import org.pac4j.core.config.Config
import org.pac4j.core.context.DefaultAuthorizers
import org.pac4j.core.credentials.TokenCredentials
import org.pac4j.core.profile.CommonProfile
import org.pac4j.core.profile.ProfileManager
import org.pac4j.http.client.direct.HeaderClient
import org.pac4j.sparkjava.DefaultHttpActionAdapter
import org.pac4j.sparkjava.SecurityFilter
import org.pac4j.sparkjava.SparkWebContext
import spark.Request
import spark.Response
import spark.Spark
import spark.kotlin.before
import spark.kotlin.halt
import java.net.URI
import java.util.logging.Level
import java.util.logging.Logger

/**
 * [SecurityFilters] can build different specialized security filters.
 * Best practice: when using it, you should create a singleton from this class.
 *
 * @property adminEmails the list of emails belong to the admin user, which defaults to `null`.
 */
open class SecurityFilters(private val adminEmails: Set<String> = emptySet()) {

    /**
     * [firebaseApp] is the global firebase app.
     */
    val firebaseApp: FirebaseApp = FirebaseApp.initializeApp()

    /**
     * [firebaseAuth] is the global Authentication Handler.
     */
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(firebaseApp)

    /**
     * [authorizationGenerator] is the generator used to assign roles to users.
     */
    private val authorizationGenerator = AuthorizationGenerator<CommonProfile> { _, p ->
        p.apply {
            val user = (p as Profile).user
            val role = if (user.email in adminEmails) Role.ADMIN else Role.USER
            addRole(role.name)
        }
    }

    /**
     * [headerClient] is the specialized client for processing the firebase token from header.
     */
    private val headerClient: HeaderClient = HeaderClient(HEADER_NAME) { credentials, _ ->
        val idToken = (credentials as? TokenCredentials)?.token ?: return@HeaderClient
        val firebaseToken = try {
            firebaseAuth.verifyIdToken(idToken)
        } catch (e: Exception) {
            Logger.getGlobal().log(Level.SEVERE, e) { "Auth Error" }
            return@HeaderClient
        }
        val googleUser = GoogleUser(
                uid = firebaseToken.uid, name = firebaseToken.name,
                email = firebaseToken.email, picture = firebaseToken.picture
        ).upsert(modifyAppInfo = false)
        val profile = Profile(googleUser)
        credentials.userProfile = profile
    }.apply { addAuthorizationGenerator(authorizationGenerator) }

    /**
     * [clients] is a set of singleton clients.
     */
    private val clients = Clients(headerClient)

    /**
     * [withRole] returns a new security filter that requires the user to have certain [role].
     */
    private fun withRole(role: Role): SecurityFilter = Config(clients).apply {
        addAuthorizer(ROLE_AUTHORIZER_NAME,
                RequireAllRolesAuthorizer<Profile>(role.name))
        httpActionAdapter = DefaultHttpActionAdapter()
    }.let(block = ::UserSecurityFilter)

    /**
     * [before] registers a before security filter with [path] and a user given a required [role].
     */
    fun before(path: String, role: Role): Unit = Spark.before(path, withRole(role = role))

    /**
     * [Profile] is the user profile of the firebase user.
     *
     * @constructor created and delegated by a [GoogleUser] [user].
     */
    private class Profile(val user: GoogleUser) : CommonProfile() {
        override fun getId(): String = user.uid
        override fun getEmail(): String = user.email
        override fun getDisplayName(): String = user.name
        override fun getUsername(): String = user.name
        override fun getPictureUrl(): URI = URI(user.picture)

        init {
            setId(user.uid)
            isRemembered = true
        }

    }

    /**
     * [UserSecurityFilter] is the [SecurityFilter] that automatically sets the user account in
     * the attribute for later usage.
     *
     * @param config the config constructed above.
     */
    private class UserSecurityFilter(config: Config) :
            SecurityFilter(config, HEADER_CLIENT_NAME, AUTHORIZER_NAMES) {

        override fun handle(request: Request, response: Response) {
            super.handle(request, response)
            val context = SparkWebContext(request, response)
            val manager = ProfileManager<Profile>(context)
            manager.get(false).takeIf { it.isPresent }?.let { profileOpt ->
                request.attribute(USER_ATTRIBUTE_NAME, profileOpt.get().user)
            }
        }

    }

    companion object {

        /**
         * [HEADER_NAME] is the name of header.
         */
        private const val HEADER_NAME: String = "Firebase-Auth-Token"

        /**
         * [HEADER_CLIENT_NAME] is the name of header client.
         */
        private const val HEADER_CLIENT_NAME: String = "HeaderClient"

        /**
         * [ROLE_AUTHORIZER_NAME] is the name of the authorizer that checks roles.
         */
        private const val ROLE_AUTHORIZER_NAME: String = "RoleAuthorizer"

        /**
         * [AUTHORIZER_NAMES] are the names of the used authorizers.
         */
        private const val AUTHORIZER_NAMES: String =
                "$ROLE_AUTHORIZER_NAME,${DefaultAuthorizers.SECURITYHEADERS}"

        /**
         * [USER_ATTRIBUTE_NAME] is the attribute name for user.
         */
        private const val USER_ATTRIBUTE_NAME: String = "GoogleUser"

        /**
         * [Request.user] returns the [GoogleUser] detected from the request.
         * If a user is not found, it will throw halt with 401 error code.
         */
        val Request.user: GoogleUser
            get() = attribute(USER_ATTRIBUTE_NAME) ?: throw halt(code = 401)

    }

}
