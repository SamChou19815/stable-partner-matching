import random
import json

#import pandas as pd

"""
type Student {
    graduationYear: int
    name: string
    pastCourses: string[]
    currentCourses: string[]
    futureCourses: string[]
}

Output type: Student[]


The above translated to python:
student = {
    "name": "Alice",
    "graduationYear": 2021,
    "pastCourses": [{"courseName":"CS 1110", "grade": 10}, {"courseName":"CS 2110", "grade": 12}],
    "currentCourses": [{"courseName":"CS 3110", "grade": -1}, {"courseName":"CS 2800", "grade": -1}],
    "futureCourses": [{"courseName":"CS 3410", "grade": -1}, {"courseName":"CS 4740", "grade": -1}],
}
"""

# stdin
#num_student = input()


# Step -1: Make sure json works
def naive_student():
    student_dict = dict()
    student_dict["name"] = "Alice"
    student_dict["graduationYear"] = 2021
    student_dict["pastCourses"] = [{"courseName":"CS 1110", "grade": 10}, {"course_name":"CS 2110", "grade": 12}]
    student_dict["currentCourses"] = [{"courseName":"CS 3110", "grade": -1}, {"course_name":"CS 2800", "grade": -1}]
    student_dict["futureCourses"] = [{"courseName":"CS 3410", "grade": -1}, {"course_name":"CS 4740", "grade": -1}]
    student_json = json.dumps(student_dict)
    return student_json

print(naive_student())

# Step 1: Randomize the student's name

# TODO: importing a csv of names have some problems.
# names = pd.read_csv("./baby-names.csv")

name_temp = ["Alice", "Bob", "Carol", "Douglas"]

def name_randomize():
    random_name = random.choice(name_temp)
    return random_name

# Step 2: Randomize the graduation year

years = [2019, 2020, 2021, 2022]

def year_randomize():
    random_year = random.choice(years)
    return random_year


# Step 3: Randomize the gpa scores
# 4.3 corresponds to 12, 0 corresponds to 0, no score correspond to -1
# See http://courses.cornell.edu/content.php?catoid=31&navoid=7933

uniform_gpa = range(5, 13)

def gpa_uniform():
    random_gpa = random.choice(uniform_gpa)
    return random_gpa

# TODO: Make the gpa gaussian based on past median

# Step 4: Randomize the past courses
# Idea 1: Typical Student Profile

def grade_courses_gen(past_course_list):
    """
    Takes a list of courses and generate the list of dictionaries of each course
    :param past_course_list:
    :return: dictionary in the form [{"courseName":"CS 3110", "grade": -1}, {"course_name":"CS 2800", "grade": -1}]
    """
    all_courses_list = []
    for course in past_course_list:
        temp_course_dict = dict()
        temp_course_dict['courseName'] = course
        temp_course_dict['grade'] = gpa_uniform()
        all_courses_list.append(temp_course_dict)
    return all_courses_list

def nograde_courses_gen(past_course_list):
    all_courses_list = []
    for course in past_course_list:
        temp_course_dict = dict()
        temp_course_dict['courseName'] = course
        temp_course_dict['grade'] = -1
        all_courses_list.append(temp_course_dict)
    return all_courses_list


# Hard code some typical students

ELECTIVE_LOWER = ["CS 1300", "CS 1380", "CS 1710", "CS 2043", "CS 2300", "CS 2850"]
ELECTIVE_UPPER = ["CS 3300", "CS 3758", "CS 4110", "CS 4120", "CS 4121", "CS 4160",
                  "CS 4220", "CS 4300", "CS 4411", "CS 4450", "CS 4670", "CS 4700",
                  "CS 4701", "CS 4744", "CS 4754", "CS 4780", "CS 4786", "CS 4787",
                  "CS 4810", "CS 4850", "CS 4852"]

def make_choice_dict_prob(prob_1110, prob_1112, prob_2110, prob_2112, prob_2800, prob_3110, prob_3410, prob_3420,
                          prob_4410, prob_4820, lower_prob, upper_prob):
    """

    :return: probability of a class appearing in the student's list [Not specified where]
    """
    choice_dict = dict()
    choice_dict["CS 1110"]=prob_1110
    choice_dict["CS 1112"]=prob_1112
    choice_dict["CS 2110"]=prob_2110
    choice_dict["CS 2112"]=prob_2112
    choice_dict["CS 2800"] = prob_2800
    choice_dict["CS 3110"] = prob_3110
    choice_dict["CS 3410"] = prob_3410
    choice_dict["CS 3420"] = prob_3420
    choice_dict["CS 4410"] = prob_4410
    choice_dict["CS 4820"] = prob_4820

    for new_lower_class in ELECTIVE_LOWER:
        choice_dict[new_lower_class] = lower_prob

    for new_upper_class in ELECTIVE_UPPER:
        choice_dict[new_upper_class] = upper_prob

    return choice_dict


