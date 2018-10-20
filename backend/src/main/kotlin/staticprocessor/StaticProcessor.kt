package staticprocessor

import api.cornell.data.classes.Subject
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
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
        val gsonSimpleConverter = Gson()
        val gson = GsonBuilder().registerTypeAdapter(
                CourseInfo::class.java, JsonDeserializer<CourseInfo> { json, typeOfT, context ->
            val obj = json.asJsonObject
            val weightVectorJson = obj["weightVector"]
            CourseInfo(
                    subject = Subject.valueOf(obj["subject"].asString),
                    code = obj["code"].asString,
                    title = obj["title"].asString,
                    description = obj["description"].asString,
                    weightVector = gsonSimpleConverter.toJson(weightVectorJson)
            )
        }).create()
        // javaClass.getResourceAsStream("course-info-list-example.json")
        javaClass.getResourceAsStream("vectors.json")
                .let(block = ::InputStreamReader)
                .let { reader ->
                    val courses: List<CourseInfo> = gson.fromJson(
                            reader, object : TypeToken<List<CourseInfo>>() {}.type
                    )
                    courses
                }
                .let(block = CourseInfo.Companion::addAll)
    }

}
