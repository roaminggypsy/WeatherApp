# Weather-Search-Android-Application
This Android App was made as part of course CSCI 571 Web technologies at USC

I have develeped this Android App which allows users to search for weather information of any city around the world, save cities to favorites and tweet about a city's current weather. The backend service is written in Node.js and is hosted on Amazon Web Services.

The home screen of the app is where the user can view the weather conditions of the current location along with dot indicators which provide a sliding view to any favorite cities added by the user.

Also, a search view is provided at the top where the user can search for the weather conditions of any city around the world using google "Autocomplete" feature. After choosing a city from the google autocomplete options the weather details of that city are displayed.

<img src=Screen%20Shot%202019-12-20%20at%204.34.53%20PM.png width=260 hspace=20><img src=Screen%20Shot%202019-12-20%20at%204.41.25%20PM.png width=260 hspace=20><img src=detailedWeather.png width=260>

On clicking the current weather card with the info icon the detailed weather info of that city is displayed. The darkSky API is queried with the latitude & longitude values of the city and the returned JSON is displayed in the Today and Weekly tabs. Google custom Search engine API is used for displaying the pictures of the city in photos tab. MPAndroidChart third party library is used for displaying the min-max temperature charts in the weekly tab.

<img src=current.png width=260 hspace=20><img src=weekly.png width=260 hspace=20><img src=photosTab.png width=255>

User can add a city to favorites by clicking on the floating action button at the bottom, this adds the city to the home screen with a dot indicator. The floating button acts as a switch to add and remove a city from favorites. Additionaly Webview is used to to allow the user to tweet about the weather in a city by clicking on the twitter icon in the detailed weather view menu.

<img src=favorite.png width=260 hspace=20><img src=twitter.png width=260 hspace=20>
