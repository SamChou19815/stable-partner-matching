import random
import json
import string

import numpy as np
import pandas as pd

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
    "email": adk9@cornell.edu, 
    "picture": http://profilepicturesdp.com/wp-content/uploads/2018/06/default-user-profile-picture-7.png, 
    "uid": random-user-
}
"""

num_student = 1000


# Step 1: Randomize the student's name and email
# Using data from https://github.com/hadley/data-baby-names

names = pd.read_csv("./baby-names.csv")
name_list = names['name'].tolist()[0:1000]

name_temp = ["Alice", "Bob", "Carol", "Douglas"]

def name_randomize():
    random_name = random.choice(name_list)
    return random_name

def email_randomize(initial):
    random_num = random.randrange(10000, 50000)
    string_length = random.randrange(2, 4)
    if string_length == 2:
        random_string = random.choice(str.lower(string.ascii_letters))
    else:
        random_string = random.choice(str.lower(string.ascii_letters)) + random.choice(str.lower(string.ascii_letters))
    final_email = str.lower(initial) + random_string + str(random_num) + "@cornell.edu"
    return final_email

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

def gpa_gaussian():
    gaussian_gpa = int(np.random.normal(9, 2.0))
    return gaussian_gpa

def gpa_good_gaussian():
    gaussian_gpa = int(np.random.normal(10.5, 1.0))
    if gaussian_gpa > 12:
        gaussian_gpa = 12
    return gaussian_gpa

# Step 4: Randomize the past courses
# Idea 1: Typical Student Profile

def grade_courses_gen(past_course_list):
    """
    Takes a list of courses and generate the list of dictionaries of each course
    :param past_course_list: the past course list
    :return: dictionary in the form [{"courseName":"CS 3110", "grade": -1}, {"course_name":"CS 2800", "grade": -1}]
    """
    proportion_good_student = 0.2
    if (random.random() < proportion_good_student):
        good_student_flag = True
    else:
        good_student_flag = False
    all_courses_list = []
    for course in past_course_list:
        temp_course_dict = dict()
        temp_course_dict['courseName'] = course
        if good_student_flag:
            temp_course_dict['grade'] = gpa_good_gaussian()
        else:
            temp_course_dict['grade'] = gpa_gaussian()
        all_courses_list.append(temp_course_dict)
    return all_courses_list

def nograde_courses_gen(past_course_list):
    """
        Takes a list of courses and generate the list of dictionaries of each course
        :param past_course_list: the past course list
        :return: dictionary in the form [{"courseName":"CS 3110", "grade": -1}, {"course_name":"CS 2800", "grade": -1}]
        """
    all_courses_list = []
    for course in past_course_list:
        temp_course_dict = dict()
        temp_course_dict['courseName'] = course
        temp_course_dict['grade'] = -1
        all_courses_list.append(temp_course_dict)
    return all_courses_list


# Hard code some courses
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
        decision_dict[new_upper_class+" past"] = upper_past

    for new_upper_class in ELECTIVE_UPPER:
        decision_dict[new_upper_class+" present"] = upper_present

    return decision_dict

# associate numbers with locations to put the courses
DECISION_ASSOC_DICT = {1:"pastCourses", 2: "currentCourses", 3: "futureCourses", 4: "bad!"}


def append_class(class_list, class1, prob):
    if random.random() < prob:
        class_list.append(class1)
    return class_list


def append_class_update(student_class_dict, student_choice_dict, student_decision_dict, class1):
    """
    :param student_class_dict: dict of student classes
    :param student_choice_dict:
    :param student_decision_dict:
    :param class1:
    :return: update a class based on choice_dict/decision_dict
    """
    if (student_decision_dict[class1] < 4):
        # update_type gives past/current/future courses
        update_type = DECISION_ASSOC_DICT.get(student_decision_dict[class1])
        # provide the probabilities for the class
        prob_class1 = student_choice_dict[class1]
        student_class_dict[update_type] = \
            append_class(student_class_dict[update_type], class1, prob_class1)
    return student_class_dict

def append_choice(class_list, class1, class2, prob_choice1, prob_choice2):
    """
    :param class_list: List of classes we're working with
    :param class1: first class
    :param class2: second class
    :param prob_choice1: prob of choosing first class
    :param prob_choice2: prob of choosing second class
    :return: a new class list that adds a choice between two classes base on given prob.
    """
    random_num = random.random()
    if random_num < prob_choice1:
        class_list.append(class1)
    elif (random_num >= prob_choice1) and (random_num < prob_choice1+prob_choice2):
        class_list.append(class2)
    return class_list


def append_choice_update(student_class_dict, student_choice_dict, student_decision_dict, class1, class2):
    """
    :param student_class_dict: dict of student classes
    :param student_choice_dict:
    :param student_decision_dict:
    :param class1:
    :param class2:
    :return: update a class based on choice_dict/decision_dict
    """

    if (student_decision_dict[class1] < 4):
        # update_type gives past/current/future courses
        update_type = DECISION_ASSOC_DICT.get(student_decision_dict[class1])
        # provide the probabilities for 1110 and 1112
        prob_class1 = student_choice_dict[class1]
        prob_class2 = student_choice_dict[class2]
        student_class_dict[update_type] = append_choice(student_class_dict[update_type], class1, class2, prob_class1, prob_class2)
    return student_class_dict


def append_elective_update(student_class_dict, student_choice_dict, student_decision_dict, class1):
    """
    :param student_class_dict: dict of student classes
    :param student_choice_dict:
    :param student_decision_dict:
    :param class1:
    :return: update an elective class based on choice_dict/decision_dict
    """

    random_choice = random.random()
    random_class = random.random()
    if random_choice < student_choice_dict[class1]:
        if (random_class < student_decision_dict[class1 + " past"]):
            student_class_dict["pastCourses"].append(class1)
        elif (random_class >= student_decision_dict[class1 + " past"] and
              random_class < student_decision_dict[class1 + " past"]+student_decision_dict[class1 + " present"]):
            student_class_dict["currentCourses"].append(class1)
        else:
            student_class_dict["futureCourses"].append(class1)
    return student_class_dict


def make_student_class_dict(student_choice_dict, student_decision_dict):
    """
    Make the classes dict for a student, given his choice dict and decision dict
    :param student_choice_dict: A dictionary that determines the prob that a course is chosen in the list
    :param student_decision_dict: A dictionary that determines where the course should be
    :return: A dictionary containing list of past/present/future courses
    """

    # initialize the dict
    student_class_dict = dict()
    student_class_dict["pastCourses"] = []
    student_class_dict["currentCourses"] = []
    student_class_dict["futureCourses"] = []


    # consider appending the core classes first
    append_choice_update(student_class_dict, student_choice_dict, student_decision_dict, "CS 1110", "CS 1112")
    append_choice_update(student_class_dict, student_choice_dict, student_decision_dict, "CS 2110", "CS 2112")
    append_choice_update(student_class_dict, student_choice_dict, student_decision_dict, "CS 3410", "CS 3420")

    append_class_update(student_class_dict, student_choice_dict, student_decision_dict, "CS 2800")
    append_class_update(student_class_dict, student_choice_dict, student_decision_dict, "CS 3110")
    append_class_update(student_class_dict, student_choice_dict, student_decision_dict, "CS 4410")
    append_class_update(student_class_dict, student_choice_dict, student_decision_dict, "CS 4820")

    # then deal with the elective classes
    for new_lower_class in ELECTIVE_LOWER:
        append_elective_update(student_class_dict, student_choice_dict, student_decision_dict, new_lower_class)

    for new_upper_class in ELECTIVE_UPPER:
        append_elective_update(student_class_dict, student_choice_dict, student_decision_dict, new_upper_class)

    return student_class_dict



# Step infty: Put everything together!
def make_one_student(course_dict, year_input, index):
    """

    :param past_course: A list of the student's past courses
    :param current_course: A list of the student's current courses
    :param future_course: A list of the students's future courses
    :return: a dictionary of student with profiles
    """

    new_student_dict = dict()
    new_student_dict['name'] = name_randomize()
    new_student_dict['graduationYear'] = year_input
    new_student_dict['pastCourses'] = grade_courses_gen(course_dict['pastCourses'])
    new_student_dict['currentCourses'] = nograde_courses_gen(course_dict['currentCourses'])
    new_student_dict['futureCourses'] = nograde_courses_gen(course_dict['futureCourses'])
    new_student_dict['uid'] = "random_user_"+str(index)
    new_student_dict['picture'] = \
        'http://profilepicturesdp.com/wp-content/uploads/2018/06/default-user-profile-picture-7.png'
    new_student_dict['email'] = email_randomize(new_student_dict['name'][0])
    return new_student_dict


# freshman 1
# current: CS 1110
# future: CS 2110/CS2112 and CS 2800 at some prob.
freshman_1_choice_dict = make_choice_dict_prob(0.8, 0.2, 0.8, 0.1, 0.8, 0, 0, 0, 0, 0, 0.1, 0)
freshman_1_decision_dict = make_decision_dict(2, 3, 3, 4, 4, 4, 4, 0, 0, 0, 0)

# freshman 2
# past: CS 1110
# current: CS 2110/2112
# future: CS 3110/3410 and CS 2800, each at some prob
freshman_2_choice_dict = make_choice_dict_prob(0.8, 0.2, 0.9, 0.1, 0.95, 0.5, 0.25, 0.25, 0, 0, 0.2, 0)
freshman_2_decision_dict = make_decision_dict(1, 2, 3, 3, 3, 4, 4, 0, 0, 0, 0)

# freshman 3
# past: CS 1110
# current: CS 2110/CS 2112, CS 2800
# future: CS 3110/3410, CS 4820
freshman_3_choice_dict = make_choice_dict_prob(0.8, 0.2, 0.9, 0.1, 0.9, 0.5, 0.25, 0.25, 0, 0.1, 0.3, 0.05)
freshman_3_decision_dict = make_decision_dict(1, 2, 2, 3, 3, 4, 3, 0, 0, 0, 0)

# freshman/sophomore 4
# past: CS 1110, CS 2110/CS 2112
# current: CS 2800, CS 3110
# future:  CS 3410, CS 4820
freshman_4_choice_dict = make_choice_dict_prob(0.8, 0.2, 0.9, 0.1, 1, 1, 0.5, 0.5, 0, 0.3, 0.2, 0.05)
freshman_4_decision_dict = make_decision_dict(1, 1, 2, 2, 3, 4, 3, 0, 0.1, 0, 0)

# freshman/sophomore 5
# past: CS 1110, CS 2110/CS 2112
# current: CS 2800, CS 3410
# future:  CS 3110, CS 4410, CS 4820
freshman_5_choice_dict = make_choice_dict_prob(0.8, 0.2, 0.9, 0.1, 1, 0.8, 0.5, 0.5, 0.3, 0.3, 0.2, 0.05)
freshman_5_decision_dict = make_decision_dict(1, 1, 2, 3, 2, 3, 3, 0, 0.1, 0, 0)

# freshman/sophomore 6
# past: CS 1110, CS 2110/CS 2112, CS 2800
# current: CS 3410
# future:  CS 3110, CS 4410, CS 4820
freshman_6_choice_dict = make_choice_dict_prob(0.8, 0.2, 0.9, 0.1, 1, 0.8, 0.5, 0.5, 0.3, 0.3, 0.2, 0.1)
freshman_6_decision_dict = make_decision_dict(1, 1, 1, 3, 2, 3, 3, 0.1, 0.3, 0, 0.1)

# freshman/sophomore 7
# past: CS 1110, CS 2110/CS 2112, CS 2800
# current: CS 3110
# future:  CS 3410, CS 4820
freshman_7_choice_dict = make_choice_dict_prob(0.8, 0.2, 0.9, 0.1, 1, 0.8, 0.5, 0.5, 0, 0.3, 0.2, 0.1)
freshman_7_decision_dict = make_decision_dict(1, 1, 1, 2, 3, 4, 3, 0.1, 0.3, 0, 0.1)

# sophomore 8
# past: CS 1110, CS 2110/CS 2112, CS 2800, CS 3110
# current: CS 3410
# future: CS 4410, CS 4820
freshman_8_choice_dict = make_choice_dict_prob(0.8, 0.2, 0.9, 0.1, 1, 0.8, 0.5, 0.5, 0, 0.7, 0.1, 0.2)
freshman_8_decision_dict = make_decision_dict(1, 1, 1, 1, 2, 3, 3, 0.3, 0.1, 0.1, 0.2)

# sophomore 9
# past: CS 1110, CS 2110/CS 2112, CS 2800, CS 3410
# current: CS 3110
# future: CS 4410, CS 4820
freshman_9_choice_dict = make_choice_dict_prob(0.8, 0.2, 0.9, 0.1, 1, 1, 0.5, 0.5, 0.8, 0.8, 0.1, 0.2)
freshman_9_decision_dict = make_decision_dict(1, 1, 1, 2, 1, 3, 3, 0.3, 0.1, 0.1, 0.2)

# sophomore 10
# past: CS 1110, CS 2110/CS 2112, CS 2800, CS 3410
# current: CS 3110, CS 4410
# future: CS 4820
freshman_10_choice_dict = make_choice_dict_prob(0.8, 0.2, 0.9, 0.1, 1, 1, 0.5, 0.5, 1, 0.9, 0.1, 0.2)
freshman_10_decision_dict = make_decision_dict(1, 1, 1, 2, 1, 2, 3, 0.3, 0.1, 0.1, 0.2)

# sophomore 11
# past: CS 1110, CS 2110/CS 2112, CS 2800, CS 3410
# current: CS 3110, CS 4820
# future: CS 4410
freshman_11_choice_dict = make_choice_dict_prob(0.8, 0.2, 0.9, 0.1, 1, 1, 0.5, 0.5, 0.8, 1, 0.1, 0.2)
freshman_11_decision_dict = make_decision_dict(1, 1, 1, 2, 1, 3, 2, 0.3, 0.1, 0.1, 0.2)

# sophomore 12
# past: CS 1110, CS 2110/CS 2112, CS 2800, CS 3110
# current: CS 3410, CS 4820
# future: CS 4410
freshman_12_choice_dict = make_choice_dict_prob(0.8, 0.2, 0.9, 0.1, 1, 1, 0.5, 0.5, 0.8, 1, 0.1, 0.2)
freshman_12_decision_dict = make_decision_dict(1, 1, 1, 1, 2, 3, 2, 0.3, 0.1, 0.1, 0.2)


# make the above data into a dict to choose randomly
student_choice_list = [freshman_1_choice_dict, freshman_2_choice_dict, freshman_3_choice_dict,
                       freshman_4_choice_dict, freshman_5_choice_dict, freshman_6_choice_dict,
                       freshman_7_choice_dict, freshman_8_choice_dict, freshman_9_choice_dict,
                       freshman_10_choice_dict, freshman_11_choice_dict, freshman_12_choice_dict]
student_decision_list = [freshman_1_decision_dict, freshman_2_decision_dict, freshman_3_decision_dict,
                       freshman_4_decision_dict, freshman_5_decision_dict, freshman_6_decision_dict,
                       freshman_7_decision_dict, freshman_8_decision_dict, freshman_9_decision_dict,
                       freshman_10_decision_dict, freshman_11_decision_dict, freshman_12_decision_dict]
graduation_year_list = [2022, 2022, 2022, 2022, 2022, 2021, 2021, 2021, 2021, 2020, 2020, 2020]


random_student_type = random.randrange(0, 12)


def make_n_students(n):
    student_list = []
    for i in range(n):
        random_student_type = random.randrange(0, 12)
        student_class_dict = make_student_class_dict(student_choice_list[random_student_type],
                                                      student_decision_list[random_student_type])
        student_info = make_one_student(student_class_dict, graduation_year_list[random_student_type], i)
        student_list.append(student_info)
    student_json = json.dumps(student_list, sort_keys=True, indent=4, separators=(',', ': '))
    return student_json

student_json_list = make_n_students(num_student)

with open('student_data.json', 'w') as outfile:
    json.dump(student_json_list, outfile)

with open('student_data.json', encoding='utf-8') as data_file:
    student_json_list_read = json.loads(data_file.read())

print(student_json_list_read)







