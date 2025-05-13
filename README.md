### Sprint 5.1 ###

# Project Details
- Name: Brown Bed n' Breakfast
- Github Repo: https://github.com/cs0320-s25/term-project-hang-bahar-daniela-brandon.git
- We built a web application for Brown University students to post and read reviews of their dining halls and residential halls. People with a Brown email can log in and post reviews. Our application is divided into two parts: Dining and Dorms. Under each page, there is a dashboard of the dining halls and dorms. When the user click on each dorm/dining hall, a description of that dorm/dining hall along with all the reviews will show up. Users can search for reviews and find dorms that match their preferences and lifestyles.
- Time: 

# Design Choices
- Front-end:
  - Framework: React TS
  - Authentication: Clerk
- Backend:
  - API Calls: We have Handlers to process API calls from the frontend.
  - Searching Algorithm: allows user to search for dorm reviews based on keyword. We use Levenshtein Distance by Apache Commons Text to account for typos and English Stemmer by Tartarus Snowball to account for semantically related words (such as noise and noisy) in the search query. We assign a score according to the matching field. For example, a dorm name matching the search query will receive a higher score (because the user knows what dorm they are looking for) whereas a room type matching the search query will receive a lower score because many dorms have the same room type.
  - Matching Algorithm: we process dorm matching based on entry from the user. 
# Errors/Bugs
- N/A

# Tests
- We test for:
 
# How to
- Running the Program:
    - Run the program locally using a Terminal by entering the 'client' package and running "npm run dev"
    - Next, navigate to your browser and 'http://localhost:8000/' in order to view the program
- Navigating the Program:
    - Click 'sign in' in order to sign into the application via your email
    - Click Dining Halls to see dining halls and Dorms to see dorms
- Testing:
    - To run tests, navigate to client/mock, "npm start" the program in one terminal, and "npx playwright test" in a second terminal

# Collaboration
- eygeer (Eliot Geer)
- jloncke (John Loncke)
- Emmett Young

# Resources Used:
- 








### Sprint 5.2 ###

# Project Details
- Github Repo: https://github.com/cs0320-s25/maps-hang-eliot.git
- We sought to satisfy User Stories 3-5 by adapting our program from Sprint 5.1 to accomodate for Firebase-based storage on pins, the restriction of redlining data via a Bounding Box, and the implementation of keyword-based search functionality.
- Time: 8 Hours

# Design Choices
- Code Basis:
    - We utilized the 'maps-25-gearup' code as a basis for program's mapbox features, utilizing Clerk, React, Firebase, and Mapbox-gl imports, and built off of our existing code from Sprint 5.1
- Pins Storage:
    - We substituted our localStorage method of pin storage with Firebase-based storage in order to allow for cloud-based storage unrestricted to local API calls
    - In order to implement this, we created several Java backend handler classes which react to frontend calls and communicate information regarding pin storage back to the frontend
    - Additionally, utilizing Firestore dependent upon userID as data, we modified our 'Clear Pins' functionality in order to only clear the pins of the user clicking 'Clear Pins' instead of all pins across the map
- Bounding Box/Keyword Search:
    - We implemented a back-end JSON parser which is able to parse through GeoJSON data and exclude data which falls outside of our searched for parameters
    - Utilizing this functionality, we implemented both methods of restricting redlining data overlay, as well as keyword search in order to narrow down the redlining info to be displayed via our back-end, and displaying those changes on our front-end

# Errors/Bugs
- N/A

# Tests
- We test for:
    - "I can log in, add pins, log out, then log in again, and still see pins"
        - Tests for pins persisting in Firestore between logins
    - "I can log in, add pins, log out, log into another account, and still see pins"
        - Tests for pins persisting in Firestore across multiple user accounts
    - "I can log in, add pins, clear them, and they will stay cleared on other logins"
        - Tests for clearing functionality in Firestore across multiple user accounts
    - "Pins persist on page reloads"
        - Tests for pins persisting in Firestore between page reloads
    - "Test for Bounding Box Area Changes"
        - Tests for bounding box changes to restrict overlayed redlining data
    - "Test for Bounding Box Size 0"
        - Tests for bounding box changes to restric all overlayed redlining data given bounding box of size 0
    - "Test for Keyword Search 'Boston'"
        - Tests for keyword search highlight overlay
    - "Test for Improper Keyword Search"
        - Tests for keyword search highlight overlay to be empty given improper keyword

# How to
- Running the Program:
    - Run the program locally using a Terminal by entering the 'client' package and running "npm start"
    - Next, navigate to your browser and 'http://localhost:8000/' in order to view the program
- Navigating the Program:
    - Click 'sign in' in order to sign into the program via your email, and click 'Section 2: Mapbox Demo' in order to view the map
    - Click and drag the map to move the view window
    - Scroll up and down (or use two fingers on trackpad) to zoom the view window in or out
    - Click and release the map at any point to add a pin
    - Scroll to the bottom of the page and click 'Clear Pins' in order to clear the map of all of your placed pins
    - Click 'Sign Out' at the top of the page in order to exit or switch users
- Additional 5.2 Functionality:
    - Input 'Min Latitude', 'Max Latitude', 'Min Longitude', and 'Max Longitude' values into the text boxes at the bottom of the page and click 'Compute' in order to place a Bounding Box on the redlining data being viewed
    - Input keyword values into the text box and click 'Compute' in order to highlight keyword-described areas on the map
- Testing:
    - To run tests, navigate to client/mock, "npm start" the program in one terminal, and "npx playwright test" in a second terminal

# Collaboration
- anshield
- yli795
- domondi1

# Resources Used:
- Chat GPT: OpenAI. (2023). ChatGPT (Mar 14 version) [Large language model]. https://chat.openai.com/chat (Utilized for debugging and syntax)