def make_decision_dict(prob_1110, prob_2110, prob_2800, prob_3110, prob_3410,
                          prob_4410, prob_4820, lower_past, lower_present, upper_past, upper_present):
    """

    :return: if a core class is chosen, decide where it should appear. 1 represent past, 2 represent present, 3 represent
    future, 4 placeholder for not happening. If a elective class is chosen, decide the prob for it to appear in past
    or present or future classes
    """

    decision_dict = dict()
    decision_dict["CS 1110"] = prob_1110
    decision_dict["CS 2110"] = prob_2110
    decision_dict["CS 2800"] = prob_2800
    decision_dict["CS 3110"] = prob_3110
    decision_dict["CS 3410"] = prob_3410
    decision_dict["CS 4410"] = prob_4410
    decision_dict["CS 4820"] = prob_4820

    for new_lower_class in ELECTIVE_LOWER:
        decision_dict[new_lower_class+" past"] = lower_past

    for new_lower_class in ELECTIVE_LOWER:
        decision_dict[new_lower_class+" present"] = lower_present

    for new_upper_class in ELECTIVE_UPPER:
        decision_dict[new_upper_class+" present"] = upper_past

    for new_upper_class in ELECTIVE_UPPER:
        decision_dict[new_upper_class+" present"] = upper_present

    return decision_dict

DECISION_ASSOC_DICT = {1:"pastCourses", 2: "currentCourses", 3: "futureCourses", 4: "bad!"}

freshman_1_prob_dict = make_choice_dict_prob(0.8, 0.2, 0.7, 0.2, 0.8, 0, 0, 0, 0, 0, 0.1, 0)
freshman_1_decision_dict = make_decision_dict(2, 3, 3, 4, 4, 4, 4, 0, 0, 0, 0)

def appendClass(class_list, class1, prob):
    if random.random() > prob:
        return class_list.append(class1)
    else:
        return class_list

def appendChoice(class_list, class1, class2, prob_choice1, prob_choice2):
    random_num = random.random()
    if random_num  < prob_choice1:
        return class_list.append(class1)
    elif (random_num >= prob_choice1) and (random_num < prob_choice2):
        return class_list.append(class2)
    else:
        return class_list


def make_student_class_dict(student_choice_dict, student_decision_dict):

    # initialize the dict
    student_class_dict = dict()
    student_class_dict["pastCourses"] = []
    student_class_dict["currentCourses"] = []
    student_class_dict["futureCourses"] = []

    # consider appending the core classes first
    # deal with CS 1110, CS 1112
    if (student_decision_dict["CS 1110"] < 4):
        # update_type gives past/current/future courses
        update_type = DECISION_ASSOC_DICT.get(student_decision_dict["CS 1110"])
        # provide the probabilities for 1110 and 1112
        prob_1110 = student_choice_dict["CS 1110"]
        prob_1112 = student_choice_dict["CS 1112"]
        student_class_dict[update_type] = \
            appendChoice(student_class_dict[update_type],"CS 1110", "CS 1112", prob_1110, prob_1112)


    """
    appendClass(first_year_1_dict["futureCourses"], "CS 2800", freshman_1_prob_dict["CS 2800"])

    for new_lower_class in ELECTIVE_LOWER:
        decision_dict[new_lower_class+" past"] = lower_past
    """

    pass



first_year_1_dict = dict()
first_year_1_dict["pastCourses"] = []
first_year_1_dict["currentCourses"] = ['CS 1110']
first_year_1_dict["futureCourses"] = ['CS 2110', 'CS 2800']

first_year_2_dict = dict()
first_year_2_dict["pastCourses"] = ['CS 1110']
first_year_2_dict["currentCourses"] = ['CS 2110']
first_year_2_dict["futureCourses"] = ['CS 2800', 'CS 3110']







# Step infty: Put everything together!
def make_student(course_dict):
    """

    :param past_course: A list of the student's past courses
    :param current_course: A list of the student's current courses
    :param future_course: A list of the students's future courses
    :return: a dictionary of student with profiles
    """

    new_student_dict = dict()
    new_student_dict['name'] = name_randomize()
    new_student_dict['graduationYear'] = year_randomize()
    new_student_dict['pastCourses'] = grade_courses_gen(course_dict['pastCourses'])
    new_student_dict['currentCourses'] = nograde_courses_gen(course_dict['currentCourses'])
    new_student_dict['futureCourses'] = nograde_courses_gen(course_dict['futureCourses'])


#make_student()

# stdout



print("Hello World")



