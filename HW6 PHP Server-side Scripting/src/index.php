<?php

ini_set("allow_url_fopen", 1);

$ipAPI = "http://ip-api.com/json";

$googleAPIkey = ;
$googlePlacesAPIkey = ;

$searchForm = array(
    "keyword" => "",
    "category" => "",
    "distance" => "10",
    "dist_from" => "here", 
	"input_loc" => "",
	"cur_loc" => "",
	"lat" => "0", 
	"lon" => "0"
);

$errorMessage = "";

$categoryItems = array ("cafe", "bakery", "restaurant", "beauty salon",
"casino", "movie theater", "lodging", "airport", 
"train station", "subway station", "bus station");

function getPostList(){
	global  $searchForm;	
	if ($_SERVER['REQUEST_METHOD'] == "POST") {

	$searchForm = array(
		"keyword"   => $_POST["keyword"],
		"category"  => $_POST["category"],
		"dist_from" => $_POST["dist_from"], 
		"cur_loc" => $_POST["cur_loc"]
		);	
	
	if (!empty ($_POST["input_loc"]))
		$searchForm["input_loc"] = $_POST["input_loc"]; 
	
	//TODO CHECK THIS VALUE TO BE INTEGER LESS THAN 50000
	if (!empty ($_POST["distance"]))
		$searchForm["distance"] = $_POST["distance"];
	else $searchForm["distance"] = "10";
	//Converting distance to meters from miles
	$searchForm["distance"] = intval ($searchForm["distance"] * 1609.34);
		
	}
//Uncomment to see variables
//var_dump($searchForm);
}

function getGeoLocation(){
	global  $searchForm;
	global  $googleAPIkey;
	global  $errorMessage;

	$geoLocURL = "https://maps.googleapis.com/maps/api/geocode/json?address=".urlencode(trim($searchForm["input_loc"])) . ",";	
    $geoLocURL .= "&key=" . $googleAPIkey;
    $geoLoc_json = file_get_contents($geoLocURL) or die("Error: Cannot get Geo Loc address from Google");
	$geoLoc_doc = json_decode($geoLoc_json, true);
	if ($geoLoc_doc['status'] != "OK"){
		//die ("Error, Geo loc is invalid");
		$errorMessage = "Error, Geo loc:". $searchForm["input_loc"] ." is invalid";
		return;
	}
	$searchForm['lat'] = $geoLoc_doc['results'][0]['geometry']['location']['lat'];
	$searchForm['lon'] = $geoLoc_doc['results'][0]['geometry']['location']['lng'];
	return;
}

function handleInputs(){
	getPostList();
	global $searchForm;
	if ($searchForm['dist_from'] == 'here'){
		list ($latitude, $longitude) = explode ( ",", $searchForm['cur_loc']);
		$searchForm['lat'] = $latitude;
		$searchForm['lon'] = $longitude;		
		//echo "\n", $searchForm['lat'], "\t" , $searchForm['lon'] ;     	
	}
	else if ($searchForm['dist_from'] == 'loc' && 
			$searchForm['input_loc'] != '') 
	{
		getGeoLocation();
		//echo "\n\n input loc lat: ", $searchForm['lat'],"\n\n";
		//echo "\n\n input loc lon: ", $searchForm['lon'],"\n\n";		
	}	
}

class Place  {
	public $icon;
	public $name;
	public $address;
	public $id;	
	public $lat;
	public $lon;
}

class placeDetails {
	public $id;
	public $photos;
	public $reviews;
}


function processPlacesDoc($places){
	$placesArray = array();
	$allPlaces = $places['results'];
	foreach ($allPlaces as $i){
		$cPlace = new Place;
		$cPlace->icon = "";
		$cPlace->name	= "";	
		$cPlace->id     = ""; 		
		$cPlace->address= "";
		$cPlace->lat = "";
		$cPlace->lon = "";
		
		if ($i['icon']) $cPlace->icon = $i['icon'];
		if ($i['name']) $cPlace->name = $i['name'];
		if ($i['place_id'])  $cPlace->id = $i['place_id'];
		if ($i['vicinity']) $cPlace->address = $i['vicinity'];
		if ($i['geometry']['location']) {
			$cPlace->lat = $i['geometry']['location']['lat'];
			$cPlace->lon = $i['geometry']['location']['lng'];
		}
		array_push($placesArray, $cPlace);
	}
	
	//echo "length of placesArray: ", count($placesArray);
	//var_dump($placesArray);
	
	return $placesArray;
}


function getPlaceResults(){
	global $googlePlacesAPIkey;
	global $searchForm;
	$placeURL ="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
	$placeURL .= urlencode(trim($searchForm['lat'])) . ",";
	$placeURL .= urlencode(trim($searchForm['lon']));
	$placeURL .= "&radius=". urlencode(trim($searchForm['distance']));
	$placeURL .= "&type=". urlencode(trim($searchForm['category']));
	$placeURL .= "&keyword=". urlencode(trim($searchForm['keyword']));
    $placeURL .= "&key=" . $googlePlacesAPIkey;
    $places_json = file_get_contents($placeURL) or die("Error: Cannot get Geo Loc address from Google");
	$places_doc = json_decode($places_json, true);
	if ($places_doc['status'] != "OK"){
		//die ("Error, Geo loc is invalid");
		$GLOBAL['errorMessage'] = "Error, Places is invalid";
		//echo $placeURL;
		return;
	}
	//echo "Places are retrived successfully\n";
	//var_dump($places_doc);
	
	$places_array = processPlacesDoc($places_doc);
	
	$placesJSON = json_encode($places_array, JSON_PRETTY_PRINT);
	$fileName= "./places.json";
	file_put_contents ($fileName, $placesJSON);
	
	return $places_array ;
}

