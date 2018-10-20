@file:JvmName(name = "WebApp")

import auth.GoogleUser
import auth.Role
import auth.SecurityFilters
import auth.SecurityFilters.Companion.user
import com.google.cloud.datastore.Key
import course.CourseInfo
import course.StudentCourse
import init.InitData
import partner.PartnerInvitation
import partner.StudentPartnership
import partner.StudentRatingRecord
import spark.Spark.path
import staticprocessor.StaticProcessor
import student.StudentPublicInfo
import web.badRequest
import web.delete
import web.get
import web.initServer
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
        adminEmails = setOf()
)

/*
 * ------------------------------------------------------------------------------------------
 * Part 1: Route Declarations
 * ------------------------------------------------------------------------------------------
 */

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
    }
    post(path = "/delete") {
        val studentCourse = toJson<StudentCourse>()
        studentCourse.delete()
        "OK"
    }
}

/**
 * [initializePartnerApiHandlers] initializes a list of profile related API handlers.
 */
private fun initializePartnerApiHandlers() {
    /*
    post(path = "/rating") {
        StudentRatingRecord.editRating(record = toJson())
        "OK"
    }
    */
    post(path = "/invite") {
        val success = PartnerInvitation.editPartnerInvitation(invitation = toJson())
        if (success) "OK" else "FAILED"
    }
    post(path = "/respond_invitation") {
        val accepted = queryParams("accepted")?.let { it == "true" } ?: badRequest()
        StudentPartnership.handleInvitation(invitation = toJson(), accepted = accepted)
    }
    post(path = "/remove") {
        val partnership: StudentPartnership = toJson()
        partnership.removeSelf()
        "OK"
    }
}

/**
 * [initializeMatchingApiHandlers] initializes a list of matching related API handlers.
 */
private fun initializeMatchingApiHandlers() {
    get(path = "/general") {
        val keys = GoogleUser.getAllOtherUserKeys(user = user)
        keys.map { StudentPublicInfo.buildForGeneral(studentId = it) }
    }
    get(path = "/course") {
        /*
        val courseId = queryParams("course_id")
                ?.let { Key.fromUrlSafe(it) } ?: badRequest()
                */
        val keys = GoogleUser.getAllOtherUserKeys(user = user)
        keys.map { StudentPublicInfo.buildForGeneral(studentId = it) }
    }
}

/**
 * [initializeUserApiHandlers] initializes a list of user API handlers.
 */
private fun initializeUserApiHandlers() {
    Filters.before(path = "/*", role = Role.USER)
    get(path = "/load") { InitData.getByUser(user = user) }
    path("/profile", ::initializeProfileApiHandlers)
    path("/courses", ::initializeCourseApiHandlers)
    path("/partners", ::initializePartnerApiHandlers)
    path("/matching", ::initializeMatchingApiHandlers)
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