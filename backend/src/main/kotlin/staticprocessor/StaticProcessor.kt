package staticprocessor

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import course.CourseInfo
import java.io.InputStreamReader

/**
 * [StaticProcessor] processes the static file in resources.
 */
object StaticProcessor {

    /**
     * [importAllCourses] imports all courses
     */
    fun importAllCourses() {
        javaClass.getResourceAsStream("course-info-list-example.json")
                .let(block = ::InputStreamReader)
                .let { reader ->
                    val courses: List<CourseInfo> = Gson().fromJson(
                            reader, object : TypeToken<List<CourseInfo>>() {}.type
                    )
                    courses
                }
                .let(block = CourseInfo.Companion::addAll)
    }

}