function processPlaceDetails($place) {
	$details = new placeDetails;
	
	$details->id = $place['result']['place_id'];
	if ( isset ( $place['result']['photos'] ) )
		$details->photos = $place['result']['photos'];
	else $details->photos = array();
	
	if ( isset ( $place['result']['reviews'] ) )
		$details->reviews = $place['result']['reviews'];
	else
		$details->reviews = array();
	return 	$details;
}


function getPlaceDetails(Place $p){
	global $googlePlacesAPIkey;
	$placeURL ="https://maps.googleapis.com/maps/api/place/details/json?placeid=";
	$placeURL .= urlencode(trim($p->id));
	$placeURL .= "&key=" . $googlePlacesAPIkey;
	$place_json = file_get_contents($placeURL) or die("Error: Cannot get Single Place data from Google");
	$place_doc = json_decode($place_json, true);
	if ($place_doc['status'] != "OK"){
		//die ("Error, Geo loc is invalid");
		$GLOBAL['errorMessage'] = "Error, Place ". $p->id ." is invalid";
		//echo $placeURL;
		return;
	}
	
	$details = processPlaceDetails($place_doc);
	
	return $details;
}


function getPhotos($p){
	global $googlePlacesAPIkey;
	$photoURL ="https://maps.googleapis.com/maps/api/place/photo?";
	$photoURL .= "maxwidth=1000&photoreference=";
	$photos = $p->photos;
	$photoPaths = array();
	
	$n = count ( $p->photos );
	$maxSize = ($n > 5) ? 5 : $n;
	//echo "Total Number of Pics: ", $n ;
	for ($i = 0; $i < $maxSize; $i++){
		$curPhoto = $photos[$i]['photo_reference'];
		$curURL = $photoURL;
		$curURL .= urlencode(trim($curPhoto));
		$curURL .= "&key=" . $googlePlacesAPIkey;
		//
		$downPhoto = file_get_contents($curURL) or die("Error: Cannot get Single Photo from Google");
		//$photo_doc = json_decode($photo_json, true);
		if (!empty ( $downPhoto['error_message'] )){
			//die ("Error, Geo loc is invalid");
			$GLOBAL['errorMessage'] = "Error, Photo ". $curPhoto ." is invalid";
			//echo $curURL;
			return;
		}
		//$filePath = "./images/". $p->id . "_" . $i . ".jpg";
		//$filePath = "/home/scf-34/nazarsha/public_html/yelpHW6/images/". $p->id . "_" . $i . ".jpg";
		$filePath = "/home/scf-34/nazarsha/public_html/yelpHW6/images/image_" . $i . ".jpg";
		file_put_contents ($filePath, $downPhoto);
		chmod($filePath, 0664);
		
		array_push( $photoPaths , $filePath);
	}
	
	return $photoPaths;
}

class review {
	public $authorName;
	public $text;
	public $profilePic;
}

function getReviews($p){
	$reviews = array ();
	$n = count ( $p->reviews );
	//echo "Total Number of reviews: ", $n ;
	$maxSize = ($n > 5) ? 5 : $n;
	for ($i=0; $i < $maxSize; $i++){
		$r = new review;
		$r->authorName = "";
		$r->text = "";
		$r->profilePic ="";
		
		if ( isset ( $p->reviews[$i]['author_name'] ) )
			$r->authorName = $p->reviews[$i]['author_name'] ; 
		
		if ( isset ( $p->reviews[$i]['text'] ) ) 
			$r->text = $p->reviews[$i]['text'];
		
		if ( isset ( $p->reviews[$i]['profile_photo_url'] ))
			$r->profilePic = $p->reviews[$i]['profile_photo_url'] ;
		
		array_push($reviews, $r);
	}
	
	return $reviews;
}


function getPhotosAndReviews(Place $p) {
	$detail = getPlaceDetails($p);
	$photoPaths = getPhotos($detail);
	$reviews = getReviews($detail);
	
	return array($photoPaths, $reviews);
}


class photoAndRev {
	public $photos;
	public $reviews;
}

$show_results = false;
if (! empty($_POST['keyword']) ) {
	global $places;
	global $errorMessage;
	$show_results = true;
	
	handleInputs();	

	if ($errorMessage != "") {
		//echo "Error: ", $errorMessage ;
		$places = array();	
	} else {
		$places = getPlaceResults();
		if ($errorMessage != ""){
			//echo "Error: ", $errorMessage ; 
			$places = array();
		}
		
	}
	
	

	
	//$detail = getPlaceDetails($places[0]);
	//$photoPaths = getPhotos($detail);
	//$reviews = getReviews($detail);
	
}

