(function() {
	var app = angular.module('myApp',['form-directives', 'ngAnimate']);


/* app.service('ALLDATA', function ($q) {
	
	this.places = [];
	this.detail = {};
	
	this.setPlaces = function (pl) {
		this.places = pl;
	}
	
	this.clearPlaces = function(){
		this.places = [];
	}
	
	this.getPlaces = function () {
		return this.places;
	}
	
	this.init = function(){
		this.places = [];
		this.detail = {};		
	};
	
}); */


app.controller ('allController', function ($scope, $http, $timeout, photoMap, Map) {
	$scope.info = {
		keyword: '',
		category: 'Default',
		distance: '',
		dist_from: 'here',
		input_loc: '',
	};
	
	$scope.places = [];
	
	$scope.preparePlaces = function (pl) {
		var temp = [];
		for (var i in pl) {
			var curPlace = {};
			curPlace.id = pl[i].id;
			curPlace.value = pl[i];
			temp.push(curPlace);
		}	
		return temp;
	}
	
	$scope.searchInProgress = false;
	
	$scope.shouldShowAnything = false;
	

	$scope.tab = '1';
    $scope.info = $scope.info;
	$scope.num = 0;
	$scope.revNum = 0;


	$scope.page = 0; //determines which page to show for results
	$scope.showResult = true;
	$scope.showDetails = false;
	$scope.listPage = true;
	$scope.resultFailed = false;
	$scope.noRec = false;
	
	$scope.stats = {
		'resultFailed' : false,
		'noRec' : false,
		'resultsRetrived' : false,
		'photosRet' : false
	}

$scope.storageAvailable = function (type) {
    try {
        var storage = window[type],
            x = '__storage_test__';
        storage.setItem(x, x);
        storage.removeItem(x);
        return true;
    }
    catch(e) {
        return e instanceof DOMException && (
            // everything except Firefox
            e.code === 22 ||
            // Firefox
            e.code === 1014 ||
            // test name field too, because code might not be present
            // everything except Firefox
            e.name === 'QuotaExceededError' ||
            // Firefox
            e.name === 'NS_ERROR_DOM_QUOTA_REACHED') &&
            // acknowledge QuotaExceededError only if there's something already stored
            storage.length !== 0;
    }
}	

	
	if ($scope.storageAvailable('localStorage')) {
		var t = JSON.parse(localStorage.getItem('favorites'));
		$scope.fav = (t) ? t: [];	  
	}
	else {
		$scope.fav = [];
	}	
	$scope.selectedID = '';
	$scope.quantity = 20;
	
	//$scope.curDet = detailItem;
	$scope.curDet = {};
	$scope.date = new Date();
	$scope.curDay = $scope.date.getDay();
	$scope.curHour = $scope.date.getHours();
	$scope.curMin = $scope.date.getMinutes();

	//remove, just for test
	/***/
	//$scope.places = $scope.preparePlaces(allPlaces);
	//$scope.stats.resultsRetrived = true;
	/***/


	$scope.urls = [];
	
    $scope.map_travelMode = 'Driving';
	$scope.map_view = 'road';

	$scope.map_map_inputLoc = '';
	
	$scope.map_init = function () {
		Map.init();
	}
	
	$scope.map_isView = function (v){
		return ($scope.map_view == v) ;
	}
	
	$scope.map_changeView = function (lat, lon) {
		if ($scope.map_view == 'road') {
			$scope.map_view = 'street';			
		}
		else $scope.map_view = 'road';
		Map.chView ($scope.map_view, lat, lon);		

	}


	
    $scope.map_search = function(str, mySource) {
        this.apiError = false;
        Map.search(str)
        .then(
            function(res) { // success
                //Map.addMarker(res);
                mySource.name = res.name;
                mySource.lat = res.geometry.location.lat();
                mySource.lng = res.geometry.location.lng();
            },
            function(status) { // error
                this.apiError = true;
                this.apiStatus = status;
            }
        );
    }


    $scope.map_searchAndRoute = function(src, dest, tMode) {
        this.apiError = false;
        Map.search(src)
        .then(
            function(res) { // success
                //Map.addMarker(res);
				var mySource = {};
                mySource.lat = res.geometry.location.lat();
                mySource.lng = res.geometry.location.lng();
				var s = {'lat': mySource.lat, 'lon': mySource.lng };
				Map.calcRoute(s, dest, tMode, Map.getDirDisp(), Map.showSteps, Map.map, Map.stepDisplay, Map.attachInstructionText);
				
            },
            function(status) { // error
                this.apiError = true;
                this.apiStatus = status;
				alert ('Failed to get location');
            }
        );
    }

	
	$scope.map_addMarker = function (lat, lon) {
		Map.addMarkerByLatLon(lat, lon);
	}

	$scope.map_getVal = function(loc, inp){
		//console.log ('In map_getVal', loc, '===', inp);
		if (loc == 'here') return 'Your Location';
		else return inp;
	}
   
	$scope.map_getRoute = function (fromLoc, travelMode, srcLoc, destLat, destLon){
		$scope.map_addMarker(destLat, destLon);
		console.log (fromLoc, srcLoc, destLat, destLon, travelMode);
		var dest = {'lat':destLat, 'lon': destLon };
		var fff = fromLoc.toLowerCase();
		if (fff == 'my location' || fff == 'your location') {
			var temp = srcLoc.split(',');
			var src = {'lat':temp[0], 'lon': temp[1] };
			//console.log ('Before query:', src, dest, travelMode);
			Map.calcRoute(src, dest, travelMode, Map.getDirDisp(), Map.showSteps, Map.map, Map.stepDisplay, Map.attachInstructionText );
		} else {
			$scope.map_searchAndRoute(fromLoc, dest, travelMode);
		}
		
	}
	
	$scope.map_clearMap = function(){
		
		//console.log ('Dir: ', Map.getDirDisp());
		Map.clearMap(Map.getDirDisp());		
	}
   
	
	

	$scope.initPhotoAlbum = function (id) {
		//if (!id) {return;}
		console.log ('initPhotoAlbum:', id);
		var curId = id;
		if (!id) curId = 'ChIJyyALcSaxwoARYGP9nSoZWJg'; 
		//photoMap.init(curId);
		photoMap.getPhotoUrls($scope.urls, curId);
		//console.log ('Photo urls:', $scope.urls);
		
	}
	
	
	$scope.getPhUrls = function () {
		//console.log ('urls:', $scope.urls);
		var temp = $scope.urls;
		return (temp);
	}
	
    $scope.range = function(max, step) {
        step = step || 1;
        var input = [];
        for (var i = 0; i <= max; i += step) {
            input.push(i);
        }
        return input;
    };	
	
	$scope.getPlaces = function () {
		if ( $scope.tab == '1' ) {
			var temp = $scope.places;
			return temp;
		} else {
			return $scope.fav;
		}
	}

	$scope.showHours = function(hours) {
		prompt(hours);
	}


	
	$scope.getOpenHours = function(day, utc_offset){
		
		//TODO change this, use hours
		var curday = (day != null) ? parseInt(day) : parseInt($scope.getWeekDay (utc_offset));
		var p = $scope.curDet.hours[curday];
		if ( !p ) return 'unknown';

		var h = p.split(":");
		if (h.length < 2) return p;
		
		return p.substring(h[0].length+1);
	}

	$scope.isFrac = function (val) {
		return !(val == 1 || val==2 || val==3 || val==4 || val==5);
	}

	$scope.getFrac = function (val) {
		var s = (val % 1).toFixed(4);
		return s * 100;
	}
	
	$scope.getWeekDay = function(utc_offset) {
		var d = moment();
		var cur = d.utc().utcOffset(utc_offset).day();		
		//console.log ('curDay ', cur);
		
		return (cur);
	}
			
	$scope.getWeekDayName = function (i) {
		var names = ["Sunday", "Monday", "Tuesday", "Wednesday" ,"Thursday",
			"Friday", "Saturday"];
		return names[i];
	}
	
	
	$scope.getRange = function ( cur ) {
		var inp = [];
		var c = parseInt (cur);
		for (var i=0; i < 7; i++){
			inp.push((c + i) % 7);
		}
		return inp;
	}

	$scope.doFav = function ( curId ) {
		$scope.fav;
		var index = $scope.fav.find(x => x.id === curId);
		if (index) {
			console.log(index, 'indexOf:', $scope.fav.indexOf(index));
			$scope.fav.splice($scope.fav.indexOf(index),1);
		}
		else { 
			var plIndex = $scope.places.find( x => x.id === curId );
			if (!plIndex) return;
			var curFav = {};
			curFav.id = curId;
			curFav.value = plIndex.value;
			$scope.fav.push(curFav);
		}
		console.log ($scope.fav);
		
		localStorage.setItem('favorites', JSON.stringify($scope.fav));
		
	} 
	

	$scope.isFav = function ( curId ) {
		if ($scope.fav.find(x => x.id === curId)) {
			return true;
		} 
		return false;
	}
		
	$scope.getDetails = function (index, id) {
		$scope.selectedID = id;
		
	}

	$scope.isSelected = function (id) {
		return ( $scope.selectedID === id );
	}

	$scope.showResFailed = function() {
		if ($scope.tab == '1' && $scope.places.length == 0 && $scope.stats.resultFailed) return true;
		return false;
	}
	$scope.showNoRec = function() {
		if ($scope.places.length == 0  &&  $scope.tab == '1' && $scope.stats.noRec) return true;
		else if ($scope.fav.length == 0 && $scope.tab == '2' ) return true; 
		return false;
	}

		

	$scope.getReviews = function (type) {
		if (type == 'Google Reviews'){
			if ($scope.curDet.google_reviews)
				return $scope.curDet.google_reviews;
			else return [];
		}
		else if ($scope.curDet.yelp_reviews) {
			return $scope.curDet.yelp_reviews;
		}
		else return [];
	}	
	
	$scope.getTweetURL = function (pl){
		var str = "https://twitter.com/intent/tweet?text=";
		var txt = ""
		txt += "Check out ";
		if (pl.name) txt += pl.name;
		txt += " located at ";
		if (pl.address) txt += pl.address ;
		if (pl.website || pl.url ) txt += ". Website: " ;
		//txt = '"' + txt + '"';
		str += txt;
		if (pl.website) str += "&url="+pl.website;
		else if ( pl.url ) str += "&url="+ pl.url ;
		str += "&hashtags=TravelAndEntertainmentSearch";
		return str;
		/* window.open(str,'MyWindow',width=600,height=300); 
		return false; */
		
	}
	
	
	
	$scope.showList = function () {
		return ($scope.tab == '1' )
	}
	
	
	$scope.shouldShowDet = function () {
		return ($scope.showDetails);		
	}
	$scope.shouldShowRes = function () {
		return ($scope.showResult);		
	}

	$scope.shouldShow = function() {
		return (($scope.showResult && $scope.stats.resultsRetrived) ||
				($scope.fav.length > 0 && $scope.isSet('2') && $scope.showResult ));
		//return ($scope.showResult);
	}
	
	
	
	$scope.setToShowDetails = function () {
		$scope.showResult = false;
		$scope.showDetails = true;
		$scope.num++;

	}

	$scope.setToShowResults = function () {
		$scope.showResult = true;
		$scope.showDetails = false;
		$scope.num++;

	}

	$scope.isSet = function(checkTab) {
		return $scope.tab === checkTab;
	};

	$scope.setTab = function(activeTab) {
		$scope.tab = activeTab;
		$scope.page = 0;
	};
	
	$scope.setToResultTab = function () {
		$scope.tab = '1'; 
		$scope.showResult = true;
		$scope.showDetails = false;
		$scope.page = 0;
	}

	$scope.setToFavTab = function () {
		$scope.tab = '2'; 
		$scope.showResult = true;
		$scope.showDetails = false;
		$scope.page = 0;
	}


		
	$scope.incPage = function (){
		console.log ('Inc page', $scope.page);
		if ($scope.page >= 2 ) return;
		$scope.page = $scope.page + 1;
	}

	$scope.decPage = function (){
		console.log ('Dec page', $scope.page);
		if ($scope.page <= 0 ) return;
		$scope.page = $scope.page - 1;
	}
		
	$scope.fetchIP = function () {
		var ipAPI_url = "http://ip-api.com/json";
		var curLoc ;
		$http({
		  method: 'GET',
		  url: ipAPI_url
		}).then(function successCallback(response) {
			console.log('Loaded JSON successfully', 			response);
			var ip = response.data;
			curLoc = ip.lat + "," + ip.lon;
			$scope.info.cur_loc = curLoc;
		  }, function errorCallback(error) {
			console.log ("Could not get the ipAPI json file.");
			curLoc = "34.0266,-118.2831"; 
			$scope.info.cur_loc = curLoc;
		  }, $scope);
			
		
	}


	$scope.reset = function (ss) {
		console.log (ss);
		$scope.info.keyword = '';
		$scope.info.distance = '';
		$scope.info.category = 'Default';
		$scope.info.dist_from= 'here';
		$scope.info.input_loc = '';
		console.log ('In reset', $scope.info);
		ss.keyword.$valid = true;
		ss.keyword.$touched = false;
		ss.input_loc.$valid = true;
		ss.input_loc.$touched = false;
		//$scope.showResult = false;
		$scope.stats = {
			'resultFailed' : false,
			'noRec' : false,
			'resultsRetrived' : false,
			'photosRet' : false
		}
		$scope.places = [];
		$scope.curDet = {};
		$scope.setToResultTab();
		
	}

	$scope.getPlaceURL = function (isPlReq, placeID ) {
		var url = "https://csci571hw8-198219.appspot.com/?";
		//var url = "http://sportlab.usc.edu:2021/?"
		if (isPlReq) url += "places=1";
		else { 
			url += "details=1"; 
			url += "&";
			url += "id=" + placeID;
			return url;
		}
		for (var d in $scope.info) {
			if ($scope.info.hasOwnProperty(d)) {
				url += "&" ;
				url += String(d).trim();
				url += "=";
				url += String($scope.info[d]).trim();
			}
		}
		return url;
	}

	$scope.delay = function (t) {
	   return new Promise(function(resolve) { 
		   setTimeout(resolve, t)
	   });
	}

	
	
	$scope.sendDetailReq = function (placeID) {
			$scope.searchInProgress = true;
			var url = $scope.getPlaceURL(false, placeID);
			console.log ('New details query', url);
			//reseting photoalbum
			document.getElementById('photoAlbum').innerHTML = "";
			$scope.urls = [];
			$scope.selectedID = placeID;
			
			//reseting map
			$scope.map_clearMap();
			//$scope.setToResultTab();


			
			$http({
			  method: 'GET',
			  url: url
			}).then(function successCallback(response) {
				$timeout(function() {
					console.log('Loaded details query',	response);
					console.log('Data: ', response.data);
					$scope.$apply(function () {
						if (response.data.length != 0) {
							$scope.curDet = (response.data[0]);
							$scope.stats.resultsRetrived = true;
						
							//$scope.num++;							
							$scope.showResult = false;	

							//reseting the tab
							$('.nav-tabs a:first').tab('show');

/* 							$('#orderChoices .dropdown-item').val('Default Order');
							$( "button#reviewButton " ).val('Google Reviews');
							console.log ('Selected but', $( "button#reviewButton " ).val());

							$('#reviewButton').val('Google Reviews'); */

 							$('#inlineFormInput').val( $scope.map_getVal($scope.info.dist_from, $scope.info.input_loc));
							//reseting the value of map input field
							var input = $('#inlineFormInput');
							input.val($scope.map_getVal($scope.info.dist_from, $scope.info.input_loc));
							input.trigger('input'); 

							//reseting the value of map driving mode
							$('#travelMode').val('Driving');
							$scope.map_map_inputLoc = $scope.map_getVal($scope.info.dist_from, $scope.info.input_loc);							
						
							$scope.initPhotoAlbum($scope.curDet.id);
							$scope.stats.photosRet = true;
							console.log ('Info:', $scope.info);
														
							$scope.curDet_name = $scope.curDet.name;


							
						} else {
							console.log ('Details for this item was not found');
							$scope.stats.noRec = true; 
						}
						
						console.log ('Detail updated', $scope.curDet, 'resRet: ', $scope.stats.resultsRetrived, 'urls:', $scope.urls);
						//alert ('scope updated');
						$scope.searchInProgress = false;
						$scope.setToShowDetails();
						$scope.num++;							

					});
				}, 10);
			  				
				
			  }, function errorCallback(error) {
				console.log ("Could not get the places response.", error);
				$scope.stats.resultFailed = true;
				$scope.searchInProgress = false;
			  });
		
		
	}
	

	
	
	
	$scope.submitForm = function (isValid, pl, stats) {
		console.log ('After submit:', $scope.info);
		if (isValid){
			//alert ('Form is submitted');
			console.log ('isValid : ', isValid );
			//if ($scope.info.distance == "")
				//$scope.info.distance = 10;
/* 			var url = "https://csci571hw8-198219.appspot.com/?places=1&keyword=food&category=default&dist_from=here&cur_loc=34.0266,-118.2831&distance=10"; */
			$scope.searchInProgress = true;
			var url = $scope.getPlaceURL(true);
			$http({
			  method: 'GET',
			  url: url
			}).then(function successCallback(response) {
				$timeout(function() {
					console.log('Loaded places query',	response);
					console.log('Data: ', response.data);
					$scope.$apply(function () {
						$scope.places = $scope.preparePlaces(response.data);
						if ($scope.places.length == 0) 
						{ $scope.stats.noRec = true; }
						else { $scope.stats.resultsRetrived = true;}
						console.log ('Places updated', $scope.places, 'resRet: ', $scope.stats.resultsRetrived);
						//alert ('scope updated', $scope.stats.resultsRetrived);
						$scope.searchInProgress = false;
						//$scope.setToShowResults();
						$scope.setToResultTab();
					});
				}, 10);
			  				
				
			  }, function errorCallback(error) {
				console.log ("Could not get the places response.", error);
				$scope.stats.resultFailed = true;
				$scope.searchInProgress = false;
			  });
			
			
			
		}
		
	}
  
	
	
	
});



app.controller ('fromValController', function ($scope) {
	$scope.invalidStyle = function (k) {
		if (k.$invalid && k.$touched) {
			return {

				"border": "3px solid red"				
			}
		}
	}	
	
	$scope.shouldShow = function (k) {
		if (k.$invalid && k.$touched) return true;
		return false;
	}

	$scope.gPlace;
	
});

app.controller ( 'MyCtrl', function($scope) {
    $scope.gPlace;
});

//got this from https://gist.github.com/VictorBjelkholm/6687484
app.directive('googleplace', function() {
    return { 
        require: 'ngModel',
        link: function(scope, element, attrs, model) {
            var options = {
                types: [],
                componentRestrictions: {country: 'US'}
            };
            scope.gPlace = new google.maps.places.Autocomplete(element[0], options);

            google.maps.event.addListener(scope.gPlace, 'place_changed', function() {
                scope.$apply(function() {
                    model.$setViewValue(element.val());                
                });
            });
        }
    };
});
//myApp.factory('myService', function() {});


app.controller ('keywordController', function ($scope) {
	$scope.invalidStyle = function (k) {
		if (k.$invalid && k.$touched) {
			return {

				"border": "3px solid red"				
			}
		}
	}	
	
	$scope.shouldShow = function (k) {
		if (k.$invalid && k.$touched) return true;
		return false;
	}
	
});

app.controller ('distanceController', function ($scope) {
	this.distance = '';
});


app.controller ('catController', function ($scope) {
		$scope.names = ["Default", "Airport", "Amusement Park", "Aquarium", "Art Gallery", "Bakery", "Bar", "Beauty salon", "Bowling alley", "Bus station", "Cafe", "Campground", "Car rental", "Casino", "Lodging", "Movie theater", "Museum", "Night club", "Park", "Parking", "Restaurant", "Shopping mall", "Stadium", "Subway station", "Taxi stand", "Train station", "Transit station", "Travel agency", "Zoo"];
		
	

});



app.controller ('submitController', function ($scope) {

	
});

app.controller ('reviewController', function ($scope) {
	this.reviewType = 'Google Reviews';
	this.order = 'Default Order';
	
	this.getTime = function (t) {
		if (this.reviewType == 'Google Reviews') {
			var date = new Date(null);
			date.setSeconds(t); // specify value for SECONDS here
			var result = date.toISOString();
			return result;
		}
        else {
			return t;
		}
	}
	
	this.determineOrder = function (order)	{
		if (order == 'Default Order') return ;
		else if (order == 'Highest Rating') return '-rating ';
		else if (order == 'Lowest Rating') return 'rating ';
		else if (order == 'Most Recent' ) return '-time '; 
		else if (order == 'Least Recent') return 'time';
	}

/* 	this.isReverse = function (order)	{
		if (order == 'Highest Rating'|| order == 'Most Recent'  ) return 'true';
		else return 'false'; 
	}
 */	
});


app.controller ('tableController', function($scope, $timeout,  $interval) {

	
	
	
});



app.service('photoMap', function($q) {
//https://developers.google.com/maps/documentation/javascript/places#places_photos    
/* 	this.place_id = '';
	
    this.init = function(place_id) {
		this.place_id = place_id;
	} */
	
	this.getPhotoUrls = function (pUrls, id) {
		var request = {
		  placeId: id
		};
		var map = new google.maps.Map(document.getElementById('photos'), {});
		
		this.serv = new google.maps.places.PlacesService(map);
		this.serv.getDetails(request, function (response, status){
		  if (status == google.maps.places.PlacesServiceStatus.OK) {
			var p = response.photos;
			
			if (!p) return;
			//if (!response.photos) return ; 
			for (var i=0; i < p.length; i++){
				pUrls .push ( p[i].getUrl({'maxWidth': 1000, 'maxHeight': 1000}) );
			}
			var s = pUrls.length;
			var str = "<div class='row'>\n";
			var str_pre = '<div class="col-md-3 col-sm-12 col-xs-12"> <div class="box">';
			var str_post = '</div></div>';

/* 			for (var i=0; i < 2; i++){
				str += "<tr>";
				for (var j=0; j < s; j++){
				str += "<td><a href='" + pUrls[(i*s) +j] + "' target='_blank' >" ;
				str += "<img src='" + pUrls[(i*s) +j] + 
		"' class='img img-thumbnail album_img'/> " + "</a></td>";	
				}
				str += 	"</tr>";
			}
				str += 	"</table>"; */	


		for (var i=0; i < s; i+=3){
			str += '<div class="col-md-3 col-sm-12 col-xs-12" >';
			for (var j=0; j < 3; j++){
				if (i+j >= s) break; 
				str += '<div class="box">';
				str += "<a href='" + pUrls[i+j] + "' target='_blank' >" ;
				str += "<img class='img-responsive '  src='" + pUrls[i+j] + 
		"' style='width:100%; margin:15px 0'/> " + "</a>";	
				str += '</div>';


			}
			str += '</div>';
		}
			
		str += 	"</div>"; 

	document.getElementById('photoAlbum').innerHTML += str; 

			
		  }
		});

	
	}
	
});


app.controller('photoController', function($scope, photoMap) {


	
	
	
});


app.service('Map', function($q) {
    
    this.init = function() {
        var options = {
            center: new google.maps.LatLng(40.7127837, -74.00594130000002),
            zoom: 13        }
		this.directionsService = new google.maps.DirectionsService();
		this.directionsDisplay = new google.maps.DirectionsRenderer();
        this.map = new google.maps.Map(
            document.getElementById("mapSS"), options);
			
		this.directionsDisplay.setMap(this.map);
        this.places = new google.maps.places.PlacesService(this.map);
		this.stepDisplay = new google.maps.InfoWindow();
		
    }
    
	this.getDirDisp = function () {
		return (this.directionsDisplay);
	}	
	
    this.search = function(str) {
        var d = $q.defer();
        this.places.textSearch({query: str}, function(results, status) {
            if (status == 'OK') {
                d.resolve(results[0]);
            }
            else d.reject(status);
        });
        return d.promise;
    }
    
    this.addMarker = function(res) {
        if(this.marker)  { return };
        this.marker = new google.maps.Marker({
            map: this.map,
            position: res.geometry.location,
            animation: google.maps.Animation.DROP
        });
        this.map.setCenter(res.geometry.location);
    }
    this.addMarkerByLatLon = function(lat, lon) {
        if(this.marker) this.marker.setMap(null);
        this.marker = new google.maps.Marker({
            map: this.map,
            position: new google.maps.LatLng(lat, lon),
            animation: google.maps.Animation.DROP
        });
        this.map.setCenter(this.marker.position);
    }
	
	
	this.calcRoute = function (startLatLong, endLatLong, method, dirDisp, showStepsFunc, myMap, stepDisp, attachFunc) {
	  var start = new google.maps.LatLng(startLatLong.lat, startLatLong.lon);
	  var end = new google.maps.LatLng(endLatLong.lat, endLatLong.lon);
	  var googleMethod = method.toUpperCase();
	  dirDisp.setMap(myMap);
	  
	  var request = {
		origin: start,
		destination: end,
		travelMode: googleMethod,
		provideRouteAlternatives: true
	  };
			console.log ('sdasd\n\n', dirDisp)
	  this.directionsService.route(request, function(result, status) {
		if (status == 'OK') {
			//console.log (result, '\n\n')
			dirDisp.setDirections(result);
			//https://stackoverflow.com/questions/38406549/google-maps-api-directions-service-displaying-text-directions-repeating
			dirDisp.setPanel(document.getElementById('directions'));
			//showStepsFunc(result, myMap, stepDisp, attachFunc);
		}
	  });
	}

	this.chView = function (cur, lat, lng) {
		var view = this.map.getStreetView();
		console.log ('before: ',view);//.setVisible(false);
		if (cur == 'road') {
			view.setVisible(false);
			return ;			
		}
		var panorama = new google.maps.StreetViewPanorama(
		  document.getElementById('mapSS'), {
			position: new google.maps.LatLng(lat, lng),
			pov: {
			  heading: 34,
			  pitch: 10
			}

		  });
		this.map.setStreetView(panorama);
		console.log ('after: ',view);
		
	}
	
	this.clearMap = function(dirDis){
		console.log (dirDis);
		//this.directionsDisplay.setDirections(null);
		if (dirDis)
			dirDis.setMap(null);
	}
	
	
	
/* //https://developers.google.com/maps/documentation/javascript/directions

this.showSteps = function(directionResult, myMap, myStDisp, attachFunc) {
  // For each step, place a marker, and add the text to the marker's
  // info window. Also attach the marker to an array so we
  // can keep track of it and remove it when calculating new
  // routes.
  var myRoutes = directionResult.routes;
  for (var i = 0; i < myRoute.steps.length; i++) {
      var marker = new google.maps.Marker({
        position: myRoute.steps[i].start_point,
        map: myMap
      });
      attachFunc(marker, myRoute.steps[i].instructions, myMap, myStDisp);
      //markerArray[i] = marker;
  }
}

this.attachInstructionText = function (marker, text, myMap, myStDisp) {
  google.maps.event.addListener(marker, 'click', function() {
    myStDisp.setContent(text);
    myStDisp.open(myMap, marker);
  });
}	 */


	

    
});











})();




