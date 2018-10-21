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
import spark.Spark.notFound
import spark.Spark.path
import spark.Spark.staticFileLocation
import staticprocessor.StaticProcessor
import student.StudentPublicInfo
import web.badRequest
import web.get
import web.initServer
import web.post
import web.queryParamsForKey
import web.toJson
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.system.measureTimeMillis

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
    get(path = "/load_public") {
        val studentId = queryParamsForKey(name = "student_id")
        StudentPublicInfo.buildForGeneral(studentId = studentId)
    }
    post(path = "/update") {
        val updatedUser = user.updateWith(anotherUser = toJson())
        updatedUser.upsert()
    }
}

/**
 * [initializeCourseApiHandlers] initializes a list of course related API handlers.
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
    /*
    get(path = "/general") {
        val keys = GoogleUser.getAllOtherUserKeys(user = user)
        keys.map { StudentPublicInfo.buildForGeneral(studentId = it) }
    }
    */
    get(path = "/course") {
        val startTime = System.currentTimeMillis()
        val courseId = queryParamsForKey(name = "course_id")
        val courseInfo = CourseInfo[courseId]!!
        val result = Ranking(user, courseInfo).rankingForCourse
        val endTime = System.currentTimeMillis()
        println("Matching Running Time: ${endTime - startTime}.")
        result
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
    get(path = "/init_system") {
        println("Init System")
        println(measureTimeMillis { CourseInfo.deleteAll() })
        println(measureTimeMillis { GoogleUser.deleteAll() })
        println(measureTimeMillis { StudentCourse.deleteAll() })
        println(measureTimeMillis { StaticProcessor.importAllCourses() })
        println(measureTimeMillis { StaticProcessor.importAllRandomUsers() })
    }
    get(path = "/init_system_std_courses") {
        println("Init System Student Courses")
        println(measureTimeMillis { StaticProcessor.importAllRandomUserCourses() })
    }
}

/**
 * [initializeApiHandlers] initializes a list of handlers.
 */
private fun initializeApiHandlers() {
    staticFileLocation("/static")
    notFound { _, resp ->
        resp.status(200)
        StaticProcessor::class.java.getResourceAsStream("/static/index.html")
                .let(block = ::InputStreamReader)
                .let(block = ::BufferedReader)
                .lineSequence()
                .joinToString(separator = "\n")
    }
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