var app = angular.module('form-directives', []);

app.directive("categories", function() {
  return {
    restrict: "E",
    templateUrl: "categories.html"
	}
});


app.directive ("from", function(){
	return {
		restrict: "E",
	templateUrl : 'from.html'}	
});

	
app.directive ("keyword", function(){
	return {
		restrict: "E",
	templateUrl : 'keyword.html'}	
});

app.directive ("distance", function(){
	return {
		restrict: "E",
	templateUrl : 'distance.html'}	
});

app.directive ("buttons", function(){
	return {
		restrict: "E",
	templateUrl : 'buttons.html'}	
});

app.directive ("resultable", function(){
	return {
		restrict: "E",
	templateUrl : 'resultable.html'}	
});


app.directive ("detailtable", function(){
	return {
		restrict: "E",
	templateUrl : 'detailtable.html'}	
});
app.directive ("infotab", function(){
	return {
		restrict: "E",
	templateUrl : 'infotab.html'}	
});
app.directive ("photostab", function(){
	return {
		restrict: "E",
	templateUrl : 'photostab.html'}	
});

app.directive ("maptab", function(){
	return {
		restrict: "E",
	templateUrl : 'maptab.html'}	
});

app.directive ("reviewtab", function(){
	return {
		restrict: "E",
	templateUrl : 'reviewtab.html'}	
});