if (isset ($_REQUEST['q']) ) {
	$q = $_REQUEST['q'];
	
	$placesJson = file_get_contents("./places.json");
	$allPlaces = json_decode($placesJson, true);
	$cur = $allPlaces[$q];
	$p = new Place;
	$p->icon = $cur["icon"];
	$p->name = $cur["name"];
	$p->address = $cur["address"];
	$p->id = $cur["id"];
	$p->lat = $cur["lat"];
	$p->long = $cur["lon"];
	
	list($finalPhotos, $finalReviews) = getPhotosAndReviews($p);
	
	$finalPhotosAndReviews = new photoAndRev;
	$finalPhotosAndReviews->photos = $finalPhotos;
	$finalPhotosAndReviews->reviews = $finalReviews;
	
	echo json_encode($finalPhotosAndReviews, JSON_PRETTY_PRINT); 
	
	
	exit;
} 

	
	
/* $ip_api_json = file_get_contents($ipAPI);
if ($ip_api_json == false){
	echo " <script type='text/javascript'> alert('Unable to get location from IP-API') </script> ";
}	
$ip_json_doc = json_decode($ip_api_json, true);
*/

?>



<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8"/>
    <title>Travel and Entertainment</title>
    <!--<link rel="stylesheet" type="text/css" href="main.css"/> 
    <script type="text/javascript" src="script.js"></script> -->

<script async defer src="https://maps.googleapis.com/maps/api/js?key="> 
</script>
	
<script type="text/javascript">

downArrow = "http://cs-server.usc.edu:45678/hw/hw6/images/arrow_down.png";
upArrow = "http://cs-server.usc.edu:45678/hw/hw6/images/arrow_up.png";

photoShowText = "click to show photos";
photoHideText = "click to hide photos";

reviewShowText = "click to show reviews";
reviewHideText = "click to hide reviews";

googleMapAPIKey = "AIzaSyDu4GtAGPVsgENUO_-PbrtFUGF694-rFZ8";

PHOTOWIDTH = '800';

function enableLocation(){
	if (document.getElementById("input_loc").disabled ){
		document.getElementById("input_loc").disabled = false;
		document.getElementById("input_loc").required = true;
	} 
			
}

function disableLocation(){
	document.getElementById("input_loc").disabled = true;	
}


function clearForm(){
	
	document.getElementById("keyword").value = "";
	document.getElementById("distance").value = "";
	document.getElementById("input_loc").value = "";
	document.getElementById("input_loc").disabled = true;
	document.getElementById("dist_from_here").checked = true;
	document.getElementById("category").value = "default";
	
	//document.getElementById("result_table").innerHTML = "";
	removeElements("result-box")   ;
	removeElements("final_result") ;
	removeElements("final_photos") ;
	removeElements("final_reviews");
	
	//clear query results
}




function submitForm(formName){
	
/* 	var error = false;
	if (document.getElementById("keyword").value = ""){
		alert ("Please enter the value for keyword");
		error = true;
	}
	
	if (document.getElementById("distance").value = ""){
		alert ("Please enter the value for keyword");
		error = true;
	}
	
	var old_keyword   = document.getElementById('keyword').value;
	var old_category  = document.getElementById('category').value;
	var old_distance  = document.getElementById('distance').value;
	var old_dist_from;
	if (document.getElementById('dist_from_here').checked){
		old_dist_from = document.getElementById('dist_from_here').value;
	} else {
		old_dist_from = document.getElementById('dist_from_loc').value;
	}
	
	var old_cur_loc   = document.getElementById('cur_loc').value;
	var old_input_loc = document.getElementById('input_loc').value;
	
	
 */	
	
								 
 
	document.getElementById(formName).submit(); 

	
/* 	document.getElementById('keyword').value = "<?php echo isset($searchForm['keyword']) ? $searchForm['keyword'] : '' ?>";
	
 	document.getElementById('category').value = <?php echo $searchForm['category']; ?> ; 
 	document.getElementById('distance').value = <?php echo $searchForm['distance']; ?> ;


<?php echo isset($_POST['input_loc']) ? $_POST['input_loc'] : '' ?>
  */
}

