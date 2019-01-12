Popular Movies
==============

API Key
-------
The application depends on **The Movie DB** to retrieve the movies. More information about the API can be found in their documentation [here](https://www.themoviedb.org/documentation/api).

The API Key should be placed at **app\src\main\res\values\configuration.xml** in the `movie_db_api_key` property.

Stage 1
-------
Your app will:
+ Present the user with a grid arrangement of movie posters upon launch.
+ Allow your user to change sort order via a setting:
	- The sort order can be by most popular or by highest-rated.
+ Allow the user to tap on a movie poster and transition to a details screen with additional information such as:
	- Original title
	- Movie poster image thumbnail
	- A plot synopsis
	- User rating
	- Release date


Stage 2
-------
Your app will:
+ View and play trailers (either in the YouTube app or a web browser).
+ To read reviews of a selected movie.
+ Allow users to mark a movie as a favorite in the details view by tapping a button (star).
+ Make use of Android Architecture Components (Room, LiveData, ViewModel and LifeCycle) to create a robust and efficient application.
+ Create a database using Room to store the names and ids of the user's favorite movies (and optionally, the rest of the information needed to display their favorites collection while offline).
+ Modify the existing sorting criteria for the main view to include an additional pivot to show their favorites collection.