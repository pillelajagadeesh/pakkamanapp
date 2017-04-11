'use strict';

angular.module('reachoutApp')
    .controller('MapController', function ($scope,$http,$timeout,Provider) {
    	
    	var mapOptions = {
                zoom: 10,
                center: new google.maps.LatLng(12.9716,77.5946),
                mapTypeId: google.maps.MapTypeId.ROADMAP
            }

            $scope.map = new google.maps.Map(document.getElementById('map'), mapOptions);
    	    $scope.map.setTilt(0);
    	
    	
    	$scope.maps=[];
    	$scope.maps="";
    	 $scope.loadAll = function() {
             Provider.getProviderLocation(function(result) {
                $scope.maps= result;
             });
             
         };
         $scope.loadAll();
         
         $scope.markers = [];
         
         var infoWindow = new google.maps.InfoWindow();
         
         var createMarker = function(info){
             var marker = new google.maps.Marker({
                 map: $scope.map,
                 position: new google.maps.LatLng(info.lat, info.long),
                 icon: "http://labs.google.com/ridefinder/images/mm_20_red.png",
                 title:info.title
             });
             
             
             marker.content = '<div class="infoWindowContent">' + '<B>'+ info.Address +'</B>' + '</div>';
             
             google.maps.event.addListener(marker, 'click', function(){
                 infoWindow.setContent('<h2>' + marker.title + '</h2>' + marker.content);
                 infoWindow.open($scope.map, marker);
             });
             
             $scope.markers.push(marker);
             
         } 
         
         $timeout(function() {
             for (var i = 0; i <$scope.maps.location.length ; i++){
           	  
                 createMarker($scope.maps.location[i]);
             } 
             }, 2000);
             
             
             $scope.openInfoWindow = function(e, selectedMarker){
                 e.preventDefault();
                 google.maps.event.trigger(selectedMarker, 'click');
             }
         
         
         
             
             
         $scope.consumerMaps=[];
         $scope.consumerMaps="";
         $scope.loadAll = function() {
             Provider.getConsumerLocation(function(result) {
            	 $scope.consumerMaps= result;
             });
         };
         $scope.loadAll();
    
         
    	              $scope.marks=[];
    	              
    	              var infoWindow1 = new google.maps.InfoWindow();
    	              
    	              var createMap = function(info){
    	                  var mark = new google.maps.Marker({
    	                      map: $scope.map,
    	                      position: new google.maps.LatLng(info.lat, info.long),
    	                      icon: "http://labs.google.com/ridefinder/images/mm_20_green.png",
    	                  });
    	                 
                          mark.content = '<div class="infoWindowContent">' + info.Mobile + '</div>';
    	                  
    	                  google.maps.event.addListener(mark, 'click', function(){
    	                      infoWindow.setContent('<B>' +"Consumer:"+mark.content + '</B>');
    	                      infoWindow.open($scope.map, mark);
    	                  });
    	                  $scope.marks.push(mark);
    	                  
    	              }
    	  
    	              $timeout(function() {
        	              for (var j = 0; j <$scope.consumerMaps.Consumerlocation.length ; j++){
        	            	  
        	                  createMap($scope.consumerMaps.Consumerlocation[j]);
        	              } 
        	              }, 5000);
    	              
    	              $scope.openInfoWindow1 = function(e, selectedMarker){
    	                  e.preventDefault();
    	                  google.maps.event.trigger(selectedMarker, 'click');
    	              }
    	              
    	              
    	          });