function loadJSON (url) {
	if (window.XMLHttpRequest) {
		xmlhttp=new XMLHttpRequest();
	}else {
		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	try {
		xmlhttp.open("GET",url,true); //open, send, responseText are
		xmlhttp.send(); //properties of XMLHTTPRequest
		if (xmlhttp.statusText != "OK"){
			return {stat: 'JSON file does not exist.'}
		}
		return { stat:'Success', json:xmlhttp.responseText };
	}
	catch (e) {
		return {stat: 'JSON file does not exist.'}
	}
} 		

function restorePreviousInputs(){

	var str;
	str = "<?php 
	if (isset ($_POST['keyword'])) {
		echo $_POST['keyword'];
	} else {
		echo "";
	}
	?>" ;
	document.getElementById('keyword').value = str.trim();

	str = "<?php 
	if (isset ($_POST['category'])) {
		echo $_POST['category'];
	} else {
		echo "";
	}
	?>" ;
	str = str.trim();
	if (str == ""){
	document.getElementById('category').value = "default";		
	} else {
	document.getElementById('category').value = str.trim();		
	}
	
	str = "<?php 
	if (isset ($_POST['distance'])) {
		echo $_POST['distance'];
	} else {
		echo "";
	}
	?>" ;
	document.getElementById('distance').value = str.trim();

	str = "<?php 
	if (isset ($_POST['dist_from'])) {
		echo $_POST['dist_from'];
	} else {
		echo "";
	}
	?>" ;
	str = str.trim();
	if (str == "here" || str == ""){
		document.getElementById('dist_from_here').checked = true;	
	} else {
		str = "<?php 
		if (isset ($_POST['input_loc'])) {
			echo $_POST['input_loc'];
		} else {
			echo "";
		}
		?>" ;
		document.getElementById('dist_from_loc').checked = true;
		document.getElementById('input_loc').disabled = false;
		document.getElementById('input_loc').value = str.trim();
		
	}

	
	
}
	

//TODO remove comments to enable query
function getCurLocation(){
	var ipAPI_url = "http://ip-api.com/json";
	var docStatus = loadJSON(ipAPI_url);
	if (docStatus.stat == 'Success') {
		var ipAPI_jsonDoc = docStatus.json;
		try {
			var ipApiJSON = JSON.parse(ipAPI_jsonDoc);					
			console.log('Loaded JSON successfully');
		} catch (e) {
			lat_here = 34.0266; 
			lon_here = -118.2831;	
		}	
		lat_here = ipApiJSON.lat;
		lon_here = ipApiJSON.lon;

	} else {
		//alert ("Could not get the ipAPI json file.");
		lat_here = 34.0266; 
		lon_here = -118.2831;	
	}
	
 
/* 	lat_here = 34.0266; 
	lon_here = -118.2831;	
 */	
 
	document.getElementById("search-btn").disabled = false;	
	document.getElementById("cur_loc").value = lat_here + "," + lon_here;
	
	
	restorePreviousInputs();
	
	return;
}


function convertToImageSimple(el){
	return "<td style='text-align:center'>\n<img src='" + el + "'/></td>";
}

function convertToImageWandH(el, w, h){
	return "<td style='text-align:center'>\n" + "<a href='"+ el + "' target='_blank'>" + 
	"<img src='" + el + "' style='width:"+ w +"px; max-height:" + h 
	+"px;'/></a></td>";
	
}


function convertToImage(el, w){
	return "<td style='text-align:center'>\n" +
	"<img src='" + el + "' style='width:"+ w +"px'/></a></td>";
}

function convertToImageWithText(el, w, txt){
	return "<td style='text-align:center'>\n<img src='" + el + "' style='width:"+ w +"px'/>"+ txt +"</td>";
}


function convertToImageLinkWithID (id, el, link){
	return "<td style='text-align:center' id='"+ id +"'>\n<img src='" + el + "' onclick=" + link +" ></img></td>";	
}

function convertToImageLinkWithClass (className, el, link){
	return "<td style='text-align:center' class='"+ className +"'>\n<img src='" + el + "' onclick=" + link +" ></img></td>";	
}


function convertToTextWithFunc(el, row, funcName, className){
	if (el == null) return '<td></td>';
	return '<td class="'+ className +'"onclick='+ funcName + '('+ row+')>'
	+ "<p class='cursorStyle'> "+ el+'</p></td>';			
}

function convertToTextWithFuncAndID(el, row, funcName, id, lat, lon, className) {
	if (el == null) return '<td></td>';
	var outStr = "<td>";
	outStr += '<p class=" cursorStyle '+ className +'"id="'+ id+'_'+row+'"onclick=';
	outStr += funcName + '('+ row + ',' + lat + ',' + lon +')>' +el + '</p>';
	outStr += '<div class="mapStyle" id="map_'+ row + '" ></div></td>';			
	
	return outStr;
}

function convertToTextSimpleWithID(textID, el){
	if (el == null) return '<td></td>';
	return "<td id='" + textID + "'>"+ el +'</td>';			
}

function convertToTextSimpleWithClass(className, el){
	if (el == null) return '<td></td>';
	return "<td class='" + className + "'>"+ el +'</td>';			
}

/*
      function CenterControl(controlDiv, map) {

        // Set CSS for the control border.
        var controlUI = document.createElement('div');
        controlUI.style.backgroundColor = '#fff';
        controlUI.style.border = '2px solid #fff';
        controlUI.style.borderRadius = '3px';
        controlUI.style.boxShadow = '0 2px 6px rgba(0,0,0,.3)';
        controlUI.style.cursor = 'pointer';
        controlUI.style.marginBottom = '22px';
        controlUI.style.textAlign = 'center';
        controlUI.title = 'Click to recenter the map';
        controlDiv.appendChild(controlUI);

        // Set CSS for the control interior.
        var controlText = document.createElement('div');
        controlText.style.color = 'rgb(25,25,25)';
        controlText.style.fontFamily = 'Roboto,Arial,sans-serif';
        controlText.style.fontSize = '16px';
        controlText.style.lineHeight = '38px';
        controlText.style.paddingLeft = '5px';
        controlText.style.paddingRight = '5px';
        controlText.innerHTML = 'Center Map';
        controlUI.appendChild(controlText);

        // Setup the click event listeners: simply set the map to Chicago.
        controlUI.addEventListener('click', function() {
          map.setCenter(chicago);
        });

      }

      function initMap() {
        map = new google.maps.Map(document.getElementById('map'), {
          zoom: 12,
          center: chicago
        });

        // Create the DIV to hold the control and call the CenterControl()
        // constructor passing in this DIV.
        var centerControlDiv = document.createElement('div');
        var centerControl = new CenterControl(centerControlDiv, map);

        centerControlDiv.index = 1;
        map.controls[google.maps.ControlPosition.TOP_CENTER].push(centerControlDiv);
      }

*/
function addMapButton (parentDiv, map, method, marker, dir) {
	var controlUI = document.createElement('div');
	controlUI.className  += 'mapButton';
	
	if (method == "Bike" || method == "Drive"){
		controlUI.style.position = 'relative';	
		if (method == "Bike")
			controlUI.style.top = '-25px';	
		else 
			controlUI.style.top = '-50px';				
	}
	
	parentDiv.appendChild(controlUI);
	var controlText = document.createElement('div');
	controlText.className  += 'mapButtonText';
	controlText.innerHTML = method + " there";
	controlUI.appendChild(controlText);

	var endLatLong = marker.position;
	
	//using global values lat_here, lon_here, OH OH !!
	var startLatLong ;
	if (places['dist_from'] == 'here'){
		startLatLong = new google.maps.LatLng(lat_here,lon_here);		
	}
	else {
		var input_lat = 0;
		input_lat =
		<?php if (isset ($searchForm['lat'])) {echo $searchForm['lat']; }
			else { echo "0";} ?> ;
		var input_lon = 	
		<?php if (isset ($searchForm['lon']))
			{ echo $searchForm['lon']; }
			else { echo "0";} ?> ;
		if (input_lat == 0 || input_lon == 0){
			alert ("Geo location is 0 " + input_lat+" " + input_lon);
		}
		else {
			startLatLong = new google.maps.LatLng(input_lat,input_lon);
		} 			
	}

	//console.log("input_lat: " + input_lat + " input_lon: " + input_lon);
 	controlUI.addEventListener('click', function() {
	  marker.setMap(null);
	  calcRoute(startLatLong, endLatLong, method);
	  dir.setMap(map);
	});
	
}

function initializeMap (i, lat, lon) {
	
	directionsService = new google.maps.DirectionsService();
	directionsDisplay = new google.maps.DirectionsRenderer();
	
  var prop = {
	center:new google.maps.LatLng(lat,lon),
	zoom:15,
	//mapTypeId:google.maps.MapTypeId.TERRAIN	
	//https://developers.google.com/maps/documentation/javascript/controls
	mapTypeControl: false,
	rotateControl: false, 
	mapTypeId:google.maps.MapTypeId.ROADMAP	
  };
   var map = new google.maps.Map(document.getElementById("map_"+i), prop);
	var marker = new google.maps.Marker({
	  position: prop.center,
	  map: map
	});
	directionsDisplay.setMap(map);
	return {prop: prop, map: map, marker: marker, dir: directionsDisplay};
}	  

//TODO remove routes after minimizing map
//TODO change the color of A object in route map to green

function calcRoute(startLatLong, endLatLong, method) {
  var start = startLatLong;
  var end = endLatLong;
  var googleMethod;
  if (method == "Walk"){
	  googleMethod = "WALKING"  ;
  } else if (method == "Bike"){
	  googleMethod = "BICYCLING" ;
  } else {
	  googleMethod = "DRIVING" ;
  }
  var request = {
    origin: start,
    destination: end,
    //travelMode: 'DRIVING'
	travelMode: googleMethod
  };
  //console.log(startLatLong, " -- ", endLatLong );
  directionsService.route(request, function(result, status) {
    if (status == 'OK') {
      directionsDisplay.setDirections(result);
    }
  });
}
	 
function handleMapShowHide (i, map, dir, marker) {
	//console.log(document.getElementById("mapRow_"+i));
	document.getElementById("mapRow_"+i).onclick = function () {
	var p = document.getElementById("map_"+i).style.display;
	//reseting the map
	if (p == "none")
		document.getElementById("map_"+i).style.display = 'block';
	else
		document.getElementById("map_"+i).style.display = 'none';
		
	//reseting the directions
	dir.setMap(null);
	marker.setMap(map);

/* 	console.log(dir);
	if ( dir.getMap() == null ) {
		dir.setMap(map);	
	} else {
		dir.setMap(null);
	}
	console.log("after: "+dir.getMap());		
 */	
	return false;
	}
	return;
}	 
	  
function drawMap(i, lat, lon){
	//places
	
	var init = initializeMap(i, lat, lon);
	var prop = init.prop;
	var map = init.map;
	var marker = init.marker;
	var dir = init.dir;
	
	document.getElementById("map_"+i).style.display = 'block';
	google.maps.event.trigger(map, marker);	
	
	
	handleMapShowHide(i, map, dir, marker);
	
	//handling new buttons
	var walkThere = document.createElement('div');
	var centerControlWalk = new addMapButton(walkThere, map, "Walk", marker, dir);
	var bikeThere = document.createElement('div');
	var centerControlBike = new addMapButton(bikeThere, map, "Bike", marker, dir);
	var driveThere = document.createElement('div');
	var centerControlDrive = new addMapButton(driveThere, map, "Drive", marker, dir);

	walkThere.index = 1;
	bikeThere.index = 2;
	driveThere.index = 3;


	
	map.controls[google.maps.ControlPosition.LEFT_TOP].push(walkThere);
	map.controls[google.maps.ControlPosition.LEFT_TOP].push(bikeThere);
	map.controls[google.maps.ControlPosition.LEFT_TOP].push(driveThere);
	
	console.log(map);
	
	return;
}


function addRow (p, i){
	
	var outHTML = "<tr>\n";
	outHTML += convertToImage(p.icon, 30);
	outHTML += convertToTextWithFunc(p.name, i, "retrivePhotosComments", "col2");
	outHTML += convertToTextWithFuncAndID(p.address, i, "drawMap", "mapRow", p.lat, p.lon, "col2");
	outHTML += "</tr>";		
	
	return outHTML;
}

function drawTable(rows){
	
	var outHTML  = "<table class='result_table_class'>";
	outHTML += "<th>Category</th>" + "<th>Name</th>" + "<th>Address</th>";
	for (var i=0; i < rows.length; i++){
		outHTML += addRow(rows[i], i);
	}
	outHTML += "</table>";
	document.getElementById("results_table").innerHTML += outHTML;
	
	return;
}



function handleEmptyResults(){
	var p = "No Records has been found";
	var outHTML  = "<table class='empty_table_class'>\n";
	outHTML += "<tr><td>\n" + p + "\n</td></tr>";		
	outHTML += "</table>";	
	document.getElementById("results_table").innerHTML += outHTML;
	return ;
}

function drawResultsTable(){
	//places is global variable
	places = <?php if (isset($places)) { 
	echo json_encode($places, JSON_PRETTY_PRINT); } else { echo "[]";} ?>;
	
	if (places.length != 0) {
		console.log(places);
		drawTable(places);		
	}
	else {
		handleEmptyResults();
	}
	
	return;
}

function removeReviews(){
	removeElements("final_reviews");
	changeReviewLogo("fReviewLogo");
	changeReviewSectionText("reviewText");
	
}

function removePhotos(){
	removeElements("final_photos");
	changePhotoLogo("fPhotoLogo");
	changePhotoSectionText("photoText");
}


function changeReviewLogo(docID){

	
	//downArrow = 0, upArrow = 1
	var p = ( document.getElementById(docID).getElementsByTagName('img')[0].src == downArrow ) ? 0 : 1;
	
	//downArrow
	if (p == 0) {
	document.getElementById(docID).getElementsByTagName('img')[0].src = 	upArrow;
	console.log(document.getElementById(docID).getElementsByTagName('img')[0].onclick);
	document.getElementById(docID).getElementsByTagName('img')[0].onclick = 
	function () {removeReviews(); return false;};
	} 
	else { //upArrow
	document.getElementById(docID).getElementsByTagName('img')[0].src = 	downArrow;	
	console.log(document.getElementById(docID).getElementsByTagName('img')[0].onclick);
	document.getElementById(docID).getElementsByTagName('img')[0].onclick = 
	function () {drawFinalReviews(); return false;};
	}
	
	console.log(document.getElementById(docID).getElementsByTagName('img'));
	
	return;
}

function changePhotoLogo(docID){

	
	//downArrow = 0, upArrow = 1
	var p = ( document.getElementById(docID).getElementsByTagName('img')[0].src == downArrow ) ? 0 : 1;
	
	if (p == 0) {
	document.getElementById(docID).getElementsByTagName('img')[0].src = 	upArrow;
	document.getElementById(docID).getElementsByTagName('img')[0].onclick = 
	function () {removePhotos(); return false;};
	
	} else {
	document.getElementById(docID).getElementsByTagName('img')[0].src = 	downArrow;	
	document.getElementById(docID).getElementsByTagName('img')[0].onclick = 
	function () {drawFinalPhotos(); return false;};
	}
	
//console.log(document.getElementById(docID).getElementsByTagName('img'));
	
	return;
}

function changePhotoSectionText(docID) {
	var p = document.getElementById(docID).innerHTML;
	if (p == photoShowText)
		document.getElementById(docID).innerHTML = photoHideText;
	else 
		document.getElementById(docID).innerHTML = photoShowText;
		
	return;
}

function changeReviewSectionText(docID) {
	var p = document.getElementById(docID).innerHTML;
	if (p == reviewShowText)
		document.getElementById(docID).innerHTML = reviewHideText;
	else 
		document.getElementById(docID).innerHTML = reviewShowText;
		
	return;
}


function removeElements(docID) {
	
	if (document.getElementById(docID))
	document.getElementById(docID).innerHTML = "";
	
}


function drawEmptyPhoto(){
	var p = "No Photos Found";
	var outHTML  = "<table class='no_result_found'>\n";
	
	outHTML += "<tr><th>\n" + p + "\n</th></tr>\n";		
	return outHTML;
	
}

function drawEmptyReview(){
	var p = "No Reviews Found";
	
	var outHTML  = "<table class='no_result_found'>\n";
	outHTML += "<tr><th>\n" + p + "\n</th></tr>\n";		
	return outHTML;
}


//TODO handle no reviews
function drawAllReviews(){
	
	if (finalReviews.length == 0) {
		return 	drawEmptyReview();
	}
	var outHTML  = "<table class='reviewAlbum'>\n";	
	for (var i=0; i < finalReviews.length; i++){
		outHTML += "<tr>\n";
		outHTML += convertToImageWithText(finalReviews[i].profilePic, 30, "        " + finalReviews[i].authorName);		
		outHTML += "</tr><tr>\n";
		outHTML += convertToTextSimpleWithClass("odd_review_rows", finalReviews[i].text);
		outHTML += "</tr>\n";	
	}
	
	outHTML += "</table>";
	
	return outHTML;
}

//TODO handle no photos

function drawAllPhotos() {
	
	if (finalPhotos.length == 0) {
		return 	drawEmptyPhoto();
	}
	var outHTML  = "<table class='photoAlbum'>\n";	
	for (var i=0; i < finalPhotos.length; i++){
		outHTML += "<tr>\n";
		var photoName = finalPhotos[i].split("/");
		photoName = photoName[photoName.length - 1];
		photoName = "http://www-scf.usc.edu/~nazarsha/yelpHW6/images/" + photoName;
		outHTML += convertToImageWandH(photoName, PHOTOWIDTH, PHOTOWIDTH);
		outHTML += "</tr>\n";	
	}
	outHTML += "</table>";
	
	return outHTML;
}


function drawFinalPhotos() {
	
/* 	removeElements("final_reviews");
	if (document.getElementById("fReviewLogo") == upArrow){
		changePhotoLogo("fReviewLogo");
	}
	if (document.getElementById("reviewText") == reviewHideText){
		changeReviewSectionText("reviewText");
	}
 */	
	if (document.getElementById("fReviewLogo").getElementsByTagName('img')[0].src == upArrow){
		removeReviews();		
	}
	
	changePhotoLogo("fPhotoLogo");
	changePhotoSectionText("photoText");
	document.getElementById("final_photos").innerHTML = drawAllPhotos();
	return;
}




function drawFinalReviews() {
	
/* 	removeElements("final_photos");
	if (document.getElementById("fPhotoLogo") == upArrow){
		changePhotoLogo("fPhotoLogo");
	}
	if (document.getElementById("photoText") == reviewHideText){
		changeReviewSectionText("photoText");
	}
 */	
 	if (document.getElementById("fPhotoLogo").getElementsByTagName('img')[0].src == upArrow){
		removePhotos();		
	}
	
	changeReviewLogo("fReviewLogo");
	changeReviewSectionText("reviewText");
	document.getElementById("final_reviews").innerHTML = drawAllReviews();
	
}


function drawInitialRows(i) {
	
	var outHTML = "";
	var p = places[i];
	outHTML  = "<table class='final_table'>";
	outHTML += "<tr><th style='text-align:center'>"+ p.name +"</th></tr>";		
	outHTML += "<tr>"+convertToTextSimpleWithID("reviewText", reviewShowText)+ "</tr>";
	outHTML += "<tr>"+convertToImageLinkWithID("fReviewLogo", downArrow, "drawFinalReviews()")+ "</tr>\n";
	outHTML += "<tr><td><div class='final_reviews' id='final_reviews' >\n</div></td></tr>";	

	outHTML +=  "</table>";
	outHTML += "<table class='final_table' id='last_row_table'>";
	
	outHTML += "<tr>"+convertToTextSimpleWithID("photoText", photoShowText) + "</tr>";
	outHTML += "<tr>"+convertToImageLinkWithID("fPhotoLogo", downArrow, "drawFinalPhotos()")+ "</tr>";

	outHTML += "<tr><td><div class='final_photos' id='final_photos' >\n</div></td></tr>"	
	
	outHTML +=  "</table>";
	document.getElementById("final_result").innerHTML = outHTML;
	return;
	
}

function drawPhotosAndRevies(i, photos, reviews){
	
	document.getElementById("results_table").innerHTML = ""; 	
	//global variables
	finalPhotos = photos;
	finalReviews = reviews;
	drawInitialRows(i);
	
	
	
}


function retrivePhotosComments(i){
	var photos = [];
	var reviews = [];
	var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("GET","index.php?q="+i,true);
    xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	//var rowNumber = i;

	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			console.log("Status: " + xmlhttp.readyState);
			var photosAndReviews = xmlhttp.responseText;
			var data = JSON.parse(photosAndReviews);	
			console.log(photosAndReviews);
			photos = data.photos;
			reviews = data.reviews;
			console.log( photos, reviews);
			
			drawPhotosAndRevies(i, photos, reviews);
			
	   }
	};	   
    xmlhttp.send();
	
}

