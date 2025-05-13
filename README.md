### Term Project ###

# Project Details
- Name: Brown Bed n' Breakfast
- Github Repo: https://github.com/cs0320-s25/term-project-hang-bahar-daniela-brandon.git
- We built a web application for Brown University students to post and read reviews of their dining halls and residential halls. People with a Brown email can log in and post reviews. Our application is divided into two parts: Dining and Dorms. Under each page, there is a dashboard of the dining halls and dorms. When the user clicks on each dorm/dining hall, a description of that dorm/dining hall along with all the reviews will show up. Users can search for reviews and find dorms that match their preferences and lifestyles, like proximity to a certain place on campus or program housing. 
- Time: 
- Our team had a clear project plan, distributed tasks early, and set expectations based on the project timeline.
    *Week 1: Focused on planning and designing the algorithmic structure of the project.
    *Week 2: Brandon worked on the front-end framework. Daniela developed the backend for posting reviews and handled testing. Hang and Bahar focused on implementing the matching, searching, and information retrieval algorithms.
    *Week 3: All team members collaborated to replace mock data with real-time data from the Firestore database, integrating backend and frontend components, more testing.
    *Week 4: Dedicated to documentation and demo preparation, recognizing that clearly communicating the idea is as important as implementing it.

# Design Choices
- Front-end:
  - Framework: React TS
  - Authentication: Clerk
  - Backend: Java
  - Database: Firestore and Google Drive. All the data used in the web app, including posts, is stored in firebase and retrieved through there. The images used in the webapp are stored and retrieved through Google Drive.  
  - API Calls: We have Handlers to process API calls from the frontend. For examople, adding dorms and posts to firestore, retrieving dorms and posts, deleting posts, and calculating average rating for dorms.
  - Searching Algorithm: allows user to search for dorm reviews based on keyword. We use Levenshtein Distance by Apache Commons Text to account for typos and English Stemmer by Tartarus Snowball to account for semantically related words (such as noise and noisy) in the search query. We assign a score according to the matching field. For example, a dorm name matching the search query will receive a higher score (because the user knows what dorm they are looking for) whereas a room type matching the search query will receive a lower score because many dorms have the same room type.
  - Matching algorithm: We match users to dorms based on their preferences. Users are asked to rank their expectations for various lifestyle factors, such as proximity, accessibility, and other criteria. Each criterion is assigned a score based on the user's input. The algorithm calculates a total score for each dorm and returns the top 3 matches, along with detailed information about each dorm's features and how they align with the user's priorities. If a user leaves a question blank, a default score of 0 is assigned to that criterion.
  - Accessibility was a core focus in the design of the interface. Features include: High-contrast, colorblind-friendly color schemes. ARIA labels for screen reader support. Full keyboard and button navigation considerations.


# Errors/Bugs
- N/A

# Tests
- We test for:
- JUnit test for testing the helper methods in the backend
- Postman, and local browser fetch, tests to ensure the expected behavior of GET and POST endpoints
- Playwright, and manual browser tests, to ensure the expected front-end behavior
 
# How to
- Running the Program:
    - Run the program locally using a Terminal by entering the 'client' package and running "npm run dev"
    - Next, navigate to your browser and 'http://localhost:8000/' in order to view the program
- Navigating the Program:
    - Click 'sign in' in order to sign into the application via your email
    - Click Dining Halls to see dining halls and Dorms to see dorms
- Testing:
    - To run the tests, navigate to client/mock, "npm start" the program in one terminal, and "npx playwright test" in a second terminal
    - To run the backend tests, navigate to server/tests, and click run button in IntelliJ or navigate to postman and click send request

# Collaboration
- eygeer (Eliot Geer)
- jloncke (John Loncke)
- Emmett Young
- Chat GPT: OpenAI. (2023). ChatGPT (Mar 14 version) [Large language model]. https://chat.openai.com/chat (Utilized for conceptual understanding of sql vs non-sql based firestore, finding recommendations for stemming words and detecting typos, generating boilerplate code, debugging and some helper functions)
- Antrophic.(2025).Claude(January 2025 version)[Large Language Model]. https://claude.ai/new.


# Resources Used:
- Lecture live repo, gear-up and sprint documentation and codebase
- https://www.youtube.com/watch?v=rANfiSmyMTQ
