### `README.md`

This project is a static web application designed to provide a dynamic and personalized view of celestial and temporal information. It leverages browser features and a lightweight web server to display the current date, time, lunar cycle, season, and day/night hours based on the user's location.

-----

### Features

  * **Date & Time Display**: The application shows the current day of the week in Hebrew. It also includes a visual representation of the week's progress in a circular segmented chart.
  * **Lunar Day**: A visual depiction of the current moon phase is rendered, along with the lunar day and phase name. The calculation is based on a reference new moon date.
  * **Seasonal Information**: The project identifies the current season and displays a progress bar that shows the number of days passed and remaining within that season.
  * **Dynamic Day/Night Cycle**: Using geolocation, the application calculates and displays the specific sunrise and sunset times for the user's location, along with the total duration of daylight and nighttime.
  * **Dynamic Theming**: A button allows the user to toggle between a light mode and a dark mode interface.
  * **Geolocation**: A location button uses the browser's Geolocation API to get the user's current coordinates. If a custom location is provided via URL parameters (`?lat=XX.XXXX&lon=YY.YYYY`), it will use those instead.

-----

### Technologies Used

  * **HTML**: Provides the core structure of the web page.
  * **CSS**: Styles the application, including a responsive design and the custom variables for light and dark modes.
  * **JavaScript**: Handles all dynamic functionality, including date and time calculations, lunar phase and seasonal logic, theme toggling, and geolocation. It also uses the D3.js library to create the week visualization.
  * **D3.js**: A JavaScript library used for manipulating documents based on data, specifically for generating the segmented week circle visualization.
  * **Docker**: Provides a containerized environment for easy deployment of the static web application using a lightweight Nginx image.

-----

### Project Files

  * `index.html`: The main HTML file that defines the page structure and links to the CSS and JavaScript files. It includes the containers for all the dynamic information.
  * `sky.css`: The CSS file that contains all the styling for the application, including the responsive design and the color variables for the light and dark themes.
  * `sky.js`: The JavaScript file that contains all the logic for calculating and displaying the lunar day, seasons, sun times, and handling user interactions like the theme toggle.
  * `Dockerfile`: A Dockerfile that uses an Nginx image to serve the static files of the application.
  * `docker-compose.yml`: A Docker Compose file that orchestrates the building of the Docker image and runs the container, publishing the application on port `8300`.

-----

### How to Run

To run this project locally using Docker, follow these steps:

1.  Make sure you have **Docker** and **Docker Compose** installed on your machine.

2.  Place all the project files (`index.html`, `sky.css`, `sky.js`, `Dockerfile`, `docker-compose.yml`) in the same directory.

3.  Open a terminal and navigate to that directory.

4.  Run the following command to build the Docker image and start the container:

    ```bash
    docker-compose up -d
    ```

You can then access the application in your web browser by navigating to `http://localhost:8300`.