</script>


<script>
//http://www.w3docs.com/snippets/google-maps/how-to-dynamically-load-google-maps.html
//https://developers.google.com/maps/documentation/javascript/examples/control-custom
</script>
 
<style>

label.bold {
	font-weight:bold;
}


H1.main-header{
	text-align:center;
	font-size:40px;
	border-bottom: 2px solid lightgrey;
	font-style: italic;
	font-weight: normal;
	margin-top: 0;
	margin-bottom: 10px;
}

.placeholder {
	border: 4px solid lightgrey;
	width: 840px;
	position:relative;
	left:400px;
	top:50px;
}

.btns {
	position:relative;
	left: 60px;
}

#search-btn, #clear-btn {
	background-color: white;
	border: 1px solid lightgrey;
	border-radius: .28571429rem ;
	cursor: pointer;
}


.empty_table_class {
	width : 1000px;
	border-collapse: collapse;
	background-color:lightgrey;
	text-align:center;
}

.result_table_class {
	width : 1000px;
	border-collapse: collapse;
	margin-bottom: 400px;
}

table.result_table_class th , table.result_table_class td{
	
	border: 1px solid gray;
	text-align: left; 	

	position: relative;
	top: 0px;	
	
	cursor: default;
}

table.result_table_class th {
	text-align: center;
}


