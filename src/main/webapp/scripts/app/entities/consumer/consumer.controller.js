'use strict';

angular.module('reachoutApp')
    .controller('ConsumerController', function ($scope, $http, $state, Consumer) {

        $scope.consumers = [];
        $scope.search = "";
        $scope.consumers = "";
        $scope.filterTerm= ""; 
        $scope.loadAll = function() {
            Consumer.query(function(result) {
               $scope.consumers = result;
            });
        };
        $scope.loadAll();

         $scope.change=function () {
        	 alert($scope.confirmed);
         };
        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.active = function(id,active){
        	var url = "api/consumers/userActivation"
        	active = !active
        	$scope.data = {
        			id: id,
        			active: active
        	};
        	 $http.post(url, $scope.data).success(function(data){
        		 $scope.consumers = data;
                 $scope.loadAll();
        	 });
        };
        $scope.itemsPerPage = 20;
        $scope.currentPage = 0;
        $scope.range = function() {
            var rangeSize = 5;
            var ps = [];
            var start;

            start = $scope.currentPage;
            if ( start > $scope.pageCount()-rangeSize ) {
              start = $scope.pageCount()-rangeSize+1;
            }

            for (var i=start; i<start+rangeSize; i++) {
            	 if(i>=0) {
           		  ps.push(i);
         	    }
            }
            return ps;
          };

          $scope.prevPage = function() {
            if ($scope.currentPage > 0) {
              $scope.currentPage--;
            }
          };

          $scope.DisablePrevPage = function() {
            return $scope.currentPage === 0 ? "disabled" : "";
          };

          $scope.pageCount = function() {
            return Math.ceil($scope.consumers.length/$scope.itemsPerPage)-1;
          };

          $scope.nextPage = function() {
            if ($scope.currentPage < $scope.pageCount()) {
              $scope.currentPage++;
            }
          };

          $scope.DisableNextPage = function() {
            return $scope.currentPage === $scope.pageCount() ? "disabled" : "";
          };

          $scope.setPage = function(n) {
            $scope.currentPage = n;
          };
        $scope.clear = function () {
            $scope.consumer = {
                mobile: null,
                email: null,
                status: null,
                otp: null,
                otpCount: null,
                created: null,
                updated: null,
                name: null,
                id: null
            };
        };
    }).filter('offset', function() {
     	 return function(input, start) {
       	   start = parseInt(start, 10);
       	   return input.slice(start);
       	 };
       	});
angular.module('reachoutApp').filter('consumerFilter', [function () {
    return function (consumer, selectedConsumer) {
        if (!angular.isUndefined(consumers) && !angular.isUndefined(selectedConsumers) && selectedConsumers.length > 0) {
            var tempConsumers = [];
            angular.forEach(selectedConsumers, function (name) {
                angular.forEach(consumers, function (consumer) {
                    if (angular.equals(consumer.mobile, name)) {
                    	tempConsumers.push(consumer);
                    }
                });
            });
            return tempConsumers;
        } else {
            return consumers;
        }
    };
}]);