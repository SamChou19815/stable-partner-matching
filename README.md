# BigRedMatch

## Inspiration

This semester, many CS courses introduced the randomized partner matching system. It introduces
quite a bit of chaos and results in many very unsatisfactory partnership experience.

We believe randomization does not solve the problem. There should be a much better system that just
blames unluckness during randomization. We built such a system to demonstrate that there exists a
better alternative and we welcome cooperations.

## What it Does

The user registers, and tells us some of his/her information:

- Basic Profile Information
- Skills, introduction, experience
- Free Times
- Past, current, and future courses. The user can also tell us their grade and whether they become
  TA for those courses.

Then the user can select a course, find a list of all potential partners sorted in decreasing
quality. The user can send invite and form partnership on our platform.

## How We Built It

- Cornell Course API - Get Cornell CS course information
- Google NLP API - Extract keywords from course descriptions
- Kotlin / Google Cloud Platform - Backend (server application and data storage)
- Firebase Authentication - User identification
- Angular - Front-end User Interface

## Challenges we ran into

## Accomplishments that we're proud of

We now have a working system that can give us very good ranking of student.

We tested the system on ourselves and it gives us results that make sense.

## What We Learned

Our ranking algorithm is very complex and we need to constantly changing some parameters to test
its performance. Thus, we must have automated tools that do the repeated job for us.

We learned how to build automated tools to pipe together different operations and eventually import
the generated weights and class data into the database.

Out ranking system is also running a little slow. We learned in a hard way that database latency
is a real problem. :(

## What's Next for BigRedMatch

We want to work with the adminstration to improve the product. For example, we can run the
stable roommate matching algorithm (since we can algorithmically generate partner preference) for
left-over students, and make everyone happy with their partners!

Also, if the adminstration can give our some API support, then we don't need to store students'
grades so there are better privacy control.

## Build and Deploy the Project

Build and deployed by `build.sh`.