#results_table {
	position:relative;
	left:320px;
	top:100px;
}

.final_table {
	width : 1000px;
	position:relative;
	left:320px;
	top:100px;
}


table.final_table th, table.final_table td {
	border: none;
	text-align: center;
	background-color: white;
	font-size: 20px;
}

table.final_table th {
	height: 50px;
}

table.final_table td {
	height: 40px;
}

table.final_table img {
	width: 25px;
	text-align:center;
}

.reviewAlbum {
	width: 1000px;
	position:relative;
	top:20px;
	margin-bottom: 100px;
	border-bottom: 1px solid lightgrey;	
}

table.reviewAlbum td {
 border: 1px solid lightgrey;
 border-collapse: collapse;
 height: 10px;
 padding-top: 0px;
 border-bottom-width: 0px;
 padding-bottom: 0px; 
}

.no_result_found {
	border: 1px solid lightgrey;
	text-align: center;
	width: 1000px;
}


#map,  #w3docs-map {
   width: 200px;
   height: 200px;
   background-color: grey;
 }

.mapStyle {
    width: 375px;
    height: 275px;
	display: none;		

	position: absolute;
	top:40px;
	left: 10px;
	z-index: 10;
	
}

 
.col2 {
	width: 450px;
	padding-left: 10px;
}


