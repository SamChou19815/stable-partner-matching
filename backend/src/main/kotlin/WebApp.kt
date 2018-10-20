@file:JvmName(name = "WebApp")

import auth.GoogleUser
import auth.Role
import auth.SecurityFilters
import auth.SecurityFilters.Companion.user
import course.CourseInfo
import course.StudentCourse
import init.InitData
import partner.PartnerInvitation
import partner.StudentPartnership
import partner.StudentRatingRecord
import web.get
import web.initServer
import spark.Spark.path
import staticprocessor.StaticProcessor
import web.badRequest
import web.delete
import web.post
import web.toJson

/*
 * ------------------------------------------------------------------------------------------
 * Part 0: Config
 * ------------------------------------------------------------------------------------------
 */

/**
 * [Filters] can be used to create security filters.
 */
private object Filters : SecurityFilters(
        adminEmails = setOf("sam@developersam.com")
)

/*
 * ------------------------------------------------------------------------------------------
 * Part 1: Route Declarations
 * ------------------------------------------------------------------------------------------
 */

/**
 * [initializeLoadApiHandlers] initializes a list of load related API handlers.
 */
private fun initializeLoadApiHandlers() {
    get(path = "/load") {
        InitData.getByUser(user = user)
    }
}

/**
 * [initializeProfileApiHandlers] initializes a list of profile related API handlers.
 */
private fun initializeProfileApiHandlers() {
    post(path = "/update") {
        val updatedUser = user.updateWith(anotherUser = toJson())
        updatedUser.upsert()
        "OK"
    }
}

/**
 * [initializeCourseApiHandlers] initializes a list of profile related API handlers.
 */
private fun initializeCourseApiHandlers() {
    post(path = "/edit") {
        val studentCourse = toJson<StudentCourse>()
        studentCourse.upsert()
        "OK"
    }
    delete(path = "/delete") {
        val studentCourse = toJson<StudentCourse>()
        studentCourse.delete()
        "OK"
    }
}

/**
 * [initializePartnerApiHandlers] initializes a list of profile related API handlers.
 */
private fun initializePartnerApiHandlers() {
    post(path = "/rating") {
        StudentRatingRecord.editRating(record = toJson())
        "OK"
    }
    post(path = "/invite") {
        PartnerInvitation.editPartnerInvitation(invitation = toJson())
        "OK"
    }
    post(path = "/respond_invitation") {
        val accepted = queryParams("accepted")?.let { it == "true" } ?: badRequest()
        StudentPartnership.handleInvitation(invitation = toJson(), accepted = accepted)
        "OK"
    }
}

/**
 * [initializeUserApiHandlers] initializes a list of user API handlers.
 */
private fun initializeUserApiHandlers() {
    Filters.before(path = "/*", role = Role.USER)
    initializeLoadApiHandlers()
    path("/profile", ::initializeProfileApiHandlers)
    path("/courses", ::initializeCourseApiHandlers)
    path("/partner", ::initializePartnerApiHandlers)
}

/**
 * [initializeAdminApiHandlers] initializes a list of admin API handlers.
 */
private fun initializeAdminApiHandlers() {
    // Filters.before(path = "/*", role = Role.ADMIN) // TODO add back
    get(path = "/init_courses") {
        StaticProcessor.importAllCourses()
    }
    get(path = "/remove_courses") {
        CourseInfo.removeAll()
    }
}

/**
 * [initializeApiHandlers] initializes a list of handlers.
 */
private fun initializeApiHandlers() {
    get(path = "/") { "OK" } // Used for health check
    path("/apis", ::initializeUserApiHandlers)
    path("/admin_apis", ::initializeAdminApiHandlers)
}

/*
 * ------------------------------------------------------------------------------------------
 * Part 2: Main
 * ------------------------------------------------------------------------------------------
 */

/**
 * [main] is the entry point.
 *
 * @param args these info will be ignored right now.
 */
fun main(args: Array<String>): Unit = initServer(initFunction = ::initializeApiHandlers)