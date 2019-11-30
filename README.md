# AnalyticsForSpotify
Analytics about a user's Spotify listening habits over time

## Tools
* Android Studio
* [Spotify API](https://developer.spotify.com/documentation/web-api/reference-beta/)
* [Android Volley](https://developer.android.com/training/volley)
* [Gson](https://github.com/google/gson/blob/master/UserGuide.md#TOC-Serializing-and-Deserializing-Collection-with-Objects-of-Arbitrary-Types)
* [Android Picasso](https://square.github.io/picasso/)
  * [Picasso Transformations Library](https://github.com/wasabeef/picasso-transformations)
* [Tutorial used to add Spotify Authentication](https://towardsdatascience.com/using-the-spotify-api-with-your-android-application-the-essentials-1a3c1bc36b9e)
* [Json Formatter](https://jsonformatter.org/json-pretty-print)

## Functionalities
* Users can log into their spotify account(Premium not required)
* A user's top songs of All Time, the past Six Months, and the past One Month are displayed (similar to sports standing)
* A songs placement in the Six Month/One Month catageroy is shown compared to its placement in the All Time catagory
  * A plus symbol is used to show that a song does not appear on the All Time list
  * An up arrow indicates that the user is more frequently listening to a song
  * A down arrow indicates that the user is less frequently listening to a song
  * A dash indicates that the song has maintained its position from the All Time list
* A summary page is added to display the average song lenght for the three lists
  * It also shows the song that had the largest rise from All time to the One Month list
  * And the song that had the largest drop
* A user can then choose to create any of the three lists into a playlist on their spotify account

### Future Functionalites
* Long press a song to redirect a user to its spotify page (website or Spotify app)
* Implement multithreading to reduce frame lag