.mapButton {
background-color: white;
border : 2px solid #fff;
border-radius: 3px;
/*! box-shadow: 0 2px 6px rgba(0,0,0,.3); */
cursor : pointer;
margin-bottom: 22px;
text-align: left;
height:25px;
width:73px;
}

.mapButton:hover {
	background-color: lightgrey;
}

.mapButtonText {
color : rgb(25,25,25);
font-family : Roboto,Arial,sans-serif;
font-size : 13px;
line-height : 25px;
padding-left : 5px; /*! padding-right : 5px; */ 
height:100%;
width:100%;	
}
 

table.photoAlbum {
	width: 840px;
	position: relative;
	left: 76px;
	margin-bottom: 150px;	
} 
 
table.photoAlbum td {
 border: 2px solid lightgrey;
 border-collapse: collapse;
}

table.photoAlbum td img{
	text-align: center;
	padding: 20px;
} 


.cursorStyle 
{
	cursor: pointer;
}


#final_photos {
	margin-bottom: 200px;
}

.empty_table_class td {
	font-size:18px;
}

 
</style>
	
</head>
<body onload = "getCurLocation()">

	<div class="placeholder">
    <h1 class="main-header">Travel and Entertainment Search</h1>
	<div class="search-box">
		<form name="search-form" id="search-form" method="POST" 
		accept-charset="UTF-8" >
		<table>
			<tr><td>
			<label class="bold" >Keyword </label>
			<input type="text" name="keyword" id="keyword" 
			required >
			</input>
			</td></tr>
			
			<tr><td>
			<label class="bold" >Category</label>
			<select name="category" id="category"/>
			<!-- TODO: keep value after the submit-->
				<option value="default">
				default
				</option>
				<?php
				foreach ($categoryItems as $item ) {
					echo "<option value='$item'>$item</option>";
				}
				?>
			</td></tr>
			
			<tr><td>
			<label class="bold" >Distance(miles)</label>
			<input type="text" name="distance" id="distance" 
				placeholder="10"
				value="" ><label class="bold" >from</label>
			</td>
			<td>
			<input type="radio" name="dist_from" id="dist_from_here"
					checked="checked"
					value="here"
					onclick="disableLocation()" />Here
			<input type="text" name="cur_loc" id="cur_loc"
					hidden >
			</td>
			<tr><td/><td>
			<input type="radio" name="dist_from" id="dist_from_loc" 
				value="loc"
				onclick="enableLocation()" />				
			<input  type="text" name="input_loc" id="input_loc" 
			placeholder="location" disabled />
			
			</td></tr>
			
			<tr>
			<td>
			<div class="btns">
				<input type="submit" name="search-btn" id="search-btn"
					value="Search"
					disabled
					onclick="submitForm('search-form')" />
				<input type="submit" name="clear-btn" id="clear-btn" 
					value="Clear"
					onclick="clearForm()" />
			</div>
			</td>
			</tr>
			
		</table>
		</form>
	</div>	
</div>

<?php if ($show_results): ?>
	<div class="result-box" id="result-box">
		<div id="results_table">
			
		<script type="text/javascript">
		drawResultsTable();
		</script>
        </div>
	</div>
<?php endif; ?>
	
<div class="final_result" id="final_result">

</div>
	
</body>

</html>